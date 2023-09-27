package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MLoginData {
	@XmlElement public String mobileClientKey;
	@XmlElement public String username;
	@XmlElement public String password;
	@XmlElement public int cntLoginAttempt;
}
