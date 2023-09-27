package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import nl.devoorkant.sbdr.cir.data.model.CirAdrestype;
import nl.devoorkant.sbdr.cir.data.repository.CirAdresRepository;
import nl.devoorkant.sbdr.cir.data.repository.CirAdrestypeRepository;
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
import java.util.Optional;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

/**
 * Stateless service bean with functionality for CIR_Adressen.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2015 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirAdresDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CirAdresDataServiceImpl implements CirAdresDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CirAdresDataServiceImpl.class);

    @Autowired
    private CirAdresRepository cirAdresRepository;
    
    @Autowired
    private CirAdrestypeRepository cirAdrestypeRepository;

	/**
     * Returns a new, initialised CIR_Adres Object.<br/>
     *
	 * @return  a new CIR_Adres Object
     */
    public CirAdres getNewCIR_Adres() {
        return new CirAdres();
    }

    /**
     * Returns a new, initialised CIR_Adres Object, based on the passed CIR_AdresTypeCode.<br/>
     *
     * @param pstrCIR_AdresTypeCode	a String representing a CIR_AdresType
     * @return 					        a new CIR_Adres Object
     */
    public CirAdres getNewCIR_Adres(String pstrCIR_AdresTypeCode) {
        CirAdres loResult = getNewCIR_Adres();
        loResult.setCirAdrestypeCode(pstrCIR_AdresTypeCode);
        return loResult;
    }

    /**
     * Returns the CIR_Adres Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_Adres to retrieve
     * @return 		    the CIR_Adres Object
     */
    public CirAdres getCIR_Adres(Integer poID) {

        if (poID != null) {
            Optional<CirAdres>cirAdres = cirAdresRepository.findById(poID);
            return cirAdres != null ? cirAdres.get() : null;
        } else {
        	LOGGER.debug("Method getCIR_Adres. Cannot retrieve CIR_Adres without a key.");
            return null;
        }
    }

	/**
	 * Saves the passed CIR_Adres Object.<br/>
	 * <p/>
	 * The CIR_Adres Object passed as argument will be validated {@link #validateCIR_Adres(nl.devoorkant.insolventie.data.cir.CIR_Adres)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * <li> Valid. When the passed CIR_Adres Object does not have an ID, a new client will be created, otherwise the client indicated by the ID will be updated. These
	 * actions can have two possible outcomes as well:<br/>
	 * <ol>
	 * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * </ol>
	 * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
	 *
     * @param poCIR_Adres 		the CIR_Adres to save.
	 * @return 				a Result Object, containing the result of the save action.
	 */
    @Transactional(rollbackFor=Exception.class)
	public Result saveCIR_Adres(CirAdres poCIR_Adres) {
		LOGGER.debug("Method saveCIR_Adres.");
		Result loResult = new Result();

		try {
			/** Before saving the CIR_Adres, it must be validated */
			loResult = new Result(validateCIR_Adres(poCIR_Adres));

			if (loResult.isSuccessful()) {

				LOGGER.debug("Method saveCIR_Adres.");
				poCIR_Adres = cirAdresRepository.save(poCIR_Adres);

                loResult.setResultObject(poCIR_Adres);
			}
		} catch(Exception loEx) {
			LOGGER.error("Method saveCIR_Adres. Saving the CIR_Adres failed.", loEx);
			loResult.setResult(false);
		}
		return loResult;
	}

    /**
     * Removes the CIR_Adres Object, indicated by the passed ID.<br/>
     * <p/>
     * @param poID 		an Integer indicating the CIR_Adres to remove.
     * @return 			true when the CIR_Adres is removed, otherwise false.
     */
    public boolean removeCIR_Adres(Integer poID) {

        if (poID != null) {
            CirAdres loCIR_Adres = getCIR_Adres(poID);

            if (loCIR_Adres != null) {
                LOGGER.debug("Method removeCIR_Adres. The CIR_Adres to remove is {}", loCIR_Adres.toString());
                cirAdresRepository.delete(loCIR_Adres);
                return true;

            } else {
                LOGGER.debug("Method removeCIR_Adres. The CIR_Adres to remove is not found");
            }

        } else {
            LOGGER.error("Method removeCIR_Adres. The primary key for the CIR_Adres to remove is not passed");
        }

        return false;
    }

    /**
     * Checks the validity of a CIR_Adres Object.<br/>
     * <p/>
     * A CIR_Adres Object must apply to the following rules:<br/>
     * <ol>
	 * <li> The field Straat must contain a value.</li>
     * </ol>
     *
     * @param poCIR_Adres 	the CIR_Adres that must be validated.
     * @return 			a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    public ValidationObject validateCIR_Adres(CirAdres poCIR_Adres) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if (poCIR_Adres != null) {

                // 1. The field Straat must contain a value.
                if (StringUtil.isEmptyOrNull(poCIR_Adres.getStraat())) {
                    loValidation.addMessage("Straat moet gevuld zijn.", MessageType.ERROR);
					LOGGER.error("Method validateCIR_Adres. Straat not set.");
				}

            } else {
                loValidation.addMessage("Geen CIR_Adres ontvangen.", MessageType.INVALID);
				LOGGER.error("Method validateCIR_Adres. No CIR_Adres received.");
            }
            LOGGER.debug("Method validateCIR_Adres. Number of messages = " + loValidation.getValidationMessages().size());

        }  catch (Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_Adres. Validation failed.");
        }
        return loValidation;
    }

	/**
     * Returns the CIR_AdresType Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the CIR_AdresType to retrieve
     * @return 			the CIR_AdresType Entity
     */
    public CirAdrestype getCIR_AdresType(String pstrCode) {

        if (pstrCode != null && pstrCode.trim().length() > 0) {
            return cirAdrestypeRepository.findByCode(pstrCode);
        } else {
            LOGGER.debug("Method getCIR_AdresType. Cannot retrieve CIR_AdresType without a key.");
            return null;
        }
    }

    /**
     * Returns the active CIR_AdresType Entities .<br/>
     *
     * @return 		a Collection of actieve CIR_AdresType Entities.
     */
    @SuppressWarnings({"unchecked"})
    public List<CirAdrestype> getActiveCIR_AdresTypes() {
        return cirAdrestypeRepository.findAllActief();
    }

    /**
     * Saves the passed CIR_AdresType Entity.<br/>
     * <p/>
	 * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
	 *
	 * The CIR_AdresType Entity passed as argument will be validated {@link #validateCIR_AdresType(nl.devoorkant.insolventie.data.cir.CIR_AdresType)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The CIR_AdresType indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param poCIR_AdresType 	the CIR_AdresType Entity to save.
     * @return 			a Result Object, containing the result of the save action.
     */
    @Transactional(rollbackFor=Exception.class)
    public Result saveCIR_AdresType(CirAdrestype poCIR_AdresType) {
        LOGGER.debug("Method saveCIR_AdresType.");
        Result loResult = new Result();

        try {
            // Before saving the CIR_AdresType, it must be validated
            loResult = new Result(validateCIR_AdresType(poCIR_AdresType));

            if (loResult.isSuccessful()) {
				LOGGER.debug("Method saveCIR_AdresType. Update");
				poCIR_AdresType = cirAdrestypeRepository.save(poCIR_AdresType);

                loResult.setResultObject(poCIR_AdresType);
            }
        } catch (Exception loEx) {
            LOGGER.error("Method saveCIR_AdresType. Method failed.", loEx);
            loResult.setResult(false);
        }
        return loResult;
    }

