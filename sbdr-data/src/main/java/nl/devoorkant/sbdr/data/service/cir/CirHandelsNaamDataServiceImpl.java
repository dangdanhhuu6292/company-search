package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import nl.devoorkant.sbdr.cir.data.model.CirHandelsnaam;
import nl.devoorkant.sbdr.cir.data.repository.CirAdresRepository;
import nl.devoorkant.sbdr.cir.data.repository.CirHandelsnaamRepository;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.*;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

/**
 * Stateless service bean with functionality for CIR_HandelsNamen.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
* <p/>
 * Copyright:       2015 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirHandelsNaamDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CirHandelsNaamDataServiceImpl implements CirHandelsNaamDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CirExceptieDataServiceImpl.class);

    @Autowired
    private CirAdresDataService cirAdresDataService;
    
    @Autowired 
    private CirHandelsnaamRepository cirHandelsnaamRepository;

    @Autowired
    private CirAdresRepository cirAdresRepository;

	private static final Logger ioLogger = LoggerFactory.getLogger(CirHandelsNaamDataService.class);

    /**
     * Returns a new, initialised CIR_HandelsNaam Object.<br/>
     *
	 * @return  a new CIR_HandelsNaam Object
     */
    public CirHandelsnaam getNewCIR_HandelsNaam() {
        return new CirHandelsnaam();
    }

    /**
     * Returns the CIR_HandelsNaam Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_HandelsNaam to retrieve
     * @return 		    the CIR_HandelsNaam Object
     */
    public CirHandelsnaam getCIR_HandelsNaam(Integer poID) {

        if (poID != null) {
            Optional<CirHandelsnaam> cirHandelsnaam = cirHandelsnaamRepository.findById(poID);
            return cirHandelsnaam != null ? cirHandelsnaam.get() : null;
        } else {
            LOGGER.debug("Method getCIR_HandelsNaam. Cannot retrieve CIR_HandelsNaam without a key.");
            return null;
        }
    }

    /**
     * Returns the CIR_HandelsNaam Object by insolventie id.<br/>
     *
     * @param poID		an Integer representing the CIR_HandelsNaam to retrieve
     * @return 		    the CIR_HandelsNaam Object
     */
    @Override
    public List<CirHandelsnaam> getCIR_HandelsNaamByInsolventieId(Integer cirInsolventieId) {

        if (cirInsolventieId != null) {
            return cirHandelsnaamRepository.findByInsolventieId(cirInsolventieId);
        } else {
            LOGGER.debug("Method getCIR_HandelsNaam. Cannot retrieve CIR_HandelsNaam with insolventie id: " + cirInsolventieId);
            return null;
        }
    }
    
    /**
	 * Saves the passed CIR_HandelsNaam Object.<br/>
	 * <p/>
	 * The CIR_HandelsNaam Object passed as argument will be validated {@link #validateCIR_HandelsNaam(nl.devoorkant.insolventie.data.cir.CIR_HandelsNaam)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * <li> Valid. When the passed CIR_HandelsNaam Object does not have an ID, a new client will be created, otherwise the client indicated by the ID will be updated. These
	 * actions can have two possible outcomes as well:<br/>
	 * <ol>
	 * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * </ol>
	 * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
	 *
     * @param poCIR_HandelsNaam 		the CIR_HandelsNaam to save.
	 * @return 				a Result Object, containing the result of the save action.
	 */
    @Transactional(rollbackFor=Exception.class)
	public Result saveCIR_HandelsNaam(CirHandelsnaam poCIR_HandelsNaam) throws DataServiceException {
		ioLogger.debug("Method saveCIR_HandelsNaam.");
		Result loResult = new Result();

		try {
			/** Before saving the CIR_HandelsNaam, it must be validated */
			loResult = new Result(validateCIR_HandelsNaam(poCIR_HandelsNaam));

			if (loResult.isSuccessful()) {

				if (poCIR_HandelsNaam.getId() == null) {

                    if (poCIR_HandelsNaam.getCirAdreses() != null && poCIR_HandelsNaam.getCirAdreses().size() > 0) {
                        Result loSaveResult;
                        Set<CirAdres> loSavedCIR_Adressen = new HashSet<CirAdres>(poCIR_HandelsNaam.getCirAdreses().size());

                        for (CirAdres loCIR_Adres : poCIR_HandelsNaam.getCirAdreses()) {
                            if (loCIR_Adres.getId() == null) {
                                loSaveResult = cirAdresDataService.saveCIR_Adres(loCIR_Adres);
                                if (loSaveResult.isSuccessful()) {
                                    loSavedCIR_Adressen.add((CirAdres) loSaveResult.getResultObject());
                                } else {
                                    throw new DataServiceException("Problem saving CIR_Adres");
                                }
                            } else {
                                loSavedCIR_Adressen.add(loCIR_Adres);
                            }
                        }

                        if (loSavedCIR_Adressen.size() > 0) {
                            poCIR_HandelsNaam.setCirAdreses(loSavedCIR_Adressen);
                        }
                    }

					ioLogger.debug("Method saveCIR_HandelsNaam. Create");
					poCIR_HandelsNaam = cirHandelsnaamRepository.save(poCIR_HandelsNaam);
				} else {
					ioLogger.debug("Method saveCIR_HandelsNaam. Update");
					poCIR_HandelsNaam = cirHandelsnaamRepository.save(poCIR_HandelsNaam);
				}
				loResult.setResultObject(poCIR_HandelsNaam);
			}
		} catch(Exception loEx) {
			ioLogger.error("Method saveCIR_HandelsNaam. Saving the CIR_HandelsNaam failed.", loEx);
			loResult.setResult(false);
		}
		return loResult;
	}
    
    /**
     * Remove the Handelsnaam and its underlying CIR_Adres.<br/>
     *
     * @param poCIR_Insolventie 	the CIR_Insolventie to remove.
     * @return 				        a Result Object, containing the result of the remove action.
     */
    @Override
    @Transactional(rollbackFor=Exception.class)   
    public Result removeCIR_Handelsnaam(CirHandelsnaam poCIR_Handelsnaam) {
        Result loResult = new Result();

        try {
            if (poCIR_Handelsnaam != null) {

            	boolean removalOk = true;
            	List<CirAdres> adressen = cirAdresRepository.findByHandelsnaamId(poCIR_Handelsnaam.getId());
            	
            	cirHandelsnaamRepository.delete(poCIR_Handelsnaam);
            	
            	if (adressen != null) {
            		for (CirAdres cirAdres : adressen) {
            			removalOk = cirAdresDataService.removeCIR_Adres(cirAdres.getId());
            			if (!removalOk)
            				break;            		
            		}
            	}
            	
            	if (!removalOk)
            		loResult.setResult(false);
                LOGGER.debug("Method removeCIR_Handelsnaam. The CIR_Handelsnaam is removed");

            } else {
                LOGGER.debug("Method removeCIR_Handelsnaam. The CIR_Handelsnaam to remove is not passed");
                loResult.setResult(false);
            }

        } catch(Exception loEx) {
            LOGGER.error("Method removeCIR_Handelsnaam. Removing the CIR_Handelsnaam failed.", loEx);
            loResult.setResult(false);
        }

        return loResult;
    }        

    /**
     * Checks the validity of a CIR_HandelsNaam Object.<br/>
     * <p/>
     * A CIR_HandelsNaam Object must apply to the following rules:<br/>
     * <ol>
     * <li> The field Handelsnaam must contain a value.</li>
     * <li> All the attached CIR_Adres Objects have to be valid as well.</li>
     * </ol>
     *
     * @param poCIR_HandelsNaam 	the CIR_HandelsNaam that must be validated.
     * @return 			        a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    public ValidationObject validateCIR_HandelsNaam(CirHandelsnaam poCIR_HandelsNaam) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if (poCIR_HandelsNaam != null) {

                // 1. The field Handelsnaam must contain a value.
                // JME 18-03-2014 - But is does not always contain a (meaningful) value, so empty has to be allowed
                //if (StringUtil.isEmptyOrNull(poCIR_HandelsNaam.getHandelsnaam())) {
                if (poCIR_HandelsNaam.getHandelsnaam() == null) {
                    loValidation.addMessage("Handelsnaam moet gevuld zijn.", MessageType.INVALID);
                    ioLogger.error("Method validateCIR_HandelsNaam. HandelsNaam not set.");
                }

                // 2. If there are CIR_Adressen, they have to be valid as well
                if (poCIR_HandelsNaam.getCirAdreses() != null) {
                    for (CirAdres loCIR_Adres : poCIR_HandelsNaam.getCirAdreses()) {
                        loValidation.addMessages(cirAdresDataService.validateCIR_Adres(loCIR_Adres).getValidationMessages());
                    }
                }

            } else {
                loValidation.addMessage("Geen CIR_HandelsNaam ontvangen.", MessageType.ERROR);
                ioLogger.error("Method validateCIR_HandelsNaam. No CIR_HandelsNaam received.");
            }
            ioLogger.info("Method validateCIR_HandelsNaam. Number of messages = " + loValidation.getValidationMessages().size());

        }  catch (Exception loEx) {
            loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
            ioLogger.error("Method validateCIR_HandelsNaam. Validation failed.");
        }
        return loValidation;
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
//            CIR_HandelsNaam loCIR_HandelsNaam = (CIR_HandelsNaam) poObject;
//            ArrayList<Object> loQueryParams = new ArrayList<Object>();
//            StringBuilder loWhere = new StringBuilder();
//
//            StringBuffer loSelectQuery = new StringBuffer();
//            loSelectQuery.append("SELECT ch FROM CIR_HandelsNaam ch");
//            loSelectQuery.append(loWhere);
//            loSelectQuery.append(" ORDER BY ch.handelsnaam");
//            String lstrSelectQuery = loSelectQuery.toString();
//
//            StringBuffer loCountQuery = new StringBuffer();
//            loCountQuery.append("SELECT COUNT(ch) FROM CIR_HandelsNaam ch");
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


}