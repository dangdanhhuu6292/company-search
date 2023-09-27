package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement
public class MeldingTransfer extends BedrijfTransferXml {
	private BigDecimal bedrag = null;
	private boolean bedragWeergeven = true;
	private Integer bedrijfIdGerapporteerd = null;
	private boolean bedrijfsgegevensNietJuist = false;
	private String briefStatus;
	private String briefStatusDesc;
	private Date datumAangemaakt = null;
	private Date datumFactuur = null;
	private Date datumGeaccordeerd = null;
	private Date datumLaatsteMutatie = null;
	private Date datumVerwijderd = null;
	private boolean doorBedrijfWeergeven = true;
	private Integer gebruikerAangemaaktId = null;
	private Integer gebruikerGeaccordeerdId = null;
	private Integer gebruikerLaatsteMutatieId = null;
	private Integer gebruikerVerwijderdId = null;
	private Integer meldingId = null;
	private String meldingstatus = null;
	private String meldingstatusCode = null;
	private BigDecimal oorspronkelijkBedrag = null;
	private String redenVerwijderenCode = null;
	private String redenVerwijderenOmschrijving = null;
	private String referentie = null;
	private String referentieIntern = null;
	private boolean verwerktInBatch = false;
	private boolean hasObjection = false;
	private String notities = null;
	private String telefoonNummerDebiteur = null;
	private String emailAdresDebiteur = null;

	public MeldingTransfer() {
		super();
	}

	public MeldingTransfer(Integer meldingId, Integer gebruikerAangemaaktId, Integer gebruikerLaatsteMutatieId, Integer gebruikerGeaccordeerdId, Integer gebruikerVerwijderdId, Integer bedrijfId, boolean isActief, boolean isHoofd, String naam, String kvkNummer, String subDossier, String sbdrNummer, String straat, String huisnummer, String huisnummerToevoeging, String postcode, String plaats, String referentie, String referentieIntern, Date datumAangemaakt, Date datumLaatsteMutatie, Date datumGeaccordeerd, Date datumVerwijderd, Date datumFactuur, BigDecimal bedrag, BigDecimal oorspronkelijkBedrag, Integer bedrijfIdGerapporteerd, String meldingstatus, String meldingstatusCode, boolean doorBedrijfWeergeven, boolean bedragWeergeven, boolean bedrijfsgegevensNietJuist, String redenVerwijderenCode, String redenVerwijderenOmschrijving, String telefoonnummer, String briefStatus, String briefStatusDesc, boolean verwerktInBatch, boolean hasObjection, String notities, String telefoonNummerDebiteur, String emailAdresDebiteur) {
		super(bedrijfId, isActief, isHoofd, naam, kvkNummer, subDossier, sbdrNummer, straat, huisnummer, huisnummerToevoeging, postcode, plaats, telefoonnummer, null);
		this.meldingId = meldingId;
		this.gebruikerAangemaaktId = gebruikerAangemaaktId;
		this.gebruikerLaatsteMutatieId = gebruikerLaatsteMutatieId;
		this.gebruikerGeaccordeerdId = gebruikerGeaccordeerdId;
		this.gebruikerVerwijderdId = gebruikerVerwijderdId;
		this.datumAangemaakt = datumAangemaakt;
		this.datumLaatsteMutatie = datumLaatsteMutatie;
		this.datumGeaccordeerd = datumGeaccordeerd;
		this.datumVerwijderd = datumVerwijderd;
		this.referentie = referentie;
		this.referentieIntern = referentieIntern;
		this.datumFactuur = datumFactuur;
		this.bedrag = bedrag;
		this.oorspronkelijkBedrag = oorspronkelijkBedrag;
		this.bedrijfIdGerapporteerd = bedrijfIdGerapporteerd;
		this.meldingstatus = meldingstatus;
		this.meldingstatusCode = meldingstatusCode;
		this.doorBedrijfWeergeven = doorBedrijfWeergeven;
		this.bedragWeergeven = bedragWeergeven;
		this.bedrijfsgegevensNietJuist = bedrijfsgegevensNietJuist;
		this.redenVerwijderenCode = redenVerwijderenCode;
		this.redenVerwijderenOmschrijving = redenVerwijderenOmschrijving;
		this.briefStatus = briefStatus;
		this.briefStatusDesc = briefStatusDesc;
		this.verwerktInBatch = verwerktInBatch;
		this.hasObjection = hasObjection;
		this.notities = notities;
		this.telefoonNummerDebiteur = telefoonNummerDebiteur;
		this.emailAdresDebiteur = emailAdresDebiteur;
	}

	@XmlElement(name = "bedrag")
	@XmlJavaTypeAdapter(value = MoneyAdapter.class, type = BigDecimal.class)
	public BigDecimal getBedrag() {
		return bedrag;
	}

	public void setBedrag(BigDecimal bedrag) {
		this.bedrag = bedrag;
	}

	@XmlElement(name = "bedrijfIdGerapporteerd", type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)	
	public Integer getBedrijfIdGerapporteerd() {
		return bedrijfIdGerapporteerd;
	}

	public void setBedrijfIdGerapporteerd(Integer bedrijfIdGerapporteerd) {
		this.bedrijfIdGerapporteerd = bedrijfIdGerapporteerd;
	}

