package nl.devoorkant.sbdr.business.service;

public class ServiceException extends org.springframework.transaction.TransactionSystemException {
    private static final long serialVersionUID = 1L;

    public ServiceException() {
    	super(null);
    }

    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(Throwable throwable) {
        super(null, throwable);
    }    

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
