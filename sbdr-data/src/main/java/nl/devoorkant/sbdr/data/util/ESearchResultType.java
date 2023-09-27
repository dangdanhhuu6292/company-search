package nl.devoorkant.sbdr.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ESearchResultType {
	KLANT("KLA", "", "Klant"),
    VERMELDING("VER", "", "Vermelding"),
    MONITORING("MON", "", "Monitoring"),
    RAPPORT("REP", "", "Rapport"),
    MOBILEAPPCHECK("MOC", "", "Mobile App check");

    private static final Map<String, ESearchResultType> lookup = new HashMap<String, ESearchResultType>();

    static {
        for (ESearchResultType g : EnumSet.allOf(ESearchResultType.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String prefix;
    private String omschrijving;
    
	ESearchResultType(String code, String prefix, String omschrijving) {
		this.code = code;
		this.prefix = prefix;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }
    
    public String getPrefix() {
    	return prefix;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static ESearchResultType get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}
