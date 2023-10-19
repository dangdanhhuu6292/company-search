package nl.devoorkant.kvk.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.model.CompanyInfo;
import nl.devoorkant.util.DateUtil;


public class CSConverter {
	//private static String regexpcommentary_employees = "De onderneming heeft ([0-9]+) werknemers?\\."; // De onderneming heeft 4 werknemers.
	//private static String regexpcsid = "NL[0-9]{3}\\/[A-Z]\\/(\\w+)"; // NL007/X/380247390000
	//private static String regexphuisnummer = "([\\d]+)([\\s]*)([\\W]*)(.*)"; // 4 groups: housenr, spacing opt., other chars opt., suffix opt.
	private static final Logger ioLogger = LoggerFactory.getLogger(CSConverter.class);
	

	/**
	 * Constructs a CSConverter.
	 */
	public CSConverter() {
	}
	public static List<CompanyInfo> transformToCompanyInfoList(JSONObject searchResults) {
		List<CompanyInfo> companyInfos = new ArrayList<CompanyInfo>();
		CompanyInfo companyInfo = new CompanyInfo();
		
		JSONObject _embedded = new JSONObject(searchResults.get("_embedded").toString());
		if(_embedded != null) {			
			if (_embedded.has("hoofdvestiging")) {
				JSONObject hoofdvestiging = new JSONObject(_embedded.get("hoofdvestiging").toString());
				companyInfo.setBedrijfsNaam(hoofdvestiging.has("eersteHandelsnaam") ? hoofdvestiging.get("eersteHandelsnaam").toString().trim(): "");
				companyInfo.setVestiging(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				//companyInfo.setSub(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				companyInfo.setType(hoofdvestiging.has("indHoofdvestiging") ? hoofdvestiging.get("indHoofdvestiging").toString().trim(): "");
				companyInfo.setCreditSafeHeadQuarters("H");
				JSONArray adressen = new JSONArray(hoofdvestiging.get("adressen").toString());
				if (adressen != null) {					
					for (int i = 0; i < adressen.length(); i++) {						
						JSONObject adressenObject = adressen.getJSONObject(i);
						if(adressenObject.get("type") != null 
								&& adressenObject.get("type").toString().trim().equalsIgnoreCase("bezoekadres")) {
							companyInfo.setHuisNummer(adressenObject.has("postbusnummer") ? adressenObject.get("postbusnummer").toString().trim(): ""); 
							companyInfo.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
							companyInfo.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
							companyInfo.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
						}
						
						
					}
				}
				if(hoofdvestiging.has("websites")) {
					JSONArray websitesArray = new JSONArray(hoofdvestiging.get("websites").toString());				
					if (websitesArray.length() > 0) {
						companyInfo.setWebsite(websitesArray.getString(0));
					}
				}								
			}
			JSONObject eigenaar = new JSONObject(_embedded.get("eigenaar").toString());
			companyInfo.setRechtsvorm(eigenaar.has("rechtsvorm") ? eigenaar.get("rechtsvorm").toString().trim(): "");

		}
		companyInfo.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		companyInfo.setKvKnummerVerkort(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		companyInfo.setBedrijfsNaam(searchResults.has("naam") ? searchResults.get("naam").toString().trim(): "");
		if(searchResults.has("formeleRegistratiedatum")) {			
			Date datumLaatsteUpdate = convertUtilToDate(searchResults.getString("formeleRegistratiedatum").toString().trim(), "yyyyMMdd");
			companyInfo.setDatumLaatsteUpdate(datumLaatsteUpdate != null ? datumLaatsteUpdate :DateUtil.getCurrentDate());
		}
		companyInfos.add(companyInfo);
		return companyInfos;
		
	}

	public static CompanyInfo transformToCompanyInfo(JSONObject searchResults) {		
		CompanyInfo companyInfo = new CompanyInfo();
		
		JSONObject _embedded = new JSONObject(searchResults.get("_embedded").toString());
		if(_embedded != null) {			
			if (_embedded.has("hoofdvestiging")) {
				JSONObject hoofdvestiging = new JSONObject(_embedded.get("hoofdvestiging").toString());
				companyInfo.setBedrijfsNaam(hoofdvestiging.has("eersteHandelsnaam") ? hoofdvestiging.get("eersteHandelsnaam").toString().trim(): "");
				companyInfo.setVestiging(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				//companyInfo.setSub(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				companyInfo.setType(hoofdvestiging.has("indHoofdvestiging") ? hoofdvestiging.get("indHoofdvestiging").toString().trim(): "");
				JSONArray adressen = new JSONArray(hoofdvestiging.get("adressen").toString());
				if (adressen != null) {					
					for (int i = 0; i < adressen.length(); i++) {						
						JSONObject adressenObject = adressen.getJSONObject(i);
						if(adressenObject.get("type") != null 
								&& adressenObject.get("type").toString().trim().equalsIgnoreCase("bezoekadres")) {
							companyInfo.setHuisNummer(adressenObject.has("postbusnummer") ? adressenObject.get("postbusnummer").toString().trim(): ""); 
							companyInfo.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
							companyInfo.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
							companyInfo.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
						}
						
						
					}
				}
				if(hoofdvestiging.has("websites")) {
					JSONArray websitesArray = new JSONArray(hoofdvestiging.get("websites").toString());				
					if (websitesArray.length() > 0) {
						companyInfo.setWebsite(websitesArray.getString(0));
					}
				}								
			}
			JSONObject eigenaar = new JSONObject(_embedded.get("eigenaar").toString());
			companyInfo.setRechtsvorm(eigenaar.has("rechtsvorm") ? eigenaar.get("rechtsvorm").toString().trim(): "");

		}
		companyInfo.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		companyInfo.setKvKnummerVerkort(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		companyInfo.setBedrijfsNaam(searchResults.has("naam") ? searchResults.get("naam").toString().trim(): "");
		if(searchResults.has("formeleRegistratiedatum")) {			
			Date datumLaatsteUpdate = convertUtilToDate(searchResults.getString("formeleRegistratiedatum").toString().trim(), "yyyyMMdd");
			companyInfo.setDatumLaatsteUpdate(datumLaatsteUpdate != null ? datumLaatsteUpdate :DateUtil.getCurrentDate());
		}			
		return companyInfo;
		
	}
	
	public static CIKvKDossier transformToCIKvKDossierInfo(JSONObject searchResults) {		
		CIKvKDossier ciKvKDossier = new CIKvKDossier();
		Set<Bedrijf> bedrijven = new HashSet<Bedrijf>(0);
		Bedrijf bedrijf = new Bedrijf();
		JSONObject _embedded = new JSONObject(searchResults.get("_embedded").toString());
		if(_embedded != null) {
			if(_embedded.has("hoofdvestiging")) {
				JSONObject hoofdvestiging = new JSONObject(_embedded.get("hoofdvestiging").toString());
				if (hoofdvestiging != null) {
					ciKvKDossier.setVestigingsNummer(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
					ciKvKDossier.setDatumLaatsteUpdate(hoofdvestiging.has("formeleRegistratiedatum") ? convertUtilToDate(hoofdvestiging.getString("formeleRegistratiedatum").toString().trim(), "yyyyMMdd"): DateUtil.getCurrentDate());
					if(hoofdvestiging.has("indHoofdvestiging") && hoofdvestiging.get("indHoofdvestiging").toString().trim().equalsIgnoreCase("Ja")) {
						ciKvKDossier.setHoofdNeven("H");
					}else {
						ciKvKDossier.setHoofdNeven("N");
					}					
					JSONArray adressen = new JSONArray(hoofdvestiging.get("adressen").toString());
					if (adressen != null) {					
						for (int i = 0; i < adressen.length(); i++) {						
							JSONObject adressenObject = adressen.getJSONObject(i);
							if(adressenObject.get("type") != null 
									&& adressenObject.get("type").toString().trim().equalsIgnoreCase("bezoekadres")) {
								//ciKvKDossier.setHuisNummer(adressenObject.get("postbusnummer") != null ? adressenObject.get("postbusnummer").toString().trim(): ""); 
								ciKvKDossier.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
								ciKvKDossier.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
								ciKvKDossier.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
								ciKvKDossier.setHuisnummer(adressenObject.has("huisnummer") ? adressenObject.get("huisnummer").toString().trim(): "");
								ciKvKDossier.setStraatHuisnummer(adressenObject.has("huisnummer") ? adressenObject.get("huisnummer").toString().trim(): "");
								ciKvKDossier.setHuisnummerToevoeging(adressenObject.has("huisnummerToevoeging") ? adressenObject.get("huisnummerToevoeging").toString().trim(): "");
								bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
								bedrijf.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
								bedrijf.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
								bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): "");
							}
							
							if(adressenObject.get("type") != null 
									&& adressenObject.get("type").toString().trim().equalsIgnoreCase("correspondentieadres")) {
								//ciKvKDossier.setHuisNummer(adressenObject.get("postbusnummer") != null ? adressenObject.get("postbusnummer").toString().trim(): ""); 
								ciKvKDossier.setStraatCa("Postbus"); 
								ciKvKDossier.setPlaatsCa(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
								ciKvKDossier.setPostcodeCa(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
								ciKvKDossier.setHuisnummerToevoegingCa(adressenObject.has("huisnummerToevoeging") ? adressenObject.get("huisnummerToevoeging").toString().trim(): "");
								ciKvKDossier.setHuisnummerCa(adressenObject.has("huisnummer") ? adressenObject.get("huisnummer").toString().trim(): "");
								ciKvKDossier.setStraatHuisnummerCa(adressenObject.has("postbusnummer") ? adressenObject.get("postbusnummer").toString().trim(): "");
								bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
								bedrijf.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
								bedrijf.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");								
								bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): "");
							}
							
							
						}
					}
					if(hoofdvestiging.has("materieleRegistratie")) {
						JSONObject matRegis = new JSONObject(hoofdvestiging.get("materieleRegistratie").toString());				
						ciKvKDossier.setDatumHuidigeVestiging(matRegis.has("datumAanvang") ? convertUtilToDate(matRegis.getString("datumAanvang").toString().trim(), "yyyyMMdd"): null);
						ciKvKDossier.setDatumOntbinding(matRegis.has("datumEinde") ? convertUtilToDate(matRegis.getString("datumEinde").toString().trim(), "yyyyMMdd"): null);
						ciKvKDossier.setDatumOpheffing(matRegis.has("datumEinde") ? convertUtilToDate(matRegis.getString("datumEinde").toString().trim(), "yyyyMMdd"): null);
						ciKvKDossier.setDatumUitschrijving(matRegis.has("datumEinde") ? convertUtilToDate(matRegis.getString("datumEinde").toString().trim(), "yyyyMMdd"): null);
							
					}
					if(hoofdvestiging.has("websites")) {
						JSONArray websitesArray = new JSONArray(hoofdvestiging.get("websites").toString());				
						if (websitesArray.length() > 0) {
							ciKvKDossier.setDomeinNaam(websitesArray.getString(0));
						}		
					}
								
				}
			}
			if(_embedded.has("eigenaar")) {
				JSONObject eigenaar = new JSONObject(_embedded.get("eigenaar").toString());
				if (eigenaar != null) {
					ciKvKDossier.setRsin(eigenaar.has("rsin") ? eigenaar.get("rsin").toString().trim(): "");
					ciKvKDossier.setRv(eigenaar.has("rechtsvorm") ? eigenaar.get("rechtsvorm").toString().trim(): "");											
				}
			}
			
		}
		bedrijf.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		//ciKvKDossier.setBedrijfsNaam(searchResults.get("naam") != null ? searchResults.get("naam").toString().trim(): "");
		bedrijven.add(bedrijf);		
		ciKvKDossier.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		ciKvKDossier.setBedrijven(bedrijven);
		ciKvKDossier.setIndicatieFaillissement("N");
		ciKvKDossier.setIndicatieSurseance("N");
		ciKvKDossier.setDossierNr(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		ciKvKDossier.setVenNaam(searchResults.has("statutaireNaam") ? searchResults.get("statutaireNaam").toString().trim(): "");
		ciKvKDossier.setNrEmployees(searchResults.has("totaalWerkzamePersonen") ? Integer.parseInt(searchResults.get("totaalWerkzamePersonen").toString().trim()) : 0);
		ciKvKDossier.setNrDochters(searchResults.has("totaalAantalVestigingen") ? Integer.parseInt(searchResults.get("totaalAantalVestigingen").toString().trim()) : 0);
		ciKvKDossier.setDatumInschrijving(searchResults.has("formeleRegistratiedatum") ? convertUtilToDate(searchResults.getString("formeleRegistratiedatum").toString().trim(), "yyyyMMdd"): null);
		if(searchResults.has("handelsnamen")) {
			JSONArray handelsnamenArray = new JSONArray(searchResults.get("handelsnamen").toString());				
			if (handelsnamenArray.length() > 0) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(0);
				ciKvKDossier.setHandelsNaam(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}
			if (handelsnamenArray.length() > 1) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(1);
				ciKvKDossier.setHn1x2x30(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}
			if (handelsnamenArray.length() > 2) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(2);
				ciKvKDossier.setHn1x30(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}
			if (handelsnamenArray.length() > 3) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(3);
				ciKvKDossier.setHn1x45(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}			
			if (handelsnamenArray.length() > 4) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(4);
				ciKvKDossier.setHn2x2x30(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}
			
		}
		if(searchResults.has("sbiActiviteiten")) {
			JSONArray sbiActiviteitenArray = new JSONArray(searchResults.get("sbiActiviteiten").toString());				
			if (sbiActiviteitenArray.length() > 0) {
				JSONObject sbiActiviteiten = sbiActiviteitenArray.getJSONObject(0);
				ciKvKDossier.setHoofdActiviteit(sbiActiviteiten.has("sbiOmschrijving") ? sbiActiviteiten.get("sbiOmschrijving").toString().trim(): "");
				ciKvKDossier.setSbihoofdAct(sbiActiviteiten.has("sbiOmschrijving") ? sbiActiviteiten.get("sbiOmschrijving").toString().trim(): "");
				ciKvKDossier.setCiSbi1(sbiActiviteiten.has("sbiCode") ? sbiActiviteiten.get("sbiCode").toString().trim(): "");
			}
			if (sbiActiviteitenArray.length() > 1) {
				JSONObject sbiActiviteiten = sbiActiviteitenArray.getJSONObject(1);
				ciKvKDossier.setSbinevenActiviteit1(sbiActiviteiten.has("sbiOmschrijving") ? sbiActiviteiten.get("sbiOmschrijving").toString().trim(): "");
				ciKvKDossier.setCiSbi2(sbiActiviteiten.has("sbiCode") ? sbiActiviteiten.get("sbiCode").toString().trim(): "");
				
			}
			if (sbiActiviteitenArray.length() > 2) {
				JSONObject sbiActiviteiten = sbiActiviteitenArray.getJSONObject(2);;
				ciKvKDossier.setSbinevenActiviteit2(sbiActiviteiten.has("sbiOmschrijving") ? sbiActiviteiten.get("sbiOmschrijving").toString().trim(): "");
				ciKvKDossier.setCiSbi3(sbiActiviteiten.has("sbiCode") ? sbiActiviteiten.get("sbiCode").toString().trim(): "");
				
			}
			
		}
		return ciKvKDossier;
		
	}
	
	public static CIKvKDossier transformVestToCIKvKDossInfo(JSONObject searchResults) {		
		CIKvKDossier ciKvKDossier = new CIKvKDossier();
		Set<Bedrijf> bedrijven = new HashSet<Bedrijf>(0);
		Bedrijf bedrijf = new Bedrijf();			
		ciKvKDossier.setVestigingsNummer(searchResults.has("vestigingsnummer") ? searchResults.get("vestigingsnummer").toString().trim(): "");
		ciKvKDossier.setVenNaam(searchResults.has("statutaireNaam") ? searchResults.get("statutaireNaam").toString().trim(): "");	
		ciKvKDossier.setParent(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		bedrijf.setBedrijfsNaam(searchResults.has("eersteHandelsnaam") ? searchResults.get("eersteHandelsnaam").toString().trim(): "");			
		JSONArray adressen = new JSONArray(searchResults.get("adressen").toString());
		if (adressen != null) {					
			for (int i = 0; i < adressen.length(); i++) {						
				JSONObject adressenObject = adressen.getJSONObject(i);
				if(adressenObject.get("type") != null 
						&& adressenObject.get("type").toString().trim().equalsIgnoreCase("bezoekadres")) {
					ciKvKDossier.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
					ciKvKDossier.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
					ciKvKDossier.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");					
					ciKvKDossier.setHuisnummer(adressenObject.has("huisnummer") ? adressenObject.get("huisnummer").toString().trim(): "");
					ciKvKDossier.setHuisnummerToevoeging(adressenObject.has("huisnummerToevoeging") ? adressenObject.get("huisnummerToevoeging").toString().trim(): "");
					bedrijf.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
					bedrijf.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
					bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): "");
					bedrijf.setHuisNr(adressenObject.has("huisnummer") ? Integer.parseInt(adressenObject.get("huisnummer").toString().trim()): null);
					bedrijf.setHuisNrToevoeging(adressenObject.has("huisnummerToevoeging") ? adressenObject.get("huisnummerToevoeging").toString().trim(): "");
				}
				if(adressenObject.get("type") != null 
						&& adressenObject.get("type").toString().trim().equalsIgnoreCase("correspondentieadres")) {
					ciKvKDossier.setStraatCa("Postbus"); 
					ciKvKDossier.setPlaatsCa(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
					ciKvKDossier.setPostcodeCa(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");					
					ciKvKDossier.setHuisnummerCa(adressenObject.has("huisnummer") ? adressenObject.get("huisnummer").toString().trim(): "");
					ciKvKDossier.setStraatHuisnummerCa(adressenObject.has("postbusnummer") ? adressenObject.get("postbusnummer").toString().trim(): "");
					bedrijf.setPostPostbus(adressenObject.has("postbusnummer") ? adressenObject.get("postbusnummer").toString().trim(): "");
					bedrijf.setPostPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
					bedrijf.setPostPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): "");
					if(adressenObject.has("indHoofdvestiging") && adressenObject.get("indHoofdvestiging").toString().trim().equalsIgnoreCase("Ja")) {
						bedrijf.setHoofdNeven("H");
					}else {
						bedrijf.setHoofdNeven("N");
					}				
				}				
			}
		}
		if(searchResults.has("websites")) {
			JSONArray websitesArray = new JSONArray(searchResults.get("websites").toString());				
			if (websitesArray.length() > 0) {
				ciKvKDossier.setDomeinNaam(websitesArray.getString(0));			
			}	
		}
		if(searchResults.has("handelsnamen")) {
			JSONArray handelsnamenArray = new JSONArray(searchResults.get("handelsnamen").toString());				
			if (handelsnamenArray.length() > 0) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(0);
				ciKvKDossier.setHandelsNaam(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}
			if (handelsnamenArray.length() > 1) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(1);
				ciKvKDossier.setHn1x2x30(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}
			if (handelsnamenArray.length() > 2) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(2);
				ciKvKDossier.setHn1x30(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}
			if (handelsnamenArray.length() > 3) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(3);
				ciKvKDossier.setHn1x45(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}			
			if (handelsnamenArray.length() > 4) {
				JSONObject handelsnamen = handelsnamenArray.getJSONObject(4);
				ciKvKDossier.setHn2x2x30(handelsnamen.has("naam") ? handelsnamen.get("naam").toString().trim(): "");
			}
			
		}
		if(searchResults.has("sbiActiviteiten")) {
			JSONArray sbiActiviteitenArray = new JSONArray(searchResults.get("sbiActiviteiten").toString());				
			if (sbiActiviteitenArray.length() > 0) {
				JSONObject sbiActiviteiten = sbiActiviteitenArray.getJSONObject(0);
				ciKvKDossier.setHoofdActiviteit(sbiActiviteiten.has("sbiOmschrijving") ? sbiActiviteiten.get("sbiOmschrijving").toString().trim(): "");
				ciKvKDossier.setSbihoofdAct(sbiActiviteiten.has("sbiOmschrijving") ? sbiActiviteiten.get("sbiOmschrijving").toString().trim(): "");
				ciKvKDossier.setCiSbi1(sbiActiviteiten.has("sbiCode") ? sbiActiviteiten.get("sbiCode").toString().trim(): "");
			}
			if (sbiActiviteitenArray.length() > 1) {
				JSONObject sbiActiviteiten = sbiActiviteitenArray.getJSONObject(1);
				ciKvKDossier.setSbinevenActiviteit1(sbiActiviteiten.has("sbiOmschrijving") ? sbiActiviteiten.get("sbiOmschrijving").toString().trim(): "");
				ciKvKDossier.setCiSbi2(sbiActiviteiten.has("sbiCode") ? sbiActiviteiten.get("sbiCode").toString().trim(): "");
				
			}
			if (sbiActiviteitenArray.length() > 2) {
				JSONObject sbiActiviteiten = sbiActiviteitenArray.getJSONObject(2);;
				ciKvKDossier.setSbinevenActiviteit2(sbiActiviteiten.has("sbiOmschrijving") ? sbiActiviteiten.get("sbiOmschrijving").toString().trim(): "");
				ciKvKDossier.setCiSbi3(sbiActiviteiten.has("sbiCode") ? sbiActiviteiten.get("sbiCode").toString().trim(): "");
				
			}
		}
		ciKvKDossier.setRsin(searchResults.has("rsin") ? searchResults.get("rsin").toString().trim(): "");
		if(searchResults.has("indHoofdvestiging") && searchResults.get("indHoofdvestiging").toString().trim().equalsIgnoreCase("Ja")) {
			ciKvKDossier.setHoofdNeven("H");
		}else {
			ciKvKDossier.setHoofdNeven("N");
		}		
		ciKvKDossier.setNrEmployees(searchResults.has("totaalWerkzamePersonen") ? Integer.parseInt(searchResults.get("totaalWerkzamePersonen").toString().trim()) : 0);
		bedrijf.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		bedrijven.add(bedrijf);
		ciKvKDossier.setBedrijven(bedrijven);
		ciKvKDossier.setIndicatieFaillissement("N");
		ciKvKDossier.setIndicatieSurseance("N");
		ciKvKDossier.setDossierNr(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		ciKvKDossier.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		ciKvKDossier.setDatumLaatsteUpdate(searchResults.has("formeleRegistratiedatum") ? convertUtilToDate(searchResults.getString("formeleRegistratiedatum").toString().trim(), "yyyyMMdd"): DateUtil.getCurrentDate());
		ciKvKDossier.setDatumInschrijving(searchResults.has("formeleRegistratiedatum") ? convertUtilToDate(searchResults.getString("formeleRegistratiedatum").toString().trim(), "yyyyMMdd"): null);
		
		return ciKvKDossier;
		
	}
	
	public static List<CIKvKDossier> transformVestToCIKvKDossInfoList(JSONObject searchResults) {		
		List<CIKvKDossier> ciKvKDossiers = new ArrayList<>();		
		if(searchResults.has("vestigingen")) {
			JSONArray vestigingens = new JSONArray(searchResults.get("vestigingen").toString());
			for (int i = 0; i < vestigingens.length(); i++) {
				CIKvKDossier ciKvKDossier = new CIKvKDossier();
				ciKvKDossier.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
				JSONObject vestigingen = new JSONObject(vestigingens.get(i).toString());						
				ciKvKDossier.setVestigingsNummer(vestigingen.has("vestigingsnummer") ? vestigingen.get("vestigingsnummer").toString().trim(): "");
				ciKvKDossier.setVenNaam(vestigingen.has("eersteHandelsnaam") ? vestigingen.get("eersteHandelsnaam").toString().trim(): "");	
				if(vestigingen.has("indHoofdvestiging") && vestigingen.get("indHoofdvestiging").toString().trim().equalsIgnoreCase("Ja")) {
					ciKvKDossier.setHoofdNeven("H");
				}else {
					ciKvKDossier.setHoofdNeven("N");
				}
				ciKvKDossier.setStraat(vestigingen.has("volledigAdres") ? vestigingen.get("volledigAdres").toString().trim(): "");
				ciKvKDossier.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
				ciKvKDossiers.add(ciKvKDossier);
			}
			
		}
				
		return ciKvKDossiers;
		
	}
	
	public static CIKvKDossier transformToHoofdNeven(JSONObject searchResults) {		
		CIKvKDossier ciKvKDossier = new CIKvKDossier();
		
		JSONObject _embedded = new JSONObject(searchResults.get("_embedded").toString());
		if(_embedded != null) {
			JSONObject hoofdvestiging = new JSONObject(_embedded.get("hoofdvestiging").toString());
			if (hoofdvestiging != null) {
				ciKvKDossier.setVestigingsNummer(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				//ciKvKDossier.setSub(hoofdvestiging.get("vestigingsnummer") != null ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				JSONArray adressen = new JSONArray(hoofdvestiging.get("adressen").toString());
				if (adressen != null) {					
					for (int i = 0; i < adressen.length(); i++) {						
						JSONObject adressenObject = adressen.getJSONObject(i);
						if(adressenObject.get("type") != null 
								&& adressenObject.get("type").toString().trim().equalsIgnoreCase("correspondentieadres")) {
							//ciKvKDossier.setHuisNummer(adressenObject.get("postbusnummer") != null ? adressenObject.get("postbusnummer").toString().trim(): ""); 
							ciKvKDossier.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
							ciKvKDossier.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
							ciKvKDossier.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
						}
						
						
					}
				}
				JSONArray websitesArray = new JSONArray(hoofdvestiging.get("websites").toString());				
				if (websitesArray.length() > 0) {
					//ciKvKDossier.setWebsite(websitesArray.getString(0));
				}				
			}
			JSONObject eigenaar = new JSONObject(_embedded.get("eigenaar").toString());
			//ciKvKDossier.setRechtsvorm(eigenaar.get("rechtsvorm") != null ? eigenaar.get("rechtsvorm").toString().trim(): "");

		}
		ciKvKDossier.setKvKnummer(searchResults.get("kvkNummer") != null ? searchResults.get("kvkNummer").toString().trim(): "");
		//ciKvKDossier.setBedrijfsNaam(searchResults.get("naam") != null ? searchResults.get("naam").toString().trim(): "");
		return ciKvKDossier;
		
	}
	public static List<CompanyInfo> transformZoekenToCompanyInfoList(JSONObject searchResults) {
		List<CompanyInfo> companyInfos = new ArrayList<CompanyInfo>();
		
		JSONArray resultaten = new JSONArray(searchResults.get("resultaten").toString());
		if (resultaten != null) {					
			for (int i = 0; i < resultaten.length(); i++) {
				CompanyInfo companyInfo = new CompanyInfo();
				JSONObject resultatenObject = resultaten.getJSONObject(i);				
				companyInfo.setKvKnummer(resultatenObject.get("kvkNummer") != null ? resultatenObject.get("kvkNummer").toString().trim(): "");
				if (resultatenObject.has("vestigingsnummer")) {
					companyInfo.setVestiging(resultatenObject.has("vestigingsnummer")? resultatenObject.get("vestigingsnummer").toString().trim(): "");
					companyInfo.setSub(resultatenObject.has("vestigingsnummer") ? resultatenObject.get("vestigingsnummer").toString().trim(): "");
				}else {
					continue;
				}		
				companyInfo.setBedrijfsNaam(resultatenObject.has("handelsnaam") ? resultatenObject.get("handelsnaam").toString().trim(): "");
				companyInfo.setType(resultatenObject.has("type") ? resultatenObject.get("type").toString().trim(): "");
				companyInfo.setStraat(resultatenObject.has("straatnaam") ? resultatenObject.get("straatnaam").toString().trim(): "");
				companyInfo.setPlaats(resultatenObject.has("plaats") ? resultatenObject.get("plaats").toString().trim(): "");
				
				if(resultatenObject.has("type")) {
					if (resultatenObject.get("type") != null 
							&& resultatenObject.get("type").toString().trim().equalsIgnoreCase("nevenvestiging")) {
						companyInfo.setCreditSafeHeadQuarters("N");
					}else if(resultatenObject.get("type") != null 
							&& resultatenObject.get("type").toString().trim().equalsIgnoreCase("hoofdvestiging")) {
						companyInfo.setCreditSafeHeadQuarters("H");	
						
					}					
				}
				companyInfos.add(companyInfo);
			}
			
		}		
		return companyInfos;
		
	}
	public static Date convertUtilToDate(String poValue, String format) {
		Date datumLaatsteUpdate = null;
		try {
			datumLaatsteUpdate = new SimpleDateFormat(format).parse(poValue);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    
		return datumLaatsteUpdate;
		  
	  }
}
