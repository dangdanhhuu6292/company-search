package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TokenTransfer {
	private String token = null;
	private String refresh_token = null;
	private String basicAuth = null; // Base64 clientId:clientSecret encoded
	private int maxLoginAttempts = 5;
	private int nrLoginAttempt;
	private String errorMsg;


	public TokenTransfer()
	{
		
	}
	
	public TokenTransfer(String token, String refresh_token, String basicAuth, int nrLoginAttempt, String errorMsg)
	{
		this.token = token;
		this.refresh_token = refresh_token;
		this.basicAuth = basicAuth;
		this.nrLoginAttempt = nrLoginAttempt;
		this.errorMsg = errorMsg;
	}


	@XmlElement
	public String getToken()
	{
		return this.token;
	}

	@XmlElement
	public int getMaxLoginAttempts() {
		return maxLoginAttempts;
	}

	public void setMaxLoginAttempts(int maxLoginAttempts) {
		this.maxLoginAttempts = maxLoginAttempts;
	}

	@XmlElement
	public int getNrLoginAttempt() {
		return nrLoginAttempt;
	}

	public void setNrLoginAttempt(int nrLoginAttempt) {
		this.nrLoginAttempt = nrLoginAttempt;
	}

	@XmlElement
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@XmlElement
	public String getRefreshToken() {
		return refresh_token;
	}
	
	@XmlElement
	public String getBasicAuth() {
		return basicAuth;
	}
	
	
}
