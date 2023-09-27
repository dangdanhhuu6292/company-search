package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.ActivatieCodeTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikerBedrijfTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikerTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikersDetails;
import nl.devoorkant.sbdr.business.transfer.LoginAllowed;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GebruikerService {

	/**
	 * Finds activationcode to be activated of user and returns ActiviatieCodeTransfer object
	 * 
	 * @param gebruikerNaam
	 * @param activatieCode
	 * @return
	 * @throws ServiceException
	 */
	ActivatieCodeTransfer findActivatieCodeOfGebruiker(String gebruikerNaam, String activatieCode) throws ServiceException;
	
	/**
	 * Activates the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object identified by the presented id.
	 *
	 * @param gebruikersNaam a String identifying username of Gebruiker
	 * @param activatieCode  a String representing the {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object of Gebruiker to activate
	 * @param password       a String representing the user password
	 * @return ErrorService object
	 * @throws ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	ErrorService activateGebruiker(String gebruikersNaam, String activationCode, String password) throws ServiceException;

	/**
	 * Updates the {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object of {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object with new password.
	 *
	 * @param gebruikerId a Integer representing the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object with password to update
	 * @param gebruikerIdIngelogd Integer representing user logged in who want's to change password of gebruikerId
	 * @param bedrijfId Integer representing company of gebruikerId
	 * @param String      existing password to validate
	 * @param String      new password
	 * @param boolean     resetWachtwoord is true if passoword reset procedure is enabled
	 * @return ErrorService object
	 * @throws ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	ErrorService changeWachtwoordGebruiker(Integer gebruikerId, Integer gebruikerIdIngelogd, Integer bedrijfId, String bestaandWachtwoord, String nieuwWachtwoord, boolean resetWachtwoord) throws ServiceException;

	/**
	 * Deletes the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object identified by the presented id.
	 *
	 * @param id a Long representing the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object to delete
	 * @param bedrijfId a Long representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object where the user belongs to
	 * @return ErrorService object
	 * @throws ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	ErrorService deleteGebruiker(Integer id, Integer bedrijfId) throws ServiceException;

	/**
	 * Finds page list of gebruikers {@link Gebruiker} by its klantgebruiker.
	 *
	 * @param klantGebruikerId the primary key of {@link Gebruiker}
	 * @param bedrijfId the primary key of {@link Bedrijf} of company of users
	 * 
	 * @param pageable         params for paging
	 * @return a {@link Gebruiker} instance or null in case the record could not be found.
	 */
	PageTransfer<GebruikerTransfer> findActiveGebruikersOfKlantGebruiker(Integer klantGebruikerId, Integer bedrijfId, Pageable pageable) throws ServiceException;

	/**
	 * Finds a {@link Gebruiker} by its primary key.
	 *
	 * @param gebruikerId the primary key of {@link Gebruiker}
	 * @return a {@link Gebruiker} instance or null in case the record could not be found.
	 */
	Gebruiker findByGebruikerId(Integer gebruikerId) throws ServiceException;
	
	/**
	 * Finds all active companies of user. Own + managed companies
	 * 
	 * @param gebruikerId
	 * @param currentBedrijfId
	 * @return
	 * @throws ServiceException
	 */
	List<GebruikerBedrijfTransfer> findGebruikerBedrijvenByGebruikerId(Integer gebruikerId, Integer currentBedrijfId) throws ServiceException;

	Klant findKlantOfBedrijfByBedrijfIdAndStatusCode(Integer bId, Collection<String> statusCodes) throws ServiceException;

	/**
	 * @param bedrijfId
	 * @return
	 * @throws ServiceException
	 */
	Klant findKlantgebruikerByBedrijfId(Integer bedrijfId) throws ServiceException;

	/**
	 * Get KlantData with the associated id
	 *
	 * @param gebruiker id - id
	 * @return Klant object
	 */
	// Klant findKlantgebruikerById(Integer gebruikerId) throws ServiceException;

	Gebruiker findSbdrGebruiker() throws ServiceException;

	/**
	 * Finds a {@link Gebruiker} by its primary key.
	 *
	 * @param gebruikerId the id of {@link Gebruiker}
	 * @param bedrijfId the company of the user
	 * @return a {@link GebruikerTransfer} instance or null in case the record could not be found.
	 */
	GebruikerTransfer findTransferByGebruikerId(Integer gebruikerId, Integer bedrijfId) throws ServiceException;

	/**
	 * Finds a {@link Gebruiker} by its primary key.
	 *
	 * @param gebruikersNaam the login name of {@link Gebruiker}
	 * @param bedrijfId the company of the user
	 * @return a {@link GebruikerTransfer} instance or null in case the record could not be found.
	 */
	GebruikerTransfer findTransferByGebruikersnaam(String gebruikersNaam, Integer bedrijfId) throws ServiceException;

	/**
	 * Changes the Wachtwoord object to prepare for a new password
	 *
	 * @param gebruikersNaam string containing the gebruikersNaam of the Gebruiker
	 * @throws ServiceException
	 */
	ErrorService forgotWachtwoord(String gebruikersNaam) throws ServiceException;

	/**
	 * Check correct policy for user on specific action
	 *
	 * @param gebruikerId      User id of user performing the action
	 * @param checkBevoegdheid Policy to be checked against user
	 * @param bedrijfId the company of the user
	 * @return true on user policy is valid, false otherwise
	 * @throws ServiceException
	 */
	boolean hasRightToDo(Integer gebruikerId, Integer bedrijfId, EBevoegdheid checkBevoegdheid) throws ServiceException;

	/**
	 * Updates the {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object of {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object with new password.
	 *
	 * @param activationCode a String representing the {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object with password to update
	 * @param String wachtwoord
	 * @return ErrorService object
	 * @throws ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	//ErrorService changeWachtwoordGebruiker(String activationCode, String wachtwoord) throws ServiceException;

	/**
	 * Check if Gebruiker is allowed to login
	 *
	 * @param gebruikersDetails GebruikersDetails - gebruiker details
	 * @param readonly  - true = no update of blocked account
	 * @return boolean
	 */
	LoginAllowed isGebruikerAllowedToLogin(GebruikersDetails gebruikersDetails, boolean readonly) throws ServiceException;

	/**
	 * Check if Gebruiker is allowed to login
	 *
	 * @param gebruikersnaam
	 * @param gebruikersDetails GebruikersDetails - gebruiker details
	 * @param readonly       - true = no update of blocked account
	 * @return
	 * @throws ServiceException
	 */
	LoginAllowed isGebruikerAllowedToLogin2(String gebruikersnaam, GebruikersDetails gebruikersDetails, boolean readonly, boolean gebruikersnaamIsMCK) throws ServiceException;

	/*
	 * Check if gebruiker is of klant and not of SBDR 
	 * 
	 */
	boolean isGebruikerOfKlant(Integer gebruikerId, Integer bedrijfId) throws ServiceException;

	/**
	 * @param signature
	 * @param gebruikerId
	 * @return
	 * @throws ServiceException
	 */
	boolean logoutGebruiker(String signature, Integer gebruikerId) throws ServiceException;

	/**
	 * Changes the Wachtwoord object, based on the passed gebruikersNaam.
	 *
	 * @param gebruikersNaam string containing the gebruikersNaam of the Gebruiker
	 * @param activationId   unique string used for validation
	 * @param newPassword    string containing the new password
	 * @return ErrorService
	 * @throws ServiceException
	 */
	ErrorService resetWachtwoord(String gebruikersNaam, String activationId, String newPassword) throws ServiceException;

	/**
	 * Saves the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object identified by the gebruiker.
	 *
	 * @param gebruiker  a Gebruiker representing the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object to save
	 * @param isBedrijfManaged company of gebruiker is a managed Bedrijf to which Gebruiker has certain rights
	 * @param wachtwoord a Wachtwoord representing the {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object to save
	 * @param Map        with roles
	 * @return ErrorService object
	 * @throws ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	ErrorService saveGebruiker(Gebruiker gebruiker, boolean isBedrijfManaged, Map<String, Boolean> rollen, Wachtwoord wachtwoord) throws ServiceException;

	/**
	 * @param gebruikersnaam
	 * @param loginok
	 * @return
	 * @throws ServiceException
	 */
	ErrorService updateLoginAttempts(String gebruikersnaam, boolean loginok, boolean gebruikersnaamIsMCK) throws ServiceException;

	/**
	 * @param userId
	 * @param showHelp
	 * @return
	 * @throws ServiceException
	 */
	boolean updateShowHelp(Integer userId, Integer showHelp) throws ServiceException;

	/**
	 * Validate username and password to existing user
	 *
	 * @param gebruikersnaam
	 * @param gebruikerswachtwoord
	 * @return
	 * @throws ServiceException
	 */
	boolean validateUserData(String gebruikersnaam, String gebruikerswachtwoord) throws ServiceException;

}
