package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.util.CompareUtil;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.business.job.AssignExpiredObjectionsToAdminJob;
import nl.devoorkant.sbdr.business.job.DeleteOverdueKlantenJob;
import nl.devoorkant.sbdr.business.job.SendEmailReminderNewAccountKlantenJob;
import nl.devoorkant.sbdr.business.util.*;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.repository.CIKvKDossierRepository;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.*;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;

@Service("companyAccountService")
@Transactional(readOnly = true)
public class CompanyAccountServiceImpl implements CompanyAccountService {

	@Autowired
	Scheduler scheduler;
	
	@Value("${job.cron.send_email_reminder_new_account_klanten}")
	String cronExpressionSendEmailReminderNewAccountKlanten;
	
	@Value("${job.cron.delete_overdue_klanten}")
	String cronExpressionDeleteOverdueKlanten;
	
	@Autowired
	private AlertService alertService;

	@Autowired
	private BedrijfDataService bedrijfDataService;

	@Autowired
	private BedrijfService bedrijfService;

	@Autowired
	private CompanyInfoService companyInfoService;
	
	@Autowired
	CIKvKDossierRepository ciKvkRepo;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private EmailService emailService;

	//	@Value("${email_alertnewaccount}")
	//	private String emailAlertNewAccount;
	//
	//	@Value("${email_sender}")
	//	private String emailSender;
	//
	//	@Value("${email_support}")
	//	private String emailSupport;

	@Autowired
	private GebruikerDataService gebruikerDataService;

	@Autowired
	private GebruikerService gebruikerService;

	@Autowired
	private InternalProcessService internalProcessService;

	@Autowired
	private KlantDataService klantDataService;

	@Autowired
	private KortingsCodeService kortingsCodeService;

	@Autowired
	private MeldingDataService meldingDataService;


	@Value("${overduedays_newaccount}")
	private int newAccountOverdueDays;

	@Value("${newAccountReminderHours}")
	private int newAccountReminderHours;

	@Value("${recaptchaSecret}")
	private String recaptchaSecret;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RolDataService rolDataService;

	@Value("${serverurl}")
	private String serverurl;

	@Autowired
	private WachtwoordDataService wachtwoordDataService;

	@Autowired
	private WachtwoordStatusDataService wachtwoordStatusDataService;
	
	@Autowired
	private InternalProcessDataService internalProcessDataService;

	@Autowired
	private MobileGebruikerService mobileGebruikerService;

	@Autowired
	private SupportService supportService;

	private static Logger LOGGER = LoggerFactory.getLogger(CompanyAccountServiceImpl.class);

	public CompanyAccountServiceImpl() {
		LOGGER.debug("create CompanyAccountServiceImp");
	}
	
	@PostConstruct
	public void createSchedule() {        
        JobDetail jobDetail = buildJobDetailSendEmailReminderNewAccountKlanten();
        Trigger trigger = buildJobTriggerSendEmailReminderNewAccountKlanten(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start companyAccount schedule SendEmailReminderNewAccountKlanten", e);
		} 
        
        jobDetail = buildJobDetailDeleteOverdueKlanten();
        trigger = buildJobTriggerDeleteOverdueKlanten(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start companyAccount schedule DeleteOverdueKlanten", e);
		}        
	}
	
