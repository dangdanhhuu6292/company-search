package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.devoorkant.sbdr.business.transfer.BedrijfEntityTransfer;
import nl.devoorkant.sbdr.data.model.*;

@XmlRootElement
public class CompanyAccount {

	private Boolean adresOk;
	private BedrijfEntityTransfer bedrijf;
	private String errorMessage;
	private KlantEntityTransfer klant;
	private KortingsCode kortingsCode;
	private String opmerkingenAdres;
	private String referentieIntern;
	private VerifyRecaptcha verifyRecaptcha;
	private WachtwoordEntityTransfer wachtwoord;

	@XmlElement
	public Boolean getAdresOk() {
		return adresOk;
	}

	public void setAdresOk(Boolean adresOk) {
		this.adresOk = adresOk;
	}

	@XmlElement
	public BedrijfEntityTransfer getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(BedrijfEntityTransfer bedrijf) {
		this.bedrijf = bedrijf;
	}

	@XmlElement
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String message) {
		this.errorMessage = message;
	}

	@XmlElement
	public KlantEntityTransfer getKlant() {
		return klant;
	}

	public void setKlant(KlantEntityTransfer klant) {
		this.klant = klant;
	}

	@XmlElement
	public KortingsCode getKortingsCode() {
		return kortingsCode;
	}

	public void setKortingsCode(KortingsCode kortingsCode) {
		this.kortingsCode = kortingsCode;
	}

	@XmlElement
	public String getOpmerkingenAdres() {
		return opmerkingenAdres;
	}

	public void setOpmerkingenAdres(String opmerkingenAdres) {
		this.opmerkingenAdres = opmerkingenAdres;
	}

	@XmlElement
	public String getReferentieIntern() {
		return referentieIntern;
	}

	public void setReferentieIntern(String referentieIntern) {
		this.referentieIntern = referentieIntern;
	}

	@XmlElement
	public VerifyRecaptcha getVerifyRecaptcha() {
		return verifyRecaptcha;
	}

	public void setVerifyRecaptcha(VerifyRecaptcha verifyRecaptcha) {
		this.verifyRecaptcha = verifyRecaptcha;
	}

	@XmlElement
	public WachtwoordEntityTransfer getWachtwoord() {
		return wachtwoord;
	}

	public void setWachtwoord(WachtwoordEntityTransfer wachtwoord) {
		this.wachtwoord = wachtwoord;
	}
}
