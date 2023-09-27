package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class MonitoringOverviewTransfer extends BedrijfTransferXml{
	private Integer monitoringId = null;
	private String monitoringReferentieIntern = null;
	private Integer gebruikerAangemaaktId;
	private Integer gebruikerVerwijderdId;
	private Integer gebruikerLaatsteMutatieId = null;
	private Date toegevoegd;
	private Date gewijzigd;
	private Date verwijderd;
	private String bedrijfActief;
	private String bedrijfActiefOmschrijving;
	private String faillietSurseance;
	private String faillietSurseanceOmschrijving;	
	
	public MonitoringOverviewTransfer() { 
		super();
	}
	
	public MonitoringOverviewTransfer(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, Integer monitoringId, String monitoringReferentieIntern, Date toegevoegd, Date gewijzigd, Date verwijderd, Integer gebruikerAangemaaktId, Integer gebruikerVerwijderdId, Integer gebruikerLaatsteMutatieId, String telefoonnummer) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);
		this.monitoringId = monitoringId;
		this.monitoringReferentieIntern = monitoringReferentieIntern;
		this.toegevoegd = toegevoegd;
		this.gewijzigd = gewijzigd;
		this.verwijderd = verwijderd;
		this.gebruikerAangemaaktId = gebruikerAangemaaktId;
		this.gebruikerVerwijderdId = gebruikerVerwijderdId;
		this.gebruikerLaatsteMutatieId = gebruikerLaatsteMutatieId;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)
	public Date getToegevoegd() {
		return toegevoegd;
	}

	public void setToegevoegd(Date toegevoegd) {
		this.toegevoegd = toegevoegd;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)	
	public Date getGewijzigd() {
		return gewijzigd;
	}

	public void setGewijzigd(Date gewijzigd) {
		this.gewijzigd = gewijzigd;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getMonitoringId() {
		return monitoringId;
	}

	public void setMonitoringId(Integer monitoringId) {
		this.monitoringId = monitoringId;
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
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)	
	public Date getVerwijderd() {
		return verwijderd;
	}

	public void setVerwijderd(Date verwijderd) {
		this.verwijderd = verwijderd;
	}

	@XmlElement
	public String getMonitoringReferentieIntern() {
		return monitoringReferentieIntern;
	}

	public void setMonitoringReferentieIntern(String monitoringReferentieIntern) {
		this.monitoringReferentieIntern = monitoringReferentieIntern;
	}

	@XmlElement
	public String getBedrijfActief() {
		return bedrijfActief;
	}

	public void setBedrijfActief(String bedrijfActief) {
		this.bedrijfActief = bedrijfActief;
	}

	@XmlElement
	public String getBedrijfActiefOmschrijving() {
		return bedrijfActiefOmschrijving;
	}

	public void setBedrijfActiefOmschrijving(String bedrijfActiefOmschrijving) {
		this.bedrijfActiefOmschrijving = bedrijfActiefOmschrijving;
	}

	@XmlElement
	public String getFaillietSurseance() {
		return faillietSurseance;
	}

	public void setFaillietSurseance(String faillietSurseance) {
		this.faillietSurseance = faillietSurseance;
	}

	@XmlElement
	public String getFaillietSurseanceOmschrijving() {
		return faillietSurseanceOmschrijving;
	}

	public void setFaillietSurseanceOmschrijving(
			String faillietSurseanceOmschrijving) {
		this.faillietSurseanceOmschrijving = faillietSurseanceOmschrijving;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikerLaatsteMutatieId() {
		return gebruikerLaatsteMutatieId;
	}

	public void setGebruikerLaatsteMutatieId(Integer gebruikerLaatsteMutatieId) {
		this.gebruikerLaatsteMutatieId = gebruikerLaatsteMutatieId;
	}	

}
