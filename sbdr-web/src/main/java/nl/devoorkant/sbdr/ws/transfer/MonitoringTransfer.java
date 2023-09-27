package nl.devoorkant.sbdr.ws.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.business.transfer.BedrijfTransfer;
import nl.devoorkant.sbdr.business.transfer.DateAdapterOverview;
import nl.devoorkant.sbdr.business.transfer.IdentifierAdapter;

@XmlRootElement
public class MonitoringTransfer extends BedrijfTransfer {
	private Integer bedrijfIdGerapporteerd = null;
	
	private Date datumAangemaakt = null;

	private Date datumGewijzigd = null;		

	public MonitoringTransfer()
	{
		 
	}
	
	public MonitoringTransfer(Integer id, boolean isBedrijfActief, boolean isHoofd, String name, String kvkNumber, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, Date datumAangemaakt, Date datumGewijzigd, Integer bedrijfIdGerapporteerd, String telefoonnummer)
	{
		super(id, isBedrijfActief, isHoofd, name, kvkNumber, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);
		this.datumGewijzigd = datumGewijzigd;	
		this.datumAangemaakt = datumAangemaakt;
		this.bedrijfIdGerapporteerd = bedrijfIdGerapporteerd;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	public Date getDatumAangemaakt()
	{
		return this.datumAangemaakt;
	}
	
	public void setDatumAangemaakt(Date datumAangemaakt) {
		this.datumAangemaakt = datumAangemaakt;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	public Date getDatumGewijzigd()
	{
		return this.datumGewijzigd;
	}
	
	public void setDatumGewijzigd(Date datumGewijzigd) {
		this.datumGewijzigd = datumGewijzigd;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getBedrijfIdGerapporteerd() {
		return bedrijfIdGerapporteerd;
	}
	
	public void setBedrijfIdGerapporteerd(Integer bedrijfIdGerapporteerd) {
		this.bedrijfIdGerapporteerd =  bedrijfIdGerapporteerd;
	}		
}
