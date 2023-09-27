package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface exposing data services for Gebruiker Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * <p/>
 * Defines the functionality that the GebruikerDataService must implement to support interaction between a Business Object and
 * the GebruikerRepository
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Bas Dekker
 * @version %I%
 */

public interface GebruikerDataService {

	/**
	 * Remove the associated Gebruiker
	 * record from the data repository.
	 *
	 * @param gebruiker
	 */
	void delete(Gebruiker gebruiker) throws DataServiceException;

	/**
	 * Get all Gebruiker objects of klant bedrijf.
	 *
	 * @return page collection of Gebruiker objects
	 */
	Page<Gebruiker> findActiveGebruikersOfKlantGebruiker(Integer klantGebruikerId, Pageable pageable) throws DataServiceException;

	/**
	 * Get all Gebruiker objects.
	 *
	 * @return collection of Gebruiker objects
	 */
	List<Gebruiker> findAllGebruikers() throws DataServiceException;

	List<Gebruiker> findAllGebruikersOfBedrijf(Integer bedrijfId) throws DataServiceException;

	List<Gebruiker> findAllGebruikersOfBedrijfByBedrijfId(Integer bedrijfId) throws DataServiceException;

	List<Gebruiker> findAllGebruikersWithAlertsByBedrijfId(Integer bedrijfId) throws DataServiceException;

	List<Gebruiker> findAllHoofdAndKlantGebruikersOfBedrijf(Integer bedrijfId) throws DataServiceException;

	/**
	 * Get  Gebruiker object with activatieCode.
	 *
	 * @return Gebruiker with corresponding Wachtwoord with activatieCode
	 */
	Gebruiker findByActivatieCode(String activatieCode) throws DataServiceException;
	Gebruiker findByActivatieCodeBedrijfManaged(String activatieCode) throws DataServiceException;
	
	Gebruiker findByGebruikerid(Integer gebruikerid) throws DataServiceException;

	/**
	 * Get GebruikerData with the associated gebruikerid
	 *
	 * @param gebruikersnaam - Login gebruikersnaam
	 *
	 * @return Gebruiker object
	 */
	Gebruiker findByGebruikersnaam(String gebruikersnaam) throws DataServiceException;

	Gebruiker findByGebruikersnaamOfBedrijf(String gebruikersnaam, Integer bedrijfId) throws DataServiceException;
	
	/**
	 * Get GebruikerData with the associated gebruikerid
	 *
	 * @param gebruikerid - Id
	 *
	 * @return Gebruiker object
	 */
	Gebruiker findById(Integer gebruikerid) throws DataServiceException;

	List<Gebruiker> findGebruikersByRolCode(String rolCode) throws DataServiceException;

	Gebruiker findSbdrGebruiker() throws DataServiceException;

	Gebruiker findSysteemGebruiker() throws DataServiceException;

	Long getCountActivatedUsers() throws DataServiceException;

	Long getCountAllUsers() throws DataServiceException;

	Long getCountDeactivatedUsers() throws DataServiceException;

	Long getCountNonActivatedUsers() throws DataServiceException;

	/**
	 * Save the state of the provided
	 * Gebruiker object into the data
	 * repository.
	 *
	 * @param gebruiker
	 *
	 * @return Gebruiker object
	 */
	Gebruiker save(Gebruiker gebruiker) throws DataServiceException;
}
