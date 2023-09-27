package nl.devoorkant.sbdr.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public enum ERedenVerwijderenMelding {
    ADMINSBDR("ADM", "De vordering is door betalingsachterstanden.nl verwijderd", "0", false),
	VOLDAAN("VOL", "De vordering is voldaan", "1", false),
	REGELING("REG", "Er is een regeling getroffen", "2", false),
	FOUT("FOU", "De opgegeven vermelding was niet juist", "3", false),
	DERDEPARTIJ("PAR", "Vordering is overgedragen aan derde partij", "4", false),
	ANDERS("AND", "Anders", "5", false),
	BEZWAAR("BZW", "Er is geldig bezwaar aangetekend tegen de vermelding", "6", false),

	ADMIN_VOLDAAN("AVL", "De vordering is door betalingsachterstanden.nl verwijderd: De vordering is voldaan", "7", true),
	ADMIN_REGELING("ARG", "De vordering is door betalingsachterstanden.nl verwijderd: Er is een regeling getroffen", "8", true),
	ADMIN_FOUT("AFO", "De vordering is door betalingsachterstanden.nl verwijderd: De opgegeven vermelding was niet juist", "9", true),
	ADMIN_DERDEPARTIJ("APR", "De vordering is door betalingsachterstanden.nl verwijderd: Vordering is overgedragen aan derde partij", "10", true),
	ADMIN_ANDERS("AAN", "De vordering is door betalingsachterstanden.nl verwijderd: Anders", "11", true),
	ADMIN_BEZWAAR("ABW", "De vordering is door betalingsachterstanden.nl verwijderd: Er is geldig bezwaar aangetekend tegen de vermelding", "12", true);

    private static final Map<String, ERedenVerwijderenMelding> lookup = new HashMap<String, ERedenVerwijderenMelding>();

    static {
        for (ERedenVerwijderenMelding g : EnumSet.allOf(ERedenVerwijderenMelding.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    private String frontendCode;
	private boolean adminReason;
    
	ERedenVerwijderenMelding(String code, String omschrijving, String frontendCode, boolean adminReason) {
		this.code = code;
        this.omschrijving = omschrijving;  
        this.frontendCode = frontendCode;
		this.adminReason = adminReason;
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }
    
    public String getFrontendCode() {
    	return frontendCode;
    }

	public boolean isAdminReason(){return adminReason;}

    public static ERedenVerwijderenMelding get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
    
    public static ERedenVerwijderenMelding getByFrontendCode(String frontendCode) {

    	Map.Entry<String, ERedenVerwijderenMelding> pairs = null;
    	
    	Iterator<Entry<String, ERedenVerwijderenMelding>> it = lookup.entrySet().iterator();
    	boolean notfound = true;
        while (notfound && it.hasNext()) {
            pairs = (Map.Entry<String, ERedenVerwijderenMelding>)it.next();
            
            // Check if the key exists in the look-up table.
            if (pairs.getValue().getFrontendCode().equals(frontendCode))
            	notfound = false;
        }  

        if (!notfound && pairs != null)
        	return pairs.getValue();
        else
        	return null;
    }    
}

