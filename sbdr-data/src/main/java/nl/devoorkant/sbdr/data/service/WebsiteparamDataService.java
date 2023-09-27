package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Websiteparam;

import org.springframework.transaction.annotation.Transactional;

/**
 * Interface exposing data services for Websiteparam Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the WebsiteparamDataService must implement to support interaction between a Business Object and
 * the WebsiteparamRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Martijn Bruinenberg
 * @version         %I%
 */

public interface WebsiteparamDataService {
    /**
     * 
     */
    Websiteparam find() throws DataServiceException;

    /**
     * 
     */
    @Transactional
    Websiteparam save(Websiteparam websiteparam) throws DataServiceException;

    /**
     * 
     */
    @Transactional
    void delete(Websiteparam websiteparam) throws DataServiceException;
}
