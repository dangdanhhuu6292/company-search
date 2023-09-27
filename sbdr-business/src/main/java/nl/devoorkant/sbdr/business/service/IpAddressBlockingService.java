package nl.devoorkant.sbdr.business.service;

import java.util.Date;

public interface IpAddressBlockingService {

	/**
	 * 
	 * @param ipaddress
	 * @return
	 */
	boolean isIpAllowedToLogin(String ipaddress);

	/**
	 * 
	 * @param ipaddress
	 * @param username
	 * @param password
	 * @param datetime
	 */
	void addLoginAttempt(String ipaddress, String username, String password,
			Date datetime);

	/**
	 * 
	 * @param ipaddress
	 */
	void removeLoginAttemptsIp(String ipaddress);

	/**
	 * 
	 */
	void removeInvalidLoginAttemptsIp();

	/**
	 * 
	 * @param ipaddress
	 * @return
	 */
	int getNrOfLoginAttempts(String ipaddress);

}
