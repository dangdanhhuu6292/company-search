package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Configuratie;

/**
 * Data Service bean with implemented functionality for Configuratie.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the ConfiguratieDataService must implement to support interaction between a Business Object and
 * the ConfiguratieRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface ConfiguratieDataService {
    /**
     * Retrieves a BedrijfStatus object, based on the passed gebruiker_ID.
     *
     * @param code      a String representing a Configuratie
     * @return          a Configuratie object or null when the Configuratie object could not be retrieved.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    Configuratie findByPrimaryKey(String code) throws DataServiceException;

    /**
     * Saves the passed Configuratie Entity.<br/>
     * <p/>
     * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
     *
     * The Configuratie Entity passed as argument will be validated {@link #validate(nl.devoorkant.sbdr.data.model.Configuratie)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The Configuratie indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param configuratie 	the {@link nl.devoorkant.sbdr.data.model.Configuratie} object to save.
     * @return 			            the saved {@link nl.devoorkant.sbdr.data.model.Configuratie} object.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    Configuratie save(Configuratie configuratie) throws DataServiceException;
    
}
