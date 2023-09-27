package nl.devoorkant.sbdr.data.service;

import java.util.List;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Websiteparam;
import nl.devoorkant.sbdr.data.repository.WebsiteparamRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data Service bean with implemented functionality for Websiteparam.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * Implements the functionality exposed by the WebsiteparamDataService interface.
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Martijn Bruinenberg
 * @version         %I%
 */

@Service("websiteparamDataService")
@Transactional(readOnly = true)
public class WebsiteparamDataServiceImpl implements WebsiteparamDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsiteparamDataServiceImpl.class);

    @Autowired
    private WebsiteparamRepository websiteparamRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Websiteparam find() throws DataServiceException {

        try {
            List<Websiteparam> websiteparams = websiteparamRepository.findAll();
            
            if (websiteparams != null && websiteparams.size() == 1)
            	return websiteparams.get(0);
            else {
                LOGGER.debug("Cannot retrieve Websiteparam.");
                return null;
            }
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * Saves the presented {@link nl.devoorkant.sbdr.data.model.Websiteparam} Object.
     *
     * @param wachtwoord     the {@link nl.devoorkant.sbdr.data.model.Websiteparam} Object to save
     * @throws              nl.devoorkant.sbdr.data.DataServiceException when saving of the Websiteparam Object fails
     */
    @Override
    @Transactional
    public Websiteparam save(Websiteparam websiteparam) throws DataServiceException {
        LOGGER.info("Start.");
        Websiteparam result = null;

        try {
            result = websiteparamRepository.save(websiteparam);

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(Websiteparam websiteparam) throws DataServiceException {
        try {
            websiteparamRepository.delete(websiteparam);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

}
