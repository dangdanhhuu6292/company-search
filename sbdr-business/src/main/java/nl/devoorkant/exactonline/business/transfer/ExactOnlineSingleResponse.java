package nl.devoorkant.exactonline.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExactOnlineSingleResponse {
	private String uri = null;
	private String type = null;
	
	public ExactOnlineSingleResponse() {
		
	}

	@XmlElement
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@XmlElement
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
			
}
