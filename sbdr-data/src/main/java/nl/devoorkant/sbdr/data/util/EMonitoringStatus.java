package nl.devoorkant.sbdr.data.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EMonitoringStatus {    
    ACTIEF("ACT", "Actief"),
    VERWIJDERD("DEL", "Verwijderd");

    private static final Map<String, EMonitoringStatus> lookup = new HashMap<String, EMonitoringStatus>();

    static {
        for (EMonitoringStatus g : EnumSet.allOf(EMonitoringStatus.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EMonitoringStatus(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EMonitoringStatus get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
    
    public static List<String> getAllCodes() {
    	List<String> result = new ArrayList<String>();
    	
    	result.add(ACTIEF.code);
    	result.add(VERWIJDERD.code);
    	
    	return result;
    }        
}
