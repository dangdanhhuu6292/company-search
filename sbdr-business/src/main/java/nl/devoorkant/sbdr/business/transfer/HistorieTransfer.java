package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.business.util.EHistorieType;

public class HistorieTransfer {
	private Date datum;
	private String code;
	private String label;
	private String omschrijving;
	
	public HistorieTransfer() {
		
	}
	
	public HistorieTransfer(Date datum, EHistorieType historieType) {
		this.datum = datum;
		this.code = historieType.getCode();
		this.label = historieType.getType();
		this.omschrijving = historieType.getOmschrijving();
	}
	
	public HistorieTransfer(Date datum, EHistorieType historieType, String omschrijving) {
		this.datum = datum;
		this.code = historieType.getCode();
		this.label = historieType.getType();
		this.omschrijving = omschrijving;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)	
	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}
		
	@XmlElement
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@XmlElement
	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	@XmlElement
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}	
}
