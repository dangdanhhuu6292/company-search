package nl.devoorkant.sbdr.data.util.cir;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EAansturing {
	DEFAULT("DEF", "Default");

    private static final Map<String, EAansturing> lookup = new HashMap<String, EAansturing>();

    static {
        for (EAansturing g : EnumSet.allOf(EAansturing.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EAansturing(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EAansturing get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
    
}

