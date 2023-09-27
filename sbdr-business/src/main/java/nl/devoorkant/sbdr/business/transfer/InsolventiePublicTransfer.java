package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InsolventiePublicTransfer {
	private Long nrOfFaillissementenJaar = null;
	private Long nrOfFaillissementenWeek = null;
	private Long nrOfSurseancesJaar = null;
	private Long nrOfSurseancesWeek = null;
	private Long nrOfStartupsJaar = null;
	private Long nrOfStartupsWeek = null;
	private Long nrOfVermeldingenJaar = null;
	private Long nrOfVermeldingenWeek = null;
	private String storingen = null;
	

	public InsolventiePublicTransfer(){

	}

	public InsolventiePublicTransfer(Long nrOfFaillissementenJaar, Long nrOfFaillissementenWeek, Long nrOfSurseancesJaar, Long nrOfSurseancesWeek, Long nrOfStartupsJaar, Long nrOfStartupsWeek, Long nrOfVermeldingenJaar, Long nrOfVermeldingenWeek, String storingen) {
		this.nrOfFaillissementenJaar = nrOfFaillissementenJaar == null ? 0 : nrOfFaillissementenJaar;
		this.nrOfFaillissementenWeek = nrOfFaillissementenWeek == null ? 0 : nrOfFaillissementenWeek;
		this.nrOfSurseancesJaar = nrOfSurseancesJaar == null ? 0 : nrOfSurseancesJaar;
		this.nrOfSurseancesWeek = nrOfSurseancesWeek == null ? 0 : nrOfSurseancesWeek;
		this.nrOfStartupsJaar = nrOfStartupsJaar == null ? 0 : nrOfStartupsJaar;
		this.nrOfStartupsWeek = nrOfStartupsWeek == null ? 0 : nrOfStartupsWeek;
		this.nrOfVermeldingenJaar = nrOfVermeldingenJaar == null ? 0 : nrOfVermeldingenJaar;
		this.nrOfVermeldingenWeek = nrOfVermeldingenWeek == null ? 0 : nrOfVermeldingenWeek;
		this.storingen = storingen;
	}

	@XmlElement
	public Long getNrOfFaillissementenJaar() {
		return nrOfFaillissementenJaar;
	}

	public void setNrOfFaillissementenJaar(Long nrOfFaillissementenJaar) {
		this.nrOfFaillissementenJaar = nrOfFaillissementenJaar;
	}

	@XmlElement
	public Long getNrOfFaillissementenWeek() {
		return nrOfFaillissementenWeek;
	}

	public void setNrOfFaillissementenWeek(Long nrOfFaillissementenWeek) {
		this.nrOfFaillissementenWeek = nrOfFaillissementenWeek;
	}

	@XmlElement
	public Long getNrOfSurseancesJaar() {
		return nrOfSurseancesJaar;
	}

	public void setNrOfSurseancesJaar(Long nrOfSurseancesJaar) {
		this.nrOfSurseancesJaar = nrOfSurseancesJaar;
	}

	@XmlElement
	public Long getNrOfSurseancesWeek() {
		return nrOfSurseancesWeek;
	}

	public void setNrOfSurseancesWeek(Long nrOfSurseancesWeek) {
		this.nrOfSurseancesWeek = nrOfSurseancesWeek;
	}

	@XmlElement
	public Long getNrOfStartupsJaar() {
		return nrOfStartupsJaar;
	}

	public void setNrOfStartupsJaar(Long nrOfStartupsJaar) {
		this.nrOfStartupsJaar = nrOfStartupsJaar;
	}

	@XmlElement
	public Long getNrOfStartupsWeek() {
		return nrOfStartupsWeek;
	}

	public void setNrOfStartupsWeek(Long nrOfStartupsWeek) {
		this.nrOfStartupsWeek = nrOfStartupsWeek;
	}
	
	@XmlElement
	public Long getNrOfVermeldingenJaar() {
		return nrOfVermeldingenJaar;
	}

	public void setNrOfVermeldingenJaar(Long nrOfVermeldingenJaar) {
		this.nrOfVermeldingenJaar = nrOfVermeldingenJaar;
	}

	@XmlElement
	public Long getNrOfVermeldingenWeek() {
		return nrOfVermeldingenWeek;
	}

	public void setNrOfVermeldingenWeek(Long nrOfVermeldingenWeek) {
		this.nrOfVermeldingenWeek = nrOfVermeldingenWeek;
	}

	@XmlElement
	public String getStoringen() {
		return storingen;
	}

	public void setStoringen(String storingen) {
		this.storingen = storingen;
	}


}
