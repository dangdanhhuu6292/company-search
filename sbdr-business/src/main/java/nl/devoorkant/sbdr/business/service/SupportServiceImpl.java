package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.job.AssignExpiredObjectionsToAdminJob;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.transfer.SupportBestandTransfer;
import nl.devoorkant.sbdr.business.transfer.SupportTransfer;
import nl.devoorkant.sbdr.business.util.*;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Alert;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.model.Support;
import nl.devoorkant.sbdr.data.model.SupportBestand;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.EAlertType;
import nl.devoorkant.sbdr.data.util.ERedenVerwijderenMelding;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationObject;

import org.joda.time.DateTime;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.Files;

import org.springframework.transaction.annotation.Propagation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

/**
 * Stateless service bean with functionality for Support.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Coen Hasselaar
 * @version %I%1
 */

@Service("supportService")
@Transactional(readOnly = true)
public class SupportServiceImpl implements SupportService {

	@Autowired
	Scheduler scheduler;
	
	@Value("${job.cron.assign_expired_objections_to_admin}")
	String cronExpression;
	
	@Autowired
	private AlertDataService alertDataService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private BedrijfDataService bedrijfDataService;

	@Autowired
	private BedrijfService bedrijfService;
	
	@Autowired
	private GebruikerService gebruikerService;

	@Autowired
	private EmailService emailService;

	@Value("${expired_objections_days}")
	private int expiredObjectionsDays;

	@Autowired
	private GebruikerDataService gebruikerDataService;

	@Autowired
	private KlantDataService klantDataService;

	@Autowired
	private MeldingDataService meldingDataService;

	@Autowired
	private SupportDataService supportDataService;

	private static final Logger LOGGER = LoggerFactory.getLogger(BedrijfServiceImpl.class);

	@PostConstruct
	public void createSchedule() {
		JobDetail jobDetail = buildJobDetail();
        Trigger trigger = buildJobTrigger(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start support schedules", e);
		}		
	}
	
