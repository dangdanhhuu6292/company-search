package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResetPasswordData {
	@XmlElement 
	public String activationId;
	
	@XmlElement
	public String userId;
	
	@XmlElement 
	public String newPassword;		
	
	@XmlElement
	public String bedrijfId;
}
