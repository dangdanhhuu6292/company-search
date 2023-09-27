package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirPublicatie;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatiesoort;
import nl.devoorkant.sbdr.cir.data.model.CirZittingslocatie;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;

import java.util.Collection;
import java.util.List;

/**
 * Interface exposing functionality for CirPublicaties.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel (jmeekel@devoorkant.nl)
 * @version 1.0 (4-12-2013).<br/>
 */

public interface CirPublicatieDataService {

    /**
     * Returns a new, initialised CIR_Publicatie Object.<br/>
     *
     * @return  a new CIR_Publicatie Object
     */
    CirPublicatie getNewCIR_Publicatie();

    /**
     * Returns a new, initialised CIR_Publicatie Object, based on the passed CIR_PublicatieSoortCode.<br/>
     *
     * @param pstrCIR_PublicatieSoortCode	    a String representing a CIR_PublicatieSoort
     * @return 					                a new CIR_Publicatie Object
     */
    CirPublicatie getNewCIR_Publicatie(String pstrCIR_PublicatieSoortCode);

	/**
     * Returns the CIR_Publicatie Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_Publicatie to retrieve
     * @return          the CIR_Publicatie Object
     */
    CirPublicatie getCIR_Publicatie(Integer poID);

	Result saveCIR_Publicatie(CirPublicatie poCIR_Publicatie);

    /**
     * Checks the validity of a CIR_Publicatie Object.<br/>
     *
     * @param poCIR_Publicatie 	the CIR_Publicatie that must be validated.
     * @return 			        a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    ValidationObject validateCIR_Publicatie(CirPublicatie poCIR_Publicatie);

    CirPublicatiesoort getCIR_PublicatieSoort(String pstrCode);
	Collection<CirPublicatiesoort> getActiveCIR_PublicatieSoorten();
	Result saveCIR_PublicatieSoort(CirPublicatiesoort poCIR_PublicatieSoort);

    /**
     * Returns a new, initialised and stored CIR_ZittingsLocatie Object, based on the passed parameters.<br/>
     *
     * @param pstrStraat	    a String containing the Straat
     * @param pstrPlaats	    a String containing the Plaats
     * @param pstrHuisNummer	a String containing the Huisnummer
     * @return 					a new, initialised CIR_ZittingsLocatie Object
     */
    CirZittingslocatie createNewCIR_ZittingsLocatie(String pstrStraat, String pstrPlaats, String pstrHuisNummer);

    /**
     * Returns a CIR_ZittingsLocatie Entity based on the passed criteria.<br/>
     *
     * @param pstrStraat	    a String containing the Straat
     * @param pstrPlaats	    a String containing the Plaats
     * @param pstrHuisNummer	a String containing the Huisnummer
     * @return 			        the CIR_ZittingsLocatie Entity
     */
    CirZittingslocatie getCIR_ZittingsLocatieByStraatPlaatsAndHuisNummer(String pstrStraat, String pstrPlaats, String pstrHuisNummer);

	Result removeCIR_Publicatie(CirPublicatie poCIR_Publicatie);

	List<CirPublicatie> getCIR_PublicatieByInsolventieId(
			Integer cirInsolventieId);


    /**
	 * Retrieves a PageHandler Object, positioned to the first page, based on the passed search criteria.<br/>
	 *
	 * @param poObject   	an Object, presummably a Publicatie Object, containing the search criteria.
	 * @param pnPageSize 	an int containing the Page size.
	 * @return 				a PageHandler Object, containing the first page.
	 */
	//PageHandler getFirstPage(Object poObject, int pnPageSize);

	/**
	 * Retrieves a PageHandler Object, positioned to the refreshed current page.<br/>
	 *
	 * @param poPageHandler 	an initialized PageHandler Object.
	 * @return 					a PageHandler Object, containing the refreshed current page.
	 */
	//PageHandler getCurrentPage(PageHandler poPageHandler);

	/**
	 * Retrieves a PageHandler Object, positioned to the previous page.<br/>
	 *
	 * @param poPageHandler 	an initialized PageHandler Object.
	 * @return 					a PageHandler Object, containing the previous page.
	 */
	//PageHandler getPreviousPage(PageHandler poPageHandler);

	/**
	 * Retrieves a PageHandler Object, positioned to the next page.<br/>
	 *
	 * @param poPageHandler 	an initialized PageHandler Object.
	 * @return 					a PageHandler Object, containing the next page.
	 */
	//PageHandler getNextPage(PageHandler poPageHandler);
}