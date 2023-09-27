package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirPublicatiekenmerk;
import nl.devoorkant.sbdr.cir.data.repository.CirPublicatiekenmerkRepository;
import nl.devoorkant.util.DateUtil;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

/**
 * Stateless service bean with functionality for CIR_PublicatieKenmerken.
 * <p/>
 * Copyright:       2015 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirPublicatieKenmerkDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CirPublicatieKenmerkDataServiceImpl implements CirPublicatieKenmerkDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CirPublicatieKenmerkDataServiceImpl.class);

    @Autowired 
    private CirPublicatiekenmerkRepository cirPublicatiekenmerkRepository;

    /**
     * Returns a new, initialised CIR_PublicatieKenmerk Object.<br/>
     *
     * @return 		                a new CIR_PublicatieKenmerk Object
     */
    public CirPublicatiekenmerk getNewCIR_PublicatieKenmerk() {
        CirPublicatiekenmerk loResult = new CirPublicatiekenmerk();
        loResult.setTijdExtractie(DateUtil.getCurrentTimestamp());
        return loResult;
    }

    /**
     * Returns the CIR_PublicatieKenmerk Object by primary key.<br/>
     *
     * @param poCIR_PublicatieKenmerk_ID 		an Integer representing the CIR_PublicatieKenmerk
     * @return 					                the CIR_PublicatieKenmerk Entity
     */
    public CirPublicatiekenmerk getCIR_PublicatieKenmerk(Integer poCIR_PublicatieKenmerk_ID) {

        if(poCIR_PublicatieKenmerk_ID != null) {
            Optional<CirPublicatiekenmerk> cirPublicatiekenmerk = cirPublicatiekenmerkRepository.findById(poCIR_PublicatieKenmerk_ID);
            return cirPublicatiekenmerk != null ? cirPublicatiekenmerk.get() : null;
        } else {
            LOGGER.debug("Method getCIR_PublicatieKenmerk. Cannot retrieve CIR_PublicatieKenmerk without a key.");
            return null;
        }
    }

    /**
     * Returns a Collection of CIR_PublicatieKenmerken Entity based on the passed PublicatieKenmerk.<br/>
     *
     * @param pstrPublicatieKenmerk	    a String containing the PublicatieKenmerk
     * @return 		                    a Collection of CIR_PublicatieKenmerken, containing the presented PublicatieKenmerk.
     */
    @SuppressWarnings({"unchecked"})
    public List<CirPublicatiekenmerk> getCIR_PublicatieKenmerkenByPublicatieKenmerk(String pstrPublicatieKenmerk) {

        try {
            if (StringUtil.isNotEmptyOrNull(pstrPublicatieKenmerk)) {
                return cirPublicatiekenmerkRepository.findByPublicatieKenmerk(pstrPublicatieKenmerk);
            } else {
                LOGGER.debug("Method getCIR_PublicatieKenmerkenByPublicatieKenmerk. Cannot retrieve a CIR_PublicatieKenmerk without a PublicatieKenmerk.");
            }
        } catch(NoResultException loEx) {
            LOGGER.info("Method getCIR_PublicatieKenmerkenByPublicatieKenmerk. No CIR_PublicatieKenmerk for this PublicatieKenmerk.");
        } catch(Exception loEx) {
            LOGGER.error("Method getCIR_PublicatieKenmerkenByPublicatieKenmerk. Retrieval of CIR_PublicatieKenmerk for this PublicatieKenmerk failed.", loEx);
        }

        return null;
    }

    /**
     * Returns a List of unprocessed CIR_PublicatieKenmerken .<br/>
     *
     * @return 		a Collection of unprocessed CIR_PublicatieKenmerken.
     */
    @SuppressWarnings({"unchecked"})
    public List<CirPublicatiekenmerk> getUnprocessedCIR_PublicatieKenmerken() {
        return cirPublicatiekenmerkRepository.findByTijdverwerktIsNull();
    }

    /**
     * Saves the passed CIR_PublicatieKenmerk Entity.<br/>
     * <p/>
     * The CIR_PublicatieKenmerk Object passed as argument will be validated {@link #validateCIR_PublicatieKenmerk(nl.devoorkant.insolventie.data.cir.CIR_PublicatieKenmerk)} first. The validation can have two possible
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
     * @param poCIR_PublicatieKenmerk 	the CIR_PublicatieKenmerk to save.
     * @return 					a Result Object, containing the result of the save action.
     */
    @Transactional(rollbackFor=Exception.class)
	public Result saveCIR_PublicatieKenmerk(CirPublicatiekenmerk poCIR_PublicatieKenmerk) {
		LOGGER.debug("Method saveCIR_PublicatieKenmerk. PublicatieKenmerk is {}", poCIR_PublicatieKenmerk.toString());
		Result loResult = new Result();

		try {
			/** Before saving the CIR_PublicatieKenmerk, it must be validated */
			loResult = new Result(validateCIR_PublicatieKenmerk(poCIR_PublicatieKenmerk));

			if(loResult.isSuccessful()) {
				try {
					poCIR_PublicatieKenmerk = cirPublicatiekenmerkRepository.save(poCIR_PublicatieKenmerk);
				} catch (Exception e) {
					LOGGER.warn("save CIR_PublicatieKenmerk: " + poCIR_PublicatieKenmerk.getPublicatieKenmerk() + " already exists in database: " + e.getMessage());
				}

                loResult.setResultObject(poCIR_PublicatieKenmerk);

			}
		} catch(Exception loEx) {
            LOGGER.error("Method saveCIR_PublicatieKenmerk. Method failed.", loEx);
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
//			CIR_PublicatieKenmerk loCIR_PublicatieKenmerk = (CIR_PublicatieKenmerk) poObject;
//			ArrayList<Object> loQueryParams = new ArrayList<Object>();
//			StringBuilder loWhere = new StringBuilder();
//
//			StringBuffer loSelectQuery = new StringBuffer();
//			loSelectQuery.append("SELECT c FROM CIR_PublicatieKenmerk c");
//			loSelectQuery.append(loWhere);
//			loSelectQuery.append(" ORDER BY c.code");
//			String lstrSelectQuery = loSelectQuery.toString();
//
//			StringBuffer loCountQuery = new StringBuffer();
//			loCountQuery.append("SELECT COUNT(c) FROM CIR_PublicatieKenmerk c");
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
     * Checks the validity of a CIR_PublicatieKenmerk Object.<br/>
     * <p/>
     * A CIR_PublicatieKenmerk Object must apply to the following rules:<br/>
     * <ol>
     * <li> The field TijdExtractie must contain a value.</li>
     * </ol>
     *
     * @param poCIR_PublicatieKenmerk 	CIR_PublicatieKenmerk that must be validated.
     * @return 					a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
	private ValidationObject validateCIR_PublicatieKenmerk(CirPublicatiekenmerk poCIR_PublicatieKenmerk) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(poCIR_PublicatieKenmerk != null) {

                // 1. Check TijdAangemaakt
				if(poCIR_PublicatieKenmerk.getTijdExtractie() == null) {
					loValidation.addMessage(poCIR_PublicatieKenmerk.getTijdExtractie(), "Invoertijd is verplicht.", MessageType.INVALID);
				}

                // 2. PublicatieKenmerk moet gevuld zijn.
                if (StringUtil.isEmptyOrNull(poCIR_PublicatieKenmerk.getPublicatieKenmerk())) {
                    loValidation.addMessage(poCIR_PublicatieKenmerk.getTijdExtractie(), "Publicatiekenmerk is verplicht.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_PublicatieKenmerk. No PublicatieKenmerk received.");
                }

			} else {
				loValidation.addMessage("Geen CIR_PublicatieKenmerk meegegeven.", MessageType.INVALID);
				LOGGER.info("Method validateCIR_PublicatieKenmerk. No CIR_PublicatieKenmerk recieved.");
			}
			LOGGER.debug("Method validateCIR_PublicatieKenmerk. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_PublicatieKenmerk. Validation failed.");
		}
		return loValidation;
	}
}