package nl.devoorkant.kvk.client;

import java.util.List;

import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.model.CompanyInfo;

public interface RestKVK {
	public List<CompanyInfo> searchByKvKNummer(String kvKNummer, boolean geoData);
	public CIKvKDossier getDossier(String kvKNummer, String vestigingsnummer, boolean hoofdNeven);
	public List<CompanyInfo> searchCompany(String kvKNummer, boolean iskvkNumber, boolean isCompanyName);
}
