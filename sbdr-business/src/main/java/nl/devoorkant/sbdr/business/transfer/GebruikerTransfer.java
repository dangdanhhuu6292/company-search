package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

@XmlRootElement
public class GebruikerTransfer {
	private boolean actionsPresent = false;
	private String afdeling = null;
	private Integer bedrijfId = null;
	private String bedrijfNaam = null;
	private String bedrijfTelefoonNummer = null;
	private Map<String, Boolean> bevoegdheden = null;
	private String emailAdres = null;
	private String functie = null;
	private Integer gebruikersId = null;
	private String gebruikersNaam = null;
	private String gebruikersWachtwoord = null;
	private String geslacht = null;
	private boolean isKlant = false;
	private boolean isProspect = false;
	private boolean isAdresOk = false;
	private String naam = null;
	private Map<String, Boolean> rollen = null;
	private Integer showHelp = null;
	private String telefoonNummer = null;
	private String voornaam = null;
	private boolean isBedrijfManager = false;

	public GebruikerTransfer() {

	}

	public GebruikerTransfer(Integer bedrijfId, String bedrijfNaam, String gebruikersNaam, Integer gebruikersId, String voornaam, String achternaam, String afdeling, String functie, String emailAdres, String telefoonNummer, String bedrijfTelefoonNummer, String wachtwoord, boolean isKlant, boolean isProspect, boolean isAdresOk, Map<String, Boolean> rollen, Map<String, Boolean> bevoegdheden, boolean actionsPresent, Integer showHelp, String geslacht, boolean isBedrijfManager) {
		this.bedrijfId = bedrijfId;
		this.bedrijfNaam = bedrijfNaam;
		this.gebruikersNaam = gebruikersNaam;
		this.gebruikersId = gebruikersId;
		this.voornaam = voornaam;
		this.naam = achternaam;
		this.afdeling = afdeling;
		this.functie = functie;
		this.emailAdres = emailAdres;
		this.telefoonNummer = telefoonNummer;
		this.bedrijfTelefoonNummer = bedrijfTelefoonNummer;
		this.gebruikersWachtwoord = wachtwoord;
		this.isKlant = isKlant;
		this.isProspect = isProspect;
		this.isAdresOk = isAdresOk;
		this.rollen = rollen;
		this.bevoegdheden = bevoegdheden;
		this.actionsPresent = actionsPresent;
		this.showHelp = showHelp;
		this.geslacht = geslacht;
	}

	@XmlElement
	public String getAfdeling() {
		return afdeling;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getBedrijfId() {
		return bedrijfId;
	}

	@XmlElement
	public String getBedrijfNaam() {
		return bedrijfNaam;
	}

	@XmlElement
	public String getBedrijfTelefoonNummer() {
		return bedrijfTelefoonNummer;
	}

	public void setBedrijfTelefoonNummer(String bedrijfTelefoonNummer) {
		this.bedrijfTelefoonNummer = bedrijfTelefoonNummer;
	}

	@XmlElement
	@XmlJavaTypeAdapter(MapAdapterBoolean.class)
	public Map<String, Boolean> getBevoegdheden() {
		return this.rollen;
	}

	@XmlElement
	public String getEmailAdres() {
		return emailAdres;
	}

	@XmlElement
	public String getFunctie() {
		return functie;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikersId() {
		return gebruikersId;
	}

	public void setGebruikersId(Integer gebruikersId) {
		this.gebruikersId = gebruikersId;
	}

	@XmlElement
	public String getGebruikersNaam() {
		return this.gebruikersNaam;
	}

	@XmlElement
	public String getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(String geslacht) {
		this.geslacht = geslacht;
	}

	@XmlElement
	public String getNaam() {
		return naam;
	}

	public void setNaam(String n) {
		naam = n;
	}

	@XmlElement
	@XmlJavaTypeAdapter(MapAdapterBoolean.class)
	public Map<String, Boolean> getRoles() {
		return this.rollen;
	}

	@XmlElement
	public Integer getShowHelp() {
		return showHelp;
	}

	@XmlElement
	public String getTelefoonNummer() {
		return telefoonNummer;
	}

	public void setTelefoonNummer(String t) {
		telefoonNummer = t;
	}

	@XmlElement
	public String getVoornaam() {
		return voornaam;
	}

	@XmlElement
	public String getWachtwoord() {
		return this.gebruikersWachtwoord;
	}

	@XmlElement
	public boolean isActionsPresent() {
		return actionsPresent;
	}

	@XmlElement
	public boolean isKlant() {
		return isKlant;
	}

	@XmlElement
	public boolean isProspect() {
		return isProspect;
	}

	public void setVoorname(String s) {
		voornaam = s;
	}

	public boolean isAdresOk() {
		return isAdresOk;
	}

	public void setAdresOk(boolean isAdresOk) {
		this.isAdresOk = isAdresOk;
	}
	
	@XmlElement
	public boolean isBedrijfManager() {
		return isBedrijfManager;
	}

	public void setBedrijfManager(boolean isBedrijfManager) {
		this.isBedrijfManager = isBedrijfManager;
	}	

}
