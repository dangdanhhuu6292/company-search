package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface exposing data services for Wachtwoord Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the WachtwoordDataService must implement to support interaction between a Business Object and
 * the WachtwoordRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface WachtwoordDataService {

	List<Wachtwoord> findByStatusReset() throws DataServiceException;
    /**
     * Retrieves a Wachtwoord object, based on the passed wachtwoord_ID.
     *
     * @param wachtwoord_ID an Integer representing a Wachtwoord
     * @return              a Wachtwoord object or null when the Wachtwoord object could not be retrieved.
     * @throws DataServiceException
     */
    Wachtwoord findByPrimaryKey(Integer wachtwoord_ID) throws DataServiceException;

    /**
     * Saves the presented {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object.
     *
     * @param wachtwoord     the {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object to save
     * @throws DataServiceException
     */
    @Transactional
    Wachtwoord save(Wachtwoord wachtwoord) throws DataServiceException;

    /**
     * Deletes the passed Wachtwoord Object.
     *
     * @param wachtwoord     the Wachtwoord Object to delete
     * @throws DataServiceException
     */
    @Transactional
    void delete(Wachtwoord wachtwoord) throws DataServiceException;
}
