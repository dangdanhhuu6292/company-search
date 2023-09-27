package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class ContactMomentTransfer {
	private Integer contactMomentId = null;
	private Integer gebruikerId = null;
	private Date datumContact = null;
	private Date datumContactTerug = null;
	private String contactWijze = null;
	private String contactGegevens = null;
	private String beantwoord = null;
	private String notitieIntern = null;
	private Boolean alert = null;
	/* Notitie fields */
	private String notitieType;
	private String notitie;	
	/* Notification fields for check */
	private Integer meldingId = null;
	private Integer bedrijfIdGerapporteerd = null;
	
	public ContactMomentTransfer() {
		
	}
	
	@XmlElement(name = "contactMomentId", type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)	
	public Integer getContactMomentId() {
		return contactMomentId;
	}

	public void setContactMomentId(Integer contactMomentId) {
		this.contactMomentId = contactMomentId;
	}
	
	@XmlElement(name = "gebruikerId", type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)	
	public Integer getGebruikerId() {
		return gebruikerId;
	}

	public void setGebruikerId(Integer gebruikerId) {
		this.gebruikerId = gebruikerId;
	}

	@XmlElement(name = "meldingId", type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)	
	public Integer getMeldingId() {
		return meldingId;
	}

	public void setMeldingId(Integer meldingId) {
		this.meldingId = meldingId;
	}
	
	@XmlElement(name = "bedrijfIdGerapporteerd", type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)	
	public Integer getBedrijfIdGerapporteerd() {
		return bedrijfIdGerapporteerd;
	}

	public void setBedrijfIdGerapporteerd(Integer bedrijfIdGerapporteerd) {
		this.bedrijfIdGerapporteerd = bedrijfIdGerapporteerd;
	}	

	@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
	public Date getDatumContact() {
		return datumContact;
	}

	public void setDatumContact(Date datumContact) {
		this.datumContact = datumContact;
	}

	@XmlJavaTypeAdapter(value = DateAdapter.class, type = Date.class)	
	public Date getDatumContactTerug() {
		return datumContactTerug;
	}

	public void setDatumContactTerug(Date datumContactTerug) {
		this.datumContactTerug = datumContactTerug;
	}

	@XmlElement
	public String getContactWijze() {
		return contactWijze;
	}

	public void setContactWijze(String contactWijze) {
		this.contactWijze = contactWijze;
	}
	
	@XmlElement
	public String getContactGegevens() {
		return contactGegevens;
	}

	public void setContactGegevens(String contactGegevens) {
		this.contactGegevens = contactGegevens;
	}

	@XmlElement
	public String getBeantwoord() {
		return beantwoord;
	}

	public void setBeantwoord(String beantwoord) {
		this.beantwoord = beantwoord;
	}

	@XmlElement
	public String getNotitieIntern() {
		return notitieIntern;
	}

	public void setNotitieIntern(String notitieIntern) {
		this.notitieIntern = notitieIntern;
	}
	
	@XmlElement
	public Boolean getAlert() {
		return alert;
	}

	public void setAlert(Boolean alert) {
		this.alert = alert;
	}

	/* Notitie fields */
	@XmlElement
	public String getNotitieType() {
		return notitieType;
	}

	public void setNotitieType(String notitieType) {
		this.notitieType = notitieType;
	}

	@XmlElement
	public String getNotitie() {
		return notitie;
	}

	public void setNotitie(String notitie) {
		this.notitie = notitie;
	}
	
	
}
