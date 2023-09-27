package nl.devoorkant.sbdr.data.service.cir;

import java.util.Date;
import java.util.List;
import java.util.Set;

import nl.devoorkant.sbdr.cir.data.model.CirAansturing;
import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import nl.devoorkant.sbdr.cir.data.model.CirCbv;
import nl.devoorkant.sbdr.cir.data.model.CirExceptie;
import nl.devoorkant.sbdr.cir.data.model.CirHandelsnaam;
import nl.devoorkant.sbdr.cir.data.model.CirInsolventie;
import nl.devoorkant.sbdr.cir.data.model.CirPersoon;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatie;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatiekenmerk;
import nl.devoorkant.sbdr.cir.data.model.CirZittingslocatie;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

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

@Service("insolventieDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class InsolventieDataServiceImpl implements InsolventieDataService{
    private static final Logger LOGGER = LoggerFactory.getLogger(InsolventieDataServiceImpl.class);
    
	@Autowired
	CirAansturingDataService cirAansturingDataService;

	@Autowired
	CirPublicatieKenmerkDataService cirPublicatieKenmerkDataService;
	
	@Autowired
	CirInsolventieDataService cirInsolventieDataService;
	
	@Autowired
	CirAdresDataService cirAdresDataService;
	
	@Autowired
	CirCbvDataService cirCbvDataService;
	
	@Autowired
	CirHandelsNaamDataService cirHandelsNaamDataService;
    
	@Autowired
	CirPublicatieDataService cirPublicatieDataService;
	
	@Autowired
	CirExceptieDataService cirExceptieDataService;
	
    
    /**
     * Sets the Date and Time of the Last synchronization with CIR to the passed Timestamp.<br/>
     *
     * @return      true when successful, else false.
     */
    @Override
    public boolean setTijdLaatsteSynchronizatie(Date poDatumLaatsteSynchronizatie) {
        LOGGER.info("Method setTijdLaatsteSynchronizatie.");
        Result loResult = null;

        CirAansturing cirAansturing = cirAansturingDataService.getCIR_Aansturing();

        if (poDatumLaatsteSynchronizatie != null) {
            cirAansturing.setTijdLaatsteSynchronizatie(poDatumLaatsteSynchronizatie);
            loResult = cirAansturingDataService.saveCIR_Aansturing(cirAansturing);
            //if (loResult.isSuccessful()) ioCIR_Aansturing = (CIR_Aansturing) loResult.getResultObject();
        }

        return loResult != null && loResult.isSuccessful();
    }  
    
    
    /**
     * Returns a CIR_Insolventie Entity based on the passed NummerInsolventie.<br/>
     *
     * @param pstrNummerInsolventie	    a String containing the NummerInsolventie
     * @return 			                the CIR_Insolventie Entity
     */
    @Override
    public CirInsolventie getCIR_InsolventieByNummerInsolventie(String pstrNummerInsolventie){
        return cirInsolventieDataService.getCIR_InsolventieByNummerInsolventie(pstrNummerInsolventie);
    }

    /**
     * Removes all Insolventies in broader sense, consisting of a CIR_Insolventie and the related or connected CIR_Objects, based on the passed object.<br/>
     *
     * @return      a Result Object, containing the result of the remove action.
     */
    @Override
    public Result removeAllInsolventies() {
        return cirInsolventieDataService.removeAllCIR_Insolventies();
    }

    /**
     * Removes an Insolventie in broader sense, consisting of a CIR_Insolventie and the related or connected CIR_Objects, based on the passed object.<br/>
     *
     * @param poCIR_Insolventie	                the CIR_Insolventie Object to save
     * @return 			                        a Result Object, containing the result of the remove action.
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Result removeInsolventie(CirInsolventie poCIR_Insolventie) {
        LOGGER.info("Method removeInsolventie.");
        Result loResult = new Result();

        if (poCIR_Insolventie != null) {
			List<CirPublicatie> cirPublicaties = cirPublicatieDataService.getCIR_PublicatieByInsolventieId(poCIR_Insolventie.getId());
        	List<CirCbv> cirCbvs = cirCbvDataService.getCIR_CbvByInsolventieId(poCIR_Insolventie.getId());
        	List<CirHandelsnaam> cirHandelsnaams = cirHandelsNaamDataService.getCIR_HandelsNaamByInsolventieId(poCIR_Insolventie.getId());
        	
            // Remove the Insolventie and its underlying CIR_Publicaties, CIR_HandelsNamen, CIR_Adressen and CIR_CBVs
            if (loResult.isSuccessful()) {
            	if (cirPublicaties != null) {
            		for (CirPublicatie cirPublicatie : cirPublicaties) {
            			loResult = cirPublicatieDataService.removeCIR_Publicatie(cirPublicatie);
            		}
            	}
            	if (cirCbvs != null && cirCbvs.size() > 0) {
            		for (CirCbv cirCbv : cirCbvs)
            			loResult = cirCbvDataService.removeCIR_CBV(cirCbv);
            	}
            	if (loResult.isSuccessful() && cirHandelsnaams != null && cirHandelsnaams.size() > 0) {
            		for (CirHandelsnaam cirHandelsnaam : cirHandelsnaams) 
            			loResult = cirHandelsNaamDataService.removeCIR_Handelsnaam(cirHandelsnaam);
            	}

            	if (loResult.isSuccessful())
            		loResult = cirInsolventieDataService.removeCIR_Insolventie(poCIR_Insolventie);
            }

        } else {
            LOGGER.error("Method removeInsolventie. No insolvancy to remove received!");
            loResult.getValidationObject().addMessage("Geen te verwijderen insolventie ontvangen", ValidationConstants.MessageType.INVALID);
        }

        return loResult;
    }

    /**
     * Creates an Insolventie in broader sense, consisting of a CIR_Insolventie and the related or connected CIR_Objects, based on the passed objects.<br/>
     *
     * @param poCIR_Insolventie	        the CIR_Insolventie Object to save
     * @param poCIR_Publicaties	        the Set of CIR_Publicatie Object to save
     * @param poCIR_HandelsNamen	    the Set of CIR_HandelsNaam Objects to save
     * @param poCIR_CBVs	            the Set of CIR_CBV Objects to save
     * @return 			                a Result Object, containing the result of the create action.
     */
    @Override
    public Result createInsolventie(CirInsolventie poCIR_Insolventie, Set<CirPublicatie> poCIR_Publicaties, Set<CirHandelsnaam> poCIR_HandelsNamen, Set<CirCbv> poCIR_CBVs) throws DataServiceException{
        LOGGER.info("Method createInsolventie.");
        Result loResult = new Result();

        try {

            if (poCIR_Insolventie != null) {
                LOGGER.debug("Method createInsolventie. Insolventie is {}", poCIR_Insolventie.toString());
                if (poCIR_HandelsNamen != null) {
                	for (CirHandelsnaam cirHandelsnaam : poCIR_HandelsNamen) {
                		cirHandelsNaamDataService.saveCIR_HandelsNaam(cirHandelsnaam);
                	}
                }
                poCIR_Insolventie.setCirHandelsnaams(poCIR_HandelsNamen);
                if (poCIR_CBVs != null) {
                	for (CirCbv cirCbv : poCIR_CBVs) {
                		cirCbvDataService.saveCIR_CBV(cirCbv);
                	}
                }                
                poCIR_Insolventie.setCirCbvs(poCIR_CBVs);
                
                loResult.addResult(cirInsolventieDataService.saveCIR_Insolventie(poCIR_Insolventie));

                if (loResult.isSuccessful()) {
                    poCIR_Insolventie = (CirInsolventie)loResult.getResultObject();
                    for (CirPublicatie loCIR_Publicatie : poCIR_Publicaties) {
                        loCIR_Publicatie.setCirInsolventie(poCIR_Insolventie);
                        loResult.addResult(cirPublicatieDataService.saveCIR_Publicatie(loCIR_Publicatie));
                    }
                }

                if (!loResult.isSuccessful()) throw new DataServiceException("Problem saving CIR_Insolventie");

            } else {
                loResult.getValidationObject().addMessage("Niet genoeg gegevens ontvangen om een Insolventie te creëren.", ValidationConstants.MessageType.ERROR);
            }

        }  catch (Exception loEx) {
            loResult.getValidationObject().addMessage("Creëren van de Insolventie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Method createInsolventie. Creation of the Insolvency failed.", loEx);
        }

        if (!loResult.isSuccessful()) {
            LOGGER.debug("Method createInsolventie. Rollback.");            
        }

        return loResult;
    }


    /**
     * Returns a new CIR_Adres Object.<br/>
     *
     * @return 					        a new CIR_Adres Object
     */
    @Override
    public CirAdres getNewCIR_Adres() {
        return cirAdresDataService.getNewCIR_Adres();
    }

    /**
     * Returns a new, initialised CIR_Adres Object, based on the passed CIR_AdresTypeCode.<br/>
     *
     * @param pstrCIR_AdresTypeCode	    a String representing a CIR_AdresType
     * @return 					        a new CIR_Adres Object
     */
    @Override
    public CirAdres getNewCIR_Adres(String pstrCIR_AdresTypeCode) {
        return cirAdresDataService.getNewCIR_Adres(pstrCIR_AdresTypeCode);
    }

    /**
     * Returns a new, initialised CIR_CBV Object, based on the passed CIR_CBVTypeCode.<br/>
     *
     * @param pstrCIR_CBVTypeCode	    a String representing a CIR_CBVType
     * @return 					        a new CIR_CBV Object
     */
    @Override
    public CirCbv getNewCIR_CBV(String pstrCIR_CBVTypeCode) {
        return cirCbvDataService.getNewCIR_CBV(pstrCIR_CBVTypeCode);
    }

    /**
     * Returns a new CIR_HandelsNaam Object.<br/>
     *
     * @return  a new CIR_HandelsNaam Object
     */
    @Override
    public CirHandelsnaam getNewCIR_HandelsNaam() {
        return cirHandelsNaamDataService.getNewCIR_HandelsNaam();
    }

    /**
     * Returns the CIR_Insolventie Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_Insolventie to retrieve
     * @return          the CIR_Insolventie Object
     */
    @Override
    public CirPersoon getCIR_Persoon(Integer poID) {
        return cirInsolventieDataService.getCIR_Persoon(poID);
    }

    /**
     * Returns a Collection of CIR_PublicatieKenmerken Entity based on the passed PublicatieKenmerk.<br/>
     *
     * @param pstrPublicatieKenmerk	    a String containing the PublicatieKenmerk
     * @return 			                the CIR_PublicatieKenmerk Entity
     */
    @Override
    public List<CirPublicatiekenmerk> getCIR_PublicatieKenmerkenByPublicatieKenmerk(String pstrPublicatieKenmerk) {
        return cirPublicatieKenmerkDataService.getCIR_PublicatieKenmerkenByPublicatieKenmerk(pstrPublicatieKenmerk);
    }

    /**
     * Creates a CIR_Publicatie Entity, based on the passed String.<br/>
     *
     * @param pstrCodePublicatieSoort   a String containing the identifying code of the CIR_PublicatieSoort.
     * @return 			                a new CIR_PublicatieSoort.
     */
    @Override
    public CirPublicatie getNewCIR_Publicatie(String pstrCodePublicatieSoort) {
        return cirPublicatieDataService.getNewCIR_Publicatie(pstrCodePublicatieSoort);
    }

    /**
     * Returns a List of unprocessed CIR_PublicatieKenmerken .<br/>
     *
     * @return 		a Collection of unprocessed CIR_PublicatieKenmerken.
     */
    @Override
    public List<CirPublicatiekenmerk> getUnprocessedCIR_PublicatieKenmerken() {
        return cirPublicatieKenmerkDataService.getUnprocessedCIR_PublicatieKenmerken();
    }

    /**
     * Saves the passed CIR_PublicatieKenmerk Entity.<br/>
     *
     * @param poCIR_PublicatieKenmerk 	    the CIR_PublicatieKenmerk to save.
     * @return 			    a Result Object, containing the result of the save action.
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Result saveCIR_PublicatieKenmerk(CirPublicatiekenmerk poCIR_PublicatieKenmerk) {
        return cirPublicatieKenmerkDataService.saveCIR_PublicatieKenmerk(poCIR_PublicatieKenmerk);
    }

    /**
     * Creates a CIR_PublicatieKenmerk Entity, based on the passed String.<br/>
     *
     * @param pstrCodeExceptieType 	    a String containing the identifying code of the CIR_ExceptieType.
     * @return 			                a Result Object, containing the result of the save action.
     */
    @Override
    public Result createCIR_Exceptie(String pstrCodeExceptieType) {
        return (createCIR_Exceptie(pstrCodeExceptieType, null));
    }

    /**
     * Creates a CIR_PublicatieKenmerk Entity, based on (one of) the passed String(s).<br/>
     *
     * @param pstrCodeExceptieType 	    a String containing the identifying code of the CIR_ExceptieType.
     * @param pstrOmschrijving 	        a String containing the description of the exception to create.
     * @return 			                a Result Object, containing the result of the save action.
     */
    @Override
    public Result createCIR_Exceptie(String pstrCodeExceptieType, String pstrOmschrijving) {
        Result loResult = new Result();

        if (StringUtil.isNotEmptyOrNull(pstrCodeExceptieType) || StringUtil.isEmptyOrNull(pstrOmschrijving)) {
            CirExceptie loCIR_Exceptie = null;

            if (StringUtil.isNotEmptyOrNull(pstrCodeExceptieType)) {
                loCIR_Exceptie = cirExceptieDataService.getNewCIR_Exceptie(pstrCodeExceptieType);
            } else {
                loCIR_Exceptie = cirExceptieDataService.getNewCIR_Exceptie();
            }

            loCIR_Exceptie.setOmschrijving(pstrOmschrijving);

            loResult = cirExceptieDataService.saveCIR_Exceptie(loCIR_Exceptie);
        } else {
            loResult.getValidationObject().addMessage("Niet genoeg gegevens ontvangen.", ValidationConstants.MessageType.ERROR);
        }

        return loResult;
    }

    /**
     * Creates a CIR_PublicatieKenmerk Entity, based on the passed String.<br/>
     *
     * @param pstrPublicatieKenmerk 	    a String containing the PublicatieKenmerk to create.
     * @return 			                    a Result Object, containing the result of the save action.
     */
    @Override
    public Result createCIR_PublicatieKenmerk(String pstrPublicatieKenmerk) {
        Result loResult = new Result();

        if (StringUtil.isNotEmptyOrNull(pstrPublicatieKenmerk)) {
            CirPublicatiekenmerk loCIR_PublicatieKenmerk = cirPublicatieKenmerkDataService.getNewCIR_PublicatieKenmerk();
            loCIR_PublicatieKenmerk.setPublicatieKenmerk(pstrPublicatieKenmerk);
            loResult = cirPublicatieKenmerkDataService.saveCIR_PublicatieKenmerk(loCIR_PublicatieKenmerk);
        } else {
            loResult.getValidationObject().addMessage("Publicatiekenmerk niet ontvangen.", ValidationConstants.MessageType.ERROR);
        }

        return loResult;
    }

    /**
     * Creates IR_PublicatieKenmerk Entities, based on the passed List of Strings.<br/>
     *
     * When processing of the List fails, the entire transaction is rolled back.
     *
     * @param poPublicatieKenmerken 	    a List of Strings containing the PublicatieKenmerken to create.
     * @return 			                    a Result Object, containing the result of the create action.
     */
    @Override
    public Result createCIR_PublicatieKenmerken(List<String> poPublicatieKenmerken) {
        Result loResult = new Result();

        for (String pstrPublicatieKenmerk : poPublicatieKenmerken) {
            loResult.addResult(createCIR_PublicatieKenmerk(pstrPublicatieKenmerk));
        }

        if (!loResult.isSuccessful()) {
            LOGGER.info("Method createCIR_PublicatieKenmerken. Rollback.");            
        }

        return loResult;
    }

    /**
     * Retrieves an existing or creates a new CIR_ZittingsLocatie Entity, based on the passed parameters.<br/>
     *
     * @param pstrStraat	    a String containing the Straat
     * @param pstrPlaats	    a String containing the Plaats
     * @param pstrHuisNummer	a String containing the Huisnummer
     * @return 			        a Result Object, containing the result of the create action.
     */
    @Override
    public CirZittingslocatie getCIR_ZittingsLocatie(String pstrStraat, String pstrPlaats, String pstrHuisNummer) {
        CirZittingslocatie loResult = null;

        if (StringUtil.isNotEmptyOrNull(pstrStraat) && StringUtil.isNotEmptyOrNull(pstrPlaats) && StringUtil.isNotEmptyOrNull(pstrHuisNummer)) {
            loResult = cirPublicatieDataService.getCIR_ZittingsLocatieByStraatPlaatsAndHuisNummer(pstrStraat, pstrPlaats, pstrHuisNummer);

            if (loResult == null) {
                loResult = cirPublicatieDataService.createNewCIR_ZittingsLocatie(pstrStraat, pstrPlaats, pstrHuisNummer);
            }
        }

        return loResult;
    }

//    /**
//     * Returns a new, Resultaat Object.<br/>
//     *
//     * @return 		        a new Resultaat Object
//     */
//    @Override
//    public Resultaat getNewResultaat() {
//        return ioResultaatSB.getNewResultaat();
//    }
//
//    /**
//     * Saves the passed Resultaat Entity.<br/>
//     *
//     * @param poResultaat   the Resultaat to save.
//     * @return 			    a Result Object, containing the result of the save action.
//     */
//   @Override
//    public Result saveResultaat(Resultaat poResultaat) {
//        return ioResultaatSB.saveResultaat(poResultaat);
//    }
//
//    /**
//     * Returns the ResultaatType Entity by primary key.<br/>
//     *
//     * @param pstrCode	a String representing the ResultaatType to retrieve
//     * @return 			the ResultaatType Entity
//     */
//    @Override
//    public ResultaatType getResultaatType(String pstrCode) {
//        return ioResultaatTypeSB.getResultaatType(pstrCode);
//    }    
    
}
