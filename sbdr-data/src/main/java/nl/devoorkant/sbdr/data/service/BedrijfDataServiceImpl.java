package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataLast24h;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.DataStatusAantal;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.repository.BedrijfHistorieRepository;
import nl.devoorkant.sbdr.data.repository.BedrijfRepository;
import nl.devoorkant.sbdr.data.repository.RechtsvormRepository;
import nl.devoorkant.sbdr.data.repository.SbiRepository;
import nl.devoorkant.sbdr.data.util.*;
import nl.devoorkant.util.DateUtil;
import nl.devoorkant.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

/**
 * Data Service bean with implemented functionality for Gebruiker.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * Implements the functionality exposed by the GebruikerDataService interface.
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Bas Dekker
 * @version %I%
 */

@Service("bedrijfDataService")
@Transactional(readOnly = true)
public class BedrijfDataServiceImpl implements BedrijfDataService {

	private static Logger LOGGER = LoggerFactory.getLogger(BedrijfDataService.class);
	@Autowired
	private BedrijfHistorieRepository bedrijfHistorieRepository;
	@Autowired
	private BedrijfRepository bedrijfRepository;
//	@Autowired
//	@Qualifier("sbdrEntityManagerFactory")
//	private EntityManagerFactory entityManagerFactory;
	@PersistenceContext(name="sbdr")
	private EntityManager entityManager;
	@Autowired
	private RechtsvormRepository rvRepository;
	@Autowired
	private SbiRepository sbiRepository;

