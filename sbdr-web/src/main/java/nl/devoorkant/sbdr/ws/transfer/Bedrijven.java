package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.devoorkant.sbdr.business.transfer.BedrijfTransfer;

@XmlRootElement
public class Bedrijven {
	@XmlElement (name = "totalItems")
	private Integer totalItems = null;

	@XmlElement(name = "companies", type = BedrijfTransfer.class)
	private BedrijfTransfer[] companies = null;


	public Bedrijven()
	{
		 
	}
	
	public Bedrijven(Integer totalItems, BedrijfTransfer[] companies)
	{
		this.totalItems = totalItems;
		this.companies = companies;
	}

	
	public Integer getTotalItems()
	{
		return this.totalItems;
	}
	
	public BedrijfTransfer[] getCompanies()
	{
		return this.companies;
	}

}
