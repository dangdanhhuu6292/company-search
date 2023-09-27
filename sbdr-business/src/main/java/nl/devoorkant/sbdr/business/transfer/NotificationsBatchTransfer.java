package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement
public class NotificationsBatchTransfer {
	String achternaam;
	String afdeling;
	Integer bedrijfIdDoor;
	Integer bedrijfIdOver;
	MeldingTransfer[] meldingen;
	String telefoonnummer;
	String voornaam;
	String wachtwoord;

	NotificationsBatchTransfer() {

	}

	@XmlElement
	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
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
	public Integer getBedrijfIdDoor() {
		return bedrijfIdDoor;
	}

	public void setBedrijfIdDoor(Integer bedrijfIdDoor) {
		this.bedrijfIdDoor = bedrijfIdDoor;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getBedrijfIdOver() {
		return bedrijfIdOver;
	}

	public void setBedrijfIdOver(Integer bedrijfIdOver) {
		this.bedrijfIdOver = bedrijfIdOver;
	}

	@XmlElement
	public MeldingTransfer[] getMeldingen() {
		return meldingen;
	}

	public void setMeldingen(MeldingTransfer[] meldingen) {
		this.meldingen = meldingen;
	}

	@XmlElement
	public String getTelefoonnummer() {
		return telefoonnummer;
	}

	public void setTelefoonnummer(String telefoonnummer) {
		this.telefoonnummer = telefoonnummer;
	}

	@XmlElement
	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	@XmlElement
	public String getWachtwoord() {
		return wachtwoord;
	}

	public void setWachtwoord(String wachtwoord) {
		this.wachtwoord = wachtwoord;
	}
}
