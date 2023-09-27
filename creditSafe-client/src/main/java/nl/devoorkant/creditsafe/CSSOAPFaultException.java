package nl.devoorkant.creditsafe;

import nl.devoorkant.exception.DVKException;

/**
 * Class for creation of SOAP fault exceptions, occuring while connecting to the Company Info web-services.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */
public class CSSOAPFaultException extends DVKException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a CISOAPFaultException.
     */
    public CSSOAPFaultException() {
        super();
    }

    /**
     * Constructs a CISOAPFaultException with the specified message.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public CSSOAPFaultException(String pstrMessage) {
        super(pstrMessage);
    }

    /**
     * Constructs a CISOAPFaultException based on the specified message and throwable.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     * @param poThrowable   A Throwable to add to the exception.
     */
    public CSSOAPFaultException(String pstrMessage, Throwable poThrowable) {
        super(pstrMessage, poThrowable);
    }
}
