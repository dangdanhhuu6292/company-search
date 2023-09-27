package nl.devoorkant.sbdr.business.wrapper;

import javax.xml.bind.annotation.XmlElement;

public class ErrorService {
	public static String GENERAL_FAILURE = "101";
	public static String GENERAL_FAILURE_MSG = "Opdracht kon niet worden verwerkt.";

	public static String USERNAME_ALREADY_EXISTS = "102";
	public static String USERNAME_ALREADY_EXISTS_MSG = "Er is al een registratie met dit e-mailadres. Voer een ander e-mailadres in.";

	public static String USERNAME_NOT_EXISTS = "103";
	public static String USERNAME_NOT_EXISTS_MSG = "Gebruikersnaam bestaat niet.";

	public static String USER_NOT_ACTIVATED = "104";
	public static String USER_NOT_ACTIVATED_MSG = "Gebruiker kon niet worden geactiveerd.";

	public static String ACCOUNT_NOT_ACTIVATED = "104";
	public static String ACCOUNT_NOT_ACTIVATED_MSG = "Account kon niet worden geactiveerd.";

	public static String CANNOT_CHANGE_WACHTWOORD = "105";
	public static String CANNOT_CHANGE_WACHTWOORD_MSG = "Wachtwoord kan niet worden gewijzigd.";

	public static String PASSWORD_STATUS_MISMATCH = "106";
	public static String PASSWORD_STATUS_MISMATCH_MSG = "Wachtwoord kan niet worden gewijzigd.";

	public static String ACTIVATIONID_MISMATCH = "107";
	public static String ACTIVATIONID_MISMATCH_MSG = "Wachtwoord kan niet worden gewijzigd.";

	public static String CANNOT_REMOVE_MELDING = "108";
	public static String CANNOT_REMOVE_MELDING_MSG = "Melding kan niet worden verwijderd.";

	public static String CANNOT_REMOVE_MONITORING = "117";
	public static String CANNOT_REMOVE_MONITORING_MSG = "Monitoring kan niet worden verwijderd.";

	public static String CANNOT_SAVE_COMPANY = "109";
	public static String CANNOT_SAVE_COMPANY_MSG = "Kan bedrijf niet opslaan.";

	public static String NO_KLANTUPDATE_DATA = "110";
	public static String NO_KLANTUPDATE_DATA_MSG = "Klantgegevens onjuist. Opslaan niet gelukt.";

	public static String CANNOT_SAVE_MONITORING = "111";
	public static String CANNOT_SAVE_MONITORING_MSG = "Kan monitoring niet opslaan.";

	public static String COMPANY_ACCOUNT_EXISTS = "113";
	public static String COMPANY_ACCOUNT_EXISTS_MSG = "Voor bovenstaand bedrijf is al een account aangemaakt. Mogelijk heeft u of iemand binnen uw onderneming eerder een account aangemaakt?";

	public static String CANNOT_ACTIVATE_KLANT = "114";
	public static String CANNOT_ACTIVATE_KLANT_MSG = "Kan klant niet activeren.";

	public static String CANNOT_REMOVE_KLANT = "115";
	public static String CANNOT_REMOVE_KLANT_MSG = "Kan klant niet verwijderen.";

	public static String CANNOT_SAVE_USER = "116";
	public static String CANNOT_SAVE_USER_MSG = "Kan gebruiker niet opslaan.";

	public static String NOTIFICATIONREFERENCE_ALREADY_EXISTS = "118";
	public static String NOTIFICATIONREFERENCE_ALREADY_EXISTS_MSG = "Referentie van vermelding bestaat al.";

	public static String CANNOT_SAVE_MELDING = "119";
	public static String CANNOT_SAVE_MELDING_MSG = "Kan vermelding niet opslaan.";

	public static String COMPANYEXCEPTION_NOT_RESOLVED = "120";
	public static String COMPANYEXCEPTION_NOT_RESOLVED_MSG = "Bedrijf melding kon niet worden aangepast.";

	public static String CANNOT_FIND_ALERT = "121";
	public static String CANNOT_FIND_ALERT_MSG = "Kan alert niet vinden.";

	public static String SUPPORT_NOT_VALID = "122";
	public static String SUPPORT_NOT_VALID_MSG = "Supportticket kon niet gevalideerd worden.";

	public static String CANNOT_SAVE_SUPPORT = "123";
	public static String CANNOT_SAVE_SUPPORT_MSG = "Kan supportticket niet opslaan.";

	public static String CANNOT_SAVE_SUPPORT_FILE = "124";
	public static String CANNOT_SAVE_SUPPORT_FILE_MSG = "Kan bijlage van supportticket niet opslaan.";

