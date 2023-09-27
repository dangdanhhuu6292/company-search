package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EInternalProcessStatus {
	BRF_AFGEDRUKT("BAD", EInternalProcessType.BRIEF, "Brief is gedownload"),
	BRF_VERWERKT("BVD", EInternalProcessType.BRIEF, "Brief is verwerkt"),
	BTH_KLAAR_DL("BDL", EInternalProcessType.BATCH, "Batch staat klaar voor download"),
	BTH_DOWNLOADED("BDD", EInternalProcessType.BATCH, "Batch is gedownload"),
	BTH_VERWERKT("BVW", EInternalProcessType.BATCH, "Batch is verwerkt"),
	BRF_KLAAR_DL("BKD", EInternalProcessType.BRIEF, "Brief is klaar voor download");

	private static final Map<String, EInternalProcessStatus> lookup = new HashMap<String, EInternalProcessStatus>();

	static {
		for(EInternalProcessStatus s : EnumSet.allOf(EInternalProcessStatus.class)) {
			lookup.put(s.getCode(), s);
		}
	}

	private String code;
	private String omschrijving;
	private EInternalProcessType internalProcessType;

	EInternalProcessStatus(String code, EInternalProcessType internalProcessType, String omschrijving) {
		this.code = code; this.omschrijving = omschrijving; this.internalProcessType = internalProcessType;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public EInternalProcessType getSupportType() {
		return internalProcessType;
	}

	public static EInternalProcessStatus get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		} return null;
	}
}
