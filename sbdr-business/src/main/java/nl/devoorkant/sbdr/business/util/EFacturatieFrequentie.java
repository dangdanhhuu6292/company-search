package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EFacturatieFrequentie {
    WEKELIJKS("1WK", "Wekelijks"),
    TWEEWEKELIJKS("2WK", "Twee wekelijks"),
    MAANDELIJKS("MND", "Maandelijks"),
    TEST("TST", "Test");

    private static final Map<String, EFacturatieFrequentie> lookup = new HashMap<String, EFacturatieFrequentie>();

    static {
        for (EFacturatieFrequentie g : EnumSet.allOf(EFacturatieFrequentie.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EFacturatieFrequentie(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EFacturatieFrequentie get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}

