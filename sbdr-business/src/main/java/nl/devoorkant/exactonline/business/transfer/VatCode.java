package nl.devoorkant.exactonline.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name="VATCode")
@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
public class VatCode {
	private String id;
	private String code; // 
	private String description; // B (Balance Sheet) W (Profit & Loss)
	private Double percentage;

	public VatCode() {
		
	}

	@XmlElement(name="ID") 
	@JsonProperty("ID")		
	public String getId() {
		return id;
	}

	@JsonProperty("ID")		
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name="Code") 
	@JsonProperty("Code")		
	public String getCode() {
		return code;
	}

	@JsonProperty("Code")		
	public void setCode(String code) {
		this.code = code;
	}
	

	@XmlElement(name="Description") 
	@JsonProperty("Description")		
	public String getDescription() {
		return description;
	}

	@JsonProperty("Description")		
	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name="Percentage") 
	@JsonProperty("Percentage")		
	public Double getPercentage() {
		return percentage;
	}

	@JsonProperty("Percentage")		
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
}
