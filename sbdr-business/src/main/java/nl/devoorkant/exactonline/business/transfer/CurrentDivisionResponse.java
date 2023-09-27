package nl.devoorkant.exactonline.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class CurrentDivisionResponse {
	private ExactOnlineSingleResponse __metadata = null; // JSON info String
	private Long currentDivision = null;
	private String email = null;
	private String firstName = null;
	private String fullName = null;
	private String userName = null;
	
	public CurrentDivisionResponse() {
		
	}

	@XmlElement(name="CurrentDivision") 
	public Long getCurrentDivision() {
		return currentDivision;
	}

	@JsonProperty("CurrentDivision")
	public void setCurrentDivision(Long currentDivision) {
		this.currentDivision = currentDivision;
	}

	@XmlElement(name="Email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("Email")
	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement(name="FirstName")
	public String getFirstName() {
		return firstName;
	}

	@JsonProperty("FirstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(name="FullName")
	public String getFullName() {
		return fullName;
	}

	@JsonProperty("FullName")
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@XmlElement(name="UserName")
	public String getUserName() {
		return userName;
	}

	@JsonProperty("UserName")
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement(name="__metadata")
	public ExactOnlineSingleResponse get__metadata() {
		return __metadata;
	}

	@JsonProperty("__metadata")
	public void set__metadata(ExactOnlineSingleResponse __metadata) {
		this.__metadata = __metadata;
	}
	
	
}
