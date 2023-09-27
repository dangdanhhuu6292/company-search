package nl.devoorkant.sbdr.ws.transfer;

import nl.devoorkant.sbdr.business.transfer.IdentifierAdapter;
import nl.devoorkant.sbdr.business.transfer.MapAdapterBoolean;
import nl.devoorkant.sbdr.data.util.ERol;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
public class UserTransfer {
	private boolean actionsPresent = false;
	private boolean adresOk = true;
	private String afdeling = null;
	private Integer bedrijfId = null;
	private String bedrijfNaam = null;
	private String bedrijfTelefoonNummer = null;
	private String emailAdres = null;
	private String firstName = null;
	private String fullName = null;
	private String functie = null;
	private String gebruikerTelefoonNummer = null;
	private String geslacht = null;
	private boolean klant = false;
	private String lastName = null;
	private boolean prospect = false;
	private Map<String, Boolean> roles = null;
	private Integer showHelp = null;
	private Integer userId = null;
	private String userName = null;
	private boolean mobileUser = false;
	private boolean isBedrijfManager = false;
	private String clientId = null; // API
	private String clientSecret = null; // API

	public UserTransfer() {
		//roles = new HashMap<String, Boolean>();
	}

	public UserTransfer(String userName, String voornaam, String naam, String functie, String afdeling, String emailAdres, Integer userId, Integer bedrijfId, String bedrijfNaam, String gebruikerTelefoonNummer, String bedrijfTelefoonNummer, boolean isKlant, boolean isProspect, boolean isAdresOk, Map<String, Boolean> roles, boolean actionsPresent, Integer showHelp, String geslacht, boolean mobileUser, boolean isBedrijfManager) {
		this.userName = userName;
		this.setFirstName(voornaam);
		this.setLastName(naam);
		this.functie = functie;
		this.afdeling = afdeling;
		this.emailAdres = emailAdres;
		if(voornaam != null && !voornaam.trim().equals("")) this.setFullName(voornaam + " " + naam);
		else this.setFullName(naam);
		this.userId = userId;
		this.setBedrijfId(bedrijfId);
		this.bedrijfNaam = bedrijfNaam;
		this.gebruikerTelefoonNummer = gebruikerTelefoonNummer;
		this.bedrijfTelefoonNummer = bedrijfTelefoonNummer;
		this.prospect = isProspect;
		this.adresOk = isAdresOk;
		this.klant = isKlant;
		this.setRoles(roles);
		this.actionsPresent = actionsPresent;
		this.showHelp = showHelp;
		this.geslacht = geslacht;
		this.mobileUser = mobileUser;
		this.isBedrijfManager = isBedrijfManager;
	}
	
	public UserTransfer(String userName, String fullName, String functie, String afdeling, String emailAdres, Integer userId, Integer bedrijfId, String bedrijfNaam, String gebruikerTelefoonNummer, String bedrijfTelefoonNummer, boolean isKlant, boolean isProspect, boolean isAdresOk, Map<String, Boolean> roles, boolean actionsPresent, Integer showHelp, String geslacht, boolean mobileUser, boolean isBedrijfManager) {
		this.userName = userName;
		this.fullName = fullName;
		this.functie = functie;
		this.afdeling = afdeling;
		this.emailAdres = emailAdres;
		this.userId = userId;
		this.setBedrijfId(bedrijfId);
		this.bedrijfNaam = bedrijfNaam;
		this.gebruikerTelefoonNummer = gebruikerTelefoonNummer;
		this.bedrijfTelefoonNummer = bedrijfTelefoonNummer;
		this.klant = isKlant;
		this.setRoles(roles);
		this.actionsPresent = actionsPresent;
		this.showHelp = showHelp;
		this.prospect = isProspect;
		this.adresOk = isAdresOk;
		this.geslacht = geslacht;
		this.mobileUser = mobileUser;
		this.isBedrijfManager = isBedrijfManager;
	}

	/**
	 * Deserializes an Object of class UserTransfer from its JSON representation
	 */
	public static UserTransfer fromString(String jsonRepresentation) {
		ObjectMapper mapper = new ObjectMapper(); // Jackson's JSON marshaller
		UserTransfer o = null;

		SimpleModule module = new SimpleModule("UserTransferDeserializer", new Version(1, 0, 0, null));
		module.addDeserializer(UserTransfer.class, new UserTransferDeserializer());
		mapper.registerModule(module);

		try {
			o = mapper.readValue(jsonRepresentation, UserTransfer.class);
		} catch(IOException e) {
			throw new WebApplicationException();
		}
		return o;
	}

	@XmlElement
	public boolean getAdresOk() {
		return adresOk;
	}

	public void setAdresOk(boolean adresOk) {
		this.adresOk = adresOk;
	}

	@XmlElement
	public String getAfdeling() {
		return afdeling;
	}

