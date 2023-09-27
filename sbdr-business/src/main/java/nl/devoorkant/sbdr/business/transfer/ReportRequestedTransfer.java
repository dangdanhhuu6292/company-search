package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class ReportRequestedTransfer {
	String naam;
	String referentieNummer;
	BedrijfTransfer bedrijf;	
	GebruikerTransfer gebruiker;
	String indEigenDocument;
	Date datum;
	
	public ReportRequestedTransfer() {
		
	}
	
	public ReportRequestedTransfer(String naam, String referentieNummer, BedrijfTransfer bedrijfTransfer, GebruikerTransfer gebruikerTransfer, String indEigenDocument, Date datum) {
		this.naam = naam;
		this.referentieNummer = referentieNummer;
		this.bedrijf = bedrijfTransfer;
		this.gebruiker = gebruikerTransfer;
		this.indEigenDocument = indEigenDocument;
		this.datum = datum;
	}
	
	@XmlElement	
	public String getNaam() {
		return naam;
	}
	public void setNaam(String naam) {
		this.naam = naam;
	}
	
	@XmlElement	
	public String getReferentieNummer() {
		return referentieNummer;
	}
	public void setReferentieNummer(String referentieNummer) {
		this.referentieNummer = referentieNummer;
	}
	
	@XmlElement	
	public BedrijfTransfer getBedrijf() {
		return bedrijf;
	}
	public void setBedrijf(BedrijfTransfer bedrijf) {
		this.bedrijf = bedrijf;
	}
	
	@XmlElement	
	public GebruikerTransfer getGebruiker() {
		return gebruiker;
	}
	public void setGebruiker(GebruikerTransfer gebruiker) {
		this.gebruiker = gebruiker;
	}

	@XmlElement	
	public String getIndEigenDocument() {
		return indEigenDocument;
	}
	public void setIndEigenDocument(String indEigenDocument) {
		this.indEigenDocument = indEigenDocument;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapterOverview.class, type=Date.class)	
	public Date getDatum() {
		return datum;
	}
	public void setDatum(Date datum) {
		this.datum = datum;
	}
	
	
}
