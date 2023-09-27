package nl.devoorkant.sbdr.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataStatusAantal {
	private String statusCode;
	private String status;
	private Long aantal;
	
	public DataStatusAantal() {
		
	}

	@XmlElement
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@XmlElement	
	public Long getAantal() {
		return aantal;
	}

	public void setAantal(Long aantal) {
		this.aantal = aantal;
	}

	@XmlElement	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
