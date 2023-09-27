package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.KlantType;
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
 * Created with IntelliJ IDEA.
 * User: Bas Dekker
 * Date: 6-8-14
 * Time: 7:38
 * To change this template use File | Settings | File Templates.
 */

@Service("klantTypeDataService")
public class KlantTypeDataServiceImpl implements KlantTypeDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KlantTypeDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.KlantTypeRepository klantTypeRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public KlantType findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<KlantType> klantType = klantTypeRepository.findById(code);
                return klantType != null ? klantType.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve KlantType without a key.");
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
    public List<KlantType> findByActief(boolean actief) throws DataServiceException {

        try {
            return klantTypeRepository.findByActief(actief);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public KlantType save(KlantType KlantType) throws DataServiceException {
        LOGGER.info("Start.");
        KlantType result = null;

        try {
            if(validate(KlantType).isValid()) {
                result = klantTypeRepository.save(KlantType);

            } else {
                LOGGER.warn("KlantType is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an KlantType Entity.<br/>
     * <p/>
     * An KlantType Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordType must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param klantType 	the KlantType Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(KlantType klantType) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(klantType != null) {

                // 1. The KlantType must already exist.
                if (StringUtil.isEmptyOrNull(klantType.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(klantType.getOmschrijving())) {
                    loValidation.addMessage(klantType.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen KlantType ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No KlantType received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }
}
