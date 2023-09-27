package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.BedrijfManaged;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;

import java.util.HashMap;
import java.util.List;

public interface EmailService {

	void alertsReminderEmailsQuartzJob() throws ServiceException;

	void sendActivationEmail(Klant klant, Bedrijf bedrijf) throws ServiceException;
	
	void sendExistingAccountEmail(Bedrijf bedrijf, Klant newKlant) throws ServiceException;

	void sendMonitoringChangedEmails(Bedrijf bedrijfInMonitoring) throws ServiceException;

	void sendNewUserConfirmationEmail(Klant klant, Gebruiker gebruiker) throws ServiceException;
	
	void sendNewUserBedrijfManagedEmail(Gebruiker gebruiker, Integer bedrijfId, BedrijfManaged bedrijfManaged) throws ServiceException;

	void sendNewUserEmail(Gebruiker gebruiker, Integer bedrijfId) throws ServiceException;

	void sendNotificationInProcessEmail(Gebruiker gebruiker, Bedrijf vermeldBedrijf, List<Melding> meldingen) throws ServiceException;

	void sendNotificationIsActiveEmail(Klant klant, HashMap<Bedrijf, List<Melding>> emailList) throws ServiceException;

	void sendNotificationIsPaidEmail(Klant klant) throws ServiceException;

	void sendNotificationNewInvoice(Klant klant, Integer factuurId) throws ServiceException;
	
	void sendNotificationNotInProcessEmail(Gebruiker gebruiker, Bedrijf vermeldBedrijf, List<Melding> meldingen) throws ServiceException;

	void sendNotificationObjectionEmail(Klant k, Melding mdng) throws ServiceException;

	void sendNotificationRemovedEmail(Gebruiker gebruiker, Bedrijf vermeldBedrijf, Melding melding) throws ServiceException;

	void sendPasswordChangedEmail(Gebruiker gebruiker, Integer bedrijfId) throws ServiceException;

	void sendPasswordForgottenEmail(Gebruiker gebruiker) throws ServiceException;

	void sendReminderActivationEmail(Klant klant) throws ServiceException;

	void sendReportRequestedEmail(Gebruiker gebruiker, String referentie) throws ServiceException;

	void sendVerificationReminderEmail(Klant klant) throws ServiceException;

	void sendVerificationSuccesEmail(Klant klant) throws ServiceException;

	void verificationReminderEmailsQuartzJob() throws ServiceException;
}
