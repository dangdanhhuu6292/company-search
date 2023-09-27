package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExactOnlineAccessTransfer {
	@XmlElement public String result;

	public ExactOnlineAccessTransfer() {
		
	}
	
	public ExactOnlineAccessTransfer(String result) {
		this.result = result;
	}

}
