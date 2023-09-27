package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;

public class OpmerkingenTransfer {
	private boolean isLink;
	private String omschrijving;
	private String type;
	private String kvkNr;

	public OpmerkingenTransfer() {

	}

	public OpmerkingenTransfer(String type, String omschrijving){
		this.type = type;
		this.omschrijving = omschrijving;
		this.isLink = false;
	}

	public OpmerkingenTransfer(String type, String omschrijving, boolean isLink, String kvkNr) {
		this.type = type;
		this.omschrijving = omschrijving;
		this.isLink = isLink;
		this.kvkNr = kvkNr;
	}

	@XmlElement
	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	@XmlElement
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement
	public boolean isLink() {
		return isLink;
	}

	public void setIsLink(boolean isLink) {
		this.isLink = isLink;
	}

	@XmlElement
	public String getKvkNr() {
		return kvkNr;
	}

	public void setKvkNr(String kvkNr) {
		this.kvkNr = kvkNr;
	}
}
