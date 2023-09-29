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
			StringBuilder requestUrl = new StringBuilder("https://api.kvk.nl/test/api/v1/basisprofielen");
			if(!kvKNummer.trim().isEmpty()) {
				requestUrl.append("/");
				requestUrl.append(kvKNummer);
			}
			url = new URL(requestUrl.toString().trim());
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");			
			connection.setRequestProperty("apikey", "l7xx1f2691f2520d487b902f4e0b57a0b197");
			
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

}