	private JobDetail buildJobDetail() {

        return JobBuilder.newJob(AssignExpiredObjectionsToAdminJob.class)
                .withIdentity(UUID.randomUUID().toString(), "support-jobs")
                .withDescription("Assign expired objections to admin")
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "support")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)) // "0 0/2 8-17 * * ?"
    			  .build();    	
    }
    
	@Override
	@Transactional
	public ErrorService archiveAllSupportTicketsOfBedrijf(Integer bedrijfId) throws ServiceException {
		if(bedrijfId != null) {
			try {
				List<Support> sl = supportDataService.findAllSupportTicketsByBedrijfId(bedrijfId);

				Gebruiker systemUser = gebruikerDataService.findSysteemGebruiker();

				for(Support s : sl) {
					// invalidate/delete alert if any is related to this support ticket
					if (s.getAlerts() != null) {
						for (Alert a : s.getAlerts()) {
							alertDataService.delete(a);
							s.getAlerts().remove(a);
						}
					}
					s.setGebruikerByGeslotenDoorGebruikerIdGebruikerId(systemUser.getGebruikerId());
					s.setDatumUpdate(new Date());
					s.setSupportStatusCode(ESupportStatus.GEARCHIVEERD.getCode());
					supportDataService.saveSupportTicket(s);
				}

				return null;
			} catch(DataServiceException e) {
				LOGGER.error("Error in archiveAllSupportTicketsOfBedrijf: " + e.getMessage());
				throw new ServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("Method archiveAllSupportTicketsOfBedrijf: parameter is null");
			return new ErrorService(ErrorService.PARAMETER_IS_EMPTY);
		}
	}

	@Override
	@Transactional
	public void assignExpiredObjectionsToAdmin() throws ServiceException {
		List<Support> sl;

		try {
			sl = supportDataService.findExpiredObjectionSupportTickets(expiredObjectionsDays);

			for(Support s : sl) {

				s.setSupportStatusCode(ESupportStatus.GESLOTEN.getCode());
				s.setDatumUpdate(new Date());
				Support savedS = supportDataService.saveSupportTicket(s);

				//send alert to party who made the support ticket if it doesn't exist already
				alertService.createSupportAlert(savedS, savedS.getGebruikerByGebruikerId().getBedrijfBedrijfId());

				//send alert to party who made the initial notification if it doesn't exist already
				alertService.createSupportAlert(savedS, savedS.getMelding().getBedrijfByMeldingDoorBedrijfIdBedrijfId());

				//send alert to admin if it doesn't exist already
				alertService.createSupportAlert(savedS, bedrijfDataService.findSbdr().getBedrijfId());
			}
		} catch(DataServiceException e) {
			LOGGER.error("Error in assignObjectionsToAdmin: " + e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService deleteSupportTicketAndBestanden(Integer sId) throws ServiceException {
		if(sId != null) {
			try {
				//Remove the dependent objects first, this includes the alert and possible files, if they exist
				if(alertDataService.checkIfAlertWithSupportIdExists(sId)) {
					Support s = supportDataService.findSupportTicketBySupportId(sId);
					alertDataService.deleteAlertBySupportId(sId, s.getBedrijfBedrijfId());
				}

				if(supportDataService.checkIfSupportBestandWithSupportIdExists(sId))
					supportDataService.deleteSupportTicketBestandenBySupportId(sId);

				//Then remove the support ticket itself
				if(supportDataService.checkIfSupportExists(sId)) supportDataService.deleteSupportTicket(sId);

				return null;
			} catch(DataServiceException e) {
				LOGGER.error("Method deleteSupportTicketAndBestanden: " + e.getMessage());
				throw new ServiceException(e);
			}
		} else {
			LOGGER.error("Method deleteSupportTicketAndBestanden: Parameter is null");
			return new ErrorService(ErrorService.GENERAL_FAILURE);
		}
	}

	@Override
	@Transactional
	public ErrorService assignAllSupportTicketsOfGebruikerToKlant(Integer gebruikerId, Integer bedrijfId) throws ServiceException {
		if(gebruikerId != null) {
			try {
				Gebruiker gebruiker = gebruikerDataService.findById(gebruikerId);
				Klant klant = null;
				
				if (gebruiker != null) {
					klant = klantDataService.findKlantOfGebruikerByGebruikerId(gebruiker.getGebruikerId(), bedrijfId);
					if (klant == null) {
						klant = klantDataService.findKlantOfBedrijfByBedrijfId(bedrijfId);
					}
				}
				
				if (klant != null) {
					// find all tickets of user
					List<Support> supports = supportDataService.findAllSupportTicketsByGebruikerId(gebruiker.getGebruikerId());
					
					for (Support support : supports) {
						// Old condition that excludes tickets with status Gearchiveerd
						//if (support != null && !support.getSupportStatus().getCode().equals(ESupportStatus.GEARCHIVEERD.getCode())) {

						// Also assign closed tickets to klant + only support tickets of bedrijf
						if(support!=null && support.getBedrijfBedrijfId().equals(bedrijfId)){
							// change owner of ticket to Klant user
							support.setGebruikerByGebruikerId(klant);
							supportDataService.saveSupportTicket(support);
						}
					}
					
					return null;
				} else {
					LOGGER.error("Method assignAllOpenSupportTicketsOfGebruikerToKlant: Klant is null");
					return new ErrorService(ErrorService.GENERAL_FAILURE);
				}
					
			} catch(DataServiceException e) {
				LOGGER.error("Method assignAllOpenSupportTicketsOfGebruikerToKlant: " + e.getMessage());
				throw new ServiceException(e);
			}
		} else {
			LOGGER.error("Method assignAllOpenSupportTicketsOfGebruikerToKlant: Parameter is null");
			return new ErrorService(ErrorService.GENERAL_FAILURE);
		}
	}	

	@Override
	@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public SupportBestand findSupportTicketBestandBySupportBestandId(Integer sBId) throws ServiceException {
		if(sBId != null) {
			try {
				return supportDataService.findSupportTicketBestandBySupportBestandId(sBId);
			} catch(DataServiceException e) {
				LOGGER.error("Method findSupportTicketBestandBySupportBestandId: " + e.getMessage());
				throw new ServiceException(e);
			}
		} else {
			LOGGER.error("Method findSupportTicketBestandBySupportBestandId: parameter is null");
			throw new ServiceException(ErrorService.GENERAL_FAILURE);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<SupportTransfer> findSupportTicketsAboutBedrijfByBedrijfId(Integer bId, Pageable p) throws ServiceException {
		if(bId != null && p != null) {
			try {
				return ConvertUtil.convertSupportPageToSupportPageTransfer(supportDataService.findSupportTicketsAboutBedrijfByBedrijfId(bId, p));
			} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
				LOGGER.error("Method findSupportTicketsAboutBedrijfByBedrijfId: " + e.getMessage());
				throw new ServiceException(e);
			}
		} else {
			LOGGER.error("Method findSupportTicketsAboutBedrijfByBedrijfId: parameters are null");
			throw new ServiceException(ErrorService.GENERAL_FAILURE);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<SupportTransfer> findSupportTicketsByGebruikerId(Integer gId, Pageable p) throws ServiceException {
		if(gId != null && p != null) {
			try {
				return ConvertUtil.convertObjectPageToSupportPageTransfer(supportDataService.findSupportTicketsByGebruikerId(gId, p));
			} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
				LOGGER.error("Method findSupportTicketsByGebruikerId: " + e.getMessage());
				throw new ServiceException(e);
			}
		} else {
			LOGGER.error("Method findSupportTicketsByGebruikerId: parameters are null");
			throw new ServiceException(ErrorService.GENERAL_FAILURE);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<SupportTransfer> findSupportTicketsByReferentieNummer(String ref) throws ServiceException {
		if(ref != null && !ref.isEmpty()) {
			List<SupportTransfer> sTl = new ArrayList<>();
			List<Support> sl;

			try {
				sl = supportDataService.findSupportTicketsByReferentieNummer(ref);
			} catch(DataServiceException e) {
				LOGGER.error("Method findSupportTicketsByReferentieNummer: " + e.getMessage());
				throw new ServiceException(e);
			}

			for(Support s : sl) {
				SupportTransfer sT = ConvertUtil.createSupportTransferFromSupport(s);
				sTl.add(sT);
			}

			return sTl;
		} else {
			LOGGER.error("Method findSupportTicketsByReferentieNummer: parameters are null/empty");
			throw new ServiceException(ErrorService.GENERAL_FAILURE);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageTransfer<SupportTransfer> findSupportTicketsBySupportType(String type, Pageable p) throws ServiceException {
		if(type != null && ESupportType.get(type) != null && p != null) {
			try {
				return ConvertUtil.convertSupportPageToSupportPageTransfer(supportDataService.findSupportTicketsBySupportType(type, p));
			} catch(DataServiceException | IllegalAccessException | InvocationTargetException e) {
				LOGGER.error("Method findSupportTicketsBySupportType: " + e.getMessage());
				throw new ServiceException(e);
			}
		} else {
			LOGGER.error("Method findSupportTicketsBySupportType: parameters are null");
			throw new ServiceException(ErrorService.GENERAL_FAILURE);
		}
	}

	@Override
	@Transactional
	public void pickUpSupportTicket(String refNo, Integer gebruikerId) throws ServiceException {
		if(refNo != null && !refNo.isEmpty() && gebruikerId != null) {
			try {
				List<Support> supportChain = supportDataService.findSupportTicketsByReferentieNummer(refNo);

				if(supportChain.size() != 0) {
					for(Support s : supportChain) {
						//Set the correct status
						s.setSupportStatusCode(ESupportStatus.IN_BEHANDELING.getCode());

						//And since we need to keep track of who picked up the ticket, set the geslotenDoorGebruiker_ID field
						s.setGebruikerByGeslotenDoorGebruikerIdGebruikerId(gebruikerId);

						//One or more fields are updated, so the DatumUpdate field has to be filled in
						s.setDatumUpdate(new Date());

						supportDataService.saveSupportTicket(s);
					}

					Support sC1 = supportChain.get(0);

					//When a support ticket is picked up, remove the related alert
					alertDataService.deleteAlertBySupportId(sC1.getSupportId(), sC1.getBedrijfBedrijfId());
				}
			} catch(DataServiceException e) {
				LOGGER.error("Method pickUpSupportTicket: " + e.getMessage());
				throw new ServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("Method pickUpSupportTicket: parameters are null");
			throw new ServiceException(ErrorService.GENERAL_FAILURE);
		}
	}

	@Override
	@Transactional
	public SupportTransfer saveSupportTicket(Support s, Boolean activateNotification) throws ServiceException {
		//INFO: s=support, p=parent(parent of s), gP = grandparent(parent of p, parent of parent of s)

		if(s != null) {
			try {
				Date dateNow = new Date();

				//Datum invullen
				s.setDatumAangemaakt(dateNow);

				//De reden moet null zijn of ingevuld, leeg is geen legitieme waarde
				if(s.getSupportRedenCode().isEmpty()) s.setSupportReden(null);

				Integer bId = bedrijfService.findByKlantGebruikersId(s.getGebruikerByGebruikerIdGebruikerId()).getBedrijfId();
				Integer sbdrId = bedrijfDataService.findSbdr().getBedrijfId();

				if(s.getSupport() != null) {
					//<editor-fold desc="Flows reacties">

					//Info van parent ophalen
					Support p = supportDataService.findSupportTicketBySupportId(s.getSupportSupportId());
					Gebruiker g = gebruikerService.findByGebruikerId(p.getGebruikerByGebruikerIdGebruikerId());
					Integer pBId = null;
					if (g != null)
						pBId = g.getBedrijfBedrijfId();
					else // cannot find bedrijf of gebruiker, so throw error
						throw new ServiceException(ErrorService.CANNOT_SAVE_SUPPORT);

					//Fill in the rest of the parent
					s.setSupport(p);

					//Referentie is hetzelfde voor child en parent
					s.setReferentieNummer(p.getReferentieNummer());

					//SupportType gelijkstellen aan dat van parent
					s.setSupportTypeCode(p.getSupportTypeCode());

					//Datums invullen
					p.setDatumUpdate(dateNow);

					//Ticket wordt gesloten, dus GeslotenDoorGebruikerId moet ingevuld worden
					s.setGebruikerByGeslotenDoorGebruikerIdGebruikerId(s.getGebruikerByGebruikerIdGebruikerId());
					p.setGebruikerByGeslotenDoorGebruikerIdGebruikerId(s.getGebruikerByGebruikerIdGebruikerId());

					if(s.getSupportTypeCode().equals(ESupportType.BEZWAAR.getCode())) {
						//<editor-fold desc="Flows bezwaar reacties gebruiker/SBDR">
						if(p.getSupport() != null || activateNotification != null) {
							//<editor-fold desc="Flows reactie SBDR/verlopen bezwaren">

							boolean hasGrandParent = p.getSupport() != null;

							Support gP = null;
							Integer gPBId = null;

							if(hasGrandParent) {
								//Alle info van parent's parent ophalen
								gP = supportDataService.findSupportTicketBySupportId(p.getSupportSupportId());
								gPBId = bedrijfService.findByKlantGebruikersId(gP.getGebruikerByGebruikerIdGebruikerId()).getBedrijfId();
								g = gebruikerService.findByGebruikerId(p.getGebruikerByGebruikerIdGebruikerId());
								if (g != null)
									gPBId = g.getBedrijfBedrijfId();
								else // cannot find bedrijf of gebruiker, so throw error
									throw new ServiceException(ErrorService.CANNOT_SAVE_SUPPORT);

								//Datum van gP invullen
								gP.setDatumUpdate(dateNow);

								//Als er een grandparent bestaat wordt zijn geslotenDoorGebruikerId aangepast naar de huidige gebruiker
								gP.setGebruikerByGeslotenDoorGebruikerIdGebruikerId(s.getGebruikerByGebruikerIdGebruikerId());

								//Ticket wordt gearchiveerd
								gP.setSupportStatusCode(ESupportStatus.GEARCHIVEERD.getCode());
							}

							//Ticket wordt gearchiveerd
							s.setSupportStatusCode(ESupportStatus.GEARCHIVEERD.getCode());
							p.setSupportStatusCode(ESupportStatus.GEARCHIVEERD.getCode());

							//BetwistBezwaar is niet van toepassing wanneer SBDR reageert
							s.setBetwistBezwaar(false);

							if(validateSupport(s).isValid()) {
								if(hasGrandParent) supportDataService.saveSupportTicket(gP);

								supportDataService.saveSupportTicket(p);
								Support savedS = supportDataService.saveSupportTicket(s);

								//Als vermelding WEL geactiveerd moet worden, gaat dit automatisch
								//Zo niet, dan wordt deze hieronder verwijderd
								if(activateNotification != null) {
									if(!activateNotification) {
										//HIER wordt de melding verwijderd
										if(hasGrandParent)
											bedrijfService.rejectMelding(gP.getMeldingMeldingId(), gPBId, savedS.getGebruikerByGebruikerIdGebruikerId());
										else
											bedrijfService.rejectMelding(p.getMeldingMeldingId(), pBId, savedS.getGebruikerByGebruikerIdGebruikerId());
									}
								}

								//Een alert naar beide partijen sturen ter informatie, onafhankelijk van of een bezwaar geaccepteerd/afgewezen is
								alertService.createSupportAlert(savedS, pBId, p.getGebruikerByGebruikerIdGebruikerId());

								Melding m;

								if(hasGrandParent) {
									alertService.createSupportAlert(savedS, gPBId, gP.getGebruikerByGebruikerIdGebruikerId());
									m = meldingDataService.findById(gP.getMeldingMeldingId());
								} else {
									alertService.createSupportAlert(savedS, bId, savedS.getGebruikerByGebruikerIdGebruikerId());
									m = meldingDataService.findById(p.getMeldingMeldingId());
								}

								//Send notification alert to company if the objection is accepted and the notification will be rejected/deleted
								if(activateNotification!=null&&!activateNotification){
									alertService.createMeldingAlert(m.getMeldingId(), m.getBedrijfByMeldingOverBedrijfIdBedrijfId(), m.getBedrijfByMeldingDoorBedrijfIdBedrijfId(), true, EAlertType.MELDING);
								}

								return ConvertUtil.createSupportTransferFromSupport(savedS);
							} else {
								LOGGER.error("Method saveSupportTicket: support not valid");
								throw new ServiceException(ErrorService.SUPPORT_NOT_VALID);
							}
							//</editor-fold>
						} else {
							//<editor-fold desc="Flow reacties gebruikers">
							if(s.getBetwistBezwaar()) {
								//Een bezwaar dat betwist wordt, moet verder afgehandeld worden door SBDR
								s.setSupportStatusCode(ESupportStatus.GESLOTEN.getCode());
								p.setSupportStatusCode(ESupportStatus.GESLOTEN.getCode());

								//Support ticket is bedoeld voor SBDR
								s.setBedrijfBedrijfId(sbdrId);
							} else {
								//Bezwaar wordt niet betwist en dit incident wordt afgesloten
								s.setSupportStatusCode(ESupportStatus.GEARCHIVEERD.getCode());
								p.setSupportStatusCode(ESupportStatus.GEARCHIVEERD.getCode());

								//Support ticket is bedoeld voor de andere partij
								s.setBedrijfBedrijfId(pBId);
							}

							if(validateSupport(s).isValid()) {

								supportDataService.saveSupportTicket(p);
								Support savedS = supportDataService.saveSupportTicket(s);

								//Remove the alert that the current user got
								alertDataService.deleteAlertBySupportId(p.getSupportId(), bId);

								//Stuur een alert naar de andere partij ter informatie
								alertService.createSupportAlert(savedS, pBId, p.getGebruikerByGebruikerIdGebruikerId());

								if(s.getBetwistBezwaar()) {
									//Bezwaar wordt betwist, stuur SBDR een alert voor verdere afhandeling
									alertService.createSupportAlert(savedS);
								} else {
									//Bezwaar wordt niet betwist, verwijder melding
									bedrijfService.removeMelding(p.getMeldingMeldingId(), pBId, bId, savedS.getGebruikerByGebruikerIdGebruikerId(), ERedenVerwijderenMelding.BEZWAAR.getFrontendCode(), false, true);
								}

								return ConvertUtil.createSupportTransferFromSupport(savedS);
							} else {
								LOGGER.error("Method saveSupportTicket: support not valid");
								throw new ServiceException(ErrorService.SUPPORT_NOT_VALID);
							}
							//</editor-fold>
						}
						//</editor-fold>
					} else {
						//<editor-fold desc="Flow support reactie SBDR">

						//Status op gearchiveerd zetten
						s.setSupportStatusCode(ESupportStatus.GEARCHIVEERD.getCode());
						p.setSupportStatusCode(ESupportStatus.GEARCHIVEERD.getCode());

						//betwistBezwaar is niet relevant, dus default(false)
						s.setBetwistBezwaar(false);
						p.setBetwistBezwaar(false);

						//Reden, als deze bestaat, gelijkstellen aan die van de parent
						s.setSupportRedenCode(p.getSupportRedenCode());

						if(validateSupport(s).isValid()) {
							supportDataService.saveSupportTicket(p);
							Support savedS = supportDataService.saveSupportTicket(s);

							alertService.createSupportAlert(savedS, pBId, p.getGebruikerByGebruikerIdGebruikerId());

							return ConvertUtil.createSupportTransferFromSupport(savedS);
						} else {
							LOGGER.error("Method saveSupportTicket: support not valid");
							throw new ServiceException(ErrorService.SUPPORT_NOT_VALID);
						}

						//</editor-fold>
					}
					//</editor-fold>
				} else {
					//<editor-fold desc="Flows initiele tickets">

					//Genereer een uniek referentienummer
					String ref = SerialNumber.generateRandomSerialNumber8_32();
					boolean exists = supportDataService.checkIfReferentieNummerExists(ref);
					while(exists) {
						ref = SerialNumber.generateRandomSerialNumber8_32();
						exists = supportDataService.checkIfReferentieNummerExists(ref);
					}

					s.setReferentieNummer(ref);

					//Elk nieuw toegevoegd ticket heeft de status open
					s.setSupportStatusCode(ESupportStatus.OPEN.getCode());

					//betwistBezwaar is niet relevant, dus default(false)
					s.setBetwistBezwaar(false);

					//Als het een bezwaar betreft, moet bestemdVoorBedrijf gelijk zijn aan het bedrijf waar de bijbehorende melding OVER gaat
					//Als het een klacht, probleem of vraag betreft, moet bestemdVoorBedrijf SBDR zijn
					if(s.getSupportTypeCode().equals(ESupportType.BEZWAAR.getCode())) {
						Melding m = meldingDataService.findById(s.getMeldingMeldingId());
						s.setBedrijf(m.getBedrijfByMeldingDoorBedrijfId());

						//Stuur de indiener van de vermelding een email
						emailService.sendNotificationObjectionEmail(klantDataService.findKlantOfGebruikerByGebruikerId(m.getGebruikerByMeldingDoorGebruikerIdGebruikerId(), s.getBedrijf().getBedrijfId()), m);
					} else {
						s.setBedrijf(bedrijfDataService.findSbdr());
					}

					if(validateSupport(s).isValid()) {
						Support savedS = supportDataService.saveSupportTicket(s);

						//Create an alert
						alertService.createSupportAlert(savedS);

						return ConvertUtil.createSupportTransferFromSupport(savedS);
					} else {
						LOGGER.error("Method saveSupportTicket: support not valid");
						throw new ServiceException(ErrorService.SUPPORT_NOT_VALID);
					}
					//</editor-fold>
				}
			} catch(DataServiceException e) {
				LOGGER.error("Method saveSupportTicket: " + e.getMessage());
				throw new ServiceException(ErrorService.CANNOT_SAVE_SUPPORT);
			}
		} else {
			LOGGER.error("Method saveSupportTicket: parameter is null");
			throw new ServiceException(ErrorService.SUPPORT_IS_EMPTY);
		}
	}

	@Override
	@Transactional
	public SupportBestandTransfer saveSupportTicketBestand(SupportBestand sB, byte[] doc) throws ServiceException {
		if(sB != null && doc != null) {
			try {
				String ref = SerialNumber.generateRandomSerialNumber8_32();
				boolean exists = supportDataService.checkIfBestandReferentieNummerExists(ref);
				while(exists) {
					ref = SerialNumber.generateRandomSerialNumber8_32();
					exists = supportDataService.checkIfBestandReferentieNummerExists(ref);
				}

				//String fullFilename = DocumentUtil.generateAttachmentFilename(ref) + "." + FilenameUtils.getExtension(sB.getOorspronkelijkBestandsNaam());
				String filename = ref + "." + Files.getFileExtension(sB.getOorspronkelijkBestandsNaam());
				String fullFilename = DocumentUtil.getAttachmentRootPath() + File.separator + filename;
				//String fullFilename = ref + "." + FilenameUtils.getExtension(sB.getOorspronkelijkBestandsNaam());

				sB.setDatumUpload(new Date());
				sB.setVolledigPad(filename);
				sB.setReferentieNummer(ref);

				saveFile(doc, fullFilename);

				return ConvertUtil.createSupportBestandTransferFromSupportBestand(supportDataService.saveSupportTicketBestand(sB));
			} catch(DataServiceException e) {
				LOGGER.error("Method saveSupportTicketBestand: " + e.getMessage());
				throw new ServiceException(ErrorService.CANNOT_SAVE_SUPPORT_FILE);
			}
		} else {
			LOGGER.error("Method saveSupportTicketBestand: parameters are empty");
			throw new ServiceException(ErrorService.SUPPORT_FILE_IS_EMPTY);
		}
	}
	
	private void saveFile(byte[] data, String fullPath) throws ServiceException {
		try {
			File serverFile = new File(fullPath);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
			stream.write(data);
			stream.close();

		} catch(Exception e) {
			LOGGER.error("Method saveFile: " + e.getMessage());
			throw new ServiceException(e);
		}
	}

	private ValidationObject validateSupport(Support s) {
		ValidationObject loValidation = new ValidationObject();

		if(s != null) {
			try {
				//Als GeslotenDoor ingevuld is, mag het ticket niet open staan
				if(s.getGebruikerByGeslotenDoorGebruikerId() != null && ESupportStatus.get(s.getSupportStatusCode()) == ESupportStatus.OPEN) {
					loValidation.addMessage("GeslotenDoor mag niet ingevuld zijn bij een open ticket", ValidationConstants.MessageType.INVALID);
					LOGGER.info("GeslotenDoor mag niet ingevuld zijn bij een open ticket");
				}

				//Er mag niet op een eigen bericht gereageerd worden
				if(s.getSupport() != null) {
					if(s.getSupport().getGebruikerByGebruikerIdGebruikerId().equals(s.getGebruikerByGebruikerIdGebruikerId())) {
						loValidation.addMessage("Een gebruiker mag niet op een eigen bericht reageren", ValidationConstants.MessageType.INVALID);
						LOGGER.info("Een gebruiker mag niet op een eigen bericht reageren");
					}
				}

				//Er mag geen reactie zijn wanneer de status op open staat
				if(s.getSupports() != null) {
					if(s.getSupports().size() == 1 && ESupportStatus.get(s.getSupportStatusCode()) == ESupportStatus.OPEN) {
						loValidation.addMessage("Dit ticket mag niet openstaan met een reactie", ValidationConstants.MessageType.INVALID);
						LOGGER.info("Dit ticket mag niet openstaan met een reactie");
					}

					//Er mag maar 1 keer op een bericht gereageerd worden
					if(s.getSupports().size() > 1) {
						loValidation.addMessage("Er mag maar 1 keer op een ticket gereageerd worden", ValidationConstants.MessageType.INVALID);
						LOGGER.info("Er mag maar 1 keer op een ticket gereageerd worden");
					}
				}

				//Bericht mag niet leeg zijn
				if(s.getBericht().isEmpty()) {
					loValidation.addMessage("Uw bericht moet ingevuld zijn", ValidationConstants.MessageType.INVALID);
					LOGGER.info("Uw bericht moet ingevuld zijn");
				}

				//Referentienummer mag niet leeg zijn
				if(s.getReferentieNummer().isEmpty()) {
					loValidation.addMessage("Er moet een referentienummer opgegeven worden", ValidationConstants.MessageType.INVALID);
					LOGGER.info("Er moet een referentienummer opgegeven worden");
				}

				//Gebruiker moet ingevuld zijn
				if(s.getGebruikerByGebruikerId() == null) {
					//	if(!gebruikerService.validateUserData(s.getGebruikerByGebruikerId().getGebruikersNaam(), s.getGebruikerByGebruikerId().getWachtwoord().getWachtwoord()))
					//		loValidation.addMessage("De gebruiker is niet valide", ValidationConstants.MessageType.INVALID);
					//	else
					loValidation.addMessage("De gebruiker moet ingevuld zijn", ValidationConstants.MessageType.INVALID);
					LOGGER.info("De gebruiker moet ingevuld zijn");
				}

				if(s.getDatumUpdate() != null) {
					//DatumUpdate moet in het verleden liggen
					if(s.getDatumUpdate().after(new DateTime().toDate())) {
						loValidation.addMessage("De tijd van de update mag niet in de toekomst liggen", ValidationConstants.MessageType.INVALID);
						LOGGER.info("De tijd van de update mag niet in de toekomst liggen");
					}

					//DatumUpdate moet na DatumAangemaakt liggen
					if(s.getDatumUpdate().before(s.getDatumAangemaakt())) {
						loValidation.addMessage("De tijd van de update mag neit voor de aanmaak liggen", ValidationConstants.MessageType.INVALID);
						LOGGER.info("De tijd van de update mag neit voor de aanmaak liggen");
					}
				}

				switch(ESupportType.get(s.getSupportTypeCode())) {
					case BEZWAAR: {
						//Als er geen parent bestaat, mag melding niet null zijn
						if(s.getSupport() == null) if(s.getMeldingMeldingId() == null) {
							loValidation.addMessage("Melding ID moet gevuld zijn bij bezwaren.", ValidationConstants.MessageType.INVALID);
							LOGGER.info("Melding ID moet gevuld zijn bij bezwaren");
						}

						//De opgegeven reden moet bij de bezwaar redenen horen
						if(s.getSupportRedenCode() != null) {
							String supportRedenType = ESupportReden.get(s.getSupportRedenCode()).getSupportType().getCode();
							String supportType = s.getSupportTypeCode();
							if(!supportRedenType.isEmpty() && !supportType.isEmpty()) {
								if(!supportRedenType.equals(supportType)) {
									loValidation.addMessage("De reden voor dit soort s is ongeldig", ValidationConstants.MessageType.INVALID);
									LOGGER.info("De reden voor dit soort s is ongeldig");
								}
							} else {
								loValidation.addMessage("Niet in staat om supportType van supportReden op te halen", ValidationConstants.MessageType.INVALID);
								LOGGER.info("Niet in staat om supportType van supportReden op te halen");
							}
						}

						break;
					}
					case PROBLEEM: {
						//De opgegeven reden moet bij de bezwaar redenen horen
						if(s.getSupportRedenCode() != null) {
							String supportRedenType = ESupportReden.get(s.getSupportRedenCode()).getSupportType().getCode();
							String supportType = s.getSupportTypeCode();
							if(!supportRedenType.isEmpty() && !supportType.isEmpty()) {
								if(!supportRedenType.equals(supportType)) {
									loValidation.addMessage("De reden voor dit soort s is ongeldig", ValidationConstants.MessageType.INVALID);
									LOGGER.info("De reden voor dit soort s is ongeldig");
								}
							} else {
								loValidation.addMessage("Niet in staat om supportType van supportReden op te halen", ValidationConstants.MessageType.INVALID);
								LOGGER.info("Niet in staat om supportType van supportReden op te halen");
							}
						}
						break;
					}
					case KLACHT:
					case VRAAG:
					case SUGGESTIE: {
						break;
					}
					default: {
						loValidation.addMessage("Onbekende Enum waarde(" + s.getSupportTypeCode() + ").", ValidationConstants.MessageType.INVALID);
						LOGGER.info("Onbekende Enum waarde(" + s.getSupportTypeCode() + ").");

						break;
					}
				}
			} catch(Exception e) {
				loValidation.addMessage("Validatie mislukt: " + e.getMessage(), ValidationConstants.MessageType.ERROR);
				LOGGER.error("Method validateSupport: " + e.getMessage());
			}
		} else {
			loValidation.addMessage("Support s is null", ValidationConstants.MessageType.ERROR);
			LOGGER.error("Support s is null");
		}
		return loValidation;
	}
}