	public boolean bedrijfHasAlerts(Integer bedrijfId) throws DataServiceException {
		try {
			List<AlertView> alertsOfBedrijf = bedrijfRepository.findAllActiveAlertsOfBedrijf(bedrijfId);
			return alertsOfBedrijf.size() > 0;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	public boolean bedrijfOnlyHasBedrijfLevelAlerts(Integer bedrijfId) throws DataServiceException {
		try {
			return bedrijfRepository.bedrijfOnlyHasBedrijfLevelAlerts(bedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void delete(Bedrijf bedrijf) throws DataServiceException {
		try {
			bedrijfRepository.delete(bedrijf);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<DataStatusAantal> findAantalKlantenPerActiveKlantStatus() throws DataServiceException {
		List<DataStatusAantal> result = null;

		try {
			List<Object[]> results = bedrijfRepository.findAantalKlantenPerActiveKlantStatus();

			if (results != null) {
				result = new ArrayList<DataStatusAantal>();

				for (Object[] alerto : results) {
					DataStatusAantal data = new DataStatusAantal();

					data.setStatusCode((String) alerto[1]);
					data.setStatus(EKlantStatus.get(data.getStatusCode()).getOmschrijving());
					data.setAantal((Long) alerto[2]);

					result.add(data);
				}
			}
			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<DataStatusAantal> findAantalMeldingenPerStatus() throws DataServiceException {
		List<DataStatusAantal> result = null;

		try {
			List<Object[]> results = bedrijfRepository.findAantalMeldingenPerStatus();

			if (results != null) {
				result = new ArrayList<DataStatusAantal>();

				for (Object[] alerto : results) {
					DataStatusAantal data = new DataStatusAantal();

					data.setStatusCode((String) alerto[0]);
					data.setStatus(EMeldingStatus.get(data.getStatusCode()).getOmschrijving());
					data.setAantal((Long) alerto[1]);

					result.add(data);
				}
			}
			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<DataStatusAantal> findAantalMonitoringPerStatus() throws DataServiceException {
		List<DataStatusAantal> result = null;

		try {
			List<Object[]> results = bedrijfRepository.findAantalMonitoringPerStatus();

			if (results != null) {
				result = new ArrayList<DataStatusAantal>();

				for (Object[] alerto : results) {
					DataStatusAantal data = new DataStatusAantal();

					data.setStatusCode((String) alerto[0]);
					data.setStatus(EMonitoringStatus.get(data.getStatusCode()).getOmschrijving());
					data.setAantal((Long) alerto[1]);

					result.add(data);
				}
			}
			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Long findAantalNieuweMonitoring() throws DataServiceException {
		Long result = null;

		try {
			Calendar now = Calendar.getInstance();
			Date nowdate = now.getTime();
			Calendar fromdate = Calendar.getInstance();
			fromdate.setTime(nowdate);
			fromdate.add(Calendar.HOUR, -48); // to get 2 days before subtract 48h

			List<Long> results = bedrijfRepository.findAantalNieuweMonitoring(fromdate.getTime());

			if (results != null) {
				result = results.get(0);
			}
			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Long findAantalNieuweRapporten() throws DataServiceException {
		Long result = null;

		try {
			Calendar now = Calendar.getInstance();
			Date nowdate = now.getTime();
			Calendar fromdate = Calendar.getInstance();
			fromdate.setTime(nowdate);
			fromdate.add(Calendar.HOUR, -48); // to get 2 days before subtract 48h

			List<Long> results = bedrijfRepository.findAantalNieuweRapporten(fromdate.getTime());

			if (results != null) {
				result = results.get(0);
			}
			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Long findAantalRapporten() throws DataServiceException {
		Long result = null;

		try {
			List<Long> results = bedrijfRepository.findAantalRapporten();

			if (results != null) {
				result = results.get(0);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	/*
	 * Not used anymore. Replaced by findActiveObjectionsForSbdrAdmin + findActiveSupportTicketsForSbdrAdmin
	 * 
	 * @see nl.devoorkant.sbdr.data.service.BedrijfDataService#findActiveAlertsForSbdrAdmin(java.lang.Integer, boolean, org.springframework.data.domain.Pageable)
	 */
	//	@Override
	//	public Page<Object[]> findActiveAlertsForSbdrAdmin(Integer bId, boolean includeObjections, Pageable p) throws DataServiceException {
	//		try {
	//			if(includeObjections){
	//				return bedrijfRepository.findActiveSupportAlertsForSbdrAdmin(bId, p);
	//			} else {
	//				return bedrijfRepository.findActiveSupportAlertsNoObjectionsForSbdrAdmin(bId, p);
	//			}
	//		} catch(Exception e) {
	//			LOGGER.error(e.getMessage());
	//			throw new DataServiceException(e.getMessage());
	//		}
	//	}

	@Override
	public Page<Object[]> findActiveAlertsNoMonitoringOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws DataServiceException {

		try {
			return bedrijfRepository.findActiveAlertsNoMonitoringOfBedrijf(bedrijfId, userId, pageable);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public Page<Object[]> findActiveAlertsOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws DataServiceException {

		try {
			//			if(StringUtil.isNotEmptyOrNull(search)) {
			//				if(search.length() > 2) {
			//					if(SearchUtil.isAlfanumeric(search)) {
			//						String sbdrnr = SearchUtil.sbdrNumber(search);
			//						String venr = SearchUtil.veNumber(search);
			//
			//						if(sbdrnr != null) {
			//							result = bedrijfRepository.findActiveAlertsOfBedrijf_SearchSbdrNummer(bedrijfId, userId, SearchUtil.convertDbSearchString(sbdrnr) + "%", pageable);
			//						} else if(venr != null) {
			//							result = bedrijfRepository.findActiveAlertsOfBedrijf_SearchVeNummer(bedrijfId, userId, SearchUtil.convertDbSearchString(venr) + "%", pageable);
			//						} else
			//							result = bedrijfRepository.findActiveAlertsOfBedrijf_SearchName(bedrijfId, userId, SearchUtil.convertDbSearchString(search) + "%", pageable);
			//					} else {
			//						result = bedrijfRepository.findActiveAlertsOfBedrijf_SearchNummer(bedrijfId, userId, SearchUtil.convertDbSearchString(search) + "%", pageable);
			//					}
			//				}
			//			} else
			return bedrijfRepository.findActiveAlertsOfBedrijf(bedrijfId, userId, pageable);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public Page<Melding> findMeldingenOfAllBedrijven(String search, List<String> klantStatusCodes, List<String> meldingStatusCodes, Pageable pageable) throws DataServiceException {
		Page<Melding> result = null;

		try {
			Collection<String> klantStatusCodesCol = null;
			if (klantStatusCodes != null && klantStatusCodes.size() > 0) klantStatusCodesCol = klantStatusCodes;
			Collection<String> meldingStatusCodesCol = null;
			if (meldingStatusCodes != null && meldingStatusCodes.size() > 0) meldingStatusCodesCol = meldingStatusCodes;

			if (StringUtil.isNotEmptyOrNull(search)) {
				if (search.length() > 2) {
					if (SearchUtil.isAlfanumeric(search)) {
						String sbdrnr = SearchUtil.sbdrNumber(search);
						String venr = SearchUtil.veNumber(search);

						if (sbdrnr != null) {
							result = bedrijfRepository.findMeldingenOfAllBedrijven_SearchSbdrNummer(SearchUtil.convertDbSearchString(sbdrnr) + "%", klantStatusCodesCol, meldingStatusCodesCol, pageable);
						} else if (venr != null) {
							result = bedrijfRepository.findMeldingenOfAllBedrijven_SearchVeNummer(SearchUtil.convertDbSearchString(venr) + "%", klantStatusCodesCol, meldingStatusCodesCol, pageable);
						} else
							result = bedrijfRepository.findMeldingenOfAllBedrijven_SearchName(SearchUtil.convertDbSearchString(search) + "%", klantStatusCodesCol, meldingStatusCodesCol, pageable);
					} else {
						//						if (SearchUtil.isKvKNumber(search))
						//							result = bedrijfRepository.findActiveMeldingenOfAllBedrijven_SearchKvkNummer(SearchUtil.convertDbSearchString(search) + "%", pageable);
						//						else
						result = bedrijfRepository.findMeldingenOfAllBedrijven_SearchNummer(SearchUtil.convertDbSearchString(search) + "%", klantStatusCodesCol, meldingStatusCodesCol, pageable);
					}
				} 
			} else result = bedrijfRepository.findMeldingenOfAllBedrijven(klantStatusCodesCol, meldingStatusCodesCol, pageable);

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Melding> findActiveMeldingenOfBedrijf(@Param("bedrijfId") Integer bedrijfId, String search, Pageable pageable) throws DataServiceException {
		Page<Melding> result = null;
		try {
			if (StringUtil.isNotEmptyOrNull(search)) {
				if (search.length() > 2) {
					if (SearchUtil.isKvKNumber(search))
						result = bedrijfRepository.findActiveMeldingenOfBedrijf_SearchKvkNummer(bedrijfId, SearchUtil.convertDbSearchString(search) + "%", pageable);
					else
						result = bedrijfRepository.findActiveMeldingenOfBedrijf_SearchName(bedrijfId, SearchUtil.convertDbSearchString(search) + "%", pageable);
				}
			} else result = bedrijfRepository.findActiveMeldingenOfBedrijf(bedrijfId, pageable);

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Object[]> findActiveMonitoringAlertsOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws DataServiceException {

		try {
			return bedrijfRepository.findActiveMonitoringAlertsOfBedrijf(bedrijfId, userId, pageable);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Object[]> findActiveMonitoringNotificationsOfBedrijf(Integer bedrijfId, Pageable pageable) throws DataServiceException {

		try {
			return bedrijfRepository.findActiveMonitoringNotificationsOfBedrijf(bedrijfId, pageable);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public Page<Object[]> findActiveMonitoringOfBedrijf(@Param("bedrijfId") Integer bedrijfId, String search, Pageable pageable) throws DataServiceException {
		Page<Object[]> result = null;

		try {
			if (StringUtil.isNotEmptyOrNull(search)) {
				if (search.length() > 2) {
					if (SearchUtil.isAlfanumeric(search)) {
						String sbdrnr = SearchUtil.sbdrNumber(search);
						String monr = SearchUtil.moNumber(search);

						if (sbdrnr != null) {
							result = findActiveMonitoringOfBedrijfCriteria(bedrijfId, SearchUtil.convertDbSearchString(sbdrnr) + "%", ESearchActiveMonitoringOfBedrijf.SBDRNUMMER, pageable);
						} else if (monr != null) {
							result = findActiveMonitoringOfBedrijfCriteria(bedrijfId, SearchUtil.convertDbSearchString(monr) + "%", ESearchActiveMonitoringOfBedrijf.MOREFERENTIE, pageable);
						} else
							result = findActiveMonitoringOfBedrijfCriteria(bedrijfId, SearchUtil.convertDbSearchString(search) + "%", ESearchActiveMonitoringOfBedrijf.NAAM, pageable);
					} else {
						result = findActiveMonitoringOfBedrijfCriteria(bedrijfId, SearchUtil.convertDbSearchString(search) + "%", ESearchActiveMonitoringOfBedrijf.NUMMER, pageable);
					}
				}
			} else
				result = findActiveMonitoringOfBedrijfCriteria(bedrijfId, null, ESearchActiveMonitoringOfBedrijf.NOTHING, pageable);

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Object[]> findActiveObjectionsForSbdrAdmin(Integer bId, Pageable p) throws DataServiceException {
		try {
			return bedrijfRepository.findActiveObjectionsForSbdrAdmin(bId, p);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Object[]> findActiveSupportTicketsForSbdrAdmin(Integer bId, Pageable p) throws DataServiceException {
		try {
			return bedrijfRepository.findActiveSupportTicketsForSbdrAdmin(bId, p);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<DataLast24h> findAllActiveKlantenLast24h() throws DataServiceException {
		List<DataLast24h> result = null;

		try {
			Calendar now = Calendar.getInstance();
			Date nowdate = now.getTime();
			Calendar yesterday = Calendar.getInstance();
			yesterday.setTime(nowdate);
			yesterday.add(Calendar.HOUR, -24); // to get previous day subtract 24h

			List<Object[]> results = bedrijfRepository.findAllActiveKlantenLast24h(yesterday.getTime(), now.getTime());

			if (results != null) {
				result = new ArrayList<DataLast24h>();

				for (Object[] alerto : results) {
					DataLast24h data = new DataLast24h();

					data.setHourOrder((Integer) alerto[0]);
					data.setHourNr((Short) alerto[1]);
					data.setHourDesc((String) alerto[2]);
					data.setAantal((Long) alerto[3]);

					result.add(data);
				}

			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<DataLast24h> findAllActiveMeldingenLast24h() throws DataServiceException {
		List<DataLast24h> result = null;

		try {
			Calendar now = Calendar.getInstance();
			Date nowdate = now.getTime();
			Calendar yesterday = Calendar.getInstance();
			yesterday.setTime(nowdate);
			yesterday.add(Calendar.HOUR, -24); // to get previous day subtract 24h

			List<Object[]> results = bedrijfRepository.findAllActiveMeldingenLast24h(yesterday.getTime(), now.getTime());

			if (results != null) {
				result = new ArrayList<DataLast24h>();

				for (Object[] alerto : results) {
					DataLast24h data = new DataLast24h();

					data.setHourOrder((Integer) alerto[0]);
					data.setHourNr((Short) alerto[1]);
					data.setHourDesc((String) alerto[2]);
					data.setAantal((Long) alerto[3]);

					result.add(data);
				}

			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<DataLast24h> findAllActiveMonitoringLast24h() throws DataServiceException {
		List<DataLast24h> result = null;

		try {
			Calendar now = Calendar.getInstance();
			Date nowdate = now.getTime();
			Calendar yesterday = Calendar.getInstance();
			yesterday.setTime(nowdate);
			yesterday.add(Calendar.HOUR, -24); // to get previous day subtract 24h

			List<Object[]> results = bedrijfRepository.findAllActiveMonitoringLast24h(yesterday.getTime(), now.getTime());

			if (results != null) {
				result = new ArrayList<DataLast24h>();

				for (Object[] alerto : results) {
					DataLast24h data = new DataLast24h();

					data.setHourOrder((Integer) alerto[0]);
					data.setHourNr((Short) alerto[1]);
					data.setHourDesc((String) alerto[2]);
					data.setAantal((Long) alerto[3]);

					result.add(data);
				}

			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findAllBedrijvenWithNewMeldingenOfDay(Date date) throws DataServiceException {
		try {
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.setTime(date);
			tomorrow.add(Calendar.HOUR, 24);

			return bedrijfRepository.findAllBedrijvenWithNewMeldingen(date, tomorrow.getTime());
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Object[]> findAllExceptionBedrijven(String search, List<String> statusCodes, Pageable pageable) throws DataServiceException {
		Page<Object[]> result = null;

		try {
			Collection<String> statusCodesCol = null;
			if (statusCodes != null && statusCodes.size() > 0) statusCodesCol = statusCodes;
			else {
				statusCodesCol = EBedrijfStatus.getAllCodes();
			}
			if (StringUtil.isNotEmptyOrNull(search)) {
				if (search.length() > 2) {
					if (SearchUtil.isAlfanumeric(search)) {
						String sbdrnr = SearchUtil.sbdrNumber(search);
						if (sbdrnr != null) {

							result = bedrijfRepository.findAllExceptionBedrijven_SearchSbdrNummer(SearchUtil.convertDbSearchString(sbdrnr) + "%", statusCodesCol, pageable);
						} else
							result = bedrijfRepository.findAllExceptionBedrijven_SearchName(SearchUtil.convertDbSearchString(search) + "%", statusCodesCol, pageable);
					} else {
						result = bedrijfRepository.findAllExceptionBedrijven_SearchNummer(SearchUtil.convertDbSearchString(search) + "%", statusCodesCol, pageable);
					}
				}
			} else result = bedrijfRepository.findAllExceptionBedrijven(statusCodesCol, pageable);

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Object[]> findAllKlantBedrijvenOnKlantStatus(boolean klantStatus, String search, List<String> statusCodes, Pageable pageable) throws DataServiceException {
		Page<Object[]> result = null;

		try {
			Collection<String> statusCodesCol = null;
			if (statusCodes != null && statusCodes.size() > 0) statusCodesCol = statusCodes;
			else {
				statusCodesCol = EKlantStatus.getAllCodes();
				//statusCodesCol.add("ACT");
				//statusCodesCol.add("BLK");
				//statusCodesCol.add("PRO");
				//statusCodesCol.add("REG");
			}
			if (StringUtil.isNotEmptyOrNull(search)) {
				if (search.length() > 2) {
					if (SearchUtil.isAlfanumeric(search)) {
						String sbdrnr = SearchUtil.sbdrNumber(search);
						if (sbdrnr != null) {

							result = bedrijfRepository.findAllKlantBedrijvenOnActiveKlantStatus_SearchSbdrNummer(klantStatus, SearchUtil.convertDbSearchString(sbdrnr) + "%", statusCodesCol, pageable);
						} else
							result = bedrijfRepository.findAllKlantBedrijvenOnActiveKlantStatus_SearchName(klantStatus, SearchUtil.convertDbSearchString(search) + "%", statusCodesCol, pageable);
					} else {
						//if (SearchUtil.isKvKNumber(search))
						//	result = bedrijfRepository.findAllKlantBedrijvenOnActiveKlantStatus_SearchKvkNummer(klantStatus, SearchUtil.convertDbSearchString(search) + "%", statusCodesCol, pageable);
						//else
						result = bedrijfRepository.findAllKlantBedrijvenOnActiveKlantStatus_SearchNummer(klantStatus, SearchUtil.convertDbSearchString(search) + "%", statusCodesCol, pageable);
					}
				}
			} else result = bedrijfRepository.findAllKlantBedrijvenOnActiveKlantStatus(true, statusCodesCol, pageable);

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findAllNonRecentBedrijven(Date datum) throws DataServiceException {
		try {
			return bedrijfRepository.findAllNonRecentBedrijven(datum);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<DataLast24h> findAllRapportenLast24h() throws DataServiceException {
		List<DataLast24h> result = null;

		try {
			Calendar now = Calendar.getInstance();
			Date nowdate = now.getTime();
			Calendar yesterday = Calendar.getInstance();
			yesterday.setTime(nowdate);
			yesterday.add(Calendar.HOUR, -24); // to get previous day subtract 24h

			//SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MMM.dd HH:mm:SS");
			//String nowstr = formatter.format(now.getTime());
			//String yeststr = formatter.format(yesterday.getTime());

			//LOGGER.debug("now: " + nowstr + " yestr: " + yeststr);

			List<Object[]> results = bedrijfRepository.findAllRapportenLast24h(yesterday.getTime(), now.getTime());

			if (results != null) {
				result = new ArrayList<DataLast24h>();

				for (Object[] alerto : results) {
					DataLast24h data = new DataLast24h();

					data.setHourOrder((Integer) alerto[0]);
					data.setHourNr((Short) alerto[1]);
					data.setHourDesc((String) alerto[2]);
					data.setAantal((Long) alerto[3]);

					result.add(data);
				}

			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Klant findAnyKlantRecordOfBedrijf(Integer bedrijfId) throws DataServiceException {
		try {
			List<Klant> klanten = bedrijfRepository.findAnyKlantRecordOfBedrijf(bedrijfId);

			if (klanten != null && klanten.size() >= 1) {
				if (klanten.size() == 1) return klanten.get(0);
				else
					throw new DataServiceException("Integrity error. More than one Klant attached to Bedrijf: " + bedrijfId);
			} else return null;

		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findBedrijvenMetMonitoringOpBedrijfByBedrijfId(Integer bedrijfId) throws DataServiceException {
		try {
			return bedrijfRepository.findBedrijvenMetMonitoringOpBedrijfByBedrijfId(bedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findBestaandeBedrijvenFromBedrijvenList(List bedrijvenIds) throws DataServiceException {
		try {
			Collection<Integer> col = bedrijvenIds;
			return bedrijfRepository.findBestaandeBedrijven_SearchListOfBedrijven(col);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, isolation = Isolation.SERIALIZABLE)
	public Bedrijf findByBedrijfId(Integer bedrijfId) throws DataServiceException {
		try {
			Optional<Bedrijf> bedrijf = bedrijfRepository.findById(bedrijfId);
			return bedrijf != null ? bedrijf.get() : null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Sbi findByCode(String code) throws DataServiceException {
		try {
			List<Sbi> sbis = sbiRepository.findByCode(code);

			if (sbis != null && sbis.size() == 1) return sbis.get(0);
			else return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Bedrijf findByKlantGebruikersId(Integer gebruikerId) throws DataServiceException {
		try {
			return bedrijfRepository.findByKlantGebruikersId(gebruikerId);
		} catch (Exception e) {
			LOGGER.error("findByKlantGebruikersId(ID " + gebruikerId + "): " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Bedrijf findByKvkNummer(String kvkNumber, String subDossier) throws DataServiceException {
		try {
			List<Bedrijf> bedrijven = bedrijfRepository.findByKvKNummer(kvkNumber, subDossier);

			if (bedrijven != null && bedrijven.size() == 1) return bedrijven.get(0);
			else return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findByKvkNummers(List<String> kvkNumbers) throws DataServiceException {
		try {
			return bedrijfRepository.findByKvKNummers(kvkNumbers);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Rechtsvorm findByRvCode(String code) throws DataServiceException {
		try {
			List<Rechtsvorm> rvs = rvRepository.findByCode(code);

			if (rvs != null && rvs.size() == 1) return rvs.get(0);
			else return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Rechtsvorm findByRvOmschrijving(String omschrijving) throws DataServiceException {
		try {
			List<Rechtsvorm> rvs = rvRepository.findByOmschrijving(omschrijving);

			if (rvs != null && rvs.size() == 1) return rvs.get(0);
			else return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Bedrijf findBySbdrNummer(String sbdrNummer) throws DataServiceException {
		try {
			List<Bedrijf> bedrijven = bedrijfRepository.findBySbdrNummer(sbdrNummer);

			if (bedrijven != null && bedrijven.size() == 1) return bedrijven.get(0);
			else return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Factuur> findFacturenOfBedrijf(Integer bId, Pageable p) throws DataServiceException {
		if (bId != null) {
			try {
				return bedrijfRepository.findFacturenOfBedrijf(bId, p);
			} catch (Exception e) {
				LOGGER.error("Error in findFacturenOfBedrijf: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else throw new DataServiceException("Parameter bId(Integer) cannot be null");
	}

	@Override
	public Klant findKlantOfBedrijf(Integer bedrijfId) throws DataServiceException {
		try {
			List<Klant> klanten = bedrijfRepository.findKlantOfBedrijf(bedrijfId);

			if (klanten != null && klanten.size() >= 1) {
				if (klanten.size() == 1) return klanten.get(0);
				else
					throw new DataServiceException("Integrity error. More than one Klant attached to Bedrijf: " + bedrijfId);
			} else return null;

		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Klant> findKlantOfBedrijfFromBedrijvenList(List bedrijvenIds) throws DataServiceException {
		try {
			Collection<Integer> col = bedrijvenIds;
			return bedrijfRepository.findKlantOfBedrijven(col);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findMatchingBedrijvenMetMeldingByBedrijfFromBedrijvenList(Integer bedrijfId, List bedrijvenIds) throws DataServiceException {
		try {
			Collection<Integer> col = bedrijvenIds;
			return bedrijfRepository.findBedrijvenMetMeldingByBedrijf_SearchListOfBedrijven(bedrijfId, col);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findMatchingBedrijvenMetMonitoringOfBedrijfFromBedrijvenList(Integer bedrijfId, List bedrijvenIds) throws DataServiceException {
		try {
			Collection<Integer> col = bedrijvenIds;
			return bedrijfRepository.findBedrijvenMetMonitoringOfBedrijf_SearchListOfBedrijven(bedrijfId, col);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findMatchingBedrijvenMetRapportCreatedTodayFromBedrijvenList(Integer bedrijfId, List bedrijvenIds) throws DataServiceException {
		try {
			Collection<Integer> col = bedrijvenIds;
			Date today = new Date();
			return bedrijfRepository.findBedrijvenMetRapportCreatedToday_SearchListOfBedrijven(bedrijfId, today, col);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findMeldingenOfBedrijf(Integer bedrijfId, List<String> statusCodes) throws DataServiceException {
		try {
			Collection<String> statusCodesCol = null;
			if (statusCodes != null && statusCodes.size() > 0) statusCodesCol = statusCodes;
			else {
				statusCodesCol = EMeldingStatus.getAllCodes();
			}

			return bedrijfRepository.findAllMeldingenOfBedrijf(bedrijfId, statusCodesCol);
		} catch (Exception e) {
			LOGGER.error("error fetching meldingenofbedrijf: " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Monitoring> findMonitoringOfBedrijf(Integer bedrijfId) throws DataServiceException {
		try {

			return bedrijfRepository.findActiveMonitoringByAllBedrijven(bedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Object[]> findRemovedBedrijvenOfBedrijf(@Param("bedrijfId") Integer bedrijfId, String view, String search, Pageable pageable) throws DataServiceException {
		Page<Object[]> result = null;

		try {
			if (StringUtil.isNotEmptyOrNull(search)) {
				if (search.length() > 2) {
					if (SearchUtil.isAlfanumeric(search)) {
						String sbdrnr = SearchUtil.sbdrNumber(search);

						if (sbdrnr != null)
							result = bedrijfRepository.findRemovedBedrijvenViewOfBedrijf_SearchSbdrNummer(bedrijfId, view, SearchUtil.convertDbSearchString(sbdrnr) + "%", pageable);
						else
							result = bedrijfRepository.findRemovedBedrijvenViewOfBedrijf_SearchName(bedrijfId, view, SearchUtil.convertDbSearchString(search) + "%", pageable);
					} else {
						//						if (SearchUtil.isKvKNumber(search))
						//							result = bedrijfRepository.findRemovedBedrijvenViewOfBedrijf_SearchKvkNummer(bedrijfId, view, SearchUtil.convertDbSearchString(search) + "%", pageable);
						//						else
						result = bedrijfRepository.findRemovedBedrijvenViewOfBedrijf_SearchNummer(bedrijfId, view, SearchUtil.convertDbSearchString(search) + "%", pageable);
					}
				}
			} else result = bedrijfRepository.findRemovedBedrijvenViewOfBedrijf(bedrijfId, view, pageable);

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Bedrijf findSbdr() throws DataServiceException {
		try {
			return bedrijfRepository.findSbdr();
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public String findSbiOmschrijvingOfBedrijf(Integer bedrijfId) throws DataServiceException {
		try {
			String omschrijving = bedrijfRepository.findSbiOmschrijvingOfBedrijf(bedrijfId);
			if (omschrijving != null) return omschrijving;
			else throw new DataServiceException("No SBI description found for company " + bedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Object[]> findSearchResults(String search, Boolean vermelder, Pageable pageable) throws DataServiceException {
		Page<Object[]> result = null;

		try {
			if (StringUtil.isNotEmptyOrNull(search)) {
				if (search.length() > 2) {
					if (SearchUtil.isAlfanumeric(search)) {
						String sbdrnr = SearchUtil.sbdrNumber(search);
						String venr = SearchUtil.veNumber(search);
						String monr = SearchUtil.moNumber(search);
						String repnr = SearchUtil.repNumber(search);

						if (vermelder) {
							if (sbdrnr != null) {
								result = bedrijfRepository.findSearchResultsVermelder(SearchUtil.convertDbSearchString(sbdrnr) + "%", pageable);
							} else if (venr != null) {
								result = bedrijfRepository.findSearchResultsVermelder(SearchUtil.convertDbSearchString(venr) + "%", pageable);
							} else if (monr != null) {
								result = bedrijfRepository.findSearchResultsVermelder(SearchUtil.convertDbSearchString(monr) + "%", pageable);
							} else if (repnr != null) {
								result = bedrijfRepository.findSearchResultsVermelder(SearchUtil.convertDbSearchString(repnr) + "%", pageable);
							} else
								result = bedrijfRepository.findSearchResultsVermelder(SearchUtil.convertDbSearchString(search) + "%", pageable);
						} else {
							if (sbdrnr != null) {
								result = bedrijfRepository.findSearchResultsVermeldde(SearchUtil.convertDbSearchString(sbdrnr) + "%", pageable);
							} else if (venr != null) {
								result = bedrijfRepository.findSearchResultsVermeldde(SearchUtil.convertDbSearchString(venr) + "%", pageable);
							} else if (monr != null) {
								result = bedrijfRepository.findSearchResultsVermeldde(SearchUtil.convertDbSearchString(monr) + "%", pageable);
							} else if (repnr != null) {
								result = bedrijfRepository.findSearchResultsVermeldde(SearchUtil.convertDbSearchString(repnr) + "%", pageable);
							} else
								result = bedrijfRepository.findSearchResultsVermeldde(SearchUtil.convertDbSearchString(search) + "%", pageable);
						}
					} else {
						if (vermelder)
							result = bedrijfRepository.findSearchResultsVermelder(SearchUtil.convertDbSearchString(search) + "%", pageable);
						else
							result = bedrijfRepository.findSearchResultsVermeldde(SearchUtil.convertDbSearchString(search) + "%", pageable);
					}
				}
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Bedrijf> findVermeldeBedrijvenOfBedrijfWithOverdueMeldingen(Integer bedrijfId, int overdueDays) throws DataServiceException {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -overdueDays);
			Date overdueDate = cal.getTime();

			return bedrijfRepository.findVermeldeBedrijvenOfBedrijf(bedrijfId, overdueDate);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	public List<Bedrijf> findnewNotifiedCompaniesOfCompany(Integer bedrijfId) throws DataServiceException {
		try {
			return bedrijfRepository.findnewNotifiedCompaniesOfCompany(bedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Bedrijf save(Bedrijf bedrijf) throws DataServiceException {
		try {
			//Bedrijf nbedrijf = bedrijfRepository.save(bedrijf);

			return saveBedrijf(bedrijf);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	private Page<Object[]> findActiveMonitoringOfBedrijfCriteria(Integer bedrijfId, String search, ESearchActiveMonitoringOfBedrijf searchType, Pageable pageable) {
		EntityManager em = entityManager; // Factory.createEntityManager();
		//CriteriaBuilder cb = em.getCriteriaBuilder();

		// MBR 27-08-2015 'INI' + 'INB' added
		String query = "SELECT SUM(CASE WHEN (b.bedrijfId=m2.bedrijfByMeldingOverBedrijfId.bedrijfId AND(m2.meldingStatus.code NOT IN ('NOK', 'INI', 'INB', 'DEL' , 'AFW', 'BLK'))) THEN 1 ELSE 0 END) AS aantalMeldingen, " +
				"m " +
				"FROM Bedrijf b, " +
				"Bedrijf b2 " +
				"JOIN b2.monitoringsForMonitoringVanBedrijfId m " +
				"LEFT OUTER JOIN b.meldingsForMeldingOverBedrijfId m2 " +
				"WHERE m.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
				"AND m.monitoringStatus.code = 'ACT' " +
				"AND b.kvKnummer = b2.kvKnummer ";

		if (searchType.equals(ESearchActiveMonitoringOfBedrijf.KVKNUMMER)) {
			query += "AND m.bedrijfByMonitoringVanBedrijfId.kvKnummer LIKE :search ";
		} else if (searchType.equals(ESearchActiveMonitoringOfBedrijf.MOREFERENTIE)) {
			query += "AND m.referentieNummerIntern LIKE :search ";
		} else if (searchType.equals(ESearchActiveMonitoringOfBedrijf.SBDRNUMMER)) {
			query += "AND m.bedrijfByMonitoringVanBedrijfId.sbdrNummer LIKE :search ";
		} else if (searchType.equals(ESearchActiveMonitoringOfBedrijf.NAAM)) {
			query += "AND m.bedrijfByMonitoringVanBedrijfId.bedrijfsNaam LIKE :search ";
		} else if (searchType.equals(ESearchActiveMonitoringOfBedrijf.NUMMER)) {
			query += "AND (m.bedrijfByMonitoringVanBedrijfId.bedrijfsNaam LIKE :search " +
					"OR m.bedrijfByMonitoringVanBedrijfId.kvKnummer LIKE :search " +
					"OR m.bedrijfByMonitoringVanBedrijfId.sbdrNummer LIKE :search " +
					"OR m.referentieNummerIntern LIKE :search) ";
		}

		query += "GROUP BY m.monitoringId ";

		String orders = null;
		if (pageable.getSort() != null) {
			Iterator<Order> it = pageable.getSort().iterator();
			while (it.hasNext()) {
				if (orders == null) orders = "ORDER BY ";
				else orders += ", ";
				Order order = it.next();
				orders += order.getProperty() + (order.getDirection().equals(Direction.ASC) ? " ASC " : " DESC ");
			}
		}

		if (orders != null) query += orders;

		Query cq = em.createQuery(query);
		cq.setFirstResult((pageable.getPageNumber()) * pageable.getPageSize());
		cq.setMaxResults(pageable.getPageSize());
		if (!searchType.equals(ESearchActiveMonitoringOfBedrijf.NOTHING)) cq.setParameter("search", search);
		cq.setParameter("bedrijfId", bedrijfId);

		List<Object[]> contents = cq.getResultList();
		
		int items = contents.size();

		return new PageImpl<Object[]>(contents, pageable, items);
	}

	private Bedrijf saveBedrijf(Bedrijf bedrijf) throws DataServiceException {
		Bedrijf result = null;

		if (bedrijf != null) {
			try {
				if (bedrijf.getBedrijfId() != null) {
					Bedrijf existingBedrijf = findByBedrijfId(bedrijf.getBedrijfId());

					if (CompareUtil.compareBedrijf(existingBedrijf, bedrijf)) {
						// data is equal, so only update DatumWijziging
						existingBedrijf.setDatumWijziging(DateUtil.getCurrentTimestamp());
						result = bedrijfRepository.save(existingBedrijf);
					} else {
						// copy current bedrijf to history
						BedrijfHistorie bedrijfHistorie = ConvertUtil.transformToBedrijfHistorie(existingBedrijf);
						// set referentie to current bedrijf
						bedrijfHistorie.setBedrijf(existingBedrijf);
						bedrijfHistorieRepository.save(bedrijfHistorie);

						// update current bedrijf
						// copy identifier for copy all props
						// copy not needed here?
						//bedrijf.setBedrijfId(existingBedrijf.getBedrijfId());
						//existingBedrijf = ConvertUtil.copyBedrijf(bedrijf, existingBedrijf);
						//result = bedrijfRepository.save(existingBedrijf);
						result = bedrijfRepository.save(bedrijf);
					}
				} else {
					// create new bedrijf
					result = bedrijfRepository.save(bedrijf);
				}

			} catch (DataServiceException e) {
				throw new DataServiceException("Error in Bedrijf database transaction: " + e.getMessage());
			} catch (Exception e) {
				LOGGER.error("Error saving bedrijf: " + e.getMessage());
				throw new DataServiceException("Error saving Bedrijf");
			}

			return result;
		} else throw new DataServiceException("Cannot save Bedrijf as null");

	}
}
