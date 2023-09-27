package nl.devoorkant.sbdr.data.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public enum EKlantStatus {
	DATA_NOK("NOK", "Bedrijfsgegevens niet juist"),
	REGISTRATIE("REG", "In registratie"),
	PROSPECT("PRO", "Voorlopig account"),
    ACTIEF("ACT", "Actief"),
    GEBLOKKEERD("BLK", "Geblokkeerd"),
    VERVALLEN("INV", "Vervallen"),
    VERWIJDERD("DEL", "Verwijderd"),
    AFGEWEZEN("AFW", "Afgewezen prospect");

    private static final Map<String, EKlantStatus> lookup = new HashMap<String, EKlantStatus>();

    static {
        for (EKlantStatus g : EnumSet.allOf(EKlantStatus.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EKlantStatus(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EKlantStatus get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }

	public static List<String> getAllNonVerifiedKlantCodes(){
		List<String> result = new ArrayList<String>();

		result.add(PROSPECT.code);

		return result;
	}
	
	public static List<String> getAllNewKlantCodes(){
		List<String> result = new ArrayList<String>();

		result.add(DATA_NOK.code);
		result.add(REGISTRATIE.code);
		result.add(PROSPECT.code);

		return result;
	}
    
    public static List<String> getAllCodes() {
    	List<String> result = new ArrayList<String>();
    	
    	result.add(DATA_NOK.code);
    	result.add(REGISTRATIE.code);
    	result.add(PROSPECT.code);
    	result.add(ACTIEF.code);
    	result.add(GEBLOKKEERD.code);
    	result.add(VERVALLEN.code);
    	result.add(VERWIJDERD.code);
    	result.add(AFGEWEZEN.code);
    	
    	return result;
    }

    public static List<String> getAllIncompleteCodes() {
    	List<String> result = new ArrayList<String>();
    	
    	result.add(DATA_NOK.code);
    	result.add(REGISTRATIE.code);
    	result.add(PROSPECT.code);
    	result.add(GEBLOKKEERD.code);
    	
    	return result;
    }
    
    public static boolean canBeActivated(String code) {
		return code != null && (code.equals(DATA_NOK.getCode()) || code.equals(REGISTRATIE.getCode()));
    }
    
    public static boolean isAllowedToLogin(String code) {
		return code != null && (code.equals(DATA_NOK.getCode()) || code.equals(REGISTRATIE.getCode()) || code.equals(PROSPECT.getCode()) || code.equals(ACTIEF.getCode()));
    }
}

