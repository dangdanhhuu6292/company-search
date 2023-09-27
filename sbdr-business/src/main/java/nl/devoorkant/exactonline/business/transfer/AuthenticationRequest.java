package nl.devoorkant.exactonline.business.transfer;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//@XmlRootElement(name="AuthenticationRequest")
@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
public class AuthenticationRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8078479804678643857L;
	private String clientId = null;
	private String redirectUri = null;
	private String responseType = "code";
	private String site = null; // for website purpose
	
	public AuthenticationRequest () {
		
	}

	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public String getRedirectUri() {
		return redirectUri;
	}


	public void setRedirectUri(String redirectUrl) {
		this.redirectUri = redirectUrl;
	}


	public String getResponseType() {
		return responseType;
	}


	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
	
	
	
	
}
