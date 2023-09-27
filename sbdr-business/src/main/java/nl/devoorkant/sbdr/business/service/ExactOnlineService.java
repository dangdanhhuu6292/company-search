package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.exactonline.business.transfer.AuthenticationRequest;
import nl.devoorkant.sbdr.data.model.ExactOnlineAccess;


public interface ExactOnlineService {

	ExactOnlineAccess findExactOnlineAccess() throws ServiceException;

	void processExactOnline() throws ServiceException;

	void refreshToken() throws ServiceException;

	//void currentDivisionRequest() throws ServiceException;

	void tokenRequest(String code) throws ServiceException;
	
	AuthenticationRequest fetchAuthenticationRequest() ;

}
