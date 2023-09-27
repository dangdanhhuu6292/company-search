package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;

public class ActivatieCodeTransfer {
	private String userName = null;
	private String bedrijfId = null;
	private boolean isBedrijfManaged = false;
	
	public ActivatieCodeTransfer(boolean bedrijfManaged, String bedrijfId) {
		this.isBedrijfManaged = bedrijfManaged;
		this.bedrijfId = bedrijfId;
	}
	
	@XmlElement
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlElement
	public boolean isBedrijfManaged() {
		return isBedrijfManaged;
	}
	
	@XmlElement
	public String getBedrijfId() {
		return bedrijfId;
	}
}
