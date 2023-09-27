package nl.devoorkant.sbdr.business.service;

public interface WebTokenService {

	/**
	 * Validates and refresh of WebToken
	 * 
	 * @param authToken
	 * @return
	 */
	boolean validateToken(String authToken, String userName) throws ServiceException;
	boolean validateApiKey(String key, String ipAddress, Long expiration);

	/**
	 * Create WebToken
	 * 
	 * @param userName
	 * @param authToken
	 * @return boolean success = true
	 */
	boolean addToken(String userName, String authToken) throws ServiceException;
	boolean addApiKey(String key, String ipAddress, Long expiration) throws ServiceException;

	/**
	 * Remove invalid webtokens
	 */
	void removeInvalidTokens();
	void removeInvalidApiKeys();

	/**
	 * 
	 * @param signature
	 * @return 
	 */
	boolean removeToken(String signature);
	boolean removeApiKey(String key);

	int getNrOfValidTokens();
	int getNrOfValidApiKeys();

	boolean connectWebTokenToNewUser(String userNameOld, String userNameNew);
	
	/**
	 * Remove webtoken of specific username
	 * 
	 * @param userName
	 * @return
	 */
	boolean removeWebTokenOfUser(String userName);
}
