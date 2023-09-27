package nl.devoorkant.exactonline.business.transfer;

import java.io.Serializable;

public class TokenResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3721710426031393312L;
	private String accessToken = null;
	private String tokenType = null;
	private String expiresIn = null;
	private String refreshToken = null; 
	
	public TokenResponse() {
		
	}

	public String getAccess_token() {
		return accessToken;
	}

	public void setAccess_token(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getToken_type() {
		return tokenType;
	}

	public void setToken_type(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getExpires_in() {
		return expiresIn;
	}

	public void setExpires_in(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefresh_token() {
		return refreshToken;
	}

	public void setRefresh_token(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
