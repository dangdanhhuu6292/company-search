package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;

public class CuratorTransfer {
	private String naam;
	private String adres;
	
	@XmlElement
	public String getNaam() {
		return naam;
	}
	public void setNaam(String naam) {
		this.naam = naam;
	}
	
	@XmlElement	
	public String getAdres() {
		return adres;
	}
	public void setAdres(String adres) {
		this.adres = adres;
	}
	
	
}
