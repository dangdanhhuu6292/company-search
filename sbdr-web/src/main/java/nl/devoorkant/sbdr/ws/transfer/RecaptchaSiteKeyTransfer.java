package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecaptchaSiteKeyTransfer {
	@XmlElement	public String recaptchaSiteKey;
	
}
