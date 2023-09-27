package nl.devoorkant.sbdr.business.util;

import java.io.Serializable;

public class ViesCheck implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ms = "NL";
	private String iso = "NL";
	private String vat = null;
	private String name = null;
	private String companyType = null;
	private String street1 = null;
	private String postcode = null;
	private String city = null;
	private String requesterMs = "NL";
	private String requesterIso = "NL";
	private String requesterVat = null;
	private String BtnSubmitVat = "Verify";
	
	public ViesCheck(String vat, String requesterVat) {
		this.vat = vat;
		this.requesterVat = requesterVat;
	}

	public String getMs() {
		return ms;
	}

	public void setMs(String ms) {
		this.ms = ms;
	}

	public String getIso() {
		return iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getVat() {
		return vat;
	}

	public void setVat(String vat) {
		this.vat = vat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRequesterMs() {
		return requesterMs;
	}

	public void setRequesterMs(String requesterMs) {
		this.requesterMs = requesterMs;
	}

	public String getRequesterIso() {
		return requesterIso;
	}

	public void setRequesterIso(String requesterIso) {
		this.requesterIso = requesterIso;
	}

	public String getRequesterVat() {
		return requesterVat;
	}

	public void setRequesterVat(String requesterVat) {
		this.requesterVat = requesterVat;
	}

	public String getBtnSubmitVat() {
		return BtnSubmitVat;
	}

	public void setBtnSubmitVat(String btnSubmitVat) {
		BtnSubmitVat = btnSubmitVat;
	}
}
