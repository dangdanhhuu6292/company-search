package nl.devoorkant.exactonline.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
public class SalesEntry extends SalesEntryBase {
	private SalesEntryLine[] salesEntryLines;

	public SalesEntry() {
		super();
	}

	@XmlElement(name="SalesEntryLines") 
	@JsonProperty("SalesEntryLines")
	public SalesEntryLine[] getSalesEntryLines() {
		return salesEntryLines;
	}

	@JsonProperty("SalesEntryLines")
	public void setSalesEntryLines(SalesEntryLine[] salesEntryLines) {
		this.salesEntryLines = salesEntryLines;
	}

}
