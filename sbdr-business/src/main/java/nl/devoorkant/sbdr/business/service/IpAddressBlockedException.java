package nl.devoorkant.sbdr.business.service;

public class IpAddressBlockedException extends org.springframework.transaction.TransactionSystemException {
    private static final long serialVersionUID = 1L;

    public IpAddressBlockedException() {
    	super(null);
    }

    public IpAddressBlockedException(String message) {
        super(message);
    }
    
    public IpAddressBlockedException(Throwable throwable) {
        super(null, throwable);
    }    

    public IpAddressBlockedException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
