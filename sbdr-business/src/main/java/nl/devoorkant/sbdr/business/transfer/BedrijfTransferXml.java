package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.data.model.BedrijfStatus;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;

@XmlTransient
public class BedrijfTransferXml {
	private String adres;
	private Integer bedrijfId;
	private String bedrijfsNaam;
	//private Integer dummyId;
	private String huisnummer;
	private String huisnummerToevoeging;
	private boolean isActief;
	private boolean isBekendBijSbdr;
	private boolean isHoofd;
	private boolean isMeldingBijBedrijf;
	private boolean isMonitoringBijBedrijf;
	private boolean isRapportCreatedToday;
	private String kvkNummer;
	private String plaats;
	private String postcode;
	private String sbdrNummer;
	private String straat;
	private String subDossier;
	private String telefoonnummer;
	private String sbiOmschrijving;
	
	public BedrijfTransferXml() {

	}

	public BedrijfTransferXml(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, String telefoonnummer, String sbiOmschrijving) {
		//this.dummyId = 
		this.bedrijfId = bedrijfId;
		this.isActief = isActief;
		this.isHoofd = isHoofd;
		this.isMeldingBijBedrijf = false;
		this.isMonitoringBijBedrijf = false;
		this.isRapportCreatedToday = false;
		this.bedrijfsNaam = bedrijfsNaam;
		this.kvkNummer = kvkNummer;
		this.subDossier = subDossier;
		this.sbdrNummer = sbdrNummer;
		this.straat = straat;
		this.huisnummer = huisnummer;
		this.huisnummerToevoeging = huisnummerToevoeging;
		this.postcode = postcode;
		this.plaats = plaats;
		this.adres = straat;
		this.telefoonnummer = telefoonnummer;
		this.sbiOmschrijving = sbiOmschrijving;

		this.isBekendBijSbdr = bedrijfId != null;

		if(straat != null && huisnummer != null && huisnummerToevoeging != null)
			this.adres = straat + " " + huisnummer + huisnummerToevoeging;
		else if(straat != null && huisnummer != null) this.adres = straat + " " + huisnummer;
		else this.adres = straat;

		String sep = "";
		if(this.adres != null && this.adres.trim().length() > 0) sep = " ";
		if(postcode != null && plaats != null) this.adres += sep + postcode + " " + plaats;
		else if(postcode != null) this.adres += sep + postcode;
		else if(plaats != null) this.adres += sep + plaats;
	}

	@XmlElement
	public String getSbiOmschrijving() {
		return sbiOmschrijving;
	}

	public void setSbiOmschrijving(String sbiOmschrijving) {
		this.sbiOmschrijving = sbiOmschrijving;
	}

	@XmlElement
	public String getAdres() {
		return adres;
	}

	public void setAdres(String adres) {
		this.adres = adres;
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
	public String getBedrijfsNaam() {
		return bedrijfsNaam;
	}

	public void setBedrijfsNaam(String bedrijfsNaam) {
		this.bedrijfsNaam = bedrijfsNaam;
	}

//	@XmlElement
//	public Integer getDummyId() {
//		return dummyId;
//	}
//
//	public void setDummyId(Integer dummyId) {
//		this.dummyId = dummyId;
//	}

	@XmlElement
	public String getHuisnummer() {
		return huisnummer;
	}

	public void setHuisnummer(String huisnummer) {
		this.huisnummer = huisnummer;
	}

	@XmlElement
	public String getHuisnummerToevoeging() {
		return huisnummerToevoeging;
	}

	public void setHuisnummerToevoeging(String huisnummerToevoeging) {
		this.huisnummerToevoeging = huisnummerToevoeging;
	}

	@XmlElement
	public String getKvkNummer() {
		return kvkNummer;
	}

	public void setKvkNummer(String kvkNummer) {
		this.kvkNummer = kvkNummer;
	}

	@XmlElement
	public String getPlaats() {
		return plaats;
	}

	public void setPlaats(String plaats) {
		this.plaats = plaats;
	}

	@XmlElement
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@XmlElement
	public String getSbdrNummer() {
		return sbdrNummer;
	}

	public void setSbdrNummer(String sbdrNummer) {
		this.sbdrNummer = sbdrNummer;
	}

	@XmlElement
	public String getStraat() {
		return straat;
	}

	public void setStraat(String straat) {
		this.straat = straat;
	}

	@XmlElement
	public String getSubDossier() {
		return subDossier;
	}

	public void setSubDossier(String subDossier) {
		this.subDossier = subDossier;
	}

	@XmlElement
	public String getTelefoonnummer() {
		return telefoonnummer;
	}

	public void setTelefoonnummer(String telefoonnummer) {
		this.telefoonnummer = telefoonnummer;
	}


	@XmlElement
	public boolean isActief() {
		return isActief;
	}

	public void setActief(boolean isActief) {
		this.isActief = isActief;
	}

	@XmlElement
	public boolean isBekendBijSbdr() {
		return isBekendBijSbdr;
	}

	public void setBekendBijSbdr(boolean isBekendBijSbdr) {
		this.isBekendBijSbdr = isBekendBijSbdr;
	}

	@XmlElement
	public boolean isHoofd() {
		return isHoofd;
	}

	public void setHoofd(boolean isHoofd) {
		this.isHoofd = isHoofd;
	}

	@XmlElement
	public boolean isMeldingBijBedrijf() {
		return isMeldingBijBedrijf;
	}

	public void setMeldingBijBedrijf(boolean isMeldingBijBedrijf) {
		this.isMeldingBijBedrijf = isMeldingBijBedrijf;
	}

	@XmlElement
	public boolean isMonitoringBijBedrijf() {
		return isMonitoringBijBedrijf;
	}

	public void setMonitoringBijBedrijf(boolean isMonitoringBijBedrijf) {
		this.isMonitoringBijBedrijf = isMonitoringBijBedrijf;
	}

	@XmlElement
	public boolean isRapportCreatedToday() {
		return isRapportCreatedToday;
	}

	public void setRapportCreatedToday(boolean isRapportCreatedToday) {
		this.isRapportCreatedToday = isRapportCreatedToday;
	}


}
