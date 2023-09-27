package nl.devoorkant.exactonline.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import nl.devoorkant.sbdr.business.service.EmailService;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.service.BedrijfDataService;
import nl.devoorkant.sbdr.data.service.GebruikerDataService;
import nl.devoorkant.sbdr.data.service.MeldingDataService;
import nl.devoorkant.sbdr.spring.ZbrApp;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ZbrApp.class)
public class EmailTest {
	@Autowired
	private BedrijfDataService bedrijfDataService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private GebruikerDataService gebruikerDataService;
	@Autowired
	private MeldingDataService meldingDataService;
	Logger ioLogger = LoggerFactory.getLogger(EmailTest.class);
	
	@Test(timeout=200000)
	@Transactional
	public void testAllEmails() throws Exception {

		try {
			Klant klant = bedrijfDataService.findKlantOfBedrijf(76);
			Gebruiker gebruiker = gebruikerDataService.findById(69);
			Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(76);
			//Melding melding = meldingDataService.findById(5);
			//List<Melding> meldingen = new ArrayList<>();
			//meldingen.add(melding);
			//HashMap<Bedrijf, List<Melding>> emailList = new HashMap<>();
			//emailList.put(bedrijf, meldingen);
			String referentie = "RA-18PMNZG9LC";

//			emailService.alertsReminderEmailsQuartzJob();
//
//			emailService.verificationReminderEmailsQuartzJob();
//
			emailService.sendActivationEmail(klant, bedrijf);
//
//			emailService.sendAlertEmail(klant);
//
//			emailService.sendMonitoringChangedEmails(bedrijf);
//
//			emailService.sendNewUserConfirmationEmail(klant, gebruiker);
//
		emailService.sendNewUserEmail(gebruiker, gebruiker.getBedrijfBedrijfId());
//
//			emailService.sendNotificationObjectionEmail(klant, melding);
//
//			emailService.sendNotificationInProcessEmail(gebruiker, bedrijf, meldingen);
//
//			emailService.sendNotificationIsActiveEmail(klant, emailList);
//
//			emailService.sendNotificationIsPaidEmail(klant);
//
//			emailService.sendNotificationNotInProcessEmail(gebruiker, bedrijf, meldingen);
//
//			emailService.sendNotificationRemovedEmail(gebruiker, bedrijf, melding);
//
//			emailService.sendPasswordChangedEmail(gebruiker);
//
//			emailService.sendPasswordForgottenEmail(gebruiker);
//
//			emailService.sendReminderActivationEmail(klant);
//
			emailService.sendReportRequestedEmail(gebruiker, referentie);
//
//			emailService.sendVerificationReminderEmail(klant);
		} catch(Exception e) {
			ioLogger.error(e.getMessage());
			assert (false);
		}

		assert (true);
	}	
}
