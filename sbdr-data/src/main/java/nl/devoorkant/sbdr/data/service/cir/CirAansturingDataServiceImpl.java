package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirAansturing;
import nl.devoorkant.sbdr.cir.data.repository.CirAansturingRepository;
import nl.devoorkant.sbdr.data.util.cir.EAansturing;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

import javax.annotation.PostConstruct;

/**
 * Stateless service bean with functionality for CIR_Aansturingen.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirAansturingDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CirAansturingDataServiceImpl implements CirAansturingDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CirAansturingDataServiceImpl.class);

    @Autowired
    private CirAansturingRepository cirAansturingRepository;
    
    /**
     * Returns the CIR_Aansturing Object.<br/>
     *
     * Since only one CIR_Aansturing is allowed, no parameters are necessary. When there is no CIR_Aansturing available,
     * a CIR_Aansturing will be created.
     *
     * @return      the CIR_Aansturing Entity
     */
    public CirAansturing getCIR_Aansturing() {
        CirAansturing loResult = null;

        loResult = getCIR_Aansturing(EAansturing.DEFAULT.getCode());

        if (loResult == null) {
            loResult = new CirAansturing();
            loResult.setCode(EAansturing.DEFAULT.getCode());
        }

        return loResult;
    }

    /**
     * Saves the passed CIR_Aansturing Entity.<br/>
     * <p/>
     * The CIR_Aansturing Object passed as argument will be validated {@link #validateCIR_Aansturing(nl.devoorkant.insolventie.data.cir.CIR_Aansturing)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * 	<li> Valid. The batch indicated by the ID will be updated. These actions can have two possible outcomes as well:<br/>
	 * 		<ol>
	 * 			<li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * 		 	<li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * 		</ol>
	 * 	<li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
     *
     * @param poCIR_Aansturing 	the CIR_Aansturing to save.
     * @return 					a Result Object, containing the result of the save action.
     */
    @Transactional(rollbackFor=Exception.class)
	public Result saveCIR_Aansturing(CirAansturing poCIR_Aansturing) {
		LOGGER.info("Method saveCIR_Aansturing. Aansturing is {}", poCIR_Aansturing.toString());
		Result loResult = new Result();

		try {
			/** Before saving the CIR_Aansturing, it must be validated */
			loResult = new Result(validateCIR_Aansturing(poCIR_Aansturing));

			if(loResult.isSuccessful()) {
                poCIR_Aansturing = cirAansturingRepository.save(poCIR_Aansturing);

                loResult.setResultObject(poCIR_Aansturing);
			}
		} catch(Exception loEx) {
            LOGGER.error("Method saveCIR_Aansturing. Method failed.", loEx);
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
//			CIR_Aansturing loCIR_Aansturing = (CIR_Aansturing) poObject;
//			ArrayList<Object> loQueryParams = new ArrayList<Object>();
//			StringBuilder loWhere = new StringBuilder();
//
//			StringBuffer loSelectQuery = new StringBuffer();
//			loSelectQuery.append("SELECT c FROM CIR_Aansturing c");
//			loSelectQuery.append(loWhere);
//			loSelectQuery.append(" ORDER BY c.code");
//			String lstrSelectQuery = loSelectQuery.toString();
//
//			StringBuffer loCountQuery = new StringBuffer();
//			loCountQuery.append("SELECT COUNT(c) FROM CIR_Aansturing c");
//			loCountQuery.append(loWhere);
//			String lstrCountQuery = loCountQuery.toString();
//
//			return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
//
//		} catch(Exception loEx) {
//			ioLogger.error("Method createPageHandler. Creatie pagehandler mislukt.", loEx);
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
     * Returns the CIR_Aansturing Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the CIR_Aansturing to retrieve
     * @return 			the CIR_Aansturing Entity
     */
    private CirAansturing getCIR_Aansturing(String pstrCode) {

        if (pstrCode != null && pstrCode.trim().length() > 0) {
            return cirAansturingRepository.findByCode(pstrCode);
        } else {
            LOGGER.debug("Method getCIR_Aansturing. Cannot retrieve CIR_Aansturing without a key.");
            return null;
        }
    }

    /**
     * Checks the validity of a CIR_Aansturing Object.<br/>
     * <p/>
     * A CIR_Aansturing Object must apply to the following rules:<br/>
     * <ol>
     * <li> The field Gebruikersnaam must contain a value.</li>
     * <li> The field Wachtwoord must contain a value.</li>
     * </ol>
     *
     * @param poCIR_Aansturing 	CIR_Aansturing that must be validated.
     * @return 					a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
	private ValidationObject validateCIR_Aansturing(CirAansturing poCIR_Aansturing) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(poCIR_Aansturing != null) {

                // 1. Check Gebruikersnaam
                if(StringUtil.isEmptyOrNull(poCIR_Aansturing.getGebruikersNaam())) {
                    loValidation.addMessage(poCIR_Aansturing.getGebruikersNaam(), "Gebruikersnaam is verplicht.", MessageType.INVALID);
                }

                // 2. Check Wachtwoord
                if(StringUtil.isEmptyOrNull(poCIR_Aansturing.getWachtwoord())) {
                    loValidation.addMessage(poCIR_Aansturing.getWachtwoord(), "Wachtwoord is verplicht.", MessageType.INVALID);
                }

			} else {
				loValidation.addMessage("Geen CIR_Aansturing meegegeven.", MessageType.INVALID);
				LOGGER.info("Method validateCIR_Aansturing. No CIR_Aansturing recieved.");
			}
			LOGGER.info("Method validateCIR_Aansturing. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_Aansturing. Validation failed.");
		}
		return loValidation;
	}

}