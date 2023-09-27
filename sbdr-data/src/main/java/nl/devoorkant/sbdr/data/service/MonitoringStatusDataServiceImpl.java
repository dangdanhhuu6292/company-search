package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.MonitoringStatus;
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
 * Data Service bean with implemented functionality for MonitoringStatus.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the MonitoringStatusDataService must implement to support interaction between a Business Object and
 * the MonitoringStatusRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

@Service("monitoringStatusDataService")
public class MonitoringStatusDataServiceImpl implements MonitoringStatusDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringStatusDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.MonitoringStatusRepository monitoringStatusRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public MonitoringStatus findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<MonitoringStatus> monitoringStatus = monitoringStatusRepository.findById(code);
                return monitoringStatus != null ? monitoringStatus.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve MonitoringStatus without a key.");
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
    public List<MonitoringStatus> findByActief(boolean actief) throws DataServiceException {

        try {
            return monitoringStatusRepository.findByActief(actief);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MonitoringStatus save(MonitoringStatus MonitoringStatus) throws DataServiceException {
        LOGGER.info("Start.");
        MonitoringStatus result = null;

        try {
            if(validate(MonitoringStatus).isValid()) {
                result = monitoringStatusRepository.save(MonitoringStatus);

            } else {
                LOGGER.warn("MonitoringStatus is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an MonitoringStatus Entity.<br/>
     * <p/>
     * An MonitoringStatus Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordStatus must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param MonitoringStatus 	the MonitoringStatus Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(MonitoringStatus MonitoringStatus) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(MonitoringStatus != null) {

                // 1. The MonitoringStatus must already exist.
                if (StringUtil.isEmptyOrNull(MonitoringStatus.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(MonitoringStatus.getOmschrijving())) {
                    loValidation.addMessage(MonitoringStatus.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen MonitoringStatus ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No MonitoringStatus received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }

}
