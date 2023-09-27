package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ESupportReden {
	//SupportType: Bezwaar
	BZR_VORDERING_VOLDAAN("BVV", ESupportType.BEZWAAR, "Vordering is voldaan"),
	BZR_REGELING_GETROFFEN("BRG", ESupportType.BEZWAAR, "Regeling is getroffen"),
	BZR_ORGANISATIE_ONBEKEND("BOO", ESupportType.BEZWAAR, "Organisatie is onbekend"),
	BZR_BETWIST_VORDERING("BBV", ESupportType.BEZWAAR, "Betwist de vordering"),
	BZR_FACTUUR_ONGEGROND("BFO", ESupportType.BEZWAAR, "Factuur is ongegrond"),
	BZR_ANDERS("BAN", ESupportType.BEZWAAR, "Overig"),

	//SupportType: Probleem
	PBM_BETALINGSACHTERSTAND("PBA", ESupportType.PROBLEEM, "Kan geen melding doen van betalingsachterstand"),
	PBM_GEBRUIKER("PGB", ESupportType.PROBLEEM, "Kan geen gebruiker toevoegen"),
	PBM_BEDRIJF("PBD", ESupportType.PROBLEEM, "Kan geen bedrijf opzoeken"),
	PBM_BEDRIJF_MONITOR("PBM", ESupportType.PROBLEEM, "Kan geen monitor op bedrijf plaatsen"),
	PBM_ANDERS("PAN", ESupportType.PROBLEEM, "Overig");

	private static final Map<String, ESupportReden> lookup = new HashMap<String, ESupportReden>();

	static {
		for(ESupportReden s : EnumSet.allOf(ESupportReden.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;
	private ESupportType supportType;

	ESupportReden(String code, ESupportType supportType, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving; this.supportType = supportType;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public ESupportType getSupportType() {
		return supportType;
	}

	public static ESupportReden get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
