package nl.devoorkant.sbdr.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataLast24h {
	private Integer hourOrder;
	private Short hourNr;
	private String hourDesc;
	private Long aantal;
	
	public DataLast24h() {
		
	}

	@XmlElement	
	public Integer getHourOrder() {
		return hourOrder;
	}

	public void setHourOrder(Integer hourOrder) {
		this.hourOrder = hourOrder;
	}

	@XmlElement
	public Short getHourNr() {
		return hourNr;
	}

	public void setHourNr(Short hourNr) {
		this.hourNr = hourNr;
	}

	@XmlElement
	public String getHourDesc() {
		return hourDesc;
	}

	public void setHourDesc(String hourDesc) {
		this.hourDesc = hourDesc;
	}

	@XmlElement
	public Long getAantal() {
		return aantal;
	}

	public void setAantal(Long aantal) {
		this.aantal = aantal;
	}
	
}
