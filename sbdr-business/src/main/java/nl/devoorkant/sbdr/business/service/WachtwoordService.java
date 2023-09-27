package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import nl.devoorkant.sbdr.data.model.WachtwoordStatus;
import nl.devoorkant.validation.ValidationObject;

import java.util.List;

/**
 * Interface exposing functionality for Wachtwoorden.
 * <p/>
 * EDO - Applicatie voor het verwerken van Export Documenten
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

public interface WachtwoordService {

	/**
	 * Blocks the passed Wachtwoord.<br/>
	 *
	 * @param poWachtwoord_ID an Integer representing the Wachtwoord
	 * @return true when Wachtwoord is blocked, false when blocking failed..
	 * @throws ServiceException
	 */
	boolean blockWachtwoord(Integer poWachtwoord_ID) throws ServiceException;

	/**
	 * Change the Wachtwoord for the passed Gebruiker<br/>
	 *
	 * @param poGebruiker    an Gebruiker Object, also containing the Wachtwoord Object to validate.
	 * @param pstrWachtwoord a String containing the new Wachtwoord
	 * @return true when Wachtwoord is changed, false when blocking failed..
	 * @throws ServiceException
	 */
	boolean changeWachtwoord(Gebruiker poGebruiker, String pstrWachtwoord) throws ServiceException;

	/**
	 * Generate a new wachtwoord.<br/>
	 * <p/>
	 * The criteria for a wachtwoord are:<br/>
	 * <ol>
	 * <li>Must contain one charcacter be alfanumeric and uppercase</li>
	 * <li>Must contain one charcacter be alfanumeric and lowercase</li>
	 * <li>Must contain one numeric value</li>
	 * <li>There must one charcacter be alfanumeric and uppercase</li>
	 * </ol>
	 *
	 * @return a String containing a generated random wachtwoord
	 */
	String generateWachtwoord();

	/**
	 * Returns the active {@link WachtwoordStatus} Entities .<br/>
	 *
	 * @return a List of actieve {@link WachtwoordStatus} Entities.
	 */
	List<WachtwoordStatus> getActiveWachtwoordStatussen() throws ServiceException;

	/**
	 * Returns a new initialised Wachtwoord Object.<br/>
	 *
	 * @return a new Wachtwoord Object or null when the creation of the new  Wachtwoord Object fails
	 * @throws ServiceException
	 */
	Wachtwoord getNewWachtwoord() throws ServiceException;

	/**
	 * Returns a new initialised Wachtwoord Object.<br/>
	 *
	 * @param pstrUnencryptedWachtwoord a String containing the password to encrypt.
	 * @return the Wachtwoord Object
	 * @throws ServiceException
	 */
	Wachtwoord getNewWachtwoord(String pstrUnencryptedWachtwoord) throws ServiceException;

	/**
	 * Retrieves a Wachtwoord object, based on the passed wachtwoord_ID.
	 *
	 * @param wachtwoord_ID an Integer representing a Gebruiker
	 * @return a Wachtwoord object or null when the Gebruiker object could not be retrieved.
	 * @throws ServiceException
	 */
	Wachtwoord getWachtwoord(Integer wachtwoord_ID) throws ServiceException;

	/**
	 * Retrieves a {@link WachtwoordStatus} Object, based on the passed code.
	 *
	 * @param pstrCode a String representing the {@link WachtwoordStatus} Object to retrieve
	 * @return a {@link WachtwoordStatus} Object or null when the WachtwoordStatus could not be retrieved.
	 * @throws ServiceException
	 */
	WachtwoordStatus getWachtwoordStatus(String pstrCode) throws ServiceException;

	/**
	 * Release the Wachtwoord for the passed Gebruiker.<br/>
	 * <p/>
	 * This function releases the Wachtwoord, but does NOT save the action. This is because the release is not complete without
	 * changes to the Gebruiker object.
	 *
	 * @param gebruiker a Gebruiker whose Wachtwoord has to be released.
	 * @return the Gebruiker object with the released (but NOT saved) Wachtwoord.
	 * @throws ServiceException
	 */
	Gebruiker releaseWachtwoord(Gebruiker gebruiker) throws ServiceException;

	/**
	 * Resets the Wachtwoord for the passed {@link Gebruiker} Object.<br/>
	 *
	 * @param poGebruiker a {@link Gebruiker} Object whose {@link Wachtwoord} has to be reset.
	 * @return true when {@link Wachtwoord} is changed, false when blocking failed..
	 * @throws ServiceException
	 */
	boolean resetWachtwoord(Gebruiker poGebruiker) throws ServiceException;

	/**
	 * Saves the Wachtwoord Object, part of the passed Persoon Object.<br/>
	 * <p/>
	 * The Wachtwoord Object passed as argument will be validated {@link #validateWachtwoord(nl.devoorkant.edo.data.model.Gebruiker)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * <li> Valid. When the passed Wachtwoord Object does not have an ID, a new Wachtwoord will be created, otherwise the Wachtwoord indicated by the ID will be updated. These
	 * actions can have two possible outcomes as well:<br/>
	 * <ol>
	 * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * </ol>
	 * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
	 *
	 * @param gebruiker a {@link Gebruiker} Object, containing the {@link Wachtwoord} Object to save.
	 * @return a {@link Gebruiker} Object, containing the saved {@link Wachtwoord} Object.
	 */
	Gebruiker saveWachtwoord(Gebruiker gebruiker);

	/**
	 * Saves the passed {@link WachtwoordStatus} Object.<br/>
	 *
	 * @param poWachtwoordStatus the {@link WachtwoordStatus} Object to save.
	 * @return the saved {@link WachtwoordStatus} Object.
	 */
	WachtwoordStatus saveWachtwoordStatus(WachtwoordStatus poWachtwoordStatus);

	void sendPasswordResetReminderEmailsQuartzJob() throws ServiceException;

	/**
	 * Checks the validity of a {@link Wachtwoord} Object, passed as part of a {@link Gebruiker}  Object.<br/>
	 *
	 * @param poGebruiker a {@link Gebruiker} Object, also containing the {@link Wachtwoord} Object to validate.
	 * @return a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
	 */
	ValidationObject validateWachtwoord(Gebruiker poGebruiker);
}
