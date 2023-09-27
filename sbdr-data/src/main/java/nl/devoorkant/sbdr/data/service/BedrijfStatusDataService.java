package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.BedrijfStatus;

import java.util.List;

/**
 * Interface exposing data services for BedrijfStatus Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the BedrijfStatusDataService must implement to support interaction between a Business Object and
 * the BedrijfStatusRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface BedrijfStatusDataService {
    /**
     * Retrieves a BedrijfStatus object, based on the passed gebruiker_ID.
     *
     * @param code      a String representing a BedrijfStatus
     * @return          a BedrijfStatus object or null when the BedrijfStatus object could not be retrieved.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    BedrijfStatus findByPrimaryKey(String code) throws DataServiceException;

    /**
     * Finds a List of {@link BedrijfStatus} objects, based on the passed proces_ID.
     *
     * @param actief    a boolean indicating the status of Actief
     * @return          a List of {@link BedrijfStatus} objects or null when no BedrijfStatus objects are available.
     * @throws DataServiceException
     */
    List<BedrijfStatus> findByActief(boolean actief) throws DataServiceException;

    /**
     * Saves the passed BedrijfStatus Entity.<br/>
     * <p/>
     * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
     *
     * The BedrijfStatus Entity passed as argument will be validated {@link #validate(nl.devoorkant.sbdr.data.model.BedrijfStatus)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The BedrijfStatus indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param bedrijfStatus 	the {@link BedrijfStatus} object to save.
     * @return 			            the saved {@link BedrijfStatus} object.
     * @throws DataServiceException
     */
    BedrijfStatus save(BedrijfStatus bedrijfStatus) throws DataServiceException;

}
