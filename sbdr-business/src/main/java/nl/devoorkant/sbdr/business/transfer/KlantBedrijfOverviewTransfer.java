package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement
public class KlantBedrijfOverviewTransfer extends BedrijfTransferXml {
	private String activationCode;
	private boolean adresOk;
	private String briefStatus;
	private String briefStatusDesc;
	private String klantGebruikersNaam;
	private Integer klantId;
	private String klantstatus;
	private String klantstatusCode;
	private Date datumAangemaakt;
	private Date wijziging;

	public KlantBedrijfOverviewTransfer() {
		super();
	}

	public KlantBedrijfOverviewTransfer(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, Date wijziging, Integer klantId, String klantGebruikersNaam, String activationCode, String klantstatus, String klantstatusCode, boolean isAdresOk, String telefoonnummer, String briefStatus, String briefStatusDesc, Date datumAangemaakt) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);
		this.wijziging = wijziging;
		this.klantId = klantId;
		this.klantGebruikersNaam = klantGebruikersNaam;
		this.activationCode = activationCode;
		this.klantstatus = klantstatus;
		this.klantstatusCode = klantstatusCode;
		this.setAdresOk(isAdresOk);
		this.briefStatus = briefStatus;
		this.briefStatusDesc = briefStatusDesc;
		this.datumAangemaakt = datumAangemaakt;
	}

	@XmlElement
	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	@XmlElement
	public String getBriefStatus() {
		return briefStatus;
	}

	public void setBriefStatus(String briefStatus) {
		this.briefStatus = briefStatus;
	}

	@XmlElement
	public String getBriefStatusDesc() {
		return briefStatusDesc;
	}

	public void setBriefStatusDesc(String briefStatusDesc) {
		this.briefStatusDesc = briefStatusDesc;
	}

	@XmlElement
	public String getKlantGebruikersNaam() {
		return klantGebruikersNaam;
	}

	public void setKlantGebruikersNaam(String klantGebruikersNaam) {
		this.klantGebruikersNaam = klantGebruikersNaam;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type=String.class)
	public Integer getKlantId() {
		return klantId;
	}

	public void setKlantId(Integer klantId) {
		this.klantId = klantId;
	}

	@XmlElement
	public String getKlantstatus() {
		return klantstatus;
	}

	public void setKlantstatus(String klantstatus) {
		this.klantstatus = klantstatus;
	}

	@XmlElement
	public String getKlantstatusCode() {
		return klantstatusCode;
	}

	public void setKlantstatusCode(String klantstatusCode) {
		this.klantstatusCode = klantstatusCode;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getWijziging() {
		return wijziging;
	}

	public void setWijziging(Date wijziging) {
		this.wijziging = wijziging;
	}

	@XmlElement
	public boolean isAdresOk() {
		return adresOk;
	}

	public void setAdresOk(boolean adresOk) {
		this.adresOk = adresOk;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)	
	public Date getDatumAangemaakt() {
		return datumAangemaakt;
	}

	public void setDatumAangemaakt(Date datumAangemaakt) {
		this.datumAangemaakt = datumAangemaakt;
	}
	
	
}
