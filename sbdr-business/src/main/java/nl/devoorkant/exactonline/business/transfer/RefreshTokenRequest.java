package nl.devoorkant.exactonline.business.transfer;

import java.io.Serializable;

public class RefreshTokenRequest implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6786385069306952118L;
	private String grant_type = "refresh_token";
	private String refresh_token = null;
	private String client_id = null;
	private String client_secret = null;
	
	public RefreshTokenRequest() {
		
	}

	public String getGrant_type() {
		return grant_type;
	}

	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	
}
