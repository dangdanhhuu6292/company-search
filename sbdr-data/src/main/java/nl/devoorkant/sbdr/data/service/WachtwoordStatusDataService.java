package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.WachtwoordStatus;

import java.util.List;

/**
 * Interface exposing data services for WachtwoordStatus Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the WachtwoordStatusDataService must implement to support interaction between a Business Object and
 * the WachtwoordStatusRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface WachtwoordStatusDataService {
	
    String WACHTWOORD_STATUS_CODE_INITIEEL = "INI";
    String WACHTWOORD_STATUS_CODE_BLOCKED = "BLK";
    String WACHTWOORD_STATUS_CODE_ACTIVE = "ACT";
    String WACHTWOORD_STATUS_CODE_RESET = "RES";

    /**
     * Retrieves a WachtwoordStatus object, based on the passed gebruiker_ID.
     *
     * @param code      a String representing a WachtwoordStatus
     * @return          a WachtwoordStatus object or null when the WachtwoordStatus object could not be retrieved.
     * @throws DataServiceException
     */
    WachtwoordStatus findByPrimaryKey(String code) throws DataServiceException;

    /**
     * Finds a List of {@link WachtwoordStatus} objects, based on the passed proces_ID.
     *
     * @param actief    a boolean indicating the status of Actief
     * @return          a List of {@link WachtwoordStatus} objects or null when no WachtwoordStatus objects are available.
     * @throws DataServiceException
     */
    List<WachtwoordStatus> findByActief(boolean actief) throws DataServiceException;

    /**
     * Saves the passed WachtwoordStatus Entity.<br/>
     * <p/>
     * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
     *
     * The WachtwoordStatus Entity passed as argument will be validated {@link #validate(nl.devoorkant.sbdr.data.model.WachtwoordStatus)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The WachtwoordStatus indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param wachtwoordStatus 	the {@link WachtwoordStatus} object to save.
     * @return 			            the saved {@link WachtwoordStatus} object.
     * @throws DataServiceException
     */
    WachtwoordStatus save(WachtwoordStatus wachtwoordStatus) throws DataServiceException;
}
