package nl.devoorkant.sbdr.business.util;

import java.util.Date;

public class LoginAttempt {
	private String username;
	private String password;
	private Date datetime;
	
	public LoginAttempt(String username, String password, Date datetime) {
		this.setUsername(username);
		this.setPassword(password);
		this.setDatetime(datetime);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
}
