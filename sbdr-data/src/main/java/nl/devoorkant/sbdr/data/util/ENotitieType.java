package nl.devoorkant.sbdr.data.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ENotitieType {
    MELDING_GEBRUIKER("NMG", "Notitie melding gebruiker"),
    MELDING_ADMIN("NMA", "Notitie melding admin"),
    CONTACT_MOMENT("CMO", "Contactmoment");

    private static final Map<String, ENotitieType> lookup = new HashMap<String, ENotitieType>();

    static {
        for (ENotitieType g : EnumSet.allOf(ENotitieType.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	ENotitieType(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static ENotitieType get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }

    public static List<String> getAllCodes() {
    	List<String> result = new ArrayList<String>();
    	
    	result.add(MELDING_GEBRUIKER.code);
    	result.add(MELDING_ADMIN.code);
    	result.add(CONTACT_MOMENT.code);
    	
    	return result;
    }
}

