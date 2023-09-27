package nl.devoorkant.sbdr.ws.transfer;

import nl.devoorkant.sbdr.business.wrapper.ErrorService;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResource extends ErrorService {
	public static String ERROR_PAGING = "201";
	public static String ERROR_PAGING_MSG = "Fout bij het vaststellen van de paginering";

	public static String ERROR_PARAMETER_EMPTY = "202";
	public static String ERROR_PARAMETER_EMPTY_MSG = "Een of meerdere parameters zijn leeg";

	public static String CANNOT_CREATE_ACCOUNT = "203";
	public static String CANNOT_CREATE_ACCOUNT_MSG = "Fout bij het aanmaken van het account.";

	public static String INCOMPLETE_ACCOUNT_DATA = "204";
	public static String INCOMPLETE_ACCOUNT_DATA_MSG = "Onvolledige account gegevens.";

	public static String ERROR_DELETE_GEBRUIKER = "205";
	public static String ERROR_DELETE_GEBRUIKER_MSG = "Fout bij verwijderen van gebruiker.";

	public static String CANNOT_FETCH_GEBRUIKERS = "206";
	public static String CANNOT_FETCH_GEBRUIKERS_MSG = "Kan gebruikers niet ophalen.";

	public static String CANNOT_CREATE_GEBRUIKER = "207";
	public static String CANNOT_CREATE_GEBRUIKER_MSG = "Fout bij het aanmaken van nieuwe gebruiker.";

	public static String INCOMPLETE_GEBRUIKER_DATA = "208";
	public static String INCOMPLETE_GEBRUIKER_DATA_MSG = "Onvolledige gebruiker gegevens.";

	public static String COMPANY_ACCOUNT_EXISTS = "209";
	public static String COMPANY_ACCOUNT_EXISTS_MSG = "Bedrijfsaccount bestaat al.";

	public static String CANNOT_CREATE_TICKET = "210";
	public static String CANNOT_CREATE_TICKET_MSG = "Kan ticket niet opslaan.";

	public static String CANNOT_CREATE_NOTIFICATION = "211";
	public static String CANNOT_CREATE_NOTIFICATION_MSG = "Kan vermelding niet aanmaken.";

	public static String CANNOT_FETCH_ALERTOVERVIEW = "212";
	public static String CANNOT_FETCH_ALERTOVERVIEW_MSG = "Kan waarschuwingen niet ophalen.";

	public static String CANNOT_FETCH_MONITORINGOVERVIEW = "213";
	public static String CANNOT_FETCH_MONITORINGOVERVIEW_MSG = "Kan monitoring gegevens niet ophalen.";

	public static String CANNOT_FETCH_NOTIFICATIONOVERVIEW = "214";
	public static String CANNOT_FETCH_NOTIFICATIONOVERVIEW_MSG = "Kan meldingen niet ophalen.";

	public static String CANNOT_FETCH_REMOVEDOVERVIEW = "215";
	public static String CANNOT_FETCH_REMOVEDOVERVIEW_MSG = "Kan verwijderde historiegegevens niet ophalen.";

	public static String CANNOT_FETCH_COMPANYREPORTDATA = "216";
	public static String CANNOT_FETCH_COMPANYREPORTDATA_MSG = "Kan bedrijfsrapport data niet ophalen.";

	public static String CANNOT_RESET_WACHTWOORD = "217";
	public static String CANNOT_RESET_WACHTWOORD_MSG = "Kan wachtwoord niet aanpassen.";

	public static String CANNOT_CREATE_MONITORING = "218";
	public static String CANNOT_CREATE_MONITORING_MSG = "Kan monitoring gegevens niet opslaan.";

	public static String CANNOT_REMOVE_NOTIFICATION = "219";
	public static String CANNOT_REMOVE_NOTIFICATION_MSG = "Kan meldinggegevens niet verwijderen.";

	public static String CANNOT_REMOVE_MONITORING = "220";
	public static String CANNOT_REMOVE_MONITORING_MSG = "Kan monitoring gegevens niet verwijderen.";

	public static String CANNOT_FETCH_COMPANY = "221";
	public static String CANNOT_FETCH_COMPANY_MSG = "Kan bedrijfsgegevens niet ophalen.";

	public static String CANNOT_FETCH_NOTIFICATION = "222";
	public static String CANNOT_FETCH_NOTIFICATION_MSG = "Kan meldinggegevens niet ophalen.";

	public static String CANNOT_FETCH_ACCOUNTDATA = "223";
	public static String CANNOT_FETCH_ACCOUNTDATA_MSG = "Kan account data niet ophalen.";

	public static String CANNOT_PERFORM_VIESCHECK = "224";
	public static String CANNOT_PERFORM_VIESCHECK_MSG = "Kan VIES check (BTW validatie) niet uitvoeren.";

	public static String CANNOT_UPDATE_KLANT = "225";
	public static String CANNOT_UPDATE_KLANT_MSG = "Kan klantgegevens niet opslaan.";

	public static String CANNOT_PERFORM_RECAPTCHAVERIFY = "226";
	public static String CANNOT_PERFORM_RECAPTCHAVERIFY_MSG = "Kan recaptcha verificatie niet uitvoeren.";

	public static String CANNOT_PERFORM_IBANCHECK = "227";
	public static String CANNOT_PERFORM_IBANCHECK_MSG = "Kan IBAN check (rekeningnummervalidatie) niet uitvoeren.";

	public static String CANNOT_RELOGIN = "228";
	public static String CANNOT_RELOGIN_MSG = "Het wachtwoord is succesvol gewijzigd, maar er ging iets fout met automatisch aanmelden. U moet zelf opnieuw inloggen.";

	public static String CANNOT_ACTIVATE_GEBRUIKER = "229";
	public static String CANNOT_ACTIVATE_GEBRUIKER_MSG = "Kan gebruiker niet activeren.";

	public static String CANNOT_FETCH_KLANTBEDRIJFOVERVIEW = "230";
	public static String CANNOT_FETCH_KLANTBEDRIJFOVERVIEW_MSG = "Kan klantoverzicht gegevens niet ophalen.";

	public static String CANNOT_FETCH_MELDINGOVERVIEW = "231";
	public static String CANNOT_FETCH_MELDINGOVERVIEW_MSG = "Kan meldingen van klanten niet ophalen.";

	public static String CANNOT_FETCH_DOCUMENT = "232";
	public static String CANNOT_FETCH_DOCUMENT_MSG = "Kan document niet ophalen.";

	public static String CANNOT_PROCESS_RECAPTCHA = "233";
	public static String CANNOT_PROCESS_RECAPTCHA_MSG = "Fout in verwerken van de recaptcha.";

	public static String CANNOT_FETCH_REPORTREQUESTEDOVERVIEW = "234";
	public static String CANNOT_FETCH_REPORTREQUESTEDOVERVIEW_MSG = "Kan rapportoverzicht gegevens niet ophalen.";

	public static String CANNOT_CREATE_LETTER = "235";
	public static String CANNOT_CREATE_LETTER_MSG = "Kan brief niet aanmaken.";

	public static String CANNOT_FETCH_ADMINOVERVIEWDATA = "236";
	public static String CANNOT_FETCH_ADMINOVERVIEWDATA_MSG = "Kan admin overview gegevens niet ophalen.";

	public static String CANNOT_FETCH_MONITORINGDETAILDATA = "237";
	public static String CANNOT_FETCH_MONITORINGDETAILDATA_MSG = "Kan monitoring detailgegevens niet ophalen.";

	public static String CANNOT_CREATE_CUSTOMNOTIFICATION = "238";
	public static String CANNOT_CREATE_CUSTOMNOTIFICATION_MSG = "Kan melding niet aanmaken.";

	public static String CANNOT_FETCH_FAILLISSEMENTENOVERZICHT = "239";
	public static String CANNOT_FETCH_FAILLISSEMENTENOVERZICHT_MSG = "Kan faillissementenoverzicht niet ophalen.";

	public static String WRONG_PASSWORD = "240";
	public static String WRONG_PASSWORD_MSG = "Wachtwoord onjuist.";

	public static String CANNOT_REMOVE_ALERT = "241";
	public static String CANNOT_REMOVE_ALERT_MSG = "Kan alert niet verwijderen.";

	public static String CANNOT_FETCH_USER_SUPPORT_TICKETS = "242";
	public static String CANNOT_FETCH_USER_SUPPORT_TICKETS_MSG = "Kan tickets van user niet ophalen";

	public static String ERROR_RESULTS_EMPTY = "243";
	public static String RESULTS_EMPTY_MSG = "Er zijn geen resultaten gevonden";

	public static String CANNOT_UPLOAD_FILE = "244";
	public static String CANNOT_UPLOAD_FILE_MSG = "Kan bestand niet uploaden";

	public static String CANNOT_CREATE_ATTACHMENT = "245";
	public static String CANNOT_CREATE_ATTACHMENT_MSG = "Bijlage kan niet gemaakt worden";

	public static String CANNOT_PICKUP_SUPPORTTICKET = "246";
	public static String CANNOT_PICKUP_SUPPORTTICKET_MSG = "Kan supportticket niet toe eigenen";

	public static String CANNOT_FETCH_SUPPORT_TICKET_FILES = "247";
	public static String CANNOT_FETCH_SUPPORT_TICKET_FILES_MSG = "Kan bijlagen van supportticket niet ophalen";

	public static String CANNOT_FETCH_EXACTONLINEACCESS = "248";
	public static String CANNOT_FETCH_EXACTONLINEACCESS_MSG = "Kan ExactOnlineAccess niet ophalen";

	public static String CANNOT_FETCH_FACTUUR = "249";
	public static String CANNOT_FETCH_FACTUUR_MSG = "Kan facturen van gebruiker niet ophalen";

	public static String CANNOT_FETCH_TARIEF = "250";
	public static String CANNOT_FETCH_TARIEF_MSG = "Kan tarief van product niet ophalen";

	public static String ERROR_DISCOUNT = "251";
	public static String ERROR_DISCOUNT_MSG = "Kan kortingscode niet valideren";

	public static String ERROR_NOTIFY_OWN_COMPANY = "252";
	public static String ERROR_NOTIFY_OWN_COMPANY_MSG = "Vermelding van eigen bedrijf maken is niet toegestaan";

	public static String CANNOT_FETCH_WEBSITEPARAM = "253";
	public static String CANNOT_FETCH_WEBSITEPARAM_MSG = "Kan gegevens niet ophalen";

	public static String CANNOT_SAVE_WEBSITEPARAM = "254";
	public static String CANNOT_SAVE_WEBSITEPARAM_MSG = "Kan gegevens niet opslaan";

	public static String ACTION_NOT_ALLOWED = "255";
	public static String ACTION_NOT_ALLOWED_MSG = "U bent niet gemachtigd om deze bewerking uit te voeren";

	public static String CANNOT_FETCH_LETTERS = "256";
	public static String CANNOT_FETCH_LETTERS_MSG = "Berichten over brieven konden niet worden opgehaald";

	public static String CANNOT_CHANGE_LETTER = "257";
	public static String CANNOT_CHANGE_LETTER_MSG= "Kan status van brief niet aanpassen";

	public static String CANNOT_ADD_LETTERS = "258";
	public static String CANNOT_ADD_LETTERS_MSG = "Kan geen record voor brief aanmaken";

	public static String CANNOT_FETCH_EXACTONLINEPARAM = "259";
	public static String CANNOT_FETCH_EXACTONLINEPARAM_MSG = "Kan gegevens ExactOnline niet ophalen";

	public static String CANNOT_MAKE_DONATION = "260";
	public static String CANNOT_MAKE_DONATION_MSG = "Kan geen donatie aanmaken";

	public static String CANNOT_CHECK_BATCH = "261";
	public static String CANNOT_CHECK_BATCH_MSG = "Kan niet bepalen of een nieuwe batch beschikbaar is";

	public static String CANNOT_START_BATCH_PROCESS = "262";
	public static String CANNOT_START_BATCH_PROCESS_MSG = "Kan batch proces niet starten";

	public static String COMPANY_NOT_ACTIVE = "263";
	public static String COMPANY_NOT_ACTIVE_MSG = "Alleen geactiveerde klantbedrijven mogen deze actie uitvoeren";

	public static String VERANTWOORDELIJKHEID_NIET_AKKOORD = "264";
	public static String VERANTWOORDELIJKHEID_NIET_AKKOORD_MSG = "U moet akkoord gaan dat de nieuwe hoofdgebruiker onder uw verantwoordelijkheid valt";

	public static String CANNOT_CHECK_MONITORING = "265";
	public static String CANNOT_CHECK_MONITORING_MSG = "Kan niet checken of monitoring al aan staat";
	
	public static String ERROR_COMPANY_SERVICE = "266";
	public static String ERROR_COMPANY_SERVICE_MSG = "Communicatiefout in ophalen van bedrijfsinformatie van externe service.";
	
	public static String CANNOT_REMOVE_CONTACTMOMENT = "267";
	public static String CANNOT_REMOVE_CONTACTMOMENT_MSG = "Kan contactmoment niet verwijderen.";
	
	public static String CANNOT_CREATE_CONTACTMOMENT = "268";
	public static String CANNOT_CREATE_CONTACTMOMENT_MSG = "Kan contactmoment niet opslaan.";
	
	public static String CANNOT_CREATE_NOTITIE = "269";
	public static String CANNOT_CREATE_NOTITIE_MSG = "Kan notitie niet opslaan.";
	
	public static String CANNOT_FETCH_NOTITIE = "270";
	public static String CANNOT_FETCH_NOTITIE_MSG = "Kan notitie niet ophalen.";
	
	//270

	public ErrorResource() {
		super();
	}

	public ErrorResource(String errorCode, String errorMsg) {
		super(errorCode, errorMsg);
	}

	public ErrorResource(String errorCode) {
		super(errorCode);

		if(getErrorCode().equals(GENERAL_FAILURE) && !errorCode.equals(GENERAL_FAILURE)) {
			this.setErrorCode(errorCode);

			if(errorCode.equals(ERROR_PAGING)) this.setErrorMsg(ERROR_PAGING_MSG);
			else if(errorCode.equals(ERROR_PARAMETER_EMPTY)) this.setErrorMsg(ERROR_PARAMETER_EMPTY_MSG);
			else if(errorCode.equals(CANNOT_CREATE_ACCOUNT)) this.setErrorMsg(CANNOT_CREATE_ACCOUNT_MSG);
			else if(errorCode.equals(INCOMPLETE_ACCOUNT_DATA)) this.setErrorMsg(INCOMPLETE_ACCOUNT_DATA_MSG);
			else if(errorCode.equals(ERROR_DELETE_GEBRUIKER)) this.setErrorMsg(ERROR_DELETE_GEBRUIKER_MSG);
			else if(errorCode.equals(CANNOT_FETCH_GEBRUIKERS)) this.setErrorMsg(CANNOT_FETCH_GEBRUIKERS_MSG);
			else if(errorCode.equals(CANNOT_CREATE_GEBRUIKER)) this.setErrorMsg(CANNOT_CREATE_GEBRUIKER_MSG);
			else if(errorCode.equals(INCOMPLETE_GEBRUIKER_DATA)) this.setErrorMsg(INCOMPLETE_GEBRUIKER_DATA_MSG);
			else if(errorCode.equals(COMPANY_ACCOUNT_EXISTS)) this.setErrorMsg(COMPANY_ACCOUNT_EXISTS_MSG);
			else if(errorCode.equals(CANNOT_CREATE_TICKET)) this.setErrorMsg(CANNOT_CREATE_TICKET_MSG);
			else if(errorCode.equals(CANNOT_CREATE_NOTIFICATION)) this.setErrorMsg(CANNOT_CREATE_NOTIFICATION_MSG);
			else if(errorCode.equals(CANNOT_FETCH_ALERTOVERVIEW)) this.setErrorMsg(CANNOT_FETCH_ALERTOVERVIEW_MSG);
			else if(errorCode.equals(CANNOT_FETCH_MONITORINGOVERVIEW)) this.setErrorMsg(CANNOT_FETCH_MONITORINGOVERVIEW_MSG);
			else if(errorCode.equals(CANNOT_FETCH_NOTIFICATIONOVERVIEW)) this.setErrorMsg(CANNOT_FETCH_NOTIFICATIONOVERVIEW_MSG);
			else if(errorCode.equals(CANNOT_FETCH_REMOVEDOVERVIEW)) this.setErrorMsg(CANNOT_FETCH_REMOVEDOVERVIEW_MSG);
			else if(errorCode.equals(CANNOT_FETCH_COMPANYREPORTDATA)) this.setErrorMsg(CANNOT_FETCH_COMPANYREPORTDATA_MSG);
			else if(errorCode.equals(CANNOT_RESET_WACHTWOORD)) this.setErrorMsg(CANNOT_RESET_WACHTWOORD_MSG);
			else if(errorCode.equals(CANNOT_CREATE_MONITORING)) this.setErrorMsg(CANNOT_CREATE_MONITORING_MSG);
			else if(errorCode.equals(CANNOT_REMOVE_NOTIFICATION)) this.setErrorMsg(CANNOT_REMOVE_NOTIFICATION_MSG);
			else if(errorCode.equals(CANNOT_REMOVE_MONITORING)) this.setErrorMsg(CANNOT_REMOVE_MONITORING_MSG);
			else if(errorCode.equals(CANNOT_FETCH_COMPANY)) this.setErrorMsg(CANNOT_FETCH_COMPANY_MSG);
			else if(errorCode.equals(CANNOT_FETCH_NOTIFICATION)) this.setErrorMsg(CANNOT_FETCH_NOTIFICATION_MSG);
			else if(errorCode.equals(CANNOT_FETCH_ACCOUNTDATA)) this.setErrorMsg(CANNOT_FETCH_ACCOUNTDATA_MSG);
			else if(errorCode.equals(CANNOT_PERFORM_VIESCHECK)) this.setErrorMsg(CANNOT_PERFORM_VIESCHECK_MSG);
			else if(errorCode.equals(CANNOT_UPDATE_KLANT)) this.setErrorMsg(CANNOT_UPDATE_KLANT_MSG);
			else if(errorCode.equals(CANNOT_PERFORM_RECAPTCHAVERIFY)) this.setErrorMsg(CANNOT_PERFORM_RECAPTCHAVERIFY_MSG);
			else if(errorCode.equals(CANNOT_PERFORM_IBANCHECK)) this.setErrorMsg(CANNOT_PERFORM_IBANCHECK_MSG);
			else if(errorCode.equals(CANNOT_RELOGIN)) this.setErrorMsg(CANNOT_RELOGIN_MSG);
			else if(errorCode.equals(CANNOT_ACTIVATE_GEBRUIKER)) this.setErrorMsg(CANNOT_ACTIVATE_GEBRUIKER_MSG);
			else if(errorCode.equals(CANNOT_FETCH_KLANTBEDRIJFOVERVIEW)) this.setErrorMsg(CANNOT_FETCH_KLANTBEDRIJFOVERVIEW_MSG);
			else if(errorCode.equals(CANNOT_FETCH_MELDINGOVERVIEW)) this.setErrorMsg(CANNOT_FETCH_MELDINGOVERVIEW_MSG);
			else if(errorCode.equals(CANNOT_FETCH_DOCUMENT)) this.setErrorMsg(CANNOT_FETCH_DOCUMENT_MSG);
			else if(errorCode.equals(CANNOT_PROCESS_RECAPTCHA)) this.setErrorMsg(CANNOT_PROCESS_RECAPTCHA_MSG);
			else if(errorCode.equals(CANNOT_FETCH_REPORTREQUESTEDOVERVIEW)) this.setErrorMsg(CANNOT_FETCH_REPORTREQUESTEDOVERVIEW_MSG);
			else if(errorCode.equals(CANNOT_CREATE_LETTER)) this.setErrorMsg(CANNOT_CREATE_LETTER_MSG);
			else if(errorCode.equals(CANNOT_FETCH_ADMINOVERVIEWDATA)) this.setErrorMsg(CANNOT_FETCH_ADMINOVERVIEWDATA_MSG);
			else if(errorCode.equals(CANNOT_FETCH_MONITORINGDETAILDATA)) this.setErrorMsg(CANNOT_FETCH_MONITORINGDETAILDATA_MSG);
			else if(errorCode.equals(CANNOT_CREATE_CUSTOMNOTIFICATION)) this.setErrorMsg(CANNOT_CREATE_CUSTOMNOTIFICATION_MSG);
			else if(errorCode.equals(CANNOT_FETCH_FAILLISSEMENTENOVERZICHT)) this.setErrorMsg(CANNOT_FETCH_FAILLISSEMENTENOVERZICHT_MSG);
			else if(errorCode.equals(WRONG_PASSWORD)) this.setErrorMsg(WRONG_PASSWORD_MSG);
			else if(errorCode.equals(CANNOT_REMOVE_ALERT)) this.setErrorMsg(CANNOT_REMOVE_ALERT_MSG);
			else if(errorCode.equals(CANNOT_FETCH_USER_SUPPORT_TICKETS)) this.setErrorMsg(CANNOT_FETCH_USER_SUPPORT_TICKETS_MSG);
			else if(errorCode.equals(ERROR_RESULTS_EMPTY)) this.setErrorMsg(RESULTS_EMPTY_MSG);
			else if(errorCode.equals(CANNOT_UPLOAD_FILE)) this.setErrorMsg(CANNOT_UPLOAD_FILE_MSG);
			else if(errorCode.equals(CANNOT_CREATE_ATTACHMENT)) this.setErrorMsg(CANNOT_CREATE_ATTACHMENT_MSG);
			else if(errorCode.equals(CANNOT_PICKUP_SUPPORTTICKET)) this.setErrorMsg(CANNOT_PICKUP_SUPPORTTICKET_MSG);
			else if(errorCode.equals(CANNOT_FETCH_SUPPORT_TICKET_FILES)) this.setErrorMsg(CANNOT_FETCH_SUPPORT_TICKET_FILES_MSG);
			else if(errorCode.equals(CANNOT_FETCH_EXACTONLINEACCESS)) this.setErrorMsg(CANNOT_FETCH_EXACTONLINEACCESS_MSG);
			else if(errorCode.equals(CANNOT_FETCH_FACTUUR)) this.setErrorMsg(CANNOT_FETCH_FACTUUR_MSG);
			else if(errorCode.equals(CANNOT_FETCH_TARIEF)) this.setErrorMsg(CANNOT_FETCH_TARIEF_MSG);
			else if(errorCode.equals(ERROR_DISCOUNT)) this.setErrorMsg(ERROR_DISCOUNT_MSG);
			else if(errorCode.equals(ERROR_NOTIFY_OWN_COMPANY)) this.setErrorMsg(ERROR_NOTIFY_OWN_COMPANY_MSG);
			else if(errorCode.equals(CANNOT_FETCH_WEBSITEPARAM)) this.setErrorMsg(CANNOT_FETCH_WEBSITEPARAM_MSG);
			else if(errorCode.equals(CANNOT_SAVE_WEBSITEPARAM)) this.setErrorMsg(CANNOT_SAVE_WEBSITEPARAM_MSG);
			else if(errorCode.equals(ACTION_NOT_ALLOWED)) this.setErrorMsg(ACTION_NOT_ALLOWED_MSG);
			else if(errorCode.equals(CANNOT_FETCH_LETTERS)) this.setErrorMsg(CANNOT_FETCH_LETTERS_MSG);
			else if(errorCode.equals(CANNOT_CHANGE_LETTER)) this.setErrorMsg(CANNOT_CHANGE_LETTER_MSG);
			else if(errorCode.equals(CANNOT_ADD_LETTERS)) this.setErrorMsg(CANNOT_ADD_LETTERS_MSG);
			else if(errorCode.equals(CANNOT_MAKE_DONATION)) this.setErrorMsg(CANNOT_MAKE_DONATION_MSG);
			else if(errorCode.equals(CANNOT_FETCH_EXACTONLINEPARAM)) this.setErrorMsg(CANNOT_FETCH_EXACTONLINEPARAM_MSG);
			else if(errorCode.equals(CANNOT_CHECK_BATCH)) this.setErrorMsg(CANNOT_CHECK_BATCH_MSG);
			else if(errorCode.equals(CANNOT_START_BATCH_PROCESS))this.setErrorMsg(CANNOT_START_BATCH_PROCESS_MSG);
			else if (errorCode.equals(COMPANY_NOT_ACTIVE)) this.setErrorMsg(COMPANY_NOT_ACTIVE_MSG);
			else if(errorCode.equals(VERANTWOORDELIJKHEID_NIET_AKKOORD)) this.setErrorMsg(VERANTWOORDELIJKHEID_NIET_AKKOORD_MSG);
			else if (errorCode.equals(ERROR_COMPANY_SERVICE)) this.setErrorMsg(ERROR_COMPANY_SERVICE_MSG);
			else if(errorCode.equals(CANNOT_CHECK_MONITORING))this.setErrorMsg(CANNOT_CHECK_MONITORING_MSG);
			else if (errorCode.equals(CANNOT_REMOVE_CONTACTMOMENT)) this.setErrorMsg(CANNOT_REMOVE_CONTACTMOMENT_MSG);
			else if(errorCode.equals(CANNOT_CREATE_CONTACTMOMENT))this.setErrorMsg(CANNOT_CREATE_CONTACTMOMENT_MSG);
			else if(errorCode.equals(CANNOT_CREATE_NOTITIE))this.setErrorMsg(CANNOT_CREATE_NOTITIE_MSG);
			else if(errorCode.equals(CANNOT_FETCH_NOTITIE))this.setErrorMsg(CANNOT_FETCH_NOTITIE_MSG);
		}
	}
}