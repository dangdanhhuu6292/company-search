package nl.devoorkant.insolventie;

import nl.devoorkant.exception.DVKException;

/**
 * Class for creation of CIR SOAP fault exceptions.
 * <p>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p>
 * Copyright:       Copyright (c) 2013. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         1.0. (26-11-2013)
 */
public class CIRSOAPFaultException extends DVKException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a CIRSOAPFaultException.
     */
    public CIRSOAPFaultException() {
        super();
    }

    /**
     * Constructs a CIRSOAPFaultException with the specified message.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public CIRSOAPFaultException(String pstrMessage) {
        super(pstrMessage);
    }

    /**
     * Constructs a CIRSOAPFaultException based on the specified message and throwable.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     * @param poThrowable   A Throwable to add to the exception.
     */
    public CIRSOAPFaultException(String pstrMessage, Throwable poThrowable) {
        super(pstrMessage, poThrowable);
    }
}
