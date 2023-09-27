package nl.devoorkant.exactonline.business.transfer;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class ExactOnlineError implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3379737499851666773L;

	private String code;
	private ErrorMessage message;
	
	public ExactOnlineError() {
		
	}

	@XmlElement
	@JsonProperty
	public String getCode() {
		return code;
	}

	@JsonProperty
	public void setCode(String code) {
		this.code = code;
	}

	@XmlElement
	@JsonProperty("message")
	public ErrorMessage getMessage() {
		return message;
	}

	@JsonProperty("message")
	public void setErrorMessage(ErrorMessage message) {
		this.message = message;
	}
	
	
}
