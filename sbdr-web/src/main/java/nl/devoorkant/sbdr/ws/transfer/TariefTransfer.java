package nl.devoorkant.sbdr.ws.transfer;

import nl.devoorkant.sbdr.business.transfer.MoneyAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;

@XmlRootElement
public class TariefTransfer {
	private BigDecimal tarief;

	public TariefTransfer(){

	}

	public TariefTransfer(BigDecimal tarief){
		this.tarief = tarief;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = MoneyAdapter.class, type = BigDecimal.class)
	public BigDecimal getTarief() {
		return tarief;
	}

	public void setTarief(BigDecimal tarief) {
		this.tarief = tarief;
	}
}
