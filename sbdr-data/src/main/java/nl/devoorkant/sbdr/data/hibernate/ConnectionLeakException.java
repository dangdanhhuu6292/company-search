package nl.devoorkant.sbdr.data.hibernate;

import nl.devoorkant.exception.DVKException;

public class ConnectionLeakException extends DVKException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ConnectionLeakException(String msg) {
		super(msg);
	}

}
