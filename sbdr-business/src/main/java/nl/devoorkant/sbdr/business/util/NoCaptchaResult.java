package nl.devoorkant.sbdr.business.util;


import java.security.Timestamp;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
public class NoCaptchaResult {
	boolean success;
	String hostname;
	String challenge_ts;
	String[] errorCodes;
	
	public NoCaptchaResult() {
		
	}
	
	@JsonProperty("success")
	public boolean isSuccess() {
		return success;
	}
	@JsonProperty("success")
	public void setSuccess(boolean success) {
		this.success = success;
	}
	@JsonProperty("error-codes")
	public String[] getErrorCodes() {
		return errorCodes;
	}
	@JsonProperty("error-codes")
	public void setErrorCodes(String[] errorCodes) {
		this.errorCodes = errorCodes;
	}

	@JsonProperty("hostname")
	public String getHostname() {
		return hostname;
	}
	@JsonProperty("hostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@JsonProperty("challenge_ts")
	public String getChallenge_ts() {
		return challenge_ts;
	}
	@JsonProperty("challenge_ts")
	public void setChallenge_ts(String challenge_ts) {
		this.challenge_ts = challenge_ts;
	}
	
	
}
