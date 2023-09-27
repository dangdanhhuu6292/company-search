package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirExceptie;
import nl.devoorkant.sbdr.cir.data.model.CirExceptietype;
import nl.devoorkant.sbdr.cir.data.repository.CirExceptieRepository;
import nl.devoorkant.sbdr.cir.data.repository.CirExceptietypeRepository;
import nl.devoorkant.util.DateUtil;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

/**
 * Stateless service bean with functionality for CIR_Excepties.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2015 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirExceptieDataService")
@Transactional(readOnly = true)
public class CirExceptieDataServiceImpl implements CirExceptieDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CirExceptieDataServiceImpl.class);

    @Autowired 
    private CirExceptieRepository cirExceptieRepository;

    @Autowired 
    private CirExceptietypeRepository cirExceptietypeRepository;    

    /**
     * Returns a new, initialised CIR_Exceptie Object.<br/>
     *
	 * @return  a new CIR_Exceptie Object
     */
    public CirExceptie getNewCIR_Exceptie() {
        CirExceptie loResult = new CirExceptie();
        loResult.setTijd(DateUtil.getCurrentTimestamp());
        return loResult;
    }

    /**
     * Returns a new, initialised CIR_Exceptie Object, based on the passed CIR_ExceptieTypeCode.<br/>
     *
     * @param pstrCIR_ExceptieTypeCode	a String representing a CIR_ExceptieType
     * @return 					        a new CIR_Exceptie Object
     */
    public CirExceptie getNewCIR_Exceptie(String pstrCIR_ExceptieTypeCode) {
        CirExceptie loResult = getNewCIR_Exceptie();
        loResult.setCirExceptietype(cirExceptietypeRepository.findByCode(pstrCIR_ExceptieTypeCode));
        return loResult;
    }

    /**
     * Returns the CIR_Exceptie Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_Exceptie to retrieve
     * @return 		    the CIR_Exceptie Object
     */
    public CirExceptie getCIR_Exceptie(Integer poID) {

        if (poID != null) {
            Optional<CirExceptie> cirExceptie = cirExceptieRepository.findById(poID);
            return cirExceptie != null ? cirExceptie.get() : null;
        } else {
            LOGGER.debug("Method getCIR_Exceptie. Cannot retrieve CIR_Exceptie without a key.");
            return null;
        }
    }

	/**
	 * Saves the passed CIR_Exceptie Object.<br/>
	 * <p/>
	 * The CIR_Exceptie Object passed as argument will be validated {@link #validateCIR_Exceptie(nl.devoorkant.insolventie.data.cir.CIR_Exceptie)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * <li> Valid. When the passed CIR_Exceptie Object does not have an ID, a new client will be created, otherwise the client indicated by the ID will be updated. These
	 * actions can have two possible outcomes as well:<br/>
	 * <ol>
	 * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * </ol>
	 * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
	 *
     * @param poCIR_Exceptie 		the CIR_Exceptie to save.
	 * @return 				a Result Object, containing the result of the save action.
	 */
    @Transactional(rollbackFor=Exception.class)
	public Result saveCIR_Exceptie(CirExceptie poCIR_Exceptie) {
		LOGGER.debug("Method saveCIR_Exceptie.");
		Result loResult = new Result();

		try {
			/** Before saving the CIR_Exceptie, it must be validated */
			loResult = new Result(validateCIR_Exceptie(poCIR_Exceptie));

			if (loResult.isSuccessful()) {

				LOGGER.debug("Method saveCIR_Exceptie.");
				poCIR_Exceptie = cirExceptieRepository.save(poCIR_Exceptie);

				loResult.setResultObject(poCIR_Exceptie);
			}
		} catch(Exception loEx) {
			LOGGER.error("Method saveCIR_Exceptie. Saving the CIR_Exceptie failed.", loEx);
			loResult.setResult(false);
		}
		return loResult;
	}

    /**
     * Checks the validity of a CIR_Exceptie Object.<br/>
     * <p/>
     * A CIR_Exceptie Object must apply to the following rules:<br/>
     * <ol>
	 * <li> The CIR_Exceptie must have a valid CIR_ExceptieType or the field Omschrijving must be filled (both is also allowed).</li>
     * </ol>
     *
     * @param poCIR_Exceptie 	the CIR_Exceptie that must be validated.
     * @return 			a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    public ValidationObject validateCIR_Exceptie(CirExceptie poCIR_Exceptie) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if (poCIR_Exceptie != null) {

                // 1. The CIR_Exceptie must have a CIR_ExceptieType or the field Omschrijving must be filled (both is also allowed).
                if (poCIR_Exceptie.getCirExceptietype() == null && StringUtil.isEmptyOrNull(poCIR_Exceptie.getOmschrijving())) {
                    loValidation.addMessage("CIR_ExceptieType moet bepaald zijn, of omschrijving moet gevuld zijn.", MessageType.ERROR);
					LOGGER.error("Method validateCIR_Exceptie. CIR_ExceptieType or Omschrijving not set.");
				}

            } else {
                loValidation.addMessage("Geen CIR_Exceptie ontvangen.", MessageType.INVALID);
				LOGGER.error("Method validateCIR_Exceptie. No CIR_Exceptie received.");
            }
            LOGGER.info("Method validateCIR_Exceptie. Number of messages = " + loValidation.getValidationMessages().size());

        }  catch (Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_Exceptie. Validation failed.");
        }
        return loValidation;
    }

	/**
     * Returns the CIR_ExceptieType Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the CIR_ExceptieType to retrieve
     * @return 			the CIR_ExceptieType Entity
     */
    public CirExceptietype getCIR_ExceptieType(String pstrCode) {

        if (pstrCode != null && pstrCode.trim().length() > 0) {
            return cirExceptietypeRepository.findByCode(pstrCode);
        } else {
            LOGGER.debug("Method getCIR_ExceptieType. Cannot retrieve CIR_ExceptieType without a key.");
            return null;
        }
    }

    /**
     * Returns the active CIR_ExceptieType Entities .<br/>
     *
     * @return 		a Collection of actieve CIR_ExceptieType Entities.
     */
    @SuppressWarnings({"unchecked"})
    public Collection<CirExceptietype> getActiveCIR_ExceptieTypes() {
        return cirExceptietypeRepository.findAllActief();
    }

    /**
     * Saves the passed CIR_ExceptieType Entity.<br/>
     * <p/>
	 * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
	 *
	 * The CIR_ExceptieType Entity passed as argument will be validated {@link #validateCIR_ExceptieType(nl.devoorkant.insolventie.data.cir.CIR_ExceptieType)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The CIR_ExceptieType indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param poCIR_ExceptieType 	the CIR_ExceptieType Entity to save.
     * @return 			a Result Object, containing the result of the save action.
     */
    @Transactional(rollbackFor=Exception.class)
    public Result saveCIR_ExceptieType(CirExceptietype poCIR_ExceptieType) {
        LOGGER.debug("Method saveCIR_ExceptieType.");
        Result loResult = new Result();

        try {
            // Before saving the CIR_ExceptieType, it must be validated
            loResult = new Result(validateCIR_ExceptieType(poCIR_ExceptieType));

            if (loResult.isSuccessful()) {
				LOGGER.debug("Method saveCIR_ExceptieType. Update");
				poCIR_ExceptieType = cirExceptietypeRepository.save(poCIR_ExceptieType);

                loResult.setResultObject(poCIR_ExceptieType);
            }
        } catch (Exception loEx) {
            LOGGER.error("Method saveCIR_ExceptieType. Method failed.", loEx);
            loResult.setResult(false);
        }
        return loResult;
    }

