package nl.devoorkant.sbdr.data;

import java.sql.Timestamp;

import nl.devoorkant.sbdr.data.model.Bedrijf;

public class BedrijfMelding {
	private Bedrijf bedrijf;
	private Timestamp datumGeaccordeerd;
	
	public BedrijfMelding() {
		
	}
	
	public BedrijfMelding(Bedrijf bedrijf, Timestamp datumGeaccordeerd) {
		this.bedrijf = bedrijf;
		this.datumGeaccordeerd = datumGeaccordeerd;
	}

	public Bedrijf getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(Bedrijf bedrijf) {
		this.bedrijf = bedrijf;
	}

	public Timestamp getDatumGeaccordeerd() {
		return datumGeaccordeerd;
	}

	public void setDatumGeaccordeerd(Timestamp datumGeaccordeerd) {
		this.datumGeaccordeerd = datumGeaccordeerd;
	}

}
