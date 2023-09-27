package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement
public class FactuurTransfer {
	private Integer factuurId = null;
	private BedrijfTransfer bedrijf = null;
	private Date datumFactuur = null;
	private BigDecimal bedrag = null;
	private Date datumAangemaakt = null;
	private Date datumVerwerktExactOnline = null;
	private String referentieNummer = null;
	private String salesEntryGuid = null;
	private String fileNaam = null;

	public FactuurTransfer(){

	}

	public FactuurTransfer(Integer factuurId, BedrijfTransfer bedrijf, Date datumFactuur, BigDecimal bedrag, Date datumAangemaakt, Date datumVerwerktExactOnline, String referentieNummer, String salesEntryGuid, String fileNaam){
		this.factuurId = factuurId;
		this.bedrijf = bedrijf;
		this.datumFactuur = datumFactuur;
		this.bedrag = bedrag;
		this.datumAangemaakt = datumAangemaakt;
		this.datumVerwerktExactOnline = datumVerwerktExactOnline;
		this.referentieNummer = referentieNummer;
		this.salesEntryGuid = salesEntryGuid;
		this.fileNaam = fileNaam;
	}

	//<editor-fold desc="Getter methods">

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getFactuurId() {
		return factuurId;
	}

	@XmlElement
	public BedrijfTransfer getBedrijf() {
		return bedrijf;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumFactuur() {
		return datumFactuur;
	}

	@XmlElement
	public BigDecimal getBedrag() {
		return bedrag;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumAangemaakt() {
		return datumAangemaakt;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumVerwerktExactOnline() {
		return datumVerwerktExactOnline;
	}

	@XmlElement
	public String getReferentieNummer() {
		return referentieNummer;
	}

	@XmlElement
	public String getSalesEntryGuid() {
		return salesEntryGuid;
	}

	@XmlElement
	public String getFileNaam() {
		return fileNaam;
	}

	//</editor-fold>

	//<editor-fold desc="Setter methods">

	public void setFactuurId(Integer factuurId) {
		this.factuurId = factuurId;
	}

	public void setBedrijf(BedrijfTransfer bedrijf) {
		this.bedrijf = bedrijf;
	}

	public void setDatumFactuur(Date datumFactuur) {
		this.datumFactuur = datumFactuur;
	}

	public void setBedrag(BigDecimal bedrag) {
		this.bedrag = bedrag;
	}

	public void setDatumAangemaakt(Date datumAangemaakt) {
		this.datumAangemaakt = datumAangemaakt;
	}

	public void setDatumVerwerktExactOnline(Date datumVerwerktExactOnline) {
		this.datumVerwerktExactOnline = datumVerwerktExactOnline;
	}

	public void setReferentieNummer(String referentieNummer) {
		this.referentieNummer = referentieNummer;
	}

	public void setSalesEntryGuid(String salesEntryGuid) {
		this.salesEntryGuid = salesEntryGuid;
	}

	public void setFileNaam(String fileNaam) {
		this.fileNaam = fileNaam;
	}

	//</editor-fold>
}