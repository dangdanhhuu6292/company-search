package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EInternalProcessType {
	BRIEF("BRF", "Verwerking brieven"),
	KLANT_BRIEF("BKL", "Verwerking klant brieven"),
	MELDING_BRIEF("BVM", "Verwerking vermelding brieven"),
	BATCH("BBT", "Verwerking brieven batch"),
	KLANT_BATCH("KBD", "Verwerking klant brieven batch"),
	MELDING_BATCH("MBD", "Verwerking melding brieven batch");

	private static final Map<String, EInternalProcessType> lookup = new HashMap<>();

	static {
		for(EInternalProcessType s : EnumSet.allOf(EInternalProcessType.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;

	EInternalProcessType(String code, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public static EInternalProcessType get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
