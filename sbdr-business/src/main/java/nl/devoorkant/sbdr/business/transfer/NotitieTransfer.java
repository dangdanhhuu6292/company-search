package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Notitie;

@XmlRootElement
public class NotitieTransfer {
	private Integer notitieId = null;
	private Integer gebruikerId = null;
	private Integer meldingId = null;
	private Date datum = null;
	private String notitieType;
	private String notitie;	
	/* Notification fields for check */
	private Integer bedrijfIdGerapporteerd = null;
	
	@XmlElement(name = "notitieId", type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)	
	public Integer getNotitieId() {
		return notitieId;
	}

	public void setContactMomentId(Integer notitieId) {
		this.notitieId = notitieId;
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
	
	@XmlJavaTypeAdapter(value = DateAdapter.class, type = Date.class)
	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}
	
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
