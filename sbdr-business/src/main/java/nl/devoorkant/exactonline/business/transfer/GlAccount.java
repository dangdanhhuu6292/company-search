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
public class GlAccount {
	private String id;
	private String balanceSide; // D (Debit) C (Credit)
	private String balanceType; // B (Balance Sheet) W (Profit & Loss)
	private String code;
	private String vatCode;

	public GlAccount() {
		
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

	@XmlElement(name="BalanceSide") 
	@JsonProperty("BalanceSide")		
	public String getBalanceSide() {
		return balanceSide;
	}

	@JsonProperty("BalanceSide")		
	public void setBalanceSide(String balanceSide) {
		this.balanceSide = balanceSide;
	}

	@XmlElement(name="BalanceType") 
	@JsonProperty("BalanceType")		
	public String getBalanceType() {
		return balanceType;
	}

	@JsonProperty("BalanceType")		
	public void setBalanceType(String balanceType) {
		this.balanceType = balanceType;
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

	@XmlElement(name="VATCode") 
	@JsonProperty("VATCode")		
	public String getVatCode() {
		return vatCode;
	}

	@JsonProperty("VATCode")		
	public void setVatCode(String vatCode) {
		this.vatCode = vatCode;
	}	
}
