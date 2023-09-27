package nl.devoorkant.exactonline.business.transfer;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
public class ErrorResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8418283865434433533L;
	
	private ExactOnlineError error;
	
	public ErrorResponse() {
		
	}

	@XmlElement
	@JsonProperty("error")
	public ExactOnlineError getError() {
		return error;
	}

	@JsonProperty("error")
	public void setError(ExactOnlineError error) {
		this.error = error;
	}
	
	

}
