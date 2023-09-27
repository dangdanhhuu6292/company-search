package nl.devoorkant.sbdr.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EDocumentType {
	AANMELDBRIEF("ABR", "ABR-", "Aanmeldbrief"),
    MELDINGBRIEF("MBR", "MBR-", "Meldingbrief"),
    RAPPORT("REP", "RA-", "Rapport"),
    MOBILEAPPCHECK("MOC", "MC-", "Mobile App check"),
    MONITORINGDETAIL("MON", "MD-", "Monitoring detail"),
    FACTUUR("FAC", "FA-", "Factuur"),
	KLANT_BATCH("KBT", "KB-", "Klant brieven batch"),
	MELDING_BATCH("MBT", "MB-", "Melding brieven batch");

    private static final Map<String, EDocumentType> lookup = new HashMap<String, EDocumentType>();

    static {
        for (EDocumentType g : EnumSet.allOf(EDocumentType.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
	private String prefix;
    private String omschrijving;
    
	EDocumentType(String code, String prefix, String omschrijving) {
		this.code = code;
		this.prefix = prefix;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }
    
    public String getPrefix() {
    	return prefix;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EDocumentType get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}
