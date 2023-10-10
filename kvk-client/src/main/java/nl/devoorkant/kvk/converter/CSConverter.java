package nl.devoorkant.kvk.converter;

import java.util.ArrayList;
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
			JSONObject hoofdvestiging = new JSONObject(_embedded.get("hoofdvestiging").toString());
			if (hoofdvestiging != null) {
				companyInfo.setVestiging(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				companyInfo.setSub(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				JSONArray adressen = new JSONArray(hoofdvestiging.get("adressen").toString());
				if (adressen != null) {					
					for (int i = 0; i < adressen.length(); i++) {						
						JSONObject adressenObject = adressen.getJSONObject(i);
						if(adressenObject.get("type") != null 
								&& adressenObject.get("type").toString().trim().equalsIgnoreCase("correspondentieadres")) {
							companyInfo.setHuisNummer(adressenObject.has("postbusnummer") ? adressenObject.get("postbusnummer").toString().trim(): ""); 
							companyInfo.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
							companyInfo.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
							companyInfo.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
						}
						
						
					}
				}
				JSONArray websitesArray = new JSONArray(hoofdvestiging.get("websites").toString());				
				if (websitesArray.length() > 0) {
					companyInfo.setWebsite(websitesArray.getString(0));
				}				
			}
			JSONObject eigenaar = new JSONObject(_embedded.get("eigenaar").toString());
			companyInfo.setRechtsvorm(eigenaar.has("rechtsvorm") ? eigenaar.get("rechtsvorm").toString().trim(): "");

		}
		companyInfo.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		companyInfo.setBedrijfsNaam(searchResults.has("naam") ? searchResults.get("naam").toString().trim(): "");
		companyInfos.add(companyInfo);
		return companyInfos;
		
	}

	public static CompanyInfo transformToCompanyInfo(JSONObject searchResults) {		
		CompanyInfo companyInfo = new CompanyInfo();
		
		JSONObject _embedded = new JSONObject(searchResults.get("_embedded").toString());
		if(_embedded != null) {
			JSONObject hoofdvestiging = new JSONObject(_embedded.get("hoofdvestiging").toString());
			if (hoofdvestiging != null) {
				companyInfo.setVestiging(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				companyInfo.setSub(hoofdvestiging.has("vestigingsnummer") ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
				JSONArray adressen = new JSONArray(hoofdvestiging.get("adressen").toString());
				if (adressen != null) {					
					for (int i = 0; i < adressen.length(); i++) {						
						JSONObject adressenObject = adressen.getJSONObject(i);
						if(adressenObject.get("type") != null 
								&& adressenObject.get("type").toString().trim().equalsIgnoreCase("correspondentieadres")) {
							companyInfo.setHuisNummer(adressenObject.has("postbusnummer") ? adressenObject.get("postbusnummer").toString().trim(): ""); 
							companyInfo.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
							companyInfo.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
							companyInfo.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
						}
						
						
					}
				}
				JSONArray websitesArray = new JSONArray(hoofdvestiging.get("websites").toString());				
				if (websitesArray.length() > 0) {
					companyInfo.setWebsite(websitesArray.getString(0));
				}				
			}
			JSONObject eigenaar = new JSONObject(_embedded.get("eigenaar").toString());
			companyInfo.setRechtsvorm(eigenaar.has("rechtsvorm") ? eigenaar.get("rechtsvorm").toString().trim(): "");

		}
		companyInfo.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		companyInfo.setBedrijfsNaam(searchResults.has("naam") ? searchResults.get("naam").toString().trim(): "");
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
					//ciKvKDossier.setSub(hoofdvestiging.get("vestigingsnummer") != null ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
					ciKvKDossier.setVenNaam(hoofdvestiging.has("eersteHandelsnaam") ? hoofdvestiging.get("eersteHandelsnaam").toString().trim(): "");			
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
								bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
								bedrijf.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
								bedrijf.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
								ciKvKDossier.setHuisnummer(adressenObject.has("huisnummer") ? adressenObject.get("huisnummer").toString().trim(): "");
								bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): "");
							}
							
							
						}
					}
					JSONArray websitesArray = new JSONArray(hoofdvestiging.get("websites").toString());				
					if (websitesArray.length() > 0) {
						//ciKvKDossier.setWebsite(websitesArray.getString(0));
					}				
				}
			}
			else if(_embedded.has("eigenaar")) {
				JSONObject eigenaar = new JSONObject(_embedded.get("eigenaar").toString());
				if (eigenaar != null) {
					//ciKvKDossier.setVestigingsNummer(eigenaar.get("vestigingsnummer") != null ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
					//ciKvKDossier.setSub(hoofdvestiging.get("vestigingsnummer") != null ? hoofdvestiging.get("vestigingsnummer").toString().trim(): "");
					ciKvKDossier.setVenNaam(searchResults.has("naam") ? searchResults.get("naam").toString().trim(): "");			
					JSONArray adressen = new JSONArray(eigenaar.get("adressen").toString());
					if (adressen != null) {					
						for (int i = 0; i < adressen.length(); i++) {						
							JSONObject adressenObject = adressen.getJSONObject(i);
							if(adressenObject.get("type") != null 
									&& adressenObject.get("type").toString().trim().equalsIgnoreCase("bezoekadres")) {
								//ciKvKDossier.setHuisNummer(adressenObject.get("postbusnummer") != null ? adressenObject.get("postbusnummer").toString().trim(): ""); 
								ciKvKDossier.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
								ciKvKDossier.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
								ciKvKDossier.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
								bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
								bedrijf.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
								bedrijf.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
								ciKvKDossier.setHuisnummer(adressenObject.has("huisnummer") ? adressenObject.get("huisnummer").toString().trim(): "");
								bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): "");
							}
							
							
						}
					}								
				}
			}
			
		}
		ciKvKDossier.setDatumLaatsteUpdate(DateUtil.getCurrentTimestamp());
		ciKvKDossier.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		//ciKvKDossier.setBedrijfsNaam(searchResults.get("naam") != null ? searchResults.get("naam").toString().trim(): "");
		bedrijven.add(bedrijf);
		ciKvKDossier.setBedrijven(bedrijven);
		ciKvKDossier.setIndicatieFaillissement("N");
		ciKvKDossier.setIndicatieSurseance("N");
		ciKvKDossier.setDossierNr(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		return ciKvKDossier;
		
	}
	
	public static CIKvKDossier transformVestToCIKvKDossInfo(JSONObject searchResults) {		
		CIKvKDossier ciKvKDossier = new CIKvKDossier();
		Set<Bedrijf> bedrijven = new HashSet<Bedrijf>(0);
		Bedrijf bedrijf = new Bedrijf();			
		ciKvKDossier.setVestigingsNummer(searchResults.has("vestigingsnummer") ? searchResults.get("vestigingsnummer").toString().trim(): "");
		ciKvKDossier.setVenNaam(searchResults.has("eersteHandelsnaam") ? searchResults.get("eersteHandelsnaam").toString().trim(): "");			
		JSONArray adressen = new JSONArray(searchResults.get("adressen").toString());
		if (adressen != null) {					
			for (int i = 0; i < adressen.length(); i++) {						
				JSONObject adressenObject = adressen.getJSONObject(i);
				if(adressenObject.get("type") != null 
						&& adressenObject.get("type").toString().trim().equalsIgnoreCase("bezoekadres")) {
					//ciKvKDossier.setHuisNummer(adressenObject.get("postbusnummer") != null ? adressenObject.get("postbusnummer").toString().trim(): ""); 
					ciKvKDossier.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): ""); 
					ciKvKDossier.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
					ciKvKDossier.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");					
					bedrijf.setPlaats(adressenObject.has("plaats") ? adressenObject.get("plaats").toString().trim(): ""); 
					bedrijf.setPostcode(adressenObject.has("postcode") ? adressenObject.get("postcode").toString().trim(): "");
					ciKvKDossier.setHuisnummer(adressenObject.has("huisnummer") ? adressenObject.get("huisnummer").toString().trim(): "");
					bedrijf.setStraat(adressenObject.has("straatnaam") ? adressenObject.get("straatnaam").toString().trim(): "");
				}
				
				
			}
		}
		//JSONArray websitesArray = new JSONArray(searchResults.get("websites").toString());				
		//if (websitesArray.length() > 0) {
			//ciKvKDossier.setWebsite(websitesArray.getString(0));
		//}				
				
		ciKvKDossier.setDatumLaatsteUpdate(DateUtil.getCurrentTimestamp());
		ciKvKDossier.setKvKnummer(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		//ciKvKDossier.setBedrijfsNaam(searchResults.get("naam") != null ? searchResults.get("naam").toString().trim(): "");
		bedrijven.add(bedrijf);
		ciKvKDossier.setBedrijven(bedrijven);
		ciKvKDossier.setIndicatieFaillissement("N");
		ciKvKDossier.setIndicatieSurseance("N");
		ciKvKDossier.setDossierNr(searchResults.has("kvkNummer") ? searchResults.get("kvkNummer").toString().trim(): "");
		return ciKvKDossier;
		
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

}
