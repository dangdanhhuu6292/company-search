package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Support;
import nl.devoorkant.sbdr.data.model.SupportBestand;
import nl.devoorkant.sbdr.data.repository.SupportBestandRepository;
import nl.devoorkant.sbdr.data.repository.SupportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Data Service bean with implemented functionality for Support.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * Implements the functionality exposed by the SupportDataService interface.
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Coen Hasselaar
 * @version %I%
 */

@Service("supportDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class SupportDataServiceImpl implements SupportDataService {
	@Autowired
	private SupportBestandRepository supportBestandRepository;
	@Autowired
	private SupportRepository supportRepository;
	private static Logger LOGGER = LoggerFactory.getLogger(SupportDataService.class);

	@Override
	public Boolean checkIfBestandReferentieNummerExists(String ref) throws DataServiceException {
		if(ref != null && !ref.isEmpty()) {
			try {
				List<SupportBestand> sBl = supportBestandRepository.findSupportTicketBestandenByReferentieNummer(ref);
				return sBl.size() > 0;
			} catch(Exception e) {
				LOGGER.error("Error in checkIfBestandReferentieNummerExists: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("checkIfBestandReferentieNummerExists: Parameter ref(String) cannot be null or empty");
			throw new DataServiceException("Parameter ref(String) cannot be null or empty");
		}
	}

	@Override
	public Boolean checkIfReferentieNummerExists(String ref) throws DataServiceException {
		if(ref != null && !ref.isEmpty()) {
			try {
				List<Support> sList = supportRepository.findSupportTicketsByReferentieNummer(ref);
				return sList.size() > 0;
			} catch(Exception e) {
				LOGGER.error("Error in checkIfReferentieNummerExists: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("checkIfReferentieNummerExists: Parameter ref(String) cannot be empty");
			throw new DataServiceException("Parameter ref(String) cannot be empty");
		}
	}

	@Override
	public Boolean checkIfSupportBestandWithSupportIdExists(Integer sId) throws DataServiceException {
		if(sId != null) {
			try {
				List<SupportBestand> sBl = supportBestandRepository.findSupportTicketBestandenBySupportId(sId);
				return sBl.size() > 0;
			} catch(Exception e) {
				LOGGER.error("Error in checkIfSupportBestandWithSupportIdExists: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("checkIfSupportBestandWithSupportIdExists: Parameter sId(Integer) cannot be null");
			throw new DataServiceException("Parameter sId(Integer) cannot be null");
		}
	}

	public Boolean checkIfSupportExists(Integer sId) throws DataServiceException {
		if(sId != null) {
			try {
				return supportRepository.findSupportTicketBySupportId(sId) != null;
			} catch(Exception e) {
				LOGGER.error("Error in checkIfSupportExists: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("checkIfSupportExists: Parameter sId(Integer) cannot be null");
			throw new DataServiceException("Parameter sId(Integer) cannot be null");
		}
	}

	@Override
	@Transactional
	public void deleteSupportTicket(Integer sId) throws DataServiceException {
		if(sId != null) {
			try {
				supportRepository.deleteSupportTicket(sId);
			} catch(Exception e) {
				LOGGER.error("Error in deleteSupportTicket: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("deleteSupportTicket: Parameter sId(Integer) cannot be null");
			throw new DataServiceException("Parameter sId(Integer) cannot be null");
		}
	}

	@Override
	@Transactional
	public void deleteSupportTicketBestandenBySupportId(Integer sId) throws DataServiceException {
		if(sId != null) {
			try {
				supportBestandRepository.deleteSupportTicketBestandBySupportId(sId);
			} catch(Exception e) {
				LOGGER.error("Error in deleteSupportTicketBestandenbySupportId: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("deleteSupportTicketBestandenBySupportId: Parameter sId(Integer) cannot be null");
			throw new DataServiceException("Parameter sId(Integer) cannot be null");
		}
	}

	@Override
	public List<Support> findExpiredObjectionSupportTickets(int expirationInDays) throws DataServiceException {
		try {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -expirationInDays);
			Date expirationDate = c.getTime();

			return supportRepository.findSupportTicketsMadeBeforeDate(expirationDate);
		} catch(Exception e) {
			LOGGER.error("Error in findExpiredObjectionSupportTickets: " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Object[]> findStartAndEndOfSupportTicketChainByMeldingId(Integer mId) throws DataServiceException {
		if(mId != null) {
			try {
				List<Object[]> result = supportRepository.findStartAndEndOfSupportTicketChainByMeldingId(mId);
				if(result.get(0)[0] == null && result.get(0)[1] == null) {return null;} else {return result;}
			} catch(Exception e) {
				LOGGER.error("Error in findStartAndEndOfSupportTicketChainByMeldingId: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findStartAndEndOfSupportTicketChainByMeldingId: Parameter mId(Integer) cannot be null");
			throw new DataServiceException("Parameter mId(Integer) cannot be null");
		}
	}

	@Override
	public SupportBestand findSupportTicketBestandBySupportBestandId(Integer sBId) throws DataServiceException {
		if(sBId != null) {
			try {
				return supportBestandRepository.findSupportTicketBestandBySupportBestandId(sBId);
			} catch(Exception e) {
				LOGGER.error("Error in findSupportTicketBestandBySupportBestandId: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findSupportTicketBestandBySupportBestandId: Parameter sBId(Integer) cannot be null");
			throw new DataServiceException("Parameter sBId(Integer) cannot be null");
		}
	}

	@Override
	public Support findSupportTicketBySupportId(Integer sId) throws DataServiceException {
		if(sId != null) {
			try {
				return supportRepository.findSupportTicketBySupportId(sId);
			} catch(Exception e) {
				LOGGER.error("Error in findSupportTicketBySupportId: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findSupportTicketBySupportId: Parameter sId(Integer) cannot be null");
			throw new DataServiceException("Parameter sId(Integer) cannot be null");
		}
	}

	@Override
	public Page<Support> findSupportTicketsAboutBedrijfByBedrijfId(Integer bId, Pageable p) throws DataServiceException {
		if(bId != null && p != null) {
			try {
				return supportRepository.findSupportTicketsAboutBedrijfByBedrijfId(bId, p);
			} catch(Exception e) {
				LOGGER.error("Error in findSupportTicketsAboutBedrijfByBedrijfId: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findSupportTicketsAboutBedrijfByBedrijfId: Parameter bId(Integer) or p(Pageable) cannot be null");
			throw new DataServiceException("Parameter bId(Integer) or p(Pageable) cannot be null");
		}
	}
	
	@Override
	public List<Support> findAllSupportTicketsByGebruikerId(Integer gId) throws DataServiceException {
		if(gId != null) {
			try {
				return supportRepository.findAllSupportTicketsByGebruikerId(gId);
			} catch(Exception e) {
				LOGGER.error("Error in findAllSupportTicketsOfGebruikerId: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findAllSupportTicketsByBedrijfId: Parameter bId(Integer) cannot be null");
			throw new DataServiceException("Parameter bId(Integer) cannot be null");
		}
	}
	
	@Override
	public List<Support> findAllSupportTicketsByBedrijfId(Integer bId) throws DataServiceException {
		if(bId != null) {
			try {
				return supportRepository.findAllSupportTicketsByBedrijfId(bId);
			} catch(Exception e) {
				LOGGER.error("Error in findAllSupportTicketsByBedrijfId: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findAllSupportTicketsByBedrijfId: Parameter bId(Integer) cannot be null");
			throw new DataServiceException("Parameter bId(Integer) cannot be null");
		}
	}

	@Override
	public Page<Object[]> findSupportTicketsByGebruikerId(Integer gId, Pageable p) throws DataServiceException {
		if(gId != null && p != null) {
			try {
				return supportRepository.findSupportTicketsByGebruikerId(gId, p);
			} catch(Exception e) {
				LOGGER.error("Error in findSupportTicketsByGebruikerId: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findSupportTicketsByGebruikerId: Parameter gId(Integer) or p(Pageable) cannot be null");
			throw new DataServiceException("Parameter gId(Integer) or p(Pageable) cannot be null");
		}
	}

	@Override
	public List<Support> findSupportTicketsByReferentieNummer(String ref) throws DataServiceException {
		if(ref != null && !ref.isEmpty()) {
			try {
				List<Support> supports = supportRepository.findSupportTicketsByReferentieNummer(ref);
				if(supports != null) return supports;
				else throw new DataServiceException("No supporttickets found for referentie " + ref);
			} catch(Exception e) {
				LOGGER.error("Error in findSupportTicketsByReferentieNummer: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findSupportTicketsByReferentieNummer: Parameter ref(String) cannot be empty");
			throw new DataServiceException("Parameter ref(String) cannot be empty");
		}
	}

	@Override
	public Page<Support> findSupportTicketsBySupportType(String type, Pageable p) throws DataServiceException {
		if(type != null && !type.isEmpty() && p != null) {
			try {
				return supportRepository.findSupportTicketsBySupportType(type, p);
			} catch(Exception e) {
				LOGGER.error("Error in findSupportTicketsBySupportType: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("findSupportTicketsBySupportType: Parameter type(String) or p(Pageable) cannot be null");
			throw new DataServiceException("Parameter type(String) or p(Pageable) cannot be null");
		}
	}

	@Override
	@Transactional
	public Support saveSupportTicket(Support s) throws DataServiceException {
		if(s != null) {
			try {
				return supportRepository.save(s);
			} catch(Exception e) {
				LOGGER.error("Error in saveSupportTicket: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("saveSupportTicket: Parameter s(Support) cannot be null");
			throw new DataServiceException("Parameter s(Support) cannot be null");
		}
	}

	@Override
	@Transactional
	public SupportBestand saveSupportTicketBestand(SupportBestand sb) throws DataServiceException {
		if(sb != null) {
			try {
				return supportBestandRepository.save(sb);
			} catch(Exception e) {
				LOGGER.error("Error in saveSupportTicketBestand: " + e.getMessage());
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("saveSupportTicketBestand: Parameter sb(SupportBestand) cannot be null");
			throw new DataServiceException("Parameter sb(SupportBestand) cannot be null");
		}
	}
}
