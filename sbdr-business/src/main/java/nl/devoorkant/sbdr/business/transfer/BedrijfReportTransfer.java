package nl.devoorkant.sbdr.business.transfer;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.CompanyInfo;

@XmlRootElement
public class BedrijfReportTransfer {
	private static final Logger LOGGER = LoggerFactory.getLogger(BedrijfReportTransfer.class);
	
	int aantalMeldingenActief = 0;
	int aantalMeldingenResolved = 0;
	int aantalMonitoringActief = 0;
	int ratingScore = 0;
	int aantalCrediteuren = 0;
	String ratingScoreIndicatorMessage = null;
	BigDecimal bedragOpenstaand = null;
	BigDecimal bedragResolved = null;
	BedrijfEntityTransfer bedrijf;
	BedrijfEntityTransfer bedrijfHoofd;
	BedrijfEntityTransfer bedrijfaanvrager;
	HistorieTransfer[] historie;
	KvkDossierTransfer kvkDossierTransfer;
	KvkDossierTransfer kvkDossierTransferHoofd;
	MeldingOverviewTransfer[] meldingen;
	ChartDataTransfer[] meldingenLastYear;
	OpmerkingenTransfer[] opmerkingen;
	CompanyInfoEntityTransfer parent;
	String referentieNummer;
	ChartDataTransfer[] reportsLastTwoWeeks;
	CompanyInfoEntityTransfer ultimateParent;
	List<CompanyInfo> vestigings;

	public BedrijfReportTransfer() {

	}

	public BedrijfReportTransfer(Bedrijf bedrijfaanvrager, Bedrijf bedrijf, Bedrijf bedrijfHoofd, KvkDossierTransfer kvkDossierTransfer, KvkDossierTransfer kvkDossierTransferHoofd, List<MeldingOverviewTransfer> meldingen, List<OpmerkingenTransfer> opmerkingen, List<HistorieTransfer> historie, String referentieNummer, int aantalMeldingenActief, int aantalMeldingenResolved, int aantalMonitoringActief, int aantalCrediteuren, BigDecimal bedragOpenstaand, BigDecimal bedragResolved, List<ChartDataTransfer> reportsLastTwoWeeks, List<ChartDataTransfer> meldingenLastYear, Integer ratingScore, String ratingIndicatorMessage, CompanyInfo parent, CompanyInfo ultimateParent, List<CompanyInfo> vestigings) {
		this.bedrijfaanvrager = bedrijfaanvrager != null ? new BedrijfEntityTransfer(bedrijfaanvrager) : null;
		this.bedrijf = bedrijf != null ? new BedrijfEntityTransfer(bedrijf) : null;
		this.bedrijfHoofd = bedrijfHoofd != null ? new BedrijfEntityTransfer(bedrijfHoofd) : null;
		this.kvkDossierTransfer = kvkDossierTransfer;
		this.kvkDossierTransferHoofd = kvkDossierTransferHoofd;
		this.meldingen = meldingen.toArray(new MeldingOverviewTransfer[meldingen.size()]);
		this.opmerkingen = opmerkingen.toArray(new OpmerkingenTransfer[opmerkingen.size()]);
		this.historie = historie.toArray(new HistorieTransfer[historie.size()]);
		this.referentieNummer = referentieNummer; //EDocumentType.RAPPORT.getCode() + "-" + bedrijfaanvrager.getSbdrNummer() + "-" + bedrijfaanvrager.getRapportCounter();
		this.aantalMeldingenActief = aantalMeldingenActief;
		this.aantalMeldingenResolved = aantalMeldingenResolved;
		this.aantalMonitoringActief = aantalMonitoringActief;
		this.aantalCrediteuren = aantalCrediteuren;
		if (ratingScore == null)
			this.ratingScore = -1;
		else
			this.ratingScore = ratingScore;
		this.ratingScoreIndicatorMessage = ratingIndicatorMessage;
		this.bedragOpenstaand = bedragOpenstaand;
		this.bedragResolved = bedragResolved;
		this.reportsLastTwoWeeks = reportsLastTwoWeeks.toArray(new ChartDataTransfer[reportsLastTwoWeeks.size()]);
		this.meldingenLastYear = meldingenLastYear.toArray(new ChartDataTransfer[meldingenLastYear.size()]);
		try {
			this.parent = parent != null ? new CompanyInfoEntityTransfer(parent) : null;
		} catch (Exception e) {
			LOGGER.error("Cannot create Parent for BedrijfReportTransfer");
		}
		try {
			this.ultimateParent = ultimateParent != null ? new CompanyInfoEntityTransfer(ultimateParent) : null;
		} catch (Exception e) {
			LOGGER.error("Cannot create UltimateParent for BedrijfReportTransfer");
		}
		this.vestigings = vestigings;
	}

	@XmlElement
	public int getAantalMeldingenActief() {
		return aantalMeldingenActief;
	}

	public void setAantalMeldingenActief(int aantalMeldingenActief) {
		this.aantalMeldingenActief = aantalMeldingenActief;
	}
	
	

//	@XmlElement
//	public int getAantalMeldingenResolved() {
//		return aantalMeldingenResolved;
//	}
//
//	public void setAantalMeldingenResolved(int aantalMeldingenResolved) {
//		this.aantalMeldingenResolved = aantalMeldingenResolved;
//	}

//	@XmlElement
//	public int getAantalMonitoringActief() {
//		return aantalMonitoringActief;
//	}
//
//	public void setAantalMonitoringActief(int aantalMonitoringActief) {
//		this.aantalMonitoringActief = aantalMonitoringActief;
//	}
	
