package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import nl.devoorkant.sbdr.cir.data.model.CirAdrestype;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;

import java.util.List;

/**
 * Interface exposing functionality for CIR_Adressen.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2013).<br/>
 */

public interface CirAdresDataService {

    /**
     * Returns a new, initialised CIR_Adres Object.<br/>
     *
     * @return  a new CIR_Adres Object
     */
    CirAdres getNewCIR_Adres();

    /**
     * Returns a new, initialised CIR_Adres Object, based on the passed CIR_AdresTypeCode.<br/>
     *
     * @param pstrCIR_AdresTypeCode	    a String representing a CIR_AdresType
     * @return 					        a new CIR_Adres Object
     */
    CirAdres getNewCIR_Adres(String pstrCIR_AdresTypeCode);

	/**
     * Returns the CIR_Adres Object by primary key.<br/>
     *
     * @param poID		an Integer representing the CIR_Adres to retrieve
     * @return          the CIR_Adres Object
     */
    CirAdres getCIR_Adres(Integer poID);

    /**
     * Saves the passed CIR_Adres Object.<br/>
     *
     * @param poCIR_Adres 	the CIR_Adres to save.
     * @return 				a Result Object, containing the result of the save action.
     */
    Result saveCIR_Adres(CirAdres poCIR_Adres);

    /**
     * Removes the CIR_Adres Object, indicated by the passed ID.<br/>
     * <p/>
     * @param poID 		an Integer indicating the CIR_Adres to remove.
     * @return 			true when the CIR_Adres is removed, otherwise false.
     */
    boolean removeCIR_Adres(Integer poID);

    /**
     * Checks the validity of a CIR_Adres Object.<br/>
     *
     * @param poCIR_Adres 	the CIR_Adres that must be validated.
     * @return 			    a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    ValidationObject validateCIR_Adres(CirAdres poCIR_Adres);

    CirAdrestype getCIR_AdresType(String pstrCode);
	List<CirAdrestype> getActiveCIR_AdresTypes();
	Result saveCIR_AdresType(CirAdrestype poCIR_AdresType);

    /**
	 * Retrieves a PageHandler Object, positioned to the first page, based on the passed search criteria.<br/>
	 *
	 * @param poObject   	an Object, presummably a Adres Object, containing the search criteria.
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