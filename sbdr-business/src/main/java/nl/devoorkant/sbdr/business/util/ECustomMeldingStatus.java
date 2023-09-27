package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ECustomMeldingStatus {
    INBEHANDELING("INB", "Inbehandeling"), 
    VERVALLEN("VRV", "Vervallen"), 
    VERWERKT("VRW", "Verwerkt"); 

    private static final Map<String, ECustomMeldingStatus> lookup = new HashMap<String, ECustomMeldingStatus>();

    static {
        for (ECustomMeldingStatus g : EnumSet.allOf(ECustomMeldingStatus.class)) {
            lookup.put(g.getCode(), g);
        }
    }
    
	private String code;
    private String omschrijving;
    
	ECustomMeldingStatus(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

}

