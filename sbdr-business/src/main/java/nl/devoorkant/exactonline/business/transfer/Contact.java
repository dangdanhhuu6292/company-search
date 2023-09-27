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

public class Contact {
	private String id;
	
	private String account;
	
	private String firstName;
	
	private String lastName;
	
	private String businessEmail;

	private String businessPhone;

	private String email;

	private Date startDate;

	private String gender;
	
	public Contact() {
		
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

	@XmlElement(name="FirstName") 
	@JsonProperty("FirstName")	
	public String getFirstName() {
		return firstName;
	}
	
	@JsonProperty("FirstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(name="LastName") 
	@JsonProperty("LastName")	
	public String getLastName() {
		return lastName;
	}

	@JsonProperty("LastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement(name="BusinessEmail") 
	@JsonProperty("BusinessEmail")	
	public String getBusinessEmail() {
		return businessEmail;
	}

	@JsonProperty("BusinessEmail")
	public void setBusinessEmail(String businessEmail) {
		this.businessEmail = businessEmail;
	}

	@XmlElement(name="BusinessPhone") 
	@JsonProperty("BusinessPhone")	
	public String getBusinessPhone() {
		return businessPhone;
	}

	@JsonProperty("BusinessPhone")
	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}

	@XmlElement(name="Email") 
	@JsonProperty("Email")	
	public String getEmail() {
		return email;
	}

	@JsonProperty("Email")
	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement(name="Gender") 
	@JsonProperty("Gender")	
	public String getGender() {
		return gender;
	}

	@JsonProperty("Gender")
	public void setGender(String gender) {
		this.gender = gender;
	}

	@XmlElement(name="StartDate") 
	@JsonProperty("StartDate")	
	@XmlJavaTypeAdapter(value=ODataDateTimeAdapter.class, type=Date.class)
	public Date getStartDate() {
		return startDate;
	}
	
	@JsonProperty("StartDate")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}	
}
