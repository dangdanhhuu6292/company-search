package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class GebruikerBedrijfTransfer {
    private Integer bedrijfId;
    private String kvKnummer;
    private String bedrijfsNaam;
    private boolean isManaged = false;
    private boolean isCurrentBedrijf = false;
    
    public GebruikerBedrijfTransfer() {
    	
    }
    
    public GebruikerBedrijfTransfer(Integer bedrijfId, String KvKnummer, String bedrijfsNaam,  boolean isManaged, boolean isCurrentBedrijf) {
    	this.bedrijfId = bedrijfId;
    	this.kvKnummer = KvKnummer;
    	this.bedrijfsNaam = bedrijfsNaam;
    	this.isManaged = isManaged;
    	this.isCurrentBedrijf = isCurrentBedrijf;
    }
    
	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)    
	public Integer getBedrijfId() {
		return bedrijfId;
	}
	public void setBedrijfId(Integer bedrijfId) {
		this.bedrijfId = bedrijfId;
	}
	
	@XmlElement	
	public String getKvKnummer() {
		return kvKnummer;
	}
	public void setKvKnummer(String kvKnummer) {
		this.kvKnummer = kvKnummer;
	}
	
	@XmlElement
	public String getBedrijfsNaam() {
		return bedrijfsNaam;
	}
	
	public void setBedrijfsNaam(String bedrijfsNaam) {
		this.bedrijfsNaam = bedrijfsNaam;
	}
	
	@XmlElement	
	public boolean isManaged() {
		return isManaged;
	}
	public void setManaged(boolean isManaged) {
		this.isManaged = isManaged;
	}

	@XmlElement
	public boolean isCurrentBedrijf() {
		return isCurrentBedrijf;
	}

	public void setCurrentBedrijf(boolean isCurrentBedrijf) {
		this.isCurrentBedrijf = isCurrentBedrijf;
	}
	
	@XmlElement
	public String getDescription() {
		return getBedrijfsNaam() + " (" + getKvKnummer() + ")" + (isManaged ? ", managed" : "") ;
	}
}
