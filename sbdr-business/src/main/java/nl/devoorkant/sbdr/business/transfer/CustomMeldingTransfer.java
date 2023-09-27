package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement
public class CustomMeldingTransfer {
	String achternaam;
	String afdeling;
	Integer bedrijfId;
	Integer bedrijfIdGerapporteerd;
	boolean faillissementVraag;
	boolean incorrectGegeven;
	String meldingDetails;
	String telefoonnummer;
	String voornaam;
	String wachtwoord;

	CustomMeldingTransfer() {

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
	public Integer getBedrijfId() {
		return bedrijfId;
	}

	public void setBedrijfId(Integer bedrijfId) {
		this.bedrijfId = bedrijfId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getBedrijfIdGerapporteerd() {
		return bedrijfIdGerapporteerd;
	}

	public void setBedrijfIdGerapporteerd(Integer bedrijfIdGerapporteerd) {
		this.bedrijfIdGerapporteerd = bedrijfIdGerapporteerd;
	}

	@XmlElement
	public String getMeldingDetails() {
		return meldingDetails;
	}

	public void setMeldingDetails(String meldingDetails) {
		this.meldingDetails = meldingDetails;
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

	@XmlElement
	public boolean isFaillissementVraag() {
		return faillissementVraag;
	}

	public void setFaillissementVraag(boolean faillissementVraag) {
		this.faillissementVraag = faillissementVraag;
	}

	@XmlElement
	public boolean isIncorrectGegeven() {
		return incorrectGegeven;
	}

	public void setIncorrectGegeven(boolean incorrectGegeven) {
		this.incorrectGegeven = incorrectGegeven;
	}


}
