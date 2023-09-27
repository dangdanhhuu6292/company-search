package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.Configuratie;
import nl.devoorkant.sbdr.cir.data.repository.CirConfiguratieRepository;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

/**
 * Stateless service bean with functionality for Configuraties.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirConfiguratieDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CirConfiguratieDataServiceImpl implements CirConfiguratieDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CirConfiguratieDataServiceImpl.class);

    @Autowired
    private CirConfiguratieRepository cirConfiguratieRepository;

    /**
     * Returns the Configuratie Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the Configuratie to retrieve
     * @return 			the Configuratie Entity
     */
    public Configuratie getConfiguratie(String pstrCode) {
		LOGGER.debug("Method getConfiguratie.");

		if(StringUtil.isNotEmptyOrNull(pstrCode)) {
            return cirConfiguratieRepository.findByCode(pstrCode);
        } else {
			LOGGER.debug("Method getConfiguratie. Kan geen Configuratie ophalen als key niet is meegegeven.");
			return null;
		}
	}

    /**
     * Retrieve the Configuratie Waarde for the passed Code (primary key).<br/>
     *
     * @param pstrCode 	a String representing the Configuratie.
     * @return 			A String containing the Waarde of the Configuratie, or NULL when the Configuratie could not be retrieved.
     */
	public String getConfiguratieValue(String pstrCode) {
		Configuratie loConfiguratie = getConfiguratie(pstrCode);
		return loConfiguratie != null ? loConfiguratie.getWaarde() : null;
	}

    /**
     * Returns all the displayable Configuraties.<br/>
     *
     * @return 		a Collection of displayable Configuraties.
     */
    @SuppressWarnings({"unchecked"})
    public List<Configuratie> getDisplayableConfiguraties() {
        return cirConfiguratieRepository.findByIndTonen(true);
    }

    /**
     * Saves the passed Configuratie Entity.<br/>
     * <p/>
     * The Configuratie Object passed as argument will be validated {@link #validateConfiguratie(nl.devoorkant.insolventie.data.Configuratie)} first. The validation can have two possible
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
     * @param poConfiguratie 	the Configuratie to save.
     * @return 					a Result Object, containing the result of the save action.
     */
    @Transactional(rollbackFor=Exception.class)
	public Result saveConfiguratie(Configuratie poConfiguratie) {
		LOGGER.debug("Method saveConfiguratie.");
		Result loResult = new Result();

		try {
			/** Before saving the Configuratie, it must be validated */
			loResult = new Result(validateConfiguratie(poConfiguratie));

			if(loResult.isSuccessful()) {
				LOGGER.debug("Method saveConfiguratie. Update");
				poConfiguratie = cirConfiguratieRepository.save(poConfiguratie);
                loResult.setResultObject(poConfiguratie);
			}
		} catch(Exception loEx) {
            LOGGER.error("Method saveConfiguratie. Method failed.", loEx);
			loResult.setResult(false);
		}
		return loResult;
	}

