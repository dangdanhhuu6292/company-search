package nl.devoorkant.sbdr.business.transfer;

import nl.devoorkant.sbdr.business.util.ESupportReden;
import nl.devoorkant.sbdr.business.util.ESupportStatus;
import nl.devoorkant.sbdr.business.util.ESupportType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement
public class SupportTransfer {
	private String bericht = null;
	private BedrijfTransfer bestemdVoorBedrijf = null;
	private Boolean betwistBezwaar = null;
	private Date datumAangemaakt = null;
	private Date datumUpdate = null;
	private GebruikerTransfer gebruiker = null;
	private Integer geslotenDoorGebruikerId = null;
	private MeldingTransfer melding = null;
	private String meldingBedrijfsNaam = null;
	private String meldingFactuurNr = null;
	private String referentieIntern = null;
	private String referentieNummer = null;
	private SupportBestandTransfer[] supportBestanden = null;
	private Integer supportChildId = null;
	private Integer supportId = null;
	private Integer supportParentId = null;
	private String supportReden = null;
	private String supportRedenDesc = null;
	private String supportStatus = null;
	private String supportStatusDesc = null;
	private String supportType = null;
	private String supportTypeDesc = null;

	public SupportTransfer() {

	}

	public SupportTransfer(String bericht, Date datumAangemaakt, Date datumUpdate, GebruikerTransfer gebruiker, Integer geslotenDoorGebruikerId, MeldingTransfer melding, Boolean betwistBezwaar, String referentieIntern, String referentieNummer, Integer supportId, ESupportStatus supportStatus, ESupportReden supportReden, Integer supportParentId, ESupportType supportType, Integer supportChildId, BedrijfTransfer bestemdVoorBedrijf, SupportBestandTransfer[] supportBestanden, String meldingBedrijfsNaam, String meldingFactuurNr) {
		this.bericht = bericht;
		this.datumAangemaakt = datumAangemaakt;
		this.datumUpdate = datumUpdate;
		this.gebruiker = gebruiker;
		// remove user details if BEZWAAR to hide user name
		if (supportType.equals(ESupportType.BEZWAAR)) {
			this.gebruiker.setNaam("-");
			this.gebruiker.setVoorname("-");
			this.gebruiker.setTelefoonNummer("-");		
		}			
		this.geslotenDoorGebruikerId = geslotenDoorGebruikerId;
		this.melding = melding;
		this.betwistBezwaar = betwistBezwaar;
		this.referentieIntern = referentieIntern;
		this.referentieNummer = referentieNummer;
		this.supportChildId = supportChildId;
		this.supportId = supportId;
		this.supportParentId = supportParentId;
		this.supportStatus = supportStatus.getCode();
		this.supportStatusDesc = supportStatus.getOmschrijving();
		this.supportType = supportType.getCode();
		this.supportTypeDesc = supportType.getOmschrijving();
		this.bestemdVoorBedrijf = bestemdVoorBedrijf;
		this.supportBestanden = supportBestanden;
		this.meldingBedrijfsNaam = meldingBedrijfsNaam;
		this.meldingFactuurNr = meldingFactuurNr;

		if(supportReden != null) {
			this.supportReden = supportReden.getCode();
			this.supportRedenDesc = supportReden.getOmschrijving();
		}
	}

	public SupportTransfer(String bericht, Date datumAangemaakt, Date datumUpdate, GebruikerTransfer gebruiker, Integer geslotenDoorGebruikerId, MeldingTransfer melding, Boolean betwistBezwaar, String referentieIntern, String referentieNummer, Integer supportId, ESupportStatus supportStatus, ESupportReden supportReden, Integer supportParentId, ESupportType supportType, Integer supportChildId, BedrijfTransfer bestemdVoorBedrijf, SupportBestandTransfer[] supportBestanden) {
		this.bericht = bericht;
		this.datumAangemaakt = datumAangemaakt;
		this.datumUpdate = datumUpdate;
		this.gebruiker = gebruiker;
		// remove user details if BEZWAAR to hide user name
		if (supportType.equals(ESupportType.BEZWAAR)) {
			this.gebruiker.setNaam("-");
			this.gebruiker.setVoorname("-");
			this.gebruiker.setTelefoonNummer("-");		
		}			
		this.geslotenDoorGebruikerId = geslotenDoorGebruikerId;
		this.melding = melding;
		this.betwistBezwaar = betwistBezwaar;
		this.referentieIntern = referentieIntern;
		this.referentieNummer = referentieNummer;
		this.supportChildId = supportChildId;
		this.supportId = supportId;
		this.supportParentId = supportParentId;
		this.supportStatus = supportStatus.getCode();
		this.supportStatusDesc = supportStatus.getOmschrijving();
		this.supportType = supportType.getCode();
		this.supportTypeDesc = supportType.getOmschrijving();
		this.bestemdVoorBedrijf = bestemdVoorBedrijf;
		this.supportBestanden = supportBestanden;

		if(supportReden != null) {
			this.supportReden = supportReden.getCode();
			this.supportRedenDesc = supportReden.getOmschrijving();
		}
	}

