package nl.devoorkant.sbdr.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ELogType {
    INFORMATIE("INF", "Informatie"),
    WAARSCHUWING("WAR", "Waarschuwing"),    
    FOUT("ERR", "Fout");

    private static final Map<String, ELogType> lookup = new HashMap<String, ELogType>();

    static {
        for (ELogType g : EnumSet.allOf(ELogType.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	ELogType(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static ELogType get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}

