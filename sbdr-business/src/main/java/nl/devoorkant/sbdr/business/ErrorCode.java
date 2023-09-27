package nl.devoorkant.sbdr.business;

import nl.devoorkant.util.StringUtil;

/**
 * All available error codes for BBIZ.
 *  
 * @author Martijn Wiessenberg (mwiessenberg@devoorkant.nl)
 */
public enum ErrorCode {
	/** Er is een systeem fout opgetreden. Neem contact op met de applicatie beheerder */
	DVK_101001("DVK", 10, 10, 1, "Er is een systeem fout opgetreden. Neem contact op met de applicatie beheerder."),
    //DVK_101002("DVK", 10, 10, 2, "Sessie is alreeds afgesloten"),

    DVK_102003_personid("DVK", 10, 20, 3, "PersonID heeft ongeldige waarde."),
    DVK_102003_sessionid("DVK", 10, 20, 3, "SessionID heeft ongeldige waarde."),
    DVK_102003_callreferenceid("DVK", 10, 20, 3, "CallReferenceID heeft ongeldige waarde."),    
    DVK_102007_1("DVK", 10, 20, 7, "Combinatie Company, Label, Channel niet toegestaan."),
    DVK_102007_2("DVK", 10, 20, 7, "Combinatie CallRequestData en SessionID niet toegestaan."),
    DVK_102007_3("DVK", 10, 20, 7, "Combinatie CallRequestData en CallReferenceID niet toegestaan."),

    DVK_102009("DVK", 10, 20, 9, "SessionID is al gesloten."), 
    
    DVK_102010("DVK", 10, 20, 10, "Gebruik van service niet toegestaan."),
    
//    SCHEMA_VALIDATION_ERROR(10, "Schema validatie fout"),
//    LOGICAL_VALIDATION_ERROR(20, "Logische validatie fout"),
//    INVALID_SESSION_REFERENCE(30, "De sessie met de opgegeven referentie bestaat niet"),
//    INVALID_REQUEST_REFERENCE(31, "De toets met de opgegeven referentie bestaat niet"),
//    INVALID_PERSOON_IDENTIFICATIE(35, "De persoon met de opgegeven identificatie bestaat niet"),
//    INVALID_DEELNEMER_GROEP_CODE(40, "Ongeldige deelnemer groepcode"),    
//    INVALID_WEEKCODE(50, "Geen weekcode gevonden voor deelnemer"),
//    BKR_COMMUNICATION_FAILED(60, "Communicatie met BKR is mislukt"),
//    BKR_SOAP_FAULT(70, "SOAP Fault ontvangen van BKR"),
// 
//    INVALID_DEELNEMERNUMMER(80, "Ongeldig deelnemernummer"),
// 
//    INVALID_BATCHID(100, "Ongeldig batch ID"),
//    INVALID_BATCH_STATE(110, "De operatie kan niet worden uitgevoerd omdat de batch zich hiervoor in een onjuiste status bevindt"),
// 
//    INTERNAL_ERROR(500, "Interne fout"),
//    UNKNOWN_ERROR(1000, "Onbekende fout");
    
    BCON_101001_1("BCON", 10, 10, 1, "Er is een systeem fout in BCON opgetreden. Neem contact op met de applicatie beheerder."),
    BCON_101001_2("BCON", 10, 10, 1, "Communicatie met BKR mislukt."),
    BCON_101001_3("BCON", 10, 10, 1, "SOAP Fault ontvangen van BKR."),

    BCON_102003_1("BCON", 10, 20, 3, "Formaat invoer fout (xsd validatie is gefaald)."),
    BCON_102003_2("BCON", 10, 20, 3, "Logische validatie fout"),
    BCON_102003_3("BCON", 10, 20, 3, "De sessie-referentie heeft een ongeldige waarde."),
    BCON_102003_4("BCON", 10, 20, 3, "De toets-referentie heeft een ongeldige waarde."),
    BCON_102003_5("BCON", 10, 20, 3, "De persoon-referentie heeft een ongeldige waarde."),
    
    BCON_102004_1("BCON", 10, 20, 4, "Ongeldige deelnemer groepcode."),    
    BCON_102004_2("BCON", 10, 20, 4, "Geen geldige weekcode gevonden voor deelnemer."),    
    BCON_102004_3("BCON", 10, 20, 4, "Legitimatie onjuist."),    
    
	/** Time-out overschreden */
	BKR_101002("BKR", 10, 10, 2, "Time-out overschreden."),
	
	/** Toetssoort tijdelijk buiten gebruik */
	BKR_101003("BKR", 10, 10, 3, "Toetssoort tijdelijk buiten gebruik."),
	
	/** Toetssoort niet toegestaan */
	BKR_101004("BKR", 10, 10, 4, "Toetssoort niet toegestaan."),
	
	/** Geen proeftoetsing */
	BKR_101005("BKR", 10, 10, 5, "Geen proeftoetsing."),

	/** Legitimatie onjuist */
	BKR_102004("BKR", 10, 20, 4, "Legitimatie onjuist."),
	
	/** Formaat invoer fout */
	BKR_102005("BKR", 10, 20, 5, "Formaat invoer fout."),	

	/** Toetsing mislukt. Toets nogmaals. */
	BKR_102006("BKR", 10, 20, 6, "Toetsing mislukt. Toets nogmaals."),

    /** Monitormeldingen */
    /**     General:system */
    MON_101003("DVK", 10, 10, 3, "Initialisatie ESB niet gelukt. Neem contact op met de systeem beheerder."),

    /**     General:functional */
    MON_102004("DVK", 10, 20, 4, "GroupCode {} is onbekend."),

    /**     General:configuration. */
    MON_103001("DVK", 10, 30, 1, "ESB-endpoint {} is niet geconfigureerd in ESB."),
    MON_103002("DVK", 10, 30, 2, "Voor GroupCode {} is geen ESB-endpoint opgenomen in de database."),
    ;

	/** Origin of the error, ie. DVK or BKR */
	String origin;
	
	/** Error type, ie. general error, client error */
	int typeCode; // type of the error
	
	/** Classification of the error, ie. system error, functional error, formatting error */ 
	int classificationCode;
		
	/** Type/classification specific error code */
	int errorCode;
	
	/** Description of the error */
	String description;

	ErrorCode(String origin, int typeCode, int classificationCode, int errorCode, String description) {
		this.origin = origin;
		this.typeCode = typeCode;
		this.classificationCode = classificationCode;
		this.errorCode = errorCode;
		this.description = description;
	}
	
	/**
	 * Gets the complete error code in format: [origin]-[typeCode][classificationCode][errorCode]. For example: DVK-101001.
	 *  
	 * @return the complete error code as a String
	 */
	public String getErrorCode() {
		StringBuffer buf = new StringBuffer();
		buf.append(origin).append("-");
		buf.append(String.format("%02d", typeCode));
		buf.append(String.format("%02d", classificationCode));
		buf.append(String.format("%02d", errorCode));
		return buf.toString();
	}
	
	/**
	 * Gets the description of the error.
	 */
	public String getDescription() {
		return description;
	}

    /**
	 * Gets the description of the error.
	 */
	public String getDescription(String var) {
		return StringUtil.isNotEmptyOrNull(var) ? description.replace("{}", var): description.replace("{}", "");
	}
}