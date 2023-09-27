package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EAlertType {
    VERMELDING("VER", "Vermelding"),
    MONITORING("MON", "Monitoring"),
    SUPPORT("SUP", "Support"),
    CONTACT_MOMENT("CTM", "Contactmoment");

    private static final Map<String, EAlertType> lookup = new HashMap<String, EAlertType>();

    static {
        for (EAlertType g : EnumSet.allOf(EAlertType.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EAlertType(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EAlertType get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}

