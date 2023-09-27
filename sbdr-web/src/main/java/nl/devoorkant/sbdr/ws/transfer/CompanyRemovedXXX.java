package nl.devoorkant.sbdr.ws.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.business.transfer.BedrijfTransfer;
import nl.devoorkant.sbdr.business.transfer.DateAdapterOverview;

@XmlRootElement
public class CompanyRemovedXXX extends BedrijfTransfer {
	@XmlElement(name = "reference")
	private String reference = null;

	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	private Date dateCreated = null;
	
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	private Date dateRemoved = null;

	@XmlElement(name = "type")
	private String type = null;


	public CompanyRemovedXXX()
	{
		 
	}
	
	public CompanyRemovedXXX(Integer id, String name, String kvkNumber, String address, String reference, Date dateCreated, Date dateRemoved, String type)
	{
		//super(id, name, kvkNumber, address);
		this.dateCreated = dateCreated;
		this.dateRemoved = dateRemoved;
		this.type = type;
	}

	
	public Date getDateCreated()
	{
		return this.dateCreated;
	}
	
	public Date getDateRemoved()
	{
		return this.dateRemoved;
	}
	
	public String getType()
	{
		return this.type;
	}

}
