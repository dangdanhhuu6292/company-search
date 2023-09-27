package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EBevoegdheid {
    KLANT_BEHEER("KLB", "Klanten activeren/deactiveren"),
    MELDING_BEHEER("MLB", "Meldingen deactiveren"),
    MELDING_BEHEER_KLANT("MBK", "Meldingen deactiveren"),    
    ACTIVEREN_ACCOUNT("ACT", "Activeren account"),
    KLANTGEGEVENS_MUTEREN("KLA", "Klantgegevens muteren"),
    KLANTGEBRUIKER_BEHEER("KLG", "Klantgebruikers beheren"),
    SBDRGEBRUIKER_BEHEER("SBG", "SBDR gebruikers beheren"),
    BEDRIJFGEGEVENS_MUTEREN("BDR", "Bedrijfsgegevens muteren"),
    MELDING_INVOEREN("MLD", "Melding invoeren"),
    MONITORING_TOEVOEGEN("MON", "Monitoring toevoegen"),
    RAPPORT_INZIEN("REP", "Rapport inzien"),
    EXACTONLINE("EXO", "ExactOnline login"),
    WEBSITEPARAM("WSP", "Websiteparam bewerken"),
	SUPPORT_ADMIN("SAD", "Support admin acties"),
	ALERT_ADMIN_BEZWAAR("AAB", "Bezwaar alerts behandelen"),
	ADMIN_SBDR_HOOFD("ADM", "Administrator rol"),
	SBDR_MEDEWERKER("MWR", "Medewerker"),
	HOOFD_OF_KLANT("HOK", "Hoofd en/of klantgebruikers"),
	MELDING_AANPASSEN("MAP", "Melding aanpassen"),
	API_TOEGANG("API", "API toegang");

    private static final Map<String, EBevoegdheid> lookup = new HashMap<String, EBevoegdheid>();

    static {
        for (EBevoegdheid g : EnumSet.allOf(EBevoegdheid.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private String code;
    private String omschrijving;
    
	EBevoegdheid(String code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public String getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EBevoegdheid get(String code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}

