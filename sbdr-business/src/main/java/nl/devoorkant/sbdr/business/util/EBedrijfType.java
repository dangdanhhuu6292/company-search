package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EBedrijfType {
    HOOFD("H", "Hoofdvestiging"),
    NEVEN("N", "Nevenvestiging");

    private static final Map<String, EBedrijfType> lookup = new HashMap<String, EBedrijfType>();

    static {
        for (EBedrijfType g : EnumSet.allOf(EBedrijfType.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EBedrijfType(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EBedrijfType get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}

