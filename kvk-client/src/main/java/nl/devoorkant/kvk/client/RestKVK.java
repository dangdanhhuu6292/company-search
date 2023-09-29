package nl.devoorkant.kvk.client;

import java.util.List;

import nl.devoorkant.sbdr.data.model.CompanyInfo;

public interface RestKVK {
	public List<CompanyInfo> searchByKvKNummer(String kvKNummer, boolean geoData);
}
