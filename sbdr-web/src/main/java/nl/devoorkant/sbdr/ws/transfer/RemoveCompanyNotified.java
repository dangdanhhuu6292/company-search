package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RemoveCompanyNotified extends CompanyNotified {
	@XmlElement public String reden;
	@XmlElement public String opmerking;
}
