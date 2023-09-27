package nl.devoorkant.sbdr.business.util;

public class WebTokenTuple {
	private String token;
	private long expires;
	private String userName;
	
	public WebTokenTuple(String token, String username, long expires) {
		this.token = token;
		this.expires = expires;
		this.userName = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
