package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class MonitoringDetailsTransfer {
	private MonitoringOverviewTransfer monitoring;
	private MeldingOverviewTransfer[] meldingen;
	
	public MonitoringDetailsTransfer() {
		
	}
	
	public MonitoringDetailsTransfer(MonitoringOverviewTransfer monitoring, List<MeldingOverviewTransfer> meldingen) {
		this.monitoring = monitoring;
		if (meldingen != null && meldingen.size() > 0)
			this.meldingen = meldingen.toArray(new MeldingOverviewTransfer[meldingen.size()]);
		else
			meldingen = null;
	}

	@XmlElement
	public MonitoringOverviewTransfer getMonitoring() {
		return monitoring;
	}

	public void setMonitoring(MonitoringOverviewTransfer monitoring) {
		this.monitoring = monitoring;
	}

	@XmlElement
	public MeldingOverviewTransfer[] getMeldingen() {
		return meldingen;
	}

	public void setMeldingen(MeldingOverviewTransfer[] meldingen) {
		this.meldingen = meldingen;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(value=DateTimeAdapter.class, type=Date.class)
	public Date getToegevoegd() {
		return monitoring.getToegevoegd();
	}

	public void setToegevoegd(Date toegevoegd) {
		monitoring.setToegevoegd(toegevoegd);
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateTimeAdapter.class, type=Date.class)	
	public Date getGewijzigd() {
		return monitoring.getGewijzigd();
	}

	public void setGewijzigd(Date gewijzigd) {
		monitoring.setGewijzigd(gewijzigd);
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateTimeAdapter.class, type=Date.class)	
	public Date getVerwijderd() {
		return monitoring.getVerwijderd();
	}

	public void setVerwijderd(Date verwijderd) {
		monitoring.setVerwijderd(verwijderd);
	}
	

}