/////
// Protected
/////
    /**
     * Create a new page handler.<br/>
     *
     * @param poObject   	an Object containing search criteria.
     * @param pnPageSize 	an int containing the Page size.
     * @return 				a PageHandler Object.
     */
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
//			if (poObject instanceof CIR_Adres) {
//				CIR_Adres loCIR_Adres = (CIR_Adres) poObject;
//
//                // 1. Compose the join
//                if (loCIR_Adres.getCIR_AdresType() != null) loJoin.append(" JOIN a.CIR_AdresType at");
//
//                // 2. Compose the where
//                lstrKoppel = " WHERE";
//
//                if (loCIR_Adres.getCIR_AdresType() != null) {
//                    loQueryParams.add(loCIR_Adres.getCIR_AdresType().getCode());
//
//                    loWhere.append(lstrKoppel).append(" at.code = ?").append(loQueryParams.size());
//                }
//
//				loSelectQuery.append("SELECT a FROM CIR_Adres a");
//                loSelectQuery.append(loJoin);
//				loSelectQuery.append(loWhere);
//				loSelectQuery.append(" ORDER BY a.tijd");
//				lstrSelectQuery = loSelectQuery.toString();
//
//				loCountQuery.append("SELECT COUNT(a) FROM CIR_Adres a");
//                loCountQuery.append(loJoin);
//				loCountQuery.append(loWhere);
//				lstrCountQuery = loCountQuery.toString();
//
//                ioLogger.info("Method createPageHandler. SelectQuery = {}", lstrSelectQuery);
//
//				return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
//
//			} else if (poObject instanceof CIR_AdresType) {
//				CIR_AdresType loCIR_AdresType = (CIR_AdresType) poObject;
//
//				// Compose the where
//				lstrKoppel = " WHERE";
//				if(loCIR_AdresType.getOmschrijving() != null) {
//					loQueryParams.add(loCIR_AdresType.getOmschrijving());
//
//					// Als er een %-teken in de tekst staat, dan moet er gezocht worden m.b.v. LIKE
//					if(!loCIR_AdresType.getOmschrijving().contains("%")) {
//						loWhere.append(lstrKoppel).append(" at.omschrijving = ?").append(loQueryParams.size());
//					} else loWhere.append(lstrKoppel).append(" at.omschrijving LIKE ?").append(loQueryParams.size());
//				}
//
//				loSelectQuery.append("SELECT at FROM CIR_AdresType at");
//				loSelectQuery.append(loWhere);
//				loSelectQuery.append(" ORDER BY at.omschrijving");
//				lstrSelectQuery = loSelectQuery.toString();
//
//				loCountQuery.append("SELECT COUNT(at) FROM CIR_AdresType at");
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
     * Checks the validity of an CIR_AdresType Entity.<br/>
     * <p/>
     * An CIR_AdresType Object must apply to the following rules:<br/>
     * <ol>
	 * <li> The CIR_AdresType must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
	 * @param poCIR_AdresType 	the CIR_AdresType Entity to validate.
     * @return 				a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
	private ValidationObject validateCIR_AdresType(CirAdrestype poCIR_AdresType) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(poCIR_AdresType != null) {

                // 1. The CIR_AdresType must already exist.
                if (poCIR_AdresType.getCode() == null || poCIR_AdresType.getCode().trim().equals("")) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", MessageType.INVALID);
                }

				// 2. The field omschrijving must contain a value
				if(poCIR_AdresType.getOmschrijving() == null || poCIR_AdresType.getOmschrijving().trim().equals("")) {
					loValidation.addMessage(poCIR_AdresType.getOmschrijving(), "Omschrijving is een verplicht veld.", MessageType.INVALID);
				}

			} else {
				loValidation.addMessage("Geen CIR_AdresType ontvangen.", MessageType.INVALID);
				LOGGER.error("Method validateCIR_AdresType. No CIR_AdresType received.");
			}
			LOGGER.info("Method validateCIR_AdresType. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_AdresType. Validation failed.");
		}
		return loValidation;
	}
}