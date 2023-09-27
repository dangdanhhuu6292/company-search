package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Eigenschap;

import java.util.List;

/**
 * Data Service bean with implemented functionality for Eigenschap.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the EigenschapDataService must implement to support interaction between a Business Object and
 * the EigenschapRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface EigenschapDataService {
    /**
     * Retrieves a BedrijfStatus object, based on the passed gebruiker_ID.
     *
     * @param code      a String representing a Eigenschap
     * @return          a Eigenschap object or null when the Eigenschap object could not be retrieved.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    Eigenschap findByPrimaryKey(String code) throws DataServiceException;

    /**
     * Finds a List of {@link nl.devoorkant.sbdr.data.model.Eigenschap} objects, based on the passed proces_ID.
     *
     * @param actief    a boolean indicating the status of Actief
     * @return          a List of {@link nl.devoorkant.sbdr.data.model.Eigenschap} objects or null when no Eigenschap objects are available.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    List<Eigenschap> findByActief(boolean actief) throws DataServiceException;

    /**
     * Saves the passed Eigenschap Entity.<br/>
     * <p/>
     * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
     *
     * The Eigenschap Entity passed as argument will be validated {@link #validate(nl.devoorkant.sbdr.data.model.Eigenschap)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The Eigenschap indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param eigenschap 	the {@link nl.devoorkant.sbdr.data.model.Eigenschap} object to save.
     * @return 			            the saved {@link nl.devoorkant.sbdr.data.model.Eigenschap} object.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    Eigenschap save(Eigenschap eigenschap) throws DataServiceException;
    
}
