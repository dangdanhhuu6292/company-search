package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.KlantType;

import java.util.List;

/**
 * Interface exposing data services for KlantType Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the KlantTypeDataService must implement to support interaction between a Business Object and
 * the KlantTypeRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface KlantTypeDataService {
    /**
     * Retrieves a BedrijfStatus object, based on the passed gebruiker_ID.
     *
     * @param code      a String representing a KlantType
     * @return          a KlantType object or null when the KlantType object could not be retrieved.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    KlantType findByPrimaryKey(String code) throws DataServiceException;

    /**
     * Finds a List of {@link KlantType} objects, based on the passed proces_ID.
     *
     * @param actief    a boolean indicating the status of Actief
     * @return          a List of {@link KlantType} objects or null when no KlantType objects are available.
     * @throws DataServiceException
     */
    List<KlantType> findByActief(boolean actief) throws DataServiceException;

    /**
     * Saves the passed KlantType Entity.<br/>
     * <p/>
     * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
     *
     * The KlantType Entity passed as argument will be validated {@link #validate(nl.devoorkant.sbdr.data.model.KlantType)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The KlantType indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param klantType 	the {@link KlantType} object to save.
     * @return 			            the saved {@link KlantType} object.
     * @throws DataServiceException
     */
    KlantType save(KlantType klantType) throws DataServiceException;
}
