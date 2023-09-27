package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement
public class AdminAlertTransfer {
	private Integer alertId;
	private Date datum;
	private Integer meldingId;
	private String referentieNummer;
	private String referentieNummerNoPrefix;
	private SupportTransfer support;

	public AdminAlertTransfer(){

	}

	public AdminAlertTransfer(Integer aId, Date datum, Integer mId, String ref, String refNoPfx, SupportTransfer s){
		this.alertId = aId;
		this.datum = datum;
		this.referentieNummer = ref;
		this.meldingId = mId;
		this.support = s;
		this.referentieNummerNoPrefix = refNoPfx;
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
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
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

	@XmlElement
	public SupportTransfer getSupport() {
		return support;
	}

	public void setSupport(SupportTransfer support) {
		this.support = support;
	}
}