package nl.devoorkant.sbdr.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import nl.devoorkant.sbdr.data.model.Rol;

public enum ERol {
    SBDR("SBDR", "SBDR gebruiker"),
    SBDRHOOFD("SBDRHOOFD", "SBDR Hoofd gebruiker"),    
    KLANT("KLANT", "Klantgebruiker"),    
    HOOFD("HOOFD", "Hoofdgebruiker"),
    MANAGED("MANAGED", "Managed klantgebruiker"),
    GEBRUIKER("GEBRUIKER", "Gebruiker"),
    SYSTEEM("AUTO", "SBDR Systeem"),
    REGISTRATIESTOEGESTAAN("INVOERREG", "Registraties toegestaan"),
    APITOEGESTAAN("API", "API calls toegestaan");

    private static final Map<String, ERol> lookup = new HashMap<String, ERol>();

    static {
        for (ERol g : EnumSet.allOf(ERol.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	ERol(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public Rol getRolObject(boolean actief) {
    	Rol rol = new Rol();
    	
    	rol.setActief(actief);
    	rol.setCode(this.code);
    	rol.setOmschrijving(this.omschrijving);
    	
    	return rol;
    }
    
    public static ERol get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}

