package nl.devoorkant.creditsafe.converter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ECreditSafeStatusType {
	/*
	 * For now searchcode and code must be unique.
	 */
    ACTIEF("Active", "Other", "Actief"),
    RECHTZAKEN("Pending", "Pending", "Actief"), // Rechtzaken
    NIETACTIEF("NonActive", "NonActive", "Niet actief"),
    OTHER("Other", "-", "Actief"); // was 'Niet actief'

    private static final Map<String, ECreditSafeStatusType> lookupSearchCode = new HashMap<String, ECreditSafeStatusType>();
    private static final Map<String, ECreditSafeStatusType> lookupCode = new HashMap<String, ECreditSafeStatusType>();

    static {
        for (ECreditSafeStatusType g : EnumSet.allOf(ECreditSafeStatusType.class)) {
            lookupSearchCode.put(g.getSearchCode(), g);
            lookupCode.put(g.getCode(), g);
        }
    }

    private String searchcode;
	private String code;
    private String omschrijving;
    
	ECreditSafeStatusType(String searchcode, String code, String omschrijving) {
		this.searchcode = searchcode;
		this.code = code;
        this.omschrijving = omschrijving;       
	}

	public String getSearchCode() {
		return searchcode;
	}
	
    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static ECreditSafeStatusType findByCode(String code) {

        // Check if the key exists in the look-up table.
        if (lookupCode.containsKey(code)) {
        	return lookupCode.get(code);
        }
        return null;
    }
    
    public static ECreditSafeStatusType findBySearchCode(String searchcode) {

        // Check if the key exists in the look-up table.
        if (lookupSearchCode.containsKey(searchcode)) {
        	return lookupSearchCode.get(searchcode);
        }
        return null;
    }    
}

