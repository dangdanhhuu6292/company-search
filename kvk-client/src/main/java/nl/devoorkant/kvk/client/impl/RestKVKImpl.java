package nl.devoorkant.kvk.client.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.kvk.client.RestKVK;
import nl.devoorkant.kvk.converter.CSConverter;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.model.CompanyInfo;

public class RestKVKImpl implements RestKVK {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSConverter.class);
	@Override
	public List<CompanyInfo> searchByKvKNummer(String kvKNummer, boolean geoData) {
		LOGGER.info("searchByKvKNummer begin");
		List<CompanyInfo> companyInfos = new ArrayList<CompanyInfo>();
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
					companyInfos = csConverter.transformToCompanyInfoList(response);
				}
			 
			 }
			
	} catch (Exception e) {
		LOGGER.error("Error searchByKvKNummer ", e);
	} finally {
		LOGGER.info("End searchByKvKNummer : ");
		
		
	}

		return companyInfos;
	}
	@Override
	public CIKvKDossier getDossier(String kvKNummer, String vestigingsnummer,  boolean hoofdNeven) {

		LOGGER.info("searchByKvKNummer begin");
		CIKvKDossier ciKvKDossier = null;
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
		if(ciKvKDossier == null) {
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
				ciKvKDossier = csConverter.transformToCIKvKDossierInfo(response);
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
