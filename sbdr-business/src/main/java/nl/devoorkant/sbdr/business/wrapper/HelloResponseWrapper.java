package nl.devoorkant.sbdr.business.wrapper;

import nl.devoorkant.sbdr.business.util.ConvertUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloResponseWrapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(HelloResponseWrapper.class);
	
	private String status = null;
	private String information = null;
	
	
	
	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getInformation() {
		return information;
	}



	public void setInformation(String information) {
		this.information = information;
	}



	/**
	 * Gets the whole object structure as an XML
	 */
	public String toString() {
		try {
			return new String(ConvertUtil.convertToXML(this), "UTF-8");
		} catch (Exception e) {
			LOGGER.error("Conversion failed", e);
		}
		return null;
	}	
}
