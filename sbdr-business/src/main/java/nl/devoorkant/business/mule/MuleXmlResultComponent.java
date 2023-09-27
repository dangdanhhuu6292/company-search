package nl.devoorkant.business.mule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuleXmlResultComponent {
	private static Logger LOGGER = LoggerFactory.getLogger(MuleXmlResultComponent.class);

	private String message;
	
	
	public void setXmlResult(String message) {
		LOGGER.debug("setXmlCheckResult: " + message);
		this.message = message;
	}
	
	public String getXmlResult() {
		LOGGER.debug("getXmlCheckResult: " + message);
		
		return message;
	}
	
	public void processXmlResult(String result) {
		LOGGER.debug("processXmlCheckResult: \n" + result);
	}

}
