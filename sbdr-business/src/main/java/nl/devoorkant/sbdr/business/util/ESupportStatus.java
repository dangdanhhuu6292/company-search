package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ESupportStatus {
	OPEN("OPN", "Supportticket staat open"),
	IN_BEHANDELING("IBH", "Supportticket wordt behandeld"),
	GESLOTEN("GST", "Supportticket is gesloten"),
	GEARCHIVEERD("ARC", "Supportticket is gearchiveerd");

	private static final Map<String, ESupportStatus> lookup = new HashMap<String, ESupportStatus>();

	static {
		for(ESupportStatus s : EnumSet.allOf(ESupportStatus.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;

	ESupportStatus(String code, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public static ESupportStatus get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
