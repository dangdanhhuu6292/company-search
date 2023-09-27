package nl.devoorkant.sbdr.business.service;


import nl.devoorkant.exactonline.business.transfer.TokenRequest;
import nl.devoorkant.exactonline.business.transfer.ViesCheckRequest;
import nl.devoorkant.sbdr.business.job.ActivateOverdueMeldingenJob;
import nl.devoorkant.sbdr.business.job.AssignExpiredObjectionsToAdminJob;
import nl.devoorkant.sbdr.business.job.CreateOldNotificationAlertsJob;
import nl.devoorkant.sbdr.business.job.DeleteOverdueKlantenJob;
import nl.devoorkant.sbdr.business.transfer.*;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.business.util.*;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.EAlertType;
import nl.devoorkant.sbdr.data.util.*;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationObject;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import org.joda.time.Days;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.ws.rs.Produces;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Stateless service bean with functionality for Bedrijf.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

@Service("bedrijfService")
@Transactional(readOnly = true)
public class BedrijfServiceImpl implements BedrijfService {
	@Autowired
	Scheduler scheduler;
	
	@Value("${job.cron.activate_overdue_meldingen}")
	String cronExpressionDeleteOverdueKlanten;
	
	@Value("${job.cron.create_old_notification_alerts}")
	String cronExpressionCreateOldNotificationAlerts;
	
	@Value("${job.cron.activate_overdue_meldingen}")
	String cronExpressionActivateOverdueMeldingen;
	
	@Value("${sbdrweb_mule_uri}")
	String sbdrmuleBaseUri;
	
	@Autowired
	private AlertService alertService;

	@Autowired
	private BedrijfDataService bedrijfDataService;

	@Autowired
	private CompanyInfoDataService companyInfoDataService;

	@Autowired
	private CompanyInfoService companyInfoService;

	@Autowired
	private ConfiguratieDataService configuratieDataService;

	@Autowired
	private CustomMeldingDataService customMeldingDataService;

	@Autowired
	private DocumentDataService documentDataService;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private GebruikerDataService gebruikerDataService;

	@Autowired
	private GebruikerService gebruikerService;

	@Autowired
	private InternalProcessService internalProcessService;

	@Autowired
	private KlantDataService klantDataService;

	@Autowired
	private MeldingBatchDataService meldingBatchDataService;

	@Autowired
	private MeldingDataService meldingDataService;
	@Autowired
	private MeldingService meldingService;
	@Autowired
	private MeldingStatusDataService meldingStatusDataService;
	@Autowired
	private MonitoringDataService monitoringDataService;
	
	@Autowired
	private NotitieDataService notitieDataService;

	@Value("${overduedays_newmelding}")
	private int newMeldingOverdueDays;

	@Autowired
	private ProductService productService;

	@Autowired
	private SupportDataService supportDataService;
	
	@Autowired
	private ContactMomentDataService contactMomentDataService;

	@Autowired
	private WebTokenService webTokenService;
	@Autowired
	private WebsiteparamDataService websiteparamDataService;
	private static final Logger LOGGER = LoggerFactory.getLogger(BedrijfServiceImpl.class);

	@PostConstruct
	public void createSchedule() {        
        JobDetail jobDetail = buildJobDetailCreateOldNotificationAlerts();
        Trigger trigger = buildJobTriggerCreateOldNotificationAlerts(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start bedrijf schedule CreateOldNotificationAlerts", e);
		}        
        
        jobDetail = buildJobDetailDeleteOverdueKlanten();
        trigger = buildJobTriggerDeleteOverdueKlanten(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start bedrijf schedule DeleteOverdueKlanten", e);
		}      
        
        jobDetail = buildJobDetailActivateOverdueMeldingen();
        trigger = buildJobTriggerActivateOverdueMeldingen(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start bedrijf schedule ActivateOverdueMeldingen", e);
		}          
	}
	
