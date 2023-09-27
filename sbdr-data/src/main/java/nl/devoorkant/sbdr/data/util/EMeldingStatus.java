package nl.devoorkant.sbdr.data.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EMeldingStatus {
	DATA_NOK("NOK", "Bedrijfsgegevens niet juist"),
	GEBLOKKEERD("BLK", "Geblokkeerd"), // on hold igv bezwaar via route post
	//MBR 20-12-2015 removed: INITIEEL("INI", "In behandeling"), // Ingediend
    INBEHANDELING("INB", "In behandeling"), // Ingediend, in behandeling
    AFGEWEZEN("AFW", "Afgewezen"),
    ACTIEF("ACT", "Opgenomen"),
    VERWIJDERD("DEL", "Verwijderd");

    private static final Map<String, EMeldingStatus> lookup = new HashMap<String, EMeldingStatus>();

    static {
        for (EMeldingStatus g : EnumSet.allOf(EMeldingStatus.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EMeldingStatus(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EMeldingStatus get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }

    public static List<String> getActief() {
    	List<String> result = new ArrayList<String>();

    	result.add(ACTIEF.code);
    	
    	return result;
    }  
    
    public static List<String> getActiefAndVerwijderd() {
    	List<String> result = new ArrayList<String>();

		result.add(VERWIJDERD.code);
    	result.add(ACTIEF.code);
    	
    	return result;
    }  
    
    public static List<String> getAllCodes() {
    	List<String> result = new ArrayList<String>();
    	
    	result.add(DATA_NOK.code);
    	//result.add(INITIEEL.code);
    	result.add(INBEHANDELING.code);
    	result.add(ACTIEF.code);
    	result.add(VERWIJDERD.code);
    	result.add(AFGEWEZEN.code);
    	
    	return result;
    }  
    
    public static boolean isRemoved(String code) {
		return code != null && (code.equals(VERWIJDERD.getCode()) || code.equals(AFGEWEZEN.getCode()));
    }
} 