	@XmlElement
	public int getAantalCrediteuren() {
		return aantalCrediteuren;
	}

	public void setAantalCrediteuren(int aantalCrediteuren) {
		this.aantalCrediteuren = aantalCrediteuren;
	}

	@XmlElement
	public int getRatingScore() {
		return ratingScore;
	}

	public void setRatingScore(int ratingScore) {
		this.ratingScore = ratingScore;
	}
	
	@XmlElement
	public String getRatingScoreIndicatorMessage() {
		return ratingScoreIndicatorMessage;
	}

	public void setRatingScoreIndicatorMessage(String ratingScoreIndicatorMessage) {
		this.ratingScoreIndicatorMessage = ratingScoreIndicatorMessage;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = MoneyAdapter.class, type = BigDecimal.class)
	public BigDecimal getBedragOpenstaand() {
		return bedragOpenstaand;
	}

	public void setBedragOpenstaand(BigDecimal bedragOpenstaand) {
		this.bedragOpenstaand = bedragOpenstaand;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = MoneyAdapter.class, type = BigDecimal.class)
	public BigDecimal getBedragResolved() {
		return bedragResolved;
	}

	public void setBedragResolved(BigDecimal bedragResolved) {
		this.bedragResolved = bedragResolved;
	}

	@XmlElement
	public BedrijfEntityTransfer getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(Bedrijf bedrijf) {
		this.bedrijf = bedrijf != null ? new BedrijfEntityTransfer(bedrijf) : null;
	}

	@XmlElement
	public BedrijfEntityTransfer getBedrijfHoofd() {
		return bedrijfHoofd;
	}

	public void setBedrijfHoofd(Bedrijf bedrijfHoofd) {
		this.bedrijfHoofd = bedrijfHoofd != null ? new BedrijfEntityTransfer(bedrijfHoofd) : null;
	}

	@XmlElement
	public BedrijfEntityTransfer getBedrijfaanvrager() {
		return bedrijfaanvrager;
	}

	public void setBedrijfaanvrager(Bedrijf bedrijfaanvrager) {
		this.bedrijfaanvrager = bedrijfaanvrager != null ? new BedrijfEntityTransfer(bedrijfaanvrager) : null;
	}

	@XmlElement
	public HistorieTransfer[] getHistorie() {
		return historie;
	}

	public void setHistorie(HistorieTransfer[] historie) {
		this.historie = historie;
	}

	@XmlElement
	public KvkDossierTransfer getKvkDossierTransfer() {
		return kvkDossierTransfer;
	}

	public void setKvkDossierTransfer(KvkDossierTransfer kvkDossierTransfer) {
		this.kvkDossierTransfer = kvkDossierTransfer;
	}

	@XmlElement
	public KvkDossierTransfer getKvkDossierTransferHoofd() {
		return kvkDossierTransferHoofd;
	}

	public void setKvkDossierTransferHoofd(KvkDossierTransfer kvkDossierTransferHoofd) {
		this.kvkDossierTransferHoofd = kvkDossierTransferHoofd;
	}

	@XmlElement
	public MeldingOverviewTransfer[] getMeldingen() {
		return meldingen;
	}

	public void setMeldingen(MeldingOverviewTransfer[] meldingen) {
		this.meldingen = meldingen;
	}

	@XmlElement
	public ChartDataTransfer[] getMeldingenLastYear() {
		return meldingenLastYear;
	}

	public void setMeldingenLastYear(ChartDataTransfer[] meldingenLastYear) {
		this.meldingenLastYear = meldingenLastYear;
	}

	@XmlElement
	public OpmerkingenTransfer[] getOpmerkingen() {
		return opmerkingen;
	}

	public void setOpmerkingen(OpmerkingenTransfer[] opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	@XmlElement
	public CompanyInfoEntityTransfer getParent() {
		return parent;
	}

	public void setParent(CompanyInfo parent) {
		this.parent = parent != null ? new CompanyInfoEntityTransfer(parent) : null;
	}

	@XmlElement
	public String getReferentieNummer() {
		return referentieNummer;
	}

	public void setReferentieNummer(String referentieNummer) {
		this.referentieNummer = referentieNummer;
	}

	@XmlElement
	public ChartDataTransfer[] getReportsLastTwoWeeks() {
		return reportsLastTwoWeeks;
	}

	public void setReportsLastTwoWeeks(ChartDataTransfer[] reportsLastTwoWeeks) {
		this.reportsLastTwoWeeks = reportsLastTwoWeeks;
	}

	@XmlElement
	public CompanyInfoEntityTransfer getUltimateParent() {
		return ultimateParent;
	}

	public void setUltimateParent(CompanyInfo ultimateParent) {
		this.ultimateParent = ultimateParent != null ? new CompanyInfoEntityTransfer(ultimateParent) : null;
	}
	@XmlElement
	public List<CompanyInfo> getVestigings() {
		return vestigings;
	}

	public void setVestigings(List<CompanyInfo> vestigings) {
		this.vestigings = vestigings;
	}


}
