package nl.devoorkant.sbdr.business.transfer;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class MeldingOverviewTransfer extends BedrijfTransferXml {
	private BigDecimal bedrag;
	private BedrijfTransfer bedrijfGemeldDoor = null;
	private Date datumGeaccordeerd;
	private Date datumLaatsteMutatie;
	private Date datumVerwijderd;
	private Integer gebruikerId = null;
	private boolean isViaMelding = false;
	private Integer meldingId = null;
	private BigDecimal oorspronkelijkBedrag;
	private String referentie;
	private String referentieIntern;
	private String status;
	private String statusCode = null;
	private Date toegevoegd;
	private Date verloopdatumFactuur;
	private String emailDebiteur;
	private String telefoonnummerDebiteur;

	public MeldingOverviewTransfer() {
		super();
	}

	public MeldingOverviewTransfer(Integer bedrijfId, boolean isActief, boolean isHoofd, String bedrijfsNaam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, Integer meldingId, Integer gebruikerId, String referentie, String referentieIntern, String statusCode, String status, Date toegevoegd, Date verloopdatumFactuur, Date datumGeaccoordeerd, Date datumVerwijderd, Date datumLaatsteMutatie, BigDecimal oorspronkelijkBedrag, BigDecimal bedrag, boolean isViaMelding, String telefoonnummer, String sbiOmschrijving, String telefoonnummerDebiteur, String emailDebiteur) {
		super(bedrijfId, isActief, isHoofd, bedrijfsNaam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, sbiOmschrijving);
		this.setMeldingId(meldingId);
		this.gebruikerId = gebruikerId;
		this.referentie = referentie;
		this.referentieIntern = referentieIntern;
		this.status = status;
		this.statusCode = statusCode;
		this.toegevoegd = toegevoegd;
		this.oorspronkelijkBedrag = oorspronkelijkBedrag;
		this.bedrag = bedrag;
		this.setVerloopdatumFactuur(verloopdatumFactuur);
		this.datumVerwijderd = datumVerwijderd;
		this.isViaMelding = isViaMelding;
		this.datumGeaccordeerd = datumGeaccoordeerd;
		this.datumLaatsteMutatie = datumLaatsteMutatie;
		this.telefoonnummerDebiteur = telefoonnummerDebiteur;
		this.emailDebiteur = emailDebiteur;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = MoneyAdapter.class, type = BigDecimal.class)
	public BigDecimal getBedrag() {
		return bedrag;
	}

	public void setBedrag(BigDecimal bedrag) {
		this.bedrag = bedrag;
	}

	@XmlElement
	public BedrijfTransfer getBedrijfGemeldDoor() {
		return bedrijfGemeldDoor;
	}

	public void setBedrijfGemeldDoor(BedrijfTransfer bedrijfGemeldDoor) {
		this.bedrijfGemeldDoor = bedrijfGemeldDoor;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
	public Date getDatumGeaccordeerd() {
		return datumGeaccordeerd;
	}

	public void setDatumGeaccordeerd(Date datumGeaccordeerd) {
		this.datumGeaccordeerd = datumGeaccordeerd;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
	public Date getDatumLaatsteMutatie() {
		return datumLaatsteMutatie;
	}

	public void setDatumLaatsteMutatie(Date datumLaatsteMutatie) {
		this.datumLaatsteMutatie = datumLaatsteMutatie;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
	public Date getDatumVerwijderd() {
		return datumVerwijderd;
	}

	public void setDatumVerwijderd(Date datumVerwijderd) {
		this.datumVerwijderd = datumVerwijderd;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikerId() {
		return gebruikerId;
	}

	public void setGebruikerId(Integer gebruikerId) {
		this.gebruikerId = gebruikerId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getMeldingId() {
		return meldingId;
	}

	public void setMeldingId(Integer meldingId) {
		this.meldingId = meldingId;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = MoneyAdapter.class, type = BigDecimal.class)
	public BigDecimal getOorspronkelijkBedrag() {
		return oorspronkelijkBedrag;
	}

	public void setOorspronkelijkBedrag(BigDecimal oorspronkelijkBedrag) {
		this.oorspronkelijkBedrag = oorspronkelijkBedrag;
	}

	@XmlElement
	public String getReferentie() {
		return referentie;
	}

	public void setReferentie(String referentie) {
		this.referentie = referentie;
	}

	@XmlElement
	public String getReferentieIntern() {
		return referentieIntern;
	}

	public void setReferentieIntern(String referentieIntern) {
		this.referentieIntern = referentieIntern;
	}

	@XmlElement
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
	public Date getToegevoegd() {
		return toegevoegd;
	}

	public void setToegevoegd(Date toegevoegd) {
		this.toegevoegd = toegevoegd;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getVerloopdatumFactuur() {
		return verloopdatumFactuur;
	}

	public void setVerloopdatumFactuur(Date verloopdatumFactuur) {
		this.verloopdatumFactuur = verloopdatumFactuur;
	}

	@XmlElement
	public boolean isViaMelding() {
		return isViaMelding;
	}

	public void setViaMelding(boolean isViaMelding) {
		this.isViaMelding = isViaMelding;
	}

	@XmlElement
	public String getEmailDebiteur() {
		return emailDebiteur;
	}

	public void setEmailDebiteur(String emailDebiteur) {
		this.emailDebiteur = emailDebiteur;
	}

	@XmlElement
	public String getTelefoonnummerDebiteur() {
		return telefoonnummerDebiteur;
	}

	public void setTelefoonnummerDebiteur(String telefoonnummerDebiteur) {
		this.telefoonnummerDebiteur = telefoonnummerDebiteur;
	}
	
	


}
