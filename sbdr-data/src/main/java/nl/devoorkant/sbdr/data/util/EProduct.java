package nl.devoorkant.sbdr.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EProduct {
    MONITORING("MON", "Monitoring abonnement"),
    RAPPORT("RAP", "Rapport opgevraagd"),
	DONATIE("DON", "Donatie tbv opgeloste achterstanden"),
	ACHTERSTANDCHECK("BAC", "Mobile app check"),
	VERMELDING("VER", "Vermelding indienen");

    private static final Map<String, EProduct> lookup = new HashMap<String, EProduct>();

    static {
        for (EProduct g : EnumSet.allOf(EProduct.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EProduct(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EProduct get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}

