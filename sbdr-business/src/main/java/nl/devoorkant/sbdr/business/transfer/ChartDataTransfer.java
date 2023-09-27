package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class ChartDataTransfer {
	private Long datum;
	private int aantal;
	private String label;
	
	public ChartDataTransfer() {
		
	}
	
	public ChartDataTransfer(Date datum, int aantal, String label) {
		if (datum != null)
			this.datum = datum.getTime();
		else
			this.datum = null;
		this.label = label;
	}
	
	@XmlElement
	public Long getDatum() {
		return datum;
	}

	public void setDatum(Long datum) {
		this.datum = datum;
	}

	@XmlElement
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@XmlElement
	public int getAantal() {
		return aantal;
	}

	public void setAantal(int aantal) {
		this.aantal = aantal;
	}	
	
	
}
