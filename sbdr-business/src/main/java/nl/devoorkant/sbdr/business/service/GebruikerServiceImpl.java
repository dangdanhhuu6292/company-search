package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.ActivatieCodeTransfer;
import nl.devoorkant.sbdr.business.transfer.AlertOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikerBedrijfTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikerTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikersDetails;
import nl.devoorkant.sbdr.business.transfer.LoginAllowed;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.util.CompareUtil;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.business.util.SerialNumber;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.EBedrijfStatus;
import nl.devoorkant.sbdr.data.util.EKlantStatus;
import nl.devoorkant.sbdr.data.util.ERol;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.annotation.Resource;

@Service("gebruikerService")
@Transactional(readOnly = true)
public class GebruikerServiceImpl implements GebruikerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GebruikerServiceImpl.class);

	@Autowired
	private AlertService alertService;

	@Autowired
	private BedrijfDataService bedrijfDataService;
	
	@Autowired
	private BedrijfManagedDataService bedrijfManagedDataService;

	@Autowired
	private BedrijfService bedrijfService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private GebruikerDataService gebruikerDataService;

	@Autowired
	private KlantDataService klantDataService;

	@Autowired
	private MobileGebruikerDataService mobileGebruikerDataService;

	@Autowired
	private MobileGebruikerService mobileGebruikerService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RolDataService rolDataService;

	@Autowired
	private SupportService supportService;

	@Autowired
	private WachtwoordDataService wachtwoordService;

	@Autowired
	private WachtwoordStatusDataService wachtwoordStatusDataService;

	@Autowired
	private WebTokenService webTokenService;

	@Override
	@Transactional
	public ActivatieCodeTransfer findActivatieCodeOfGebruiker(String gebruikerNaam, String activatieCode) throws ServiceException {
		boolean isBedrijfManaged = false;
		String bedrijfId = null;
		
		try {
		 	Gebruiker bestaandegebruiker = gebruikerDataService.findByActivatieCode(activatieCode);
		 	if (bestaandegebruiker == null) {
		 		bestaandegebruiker = gebruikerDataService.findByActivatieCodeBedrijfManaged(activatieCode);
		 		// if user is active + ww is actief then set isBedrijfManaged to force other user activation screen
		 		if (bestaandegebruiker.getActief() && bestaandegebruiker.getWachtwoord() != null && bestaandegebruiker.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE))
		 			isBedrijfManaged = true;
		 		if (bestaandegebruiker.getBedrijvenManagedDoorGebruikerId() != null) {
		 			for (BedrijfManaged bedrijfManaged : bestaandegebruiker.getBedrijvenManagedDoorGebruikerId())
		 				if (bedrijfManaged.getActivatieCode() != null && bedrijfManaged.getActivatieCode().equals(activatieCode))
		 					bedrijfId = ObfuscatorUtils.obfuscateInteger(bedrijfManaged.getBedrijfBedrijfId());
		 		}
		 	}
		 	
		 	if (bestaandegebruiker != null && bestaandegebruiker.getGebruikersNaam().equals(gebruikerNaam))
		 		return new ActivatieCodeTransfer(isBedrijfManaged, bedrijfId);
		 	else 
		 		return null;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public ErrorService activateGebruiker(String gebruikersNaam, String activatieCode, String password) throws ServiceException {

		try {
			if (activatieCode != null) {
				Gebruiker bestaandegebruiker = gebruikerDataService.findByActivatieCode(activatieCode);
				if (bestaandegebruiker != null && bestaandegebruiker.getGebruikersNaam().equals(gebruikersNaam)) {

					if (bestaandegebruiker.getWachtwoord() != null && !bestaandegebruiker.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL)) {
						LOGGER.warn("User not activated. User status wrong.");
						return new ErrorService(ErrorService.USER_NOT_ACTIVATED);
					}
	
					Wachtwoord wachtwoord = bestaandegebruiker.getWachtwoord();
	
					// Password may be null on activation of Klant which already has a password set
					if (password != null) {
						if (wachtwoord == null) wachtwoord = new Wachtwoord();
	
						// encode password
						wachtwoord.setWachtwoord(passwordEncoder.encode(password));
					}
	
					wachtwoord.setActivatieCode(null);
					wachtwoord.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
	
					wachtwoord = wachtwoordService.save(wachtwoord);
					bestaandegebruiker.setWachtwoord(wachtwoord);
	
					bestaandegebruiker.setActief(true);
	
					gebruikerDataService.save(bestaandegebruiker);
				} else {
					bestaandegebruiker = gebruikerDataService.findByActivatieCodeBedrijfManaged(activatieCode);
					if (bestaandegebruiker == null || !bestaandegebruiker.getGebruikersNaam().equals(gebruikersNaam)) {
						LOGGER.warn("User not activated. Code " + activatieCode + " not found.");
						return new ErrorService(ErrorService.USER_NOT_ACTIVATED);
					}	
					
					if (bestaandegebruiker.getBedrijvenManagedDoorGebruikerId() != null) {
						for (BedrijfManaged bedrijfManaged : bestaandegebruiker.getBedrijvenManagedDoorGebruikerId()) {
							if (bedrijfManaged.getActivatieCode().equals(activatieCode)) {
								bedrijfManaged.setActivatieCode(null);
								bedrijfManaged.setActief(true);
								bedrijfManaged.setGebruikerStatus(EKlantStatus.ACTIEF.getCode());
								gebruikerDataService.save(bestaandegebruiker);
								break;
							}
						}
					}
				}
				
				if (bestaandegebruiker != null) {
					Wachtwoord wachtwoord = bestaandegebruiker.getWachtwoord();
					
					if (wachtwoord.getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL)) {
						// Password may be null on activation of Klant which already has a password set
						if (password != null) {
							if (wachtwoord == null) wachtwoord = new Wachtwoord();
		
							// encode password
							wachtwoord.setWachtwoord(passwordEncoder.encode(password));
						}
		
						wachtwoord.setActivatieCode(null);
						wachtwoord.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
		
						wachtwoord = wachtwoordService.save(wachtwoord);
					}
				}
				
				return null;
			} else return new ErrorService(ErrorService.USER_NOT_ACTIVATED);
		} catch (Exception e) {
			throw new ServiceException("Cannot activateGebruiker: " + e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public ErrorService changeWachtwoordGebruiker(Integer gebruikerId, Integer gebruikerIdIngelogd, Integer bedrijfId, String bestaandWachtwoord, String nieuwWachtwoord, boolean resetWachtwoord) throws ServiceException {

		try {
			Gebruiker gebruikerIngelogd = gebruikerDataService.findByGebruikerid(gebruikerIdIngelogd);
			
			// user who wants to change password of user is of same company as user ANS is user itself OR must have proper rights
			if (gebruikerIngelogd != null && nieuwWachtwoord != null && bestaandWachtwoord != null) {
				Gebruiker bestaandegebruiker = gebruikerDataService.findById(gebruikerId);
				if (bestaandegebruiker == null) {
					LOGGER.warn("Cannot change wachtwoord. GebruikerId " + gebruikerId + " not found.");
					return new ErrorService(ErrorService.CANNOT_CHANGE_WACHTWOORD);
				} else {
					if (gebruikerIngelogd.getGebruikerId().equals(gebruikerId) || 
							(bestaandegebruiker.getBedrijf() != null && bestaandegebruiker.getBedrijfBedrijfId().equals(bedrijfId) && hasRightToDo(gebruikerIdIngelogd, bedrijfId, EBevoegdheid.KLANTGEBRUIKER_BEHEER))
							|| hasRightToDo(gebruikerIdIngelogd, bedrijfId, EBevoegdheid.SBDRGEBRUIKER_BEHEER)) {
					
						Wachtwoord bestaandwachtwoord = bestaandegebruiker.getWachtwoord();
	
						boolean changeExistingPassword = false;
						boolean passwordMatch = false;
						if (bestaandwachtwoord.getWachtwoord() != null) {
							passwordMatch = passwordEncoder.matches(bestaandWachtwoord, bestaandwachtwoord.getWachtwoord());
							changeExistingPassword = !resetWachtwoord && bestaandwachtwoord.getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
						}
	
						// if reset + status pwd = reset
						// or if not reset + status pwd = active
						// then pwd can be changed
						if ((resetWachtwoord && bestaandwachtwoord.getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_RESET)) || (changeExistingPassword && passwordMatch)) {
							bestaandwachtwoord.setDatumLaatsteWijziging(new Date());
	
							bestaandwachtwoord.setWachtwoord(passwordEncoder.encode(nieuwWachtwoord));
	
							bestaandwachtwoord.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
	
							wachtwoordService.save(bestaandwachtwoord);
	
							emailService.sendPasswordChangedEmail(bestaandegebruiker, bedrijfId);
						} else {
							LOGGER.warn("Cannot change wachtwoord. Wachtwoordstatus not valid for change " + bestaandwachtwoord.getWachtwoordStatusCode() + " not found. Reset pwd: " + resetWachtwoord);
							if (changeExistingPassword && !passwordMatch) {
								return new ErrorService(ErrorService.WRONG_PASSWORD);
							} else {
								return new ErrorService(ErrorService.CANNOT_CHANGE_WACHTWOORD);
							}
						}
					} else {
						LOGGER.warn("Cannot change wachtwoord. GebruikerId " + gebruikerId + " no rights.");
						return new ErrorService(ErrorService.CANNOT_CHANGE_WACHTWOORD);
					}
				}

				return null;
			} else return new ErrorService(ErrorService.CANNOT_CHANGE_WACHTWOORD);
		} catch (Exception e) {
			throw new ServiceException("Cannot change wachtwoord: " + e.getMessage());
		}
	}

	/**
	 * Deletes the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object identified by the presented id.
	 *
	 * @param id a Long representing the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object to delete
	 * @param bedrijfId a Long representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object where the user belongs to
	 *
	 * @throws ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	@Override
	@Transactional
	public ErrorService deleteGebruiker(Integer id, Integer bedrijfId) throws ServiceException {
		try {
			ErrorService error = supportService.assignAllSupportTicketsOfGebruikerToKlant(id, bedrijfId);

			if (error != null) {
				return error;
			} else {
				error = alertService.assignAllAlertsOfGebruikerToKlant(id, bedrijfId);

				if(error!=null){
					return error;
				}
			}
			
			Gebruiker gebruiker = gebruikerDataService.findById(id);

			if (gebruiker != null) {
				// assign all open tickets to Klant of user
				Klant klant = klantDataService.findKlantOfGebruikerByGebruikerId(gebruiker.getGebruikerId(), bedrijfId);

				// Remove user of company
				if (gebruiker.getBedrijf() != null && gebruiker.getBedrijfBedrijfId().equals(bedrijfId)) {
					gebruiker.setActief(false);
					// set gebruikersnaam null to enable reuse of the same gebruikersnaam/email in future gebruiker objects
					gebruiker.setGebruikersNaam(null);
					gebruikerDataService.save(gebruiker);
	
					//Remove all MCKs of this user
					List<MobileGebruiker> MCKs = mobileGebruikerService.findByGebruikerId(id);
					if (MCKs != null) {
						for (MobileGebruiker MCK : MCKs) {
							mobileGebruikerService.removeMobileGebruikerRecord(MCK.getMobileClientKey());
						}
					}
				} 
				// Remove bedrijfmanaged user
				else if (gebruiker.getBedrijvenManagedDoorGebruikerId() != null) {
					for (BedrijfManaged bedrijfManaged : gebruiker.getBedrijvenManagedDoorGebruikerId()) {
						if (bedrijfManaged.getBedrijfBedrijfId().equals(bedrijfId)) {
							bedrijfManaged.setDatumVerwijderd(new Date());
							bedrijfManaged.setActief(false);
							bedrijfManaged.setGebruikerStatus(EKlantStatus.VERWIJDERD.getCode());
							bedrijfManagedDataService.save(bedrijfManaged);
						}
					}
				}

				return null;
			} else return new ErrorService(ErrorService.USERNAME_NOT_EXISTS);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PageTransfer<GebruikerTransfer> findActiveGebruikersOfKlantGebruiker(Integer klantGebruikerId, Integer bedrijfId, Pageable pageable) throws ServiceException {
		Page<Gebruiker> gebruikers;

		try {
			Klant klant = klantDataService.findKlantOfGebruikerByGebruikerId(klantGebruikerId, bedrijfId);
			gebruikers = gebruikerDataService.findActiveGebruikersOfKlantGebruiker(klant.getGebruikerId(), pageable);

			return ConvertUtil.convertPageToGebruikerPageTransfer(gebruikers, klant.getBedrijf(), klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode()), !klant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode()), klant.getBedrijf().getTelefoonnummer());
		} catch (DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Gebruiker findByGebruikerId(Integer gebruikerId) throws ServiceException {
		try {
			return gebruikerDataService.findById(gebruikerId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<GebruikerBedrijfTransfer> findGebruikerBedrijvenByGebruikerId(Integer gebruikerId, Integer currentBedrijfId) throws ServiceException {
		List<GebruikerBedrijfTransfer> results = null;
		try {
			Gebruiker gebruiker = findByGebruikerId(gebruikerId);
			
			if (gebruiker != null) {
				results = new ArrayList<GebruikerBedrijfTransfer>();
				if (gebruiker.getBedrijf() != null && gebruiker.getBedrijf().isActief()) {
					Bedrijf bedrijf = gebruiker.getBedrijf();
					Klant klant = findKlantgebruikerByBedrijfId(bedrijf.getBedrijfId());
					if (bedrijf.isActief() && klant != null) {
						// add own company of user
						results.add(new GebruikerBedrijfTransfer(bedrijf.getBedrijfId(), bedrijf.getKvKnummer(), bedrijf.getBedrijfsNaam(), false, bedrijf.getBedrijfId().equals(currentBedrijfId)));						
					}
				}
				
				if (gebruiker.getBedrijvenManagedDoorGebruikerId() != null) {
					for (BedrijfManaged bedrijfManaged : gebruiker.getBedrijvenManagedDoorGebruikerId()) {
						Bedrijf managedBedrijf = bedrijfManaged.getBedrijf();
						Klant managedKlant = findKlantgebruikerByBedrijfId(managedBedrijf.getBedrijfId());
						if (bedrijfManaged.isActief() && managedKlant != null && 
								bedrijfManaged.isActief() && bedrijfManaged.getGebruikerStatus().equals(EKlantStatus.ACTIEF.getCode())) {
							// add managedBedrijf of user
							results.add(new GebruikerBedrijfTransfer(managedBedrijf.getBedrijfId(), managedBedrijf.getKvKnummer(), managedBedrijf.getBedrijfsNaam(), true, managedBedrijf.getBedrijfId().equals(currentBedrijfId)));
						}
					}
 				}
			}
			
			return results;
		} catch (Exception e) {
			throw new ServiceException(e);
		}		
	}

	@Override
	public Klant findKlantOfBedrijfByBedrijfIdAndStatusCode(Integer bId, Collection<String> statusCodes) throws ServiceException {
		try {
			return klantDataService.findKlantOfBedrijfByBedrijfIdAndStatusCode(bId, statusCodes);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Finds Klant by gebruikerId of Gebruiker
	 */
	@Override
	public Klant findKlantgebruikerByBedrijfId(Integer bedrijfId) throws ServiceException {
		try {
			return klantDataService.findKlantOfBedrijfByBedrijfId(bedrijfId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Finds Klant by gebruikerId of Gebruiker
	 */
//	@Override
//	public Klant findKlantgebruikerById(Integer gebruikerId) throws ServiceException {
//		try {
//			return klantDataService.findKlantOfGebruikerByGebruikerId(gebruikerId);
//		} catch (Exception e) {
//			throw new ServiceException(e);
//		}
//	}

	@Override
	public Gebruiker findSbdrGebruiker() throws ServiceException {
		try {
			return gebruikerDataService.findSbdrGebruiker();
		} catch (DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public GebruikerTransfer findTransferByGebruikerId(Integer gebruikerId, Integer bedrijfId) throws ServiceException {
		try {
			boolean isKlant = false;
			boolean isProspect = false;
			boolean isAdresOk = true;
			String klantTelefoonNummer = null;

			try {
				Klant klant = klantDataService.findByGebruikerId(gebruikerId);

				if (klant != null) {
					isKlant = true;
					if (klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode())) isProspect = true;
					else if (klant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode())) isAdresOk = false;
				}
			} catch (DataServiceException e) {
				// error in finding klant

			}
			Gebruiker gebruiker = gebruikerDataService.findById(gebruikerId);

			GebruikerTransfer gebruikerTransfer = null;
			if (gebruiker != null) {
				boolean isActionsPresent = false;
				
				
				//Klant klant = klantDataService.findKlantOfGebruikerByGebruikerId(gebruiker.getGebruikerId(), bedrijfId);
				//if (klant != null) {
				//	klantTelefoonNummer = bedrijfDataService.findByBedrijfId(klant.getBedrijfBedrijfId()).getTelefoonnummer();
				//}
				
				// try to find bedrijf of gebruiker, only when gebruiker related to klant company
				Bedrijf bedrijf = null;
				boolean gebruikerAllowedToBedrijf = false;
				try {
					if (bedrijfId != null)
						bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);
					if (bedrijf != null) {
						if (!(gebruiker.getBedrijf() != null && gebruiker.getBedrijfBedrijfId().equals(bedrijf.getBedrijfId()))) {
							for (BedrijfManaged bedrijfManaged : gebruiker.getBedrijvenManagedDoorGebruikerId()) {
								if (bedrijfManaged.getBedrijfBedrijfId().equals(bedrijf.getBedrijfId()))
									gebruikerAllowedToBedrijf = true;
							}
						} else
							gebruikerAllowedToBedrijf = true;
					} else {
						// no bedrijf, could be 'bedrijfmanager' not strictly related to a company/klant, so then pick first managed bedrijf
						if (!gebruiker.getBedrijvenManagedDoorGebruikerId().isEmpty()) {
							BedrijfManaged bedrijfManaged = gebruiker.getBedrijvenManagedDoorGebruikerId().iterator().next();
							bedrijf = bedrijfManaged.getBedrijf();
							gebruikerAllowedToBedrijf = true;
						}
					}

				} catch (Exception e) {
					LOGGER.error("Error fetching bedrijf of gebruiker: " + e.getMessage());
				}
				
				if (bedrijf != null)
					klantTelefoonNummer = bedrijfDataService.findByBedrijfId(bedrijf.getBedrijfId()).getTelefoonnummer();				
				
				if (gebruikerAllowedToBedrijf) {
					Pageable pageable = new PageRequest(0, 10);
					PageTransfer<AlertOverviewTransfer> alerts = bedrijfService.findActiveAlertsOfBedrijf(bedrijfId, gebruikerId, "", pageable);
					if (alerts != null && alerts.getContent() != null && alerts.getContent().size() > 0)
						isActionsPresent = true;
				}
				
				if (gebruikerAllowedToBedrijf || bedrijf == null)
					gebruikerTransfer = ConvertUtil.createGebruikerTransferFromGebruiker(gebruiker, bedrijf, klantTelefoonNummer, isKlant, isProspect, isAdresOk, true, isActionsPresent);
			}

			return gebruikerTransfer;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public GebruikerTransfer findTransferByGebruikersnaam(String gebruikersnaam, Integer bedrijfId) throws ServiceException {
		try {
			boolean isKlant = false;
			boolean isProspect = false;
			boolean isAdresOk = true;
			String klantTelefoonNummer = null;

			// active Gebruiker
			Gebruiker gebruiker = gebruikerDataService.findByGebruikersnaam(gebruikersnaam);

			GebruikerTransfer gebruikerTransfer = null;
			if (gebruiker != null) {
				boolean isActionsPresent = false;
				
				Bedrijf bedrijf = null;
				boolean gebruikerAllowedToBedrijf = false;
				try {
					if (bedrijfId != null)
						bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);
					
					Klant klant = null;
					
					if (bedrijf != null) {
						try {
							// active Klant of Bedrijf
							klant = klantDataService.findKlantOfBedrijfByBedrijfId(bedrijf.getBedrijfId());
						} catch (DataServiceException e) {
							throw new ServiceException(ErrorService.GENERAL_FAILURE);
						}	
						
						if (!(gebruiker.getBedrijf() != null && gebruiker.getBedrijfBedrijfId().equals(bedrijf.getBedrijfId()))) {
							for (BedrijfManaged bedrijfManaged : gebruiker.getBedrijvenManagedDoorGebruikerId()) {
								if (bedrijfManaged.getBedrijfBedrijfId().equals(bedrijf.getBedrijfId()))
									gebruikerAllowedToBedrijf = true;
							}
						} else
							gebruikerAllowedToBedrijf = true;
					} 
					// default Bedrijf if available
					else {
						try {
							// active Klant of Gebruiker
							klant = klantDataService.findByGebruikersNaam(gebruikersnaam);
						} catch (DataServiceException e) {
							throw new ServiceException(ErrorService.GENERAL_FAILURE);
						}
						
						if (gebruiker.getBedrijf() != null) {
							if (gebruiker.getBedrijf().isActief()) {
								bedrijf = gebruiker.getBedrijf();
								gebruikerAllowedToBedrijf = true;
							}
						}
						// no bedrijf, could be 'bedrijfmanager' not strictly related to a company/klant, so then pick first managed bedrijf
						else if (!gebruiker.getBedrijvenManagedDoorGebruikerId().isEmpty()) {
							BedrijfManaged bedrijfManaged = gebruiker.getBedrijvenManagedDoorGebruikerId().iterator().next();
							bedrijf = bedrijfManaged.getBedrijf();
							gebruikerAllowedToBedrijf = true;
						}						
					}
					
					// Get Klant props
					if (klant != null) {
						isKlant = true;
						if (klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode())) isProspect = true;
						else if (klant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode())) isAdresOk = false;
					}					

				} catch (Exception e) {
					LOGGER.error("Error fetching bedrijf of gebruiker: " + e.getMessage());
				}
				
				if (bedrijf != null)
					klantTelefoonNummer = bedrijfDataService.findByBedrijfId(bedrijf.getBedrijfId()).getTelefoonnummer();
				
				if (gebruikerAllowedToBedrijf) {
					Pageable pageable = new PageRequest(0, 10);
					PageTransfer<AlertOverviewTransfer> alerts = bedrijfService.findActiveAlertsOfBedrijf(bedrijfId, gebruiker.getGebruikerId(), "", pageable);
					if (alerts != null && alerts.getContent() != null && alerts.getContent().size() > 0)
						isActionsPresent = true;
				}
				
				if (gebruikerAllowedToBedrijf || bedrijf == null)				
					gebruikerTransfer = ConvertUtil.createGebruikerTransferFromGebruiker(gebruiker, bedrijf, klantTelefoonNummer, isKlant, isProspect, isAdresOk, true, isActionsPresent);
			}

			return gebruikerTransfer;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public ErrorService forgotWachtwoord(String gebruikersNaam) throws ServiceException {
		ErrorService result = null;

		try {
			if (StringUtil.isNotEmptyOrNull(gebruikersNaam)) {
				Gebruiker gebruiker = gebruikerDataService.findByGebruikersnaam(gebruikersNaam);

				if (gebruiker != null) {
					if (gebruiker.getActief()) {
						Wachtwoord wachtwoord = gebruiker.getWachtwoord(); // Wachtwoord cannot be null

						if (wachtwoord.getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE) || wachtwoord.getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_BLOCKED)) {
							//Normal flow, user has forgotten password and requests a reset
							wachtwoord.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_RESET);
							wachtwoord.setForgotPasswordEmailSent(false); //At the start of this process, the forgotPasswordEmailSent field should be false
							wachtwoord.setActivatieCode(generateActivatieCodeEmail());
							wachtwoord.setDatumLaatsteWijziging(new Date());

							wachtwoordService.save(wachtwoord);
							emailService.sendPasswordForgottenEmail(gebruiker);
						} else {
							if (wachtwoord.getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_RESET)) {
								//If there's already a reset requested
								Calendar cal = Calendar.getInstance();
								cal.setTime(new Date());
								cal.add(Calendar.HOUR, -1);
								Date oneHourPast = cal.getTime();

								if (wachtwoord.getDatumLaatsteWijziging().after(oneHourPast)) {
									//Users have to wait at least an hour before submitting a new request
									LOGGER.error("Password reset request too close to previous request for user " + gebruikersNaam);
									result = new ErrorService(ErrorService.PASSWORD_REQUEST_TOO_SOON);
								} else {

									wachtwoord.setDatumLaatsteWijziging(new Date()); //When a new reset request is made, the datumLaatsteWijziging has to be updated to prevent spam
									wachtwoord.setForgotPasswordEmailSent(false); //At the start of this process, the forgotPasswordEmailSent field should be false

									wachtwoordService.save(wachtwoord);
									emailService.sendPasswordForgottenEmail(gebruiker);

									LOGGER.info("Password reset request email has been sent again for user " + gebruikersNaam);
									result = new ErrorService(ErrorService.PASSWORD_REQUEST_RESENT);
								}
							} else {
								LOGGER.error("Password not active for user: " + gebruikersNaam);
								result = new ErrorService(ErrorService.PASSWORD_STATUS_MISMATCH);
							}
						}
					} else {
						LOGGER.error("User not active: " + gebruikersNaam);
						result = new ErrorService(ErrorService.USER_NOT_ACTIVATED);
					}
				} else {
					LOGGER.error("No Gebruiker found by the name of: " + gebruikersNaam);
					result = new ErrorService(ErrorService.USERNAME_NOT_EXISTS); // TODO: Do we want to return this to a user? (people can guess usernames)
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw (new ServiceException(e));
		}

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasRightToDo(Integer gebruikerId, Integer bedrijfId, EBevoegdheid checkBevoegdheid) throws ServiceException {
		boolean result = false;

		try {
			Klant klant = klantDataService.findKlantOfGebruikerByGebruikerId(gebruikerId, bedrijfId);
			Gebruiker gebruiker = gebruikerDataService.findById(gebruikerId);

			if (gebruiker != null && gebruiker.getActief()) {
				Set<Rol> rollen = null;
				
				if (bedrijfId != null) {
					// get roles to check for company
					if (bedrijfId.equals(gebruiker.getBedrijfBedrijfId()))
						rollen = gebruiker.getRollen();
					else if (gebruiker.getBedrijvenManagedDoorGebruikerId() != null) {
						for (BedrijfManaged bedrijfManaged : gebruiker.getBedrijvenManagedDoorGebruikerId()) {
							if (bedrijfManaged.getBedrijfBedrijfId().equals(bedrijfId) && bedrijfManaged.isActief())
								rollen = bedrijfManaged.getRollen();
						}
					}
				} else
					rollen = gebruiker.getRollen();
				
				// may be sbdr user...
				boolean checkForSbdrRoles = false;
				if (rollen == null && klant == null) {
					rollen = gebruiker.getRollen();
					checkForSbdrRoles = true;
				}
				
				LOGGER.info("Printing roles, bevoegdheid to check is " + checkBevoegdheid.getOmschrijving() + " for userId " + gebruikerId);
				String roles = "";
				boolean first = true;
				for (Rol r : rollen) {
					if (!first) {
						roles += ", " + r.getCode();
					} else {
						roles = r.getCode();
						first = false;
					}
				}
				LOGGER.info("Roles: " + roles);
				
				// if should be sbdr user is not true, result = false, else check for bevoegdheid
				if (checkForSbdrRoles && !(CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))))
					result = false;
				else {
					switch (checkBevoegdheid) {
						case KLANT_BEHEER:
							if (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))))
								result = true;
							break;
						case MELDING_BEHEER:
							if (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))))
								result = true;
							break;
						case ACTIVEREN_ACCOUNT:
							if (CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true))) result = true;
							break;
						case KLANTGEGEVENS_MUTEREN:
							if (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true)) ||
									(klant != null && EKlantStatus.isAllowedToLogin(klant.getKlantStatusCode()) && 
											(CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true)) || 
													CompareUtil.containsRol(rollen, ERol.HOOFD.getRolObject(true)))))
								result = true;
							break;
						case BEDRIJFGEGEVENS_MUTEREN: // only admin because this is also used for admin sbdr check....
							if (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))))
								result = true;
							break;
						case MELDING_INVOEREN:
							if (klant != null && EKlantStatus.isAllowedToLogin(klant.getKlantStatusCode()) && CompareUtil.containsRol(rollen, ERol.REGISTRATIESTOEGESTAAN.getRolObject(true)) &&
									(CompareUtil.containsRol(rollen, ERol.GEBRUIKER.getRolObject(true)) || 
											CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true)) || 
											CompareUtil.containsRol(rollen, ERol.HOOFD.getRolObject(true)) || 
											CompareUtil.containsRol(rollen, ERol.MANAGED.getRolObject(true))))
								result = true;
							break;
						case MONITORING_TOEVOEGEN:
							if (EKlantStatus.isAllowedToLogin(klant.getKlantStatusCode()) && 
									(CompareUtil.containsRol(rollen, ERol.GEBRUIKER.getRolObject(true)) || 
											CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true)) || 
											CompareUtil.containsRol(rollen, ERol.HOOFD.getRolObject(true)) || 
										CompareUtil.containsRol(rollen, ERol.MANAGED.getRolObject(true))))
								result = true;
							break;
						case RAPPORT_INZIEN:
							if (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true)) || 
									(klant != null && EKlantStatus.isAllowedToLogin(klant.getKlantStatusCode()) && 
											(CompareUtil.containsRol(rollen, ERol.GEBRUIKER.getRolObject(true)) || 
													CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true)) || 
													CompareUtil.containsRol(rollen, ERol.HOOFD.getRolObject(true)) || 
													CompareUtil.containsRol(rollen, ERol.MANAGED.getRolObject(true)))
									))
								result = true;
							break;
						case EXACTONLINE:
							if (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))) result = true;
							break;
						case WEBSITEPARAM:
							if (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))) result = true;
							break;
						case SUPPORT_ADMIN:
							if (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))))
								result = true;
							break;
						case ALERT_ADMIN_BEZWAAR:
							if (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))) result = true;
							break;
						case ADMIN_SBDR_HOOFD:
							if (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))) result = true;
							break;
						case SBDR_MEDEWERKER:
							if (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))))
								result = true;
							break;
						case HOOFD_OF_KLANT:
							if (CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.HOOFD.getRolObject(true)))
								result = true;
							break;
						case MELDING_BEHEER_KLANT:
							if (klant != null && EKlantStatus.isAllowedToLogin(klant.getKlantStatusCode()) && 
									(CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true)) || 
									CompareUtil.containsRol(rollen, ERol.HOOFD.getRolObject(true)) ||
									CompareUtil.containsRol(rollen, ERol.MANAGED.getRolObject(true))))
								result = true;
							break;
						case KLANTGEBRUIKER_BEHEER:
							if (klant != null && EKlantStatus.isAllowedToLogin(klant.getKlantStatusCode()) && (CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.HOOFD.getRolObject(true))))
								result = true;
							else if (klant == null && (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true))) || CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true)) )
								result = true;
							break;
						case SBDRGEBRUIKER_BEHEER:
							if (CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))) result = true;
							break;
						case MELDING_AANPASSEN:
							if (klant != null && EKlantStatus.isAllowedToLogin(klant.getKlantStatusCode()) && CompareUtil.containsRol(rollen, ERol.REGISTRATIESTOEGESTAAN.getRolObject(true)) &&
									(CompareUtil.containsRol(rollen, ERol.GEBRUIKER.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.KLANT.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.HOOFD.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.MANAGED.getRolObject(true))))
								result = true;
							else if (klant == null && (CompareUtil.containsRol(rollen, ERol.SBDR.getRolObject(true)) || CompareUtil.containsRol(rollen, ERol.SBDRHOOFD.getRolObject(true))))
								result = true;
							break;
					}
				}
			}

			return result;
		} catch (DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public LoginAllowed isGebruikerAllowedToLogin(GebruikersDetails gebruikerDetails, boolean readonly) throws ServiceException {
		try {
			Gebruiker gebruiker = gebruikerDataService.findById(gebruikerDetails.getUserId());

			LoginAllowed result = getLoginAllowed(gebruiker, gebruikerDetails, readonly);
			
			return result;
		} catch (DataServiceException e) {
			throw new ServiceException(e);
		} catch (ServiceException e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public LoginAllowed isGebruikerAllowedToLogin2(String gebruikersNaam, GebruikersDetails gebruikerDetails, boolean readonly, boolean gebruikersnaamIsMCK) throws ServiceException {
		try {
			Gebruiker gebruiker = null;

			if (gebruikersnaamIsMCK) {
				MobileGebruiker mG = mobileGebruikerDataService.findByKey(gebruikersNaam);
				if (mG != null) {
					gebruiker = gebruikerDataService.findByGebruikerid(mG.getGebruikerGebruikerId());
				}
			} else {
				gebruiker = gebruikerDataService.findByGebruikersnaam(gebruikerDetails.getUsername());
			}

			LoginAllowed result = getLoginAllowed(gebruiker, gebruikerDetails, readonly);
			
			return result;
		} catch (DataServiceException e) {
			throw new ServiceException(e);
		} catch (ServiceException e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public boolean isGebruikerOfKlant(Integer gebruikerId, Integer bedrijfId) throws ServiceException {
		boolean result = true;

		try {
			Klant klant = klantDataService.findKlantOfGebruikerByGebruikerId(gebruikerId, bedrijfId);

			if (klant != null && klant.getBedrijf() != null)
				if (klant.getBedrijf().getKvKnummer().equals(BedrijfService.KVK_SBDR)) result = false;

			return result;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public boolean logoutGebruiker(String signature, Integer gebruikerId) throws ServiceException {
		try {
			boolean result = false;

			Gebruiker gebruiker = gebruikerDataService.findById(gebruikerId);

			if (gebruiker != null) {
				gebruiker.setDatumLaatsteLogout(new Date());

				gebruikerDataService.save(gebruiker);

				result = webTokenService.removeToken(signature);
			}

			return result;
		} catch (DataServiceException e) {
			throw new ServiceException(e);
		} catch (ServiceException e) {
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public ErrorService resetWachtwoord(String gebruikersNaam, String activationId, String password) throws ServiceException {
		ErrorService result = null;

		try {
			if (StringUtil.isNotEmptyOrNull(gebruikersNaam)) {
				Gebruiker gebruiker = gebruikerDataService.findByGebruikersnaam(gebruikersNaam);

				if (gebruiker != null) {
					if (gebruiker.getActief()) {
						Wachtwoord wachtwoord = gebruiker.getWachtwoord();

						if (wachtwoord.getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_RESET)) {
							if (wachtwoord.getActivatieCode() != null) {
								if (wachtwoord.getActivatieCode().equals(activationId)) {
									// encode password
									wachtwoord.setWachtwoord(passwordEncoder.encode(password));
									wachtwoord.setActivatieCode(null);

									wachtwoord.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
									wachtwoord.setDatumLaatsteWijziging(new Date());

									// Save changes to the database
									wachtwoordService.save(wachtwoord);

									//emailService.sendPasswordChangedEmail(gebruiker);
								} else {
									LOGGER.error("Activation code does not match the activationId for user: " + gebruikersNaam);
									result = new ErrorService(ErrorService.ACTIVATIONID_MISMATCH);
								}
							} else {
								LOGGER.error("No activation code found for user: " + gebruikersNaam);
							}
						} else {
							LOGGER.error("WachtwoordStatusCode does not match RESET: " + wachtwoord.getWachtwoordStatusCode() + ". User: " + gebruikersNaam);
							result = new ErrorService(ErrorService.PASSWORD_STATUS_MISMATCH);
						}
					} else {
						LOGGER.error("User not active: " + gebruikersNaam);
						result = new ErrorService(ErrorService.USER_NOT_ACTIVATED);
					}
				} else {
					LOGGER.error("No user found with the name: " + gebruikersNaam);
					result = new ErrorService(ErrorService.USERNAME_NOT_EXISTS);
				}
			} else {
				LOGGER.error("No gebruikersNaam given: " + gebruikersNaam);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw (new ServiceException(e));
		}

		return result;
	}

	@Override
	@Transactional
	public ErrorService saveGebruiker(Gebruiker gebruiker, boolean isBedrijfManaged, Map<String, Boolean> rollen, Wachtwoord wachtwoord) throws ServiceException {
		boolean isNew = false;
		Wachtwoord bestaandWachtwoord = null;
		Gebruiker bestaandegebruiker = null;
		boolean wachtwoordAangepast = false;
		Wachtwoord newWachtwoord = null;

		try {
			if (gebruiker.getGebruikerId() == null) {
				isNew = true;
				
				// Check if gebruiker already exists
				// Difference between Managed account and Bedrijf account
				if (isBedrijfManaged)
					bestaandegebruiker = gebruikerDataService.findByGebruikersnaamOfBedrijf(gebruiker.getGebruikersNaam(), gebruiker.getBedrijfBedrijfId());
				else
					bestaandegebruiker = gebruikerDataService.findByGebruikersnaam(gebruiker.getGebruikersNaam());
				
				if (bestaandegebruiker != null) 
					return new ErrorService(ErrorService.USERNAME_ALREADY_EXISTS);
				// if bedrijfManaged user may exist already, but not for this company of course
				else if (isBedrijfManaged) {
					bestaandegebruiker = gebruikerDataService.findByGebruikersnaam(gebruiker.getGebruikersNaam());		
					if (bestaandegebruiker != null) {
						bestaandWachtwoord = bestaandegebruiker.getWachtwoord();
						newWachtwoord = bestaandWachtwoord;
					} 
					// Managed user must already have an account
					else {
						return new ErrorService(ErrorService.MANAGED_USER_NOT_EXISTS);
					}
					// new bedrijfManaged user, so create new ww
					//else {
						// Dummy ww!!!
					//	wachtwoord = new Wachtwoord();
					//	wachtwoord.setWachtwoord("ABD#$HEwaAbdcdwq!");						
					//}
				} 
				// new default user, so create new ww
				else {
					// Dummy ww!!!
					wachtwoord = new Wachtwoord();
					wachtwoord.setWachtwoord("ABD#$HEwaAbdcdwq!");					
				}

				// to do: This is probably not working + wrong error message. Needs to check if former bedrijfManaged user is changed to other company
				// if not managed + new gebruiker then no other user may exist with other company
				// for now only may edit existing user without changing to other managed company
				if (!isBedrijfManaged && bestaandegebruiker!= null && !gebruiker.getBedrijfBedrijfId().equals(bestaandegebruiker.getBedrijfBedrijfId()))
					return new ErrorService(ErrorService.USERNAME_ALREADY_EXISTS);
				
				gebruiker.setDatumAangemaakt(new Date());
				gebruiker.setActief(gebruiker.getActief());
			} else {
				bestaandegebruiker = gebruikerDataService.findById(gebruiker.getGebruikerId());

				if (!isBedrijfManaged) {
					// Check if gebruikersnaam already exists for other user
					Gebruiker bestaandegebruikerOpNaam = gebruikerDataService.findByGebruikersnaam(gebruiker.getGebruikersNaam());
					if (bestaandegebruikerOpNaam != null && !bestaandegebruikerOpNaam.getGebruikerId().equals(bestaandegebruiker.getGebruikerId()))
						return new ErrorService(ErrorService.USERNAME_ALREADY_EXISTS);
					// no other user may exist with other company
					else if (!gebruiker.getBedrijfBedrijfId().equals(bestaandegebruiker.getBedrijfBedrijfId())) {
						// This may only occur on SBDR user changing user details of klant
					}
				}
				
				bestaandWachtwoord = bestaandegebruiker.getWachtwoord();

				gebruiker.setActief(gebruiker.getActief());
				
				newWachtwoord = bestaandWachtwoord;
			}

			// wachtwoord is changed
			if (wachtwoord != null && wachtwoord.getWachtwoord() != null && !wachtwoord.getWachtwoord().equals("")) {
				// if existing user
				if (bestaandWachtwoord != null) {
					wachtwoordAangepast = true;

					// encode password
					bestaandWachtwoord.setWachtwoord(passwordEncoder.encode(wachtwoord.getWachtwoord()));
					if (wachtwoord.getWachtwoordStatusCode() != null) // if wachtwoord status is set, update status
						bestaandWachtwoord.setWachtwoordStatusCode(wachtwoord.getWachtwoordStatusCode());
					//bestaandWachtwoord.setWachtwoordStatus(wachtwoordStatusDataService.findByPrimaryKey(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL));
					newWachtwoord = bestaandWachtwoord;
				} else {
					// encode password
					wachtwoord.setWachtwoord(passwordEncoder.encode(wachtwoord.getWachtwoord()));
					// only for new Gebruiker own company create UUID for activation
					if (!isBedrijfManaged || bestaandegebruiker == null)
						wachtwoord.setActivatieCode(UUID.randomUUID().toString());
					wachtwoord.setWachtwoordStatus(wachtwoordStatusDataService.findByPrimaryKey(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL));
					newWachtwoord = wachtwoord;
				}
			}

			if (wachtwoord != null && newWachtwoord != null) newWachtwoord = wachtwoordService.save(newWachtwoord);

			Set<Rol> existingRollen = new HashSet<Rol>();

			// find BedrijfManaged
			BedrijfManaged bedrijfManaged = null;
			if (bestaandegebruiker != null) {
				if (isBedrijfManaged && bestaandegebruiker.getBedrijvenManagedDoorGebruikerId() != null) {
					
					for (BedrijfManaged gebruikerBedrijfManaged : bestaandegebruiker.getBedrijvenManagedDoorGebruikerId()) {
						if (gebruikerBedrijfManaged.getBedrijfBedrijfId().equals(gebruiker.getBedrijfBedrijfId())) {
							bedrijfManaged = gebruikerBedrijfManaged;
							if (gebruikerBedrijfManaged.isActief()) {
								existingRollen = bedrijfManaged.getRollen();
							}
						}
					}
				} else
					existingRollen = bestaandegebruiker.getRollen();
			} 	
			
			// if bedrijf must be managed + is not already managed, create new bedrijfManaged
			boolean newCreated = false;
			boolean newReactivated = false;
			if (isBedrijfManaged) {
				if (bedrijfManaged == null) {
					bedrijfManaged = new BedrijfManaged();
					bedrijfManaged.setBedrijf(gebruiker.getBedrijf());	
					newCreated = true;
				}
				// reactivate bedrijfManaged when needed
				if (newCreated || bedrijfManaged.getGebruikerStatus().equals(EKlantStatus.VERWIJDERD.getCode()) || !bedrijfManaged.isActief()) {
					bedrijfManaged.setDatumAangemaakt(new Date());
					bedrijfManaged.setGebruikerStatus(EKlantStatus.REGISTRATIE.getCode());
					bedrijfManaged.setActivatieCode(generateActivatieCodeEmail());						
					bedrijfManaged.setActief(true);
					// if not newly created, then reactivated
					if (!newCreated)
						newReactivated = true;
				}
				// keep bedrijfManaged value for setting user id etc further down
			} 
			// if bedrijf must not be managed anymore + is already managed, stop managing it
			else if (!isBedrijfManaged && bedrijfManaged != null) {
				bedrijfManaged.setActief(false);
				bedrijfManaged.setGebruikerStatus(EKlantStatus.VERWIJDERD.getCode());
				bedrijfManaged.setDatumVerwijderd(new Date());
				bedrijfManaged = bedrijfManagedDataService.save(bedrijfManaged);
				// no need to preserve + prevent save action further down
				bedrijfManaged = null;
			}
			
			Gebruiker nieuwegebruiker = null;
			if (bestaandegebruiker != null) {
				bestaandegebruiker.setActief(gebruiker.getActief());
				if (bedrijfManaged == null) {
					bestaandegebruiker.setEmailAdres(gebruiker.getEmailAdres());
					bestaandegebruiker.setFunctie(gebruiker.getFunctie());
					bestaandegebruiker.setAfdeling(gebruiker.getAfdeling());
					bestaandegebruiker.setTelefoonNummer(gebruiker.getTelefoonNummer());
					bestaandegebruiker.setNaam(gebruiker.getNaam());
					bestaandegebruiker.setVoornaam(gebruiker.getVoornaam());
					bestaandegebruiker.setGeslacht(gebruiker.getGeslacht());
				}
				// username has changed so remove login token
				if (!bestaandegebruiker.getGebruikersNaam().equals(gebruiker.getGebruikersNaam()))
					webTokenService.removeWebTokenOfUser(bestaandegebruiker.getGebruikersNaam());
				bestaandegebruiker.setGebruikersNaam(gebruiker.getGebruikersNaam());

				bestaandegebruiker.setWachtwoord(newWachtwoord);

				Set<Rol> newRollen = createRoles(rollen);

				if (bestaandegebruiker.getRollen() != null) {
					// May never remove role KLANT from existing user!
					if (!CompareUtil.containsRol(newRollen, ERol.KLANT.getRolObject(true)) && CompareUtil.containsRol(existingRollen, ERol.KLANT.getRolObject(true)))
						return new ErrorService(ErrorService.CANNOT_SAVE_USER);

					// May never add role KLANT to existing user unless it is a managed user of other company!
					if (CompareUtil.containsRol(newRollen, ERol.KLANT.getRolObject(true)) && !CompareUtil.containsRol(existingRollen, ERol.MANAGED.getRolObject(true)) && !CompareUtil.containsRol(existingRollen, ERol.KLANT.getRolObject(true)) &&
							!gebruiker.getBedrijfBedrijfId().equals(bestaandegebruiker.getBedrijfBedrijfId()))
						return new ErrorService(ErrorService.CANNOT_SAVE_USER);

					// May never create new user with role SBDR!
					if (bedrijfService.isBedrijfOfKlant(gebruiker.getBedrijfBedrijfId()) && ((CompareUtil.containsRol(newRollen, ERol.SBDR.getRolObject(true)) && !CompareUtil.containsRol(existingRollen, ERol.SBDR.getRolObject(true))) || (CompareUtil.containsRol(newRollen, ERol.SBDRHOOFD.getRolObject(true)) && !CompareUtil.containsRol(existingRollen, ERol.SBDRHOOFD.getRolObject(true)))))
						return new ErrorService(ErrorService.CANNOT_SAVE_USER);

					// Managed user may not be a SBDR user. Just to prevent confusion
					if (CompareUtil.containsRol(newRollen, ERol.MANAGED.getRolObject(true)) && 
							(CompareUtil.containsRol(existingRollen, ERol.SBDR.getRolObject(true)) || CompareUtil.containsRol(existingRollen, ERol.SBDRHOOFD.getRolObject(true)))	)
						return new ErrorService(ErrorService.CANNOT_SAVE_USER);	
					
					// If API role is added, then create API authentication + send email
					if (CompareUtil.containsRol(newRollen, ERol.APITOEGESTAAN.getRolObject(true)) && !CompareUtil.containsRol(existingRollen, ERol.APITOEGESTAAN.getRolObject(true))) {
						//xxx
					}
					// If API role is revoked, then revoke API authentication + send email
					else if (!CompareUtil.containsRol(newRollen, ERol.APITOEGESTAAN.getRolObject(true)) && CompareUtil.containsRol(existingRollen, ERol.APITOEGESTAAN.getRolObject(true))) {
						//xxx
					}
				}
				
				// if new bedrijfManaged set user roles in bedrijfmanaged (roles are of company managed), otherwise set roles to user (roles are of own company)
				if (bedrijfManaged != null) {
					bedrijfManaged.setEmailAdres(gebruiker.getEmailAdres());
					bedrijfManaged.setGebruiker(bestaandegebruiker);
					bedrijfManaged.setFunctie(gebruiker.getFunctie());
					bedrijfManaged.setAfdeling(gebruiker.getAfdeling());
					bedrijfManaged.setTelefoonNummer(gebruiker.getTelefoonNummer());
					bedrijfManaged.setNaam(gebruiker.getNaam());
					bedrijfManaged.setVoornaam(gebruiker.getVoornaam());
					bedrijfManaged.setGeslacht(gebruiker.getGeslacht());					
					bedrijfManaged.setRollen(newRollen);	
					bedrijfManaged = bedrijfManagedDataService.save(bedrijfManaged);
					bestaandegebruiker.getBedrijvenManagedDoorGebruikerId().add(bedrijfManaged);
					
					// if existing user already has a (own) company
					if(bestaandegebruiker.getBedrijfBedrijfId() != null && !gebruiker.getBedrijfBedrijfId().equals(bestaandegebruiker.getBedrijfBedrijfId()))
						gebruiker.setBedrijfBedrijfId(bestaandegebruiker.getBedrijfBedrijfId());					
				} else
					bestaandegebruiker.setRollen(newRollen);	
				
				nieuwegebruiker = gebruikerDataService.save(bestaandegebruiker);

				// bedrijfId = bedrijf of user OR managed bedrijf
				if (wachtwoordAangepast) emailService.sendPasswordChangedEmail(nieuwegebruiker, gebruiker.getBedrijfBedrijfId());
				// If new bedrijfManaged sent email to user for activation
				if (bedrijfManaged != null) {
					// bedrijfId = bedrijf of user OR managed bedrijf
					if (newCreated || newReactivated)
						emailService.sendNewUserBedrijfManagedEmail(bestaandegebruiker, gebruiker.getBedrijfBedrijfId(), bedrijfManaged);
				}
			} else {
				gebruiker.setWachtwoord(newWachtwoord);
				if (isNew)  // if new users
					gebruiker.setActief(true); // Always set true also for new users!

				// May never create new KLANT as user!
				if (rollen.get(ERol.KLANT.getCode()) != null)
					return new ErrorService(ErrorService.CANNOT_SAVE_USER);

				// May never create new user with role SBDR!
				// MBR: Not completely true, SBDR admin may create new SBDR users for Company SBDR only!
				if (bedrijfService.isBedrijfOfKlant(gebruiker.getBedrijfBedrijfId()) && (rollen.get(ERol.SBDR.getCode()) != null ||rollen.get(ERol.SBDRHOOFD.getCode()) != null))
					return new ErrorService(ErrorService.CANNOT_SAVE_USER);
				
				Integer bedrijfId = gebruiker.getBedrijfBedrijfId();
				
				// if new bedrijfManaged set user roles in bedrijfmanaged (roles are of company managed), otherwise set roles to user (roles are of own company)
				if (bedrijfManaged != null) {
					// don't set Bedrijf to gebruiker, because it is a managed Bedrijf
					gebruiker.setBedrijf(null);
					nieuwegebruiker = gebruikerDataService.save(gebruiker);
					
					bedrijfManaged.setEmailAdres(nieuwegebruiker.getEmailAdres());
					bedrijfManaged.setGebruiker(nieuwegebruiker);
					bedrijfManaged.setFunctie(nieuwegebruiker.getFunctie());
					bedrijfManaged.setAfdeling(nieuwegebruiker.getAfdeling());
					bedrijfManaged.setTelefoonNummer(nieuwegebruiker.getTelefoonNummer());		
					bedrijfManaged.setNaam(nieuwegebruiker.getNaam());
					bedrijfManaged.setVoornaam(nieuwegebruiker.getVoornaam());
					bedrijfManaged.setGeslacht(nieuwegebruiker.getGeslacht());					
					bedrijfManaged.setRollen(createRoles(rollen));
					bedrijfManaged = bedrijfManagedDataService.save(bedrijfManaged);
					//gebruiker.getBedrijvenManagedDoorGebruikerId().add(bedrijfManaged);
				} else {
					gebruiker.setRollen(createRoles(rollen));
					
					nieuwegebruiker = gebruikerDataService.save(gebruiker);
				}
								
				// bedrijfId = bedrijf of user OR managed bedrijf
				emailService.sendNewUserEmail(nieuwegebruiker, bedrijfId);

				// bedrijfId = bedrijf of user OR managed bedrijf
				Klant klant = klantDataService.findKlantOfGebruikerByGebruikerId(nieuwegebruiker.getGebruikerId(), bedrijfId);
				if (klant != null && !klant.getGebruikerId().equals(nieuwegebruiker.getGebruikerId())) {
					emailService.sendNewUserConfirmationEmail(klant, nieuwegebruiker);
				}
				// If new bedrijfManaged sent email to user for activation
				if (bedrijfManaged != null) {
					// bedrijfId = bedrijf of user OR managed bedrijf
					emailService.sendNewUserBedrijfManagedEmail(gebruiker, bedrijfId, bedrijfManaged);
				}
			}
			
			//Klant klant = klantDataService.save((Klant) gebruiker);

			return null;
		} catch (DataServiceException e) {
			throw new ServiceException("Cannot createGebruiker: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService updateLoginAttempts(String gebruikersnaam, boolean loginok, boolean gebruikersnaamIsMCK) throws ServiceException {
		Calendar now = Calendar.getInstance();

		try {
			Gebruiker gebruiker = null;

			try {
				if (gebruikersnaamIsMCK) {
					MobileGebruiker mG = mobileGebruikerDataService.findByKey(gebruikersnaam);

					if (mG != null) {
						gebruiker = gebruikerDataService.findByGebruikerid(mG.getGebruikerGebruikerId());
					}
				} else {
					gebruiker = gebruikerDataService.findByGebruikersnaam(gebruikersnaam);
				}
			} catch (Exception e) {
				return new ErrorService(ErrorService.USERNAME_NOT_EXISTS);
			}

			// Gebruiker is active && Wachtwoord is non-blocked
			if (gebruiker != null) {
				if (gebruiker.getActief() && gebruiker.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE)) {

					short nrOfLogin = 0;

					if (!loginok) {
						// add login attempt
						if (gebruiker.getNrAanmeldPogingen() != null)
							nrOfLogin = (short) (gebruiker.getNrAanmeldPogingen() + 1);

						// block account
						if (nrOfLogin == LoginAllowed.MAX_LOGINATTEMPTS) {
							// update wachtwoord
							gebruiker.getWachtwoord().setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_BLOCKED);
							gebruiker.getWachtwoord().setDatumLaatsteWijziging(now.getTime());
							wachtwoordService.save(gebruiker.getWachtwoord());
						}

					}

					// update gebruiker
					gebruiker.setNrAanmeldPogingen(nrOfLogin);
					gebruiker.setDatumLaatsteAanmeldpoging(now.getTime());
					if (loginok) gebruiker.setDatumLaatsteAanmelding(now.getTime());
					gebruikerDataService.save(gebruiker);
				}

				return null;
			}
			// else userid does not exists, so no blocking needed...
			else return new ErrorService(ErrorService.USERNAME_NOT_EXISTS);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public boolean updateShowHelp(Integer userId, Integer showHelp) throws ServiceException {
		try {
			boolean result = true;

			Gebruiker gebruiker = gebruikerDataService.findById(userId);

			if (gebruiker != null) {
				gebruiker.setShowHelp(showHelp);

				gebruikerDataService.save(gebruiker);
			} else result = false;

			return result;
		} catch (DataServiceException e) {
			throw new ServiceException(e);
		} catch (ServiceException e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public boolean validateUserData(String gebruikersnaam, String gebruikerswachtwoord) throws ServiceException {
		try {
			Gebruiker gebruiker = gebruikerDataService.findByGebruikersnaam(gebruikersnaam);
			Wachtwoord wachtwoord = gebruiker.getWachtwoord();

			boolean pwdmatch = false;
			if (wachtwoord.getWachtwoord() != null && passwordEncoder.matches(gebruikerswachtwoord, wachtwoord.getWachtwoord()))
				pwdmatch = true;

			return pwdmatch;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	private Set<Rol> createRoles(Map<String, Boolean> roles) throws DataServiceException {
		Set<Rol> result = null;

		if (roles == null) return null;
		else {
			Rol rol = null;
			result = new HashSet<Rol>();
			Boolean auth = null;
			try {
				auth = (Boolean) roles.get(ERol.KLANT.getCode());
				if (auth != null) {
					rol = rolDataService.findByCode(ERol.KLANT.getCode());
					result.add(rol);
				}

				auth = (Boolean) roles.get(ERol.HOOFD.getCode());
				if (auth != null) {
					rol = rolDataService.findByCode(ERol.HOOFD.getCode());
					result.add(rol);
				}

				auth = (Boolean) roles.get(ERol.MANAGED.getCode());
				if (auth != null) {
					rol = rolDataService.findByCode(ERol.MANAGED.getCode());
					result.add(rol);
				}
				
				auth = (Boolean) roles.get(ERol.GEBRUIKER.getCode());
				if (auth != null) {
					rol = rolDataService.findByCode(ERol.GEBRUIKER.getCode());
					result.add(rol);
				}

				auth = (Boolean) roles.get(ERol.SBDR.getCode());
				if (auth != null) {
					rol = rolDataService.findByCode(ERol.SBDR.getCode());
					result.add(rol);
				}

				auth = (Boolean) roles.get(ERol.SBDRHOOFD.getCode());
				if (auth != null) {
					rol = rolDataService.findByCode(ERol.SBDRHOOFD.getCode());
					result.add(rol);
				}
				
				auth = (Boolean) roles.get(ERol.REGISTRATIESTOEGESTAAN.getCode());
				if (auth != null) {
					rol = rolDataService.findByCode(ERol.REGISTRATIESTOEGESTAAN.getCode());
					result.add(rol);
				}				
				
				auth = (Boolean) roles.get(ERol.APITOEGESTAAN.getCode());
				if (auth != null) {
					rol = rolDataService.findByCode(ERol.APITOEGESTAAN.getCode());
					result.add(rol);
				}	
			} catch (DataServiceException e) {
				throw new DataServiceException();
			}
		}

		return result;
	}
	
	private String generateActivatieCodeEmail() throws ServiceException {
		// ActivatieCode
		String activatieCode = null;
		boolean generateNewCode = true;
		int iteration = 0;
		while (generateNewCode) {
			iteration++;
			activatieCode = SerialNumber.generateRandomSerialNumber16_32();
			Klant bestaandeklant;
			try {
				bestaandeklant = klantDataService.findByKlantActivatieCode(activatieCode);
			} catch (DataServiceException e) {
				LOGGER.error("Cannot find klant with activation code: " + activatieCode);
				throw new ServiceException("Cannot find klant with activation code: " + activatieCode);
			}

			if (bestaandeklant == null) generateNewCode = false;

			if (iteration > 20) throw new ServiceException("Cannot generate activation code");
		}

		return activatieCode;
	}

	private LoginAllowed getLoginAllowed(Gebruiker gebruiker, GebruikersDetails gebruikerDetails, boolean readonly) throws ServiceException {
		LoginAllowed loginAllowed = new LoginAllowed();
		Calendar now = Calendar.getInstance();

		try {
			if (gebruiker != null && gebruikerDetails != null) {
				Klant klant = null;
				// Bedrijf might be a managed bedrijf instead of own bedrijf
				if (gebruikerDetails.getBedrijfId() != null)
					klant = klantDataService.findKlantOfGebruikerByGebruikerId(gebruiker.getGebruikerId(), gebruikerDetails.getBedrijfId());

				// Klant is active || klant == user)
				if (klant != null) {
					if (klant.getGebruikerId().equals(gebruiker.getGebruikerId())) loginAllowed.setKlant(true);

					if (gebruiker.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL)) {
						loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTACTIVATED);
					} else if (klant.getKlantStatusCode().equals(EKlantStatus.ACTIEF.getCode()) || (klant.getGebruikerId().equals(gebruiker.getGebruikerId()) && (klant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode()) || klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode())))) {
						Bedrijf bedrijf = klant.getBedrijf();

						if (bedrijf.getBedrijfStatusCode().equals(EBedrijfStatus.ACTIEF.getCode()))
							loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_ALLOWED);
						else
							loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTALLOWED_ACCOUNTDISABLED); // probably may no occur						
					} else {
						loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTALLOWED_ACCOUNTDISABLED);
					}

					//					if(klant.getKlantStatusCode().equals(EKlantStatus.ACTIEF.getCode()) || (klant.getGebruikerId().equals(gebruiker.getGebruikerId()) && (klant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode()) || klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode())))) {
					//						Bedrijf bedrijf = klant.getBedrijf();
					//
					//						if(bedrijf.getBedrijfStatusCode().equals(EBedrijfStatus.ACTIEF.getCode()))
					//							loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_ALLOWED);
					//						else
					//							loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTALLOWED_ACCOUNTDISABLED); // probably may no occur
					//					} else {
					//						if(gebruiker.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL))
					//							loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTACTIVATED);
					//						else loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTALLOWED_ACCOUNTDISABLED);
					//					}
				} else loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTALLOWED_NOKLANT);

				// Not already set loginallowed error && Gebruiker is active && Wachtwoord is blocked
				if (loginAllowed.getLoginAllowed() == LoginAllowed.LOGIN_ALLOWED && gebruiker.getActief()) { // && gebruiker.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_BLOCKED)) {

					if (!readonly) {
						Calendar laatstePoging = Calendar.getInstance();

						// get last attempt, or set to now if not present
						if (gebruiker.getDatumLaatsteAanmeldpoging() != null)
							laatstePoging.setTime(gebruiker.getDatumLaatsteAanmeldpoging());
						else laatstePoging.setTime(now.getTime());

						laatstePoging.add(Calendar.MINUTE, LoginAllowed.MAX_BLOCKING_MINUTES);
						if (laatstePoging.before(now)) // blocking time expired
						{
							// update wachtwoord
							gebruiker.getWachtwoord().setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
							gebruiker.getWachtwoord().setDatumLaatsteWijziging(now.getTime());
							wachtwoordService.save(gebruiker.getWachtwoord());

							//	    					// update gebruiker
							//	    					gebruiker.setNrAanmeldPogingen((short) 0);
							//	    					gebruiker.setDatumLaatsteAanmeldpoging(now.getTime());
							//	    					repository.save(gebruiker);

							//loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_ALLOWED);

						} else if (gebruiker.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_BLOCKED)) {
							loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTALLOWED_ACCOUNTBLOCKED);
						}
					} else {
						if (gebruiker.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_BLOCKED))
							loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTALLOWED_ACCOUNTBLOCKED);
						//else
						//	loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_ALLOWED);
					}

					if (gebruiker.getNrAanmeldPogingen() != null)
						loginAllowed.setNrAanmeldpogingen(gebruiker.getNrAanmeldPogingen());
					else loginAllowed.setNrAanmeldpogingen((short) 0);
					if (gebruiker.getDatumLaatsteAanmeldpoging() != null)
						loginAllowed.setDatumLaatsteAanmeldpoging(gebruiker.getDatumLaatsteAanmeldpoging());
					else loginAllowed.setDatumLaatsteAanmeldpoging(now.getTime());

				}
			} else loginAllowed.setLoginAllowed(LoginAllowed.LOGIN_NOTALLOWED_NOGEBRUIKER);

			return loginAllowed;

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	//
	//	@Override
	//	@Transactional
	//	public ErrorService changeWachtwoordGebruiker(String activationCode, String wachtwoord) throws ServiceException {
	//
	//		try {
	//			if (wachtwoord != null && activationCode != null)
	//			{
	//				Gebruiker bestaandegebruiker = repository.findByActivatieCode(activationCode);
	//				if (bestaandegebruiker == null)
	//				{
	//					LOGGER.warn("Cannot change wachtwoord. activationCode " + activationCode + " not found.");
	//					return new ErrorService(ErrorService.CANNOT_CHANGE_WACHTWOORD);
	//				}
	//				else {
	//					Wachtwoord bestaandwachtwoord = bestaandegebruiker.getWachtwoord();
	//					// if status pwd = reset
	//					// then pwd can be changed
	//					if (bestaandwachtwoord.getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_RESET) ) {
	//						bestaandwachtwoord.setDatumLaatsteWijziging(new Date());
	//
	//						bestaandwachtwoord.setWachtwoord(passwordEncoder.encode(wachtwoord));
	//
	//						bestaandwachtwoord.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
	//
	//						wachtwoordService.save(bestaandwachtwoord);
	//					}
	//					else {
	//						LOGGER.warn("Cannot change wachtwoord. Wachtwoordstatus not valid for change " + bestaandwachtwoord.getWachtwoordStatusCode() + " not found. Reset pwd: " + true);
	//						return new ErrorService(ErrorService.CANNOT_CHANGE_WACHTWOORD);
	//					}
	//				}
	//
	//				return null;
	//			}
	//			else
	//				return new ErrorService(ErrorService.CANNOT_CHANGE_WACHTWOORD);
	//		}
	//		catch (DataServiceException e)
	//		{
	//			throw new ServiceException("Cannot change wachtwoord: " + e.getMessage());
	//		}
	//	}
}
