package nl.devoorkant.sbdr.business.transfer;

import nl.devoorkant.sbdr.business.transfer.DateAdapterOverview;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement
public class SupportBestandTransfer {
	private Date datumUpload;
	private boolean gearchiveerd;
	private String oorspronkelijkBestandsNaam;
	private String referentieNummer;
	private Integer supportBestandId;
	private Integer supportId;
	private String volledigPad;

	public SupportBestandTransfer() {

	}

	public SupportBestandTransfer(Date datumUpload, boolean gearchiveerd, String oorspronkelijkBestandsNaam, String referentieNummer, Integer supportBestandId, Integer supportId, String volledigPad) {
		this.gearchiveerd = gearchiveerd;
		this.oorspronkelijkBestandsNaam = oorspronkelijkBestandsNaam;
		this.referentieNummer = referentieNummer;
		this.supportBestandId = supportBestandId;
		this.supportId = supportId;
		this.volledigPad = volledigPad;
	}

	//<editor-fold desc="Getter methods">

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumUpload() {
		return datumUpload;
	}

	@XmlElement
	public String getOorspronkelijkBestandsNaam() {
		return oorspronkelijkBestandsNaam;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getSupportBestandId() {
		return supportBestandId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getSupportId() {
		return supportId;
	}

	@XmlElement
	public String getVolledigPad() {
		return volledigPad;
	}

	@XmlElement
	public boolean isGearchiveerd() {
		return gearchiveerd;
	}

	//</editor-fold>

	//<editor-fold desc="Setter methods">

	public void setDatumUpload(Date datumUpload) {
		this.datumUpload = datumUpload;
	}

	public void setGearchiveerd(boolean gearchiveerd) {
		this.gearchiveerd = gearchiveerd;
	}

	public void setOorspronkelijkBestandsNaam(String oorspronkelijkBestandsNaam) {
		this.oorspronkelijkBestandsNaam = oorspronkelijkBestandsNaam;
	}

	public void setSupportBestandId(Integer supportBestandId) {
		this.supportBestandId = supportBestandId;
	}

	public void setSupportId(Integer supportId) {
		this.supportId = supportId;
	}

	public void setVolledigPad(String volledigPad) {
		this.volledigPad = volledigPad;
	}

	//</editor-fold>
}
