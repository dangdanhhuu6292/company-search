package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GebruikerIdsTransfer {
	private String[] gebruikerIds;

	public GebruikerIdsTransfer() {
		
	}
	
	@XmlElement
	public String[] getGebruikerIds() {
		return gebruikerIds;
	}

	public void setGebruikerIds(String[] gebruikerIds) {
		this.gebruikerIds = gebruikerIds;
	}
	
	
}
