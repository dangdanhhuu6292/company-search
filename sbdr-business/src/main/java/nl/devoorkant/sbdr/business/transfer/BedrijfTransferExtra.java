package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BedrijfTransferExtra extends BedrijfTransferXml{
	private String postPostbus;
	private String postPlaats;
	private String postPostcode;
	
	public BedrijfTransferExtra() {
		
	}
	
	public BedrijfTransferExtra(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam,String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, String telefoonnummer, String sbiOmschrijving, String postPostbus, String postPlaats, String postPostcode) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, sbiOmschrijving);
		
		this.postPostbus = postPostbus;
		this.postPlaats = postPlaats;
		this.postPostcode = postPostcode;
	}

	@XmlElement
	public String getPostPostbus() {
		return postPostbus;
	}

	@XmlElement
	public String getPostPlaats() {
		return postPlaats;
	}

	@XmlElement
	public String getPostPostcode() {
		return postPostcode;
	}
	
}
