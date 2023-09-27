package nl.devoorkant.sbdr.ws.transfer;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.Map;

@XmlRootElement
public class UserAccount extends UserTransfer {
	//private String emailAdres = null;
	private String functie = null;
	private String naam = null;
	private String voornaam = null;
	//private Integer bedrijfId = null;
	private String wachtwoord = null;
	private boolean verantwoordelijkheidAkkoord = false;
	private String clientId; // API
	private String clientSecret; // API

	public UserAccount() {
		super();
	}

	public UserAccount(Integer bedrijfId, String bedrijfNaam, Integer userId, String userName, String wachtwoord, String voornaam, String naam, String functie, String afdeling, String emailAdres, String gebruikerTelefoonNummer, String bedrijfTelefoonNummer, boolean isKlant, boolean isProspect, boolean isAdresOk, Map<String, Boolean> roles, boolean actionsPresent, Integer showHelp, String geslacht, boolean mobileUser, boolean isBedrijfManager) {
		super(userName, voornaam, naam, functie, afdeling, emailAdres, userId, bedrijfId, bedrijfNaam, gebruikerTelefoonNummer, bedrijfTelefoonNummer, isKlant, isProspect, isAdresOk, roles, actionsPresent, showHelp, geslacht, mobileUser, isBedrijfManager);
		//this.setBedrijfId(bedrijfId);
		this.wachtwoord = wachtwoord;
		this.voornaam = voornaam;
		this.naam = naam;
		//this.emailAdres = emailAdres;
		this.functie = functie;
	}

	/**
	 * Deserializes an Object of class User from its JSON representation
	 */
	public static UserAccount fromString(String jsonRepresentation) {
		ObjectMapper mapper = new ObjectMapper(); // Jackson's JSON marshaller
		UserAccount o = null;

		SimpleModule module = new SimpleModule("UserAccountDeserializer", new Version(1, 0, 0, null));
		//module.addDeserializer(UserTransfer.class, new UserDeserializer());
		mapper.registerModule(module);


		try {
			o = mapper.readValue(jsonRepresentation, UserAccount.class);
		} catch(IOException e) {
			throw new WebApplicationException();
		}
		return o;
	}

	@XmlElement
	public boolean isVerantwoordelijkheidAkkoord() {
		return verantwoordelijkheidAkkoord;
	}

	public void setVerantwoordelijkheidAkkoord(boolean verantwoordelijkheidAkkoord) {
		this.verantwoordelijkheidAkkoord = verantwoordelijkheidAkkoord;
	}

	@XmlElement
	public String getFunctie() {
		return functie;
	}

	public void setFunctie(String functie) {
		this.functie = functie;
	}

	@XmlElement
	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	@XmlElement
	public String getVoornaam() {
		return voornaam;
	}

	//	@XmlElement
	//	public String getEmailAdres() {
	//		return emailAdres;
	//	}
	//
	//	public void setEmailAdres(String emailAdres) {
	//		this.emailAdres = emailAdres;
	//	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	@XmlElement
	public String getWachtwoord() {
		return wachtwoord;
	}


	//	public Integer getBedrijfId() {
	//		return bedrijfId;
	//	}
	//
	//	public void setBedrijfId(Integer bedrijfId) {
	//		this.bedrijfId = bedrijfId;
	//	}

	public void setWachtwoord(String wachtwoord) {
		this.wachtwoord = wachtwoord;
	}

	@XmlElement
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@XmlElement
	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	

}

