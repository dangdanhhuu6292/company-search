package nl.devoorkant.sbdr.business.util;

import java.io.Serializable;

public class Mail implements Serializable {

	private static final long serialVersionUID = 247917377400301297L;
	private Integer gebruikerId;
	private byte[] attachment;
	private String attachmentName = null;
	EEMailType emailType;
	private String pstrAddressFrom;
	private String pstrBodyHTML;
	private String pstrBodyPlain;
	private String pstrSubject;
	private String[] pstraAddressesCc;
	private String[] pstraAddressesTo;	

	public Mail() {

	}

	public Mail(EEMailType emailType) {
		this.emailType = emailType;
	}

	public String getAddressFrom() {
		return pstrAddressFrom;
	}

	public void setAddressFrom(String pstrValue) {
		pstrAddressFrom = pstrValue;
	}

	public String[] getAddressesCc() {
		return pstraAddressesCc;
	}

	public void setAddressesCc(String[] pstraAddressesCc) {
		this.pstraAddressesCc = pstraAddressesCc;
	}

	public String[] getAddressesTo() {
		return pstraAddressesTo;
	}

	public void setAddressesTo(String[] pstraValue) {
		pstraAddressesTo = pstraValue;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public String getBodyHTML() {
		return pstrBodyHTML;
	}

	public void setBodyHTML(String pstrValue) {
		pstrBodyHTML = pstrValue;
	}

	public String getBodyPlain() {
		return pstrBodyPlain;
	}

	public void setBodyPlain(String pstrValue) {
		pstrBodyPlain = pstrValue;
	}

	public EEMailType getEmailType() {
		return emailType;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public void setEmailType(EEMailType emailType) {
		this.emailType = emailType;
	}

	public String getSubject() {
		return pstrSubject;
	}

	public void setSubject(String pstrValue) {
		pstrSubject = pstrValue;
	}

	public Integer getGebruikerId() {
		return gebruikerId;
	}

	public void setGebruikerId(Integer gebruikerId) {
		this.gebruikerId = gebruikerId;
	}

}

