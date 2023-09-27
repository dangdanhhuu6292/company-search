package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EHistorieType {
    VESTIGING("VES", "Vestiging", "Vestiging bedrijf"),
    VOORTZETTING("VOO", "Voortzetting", "Voortzetting van bedrijfsvoering"),
    AANVANG_SURSEANCE("ASU", "Surseance", "Aanvang surseance"),
    EINDE_SURSEANCE("ESU", "Surseance", "Einde surseance"),
    AANVANG_FAILLISSEMENT("AFA", "Faillissement", "Aanvang faillissement"),
    EINDE_FAILLISSEMENT("EFA", "Faillissement", "Einde faillissement"),
    ONTBINDING("ONT", "Ontbinding", "Ontbinding bedrijfsvoering"),
    OPHEFFING("OPH", "Opheffing", "Opheffing"),
    VERMELDING_INGEDIEND("VEI", "Vermelding", "Er is een vermelding ingediend"),
    VERMELDING_ACTIEF("VEA", "Vermelding", "Er is een vermelding actief"),
    VERMELDING_OPGELOST("VEO", "Vermelding", "Er is een vermelding opgelost"),
    VERMELDING_AFGEWEZEN("VEN", "Vermelding", "Er is een vermelding afgewezen");
    

    private static final Map<String, EHistorieType> lookup = new HashMap<String, EHistorieType>();

    static {
        for (EHistorieType g : EnumSet.allOf(EHistorieType.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
	private String type;
    private String omschrijving;
    
	EHistorieType(String code, String type, String omschrijving) {
		this.code = code;
		this.type = type;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }
    
    public String getType() {
    	return type;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EHistorieType get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
    
    public static boolean isInfo(String code) {
    	boolean result = false;
    	
        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	EHistorieType historieType = lookup.get(code);
        	
        	if (historieType == VESTIGING ||
        			historieType == VOORTZETTING)
        		result = true;        	
        }
        
        return result;
    }
    
    public static boolean isWarning(String code) {
    	boolean result = false;
    	
        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	EHistorieType historieType = lookup.get(code);
        	
        	if (historieType == ONTBINDING ||
        			historieType == OPHEFFING ||
        			historieType == EINDE_SURSEANCE ||
        			historieType == EINDE_FAILLISSEMENT ||
        			historieType == VERMELDING_INGEDIEND ||
        			historieType == VERMELDING_ACTIEF ||
        			historieType == VERMELDING_AFGEWEZEN ||
        			historieType == VERMELDING_AFGEWEZEN)
        		result = true;        	
        }
        
        return result;
    }  
    
    public static boolean isDanger(String code) {
    	boolean result = false;
    	
        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	EHistorieType historieType = lookup.get(code);
        	
        	if (historieType == AANVANG_FAILLISSEMENT ||
        			historieType == AANVANG_SURSEANCE)
        		result = true;        	
        }
        
        return result;
    }    
    
    
}

