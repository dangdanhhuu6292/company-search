package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.job.AssignExpiredObjectionsToAdminJob;
import nl.devoorkant.sbdr.business.job.CreateInvoicesJob;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.business.util.EFacturatieFrequentie;
import nl.devoorkant.sbdr.business.util.FactuurRegelAggregate;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.EProduct;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

@Service("factuurService")
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

	@Autowired
	Scheduler scheduler;
	
	@Value("${job.cron.create_facturen}")
	String cronExpressionCreateFacturen;
	
	@Value("${testinvoiceperiod}")
	private int testinvoiceperiod;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
	@Autowired
	BedrijfDataService bedrijfDataService;
	@Autowired
	ConfiguratieDataService configuratieDataService;
	@Autowired
	DocumentService documentService;
	@Autowired
	FactuurConfigDataService factuurConfigDataService;
	@Autowired
	FactuurDataService factuurDataService;
	@Autowired
	KlantDataService klantDataService;
	@Autowired
	KortingsCodeService kortingsCodeService;
	@Autowired
	MeldingDataService meldingDataService;
	@Autowired
	ProductDataService productDataService;
	@Autowired
	EmailService emailService;

	
// MBR 7-2-2020 Invoices turned off for now	
//	@PostConstruct
//	public void createSchedule() {        
//        JobDetail jobDetail = buildJobDetail();
//        Trigger trigger = buildJobTrigger(jobDetail);
//        try {
//			scheduler.scheduleJob(jobDetail, trigger);
//		} catch (SchedulerException e) {
//			LOGGER.error("Cannot start product schedule CreateFacturen", e);
//		}        
//	}
	
	private JobDetail buildJobDetail() {

        return JobBuilder.newJob(CreateInvoicesJob.class)
                .withIdentity(UUID.randomUUID().toString(), "product-jobs")
                .withDescription("Create facturen")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTrigger(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "product")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionCreateFacturen))
    			  .build();    	
    }  
    
	/**
	 * Performed by Mule Quarz process
	 *
	 * @throws ServiceException
	 */
	@Override
	@Transactional
	public void createFacturen() throws ServiceException {
		try {
			Date factuurDatum = doCreateFacturen();

			while(factuurDatum != null) factuurDatum = doCreateFacturen();

			//			// for now configs for all products are the same, so take one product
			//			FactuurConfig factuurConfig = factuurConfigDataService.findByProductCode(EProduct.MONITORING.getCode());
			//			Date datumVorigeFactuur = factuurConfig.getLaatsteFactuurDatum();
			//			Date datumNieuweFactuur = getNieuweFactuurDatum(factuurConfig);
			//
			//			while(datumNieuweFactuur != null) {
			//				checkForPurchasesAfterTrialPeriod(datumVorigeFactuur, datumNieuweFactuur);
			//
			//				closeAllOpenFacturen(datumVorigeFactuur, datumNieuweFactuur);
			//
			//				// update factuurdatum
			//				factuurConfig.setLaatsteFactuurDatum(datumNieuweFactuur);
			//
			//				// maybe another factuur-regels can be created?
			//				datumNieuweFactuur = getNieuweFactuurDatum(factuurConfig);
			//
			//				factuurConfig = factuurConfigDataService.save(factuurConfig);
			//			}

		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public Tarief getCurrentTarief(EProduct product) throws ServiceException {
		try {
			List<Tarief> tarief = productDataService.findTariefByProdCodeGeldigVanaf(product.getCode(), new Date());

			if(tarief != null && tarief.size() >= 1) {
				return tarief.get(0);
			} else throw new ServiceException("Error finding tarief in getCurrentTarief");
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<FactuurRegelAggregate> getFactuurRegelAggregate(Integer factuurId) throws ServiceException {
		List<FactuurRegelAggregate> result = null;

		try {
			List<Object[]> factuurRegelAggregate = factuurDataService.getFactuurRegelAggregate(factuurId);

			result = ConvertUtil.convertToFactuurRegelAggregate(factuurRegelAggregate);

			return result;
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void makeDonation(Integer bedrijfId, Integer managedBedrijfId, Date datum, BigDecimal amount) throws ServiceException {
		try {
			if(bedrijfId != null && datum != null && amount != null) {
				factuurDataService.addFactuurRegel(bedrijfId, managedBedrijfId, EProduct.DONATIE.getCode(), amount, datum);
			} else throw new ServiceException("Parameters cannot be null");
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void purchaseAchterstandCheck(Integer bedrijfId, Integer managedBedrijfId, Date datum) throws ServiceException {
		try {
			Tarief tarief = getCurrentTarief(EProduct.ACHTERSTANDCHECK);

			if(tarief != null) {
				factuurDataService.addFactuurRegel(bedrijfId, managedBedrijfId, EProduct.ACHTERSTANDCHECK.getCode(), tarief.getBedrag(), datum);
			} else throw new ServiceException("Error finding tarief in purchaseAchterstandCheck");
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void purchaseMonitoring(Integer bedrijfId, Integer managedBedrijfId, Date datum) throws ServiceException {
		try {
			FactuurRegel factuurRegel = factuurDataService.getOpenFactuurRegelOfBedrijf(bedrijfId, EProduct.MONITORING);

			if(factuurRegel == null) {
				Tarief tarief = getCurrentTarief(EProduct.MONITORING);

				if(tarief != null) {
					factuurDataService.addFactuurRegel(bedrijfId, managedBedrijfId, EProduct.MONITORING.getCode(), tarief.getBedrag(), datum);
				} else throw new ServiceException("Error finding tarief in purchaseMonitoring");
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void purchaseRapport(Integer bedrijfId, Integer managedBedrijfId, Date datum) throws ServiceException {
		try {
			Tarief tarief = getCurrentTarief(EProduct.RAPPORT);

			if(tarief != null) {
				factuurDataService.addFactuurRegel(bedrijfId, managedBedrijfId, EProduct.RAPPORT.getCode(), tarief.getBedrag(), datum);
			} else throw new ServiceException("Error finding tarief in purchaseMonitoring");
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void purchaseVermelding(Integer bedrijfId, Integer managedBedrijfId, Date datum) throws ServiceException {
		try {
			Tarief tarief = getCurrentTarief(EProduct.VERMELDING);

			if(tarief != null) {
				factuurDataService.addFactuurRegel(bedrijfId, managedBedrijfId, EProduct.VERMELDING.getCode(), tarief.getBedrag(), datum);
			} else throw new ServiceException("Error finding tarief in purchaseVermelding");
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void removeMonitoringOfBedrijfFromFactuur(Integer bedrijfId) throws ServiceException {
		try {
			FactuurRegel factuurRegel = factuurDataService.getOpenFactuurRegelOfBedrijf(bedrijfId, EProduct.MONITORING);

			if(factuurRegel != null) {
				factuurDataService.removeOpenFactuurRegel(factuurRegel);
			}

		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Check for Monitoring subscription purchases.
	 *
	 * @throws ServiceException
	 */
	private void checkForPurchasesAfterTrialPeriod(Date factuurDatum, Date factuurDatumNieuw) throws ServiceException {
		try {
			// check monitoring

			//FactuurConfig factuurConfig = factuurConfigDataService.findByProductCode(EProduct.MONITORING.getCode());
			//Date factuurDatum = factuurConfig.getLaatsteFactuurDatum();
			//Date factuurDatumNieuw = getNieuweFactuurDatum(factuurConfig);
			//
			//while(factuurDatumNieuw != null) { // while factuur can be created...
			List<Object[]> monitoringsTarief = productDataService.findAllActiveMonitoringTarief(factuurDatum, factuurDatumNieuw);
			if(monitoringsTarief != null && monitoringsTarief.size() > 0) {
				for(Object[] monitoringTarief : monitoringsTarief) {
					Monitoring monitoring = (Monitoring) monitoringTarief[0];
					Tarief starttarief = (Tarief) monitoringTarief[1];
					Tarief huidigtarief = (Tarief) monitoringTarief[2];

					Calendar monitoringStartFactuur = Calendar.getInstance();
					Calendar monitoringStart = Calendar.getInstance();
					monitoringStartFactuur.setTime(monitoring.getDatumStart());
					monitoringStart.setTime(monitoring.getDatumStart());

					if(starttarief != null && starttarief.getDagenTrial() != null)
						monitoringStartFactuur.add(Calendar.DAY_OF_MONTH, starttarief.getDagenTrial());
					// upcoming period
					BigDecimal bedrag = huidigtarief.getBedrag();

					// if there was no previous invoice create trial_date to prevent accounting trial period costs too early
					Date previousFactuurDatum = null;
					if(factuurDatum == null) {
						Calendar trialcal = Calendar.getInstance();
						trialcal.setTime(factuurDatumNieuw);
						trialcal.add(Calendar.DAY_OF_MONTH, -1 * starttarief.getDagenTrial());
						previousFactuurDatum = trialcal.getTime();
					} else previousFactuurDatum = factuurDatum;

					// MBR 18-2-2016 solved in query findAllActiveMonitoringTarief
					// If current invoice date > end-of-trial date AND
					// no previous invoice
					// OR If previousFactuurDatum BEFORE end-of-trial date AND previousFactuurDatum AFTER the start of Monitoring THEN add trial period costs
					//if(factuurDatumNieuw.after(monitoringStartFactuur.getTime()) && (factuurDatum == null || (!previousFactuurDatum.after(monitoringStartFactuur.getTime()) && previousFactuurDatum.after(monitoringStart.getTime()))))
					//	bedrag = bedrag.add(starttarief.getBedrag());

					// Invoice to company of user. So if company is managed then invoice to manager company
					Integer managedBedrijfId = null;
					if (monitoring.getGebruikerByMonitoringDoorGebruikerId() != null &&
							monitoring.getGebruikerByMonitoringDoorGebruikerId().getBedrijf() != null &&
							!monitoring.getGebruikerByMonitoringDoorGebruikerId().getBedrijf().getBedrijfId().equals(monitoring.getBedrijfByMonitoringDoorBedrijfIdBedrijfId()))
						managedBedrijfId = monitoring.getGebruikerByMonitoringDoorGebruikerId().getBedrijf().getBedrijfId();
					
					if (managedBedrijfId == null || managedBedrijfId.equals(monitoring.getBedrijfByMonitoringDoorBedrijfIdBedrijfId()))
						factuurDataService.addFactuurRegel(monitoring.getBedrijfByMonitoringDoorBedrijfIdBedrijfId(), null, EProduct.MONITORING.getCode(), bedrag, factuurDatumNieuw);
					else
						factuurDataService.addFactuurRegel(managedBedrijfId, monitoring.getBedrijfByMonitoringDoorBedrijfIdBedrijfId(), EProduct.MONITORING.getCode(), bedrag, factuurDatumNieuw);
				}

				// this functionality is moved afterwards. All open factuurregels appear on one single factuur
				//closeAllOpenFacturen(factuurDatumNieuw);

			}
			//
			//	// update factuurdatum
			//	factuurConfig.setLaatsteFactuurDatum(factuurDatumNieuw);
			//
			//	// maybe another factuur-regels can be created?
			//	factuurDatumNieuw = getNieuweFactuurDatum(factuurConfig);
			//}
			//
			//
			//factuurConfig = factuurConfigDataService.save(factuurConfig);

		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	private static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
	
	private void closeAllOpenFacturen(Date datumOudeFactuur, Date datumNieuweFactuur) throws ServiceException {

		try {
			// add one day, 
			// because new products (factuurRegel) could be on invoice (factuur) to be processed with purchase date 
			// after the value of datumNieuweFactuur (which has time 0:00:00)
			Date datumNieuweFactuurNewDay = datumNieuweFactuur == null ? null : addDays(datumNieuweFactuur, 1);
			List<Factuur> facturen = factuurDataService.getAllOpenFacturen(datumNieuweFactuurNewDay);

			if(facturen != null) {
				for(Factuur factuur : facturen) {
					Calendar d = Calendar.getInstance();
					d.setTime(datumNieuweFactuur);

					// generate factuur ref
					Integer factuurCurrent = configuratieDataService.findByPrimaryKey("FAC_CURRENT").getWaardeInt();
					Integer factuurIncrement = configuratieDataService.findByPrimaryKey("FAC_INCR").getWaardeInt();
					Integer factuurNew = factuurCurrent;

					String referentie = factuurNew.toString();
					while(factuurDataService.findByReferentie(referentie) != null) {
						factuurNew += factuurIncrement;
						referentie = Integer.toString(factuurNew);
					}

					//Save new factuur number
					Configuratie config = configuratieDataService.findByPrimaryKey("FAC_CURRENT");
					config.setWaardeInt(factuurNew);
					configuratieDataService.save(config);

					factuur.setReferentie(referentie);
					factuur.setDatumFactuur(datumNieuweFactuur);
					factuur.setDatumAangemaakt(new Date());

// MBR 18-2-2016 For future use. Code is not correct: findNewMeldingenBetweenDates fetches all (of all Companies) notifications, this is not ok!!
//					Klant klantOfBedrijf = klantDataService.findKlantOfBedrijfByBedrijfId(factuur.getBedrijfBedrijfId());
//					if(klantOfBedrijf != null) {
//						KortingsCode code = kortingsCodeService.findKortingsCodeOfKlant(klantOfBedrijf.getGebruikerId());
//						if(code != null) {
//							if(kortingsCodeService.checkIfCodeIsValid(code.getCode(), new Date())) {
//								//There is a valid, non-expired, discount code
//								int total = meldingDataService.findNewMeldingenBetweenDates(datumOudeFactuur, datumNieuweFactuur).size();
//								int progress = 0;
//
//								for(FactuurRegel fR : factuurDataService.findRapportFactuurRegelsByFactuurId(factuur.getFactuurId())) {
//									if(progress < total) {
//										fR.setBedrag(new BigDecimal(0));
//
//										factuurDataService.saveFactuurRegel(fR);
//									} else {
//										break;
//									}
//									progress++;
//								}
//							}
//						}
//					}
					
					// Discount max reports requested
					int rap_discount = configuratieDataService.findByPrimaryKey("RAP_DISCOUNT").getWaardeInt();
					// Discount max notifications requested
					int ver_discount = configuratieDataService.findByPrimaryKey("VER_DISCOUNT").getWaardeInt();
					
					if (rap_discount > 0 || ver_discount > 0) {
						List<Factuur> allFacturen = factuurDataService.findAllFacturenByBedrijfId(factuur.getBedrijfBedrijfId());
						LOGGER.debug("Company "+ factuur.getBedrijfBedrijfId() + " nrInvoices: "+ allFacturen.size());
						
						Factuur newFactuur = null; // = new invoice to be processed
						if (allFacturen != null) {
							for(Factuur indFactuur : allFacturen) {
								newFactuur = indFactuur;
								// if there is still a discount to be processed now or in future loop all invoice data
								if (rap_discount > 0) {
									for(FactuurRegel fR : factuurDataService.findRapportFactuurRegelsByFactuurId(indFactuur.getFactuurId())) {
										if(fR.getProductCode().equals(EProduct.RAPPORT.getCode())) {
											// if factuur is equal to new invoice, then discount must be processed in amount
											if (newFactuur.getFactuurId().equals(factuur.getFactuurId())) {
												fR.setBedrag(new BigDecimal(0));
												factuurDataService.saveFactuurRegel(fR);
												LOGGER.debug("RAP Discount on invoice " + fR.getFactuurFactuurId() + ", #" + rap_discount + "report: " + fR.getFactuurRegelId());
											}
											rap_discount--;
											if (rap_discount == 0)
												break; // on max discount reports
										}
									}
								} else
									break;
							}	
						}
						
						newFactuur = null; // = new invoice to be processed
						if (allFacturen != null) {
							for(Factuur indFactuur : allFacturen) {
								newFactuur = indFactuur;
								// if there is still a discount to be processed now or in future loop all invoice data
								if (ver_discount > 0) {
									for(FactuurRegel fR : factuurDataService.findVermeldingFactuurRegelsByFactuurId(indFactuur.getFactuurId())) {
										if(fR.getProductCode().equals(EProduct.VERMELDING.getCode())) {
											// if factuur is equal to new invoice, then discount must be processed in amount
											if (newFactuur.getFactuurId().equals(factuur.getFactuurId())) {
												fR.setBedrag(new BigDecimal(0));
												factuurDataService.saveFactuurRegel(fR);
												LOGGER.debug("VER Discount on invoice " + fR.getFactuurFactuurId() + ", #" + ver_discount + "report: " + fR.getFactuurRegelId());
											}
											ver_discount--;
											if (ver_discount == 0)
												break; // on max discount notifications
										}
									}
								} else
									break;
							}	
						}
					}
					
					factuur.setBedrag(factuurDataService.getTotalOfFactuur(factuur.getFactuurId()));

					Factuur nieuweFactuur = factuurDataService.save(factuur);

					// create PDF factuur
					documentService.createFactuurPdf(nieuweFactuur.getFactuurId());
					
					Klant klant = klantDataService.findKlantOfBedrijfByBedrijfId(factuur.getBedrijfBedrijfId());
					emailService.sendNotificationNewInvoice(klant, nieuweFactuur.getFactuurId());
				}
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	private Date doCreateFacturen() throws DataServiceException {
		List<Date> nieuweFactuurDatums = new ArrayList<Date>();
		List<FactuurConfig> factuurConfs = new ArrayList<FactuurConfig>();
		Date overallVorigeFactuurDatum = null;
		Date overallFactuurDatum = null;

		// 1WK, 2WK, MND
		for(EFacturatieFrequentie freq : EFacturatieFrequentie.values()) {
			List<FactuurConfig> factuurConfigs = factuurConfigDataService.findAllOfFrequency(freq.getCode());

			if(factuurConfigs != null && factuurConfigs.size() > 0) {
				// all factuurconfig of certain invoice frequency 1WK, 2WK or MND
				for(FactuurConfig factuurConfig : factuurConfigs) {
					Date nieuweFactuurDatum = getNieuweFactuurDatum(factuurConfig);

					// nieuweFactuurDatum may not be in future
					if(nieuweFactuurDatum != null) {
						nieuweFactuurDatums.add(nieuweFactuurDatum);
						factuurConfs.add(factuurConfig);

						// only Monitoring can have extra purchase entries on invoice
						if(factuurConfig.getProductCode().equals(EProduct.MONITORING.getCode())) {
							// check for purchases per date
							checkForPurchasesAfterTrialPeriod(factuurConfig.getLaatsteFactuurDatum(), nieuweFactuurDatum);
						}

						// set overallVorigeFactuurDatum to the min vorigeFactuurDatum
						if(overallVorigeFactuurDatum == null)
							overallVorigeFactuurDatum = factuurConfig.getLaatsteFactuurDatum();
						else if(overallVorigeFactuurDatum.after(factuurConfig.getLaatsteFactuurDatum()))
							overallVorigeFactuurDatum = factuurConfig.getLaatsteFactuurDatum();

						// set overallFactuurDatum to the max nieuweFactuurDatum
						if(overallFactuurDatum == null) overallFactuurDatum = nieuweFactuurDatum;
						else if(overallFactuurDatum.before(nieuweFactuurDatum))
							overallFactuurDatum = nieuweFactuurDatum;
					} else // add dummy factuurdatum for update loop
						nieuweFactuurDatums.add(factuurConfig.getLaatsteFactuurDatum());
				}
			}
		}

		// create facturen!
		if(!nieuweFactuurDatums.isEmpty() && overallFactuurDatum != null) {
			closeAllOpenFacturen(overallVorigeFactuurDatum, overallFactuurDatum);

			// update all FactuurConfigs with new date
			int i = 0;
			for(FactuurConfig factuurConfig : factuurConfs) {
				// set nieuweFactuurDatum to new date belonging to specific factuurConfig
				factuurConfig.setLaatsteFactuurDatum(nieuweFactuurDatums.get(i++));
				factuurConfig = factuurConfigDataService.save(factuurConfig);
			}
		}

		return overallFactuurDatum;

	}

	private Date getNextMaandelijksDatum(Date datum) {
		// current month
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentmonth = cal.get(Calendar.MONTH);
		int maximummonth = cal.getActualMaximum(Calendar.MONTH);
		// current year
		int currentyear = cal.get(Calendar.YEAR);

		// determine last factuur date
		Date lastdatum = datum;
		boolean nolastdatum = false;
		if(lastdatum == null) {
			nolastdatum = true;
			Calendar startofyear = Calendar.getInstance();
			startofyear.set(currentyear, Calendar.JANUARY, 1); // set start date to 1st of January of current year
			int startweek = startofyear.get(Calendar.WEEK_OF_YEAR);
			startofyear.clear();
			startofyear.set(Calendar.WEEK_OF_YEAR, startweek);
			startofyear.set(Calendar.YEAR, currentyear);
			lastdatum = startofyear.getTime();
		}
		cal.setTime(lastdatum);

		// NO TIME
		int lastdays = cal.get(Calendar.DAY_OF_MONTH);
		int lastmonth = cal.get(Calendar.MONTH);
		int lastyear = cal.get(Calendar.YEAR);

		// Get calendar, clear it and set day, month and year.
		Calendar lastcalendar = Calendar.getInstance();
		lastcalendar.clear();
		lastcalendar.set(Calendar.DAY_OF_MONTH, lastdays);
		lastcalendar.set(Calendar.MONTH, lastmonth);
		lastcalendar.set(Calendar.YEAR, lastyear);
		// Now get the first day of month.
		Date lastdate = lastcalendar.getTime();

		Calendar result = Calendar.getInstance();
		result.clear();
		result.setTime(lastdate);
		if(!nolastdatum) // add one month
			result.add(Calendar.MONTH, 1);

		return result.getTime();
	}

	private Date getNextTestDatum(Date datum) {
		// Invoices every day
		int invoiceafterdays = testinvoiceperiod;

		// current weeknumber
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentweek = cal.get(Calendar.WEEK_OF_YEAR);
		int maximumweek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
		// current year
		int currentyear = cal.get(Calendar.YEAR);

		// determine last factuur date
		Date lastdatum = datum;
		boolean nolastdatum = false;
		if(lastdatum == null) {
			nolastdatum = true;
			Calendar startofyear = Calendar.getInstance();
			startofyear.set(currentyear, Calendar.JANUARY, 1); // set start date to 1st of January of current year
			int startweek = startofyear.get(Calendar.WEEK_OF_YEAR);
			startofyear.clear();
			startofyear.set(Calendar.WEEK_OF_YEAR, startweek);
			startofyear.set(Calendar.YEAR, currentyear);
			lastdatum = startofyear.getTime();
		}
		cal.setTime(lastdatum);

		// NO TIME
		int lastdays = cal.get(Calendar.DAY_OF_MONTH);
		int lastmonth = cal.get(Calendar.MONTH);
		int lastyear = cal.get(Calendar.YEAR);

		// Get calendar, clear it and set day, month and year.
		Calendar lastcalendar = Calendar.getInstance();
		lastcalendar.clear();
		lastcalendar.set(Calendar.DAY_OF_MONTH, lastdays);
		lastcalendar.set(Calendar.MONTH, lastmonth);
		lastcalendar.set(Calendar.YEAR, lastyear);

		Calendar result = Calendar.getInstance();
		result.clear();
		// Now get the first day of week.
		result.setTime(lastcalendar.getTime());
		if(!nolastdatum) // add nr of days for new invoice date
			result.add(Calendar.DAY_OF_YEAR, invoiceafterdays);

		return result.getTime();
	}

	private Date getNextTweewekelijksDatum(Date datum) {
		// current weeknumber
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentweek = cal.get(Calendar.WEEK_OF_YEAR);
		int maximumweek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
		// current year
		int currentyear = cal.get(Calendar.YEAR);

		// determine last factuur date
		Date lastdatum = datum;
		boolean nolastdatum = false;
		if(lastdatum == null) {
			nolastdatum = true;
			Calendar startofyear = Calendar.getInstance();
			startofyear.set(currentyear, Calendar.JANUARY, 1); // set start date to 1st of January of current year
			int startweek = startofyear.get(Calendar.WEEK_OF_YEAR);
			startofyear.clear();
			startofyear.set(Calendar.WEEK_OF_YEAR, startweek);
			startofyear.set(Calendar.YEAR, currentyear);
			lastdatum = startofyear.getTime();
		}
		cal.setTime(lastdatum);

		// NO TIME
		int lastdays = cal.get(Calendar.DAY_OF_MONTH);
		int lastmonth = cal.get(Calendar.MONTH);
		int lastyear = cal.get(Calendar.YEAR);

		// Get calendar, clear it and set day, month and year.
		Calendar lastcalendar = Calendar.getInstance();
		lastcalendar.clear();
		lastcalendar.set(Calendar.DAY_OF_MONTH, lastdays);
		lastcalendar.set(Calendar.MONTH, lastmonth);
		lastcalendar.set(Calendar.YEAR, lastyear);

		Calendar result = Calendar.getInstance();
		result.clear();
		// Now get the first day of week.
		result.setTime(lastcalendar.getTime());
		if(!nolastdatum) // add two weeks, 14 days from a monday
			result.add(Calendar.DAY_OF_YEAR, 14);

		return result.getTime();
	}

	private Date getNextWekelijksDatum(Date datum) {
		// current weeknumber
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentweek = cal.get(Calendar.WEEK_OF_YEAR);
		int maximumweek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
		// current year
		int currentyear = cal.get(Calendar.YEAR);

		// determine last factuur date
		Date lastdatum = datum;
		boolean nolastdatum = false;
		if(lastdatum == null) {
			nolastdatum = true;
			Calendar startofyear = Calendar.getInstance();
			startofyear.set(currentyear, Calendar.JANUARY, 1); // set start date to 1st of January of current year
			int startweek = startofyear.get(Calendar.WEEK_OF_YEAR);
			startofyear.clear();
			startofyear.set(Calendar.WEEK_OF_YEAR, startweek);
			startofyear.set(Calendar.YEAR, currentyear);
			lastdatum = startofyear.getTime();
		}
		cal.setTime(lastdatum);

		// NO TIME
		int lastdays = cal.get(Calendar.DAY_OF_MONTH);
		int lastmonth = cal.get(Calendar.MONTH);
		int lastyear = cal.get(Calendar.YEAR);

		// Get calendar, clear it and set day, month and year.
		Calendar lastcalendar = Calendar.getInstance();
		lastcalendar.clear();
		lastcalendar.set(Calendar.DAY_OF_MONTH, lastdays);
		lastcalendar.set(Calendar.MONTH, lastmonth);
		lastcalendar.set(Calendar.YEAR, lastyear);

		Calendar result = Calendar.getInstance();
		result.clear();
		// Now get the first day of week.
		result.setTime(lastcalendar.getTime());
		if(!nolastdatum) // add one weeks, 7 days from a monday
			result.add(Calendar.DAY_OF_YEAR, 7);

		return result.getTime();
	}
	
	private Date getNieuweFactuurDatum(FactuurConfig factuurConfig) throws ServiceException {
		Date factuurDatumNieuw = null;

		if(factuurConfig != null) {
			if(factuurConfig.getFacturatieFrequentieCode().equals(EFacturatieFrequentie.MAANDELIJKS.getCode())) {
				factuurDatumNieuw = getNextMaandelijksDatum(factuurConfig.getLaatsteFactuurDatum());
			} else if(factuurConfig.getFacturatieFrequentieCode().equals(EFacturatieFrequentie.TWEEWEKELIJKS.getCode())) {
				factuurDatumNieuw = getNextTweewekelijksDatum(factuurConfig.getLaatsteFactuurDatum());
			} else if (factuurConfig.getFacturatieFrequentie().equals(EFacturatieFrequentie.WEKELIJKS.getCode())) {
				factuurDatumNieuw = getNextWekelijksDatum(factuurConfig.getLaatsteFactuurDatum());				
			}			
			// FOR TEST PURPOSE ONLY!!
			else if(factuurConfig.getFacturatieFrequentieCode().equals(EFacturatieFrequentie.TEST.getCode())) {
				factuurDatumNieuw = getNextTestDatum(factuurConfig.getLaatsteFactuurDatum());
			}

			// current date without time 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date currentDate = null;
			try {
				currentDate = sdf.parse(sdf.format(new Date()));
			} catch(Exception e) {
				// do nothing
			}

			if(factuurDatumNieuw.after(currentDate) || factuurDatumNieuw.equals(currentDate)) // may not be in future
				factuurDatumNieuw = null;
		}

		return factuurDatumNieuw;
	}
}