	public static String CANNOT_SAVE_REACTION = "125";
	public static String CANNOT_SAVE_REACTION_MSG = "Dit supportticket kan niet met deze methode opgeslagen worden";

	public static String SUPPORT_IS_EMPTY = "126";
	public static String SUPPORT_IS_EMPTY_MSG = "Het support object is null";

	public static String SUPPORT_FILE_IS_EMPTY = "127";
	public static String SUPPORT_FILE_IS_EMPTY_MSG = "Het supportBestand object is null";

	public static String PARAMETER_IS_EMPTY = "128";
	public static String PARAMETER_IS_EMPTY_MSG = "Een of meer parameters zijn niet ingevuld";

	public static String NO_OBJECTS_FOUND = "129";
	public static String NO_OBJECTS_FOUND_MSG = "Er is niets gevonden met de gegeven parameters";

	public static String CANNOT_CREATE_ALERT = "130";
	public static String CANNOT_CREATE_ALERT_MSG = "Er kon geen alert aangemaakt worden";

	public static String INVALID_DISCOUNT_CODE = "131";
	public static String INVALID_DISCOUNT_CODE_MSG = "Voer een geldige BA-code in";

	public static String EXPIRED_DISCOUNT_CODE = "132";
	public static String EXPIRED_DISCOUNT_CODE_MSG = "Voer een geldige BA-code in";

	public static String WRONG_ACTIVATION_CODE = "133";
	public static String WRONG_ACTIVATION_CODE_MSG = "Activatiecode is onjuist";

	public static String CREATE_ACC_COMPANY_DELETED = "134";
	public static String CREATE_ACC_COMPANY_DELETED_MSG = "Er bestaat al een account bij CRZB.";

	public static String USER_CREATED_NOTIFICATION = "135";
	public static String USER_CREATED_NOTIFICATION_MSG = "U bent niet de gebruiker die deze vermelding gemaakt heeft";

	public static String PASSWORD_REQUEST_TOO_SOON = "136";
	public static String PASSWORD_REQUEST_TOO_SOON_MSG = "U moet minimaal een uur wachten voordat u opnieuw een aanvraag mag doen";

	public static String PASSWORD_REQUEST_RESENT = "137";
	public static String PASSWORD_REQUEST_RESENT_MSG = "U heeft al een wachtwoord reset aangevraagd, maar er is opnieuw een email naar uw adres gestuurd";

	public static String WRONG_PASSWORD = "138";
	public static String WRONG_PASSWORD_MSG = "Verkeerd (huidig) wachtwoord ingevoerd.";

	public static String COMPANY_BLOCKED = "139";
	public static String COMPANY_BLOCKED_MSG = "Er kan geen account worden aangemaakt. Uw bedrijf is geblokkeerd.";

	public static String MANAGED_USER_NOT_EXISTS = "140";
	public static String MANAGED_USER_NOT_EXISTS_MSG = "Een managed account user moet al een bestaand account hebben.";
	
	//141
	String errorMsg;
	String errorCode;

	public ErrorService() {

	}

