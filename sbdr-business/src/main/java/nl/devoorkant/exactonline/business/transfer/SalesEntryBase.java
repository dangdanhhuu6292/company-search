package nl.devoorkant.exactonline.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.business.transfer.ODataDateTimeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@JsonSerialize //(include=JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
public class SalesEntryBase {
	private String entryId;
	private String customer;
	private String description;
	private Date entryDate;
	private String invoiceNumber;
	private String journal; // sales journal code 'VerkoopBoek = 70?'
	private Integer reportingPeriod;
	private Integer reportingYear;
	private String paymentCondition; // payment condition code = incasso (IN?)

	/*
	 * The status of the entry. 10 = draft.  20 = open. New invoices get the status open by default. 50 = processed. 
	 */
	private Integer status;
	private String yourRef;
	
	public SalesEntryBase() {
		
	}

	@XmlElement(name="EntryID") 
	@JsonProperty("EntryID")
	public String getEntryId() {
		return entryId;
	}

	@JsonProperty("EntryID")
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	@XmlElement(name="Customer") 
	@JsonProperty("Customer")
	public String getCustomer() {
		return customer;
	}

	@JsonProperty("Customer")
	public void setCustomer(String customer) {
		this.customer = customer;
	}

	@XmlElement(name="Description") 
	@JsonProperty("Description")
	public String getDescription() {
		return description;
	}
	
	@JsonProperty("Description")
	public void setDescription(String description) {
		this.description = description;
	}	

	@XmlElement(name="EntryDate") 
	@JsonProperty("EntryDate")
	@XmlJavaTypeAdapter(value=ODataDateTimeAdapter.class, type=Date.class)		
	public Date getEntryDate() {
		return entryDate;
	}
	
	@JsonProperty("EntryDate")
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	@XmlElement(name="InvoiceNumber") 
	@JsonProperty("InvoiceNumber")
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	@JsonProperty("InvoiceNumber")
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	@XmlElement(name="Journal") 
	@JsonProperty("Journal")
	public String getJournal() {
		return journal;
	}

	@JsonProperty("Journal")
	public void setJournal(String journal) {
		this.journal = journal;
	}

	@XmlElement(name="ReportingPeriod") 
	@JsonProperty("ReportingPeriod")
	public Integer getReportingPeriod() {
		return reportingPeriod;
	}

	@JsonProperty("ReportingPeriod")
	public void setReportingPeriod(Integer reportingPeriod) {
		this.reportingPeriod = reportingPeriod;
	}

	@XmlElement(name="ReportingYear") 
	@JsonProperty("ReportingYear")
	public Integer getReportingYear() {
		return reportingYear;
	}

	@JsonProperty("ReportingYear")
	public void setReportingYear(Integer reportingYear) {
		this.reportingYear = reportingYear;
	}
	
	@XmlElement(name="PaymentCondition") 
	@JsonProperty("PaymentCondition")
	public String getPaymentCondition() {
		return paymentCondition;
	}

	@JsonProperty("PaymentCondition")
	public void setPaymentCondition(String paymentCondition) {
		this.paymentCondition = paymentCondition;
	}

	@XmlElement(name="Status") 
	@JsonProperty("Status")
	public Integer getStatus() {
		return status;
	}

	@JsonProperty("Status")
	public void setStatus(Integer status) {
		this.status = status;
	}

	@XmlElement(name="YourRef") 
	@JsonProperty("YourRef")
	public String getYourRef() {
		return yourRef;
	}

	@JsonProperty("YourRef")
	public void setYourRef(String yourRef) {
		this.yourRef = yourRef;
	}	
	
	
}
