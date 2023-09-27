package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VerifyRecaptcha {
	@XmlElement public String ipaddress;
	@XmlElement public String response;
	@XmlElement public String challenge;
}
