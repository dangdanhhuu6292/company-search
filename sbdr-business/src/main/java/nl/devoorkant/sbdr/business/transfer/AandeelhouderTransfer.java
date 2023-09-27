package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;

public class AandeelhouderTransfer {
	private String naam;
	private String adres;
	private String postcodeplaats;
	private Double aandelenpct;
	
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
	@XmlElement
	public String getPostcodeplaats() {
		return postcodeplaats;
	}
	public void setPostcodeplaats(String postcodeplaats) {
		this.postcodeplaats = postcodeplaats;
	}
	@XmlElement
	public Double getAandelenpct() {
		return aandelenpct;
	}
	public void setAandelenpct(Double aandelenpct) {
		this.aandelenpct = aandelenpct;
	}
	
	
}
