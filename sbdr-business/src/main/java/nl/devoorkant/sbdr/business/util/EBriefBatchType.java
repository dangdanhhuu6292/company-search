package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EBriefBatchType {
	KLANT_BATCH("KLT", "Klant brieven batch"),
	MELDING_BATCH("MLD", "Melding brieven batch");

	private static final Map<String, EBriefBatchType> lookup = new HashMap<>();

	static {
		for(EBriefBatchType s : EnumSet.allOf(EBriefBatchType.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;

	EBriefBatchType(String code, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public static EBriefBatchType get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
