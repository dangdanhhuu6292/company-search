package nl.devoorkant.sbdr.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EReferentieInternType {
	BEDRIJF("BA", "BA-", "Bedrijf"),
	//KLANT("KL", "KL-", "Klant"),
	VERMELDING("VE", "VE-", "Vermelding"),
	MONITORING("MO", "MO-", "Monitoring"),
	RAPPORT("RE", "RE-", "Rapport"),
	SUPPORT("SU", "SU-", "Support"),
	CONTACT_MOMENT("CM", "CM-", "Contactmoment");

	private String code;
	private static final Map<String, EReferentieInternType> lookup = new HashMap<String, EReferentieInternType>();
	private String omschrijving;
	private String prefix;
	EReferentieInternType(String code, String prefix, String omschrijving) {
		this.code = code;
		this.prefix = prefix;
		this.omschrijving = omschrijving;
	}

	public static EReferentieInternType get(String code) {

		// Check if the key exists in the look-up table.
		if (lookup.containsKey(code)) {
			return lookup.get(code);
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public String getPrefix() {
		return prefix;
	}

	static {
		for (EReferentieInternType g : EnumSet.allOf(EReferentieInternType.class)) {
			lookup.put(g.getCode(), g);
		}
	}
}
