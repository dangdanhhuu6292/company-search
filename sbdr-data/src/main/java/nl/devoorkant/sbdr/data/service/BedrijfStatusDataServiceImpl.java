package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.BedrijfStatus;
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
 * Data Service bean with implemented functionality for BedrijfStatus.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the BedrijfStatusDataService must implement to support interaction between a Business Object and
 * the BedrijfStatusRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

@Service("bedrijfStatusDataService")
public class BedrijfStatusDataServiceImpl implements BedrijfStatusDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BedrijfStatusDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.BedrijfStatusRepository bedrijfStatusRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public BedrijfStatus findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<BedrijfStatus> bedrijfStatus = bedrijfStatusRepository.findById(code);
                return bedrijfStatus != null ? bedrijfStatus.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve BedrijfStatus without a key.");
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
    public List<BedrijfStatus> findByActief(boolean actief) throws DataServiceException {

        try {
            return bedrijfStatusRepository.findByActief(actief);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public BedrijfStatus save(BedrijfStatus bedrijfStatus) throws DataServiceException {
        LOGGER.info("Start.");
        BedrijfStatus result = null;

        try {
            if(validate(bedrijfStatus).isValid()) {
                result = bedrijfStatusRepository.save(bedrijfStatus);

            } else {
                LOGGER.warn("BedrijfStatus is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an BedrijfStatus Entity.<br/>
     * <p/>
     * An BedrijfStatus Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordStatus must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param wachtwoordStatus 	the BedrijfStatus Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(BedrijfStatus wachtwoordStatus) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(wachtwoordStatus != null) {

                // 1. The BedrijfStatus must already exist.
                if (StringUtil.isEmptyOrNull(wachtwoordStatus.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(wachtwoordStatus.getOmschrijving())) {
                    loValidation.addMessage(wachtwoordStatus.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen BedrijfStatus ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No BedrijfStatus received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }
}
