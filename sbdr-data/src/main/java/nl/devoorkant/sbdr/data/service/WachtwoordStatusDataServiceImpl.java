package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.WachtwoordStatus;
import nl.devoorkant.sbdr.data.repository.WachtwoordStatusRepository;

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
 * Data Service bean with implemented functionality for WachtwoordStatus.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * Implements the functionality exposed by the WachtwoordStatusDataService interface.
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

@Service("wachtwoordStatusDataService")
public class WachtwoordStatusDataServiceImpl implements WachtwoordStatusDataService{
    private static final Logger LOGGER = LoggerFactory.getLogger(WachtwoordStatusDataServiceImpl.class);
    
    @Autowired
    private WachtwoordStatusRepository wachtwoordStatusRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public WachtwoordStatus findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<WachtwoordStatus> wachtwoordStatus = wachtwoordStatusRepository.findById(code);
                return wachtwoordStatus != null ? wachtwoordStatus.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve WachtwoordStatus without a key.");
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
    public List<WachtwoordStatus> findByActief(boolean actief) throws DataServiceException {

        try {
            return wachtwoordStatusRepository.findByActief(actief);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public WachtwoordStatus save(WachtwoordStatus wachtwoordStatus) throws DataServiceException {
        LOGGER.info("Start.");
        WachtwoordStatus result = null;

        try {
            if(validate(wachtwoordStatus).isValid()) {
                result = wachtwoordStatusRepository.save(wachtwoordStatus);

            } else {
                LOGGER.warn("WachtwoordStatus is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an WachtwoordStatus Entity.<br/>
     * <p/>
     * An WachtwoordStatus Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordStatus must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param wachtwoordStatus 	the WachtwoordStatus Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(WachtwoordStatus wachtwoordStatus) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(wachtwoordStatus != null) {

                // 1. The WachtwoordStatus must already exist.
                if (StringUtil.isEmptyOrNull(wachtwoordStatus.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(wachtwoordStatus.getOmschrijving())) {
                    loValidation.addMessage(wachtwoordStatus.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen WachtwoordStatus ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No WachtwoordStatus received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }
}
