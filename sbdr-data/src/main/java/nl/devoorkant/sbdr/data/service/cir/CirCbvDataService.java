package nl.devoorkant.sbdr.data.service.cir;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirCbv;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;

/**
 * Interface exposing functionality for CIR_CBVs.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel (jmeekel@devoorkant.nl)
 * @version 1.0 (9-12-2013).<br/>
 */

public interface CirCbvDataService {

    /**
     * Returns a new, initialised CIR_CBV Object.<br/>
     *
     * @return  a new CIR_CBV Object
     */
    CirCbv getNewCIR_CBV();

    /**
     * Returns a new, initialised CIR_CBV Object, based on the passed CIR_CBVTypeCode.<br/>
     *
     * @param pstrCIR_CBVTypeCode	a String representing a CIR_CBVType
     * @return 					        a new CIR_CBV Object
     */
    CirCbv getNewCIR_CBV(String pstrCIR_CBVTypeCode);

    /**
     * Returns the CIR_CBV Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_CBV to retrieve
     * @return          the CIR_CBV Object
     */
    CirCbv getCIR_CBV(Integer poID);

	Result saveCIR_CBV(CirCbv poCIR_CBV);

    /**
     * Checks the validity of a CIR_CBV Object.<br/>
     *
     * @param poCIR_CBV 	the CIR_CBV that must be validated.
     * @return 			        a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    ValidationObject validateCIR_CBV(CirCbv poCIR_CBV);

	Result removeCIR_CBV(CirCbv poCIR_CBV);

	List<CirCbv> getCIR_CbvByInsolventieId(Integer cirInsolventieId);

    /**
	 * Retrieves a PageHandler Object, positioned to the first page, based on the passed search criteria.<br/>
	 *
	 * @param poObject   	an Object, presummably a CBV Object, containing the search criteria.
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