package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Configuratie;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationObject;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service("configuratieDataService")
public class ConfiguratieDataServiceImpl implements ConfiguratieDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguratieDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.ConfiguratieRepository configuratieRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuratie findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<Configuratie> configuratie = configuratieRepository.findById(code);
                return configuratie != null ? configuratie.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve Configuratie without a key.");
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
    @Transactional
    public Configuratie save(Configuratie Configuratie) throws DataServiceException {
        LOGGER.info("Start.");
        Configuratie result = null;

        try {
            if(validate(Configuratie).isValid()) {
                result = configuratieRepository.save(Configuratie);

            } else {
                LOGGER.warn("Configuratie is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an Configuratie Entity.<br/>
     * <p/>
     * An Configuratie Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordStatus must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param Configuratie 	the Configuratie Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(Configuratie Configuratie) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(Configuratie != null) {

                // 1. The Configuratie must already exist.
                if (StringUtil.isEmptyOrNull(Configuratie.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(Configuratie.getOmschrijving())) {
                    loValidation.addMessage(Configuratie.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen Configuratie ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No Configuratie received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }

}
