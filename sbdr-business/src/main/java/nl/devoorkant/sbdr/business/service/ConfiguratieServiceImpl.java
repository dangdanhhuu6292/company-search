package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.model.Configuratie;
import nl.devoorkant.sbdr.data.repository.ConfiguratieRepository;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

/**
 * Stateless service bean with functionality for Configuraties.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

@Service("configuratieService")
@Transactional(readOnly = true)
public class ConfiguratieServiceImpl implements ConfiguratieService {
    private static final Logger ioLogger = LoggerFactory.getLogger(ConfiguratieService.class);

    @Autowired
    private ConfiguratieRepository configuratieRepository;

    /**
     * Returns the Configuratie Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the Configuratie to retrieve
     * @return 			the Configuratie Entity, or NULL when the Configuratie could not be retrieved.
     */
    @Override
    public Configuratie getConfiguratie(String pstrCode) {
		ioLogger.debug("Method getConfiguratie.");

		if(StringUtil.isNotEmptyOrNull(pstrCode)) {
            Optional<Configuratie> configuratie = configuratieRepository.findById(pstrCode);
            return configuratie != null ? configuratie.get() : null;
        } else {
			ioLogger.debug("Method getConfiguratie. Kan geen Configuratie ophalen als key niet is meegegeven.");
			return null;
		}
	}

    /**
     * Returns all the displayable Configuraties.<br/>
     *
     * @return 		a Collection of displayable Configuraties.
     */
    @Override
    public Collection<Configuratie> getDisplayableConfiguraties() {
        return configuratieRepository.findByIndTonen(true);
    }

    /**
     * Returns a Page containing configurations with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Configuraties, or NULL when the Configuraties could not be retrieved..
     */
    @Override
    public Page<Configuratie> getConfiguratiePageByOmschrijving(String omschrijving, Pageable pageable) {
        if(StringUtil.isNotEmptyOrNull(omschrijving)) {
            return configuratieRepository.findByOmschrijving(omschrijving, pageable);
        } else {
            ioLogger.debug("Method getConfiguratiePageByOmschrijving. Cannot retrieve a Configuratie Page when omschrijving is not set.");
            return null;
        }
    }

    /**
     * Returns a Page containing configurations the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Configuraties, or NULL when the Configuraties could not be retrieved..
     */
    @Override
    public Page<Configuratie> getConfiguratiePageByOmschrijvingLike(String omschrijving, Pageable pageable) {
        if(StringUtil.isNotEmptyOrNull(omschrijving)) {
            return configuratieRepository.findByOmschrijvingLike(omschrijving, pageable);
        } else {
            ioLogger.debug("Method getConfiguratiePageByOmschrijvingLike. Cannot retrieve a Configuratie Page when omschrijving is not set.");
            return null;
        }
    }

    /**
     * Saves the passed ConfiguratieService Entity.<br/>
     * <p/>
     * The ConfiguratieService Object passed as argument will be validated {@link #validateConfiguratie(nl.devoorkant.sbdr.data.model.Configuratie)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * 	<li> Valid. The configuratie indicated by the ID will be updated. These actions can have two possible outcomes as well:<br/>
	 * 		<ol>
	 * 			<li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * 		 	<li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * 		</ol>
	 * 	<li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
     *
     * @param poConfiguratie 	the ConfiguratieService to save.
     * @return 					a Result Object, containing the result of the save action.
     */
    @Override
    @Transactional
    public Result saveConfiguratie(Configuratie poConfiguratie) {
		ioLogger.debug("Method saveConfiguratie.");
		Result loResult = new Result();

		try {
			/** Before saving the ConfiguratieService, it must be validated */
			loResult = new Result(validateConfiguratie(poConfiguratie));

			if(loResult.isSuccessful()) {
				ioLogger.debug("Method saveConfiguratie. Update");
                configuratieRepository.save(poConfiguratie);
                loResult.setResultObject(poConfiguratie);
			}
		} catch(Exception loEx) {
            ioLogger.error("Method saveConfiguratie. Method failed.", loEx);
			loResult.setResult(false);
		}
		return loResult;
	}

    /**
     * Checks the validity of a Configuratie Object.<br/>
     * <p/>
     * A Configuratie Object must apply to the following rules:<br/>
     * <ol>
     * <li> The field Omschrijving must contain a value.</li>
     * </ol>
     *
     * @param poConfiguratie 	Configuratie that must be validated.
     * @return 					a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
	private ValidationObject validateConfiguratie(Configuratie poConfiguratie) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(poConfiguratie != null) {

				// 1. Check Omschrijving
				String lstrOmschrijving = poConfiguratie.getOmschrijving();
				if(StringUtil.isEmptyOrNull(lstrOmschrijving)) {
					loValidation.addMessage(poConfiguratie.getOmschrijving(), "Omschrijving is verplicht.", MessageType.INVALID);
				}
			} else {
				loValidation.addMessage("Geen Configuratie meegegeven.", MessageType.INVALID);
				ioLogger.info("Method validateConfiguratie. No ConfiguratieService recieved.");
			}
			ioLogger.info("Method validateConfiguratie. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			ioLogger.error("Method validateConfiguratie. Validation failed.");
		}
		return loValidation;
	}
}