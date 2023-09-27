package nl.devoorkant.sbdr.business.transfer;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.devoorkant.sbdr.data.DataLast24h;
import nl.devoorkant.sbdr.data.DataStatusAantal;

@XmlRootElement
public class AdminOverviewTransfer {
	
	List<DataStatusAantal> klantenPerStatus;
	List<DataStatusAantal> meldingenPerStatus;
	List<DataStatusAantal> monitoringPerStatus;
	long totaalAantalRapporten;
	long totaalAantalNieuweMonitoring; // 2 dagen;
	long totaalAantalNieuweRapporten; // 2 dagen;

	List<DataLast24h> activeKlantenLast24h;
	List<DataLast24h> activeMeldingenLast24h;
	List<DataLast24h> activeMonitoringLast24h;
	List<DataLast24h> rapportenLast24h;
	
	int nrOfGebruikersIngelogd;
	long totaalAantalGebruikers;
	long totaalAantalActivatedGebruikers;
	long totaalAantalNonActivatedGebruikers;
	long totaalAantalDeactivatedGebruikers;

	public AdminOverviewTransfer() {
		
	}
	
	@XmlElement
	public List<DataStatusAantal> getKlantenPerStatus() {
		return klantenPerStatus;
	}

	public void setKlantenPerStatus(List<DataStatusAantal> klantenPerStatus) {
		this.klantenPerStatus = klantenPerStatus;
	}

	@XmlElement	
	public List<DataStatusAantal> getMeldingenPerStatus() {
		return meldingenPerStatus;
	}

	public void setMeldingenPerStatus(List<DataStatusAantal> meldingenPerStatus) {
		this.meldingenPerStatus = meldingenPerStatus;
	}

	@XmlElement	
	public List<DataStatusAantal> getMonitoringPerStatus() {
		return monitoringPerStatus;
	}

	public void setMonitoringPerStatus(List<DataStatusAantal> monitoringPerStatus) {
		this.monitoringPerStatus = monitoringPerStatus;
	}

	@XmlElement	
	public List<DataLast24h> getActiveKlantenLast24h() {
		return activeKlantenLast24h;
	}

	public void setActiveKlantenLast24h(List<DataLast24h> klantenLast24h) {
		this.activeKlantenLast24h = klantenLast24h;
	}

	@XmlElement	
	public List<DataLast24h> getActiveMeldingenLast24h() {
		return activeMeldingenLast24h;
	}

	public void setActiveMeldingenLast24h(List<DataLast24h> meldingenLast24h) {
		this.activeMeldingenLast24h = meldingenLast24h;
	}

	@XmlElement	
	public List<DataLast24h> getActiveMonitoringLast24h() {
		return activeMonitoringLast24h;
	}

	public void setActiveMonitoringLast24h(List<DataLast24h> monitoringLast24h) {
		this.activeMonitoringLast24h = monitoringLast24h;
	}

	@XmlElement	
	public int getNrOfGebruikersIngelogd() {
		return nrOfGebruikersIngelogd;
	}

	public void setNrOfGebruikersIngelogd(int nrOfGebruikersIngelogd) {
		this.nrOfGebruikersIngelogd = nrOfGebruikersIngelogd;
	}
	
	@XmlElement	
	public long getTotaalAantalRapporten() {
		return totaalAantalRapporten;
	}

	public void setTotaalAantalRapporten(long totaalAantalRapporten) {
		this.totaalAantalRapporten = totaalAantalRapporten;
	}

	@XmlElement	
	public long getTotaalAantalNieuweRapporten() {
		return totaalAantalNieuweRapporten;
	}

	public void setTotaalAantalNieuweRapporten(long totaalAantalNieuweRapporten) {
		this.totaalAantalNieuweRapporten = totaalAantalNieuweRapporten;
	}
	
	@XmlElement	
	public List<DataLast24h> getRapportenLast24h() {
		return rapportenLast24h;
	}

	public void setRapportenLast24h(List<DataLast24h> rapportenLast24h) {
		this.rapportenLast24h = rapportenLast24h;
	}	
	
	@XmlElement
	public long getTotaalAantalNieuweMonitoring() {
		return totaalAantalNieuweMonitoring;
	}

	public void setTotaalAantalNieuweMonitoring(long totaalAantalNieuweMonitoring) {
		this.totaalAantalNieuweMonitoring = totaalAantalNieuweMonitoring;
	}

	@XmlElement
	public long getTotaalAantalGebruikers() {
		return totaalAantalGebruikers;
	}

	public void setTotaalAantalGebruikers(long totaalAantalGebruikers) {
		this.totaalAantalGebruikers = totaalAantalGebruikers;
	}

	public long getTotaalAantalActivatedGebruikers() {
		return totaalAantalActivatedGebruikers;
	}

	public void setTotaalAantalActivatedGebruikers(
			long totaalAantalActivatedGebruikers) {
		this.totaalAantalActivatedGebruikers = totaalAantalActivatedGebruikers;
	}

	public long getTotaalAantalNonActivatedGebruikers() {
		return totaalAantalNonActivatedGebruikers;
	}

	public void setTotaalAantalNonActivatedGebruikers(
			long totaalAantalNonActivatedGebruikers) {
		this.totaalAantalNonActivatedGebruikers = totaalAantalNonActivatedGebruikers;
	}

	public long getTotaalAantalDeactivatedGebruikers() {
		return totaalAantalDeactivatedGebruikers;
	}

	public void setTotaalAantalDeactivatedGebruikers(
			long totaalAantalDeactivatedGebruikers) {
		this.totaalAantalDeactivatedGebruikers = totaalAantalDeactivatedGebruikers;
	}		
}
