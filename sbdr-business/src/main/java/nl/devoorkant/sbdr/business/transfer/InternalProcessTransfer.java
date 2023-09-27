package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;

@XmlRootElement
public class InternalProcessTransfer extends BedrijfTransferXml {
	private BriefBatchTransfer briefBatchTransfer = null;
	private Date datumAangemaakt = null;
	private Date datumVerwerkt = null;
	private String documentReferentie = null;
	private Integer internalProcessId = null;
	private String internalProcessStatus = null;
	private String internalProcessStatusDesc = null;
	private String internalProcessType = null;
	private String internalProcessTypeDesc = null;
	private Integer verwerktDoorGebruikerId = null;
	private Integer documentId = null;

	public InternalProcessTransfer() {
		super();
	}

	public InternalProcessTransfer(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, String telefoonnummer, Integer internalProcessId, String internalProcessStatus, String internalProcessType, Date datumAangemaakt, Date datumVerwerkt, Integer verwerktDoorGebruikerId, String internalProcessStatusDesc, String internalProcessTypeDesc, String documentReferentie, BriefBatchTransfer briefBatchTransfer, Integer documentId) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);
		this.internalProcessId = internalProcessId;
		this.internalProcessStatus = internalProcessStatus;
		this.internalProcessType = internalProcessType;
		this.datumAangemaakt = datumAangemaakt;
		this.datumVerwerkt = datumVerwerkt;
		this.verwerktDoorGebruikerId = verwerktDoorGebruikerId;
		this.internalProcessStatusDesc = internalProcessStatusDesc;
		this.internalProcessTypeDesc = internalProcessTypeDesc;
		this.documentReferentie = documentReferentie;
		this.briefBatchTransfer = briefBatchTransfer;
		this.documentId = documentId;
	}

	public InternalProcessTransfer(Integer internalProcessId, String internalProcessStatus, String internalProcessType, Date datumAangemaakt, Date datumVerwerkt, Integer verwerktDoorGebruikerId, String internalProcessStatusDesc, String internalProcessTypeDesc, String documentReferentie, BriefBatchTransfer briefBatchTransfer, Integer documentId) {
		this.internalProcessId = internalProcessId;
		this.internalProcessStatus = internalProcessStatus;
		this.internalProcessType = internalProcessType;
		this.datumAangemaakt = datumAangemaakt;
		this.datumVerwerkt = datumVerwerkt;
		this.verwerktDoorGebruikerId = verwerktDoorGebruikerId;
		this.internalProcessStatusDesc = internalProcessStatusDesc;
		this.internalProcessTypeDesc = internalProcessTypeDesc;
		this.documentReferentie = documentReferentie;
		this.briefBatchTransfer = briefBatchTransfer;
		this.documentId = documentId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	@XmlElement
	public BriefBatchTransfer getBriefBatchTransfer() {
		return briefBatchTransfer;
	}

	public void setBriefBatchTransfer(BriefBatchTransfer briefBatchTransfer) {
		this.briefBatchTransfer = briefBatchTransfer;
	}

	@XmlElement
	public Date getDatumAangemaakt() {
		return datumAangemaakt;
	}

	public void setDatumAangemaakt(Date datumAangemaakt) {
		this.datumAangemaakt = datumAangemaakt;
	}

	@XmlElement
	public Date getDatumVerwerkt() {
		return datumVerwerkt;
	}

	public void setDatumVerwerkt(Date datumVerwerkt) {
		this.datumVerwerkt = datumVerwerkt;
	}

	@XmlElement
	public String getDocumentReferentie() {
		return documentReferentie;
	}

	public void setDocumentReferentie(String documentReferentie) {
		this.documentReferentie = documentReferentie;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getInternalProcessId() {
		return internalProcessId;
	}

	public void setInternalProcessId(Integer internalProcessId) {
		this.internalProcessId = internalProcessId;
	}

	@XmlElement
	public String getInternalProcessStatus() {
		return internalProcessStatus;
	}

	public void setInternalProcessStatus(String internalProcessStatus) {
		this.internalProcessStatus = internalProcessStatus;
	}

	@XmlElement
	public String getInternalProcessStatusDesc() {
		return internalProcessStatusDesc;
	}

	public void setInternalProcessStatusDesc(String internalProcessStatusDesc) {
		this.internalProcessStatusDesc = internalProcessStatusDesc;
	}

	@XmlElement
	public String getInternalProcessType() {
		return internalProcessType;
	}

	public void setInternalProcessType(String internalProcessType) {
		this.internalProcessType = internalProcessType;
	}

	@XmlElement
	public String getInternalProcessTypeDesc() {
		return internalProcessTypeDesc;
	}

	public void setInternalProcessTypeDesc(String internalProcessTypeDesc) {
		this.internalProcessTypeDesc = internalProcessTypeDesc;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getVerwerktDoorGebruikerId() {
		return verwerktDoorGebruikerId;
	}

	public void setVerwerktDoorGebruikerId(Integer verwerktDoorGebruikerId) {
		this.verwerktDoorGebruikerId = verwerktDoorGebruikerId;
	}
}
