package nl.devoorkant.sbdr.business.service.cir;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;

import nl.devoorkant.insolventie.CIRException;
import nl.devoorkant.insolventie.client.CirParameters;
import nl.devoorkant.insolventie.client.WebServiceClient;
import nl.devoorkant.insolventie.converter.InspubWebserviceInsolventieConverter;
import nl.devoorkant.sbdr.business.job.FetchCirDataJob;
import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.transfer.FaillissementenOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.InsolventiePublicTransfer;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.cir.data.model.CirAansturing;
import nl.devoorkant.sbdr.cir.data.model.CirCbv;
import nl.devoorkant.sbdr.cir.data.model.CirHandelsnaam;
import nl.devoorkant.sbdr.cir.data.model.CirInsolventie;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatie;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatiekenmerk;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Websiteparam;
import nl.devoorkant.sbdr.data.service.WebsiteparamDataService;
import nl.devoorkant.sbdr.data.service.cir.CirAansturingDataService;
import nl.devoorkant.sbdr.data.service.cir.CirConfiguratieDataService;
import nl.devoorkant.sbdr.data.service.cir.CirInsolventieDataService;
import nl.devoorkant.sbdr.data.service.cir.CirPublicatieDataService;
import nl.devoorkant.sbdr.data.service.cir.CirPublicatieKenmerkDataService;
import nl.devoorkant.sbdr.data.service.cir.InsolventieDataService;
import nl.devoorkant.util.DateUtil;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.util.XMLUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationMessage;
import nl.rechtspraak.namespaces.cir01.SearchModifiedSinceResponse;
import nl.rechtspraak.namespaces.insolvency.content02.InspubWebserviceInsolvente;
import nl.rechtspraak.namespaces.insolvency.responselist02.PublicatieLijst;

