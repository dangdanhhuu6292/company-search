package nl.devoorkant.insolventie;

import nl.devoorkant.exception.DVKException;

public class CIRException extends DVKException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a CIRException.
     */
    public CIRException() {
        super();
    }

    /**
     * Constructs a CIRException with the specified message.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public CIRException(String pstrMessage) {
        super(pstrMessage);
    }

    /**
     * Constructs a CIRException based on the specified message and throwable.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     * @param poThrowable   A Throwable to add to the exception.
     */
    public CIRException(String pstrMessage, Throwable poThrowable) {
        super(pstrMessage, poThrowable);
    }
}