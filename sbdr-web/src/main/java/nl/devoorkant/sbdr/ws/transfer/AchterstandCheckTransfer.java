package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
public class AchterstandCheckTransfer {
	private Integer noOfAchterstanden;
	private BigDecimal totalAmount;

	@XmlElement
	public Integer getNoOfAchterstanden() {
		return noOfAchterstanden;
	}

	public void setNoOfAchterstanden(Integer noOfAchterstanden) {
		this.noOfAchterstanden = noOfAchterstanden;
	}

	@XmlElement
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
}
