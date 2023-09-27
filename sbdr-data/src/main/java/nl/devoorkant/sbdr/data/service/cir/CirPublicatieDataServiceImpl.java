package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirPublicatie;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatiesoort;
import nl.devoorkant.sbdr.cir.data.model.CirZittingslocatie;
import nl.devoorkant.sbdr.cir.data.repository.CirPublicatieRepository;
import nl.devoorkant.sbdr.cir.data.repository.CirPublicatiesoortRepository;
import nl.devoorkant.sbdr.cir.data.repository.CirZittingslocatieRepository;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.*;
import java.util.*;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

/**
 * Stateless service bean with functionality for CIR_Publicaties.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2015 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirPublicatieDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CirPublicatieDataServiceImpl implements CirPublicatieDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CirPublicatieDataServiceImpl.class);

    @Autowired
    private CirPublicatieRepository cirPublicatieRepository;
    
    @Autowired
    private CirPublicatiesoortRepository cirPublicatiesoortRepository;
    
    @Autowired
    private CirZittingslocatieRepository cirZittingslocatieRepository;
        
    @Autowired
    private CirInsolventieDataService cirInsolventieDataService;

    /**
     * Returns a new, initialised CIR_Publicatie Object.<br/>
     *
	 * @return  a new CIR_Publicatie Object
     */
    public CirPublicatie getNewCIR_Publicatie() {
        return new CirPublicatie();
    }

    /**
     * Returns a new, initialised CIR_Publicatie Object, based on the passed CIR_PublicatieSoortCode.<br/>
     *
     * @param pstrCIR_PublicatieSoortCode	a String representing a CIR_PublicatieSoort
     * @return 					        a new CIR_Publicatie Object
     */
    @Override
    public CirPublicatie getNewCIR_Publicatie(String pstrCIR_PublicatieSoortCode) {
        CirPublicatie loResult = getNewCIR_Publicatie();
        loResult.setCirPublicatiesoort(getCIR_PublicatieSoort(pstrCIR_PublicatieSoortCode));
        return loResult;
    }

    /**
     * Returns the CIR_Publicatie Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_Publicatie to retrieve
     * @return 		    the CIR_Publicatie Object
     */
    @Override
    public CirPublicatie getCIR_Publicatie(Integer poID) {

        if (poID != null) {
            Optional<CirPublicatie> cirPublicatie = cirPublicatieRepository.findById(poID);
            return cirPublicatie != null ? cirPublicatie.get() : null;
        } else {
            LOGGER.debug("Method getCIR_Publicatie. Cannot retrieve CIR_Publicatie without a key.");
            return null;
        }
    }
    
    @Override
    public List<CirPublicatie> getCIR_PublicatieByInsolventieId(Integer cirInsolventieId) {
    	if (cirInsolventieId != null)
    		return cirPublicatieRepository.findByInsolventieId(cirInsolventieId);
    	else {
            LOGGER.debug("Method getCIR_PublicatieByInsolventieId. Cannot retrieve CIR_Publicatie with insolventie id: " + cirInsolventieId);
            return null;
        }
    		
    }

	/**
	 * Saves the passed CIR_Publicatie Object.<br/>
	 * <p/>
	 * The CIR_Publicatie Object passed as argument will be validated {@link #validateCIR_Publicatie(nl.devoorkant.insolventie.data.cir.CIR_Publicatie)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * <li> Valid. When the passed CIR_Publicatie Object does not have an ID, a new client will be created, otherwise the client indicated by the ID will be updated. These
	 * actions can have two possible outcomes as well:<br/>
	 * <ol>
	 * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * </ol>
	 * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
	 *
     * @param poCIR_Publicatie 		the CIR_Publicatie to save.
	 * @return 				a Result Object, containing the result of the save action.
	 */
    @Transactional(rollbackFor=Exception.class)
	public Result saveCIR_Publicatie(CirPublicatie poCIR_Publicatie) {
		LOGGER.debug("Method saveCIR_Publicatie.");
		Result loResult = new Result();

		try {
			/** Before saving the CIR_Publicatie, it must be validated */
			loResult = new Result(validateCIR_Publicatie(poCIR_Publicatie));

			if (loResult.isSuccessful()) {

				LOGGER.debug("Method saveCIR_Publicatie.");
				poCIR_Publicatie = cirPublicatieRepository.save(poCIR_Publicatie);
				
				loResult.setResultObject(poCIR_Publicatie);
			}
		} catch(Exception loEx) {
			LOGGER.error("Method saveCIR_Publicatie. Saving the CIR_Publicatie failed.", loEx);
			loResult.setResult(false);
		}
		return loResult;
	}
    
    /**
     * Remove the Publicatie and its underlying CIR_Zittinglocatie, CIR_Insolventie.<br/>
     *
     * @param poCIR_Insolventie 	the CIR_Insolventie to remove.
     * @return 				        a Result Object, containing the result of the remove action.
     */
    @Override
    @Transactional(rollbackFor=Exception.class)    
    public Result removeCIR_Publicatie(CirPublicatie poCIR_Publicatie) {
        Result loResult = new Result();

        try {
            if (poCIR_Publicatie != null) {

            	boolean removalOk = true;
            	
            	CirZittingslocatie cirZittinglocatie = poCIR_Publicatie.getCirZittingslocatie();
            	//cirPublicatieRepository.findZittingslocatieByPublicatieId(poCIR_Publicatie.getId());
            	//CirInsolventie cirInsolventie = poCIR_Publicatie.getCirInsolventie();
            	
            	cirPublicatieRepository.delete(poCIR_Publicatie);

            	
            	// May be more publicaties reference same zittingslocatie
            	//if (cirZittinglocatie != null) {
            	//	cirZittingslocatieRepository.delete(cirZittinglocatie);
            	//}
            	
            	LOGGER.debug("Method removeCIR_Publicatie. The CIR_Publicatie is removed");

            } else {
                LOGGER.debug("Method removeCIR_Publicatie. The CIR_Publicatie to remove is not passed");
                loResult.setResult(false);
            }

        } catch(Exception loEx) {
            LOGGER.error("Method removeCIR_Publicatie. Removing the CIR_Publicatie failed.", loEx);
            loResult.setResult(false);
        }

        return loResult;
    }     

    /**
     * Checks the validity of a CIR_Publicatie Object.<br/>
     * <p/>
     * A CIR_Publicatie Object must apply to the following rules:<br/>
     * <ol>
	 * <li> The PublicatieSoort must be set.</li>
     * <li> The field PublicatieKenmerk must contain a value.</li>
     * <li> The field DatumPublicatie must contain a value.</li>
     * <li> The field PublicatieOmschrijving must contain a value.</li>
     * <li> The field CodePublicerendeInstantie must contain a value.</li>
     * </ol>
     *
     * @param poCIR_Publicatie 	the CIR_Publicatie that must be validated.
     * @return 			        a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    public ValidationObject validateCIR_Publicatie(CirPublicatie poCIR_Publicatie) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if (poCIR_Publicatie != null) {

                // 1. The PublicatieSoort must be set.
                if (poCIR_Publicatie.getCirPublicatiesoort() == null) {
                    loValidation.addMessage("Publicatiesoort moet bepaald zijn.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_Publicatie. PublicatieSoort not set.");
                }

                // 2. The field PublicatieKenmerk must contain a value.
                if (StringUtil.isEmptyOrNull(poCIR_Publicatie.getPublicatieKenmerk())) {
                    loValidation.addMessage("Publicatiekenmerk moet gevuld zijn.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_Publicatie. PublicatieKenmerk not set.");
                }

                // 3. The field DatumPublicatie must contain a value.
                if (poCIR_Publicatie.getDatumPublicatie() == null) {
                    loValidation.addMessage("Datum publicatie moet gevuld zijn.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_Publicatie. DatumPublicatie not set.");
                }

                // 4. The field PublicatieOmschrijving must contain a value.
                if (StringUtil.isEmptyOrNull(poCIR_Publicatie.getPublicatieOmschrijving())) {
                    loValidation.addMessage("Omschrijving moet gevuld zijn.", MessageType.INVALID);
					LOGGER.error("Method validateCIR_Publicatie. PublicatieOmschrijving not set.");
				}

                // 5. The field CodePublicerendeInstantie must contain a value.
                if (StringUtil.isEmptyOrNull(poCIR_Publicatie.getCodePublicerendeInstantie())) {
                    loValidation.addMessage("Publicerende instantie moet gevuld zijn.", MessageType.INVALID);
                    LOGGER.error("Method validateCIR_Publicatie. CodePublicerendeInstantie not set.");
                }

                // 6. If there is a CIR_ZittingsLocatie, it has to be valid as well
                if (poCIR_Publicatie.getCirZittingslocatie() !=null) {
                    loValidation.addMessages(validateCIR_ZittingsLocatie(poCIR_Publicatie.getCirZittingslocatie()).getValidationMessages());
                }

                // 7. If there is a CIR_Insolventie, it has to be valid as well
                if (poCIR_Publicatie.getCirInsolventie() !=null) {
                    loValidation.addMessages(cirInsolventieDataService.validateCIR_Insolventie(poCIR_Publicatie.getCirInsolventie()).getValidationMessages());
                }

            } else {
                loValidation.addMessage("Geen CIR_Publicatie ontvangen.", MessageType.ERROR);
				LOGGER.error("Method validateCIR_Publicatie. No CIR_Publicatie received.");
            }
            LOGGER.info("Method validateCIR_Publicatie. Number of messages = " + loValidation.getValidationMessages().size());

        }  catch (Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_Publicatie. Validation failed.");
        }
        return loValidation;
    }

	/**
     * Returns the CIR_PublicatieSoort Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the CIR_PublicatieSoort to retrieve
     * @return 			the CIR_PublicatieSoort Entity
     */
    public CirPublicatiesoort getCIR_PublicatieSoort(String pstrCode) {
        LOGGER.debug("Method getCIR_PublicatieSoort. Code is {}.", pstrCode);

        if (pstrCode != null && pstrCode.trim().length() > 0) {
            return cirPublicatiesoortRepository.findByCode(pstrCode);
        } else {
            LOGGER.debug("Method getCIR_PublicatieSoort. Cannot retrieve CIR_PublicatieSoort without a key.");
            return null;
        }
    }

    /**
     * Returns the active CIR_PublicatieSoort Entities .<br/>
     *
     * @return 		a Collection of actieve CIR_PublicatieSoort Entities.
     */
    @SuppressWarnings({"unchecked"})
    public List<CirPublicatiesoort> getActiveCIR_PublicatieSoorten() {
        return cirPublicatiesoortRepository.findAllActief();
    }

    /**
     * Saves the passed CIR_PublicatieSoort Entity.<br/>
     * <p/>
	 * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
	 *
	 * The CIR_PublicatieSoort Entity passed as argument will be validated {@link #validateCIR_PublicatieSoort(nl.devoorkant.insolventie.data.cir.CIR_PublicatieSoort)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The CIR_PublicatieSoort indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param poCIR_PublicatieSoort 	the CIR_PublicatieSoort Entity to save.
     * @return 			                a Result Object, containing the result of the save action.
     */
    @Transactional(rollbackFor=Exception.class)
    public Result saveCIR_PublicatieSoort(CirPublicatiesoort poCIR_PublicatieSoort) {
        LOGGER.debug("Method saveCIR_PublicatieSoort.");
        Result loResult = new Result();

        try {
            // Before saving the CIR_PublicatieSoort, it must be validated
            loResult = new Result(validateCIR_PublicatieSoort(poCIR_PublicatieSoort));

            if (loResult.isSuccessful()) {
				LOGGER.debug("Method saveCIR_PublicatieSoort. Update");
				poCIR_PublicatieSoort = cirPublicatiesoortRepository.save(poCIR_PublicatieSoort);

                loResult.setResultObject(poCIR_PublicatieSoort);
            }
        } catch (Exception loEx) {
            LOGGER.error("Method saveCIR_PublicatieSoort. Method failed.", loEx);
            loResult.setResult(false);
        }
        return loResult;
    }

    /**
     * Returns a new, initialised and stored CIR_ZittingsLocatie Object, based on the passed parameters.<br/>
     *
     * @param pstrStraat	    a String containing the Straat
     * @param pstrPlaats	    a String containing the Plaats
     * @param pstrHuisNummer	a String containing the Huisnummer
     * @return 					a new, initialised CIR_ZittingsLocatie Object
     */
    public CirZittingslocatie createNewCIR_ZittingsLocatie(String pstrStraat, String pstrPlaats, String pstrHuisNummer) {
        LOGGER.info("Method createNewCIR_ZittingsLocatie.");
        CirZittingslocatie loCIR_ZittingsLocatie = new CirZittingslocatie();

        loCIR_ZittingsLocatie.setPlaats(pstrPlaats);
        loCIR_ZittingsLocatie.setStraat(pstrStraat);
        loCIR_ZittingsLocatie.setHuisNummer(pstrHuisNummer);

        return (CirZittingslocatie)saveCIR_ZittingsLocatie(loCIR_ZittingsLocatie).getResultObject();
    }

    /**
     * Returns a CIR_ZittingsLocatie Entity based on the passed criteria.<br/>
     *
     * @param pstrStraat	    a String containing the Straat
     * @param pstrPlaats	    a String containing the Plaats
     * @param pstrHuisNummer	a String containing the Huisnummer
     * @return 			        the CIR_ZittingsLocatie Entity
     */
    public CirZittingslocatie getCIR_ZittingsLocatieByStraatPlaatsAndHuisNummer(String pstrStraat, String pstrPlaats, String pstrHuisNummer) {

        try {
            if (StringUtil.isNotEmptyOrNull(pstrStraat) && StringUtil.isNotEmptyOrNull(pstrPlaats) && StringUtil.isNotEmptyOrNull(pstrHuisNummer)) {
                List<CirZittingslocatie> results = cirZittingslocatieRepository.findByPlaatsStraatHuisnummer(pstrPlaats, pstrStraat, pstrHuisNummer);

                if (results != null && results.size() == 1)
                	return results.get(0);
                else
                	return null;
            } else {
                LOGGER.debug("Method getCIR_ZittingsLocatieByStraatPlaatsAndHuisNummer. Cannot retrieve a CIR_ZittingsLocatie without (some of the) parameters.");
            }
        } catch(NoResultException loEx) {
            LOGGER.info("Method getCIR_ZittingsLocatieByStraatPlaatsAndHuisNummer. No CIR_ZittingsLocatie for this combination.");
        } catch(NonUniqueResultException loEx) {
            LOGGER.error("Method getCIR_ZittingsLocatieByStraatPlaatsAndHuisNummer. More than one CIR_ZittingsLocatie for this combination.");
        } catch(Exception loEx) {
            LOGGER.error("Method getCIR_ZittingsLocatieByStraatPlaatsAndHuisNummer. Retrieval of CIR_ZittingsLocatie for this combination failed.", loEx);
        }

        return null;
    }

    /**
     * Saves the passed CIR_ZittingsLocatie Object.<br/>
     * <p/>
     * The CIR_ZittingsLocatie Object passed as argument will be validated {@link #validateCIR_ZittingsLocatie(nl.devoorkant.insolventie.data.cir.CIR_ZittingsLocatie)} first. The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. When the passed CIR_ZittingsLocatie Object does not have an ID, a new entity will be created, otherwise the entity indicated by the ID will be updated. These
     * actions can have two possible outcomes as well:<br/>
     * <ol>
     * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param poCIR_ZittingsLocatie 		the CIR_ZittingsLocatie to save.
     * @return 				                a Result Object, containing the result of the save action.
     */
    @Transactional(rollbackFor=Exception.class)
    public Result saveCIR_ZittingsLocatie(CirZittingslocatie poCIR_ZittingsLocatie) {
        LOGGER.debug("Method saveCIR_ZittingsLocatie.");
        Result loResult = new Result();

        try {
            /** Before saving the CIR_ZittingsLocatie, it must be validated */
            loResult = new Result(validateCIR_ZittingsLocatie(poCIR_ZittingsLocatie));

            if (loResult.isSuccessful()) {

            	LOGGER.debug("Method saveCIR_ZittingsLocatie.");
            	poCIR_ZittingsLocatie = cirZittingslocatieRepository.save(poCIR_ZittingsLocatie);
                loResult.setResultObject(poCIR_ZittingsLocatie);
            }
        } catch(Exception loEx) {
            LOGGER.error("Method saveCIR_ZittingsLocatie. Saving the CIR_ZittingsLocatie failed.", loEx);
            loResult.setResult(false);
        }
        return loResult;
    }

    /**
     * Checks the validity of a CIR_ZittingsLocatie Object.<br/>
     * <p/>
     * A CIR_ZittingsLocatie Object must apply to the following rules:<br/>
     * <ol>
     * <li> The field Straat must contain a value.</li>
     * <li> The field Plaats must contain a value.</li>
     * <li> The field Huisnummer must contain a value.</li>
     * <li> The combination Straat, Plaats and Huisnummer must be unique.</li>
     * </ol>
     *
     * @param poCIR_ZittingsLocatie 	the CIR_ZittingsLocatie that must be validated.
     * @return 			                a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    public ValidationObject validateCIR_ZittingsLocatie(CirZittingslocatie poCIR_ZittingsLocatie) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if (poCIR_ZittingsLocatie != null) {

                // 1. The field Straat must contain a value.
                if (StringUtil.isEmptyOrNull(poCIR_ZittingsLocatie.getStraat())) {
                    loValidation.addMessage("Straat moet gevuld zijn.", MessageType.ERROR);
                    LOGGER.error("Method validateCIR_ZittingsLocatie. Straat not set.");
                }

                // 2. The field Plaats must contain a value.
                if (StringUtil.isEmptyOrNull(poCIR_ZittingsLocatie.getPlaats())) {
                    loValidation.addMessage("Plaats moet gevuld zijn.", MessageType.ERROR);
                    LOGGER.error("Method validateCIR_ZittingsLocatie. Plaats not set.");
                }

                // 3. The field HuisNummer must contain a value.
                if (StringUtil.isEmptyOrNull(poCIR_ZittingsLocatie.getHuisNummer())) {
                    loValidation.addMessage("Huisnummer moet gevuld zijn.", MessageType.ERROR);
                    LOGGER.error("Method validateCIR_ZittingsLocatie. HuisNummer not set.");
                }

                // 4. The combination of filled Straat, Plaats and Huisnummer must be unique.
                if (loValidation.isValid()) {
                    CirZittingslocatie loCIR_ZittingsLocatie = getCIR_ZittingsLocatieByStraatPlaatsAndHuisNummer(poCIR_ZittingsLocatie.getStraat(), poCIR_ZittingsLocatie.getPlaats(), poCIR_ZittingsLocatie.getHuisNummer());

                    if (loCIR_ZittingsLocatie != null) {
                        if (poCIR_ZittingsLocatie.getId() == null || loCIR_ZittingsLocatie.getId().compareTo(poCIR_ZittingsLocatie.getId()) != 0) {
                            loValidation.addMessage("Combinatie van Straat, Plaats en Huisnummer moet uniek zijn.", MessageType.ERROR);
                            LOGGER.error("Method validateCIR_ZittingsLocatie. Combination of Straat, Plaats and HuisNummer not unique.");
                        }
                    }
                }

            } else {
                loValidation.addMessage("Geen CIR_ZittingsLocatie ontvangen.", MessageType.INVALID);
                LOGGER.error("Method validateCIR_ZittingsLocatie. No CIR_ZittingsLocatie received.");
            }
            LOGGER.debug("Method validateCIR_ZittingsLocatie. Number of messages = " + loValidation.getValidationMessages().size());

        }  catch (Exception loEx) {
            loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
            LOGGER.error("Method validateCIR_ZittingsLocatie. Validation failed.");
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
//        LOGGER.info("Method createPageHandler");
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
//			if (poObject instanceof CIR_Publicatie) {
//				CIR_Publicatie loCIR_Publicatie = (CIR_Publicatie) poObject;
//
//                // 1. Compose the join
//                if (loCIR_Publicatie.getCIR_PublicatieSoort() != null) loJoin.append(" JOIN a.CIR_PublicatieSoort at");
//
//                // 2. Compose the where
//                lstrKoppel = " WHERE";
//
//                if (loCIR_Publicatie.getCIR_PublicatieSoort() != null) {
//                    loQueryParams.add(loCIR_Publicatie.getCIR_PublicatieSoort().getCode());
//
//                    loWhere.append(lstrKoppel).append(" at.code = ?").append(loQueryParams.size());
//                }
//
//				loSelectQuery.append("SELECT a FROM CIR_Publicatie a");
//                loSelectQuery.append(loJoin);
//				loSelectQuery.append(loWhere);
//				loSelectQuery.append(" ORDER BY a.tijd");
//				lstrSelectQuery = loSelectQuery.toString();
//
//				loCountQuery.append("SELECT COUNT(a) FROM CIR_Publicatie a");
//                loCountQuery.append(loJoin);
//				loCountQuery.append(loWhere);
//				lstrCountQuery = loCountQuery.toString();
//
//                LOGGER.info("Method createPageHandler. SelectQuery = {}", lstrSelectQuery);
//
//				return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
//
//			} else if (poObject instanceof CIR_PublicatieSoort) {
//				CIR_PublicatieSoort loCIR_PublicatieSoort = (CIR_PublicatieSoort) poObject;
//
//				// Compose the where
//				lstrKoppel = " WHERE";
//				if(loCIR_PublicatieSoort.getOmschrijving() != null) {
//					loQueryParams.add(loCIR_PublicatieSoort.getOmschrijving());
//
//					// Als er een %-teken in de tekst staat, dan moet er gezocht worden m.b.v. LIKE
//					if(!loCIR_PublicatieSoort.getOmschrijving().contains("%")) {
//						loWhere.append(lstrKoppel).append(" at.omschrijving = ?").append(loQueryParams.size());
//					} else loWhere.append(lstrKoppel).append(" at.omschrijving LIKE ?").append(loQueryParams.size());
//				}
//
//				loSelectQuery.append("SELECT at FROM CIR_PublicatieSoort at");
//				loSelectQuery.append(loWhere);
//				loSelectQuery.append(" ORDER BY at.omschrijving");
//				lstrSelectQuery = loSelectQuery.toString();
//
//				loCountQuery.append("SELECT COUNT(at) FROM CIR_PublicatieSoort at");
//				loCountQuery.append(loWhere);
//				lstrCountQuery = loCountQuery.toString();
//
//				return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
//			}
//
//		} catch(Exception loEx) {
//			LOGGER.error("Method createPageHandler. Method failed.", loEx);
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
     * Checks the validity of an CIR_PublicatieSoort Entity.<br/>
     * <p/>
     * An CIR_PublicatieSoort Object must apply to the following rules:<br/>
     * <ol>
	 * <li> The CIR_PublicatieSoort must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
	 * @param poCIR_PublicatieSoort 	the CIR_PublicatieSoort Entity to validate.
     * @return 				a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
	private ValidationObject validateCIR_PublicatieSoort(CirPublicatiesoort poCIR_PublicatieSoort) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(poCIR_PublicatieSoort != null) {

                // 1. The CIR_PublicatieSoort must already exist.
                if (poCIR_PublicatieSoort.getCode() == null || poCIR_PublicatieSoort.getCode().trim().equals("")) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", MessageType.INVALID);
                }

				// 2. The field omschrijving must contain a value
				if(poCIR_PublicatieSoort.getOmschrijving() == null || poCIR_PublicatieSoort.getOmschrijving().trim().equals("")) {
					loValidation.addMessage(poCIR_PublicatieSoort.getOmschrijving(), "Omschrijving is een verplicht veld.", MessageType.INVALID);
				}

			} else {
				loValidation.addMessage("Geen CIR_PublicatieSoort ontvangen.", MessageType.INVALID);
				LOGGER.error("Method validateCIR_PublicatieSoort. No CIR_PublicatieSoort received.");
			}
			LOGGER.debug("Method validateCIR_PublicatieSoort. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_PublicatieSoort. Validation failed.");
		}
		return loValidation;
	}
}