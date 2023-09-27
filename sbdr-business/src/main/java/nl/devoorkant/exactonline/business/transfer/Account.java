package nl.devoorkant.exactonline.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.devoorkant.sbdr.business.transfer.ODataDateTimeAdapter;

@XmlRootElement
@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
	private String id;
	private String name;
	private String mainContact;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String postcode;
	private String phone;
	private String country;
	private String VatNumber;
	private Short isAccountant;
	private String email;
	private String city;
	private String chamberOfCommerce;
	private String status;
	private Date startDate;
	
	public Account() {
		
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

	@XmlElement(name="Name") 	
	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	@JsonProperty("Name")
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name="MainContact") 	
	@JsonProperty("MainContact")
	public String getMainContact() {
		return mainContact;
	}

	@JsonProperty("MainContact")
	public void setMainContact(String mainContact) {
		this.mainContact = mainContact;
	}

	@XmlElement(name="AddressLine1") 	
	@JsonProperty("AddressLine1")
	public String getAddressLine1() {
		return addressLine1;
	}

	@JsonProperty("AddressLine1")
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	@XmlElement(name="AddressLine2") 	
	@JsonProperty("AddressLine2")
	public String getAddressLine2() {
		return addressLine2;
	}

	@JsonProperty("AddressLine2")
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	@XmlElement(name="AddressLine3") 	
	@JsonProperty("AddressLine3")
	public String getAddressLine3() {
		return addressLine3;
	}

	@JsonProperty("AddressLine3")
	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	@XmlElement(name="Postcode") 	
	@JsonProperty("Postcode")
	public String getPostcode() {
		return postcode;
	}

	@JsonProperty("Postcode")
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@XmlElement(name="Phone") 	
	@JsonProperty("Phone")
	public String getPhone() {
		return phone;
	}

	@JsonProperty("Phone")
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@XmlElement(name="Country") 	
	@JsonProperty("Country")
	public String getCountry() {
		return country;
	}

	@JsonProperty("Country")
	public void setCountry(String country) {
		this.country = country;
	}

	@XmlElement(name="VATNumber") 		
	@JsonProperty("VATNumber")
	public String getVatNumber() {
		return VatNumber;
	}

	@JsonProperty("VATNumber")
	public void setVatNumber(String vatNumber) {
		VatNumber = vatNumber;
	}

	@XmlElement(name="IsAccountant") 	
	@JsonProperty("IsAccountant")
	public Short getIsAccountant() {
		return isAccountant;
	}

	@JsonProperty("IsAccountant")
	public void setIsAccountant(Short isAccountant) {
		this.isAccountant = isAccountant;
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

	@XmlElement(name="City") 	
	@JsonProperty("City")
	public String getCity() {
		return city;
	}

	@JsonProperty("City")
	public void setCity(String city) {
		this.city = city;
	}

	@XmlElement(name="ChamberOfCommerce") 	
	@JsonProperty("ChamberOfCommerce")
	public String getChamberOfCommerce() {
		return chamberOfCommerce;
	}

	@JsonProperty("ChamberOfCommerce")
	public void setChamberOfCommerce(String chamberOfCommerce) {
		this.chamberOfCommerce = chamberOfCommerce;
	}

	@XmlElement(name="Status") 	
	@JsonProperty("Status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("Status")
	public void setStatus(String status) {
		this.status = status;
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
