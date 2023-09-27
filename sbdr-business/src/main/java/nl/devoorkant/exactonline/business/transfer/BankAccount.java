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
public class BankAccount {
	private String id;
	private String account;
	private String bankAccount;
	private String bankAccountHolderName;
	private Boolean main;
	private Date created;
	
	public BankAccount() {
		
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

	@XmlElement(name="BankAccountHolderName") 	
	@JsonProperty("BankAccountHolderName")
	public String getBankAccountHolderName() {
		return bankAccountHolderName;
	}
	
	@JsonProperty("BankAccountHolderName")
	public void setBankAccountHolderName(String bankAccountHolderName) {
		this.bankAccountHolderName = bankAccountHolderName;
	}	
	
	@XmlElement(name="Main") 	
	@JsonProperty("Main")
	public Boolean getMain() {
		return main;
	}

	@JsonProperty("Main")
	public void setMain(Boolean main) {
		this.main = main;
	}
	
	@XmlElement(name="Created") 
	@JsonProperty("Created")
	@XmlJavaTypeAdapter(value=ODataDateTimeAdapter.class, type=Date.class)	
	public Date getCreated() {
		return created;
	}
	
	@JsonProperty("Created")
	public void setCreated(Date created) {
		this.created = created;
	}
}



