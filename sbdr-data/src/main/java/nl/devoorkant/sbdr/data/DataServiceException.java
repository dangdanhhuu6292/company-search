package nl.devoorkant.sbdr.data;

import nl.devoorkant.exception.DVKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creation of DataService exceptions.
 * <p/>
 * EDO - Applicatie voor het verwerken van Export Documenten
 * <p/>
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public class DataServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DataServiceException.class);

    /**
     * Constructs a DataServiceException.
     */
    public DataServiceException() {
        super();
    }

    /**
     * Constructs a DataServiceException with the specified message.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public DataServiceException(String pstrMessage) {
        super(pstrMessage);
		LOGGER.error(pstrMessage);
    }

    /**
     * Constructs a DataServiceException based on the specified message and throwable.
     *
     * @param pstrMessage   A String containing a message, which is saved for later retrieval by the {@link #getMessage()} method.
     * @param poThrowable   A Throwable to add to the exception.
     */
    public DataServiceException(String pstrMessage, Throwable poThrowable) {
        super(pstrMessage, poThrowable);
    }
}