	@XmlElement
	public String getBericht() {
		return bericht;
	}

	public void setBericht(String bericht) {
		this.bericht = bericht;
	}

	@XmlElement
	public BedrijfTransfer getBestemdVoorBedrijf() {
		return bestemdVoorBedrijf;
	}

	public void setBestemdVoorBedrijf(BedrijfTransfer bestemdVoorBedrijf) {
		this.bestemdVoorBedrijf = bestemdVoorBedrijf;
	}

	@XmlElement
	public Boolean getBetwistBezwaar() {
		return betwistBezwaar;
	}

	public void setBetwistBezwaar(Boolean betwistBezwaar) {
		this.betwistBezwaar = betwistBezwaar;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
	public Date getDatumAangemaakt() {
		return datumAangemaakt;
	}

	public void setDatumAangemaakt(Date datumAangemaakt) {
		this.datumAangemaakt = datumAangemaakt;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
	public Date getDatumUpdate() {
		return datumUpdate;
	}

	public void setDatumUpdate(Date datumUpdate) {
		this.datumUpdate = datumUpdate;
	}

	@XmlElement
	public GebruikerTransfer getGebruiker() {
		return gebruiker;
	}

	public void setGebruiker(GebruikerTransfer gebruiker) {
		this.gebruiker = gebruiker;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGeslotenDoorGebruikerId() {
		return geslotenDoorGebruikerId;
	}

	public void setGeslotenDoorGebruikerId(Integer geslotenDoorGebruikerId) {
		this.geslotenDoorGebruikerId = geslotenDoorGebruikerId;
	}

	@XmlElement
	public MeldingTransfer getMelding() {
		return melding;
	}

	public void setMelding(MeldingTransfer melding) {
		this.melding = melding;
	}

	@XmlElement
	public String getMeldingBedrijfsNaam() {
		return meldingBedrijfsNaam;
	}

	public void setMeldingBedrijfsNaam(String meldingBedrijfsNaam) {
		this.meldingBedrijfsNaam = meldingBedrijfsNaam;
	}

	@XmlElement
	public String getMeldingFactuurNr() {
		return meldingFactuurNr;
	}

	public void setMeldingFactuurNr(String meldingFactuurNr) {
		this.meldingFactuurNr = meldingFactuurNr;
	}

	@XmlElement
	public String getReferentieIntern() {
		return referentieIntern;
	}

	public void setReferentieIntern(String referentieIntern) {
		this.referentieIntern = referentieIntern;
	}

	@XmlElement
	public String getReferentieNummer() {
		return referentieNummer;
	}

	public void setReferentieNummer(String referentieNummer) {
		this.referentieNummer = referentieNummer;
	}

	@XmlElement
	public SupportBestandTransfer[] getSupportBestanden() {
		return supportBestanden;
	}

	public void setSupportBestanden(SupportBestandTransfer[] supportBestanden) {
		this.supportBestanden = supportBestanden;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getSupportChildId() {
		return supportChildId;
	}

	public void setSupportChildId(Integer supportChildId) {
		this.supportChildId = supportChildId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getSupportId() {
		return supportId;
	}

	public void setSupportId(Integer supportId) {
		this.supportId = supportId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getSupportParentId() {
		return supportParentId;
	}

	public void setSupportParentId(Integer supportParentId) {
		this.supportParentId = supportParentId;
	}

	@XmlElement
	public String getSupportReden() {
		return supportReden;
	}

	public void setSupportReden(String supportReden) {
		this.supportReden = supportReden;
	}

	@XmlElement
	public String getSupportRedenDesc() {
		return supportRedenDesc;
	}

	public void setSupportRedenDesc(String supportRedenDesc) {
		this.supportRedenDesc = supportRedenDesc;
	}

	@XmlElement
	public String getSupportStatus() {
		return supportStatus;
	}

	public void setSupportStatus(String supportStatus) {
		this.supportStatus = supportStatus;
	}

	@XmlElement
	public String getSupportStatusDesc() {
		return supportStatusDesc;
	}

	public void setSupportStatusDesc(String supportStatusDesc) {
		this.supportStatusDesc = supportStatusDesc;
	}

	@XmlElement
	public String getSupportType() {
		return supportType;
	}

	public void setSupportType(String supportType) {
		this.supportType = supportType;
	}

	@XmlElement
	public String getSupportTypeDesc() {
		return supportTypeDesc;
	}

	public void setSupportTypeDesc(String supportTypeDesc) {
		this.supportTypeDesc = supportTypeDesc;
	}
}
