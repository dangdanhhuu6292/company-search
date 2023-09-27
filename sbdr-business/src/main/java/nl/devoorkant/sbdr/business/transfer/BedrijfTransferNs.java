package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class BedrijfTransferNs extends BedrijfTransferXml{

	public BedrijfTransferNs() {
		
	}
	
	public BedrijfTransferNs(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam,String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, String telefoonnummer, String sbiOmschrijving) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, sbiOmschrijving);
	}
	
	@Override	
	@XmlTransient
	public boolean isBekendBijSbdr() {
		return super.isBekendBijSbdr();
	}

	public void setBekendBijSbdr(boolean isBekendBijSbdr) {
		super.setBekendBijSbdr(isBekendBijSbdr);
	}
	
	@Override
	@XmlTransient
	public boolean isMeldingBijBedrijf() {
		return super.isMeldingBijBedrijf();
	}

	public void setMeldingBijBedrijf(boolean isMeldingBijBedrijf) {
		super.setMeldingBijBedrijf(isMeldingBijBedrijf);
	}

	@Override
	@XmlTransient
	public boolean isMonitoringBijBedrijf() {
		return super.isMonitoringBijBedrijf();
	}

	public void setMonitoringBijBedrijf(boolean isMonitoringBijBedrijf) {
		super.setMonitoringBijBedrijf(isMonitoringBijBedrijf);
	}

	@Override
	@XmlTransient
	public boolean isRapportCreatedToday() {
		return super.isRapportCreatedToday();
	}

	public void setRapportCreatedToday(boolean isRapportCreatedToday) {
		super.setRapportCreatedToday(isRapportCreatedToday);
	}	
}
