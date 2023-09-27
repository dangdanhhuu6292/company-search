package nl.devoorkant.exactonline.business.transfer;

import java.io.Serializable;

public class TokenRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6786385069306952118L;
	private String code = null;
	private String redirect_uri = null;
	private String grant_type = "authorization_code";
	private String client_id = null;
	private String client_secret = null;
	//private int force_login = 0;
	
	public TokenRequest() {
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRedirect_uri() {
		return redirect_uri;
	}

	public void setRedirect_uri(String redirect_uri) {
		this.redirect_uri = redirect_uri;
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

	//public int getForce_login() {
	//	return force_login;
	//}

	//public void setForce_login(int force_login) {
	//	this.force_login = force_login;
	//}

	//public static long getSerialversionuid() {
	//	return serialVersionUID;
	//}
	
	
}
