package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.KlantStatus;
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
 * Data Service bean with implemented functionality for KlantStatus.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the KlantStatusDataService must implement to support interaction between a Business Object and
 * the KlantStatusRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

@Service("klantStatusDataService")
public class KlantStatusDataServiceImpl implements KlantStatusDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KlantStatusDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.KlantStatusRepository klantStatusRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public KlantStatus findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<KlantStatus> klantStatus = klantStatusRepository.findById(code);
                return klantStatus != null ? klantStatus.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve KlantStatus without a key.");
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
    public List<KlantStatus> findByActief(boolean actief) throws DataServiceException {

        try {
            return klantStatusRepository.findByActief(actief);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public KlantStatus save(KlantStatus KlantStatus) throws DataServiceException {
        LOGGER.info("Start.");
        KlantStatus result = null;

        try {
            if(validate(KlantStatus).isValid()) {
                result = klantStatusRepository.save(KlantStatus);

            } else {
                LOGGER.warn("KlantStatus is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an KlantStatus Entity.<br/>
     * <p/>
     * An KlantStatus Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordStatus must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param klantStatus 	the KlantStatus Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(KlantStatus klantStatus) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(klantStatus != null) {

                // 1. The KlantStatus must already exist.
                if (StringUtil.isEmptyOrNull(klantStatus.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(klantStatus.getOmschrijving())) {
                    loValidation.addMessage(klantStatus.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen KlantStatus ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No KlantStatus received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }    
}
