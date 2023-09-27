package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginData {
	@XmlElement public String ipaddress;
    @XmlElement public String username;
    @XmlElement public String password;
    @XmlElement public String bedrijfId;
	@XmlElement public int cntLoginAttempt;
}
