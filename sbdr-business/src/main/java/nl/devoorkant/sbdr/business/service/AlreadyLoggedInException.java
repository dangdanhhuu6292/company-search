package nl.devoorkant.sbdr.business.service;

public class AlreadyLoggedInException extends org.springframework.transaction.TransactionSystemException {
    private static final long serialVersionUID = 1L;

    public AlreadyLoggedInException() {
    	super(null);
    }

    public AlreadyLoggedInException(String message) {
        super(message);
    }
    
    public AlreadyLoggedInException(Throwable throwable) {
        super(null, throwable);
    }    

    public AlreadyLoggedInException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
