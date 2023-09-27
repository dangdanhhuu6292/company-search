package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.repository.KlantRepository;
import nl.devoorkant.sbdr.data.util.EKlantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

import java.util.*;


/**
 * Data Service bean with implemented functionality for Klant.
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

@Service("klantDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class KlantDataServiceImpl implements KlantDataService {
	@Autowired
	private KlantRepository klantRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(KlantDataServiceImpl.class);

	@Transactional
	public void delete(Klant klant) throws DataServiceException {
		try {
			klantRepository.delete(klant);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, isolation = Isolation.SERIALIZABLE)
	public Klant findByGebruikerId(Integer id) throws DataServiceException {
		try {
			List<Klant> klanten = klantRepository.findByGebruikerId(id);

			if(klanten != null && klanten.size() == 1) return klanten.get(0);
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, isolation = Isolation.SERIALIZABLE)
	public Klant findByGebruikersNaam(String gebruikersnaam) throws DataServiceException {
		try {
			List<Klant> klanten = klantRepository.findByGebruikersNaam(gebruikersnaam);

			if(klanten != null && klanten.size() == 1) return klanten.get(0);
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Klant findByKlantActivatieCode(String activatieCode) throws DataServiceException {
		try {
			List<Klant> klanten = klantRepository.findByActivatieCode(activatieCode);

			if(klanten != null && klanten.size() == 1) return klanten.get(0);
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Klant findByKlantId(Integer id) throws DataServiceException {
		try {

			Optional<Klant> klant = klantRepository.findById(id);
			return klant != null ? klant.get() : null;
		} catch(Exception e) {
			LOGGER.error("findKlantOfGebruikerByGebruikerId error: " + e.getMessage());
			LOGGER.error("findKlantOfGebruikerByGebruikerId stack: " + e);
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Klant findKlantOfBedrijfByBedrijfId(Integer bedrijfId) throws DataServiceException {
		try {
			List<Klant> klanten = klantRepository.findKlantOfBedrijfByBedrijfId(bedrijfId);

			if(klanten != null && klanten.size() == 1) return klanten.get(0);
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Klant findKlantOfBedrijfByBedrijfIdAndStatusCode(Integer bId, Collection<String> statusCodes) throws DataServiceException {
		try {
			List<Klant> klanten = klantRepository.findKlantOfBedrijfByBedrijfIdAndStatus(bId, statusCodes);

			if(klanten != null && klanten.size() == 1) {return klanten.get(0);} else {return null;}
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Klant findKlantOfGebruikerByGebruikerId(Integer gebruikerId, Integer bedrijfId) throws DataServiceException {
		try {
			List<Klant> klanten = null;
			
			// MBR 7-12-2017 Find klanten of gebruikerId and specific bedrijfId OR find klanten of gebruikerId otherwise
			if (bedrijfId != null)
				klanten = klantRepository.findKlantOfGebruikerByGebruikerIdBedrijfId(gebruikerId, bedrijfId);
			else
				klanten = klantRepository.findKlantOfGebruikerByGebruikerId(gebruikerId);

			if(klanten != null && klanten.size() == 1) {
				return klanten.get(0);
			}
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Object[]> findKlantenToProcessExactOnline() throws DataServiceException {
		try {
			return klantRepository.findKlantenToProcessExactOnline();
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Klant findNewKlantById(Integer gebruikerId) throws DataServiceException {
		try {
			return klantRepository.findKlantByIdAndStatus(gebruikerId, EKlantStatus.getAllNewKlantCodes());
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Klant> findNonActivatedKlanten() throws DataServiceException {
		try {
			//Substract 24 hours(or 1 day)
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			Date overdueDate = cal.getTime();

			List<String> klantCodes = new ArrayList<String>();

			klantCodes.add(EKlantStatus.REGISTRATIE.getCode());

			return klantRepository.findKlantAccountByOverdueDateAndStatus(overdueDate, klantCodes);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Klant> findNonVerifiedKlanten() throws DataServiceException {
		try {
			return klantRepository.findKlantenByStatus(EKlantStatus.getAllNonVerifiedKlantCodes());
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Klant> findOverdueNewKlanten(int daysoverdue) throws DataServiceException {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -daysoverdue);
			Date overdueDate = cal.getTime();

			return klantRepository.findKlantAccountByOverdueDateAndStatus(overdueDate, EKlantStatus.getAllNewKlantCodes());
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Klant> findReminderNewKlanten(int hoursOverdue) throws DataServiceException {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -hoursOverdue);
			Date overdueDate = cal.getTime();

			List<String> klantCodes = new ArrayList<String>();

			klantCodes.add(EKlantStatus.REGISTRATIE.getCode());
			//klantCodes.add(EKlantStatus.PROSPECT.getCode()); MBR 02-12-2015: Only REG klanten may receive activation reminder. PRO klanten already are activated

			return klantRepository.findKlantAccountByOverdueDateAndStatus(overdueDate, klantCodes);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Transactional
	public Klant save(Klant klant) throws DataServiceException {
		try {
			LOGGER.info("emailfacturatie klant: " + klant.getEmailAdresFacturatie());
			return klantRepository.save(klant);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public void createNewKlantRecord(Integer gebruikerId) throws DataServiceException {
		try {
			klantRepository.createNewKlantRecord(gebruikerId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
}
