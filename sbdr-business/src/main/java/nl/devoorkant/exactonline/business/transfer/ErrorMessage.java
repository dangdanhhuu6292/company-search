package nl.devoorkant.exactonline.business.transfer;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;


@XmlRootElement
public class ErrorMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6772349355621100874L;

	private String lang;
	private String value;
	
	public ErrorMessage() {
		
	}

	@XmlElement
	@JsonProperty
	public String getLang() {
		return lang;
	}

	@JsonProperty
	public void setLang(String lang) {
		this.lang = lang;
	}

	@XmlElement
	@JsonProperty
	public String getValue() {
		return value;
	}

	@JsonProperty
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