//    /**
//     * Create a new page handler.<br/>
//     *
//     * @param poObject   Object.
//     * @param pnPageSize Page size.
//     * @return PageHandler.
//     */
//	protected PageHandler createPageHandler(Object poObject, int pnPageSize) {
//
//		try {
//			Configuratie loConfiguratie = (Configuratie) poObject;
//			ArrayList<Object> loQueryParams = new ArrayList<Object>();
//			StringBuilder loWhere = new StringBuilder();
//
//			String lstrKoppel = " WHERE";
//			if(loConfiguratie.getOmschrijving() != null) {
//				loQueryParams.add(loConfiguratie.getOmschrijving());
//
//				/** Als er een %-teken in de tekst staat, dan moet er gezocht worden m.b.v. LIKE */
//				if(!loConfiguratie.getOmschrijving().contains("%")) {
//					loWhere.append(lstrKoppel).append(" c.omschrijving = ?").append(loQueryParams.size());
//				} else {
//					loWhere.append(lstrKoppel).append(" c.omschrijving LIKE ?").append(loQueryParams.size());
//				}
//			}
//
//			StringBuffer loSelectQuery = new StringBuffer();
//			loSelectQuery.append("SELECT c FROM Configuratie c");
//			loSelectQuery.append(loWhere);
//			loSelectQuery.append(" ORDER BY c.code");
//			String lstrSelectQuery = loSelectQuery.toString();
//
//			StringBuffer loCountQuery = new StringBuffer();
//			loCountQuery.append("SELECT COUNT(c) FROM Configuratie c");
//			loCountQuery.append(loWhere);
//			String lstrCountQuery = loCountQuery.toString();
//
//			return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
//
//		} catch(Exception loEx) {
//			LOGGER.error("Method createPageHandler. Creatie pagehandler mislukt.", loEx);
//		}
//		return null;
//	}
//
//    /**
//     * Retrieves the requested page.
//     *
//     * @param poPageHandler     Initialized PageHandler object.
//     * @param poPageAction      Enumeration, indicating the page to create.
//     *
//     * @return  				PageHandler object with the requested page.
//     */
//	protected PageHandler getPage(PageHandler poPageHandler, PageHandler.PageAction poPageAction) {
//
//		ArrayList<Object> loQueryParams = poPageHandler.getQueryParams();
//
//		Query loQuery = ioEntityManager.createQuery(poPageHandler.getCountQuery());
//		for(int lnIndex = 0; lnIndex < loQueryParams.size(); lnIndex++) {
//			loQuery.setParameter(lnIndex + 1, loQueryParams.get(lnIndex));
//		}
//		poPageHandler.setNrOfRecordsAvailable(((Long) loQuery.getSingleResult()).intValue());
//
//		poPageHandler.setStartPosition(poPageAction);
//		loQuery = ioEntityManager.createQuery(poPageHandler.getSelectQuery());
//		for(int lnIndex = 0; lnIndex < loQueryParams.size(); lnIndex++) {
//			loQuery.setParameter(lnIndex + 1, loQueryParams.get(lnIndex));
//		}
//		loQuery.setMaxResults(poPageHandler.getMaxPageSize());
//		loQuery.setFirstResult(poPageHandler.getStartPosition());
//        List loList = loQuery.getResultList();
//        poPageHandler.setPage(loList);
//
//		return poPageHandler;
//	}
//
//    /**
//     * Retrieves the requested page.
//     *
//     * @param poPageHandler     Initialized PageHandler object.
//     * @param pnPageNr 	        an int containing the Page to retrieve.
//     *
//     * @return  				PageHandler object with the requested page.
//     */
//    protected PageHandler getPage(PageHandler poPageHandler, int pnPageNr) {
//
//        ArrayList<Object> loQueryParams = poPageHandler.getQueryParams();
//
//        Query loQuery = ioEntityManager.createQuery(poPageHandler.getCountQuery());
//        for(int lnIndex = 0; lnIndex < loQueryParams.size(); lnIndex++) {
//            loQuery.setParameter(lnIndex + 1, loQueryParams.get(lnIndex));
//        }
//        poPageHandler.setNrOfRecordsAvailable(((Long) loQuery.getSingleResult()).intValue());
//
//        poPageHandler.setStartPosition(pnPageNr);
//        loQuery = ioEntityManager.createQuery(poPageHandler.getSelectQuery());
//        for(int lnIndex = 0; lnIndex < loQueryParams.size(); lnIndex++) {
//            loQuery.setParameter(lnIndex + 1, loQueryParams.get(lnIndex));
//        }
//        loQuery.setMaxResults(poPageHandler.getMaxPageSize());
//        loQuery.setFirstResult(poPageHandler.getStartPosition());
//        List loList = loQuery.getResultList();
//        poPageHandler.setPage(loList);
//
//        return poPageHandler;
//    }

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
				LOGGER.info("Method validateConfiguratie. No Configuratie recieved.");
			}
			LOGGER.info("Method validateConfiguratie. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateConfiguratie. Validation failed.");
		}
		return loValidation;
	}
}