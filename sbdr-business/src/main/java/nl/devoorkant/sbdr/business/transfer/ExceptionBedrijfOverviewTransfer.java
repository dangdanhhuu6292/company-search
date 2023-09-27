package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement
public class ExceptionBedrijfOverviewTransfer extends BedrijfTransferXml {
	Date aangemaakt;
	BedrijfTransfer bedrijfDoor;
	Integer customMeldingId;
	boolean dreigendFaillissement;
	boolean faillissementVraag;
	boolean frauduleusBedrijf;
	GebruikerTransfer gebruiker;
	boolean incorrectGegeven;
	boolean isKlant = false;
	boolean isProspect = false;
	String meldingDetails;
	String signTelefoonNummer;

	public ExceptionBedrijfOverviewTransfer() {
		super();
	}

	public ExceptionBedrijfOverviewTransfer(Integer customMeldingId, Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, BedrijfTransfer bedrijfDoor, boolean isKlant, boolean isProspect, Date aangemaakt, GebruikerTransfer gebruiker, boolean frauduleusBedrijf, boolean dreigendFaillissement, boolean incorrectGegeven, boolean faillissementVraag, String meldingDetails, String telefoonnummer, String signTelefoonNummer) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);

		this.customMeldingId = customMeldingId;
		this.bedrijfDoor = bedrijfDoor;
		this.isKlant = isKlant;
		this.isProspect = isProspect;
		this.gebruiker = gebruiker;
		this.aangemaakt = aangemaakt;
		this.frauduleusBedrijf = frauduleusBedrijf;
		this.dreigendFaillissement = dreigendFaillissement;
		this.incorrectGegeven = incorrectGegeven;
		this.faillissementVraag = faillissementVraag;
		this.meldingDetails = meldingDetails;
		this.signTelefoonNummer = signTelefoonNummer;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getAangemaakt() {
		return aangemaakt;
	}

	public void setAangemaakt(Date aangemaakt) {
		this.aangemaakt = aangemaakt;
	}

	@XmlElement
	public BedrijfTransfer getBedrijfDoor() {
		return bedrijfDoor;
	}

	public void setBedrijfDoor(BedrijfTransfer bedrijfDoor) {
		this.bedrijfDoor = bedrijfDoor;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getCustomMeldingId() {
		return customMeldingId;
	}

	public void setCustomMeldingId(Integer customMeldingId) {
		this.customMeldingId = customMeldingId;
	}

	@XmlElement
	public GebruikerTransfer getGebruiker() {
		return gebruiker;
	}

	public void setGebruiker(GebruikerTransfer gebruiker) {
		this.gebruiker = gebruiker;
	}

	@XmlElement
	public String getMeldingDetails() {
		return meldingDetails;
	}

	public void setMeldingDetails(String meldingDetails) {
		this.meldingDetails = meldingDetails;
	}

	@XmlElement
	public String getSignTelefoonNummer() {
		return signTelefoonNummer;
	}

	public void setSignTelefoonNummer(String signTelefoonNummer) {
		this.signTelefoonNummer = signTelefoonNummer;
	}

	@XmlElement
	public boolean isDreigendFaillissement() {
		return dreigendFaillissement;
	}

	public void setDreigendFaillissement(boolean dreigendFaillissement) {
		this.dreigendFaillissement = dreigendFaillissement;
	}

	@XmlElement
	public boolean isFaillissementVraag() {
		return faillissementVraag;
	}

	public void setFaillissementVraag(boolean faillissementVraag) {
		this.faillissementVraag = faillissementVraag;
	}

	@XmlElement
	public boolean isFrauduleusBedrijf() {
		return frauduleusBedrijf;
	}

	public void setFrauduleusBedrijf(boolean frauduleusBedrijf) {
		this.frauduleusBedrijf = frauduleusBedrijf;
	}

	@XmlElement
	public boolean isIncorrectGegeven() {
		return incorrectGegeven;
	}

	public void setIncorrectGegeven(boolean incorrectGegeven) {
		this.incorrectGegeven = incorrectGegeven;
	}

	@XmlElement
	public boolean isKlant() {
		return isKlant;
	}

	public void setKlant(boolean isKlant) {
		this.isKlant = isKlant;
	}

	@XmlElement
	public boolean isProspect() {
		return isProspect;
	}

	public void setProspect(boolean isProspect) {
		this.isProspect = isProspect;
	}


}
