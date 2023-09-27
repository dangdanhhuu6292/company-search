package nl.devoorkant.sbdr.ws.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.business.transfer.BedrijfTransfer;
import nl.devoorkant.sbdr.business.transfer.DateAdapterOverview;

@XmlRootElement
public class CompanyAlertXXX extends BedrijfTransfer {
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	private Date dateModified = null;

	@XmlElement(name = "alert")
	private String alert = null;


	public CompanyAlertXXX()
	{
		 
	}
	
	public CompanyAlertXXX(Integer id, String name, String kvkNumber, String address, Date dateModified, String alert)
	{
		//super(id, name, kvkNumber, address);
		this.dateModified = dateModified;
		this.alert = alert;
	}

	
	public Date getDateModified()
	{
		return this.dateModified;
	}
	
	public String getAlert()
	{
		return this.alert;
	}

}
