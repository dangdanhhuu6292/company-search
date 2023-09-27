package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;

@XmlRootElement
public class BriefBatchTransfer {
	private Integer briefBatchId;
	private String briefBatchStatus;
	private String briefBatchStatusDesc;
	private String briefBatchType;
	private String briefBatchTypeDesc;
	private Date datumAangemaakt;
	private Date datumVoltooid;
	private Integer gemaaktDoorGebruikerId;

	public BriefBatchTransfer() {

	}

	public BriefBatchTransfer(Integer briefBatchId, String briefBatchStatus, String briefBatchStatusDesc, String briefBatchType, String briefBatchTypeDesc, Date datumAangemaakt, Date datumVoltooid, Integer gemaaktDoorGebruikerId) {
		this.briefBatchId = briefBatchId;
		this.briefBatchStatus = briefBatchStatus;
		this.briefBatchStatusDesc = briefBatchStatusDesc;
		this.briefBatchType = briefBatchType;
		this.briefBatchTypeDesc = briefBatchTypeDesc;
		this.datumAangemaakt = datumAangemaakt;
		this.datumVoltooid = datumVoltooid;
		this.gemaaktDoorGebruikerId = gemaaktDoorGebruikerId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getBriefBatchId() {
		return briefBatchId;
	}

	public void setBriefBatchId(Integer briefBatchId) {
		this.briefBatchId = briefBatchId;
	}

	@XmlElement
	public String getBriefBatchStatus() {
		return briefBatchStatus;
	}

	public void setBriefBatchStatus(String briefBatchStatus) {
		this.briefBatchStatus = briefBatchStatus;
	}

	@XmlElement
	public String getBriefBatchStatusDesc() {
		return briefBatchStatusDesc;
	}

	public void setBriefBatchStatusDesc(String briefBatchStatusDesc) {
		this.briefBatchStatusDesc = briefBatchStatusDesc;
	}

	@XmlElement
	public String getBriefBatchType() {
		return briefBatchType;
	}

	public void setBriefBatchType(String briefBatchType) {
		this.briefBatchType = briefBatchType;
	}

	@XmlElement
	public String getBriefBatchTypeDesc() {
		return briefBatchTypeDesc;
	}

	public void setBriefBatchTypeDesc(String briefBatchTypeDesc) {
		this.briefBatchTypeDesc = briefBatchTypeDesc;
	}

	@XmlElement
	public Date getDatumAangemaakt() {
		return datumAangemaakt;
	}

	public void setDatumAangemaakt(Date datumAangemaakt) {
		this.datumAangemaakt = datumAangemaakt;
	}

	@XmlElement
	public Date getDatumVoltooid() {
		return datumVoltooid;
	}

	public void setDatumVoltooid(Date datumVoltooid) {
		this.datumVoltooid = datumVoltooid;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGemaaktDoorGebruikerId() {
		return gemaaktDoorGebruikerId;
	}

	public void setGemaaktDoorGebruikerId(Integer gemaaktDoorGebruikerId) {
		this.gemaaktDoorGebruikerId = gemaaktDoorGebruikerId;
	}
}
