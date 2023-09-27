package nl.devoorkant.sbdr.ws.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.business.transfer.BedrijfTransfer;
import nl.devoorkant.sbdr.business.transfer.DateAdapterOverview;

@XmlRootElement
public class NotificationStatusXXX {
	@XmlElement (name = "company")
	BedrijfTransfer company = null;
	
	@XmlElement(name = "status")
	private String status = null;
	
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	private Date dateCreated = null;
	
	public NotificationStatusXXX()
	{
		 
	}
	
	public NotificationStatusXXX(BedrijfTransfer company, String status, Date dateCreated)
	{
		this.company = company;
		this.dateCreated = dateCreated;
		this.status = status;
	}
	
	public BedrijfTransfer getCompany()
	{
		return this.company;
	}
	
	public Date getDateCreated()
	{
		return this.dateCreated;
	}
	
	public String getStatus()
	{
		return this.status;
	}

}
