package nl.devoorkant.business.mule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuleViesCheckResultComponent {
	private static Logger LOGGER = LoggerFactory.getLogger(MuleViesCheckResultComponent.class);

	private String message;
	
	
	public void setViesCheckResult(String message) {
		LOGGER.debug("setViesCheckResult: " + message);
		this.message = message;
	}
	
	public String getViesCheckResult() {
		LOGGER.debug("getViesCheckResult: " + message);
		
		return message;
	}
	
	public void processViesCheckResult(String result) {
		LOGGER.debug("processViesCheckResult: \n" + result);
	}

}
