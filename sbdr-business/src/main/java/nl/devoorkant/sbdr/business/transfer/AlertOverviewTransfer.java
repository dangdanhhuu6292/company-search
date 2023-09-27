package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class AlertOverviewTransfer extends BedrijfTransferXml {
	private String adresStatus;
	private Integer alertId;
	private String alertType;
	private String supportType;
	private Date gewijzigd;
	private String indDoorEigenBedrijf;
	private String indFaillissement;
	private String indMonitoringBedrijf;
	private String indSurseance;
	private Integer meldingId;
	private String meldingStatus;
	private Integer monitoringId;
	private Integer supportId;
	private String referentieNummer;
	private String referentieNummerNoPrefix;
	
	public AlertOverviewTransfer() {
		super();
	}
	
	public AlertOverviewTransfer(Integer alertId, String alertType, String supportType, Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, Integer meldingId, Integer monitoringId, Integer supportId, String meldingStatus, Date gewijzigd, String indFaillissement, String indSurseance, String indDoorEigenBedrijf, String indMonitoringBedrijf, String adresStatus, String referentieNummer, String referentieNummerNoPrefix, String telefoonnummer) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);
		this.alertId = alertId;
		this.indDoorEigenBedrijf = indDoorEigenBedrijf;
		this.meldingStatus = meldingStatus;
		this.gewijzigd = gewijzigd;
		this.indFaillissement =indFaillissement;
		this.indSurseance = indSurseance;
		this.adresStatus = adresStatus;
		this.indMonitoringBedrijf = indMonitoringBedrijf;
		this.meldingId = meldingId;
		this.monitoringId = monitoringId;
		this.supportId = supportId;
		this.referentieNummer = referentieNummer;
		this.alertType = alertType;
		this.supportType = supportType;
		this.referentieNummerNoPrefix = referentieNummerNoPrefix;
	}

	@XmlElement
	public String getAdresStatus() {
		return adresStatus;
	}

	public void setAdresStatus(String adresStatus) {
		this.adresStatus = adresStatus;
	}

	@XmlElement
	public String getAlert() {
		return meldingStatus;
	}

	public void setAlert(String alert) {
		this.meldingStatus = alert;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getAlertId() {
		return alertId;
	}

	public void setAlertId(Integer alertId) {
		this.alertId = alertId;
	}

	@XmlElement
	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	public Date getGewijzigd() {
		return gewijzigd;
	}

	public void setGewijzigd(Date gewijzigd) {
		this.gewijzigd = gewijzigd;
	}

	@XmlElement
	public String getIndDoorEigenBedrijf() {
		return indDoorEigenBedrijf;
	}

	public void setIndDoorEigenBedrijf(String indDoorEigenBedrijf) {
		this.indDoorEigenBedrijf = indDoorEigenBedrijf;
	}

	@XmlElement
	public String getIndFaillissement() {
		return indFaillissement;
	}

	public void setIndFaillissement(String indFaillissement) {
		this.indFaillissement = indFaillissement;
	}

	@XmlElement
	public String getIndMonitoringBedrijf() {
		return indMonitoringBedrijf;
	}

	public void setIndMonitoringBedrijf(String indMonitoringBedrijf) {
		this.indMonitoringBedrijf = indMonitoringBedrijf;
	}

	@XmlElement
	public String getIndSurseance() {
		return indSurseance;
	}

	public void setIndSurseance(String indSurseance) {
		this.indSurseance = indSurseance;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getMeldingId() {
		return meldingId;
	}

	public void setMeldingId(Integer meldingId) {
		this.meldingId = meldingId;
	}

	@XmlElement
	public String getMeldingStatus() {
		return meldingStatus;
	}

	public void setMeldingStatus(String meldingStatus) {
		this.meldingStatus = meldingStatus;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getMonitoringId() {
		return monitoringId;
	}

	public void setMonitoringId(Integer monitoringId) {
		this.monitoringId = monitoringId;
	}

	@XmlElement
	public String getReferentieNummer() {
		return referentieNummer;
	}

	public void setReferentieNummer(String referentieNummer) {
		this.referentieNummer = referentieNummer;
	}

	@XmlElement
	public String getReferentieNummerNoPrefix() {
		return referentieNummerNoPrefix;
	}

	public void setReferentieNummerNoPrefix(String referentieNummerNoPrefix) {
		this.referentieNummerNoPrefix = referentieNummerNoPrefix;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getSupportId() {
		return supportId;
	}

	public void setSupportId(Integer supportId) {
		this.supportId = supportId;
	}

	public String getSupportType() {
		return supportType;
	}

	public void setSupportType(String supportType) {
		this.supportType = supportType;
	}

	
}