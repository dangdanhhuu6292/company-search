package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;

public enum EVatType {
	PCT_0("0", "0 procent BTW"),
	PCT_21("21", "21 procent BTW");

	private static final Map<String, EVatType> lookup = new HashMap<String, EVatType>();

	static {
		for(EVatType s : EnumSet.allOf(EVatType.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;

	EVatType(String code, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public static EVatType get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
