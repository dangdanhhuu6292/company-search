package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import nl.devoorkant.sbdr.data.repository.WachtwoordRepository;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;


import java.util.List;
import java.util.Optional;

/**
 * Data Service bean with implemented functionality for Wachtwoord.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * Implements the functionality exposed by the WachtwoordDataService interface.
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

@Service("wachtwoordDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class WachtwoordDataServiceImpl implements WachtwoordDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WachtwoordDataServiceImpl.class);

    @Autowired
    private WachtwoordRepository wachtwoordRepository;

	@Override
	public List<Wachtwoord> findByStatusReset() throws DataServiceException{
		try{
			return wachtwoordRepository.findByStatusReset();
		} catch(Exception e){
			throw new DataServiceException(e.getMessage());
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Wachtwoord findByPrimaryKey(Integer wachtwoord_ID) throws DataServiceException {

        try {
            if (wachtwoord_ID != null) {
                Optional<Wachtwoord> wachtwoord = wachtwoordRepository.findById(wachtwoord_ID);
                return wachtwoord != null ? wachtwoord.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve Wachtwoord without a key.");
                return null;
            }
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * Saves the presented {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object.
     *
     * @param wachtwoord     the {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object to save
     * @throws              nl.devoorkant.sbdr.data.DataServiceException when saving of the Wachtwoord Object fails
     */
    @Override
    @Transactional
    public Wachtwoord save(Wachtwoord wachtwoord) throws DataServiceException {
        LOGGER.info("Start.");
        Wachtwoord result = null;

        try {
            if(validate(wachtwoord).isValid()) {

                result = wachtwoordRepository.save(wachtwoord);

            } else {
                LOGGER.warn("Wachtwoord is ongeldig en kan niet opgeslagen worden.");
            }

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
    public void delete(Wachtwoord wachtwoord) throws DataServiceException {
        try {
            wachtwoordRepository.delete(wachtwoord);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * Checks the validity of a {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object.<br/>
     * <p/>
     * A Wachtwoord Object must apply to the following rules:<br/>
     * <ol>
     * <li> To be determined.</li>
     * </ol>
     *
     * @param wachtwoord   the {@link nl.devoorkant.sbdr.data.model.Wachtwoord} Object that must be validated.
     * @return          a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    // ToDo (JME 1-08-2014) Expand this function with additional Business logic
    private ValidationObject validate(Wachtwoord wachtwoord) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(wachtwoord != null) {


            } else {
                loValidation.addMessage("Geen Wachtwoord meegegeven.", ValidationConstants.MessageType.INVALID);
                LOGGER.info("No Wachtwoord recieved.");
            }

            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }
}
