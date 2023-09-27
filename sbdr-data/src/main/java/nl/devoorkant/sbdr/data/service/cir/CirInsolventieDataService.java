package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.CirCbv;
import nl.devoorkant.sbdr.cir.data.model.CirHandelsnaam;
import nl.devoorkant.sbdr.cir.data.model.CirInsolventie;
import nl.devoorkant.sbdr.cir.data.model.CirPersoon;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface exposing functionality for CIR_Insolventies.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2013 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel (jmeekel@devoorkant.nl)
 * @version 1.0 (10-12-2013).<br/>
 */

public interface CirInsolventieDataService {

	/**
	 * Adds the passed CIR_CBVs to the passed CIR_Insolventie Object.<br/>
	 *
	 * @param poCIR_Insolventie the CIR_Insolventie Object to which the CIR_CBV objects are added.
	 * @param poCIR_CBVs        the CIR_CBV Objecten to add.
	 * @return a Result Object, containing the adjusted CIR_InsolventieObject.
	 */
	Result addCIR_CBVs(CirInsolventie poCIR_Insolventie, Set<CirCbv> poCIR_CBVs);

	/**
	 * Adds the passed CIR_HandelsNamen to the passed CIR_Insolventie Object.<br/>
	 *
	 * @param poCIR_Insolventie  the CIR_Insolventie Object to which the CIR_HandelsNaam objects are added.
	 * @param poCIR_HandelsNamen the CIR_HandelsNaam Objecten to add.
	 * @return a Result Object, containing the adjusted CIR_InsolventieObject.
	 */
	Result addCIR_HandelsNamen(CirInsolventie poCIR_Insolventie, Set<CirHandelsnaam> poCIR_HandelsNamen);

	/**
	 * @param pageable
	 * @return
	 */
	Page<Object[]> findAllFaillissementenLastWeek(Pageable pageable) throws DataServiceException;

	/**
	 * Returns the CIR_Insolventie Object by primary key.<br/>
	 *
	 * @param poID an Integer representing the CIR_Insolventie to retrieve
	 * @return the CIR_Insolventie Object
	 */
	CirInsolventie getCIR_Insolventie(Integer poID);

	/**
	 * Returns a CIR_Insolventie Entity based on the passed NummerInsolventie.<br/>
	 *
	 * @param pstrNummerInsolventie a String containing the NummerInsolventie
	 * @return the CIR_Insolventie Entity
	 */
	CirInsolventie getCIR_InsolventieByNummerInsolventie(String pstrNummerInsolventie);

	/**
	 * Returns the CIR_Insolventie Object by primary key.<br/>
	 *
	 * @param poID an Integer representing the CIR_Insolventie to retrieve
	 * @return the CIR_Insolventie Object
	 */
	CirPersoon getCIR_Persoon(Integer poID);

	long getCountOfFaillissementenDezeWeek() throws DataServiceException;

	long getCountOfFaillissementenDitJaar() throws DataServiceException;

	long getCountOfSurseancesDezeWeek() throws DataServiceException;

	long getCountOfSurseancesDitJaar() throws DataServiceException;

	/**
	 * Returns a new, initialised CIR_Insolventie Object.<br/>
	 *
	 * @return a new CIR_Insolventie Object
	 */
	CirInsolventie getNewCIR_Insolventie();

	/**
	 * Removes all Insolventies in broader sense, consisting of a CIR_Insolventie and the related or connected CIR_Objects, based on the passed object.<br/>
	 *
	 * @return a Result Object, containing the result of the remove action.
	 */
	Result removeAllCIR_Insolventies();

	/**
	 * Remove the Insolventie and its underlying CIR_HandelsNamen, CIR_Adressen and CIR_CBVs.<br/>
	 *
	 * @param poCIR_Insolventie the CIR_Insolventie to remove.
	 * @return a Result Object, containing the result of the remove action.
	 */
	Result removeCIR_Insolventie(CirInsolventie poCIR_Insolventie);

	/**
	 * Saves the passed CIR_Insolventie Object.<br/>
	 *
	 * @param poCIR_Insolventie the CIR_Insolventie to save.
	 * @return a Result Object, containing the result of the save action.
	 */
	Result saveCIR_Insolventie(CirInsolventie poCIR_Insolventie);

	Result saveCIR_Persoon(CirPersoon poCIR_Persoon);

	/**
	 * Checks the validity of a CIR_Insolventie Object.<br/>
	 *
	 * @param poCIR_Insolventie the CIR_Insolventie that must be validated.
	 * @return a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
	 */
	ValidationObject validateCIR_Insolventie(CirInsolventie poCIR_Insolventie);

	/**
	 * Retrieves a PageHandler Object, positioned to the first page, based on the passed search criteria.<br/>
	 *
	 * @param poObject    an Object, presummably a Insolventie Object, containing the search criteria.
	 * @param pnPageSize    an int containing the Page size.
	 * @return a PageHandler Object, containing the first page.
	 */
	//PageHandler getFirstPage(Object poObject, int pnPageSize);

	/**
	 * Retrieves a PageHandler Object, positioned to the refreshed current page.<br/>
	 *
	 * @param poPageHandler    an initialized PageHandler Object.
	 * @return a PageHandler Object, containing the refreshed current page.
	 */
	//PageHandler getCurrentPage(PageHandler poPageHandler);

	/**
	 * Retrieves a PageHandler Object, positioned to the previous page.<br/>
	 *
	 * @param poPageHandler    an initialized PageHandler Object.
	 * @return a PageHandler Object, containing the previous page.
	 */
	//PageHandler getPreviousPage(PageHandler poPageHandler);

	/**
	 * Retrieves a PageHandler Object, positioned to the next page.<br/>
	 *
	 * @param poPageHandler    an initialized PageHandler Object.
	 * @return a PageHandler Object, containing the next page.
	 */
	//PageHandler getNextPage(PageHandler poPageHandler);
}