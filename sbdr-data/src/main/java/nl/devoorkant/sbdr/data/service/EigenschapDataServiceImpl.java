package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Eigenschap;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

@Service("eigenschapDataService")
public class EigenschapDataServiceImpl implements EigenschapDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EigenschapDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.EigenschapRepository eigenschapRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Eigenschap findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<Eigenschap> eigenschap = eigenschapRepository.findById(code);
                return eigenschap != null ? eigenschap.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve Eigenschap without a key.");
                return null;
            }
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Eigenschap> findByActief(boolean actief) throws DataServiceException {

        try {
            return eigenschapRepository.findByActief(actief);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Eigenschap save(Eigenschap Eigenschap) throws DataServiceException {
        LOGGER.info("Start.");
        Eigenschap result = null;

        try {
            if(validate(Eigenschap).isValid()) {
                result = eigenschapRepository.save(Eigenschap);

            } else {
                LOGGER.warn("Eigenschap is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an Eigenschap Entity.<br/>
     * <p/>
     * An Eigenschap Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordStatus must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param Eigenschap 	the Eigenschap Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(Eigenschap Eigenschap) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(Eigenschap != null) {

                // 1. The Eigenschap must already exist.
                if (StringUtil.isEmptyOrNull(Eigenschap.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(Eigenschap.getOmschrijving())) {
                    loValidation.addMessage(Eigenschap.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen Eigenschap ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No Eigenschap received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }

}
