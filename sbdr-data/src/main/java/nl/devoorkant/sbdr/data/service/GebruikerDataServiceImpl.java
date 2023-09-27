package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.repository.BedrijfRepository;
import nl.devoorkant.sbdr.data.repository.GebruikerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

@Service("gebruikerDataService")
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class GebruikerDataServiceImpl implements GebruikerDataService {

	@Autowired
	private BedrijfRepository bedrijfRepository;

	@Autowired
	private GebruikerRepository gebruikerRepository;

	@Transactional
	public void delete(Gebruiker gebruiker) throws DataServiceException {
		try {
			gebruikerRepository.delete(gebruiker);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Gebruiker> findActiveGebruikersOfKlantGebruiker(Integer klantGebruikerId, Pageable pageable) throws DataServiceException {
		Page<Gebruiker> result = null;

		try {
			Bedrijf bedrijf = bedrijfRepository.findByKlantGebruikersId(klantGebruikerId);

			if (bedrijf != null)
				result = gebruikerRepository.findActiveGebruikersOfBedrijf(bedrijf.getBedrijfId(), pageable);

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	public List<Gebruiker> findAllGebruikers() throws DataServiceException {
		try {
			return gebruikerRepository.findAll();
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Gebruiker> findAllGebruikersOfBedrijf(Integer bedrijfId) throws DataServiceException {
		try {
			Pageable pageRequest = new PageRequest(0, 1000); // maximized

			Page<Gebruiker> page = gebruikerRepository.findGebruikersOfBedrijf(bedrijfId, pageRequest);

			if (page != null && page.hasContent()) return page.getContent();
			else return null;

		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Gebruiker> findAllGebruikersOfBedrijfByBedrijfId(Integer bedrijfId) throws DataServiceException {
		try {
			return gebruikerRepository.findAllGebruikersOfBedrijfByBedrijfId(bedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Gebruiker> findAllGebruikersWithAlertsByBedrijfId(Integer bedrijfId) throws DataServiceException {
		try{
			return gebruikerRepository.findAllGebruikersWithAlertsByBedrijfId(bedrijfId);
		} catch (Exception e){
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Gebruiker> findAllHoofdAndKlantGebruikersOfBedrijf(Integer bedrijfId) throws DataServiceException {
		try {
			return gebruikerRepository.findAllHoofdAndKlantGebruikersOfBedrijf(bedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Gebruiker findByActivatieCode(String activatieCode) throws DataServiceException {
		Gebruiker result = null;

		try {
			List<Gebruiker> gebruikers = gebruikerRepository.findByActivatieCode(activatieCode);

			if (gebruikers != null && gebruikers.size() == 1) {
				result = gebruikers.get(0);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public Gebruiker findByActivatieCodeBedrijfManaged(String activatieCode) throws DataServiceException {
		Gebruiker result = null;

		try {
			List<Gebruiker> gebruikers = gebruikerRepository.findByActivatieCodeBedrijfManaged(activatieCode);
	
			if (gebruikers != null && gebruikers.size() == 1) {
				result = gebruikers.get(0);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}	

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, isolation = Isolation.SERIALIZABLE)
	public Gebruiker findByGebruikerid(Integer gebruikerid) throws DataServiceException {
		try {
			Optional<Gebruiker> gebruiker = gebruikerRepository.findById(gebruikerid);
			return gebruiker != null ? gebruiker.get() : null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, isolation = Isolation.SERIALIZABLE)
	public Gebruiker findByGebruikersnaam(String gebruikersnaam) throws DataServiceException {
		Gebruiker result = null;
		
		try {
			List<Gebruiker> gebruikers = gebruikerRepository.findByGebruikersNaam(gebruikersnaam);
			if (gebruikers != null && gebruikers.size() > 0) {
				result = (Gebruiker) gebruikers.get(0);
				
				return result;
			}
			else return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, isolation = Isolation.SERIALIZABLE)
	public Gebruiker findByGebruikersnaamOfBedrijf(String gebruikersnaam, Integer bedrijfId) throws DataServiceException {
		try {
			List<Gebruiker> gebruikers = gebruikerRepository.findByGebruikersNaamOfBedrijf(gebruikersnaam, bedrijfId);
			if (gebruikers != null && gebruikers.size() > 0) return gebruikers.get(0);
			else return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, isolation = Isolation.SERIALIZABLE)
	public Gebruiker findById(Integer gebruikerid) throws DataServiceException {
		return this.findByGebruikerid(gebruikerid);
	}

	@Override
	public List<Gebruiker> findGebruikersByRolCode(String rolCode) throws DataServiceException {
		try {
			return gebruikerRepository.findGebruikersByRolCode(rolCode);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Gebruiker findSbdrGebruiker() throws DataServiceException {
		try {
			return gebruikerRepository.findSbdrGebruiker();
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Gebruiker findSysteemGebruiker() throws DataServiceException {
		try {
			return gebruikerRepository.findSysteemGebruiker();
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Long getCountActivatedUsers() throws DataServiceException {
		Long result = null;

		try {
			List<Long> results = gebruikerRepository.findCountActivatedUsers();

			if (results != null) {
				result = (Long) results.get(0);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Long getCountAllUsers() throws DataServiceException {
		Long result = null;

		try {
			List<Long> results = gebruikerRepository.findCountAllUsers();

			if (results != null) {
				result = (Long) results.get(0);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Long getCountDeactivatedUsers() throws DataServiceException {
		Long result = null;

		try {
			List<Long> results = gebruikerRepository.findCountDeactivatedUsers();

			if (results != null) {
				result = (Long) results.get(0);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Long getCountNonActivatedUsers() throws DataServiceException {
		Long result = null;

		try {
			List<Long> results = gebruikerRepository.findCountNonActivatedUsers();

			if (results != null) {
				result = (Long) results.get(0);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	//@Transactional
	@Override
	public Gebruiker save(Gebruiker person) throws DataServiceException {
		try {
			return gebruikerRepository.save(person);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

}
