package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;

public enum ESupportType {
	BEZWAAR("BZW", "Bezwaar tegen vermelding indienen"),
	PROBLEEM("PRB", "Probleem met de web applicatie melden"),
	VRAAG("VRG", "Vraag over de web applicatie stellen"),
	KLACHT("KLT", "Klacht indienen over de web applicatie"),
	SUGGESTIE("SGT","Suggestie indienen bij CRZB ");

	private static final Map<String, ESupportType> lookup = new HashMap<String, ESupportType>();

	static {
		for(ESupportType s : EnumSet.allOf(ESupportType.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;

	ESupportType(String code, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public static ESupportType get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
