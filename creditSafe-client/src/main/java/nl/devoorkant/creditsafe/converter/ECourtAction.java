package nl.devoorkant.creditsafe.converter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public enum ECourtAction {
	IN_SURSEANCE(new String[]{"Suspension of Payments", "admission to interim moratorium meeting of creditors"}, "In surseance"),
	IN_FAILLISSEMENT(new String[]{"declaration of bankruptcy", "replacement of administrator"}, "In faillissement"),
	FAILLIET(new String[]{"Bankruptcy (NL)", "declaration of bankruptcy after termination of moratorium"}, "Faillissement afgerond");

	private String[] codes;
	private String omschrijving;
	private static final Map<String[], ECourtAction> lookup = new HashMap<String[], ECourtAction>();

	static {
		for(ECourtAction g : EnumSet.allOf(ECourtAction.class)) {
			lookup.put(g.getCodes(), g);
		}
	}

	ECourtAction(String[] codes, String omschrijving) {
		this.codes = codes;
		this.omschrijving = omschrijving;
	}

	public static ECourtAction get(String code) {
		ECourtAction result = null;
		
		// Check if the key exists in the look-up table.
		for (Entry<String[], ECourtAction> item : lookup.entrySet()) {
			for (String itemcode : item.getKey()) {
				if (itemcode.equalsIgnoreCase(code))
					result = item.getValue();
			}
		}

		return result;
	}

	public String[] getCodes() {
		return codes;
	}

	public String getOmschrijving() {
		return omschrijving;
	}
}