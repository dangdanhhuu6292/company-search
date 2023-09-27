package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;

public class WebsiteparamTransfer {
	//private Date datum;
	private String storingen;
	private Integer startupsYtd;
	private Integer startupsWeek;
	private Integer vermeldingenYtd;
	private Integer vermeldingenWeek;
	
	public WebsiteparamTransfer() {
		
	}
	
	public WebsiteparamTransfer(String storingen, Integer startupsYtd, Integer startupsWeek, Integer vermeldingenYtd, Integer vermeldingenWeek) {
		//this.datum = datum;
		this.storingen = storingen;
		this.startupsYtd = startupsYtd;
		this.startupsWeek = startupsWeek;
		this.vermeldingenYtd = vermeldingenYtd;
		this.vermeldingenWeek = vermeldingenWeek;
	}

//	@XmlElement
//	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)	
//	public Date getDatum() {
//		return datum;
//	}
//
//	public void setDatum(Date datum) {
//		this.datum = datum;
//	}
	
	@XmlElement
	public String getStoringen() {
		return storingen;
	}

	public void setStoringen(String storingen) {
		this.storingen = storingen;
	}

	@XmlElement
	public Integer getStartupsYtd() {
		return startupsYtd;
	}

	public void setStartupsYtd(Integer startupsYtd) {
		this.startupsYtd = startupsYtd;
	}

	@XmlElement
	public Integer getStartupsWeek() {
		return startupsWeek;
	}

	public void setStartupsWeek(Integer startupsWeek) {
		this.startupsWeek = startupsWeek;
	}
	
	@XmlElement
	public Integer getVermeldingenYtd() {
		return vermeldingenYtd;
	}

	public void setVermeldingenYtd(Integer vermeldingenYtd) {
		this.vermeldingenYtd = vermeldingenYtd;
	}

	@XmlElement
	public Integer getVermeldingenWeek() {
		return vermeldingenWeek;
	}

	public void setVermeldingenWeek(Integer vermeldingenWeek) {
		this.vermeldingenWeek = vermeldingenWeek;
	}
}
