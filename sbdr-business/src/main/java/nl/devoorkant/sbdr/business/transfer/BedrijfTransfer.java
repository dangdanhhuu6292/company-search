package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BedrijfTransfer extends BedrijfTransferXml{

	public BedrijfTransfer() {
		
	}
	
	public BedrijfTransfer(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam,String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, String telefoonnummer, String sbiOmschrijving) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, sbiOmschrijving);
	}
}