/////
// Protected
/////
//    /**
//     * Create a new page handler.<br/>
//     *
//     * @param poObject   	an Object containing search criteria.
//     * @param pnPageSize 	an int containing the Page size.
//     * @return 				a PageHandler Object.
//     */
//    protected PageHandler createPageHandler(Object poObject, int pnPageSize) {
//        ioLogger.info("Method createPageHandler");
//
//        try {
//			ArrayList<Object> loQueryParams = new ArrayList<Object>();
//			StringBuilder loWhere = new StringBuilder();
//			StringBuilder loJoin = new StringBuilder();
//			StringBuilder loSelectQuery = new StringBuilder();
//			StringBuilder loCountQuery = new StringBuilder();
//			String lstrKoppel;
//			String lstrSelectQuery;
//			String lstrCountQuery;
//
//			if (poObject instanceof CIR_Exceptie) {
//				CIR_Exceptie loCIR_Exceptie = (CIR_Exceptie) poObject;
//
//                // 1. Compose the join
//                if (loCIR_Exceptie.getCIR_ExceptieType() != null) loJoin.append(" JOIN e.CIR_ExceptieType et");
//
//                // 2. Compose the where
//                lstrKoppel = " WHERE";
//
//                if (loCIR_Exceptie.getCIR_ExceptieType() != null) {
//                    loQueryParams.add(loCIR_Exceptie.getCIR_ExceptieType().getCode());
//
//                    loWhere.append(lstrKoppel).append(" et.code = ?").append(loQueryParams.size());
//                }
//
//				loSelectQuery.append("SELECT e FROM CIR_Exceptie e");
//                loSelectQuery.append(loJoin);
//				loSelectQuery.append(loWhere);
//				loSelectQuery.append(" ORDER BY e.tijd");
//				lstrSelectQuery = loSelectQuery.toString();
//
//				loCountQuery.append("SELECT COUNT(e) FROM CIR_Exceptie e");
//                loCountQuery.append(loJoin);
//				loCountQuery.append(loWhere);
//				lstrCountQuery = loCountQuery.toString();
//
//                ioLogger.info("Method createPageHandler. SelectQuery = {}", lstrSelectQuery);
//
//				return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
//
//			} else if (poObject instanceof CIR_ExceptieType) {
//				CIR_ExceptieType loCIR_ExceptieType = (CIR_ExceptieType) poObject;
//
//				// Compose the where
//				lstrKoppel = " WHERE";
//				if(loCIR_ExceptieType.getOmschrijving() != null) {
//					loQueryParams.add(loCIR_ExceptieType.getOmschrijving());
//
//					// Als er een %-teken in de tekst staat, dan moet er gezocht worden m.b.v. LIKE
//					if(!loCIR_ExceptieType.getOmschrijving().contains("%")) {
//						loWhere.append(lstrKoppel).append(" et.omschrijving = ?").append(loQueryParams.size());
//					} else loWhere.append(lstrKoppel).append(" et.omschrijving LIKE ?").append(loQueryParams.size());
//				}
//
//				loSelectQuery.append("SELECT et FROM CIR_ExceptieType et");
//				loSelectQuery.append(loWhere);
//				loSelectQuery.append(" ORDER BY et.omschrijving");
//				lstrSelectQuery = loSelectQuery.toString();
//
//				loCountQuery.append("SELECT COUNT(et) FROM CIR_ExceptieType et");
//				loCountQuery.append(loWhere);
//				lstrCountQuery = loCountQuery.toString();
//
//				return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
//			}
//
//		} catch(Exception loEx) {
//			ioLogger.error("Method createPageHandler. Method failed.", loEx);
//		}
//		return null;
//	}
//
//    /**
//     * Retrieves the requested page.
//     *
//     * @param poPageHandler 	an initialized PageHandler Object.
//     * @param poPageAction  	an Enumeration, indicating the page to create.
//     * @return 					a PageHandler Object, containing the requested page.
//     */
//	protected PageHandler getPage(PageHandler poPageHandler, PageHandler.PageAction poPageAction) {
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
//		List loList = loQuery.getResultList();
//		poPageHandler.setPage(loList);
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
     * Checks the validity of an CIR_ExceptieType Entity.<br/>
     * <p/>
     * An CIR_ExceptieType Object must apply to the following rules:<br/>
     * <ol>
	 * <li> The CIR_ExeptieType must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
	 * @param poCIR_ExceptieType 	the CIR_ExceptieType Entity to validate.
     * @return 				a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
	private ValidationObject validateCIR_ExceptieType(CirExceptietype poCIR_ExceptieType) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(poCIR_ExceptieType != null) {

                // 1. The action must already exist.
                if (poCIR_ExceptieType.getCode() == null || poCIR_ExceptieType.getCode().trim().equals("")) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", MessageType.INVALID);
                }

				// 2. The field omschrijving must contain a value
				if(poCIR_ExceptieType.getOmschrijving() == null || poCIR_ExceptieType.getOmschrijving().trim().equals("")) {
					loValidation.addMessage(poCIR_ExceptieType.getOmschrijving(), "Omschrijving is een verplicht veld.", MessageType.INVALID);
				}

			} else {
				loValidation.addMessage("Geen CIR_ExceptieType ontvangen.", MessageType.INVALID);
				LOGGER.error("Method validateCIR_ExceptieType. No CIR_ExceptieType received.");
			}
			LOGGER.info("Method validateCIR_ExceptieType. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_ExceptieType. Validation failed.");
		}
		return loValidation;
	}
}