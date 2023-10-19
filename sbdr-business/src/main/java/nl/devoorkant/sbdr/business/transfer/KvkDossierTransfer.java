package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class KvkDossierTransfer {
	private String aanhef;
	private String achternaam;
	private String actief;
	private String actiefOmschrijving;
	private Boolean btwPlichtig;
	private String btwnummer;
	private Date datumFaillissementEinde;
	private Date datumFaillissementStart;
	private Date datumHuidigeVestiging;
	private Date datumInschrijving;
	private Date datumOnbinding;
	private Date datumOpheffing;
	private Date datumSurseanceEinde;
	private Date datumSurseanceStart;
	private Date datumUitschrijving;
	private Date datumVoorzetting;
	private String deponeringJaarstukken;
	private Integer dochters;
	private String domeinnaam;
	private String email;
	private String exportHandel;
	private String faillietSurseance;
	private String faillietSurseanceOmschrijving;
	private String functie;
	private Long gestortKapitaal;
	private String handelsNaam;
	private String hoofdNeven;
	private String hoofdactiviteit;
	private String hoofdactiviteitSbi;
	private String huisnummer;
	private String huisnummerToevoeging;
	private String importHandel;
	private String indFaillissement;
	private String indSurseance;
	private Boolean isKlant;
	private boolean isKvkContact;
	private Set<KvkBestuurderTransfer> kvkBestuurderTransfer;
	private Set<AandeelhouderTransfer> aandeelhoudersTransfer;
	private CuratorTransfer curatorTransfer;
	private String kvkNummer;
	private Date laatsteUpdate;
	private Integer medewerkers;
	private String mobielKvk;
	private String nevenactiviteit1;
	private String nevenactiviteit1Sbi;
	private String nevenactiviteit2;
	private String nevenactiviteit2Sbi;
	private Long nominaalAandelenKapitaal;
	private int nrOfActiveMonitorings;
	private int[] nrOfReports;
	private String parentKvKNummer;
	private String plaats;
	private String post_plaats;
	private String post_postbus;
	private String post_postcode;
	private String postcode;
	private String rechtsvorm;
	private String redenOpheffing;
	private String redenUitschrijving;
	private String rsin;
	private String sbdrNummer;
	private String straat;
	private String telefoon;
	private String telefoonKvk;
	private String ultimateParentKvKNummer;
	private String vennootschapsNaam;
	private String voornaam;
	private String vestigingsNummer;

	public KvkDossierTransfer() {

	}

	@XmlElement
	public String getAanhef() {
		return aanhef;
	}

	public void setAanhef(String aanhef) {
		this.aanhef = aanhef;
	}

	@XmlElement
	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	@XmlElement
	public String getActief() {
		return actief;
	}

	public void setActief(String actief) {
		this.actief = actief;
	}

	@XmlElement
	public String getActiefOmschrijving() {
		return actiefOmschrijving;
	}

	public void setActiefOmschrijving(String actiefOmschrijving) {
		this.actiefOmschrijving = actiefOmschrijving;
	}

	@XmlElement
	public Boolean getBtwPlichtig() {
		return btwPlichtig;
	}

	public void setBtwPlichtig(Boolean btwPlichtig) {
		this.btwPlichtig = btwPlichtig;
	}

	@XmlElement
	public String getBtwnummer() {
		return btwnummer;
	}

	public void setBtwnummer(String btwnummer) {
		this.btwnummer = btwnummer;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumFaillissementEinde() {
		return datumFaillissementEinde;
	}

	public void setDatumFaillissementEinde(Date datumFaillissementEinde) {
		this.datumFaillissementEinde = datumFaillissementEinde;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumFaillissementStart() {
		return datumFaillissementStart;
	}

	public void setDatumFaillissementStart(Date datumFaillissementStart) {
		this.datumFaillissementStart = datumFaillissementStart;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumHuidigeVestiging() {
		return datumHuidigeVestiging;
	}

	public void setDatumHuidigeVestiging(Date datumHuidigeVestiging) {
		this.datumHuidigeVestiging = datumHuidigeVestiging;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumInschrijving() {
		return datumInschrijving;
	}

	public void setDatumInschrijving(Date datumInschrijving) {
		this.datumInschrijving = datumInschrijving;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumOnbinding() {
		return datumOnbinding;
	}

	public void setDatumOnbinding(Date datumOnbinding) {
		this.datumOnbinding = datumOnbinding;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumOpheffing() {
		return datumOpheffing;
	}

	public void setDatumOpheffing(Date datumOpheffing) {
		this.datumOpheffing = datumOpheffing;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumSurseanceEinde() {
		return datumSurseanceEinde;
	}

	public void setDatumSurseanceEinde(Date datumSurseanceEinde) {
		this.datumSurseanceEinde = datumSurseanceEinde;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumSurseanceStart() {
		return datumSurseanceStart;
	}

	public void setDatumSurseanceStart(Date datumSurseanceStart) {
		this.datumSurseanceStart = datumSurseanceStart;
	}

	public Date getDatumUitschrijving() {
		return datumUitschrijving;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public void setDatumUitschrijving(Date datumUitschrijving) {
		this.datumUitschrijving = datumUitschrijving;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getDatumVoorzetting() {
		return datumVoorzetting;
	}

	public void setDatumVoorzetting(Date datumVoorzetting) {
		this.datumVoorzetting = datumVoorzetting;
	}

	public String getDeponeringJaarstukken() {
		return deponeringJaarstukken;
	}

	public void setDeponeringJaarstukken(String deponeringJaarstukken) {
		this.deponeringJaarstukken = deponeringJaarstukken;
	}

	@XmlElement
	public Integer getDochters() {
		return dochters;
	}

	public void setDochters(Integer dochters) {
		this.dochters = dochters;
	}

	@XmlElement
	public String getDomeinnaam() {
		return domeinnaam;
	}

	public void setDomeinnaam(String domeinnaam) {
		this.domeinnaam = domeinnaam;
	}

	@XmlElement
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement
	public String getExportHandel() {
		return exportHandel;
	}

	public void setExportHandel(String exportHandel) {
		this.exportHandel = exportHandel;
	}

	@XmlElement
	public String getFaillietSurseance() {
		return faillietSurseance;
	}

	public void setFaillietSurseance(String faillietSurseance) {
		this.faillietSurseance = faillietSurseance;
	}

	@XmlElement
	public String getFaillietSurseanceOmschrijving() {
		return faillietSurseanceOmschrijving;
	}

	public void setFaillietSurseanceOmschrijving(String faillietSurseanceOmschrijving) {
		this.faillietSurseanceOmschrijving = faillietSurseanceOmschrijving;
	}

	@XmlElement
	public String getFunctie() {
		return functie;
	}

	public void setFunctie(String functie) {
		this.functie = functie;
	}

	@XmlElement
	public Long getGestortKapitaal() {
		return gestortKapitaal;
	}

	public void setGestortKapitaal(Long gestortKapitaal) {
		this.gestortKapitaal = gestortKapitaal;
	}

	@XmlElement
	public String getHandelsNaam() {
		return handelsNaam;
	}

	public void setHandelsNaam(String handelsNaam) {
		this.handelsNaam = handelsNaam;
	}	

	@XmlElement
	public String getHoofdNeven() {
		return hoofdNeven;
	}

	public void setHoofdNeven(String hoofdNeven) {
		this.hoofdNeven = hoofdNeven;
	}

	@XmlElement
	public String getHoofdactiviteit() {
		return hoofdactiviteit;
	}

	public void setHoofdactiviteit(String hoofdactiviteit) {
		this.hoofdactiviteit = hoofdactiviteit;
	}

	@XmlElement
	public String getHoofdactiviteitSbi() {
		return hoofdactiviteitSbi;
	}

	public void setHoofdactiviteitSbi(String hoofdactiviteitSbi) {
		this.hoofdactiviteitSbi = hoofdactiviteitSbi;
	}

	@XmlElement
	public String getHuisnummer() {
		return huisnummer;
	}

	public void setHuisnummer(String huisnummer) {
		this.huisnummer = huisnummer;
	}

	@XmlElement
	public String getHuisnummerToevoeging() {
		return huisnummerToevoeging;
	}

	public void setHuisnummerToevoeging(String huisnummerToevoeging) {
		this.huisnummerToevoeging = huisnummerToevoeging;
	}

	@XmlElement
	public String getImportHandel() {
		return importHandel;
	}

	public void setImportHandel(String importHandel) {
		this.importHandel = importHandel;
	}

	@XmlElement
	public String getIndFaillissement() {
		return indFaillissement;
	}

	public void setIndFaillissement(String indFaillissement) {
		this.indFaillissement = indFaillissement;
	}

	@XmlElement
	public String getIndSurseance() {
		return indSurseance;
	}

	public void setIndSurseance(String indSurseance) {
		this.indSurseance = indSurseance;
	}

	@XmlElement
	public Set<KvkBestuurderTransfer> getKvkBestuurderTransfer() {
		return kvkBestuurderTransfer;
	}

	public void setKvkBestuurderTransfer(Set<KvkBestuurderTransfer> kvkBestuurderTransfer) {
		this.kvkBestuurderTransfer = kvkBestuurderTransfer;
	}

	@XmlElement
	public String getKvkNummer() {
		return kvkNummer;
	}

	public void setKvkNummer(String kvkNummer) {
		this.kvkNummer = kvkNummer;
	}

	@XmlElement
	@XmlJavaTypeAdapter(value = DateAdapterOverview.class, type = Date.class)
	public Date getLaatsteUpdate() {
		return laatsteUpdate;
	}

	public void setLaatsteUpdate(Date laatsteUpdate) {
		this.laatsteUpdate = laatsteUpdate;
	}

	@XmlElement
	public Integer getMedewerkers() {
		return medewerkers;
	}

	public void setMedewerkers(Integer medewerkers) {
		this.medewerkers = medewerkers;
	}

	@XmlElement
	public String getMobielKvk() {
		return mobielKvk;
	}

	public void setMobielKvk(String mobielKvk) {
		this.mobielKvk = mobielKvk;
	}

	@XmlElement
	public String getNevenactiviteit1() {
		return nevenactiviteit1;
	}

	public void setNevenactiviteit1(String nevenactiviteit1) {
		this.nevenactiviteit1 = nevenactiviteit1;
	}

	@XmlElement
	public String getNevenactiviteit1Sbi() {
		return nevenactiviteit1Sbi;
	}

	public void setNevenactiviteit1Sbi(String nevenactiviteit1Sbi) {
		this.nevenactiviteit1Sbi = nevenactiviteit1Sbi;
	}

	@XmlElement
	public String getNevenactiviteit2() {
		return nevenactiviteit2;
	}

	public void setNevenactiviteit2(String nevenactiviteit2) {
		this.nevenactiviteit2 = nevenactiviteit2;
	}

	@XmlElement
	public String getNevenactiviteit2Sbi() {
		return nevenactiviteit2Sbi;
	}

	public void setNevenactiviteit2Sbi(String nevenactiviteit2Sbi) {
		this.nevenactiviteit2Sbi = nevenactiviteit2Sbi;
	}

	@XmlElement
	public Long getNominaalAandelenKapitaal() {
		return nominaalAandelenKapitaal;
	}

	public void setNominaalAandelenKapitaal(Long nominaalAandelenKapitaal) {
		this.nominaalAandelenKapitaal = nominaalAandelenKapitaal;
	}

	@XmlElement
	public int getNrOfActiveMonitorings() {
		return nrOfActiveMonitorings;
	}

	public void setNrOfActiveMonitorings(int nrOfActiveMonitorings) {
		this.nrOfActiveMonitorings = nrOfActiveMonitorings;
	}

	@XmlElement
	public int[] getNrOfReports() {
		return nrOfReports;
	}

	public void setNrOfReports(int[] nrOfReports) {
		this.nrOfReports = nrOfReports;
	}

	@XmlElement
	public String getParentKvKNummer() {
		return parentKvKNummer;
	}

	public void setParentKvKNummer(String parentKvKNummer) {
		this.parentKvKNummer = parentKvKNummer;
	}

	@XmlElement
	public String getPlaats() {
		return plaats;
	}

	public void setPlaats(String plaats) {
		this.plaats = plaats;
	}

	@XmlElement
	public String getPost_plaats() {
		return post_plaats;
	}

	public void setPost_plaats(String post_plaats) {
		this.post_plaats = post_plaats;
	}

	@XmlElement
	public String getPost_postbus() {
		return post_postbus;
	}

	public void setPost_postbus(String post_postbus) {
		this.post_postbus = post_postbus;
	}

	@XmlElement
	public String getPost_postcode() {
		return post_postcode;
	}

	public void setPost_postcode(String post_postcode) {
		this.post_postcode = post_postcode;
	}

	@XmlElement
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@XmlElement
	public String getRechtsvorm() {
		return rechtsvorm;
	}

	public void setRechtsvorm(String rechtsvorm) {
		this.rechtsvorm = rechtsvorm;
	}

	@XmlElement
	public String getRedenOpheffing() {
		return redenOpheffing;
	}

	public void setRedenOpheffing(String redenOpheffing) {
		this.redenOpheffing = redenOpheffing;
	}

	@XmlElement
	public String getRedenUitschrijving() {
		return redenUitschrijving;
	}

	public void setRedenUitschrijving(String redenUitschrijving) {
		this.redenUitschrijving = redenUitschrijving;
	}

	@XmlElement
	public String getRsin() {
		return rsin;
	}

	public void setRsin(String rsin) {
		this.rsin = rsin;
	}

	@XmlElement
	public String getSbdrNummer() {
		return sbdrNummer;
	}

	public void setSbdrNummer(String sbdrNummer) {
		this.sbdrNummer = sbdrNummer;
	}

	@XmlElement
	public String getStraat() {
		return straat;
	}

	public void setStraat(String straat) {
		this.straat = straat;
	}

	@XmlElement
	public String getTelefoon() {
		return telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
	}

	@XmlElement
	public String getTelefoonKvk() {
		return telefoonKvk;
	}

	public void setTelefoonKvk(String telefoonKvk) {
		this.telefoonKvk = telefoonKvk;
	}

	@XmlElement
	public String getUltimateParentKvKNummer() {
		return ultimateParentKvKNummer;
	}

	public void setUltimateParentKvKNummer(String ultimateParentKvKNummer) {
		this.ultimateParentKvKNummer = ultimateParentKvKNummer;
	}

	@XmlElement
	public String getVennootschapsNaam() {
		return vennootschapsNaam;
	}

	public void setVennootschapsNaam(String vennootschapsNaam) {
		this.vennootschapsNaam = vennootschapsNaam;
	}

	@XmlElement
	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	@XmlElement
	public Boolean isKlant() {
		return isKlant;
	}

	public void setKlant(Boolean isKlant) {
		this.isKlant = isKlant;
	}

	@XmlElement
	public Boolean isKvkContact() {
		return isKvkContact;
	}

	public void setKvkContact(boolean isKvkContact) {
		this.isKvkContact = isKvkContact;
	}

	@XmlElement
	public Set<AandeelhouderTransfer> getAandeelhoudersTransfer() {
		return aandeelhoudersTransfer;
	}

	public void setAandeelhoudersTransfer(
			Set<AandeelhouderTransfer> aandeelhoudersTransfer) {
		this.aandeelhoudersTransfer = aandeelhoudersTransfer;
	}

	@XmlElement
	public CuratorTransfer getCuratorTransfer() {
		return curatorTransfer;
	}

	public void setCuratorTransfer(CuratorTransfer curatorTransfer) {
		this.curatorTransfer = curatorTransfer;
	}

	public String getVestigingsNummer() {
		return vestigingsNummer;
	}

	public void setVestigingsNummer(String vestigingsNummer) {
		this.vestigingsNummer = vestigingsNummer;
	}


}
