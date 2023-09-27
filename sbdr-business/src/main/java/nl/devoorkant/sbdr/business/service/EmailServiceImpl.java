package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.util.EEMailType;
import nl.devoorkant.sbdr.business.util.Mail;
import nl.devoorkant.sbdr.business.util.pdf.PrintUtil;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import nl.devoorkant.sbdr.data.util.ERedenVerwijderenMelding;
import nl.devoorkant.sbdr.data.util.SearchUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

@Service("emailService")
public class EmailServiceImpl implements EmailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BedrijfServiceImpl.class);
	@Autowired
	private BedrijfDataService bedrijfDataService;
	@Value("${companyIdleTimeDays}")
	private Integer companyIdleTimeDays;
    @Value("${emailAllowToSend:true}")
    private String emailAllowToSend;	
	@Autowired
	private ConfiguratieDataService configuratieDataService;
	@Autowired
	private DocumentDataService documentDataService;
	@Autowired
	private DocumentService documentService;

	//	@Value("${emailReceiverGCH}")
	//	private String emailReceiverGCH;
	@Value("${email_smtp_user}")
	private String emailSMTPuser;
	
	@Value("${email_smtp_password}") 
	private String emailSMTPpassword;
	
	@Value("${emailSenderAdmin}")
	private String emailSenderAdmin;

	@Value("${emailSenderBackoffice}")
	private String emailSenderBackoffice;

	@Value("${emailSenderBezwaren}")
	private String emailSenderBezwaren;

	@Value("${emailSenderDomain}")
	private String emailSenderDomain;

	@Value("${emailSenderMonitoring}")
	private String emailSenderMonitoring;

	@Value("${emailSenderNoReply}")
	private String emailSenderNoReply;

	@Value("${emailSenderSupport}")
	private String emailSenderSupport;
	@Autowired
	private FactuurDataService factuurDataService;
	@Autowired
	private GebruikerDataService gebruikerDataService;
	@Autowired
	private KlantDataService klantDataService;
	@Autowired
	private MeldingDataService meldingDataService;
	@Value("${overduedays_newmelding}")
	private int newMeldingOverdueDays;
	@Value("${serverurl}")
	private String serverUrl;
	@Value("${serverurlExtension}")
	private String serverUrlExtension;
	@Value("${verificationFirstReminderDays}")
	private int verificationFirstReminderDays;
	@Value("${verificationSecondReminderDays}")
	private int verificationSecondReminderDays;
	@Value("${verificationThirdReminderDays}")
	private int verificationThirdReminderDays;

	@Override
	public void alertsReminderEmailsQuartzJob() throws ServiceException {
		// if disabled return
		if (companyIdleTimeDays == -1)
			return;
		
		Calendar idleTimeThreshold = Calendar.getInstance();
		idleTimeThreshold.add(Calendar.DATE, -companyIdleTimeDays);
		List<Bedrijf> allNonRecentBedrijven = null;

		try {
			allNonRecentBedrijven = bedrijfDataService.findAllNonRecentBedrijven(idleTimeThreshold.getTime());
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		if (allNonRecentBedrijven == null) {
			throw new ServiceException("Lijst met bedrijven die 3 of meer dagen niet ingelogd zijn is leeg");
		}

		for (Bedrijf bedrijf : allNonRecentBedrijven) {
			try {
				if (bedrijfDataService.bedrijfHasAlerts(bedrijf.getBedrijfId())) {
					LOGGER.debug("bedrijf ID: " + bedrijf.getBedrijfId() + ", naam: "+bedrijf.getBedrijfsNaam()+", heeft alerts");
					sendAlertEmail(bedrijf);
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				LOGGER.error(e.getLocalizedMessage());
				throw new ServiceException(e);
			}
		}
	}

	@Override
	public void sendActivationEmail(Klant klant, Bedrijf bedrijf) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.ACCOUNT_ACTIVATION, klant.getEmailAdres(), emailSenderAdmin + emailSenderDomain, null);

		mail.setBodyPlain(EEMailType.ACCOUNT_ACTIVATION.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(klant.getGeslacht()) + " " + klant.getNaam() + ",</p>" +
				"<p style=\"font-weight:bold\">Welkom bij het CRZB</p>" +
				"<p>Hartelijk dank voor uw registratie en uw deelname. Volg de hierna genoemde stappen om uw account te activeren.</p>" +
				"<p style=\"font-weight:bold\">Stap 1: account activatie</p>" +
				"<table>" +
				"<tr>" +
				"<td style=\"height:35px;text-align:center;vertical-align:center;border-radius:2px; background-color:#f4792b; \" width=\"150\">" +
				"<a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/login?activationid=" + klant.getActivatieCode() + "&userid=" + klant.getGebruikersNaam() + "&bbset=true\">Account activeren</a>" +
				"</td>" +
				"</tr>" +
				"</table>" +
				"<p>Indien de link niet werkt, kopieer dan onderstaande tekst en plak deze in uw adresbalk.</p>" +
				"<p>" + serverUrl + serverUrlExtension + "/login?activationid=" + klant.getActivatieCode() + "&userid=" + klant.getGebruikersNaam() + "&bbset=true</p>" +
				"<p style=\"font-weight:bold\">Stap 2: verificatie van uw account</p>" +
				"<p>U heeft nu een voorlopig account aangemaakt. U kunt beginnen met het melden van uw openstaande vorderingen en waar nodig gegevens aanpassen. Uw vermeldingen zullen echter pas in behandeling worden genomen nadat u uw account heeft geverifieerd.</p>" +
				"<p>Na activatie ontvangt u van ons binnen enkele dagen een brief per post ter bevestiging van uw registratie. Hierin is een verificatiecode opgenomen waarmee u uw voorlopige account kunt omzetten in een definitief account. Vanaf dit moment worden de door u opgegeven betalingsachterstanden verwerkt. Tevens kunt u dan ook rapporten uit het CRZB opvragen en bedrijven in monitoring plaatsen.</p>" +
				"<p style=\"font-weight:bold\">Belangrijk</p>" +
				"<p>Wordt de verificatiecode niet binnen 30 dagen na verzending ingevuld dan zullen uw account en de door u gedane vermeldingen automatisch verwijderd worden.</p>";

		mail.setBodyHTML(constructBody("ACTIVATIELINK", bedrijf.getSbdrNummer(), message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, klant.getGebruikerId(), EEMailType.ACCOUNT_ACTIVATION);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void sendExistingAccountEmail(Bedrijf bedrijf, Klant newKlant) throws ServiceException {
		Klant klantOfBedrijf;

		try {
			klantOfBedrijf = klantDataService.findKlantOfBedrijfByBedrijfId(bedrijf.getBedrijfId());

		} catch (DataServiceException e) {
			LOGGER.error("Error in sendExistingAccountEmail. Cannot fetch klantOfBedrijf.", e);
			throw new ServiceException(e);
		}

		if (klantOfBedrijf == null) {
			throw new ServiceException("Niet in staat om klant van bedrijf " + bedrijf.getBedrijfId() + " op te halen.");
		}

		Mail mail = constructMailObject(EEMailType.EXISTING_ACCOUNT, klantOfBedrijf.getEmailAdres(), emailSenderBackoffice + emailSenderDomain, null);

		mail.setBodyPlain(EEMailType.EXISTING_ACCOUNT.getOmschrijving());

	    String message = "<p>Geachte " + constructTitle(klantOfBedrijf.getGeslacht()) + " " + klantOfBedrijf.getNaam() + ",</p>" +
				"<p style=\"font-weight:bold\">Er is geprobeerd om een nieuw account aan te maken met uw bedrijfsnaam.</p>" +
				"<p>De gegevens van de nieuwe klant zijn als volgt:" +
				"<ul>" +
				"<li>Klant naam: " + newKlant.getNaam() + "</li>" +
				"<li>Bedrijfsnaam: " + bedrijf.getBedrijfsNaam() + "</li>" +
				"<li>Telefoonnummer: " + bedrijf.getTelefoonnummer() + "</li>" +
				"<li>Kvk-nummer: " + bedrijf.getKvKnummer() + bedrijf.getSubDossier() + "</li>" +
				"</ul></p>" +
				"<p>Er is nog geen activatie e-mail verstuurd naar de klant</p>";

		mail.setBodyHTML(constructBody("REEDS BESTAAND ACCOUNT", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, klantOfBedrijf.getGebruikerId(), EEMailType.EXISTING_ACCOUNT);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendMonitoringChangedEmails(Bedrijf bedrijfOnderMonitor) throws ServiceException {
		List<Bedrijf> bedrijvenMetMonitor;

		try {
			bedrijvenMetMonitor = bedrijfDataService.findBedrijvenMetMonitoringOpBedrijfByBedrijfId(bedrijfOnderMonitor.getBedrijfId());
		} catch (DataServiceException e) {
			LOGGER.error("Error in sendMonitoringChangedEmails. Cannot fetch bedrijvenMetMonitor.", e);
			throw new ServiceException(e);
		}

		if (bedrijvenMetMonitor == null) {
			LOGGER.error("Error in sendMonitoringChangedEmails. bedrijvenMetmonitor == null.");
			throw new ServiceException("Niet in staat bedrijven met monitor op bedrijf " + bedrijfOnderMonitor.getBedrijfId() + " op te halen.");
		}

		for (Bedrijf bedrijfMetMonitor : bedrijvenMetMonitor) {
			List<String> gebruikerEmailsVanBedrijf = new ArrayList<>();
			Klant klantVanBedrijf = null;

			try {
				klantVanBedrijf = bedrijfDataService.findKlantOfBedrijf(bedrijfMetMonitor.getBedrijfId());
			} catch (Exception e) {
				LOGGER.error("Error in sendMonitoringChangedEmails. Cannot fetch klantVanBedrijf.", e);
				throw new ServiceException(e);
			}

			if (klantVanBedrijf == null) {
				LOGGER.error("Error in sendMonitoringChangedEmails. klantVanBedrijf == null.");
				throw new ServiceException("Kan klant van bedrijf " + bedrijfMetMonitor.getBedrijfId() + " niet ophalen.");
			}

			for (Gebruiker g : bedrijfMetMonitor.getGebruikers()) {
				if (!g.getGebruikerId().equals(klantVanBedrijf.getGebruikerId())) {
					gebruikerEmailsVanBedrijf.add(g.getEmailAdres());
				}
			}

			Mail mail = constructMailObject(EEMailType.MONITORING_DETAILS, klantVanBedrijf.getEmailAdres(), emailSenderMonitoring + emailSenderDomain, gebruikerEmailsVanBedrijf.toArray(new String[gebruikerEmailsVanBedrijf.size()]));

			mail.setBodyPlain(EEMailType.MONITORING_DETAILS.getOmschrijving());

			String message = "<p>Geachte medewerkers van " + bedrijfMetMonitor.getBedrijfsNaam() + ",</p><p>Er heeft zich een wijziging voorgedaan m.b.t. de status van een bedrijf dat u bij het CRZB in monitoring heeft geplaatst.</p><p>" + bedrijfOnderMonitor.getBedrijfsNaam() + ", KvK-nummer " + bedrijfOnderMonitor.getKvKnummer() + (bedrijfOnderMonitor.getSubDossier() == null ? "0000" : bedrijfOnderMonitor.getSubDossier()) + "</p><p>Log in op uw account voor meer informatie.</p><table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:2px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/login\">Inloggen</a></td></tr></table>";

			mail.setBodyHTML(constructBody("MONITORING WAARSCHUWING", "", message));

			mail.setSubject(mail.getEmailType().getOmschrijving());

			try {
				submitMail(mail, klantVanBedrijf.getGebruikerId(), EEMailType.MONITORING_DETAILS);
			} catch (ServiceException e) {
				throw new ServiceException(e);
			}
		}
	}

	@Override
	public void sendNewUserConfirmationEmail(Klant klant, Gebruiker gebruiker) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.NEW_USER_CONFIRMATION, klant.getEmailAdres(), emailSenderAdmin + emailSenderDomain, null);

		mail.setBodyPlain(EEMailType.NEW_USER_CONFIRMATION.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(klant.getGeslacht()) + " " + klant.getNaam() + ",</p>" +
				"<p>De volgende nieuwe gebruiker is toegevoegd aan uw account: " + gebruiker.getVoornaam() + " " + gebruiker.getNaam() + "</p>" +
				"<p>Deze gebruiker kan nu betalingsachterstanden vermelden, rapporten opvragen en een bedrijf in monitoring plaatsen op naam van uw account.</p>" +
				"<p>U verklaart hierbij dat deze persoon onderdeel uitmaakt van uw interne bedrijfsvoering en stemt ermee in dat deze gebruiker onder uw verantwoordelijkheid valt en ook gemachtigd is om kosten te maken bij het CRZB.</p>"
				+ "<p>Is dit niet het geval? Meld dit ons dan zo snel mogelijk.</p>";

		mail.setBodyHTML(constructBody("BEVESTIGING NIEUWE GEBRUIKER", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.NEW_USER_CONFIRMATION);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendNewUserEmail(Gebruiker gebruiker, Integer bedrijfId) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.NEW_USER, gebruiker.getEmailAdres(), emailSenderAdmin + emailSenderDomain, new String[] {});

		Klant klant;
		try {
			klant = bedrijfDataService.findKlantOfBedrijf(bedrijfId);
		} catch (Exception e) {
			LOGGER.error("Error in sendNewUserEmail. Cannot fetch klant.", e);
			throw new ServiceException(e);
		}

		if (klant == null) {
			LOGGER.error("Error in sendNewUserEmail. klant == null");
			throw new ServiceException("Niet in staat om klant op te halen van bedrijf " + bedrijfId);
		}

		mail.setBodyPlain(EEMailType.NEW_USER.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(gebruiker.getGeslacht()) + " " + gebruiker.getNaam() + ",</p>" +
				"<p>Er is een account voor u aangemaakt door " + klant.getVoornaam() + " " + klant.getNaam() + " (hoofdaccount) bij het CRZB.</p>" +
				"<p>Met behulp van de onderstaande activatielink kunt u uw account activeren.</p>" +
				"<table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:4px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/activateuser/" + gebruiker.getWachtwoord().getActivatieCode() + "/" + gebruiker.getGebruikersNaam() + "\">Account activeren</a></td></tr></table>";

		mail.setBodyHTML(constructBody("NIEUWE GEBRUIKER", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.NEW_USER);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void sendNewUserBedrijfManagedEmail(Gebruiker gebruiker, Integer bedrijfId, BedrijfManaged bedrijfManaged) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.NEW_USER, gebruiker.getEmailAdres(), emailSenderAdmin + emailSenderDomain, new String[] {});

		Klant klant;
		try {
			klant = bedrijfDataService.findKlantOfBedrijf(bedrijfId);
		} catch (Exception e) {
			LOGGER.error("Error in sendNewUserBedrijfManagedEmail. Cannot fetch klant.", e);
			throw new ServiceException(e);
		}

		if (klant == null) {
			LOGGER.error("Error in sendNewUserBedrijfManagedEmail. klant == null");
			throw new ServiceException("Niet in staat om klant op te halen van bedrijf " + bedrijfId);
		}

		mail.setBodyPlain(EEMailType.NEW_USER.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(gebruiker.getGeslacht()) + " " + gebruiker.getNaam() + ",</p>" +
				"<p>Er is een bedrijf managed account voor u aangemaakt van bedrijf " + bedrijfManaged.getBedrijf().getBedrijfsNaam() + " bij het CRZB.</p>" +
				"<p>Met behulp van de onderstaande activatielink kunt u uw managed account activeren.</p>" +
				"<table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:4px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/activateuser/" + gebruiker.getWachtwoord().getActivatieCode() + "/" + gebruiker.getGebruikersNaam() + "\">Account activeren</a></td></tr></table>";

		mail.setBodyHTML(constructBody("NIEUWE GEBRUIKER", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.NEW_USER);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}	

	@Override
	public void sendNotificationInProcessEmail(Gebruiker gebruiker, Bedrijf vermeldBedrijf, List<Melding> meldingen) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.NOTIFICATION_PROCESS, gebruiker.getEmailAdres(), emailSenderBackoffice + emailSenderDomain, new String[] {});

		mail.setBodyPlain(EEMailType.NOTIFICATION_PROCESS.getOmschrijving());

		String meldingenTekst = "";
		try {
			for (Melding melding : meldingen) {
				if (melding.getBedrag() != null)
					meldingenTekst += "<li>Factuurnummer: " + melding.getReferentieNummer() + ", bedrag: &euro; " + PrintUtil.formatCurrency(melding.getBedrag().doubleValue()) + ", verloopdatum: " + new SimpleDateFormat("dd-MM-yyyy").format(melding.getDatumVerloopFactuur()) + "</li>";
				else
					meldingenTekst += "<li>Factuurnummer: " + melding.getReferentieNummer() + ", bedrag: &euro; 0,00" + ", verloopdatum: " + new SimpleDateFormat("dd-MM-yyyy").format(melding.getDatumVerloopFactuur()) + "</li>";
			}
		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationInProcessEmail. Cannot create meldingenTekst.", e);
			throw new ServiceException(e);
		}

		String message = "";
		try {
		message = "<p>Geachte " + constructTitle(gebruiker.getGeslacht()) + " " + gebruiker.getNaam() + ",</p>"
					+ "<p>Uw vermelding" + (meldingen.size() == 1 ? "" : "en") + " betreffende " + vermeldBedrijf.getBedrijfsNaam() + " " + (meldingen.size() == 1 ? "is" : "zijn") + " in goede orde ontvangen en " + (meldingen.size() == 1 ? "zal" : "zullen") + " door ons in behandeling worden genomen.</p><p>De onderstaande vermelding" + (meldingen.size() == 1 ? "" : "en") + " worden in behandeling genomen:<br/><ul>" + meldingenTekst + "</ul></p>"
					+ "<p>Uw debiteur zal op de hoogte worden gebracht en worden verzocht om contact met u op te nemen. Mocht uw debiteur het niet eens zijn met de vermelding" + (meldingen.size() == 1 ? "" : "en") + " dan heeft hij 14 dagen de tijd om er met u uit te komen. Doet hij dit niet en heeft hij geen bezwaar ingediend, dan gaan wij over tot registratie van de vermelding" + (meldingen.size() == 1 ? "" : "en") + " in het CRZB.</p>"
					+ "<p>U wordt met behulp van uw account op de hoogte gehouden van de status van de vermelding" + (meldingen.size() == 1 ? "" : "en") + ".</p>"
					+ "<p>Log in op uw account voor meer informatie.</p>"
					+ "<table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:2px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/login\">Inloggen</a></td></tr></table>";
		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationInProcessEmail. Cannot create message.", e);
			throw new ServiceException(e);
		}

		mail.setBodyHTML(constructBody("VERMELDING IN BEHANDELING", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.NOTIFICATION_PROCESS);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendNotificationIsActiveEmail(Klant klant, HashMap<Bedrijf, List<Melding>> gedaneMeldingen) throws ServiceException {
		String meldingenBedrijvenTekst = "", meldingenTekst = "";
		boolean multipleNotifications = false;

		if (gedaneMeldingen.size() == 0) {
			meldingenBedrijvenTekst = "Geen bedrijven gevonden";
		}

		if (gedaneMeldingen.size() > 1) {
			multipleNotifications = true;
		}

		try {
			for (Bedrijf bedrijfOver : gedaneMeldingen.keySet()) {
				List<Melding> meldingen = gedaneMeldingen.get(bedrijfOver);

				if (meldingen.size() > 1) {
					multipleNotifications = true;
				}

				if (meldingen.size() > 0) {
					for (Melding melding : meldingen) {
						if (melding.getBedrag() != null)
							meldingenTekst += "<li>Factuurnummer: " + melding.getReferentieNummer() + ", bedrag: &euro; " + PrintUtil.formatCurrency(melding.getBedrag().doubleValue()) + ", verloopdatum: " + new SimpleDateFormat("dd-MM-yyyy").format(melding.getDatumVerloopFactuur()) + "</li>";
						else
							meldingenTekst += "<li>Factuurnummer: " + melding.getReferentieNummer() + ", bedrag: &euro; 0,00" + ", verloopdatum: " + new SimpleDateFormat("dd-MM-yyyy").format(melding.getDatumVerloopFactuur()) + "</li>";
					}
					meldingenBedrijvenTekst += "<p><b>Vermeldingen voor " + bedrijfOver.getBedrijfsNaam() + ":</b><br/><ul>" + meldingenTekst + "</ul></p>";
					meldingenTekst = "";
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationIsActiveEmail. Cannot create meldingenTekst.", e);
			throw new ServiceException(e);
		}

		Mail mail = constructMailObject(EEMailType.NOTIFICATION_ACTIVE, klant.getEmailAdres(), emailSenderBackoffice + emailSenderDomain, new String[] {});

		mail.setBodyPlain(EEMailType.NOTIFICATION_ACTIVE.getOmschrijving());

		String message = "";
		try {
			message = "<p>Geachte " + constructTitle(klant.getGeslacht()) + " " + klant.getNaam() + ",</p><p>De door u aangemelde " + (multipleNotifications ? "facturen zijn" : "factuur is") + " opgenomen in het register.</p>"
					+ "<p>Hieronder vindt u een overzicht van bedrijven die u of een van uw collega's heeft aangemeld bij het CRZB." + meldingenBedrijvenTekst + "</p>"
					+ "<p>In de afgelopen periode hebben wij uw debiteur schriftelijk op de hoogte gesteld door middel van een vooraankondiging tot registratie. Hiertegen is geen (gegrond) bezwaar ingediend. Bovendien heeft er volgens onze informatie nog geen betaling plaatsgevonden en/of is er geen betalingsregeling getroffen. Hierdoor is de achterstand opgenomen in het register.</p>"
					+ "<p>Registratie van een betalingsachterstand heeft negatieve gevolgen voor de kredietwaardigheid van uw debiteur. Zolang uw debiteur uw factuur niet heeft betaald, zal de achterstand zichtbaar blijven voor alle gebruikers van het CRZB. Dit kan o.a. leiden tot de weigering van kredietverstrekkingen. Als uw debiteur alsnog tot betaling overgaat, dan zal de registratie worden verwijderd en zijn kredietwaardigheid worden hersteld.</p>"
					+ "<p>Is uw vordering betaald of zijn er reeds concrete betalingsafspraken gemaakt? Dan dient u de vermelding te verwijderen.</p>";
		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationIsActiveEmail. Cannot create message.", e);
			throw new ServiceException(e);
		}
		mail.setBodyHTML(constructBody("VERMELDING OPGENOMEN", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, klant.getGebruikerId(), EEMailType.NOTIFICATION_ACTIVE);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendNotificationIsPaidEmail(Klant klant) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.NOTIFICATION_RESOLVED, klant.getEmailAdres(), emailSenderNoReply + emailSenderDomain, new String[] {});

		mail.setBodyPlain(EEMailType.NOTIFICATION_RESOLVED.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(klant.getGeslacht()) + " " + constructTitle(klant.getGeslacht()) + " " + klant.getVoornaam() + " " + klant.getNaam() + ",</p>"
				+ "<p>Goed om te horen dat uw vordering inmiddels is voldaan.</p>"
				+ "<p>Indien u in de toekomst wederom te maken krijgt met betalingsachterstanden, vermeld deze dan bij het CRZB. Door elkaar op de hoogte te houden met betrekking tot niet-betalende of te laat-betalende partijen kunnen ondernemers in Nederland ervoor zorgen dat veel schade wordt voorkomen. Ook wordt hiermee actief misbruik en fraude tegengegaan.</p>";

		mail.setBodyHTML(constructBody("VORDERING VOLDAAN", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, klant.getGebruikerId(), EEMailType.NOTIFICATION_RESOLVED);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void sendNotificationNewInvoice(Klant klant, Integer factuurId) throws ServiceException {
		byte[] factuurContent = null;
		Document document = null;
		try {
			factuurContent = documentService.getFactuur(factuurId);

		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationNewInvoice. Cannot fetch factuur content.", e);
			throw new ServiceException(e);
		}

		if (factuurContent == null) {
			LOGGER.error("Error in sendNotificationNewInvoice. factuur == null.");
			throw new ServiceException("Factuur met factuurId " + factuurId + " is leeg");
		}

		Mail mail = null;
		
		if (klant.getEmailAdresFacturatie() != null)
			mail = constructMailObject(EEMailType.NOTIFICATION_NEW_INVOICE, klant.getEmailAdresFacturatie(), emailSenderNoReply + emailSenderDomain, new String[] {});
		else
			mail = constructMailObject(EEMailType.NOTIFICATION_NEW_INVOICE, klant.getEmailAdres(), emailSenderNoReply + emailSenderDomain, new String[] {});
		
		try {
			mail.setAttachment(factuurContent);

			Factuur factuur = factuurDataService.findByFactuurId(factuurId);
			if (factuur != null && factuur.getBedrijfBedrijfId().equals(klant.getBedrijfBedrijfId())) {
				mail.setAttachmentName(EDocumentType.FACTUUR.getOmschrijving() + "_" + factuur.getReferentie());
			} else
				throw new ServiceException("Factuurgegevens: " + factuurId + " corresponderen niet met bedrijf van klant met id: " + klant.getGebruikerId()); // may not occur

			mail.setSubject(mail.getEmailType().getOmschrijving());
		} catch (Exception e) {
			LOGGER.error("Error in sendReportRequestedEmail. Cannot fetch attachment details.", e);
		}

		mail.setBodyPlain(EEMailType.NOTIFICATION_NEW_INVOICE.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(klant.getGeslacht()) + " " + " " + klant.getVoornaam() + " " + klant.getNaam() + ",</p>"
				+ "<p>Uw rekeningoverzicht behorend bij de door u gebruikte diensten van het CRZB is nu beschikbaar. U kunt uw overzicht terugvinden binnen de bijlage van deze mail. Daarnaast kunt u deze bekijken door in te loggen op <a href=\"https://www.crzb.nl/register/#/login\">www.crzb.nl</a> en te kiezen voor 'mijn account' en vervolgens op het tabblad 'facturen' te klikken.</p>"
				+ "<p><b>Wijze van betaling</b><br>Graag ontvangen wij het factuurbedrag binnen 14 dagen na de datum genoemd in het rekeningoverzicht. Heeft u gekozen voor betaling per automatische incasso? Wij incasseren het bedrag van de door u opgegeven rekening dan automatisch. U hoeft hiervoor uiteraard niets te doen.</p>";

		mail.setBodyHTML(constructBody("FACTUUR", "", message));

		try {
			submitMail(mail, klant.getGebruikerId(), EEMailType.NOTIFICATION_NEW_INVOICE);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendNotificationNotInProcessEmail(Gebruiker gebruiker, Bedrijf vermeldBedrijf, List<Melding> meldingen) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.NOTIFICATION_ON_HOLD, gebruiker.getEmailAdres(), emailSenderBackoffice + emailSenderDomain, new String[] {});

		mail.setBodyPlain(EEMailType.NOTIFICATION_ON_HOLD.getOmschrijving());

		String meldingenTekst = "";
		try {

			for (Melding melding : meldingen) {
				if (melding.getBedrag() != null)
					meldingenTekst += "<li>Factuurnummer: " + melding.getReferentieNummer() + ", bedrag: &euro; " + PrintUtil.formatCurrency(melding.getBedrag().doubleValue()) + ", verloopdatum: " + new SimpleDateFormat("dd-MM-yyyy").format(melding.getDatumVerloopFactuur()) + "</li>";
				else
					meldingenTekst += "<li>Factuurnummer: " + melding.getReferentieNummer() + ", bedrag: &euro; 0,00" + ", verloopdatum: " + new SimpleDateFormat("dd-MM-yyyy").format(melding.getDatumVerloopFactuur()) + "</li>";
			}
		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationNotInProcessEmail. Cannot create meldingenTekst.", e);
			throw new ServiceException(e);
		}

		String message = "";
		try {
			message = "<p>Geachte " + constructTitle(gebruiker.getGeslacht()) + " " + gebruiker.getNaam() + ",</p>"
					+ "<p>Zojuist " + (meldingen.size() == 1 ? "is" : "zijn") + " wij uw vermelding" + (meldingen.size() == 1 ? "" : "en") + " van " + vermeldBedrijf.getBedrijfsNaam() + " ontvangen.</p>"
					+ "<p>Het betreft de onderstaande vermelding" + (meldingen.size() == 1 ? "" : "en") + ": <br/><ul>" + meldingenTekst + "</ul></p>"
					+ "<p>Alvorens deze vermelding" + (meldingen.size() == 1 ? "" : "en") + " in behandeling wordt genomen, dient u uw account te verifi&#235;ren. Hiertoe hebben wij naar het adres van uw bedrijf, zoals dit bekend is bij het Handelsregister, een brief verzonden met daarin de verificatiecode.</p>"
					+ "<p>Deze code dient binnen 30 dagen na verzenden te zijn ingevoerd om uw voorlopige account om te zetten naar een definitief account. Indien verificatie niet tijdig plaatsvindt, zullen het account en de gedane vermeldingen verwijderd worden.</p>"
					+ "<p>Mocht u de verificatiebrief niet hebben ontvangen of heeft u nog vragen? Neem dan contact op met onze serviceafdeling op het telefoonnummer 085-4845700 of ga naar www.crzb.nl.</p>";
		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationNotInProcessEmail. Cannot create message.", e);
			throw new ServiceException(e);
		}

		mail.setBodyHTML(constructBody("VERMELDING NIET IN BEHANDELING", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.NOTIFICATION_ON_HOLD);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendNotificationObjectionEmail(Klant klant, Melding melding) throws ServiceException {
		List<Gebruiker> allUsers;
		List<String> allUsersEmails = new ArrayList<>();
		Bedrijf bedrijf;

		try {
			allUsers = gebruikerDataService.findAllHoofdAndKlantGebruikersOfBedrijf(klant.getBedrijfBedrijfId());
			bedrijf = bedrijfDataService.findByBedrijfId(klant.getBedrijfBedrijfId());
		} catch (DataServiceException e) {
			LOGGER.error("Error in sendNotificationObjectionEmail. Cannot fetch allUsers or bedrijf.", e);
			throw new ServiceException(e);
		}

		if (allUsers == null) {
			LOGGER.error("Error in sendNotificationObjectionEmail. allUsers == null");
			throw new ServiceException("Niet in staat gebruikers van bedrijf " + klant.getBedrijfBedrijfId() + " op te halen.");
		}
		if (bedrijf == null) {
			LOGGER.error("Error in sendNotificationObjectionEmail. bedrijf == null");
			throw new ServiceException("Niet in staat bedrijf met ID " + klant.getBedrijfBedrijfId() + " op te halen");
		}

		try {
			for (Gebruiker g : allUsers) {
				if (!g.getGebruikerId().equals(klant.getGebruikerId())) {
					allUsersEmails.add(g.getEmailAdres());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationObjectionEmail. Cannot fetch allUsersEmails.", e);
			throw new ServiceException(e);
		}

		Mail mail = constructMailObject(EEMailType.NOTIFICATION_OBJECTION, klant.getEmailAdres(), emailSenderBackoffice + emailSenderDomain, allUsersEmails.toArray(new String[allUsersEmails.size()]));

		mail.setBodyPlain(EEMailType.NOTIFICATION_OBJECTION.getOmschrijving());

		String message = "<p>Geachte medewerkers van " + bedrijf.getBedrijfsNaam() + ",</p>"
				+ "<p>Uw debiteur tekent bezwaar aan tegen vermelding " + melding.getReferentieNummerIntern() + " in het CRZB. Graag ontvangen wij uw reactie op het bezwaar.</p>"
				+ "<p>Op www.crzb.nl staat een bericht voor u klaar met de nadere gegevens en wordt u de mogelijkheid geboden het bezwaar van uw debiteur te weerleggen.</p>"
				+ "<p>Log in op uw account voor meer informatie.</p>"
				+ "<table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:4px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/login\">Inloggen</a></td></tr></table>";

		mail.setBodyHTML(constructBody("BEZWAAR VERMELDING", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, klant.getGebruikerId(), EEMailType.NOTIFICATION_OBJECTION);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void sendNotificationRemovedEmail(Gebruiker gebruiker, Bedrijf vermeldBedrijf, Melding melding) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.NOTIFICATION_REMOVED, gebruiker.getEmailAdres(), emailSenderBackoffice + emailSenderDomain, new String[] {});

		mail.setBodyPlain(EEMailType.NOTIFICATION_REMOVED.getOmschrijving());

		String message = "";
		try {
			message = "<p>Geachte " + constructTitle(gebruiker.getGeslacht()) + " " + gebruiker.getVoornaam() + " " + gebruiker.getNaam() + ",</p>"
					+ "<p>Uw vermelding van " + vermeldBedrijf.getBedrijfsNaam() + " zal niet in het CRZB worden opgenomen.</p>"
					+ "<p>Reden hiervoor is: " + ERedenVerwijderenMelding.get(melding.getRedenVerwijderenMeldingCode()).getOmschrijving().toLowerCase() + ".</p>"
					+ "<p>Heeft u nog vragen? Neem dan contact op met onze serviceafdeling op 085-4845700 of ga naar www.crzb.nl.</p>";
		} catch (Exception e) {
			LOGGER.error("Error in sendNotificationRemovedEmail. Cannot create message.", e);
			throw new ServiceException(e);
		}

		mail.setBodyHTML(constructBody("VERMELDING VERWIJDERD", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.NOTIFICATION_REMOVED);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendPasswordChangedEmail(Gebruiker gebruiker, Integer bedrijfId) throws ServiceException {
		Bedrijf bedrijf;

		try {
			bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);
		} catch (DataServiceException e) {
			LOGGER.error("Error in sendPasswordChangedEmail. Cannot fetch bedrijf.", e);
			throw new ServiceException(e);
		}

		if (bedrijf == null) {
			LOGGER.error("Error in sendPasswordChangedEmail. bedrijf == null");
			throw new ServiceException("Niet in staat om bedrijf van gebruiker " + gebruiker.getGebruikerId() + " op te halen");
		}

		Mail mail = constructMailObject(EEMailType.PASSWORD_CHANGE, gebruiker.getEmailAdres(), emailSenderAdmin + emailSenderDomain, new String[] {});

		Wachtwoord wachtwoord = gebruiker.getWachtwoord();

		if (wachtwoord == null) {
			LOGGER.error("Error in sendPasswordChangedEmail. wachtwoord == null.");
			throw new ServiceException("Niet in staat wachtwoord van gebuiker " + gebruiker.getGebruikerId() + " op te halen");
		}

		mail.setBodyPlain(EEMailType.PASSWORD_CHANGE.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(gebruiker.getGeslacht()) +  " " + gebruiker.getNaam() + ",</p>" +
				"<p>Het wachtwoord van uw account is succesvol gewijzigd.</p>" +
				"<p>Indien u uw wachtwoord niet zelf heeft gewijzigd en evenmin hiertoe toestemming heeft gegeven aan een derde, kan er sprake zijn van mogelijk misbruik van uw account. Neem in dat geval zo spoedig mogelijk contact met ons op via onderstaande contactgegevens.</p>" +
				"<p>Houd daarbij de volgende gegevens bij de hand, zodat wij de plaats van de wijziging kunnen identificeren en traceren:</p>" +
				"<p><ul><li>Tijd: " + wachtwoord.getDatumLaatsteWijziging() + "</li><li>BA-nummer: " + bedrijf.getSbdrNummer() + "</li></ul></p>";

		mail.setBodyHTML(constructBody("WACHTWOORD AANGEPAST", bedrijf.getSbdrNummer(), message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.PASSWORD_CHANGE);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendPasswordForgottenEmail(Gebruiker gebruiker) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.PASSWORD_FORGOT, gebruiker.getEmailAdres(), emailSenderNoReply + emailSenderDomain, new String[] {});

		mail.setBodyPlain(EEMailType.PASSWORD_FORGOT.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(gebruiker.getGeslacht()) + " " + gebruiker.getNaam() + ",</p>" +
				"<p>U heeft aangegeven dat u uw wachtwoord bent vergeten.</p><p>Met behulp van de onderstaande activatielink kunt u een nieuw wachtwoord instellen.</p>" +
				"<table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:4px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/resetpassword?activationid=" + gebruiker.getWachtwoord().getActivatieCode() + "&userid=" + gebruiker.getGebruikersNaam() + "\">Wachtwoord aanpassen</a></td></tr></table>" +
				"<p>Indien u uw wachtwoord niet zelf heeft gewijzigd en evenmin hiertoe toestemming heeft gegeven aan een derde, kan er sprake zijn van mogelijk misbruik van uw account. Neem in dat geval zo spoedig mogelijk contact met ons op via onderstaande contactgegevens.</p>";

		mail.setBodyHTML(constructBody("WACHTWOORD VERGETEN", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.PASSWORD_FORGOT);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendReminderActivationEmail(Klant klant) throws ServiceException {
		Bedrijf klantBedrijf;
		try {
			klantBedrijf = bedrijfDataService.findByBedrijfId(klant.getBedrijfBedrijfId());
		} catch (DataServiceException e) {
			LOGGER.error("Error in sendReminderActivationEmail. Cannot fetch klantbedrijf.", e);
			throw new ServiceException(e);
		}

		Mail mail = constructMailObject(EEMailType.ACTIVATION_REMINDER, klant.getEmailAdres(), emailSenderAdmin + emailSenderDomain, new String[] {});

		mail.setBodyPlain(EEMailType.ACTIVATION_REMINDER.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(klant.getGeslacht()) + " " + klant.getNaam() + ",</p>" +
				"<p>U heeft eerder een activatielink ontvangen, waarvan u nog geen gebruik heeft gemaakt. U wordt verzocht alsnog uw account te activeren op www.crzb.nl, zodat u onder meer openstaande facturen kunt vermelden en zodoende bijdraagt aan een betrouwbare en transparante zakelijke markt.</p>" +
				"<p>Hierbij nogmaals uw activatielink:</p>" +
				"<table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:4px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/login?activationid=" + klant.getActivatieCode() + "&userid=" + klant.getGebruikersNaam() + "&bbset=true\">Account activeren</a></td></tr></table>" +
				"<p>Mocht u eerder geen e-mail hebben ontvangen om uw account te activeren, dan kunt u tevens gebruik maken van bovenstaande activatielink.</p>";

		mail.setBodyHTML(constructBody("ACTIVATIE HERINNERING", klantBedrijf.getSbdrNummer(), message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, klant.getGebruikerId(), EEMailType.ACTIVATION_REMINDER);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendReportRequestedEmail(Gebruiker gebruiker, String referentie) throws ServiceException {
		byte[] report = null;
		Document document = null;
		Bedrijf bedrijf = null;
		try {
			String ref = SearchUtil.repNumber(referentie);
			document = documentDataService.findDocumentByReference(ref);
			if (document.getBedrijfByBedrijfIdBedrijfId() != null)
				bedrijf = bedrijfDataService.findByBedrijfId(document.getBedrijfByRapportBedrijfIdBedrijfId());
			report = documentService.getDocumentContent(EDocumentType.RAPPORT, referentie);

		} catch (Exception e) {
			LOGGER.error("Error in sendReportRequestedEmail. Cannot fetch ref, document, bedrijf or report.", e);
			throw new ServiceException(e);
		}

		if (report == null) {
			LOGGER.error("Error in sendReportRequestedEmail. report == null.");
			throw new ServiceException("Rapport met referentie " + referentie + " is leeg");
		}

		Mail mail = constructMailObject(EEMailType.REPORT_REQUESTED, gebruiker.getEmailAdres(), emailSenderBackoffice + emailSenderDomain, new String[] {});

		try {
			mail.setAttachment(report);
			if (bedrijf != null) {
				mail.setAttachmentName(EDocumentType.RAPPORT.getOmschrijving() + "_" + bedrijf.getKvKnummer()); // + "_" + document.getReferentieNummer());
				mail.setSubject(mail.getEmailType().getOmschrijving() + ": " + bedrijf.getBedrijfsNaam());
			} else {
				mail.setAttachmentName(EDocumentType.RAPPORT.getOmschrijving() + "_" + document.getReferentieNummer());
				mail.setSubject(mail.getEmailType().getOmschrijving());

			}
		} catch (Exception e) {
			LOGGER.error("Error in sendReportRequestedEmail. Cannot fetch attachment details.", e);
		}

		mail.setBodyPlain(EEMailType.REPORT_REQUESTED.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(gebruiker.getGeslacht()) + " " + gebruiker.getNaam() + ",</p><p>Hierbij ontvangt u het door u opgevraagde rapport uit het CRZB.</p><p>In de bijlage vindt u het rapport met hierin de bedrijfsgegevens en de bij het CRZB geregistreerde betalingsachterstanden van de onderneming waarover u meer te weten wil komen.</p><p>Dit rapport wordt eenmalig voor interne doeleinden verstrekt en is strikt vertrouwelijk. U dient zelf zorg te dragen voor bewaring van het rapport.</p>";

		mail.setBodyHTML(constructBody("RAPPORT OPGEVRAAGD", "", message));

		try {
			submitMail(mail, gebruiker.getGebruikerId(), EEMailType.REPORT_REQUESTED);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendVerificationReminderEmail(Klant klant) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.VERIFICATION_REMINDER, klant.getEmailAdres(), emailSenderAdmin + emailSenderDomain, new String[] {});

		mail.setBodyPlain(EEMailType.VERIFICATION_REMINDER.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(klant.getGeslacht()) + " " + klant.getNaam() + ",</p>" +
				"<p>Graag attenderen wij u erop dat u uw account bij het CRZB nog niet heeft geverifieerd.</p>" +
				"<p>Op dit moment heeft u een voorlopig account. Uw voorlopige account heeft echter een maximale geldigheidsduur van 30 dagen. Meldingen die tot op heden door u zijn gemaakt zijn nog niet verwerkt en zullen pas in behandeling genomen worden na verificatie. Wij verzoeken u derhalve om uw voorlopige account om te zetten in een definitief account. Dit is mogelijk met behulp van de verificatiecode die u per brief is toegezonden.</p>" +
				"<p>Mocht u onverhoopt te laat zijn met het invullen van de verificatiecode, dan zal dit noodzakelijk leiden tot verwijdering van uw account en vermelding(en). Wij verzoeken u daarom tijdig te verifi&#235;ren.</p>" +
				"<p>Indien u geen verificatiebrief heeft ontvangen, neem dan contact op met het CRZB via onze serviceafdeling op het telefoonnummer 085-4845700 of via ons e-mailadres support@crzb.nl.</p>";

		mail.setBodyHTML(constructBody("HERINNERING VERIFICATIE", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, klant.getGebruikerId(), EEMailType.VERIFICATION_REMINDER);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void sendVerificationSuccesEmail(Klant klant) throws ServiceException {
		Mail mail = constructMailObject(EEMailType.ACCOUNT_VERIFICATION_SUCCESS, klant.getEmailAdres(), emailSenderAdmin + emailSenderDomain, new String[]{});

		mail.setBodyPlain(EEMailType.ACCOUNT_VERIFICATION_SUCCESS.getOmschrijving());

		String message = "<p>Geachte " + constructTitle(klant.getGeslacht()) + " " + klant.getNaam() + ",</p>" +
				"<p>Uw account is succesvol geverifieerd!</p>" +
				"<p>Welkom als deelnemer bij het CRZB.</p>" +
				"<p>U kunt vanaf nu gebruik maken van de gebruikersomgeving van het CRZB en daar bedrijfsrapportages opvragen en betalingsachterstanden aanmelden. Verder kunt u gebruik maken van de Bedrijfsmonitor en het Steunvorderingsregister.</p>";

		mail.setBodyHTML(constructBody("ACCOUNT GEVERIFIEERD", "", message));

		mail.setSubject(mail.getEmailType().getOmschrijving());

		try {
			submitMail(mail, klant.getGebruikerId(), EEMailType.ACCOUNT_VERIFICATION_SUCCESS);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void verificationReminderEmailsQuartzJob() throws ServiceException {
		List<Klant> newKlanten = null;

		LocalDate today = new DateTime().toLocalDate();
		Date firstReminder = today.minusDays(verificationFirstReminderDays).toDateTimeAtStartOfDay().toDate();
		Date secondReminder = today.minusDays(verificationSecondReminderDays).toDateTimeAtStartOfDay().toDate();
		Date thirdReminder = today.minusDays(verificationThirdReminderDays).toDateTimeAtStartOfDay().toDate();

		try {
			newKlanten = klantDataService.findNonVerifiedKlanten();
		} catch (Exception e) {
			LOGGER.error("Error in verificationReminderEmailsQuartzJob. Error fetching newKlanten.", e);
			throw new ServiceException(e);
		}

		if (newKlanten == null) {
			throw new ServiceException("Lijst met nieuwe klanten is leeg");
		}

		for (Klant klant : newKlanten) {
			Date datumAangemaakt = new DateTime(klant.getDatumAangemaakt()).toLocalDate().toDateTimeAtStartOfDay().toDate();

			if (datumAangemaakt.compareTo(firstReminder) == 0 || datumAangemaakt.compareTo(secondReminder) == 0 || datumAangemaakt.compareTo(thirdReminder) == 0) {
				sendVerificationReminderEmail(klant);
			}
		}
	}

	private String constructBody(String title, String BANr, String message) {
		String BANrString = (!BANr.equals("")) ? "<td style=\"text-align:right;font-size:11px;color:white;font-weight:700;font-family:Helvetica, Arial, sans-serif;vertical-align:bottom;line-height:3em\">BA-nummer: " + BANr + "</td>" : "";

		// MBR 10-03-2016 added html header + html tag
		return "<!DOCTYPE html>\n" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
				"<head>" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
				"<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0\" />" +
				"<title>CRZB Email</title>" +
				"</head>" +
				"<body>" +
				"<div style=\"margin:0; padding:0;background:#f0f0f0\"><table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#F0F0F0\"><tr><td align=\"center\" valign=\"top\" bgcolor=\"#F0F0F0\" style=\"background-color: #F0F0F0;\"><br/><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"container\" style=\"max-width:750px;\"><tr><td class=\"container-padding content\" align=\"left\" style=\"padding-left:24px;padding-right:24px;padding-top:12px;padding-bottom:12px;background-color:#ffffff;border:1px solid #ccc;border-radius:2px;\"><br/><table width=\"100%\" style=\"border-collapse:collapse;background-color:#102c40;\"><tr><td style=\"font-size:20px;color:white;font-weight:700;font-family:Helvetica, Arial, sans-serif;line-height:2em\">&nbsp;&nbsp;" + title + "</td>" + BANrString + "<td width=\"10\">&nbsp;</td></tr></table><br/><table width=\"100%\"><tr><td style=\"text-align:right;font-family:Helvetica, Arial, sans-serif;font-size:14px;line-height:20px;color:#102c40;font-style:italic;\">CRZB  <br/></td></tr></table><table width=\"100%\"><tr><td style=\"font-family:Helvetica, Arial, sans-serif;font-size:14px;line-height:20px;text-align:left;color:#102c40;\">" + message + "<br/>Met vriendelijke groet,<br/><br/>Stichting Betalingsachterstandenregistratie<table width=\"100%\" style=\"border-collapse:collapse;\"><tr><td style=\"vertical-align:bottom\"><span style=\"font-family:Helvetica, Arial, sans-serif;font-size:11px;line-height:20px;text-align:left;color:#102c40;\">Jadelaan 20, 2132 XW Hoofddorp, kvk-nummer 64814432</span></td><td style=\"text-align:right\"></td></tr></table><br/><p style=\"border-top:4px solid #102c40;font-family:Helvetica, Arial, sans-serif;font-size:10px;line-height:9px;text-align:left;color:#102c40;\"><br/>De informatie verzonden in deze e-mail is uitsluitend bestemd voor de geadresseerde. Gebruik van deze informatie door anderen dan de geadresseerde is verboden. Indien u dit bericht ten onrechte ontvangt, wordt u verzocht de inhoud niet te gebruiken maar de afzender direct te informeren door het bericht te retourneren en het daarna te verwijderen. Openbaarmaking, vermenigvuldiging, verspreiding en/of verstrekking van de in de e-mail ontvangen informatie aan derden is niet toegestaan. Het CRZB staat niet in voor de juiste overbrenging van een verzonden e-mail, noch voor de tijdige ontvangst daarvan. Externe e-mail wordt niet gebruikt voor het aangaan van verplichtingen.<br/><br/>The information contained in this communication is confidential and may be legally privileged. It is intended solely for the use of the individual or the entity to whom it is addressed and the others authorised to receive it. If you are not the intended recipient, you are hereby notified that any disclosure, copying, distribution or taking any action in reliance of the contents of this information is strictly prohibited and may be unlawful. The CRZB is neither liable for the proper and complete transmission of the information contained in this communication nor for any delay in its receipt. Any e-mail messages are given in good faith but shall not be binding nor shall they construe any obligation.</p></td></tr></table></td></tr></table><br/><br/></td></tr></table></div>" +
				"</body>" +
				"</html>";
	}

	private Mail constructMailObject(EEMailType eEMailType, String receiver, String sender, String[] ccReceivers) {
		Mail mail = new Mail(eEMailType);
		String[] addresses = new String[1];
		addresses[0] = receiver;
		if (ccReceivers != null && ccReceivers.length > 0) mail.setAddressesCc(ccReceivers);
		mail.setAddressesTo(addresses);
		mail.setAddressFrom("\"Betalingsachterstanden.nl\" <" + sender + ">");

		return mail;
	}

	private String constructTitle(String geslacht) {
		switch (geslacht) {
			case "M":
				return "heer";
			case "V":
				return "mevrouw";
			default:
				return "heer/mevrouw";
		}
	}

	private void sendAlertEmail(Bedrijf bedrijf) throws ServiceException {
		boolean onlyAlertsToBedrijfLevel = false;

		try {
			onlyAlertsToBedrijfLevel = bedrijfDataService.bedrijfOnlyHasBedrijfLevelAlerts(bedrijf.getBedrijfId());
		} catch (DataServiceException e) {
			LOGGER.error("Error in sendAlertEmail. Cannot fetch allUsers or bedrijf.", e);
			throw new ServiceException(e);
		}

		if(onlyAlertsToBedrijfLevel){
			List<Gebruiker> allUsers;
			List<String> allUsersEmails = new ArrayList<>();
			Klant klant;

			try{
				allUsers = gebruikerDataService.findAllGebruikersOfBedrijfByBedrijfId(bedrijf.getBedrijfId());
				klant = bedrijfDataService.findKlantOfBedrijf(bedrijf.getBedrijfId());
			} catch (DataServiceException e) {
				LOGGER.error("Error in sendAlertEmail. Cannot fetch allUsers or bedrijf.", e);
				throw new ServiceException(e);
			}

			if (allUsers == null) {
				LOGGER.error("Error in sendAlertEmail. allUsers == null");
				throw new ServiceException("Niet in staat gebruikers van bedrijf " + klant.getBedrijfBedrijfId() + " op te halen.");
			}

			for (Gebruiker g : allUsers) {
				if (!g.getGebruikerId().equals(klant.getGebruikerId())&&g.getEmailAdres()!=null) {
					allUsersEmails.add(g.getEmailAdres());
				}
			}

			Mail mail = constructMailObject(EEMailType.NEW_ALERT, klant.getEmailAdres(), emailSenderSupport + emailSenderDomain, allUsersEmails.toArray(new String[allUsersEmails.size()]));

			mail.setBodyPlain(EEMailType.NEW_ALERT.getOmschrijving());

			String message = "<p>Geachte medewerkers van " + bedrijf.getBedrijfsNaam() + ",</p><p>Er is op het CRZB een bericht voor u binnengekomen.</p><p>Log in op uw account voor meer informatie.</p><table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:4px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/login\">Inloggen</a></td></tr></table><p>Heeft u nog vragen? Neem dan contact op met onze serviceafdeling op 085-4845700 of ga naar www.crzb.nl.</p>";

			mail.setBodyHTML(constructBody("NIEUW BERICHT", "", message));

			mail.setSubject(mail.getEmailType().getOmschrijving());

			try {
				submitMail(mail, klant.getGebruikerId(), EEMailType.NEW_ALERT);
			} catch (ServiceException e) {
				throw new ServiceException(e);
			}
		} else {
			List<Gebruiker> usersWithAlerts;

			try{
				usersWithAlerts = gebruikerDataService.findAllGebruikersWithAlertsByBedrijfId(bedrijf.getBedrijfId());
			}catch (DataServiceException e) {
				LOGGER.error("Error in sendAlertEmail. Cannot fetch users of bedrijf with alerts.", e);
				throw new ServiceException(e);
			}

			if(usersWithAlerts!=null){
				for(Gebruiker g : usersWithAlerts){
					Mail mail = constructMailObject(EEMailType.NEW_ALERT, g.getEmailAdres(), emailSenderSupport+emailSenderDomain, new String[]{});

					mail.setBodyPlain(EEMailType.NEW_ALERT.getOmschrijving());

					String message = "<p>Geachte medewerkers van " + bedrijf.getBedrijfsNaam() + ",</p><p>Op het CRZB is een bericht voor u binnengekomen.</p><p>Log in op uw account voor meer informatie.</p><table><tr><td style=\"height:35px;text-align:center;vertical-align:center;border-radius:2px; background-color:#f4792b; \" width=\"150\"><a style=\"font-family:Helvetica, Arial, sans-serif; font-size:13px;color:#ffffff; font-weight: 700;line-height:2.8em;display:block;text-align:center;text-decoration:none;vertical-align:center;cursor:pointer\" href=\"" + serverUrl + serverUrlExtension + "/login\">Inloggen</a></td></tr></table>";

					mail.setBodyHTML(constructBody("NIEUW BERICHT", "", message));

					mail.setSubject(mail.getEmailType().getOmschrijving());

					try {
						submitMail(mail, g.getGebruikerId(), EEMailType.NEW_ALERT);
					} catch (ServiceException e) {
						throw new ServiceException(e);
					}
				}
			}
		}
	}

	private void submitMail(Mail mail, Integer userId, EEMailType eMailType) throws ServiceException  {		
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp02.hostnet.nl");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", emailSMTPuser);
        properties.put("mail.password", emailSMTPpassword);
 
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailSMTPuser, emailSMTPpassword);
            }
        };
        Session session = Session.getInstance(properties, auth);
 
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
 
        try {
	        // add from + receipients
	        msg.setFrom(new InternetAddress(mail.getAddressFrom()));
	        List<InternetAddress> toAddresses = null;
	        if (mail.getAddressesTo() != null) {
	        	toAddresses = new ArrayList<InternetAddress>();
	        	for (int i=0; i< mail.getAddressesTo().length; i++) {
	        		toAddresses.add(new InternetAddress(mail.getAddressesTo()[i]));
	        	}
	        	msg.setRecipients(Message.RecipientType.TO, toAddresses.toArray(new InternetAddress[toAddresses.size()]));
	        }
	        msg.setSubject(mail.getSubject());
	        msg.setSentDate(new Date());
	        
	        // create multipart
	        Multipart multipart = new MimeMultipart();
	 
	        // creates message part       
	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	        if (mail.getBodyHTML() != null) {
		        messageBodyPart.setContent(mail.getBodyHTML(), "text/html; charset=utf-8");
		        multipart.addBodyPart(messageBodyPart);
	        } else {	        
		        messageBodyPart.setContent(mail.getBodyPlain(), "text/plain");
		        multipart.addBodyPart(messageBodyPart);
	        }
	        
	        if (mail.getAttachment() != null) {
	        	MimeBodyPart attachment = new MimeBodyPart();
	        	
	        	ByteArrayDataSource bds = new ByteArrayDataSource(mail.getAttachment(), "application/pdf");
	        	//ByteArrayDataSource bds = new ByteArrayDataSource(Base64.getEncoder().encodeToString(mail.getAttachment()), "application/octet-stream"); 
	        	//attachment.setHeader("Content-Transfer-Encoding", "base64");
	        	attachment.setDataHandler(new DataHandler(bds)); 
	        	attachment.setFileName(mail.getAttachmentName() + ".pdf"); 
	        	
	        	//attachment.attachFile("sample.pdf", "application/pdf", "base64");
	        	multipart.addBodyPart(attachment);        	
	        }
	         
	        // sets the multi-part as e-mail's content
	        msg.setContent(multipart);
	 
	        // sends the e-mail
	        Transport.send(msg);
        } catch (Exception e) {
        	LOGGER.error("Error sending email", e);
        }
	}
	
	private void submitMailMailjet(Mail mail, Integer userId, EEMailType eEMailType) throws ServiceException {
		try {
			if (mail.getAddressesTo() != null && mail.getAddressesTo().length > 0)
				LOGGER.info("Send e-mail: " + eEMailType.getOmschrijving() + " to (first receipient): " + mail.getAddressesTo()[0]);
			else
				LOGGER.info("Send e-mail: " + eEMailType.getOmschrijving() + " NO ADDRESS TO");

			Map<String, Object> properties = new HashMap<>();
			mail.setGebruikerId(userId);
			mail.setEmailType(eEMailType);
			
			
//			RestTemplate restTemplate = new RestTemplate();
//			
//			try {
//				HttpHeaders headers = new HttpHeaders();
//				//headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
//				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//
//				HttpEntity<Mail> entity = new HttpEntity<Mail>(mail,headers);				
//				String result = restTemplate.postForObject("http://localhost:8082/services/secure/sendMail", entity, String.class);
//				
//				if(result != null) {
//					LOGGER.info("Result sending mail: " + result);	
//				} else 
//					LOGGER.error("Cannot send mail");
//			} catch(RestClientException e) {
//				throw new Exception("Cannot call sendMail service");
//			}
			
			
            MailjetRequest request = null;
            MailjetResponse response = null;

            try {
            	String emailMailjetPublic = "b3509565e3a3983c3060ae0e9c0fbf7f";
            	String emailMailjetPrivate = "eff3976541b6251faf7ba4bd757b097a";
                MailjetClient client = new MailjetClient(emailMailjetPublic, emailMailjetPrivate, new ClientOptions("v3.1"));
                
                JSONObject content = new JSONObject()
                        .put(Emailv31.Message.FROM, new JSONObject()
                                .put("Email", mail.getAddressFrom())
                                .put("Name", "Betalingsachterstanden.nl"))
                        .put(Emailv31.Message.TO, new JSONArray()
                                .put(new JSONObject()
                                                .put("Email", mail.getAddressesTo()[0])
                                        /*.put("Name", "You")*/))
                        .put(Emailv31.Message.SUBJECT, mail.getSubject())                        
                        .put(Emailv31.Message.TEXTPART, mail.getBodyPlain())
                        .put(Emailv31.Message.HTMLPART, mail.getBodyHTML());
                
                if (mail.getAttachment() != null)
                	content.put(Emailv31.Message.ATTACHMENTS, new JSONArray()
                                .put(new JSONObject()
                                    .put("ContentType", "application/pdf")
                                    .put("Filename", mail.getAttachmentName())
                                    .put("Base64Content", Base64.getEncoder().encodeToString(mail.getAttachment()))));
                
                request = new MailjetRequest(Emailv31.resource)
                        .property(Emailv31.MESSAGES, new JSONArray()
                                .put(content));
                if ("true".equalsIgnoreCase(emailAllowToSend)) {
                    response = client.post(request);

                    // Retry once on error.
                    if (response != null && response.getStatus() >= 500) {

                        LOGGER.warn("Got response 500 from mailjet, retrying once in 5 seconds.");

                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException ie) {
//                            Thread.currentThread().interrupt();
                        }

                        response = client.post(request);
                    }
                }               
            } catch (Exception ex) {
                LOGGER.warn("submitMail error for userId: " + userId + " and mailtype: " + (eEMailType != null ? eEMailType.getOmschrijving() : " onbekend"), ex);
            }			

			LOGGER.info("Email sent by process: " + eEMailType.getOmschrijving() + " userid: " + userId);
		} catch (Exception e) {
			LOGGER.error("Error Sending email of type:" + eEMailType.getOmschrijving() + " to userid:" + userId, e);
			throw new ServiceException(e);
		}
	}
}