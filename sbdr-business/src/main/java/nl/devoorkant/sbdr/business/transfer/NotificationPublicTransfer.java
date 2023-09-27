package nl.devoorkant.sbdr.business.transfer;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class NotificationPublicTransfer {

	private String id = null;
	private Date date = null;
	private BigDecimal amount = null;
	private boolean open = false;
	private Integer days = null;
	
	public NotificationPublicTransfer(){

	}

	@XmlElement
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}


	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapter.class, type = Date.class)
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}


	@XmlElement(name = "amount")
	@XmlJavaTypeAdapter(value = MoneyAdapter.class, type = BigDecimal.class)
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	@XmlElement
	public boolean getOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	

	@XmlElement
	public Integer getDays() {
		return days;
	}
	
	public void setDays(Integer days) {
		this.days = days;
	}	
}