@Service("insolventieService")
public class InsolventieServiceImpl implements InsolventieService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InsolventieServiceImpl.class);

    @Autowired
    Scheduler scheduler;
    
    @Value("${job.cron.fetch_cir_data}")
    String cronExpressionFetchCirData;
    
	@Autowired
	CirAansturingDataService cirAansturingDataService;
	
	@Autowired
	CirConfiguratieDataService cirConfiguratieDataService;
	
	@Autowired
	CirPublicatieKenmerkDataService cirPublicatieKenmerkDataService;
	
	@Autowired
	InsolventieDataService insolventieDataService;
	
	@Autowired
	CirInsolventieDataService cirInsolventieDataService;
	
	@Autowired
	CirPublicatieDataService cirPublicatieDataService;
	
	@Autowired
	WebsiteparamDataService websiteparamDataService;
	
	@Autowired
	WebServiceClient webserviceClient;
    
	
	@PostConstruct
	public void createSchedule() {        
        JobDetail jobDetail = buildJobDetail();
        Trigger trigger = buildJobTrigger(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start product schedule FetchCirData", e);
		}        
	}
	
	private JobDetail buildJobDetail() {

        return JobBuilder.newJob(FetchCirDataJob.class)
                .withIdentity(UUID.randomUUID().toString(), "insolventie-jobs")
                .withDescription("Fetch Cir Data")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTrigger(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "insolventie")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionFetchCirData))
    			  .build();    	
    } 
    
    /*******/
    
	@Override
    public CirParameters getCirParameters(String configuratieCode) {
    	String configuratieValue = cirConfiguratieDataService.getConfiguratieValue(configuratieCode);
        CirAansturing cirAansturing = cirAansturingDataService.getCIR_Aansturing();

        if (configuratieValue != null && cirAansturing != null)
        	return new CirParameters(configuratieValue, cirAansturing.getTijdLaatsteSynchronizatie(), cirAansturing.getGebruikersNaam(), cirAansturing.getWachtwoord());
        else
        	return null;
    }

	@Override
    public CirParameters getCirParameters() {
        CirAansturing cirAansturing = cirAansturingDataService.getCIR_Aansturing();

        if (cirAansturing != null)
        	return new CirParameters(null, cirAansturing.getTijdLaatsteSynchronizatie(), cirAansturing.getGebruikersNaam(), cirAansturing.getWachtwoord());
        else
        	return null;    	
    }

    
    /**
     * RESULTS!!!
     */
	
    private Result persistInsolventie(InspubWebserviceInsolvente poInsolvente) throws ServiceException{
        //LOGGER.info("Method persistInsolventie.");
        Result loResult = new Result();

        if (poInsolvente != null) {
	        //ApplicationContextProvider appContextProvider = new ApplicationContextProvider();
	        //ApplicationContext appContext = appContextProvider.getApplicationContext();
	        //InsolventieDataService insolventieDataService = appContext.getBean("insolventieDataService", InsolventieDataService.class);

            if (poInsolvente.getInsolvente() != null) {                
                CirInsolventie loCIR_Insolventie = null;
                Set<CirPublicatie> loCIR_Publicaties = null;
                Set<CirHandelsnaam> loCIR_HandelsNamen = null;
                Set<CirCbv> loCIR_CBVs = null;

                // Create the CIR_Insolventie.
                loCIR_Insolventie = InspubWebserviceInsolventieConverter.transformToCIR_Insolventie(poInsolvente.getInsolvente(), insolventieDataService);

                if (loCIR_Insolventie != null) {

                    // Check if the Insolventie is already registered (e.g. based on a previous CIR-PublicatieKenmerk)
                    CirInsolventie loPreviousCIR_Insolventie = insolventieDataService.getCIR_InsolventieByNummerInsolventie(loCIR_Insolventie.getNummerInsolventie());
                    if (loPreviousCIR_Insolventie != null) {
                        loResult = insolventieDataService.removeInsolventie(loPreviousCIR_Insolventie);
                    }

                    if (loResult.isSuccessful()) {
                        // Create the CIR_PublicatieGeschiedenis.
                        if (poInsolvente.getInsolvente().getPublicatiegeschiedenis() != null) {
                            loCIR_Publicaties = InspubWebserviceInsolventieConverter.transformToCIR_Publicaties(poInsolvente.getInsolvente().getPublicatiegeschiedenis(), insolventieDataService);
                        }

                        // Create the list of CIR_Handelsnaam.
                        if (poInsolvente.getInsolvente().getHandelendOnderDeNamen() != null && poInsolvente.getInsolvente().getHandelendOnderDeNamen().getHandelendOnderDeNaam() != null) {
                            loCIR_HandelsNamen = InspubWebserviceInsolventieConverter.transformToCIR_HandelsNamen(poInsolvente.getInsolvente().getHandelendOnderDeNamen().getHandelendOnderDeNaam(), insolventieDataService.getNewCIR_HandelsNaam(), insolventieDataService);
                        }

                        // Create the list of CIR_CBV.
                        if (poInsolvente.getInsolvente().getCbvers() != null && poInsolvente.getInsolvente().getCbvers().getCbv() != null) {
                            loCIR_CBVs = InspubWebserviceInsolventieConverter.transformToCIR_CBVs(poInsolvente.getInsolvente().getCbvers().getCbv(), insolventieDataService);
                        }
                        
                        // Create an Insolventie
                        try {
                        	loResult = insolventieDataService.createInsolventie(loCIR_Insolventie, loCIR_Publicaties, loCIR_HandelsNamen, loCIR_CBVs);
                        } catch (DataServiceException e) {
                        	throw new ServiceException("Error creating Insolventie: " + e.getMessage());
                        }

                    } else {
                        LOGGER.error("Method persistInsolventie. Unable to remove existing insolvancy!");
                        loResult.getValidationObject().addMessage("Probleem bij verwerken opgehaalde insolventie", ValidationConstants.MessageType.INVALID);
                    }

                } else {
                    LOGGER.error("Method persistInsolventie. Unable to create insolvancy based on the received message!");
                    loResult.getValidationObject().addMessage("Ontvangen bericht onjuist", ValidationConstants.MessageType.INVALID);
                }

            } else if (poInsolvente.getExceptie() != null) {
                // Process Error (according to the predefined list)
                // ToDo Implement specific Exceptie handling
                loResult = insolventieDataService.createCIR_Exceptie(poInsolvente.getExceptie().getErrorcode());
            }
        }

        return loResult;
    }
	
    private <T> T nodeToJAXB(Node node, Class<T> type) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return type.cast(unmarshaller.unmarshal(node));
    }
    
    private int persistModifiedSincePublicatieKenmerken(SearchModifiedSinceResponse.SearchModifiedSinceResult searchModifiedSinceResult) throws CIRException {
    	int lnAantal = 0;
    	
        if (searchModifiedSinceResult != null) {
        	try {	        	
	            PublicatieLijst loPublicatieLijst = nodeToJAXB((Node)searchModifiedSinceResult.getContent().get(0), PublicatieLijst.class);
	
	            if (loPublicatieLijst.getPublicatieKenmerk() != null) {
	                Result loResult = new Result();
	                lnAantal = loPublicatieLijst.getPublicatieKenmerk().size();
	
	                //Process PublicatieKenmerken (List)
	                if (lnAantal > 0) {
	                    loResult = insolventieDataService.createCIR_PublicatieKenmerken(loPublicatieLijst.getPublicatieKenmerk());
	                }
	
	                // Bijwerken datum laatste synchronizatie with value from extractiedatum in the received response
	                if (loResult.isSuccessful()) {
	                	insolventieDataService.setTijdLaatsteSynchronizatie(DateUtil.convertToTimestamp(XMLUtil.convertToSQLDate(loPublicatieLijst.getExtractiedatum())));
	                }
	
	            } else if (loPublicatieLijst.getExceptie() != null) {
	                // ToDo JME 13-3-2014: Process Error other exceptions
	
	                if (StringUtil.isNotEmptyOrNull(loPublicatieLijst.getExceptie().getErrorcode())) {
	
	                    if (loPublicatieLijst.getExceptie().getErrorcode().equalsIgnoreCase("5")) {
	                        LOGGER.error("Method modifiedSince. Based on exception - {} - received, a complete rebuild of the database is necessary (See webservice documentation)", loPublicatieLijst.getExceptie().getValue());
	                        insolventieDataService.removeAllInsolventies();
	                    }
	
	                    Result loResult = insolventieDataService.createCIR_Exceptie(loPublicatieLijst.getExceptie().getErrorcode());
	                }
	
	            }
	            
	            return lnAantal;
        	} catch (JAXBException e) {
        		throw new CIRException("Cannot cast nodeToJAXB: " + e.getMessage());
        	}

        } else {
            // Nothing received from web service
            LOGGER.error("Method modifiedSince. No result received");
            throw new CIRException("No response receved: " + new Date());
        }    	
    }
	
    @Override
    public void processCir() throws ServiceException {
        //LOGGER.debug("Method processCir.");
        
        try {
        	CirParameters cirParameters = getCirParameters();
        	if (cirParameters != null) {
        		SearchModifiedSinceResponse.SearchModifiedSinceResult searchModifiedSinceResult = webserviceClient.modifiedSince(cirParameters);
        		
        		persistModifiedSincePublicatieKenmerken(searchModifiedSinceResult);
        		
        		List<CirPublicatiekenmerk> toresolve = cirPublicatieKenmerkDataService.getUnprocessedCIR_PublicatieKenmerken();
        			
        		for (CirPublicatiekenmerk cirPublicatiekenmerk : toresolve) {
        			InspubWebserviceInsolvente insolvente = null;
        			try {
        				insolvente = webserviceClient.fetchCIR_PublicatieKenmerk(cirPublicatiekenmerk, cirParameters);
        			}
        			catch (JAXBException e) {
        				LOGGER.error("CIR error: " + e.getMessage());
        				LOGGER.error("cirPublicatiekenmerk: " + cirPublicatiekenmerk.getPublicatieKenmerk());
        				throw new JAXBException(e);
        			}
    				Result resultInsolvente = persistInsolventie(insolvente);
    				if (!resultInsolvente.isSuccessful()) {
    					for (ValidationMessage message : resultInsolvente.getValidationMessages()) {
    						LOGGER.error("Error processing insolvente: " + message.getMessage());
    					}
    				} else {
    					cirPublicatiekenmerk.setTijdVerwerkt(new Date());
    					cirPublicatieKenmerkDataService.saveCIR_PublicatieKenmerk(cirPublicatiekenmerk);
    				}
    			}
        	}	        

        }catch (JAXBException e) {
            throw new ServiceException(e);
        } catch (CIRException e) {
        	throw new ServiceException(e);
        }
        
    }
    
    @Override
    public InsolventiePublicTransfer findPublicData() throws ServiceException {
    	try {
			long flsmtJaar = cirInsolventieDataService.getCountOfFaillissementenDitJaar();
			long flsmtWeek = cirInsolventieDataService.getCountOfFaillissementenDezeWeek();
			long ssnsJaar = cirInsolventieDataService.getCountOfSurseancesDitJaar();
			long ssnsWeek = cirInsolventieDataService.getCountOfSurseancesDezeWeek();
			
			Websiteparam websiteparam = websiteparamDataService.find();
					
			if (websiteparam != null) {
				long vermJaar = websiteparam.getVermeldingenYtd() == null ? 0 : websiteparam.getVermeldingenYtd();
				long vermWeek = websiteparam.getVermeldingenWeek() == null ? 0 : websiteparam.getVermeldingenWeek();
				return new InsolventiePublicTransfer(flsmtJaar, flsmtWeek, ssnsJaar, ssnsWeek, new Long(websiteparam.getStartupsYtd()), new Long(websiteparam.getStartupsWeek()), new Long(vermJaar), new Long(vermWeek), websiteparam.getStoringen());
			}
			else
				return new InsolventiePublicTransfer(flsmtJaar, flsmtWeek, ssnsJaar, ssnsWeek, new Long(0), new Long(0), new Long(0), new Long(0), websiteparam.getStoringen());
    	} catch (Exception e) {
    		throw new ServiceException("Cannot fetch public CIR data and/or websiteparam: " + e.getMessage());
    	}
    }
    

    @Override
    @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
    public PageTransfer<FaillissementenOverviewTransfer> findFaillissementenAfgelopenWeek(Pageable pageable) throws ServiceException {
		PageTransfer<FaillissementenOverviewTransfer> result = null;
		
		try {
			Page<Object[]> objects = cirInsolventieDataService.findAllFaillissementenLastWeek(pageable);
			
			if (objects != null)
				result = ConvertUtil.convertPageToFaillissementenOverviewPageTransfer(objects);
						
			return result;
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		} catch (IllegalAccessException e) {
			throw new ServiceException(e);
		} catch (InvocationTargetException e) {
			throw new ServiceException(e);
		}
    }  
    
}
