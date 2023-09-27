package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEMailType {
	ACCOUNT_ACTIVATION("ACT", "Account activatie"),
	ACCOUNT_VERIFICATION_SUCCESS("VER", "Gratis uitproberen"),
	ACTIVATION_REMINDER("RAC", "Herinnering account activatie"),
	VERIFICATION_REMINDER("VRM", "Herinnering verificatie"),
	PASSWORD_FORGOT("PFO", "Wachtwoord vergeten"),
	PASSWORD_CHANGE("PCH", "Wachtwoord aangepast"),
	NEW_USER("NUS", "Nieuwe gebruiker"),
	NEW_USER_CONFIRMATION("NUC", "Bevestiging nieuwe gebruiker"),
	NOTIFICATION_PROCESS("NPR", "Vermelding in behandeling"),
	NOTIFICATION_ON_HOLD("NOH", "Vermelding (nog) niet in behandeling"),
	NOTIFICATION_ACTIVE("NAC", "Vermelding opgenomen"),
	NOTIFICATION_NEW_INVOICE("NNI", "Uw rekening bekijken en betalen"),
	MONITORING_DETAILS("MDT", "Bedrijf in monitoring gewijzigd"),
	NOTIFICATION_RESOLVED("NRV", "Vordering voldaan"),
	REPORT_REQUESTED("RRD", "Rapport"),
	NOTIFICATION_REMOVED("NRM", "Vermelding verwijderd"),
	NEW_ALERT("NAL", "Nieuw bericht staat klaar"),
	NOTIFICATION_OBJECTION("NOB", "Bezwaar gemaakt tegen uw vermelding"),
	EXISTING_ACCOUNT("EAC", "Reeds bestaand account"),
	TEST("TST", "Test purpose");

	private String code;
	private String omschrijving;
	private static final Map<String, EEMailType> lookup = new HashMap<String, EEMailType>();

	static {
		for(EEMailType g : EnumSet.allOf(EEMailType.class)) {
			lookup.put(g.getCode(), g);
		}
	}

	EEMailType(String code, String omschrijving) {
		this.code = code;
		this.omschrijving = omschrijving;
	}

	public static EEMailType get(String code) {

		// Check if the key exists in the look-up table.
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}
}

