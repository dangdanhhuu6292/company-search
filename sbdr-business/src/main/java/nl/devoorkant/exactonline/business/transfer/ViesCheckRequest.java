package nl.devoorkant.exactonline.business.transfer;

import java.io.Serializable;

public class ViesCheckRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2223984655094503023L;
	private String iso;
	private String ms;
	private String vat;
	
	public ViesCheckRequest() {
		
	}

	public String getIso() {
		return iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getMs() {
		return ms;
	}

	public void setMs(String ms) {
		this.ms = ms;
	}

	public String getVat() {
		return vat;
	}

	public void setVat(String vat) {
		this.vat = vat;
	}
	

}
