package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import nl.devoorkant.sbdr.cir.data.model.CirCbv;
import nl.devoorkant.sbdr.cir.data.model.CirCbvtype;
import nl.devoorkant.sbdr.cir.data.repository.CirCbvRepository;
import nl.devoorkant.sbdr.cir.data.repository.CirCbvtypeRepository;
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
 * Stateless service bean with functionality for CIR_CBVs.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2015 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirCbvDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CirCbvDataServiceImpl implements CirCbvDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CirCbvDataServiceImpl.class);

    @Autowired 
    private CirAdresDataService cirAdresDataService;
    
    @Autowired
    private CirCbvRepository cirCbvRepository;

    @Autowired
    private CirCbvtypeRepository cirCbvtypeRepository;
    
    /**
     * Returns a new, initialised CIR_CBV Object.<br/>
     *
	 * @return  a new CIR_CBV Object
     */
    public CirCbv getNewCIR_CBV() {
        return new CirCbv();
    }

    /**
     * Returns a new, initialised CIR_CBV Object, based on the passed CIR_CBVTypeCode.<br/>
     *
     * @param pstrCIR_CBVTypeCode	a String representing a CIR_CBVType
     * @return 					        a new CIR_CBV Object
     */
    public CirCbv getNewCIR_CBV(String pstrCIR_CBVTypeCode) {
        CirCbv loResult = getNewCIR_CBV();
        loResult.setCirCbvtype(cirCbvtypeRepository.findByCode(pstrCIR_CBVTypeCode));
        return loResult;
    }

    /**
     * Returns the CIR_CBV Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_CBV to retrieve
     * @return 		    the CIR_CBV Object
     */
    public CirCbv getCIR_CBV(Integer poID) {

        if (poID != null) {
            Optional<CirCbv> cirCbv = cirCbvRepository.findById(poID);
            return cirCbv != null ? cirCbv.get() : null;
        } else {
            LOGGER.debug("Method getCIR_CBV. Cannot retrieve CIR_CBV without a key.");
            return null;
        }
    }
    
    /**
     * Returns the CIR_Cbv Object by insolventie id.<br/>
     *
     * @param poID		an Integer representing the CIR_HandelsNaam to retrieve
     * @return 		    the CIR_HandelsNaam Object
     */
    @Override
    public List<CirCbv> getCIR_CbvByInsolventieId(Integer cirInsolventieId) {

        if (cirInsolventieId != null) {
            return cirCbvRepository.findByInsolventieId(cirInsolventieId);
        } else {
            LOGGER.debug("Method getCIR_HandelsNaam. Cannot retrieve CIR_HandelsNaam with insolventie id: " + cirInsolventieId);
            return null;
        }
    }    

	/**
	 * Saves the passed CIR_CBV Object.<br/>
	 * <p/>
	 * The CIR_CBV Object passed as argument will be validated {@link #validateCIR_CBV(nl.devoorkant.insolventie.data.cir.CIR_CBV)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * <li> Valid. When the passed CIR_CBV Object does not have an ID, a new client will be created, otherwise the client indicated by the ID will be updated. These
	 * actions can have two possible outcomes as well:<br/>
	 * <ol>
	 * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * </ol>
	 * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
	 *
     * @param poCIR_CBV 		the CIR_CBV to save.
	 * @return 				a Result Object, containing the result of the save action.
	 */
    @Transactional(rollbackFor=Exception.class)
	public Result saveCIR_CBV(CirCbv poCIR_CBV) {
		LOGGER.debug("Method saveCIR_CBV.");
		Result loResult = new Result();

		try {
			/** Before saving the CIR_CBV, it must be validated */
			loResult = new Result(validateCIR_CBV(poCIR_CBV));

			if (loResult.isSuccessful()) {

				if (poCIR_CBV.getId() == null) {

                    if (poCIR_CBV.getCirAdres() != null && poCIR_CBV.getCirAdres().getId() == null) {
                    	loResult = cirAdresDataService.saveCIR_Adres(poCIR_CBV.getCirAdres());
                        poCIR_CBV.setCirAdres((CirAdres)loResult.getResultObject());
                    }

                    if (loResult.isSuccessful()) {
                        poCIR_CBV = cirCbvRepository.save(poCIR_CBV);
                    }

				} else {
					LOGGER.debug("Method saveCIR_CBV. Update");
					poCIR_CBV = cirCbvRepository.save(poCIR_CBV);
				}
				loResult.setResultObject(poCIR_CBV);
			}
		} catch(Exception loEx) {
			LOGGER.error("Method saveCIR_CBV. Saving the CIR_CBV failed.", loEx);
			loResult.setResult(false);
		}
        LOGGER.info("Method saveCIR_CBV. Saved Object is {}", ((CirCbv)loResult.getResultObject()).toString());
		return loResult;
	}

    /**
     * Checks the validity of a CIR_CBV Object.<br/>
     * <p/>
     * A CIR_CBV Object must apply to the following rules:<br/>
     * <ol>
     * <li> There has to be a CBVType</li>
     * <li> There has to be a CIR_Adres, which must be valid as well</li>
     * <li> The field DatumBegin must contain a value.</li>
     * <li> The field Naam must contain a value.</li>
     * </ol>
     *
     * @param poCIR_CBV 	the CIR_CBV that must be validated.
     * @return 			        a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    public ValidationObject validateCIR_CBV(CirCbv poCIR_CBV) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if (poCIR_CBV != null) {

                // 1. There has to be a CBVType.
                if (poCIR_CBV.getCirCbvtype() == null) {
                    loValidation.addMessage("CBVType moet gezet zijn.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_CBV. CBVType not set.");
                }

                // 2. There has to be a CIR_Adres, which must be valid as well.
                if (poCIR_CBV.getCirAdres() !=null) {
                    loValidation.addMessages(cirAdresDataService.validateCIR_Adres(poCIR_CBV.getCirAdres()).getValidationMessages());
                } else {
                    loValidation.addMessage("Adres moet gezet zijn.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_CBV. CIR_Adres not set.");
                }

                // 3. The field DatumPublicatie must contain a value.
                if (poCIR_CBV.getDatumBegin() == null) {
                    loValidation.addMessage("Datum begin moet gevuld zijn.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_Publicatie. DatumBegin not set.");
                }

                // 4. The field Naam must contain a value.
                if (StringUtil.isEmptyOrNull(poCIR_CBV.getNaam())) {
                    loValidation.addMessage("Naam moet gevuld zijn.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_Publicatie. Naam not set.");
                }

            } else {
                loValidation.addMessage("Geen CIR_CBV ontvangen.", MessageType.ERROR);
                LOGGER.error("Method validateCIR_CBV. No CIR_CBV received.");
            }
            LOGGER.info("Method validateCIR_CBV. Number of messages = " + loValidation.getValidationMessages().size());

        }  catch (Exception loEx) {
            loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
            LOGGER.error("Method validateCIR_CBV. Validation failed.");
        }
        return loValidation;
    }

    /**
     * Returns the CIR_CBVType Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the CIR_CBVType to retrieve
     * @return 			the CIR_CBVType Entity
     */
    public CirCbvtype getCIR_CBVType(String pstrCode) {

        if (pstrCode != null && pstrCode.trim().length() > 0) {
            return cirCbvtypeRepository.findByCode(pstrCode);
        } else {
            LOGGER.debug("Method getCIR_CBVType. Cannot retrieve CIR_CBVType without a key.");
            return null;
        }
    }

    /**
     * Returns the active CIR_CBVType Entities .<br/>
     *
     * @return 		a Collection of actieve CIR_CBVType Entities.
     */
    @SuppressWarnings({"unchecked"})
    public List<CirCbvtype> getActiveCIR_CBVTypes() {
        return cirCbvtypeRepository.findAllActief();
    }

    /**
     * Saves the passed CIR_CBVType Entity.<br/>
     * <p/>
     * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
     *
     * The CIR_CBVType Entity passed as argument will be validated {@link #validateCIR_CBVType(nl.devoorkant.insolventie.data.cir.CIR_CBVType)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The CIR_CBVType indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param poCIR_CBVType 	the CIR_CBVType Entity to save.
     * @return 			a Result Object, containing the result of the save action.
     */
    @Transactional(rollbackFor=Exception.class)
    public Result saveCIR_CBVType(CirCbvtype poCIR_CBVType) {
        LOGGER.debug("Method saveCIR_CBVType.");
        Result loResult = new Result();

        try {
            // Before saving the CIR_CBVType, it must be validated
            loResult = new Result(validateCIR_CBVType(poCIR_CBVType));

            if (loResult.isSuccessful()) {
                LOGGER.debug("Method saveCIR_CBVType. Update");
                poCIR_CBVType = cirCbvtypeRepository.save(poCIR_CBVType);

                loResult.setResultObject(poCIR_CBVType);
            }
        } catch (Exception loEx) {
            LOGGER.error("Method saveCIR_CBVType. Method failed.", loEx);
            loResult.setResult(false);
        }
        return loResult;
    }
    
    /**
     * Remove the Cbv and its underlying CIR_Adres.<br/>
     *
     * @param poCIR_Insolventie 	the CIR_Insolventie to remove.
     * @return 				        a Result Object, containing the result of the remove action.
     */
    @Override
    @Transactional(rollbackFor=Exception.class) 
    public Result removeCIR_CBV(CirCbv poCIR_CBV) {
        Result loResult = new Result();

        try {
            if (poCIR_CBV != null) {

            	boolean removalOk = true;

            	CirAdres adres = poCIR_CBV.getCirAdres();
            	
            	cirCbvRepository.delete(poCIR_CBV);
            	           	
            	if (adres != null) {
            		removalOk = cirAdresDataService.removeCIR_Adres(adres.getId());
            	}
            	
            	if (!removalOk)
            		loResult.setResult(false);             	
                LOGGER.debug("Method removeCIR_CBV. The CIR_CBV is removed");

            } else {
                LOGGER.debug("Method removeCIR_CBV. The CIR_CBV to remove is not passed");
                loResult.setResult(false);
            }

        } catch(Exception loEx) {
            LOGGER.error("Method removeCIR_CBV. Removing the CIR_CBV failed.", loEx);
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
//            CIR_CBV loCIR_CBV = (CIR_CBV) poObject;
//            ArrayList<Object> loQueryParams = new ArrayList<Object>();
//            StringBuilder loWhere = new StringBuilder();
//
//            StringBuffer loSelectQuery = new StringBuffer();
//            loSelectQuery.append("SELECT ch FROM CIR_CBV ch");
//            loSelectQuery.append(loWhere);
//            loSelectQuery.append(" ORDER BY ch.handelsnaam");
//            String lstrSelectQuery = loSelectQuery.toString();
//
//            StringBuffer loCountQuery = new StringBuffer();
//            loCountQuery.append("SELECT COUNT(ch) FROM CIR_CBV ch");
//            loCountQuery.append(loWhere);
//            String lstrCountQuery = loCountQuery.toString();
//
//            return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
//
//        } catch(Exception loEx) {
//            ioLogger.error("Method createPageHandler. Creatie pagehandler mislukt.", loEx);
//        }
//        return null;
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
     * Checks the validity of an CIR_CBVType Entity.<br/>
     * <p/>
     * An CIR_CBVType Object must apply to the following rules:<br/>
     * <ol>
     * <li> The CIR_CBVType must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param poCIR_CBVType 	the CIR_CBVType Entity to validate.
     * @return 				    a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validateCIR_CBVType(CirCbvtype poCIR_CBVType) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(poCIR_CBVType != null) {

                // 1. The CIR_CBVType must already exist.
                if (poCIR_CBVType.getCode() == null || poCIR_CBVType.getCode().trim().equals("")) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(poCIR_CBVType.getOmschrijving() == null || poCIR_CBVType.getOmschrijving().trim().equals("")) {
                    loValidation.addMessage(poCIR_CBVType.getOmschrijving(), "Omschrijving is een verplicht veld.", MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen CIR_CBVType ontvangen.", MessageType.INVALID);
                LOGGER.error("Method validateCIR_CBVType. No CIR_CBVType received.");
            }
            LOGGER.info("Method validateCIR_CBVType. Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
            LOGGER.error("Method validateCIR_CBVType. Validation failed.");
        }
        return loValidation;
    }

}