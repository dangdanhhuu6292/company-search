package nl.devoorkant.business.mule;


import nl.devoorkant.sbdr.business.service.MeldingService;
import nl.devoorkant.sbdr.business.service.cir.InsolventieService;
import nl.devoorkant.sbdr.business.transfer.InsolventiePublicTransfer;
import nl.devoorkant.sbdr.business.transfer.NotificationPublicTransfer;
import nl.devoorkant.sbdr.business.transfer.NotificationsPublicTransfer;
import nl.devoorkant.sbdr.data.service.GebruikerDataService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MuleResultComponent {
	private static Logger LOGGER = LoggerFactory.getLogger(MuleResultComponent.class);

	@Value("${publicApiKey}")
	private String apiKey;
	
	@Autowired
	private GebruikerDataService gebruikerDataService;

	@Autowired
	private InsolventieService insolventieService;
	
	@Autowired
	private MeldingService meldingService;
	
	private String message;
	
	public void setEmailResult(String message) {
		LOGGER.debug("setEmailResult: " + message);
		this.message = message;
	}
	
	public void emailResultOk(String result, String gebruikerId, String emailType) {
		LOGGER.debug("OK getEmailResult: " + result + " gebruikerId: " + gebruikerId + " type: " + emailType);
		
		//return message;
	}
	
	public void emailResultError(String error, String gebruikerId, String emailType) {
		LOGGER.error("ERROR getEmailResult: " + error + " gebruikerId: " + gebruikerId + " type: " + emailType);
	}
	
	public void cirPublicDataResultError(String error) {
		LOGGER.error("ERROR getCirPublicDataResult: " + error);
	}
	
	public InsolventiePublicTransfer getCirPublicData() {
		return insolventieService.findPublicData();
	}

	public void notificationsPublicDataResultError(String error) {
		LOGGER.error("ERROR getCirPublicDataResult: " + error);
	}
	
	public NotificationsPublicTransfer getNotificationsPublicData(String key, Integer nrOfDays) {
		NotificationsPublicTransfer notifications = null;
		
		if (apiKey != null && key != null && apiKey.equals(key)) {	
			List<NotificationPublicTransfer> results = meldingService.findNotificationsPublicData(nrOfDays);
			
			if (results != null) {
				notifications = new NotificationsPublicTransfer();
				notifications.notifications = results;
			}
		}
		
		return notifications;
	}		
}
