package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Ticket {
	private String categorie;
	private String referentieNummer;
	private String text;
	
	public Ticket() {
		
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public String getReferentieNummer() {
		return referentieNummer;
	}

	public void setReferentieNummer(String referentieNummer) {
		this.referentieNummer = referentieNummer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
