package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class SearchResultsOverviewTransfer extends BedrijfTransferXml{
	private BedrijfTransfer bedrijfVan = null;
	private String referentieIntern;
	private String type;
	private Integer meldingId = null;
	private Integer monitoringId = null;
	private Integer documentId = null;
	//private Integer bedrijfId = null;
	private Date datumStart = null;
	private Date datumEinde = null;
	private Integer gebruikerAangemaaktId = null;
	private String status = null;
	private String statusCode = null;
	
	private String redenVerwijderenCode = null;
	
	public SearchResultsOverviewTransfer() {
		super();		
	}
	
	public SearchResultsOverviewTransfer(Integer doorbedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, 
			BedrijfTransfer bedrijfVan, String referentieIntern, String type, Integer meldingId, Integer monitoringId, Integer documentId, Integer bedrijfId, Date datumStart, Date datumEinde, Integer gebruikerAangemaaktId, String statusCode, String status, String telefoonnummer) {
		super(doorbedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);
		this.bedrijfVan = bedrijfVan;
		this.referentieIntern = referentieIntern;
		this.type = type;
		this.meldingId = meldingId;
		this.monitoringId = monitoringId;
		this.documentId = documentId;
		//this.bedrijfId = bedrijfId;
		this.datumStart = datumStart;
		this.datumEinde = datumEinde;
		this.gebruikerAangemaaktId = gebruikerAangemaaktId;
		this.statusCode = statusCode;
		this.status = status;
	}

	@XmlElement
	public BedrijfTransfer getBedrijfVan() {
		return bedrijfVan;
	}

	public void setBedrijfVan(BedrijfTransfer bedrijfVan) {
		this.bedrijfVan = bedrijfVan;
	}

	@XmlElement
	public String getReferentieIntern() {
		return referentieIntern;
	}

	public void setReferentieIntern(String referentieIntern) {
		this.referentieIntern = referentieIntern;
	}

	@XmlElement
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

//	@XmlElement
//	public Integer getBedrijfId() {
//		return bedrijfId;
//	}
//
//	public void setBedrijfId(Integer bedrijfId) {
//		this.bedrijfId = bedrijfId;
//	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateTimeAdapter.class, type=Date.class)		
	public Date getDatumStart() {
		return datumStart;
	}

	public void setDatumStart(Date datumStart) {
		this.datumStart = datumStart;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateTimeAdapter.class, type=Date.class)		
	public Date getDatumEinde() {
		return datumEinde;
	}

	public void setDatumEinde(Date datumEinde) {
		this.datumEinde = datumEinde;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikerAangemaaktId() {
		return gebruikerAangemaaktId;
	}

	public void setGebruikerAangemaaktId(Integer gebruikerAangemaaktId) {
		this.gebruikerAangemaaktId = gebruikerAangemaaktId;
	}

	@XmlElement
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@XmlElement
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement
	public String getRedenVerwijderenCode() {
		return redenVerwijderenCode;
	}

	public void setRedenVerwijderenCode(String redenVerwijderenCode) {
		this.redenVerwijderenCode = redenVerwijderenCode;
	}
	
}
