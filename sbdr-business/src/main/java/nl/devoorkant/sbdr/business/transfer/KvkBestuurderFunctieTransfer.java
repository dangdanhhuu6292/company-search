package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by chasselaar on 16-7-2015.
 */
public class KvkBestuurderFunctieTransfer {
	private String functie;

	public KvkBestuurderFunctieTransfer(){

	}

	public KvkBestuurderFunctieTransfer(String functie){
		this.functie = functie;
	}

	@XmlElement
	public String getFunctie() {
		return functie;
	}

	public void setFunctie(String functie) {
		this.functie = functie;
	}
}
