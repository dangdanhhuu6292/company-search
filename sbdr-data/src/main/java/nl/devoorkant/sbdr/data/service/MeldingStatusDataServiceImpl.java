package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.MeldingStatus;
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
 * Data Service bean with implemented functionality for MeldingStatus.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the MeldingStatusDataService must implement to support interaction between a Business Object and
 * the MeldingStatusRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

@Service("meldingStatusDataService")
public class MeldingStatusDataServiceImpl implements MeldingStatusDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeldingStatusDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.MeldingStatusRepository meldingStatusRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public MeldingStatus findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<MeldingStatus> meldingStatus = meldingStatusRepository.findById(code);
                return meldingStatus != null ? meldingStatus.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve MeldingStatus without a key.");
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
    public List<MeldingStatus> findByActief(boolean actief) throws DataServiceException {

        try {
            return meldingStatusRepository.findByActief(actief);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MeldingStatus save(MeldingStatus MeldingStatus) throws DataServiceException {
        LOGGER.info("Start.");
        MeldingStatus result = null;

        try {
            if(validate(MeldingStatus).isValid()) {
                result = meldingStatusRepository.save(MeldingStatus);

            } else {
                LOGGER.warn("MeldingStatus is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an MeldingStatus Entity.<br/>
     * <p/>
     * An MeldingStatus Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordStatus must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param MeldingStatus 	the MeldingStatus Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(MeldingStatus MeldingStatus) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(MeldingStatus != null) {

                // 1. The MeldingStatus must already exist.
                if (StringUtil.isEmptyOrNull(MeldingStatus.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(MeldingStatus.getOmschrijving())) {
                    loValidation.addMessage(MeldingStatus.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen MeldingStatus ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No MeldingStatus received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }

}