	public ErrorService(String errorCode, String errorMsg)
	{
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public ErrorService(String errorCode) {
		this.errorCode = errorCode;

		if(errorCode.equals(USERNAME_ALREADY_EXISTS)) this.errorMsg = USERNAME_ALREADY_EXISTS_MSG;
		else if(errorCode.equals(USERNAME_NOT_EXISTS)) this.errorMsg = USERNAME_NOT_EXISTS_MSG;
		else if(errorCode.equals(USER_NOT_ACTIVATED)) this.errorMsg = USER_NOT_ACTIVATED_MSG;
		else if(errorCode.equals(ACCOUNT_NOT_ACTIVATED)) this.errorMsg = ACCOUNT_NOT_ACTIVATED_MSG;
		else if(errorCode.equals(CANNOT_CHANGE_WACHTWOORD)) this.errorMsg = CANNOT_CHANGE_WACHTWOORD_MSG;
		else if(errorCode.equals(PASSWORD_STATUS_MISMATCH)) this.errorMsg = PASSWORD_STATUS_MISMATCH_MSG;
		else if(errorCode.equals(ACTIVATIONID_MISMATCH)) this.errorMsg = ACTIVATIONID_MISMATCH_MSG;
		else if(errorCode.equals(CANNOT_REMOVE_MONITORING)) this.errorMsg = CANNOT_REMOVE_MONITORING_MSG;
		else if(errorCode.equals(CANNOT_REMOVE_MELDING)) this.errorMsg = CANNOT_REMOVE_MELDING_MSG;
		else if(errorCode.equals(CANNOT_SAVE_COMPANY)) this.errorMsg = CANNOT_SAVE_COMPANY_MSG;
		else if(errorCode.equals(NO_KLANTUPDATE_DATA)) this.errorMsg = NO_KLANTUPDATE_DATA_MSG;
		else if(errorCode.equals(CANNOT_SAVE_MONITORING)) this.errorMsg = CANNOT_SAVE_MONITORING_MSG;
		else if(errorCode.equals(COMPANY_ACCOUNT_EXISTS)) this.errorMsg = COMPANY_ACCOUNT_EXISTS_MSG;
		else if(errorCode.equals(CANNOT_ACTIVATE_KLANT)) this.errorMsg = CANNOT_ACTIVATE_KLANT_MSG;
		else if(errorCode.equals(CANNOT_REMOVE_KLANT)) this.errorMsg = CANNOT_REMOVE_KLANT_MSG;
		else if(errorCode.equals(CANNOT_SAVE_USER)) this.errorMsg = CANNOT_SAVE_USER_MSG;
		else if(errorCode.equals(NOTIFICATIONREFERENCE_ALREADY_EXISTS)) this.errorMsg = NOTIFICATIONREFERENCE_ALREADY_EXISTS_MSG;
		else if(errorCode.equals(CANNOT_SAVE_MELDING)) this.errorMsg = CANNOT_SAVE_MELDING_MSG;
		else if(errorCode.equals(COMPANYEXCEPTION_NOT_RESOLVED)) this.errorMsg = COMPANYEXCEPTION_NOT_RESOLVED_MSG;
		else if(errorCode.equals(CANNOT_FIND_ALERT)) this.errorMsg = CANNOT_FIND_ALERT_MSG;
		else if(errorCode.equals(SUPPORT_NOT_VALID)) this.errorMsg = SUPPORT_NOT_VALID_MSG;
		else if(errorCode.equals(CANNOT_SAVE_SUPPORT)) this.errorMsg = CANNOT_SAVE_SUPPORT_MSG;
		else if(errorCode.equals(CANNOT_SAVE_SUPPORT_FILE)) this.errorMsg = CANNOT_SAVE_SUPPORT_FILE_MSG;
		else if(errorCode.equals(CANNOT_SAVE_REACTION)) this.errorMsg = CANNOT_SAVE_REACTION_MSG;
		else if(errorCode.equals(SUPPORT_IS_EMPTY)) this.errorMsg = SUPPORT_IS_EMPTY_MSG;
		else if(errorCode.equals(SUPPORT_FILE_IS_EMPTY)) this.errorMsg = SUPPORT_FILE_IS_EMPTY_MSG;
		else if(errorCode.equals(PARAMETER_IS_EMPTY)) this.errorMsg = PARAMETER_IS_EMPTY_MSG;
		else if(errorCode.equals(NO_OBJECTS_FOUND)) this.errorMsg = NO_OBJECTS_FOUND_MSG;
		else if(errorCode.equals(CANNOT_CREATE_ALERT)) this.errorMsg = CANNOT_CREATE_ALERT_MSG;
		else if(errorCode.equals(INVALID_DISCOUNT_CODE)) this.errorMsg = INVALID_DISCOUNT_CODE_MSG;
		else if(errorCode.equals(EXPIRED_DISCOUNT_CODE)) this.errorMsg = EXPIRED_DISCOUNT_CODE_MSG;
		else if(errorCode.equals(WRONG_ACTIVATION_CODE)) this.errorMsg = WRONG_ACTIVATION_CODE_MSG;
		else if(errorCode.equals(CREATE_ACC_COMPANY_DELETED)) this.errorMsg = CREATE_ACC_COMPANY_DELETED_MSG;
		else if(errorCode.equals(USER_CREATED_NOTIFICATION)) this.errorMsg = USER_CREATED_NOTIFICATION_MSG;
		else if(errorCode.equals(PASSWORD_REQUEST_TOO_SOON)) this.errorMsg = PASSWORD_REQUEST_TOO_SOON_MSG;
		else if(errorCode.equals(PASSWORD_REQUEST_RESENT)) this.errorMsg = PASSWORD_REQUEST_RESENT_MSG;
		else if(errorCode.equals(WRONG_PASSWORD)) this.errorMsg = WRONG_PASSWORD_MSG;
		else if(errorCode.equals(COMPANY_BLOCKED))this.errorMsg = COMPANY_BLOCKED_MSG;
		else if(errorCode.equals(MANAGED_USER_NOT_EXISTS))this.errorMsg = MANAGED_USER_NOT_EXISTS_MSG;
		else {
			this.errorCode = GENERAL_FAILURE;
			this.errorMsg = GENERAL_FAILURE_MSG;
		}
	}

	@XmlElement
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@XmlElement
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


}
