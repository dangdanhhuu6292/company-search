package nl.devoorkant.sbdr.business.util;

import java.io.*;
import java.util.*;

/**
 * Utility class for storing multiple paging parameters and objects.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * This class keeps information about the selected page and keeps track of number of available pages, current page number and so on.<br/>
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 * @since           0.0.1
 */

public class PageHandler implements Serializable {

    public enum PageAction  {FIRST_PAGE, CURRENT_PAGE, PREVIOUS_PAGE, NEXT_PAGE}

    private final String istrSelectQuery;
    private final String istrCountQuery;
    private final ArrayList<Object> ioQueryParams;
    private final int inMaxPageSize;

    private List ioPage;
    private int inCurrentPageSize;
    private int inEndPosition;
    private int inStartPosition;
    private int inNrOfRecordsAvailable;

    public PageHandler(String pstrSelectQuery, String pstrCountQuery, ArrayList<Object> poQueryParams, int pnMaxPageSize)  {
        istrSelectQuery = pstrSelectQuery;
        istrCountQuery = pstrCountQuery;
        ioQueryParams = poQueryParams;
        inMaxPageSize = pnMaxPageSize;
        inStartPosition = 0;
    }

    /**
     * Get the select query.<br/>
     *
     * @return Select Query.
     */
    public String getSelectQuery() {
        return istrSelectQuery;
    }

    /**
     * Get the count query.<br/>
     *
     * @return count Query.
     */
    public String getCountQuery() {
        return istrCountQuery;
    }

    /**
     * Get Query parameters.<br/>
     *
     * @return Query parameters.
     */
    public ArrayList<Object> getQueryParams() {
        return ioQueryParams;
    }

    /**
     * Get max page size.<br/>
     *
     * @return Max page size.
     */
    public int getMaxPageSize() {
        return inMaxPageSize;
    }

    /**
     * Check if there is a previous page.<br/>
     *
     * @return True if there is a previous page.
     */
    public boolean hasPrevious() {
        return getCurrentPageNr() > 1;
    }

    /**
     * Check if there is a next page.<br/>
     *
     * @return True if there is a next page.
     */
    public boolean hasNextPage() {
        return getCurrentPageNr() < getTotalPageNr();
    }

    /**
     * Get current page number.<br/>
     *
     * @return Current page number.
     */
    public int getCurrentPageNr() {
        if (inMaxPageSize != 0) {
            int loCurrentPageNr = inEndPosition / inMaxPageSize;
            if (inEndPosition % inMaxPageSize > 0) {
                loCurrentPageNr++;
            }
            return loCurrentPageNr;
        } else return 0;
    }

    /**
     * Get total number of pages.<br/>
     *
     * @return Number of pages.
     */
    public int getTotalPageNr() {
        if (inMaxPageSize != 0) {
            int loTotalPageNr = inNrOfRecordsAvailable / inMaxPageSize;
            if (inNrOfRecordsAvailable % inMaxPageSize > 0) {
                loTotalPageNr++;
            }
            return loTotalPageNr;
        } else  return 0;
    }

    /**
     * Get Query parameters.<br/>
     *
     * @return Query parameters.
     */
    public int getNrOfRecordsAvailable() {
        return inNrOfRecordsAvailable;
    }

    /**
     * Set number of records available<br/>
     *
     * @param pnNrOfRecordsAvailable Number of records that are available. 
     */
    public void setNrOfRecordsAvailable(int pnNrOfRecordsAvailable) {
        inNrOfRecordsAvailable = pnNrOfRecordsAvailable;
    }

    /**
     * Get start position.<br/>
     *
     * @return Start position.
     */
    public int getStartPosition() {
        return inStartPosition;
    }

    /**
     * Set start position based on the action.<br/>
     *
     * @param poAction Page action.
     */
    public void setStartPosition(PageAction poAction) {
        switch (poAction) {
            case FIRST_PAGE :
                inStartPosition = 0;
                break;
            case CURRENT_PAGE :
                if (inStartPosition > inNrOfRecordsAvailable) {
                    inStartPosition = (getTotalPageNr() - 1) * inMaxPageSize;
                }
                break;
            case PREVIOUS_PAGE :
                inStartPosition = inEndPosition - inCurrentPageSize - inMaxPageSize;
                if (inStartPosition < 0) {
                    inStartPosition = 0;
                }
                break;
            case NEXT_PAGE :
                inStartPosition = inEndPosition;
                if (inStartPosition > inNrOfRecordsAvailable) {
                    inStartPosition = (getTotalPageNr() - 1) * inMaxPageSize;
                }
                break;
        }
    }

    /**
     * Set start position based on the page number.<br/>
     *
     * @param pnPageNr Page number.
     */
    public void setStartPosition(int pnPageNr) {
        inStartPosition = pnPageNr * inMaxPageSize;
        if (inStartPosition > inNrOfRecordsAvailable) {
            inStartPosition = (getTotalPageNr() - 1) * inMaxPageSize;
        }
    }

    /**
     * Get page object.<br/>
     *
     * @return Page objects.
     */
    public List getPage() {
        return ioPage;
    }

    /**
     * Set list of page objects.<br/>
     *
     * @param poPage List with page object.
     */
    public void setPage(List poPage) {
        ioPage = poPage;
        inCurrentPageSize = ioPage.size();
        inEndPosition = inStartPosition + inCurrentPageSize;
	}
}
