package nl.devoorkant.sbdr.data.service.cir;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirHandelsnaam;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;

/**
 * Interface exposing functionality for CIR_HandelsNamen.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel (jmeekel@devoorkant.nl)
 * @version 1.0 (9-12-2013).<br/>
 */

public interface CirHandelsNaamDataService {

    /**
     * Returns a new, initialised CIR_HandelsNaam Object.<br/>
     *
     * @return  a new CIR_HandelsNaam Object
     */
    CirHandelsnaam getNewCIR_HandelsNaam();

	/**
     * Returns the CIR_HandelsNaam Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_HandelsNaam to retrieve
     * @return          the CIR_HandelsNaam Object
     */
    CirHandelsnaam getCIR_HandelsNaam(Integer poID);

	Result saveCIR_HandelsNaam(CirHandelsnaam poCIR_HandelsNaam)  throws DataServiceException;

    /**
     * Checks the validity of a CIR_HandelsNaam Object.<br/>
     *
     * @param poCIR_HandelsNaam 	the CIR_HandelsNaam that must be validated.
     * @return 			        a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    ValidationObject validateCIR_HandelsNaam(CirHandelsnaam poCIR_HandelsNaam);

	Result removeCIR_Handelsnaam(CirHandelsnaam poCIR_Handelsnaam);

	List<CirHandelsnaam> getCIR_HandelsNaamByInsolventieId(
			Integer cirInsolventieId);

    /**
	 * Retrieves a PageHandler Object, positioned to the first page, based on the passed search criteria.<br/>
	 *
	 * @param poObject   	an Object, presummably a HandelsNaam Object, containing the search criteria.
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