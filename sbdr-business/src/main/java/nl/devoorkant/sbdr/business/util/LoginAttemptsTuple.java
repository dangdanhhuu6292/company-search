package nl.devoorkant.sbdr.business.util;

import java.util.Date;
import java.util.HashMap;

public class LoginAttemptsTuple {
	HashMap<String, LoginAttempt> loginAttempts = new HashMap<String, LoginAttempt>();
	private int nrloginAttempts = 0;

	private long blockexpires;
	
	public LoginAttemptsTuple(LoginAttempt loginAttempt, long blockexpires) {
		this.nrloginAttempts = 1;
		this.blockexpires = blockexpires;
		this.loginAttempts.put(loginAttempt.getUsername() + "@1", loginAttempt);
	}

	public HashMap<String, LoginAttempt> getLoginAttempts() {
		return this.loginAttempts;
	}

	public void addLoginAttempt(String username, String password, Date datetime) {
		this.nrloginAttempts++;
		this.loginAttempts.put(username + "@" + this.nrloginAttempts, new LoginAttempt(username, password, datetime));
	}

	public long getBlockExpires() {
		return blockexpires;
	}

	public void setBlockExpires(long expires) {
		this.blockexpires = expires;
	}
	
	public int getNrloginAttempts() {
		return nrloginAttempts;
	}
}
