package nl.devoorkant.sbdr.business.exception;

/**
 * An exception to be thrown in case of internal conversion exceptions.
 * 
 * @author <a href="mailto:jmarsman@devoorkant.nl">Jeroen Marsman</a>
 */
public class ConverterException extends Exception implements EdoException {
    private static final long serialVersionUID = 1L;
    
    public ConverterException(String message, Throwable throwable) {
    	super(throwable);
    }
    
    public ConverterException(String message) {
    	super(message);
    }    
}
