package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.devoorkant.sbdr.business.transfer.IdentifierAdapter;

@XmlRootElement
public class RemoveCompanyMonitoring {
	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer monitoringId;
	
	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer bedrijfId;
}
