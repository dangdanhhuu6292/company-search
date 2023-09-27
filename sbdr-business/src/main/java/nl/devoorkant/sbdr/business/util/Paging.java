package nl.devoorkant.sbdr.business.util;

import static nl.devoorkant.sbdr.business.util.PageHandler.PageAction;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Abstract class containing the basic paging functionality.
 * <p/>
 * Aut - The Authorisation module
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 * @since           0.0.1
 */

public abstract class Paging {

	private static final Logger ioLogger = LoggerFactory.getLogger(Paging.class.getName());

	/**
	 * Retrieves a PageHandler Object, positioned to the first page, based on the passed search criteria.<br/>
	 *
	 * @param poObject   	an Object, containing the search criteria.
	 * @param pnPageSize 	an int containing the Page size.
	 * @return 				a PageHandler Object, containing the first page.
	 */
	public PageHandler getFirstPage(Object poObject, int pnPageSize) {
		ioLogger.debug("Method getFirstPage.");

		PageHandler loPageHandler = createPageHandler(poObject, pnPageSize);

		if(loPageHandler != null) return getPage(loPageHandler, PageAction.FIRST_PAGE);
		else return null;
	}

	/**
	 * Retrieves a PageHandler Object, positioned to the refreshed current page.<br/>
	 *
	 * @param poPageHandler 	an initialized PageHandler Object.
	 * @return 					a PageHandler Object, containing the refreshed current page.
	 */
	public PageHandler getCurrentPage(PageHandler poPageHandler) {
		ioLogger.debug("Method getCurrentPage.");

		if(poPageHandler != null) return getPage(poPageHandler, PageAction.CURRENT_PAGE);
		else return null;
	}

	/**
	 * Retrieves a PageHandler Object, positioned to the previous page.<br/>
	 *
	 * @param poPageHandler 	an initialized PageHandler Object.
	 * @return 					a PageHandler Object, containing the previous page.
	 */
	public PageHandler getPreviousPage(PageHandler poPageHandler) {
		ioLogger.debug("Method getPreviousPage.");

		if(poPageHandler != null && poPageHandler.hasPrevious()) return getPage(poPageHandler, PageAction.PREVIOUS_PAGE);
		else return null;
	}

	/**
	 * Retrieves a PageHandler Object, positioned to the next page.<br/>
	 *
	 * @param poPageHandler 	an initialized PageHandler Object.
	 * @return 					a PageHandler Object, containing the next page.
	 */
	public PageHandler getNextPage(PageHandler poPageHandler) {
		ioLogger.debug("Method getNextPage.");

		if(poPageHandler != null && poPageHandler.hasNextPage()) return getPage(poPageHandler, PageAction.NEXT_PAGE);
		else return null;
	}

    /**
     * Retrieves a PageHandler Object, positioned to the specified page.<br/>
     *
     * @param poPageHandler 	an initialized PageHandler Object.
     * @return 					a PageHandler Object, containing the next page.
     */
    public PageHandler getSpecifiedPage(PageHandler poPageHandler, int pnPageNr) {
        ioLogger.debug("Method getSpecifiedPage.");

        if(poPageHandler != null && poPageHandler.hasNextPage()) return getPage(poPageHandler, pnPageNr);
        else return null;
    }

    /**
     * Creates a new PageHandler Object.<br/>
     *
     * @param poObject   	an Object, containing the search criteria.
     * @param pnPageSize 	an int containing the Page size.
     * @return 				a PageHandler Object.
     */
	protected abstract PageHandler createPageHandler(Object poObject, int pnPageSize);

    /**
	 * Retrieves a PageHandler Object, positioned to the requested page.<br/>
     *
     * @param poPageHandler 	an initialized PageHandler Object.
     * @param poPageAction  	an Enumeration, indicating the page to create.
     * @return 					a PageHandler Object, containing the requested page.
     */
	protected abstract PageHandler getPage(PageHandler poPageHandler, PageAction poPageAction);

    /**
     * Retrieves a PageHandler Object, positioned to the requested page.<br/>
     *
     * @param poPageHandler 	an initialized PageHandler Object.
     * @param pnPageNr 	        an int containing the Page to retrieve.
     * @return 					a PageHandler Object, containing the requested page.
     */
    protected abstract PageHandler getPage(PageHandler poPageHandler, int pnPageNr);
}
