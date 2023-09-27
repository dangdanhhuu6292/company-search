package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirAansturing;
import nl.devoorkant.validation.Result;

/**
 * Interface exposing functionality for CIR_Aansturingen.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel (jmeekel@devoorkant.nl)
 * @version 1.0 (27-11-2013).<br/>
 */

public interface CirAansturingDataService {

    /**
     * Returns the CIR_Aansturing Object by primary key.<br/>
     *
     * @return 					the CIR_Aansturing Entity
     */
    CirAansturing getCIR_Aansturing();

    /**
     * Saves the passed CIR_Aansturing Entity.<br/>
     *
     * @param poCIR_Aansturing 	    the CIR_Aansturing to save.
     * @return 			            a Result Object, containing the result of the save action.
     */
    Result saveCIR_Aansturing(CirAansturing poCIR_Aansturing);

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