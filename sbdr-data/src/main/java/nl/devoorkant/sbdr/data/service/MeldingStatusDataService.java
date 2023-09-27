package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.MeldingStatus;

import java.util.List;

/**
 * Data Service bean with implemented functionality for MeldingStatus.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the MeldingStatusDataService must implement to support interaction between a Business Object and
 * the MeldingStatusRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface MeldingStatusDataService {

    /**
     * Retrieves a BedrijfStatus object, based on the passed gebruiker_ID.
     *
     * @param code      a String representing a MeldingStatus
     * @return          a MeldingStatus object or null when the MeldingStatus object could not be retrieved.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    MeldingStatus findByPrimaryKey(String code) throws DataServiceException;

    /**
     * Finds a List of {@link MeldingStatus} objects, based on the passed proces_ID.
     *
     * @param actief    a boolean indicating the status of Actief
     * @return          a List of {@link MeldingStatus} objects or null when no MeldingStatus objects are available.
     * @throws DataServiceException
     */
    List<MeldingStatus> findByActief(boolean actief) throws DataServiceException;

    /**
     * Saves the passed MeldingStatus Entity.<br/>
     * <p/>
     * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
     *
     * The MeldingStatus Entity passed as argument will be validated {@link #validate(nl.devoorkant.sbdr.data.model.MeldingStatus)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The MeldingStatus indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param meldingStatus 	the {@link MeldingStatus} object to save.
     * @return 			            the saved {@link MeldingStatus} object.
     * @throws DataServiceException
     */
    MeldingStatus save(MeldingStatus meldingStatus) throws DataServiceException;
    
}
