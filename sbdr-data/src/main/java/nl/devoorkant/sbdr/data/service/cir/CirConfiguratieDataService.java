package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.Configuratie;
import nl.devoorkant.validation.Result;

import java.util.List;

/**
 * Interface exposing functionality for Configuraties.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel (jmeekel@devoorkant.nl)
 * @version 1.0 (21-11-2013).<br/>
 */

public interface CirConfiguratieDataService {

    /**
     * Returns the Configuratie Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the Configuratie to retrieve
     * @return 			the Configuratie Entity
     */
    Configuratie getConfiguratie(String pstrCode);

    /**
     * Retrieve the Configuratie Waarde for the passed Code (primary key).<br/>
     *
     * @param pstrCode 	a String representing the Configuratie.
     * @return 			A String containing the Waarde of the Configuratie, or NULL when the Configuratie could not be retrieved.
     */
    String getConfiguratieValue(String pstrCode);

    /**
     * Returns all the displayable Configuraties.<br/>
     *
     * @return 		a Collection of displayable Configuraties.
     */
    List<Configuratie> getDisplayableConfiguraties();

    /**
     * Saves the passed Configuratie Entity.<br/>
     *
     * @param poConfiguratie 	the Configuratie Entity to save.
     * @return 			        a Result Object, containing the result of the save action.
     */
    Result saveConfiguratie(Configuratie poConfiguratie);

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