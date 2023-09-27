package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChangePasswordTransfer {
	@XmlElement public Integer userId;
	@XmlElement public String userName;
	@XmlElement public String currentPassword;
	@XmlElement public String newPassword;
}
