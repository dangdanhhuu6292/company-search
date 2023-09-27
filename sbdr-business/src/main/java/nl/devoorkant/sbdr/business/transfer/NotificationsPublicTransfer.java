package nl.devoorkant.sbdr.business.transfer;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NotificationsPublicTransfer {
	
	@XmlElement
	public List<NotificationPublicTransfer> notifications;
	
	public NotificationsPublicTransfer() {
		
	}
}
