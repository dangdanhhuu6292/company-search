package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirExceptie;
import nl.devoorkant.sbdr.cir.data.model.CirExceptietype;
import nl.devoorkant.validation.Result;

import java.util.Collection;

/**
 * Interface exposing functionality for CIR_Excepties.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel (jmeekel@devoorkant.nl)
 * @version 1.0 (2-12-2013).<br/>
 */

public interface CirExceptieDataService {

    /**
     * Returns a new, initialised CIR_Exceptie Object.<br/>
     *
     * @return  a new CIR_Exceptie Object
     */
    CirExceptie getNewCIR_Exceptie();

    /**
     * Returns a new, initialised CIR_Exceptie Object, based on the passed CIR_ExceptieTypeCode.<br/>
     *
     * @param pstrCIR_ExceptieTypeCode	a String representing a CIR_ExceptieType
     * @return 					        a new CIR_Exceptie Object
     */
    CirExceptie getNewCIR_Exceptie(String pstrCIR_ExceptieTypeCode);

	/**
     * Returns the CIR_Exceptie Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_Exceptie to retrieve
     * @return          the CIR_Exceptie Object
     */
    CirExceptie getCIR_Exceptie(Integer poID);

	Result saveCIR_Exceptie(CirExceptie poCIR_Exceptie);

	CirExceptietype getCIR_ExceptieType(String pstrCode);
	Collection<CirExceptietype> getActiveCIR_ExceptieTypes();
	Result saveCIR_ExceptieType(CirExceptietype poCIR_ExceptieType);

    /**
	 * Retrieves a PageHandler Object, positioned to the first page, based on the passed search criteria.<br/>
	 *
	 * @param poObject   	an Object, presummably a Exceptie Object, containing the search criteria.
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