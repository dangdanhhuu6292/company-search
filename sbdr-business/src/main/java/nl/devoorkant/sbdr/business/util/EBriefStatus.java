package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EBriefStatus {
	NIET_VERWERKT("NVW", "Brief is niet verwerkt"),
	IN_BATCH("IBT", "Brief zit in batch"),
	VERWERKT("VWT", "Brief is verwerkt"),
	GEDOWNLOAD("DWL", "Brief is gedownload"),
	DL_READY("KDL", "Klaar voor download");

	private static final Map<String, EBriefStatus> lookup = new HashMap<>();

	static {
		for(EBriefStatus s : EnumSet.allOf(EBriefStatus.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;

	EBriefStatus(String code, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public static EBriefStatus get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
