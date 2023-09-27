package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class FaillissementenOverviewTransfer {
	private Date datumPublicatie;
	private String naam; 
	private Integer persoonId; 
	private Integer insolventieId;
	private String kvkNummer;
	private Integer publicatieId;
	private String publicatieKenmerk;
	private String publicatieOmschrijving;
	private String publicatieSoortCode;
	private String publicatieSoortOmschrijving;

	public FaillissementenOverviewTransfer() {

	}

	public FaillissementenOverviewTransfer(Integer insolventieId, Integer publicatieId, Date datumPublicatie, String publicatieKenmerk, String publicatieOmschrijving, Integer persoonId, String naam, String kvkNummer, String publicatieSoortCode, String publicatieSoortOmschrijving) {
		this.publicatieId = publicatieId;
		this.insolventieId = insolventieId;
		this.datumPublicatie = datumPublicatie;
		this.publicatieKenmerk = publicatieKenmerk;
		this.publicatieOmschrijving = publicatieOmschrijving;
		this.persoonId = persoonId;
		this.naam = naam;
		this.kvkNummer = kvkNummer;
		this.publicatieSoortCode = publicatieSoortCode;
		this.publicatieSoortOmschrijving = publicatieSoortOmschrijving;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumPublicatie() {
		return datumPublicatie;
	}

	public void setDatumPublicatie(Date datumPublicatie) {
		this.datumPublicatie = datumPublicatie;
	}

	@XmlElement
	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getPersoonId() {
		return persoonId;
	}

	public void setPersoonId(Integer persoonId) {
		this.persoonId = persoonId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getInsolventieId() {
		return insolventieId;
	}

	public void setInsolventieId(Integer insolventieId) {
		this.insolventieId = insolventieId;
	}

	@XmlElement
	public String getKvkNummer() {
		return kvkNummer;
	}

	public void setKvkNummer(String kvkNummer) {
		this.kvkNummer = kvkNummer;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getPublicatieId() {
		return publicatieId;
	}

	public void setPublicatieId(Integer publicatieId) {
		this.publicatieId = publicatieId;
	}

	@XmlElement
	public String getPublicatieKenmerk() {
		return publicatieKenmerk;
	}

	public void setPublicatieKenmerk(String publicatieKenmerk) {
		this.publicatieKenmerk = publicatieKenmerk;
	}

	@XmlElement
	public String getPublicatieOmschrijving() {
		return publicatieOmschrijving;
	}

	public void setPublicatieOmschrijving(String publicatieOmschrijving) {
		this.publicatieOmschrijving = publicatieOmschrijving;
	}

	@XmlElement
	public String getPublicatieSoortCode() {
		return publicatieSoortCode;
	}

	public void setPublicatieSoortCode(String publicatieSoortCode) {
		this.publicatieSoortCode = publicatieSoortCode;
	}

	@XmlElement
	public String getPublicatieSoortOmschrijving() {
		return publicatieSoortOmschrijving;
	}

	public void setPublicatieSoortOmschrijving(String publicatieSoortOmschrijving) {
		this.publicatieSoortOmschrijving = publicatieSoortOmschrijving;
	}
}