package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Klant;

import java.util.Collection;
import java.util.List;

/**
 * Interface exposing data services for Bedrijf Objects.
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

public interface KlantDataService {
	/**
	 * Remove the associated Klant
	 * record from the data repository.
	 *
	 * @param klant
	 */
	void delete(Klant klant) throws DataServiceException;

	/**
	 * Get KlantData with the associated id
	 *
	 * @param gebruiker id - id
	 * @return Klant object
	 */
	Klant findByGebruikerId(Integer id) throws DataServiceException;

	/**
	 * Get KlantData with the associated gebruikersnaam
	 *
	 * @param gebruikersnaam - string
	 * @return Klant object
	 */
	Klant findByGebruikersNaam(String gebruikersnaam) throws DataServiceException;

	/**
	 * Get KlantData with the associated activatieCode
	 *
	 * @param activatieCode - string
	 * @return Klant object
	 */
	Klant findByKlantActivatieCode(String activatieCode) throws DataServiceException;

	/**
	 * @param id is identifier of Klant
	 * @return Klant record or null
	 * @throws DataServiceException
	 */
	Klant findByKlantId(Integer id) throws DataServiceException;

	/**
	 * Get KlantData belonging to Bedrijf
	 *
	 * @param gebruiker id - id
	 * @return Klant object
	 */
	Klant findKlantOfBedrijfByBedrijfId(Integer bedrijfId) throws DataServiceException;

	Klant findKlantOfBedrijfByBedrijfIdAndStatusCode(Integer bId, Collection<String> statusCodes) throws DataServiceException;

	/**
	 * Get KlantData belonging to Gebruiker
	 *
	 * @param gebruiker id - id
	 * @param bedrijf id = bedrijf van klant
	 * @return Klant object
	 */
	Klant findKlantOfGebruikerByGebruikerId(Integer gebruikerId, Integer bedrijfId) throws DataServiceException;

	List<Object[]> findKlantenToProcessExactOnline() throws DataServiceException;

	Klant findNewKlantById(Integer gebruikerId) throws DataServiceException;

	List<Klant> findNonActivatedKlanten() throws DataServiceException;

	List<Klant> findNonVerifiedKlanten() throws DataServiceException;

	/**
	 * Find all Klant records which are in Status REGISTRATIE or PROSPECT
	 * and which are more than 'daysoverdue' days ago created
	 *
	 * @param daysoverdue days overdue
	 * @return
	 * @throws DataServiceException
	 */
	List<Klant> findOverdueNewKlanten(int daysoverdue) throws DataServiceException;

	/**
	 * @param daysoverdue
	 * @return
	 * @throws DataServiceException
	 */
	List<Klant> findReminderNewKlanten(int daysoverdue) throws DataServiceException;

	/**
	 * Creates new klant record in dirty way
	 * 
	 * @param gebruikerId
	 * @throws DataServiceException
	 */
	public void createNewKlantRecord(Integer gebruikerId) throws DataServiceException;	
	
	/**
	 * Save the state of the provided
	 * Bedrijf object into the data
	 * repository.
	 *
	 * @param klant
	 * @return Klant object
	 */
	Klant save(Klant klant) throws DataServiceException;

}
