package nl.devoorkant.kvk.client.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.kvk.client.RestKVK;
import nl.devoorkant.kvk.converter.CSConverter;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.model.CompanyInfo;
import nl.devoorkant.util.DateUtil;

public class RestKVKImpl implements RestKVK {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSConverter.class);
	@Override
	public CompanyInfo searchByKvKNummer(String kvKNummer, boolean geoData) {
		LOGGER.info("searchByKvKNummer begin");
		CompanyInfo companyInfo = new CompanyInfo();
		CSConverter csConverter = new CSConverter();
		try {		
			URL url;
			StringBuilder requestUrl = new StringBuilder("https://api.kvk.nl/api/v1/basisprofielen");
			if(!kvKNummer.trim().isEmpty()) {
				requestUrl.append("/");
				requestUrl.append(kvKNummer);
			}
			url = new URL(requestUrl.toString().trim());
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");			
			connection.setRequestProperty("apikey", "l7xxe43f87c4bbd9451db35fa4923480143e");
			
			//OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());			
			connection.connect();			
			//out.close();		
			int status = connection.getResponseCode();
			 switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + " ");				
				}
				LOGGER.info(sb.toString());	
				
				connection.disconnect();
				JSONObject response = new JSONObject(sb.toString());
				if (response.get("kvkNummer") != null 
						&& !response.get("kvkNummer").toString().trim().isEmpty()) {
					companyInfo = csConverter.transformToCompanyInfo(response);
					
				}
			 
			 }
			
	} catch (Exception e) {
		LOGGER.error("Error searchByKvKNummer ", e);
	} finally {
		LOGGER.info("End searchByKvKNummer : ");
		
		
	}

		return companyInfo;
	}
	@Override
	public List<CompanyInfo> searchByKvKNummers(String kvKNummer, boolean geoData) {
		LOGGER.info("searchByKvKNummer begin");
		List<CompanyInfo> companyInfo = new ArrayList<CompanyInfo>();
		CSConverter csConverter = new CSConverter();
		try {		
			URL url;
			StringBuilder requestUrl = new StringBuilder("https://api.kvk.nl/api/v1/basisprofielen");
			if(!kvKNummer.trim().isEmpty()) {
				requestUrl.append("/");
				requestUrl.append(kvKNummer);
			}
			url = new URL(requestUrl.toString().trim());
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");			
			connection.setRequestProperty("apikey", "l7xxe43f87c4bbd9451db35fa4923480143e");
			
			//OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());			
			connection.connect();			
			//out.close();		
			int status = connection.getResponseCode();
			 switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + " ");				
				}
				LOGGER.info(sb.toString());	
				
				connection.disconnect();
				JSONObject response = new JSONObject(sb.toString());
				if (response.get("kvkNummer") != null 
						&& !response.get("kvkNummer").toString().trim().isEmpty()) {
					companyInfo = csConverter.transformToCompanyInfoList(response);
					
				}
			 
			 }
			
	} catch (Exception e) {
		LOGGER.error("Error searchByKvKNummer ", e);
	} finally {
		LOGGER.info("End searchByKvKNummer : ");
		
		
	}

		return companyInfo;
	}
	@Override
	public CIKvKDossier getDossier(String kvKNummer, String vestigingsnummer,  boolean hoofdNeven) {

		LOGGER.info("searchByKvKNummer begin");
		CIKvKDossier ciKvKDossier = null;
		CIKvKDossier ciKvKDossierUpdate = null;
		CSConverter csConverter = new CSConverter();		
		try {						
			URL urlhoofdNeven;
			StringBuilder requestUrlhoofdNeven = new StringBuilder("https://api.kvk.nl/api/v1/vestigingsprofielen");
			
			requestUrlhoofdNeven.append("/");
			requestUrlhoofdNeven.append(vestigingsnummer);
			
			urlhoofdNeven = new URL(requestUrlhoofdNeven.toString().trim());
			HttpsURLConnection connectionhoofdNeven = (HttpsURLConnection) urlhoofdNeven.openConnection();
			connectionhoofdNeven.setDoOutput(true);
			connectionhoofdNeven.setRequestMethod("GET");			
			connectionhoofdNeven.setRequestProperty("apikey", "l7xxe43f87c4bbd9451db35fa4923480143e");
			
			//OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());			
			connectionhoofdNeven.connect();			
			//out.close();		
			int statushoofdNeven = connectionhoofdNeven.getResponseCode();
			 switch (statushoofdNeven) {
	            case 200:
	            case 201:
	                BufferedReader brhoofdNeven = new BufferedReader(new InputStreamReader(connectionhoofdNeven.getInputStream()));
	                StringBuilder sbhoofdNeven = new StringBuilder();
	                String linehoofdNeven;
				while ((linehoofdNeven = brhoofdNeven.readLine()) != null) {
					sbhoofdNeven.append(linehoofdNeven + " ");				
				}
				LOGGER.info(sbhoofdNeven.toString());	
				
				connectionhoofdNeven.disconnect();
				JSONObject responsehoofdNeven = new JSONObject(sbhoofdNeven.toString());
				ciKvKDossier = csConverter.transformVestToCIKvKDossInfo(responsehoofdNeven);
		
			 	}
				URL url;
				StringBuilder requestUrl = new StringBuilder("https://api.kvk.nl/api/v1/basisprofielen");
				if(!kvKNummer.trim().isEmpty()) {
					requestUrl.append("/");
					requestUrl.append(kvKNummer);
				}
				url = new URL(requestUrl.toString().trim());
				HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("GET");			
				connection.setRequestProperty("apikey", "l7xxe43f87c4bbd9451db35fa4923480143e");
				
				//OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());			
				connection.connect();			
				//out.close();		
				int status = connection.getResponseCode();
				 switch (status) {
		            case 200:
		            case 201:
		                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		                StringBuilder sb = new StringBuilder();
		                String line;
					while ((line = br.readLine()) != null) {
						sb.append(line + " ");				
					}
					LOGGER.info(sb.toString());	
					
					connection.disconnect();
					JSONObject response = new JSONObject(sb.toString());
					if(ciKvKDossier == null) {
						ciKvKDossier = csConverter.transformToCIKvKDossierInfo(response);
					}else {
						ciKvKDossierUpdate = csConverter.transformToCIKvKDossierInfo(response);
						if(ciKvKDossierUpdate != null && ciKvKDossierUpdate != null) {
							ciKvKDossier.setNrDochters(ciKvKDossierUpdate.getNrDochters());
							ciKvKDossier.setDatumHuidigeVestiging(ciKvKDossierUpdate.getDatumHuidigeVestiging());
							ciKvKDossier.setDatumInschrijving(ciKvKDossierUpdate.getDatumInschrijving());
							ciKvKDossier.setDatumOntbinding(ciKvKDossierUpdate.getDatumOntbinding());
							ciKvKDossier.setDatumOpheffing(ciKvKDossierUpdate.getDatumOpheffing());
							ciKvKDossier.setDatumUitschrijving(ciKvKDossierUpdate.getDatumUitschrijving());
							ciKvKDossier.setRv(ciKvKDossierUpdate.getRv());
							if(ciKvKDossier.getBedrijven() != null 
									&& !ciKvKDossier.getBedrijven().isEmpty()									
									&& ciKvKDossierUpdate.getBedrijven() != null 
											&& !ciKvKDossierUpdate.getBedrijven().isEmpty()) {
								Bedrijf[] bedrijfsUpdate = ciKvKDossierUpdate.getBedrijven().toArray(new Bedrijf[ciKvKDossier.getBedrijven().size()]);
								Bedrijf[] bedrijfsOri = ciKvKDossier.getBedrijven().toArray(new Bedrijf[ciKvKDossier.getBedrijven().size()]);
								bedrijfsOri[0].setRechtsvorm(bedrijfsUpdate[0].getRechtsvorm());
								ciKvKDossier.getBedrijven().clear();
								ciKvKDossier.getBedrijven().add(bedrijfsOri[0]);								
							}else if(ciKvKDossierUpdate.getBedrijven() != null 
									&& !ciKvKDossierUpdate.getBedrijven().isEmpty()) {
								ciKvKDossier.setBedrijven(ciKvKDossierUpdate.getBedrijven());
							}
							
						}
					}
			
				 }	
		} catch (Exception e) {
		LOGGER.error("Error getDossier ", e);
	} finally {
		LOGGER.info("End getDossier : ");		
		
	}

	return ciKvKDossier;
	
	}
	@Override
	public List<CompanyInfo> searchCompany(String kvKNummer, boolean iskvkNumber, boolean isCompanyName) {

		LOGGER.info("searchByKvKNummer begin");
		List<CompanyInfo> companyInfos = new ArrayList<CompanyInfo>();
		CSConverter csConverter = new CSConverter();
		try {		
			URL url;
			StringBuilder requestUrl = new StringBuilder("https://api.kvk.nl/api/v1/zoeken?");
			if(iskvkNumber) {
				requestUrl.append("kvkNummer=");
				requestUrl.append(kvKNummer);
			}
			if(isCompanyName) {
				requestUrl.append("handelsnaam=");
				requestUrl.append(kvKNummer.replace(" ", "_"));
			}
			url = new URL(requestUrl.toString());
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");			
			connection.setRequestProperty("apikey", "l7xxe43f87c4bbd9451db35fa4923480143e");
			
			//OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());			
			connection.connect();			
			//out.close();		
			int status = connection.getResponseCode();
			 switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + " ");				
				}
				LOGGER.info(sb.toString());	
				
				connection.disconnect();
				JSONObject response = new JSONObject(sb.toString());
				if (response.has("resultaten")) {
					companyInfos = csConverter.transformZoekenToCompanyInfoList(response);
					if (response.has("kvkNummer")) {
						CompanyInfo companyInfo = searchByKvKNummer(kvKNummer, true);
						if (companyInfo != null) {
							for (CompanyInfo company : companyInfos) {
								company.setWebsite(companyInfo.getWebsite() != null ? companyInfo.getWebsite(): "");
								company.setRechtsvorm(companyInfo.getRechtsvorm() != null ? companyInfo.getRechtsvorm(): "");
								company.setDatumLaatsteUpdate(companyInfo.getDatumLaatsteUpdate() != null ? companyInfo.getDatumLaatsteUpdate(): DateUtil.getCurrentDate());
							}
						}
					}
					
					
				}
			 
			 }
			
	} catch (Exception e) {
		LOGGER.error("Error searchByKvKNummer ", e);
	} finally {
		LOGGER.info("End searchByKvKNummer : ");
		
		
	}

		return companyInfos;
	
	}

}
