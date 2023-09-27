package nl.devoorkant.sbdr.business.service;

public class ThirdPartyServiceException extends org.springframework.transaction.TransactionSystemException {
    private static final long serialVersionUID = 1L;

    public ThirdPartyServiceException() {
    	super(null);
    }

    public ThirdPartyServiceException(String message) {
        super(message);
    }
    
    public ThirdPartyServiceException(Throwable throwable) {
        super(null, throwable);
    }    

    public ThirdPartyServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
