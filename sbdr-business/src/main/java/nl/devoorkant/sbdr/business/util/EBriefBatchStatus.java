package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EBriefBatchStatus {
	GESTART("GST", "Verwerking batch is gestart"),
	VOLTOOID("VTD", "Verwerking batch is voltooid"),
	FOUT("ERR", "Fout in bewerking"),
	ONDERBROKEN("OBN", "Verwerking batch is onderbroken");

	private static final Map<String, EBriefBatchStatus> lookup = new HashMap<>();

	static {
		for(EBriefBatchStatus s : EnumSet.allOf(EBriefBatchStatus.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;

	EBriefBatchStatus(String code, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public static EBriefBatchStatus get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