	public void setAfdeling(String afdeling) {
		this.afdeling = afdeling;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getBedrijfId() {
		return bedrijfId;
	}

	public void setBedrijfId(Integer bedrijfId) {
		this.bedrijfId = bedrijfId;
	}

	@XmlElement
	public String getBedrijfNaam() {
		return bedrijfNaam;
	}

	public void setBedrijfNaam(String bedrijfNaam) {
		this.bedrijfNaam = bedrijfNaam;
	}

	@XmlElement
	public String getBedrijfTelefoonNummer() {
		return bedrijfTelefoonNummer;
	}

	public void setBedrijfTelefoonNummer(String bedrijfTelefoonNummer) {
		this.bedrijfTelefoonNummer = bedrijfTelefoonNummer;
	}

	@XmlElement
	public String getEmailAdres() {
		return emailAdres;
	}

	public void setEmailAdres(String emailAdres) {
		this.emailAdres = emailAdres;
	}

	@XmlElement
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@XmlElement
	public String getFunctie() {
		return functie;
	}

	public void setFunctie(String functie) {
		this.functie = functie;
	}

	@XmlElement
	public String getGebruikerTelefoonNummer() {
		return gebruikerTelefoonNummer;
	}

	public void setGebruikerTelefoonNummer(String gebruikerTelefoonNummer) {
		this.gebruikerTelefoonNummer = gebruikerTelefoonNummer;
	}

	@XmlElement
	public String getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(String geslacht) {
		this.geslacht = geslacht;
	}

	@XmlElement
	public boolean getKlant() {
		return this.klant;
	}

	public void setKlant(boolean klant) {
		this.klant = klant;
	}

	@XmlElement
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement
	public boolean getProspect() {
		return this.prospect;
	}

	public void setProspect(boolean prospect) {
		this.prospect = prospect;
	}

	@XmlElement
	@XmlJavaTypeAdapter(MapAdapterBoolean.class)
	public Map<String, Boolean> getRoles() {
		return roles;
	}

	public void setRoles(Map<String, Boolean> roles) {
		if(roles != null) {
			this.roles = new HashMap<String, Boolean>();

			Boolean auth = (Boolean) roles.get(ERol.KLANT.getCode());
			Boolean auth2 = (Boolean) roles.get("admin_klant");
			if(auth != null) this.roles.put("admin_klant", auth); // ROLE_KLANT
			else if(auth2 != null) this.roles.put("admin_klant", auth2); // ROLE_KLANT

			auth = (Boolean) roles.get(ERol.HOOFD.getCode());
			auth2 = (Boolean) roles.get("hoofd_klant");
			if(auth != null) this.roles.put("hoofd_klant", auth); // ROLE_HOOFD
			else if(auth2 != null) this.roles.put("hoofd_klant", auth2); // ROLE_HOOFD

			auth = (Boolean) roles.get(ERol.GEBRUIKER.getCode());
			auth2 = (Boolean) roles.get("gebruiker_klant");
			if(auth != null) this.roles.put("gebruiker_klant", auth); // ROLE_USER
			else if(auth2 != null) this.roles.put("gebruiker_klant", auth2); // ROLE_USER

			auth = (Boolean) roles.get(ERol.MANAGED.getCode());
			auth2 = (Boolean) roles.get("bedrijf_manager");
			if(auth != null) this.roles.put("bedrijf_manager", auth); // ROLE_MANAGED
			else if(auth2 != null) this.roles.put("bedrijf_manager", auth2); // ROLE_MANAGED
			
			auth = (Boolean) roles.get(ERol.SBDR.getCode());
			auth2 = (Boolean) roles.get("admin_sbdr");
			if(auth != null) this.roles.put("admin_sbdr", auth); // ROLE_SBDR
			else if(auth2 != null) this.roles.put("admin_sbdr", auth2); // ROLE_SBDR

			auth = (Boolean) roles.get(ERol.SBDRHOOFD.getCode());
			auth2 = (Boolean) roles.get("admin_sbdr_hoofd");
			if(auth != null) this.roles.put("admin_sbdr_hoofd", auth); // ROLE_SBDR_HOOFD
			else if(auth2 != null) this.roles.put("admin_sbdr_hoofd", auth2); // ROLE_SBDR_HOOFD
			
			auth = (Boolean) roles.get(ERol.REGISTRATIESTOEGESTAAN.getCode());
			auth2 = (Boolean) roles.get("registraties_toegestaan");
			if(auth != null) this.roles.put("registraties_toegestaan", auth); // ROLE_REGISTRATIES
			else if(auth2 != null) this.roles.put("registraties_toegestaan", auth2); // ROLE_REGISTRATIES
			
			auth = (Boolean) roles.get(ERol.APITOEGESTAAN.getCode());
			auth2 = (Boolean) roles.get("api_toegestaan");
			if(auth != null) this.roles.put("api_toegestaan", auth); // ROLE_REGISTRATIES
			else if(auth2 != null) this.roles.put("api_toegestaan", auth2); // ROLE_REGISTRATIES
			
		} else this.roles = null;
	}

	@XmlElement
	public Integer getShowHelp() {
		return showHelp;
	}

	public void setShowHelp(Integer showHelp) {
		this.showHelp = showHelp;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@XmlElement
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement
	public boolean isActionsPresent() {
		return actionsPresent;
	}

	public void setActionsPresent(boolean actionsPresent) {
		this.actionsPresent = actionsPresent;
	}

	@XmlElement
	public boolean isMobileUser() {
		return mobileUser;
	}

	public void setMobileUser(boolean mobileUser) {
		this.mobileUser = mobileUser;
	}

	@XmlElement
	public boolean isBedrijfManager() {
		return isBedrijfManager;
	}

	public void seBedrijfManager(boolean isBedrijfManager) {
		this.isBedrijfManager = isBedrijfManager;
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