	@XmlElement
	public String getBriefStatus() {
		return briefStatus;
	}

	public void setBriefStatus(String briefStatus) {
		this.briefStatus = briefStatus;
	}

	@XmlElement
	public String getBriefStatusDesc() {
		return briefStatusDesc;
	}

	public void setBriefStatusDesc(String briefStatusDesc) {
		this.briefStatusDesc = briefStatusDesc;
	}

	@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
	public Date getDatumAangemaakt() {
		return datumAangemaakt;
	}

	public void setDatumAangemaakt(Date datumAangemaakt) {
		this.datumAangemaakt = datumAangemaakt;
	}

	@XmlJavaTypeAdapter(value = DateAdapter.class, type = Date.class)
	public Date getDatumFactuur() {
		return datumFactuur;
	}

	public void setDatumFactuur(Date datumFactuur) {
		this.datumFactuur = datumFactuur;
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
	public Integer getGebruikerAangemaaktId() {
		return gebruikerAangemaaktId;
	}

	public void setGebruikerAangemaaktId(Integer gebruikerId) {
		this.gebruikerAangemaaktId = gebruikerId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikerGeaccordeerdId() {
		return gebruikerGeaccordeerdId;
	}

	public void setGebruikerGeaccordeerdId(Integer gebruikerGeaccordeerdId) {
		this.gebruikerGeaccordeerdId = gebruikerGeaccordeerdId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikerLaatsteMutatieId() {
		return gebruikerLaatsteMutatieId;
	}

	public void setGebruikerLaatsteMutatieId(Integer gebruikerChangedId) {
		this.gebruikerLaatsteMutatieId = gebruikerChangedId;
	}

	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)
	public Integer getGebruikerVerwijderdId() {
		return gebruikerVerwijderdId;
	}

	public void setGebruikerVerwijderdId(Integer gebruikerVerwijderdId) {
		this.gebruikerVerwijderdId = gebruikerVerwijderdId;
	}

	@XmlElement(name = "meldingId", type = String.class)
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class)	
	public Integer getMeldingId() {
		return meldingId;
	}

	public void setMeldingId(Integer meldingId) {
		this.meldingId = meldingId;
	}

	@XmlElement
	public String getMeldingstatus() {
		return meldingstatus;
	}

	public void setMeldingstatus(String meldingstatus) {
		this.meldingstatus = meldingstatus;
	}

	@XmlElement
	public String getMeldingstatusCode() {
		return meldingstatusCode;
	}

	public void setMeldingstatusCode(String meldingstatusCode) {
		this.meldingstatusCode = meldingstatusCode;
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
	public String getRedenVerwijderenCode() {
		return redenVerwijderenCode;
	}

	public void setRedenVerwijderenCode(String redenVerwijderenCode) {
		this.redenVerwijderenCode = redenVerwijderenCode;
	}

	@XmlElement
	public String getRedenVerwijderenOmschrijving() {
		return redenVerwijderenOmschrijving;
	}

	public void setRedenVerwijderenOmschrijving(String redenVerwijderenOmschrijving) {
		this.redenVerwijderenOmschrijving = redenVerwijderenOmschrijving;
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
	public boolean isBedragWeergeven() {
		return bedragWeergeven;
	}

	public void setBedragWeergeven(boolean bedragWeergeven) {
		this.bedragWeergeven = bedragWeergeven;
	}

	@XmlElement
	public boolean isBedrijfsgegevensNietJuist() {
		return bedrijfsgegevensNietJuist;
	}

	public void setBedrijfsgegevensNietJuist(boolean bedrijfsgegevensNietJuist) {
		this.bedrijfsgegevensNietJuist = bedrijfsgegevensNietJuist;
	}

	@XmlElement
	public boolean isDoorBedrijfWeergeven() {
		return doorBedrijfWeergeven;
	}

	public void setDoorBedrijfWeergeven(boolean doorBedrijfWeergeven) {
		this.doorBedrijfWeergeven = doorBedrijfWeergeven;
	}

	@XmlElement
	public boolean isVerwerktInBatch() {
		return verwerktInBatch;
	}

	public void setVerwerktInBatch(boolean verwerktInBatch) {
		this.verwerktInBatch = verwerktInBatch;
	}

	@XmlElement
	public boolean isHasObjection() {
		return hasObjection;
	}

	public void setHasObjection(boolean hasObjection) {
		this.hasObjection = hasObjection;
	}

	@XmlElement
	public String getNotities() {
		return notities;
	}

	public void setNotities(String notities) {
		this.notities = notities;
	}

	@XmlElement
	public String getTelefoonNummerDebiteur() {
		return telefoonNummerDebiteur;
	}

	public void setTelefoonNummerDebiteur(String telefoonNummerDebiteur) {
		this.telefoonNummerDebiteur = telefoonNummerDebiteur;
	}

	@XmlElement
	public String getEmailAdresDebiteur() {
		return emailAdresDebiteur;
	}

	public void setEmailAdresDebiteur(String emailAdresDebiteur) {
		this.emailAdresDebiteur = emailAdresDebiteur;
	}

	

}
