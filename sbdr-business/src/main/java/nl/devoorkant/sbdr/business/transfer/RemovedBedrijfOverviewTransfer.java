package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class RemovedBedrijfOverviewTransfer extends BedrijfTransferXml{
	private String referentieMelding;
	private String referentieIntern;
	private String portfolio;
	private Integer meldingId = null;
	private Integer monitoringId = null;
	private Date datumStart = null;
	private Date datumEinde = null;
	private Integer gebruikerAangemaaktId = null;
	private Integer gebruikerVerwijderdId = null;
	private String status = null;
	
	private String redenVerwijderenCode = null;
	
	public RemovedBedrijfOverviewTransfer() {
		super();		
	}

	public RemovedBedrijfOverviewTransfer(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, String referentieMelding, String referentieIntern, String portfolio, Integer meldingId, Integer monitoringId, Date datumStart, Date datumEinde, Integer gebruikerAangemaaktId, Integer gebruikerVerwijderdId, String status, String redenCode, String telefoonnummer) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);
		this.referentieMelding = referentieMelding;
		this.referentieIntern = referentieIntern;
		this.portfolio = portfolio;
		this.meldingId = meldingId;
		this.monitoringId = monitoringId;
		this.datumStart = datumStart;
		this.datumEinde = datumEinde;
		this.setGebruikerAangemaaktId(gebruikerAangemaaktId);
		this.setGebruikerVerwijderdId(gebruikerVerwijderdId);
		this.status = status;
		this.redenVerwijderenCode = redenCode;
	}

	@XmlElement
	public String getReferentieMelding() {
		return referentieMelding;
	}

	public void setReferentieMelding(String referentieMelding) {
		this.referentieMelding = referentieMelding;
	}

	@XmlElement
	public String getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(String portfolio) {
		this.portfolio = portfolio;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getMeldingId() {
		return meldingId;
	}

	public void setMeldingId(Integer meldingId) {
		this.meldingId = meldingId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getMonitoringId() {
		return monitoringId;
	}

	public void setMonitoringId(Integer monitoringId) {
		this.monitoringId = monitoringId;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)	
	public Date getDatumStart() {
		return datumStart;
	}

	public void setDatumStart(Date datumStart) {
		this.datumStart = datumStart;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)	
	public Date getDatumEinde() {
		return datumEinde;
	}

	public void setDatumEinde(Date datumEinde) {
		this.datumEinde = datumEinde;
	}

	@XmlElement
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikerAangemaaktId() {
		return gebruikerAangemaaktId;
	}

	public void setGebruikerAangemaaktId(Integer gebruikerAangemaaktId) {
		this.gebruikerAangemaaktId = gebruikerAangemaaktId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikerVerwijderdId() {
		return gebruikerVerwijderdId;
	}

	public void setGebruikerVerwijderdId(Integer gebruikerVerwijderdId) {
		this.gebruikerVerwijderdId = gebruikerVerwijderdId;
	}

	@XmlElement
	public String getReferentieIntern() {
		return referentieIntern;
	}

	public void setReferentiegIntern(String referentieMeldingIntern) {
		this.referentieIntern = referentieMeldingIntern;
	}

	@XmlElement
	public String getRedenVerwijderenCode() {
		return redenVerwijderenCode;
	}

	public void setRedenVerwijderenCode(String redenVerwijderenCode) {
		this.redenVerwijderenCode = redenVerwijderenCode;
	}	
}