	private JobDetail buildJobDetailCreateOldNotificationAlerts() {

        return JobBuilder.newJob(CreateOldNotificationAlertsJob.class)
                .withIdentity(UUID.randomUUID().toString(), "bedrijf-jobs")
                .withDescription("Assign expired objections to admin")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTriggerCreateOldNotificationAlerts(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "bedrijf")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionCreateOldNotificationAlerts))
    			  .build();    	
    }    
    
	private JobDetail buildJobDetailDeleteOverdueKlanten() {

        return JobBuilder.newJob(DeleteOverdueKlantenJob.class)
                .withIdentity(UUID.randomUUID().toString(), "bedrijf-jobs")
                .withDescription("Delete overdue klanten")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTriggerDeleteOverdueKlanten(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "bedrijf")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionDeleteOverdueKlanten))
    			  .build();    	
    }       

	private JobDetail buildJobDetailActivateOverdueMeldingen() {

        return JobBuilder.newJob(ActivateOverdueMeldingenJob.class)
                .withIdentity(UUID.randomUUID().toString(), "bedrijf-jobs")
                .withDescription("Delete overdue klanten")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTriggerActivateOverdueMeldingen(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "bedrijf")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionActivateOverdueMeldingen))
    			  .build();    	
    }        
	@Override
	@Transactional
	public ErrorService activateMelding(Integer meldingId, Integer gebruikerId) throws ServiceException {
		try {
			Melding melding = meldingDataService.findById(meldingId);

			if(melding != null && !melding.getMeldingStatusCode().equals(EMeldingStatus.ACTIEF.getCode())) {
				if(gebruikerId != null) melding.setGebruikerByGeaccordeerdDoorGebruikerIdGebruikerId(gebruikerId);
				melding.setMeldingStatusCode(EMeldingStatus.ACTIEF.getCode());
				melding.setDatumGeaccordeerd(new Date());
				Melding newMelding = meldingDataService.save(melding);

				// create alert
				alertService.createMeldingAlert(newMelding);

				//send monitoring update
				emailService.sendMonitoringChangedEmails(melding.getBedrijfByMeldingOverBedrijfId());

				return null;

			} else return new ErrorService(ErrorService.CANNOT_REMOVE_MELDING);
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot create notification: " + e.getMessage());
		}
	}


	@Override
	@Transactional
	public ErrorService activateOverdueMeldingen() throws ServiceException {
		try {
			List<Melding> meldingen = meldingDataService.findOverdueNewMeldingen(newMeldingOverdueDays);
			List<Gebruiker> systeemgebruikers = gebruikerDataService.findGebruikersByRolCode(ERol.SYSTEEM.getCode());
			List<Bedrijf> emailHasBeenSentToBedrijfOver = new ArrayList<>();
			HashMap<Bedrijf, HashMap<Bedrijf, List<Melding>>> emailList = new HashMap<>();
			Gebruiker systeemgebruiker = null;

			if(systeemgebruikers.size() == 1) systeemgebruiker = systeemgebruikers.get(0);

			if(meldingen != null) {
				for(Melding melding : meldingen) {
					if(!meldingService.meldingHasObjection(melding.getMeldingId())) {
						melding.setMeldingStatusCode(EMeldingStatus.ACTIEF.getCode());
						melding.setDatumGeaccordeerd(new Date());
						if(systeemgebruiker != null)
							melding.setGebruikerByGeaccordeerdDoorGebruikerId(systeemgebruiker);
						Melding newMelding = meldingDataService.save(melding);

						// create alert
						alertService.createMeldingAlert(newMelding);

						Bedrijf bedrijfOver = bedrijfDataService.findByBedrijfId(newMelding.getBedrijfByMeldingOverBedrijfIdBedrijfId());
						Bedrijf bedrijfDoor = bedrijfDataService.findByBedrijfId(newMelding.getBedrijfByMeldingDoorBedrijfIdBedrijfId());

						//Send email if an email hasn't been send to bedrijfOver yet
						boolean bedrijfOverEmailSent = false;
						for(Bedrijf emailIsSentAboutBedrijfOver : emailHasBeenSentToBedrijfOver) {
							if(emailIsSentAboutBedrijfOver.getBedrijfId().equals(bedrijfOver.getBedrijfId())) {
								bedrijfOverEmailSent = true;
								break;
							}
						}

						if(!bedrijfOverEmailSent) {
							emailService.sendMonitoringChangedEmails(bedrijfOver);
							emailHasBeenSentToBedrijfOver.add(bedrijfOver);
						}

						boolean bedrijfDoorExistsInList = false;

						for(Bedrijf bedrijfDoorEntry : emailList.keySet()) {
							if(bedrijfDoorEntry.getBedrijfId().equals(bedrijfDoor.getBedrijfId())) {
								bedrijfDoorExistsInList = true;
								break;
							}
						}

						if(!bedrijfDoorExistsInList) {
							HashMap<Bedrijf, List<Melding>> hashMapValue = new HashMap<>();
							List<Melding> meldingenList = new ArrayList<>();

							meldingenList.add(melding);
							hashMapValue.put(bedrijfOver, meldingenList);

							emailList.put(bedrijfDoor, hashMapValue);
							//hashMapValue.put(bedrijfOver, new ArrayList<Melding>();
						} else {
							boolean bedrijfOverExistsInList = false;

							for(Bedrijf bedrijfOverEntry : emailList.get(bedrijfDoor).keySet()) {
								if(bedrijfOverEntry.getBedrijfId().equals(bedrijfOver.getBedrijfId())) {
									bedrijfOverExistsInList = true;
									break;
								}
							}

							if(!bedrijfOverExistsInList) {
								List<Melding> meldingenList = new ArrayList<>();
								meldingenList.add(melding);

								emailList.get(bedrijfDoor).put(bedrijfOver, meldingenList);
							} else {
								emailList.get(bedrijfDoor).get(bedrijfOver).add(melding);
							}
						}
					}
				}

				for(Bedrijf bedrijf : emailList.keySet()) {
					Klant klant = klantDataService.findKlantOfBedrijfByBedrijfId(bedrijf.getBedrijfId());

					if(klant == null) {
						throw new ServiceException("Niet in staat klant van bedrijf " + bedrijf.getBedrijfsNaam() + " op te halen");
					}

					emailService.sendNotificationIsActiveEmail(klant, emailList.get(bedrijf));
				}
			}

			return null;
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot activateOverdueMeldingen: " + e.getMessage());
		}
	}

	@Override
	public List<BedrijfTransfer> addBekendBijSbdrProperty(List<BedrijfTransfer> bedrijfTransfers) throws ServiceException {
		if(bedrijfTransfers != null) {
			try {
				List<String> kvkNumbers = new ArrayList<String>();
				for(BedrijfTransfer bedrijf : bedrijfTransfers) {
					if(bedrijf.getKvkNummer() != null) {
						kvkNumbers.add(bedrijf.getKvkNummer());
					}
				}

				if(kvkNumbers.size() > 0) {
					List<Bedrijf> kvkbedrijven = bedrijfDataService.findByKvkNummers(kvkNumbers);

					for(BedrijfTransfer bedrijfTransfer : bedrijfTransfers) {
						for(Bedrijf kvkbedrijf : kvkbedrijven)
							if(bedrijfTransfer.getKvkNummer() != null && bedrijfTransfer.getKvkNummer().equals(kvkbedrijf.getKvKnummer())) {
								if((bedrijfTransfer.getSubDossier() == null && kvkbedrijf.getSubDossier() == null) || (bedrijfTransfer.getSubDossier() != null && bedrijfTransfer.getSubDossier().equals(kvkbedrijf.getSubDossier()))) {
									bedrijfTransfer.setBekendBijSbdr(true);

									String referentieIntern = null;
									if(kvkbedrijf.getSbdrNummer() != null)
										referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + kvkbedrijf.getSbdrNummer();
									bedrijfTransfer.setSbdrNummer(referentieIntern);

									if(bedrijfTransfer.getBedrijfId() == null)
										bedrijfTransfer.setBedrijfId(kvkbedrijf.getBedrijfId());
									break;
								}
							}
					}
				}

			} catch(DataServiceException e) {
				throw new ServiceException("Cannot enrich BedrijfTransfer with properties");
			}
		}

		return bedrijfTransfers;
	}

	@Override
	public List<BedrijfTransfer> addHeeftMeldingProperty(Integer bedrijfId, List<BedrijfTransfer> bedrijfTransfers) throws ServiceException {
		if(bedrijfTransfers != null) {
			try {
				List<Integer> bedrijvenIds = new ArrayList<Integer>();
				for(BedrijfTransfer bedrijf : bedrijfTransfers) {
					if(bedrijf.getBedrijfId() != null) bedrijvenIds.add(bedrijf.getBedrijfId());
				}

				if(bedrijvenIds.size() > 0) {
					List<Bedrijf> meldingbedrijven = bedrijfDataService.findMatchingBedrijvenMetMeldingByBedrijfFromBedrijvenList(bedrijfId, bedrijvenIds);

					for(BedrijfTransfer bedrijfTransfer : bedrijfTransfers) {
						for(Bedrijf meldingbedrijf : meldingbedrijven)
							if(bedrijfTransfer.getBedrijfId() != null && bedrijfTransfer.getBedrijfId().equals(meldingbedrijf.getBedrijfId())) {
								bedrijfTransfer.setMeldingBijBedrijf(true);
								break;
							}
					}
				}
			} catch(DataServiceException e) {
				throw new ServiceException("Cannot enrich BedrijfTransfer with properties");
			}
		}

		return bedrijfTransfers;
	}

	@Override
	public List<BedrijfTransfer> addHeeftMonitoringProperty(Integer bedrijfId, List<BedrijfTransfer> bedrijfTransfers) throws ServiceException {
		if(bedrijfTransfers != null) {
			try {
				List<Integer> bedrijvenIds = new ArrayList<Integer>();
				for(BedrijfTransfer bedrijf : bedrijfTransfers) {
					if(bedrijf.getBedrijfId() != null) bedrijvenIds.add(bedrijf.getBedrijfId());
				}

				if(bedrijvenIds.size() > 0) {
					List<Bedrijf> meldingbedrijven = bedrijfDataService.findMatchingBedrijvenMetMonitoringOfBedrijfFromBedrijvenList(bedrijfId, bedrijvenIds);

					for(BedrijfTransfer bedrijfTransfer : bedrijfTransfers) {
						for(Bedrijf meldingbedrijf : meldingbedrijven)
							if(bedrijfTransfer.getBedrijfId() != null && bedrijfTransfer.getBedrijfId().equals(meldingbedrijf.getBedrijfId())) {
								bedrijfTransfer.setMonitoringBijBedrijf(true);
								break;
							}
					}
				}
			} catch(DataServiceException e) {
				throw new ServiceException("Cannot enrich BedrijfTransfer with properties");
			}
		}

		return bedrijfTransfers;
	}

	@Override
	public List<ExceptionBedrijfOverviewTransfer> addKlantProperty(List<ExceptionBedrijfOverviewTransfer> exceptionCompanies) throws ServiceException {
		if(exceptionCompanies != null) {
			try {
				List<Integer> bedrijvenIds = new ArrayList<Integer>();
				for(ExceptionBedrijfOverviewTransfer bedrijf : exceptionCompanies) {
					if(bedrijf.getBedrijfId() != null) {
						bedrijvenIds.add(bedrijf.getBedrijfId());
					}
				}

				if(bedrijvenIds.size() > 0) {
					List<Klant> klantbedrijven = bedrijfDataService.findKlantOfBedrijfFromBedrijvenList(bedrijvenIds);

					for(ExceptionBedrijfOverviewTransfer bedrijf : exceptionCompanies) {
						for(Klant klantbedrijf : klantbedrijven) {
							if(klantbedrijf.getBedrijfBedrijfId().equals(bedrijf.getBedrijfId())) {
								if(klantbedrijf.getActief() && EKlantStatus.isAllowedToLogin(klantbedrijf.getKlantStatusCode())) {
									bedrijf.setKlant(true);
									if(klantbedrijf.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode()))
										bedrijf.setProspect(true);
									if(bedrijf.getGebruiker() != null && bedrijf.getTelefoonnummer() != null)
										bedrijf.getGebruiker().setBedrijfTelefoonNummer(bedrijf.getTelefoonnummer());
								}
							}
						}
					}
				}

			} catch(DataServiceException e) {
				throw new ServiceException("Cannot enrich BedrijfTransfer with properties");
			}
		}

		return exceptionCompanies;
	}

	@Override
	public List<BedrijfTransfer> addRapportTodayCreated(Integer bedrijfId, List<BedrijfTransfer> bedrijfTransfers) throws ServiceException {
		if(bedrijfTransfers != null) {
			try {
				List<Integer> bedrijvenIds = new ArrayList<Integer>();
				for(BedrijfTransfer bedrijf : bedrijfTransfers) {
					if(bedrijf.getBedrijfId() != null) bedrijvenIds.add(bedrijf.getBedrijfId());
				}

				if(bedrijvenIds.size() > 0) {
					List<Bedrijf> rapportbedrijven = bedrijfDataService.findMatchingBedrijvenMetRapportCreatedTodayFromBedrijvenList(bedrijfId, bedrijvenIds);

					for(BedrijfTransfer bedrijfTransfer : bedrijfTransfers) {
						for(Bedrijf rapportbedrijf : rapportbedrijven) {
							if(bedrijfTransfer.getBedrijfId() != null && bedrijfTransfer.getBedrijfId().equals(rapportbedrijf.getBedrijfId())) {
								bedrijfTransfer.setRapportCreatedToday(true);
								break;
							}
						}
					}
				}
			} catch(DataServiceException e) {
				throw new ServiceException("Cannot enrich BedrijfTransfer with properties");
			}
		}

		return bedrijfTransfers;
	}

	@Override
	public List<BedrijfTransferNs> addSbdrNummerProperty(List<BedrijfTransferNs> bedrijfTransfers) throws ServiceException {
		if(bedrijfTransfers != null) {
			try {
				List<Integer> bedrijvenIds = new ArrayList<Integer>();
				for(BedrijfTransferNs bedrijf : bedrijfTransfers) {
					if(bedrijf.getBedrijfId() != null) bedrijvenIds.add(bedrijf.getBedrijfId());
				}

				if(bedrijvenIds.size() > 0) {
					List<Bedrijf> bedrijven = bedrijfDataService.findBestaandeBedrijvenFromBedrijvenList(bedrijvenIds);

					for(BedrijfTransferNs bedrijfTransfer : bedrijfTransfers) {
						for(Bedrijf bedrijf : bedrijven) {
							if(bedrijfTransfer.getBedrijfId() != null && bedrijfTransfer.getBedrijfId().equals(bedrijf.getBedrijfId())) {
								String referentieIntern = null;
								if(bedrijf.getSbdrNummer() != null)
									referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();
								bedrijfTransfer.setSbdrNummer(referentieIntern);

								break;
							}
						}
					}
				}
			} catch(DataServiceException e) {
				throw new ServiceException("Cannot enrich BedrijfTransfer with properties");
			}
		}

		return bedrijfTransfers;
	}

	@Override
	public AdminOverviewTransfer adminOverviewTransfer() throws ServiceException {
		AdminOverviewTransfer result = new AdminOverviewTransfer();

		try {
			result.setKlantenPerStatus(bedrijfDataService.findAantalKlantenPerActiveKlantStatus());
			result.setMeldingenPerStatus(bedrijfDataService.findAantalMeldingenPerStatus());
			result.setMonitoringPerStatus(bedrijfDataService.findAantalMonitoringPerStatus());

			result.setActiveKlantenLast24h(bedrijfDataService.findAllActiveKlantenLast24h());
			result.setActiveMeldingenLast24h(bedrijfDataService.findAllActiveMeldingenLast24h());
			result.setActiveMonitoringLast24h(bedrijfDataService.findAllActiveMonitoringLast24h());

			result.setTotaalAantalRapporten(bedrijfDataService.findAantalRapporten());
			result.setRapportenLast24h(bedrijfDataService.findAllRapportenLast24h());

			result.setTotaalAantalNieuweMonitoring(bedrijfDataService.findAantalNieuweMonitoring()); // 2 dagen;
			result.setTotaalAantalNieuweRapporten(bedrijfDataService.findAantalNieuweRapporten()); // 2 dagen;

			result.setNrOfGebruikersIngelogd(webTokenService.getNrOfValidTokens());
			result.setTotaalAantalGebruikers(gebruikerDataService.getCountAllUsers());
			result.setTotaalAantalActivatedGebruikers(gebruikerDataService.getCountActivatedUsers());
			result.setTotaalAantalDeactivatedGebruikers(gebruikerDataService.getCountDeactivatedUsers());
			result.setTotaalAantalNonActivatedGebruikers(gebruikerDataService.getCountNonActivatedUsers());

		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}

		return result;
	}

	@Override
	@Transactional
	public List<Melding> findAllActiveMeldingenOfBedrijf(Integer bId) throws ServiceException {
		if(bId != null) {
			List<String> meldingCodes = new ArrayList<String>();
			meldingCodes.add(EMeldingStatus.ACTIEF.getCode());
			return bedrijfDataService.findMeldingenOfBedrijf(bId, meldingCodes);
		} else {
			LOGGER.error("Method bedrijfHasAchterstand: parameter bId(Integer) is null");
			throw new ServiceException("Method bedrijfHasAchterstand: parameter bId(Integer) is null");
		}
	}

	@Override
	public boolean bedrijfHasMeldingenByGebruikerId(Integer gId) throws ServiceException {
		if(gId != null) {
			try {
				List<Melding> ml = meldingDataService.findBygebruikerIdOfCompany(gId);

				return ml.size() > 0;
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else {
			LOGGER.error("Method bedrijfHasMeldingenByGebruikerId: parameter gId(Integer) is null");
			throw new ServiceException("Method bedrijfHasMeldingenByGebruikerId: parameter gId(Integer) is null");
		}
	}

	@Override
	@Transactional
	public void blokkeerMelding(Integer meldingId, Integer gebruikerId, boolean onHold) throws ServiceException {
		Melding newMelding = null;

		try {
			if(meldingId != null) {
				Melding melding = meldingDataService.findById(meldingId);

				if(melding != null) {
					if(onHold) {
						if(melding.getMeldingStatusCode().equals(EMeldingStatus.INBEHANDELING.getCode())) {
							if(gebruikerId != null)
								melding.setGebruikerByGeparkeerdDoorGebruikerIdGebruikerId(gebruikerId);

							melding.setMeldingStatusCode(EMeldingStatus.GEBLOKKEERD.getCode());
							melding.setDatumGeparkeerd(new Date());
							newMelding = meldingDataService.save(melding);
						}
					} else {
						if(melding.getMeldingStatusCode().equals(EMeldingStatus.GEBLOKKEERD.getCode())) {
							if(gebruikerId != null) melding.setGebruikerByGeparkeerdDoorGebruikerId(null);

							melding.setMeldingStatusCode(EMeldingStatus.INBEHANDELING.getCode());
							melding.setDatumGeparkeerd(null);
							newMelding = meldingDataService.save(melding);
						}
					}
				} else throw new ServiceException("No notification found to set/reset on hold");
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public ErrorService createCustomMelding(Integer gebruikerId, CustomMeldingTransfer customMeldingTransfer) throws ServiceException {
		int resultnr = 0;
		ErrorService result = null;

		try {
			// if all notifications are ok, add them
			if(result == null) {

				CustomMelding customMelding = new CustomMelding();

				customMelding.setBedrijfBedrijfId(customMeldingTransfer.getBedrijfId());
				customMelding.setDatumAangemaakt(new Date());
				customMelding.setGebruikerGebruikerId(gebruikerId);
				customMelding.setSignAchternaam(customMeldingTransfer.getAchternaam());
				customMelding.setSignAfdeling(customMeldingTransfer.getAfdeling());
				customMelding.setSignTelefoonNummer(customMeldingTransfer.getTelefoonnummer());
				customMelding.setSignVoornaam(customMeldingTransfer.getVoornaam());

				customMelding.setFrauduleusBedrijf(false);
				customMelding.setDreigendFaillissement(false);
				customMelding.setIncorrectGegeven(customMeldingTransfer.isIncorrectGegeven());
				customMelding.setFaillissementVraag(customMeldingTransfer.isFaillissementVraag());

				customMelding.setMeldingDetails(customMeldingTransfer.getMeldingDetails());

				customMelding.setCustomMeldingStatusCode(ECustomMeldingStatus.INBEHANDELING.getCode());
				customMelding = customMeldingDataService.save(customMelding);


			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}

		return result;
	}

	@Override
	@Transactional
	public void createInitialMeldingenLetter(Integer meldingId, Integer gebruikerId) throws ServiceException {
		try {
			List<Melding> meldingen = meldingDataService.findMeldingenBetweenCompaniesByMeldingId(meldingId);
			Melding melding = meldingDataService.findById(meldingId);

			ContactMomentTransfer contactMoment = new ContactMomentTransfer();
			contactMoment.setBeantwoord("Nvt");
			contactMoment.setContactWijze("Vermeldingsbrief");
			contactMoment.setDatumContact(new Date());
			contactMoment.setGebruikerId(gebruikerId);
			contactMoment.setMeldingId(melding.getMeldingId());
			Notitie notitie = new Notitie();
			notitie.setDatum(new Date());
			notitie.setGebruikerGebruikerId(gebruikerId);
			notitie.setNotitie("Vermeldingsbrief is verstuurd.");
			notitie.setNotitieType(ENotitieType.CONTACT_MOMENT.getCode());
			saveContactMomentNotification(contactMoment, notitie);
			
			documentService.createMeldingLetter(melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId(), melding.getBedrijfByMeldingOverBedrijfIdBedrijfId(), meldingen, new Date());
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public ErrorService createMeldingBatch(Integer gebruikerId, NotificationsBatchTransfer notificationsBatch) throws ServiceException {
		int resultnr = 0;
		ErrorService result = null;
		
		// validate all notifications
		for(MeldingTransfer companyNotified : notificationsBatch.getMeldingen()) {
			result = validateMeldingData(companyNotified);

			if(result != null) break;
		}

		try {
			// if all notifications are ok, add them
			if(result == null) {
				MeldingBatch meldingBatch = new MeldingBatch();
				meldingBatch.setDatumAangemaakt(new Date());
				meldingBatch.setGebruikerGebruikerId(gebruikerId);
				meldingBatch.setSignAchternaam(notificationsBatch.getAchternaam());
				meldingBatch.setSignAfdeling(notificationsBatch.getAfdeling());
				meldingBatch.setSignTelefoonNummer(notificationsBatch.getTelefoonnummer());
				meldingBatch.setSignVoornaam(notificationsBatch.getVoornaam());

				List<Melding> meldingen = new ArrayList<>();
				meldingBatch = meldingBatchDataService.save(meldingBatch);

				ErrorService error = null;
				
				Date datumAangemaakt = new Date();

				for(MeldingTransfer companyNotified2 : notificationsBatch.getMeldingen()) {
					Melding melding = new Melding();

					//melding.setMeldingId(companyNotified.getMeldingId());
					melding.setMeldingId(null);
					melding.setGebruikerByMeldingDoorGebruikerIdGebruikerId(companyNotified2.getGebruikerAangemaaktId());
					try {
						if(companyNotified2.getBedrag() != null)
							melding.setBedrag(new BigDecimal(companyNotified2.getBedrag().doubleValue())); // MBR 15-05 precision???
					} catch(Exception e) {
						// do nothing
					}
					melding.setDatumVerloopFactuur(companyNotified2.getDatumFactuur());
					melding.setReferentieNummer(companyNotified2.getReferentie());
					melding.setBedrijfByMeldingDoorBedrijfIdBedrijfId(companyNotified2.getBedrijfIdGerapporteerd());
					melding.setBedrijfByMeldingOverBedrijfIdBedrijfId(companyNotified2.getBedrijfId());

					// Changed to defaults
					//melding.setBedragWeergeven(companyNotified2.isBedragWeergeven());
					//melding.setDoorBedrijfWeergeven(companyNotified2.isDoorBedrijfWeergeven());
					melding.setBedragWeergeven(true);
					melding.setDoorBedrijfWeergeven(false);
					
					melding.setBriefStatusCode(EBriefStatus.NIET_VERWERKT.getCode());
					//melding.setBriefGedownload(false);

					if(companyNotified2.getMeldingId() == null) {
						if(companyNotified2.isBedrijfsgegevensNietJuist())
							melding.setMeldingStatusCode(EMeldingStatus.DATA_NOK.getCode());
						else melding.setMeldingStatusCode(EMeldingStatus.INBEHANDELING.getCode());
					} else melding.setMeldingStatusCode(companyNotified2.getMeldingstatusCode());

					melding.setMeldingBatchMeldingBatchId(meldingBatch.getMeldingBatchId());
					
					melding.setTelefoonNummerDebiteur(companyNotified2.getTelefoonNummerDebiteur());
					melding.setEmailAdresDebiteur(companyNotified2.getEmailAdresDebiteur());

					Notitie createdNotitie = null;
					
					Melding createdMelding = saveMelding(melding);
					
					if (companyNotified2.getNotities() != null) {
						Notitie notitie = new Notitie();
						
						notitie.setDatum(new Date());
						notitie.setGebruikerGebruikerId(gebruikerId);
						//notitie.setMeldingMeldingId(createdMelding.getMeldingId());
						notitie.setNotitie(companyNotified2.getNotities());
						notitie.setNotitieType(ENotitieType.MELDING_GEBRUIKER.getCode());	
						
						createdNotitie = saveNotitie(notitie, createdMelding.getMeldingId());
						
						melding.setNotitieGebruiker(createdNotitie);
					}									
					
					// purchase Notification
					Gebruiker gebruiker = gebruikerService.findByGebruikerId(gebruikerId);
					Integer eigenBedrijfId = null;
					if (gebruiker != null)
						eigenBedrijfId = gebruiker.getBedrijfBedrijfId();
					
					if (eigenBedrijfId == null || eigenBedrijfId.equals(melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId()))							
						productService.purchaseVermelding(melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId(), null, datumAangemaakt);
					else
						productService.purchaseVermelding(eigenBedrijfId, melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId(), datumAangemaakt);
					
					meldingen.add(createdMelding);

					if(createdMelding != null) {
						resultnr++;
					} else {
						result = new ErrorService(ErrorService.CANNOT_SAVE_MELDING);
					}
				}

				Klant klant = klantDataService.findNewKlantById(gebruikerId);
				Gebruiker gebruiker = gebruikerDataService.findById(gebruikerId);
				Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(notificationsBatch.getBedrijfIdOver());

				if(klant != null) {
					//new klant
					emailService.sendNotificationNotInProcessEmail(gebruiker, bedrijf, meldingen);
				} else {
					//existing klant
					emailService.sendNotificationInProcessEmail(gebruiker, bedrijf, meldingen);
				}
			}
		} catch(DataServiceException e) {
			LOGGER.error("Error in createmeldingBatch. Email probably not sent!", e);
			throw new ServiceException(e);
		}

		return result;
	}

	@Override
	@Transactional
	public void createMeldingenQuartzJob() throws ServiceException {
		try {
			DateTime today = new DateTime(new Date());
			Configuratie configObject = configuratieDataService.findByPrimaryKey("MLD_VERWERKT");
			DateTime lastProcessDate = new DateTime(configObject.getWaardeDate());
			Integer differenceInDays = Days.daysBetween(lastProcessDate, today).getDays();

			for(Integer i = 0; i <= differenceInDays; i++) {
				Date iterationDate = lastProcessDate.plusDays(i).toDate();

				List<Bedrijf> bedrijvenMetMeldingen = bedrijfDataService.findAllBedrijvenWithNewMeldingenOfDay(iterationDate);

				if(bedrijvenMetMeldingen == null) {
					throw new ServiceException("Lijst met bedrijven met nieuwe meldingen moet bestaan");
				}

				for(Bedrijf bedrijf : bedrijvenMetMeldingen) {
					createMeldingenBriefForBedrijf(bedrijf, iterationDate);
				}

				configObject.setWaardeDate(iterationDate);

				configuratieDataService.save(configObject);
			}
		} catch(DataServiceException e) {throw new ServiceException(e);}
	}

	@Override
	@Transactional
	public ErrorService createMonitoring(Monitoring monitoring) throws ServiceException {
		ErrorService error = null;
		try {
			if(monitoring.getBedrijfByMonitoringDoorBedrijfIdBedrijfId() != null && monitoring.getBedrijfByMonitoringVanBedrijfId() != null) {
				Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(monitoring.getBedrijfByMonitoringVanBedrijfIdBedrijfId());

				// Create new reference number for notification
				String referentie = SerialNumber.generateRandomSerialNumber8_32();
				while(monitoringDataService.findByReferentieNummerIntern(referentie) != null) {
					referentie = SerialNumber.generateRandomSerialNumber8_32();
				}
				monitoring.setReferentieNummerIntern(referentie);

				monitoring.setDatumStart(new Date());
				monitoring.setMonitoringStatusCode(MonitoringStatusDataService.MONITORING_ACTIVE);
				monitoring.setKvKnummer(bedrijf.getKvKnummer());

				Monitoring newMonitoring = monitoringDataService.save(monitoring);

				// monitoring is abbo so remove not needed
				//productService.purchaseMonitoring(newMonitoring.getBedrijfByMonitoringDoorBedrijfIdBedrijfId(), newMonitoring.getDatumStart());
			} else error = new ErrorService(ErrorService.CANNOT_SAVE_MONITORING);
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot create notification: " + e.getMessage());
		}

		return error;
	}

	@Override
	@Transactional
	public void createNewMeldingenLetter(Integer meldingId, Integer gebruikerId, boolean generateForAllMeldingen) throws ServiceException {
		try {
			List<Melding> meldingenForLetter = null;
			if(generateForAllMeldingen) {
				meldingenForLetter = meldingDataService.findMeldingenOfDocumentByMeldingId(meldingId);
			} else {
				meldingenForLetter = new ArrayList<>();
				meldingenForLetter.add(meldingDataService.findById(meldingId));
			}

			if(meldingenForLetter.size() == 0) {throw new ServiceException("Size of list with meldingen cannot be 0.");}
			Melding firstInList = meldingenForLetter.get(0);
			documentService.createMeldingLetter(firstInList.getBedrijfByMeldingDoorBedrijfIdBedrijfId(), firstInList.getBedrijfByMeldingOverBedrijfIdBedrijfId(), meldingenForLetter, new Date());
			
			ContactMomentTransfer contactMoment = new ContactMomentTransfer();
			contactMoment.setBeantwoord("Nvt");
			contactMoment.setContactWijze("Vermeldingsbrief");
			contactMoment.setDatumContact(new Date());
			contactMoment.setGebruikerId(gebruikerId);
			contactMoment.setMeldingId(meldingId);
			Notitie notitie = new Notitie();
			notitie.setDatum(new Date());
			notitie.setGebruikerGebruikerId(gebruikerId);
			notitie.setNotitie("Vermeldingsbrief is verstuurd.");
			notitie.setNotitieType(ENotitieType.CONTACT_MOMENT.getCode());
			saveContactMomentNotification(contactMoment, notitie);
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public void createOldNotificationAlertsQuartzJob() throws ServiceException {
		List<Bedrijf> bedrijvenMetOpenVermeldingen = null;

		try {
			bedrijvenMetOpenVermeldingen = new ArrayList<>();

			for(Bedrijf bedrijf : bedrijvenMetOpenVermeldingen) {
				alertService.createOverdueNotificationAlert(bedrijf);
			}
		} catch(Exception e) {
			LOGGER.error("Error in createOldNotificationAlertsQuartzJob: " + e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * Deletes the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object identified by the presented id.
	 *
	 * @param bedrijf_ID an Integer representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to delete
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	@Override
	@Transactional
	public void deleteBedrijf(Integer bedrijf_ID) throws ServiceException {
		LOGGER.info("Method getBedrijf.");

		try {
			if(bedrijf_ID != null) {
				Bedrijf bedrijf = getBedrijf(bedrijf_ID);
				if(bedrijf != null) bedrijfDataService.delete(bedrijf);
			} else {
				LOGGER.warn("Method getBedrijf. Kan geen Bedrijf ophalen als key niet is meegegeven.");
			}
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public WebsiteparamTransfer fetchWebsiteparam() throws ServiceException {
		WebsiteparamTransfer result = null;

		Websiteparam websiteparam;
		try {
			websiteparam = websiteparamDataService.find();
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot fetch websiteparam: " + e.getMessage());
		}

		if(websiteparam != null) {
			result = new WebsiteparamTransfer(websiteparam.getStoringen(), websiteparam.getStartupsYtd(), websiteparam.getStartupsWeek(), websiteparam.getVermeldingenYtd(), websiteparam.getVermeldingenWeek());
		}

		return result;
	}

	/*
	 * Not used anymore. Replaced by findActiveObjectionsForSbdrAdmin + findActiveSupportTicketsForSbdrAdmin
	 * 
	 * @see nl.devoorkant.sbdr.business.service.BedrijfService#findActiveAlertsForSbdrAdmin(java.lang.Integer, boolean, org.springframework.data.domain.Pageable)
	 */
//	@Override
//	@Transactional(readOnly = true)
//	public PageTransfer<AdminAlertTransfer> findActiveAlertsForSbdrAdmin(Integer bId, boolean includeObjections, Pageable p) throws ServiceException {
//		if(bId != null) {
//			try {
//				Page<Object[]> r = bedrijfDataService.findActiveAlertsForSbdrAdmin(bId, includeObjections, p);
//				if(r != null) return ConvertUtil.convertPageToAdminAlertPageTransfer(r);
//				else return null;
//			} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
//				throw new ServiceException(e);
//			}
//		} else
//			throw new ServiceException("Error in findActiveAlertsForSbdrAdmin: Parameter bId(Integer) cannot be null");
//	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<AlertOverviewTransfer> findActiveAlertsOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws ServiceException {
		PageTransfer<AlertOverviewTransfer> result = null;

		try {
			Page<Object[]> alerts = bedrijfDataService.findActiveAlertsOfBedrijf(bedrijfId, userId, search, pageable);

			if(alerts != null) result = ConvertUtil.convertPageToAlertOverviewPageTransfer(alerts, false);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<AlertOverviewTransfer> findActiveAlertsNoMonitoringOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws ServiceException {
		PageTransfer<AlertOverviewTransfer> result = null;

		try {
			Page<Object[]> alerts = bedrijfDataService.findActiveAlertsNoMonitoringOfBedrijf(bedrijfId, userId, search, pageable);

			if(alerts != null) result = ConvertUtil.convertPageToAlertOverviewPageTransfer(alerts, false);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<AlertOverviewTransfer> findActiveMonitoringAlertsOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws ServiceException {
		PageTransfer<AlertOverviewTransfer> result = null;

		try {
			Page<Object[]> alerts = bedrijfDataService.findActiveMonitoringAlertsOfBedrijf(bedrijfId, userId, search, pageable);

			if(alerts != null) result = ConvertUtil.convertPageToAlertOverviewPageTransfer(alerts, false);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageTransfer<AlertOverviewTransfer> findActiveMonitoringNotificationsOfBedrijf(Integer bedrijfId, Pageable pageable) throws DataServiceException {
		PageTransfer<AlertOverviewTransfer> result = null;

		try {
			Page<Object[]> alerts = bedrijfDataService.findActiveMonitoringNotificationsOfBedrijf(bedrijfId, pageable);
			
			if(alerts != null) result = ConvertUtil.convertPageToAlertOverviewPageTransfer(alerts, true);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public PageTransfer<AdminAlertTransfer> findActiveObjectionsForSbdrAdmin(Integer bedrijfId, Pageable p) throws ServiceException {
		if(bedrijfId != null) {
			try {
				Page<Object[]> r = bedrijfDataService.findActiveObjectionsForSbdrAdmin(bedrijfId, p);
				if(r != null) return ConvertUtil.convertPageToAdminAlertPageTransfer(r);
				else return null;
			} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
				throw new ServiceException(e);
			}
		} else
			throw new ServiceException("Error in findActiveObjectionsForSbdrAdmin: Parameter bId(Integer) cannot be null");		
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<AdminAlertTransfer> findActiveSupportTicketsForSbdrAdmin(Integer bedrijfId, Pageable p) throws ServiceException {
		if(bedrijfId != null) {
			try {
				Page<Object[]> r = bedrijfDataService.findActiveSupportTicketsForSbdrAdmin(bedrijfId, p);
				if(r != null) return ConvertUtil.convertPageToAdminAlertPageTransfer(r);
				else return null;
			} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
				throw new ServiceException(e); 
			}
		} else
			throw new ServiceException("Error in findActiveSupportTicketsForSbdrAdmin: Parameter bId(Integer) cannot be null");		
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageTransfer<MeldingTransfer> findMeldingenOfAllBedrijven(String search, List<String> klantStatusCodes, List<String> meldingStatusCodes, Pageable pageable) throws ServiceException {
		PageTransfer<MeldingTransfer> result = null;

		try {
			Page<Melding> meldingen = bedrijfDataService.findMeldingenOfAllBedrijven(search, klantStatusCodes, meldingStatusCodes, pageable);

			if(meldingen != null) result = ConvertUtil.convertPageToMeldingPageTransfer(meldingen);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<MeldingOverviewTransfer> findActiveMeldingenOfBedrijf(Integer bedrijfId, String search, Pageable pageable) throws ServiceException {
		PageTransfer<MeldingOverviewTransfer> result = null;

		try {
			Page<Melding> meldingen = bedrijfDataService.findActiveMeldingenOfBedrijf(bedrijfId, search, pageable);

			if(meldingen != null) result = ConvertUtil.convertPageToMeldingOverviewPageTransfer(meldingen);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<MonitoringOverviewTransfer> findActiveMonitoringOfBedrijf(Integer bedrijfId, String search, Pageable pageable) throws ServiceException {
		PageTransfer<MonitoringOverviewTransfer> result = null;

		try {
			Page<Object[]> monitoring = bedrijfDataService.findActiveMonitoringOfBedrijf(bedrijfId, search, pageable);

			if(monitoring != null) {
				result = ConvertUtil.convertPageToMonitoringOverviewPageTransfer(monitoring);
				if(result.getContent() != null) {
					List<MonitoringOverviewTransfer> monitoringTransfers = new ArrayList<MonitoringOverviewTransfer>();
					for(MonitoringOverviewTransfer monitoringTransfer : result.getContent())
						monitoringTransfers.add(monitoringTransfer);
					// solved in HQL, property set in convertUtil
					//addHeeftMeldingProperty_MonitoringOverviewTransfer(bedrijfId, monitoringTransfers);
					addHeeftMonitoringProperty_MonitoringOverviewTransfer(bedrijfId, monitoringTransfers);
				}
			}

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<ExceptionBedrijfOverviewTransfer> findAllExceptionBedrijven(String search, List<String> statusCodes, Pageable pageable) throws ServiceException {
		PageTransfer<ExceptionBedrijfOverviewTransfer> result = null;

		try {
			Page<Object[]> objects = bedrijfDataService.findAllExceptionBedrijven(search, statusCodes, pageable);

			if(objects != null) result = ConvertUtil.convertPageToExceptionBedrijfOverviewPageTransfer(objects);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<KlantBedrijfOverviewTransfer> findAllKlantBedrijvenOnActiveKlantStatus(String search, List<String> statusCodes, Pageable pageable) throws ServiceException {
		PageTransfer<KlantBedrijfOverviewTransfer> result = null;

		try {
			Page<Object[]> objects = bedrijfDataService.findAllKlantBedrijvenOnKlantStatus(true, search, statusCodes, pageable);

			if(objects != null) result = ConvertUtil.convertPageToKlantBedrijfOverviewPageTransfer(objects);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Returns the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object by klantgebruikerId.<br/>
	 *
	 * @param gebruikerId an Integer representing the {@link nl.devoorkant.sbdr.data.model.Gebruiker} Object which references the {@link nl.devoorkant.sbdr.data.model.Bedrijf} to retrieve
	 * @return the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object
	 */
	@Override
	public Bedrijf findByKlantGebruikersId(Integer gebruikerId) throws ServiceException {
		LOGGER.info("Method getBedrijf.");

		if(gebruikerId != null) {
			try {
				return bedrijfDataService.findByKlantGebruikersId(gebruikerId);
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else {
			LOGGER.warn("Method getBedrijf. Kan geen Bedrijf ophalen als key niet is meegegeven.");
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<FactuurTransfer> findFacturenOfBedrijf(Integer bId, Pageable p) throws ServiceException {
		if(bId != null && p != null) {
			try {
				return ConvertUtil.convertPageToFactuurPageTransfer(bedrijfDataService.findFacturenOfBedrijf(bId, p));
			} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
				throw new ServiceException(e);
			}
		} else throw new ServiceException(ErrorService.GENERAL_FAILURE);
	}

	@Override
	public List<MeldingTransfer> findMeldingenAboutBedrijfByGebruikerId(Integer gebruikerId) throws ServiceException {
		LOGGER.info("Method findMeldingenAboutBedrijfByGebruikerId");

		List<MeldingTransfer> meldingenTransfer = new LinkedList<>();

		if(gebruikerId != null) {
			try {
				List<Melding> meldingen = meldingDataService.findBygebruikerIdOfCompany(gebruikerId);

				for(Melding m : meldingen)
					meldingenTransfer.add(ConvertUtil.createMeldingTransferFromMelding(m, m.getNotitieGebruiker()));

				return meldingenTransfer;
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else {
			LOGGER.warn("Method findMeldingenAboutBedrijfByGebruikerId. Kan geen meldingen ophalen als gebruikerId niet bekend is");
			return null;
		}
	}

	@Override
	public PageTransfer<RemovedBedrijfOverviewTransfer> findRemovedBedrijvenOfBedrijf(Integer bedrijfId, String view, String search, Pageable pageable) throws ServiceException {
		PageTransfer<RemovedBedrijfOverviewTransfer> result = null;

		try {
			Page<Object[]> removed = bedrijfDataService.findRemovedBedrijvenOfBedrijf(bedrijfId, view, search, pageable);

			if(removed != null)
				result = ConvertUtil.convertPageToRemovedBedrijvenOverviewPageTransfer(removed, bedrijfId);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public PageTransfer<SearchResultsOverviewTransfer> findSearchResults(String search, Boolean vermelder, Pageable pageable) throws ServiceException {
		PageTransfer<SearchResultsOverviewTransfer> result = null;

		try {
			Page<Object[]> results = bedrijfDataService.findSearchResults(search, vermelder, pageable);

			if(results != null) result = ConvertUtil.convertPageToSearchResultsOverviewPageTransfer(results);

			return result;
		} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}

	//	@Override
	//	Transactional(readonly=true)
	//	public InformationOverviewTransfer getInformationOverview() throws ServiceException {
	//		InformationOverviewTransfer result = null;
	//
	//		try {
	//
	//		} catch(DataServiceException e) {
	//			throw new ServiceException(e);
	//		}
	//	}
	//

	/**
	 * Returns the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object by primary key.<br/>
	 *
	 * @param bedrijf_ID an Integer representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to retrieve
	 * @return the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object
	 */
	@Override
	public Bedrijf getBedrijf(Integer bedrijf_ID) throws ServiceException {
		LOGGER.info("Method getBedrijf.");

		if(bedrijf_ID != null) {
			try {
				return bedrijfDataService.findByBedrijfId(bedrijf_ID);
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else {
			LOGGER.warn("Method getBedrijf. Kan geen Bedrijf ophalen als key niet is meegegeven.");
			return null;
		}
	}

	@Override
	public BedrijfTransfer getBedrijfData(Integer bedrijfId) throws ServiceException {
		BedrijfTransfer result = null;
		try {
			Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);

			if(bedrijf != null) {
				String huisnr = null;

				if(bedrijf.getHuisNr() != null) huisnr = bedrijf.getHuisNr().toString();

				String referentieIntern = null;
				if(bedrijf.getSbdrNummer() != null)
					referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

				result = new BedrijfTransfer(bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieIntern, bedrijf.getStraat(), huisnr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), bedrijf.getTelefoonnummer(), null);
			} else throw new ServiceException("Cannot fetch bedrijf");

			return result;

		} catch(DataServiceException e) {
			throw new ServiceException("Cannot fetch bedrijf");
		} catch(Exception e) {
			throw new ServiceException("Cannot fetch bedrijf");
		}
	}
	
	@Override
	public BedrijfTransferExtra getBedrijfDataExtra(Integer bedrijfId) throws ServiceException {
		BedrijfTransferExtra result = null;
		try {
			Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);

			if(bedrijf != null) {
				String huisnr = null;

				if(bedrijf.getHuisNr() != null) huisnr = bedrijf.getHuisNr().toString();

				String referentieIntern = null;
				if(bedrijf.getSbdrNummer() != null)
					referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();
				
				String postPostbus = null;
				String postPlaats = null;
				String postPostcode = null;
				
				// Postadres
				CIKvKDossier dossier = companyInfoDataService.findByCIKvKDossierId(bedrijf.getCIKvKDossierCikvKdossierId());
				if(dossier.getStraatCa() != null && dossier.getStraatCa().equalsIgnoreCase("Postbus")) {
					postPostbus = "Postbus " + dossier.getStraatHuisnummerCa();
					postPlaats = dossier.getPlaatsCa();
					postPostcode = dossier.getPostcodeCa();
				}

				result = new BedrijfTransferExtra(bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieIntern, bedrijf.getStraat(), huisnr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), bedrijf.getTelefoonnummer(), null, postPostbus, postPlaats, postPostcode);
			} else throw new ServiceException("Cannot fetch bedrijf");

			return result;

		} catch(DataServiceException e) {
			throw new ServiceException("Cannot fetch bedrijf");
		} catch(Exception e) {
			throw new ServiceException("Cannot fetch bedrijf");
		}		
	}
	
	@Override
	public Bedrijf findBySbdrNummer(String sbdrNummer) throws ServiceException {
		try {
			if (sbdrNummer != null)
				return bedrijfDataService.findBySbdrNummer(sbdrNummer);
			else
				return null;
		} catch (DataServiceException e) {
			throw new ServiceException(e);
		}
	}	

	@Override
	@Transactional
	public MeldingTransfer getMeldingData(Integer meldingId, Integer doorBedrijfId, Integer bedrijfId, Integer gebruikerId) throws ServiceException {
		MeldingTransfer result = null;

		try {
			LOGGER.debug("getMeldingData: find MeldingById");
			Melding melding = meldingDataService.findById(meldingId);
			boolean hasObjection = false;

			try {
				LOGGER.debug("getMeldingData: find meldingHasObjection");
				hasObjection = meldingService.meldingHasObjection(melding.getMeldingId());
			} catch(Exception e) {
				// do nothing
			}

			//Bedrijf meldingDoorBedrijf = melding.getBedrijfByMeldingDoorBedrijfId();

			Integer meldingDoorBedrijfId = null;
			LOGGER.debug("getMeldingData: find door/over bedrijf");
			if(melding.getBedrijfByMeldingDoorBedrijfId() != null)
				meldingDoorBedrijfId = melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId();

			Integer meldingOverBedrijfId = null;
			if(melding.getBedrijfByMeldingOverBedrijfId() != null)
				meldingOverBedrijfId = melding.getBedrijfByMeldingOverBedrijfIdBedrijfId();
			

			// doorBedrijfId == null if admin is using this method
			// or doorbedrijfId is company which wants to send reaction/objection, so doorBedrijfId then equals meldingOverBedrijfId
			// otherwise doorBedrijfId may fetch only its own notifications
			
			LOGGER.debug("getMeldingData: check user before fetching data");
			if(melding != null && meldingOverBedrijfId != null && (doorBedrijfId == null || meldingDoorBedrijfId.equals(doorBedrijfId) || meldingOverBedrijfId.equals(doorBedrijfId)) && meldingOverBedrijfId.equals(bedrijfId)) {
				LOGGER.debug("getMeldingData: find bedrijf byBedrijf");
				Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(meldingOverBedrijfId); //melding.getBedrijfByMeldingOverBedrijfId();
				String huisnr = null;

				if(bedrijf.getHuisNr() != null) huisnr = bedrijf.getHuisNr().toString();

				String meldingstatus = EMeldingStatus.get(melding.getMeldingStatusCode()).getOmschrijving();
				String meldingstatusCode = EMeldingStatus.get(melding.getMeldingStatusCode()).getCode();

				String redenVerwijderenCode = null;
				String redenVerwijderenOmschrijving = null;
				if(melding.getRedenVerwijderenMeldingCode() != null) {
					redenVerwijderenCode = ERedenVerwijderenMelding.get(melding.getRedenVerwijderenMeldingCode()).getCode();
					redenVerwijderenOmschrijving = ERedenVerwijderenMelding.get(melding.getRedenVerwijderenMeldingCode()).getOmschrijving();
				}

				boolean bedrijfsgegevensNietJuist = false;
				if(meldingstatusCode != null && meldingstatusCode.equals(EMeldingStatus.DATA_NOK.getCode()))
					bedrijfsgegevensNietJuist = true;

				String referentieInternBE = null;
				if(bedrijf.getSbdrNummer() != null)
					referentieInternBE = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();


				String referentieInternVE = null;
				if(melding.getReferentieNummerIntern() != null)
					referentieInternVE = EReferentieInternType.VERMELDING.getPrefix() + melding.getReferentieNummerIntern();

				LOGGER.debug("getMeldingData: find Notitie");
				Notitie notitie = notitieDataService.findByNotitieMeldingGebruiker(melding.getMeldingId(), null, ENotitieType.MELDING_GEBRUIKER.getCode());
				
				result = new MeldingTransfer(melding.getMeldingId(), melding.getGebruikerByMeldingDoorGebruikerIdGebruikerId(), melding.getGebruikerByLaatsteMutatieDoorGebruikerIdGebruikerId(), melding.getGebruikerByGeaccordeerdDoorGebruikerIdGebruikerId(), melding.getGebruikerByVerwijderdDoorGebruikerIdGebruikerId(), bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieInternBE, bedrijf.getStraat(), huisnr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), melding.getReferentieNummer(), referentieInternVE, melding.getDatumIngediend(), melding.getDatumLaatsteMutatie(), melding.getDatumGeaccordeerd(), melding.getDatumVerwijderd(), melding.getDatumVerloopFactuur(), melding.getBedrag(), melding.getOorspronkelijkBedrag(), meldingDoorBedrijfId, meldingstatus, meldingstatusCode, melding.isDoorBedrijfWeergeven(), melding.isBedragWeergeven(), bedrijfsgegevensNietJuist, redenVerwijderenCode, redenVerwijderenOmschrijving, bedrijf.getTelefoonnummer(), melding.getBriefStatusCode(), EBriefStatus.get(melding.getBriefStatusCode()).getOmschrijving(), melding.getBriefBatchBriefBatchId() != null, hasObjection, notitie != null ? notitie.getNotitie() : null, melding.getTelefoonNummerDebiteur() != null ? melding.getTelefoonNummerDebiteur() : bedrijf.getTelefoonnummer(), melding.getEmailAdresDebiteur());

				//LOGGER.debug("test: " + melding.getBedrijfByMeldingDoorBedrijfId().getBedrijfsNaam());

				LOGGER.debug("getMeldingData: finished fetching data");
				return result;
			} else throw new ServiceException("Cannot fetch melding.");


		} catch(DataServiceException e) {
			throw new ServiceException("Cannot fetch bedrijf");
		}
	}

	@Override
	@Transactional
	public BedrijfReportTransfer getReportData(Integer bedrijfAanvragerId, Integer bedrijfId, String referentieNummer) throws ServiceException {

		try {
			String ref = referentieNummer;
			BedrijfReportTransfer result = null;
			if(bedrijfId != null) {
				Bedrijf bedrijfAanvrager = bedrijfDataService.findByBedrijfId(bedrijfAanvragerId);
				if(referentieNummer == null)
					ref = EDocumentType.RAPPORT.getCode() + "-" + bedrijfAanvrager.getSbdrNummer() + "-" + bedrijfAanvrager.getRapportCounter();

				Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);

				Bedrijf bedrijfHoofdvestiging = null;
				// Try to find HQ of company
				CompanyInfo companyHq = null;
				if (bedrijf.getHoofdNeven().equals(EBedrijfType.NEVEN.getCode()))
					companyHq = companyInfoService.retrieveHqFromCompanyInfo(bedrijf.getKvKnummer());
				if(companyHq != null)
					bedrijfHoofdvestiging = bedrijfDataService.findByKvkNummer(companyHq.getKvKnummer(), companyHq.getSub());
					
				//if(bedrijf != null && bedrijf.getSubDossier() != null)
				//	bedrijfHoofdvestiging = bedrijfDataService.findByKvkNummer(bedrijf.getKvKnummer(), null);

				// find all active meldingen of all companies related to bedrijf (by kvknummer)
				List<String> meldingCodes = new ArrayList<String>();
				meldingCodes.add(EMeldingStatus.ACTIEF.getCode());
				List<Melding> meldingen = bedrijfDataService.findMeldingenOfBedrijf(bedrijfId, meldingCodes);

				// 13-10-2017 MBR not needed for report? Klant klant = bedrijfDataService.findKlantOfBedrijf(bedrijf.getBedrijfId());
				Klant klant = null;
				CIKvKDossier dossier = companyInfoDataService.findByCIKvKDossierId(bedrijf.getCIKvKDossierCikvKdossierId());
				KvkDossierTransfer kvkDossierTransfer = ConvertUtil.convertCiKvkDossierToKvkDossierTransfer(bedrijf, dossier, klant);

				KvkDossierTransfer kvkDossierTransferHoofd = null;
				if(bedrijfHoofdvestiging != null) {
					CIKvKDossier dossierHoofd = companyInfoDataService.findById(bedrijfHoofdvestiging.getCIKvKDossierCikvKdossierId());
					kvkDossierTransferHoofd = ConvertUtil.convertCiKvkDossierToKvkDossierTransfer(bedrijfHoofdvestiging, dossierHoofd, klant);
					
					// prevent showing is customer info to other customers
					kvkDossierTransferHoofd.setKlant(null);
				}
				
				// prevent showing is customer info to other customers
				kvkDossierTransfer.setKlant(null);

				// sbi omschrijvingen
				if(kvkDossierTransfer.getHoofdactiviteitSbi() != null) {
					Sbi sbi = bedrijfDataService.findByCode(kvkDossierTransfer.getHoofdactiviteitSbi());

					if(sbi != null) kvkDossierTransfer.setHoofdactiviteit(sbi.getOmschrijving());
				}
				if(kvkDossierTransfer.getNevenactiviteit1Sbi() != null) {
					Sbi sbi = bedrijfDataService.findByCode(kvkDossierTransfer.getNevenactiviteit1Sbi());

					if(sbi != null) kvkDossierTransfer.setNevenactiviteit1(sbi.getOmschrijving());
				}
				if(kvkDossierTransfer.getNevenactiviteit2Sbi() != null) {
					Sbi sbi = bedrijfDataService.findByCode(kvkDossierTransfer.getNevenactiviteit2Sbi());

					if(sbi != null) kvkDossierTransfer.setNevenactiviteit2(sbi.getOmschrijving());
				}

				// RSIN nvt in case of 'eenmanszaken'
				if(kvkDossierTransfer.getRsin() == null || kvkDossierTransfer.getRsin().equals(""))
					kvkDossierTransfer.setRsin("n.v.t.");

				// rechtsvorm
				if(bedrijf.getCIKvKDossier().getRv() != null) {
					Rechtsvorm rechtsvorm = bedrijfDataService.findByRvCode(bedrijf.getCIKvKDossier().getRv());

					if(rechtsvorm != null) kvkDossierTransfer.setRechtsvorm(rechtsvorm.getOmschrijving());
				} else kvkDossierTransfer.setRechtsvorm("-");

				// actief status
				if((kvkDossierTransfer.getIndFaillissement() != null && kvkDossierTransfer.getIndFaillissement().equalsIgnoreCase("J"))) { // || (kvkDossierTransfer.getIndSurseance() != null && kvkDossierTransfer.getIndSurseance().equalsIgnoreCase("J"))
					kvkDossierTransfer.setActief(EBedrijfUitvoeringStatus.NIET_ACTIEF.getKorteOmschrijving());
					kvkDossierTransfer.setActiefOmschrijving(EBedrijfUitvoeringStatus.NIET_ACTIEF.getOmschrijving());
				} else if(kvkDossierTransfer.getDatumOnbinding() != null) {
					kvkDossierTransfer.setActief(EBedrijfUitvoeringStatus.ONTBONDEN.getKorteOmschrijving());
					kvkDossierTransfer.setActiefOmschrijving(EBedrijfUitvoeringStatus.ONTBONDEN.getOmschrijving());
				} else if(kvkDossierTransfer.getDatumOpheffing() != null) {
					kvkDossierTransfer.setActief(EBedrijfUitvoeringStatus.OPGEHEVEN.getKorteOmschrijving());
					kvkDossierTransfer.setActiefOmschrijving(EBedrijfUitvoeringStatus.OPGEHEVEN.getOmschrijving());
				} else if(kvkDossierTransfer.getDatumUitschrijving() != null) {
					kvkDossierTransfer.setActief(EBedrijfUitvoeringStatus.UITGESCHREVEN.getKorteOmschrijving());
					kvkDossierTransfer.setActiefOmschrijving(EBedrijfUitvoeringStatus.UITGESCHREVEN.getOmschrijving());
				} else {
					kvkDossierTransfer.setActief(EBedrijfUitvoeringStatus.ACTIEF.getKorteOmschrijving());
					kvkDossierTransfer.setActiefOmschrijving(EBedrijfUitvoeringStatus.ACTIEF.getOmschrijving());
				}

				// faillissements / surseance status
				if(kvkDossierTransfer.getIndFaillissement() != null && kvkDossierTransfer.getIndFaillissement().equalsIgnoreCase("J")) {
					if(kvkDossierTransfer.getIndSurseance() != null && kvkDossierTransfer.getIndSurseance().equalsIgnoreCase("J")) {
						kvkDossierTransfer.setFaillietSurseance(EBedrijfUitvoeringStatus.FAILLIET_SURSEANCE.getKorteOmschrijving());
						kvkDossierTransfer.setFaillietSurseanceOmschrijving(EBedrijfUitvoeringStatus.FAILLIET_SURSEANCE.getOmschrijving());
					} else {
						kvkDossierTransfer.setFaillietSurseance(EBedrijfUitvoeringStatus.FAILLIET.getKorteOmschrijving());
						kvkDossierTransfer.setFaillietSurseanceOmschrijving(EBedrijfUitvoeringStatus.FAILLIET.getOmschrijving());
					}
				} else if(kvkDossierTransfer.getIndSurseance() != null && kvkDossierTransfer.getIndSurseance().equalsIgnoreCase("J")) {
					kvkDossierTransfer.setFaillietSurseance(EBedrijfUitvoeringStatus.SURSEANCE.getKorteOmschrijving());
					kvkDossierTransfer.setFaillietSurseanceOmschrijving(EBedrijfUitvoeringStatus.SURSEANCE.getOmschrijving());
				} else {
					kvkDossierTransfer.setFaillietSurseance("Nee");
					kvkDossierTransfer.setFaillietSurseanceOmschrijving("Geen bijzonderheden");
				}

				List<Document> reportsRequested = documentDataService.findRequestedReportsOfBedrijfId(bedrijfId);
				int nrOfReportsLaatsteJaar = 0;
				int nrOfReportsLaatsteHalfjaar = 0;
				int nrOfReportsLaatsteKwartaal = 0;
				int nrOfReportsLaatsteMaand = 0;
				int nrOfReportsLaatsteTweeWeken = 0;
				int nrOfReportsLaatsteWeek = 0;
				List<ChartDataTransfer> reportsLastTwoWeeks = new ArrayList<ChartDataTransfer>();
				Date now = ConvertUtil.dateWithoutTime(null);

				// create chart items
				for(int i = -13; i <= 0; i++) {
					Calendar reportDateLastTwoWeeks = Calendar.getInstance();
					reportDateLastTwoWeeks.setTime(now);
					reportDateLastTwoWeeks.add(Calendar.DAY_OF_MONTH, i);

					String colKey = ConvertUtil.dayMonthFromTime(reportDateLastTwoWeeks.getTime());

					ChartDataTransfer chartData = new ChartDataTransfer(reportDateLastTwoWeeks.getTime(), 0, colKey);
					reportsLastTwoWeeks.add(chartData);
				}

				if(reportsRequested != null) {
					for(Document report : reportsRequested) {
						Calendar reportDateJaar = Calendar.getInstance();
						reportDateJaar.add(Calendar.YEAR, -1); // to get previous year add -1

						Calendar reportDateHalfjaar = Calendar.getInstance();
						reportDateHalfjaar.add(Calendar.MONTH, -6);

						Calendar reportDateKwartaal = Calendar.getInstance();
						reportDateKwartaal.add(Calendar.MONTH, -3);

						Calendar reportDateMaand = Calendar.getInstance();
						reportDateMaand.add(Calendar.MONTH, -1);

						Calendar reportDateTwoWeeks = Calendar.getInstance();
						reportDateTwoWeeks.add(Calendar.DAY_OF_MONTH, -14);  // or -13? With or without today?

						Calendar reportDateOneWeek = Calendar.getInstance();
						reportDateOneWeek.add(Calendar.DAY_OF_MONTH, -7);  // or -6? With or without today?

						if(report.getDatumAangemaakt().after(reportDateJaar.getTime())) nrOfReportsLaatsteJaar++;
						if(report.getDatumAangemaakt().after(reportDateHalfjaar.getTime()))
							nrOfReportsLaatsteHalfjaar++;
						if(report.getDatumAangemaakt().after(reportDateKwartaal.getTime()))
							nrOfReportsLaatsteKwartaal++;
						if(report.getDatumAangemaakt().after(reportDateMaand.getTime())) nrOfReportsLaatsteMaand++;
						if(report.getDatumAangemaakt().after(reportDateTwoWeeks.getTime())) {
							nrOfReportsLaatsteTweeWeken++;

							for(ChartDataTransfer reportLastTwoWeeks : reportsLastTwoWeeks) {
								Date reportChartDate = new Date(reportLastTwoWeeks.getDatum());
								if(ConvertUtil.dateWithoutTime(report.getDatumAangemaakt()).equals(reportChartDate))
									reportLastTwoWeeks.setAantal(reportLastTwoWeeks.getAantal() + 1);
							}
						}
						if(report.getDatumAangemaakt().after(reportDateOneWeek.getTime())) nrOfReportsLaatsteWeek++;
					}
				}
				int[] nrOfReports = new int[6];
				nrOfReports[0] = nrOfReportsLaatsteJaar;
				nrOfReports[1] = nrOfReportsLaatsteHalfjaar;
				nrOfReports[2] = nrOfReportsLaatsteKwartaal;
				nrOfReports[3] = nrOfReportsLaatsteMaand;
				nrOfReports[4] = nrOfReportsLaatsteTweeWeken;
				nrOfReports[5] = nrOfReportsLaatsteWeek;

				kvkDossierTransfer.setNrOfReports(nrOfReports);

				List<Monitoring> activeMonitoring = bedrijfDataService.findMonitoringOfBedrijf(bedrijfId);
				if(activeMonitoring != null) kvkDossierTransfer.setNrOfActiveMonitorings(activeMonitoring.size());
				else kvkDossierTransfer.setNrOfActiveMonitorings(0);

				// find all resolved meldingen of all companies related to bedrijf (by kvknummer)
				meldingCodes = new ArrayList<String>();
				meldingCodes.add(EMeldingStatus.VERWIJDERD.getCode());
				List<Melding> meldingenResolved = bedrijfDataService.findMeldingenOfBedrijf(bedrijfId, meldingCodes);

				int aantalMeldingenActief = 0;
				int aantalMeldingenResolved = 0;
				BigDecimal bedragOpen = null;
				BigDecimal bedragResolved = null;

				// iterate active meldingen and get nrs + amounts
				for(Melding melding : meldingen) {
					aantalMeldingenActief++;

					if(melding.isBedragWeergeven() && melding.getBedrag() != null) {
						if(bedragOpen == null) bedragOpen = melding.getBedrag();
						else
							bedragOpen = bedragOpen.add(melding.getBedrag()); //new Double(bedragOpen.doubleValue() + melding.getBedrag().doubleValue());
					}
				}

				CompanyInfo parent = null;
				CompanyInfo ultParent = null;

				if(kvkDossierTransfer.getParentKvKNummer() != null) {
					List<CompanyInfo> parentResults = companyInfoService.retrieveFromCompanyInfo(kvkDossierTransfer.getParentKvKNummer(), null, 1);
					if(parentResults != null && parentResults.size() > 0) {
						parent = parentResults.get(0);
					}
				}

				if(kvkDossierTransfer.getUltimateParentKvKNummer() != null) {
					List<CompanyInfo> ultParentResults = companyInfoService.retrieveFromCompanyInfo(kvkDossierTransfer.getUltimateParentKvKNummer(), null, 1);
					if(ultParentResults != null && ultParentResults.size() > 0) {
						ultParent = ultParentResults.get(0);
					}
				}

				//iterate resolved meldingen and get nrs + amounts
				//for (Melding melding: meldingenResolved) {
				//	aantalMeldingenResolved++;
				//
				//	if (melding.isBedragWeergeven() && melding.getBedrag() != null) {
				//		if (bedragResolved == null)
				//			bedragResolved = melding.getBedrag();
				//		else
				//			bedragResolved = new Double(bedragResolved.doubleValue() + melding.getBedrag().doubleValue());
				//	}
				//}

				// bouw historie lijst op
				List<HistorieTransfer> historie = ConvertUtil.convertToHistorieTransfer(kvkDossierTransfer, meldingen);
				// haal meldingen op
				List<MeldingOverviewTransfer> meldingenoverview = ConvertUtil.convertMeldingenToMeldingenOverviewTransfer(meldingen, bedrijfId);
				// set sbi description from sbicode
				List<String> bedrijfIds = new ArrayList<String>();
				for(MeldingOverviewTransfer meldingoverview : meldingenoverview) {
					if(meldingoverview.getSbiOmschrijving() != null) {
						Sbi sbiCode = bedrijfDataService.findByCode(meldingoverview.getSbiOmschrijving());
						if (sbiCode != null)
							meldingoverview.setSbiOmschrijving(sbiCode.getOmschrijving());
						else
							meldingoverview.setSbiOmschrijving("onbekend");
					}
					if (!bedrijfIds.contains(meldingoverview.getKvkNummer()))
	    				bedrijfIds.add(meldingoverview.getKvkNummer());
					// remove kvknumber, only added for this count purpose
					meldingoverview.setKvkNummer(null);
				}
				int countCrediteuren = bedrijfIds.size();
				
				// maak chartdata meldingen
				List<ChartDataTransfer> meldingenLastYear = createMeldingenChartData(meldingenoverview);

				// bepaal active betalingsachterstanden
				boolean activemeldingen = false;
				for(MeldingOverviewTransfer melding : meldingenoverview) {
					if(melding.getStatusCode().equals(EMeldingStatus.ACTIEF.getCode())) {
						activemeldingen = true;
						break;
					}
				}
				
				// bepaal ratingScore
				Integer ratingScore = dossier.getCsCreditRating();
				// minus 2
				if (ratingScore != null)
					ratingScore -= 2;
				if (ratingScore != null && ratingScore != 0) {
					// if eenmanszaak then +16
					if (dossier.getRv().equals("01") || dossier.getRv().equals("02") || dossier.getRv().equals("07") || dossier.getRv().equals("11"))
					{
						if (ratingScore != null)
							ratingScore += 16;
						else
							ratingScore = 16;
						
						if (ratingScore > 100)
							ratingScore = 100;
					}
				} else
					ratingScore = -1;
				if (activemeldingen)
					ratingScore = 1;
				
				// rating text
				String ratingScoreIndicatorMessage = "";
				if (!activemeldingen && ratingScore != null) {
					if (ratingScore < 0 || ratingScore > 100) {
					    // out of range, no score
						ratingScoreIndicatorMessage = dossier.getCsProviderDescription();
					} else {
					   ratingScoreIndicatorMessage = BedrijfService.ratingScoreMessage.floorEntry(ratingScore).getValue()[BedrijfService.RATINGMESSAGE_INDICATOR];
					}		
				} else 
					ratingScoreIndicatorMessage = BedrijfService.ratingBetalingsachterstandMessage[BedrijfService.RATINGMESSAGE_INDICATOR];
				
				// bouw rapport notificatie lijst op
				List<OpmerkingenTransfer> opmerkingen = ConvertUtil.convertToOpmerkingenTransfer(kvkDossierTransfer.getActief(), kvkDossierTransfer.getActiefOmschrijving(), meldingenoverview, nrOfReports, kvkDossierTransfer.getNrOfActiveMonitorings(), kvkDossierTransfer.getDatumHuidigeVestiging(), ratingScore, parent, ultParent);

				
				// Report data
				result = new BedrijfReportTransfer(bedrijfAanvrager, bedrijf, bedrijfHoofdvestiging, kvkDossierTransfer, kvkDossierTransferHoofd, meldingenoverview, opmerkingen, historie, referentieNummer, aantalMeldingenActief, aantalMeldingenResolved, kvkDossierTransfer.getNrOfActiveMonitorings(), countCrediteuren, bedragOpen, bedragResolved, reportsLastTwoWeeks, meldingenLastYear, ratingScore, ratingScoreIndicatorMessage, parent, ultParent);
			}

			return result;
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		} 
	}
	
	@Override
	@Transactional
	public EMeldingStatus maxMeldingStatus(String kvkNumber) {
		EMeldingStatus meldingStatus = null;
		
		List<String> kvkNumbers = new ArrayList<String>();
		kvkNumbers.add(kvkNumber);
		List<Bedrijf> bedrijven = bedrijfDataService.findByKvkNummers(kvkNumbers);
		
		if (bedrijven != null && !bedrijven.isEmpty()) {
			for (Bedrijf bedrijf : bedrijven) {
				if (bedrijf.getMeldingsForMeldingOverBedrijfId() != null) {
					for (Melding melding : bedrijf.getMeldingsForMeldingOverBedrijfId()) {
						if (melding.getMeldingStatusCode().equals(EMeldingStatus.ACTIEF.getCode())) {
							meldingStatus = EMeldingStatus.ACTIEF;
							break;
						} else if (melding.getMeldingStatusCode().equals(EMeldingStatus.INBEHANDELING.getCode()))
							meldingStatus = EMeldingStatus.INBEHANDELING;
					}
					
					if (meldingStatus != null && meldingStatus.equals(EMeldingStatus.ACTIEF))
						break;
				}
			}
		}
		
		return meldingStatus;
	}

	@Override
	public boolean hasRightToDo(Integer bedrijfId, EBevoegdheid checkBevoegdheid) throws ServiceException {
		boolean result = false;

		try {
			Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);
			Klant klant = bedrijfDataService.findKlantOfBedrijf(bedrijfId);

			if(klant != null) {
				switch(checkBevoegdheid) {
					case RAPPORT_INZIEN:
					case MELDING_INVOEREN:
					case MONITORING_TOEVOEGEN:
						if(bedrijf.getBedrijfStatusCode().equals(EBedrijfStatus.ACTIEF.getCode()) && klant.getKlantStatusCode().equals(EKlantStatus.ACTIEF.getCode()))
							result = true;
						break;
				}
			}

			return result;
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}


	}

	@Override
	@Transactional
	public void ignoreException(Integer customMeldingId, Integer bedrijfId) throws ServiceException {
		if(bedrijfId != null) {
			try {
				CustomMelding customMelding = customMeldingDataService.findByBedrijfId(customMeldingId, bedrijfId);

				if(customMelding != null) {
					customMelding.setDatumVerwerkt(new Date());

					customMelding.setCustomMeldingStatusCode(ECustomMeldingStatus.VERVALLEN.getCode());

					customMeldingDataService.save(customMelding);
				}
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		}
	}

	@Override
	public boolean isBedrijfOfKlant(Integer bedrijfId) throws ServiceException {
		boolean result = true;

		try {
			Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);

			if(bedrijf != null) {
				if(bedrijf.getKvKnummer().equals(BedrijfService.KVK_SBDR)) result = false;
			}

			return result;
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}

	}

	@Override
	public boolean isIbanCheckOk(String ibanNumber) throws ServiceException {
		if(ibanNumber == null) throw new ServiceException("Iban number null not allowed");

		Map<String, String> vars = new HashMap<String, String>();
		vars.put("iban", ibanNumber);

		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);		
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		try {
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
			messageConverters.add(new MappingJackson2HttpMessageConverter());
			messageConverters.add(new FormHttpMessageConverter());
			messageConverters.add(new StringHttpMessageConverter());
			restTemplate.setMessageConverters(messageConverters);

			IbanCheckServiceResponse response = restTemplate.getForObject("https://openiban.com/validate/{iban}?getBIC=false&validateBankCode=false", IbanCheckServiceResponse.class, vars);

			if(response != null) {
				return response.getValid().equals("true");
			} else throw new ServiceException("Cannot call IBAN check");

		} catch(RestClientException e) {
			throw new ServiceException("Cannot call IBAN check");
		}
	}

	@Override
	public boolean isViesCheckOk(String vatNumber) throws ServiceException {
		if(vatNumber == null) throw new ServiceException("Vat number null not allowed");

		Map<String, String> vars = new HashMap<String, String>();
		vars.put("ms", "NL");
		vars.put("iso", "NL");
		vars.put("vat", vatNumber);
		
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

			ViesCheckRequest viesCheckRequest = new ViesCheckRequest();
			viesCheckRequest.setMs("NL");
			viesCheckRequest.setIso("NL");
			viesCheckRequest.setVat(vatNumber);
			HttpEntity<ViesCheckRequest> entity = new HttpEntity<ViesCheckRequest>(viesCheckRequest,headers);		
			ResponseEntity<String> result = restTemplate.exchange(sbdrmuleBaseUri + "services/secure/viesCheck", HttpMethod.GET, entity, String.class);
			
			if(result != null && result.hasBody())
				return result.getBody().contains("Yes, valid VAT number");
			else
				 throw new ServiceException("Cannot call VIES check");
			

		} catch(RestClientException e) {
			throw new ServiceException("Cannot call viesCheck service");
		}		
		
//		LOGGER.debug("ViesCheck request processed");
//		
//
//		restTemplate = new RestTemplate();
//
//		try {
//			String result = restTemplate.getForObject("http://ec.europa.eu/taxation_customs/vies/viesquer.do?ms={ms}&iso={iso}&vat={vat}&name=&companyType=&street1=&postcode=&city=&BtnSubmitVat=Verify", String.class, vars);
//
//			if(result != null) {
//				return result.contains("Yes, valid VAT number");
//			} else throw new ServiceException("Cannot call VIES check");
//		} catch(RestClientException e) {
//			throw new ServiceException("Cannot call VIES check");
//		}
	}

	@Override
	@Transactional(readOnly = true)
	public MonitoringDetailsTransfer monitoringDetailTransfer(Integer doorBedrijfId, Integer monitoringId) throws ServiceException {
		MonitoringDetailsTransfer result = null;

		if(doorBedrijfId != null && monitoringId != null) {
			try {
				Monitoring monitoring = monitoringDataService.findById(monitoringId);

				Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(monitoring.getBedrijfByMonitoringVanBedrijfIdBedrijfId());
				CIKvKDossier ciKvkDossier = bedrijf.getCIKvKDossier();

				MonitoringOverviewTransfer monitoringOverview = ConvertUtil.createMonitoringOverviewTransferFromMonitoring(monitoring);

				Date datumInschrijving = ciKvkDossier.getDatumInschrijving();
				Date datumVoorzetting = ciKvkDossier.getDatumVoortzetting();

				SimpleDateFormat format1 = new SimpleDateFormat("yyyymmdd");

				Date datumFaillissementStart = null;
				Date datumFaillissementEinde = null;
				Date datumSurseanceStart = null;
				Date datumSurseanceEinde = null;
				String indFaillissement = "N";
				String indSurseance = "N";

				try {
					if(ciKvkDossier.getAanvangFaillissement() != null && !ciKvkDossier.getAanvangFaillissement().equals("") && !ciKvkDossier.getAanvangFaillissement().equals("0")) {
						datumFaillissementStart = format1.parse(ciKvkDossier.getAanvangFaillissement());
						indFaillissement = "J";
					}
					if(ciKvkDossier.getAanvangSurseance() != null && !ciKvkDossier.getAanvangSurseance().equals("0") && !ciKvkDossier.getAanvangSurseance().equals("0")) {
						indSurseance = "J";
						datumSurseanceStart = format1.parse(ciKvkDossier.getAanvangSurseance());
					}
					if(ciKvkDossier.getEindeFaillissement() != null && !ciKvkDossier.getEindeFaillissement().equals("0") && !ciKvkDossier.getEindeFaillissement().equals("0")) {
						datumFaillissementEinde = format1.parse(ciKvkDossier.getEindeFaillissement());
						if(!datumFaillissementStart.after(datumFaillissementEinde)) indFaillissement = "N";
					}
					if(ciKvkDossier.getEindeSurseance() != null && !ciKvkDossier.getEindeSurseance().equals("0") && !ciKvkDossier.getEindeSurseance().equals("0")) {
						datumSurseanceEinde = format1.parse(ciKvkDossier.getEindeSurseance());
						if(!datumSurseanceStart.after(datumSurseanceEinde)) indSurseance = "N";
					}
				} catch(ParseException e) {
					// do nothing
				}

				Date datumOntbinding = ciKvkDossier.getDatumOntbinding();
				Date datumOpheffing = ciKvkDossier.getDatumOpheffing();
				Date datumUitschrijving = ciKvkDossier.getDatumUitschrijving();
				Date datumVoortzetting = ciKvkDossier.getDatumVoortzetting();


				if(indFaillissement != null && indFaillissement.equalsIgnoreCase("J")) { // ) || (indSurseance != null && indSurseance.equalsIgnoreCase("J"))
					monitoringOverview.setBedrijfActief(EBedrijfUitvoeringStatus.NIET_ACTIEF.getKorteOmschrijving());
					monitoringOverview.setBedrijfActiefOmschrijving(EBedrijfUitvoeringStatus.NIET_ACTIEF.getOmschrijving());
				} else if(datumOntbinding != null) {
					monitoringOverview.setBedrijfActief(EBedrijfUitvoeringStatus.ONTBONDEN.getKorteOmschrijving());
					monitoringOverview.setBedrijfActiefOmschrijving(EBedrijfUitvoeringStatus.ONTBONDEN.getOmschrijving());
				} else if(datumOpheffing != null) {
					monitoringOverview.setBedrijfActief(EBedrijfUitvoeringStatus.OPGEHEVEN.getKorteOmschrijving());
					monitoringOverview.setBedrijfActiefOmschrijving(EBedrijfUitvoeringStatus.OPGEHEVEN.getOmschrijving());
				} else if(datumUitschrijving != null) {
					monitoringOverview.setBedrijfActief(EBedrijfUitvoeringStatus.UITGESCHREVEN.getKorteOmschrijving());
					monitoringOverview.setBedrijfActiefOmschrijving(EBedrijfUitvoeringStatus.UITGESCHREVEN.getOmschrijving());
				} else {
					monitoringOverview.setBedrijfActief(EBedrijfUitvoeringStatus.ACTIEF.getKorteOmschrijving());
					monitoringOverview.setBedrijfActiefOmschrijving(EBedrijfUitvoeringStatus.ACTIEF.getOmschrijving());
				}

				// faillissements / surseance status
				if(indFaillissement != null && indFaillissement.equalsIgnoreCase("J")) {
					if(indSurseance != null && indSurseance.equalsIgnoreCase("J")) {
						monitoringOverview.setFaillietSurseance(EBedrijfUitvoeringStatus.FAILLIET_SURSEANCE.getKorteOmschrijving());
						monitoringOverview.setFaillietSurseanceOmschrijving(EBedrijfUitvoeringStatus.FAILLIET_SURSEANCE.getOmschrijving());
					} else {
						monitoringOverview.setFaillietSurseance(EBedrijfUitvoeringStatus.FAILLIET.getKorteOmschrijving());
						monitoringOverview.setFaillietSurseanceOmschrijving(EBedrijfUitvoeringStatus.FAILLIET.getOmschrijving());
					}
				} else if(indSurseance != null && indSurseance.equalsIgnoreCase("J")) {
					monitoringOverview.setFaillietSurseance(EBedrijfUitvoeringStatus.SURSEANCE.getKorteOmschrijving());
					monitoringOverview.setFaillietSurseanceOmschrijving(EBedrijfUitvoeringStatus.SURSEANCE.getOmschrijving());
				} else {
					monitoringOverview.setFaillietSurseance("Nee");
					monitoringOverview.setFaillietSurseanceOmschrijving("Geen bijzonderheden");
				}

				//result.setMonitoring(monitoringOverview);

				// find all active meldingen of all companies related to bedrijf (by kvknummer)
				List<String> meldingCodes = EMeldingStatus.getActiefAndVerwijderd();
				List<Melding> meldingen = bedrijfDataService.findMeldingenOfBedrijf(monitoring.getBedrijfByMonitoringVanBedrijfIdBedrijfId(), meldingCodes);
				List<MeldingOverviewTransfer> meldingOverviews = new ArrayList<MeldingOverviewTransfer>();

				List<MeldingOverviewTransfer> meldingOverviewsConverted = ConvertUtil.convertMeldingenToMeldingenOverviewTransfer(meldingen, bedrijf.getBedrijfId());
				for(MeldingOverviewTransfer meldingoverview : meldingOverviewsConverted) {
					// Only active meldingen may be added with activation date and/or mutation date after monitoring date.
					//if (melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId().equals(doorBedrijfId)) {

					if(meldingoverview.getSbiOmschrijving() != null)
						meldingoverview.setSbiOmschrijving(bedrijfDataService.findByCode(meldingoverview.getSbiOmschrijving()).getOmschrijving());

					// MBR 13-10-2015 datum check uit: 'vanaf moment in monitoring ACT meldingen laten zien'
					// add all active meldingen
					if(meldingoverview.getDatumVerwijderd() == null) {
						//		&&
						//	((meldingoverview.getDatumGeaccordeerd() != null && monitoringOverview.getToegevoegd().compareTo(meldingoverview.getDatumGeaccordeerd()) <= 0) ||
						//		(meldingoverview.getDatumLaatsteMutatie() != null && monitoringOverview.getToegevoegd().compareTo(meldingoverview.getDatumLaatsteMutatie()) <= 0))) {

						meldingOverviews.add(meldingoverview);
						// don't add removed meldingen which were never activated + with date removed BEFORE monitoring date
					} else if(meldingoverview.getDatumGeaccordeerd() != null && meldingoverview.getDatumVerwijderd() != null && monitoringOverview.getToegevoegd().compareTo(meldingoverview.getDatumVerwijderd()) <= 0) {
						meldingOverviews.add(meldingoverview);
					}
				}

				result = new MonitoringDetailsTransfer(monitoringOverview, meldingOverviews);

				return result;
			} catch(DataServiceException e) {
				throw new ServiceException(e.getMessage());
			} catch(ServiceException e) {
				throw new ServiceException(e);
			}
		} else return null;
	}

	@Override
	@Transactional
	public Integer newRapportCounter(Integer bedrijfId) throws ServiceException {
		Integer rapportcounter = null;

		try {
			if(bedrijfId != null) {
				Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);

				if(bedrijf != null) {
					if(bedrijf.getRapportCounter() == null) {
						rapportcounter = new Integer(1);
						bedrijf.setRapportCounter(rapportcounter);
					} else {
						rapportcounter = new Integer(bedrijf.getRapportCounter() + 1);
						bedrijf.setRapportCounter(rapportcounter);
					}

					bedrijfDataService.save(bedrijf);
				}
			}
		} catch(Exception e) {
			throw new ServiceException("Cannot create/update rapport counter");
		}

		return rapportcounter;
	}

	@Override
	@Transactional
	public ErrorService rejectMelding(Integer meldingId, Integer bedrijfId, Integer gebruikerId) throws ServiceException {
		try {
			Melding melding = meldingDataService.findById(meldingId);

			if(melding != null && !EMeldingStatus.isRemoved(melding.getMeldingStatusCode())) {
				//Integer meldingDoorBedrijfId = null;
				//if (melding.getBedrijfByMeldingDoorBedrijfId() != null)
				//	meldingDoorBedrijfId = melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId();

				Integer meldingOverBedrijfId = null;
				if(melding.getBedrijfByMeldingOverBedrijfId() != null)
					meldingOverBedrijfId = melding.getBedrijfByMeldingOverBedrijfIdBedrijfId();


				if(meldingOverBedrijfId != null && meldingOverBedrijfId.equals(bedrijfId)) {
					melding.setGebruikerByVerwijderdDoorGebruikerIdGebruikerId(gebruikerId);
					melding.setMeldingStatusCode(EMeldingStatus.AFGEWEZEN.getCode());
					melding.setDatumVerwijderd(new Date());
					melding.setRedenVerwijderenMeldingCode(ERedenVerwijderenMelding.ADMIN_BEZWAAR.getCode());
					meldingDataService.save(melding);

					return null;
				} else return new ErrorService(ErrorService.CANNOT_REMOVE_MELDING);
			} else return new ErrorService(ErrorService.CANNOT_REMOVE_MELDING);
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot create notification: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService removeMelding(Integer meldingId, Integer bedrijfId, Integer bedrijfIdGerapporteerd, Integer gebruikerId, String redenVerwijderen, boolean sbdrUser, boolean createAlerts) throws ServiceException {
		try {
			Melding melding = meldingDataService.findById(meldingId);
			EMeldingStatus startStatus = EMeldingStatus.get(melding.getMeldingStatusCode());
			//Klant klantAccount = klantDataService.findKlantOfGebruikerByGebruikerId(gebruikerId);

			// Only active notifications of company or own notifications of user can be deleted
			// SBDR users always can delete notifications
			if(melding != null && (melding.getGebruikerByMeldingDoorGebruikerIdGebruikerId().equals(gebruikerId) || sbdrUser || gebruikerService.hasRightToDo(gebruikerId, bedrijfId, EBevoegdheid.HOOFD_OF_KLANT)) && !EMeldingStatus.isRemoved(melding.getMeldingStatusCode())) {
				Integer meldingDoorBedrijfId = null;
				if(melding.getBedrijfByMeldingDoorBedrijfId() != null)
					meldingDoorBedrijfId = melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId();

				Integer meldingOverBedrijfId = null;
				if(melding.getBedrijfByMeldingOverBedrijfId() != null)
					meldingOverBedrijfId = melding.getBedrijfByMeldingOverBedrijfIdBedrijfId();

				if(meldingOverBedrijfId != null && (meldingDoorBedrijfId.equals(bedrijfIdGerapporteerd) || sbdrUser) &&
						meldingOverBedrijfId.equals(bedrijfId))// only user from company which reported the notification or SBDR user may delete notification
				{
					melding.setGebruikerByVerwijderdDoorGebruikerIdGebruikerId(gebruikerId);
					melding.setMeldingStatusCode(EMeldingStatus.VERWIJDERD.getCode());
					melding.setRedenVerwijderenMeldingCode(ERedenVerwijderenMelding.getByFrontendCode(redenVerwijderen).getCode());
					melding.setDatumVerwijderd(new Date());
					melding = meldingDataService.save(melding);

					if(createAlerts) {
						//To company who made the notification, user level
						alertService.createMeldingAlert(melding.getMeldingId(), melding.getBedrijfByMeldingOverBedrijfIdBedrijfId(), melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId(), true, EAlertType.MELDING);

						//To company who was notified, company level
						// MBR 02-12-2015: 2nd param, getBedrijfByMeldingOverBedrijfIdBedrijfId changed into getBedrijfByMeldingDoorBedrijfIdBedrijfId
						//  CH 03-12-2015: bedrijfDoor and bedrijfOver switched, otherwise 2 alerts are sent to the same company, see the previous line(also creating an alert)
						//  CH 04-12-2015: commented this line, alerts are no longer sent to this company
						//alertService.createMeldingAlert(melding, melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId(), melding.getBedrijfByMeldingOverBedrijfIdBedrijfId(), false, EAlertType.MELDING);
					}

					Klant klant = bedrijfDataService.findAnyKlantRecordOfBedrijf(melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId());

					// MBR 18-01-2016: VOLDAAN (user/klant) moet weg. Versturen van email moet na NIET doneren plaatsvinden. Indien wel donatie, dan geen email
					if(ERedenVerwijderenMelding.getByFrontendCode(redenVerwijderen).getCode().equals(ERedenVerwijderenMelding.VOLDAAN.getCode()) || ERedenVerwijderenMelding.getByFrontendCode(redenVerwijderen).getCode().equals(ERedenVerwijderenMelding.ADMIN_VOLDAAN.getCode())) {
						emailService.sendNotificationIsPaidEmail(klant);
					} else {
						emailService.sendNotificationRemovedEmail(melding.getGebruikerByMeldingDoorGebruikerId(), melding.getBedrijfByMeldingOverBedrijfId(), melding);
					}

					//MBR 02-12-2015 no email on 'remove notification' action
					//If the notification was active before deletenion(so publicly available), send an update to the companies who have a monitor
					//					if(startStatus.getCode().equals(EMeldingStatus.ACTIEF.getCode())) {
					//						emailService.sendMonitoringChangedEmails(melding.getBedrijfByMeldingOverBedrijfId());
					//					}

					//To companies who have the notified company in monitoring, company level
					//for(Monitoring m : monitoringDataService.findMonitorsOfBedrijf(bedrijfId))
					//	alertService.createMeldingAlert(melding, bedrijfId, m.getBedrijfByMonitoringDoorBedrijfIdBedrijfId(), false, EAlertType.MONITORING);

					return null;
				} else return new ErrorService(ErrorService.CANNOT_REMOVE_MELDING);
			} else {
				if(melding != null && !melding.getGebruikerByMeldingDoorGebruikerIdGebruikerId().equals(gebruikerId)) {
					return new ErrorService(ErrorService.USER_CREATED_NOTIFICATION);
				} else {
					return new ErrorService(ErrorService.CANNOT_REMOVE_MELDING);
				}
			}
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot create notification: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService removeMonitoring(Integer monitoringId, Integer bedrijfId, Integer gebruikerId) throws ServiceException {
		try {
			Monitoring monitoring = monitoringDataService.findById(monitoringId);

			if(monitoring != null) {
				if(monitoring.getBedrijfByMonitoringVanBedrijfIdBedrijfId() != null && monitoring.getBedrijfByMonitoringVanBedrijfIdBedrijfId().equals(bedrijfId)) {
					monitoring.setGebruikerByVerwijderdDoorGebruikerIdGebruikerId(gebruikerId);
					monitoring.setMonitoringStatusCode(EMonitoringStatus.VERWIJDERD.getCode());
					monitoring.setDatumEinde(new Date());
					monitoringDataService.save(monitoring);

					// monitoring is abbo so remove not needed
					//productService.removeMonitoringOfBedrijfFromFactuur(bedrijfId);
					return null;
				} else return new ErrorService(ErrorService.CANNOT_REMOVE_MONITORING);
			} else return new ErrorService(ErrorService.CANNOT_REMOVE_MONITORING);
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot create notification: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void removeMonitorsOfBedrijf(Integer bedrijfId) throws ServiceException {
		try {
			List<Monitoring> monitors = monitoringDataService.findMonitorsOfBedrijf(bedrijfId);
			Gebruiker sbdrGebruiker = gebruikerDataService.findSbdrGebruiker();

			for(Monitoring m : monitors) {
				removeMonitoring(m.getMonitoringId(), m.getBedrijfByMonitoringVanBedrijfIdBedrijfId(), sbdrGebruiker.getGebruikerId());
			}

		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw new ServiceException("Cannot remove all monitors of bedrijf " + bedrijfId + ". Exception: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void resolveException(Integer customMeldingId, Integer bedrijfId) throws ServiceException {
		if(bedrijfId != null) {
			try {
				CustomMelding customMelding = customMeldingDataService.findByBedrijfId(customMeldingId, bedrijfId);

				if(customMelding != null) {
					customMelding.setDatumVerwerkt(new Date());

					customMelding.setCustomMeldingStatusCode(ECustomMeldingStatus.VERWERKT.getCode());

					customMeldingDataService.save(customMelding);
				}
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		}
	}

	/**
	 * Saves the presented {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object.
	 *
	 * @param bedrijf the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to save
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	@Override
	@Transactional
	public Bedrijf saveBedrijf(Bedrijf bedrijf) throws ServiceException {
		LOGGER.info("Method getBedrijf.");
		Bedrijf result = null;
		try {
			if(validateBedrijf(bedrijf).isValid()) {

				result = bedrijfDataService.save(bedrijf);

			} else {
				LOGGER.warn("Method getBedrijf. Bedrijf is ongeldig en kan niet opgeslagen worden.");
			}

		} catch(Exception e) {
			throw new ServiceException(e);
		}

		return result;
	}

	@Override
	@Transactional
	public Melding saveMelding(Melding melding) throws ServiceException {
		try {
			boolean isnew = false;

			if(melding.getMeldingId() == null) {
				isnew = true;
				melding.setDatumIngediend(new Date());
				melding.setOorspronkelijkBedrag(melding.getBedrag());

				if(melding.getMeldingStatusCode() == null) 
					melding.setMeldingStatusCode(EMeldingStatus.INBEHANDELING.getCode()); // TODO: set status on INI, create letter, set status on ACT, monitor it for 30 days, after that

				// Create new reference number for notification
				String referentie = SerialNumber.generateRandomSerialNumber8_32();
				while(meldingDataService.findByReferentieNummerIntern(referentie) != null) {
					referentie = SerialNumber.generateRandomSerialNumber8_32();
				}
				melding.setReferentieNummerIntern(referentie);
			} else {
				Melding bestaandeMelding = meldingDataService.findById(melding.getMeldingId());
				if(melding.getGebruikerByGeaccordeerdDoorGebruikerId() != null)
					bestaandeMelding.setGebruikerByGeaccordeerdDoorGebruikerIdGebruikerId(melding.getGebruikerByGeaccordeerdDoorGebruikerIdGebruikerId());
				if(melding.getGebruikerByMeldingDoorGebruikerId() != null)
					bestaandeMelding.setGebruikerByMeldingDoorGebruikerIdGebruikerId(melding.getGebruikerByMeldingDoorGebruikerIdGebruikerId());
				if(melding.getGebruikerByVerwijderdDoorGebruikerIdGebruikerId() != null)
					bestaandeMelding.setGebruikerByVerwijderdDoorGebruikerIdGebruikerId(melding.getGebruikerByVerwijderdDoorGebruikerIdGebruikerId());
				if(melding.getGebruikerByLaatsteMutatieDoorGebruikerIdGebruikerId() != null)
					bestaandeMelding.setGebruikerByLaatsteMutatieDoorGebruikerIdGebruikerId(melding.getGebruikerByLaatsteMutatieDoorGebruikerIdGebruikerId());
				if(melding.getDatumGeaccordeerd() != null)
					bestaandeMelding.setDatumGeaccordeerd(melding.getDatumGeaccordeerd());
				if(melding.getDatumLaatsteMutatie() != null)
					bestaandeMelding.setDatumLaatsteMutatie(melding.getDatumLaatsteMutatie());
		
				if(melding.getDatumVerloopFactuur() != null)
					bestaandeMelding.setDatumVerloopFactuur(melding.getDatumVerloopFactuur());
				if(melding.getBedrag() != null) bestaandeMelding.setBedrag(melding.getBedrag());

				// update referentienummer
				bestaandeMelding.setReferentieNummer(melding.getReferentieNummer());

				bestaandeMelding.setDoorBedrijfWeergeven(melding.isDoorBedrijfWeergeven());
				bestaandeMelding.setBedragWeergeven(melding.isBedragWeergeven());

				// Issue with datum ingediend on existing meldingen
				//bestaandeMelding.setDatumIngediend(melding.getDatumIngediend());
				if(melding.getDatumVerwijderd() != null)
					bestaandeMelding.setDatumVerwijderd(melding.getDatumVerwijderd()); // this may not be filled!

				if(melding.getMeldingStatusCode() != null)
					bestaandeMelding.setMeldingStatusCode(melding.getMeldingStatusCode());
				else {
					// this may not happen on existing Melding!
					bestaandeMelding.setMeldingStatusCode(EMeldingStatus.INBEHANDELING.getCode());    // TODO: set status on INI, create letter, set status on ACT, monitor it for 30 days, after that
				}
				
				bestaandeMelding.setTelefoonNummerDebiteur(melding.getTelefoonNummerDebiteur());
				bestaandeMelding.setEmailAdresDebiteur(melding.getEmailAdresDebiteur());

				melding = bestaandeMelding;
			}

			Melding newMelding = meldingDataService.save(melding);

			Gebruiker gebruiker = gebruikerDataService.findById(newMelding.getGebruikerByMeldingDoorGebruikerIdGebruikerId());
			Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(newMelding.getBedrijfByMeldingOverBedrijfIdBedrijfId());

			if(isnew && newMelding.getMeldingStatusCode().equals(EMeldingStatus.INBEHANDELING.getCode())) {
				//				Klant klant = null;
				//				klant = klantDataService.findKlantByIdAndStatus(newMelding.getGebruikerByMeldingDoorGebruikerIdGebruikerId());
				//				if(klant != null) {
				//					emailService.sendNotificationNotInProcessEmail(gebruiker, bedrijf);
				//				} else {
				//					emailService.sendNotificationInProcessEmail(gebruiker, bedrijf);
				//				}

			} else if(newMelding.getMeldingStatusCode().equals(EMeldingStatus.DATA_NOK.getCode())) {
				// Update bedrijf to not ok
				//Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(newMelding.getBedrijfByMeldingOverBedrijfIdBedrijfId());

				bedrijf.setAdresOk(false);

				bedrijfDataService.save(bedrijf);
			}

			return newMelding;
		} catch(DataServiceException e) {
			throw new ServiceException("Cannot create notification: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void saveWebsiteparam(WebsiteparamTransfer websiteparamTransfer) throws ServiceException {
		if(websiteparamTransfer != null) {
			Websiteparam websiteparam = null;

			try {
				websiteparam = websiteparamDataService.find();
			} catch(DataServiceException e) {
				// do nothing
			}
			if(websiteparam == null) websiteparam = new Websiteparam();

			websiteparam.setStoringen(websiteparamTransfer.getStoringen());
			websiteparam.setStartupsYtd(websiteparamTransfer.getStartupsYtd());
			websiteparam.setStartupsWeek(websiteparamTransfer.getStartupsWeek());
			websiteparam.setVermeldingenYtd(websiteparamTransfer.getVermeldingenYtd());
			websiteparam.setVermeldingenWeek(websiteparamTransfer.getVermeldingenWeek());

			try {
				websiteparamDataService.save(websiteparam);
			} catch(DataServiceException e) {
				throw new ServiceException("Cannot save websiteparam: " + e.getMessage());
			}
		}
	}

	@Override
	@Transactional
	public void updateMeldingenBriefStatus(List<Melding> meldingen, EBriefStatus status) throws ServiceException {
		try {
			for(Melding melding : meldingen) {
				melding.setBriefStatusCode(status.getCode());
				meldingDataService.save(melding);
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public ErrorService updateMonitoring(Integer vanBedrijfId, Integer doorBedrijfId, Integer gebruikerId) throws ServiceException {
		ErrorService error = null;

		if(vanBedrijfId != null && doorBedrijfId != null) {
			Monitoring monitoring;
			try {
				monitoring = monitoringDataService.findActiveMonitoringOfBedrijfByBedrijf(doorBedrijfId, vanBedrijfId);

				if(monitoring != null) {
					monitoring.setDatumLaatsteCheck(new Date());
					monitoring.setGebruikerByLaatsteMutatieDoorGebruikerIdGebruikerId(gebruikerId);
					monitoringDataService.save(monitoring);
				} else error = new ErrorService(ErrorService.CANNOT_SAVE_MONITORING);
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else error = new ErrorService(ErrorService.CANNOT_SAVE_MONITORING);

		return error;
	}

	@Override
	public ErrorService validateMeldingData(MeldingTransfer meldingTransfer) throws ServiceException {
		ErrorService result = null;

		try {
			if(meldingTransfer != null) {
				if(meldingTransfer.getReferentie() != null) {
					List<Melding> melding = meldingDataService.findByReferentieNummer(meldingTransfer.getMeldingId(), meldingTransfer.getBedrijfIdGerapporteerd(), meldingTransfer.getReferentie());

					if(melding != null && ((meldingTransfer.getMeldingId() != null && melding.size() > 1) || (meldingTransfer.getMeldingId() != null &&meldingTransfer.getMeldingId() == 0 && melding.size() > 0)))
						result = new ErrorService(ErrorService.NOTIFICATIONREFERENCE_ALREADY_EXISTS);
				}

				if(meldingTransfer.getMeldingstatusCode() != null && meldingTransfer.getMeldingstatusCode().equals(EMeldingStatus.ACTIEF.getCode())) {
					Melding bestaandeMelding = meldingDataService.findById(meldingTransfer.getMeldingId());

					if(bestaandeMelding != null) {
						if(bestaandeMelding.isDoorBedrijfWeergeven() != meldingTransfer.isDoorBedrijfWeergeven() || bestaandeMelding.isBedragWeergeven() != meldingTransfer.isBedragWeergeven()) {
							result = new ErrorService(ErrorService.CANNOT_SAVE_MELDING);
						}
					}
				}
			}

			return result;
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	private List<MonitoringOverviewTransfer> addHeeftMeldingProperty_MonitoringOverviewTransfer(Integer bedrijfId, List<MonitoringOverviewTransfer> monitoringTransfers) throws ServiceException {
		if(monitoringTransfers != null) {
			try {
				List<Integer> bedrijvenIds = new ArrayList<Integer>();
				for(MonitoringOverviewTransfer monitoringTransfer : monitoringTransfers) {
					if(monitoringTransfer.getBedrijfId() != null) bedrijvenIds.add(monitoringTransfer.getBedrijfId());
				}

				if(bedrijvenIds.size() > 0) {
					List<Bedrijf> meldingbedrijven = bedrijfDataService.findMatchingBedrijvenMetMeldingByBedrijfFromBedrijvenList(bedrijfId, bedrijvenIds);

					for(MonitoringOverviewTransfer monitoringTransfer : monitoringTransfers) {
						for(Bedrijf meldingbedrijf : meldingbedrijven)
							if(monitoringTransfer.getBedrijfId() != null && monitoringTransfer.getBedrijfId().equals(meldingbedrijf.getBedrijfId())) {
								monitoringTransfer.setMeldingBijBedrijf(true);
								break;
							}
					}
				}
			} catch(DataServiceException e) {
				throw new ServiceException("Cannot enrich BedrijfTransfer with properties");
			}
		}

		return monitoringTransfers;
	}

	private List<MonitoringOverviewTransfer> addHeeftMonitoringProperty_MonitoringOverviewTransfer(Integer bedrijfId, List<MonitoringOverviewTransfer> monitoringTransfers) throws ServiceException {
		if(monitoringTransfers != null) {
			try {
				List<Integer> bedrijvenIds = new ArrayList<Integer>();
				for(MonitoringOverviewTransfer monitoringTransfer : monitoringTransfers) {
					if(monitoringTransfer.getBedrijfId() != null) bedrijvenIds.add(monitoringTransfer.getBedrijfId());
				}

				if(bedrijvenIds.size() > 0) {
					List<Bedrijf> meldingbedrijven = bedrijfDataService.findMatchingBedrijvenMetMonitoringOfBedrijfFromBedrijvenList(bedrijfId, bedrijvenIds);

					for(MonitoringOverviewTransfer monitoringTransfer : monitoringTransfers) {
						for(Bedrijf meldingbedrijf : meldingbedrijven)
							if(monitoringTransfer.getBedrijfId() != null && monitoringTransfer.getBedrijfId().equals(meldingbedrijf.getBedrijfId())) {
								monitoringTransfer.setMonitoringBijBedrijf(true);
								break;
							}
					}
				}
			} catch(DataServiceException e) {
				throw new ServiceException("Cannot enrich BedrijfTransfer with properties");
			}
		}

		return monitoringTransfers;
	}

	private void createMeldingenBriefForBedrijf(Bedrijf bedrijf, Date iterationDate) throws ServiceException {
		if(bedrijf == null) {throw new ServiceException("bedrijf cannot be null");} else {
			try {

				List<Bedrijf> vermeldeBedrijven = bedrijfDataService.findnewNotifiedCompaniesOfCompany(bedrijf.getBedrijfId());

				if(vermeldeBedrijven == null) {
					throw new ServiceException("Lijst met vermelde bedrijven moet bestaan");
				}

				for(Bedrijf vermeldBedrijf : vermeldeBedrijven) {
					List<Melding> meldingen = meldingDataService.findNewMeldingenOfBedrijfAboutBedrijf(bedrijf.getBedrijfId(), vermeldBedrijf.getBedrijfId());

					if(meldingen == null) {throw new ServiceException("Lijst met meldingen moet bestaan");}

					if(meldingen.size() > 0) {						
						documentService.createMeldingLetter(bedrijf.getBedrijfId(), vermeldBedrijf.getBedrijfId(), meldingen, iterationDate);
						
						Gebruiker sysUser = gebruikerDataService.findSysteemGebruiker();
						
						for (Melding melding : meldingen) {
							ContactMomentTransfer contactMoment = new ContactMomentTransfer();
							contactMoment.setBeantwoord("Nvt");
							contactMoment.setContactWijze("Vermeldingsbrief");
							contactMoment.setDatumContact(new Date());
							contactMoment.setGebruikerId(sysUser.getGebruikerId());
							contactMoment.setMeldingId(melding.getMeldingId());
							Notitie notitie = new Notitie();
							notitie.setDatum(new Date());
							notitie.setGebruikerGebruikerId(sysUser.getGebruikerId());
							notitie.setNotitie("Vermeldingsbrief is verstuurd.");
							notitie.setNotitieType(ENotitieType.CONTACT_MOMENT.getCode());
							saveContactMomentNotification(contactMoment, notitie);
						}
					}
				}
			} catch(DataServiceException e) {throw new ServiceException(e);}
		}
	}

	private List<ChartDataTransfer> createMeldingenChartData(List<MeldingOverviewTransfer> meldingenoverview) {
		List<ChartDataTransfer> result = new ArrayList<ChartDataTransfer>();

		Date now = ConvertUtil.dateWithoutTime(null);

		// create chart items
		for(int i = -12; i <= 0; i++) {
			Calendar meldingenDate = Calendar.getInstance();
			meldingenDate.setTime(now);
			meldingenDate.set(Calendar.DAY_OF_MONTH, 1);
			meldingenDate.add(Calendar.MONTH, i);

			String colKey = ConvertUtil.monthYearFromTime(meldingenDate.getTime());

			ChartDataTransfer chartData = new ChartDataTransfer(meldingenDate.getTime(), 0, colKey);
			result.add(chartData);
		}

		for(MeldingOverviewTransfer melding : meldingenoverview) {
			if(melding.getStatusCode().equals(EMeldingStatus.ACTIEF.getCode())) {
				for(ChartDataTransfer meldingenData : result) {
					Date reportChartDate = new Date(meldingenData.getDatum());
					Calendar nextMonth = Calendar.getInstance();
					nextMonth.setTime(reportChartDate);
					nextMonth.add(Calendar.MONTH, 1);

					// MBR 24022017: Verloopdatum factuur ipv vermeldingsdatum
					//Date meldingDateWithoutTime = ConvertUtil.dateWithoutTime(melding.getDatumGeaccordeerd());
					Date meldingDateWithoutTime = ConvertUtil.dateWithoutTime(melding.getVerloopdatumFactuur());

					//if((meldingDateWithoutTime.after(reportChartDate) || meldingDateWithoutTime.equals(reportChartDate)) && meldingDateWithoutTime.before(nextMonth.getTime()))
					if(meldingDateWithoutTime.before(reportChartDate) || meldingDateWithoutTime.equals(reportChartDate))
						meldingenData.setAantal(meldingenData.getAantal() + 1);
				}
			}
		}

		return result;
	}

	/**
	 * Checks the validity of a {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object.<br/>
	 * <p/>
	 * A Bedrijf Object must apply to the following rules:<br/>
	 * <ol>
	 * <li> The field KvKNummer must contain a value.</li>
	 * </ol>
	 *
	 * @param bedrijf the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object that must be validated.
	 * @return a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
	 */
	// ToDo (JME 29-07-2014) Expand this function with additional Business logic
	private ValidationObject validateBedrijf(Bedrijf bedrijf) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(bedrijf != null) {

				// 1. Check KvKNummer
				if(StringUtil.isEmptyOrNull(bedrijf.getKvKnummer())) {
					loValidation.addMessage(bedrijf.getKvKnummer(), "KvKNummer moet gevuld zijn.", ValidationConstants.MessageType.INVALID);
				}
			} else {
				loValidation.addMessage("Geen Bedrijf meegegeven.", ValidationConstants.MessageType.INVALID);
				LOGGER.info("Method validateBedrijf. No Bedrijf recieved.");
			}

			LOGGER.info("Method validateBedrijf. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
			LOGGER.error("Method validateBedrijf. Validation failed.");
		}
		
		return loValidation;
	}
	
	@Override 
	@Transactional
	public Notitie saveNotitie(Notitie notitie, Integer meldingId) throws ServiceException {
		Notitie result = null;
		
		if (notitie != null) {
			try {
				Notitie nieuwNotitie = null;
				
				if (notitie != null) {
					if (notitie.getNotitieId() != null) {
						nieuwNotitie = notitieDataService.findById(notitie.getNotitieId());
					} else {
						nieuwNotitie = new Notitie();
					}
					
					Melding melding = null;
					if (meldingId != null) {
						melding = meldingDataService.findById(meldingId);
					} else {
						throw new ServiceException("Cannot save notitie, no existing Melding for update.");	
					}
						
					
					nieuwNotitie.setDatum(notitie.getDatum());
					nieuwNotitie.setGebruikerGebruikerId(notitie.getGebruikerId());
					nieuwNotitie.setNotitie(notitie.getNotitie());
					nieuwNotitie.setNotitieType(notitie.getNotitieType());
					
					result = notitieDataService.save(nieuwNotitie);
					
					if (melding != null) {
						if (notitie.getNotitieType().equals(ENotitieType.MELDING_GEBRUIKER.getCode()))
							melding.setNotitieGebruikerId(result.getNotitieId());
						else if (notitie.getNotitieType().equals(ENotitieType.MELDING_ADMIN.getCode()))
							melding.setNotitieAdminId(result.getNotitieId());
						
						meldingDataService.save(melding);
					}
					
				} 					
			} catch (Exception e) {
				throw new ServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("Cannot update notitie, no existing notitie to update.");
			throw new ServiceException("Cannot save notitie, no existing notitie for update.");
		}
		
		return result;
	}
	
	@Override
	@Transactional
	public ContactMoment saveContactMomentNotification(ContactMomentTransfer contactMoment, Notitie notitie) throws ServiceException {
		ContactMoment result = null;
		
		if (contactMoment != null && contactMoment.getMeldingId() != null) {
			try {
				ContactMoment nieuwContactMoment = null;
				
				if (contactMoment.getContactMomentId() != null) {
					nieuwContactMoment = contactMomentDataService.findById(contactMoment.getContactMomentId());
				} else {
					nieuwContactMoment = new ContactMoment();
				}
				
				Melding melding = meldingDataService.findById(contactMoment.getMeldingId());
						
				if (nieuwContactMoment != null && melding != null) {
					Notitie savedNotitie = null;
					
					if (notitie != null) {
						savedNotitie = saveNotitie(notitie, melding.getMeldingId());
						
						if (savedNotitie != null)
							nieuwContactMoment.setNotitie(savedNotitie);
						else {
							LOGGER.error("Notitie of contactMoment not saved due error.");
							throw new ServiceException("Notitie of contactMoment not save due error.");
						}
					} else
						nieuwContactMoment.setNotitie(null);
					
					nieuwContactMoment.setBeantwoord(contactMoment.getBeantwoord());
					nieuwContactMoment.setContactWijze(contactMoment.getContactWijze());
					nieuwContactMoment.setDatumContact(contactMoment.getDatumContact());
					nieuwContactMoment.setDatumContactTerug(contactMoment.getDatumContactTerug());
					nieuwContactMoment.setGebruikerGebruikerId(contactMoment.getGebruikerId());
					nieuwContactMoment.setNotitieIntern(contactMoment.getNotitieIntern());
					nieuwContactMoment.setMeldingMeldingId(contactMoment.getMeldingId());
					nieuwContactMoment.setGebruikerGebruikerId(contactMoment.getGebruikerId());
					nieuwContactMoment.setContactGegevens(contactMoment.getContactGegevens());
					
					result = contactMomentDataService.save(nieuwContactMoment);	
					
					// if alert create alerts
					if (contactMoment.getAlert() != null && contactMoment.getAlert()) {
						alertService.createContactMomentNotificationAlert(melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId());
					}
				} else {
					LOGGER.error("Cannot update contactMoment, no existing contactMoment to update.");
					throw new ServiceException("Cannot save contactMoment, no existing contactMoment for update.");
				}
			} catch(Exception e) {
				throw new ServiceException(e.getMessage());
			}
		}
		
		return result;
	}
	
	@Override
	public ContactMomentTransfer getContactMomentNotificationData(Integer contactMomentId) throws ServiceException {
		ContactMomentTransfer result = null;
		
		try {
			if (contactMomentId != null) {
				ContactMoment contactMoment = contactMomentDataService.findById(contactMomentId);
				Notitie notitie = null;
				if (contactMoment != null) {
					if (contactMoment.getNotitieId() != null)
						notitie = notitieDataService.findById(contactMoment.getNotitieId());
					
					Object[] contactMomentAndNotitie = new Object[2];
					contactMomentAndNotitie[0] = contactMoment;
					contactMomentAndNotitie[1] = notitie;
					
					result = ConvertUtil.convertToContactMomentTransfer(contactMomentAndNotitie);
				}
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		
		return result;
	}
	
	@Override
	public NotitieTransfer getNotitieAdminData(Integer meldingId) throws ServiceException {
		NotitieTransfer result = null;
		
		try {
			if (meldingId != null) {
				Notitie notitie = notitieDataService.findByNotitieMeldingGebruiker(meldingId, null, ENotitieType.MELDING_ADMIN.getCode());
				
				if (notitie != null) {
					result = new NotitieTransfer();
					result.setDatum(notitie.getDatum());
					result.setGebruikerId(notitie.getGebruikerId());
					result.setMeldingId(meldingId);
					result.setNotitie(notitie.getNotitie());
					result.setNotitieType(notitie.getNotitieType());
				}
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		return result;
	}
	
	@Override
	public List<ContactMomentTransfer> findContactMomentsOfNotification(Integer meldingId, Integer userId) throws ServiceException {
		List<ContactMomentTransfer> results = null;
		
		try {
			if (meldingId != null) {
				List<Object[]> contactMoments = contactMomentDataService.findAllOfNotification(meldingId);
				results = ConvertUtil.convertToContactMomentTranfers(contactMoments);
			}
		} catch (DataServiceException e) {
			throw new ServiceException(e.getMessage());
		}
		
		return results;
	}
	
	@Override
	@Transactional
	public ErrorService removeContactMomentNotification(Integer contactMomentId, Integer bedrijfId, Integer userId) throws ServiceException {
		try {
			if (contactMomentId != null) {
				contactMomentDataService.delete(contactMomentId);
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}

		return null;
	}
}