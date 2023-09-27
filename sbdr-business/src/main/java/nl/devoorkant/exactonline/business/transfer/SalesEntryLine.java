package nl.devoorkant.exactonline.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name="SalesEntryLine")
@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
public class SalesEntryLine {
	private String id;
	private Double amountFc;
	private String entryId;
	private String glAccount; // 8500?
	private String VatCode;
	private String description;
			
	public SalesEntryLine () {
		
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

	@XmlElement(name="AmountFC") 
	@JsonProperty("AmountFC")	
	public Double getAmountFc() {
		return amountFc;
	}

	@JsonProperty("AmountFC")	
	public void setAmountFc(Double amountFc) {
		this.amountFc = amountFc;
	}

	@XmlElement(name="EntryID") 
	@JsonProperty("EntryID")	
	public String getEntryId() {
		return entryId;
	}

	@JsonProperty("EntryID")	
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	@XmlElement(name="GLAccount") 
	@JsonProperty("GLAccount")	
	public String getGlAccount() {
		return glAccount;
	}

	@JsonProperty("GLAccount")	
	public void setGlAccount(String glAccount) {
		this.glAccount = glAccount;
	}

	@XmlElement(name="VATCode") 
	@JsonProperty("VATCode")		
	public String getVatCode() {
		return VatCode;
	}

	@JsonProperty("VATCode")
	public void setVatCode(String vatCode) {
		VatCode = vatCode;
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
	
	
	
	
	
	
}
