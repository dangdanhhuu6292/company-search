package nl.devoorkant.insolventie;

import nl.devoorkant.exception.DVKException;

/**
 * Class for creation of CIR communication exceptions.
 * <p>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p>
 * Copyright:       Copyright (c) 2013. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         1.0. (26-11-2013)
 */
public class CIRCommunicationException extends DVKException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a CIRException.
     */
    public CIRCommunicationException() {
        super();
    }

    /**
     * Constructs a CIRException with the specified message.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public CIRCommunicationException(String pstrMessage) {
        super(pstrMessage);
    }

    /**
     * Constructs a CIRException based on the specified message and throwable.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     * @param poThrowable   A Throwable to add to the exception.
     */
    public CIRCommunicationException(String pstrMessage, Throwable poThrowable) {
        super(pstrMessage, poThrowable);
    }
}