	private JobDetail buildJobDetailSendEmailReminderNewAccountKlanten() {

        return JobBuilder.newJob(SendEmailReminderNewAccountKlantenJob.class)
                .withIdentity(UUID.randomUUID().toString(), "companyaccount-jobs")
                .withDescription("Send email reminder new account klanten")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTriggerSendEmailReminderNewAccountKlanten(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "companyaccount")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionSendEmailReminderNewAccountKlanten))
    			  .build();    	
    }  	
    
	private JobDetail buildJobDetailDeleteOverdueKlanten() {

        return JobBuilder.newJob(DeleteOverdueKlantenJob.class)
                .withIdentity(UUID.randomUUID().toString(), "companyaccount-jobs")
                .withDescription("Delete overdue klanten")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTriggerDeleteOverdueKlanten(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "companyaccount")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionDeleteOverdueKlanten))
    			  .build();    	
    }     

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation=Isolation.SERIALIZABLE)
	public ErrorService activateKlant(String activatieCode, String gebruikersNaam) throws ServiceException {
		try {
			if(activatieCode != null) {
				Klant bestaandeklant = klantDataService.findByKlantActivatieCode(activatieCode);
				if(bestaandeklant == null || !EKlantStatus.canBeActivated(bestaandeklant.getKlantStatusCode()) || !bestaandeklant.getGebruikersNaam().equals(gebruikersNaam)) {
					LOGGER.warn("Klant not activated. Code " + activatieCode + " not found.");
					return new ErrorService(ErrorService.ACCOUNT_NOT_ACTIVATED);
				}

				// not yet for DATA_NOK accounts....
				if(bestaandeklant.getKlantStatusCode().equals(EKlantStatus.REGISTRATIE.getCode())) {
					bestaandeklant.setKlantStatusCode(EKlantStatus.PROSPECT.getCode());
					bestaandeklant.setActivatieCode(generateActivatieCodeBrief());
				}
				bestaandeklant.setActief(true);

				bestaandeklant.getWachtwoord().setWachtwoordStatus(wachtwoordStatusDataService.findByPrimaryKey(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE));

				Klant nieuweklant = klantDataService.save(bestaandeklant);

				// TODO: Instead of letter send email to support with new activationcode
				//sendActivationTwoFactorEmail(nieuweklant);
				// Create new account letter

				// not yet for DATA_NOK accounts....REGISTRATIE already updated to PROSPECT
				if(bestaandeklant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode()))
					documentService.createNewAccountLetterPdf(nieuweklant.getGebruikerId());

				return null;
			} else return new ErrorService(ErrorService.USER_NOT_ACTIVATED);
		} catch(Exception e) {
			throw new ServiceException("Cannot activateKlant: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService activateKlantTwoFactorCode(String activatieCode, Integer gebruikerId) throws ServiceException {
		try {
			if(activatieCode != null && gebruikerId != null) {
				Klant klant = klantDataService.findByGebruikerId(gebruikerId);

				if(klant == null) return new ErrorService(ErrorService.CANNOT_ACTIVATE_KLANT);

				Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(klant.getBedrijfBedrijfId());
				if(hasBedrijfAccount(bedrijf.getKvKnummer(), bedrijf.getSubDossier(), true)) {
					// send email warning
					return new ErrorService(ErrorService.COMPANY_ACCOUNT_EXISTS);
				}

				if(klant.getActief() && klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode())) {
					if(activatieCode.equals(klant.getActivatieCode())) {
						klant.setActief(true);
						klant.setActivatieCode(null);
						klant.setKlantStatusCode(EKlantStatus.ACTIEF.getCode());

						klantDataService.save(klant);

						// Send account successfully verified email + report discount 
						emailService.sendVerificationSuccesEmail(klant);
						
						return null;
					} else return new ErrorService(ErrorService.WRONG_ACTIVATION_CODE);
				} else return new ErrorService(ErrorService.CANNOT_ACTIVATE_KLANT);
			} else return new ErrorService(ErrorService.CANNOT_ACTIVATE_KLANT);
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot activateKlant: " + e.getMessage());
		}
	}

	@Override
	public boolean checkIfUserCanPay(Integer gId, Integer bId) throws ServiceException {
		if(gId != null) {
			try {
				// to do check if gId works at bId, also for BedrijfManaged!
				//klantDataService.findKlantOfBedrijfByBedrijfId(bId);
				Klant k = klantDataService.findKlantOfGebruikerByGebruikerId(gId, bId);
			
				if(k != null) {
					return k.isAkkoordIncasso() && k.getBankrekeningNummer() != null && k.getTenaamstelling() != null;
				} else {
					LOGGER.error("Method checkIfUserCanPay: No Klant found with gebruikerId " + gId);
					throw new ServiceException(ErrorService.NO_OBJECTS_FOUND);
				}
			} catch(DataServiceException e) {
				LOGGER.error("Method checkIfUserCanPay: " + e.getMessage());
				throw new ServiceException(e);
			}
		} else {
			LOGGER.error("Method checkIfUserCanPay: Parameter is null");
			throw new ServiceException(ErrorService.PARAMETER_IS_EMPTY);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	private void createNewKlantRecord(Integer gebruikerId) {
		try {
			// dirty, gebruiker must be converted to a klant-gebruiker
			klantDataService.createNewKlantRecord(gebruikerId);
			
//			EntityManager em = entityManagerFactory.createEntityManager();
//			String query = "INSERT INTO klant(gebruiker_id, KlantStatus, BTWNummer, ActivatieReminderSent) VALUES (?,'ACT','', 0)";
//			em.getTransaction().begin();
//			Query cq = em.createNativeQuery(query);
//			cq.setParameter(1, gebruikerId);
//			cq.unwrap( org.hibernate.SQLQuery.class ).addSynchronizedEntityClass( Klant.class );
//			cq.unwrap( org.hibernate.SQLQuery.class ).addSynchronizedEntityClass( Gebruiker.class );
//
//	        cq.executeUpdate();
//	        em.getTransaction().commit();        
//	        em.close();
			
		} catch (Exception e) {
			LOGGER.error("Cannot create new klant record: " + e.getMessage());
			throw new ServiceException(e);
		}
	}
	@Override
	@Transactional
	public ErrorService createAccount(Klant klant, Bedrijf bedrijf, Boolean adresOk, String opmerkingenAdres) throws ServiceException {
		try {
			// Only active companies can create account!!
			bedrijf.setBedrijfActief(true);

			if(bedrijf.getBedrijfId() != null) {
				Bedrijf bestaandBedrijf = bedrijfDataService.findByBedrijfId(bedrijf.getBedrijfId());

				if(bestaandBedrijf != null) {
					if(bestaandBedrijf.getBedrijfStatusCode().equals(EBedrijfStatus.VERWIJDERD.getCode())) {
						return new ErrorService(ErrorService.COMPANY_BLOCKED);
					}
				}
			}

			// Check if bedrijf already has account
			if(hasBedrijfAccount(bedrijf.getKvKnummer(), bedrijf.getSubDossier(), false)) {
				// send email warning
				//emailService.sendExistingAccountEmail(bedrijf,klant);
				return new ErrorService(ErrorService.COMPANY_ACCOUNT_EXISTS);
			}

			Klant nieuweklant = null;
			
			// Check if klantgebruiker already exists
			Gebruiker bestaandegebruiker = gebruikerDataService.findByGebruikersnaam(klant.getGebruikersNaam());
			if(bestaandegebruiker != null ) {
				if (bestaandegebruiker.getBedrijf() != null)
					return new ErrorService(ErrorService.USERNAME_ALREADY_EXISTS);
				else {
					// upgrade gebruiker to klant
					// this is in a dirty way, because of JPA object structure
					createNewKlantRecord(bestaandegebruiker.getGebruikerId());
			        nieuweklant = klantDataService.findByGebruikerId(bestaandegebruiker.getGebruikerId());
			        
					// there is already a user with bedrijfManaged companies only, so use existing user
			        Wachtwoord wachtwoord = nieuweklant.getWachtwoord();
					wachtwoord.setWachtwoord(passwordEncoder.encode(klant.getWachtwoord().getWachtwoord()));
					wachtwoord.setWachtwoordStatus(wachtwoordStatusDataService.findByPrimaryKey(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL));
					
					wachtwoord = wachtwoordDataService.save(wachtwoord);							
					nieuweklant.setWachtwoord(wachtwoord);
				}
			} else {
				nieuweklant = klant;
				
				// encode password
				Wachtwoord wachtwoord = nieuweklant.getWachtwoord();
				wachtwoord.setWachtwoord(passwordEncoder.encode(wachtwoord.getWachtwoord()));
				wachtwoord.setWachtwoordStatus(wachtwoordStatusDataService.findByPrimaryKey(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL));

				wachtwoord = wachtwoordDataService.save(wachtwoord);	
				nieuweklant.setWachtwoord(wachtwoord);
			}

			//Validate discount code
			if(klant.getKortingsCode() != null) {
				if(klant.getKortingsCode().getCode() != null)
					if(!kortingsCodeService.checkIfCodeIsValid(klant.getKortingsCode().getCode(), new Date())) {
						if(kortingsCodeService.checkIfCodeIsExpired(klant.getKortingsCode().getCode(), new Date()))
							return new ErrorService(ErrorService.EXPIRED_DISCOUNT_CODE);
						else return new ErrorService(ErrorService.INVALID_DISCOUNT_CODE);
					}
			}

			//Check if bedrijf exists and is 'deleted': Bedrijf = DEL when Klant is removed/disabled or Klant account is overdue
			if(bedrijf.getBedrijfStatusCode() != null && bedrijf.getBedrijfStatusCode().equals(EBedrijfStatus.VERWIJDERD.getCode())) {
				return new ErrorService(ErrorService.CREATE_ACC_COMPANY_DELETED);
			}

			Bedrijf savedBedrijf = null;
			Bedrijf bedrijfBestaand = null;

			// Check if bedrijf record already exists
			try {
				if(bedrijf.getBedrijfId() != null)
					bedrijfBestaand = bedrijfDataService.findByBedrijfId(bedrijf.getBedrijfId());
				else
					bedrijfBestaand = bedrijfDataService.findByKvkNummer(bedrijf.getKvKnummer(), bedrijf.getSubDossier());
			} catch(DataServiceException d) {
				throw new ServiceException("Cannot create new account data");
			}

			if(bedrijfBestaand != null) {
				if(bedrijfBestaand != null) {
					bedrijfBestaand.setBedrijfActief(bedrijf.isBedrijfActief());
					bedrijfBestaand.setBedrijfsNaam(bedrijf.getBedrijfsNaam());
					bedrijfBestaand.setStraat(bedrijf.getStraat());
					bedrijfBestaand.setHuisNr(bedrijf.getHuisNr());
					bedrijfBestaand.setHuisNrToevoeging(bedrijf.getHuisNrToevoeging());
					bedrijfBestaand.setPostcode(bedrijf.getPostcode());
					bedrijfBestaand.setPlaats(bedrijf.getPlaats());
					bedrijfBestaand.setPostPostbus(bedrijf.getPostPostbus());
					bedrijfBestaand.setPostPostcode(bedrijf.getPostPostcode());
					bedrijfBestaand.setPostPlaats(bedrijf.getPostPlaats());
					if(bedrijf.getTelefoonnummer() != null)
						bedrijfBestaand.setTelefoonnummer(bedrijf.getTelefoonnummer());
					bedrijfBestaand.setAdresOk(adresOk);
					// If bedrijf was inactive
					bedrijfBestaand.setBedrijfStatusCode(EBedrijfStatus.ACTIEF.getCode());
					bedrijfBestaand.setActief(true);
					bedrijfBestaand.setDatumWijziging(new Date());
					savedBedrijf = bedrijfDataService.save(bedrijfBestaand);
				} else return new ErrorService(ErrorService.CANNOT_SAVE_COMPANY);
			} else {
				// Save new bedrijf + klant
				bedrijf.setSbdrNummer(createNewSbdrNummer());
				bedrijf.setBedrijfStatusCode(EBedrijfStatus.ACTIEF.getCode());
				bedrijf.setAdresOk(adresOk);
				bedrijf.setActief(true);
				bedrijf.setDatumWijziging(new Date());
				savedBedrijf = bedrijfDataService.save(bedrijf);
			}

			nieuweklant.setBtwnummer(klant.getBtwnummer());
			nieuweklant.setNietBtwPlichtig(klant.isNietBtwPlichtig());
			nieuweklant.setTelefoonNummer(klant.getTelefoonNummer());
			if(adresOk) nieuweklant.setKlantStatusCode(EKlantStatus.REGISTRATIE.getCode());
			else nieuweklant.setKlantStatusCode(EKlantStatus.DATA_NOK.getCode());
			nieuweklant.setBedrijf(savedBedrijf);
			nieuweklant.setDatumAangemaakt(new Date());
			nieuweklant.setBriefStatusCode(EBriefStatus.NIET_VERWERKT.getCode());
			
			nieuweklant.setFunctie(klant.getFunctie());
			nieuweklant.setAfdeling(klant.getAfdeling());
			nieuweklant.setNaam(klant.getNaam());
			nieuweklant.setVoornaam(klant.getVoornaam());

			nieuweklant.setActivatieCode(generateActivatieCodeEmail());

			Rol rol = rolDataService.findByCode(ERol.KLANT.getCode());
			Set<Rol> rollen = new HashSet<Rol>();
			if(rol != null) rollen.add(rol);
			// klant has always right to add/change registrations
			rol = rolDataService.findByCode(ERol.REGISTRATIESTOEGESTAAN.getCode());
			if(rol!=null) rollen.add(rol);
			nieuweklant.setRollen(rollen);

			nieuweklant.setActief(true);			
			nieuweklant = klantDataService.save(nieuweklant);

			emailService.sendActivationEmail(nieuweklant, savedBedrijf);

			return null;
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot createAccount: " + e.getMessage());
		} 
	}

	@Override
	@Transactional
	public Bedrijf createBedrijfFromCIKvkDossier(CIKvKDossier kvkDossier, boolean createNewOrUpdate, boolean activateBedrijf) throws ServiceException, ThirdPartyServiceException {
		boolean bedrijfIsActief = true;
		Bedrijf bedrijf = new Bedrijf();

		if(kvkDossier.getDatumOpheffing() != null || kvkDossier.getIndicatieFaillissement().equals("J") || kvkDossier.getIndicatieSurseance().equals("J"))
			bedrijfIsActief = false;

		// bedrijf is actief bij KVK
		bedrijf.setBedrijfActief(bedrijfIsActief);

		//bedrijf.setKvKnummer(kvkDossier.getKvKnummer());
		bedrijf.setKvKnummer(kvkDossier.getDossierNr());
		// null, empty or 0000 subDossier numbers result in bedrijf.subDossier = null
		// MBR subdossier issue 16-2-2016 may be 0000!!!!
		if(kvkDossier.getSubDossier() != null) // && !kvkDossier.getSubDossier().replace('0', ' ').trim().equals(""))
			bedrijf.setSubDossier(kvkDossier.getSubDossier());
		else bedrijf.setSubDossier(null);

		boolean subdossierNullNotHoofd = false;
		
		// This is not certain....
		if(kvkDossier.getHoofdNeven() != null) {
			if(kvkDossier.getHoofdNeven().startsWith("H")) {
				bedrijf.setHoofdNeven(EBedrijfType.HOOFD.getCode());
			}
			else { 
				// prepare hack for main kvk nr. 
				if(bedrijf.getSubDossier() == null) subdossierNullNotHoofd = true;
				
				bedrijf.setHoofdNeven(EBedrijfType.NEVEN.getCode());
			}

		} else {
			if(bedrijf.getSubDossier() == null) bedrijf.setHoofdNeven(EBedrijfType.HOOFD.getCode());
			else bedrijf.setHoofdNeven(EBedrijfType.NEVEN.getCode());
		}

		// Try to find HQ of company
		CompanyInfo companyHq = null;
		if (bedrijf.getHoofdNeven().equals(EBedrijfType.NEVEN.getCode())) {
			companyHq = companyInfoService.retrieveHqFromCompanyInfo(bedrijf.getKvKnummer());
			// 
			if (companyHq != null && companyHq.getKvKnummer().equals(bedrijf.getKvKnummer()) && (companyHq.getSub() == null || companyHq.getSub().equals(bedrijf.getSubDossier())))
			{
				// fix for wrong Hoofd/Neven values in database
				// shouldn't be needed if bedrijf contains correct value
				LOGGER.info("Reset hoofd/neven of company, because of wrong hoofd/neven: " + bedrijf.getBedrijfId() + " " + bedrijf.getBedrijfsNaam());
				bedrijf.setHoofdNeven(companyHq.getCreditSafeHeadQuarters());
				
				// don't forget to save this change in dossier!
				
				companyHq = null;
			}
		}
		if(createNewOrUpdate && companyHq != null) {
			Bedrijf hoofdBedrijf = findBedrijf(companyHq.getKvKnummer(), companyHq.getSub());

			if(hoofdBedrijf == null) {
				try {
					// find + save Hq
					CIKvKDossier kvkDossierHq = companyInfoService.getCIKvKDossierFromCompanyInfo(companyHq.getKvKnummer(), companyHq.getSub(), true); // HQ of company

					// if found save/update bedrijfsinfo if new data is available
					kvkDossierHq = companyInfoService.saveCIKvkDossier(kvkDossierHq); // MBR 05-11-2015: changed parameter kvkDossier in kvkDossierHq

					if((kvkDossier.getKvKnummer() != null && kvkDossierHq.getKvKnummer() != null && !kvkDossier.getKvKnummer().equals(kvkDossierHq.getKvKnummer())) || ((kvkDossier.getSubDossier() == null && kvkDossierHq.getSubDossier() != null) || (kvkDossier.getSubDossier() != null && kvkDossierHq.getSubDossier() == null) || !kvkDossier.getSubDossier().equals(kvkDossierHq.getSubDossier())))
						createBedrijfFromCIKvkDossier(kvkDossierHq, createNewOrUpdate, false);
					//else
					//	bedrijf.setHoofdNeven(EBedrijfType.HOOFD.getCode()); // might be wrong.... but if subdossier has no other HQ kvkdossier then
				} catch(ServiceException e) {
					LOGGER.warn("Hoofdvestiging could not be fetched/created/saved of " + bedrijf.getKvKnummer() + ": " + e.getMessage());
					// do nothing
				} 
			}
							
		} else if (companyHq == null) {
			// hack for main kvk nr. Not sure if this is ok
			// if there is NOT a hoofdBedrijf + subdossier of bedrijf was null then bedrijf is HOOFD
			if (subdossierNullNotHoofd && bedrijf.getSubDossier() == null) 
				bedrijf.setHoofdNeven(EBedrijfType.HOOFD.getCode());				
		}
		
		// if Hoofd/Neven is changed update dossier
		if (bedrijf != null && bedrijf.getHoofdNeven() != null && !bedrijf.getHoofdNeven().equals(kvkDossier.getHoofdNeven())) {
			kvkDossier.setHoofdNeven(bedrijf.getHoofdNeven());
			ciKvkRepo.save(kvkDossier);
		}

		// Replaced handelsNaam with venNaam
		// if(kvkDossier.getHandelsNaam() != null && !kvkDossier.getHandelsNaam().equals(""))
		// 	bedrijf.setBedrijfsNaam(kvkDossier.getHandelsNaam());
		if(kvkDossier.getVenNaam() != null && !kvkDossier.getVenNaam().equals(""))
			bedrijf.setBedrijfsNaam(kvkDossier.getVenNaam());
		else if(kvkDossier.getHn1x45() != null && !kvkDossier.getHn1x45().equals(""))
			bedrijf.setBedrijfsNaam(kvkDossier.getHn1x45());
		else bedrijf.setBedrijfsNaam(kvkDossier.getHn1x30());


		bedrijf.setStraat(kvkDossier.getStraat());
		try {
			bedrijf.setHuisNr(ConvertUtil.convertStringToInteger(kvkDossier.getHuisnummer()));
		} catch(NumberFormatException e) {
			LOGGER.warn("Cannot convert huisnummer.");
		}
		bedrijf.setHuisNrToevoeging(kvkDossier.getHuisnummerToevoeging());
		bedrijf.setPostcode(kvkDossier.getPostcode());
		bedrijf.setPlaats(kvkDossier.getPlaats());

		// if there is a postbus
		if(kvkDossier.getStraatCa() != null && kvkDossier.getStraatCa().equalsIgnoreCase("Postbus")) {
			bedrijf.setPostPostbus(kvkDossier.getStraatHuisnummerCa());
			bedrijf.setPostPostcode(kvkDossier.getPostcodeCa());
			bedrijf.setPostPlaats(kvkDossier.getPlaatsCa());
		}

		bedrijf.setCIKvKDossier(kvkDossier);
		LOGGER.info("bedrijf.kvkDossier id: " + bedrijf.getCIKvKDossierCikvKdossierId());

		Bedrijf bedrijfBestaand = null;
		try {
			bedrijfBestaand = bedrijfDataService.findByKvkNummer(bedrijf.getKvKnummer(), bedrijf.getSubDossier());
		} catch(DataServiceException d) {
			throw new ServiceException("Cannot create new account data");
		}

		if(createNewOrUpdate) {
			try {
				if(bedrijfBestaand != null) {

					if(!bedrijfBestaand.isHandmatigGewijzigd()) {
						bedrijfBestaand.setHoofdNeven(bedrijf.getHoofdNeven());
						bedrijfBestaand.setBedrijfActief(bedrijf.isBedrijfActief());
						bedrijfBestaand.setBedrijfsNaam(bedrijf.getBedrijfsNaam());
						bedrijfBestaand.setStraat(bedrijf.getStraat());
						bedrijfBestaand.setHuisNr(bedrijf.getHuisNr());
						bedrijfBestaand.setHuisNrToevoeging(bedrijf.getHuisNrToevoeging());
						bedrijfBestaand.setPostcode(bedrijf.getPostcode());
						bedrijfBestaand.setPlaats(bedrijf.getPlaats());
						bedrijfBestaand.setPostPostbus(bedrijf.getPostPostbus());
						bedrijfBestaand.setPostPostcode(bedrijf.getPostPostcode());
						bedrijfBestaand.setPostPlaats(bedrijf.getPostPlaats());
						// Telnr could be changed on klant creation. On klant creation isHandmatigGewijzigd == false
						if(bedrijf.getTelefoonnummer() != null)
							bedrijfBestaand.setTelefoonnummer(bedrijf.getTelefoonnummer());
						bedrijfBestaand.setDatumWijziging(new Date());
					}
					if(activateBedrijf) {
						// If bedrijf was inactive
						bedrijf.setBedrijfStatusCode(EBedrijfStatus.ACTIEF.getCode());
						bedrijf.setActief(true);
					}
					bedrijf = bedrijfDataService.save(bedrijfBestaand);
				} else if(activateBedrijf) {
					bedrijf.setAdresOk(true);
					bedrijf.setSbdrNummer(createNewSbdrNummer());
					bedrijf.setBedrijfStatusCode(EBedrijfStatus.ACTIEF.getCode());
					bedrijf.setActief(true);
					bedrijf = bedrijfDataService.save(bedrijf);
				} else {
					// create bedrijf data, but not activate as account
					bedrijf.setAdresOk(true);
					bedrijf.setSbdrNummer(createNewSbdrNummer());
					bedrijf.setBedrijfStatusCode(EBedrijfStatus.INACTIEF.getCode());
					bedrijf.setActief(false);
					bedrijf = bedrijfDataService.save(bedrijf);
				}

			} catch(DataServiceException e) {
				throw new ServiceException("Cannot save Bedrijf");
			}
		}
		// Set BedrijfId on existing company for saving 'new' company data later on account creation
		else if(bedrijfBestaand != null) bedrijf.setBedrijfId(bedrijfBestaand.getBedrijfId());

		return bedrijf;
	}

	@Override
	@Transactional
	public void createNewAccountLetterForKlant(Integer bedrijfId) throws ServiceException {
		try {
			if(bedrijfId != null) {
				Klant bestaandeklant = klantDataService.findKlantOfBedrijfByBedrijfId(bedrijfId);

				documentService.removeOldNewAccountLetter(bedrijfId);

				//bestaandeklant.setKlantStatusCode(EKlantStatus.PROSPECT.getCode());
				bestaandeklant.setActivatieCode(generateActivatieCodeBrief());

				Klant nieuweklant = klantDataService.save(bestaandeklant);

				documentService.createNewAccountLetterPdf(nieuweklant.getGebruikerId());
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public ErrorService deleteKlant(Integer klantId, Integer gebruikerId) throws ServiceException {
		try {
			Klant klant = klantDataService.findByKlantId(klantId);
			if(klant != null) {
				Bedrijf bedrijf = null;

				if(klant.getKlantStatusCode().equals(EKlantStatus.ACTIEF.getCode())) {
					bedrijf = klant.getBedrijf();
					//klant.setActief(false);
					klant.setKlantStatusCode(EKlantStatus.VERWIJDERD.getCode());
					deleteAllMeldingenOfKlant(bedrijf.getBedrijfId());

				} else if(klant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode()) || klant.getKlantStatusCode().equals(EKlantStatus.REGISTRATIE.getCode()) || klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode())) {
					bedrijf = klant.getBedrijf();
					//klant.setActief(false);
					klant.setKlantStatusCode(EKlantStatus.AFGEWEZEN.getCode()); //
					deleteAllMeldingenOfKlant(bedrijf.getBedrijfId());

				} else {
					return new ErrorService(ErrorService.CANNOT_REMOVE_KLANT);
				}

				//Delete customer letter, if it exists
				for(InternalProcess iP : klant.getInternalProcesses()) {
					//internalProcessService.removeInternalProcessRow(iP.getInternalProcessId());
					klant.getInternalProcesses().remove(iP);
					internalProcessDataService.delete(iP);
				}				

				//Remove all monitors
				bedrijfService.removeMonitorsOfBedrijf(bedrijf.getBedrijfId());

				//Set all relevant document as inactive
				documentService.removeAllDocumentsOfBedrijf(bedrijf.getBedrijfId());

				//Remove all MCKs of all users
				mobileGebruikerService.removeAllMobileGebruikerRecordsByBedrijfId(bedrijf.getBedrijfId());

				//Set all users of klant on inactive
				deleteUsersOfKlant(klant.getGebruikerId(), bedrijf);

				//Delete klant gebruiker
				gebruikerService.deleteGebruiker(klant.getGebruikerId(), bedrijf.getBedrijfId());

				//Archive all support tickets
				supportService.archiveAllSupportTicketsOfBedrijf(bedrijf.getBedrijfId());

				//Remove any and all alerts meant for the company
				alertService.deleteAlertsByBedrijfId(bedrijf.getBedrijfId());

				klant.setActief(false);

				klantDataService.save(klant);

				if(bedrijf != null) {
					bedrijf.setBedrijfStatusCode(EBedrijfStatus.VERWIJDERD.getCode());
					bedrijf.setActief(false);
					bedrijf.setDatumWijziging(new Date());
					bedrijfDataService.save(bedrijf);
				}
			}

			return null;

		} catch(DataServiceException e) {
			throw new ServiceException("Cannot deleteKlant: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService deleteKlantOfBedrijf(Integer bedrijfId, Integer gebruikerId) throws ServiceException {
		try {
			Klant klant = klantDataService.findKlantOfBedrijfByBedrijfId(bedrijfId);
			return deleteKlant(klant.getGebruikerId(), gebruikerId);
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot deleteKlantOfBedrijf: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService deleteOverdueKlanten() throws ServiceException {
		try {
			//Delete non activated klanten
			List<Klant> nonActivatedKlanten = klantDataService.findNonActivatedKlanten();
			if(nonActivatedKlanten != null) {
				deleteKlantenSet(nonActivatedKlanten);
			}

			//Delete overdue klanten
			List<Klant> klanten = klantDataService.findOverdueNewKlanten(newAccountOverdueDays);
			if(klanten != null) {
				deleteKlantenSet(klanten);
			}

			return null;

		} catch(DataServiceException e) {
			throw new ServiceException("Cannot deleteOverdueKlanten: " + e.getMessage());
		}
	}

	public void emailResult(String result) {
		LOGGER.info("Mule SendEmail result: " + result);
	}

	@Override
	public Bedrijf findBedrijf(String kvkNumber, String subDossier) throws ServiceException {
		Bedrijf result = null;

		if(kvkNumber != null) {
			Bedrijf bedrijf;
			try {
				bedrijf = bedrijfDataService.findByKvkNummer(kvkNumber, subDossier);
			} catch(DataServiceException e) {
				throw new ServiceException("Error finding bedrijf in database");
			}

			result = bedrijf;
		}

		return result;
	}

	@Override
	@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public boolean hasBedrijfAccount(String kvkNumber, String subDossier, boolean onlyActive) throws ServiceException {
		boolean result = false;

		if(kvkNumber != null) {
			Bedrijf bedrijf;
			try {
				bedrijf = bedrijfDataService.findByKvkNummer(kvkNumber, subDossier);

				if(bedrijf != null) {

					Klant klant = bedrijfDataService.findAnyKlantRecordOfBedrijf(bedrijf.getBedrijfId());

					if(!onlyActive && klant != null && klant.getKlantStatus() != null && (klant.getKlantStatusCode().equals(EKlantStatus.ACTIEF.getCode()) || klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode()) || klant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode()) || klant.getKlantStatusCode().equals(EKlantStatus.REGISTRATIE.getCode()))) {
						result = true;
					} else if(onlyActive && klant != null && klant.getKlantStatus() != null &&
							klant.getKlantStatusCode().equals(EKlantStatus.ACTIEF.getCode())) {
						result = true;
					}
				}
			} catch(DataServiceException e) {
				throw new ServiceException("Error finding bedrijf in database");
			}
		}

		return result;
	}

	@Override
	public boolean isRecaptchaOk(String ipaddress, String response, String challenge) throws ServiceException {
		boolean result = false;

		//	http://www.google.com/recaptcha/api/verify

		Map<String, String> vars = new HashMap<String, String>();
		vars.put("secret", recaptchaSecret);
		//vars.put("secret", "6LdvWvsSAAAAAMPuffWYblNPKlp1ozG5_I0Z2W31");
		////vars.put("secret", "6LdvWvsSAAAAAGgUzkE2pMq5U-0_Jj9UfMxcJAfg");
		if(ipaddress != null && !ipaddress.equals("0.0.0.0")) vars.put("remoteip", ipaddress);
		else vars.put("remoteip", "127.0.0.1");
		//recaptcha vars.put("challenge", challenge);
		vars.put("response", response);

		try {
			RestTemplate rest = new RestTemplate();

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			//recaptcha map.add("privatekey", vars.get("privatekey"));
			map.add("secret", vars.get("secret"));
			map.add("remoteip", vars.get("remoteip"));
			//recaptcha map.add("challenge", vars.get("challenge"));
			map.add("response", vars.get("response"));


			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);


			List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
			messageConverters.add(new MappingJackson2HttpMessageConverter());
			messageConverters.add(new FormHttpMessageConverter());
			messageConverters.add(new StringHttpMessageConverter());
			rest.setMessageConverters(messageConverters);

			// recaptcha String resultValue = rest.postForObject("http://www.google.com/recaptcha/api/verify", request, String.class);
			LOGGER.debug("isRecaptchaOk: " + ipaddress + " " + response);
			NoCaptchaResult resultCaptcha = rest.getForObject("https://www.google.com/recaptcha/api/siteverify?secret={secret}&response={response}&remoteip={remoteip}", NoCaptchaResult.class, vars);
			if(resultCaptcha != null && resultCaptcha.getErrorCodes() != null) {
				LOGGER.debug("recaptcha result: " + resultCaptcha.isSuccess());
				for(String err : resultCaptcha.getErrorCodes())
					LOGGER.error("recaptcha error: " + err);
			}

			if(resultCaptcha != null && resultCaptcha.isSuccess()) result = true;

			return result;

		} catch(Exception e) {
			LOGGER.error("isRecaptchaOk exception: " + e.getMessage(), e);
			throw new ServiceException("Cannot call Recaptcha validation");
		}

	}

	@Override
	@Transactional
	public ErrorService sendEmailReminderNewAccountKlanten() throws ServiceException {
		try {
			List<Klant> klanten = klantDataService.findReminderNewKlanten(newAccountReminderHours);

			if(klanten != null) {
				for(Klant klant : klanten) {
					if(!klant.isActivatieReminderSent()) {
						emailService.sendReminderActivationEmail(klant);

						klant.setActivatieReminderSent(true);
						klantDataService.save(klant);
					}
				}
			}

			return null;

		} catch(DataServiceException e) {
			throw new ServiceException("Cannot remind new Klanten: " + e.getMessage());
		}
	}

	public void setEmailResult(String result) {
		LOGGER.info("Mule SendEmail result: " + result);
	}

	@Override
	@Transactional
	public ErrorService updateAccountData(Klant klant, Bedrijf b) throws ServiceException {
		ErrorService result = null;

		if(klant != null && klant.getGebruikerId() != null) {
			try {
				Klant bestaandeKlant = klantDataService.findByGebruikerId(klant.getGebruikerId());

				bestaandeKlant.setTenaamstelling(klant.getTenaamstelling());
				bestaandeKlant.setBankrekeningNummer(klant.getBankrekeningNummer());
				bestaandeKlant.setBtwnummer(klant.getBtwnummer());
				bestaandeKlant.setNietBtwPlichtig(klant.isNietBtwPlichtig());
				bestaandeKlant.setTelefoonNummer(klant.getTelefoonNummer());
				bestaandeKlant.setAkkoordIncasso(klant.isAkkoordIncasso());
				bestaandeKlant.setEmailAdresFacturatie(klant.getEmailAdresFacturatie());

				klantDataService.save(bestaandeKlant);

				Bedrijf bestaandB = bedrijfDataService.findByBedrijfId(bestaandeKlant.getBedrijfBedrijfId());

				bestaandB.setTelefoonnummer(b.getTelefoonnummer());

				bedrijfDataService.save(bestaandB);

				return null;

			} catch(DataServiceException e) {
				throw new ServiceException("Cannot fetch klant data.");
			}
		} else return new ErrorService(ErrorService.NO_KLANTUPDATE_DATA);
	}

	@Override
	@Transactional
	public ErrorService updateBedrijfData(Bedrijf bedrijf) throws ServiceException {

		if(bedrijf != null) {
			try {
				Bedrijf bedrijfBestaand = null;

				if(bedrijf.getBedrijfId() != null)
					bedrijfBestaand = bedrijfDataService.findByBedrijfId(bedrijf.getBedrijfId());

				if(bedrijfBestaand != null) {

// ADRESS NOT OK -> For now do not overwrite Company data due issues when Company data is changed at source 					
					if(
//							!CompareUtil.equalsString(bedrijfBestaand.getBedrijfsNaam(), bedrijf.getBedrijfsNaam()) ||
//							!CompareUtil.equalsString(bedrijfBestaand.getStraat(), bedrijf.getStraat()) ||
//							!CompareUtil.equalsInteger(bedrijfBestaand.getHuisNr(), bedrijf.getHuisNr()) ||
//							!CompareUtil.equalsString(bedrijfBestaand.getHuisNrToevoeging(), bedrijf.getHuisNrToevoeging()) ||
//							!CompareUtil.equalsString(bedrijfBestaand.getPostcode(), bedrijf.getPostcode()) ||
//							!CompareUtil.equalsString(bedrijfBestaand.getPlaats(), bedrijf.getPlaats()) ||
//							!CompareUtil.equalsString(bedrijfBestaand.getPostPostbus(), bedrijf.getPostPostbus()) ||
//							!CompareUtil.equalsString(bedrijfBestaand.getPostPostcode(), bedrijf.getPostPostcode()) ||
//							!CompareUtil.equalsString(bedrijfBestaand.getPostPlaats(), bedrijf.getPostPlaats()) ||
							!CompareUtil.equalsString(bedrijfBestaand.getTelefoonnummer(), bedrijf.getTelefoonnummer()) // ||
//							bedrijfBestaand.isAdresOk() != bedrijf.isAdresOk()
							) {
//						bedrijfBestaand.setBedrijfsNaam(bedrijf.getBedrijfsNaam());
//						bedrijfBestaand.setStraat(bedrijf.getStraat());
//						bedrijfBestaand.setHuisNr(bedrijf.getHuisNr());
//						bedrijfBestaand.setHuisNrToevoeging(bedrijf.getHuisNrToevoeging());
//						bedrijfBestaand.setPostcode(bedrijf.getPostcode());
//						bedrijfBestaand.setPlaats(bedrijf.getPlaats());
//						bedrijfBestaand.setPostPostbus(bedrijf.getPostPostbus());
//						bedrijfBestaand.setPostPostcode(bedrijf.getPostPostcode());
//						bedrijfBestaand.setPostPlaats(bedrijf.getPostPlaats());
						bedrijfBestaand.setTelefoonnummer(bedrijf.getTelefoonnummer());
					}
//
//						bedrijfBestaand.setHandmatigGewijzigd(true);

						boolean updateAllNOK = false;
						if(!bedrijfBestaand.isAdresOk() && bedrijf.isAdresOk()) updateAllNOK = true;
						bedrijfBestaand.setAdresOk(bedrijf.isAdresOk());
						bedrijfBestaand.setDatumWijziging(new Date());
						bedrijfDataService.save(bedrijfBestaand);

						// update meldingen + create notification letter
						if(updateAllNOK) {
							updateAllAccountsNOKtoPRO(bedrijfBestaand.getBedrijfId());
							updateAllMeldingenNOKtoINI(bedrijfBestaand.getBedrijfId());
						}
//					 }
				} else throw new ServiceException("Cannot update bedrijf data.");

				return null;
			} catch(DataServiceException e) {
				throw new ServiceException("Cannot update bedrijf data.");
			}
		} else throw new ServiceException("Cannot update bedrijf data.");
	}

	@Override
	@Transactional
	public ErrorService updateKlantBedrijfAccountData(Klant klant, Bedrijf bedrijf, Integer gebruikerId) throws ServiceException {
		ErrorService result = null;

		if(klant != null && bedrijf != null) {
			try {
				Klant bestaandeKlant = null;

				if(klant.getGebruikerId() != null)
					bestaandeKlant = klantDataService.findByGebruikerId(klant.getGebruikerId());

				if(bestaandeKlant != null) {

					bestaandeKlant.setTenaamstelling(klant.getTenaamstelling());
					bestaandeKlant.setBankrekeningNummer(klant.getBankrekeningNummer());
					bestaandeKlant.setEmailAdresFacturatie(klant.getEmailAdresFacturatie());
					if (klant.getBtwnummer() != null)
						bestaandeKlant.setBtwnummer(klant.getBtwnummer());
					// only update isNietBtwPlichting on BTW number available or !Btw vink
					if (klant.getBtwnummer() != null || !klant.isNietBtwPlichtig())
						bestaandeKlant.setNietBtwPlichtig(klant.isNietBtwPlichtig());
					bestaandeKlant.setTelefoonNummer(klant.getTelefoonNummer());
					klantDataService.save(bestaandeKlant);
				} else throw new ServiceException("Cannot update klant/bedrijf data.");

				
				updateBedrijfData(bedrijf);

				return null;

			} catch(DataServiceException e) {
				throw new ServiceException("Cannot update klant/bedrijf data.");
			} catch(ServiceException e) {
				throw new ServiceException(e);
			}

		} else return new ErrorService(ErrorService.NO_KLANTUPDATE_DATA);
	}

	private String createNewSbdrNummer() {

		// create new number
		Long newSbdrNummer = SerialNumber.generateRandomSerialNumber9_10(true);
		int checkdigit = LuhnUtil.generateCheckDigit(newSbdrNummer);
		newSbdrNummer = Long.valueOf(String.valueOf(newSbdrNummer) + checkdigit);
		String newSbdrNummerstr = Long.toString(newSbdrNummer);

		try {
			while(bedrijfDataService.findBySbdrNummer(newSbdrNummerstr) != null) {
				// create new number
				newSbdrNummer = SerialNumber.generateRandomSerialNumber9_10(true);
				checkdigit = LuhnUtil.generateCheckDigit(newSbdrNummer);
				newSbdrNummer = Long.valueOf(String.valueOf(newSbdrNummer) + checkdigit);
				newSbdrNummerstr = Long.toString(newSbdrNummer);
			}
		} catch(Exception e) {
			throw new ServiceException("Cannot create SbdrNummer.");
		}

		return newSbdrNummerstr;
	}

	private void deleteAllMeldingenOfKlant(Integer bedrijfId) throws ServiceException {
		try {
			List<Melding> meldingen = meldingDataService.findAllMeldingenOfBedrijf(bedrijfId);
			List<Gebruiker> systeemgebruikers = gebruikerDataService.findGebruikersByRolCode(ERol.SYSTEEM.getCode());
			Gebruiker systeemgebruiker = null;

			if(systeemgebruikers.size() == 1) systeemgebruiker = systeemgebruikers.get(0);


			if(meldingen != null) {
				for(Melding melding : meldingen) {
					melding.setGebruikerByVerwijderdDoorGebruikerIdGebruikerId(systeemgebruiker.getGebruikerId());
					melding.setMeldingStatusCode(EMeldingStatus.VERWIJDERD.getCode());
					melding.setRedenVerwijderenMeldingCode(ERedenVerwijderenMelding.ADMINSBDR.getCode());
					melding.setDatumVerwijderd(new Date());

					meldingDataService.save(melding);
				}
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Transactional
	private void deleteKlantenSet(List<Klant> klanten) {
		try {
			for(Klant klant : klanten) {
				Bedrijf bedrijf = klant.getBedrijf();

				deleteAllMeldingenOfKlant(bedrijf.getBedrijfId());
				
				//Delete customer letter, if it exists
				for(InternalProcess iP : klant.getInternalProcesses()) {
					//internalProcessService.removeInternalProcessRow(iP.getInternalProcessId());
					klant.getInternalProcesses().remove(iP);
					internalProcessDataService.delete(iP);
				}

				//Remove all monitors
				bedrijfService.removeMonitorsOfBedrijf(bedrijf.getBedrijfId());

				//Set all relevant document as inactive
				documentService.removeAllDocumentsOfBedrijf(bedrijf.getBedrijfId());

				//Remove all MCKs of all users
				mobileGebruikerService.removeAllMobileGebruikerRecordsByBedrijfId(bedrijf.getBedrijfId());

				//Set all users of klant on inactive
				deleteUsersOfKlant(klant.getGebruikerId(), bedrijf);

				//Delete klant gebruiker
				gebruikerService.deleteGebruiker(klant.getGebruikerId(), bedrijf.getBedrijfId());

				//Archive all support tickets
				supportService.archiveAllSupportTicketsOfBedrijf(bedrijf.getBedrijfId());

				//Remove any and all alerts meant for the company
				alertService.deleteAlertsByBedrijfId(bedrijf.getBedrijfId());
				
				klant.setKlantStatusCode(EKlantStatus.VERVALLEN.getCode());				

				klant.setActief(false);

				klantDataService.save(klant);

				if(bedrijf != null) {
					bedrijf.setBedrijfStatusCode(EBedrijfStatus.INACTIEF.getCode());
					bedrijf.setActief(false);
					bedrijf.setDatumWijziging(new Date());
					bedrijfDataService.save(bedrijf);
				}

			}
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot delete Klant set: " + e.getMessage());
		}
	}

	private void deleteUsersOfKlant(Integer klantGebruikerId, Bedrijf bedrijf) throws ServiceException {
		try {
			Page<Gebruiker> page = gebruikerDataService.findActiveGebruikersOfKlantGebruiker(klantGebruikerId, new PageRequest(0, 10000));

			if(page != null) {
				List<Gebruiker> gebruikers = page.getContent();

				for(Gebruiker gebruiker : gebruikers) {

					if(!gebruiker.getGebruikerId().equals(klantGebruikerId)) {
						try {
							if (gebruiker.getBedrijf() != null && gebruiker.getBedrijf().getBedrijfId().equals(bedrijf.getBedrijfId()))
								gebruikerService.deleteGebruiker(gebruiker.getGebruikerId(), bedrijf.getBedrijfId());
							else if (gebruiker.getBedrijvenManagedDoorGebruikerId() != null) {
								for (BedrijfManaged bedrijfManaged : gebruiker.getBedrijvenManagedDoorGebruikerId())
									if (bedrijfManaged.getBedrijfBedrijfId().equals(bedrijf.getBedrijfId())) {
										gebruikerService.deleteGebruiker(gebruiker.getGebruikerId(), bedrijf.getBedrijfId());	
									}
							}
						} catch(DataServiceException e) {
							// gebruiker already connected to monitoring/notification/...
							gebruiker.setActief(false);
							gebruikerDataService.save(gebruiker);
						}
					}

				}
			}
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot delete users of customer");
		}
	}

	private String generateActivatieCodeBrief() throws ServiceException {
		// ActivatieCode
		String activatieCode = null;
		boolean generateNewCode = true;
		int iteration = 0;
		while(generateNewCode) {
			iteration++;
			activatieCode = SerialNumber.generateRandomSerialNumber6_32();
			Klant bestaandeklant = null;
			try {
				bestaandeklant = klantDataService.findByKlantActivatieCode(activatieCode);
			} catch(DataServiceException e) {
				LOGGER.error("Error in generateActivatieCodeBrief: " + e.getMessage() );
				throw new ServiceException("Cannot generate activation code for bestaandeKlant.");
			}

			if(bestaandeklant == null) generateNewCode = false;

			if(iteration > 20) throw new ServiceException("Cannot generate activation code");
		}

		return activatieCode;
	}

	private String generateActivatieCodeEmail() throws ServiceException {
		// ActivatieCode
		String activatieCode = null;
		boolean generateNewCode = true;
		int iteration = 0;
		while(generateNewCode) {
			iteration++;
			activatieCode = SerialNumber.generateRandomSerialNumber16_32();
			Klant bestaandeklant;
			try {
				bestaandeklant = klantDataService.findByKlantActivatieCode(activatieCode);
			} catch(DataServiceException e) {
				LOGGER.error("Cannot find klant with activation code: " + activatieCode);
				throw new ServiceException("Cannot find klant with activation code: " + activatieCode);
			}

			if(bestaandeklant == null) generateNewCode = false;

			if(iteration > 20) throw new ServiceException("Cannot generate activation code");
		}

		return activatieCode;
	}

	private void updateAllAccountsNOKtoPRO(Integer bedrijfId) throws DataServiceException {
		try {
			Collection<String> klantCodes = new ArrayList<>();
			klantCodes.add(EKlantStatus.DATA_NOK.getCode());

			Klant bestaandeklant = klantDataService.findKlantOfBedrijfByBedrijfIdAndStatusCode(bedrijfId, klantCodes);

			// if there is a klant record of bedrijf generate letter + update it!
			if(bestaandeklant != null) {
				if(bestaandeklant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode())) {
					// If klant has not yet activated himself by email link set status to registered
					if (bestaandeklant.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL) ){
						bestaandeklant.setKlantStatusCode(EKlantStatus.REGISTRATIE.getCode());
					} else if (bestaandeklant.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE) ||
							bestaandeklant.getWachtwoord().getWachtwoordStatusCode().equals(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_RESET)){
						bestaandeklant.setKlantStatusCode(EKlantStatus.PROSPECT.getCode());
						bestaandeklant.setActivatieCode(generateActivatieCodeBrief());
					}
					
					Klant nieuweklant = klantDataService.save(bestaandeklant);

					// not yet for DATA_NOK accounts...
					if(bestaandeklant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode()))
						documentService.createNewAccountLetterPdf(nieuweklant.getGebruikerId());
				}
			}

		} catch(DataServiceException e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	private void updateAllMeldingenNOKtoINI(Integer meldingOverBedrijfId) throws DataServiceException {
		try {
			List<String> nokStatus = new ArrayList<String>();
			nokStatus.add(EMeldingStatus.DATA_NOK.getCode());

			List<Melding> meldingen = bedrijfDataService.findMeldingenOfBedrijf(meldingOverBedrijfId, nokStatus);

			for(Melding melding : meldingen) {

				// update DATA_NOK status to ini and create letter.
				if(melding.getMeldingStatusCode().equals(EMeldingStatus.DATA_NOK.getCode())) {
					Date now = new Date();
					melding.setDatumIngediend(now);
					melding.setMeldingStatusCode(EMeldingStatus.INBEHANDELING.getCode());
					melding.setDatumLaatsteMutatie(now);

					meldingDataService.save(melding);
				}

			}
		} catch(DataServiceException e) {
			throw new DataServiceException(e.getMessage());
		}
	}
}