package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirPublicatiekenmerk;
import nl.devoorkant.validation.Result;

import java.util.List;

/**
 * Interface exposing functionality for CIR_PublicatieKenmerken.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel (jmeekel@devoorkant.nl)
 * @version 1.0 (28-11-2013).<br/>
 */

public interface CirPublicatieKenmerkDataService {

    /**
     * Returns a new, initialised CIR_PublicatieKenmerk Object.<br/>
     *
     * @return 		                    a new CIR_PublicatieKenmerk Object
     */
    CirPublicatiekenmerk getNewCIR_PublicatieKenmerk();

    /**
     * Returns the CIR_PublicatieKenmerk Object by primary key.<br/>
     *
     * @param poCIR_PublicatieKenmerk_ID 		an Integer representing the CIR_PublicatieKenmerk
     * @return 					the CIR_PublicatieKenmerk Entity
     */
    CirPublicatiekenmerk getCIR_PublicatieKenmerk(Integer poCIR_PublicatieKenmerk_ID);

    /**
     * Returns a Collection of CIR_PublicatieKenmerken Entity based on the passed PublicatieKenmerk.<br/>
     *
     * @param pstrPublicatieKenmerk	    a String containing the PublicatieKenmerk
     * @return 		                    a Collection of CIR_PublicatieKenmerken, containing the presented PublicatieKenmerk.
     */
    @SuppressWarnings({"unchecked"})
    List<CirPublicatiekenmerk> getCIR_PublicatieKenmerkenByPublicatieKenmerk(String pstrPublicatieKenmerk);

    /**
     * Returns a List of unprocessed CIR_PublicatieKenmerken .<br/>
     *
     * @return 		a List of unprocessed CIR_PublicatieKenmerken.
     */
    List<CirPublicatiekenmerk> getUnprocessedCIR_PublicatieKenmerken();

    /**
     * Saves the passed CIR_PublicatieKenmerk Entity.<br/>
     *
     * @param poCIR_PublicatieKenmerk 	    the CIR_PublicatieKenmerk to save.
     * @return 			    a Result Object, containing the result of the save action.
     */
    Result saveCIR_PublicatieKenmerk(CirPublicatiekenmerk poCIR_PublicatieKenmerk);

    /**
     * Retrieves a PageHandler Object, positioned to the first page, based on the passed search criteria.<br/>
     *
     * @param poObject   	an Object, presumably a Bevoegdheid Object, containing the search criteria.
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