package nl.devoorkant.exactonline.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.business.transfer.ODataDateTimeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectDebitMandates {
	private String id;
	private String account;
	private String bankAccount;
	//private Date cancellationDate;
	private String description;
	private Short firstSend;
	private Integer paymentType;
	private Date signatureDate;
	private Integer type = 0; // Default Standaard Europese Incasso
	
	@XmlElement(name="ID") 
	@JsonProperty("ID")	
	public String getId() {
		return id;
	}
	@JsonProperty("ID")
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name="Account") 
	@JsonProperty("Account")	
	public String getAccount() {
		return account;
	}
	@JsonProperty("Account")	
	public void setAccount(String account) {
		this.account = account;
	}
	
	@XmlElement(name="BankAccount") 
	@JsonProperty("BankAccount")		
	public String getBankAccount() {
		return bankAccount;
	}
	@JsonProperty("BankAccount")		
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	
//	@XmlElement(name="CancellationDate") 
//	@JsonProperty("CancellationDate")		
//	public Date getCancellationDate() {
//		return cancellationDate;
//	}
//	@JsonProperty("CancellationDate")		
//	public void setCancellationDate(Date cancellationDate) {
//		this.cancellationDate = cancellationDate;
//	}
	
	@XmlElement(name="Description") 
	@JsonProperty("Description")		
	public String getDescription() {
		return description;
	}
	@JsonProperty("Description")		
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name="FirstSend") 
	@JsonProperty("FirstSend")		
	public Short getFirstSend() {
		return firstSend;
	}
	@JsonProperty("FirstSend")		
	public void setFirstSend(Short firstSend) {
		this.firstSend = firstSend;
	}
	
	@XmlElement(name="PaymentType") 
	@JsonProperty("PaymentType")		
	public Integer getPaymentType() {
		return paymentType;
	}
	@JsonProperty("PaymentType")		
	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}
	
	@XmlElement(name="SignatureDate") 
	@JsonProperty("SignatureDate")	
	@XmlJavaTypeAdapter(value=ODataDateTimeAdapter.class, type=Date.class)	
	public Date getSignatureDate() {
		return signatureDate;
	}
	@JsonProperty("SignatureDate")		
	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}
	
	@XmlElement(name="Type") 
	@JsonProperty("Type")		
	public Integer getType() {
		return type;
	}
	@JsonProperty("Type")		
	public void setType(Integer type) {
		this.type = type;
	}
}
