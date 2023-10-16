package test;

import nl.devoorkant.kvk.client.RestKVK;
import nl.devoorkant.kvk.client.impl.RestKVKImpl;
import nl.devoorkant.sbdr.data.model.CompanyInfo;

public class TestApI {

	public static void main(String[] args) {
		RestKVK restKVK = new RestKVKImpl();
		CompanyInfo companyInfos = restKVK.searchByKvKNummer("90003942", true);
	}

}
