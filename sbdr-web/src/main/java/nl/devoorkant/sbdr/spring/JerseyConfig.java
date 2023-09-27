package nl.devoorkant.sbdr.spring;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.ext.ContextResolver;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import nl.devoorkant.sbdr.business.transfer.ActivatieCodeTransfer;
import nl.devoorkant.sbdr.business.transfer.AdminAlertTransfer;
import nl.devoorkant.sbdr.business.transfer.AdminOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.AlertOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.BedrijfEntityTransfer;
import nl.devoorkant.sbdr.business.transfer.BedrijfReportTransfer;
import nl.devoorkant.sbdr.business.transfer.BedrijfTransfer;
import nl.devoorkant.sbdr.business.transfer.BedrijfTransferNs;
import nl.devoorkant.sbdr.business.transfer.BedrijfTransferXml;
import nl.devoorkant.sbdr.business.transfer.BriefBatchTransfer;
import nl.devoorkant.sbdr.business.transfer.ChartDataTransfer;
import nl.devoorkant.sbdr.business.transfer.CompanyInfoEntityTransfer;
import nl.devoorkant.sbdr.business.transfer.ContactMomentTransfer;
import nl.devoorkant.sbdr.business.transfer.ExceptionBedrijfOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.FactuurTransfer;
import nl.devoorkant.sbdr.business.transfer.FaillissementenOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikerBedrijfTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikerTransfer;
import nl.devoorkant.sbdr.business.transfer.HistorieTransfer;
import nl.devoorkant.sbdr.business.transfer.InsolventiePublicTransfer;
import nl.devoorkant.sbdr.business.transfer.InternalProcessTransfer;
import nl.devoorkant.sbdr.business.transfer.KlantBedrijfOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.KvkBestuurderFunctieTransfer;
import nl.devoorkant.sbdr.business.transfer.KvkBestuurderTransfer;
import nl.devoorkant.sbdr.business.transfer.KvkDossierTransfer;
import nl.devoorkant.sbdr.business.transfer.MeldingOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.MeldingTransfer;
import nl.devoorkant.sbdr.business.transfer.MonitoringDetailsTransfer;
import nl.devoorkant.sbdr.business.transfer.MonitoringOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.NotificationPublicTransfer;
import nl.devoorkant.sbdr.business.transfer.NotificationsPublicTransfer;
import nl.devoorkant.sbdr.business.transfer.OpmerkingenTransfer;
import nl.devoorkant.sbdr.business.transfer.RemovedBedrijfOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.ReportRequestedTransfer;
import nl.devoorkant.sbdr.business.transfer.SearchResultsOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.SupportBestandTransfer;
import nl.devoorkant.sbdr.business.transfer.SupportTransfer;
import nl.devoorkant.sbdr.data.DataLast24h;
import nl.devoorkant.sbdr.data.DataStatusAantal;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.KortingsCode;
import nl.devoorkant.sbdr.ws.AccountResource;
import nl.devoorkant.sbdr.ws.CompanyResource;
import nl.devoorkant.sbdr.ws.DashboardResource;
import nl.devoorkant.sbdr.ws.DocumentResource;
import nl.devoorkant.sbdr.ws.ExactOnlineResource;
import nl.devoorkant.sbdr.ws.ExternalResource;
import nl.devoorkant.sbdr.ws.GebruikerResource;
import nl.devoorkant.sbdr.ws.InternalProcessResource;
import nl.devoorkant.sbdr.ws.KortingsCodeResource;
import nl.devoorkant.sbdr.ws.LoginResource;
import nl.devoorkant.sbdr.ws.NewAccountResource;
import nl.devoorkant.sbdr.ws.SupportResource;
import nl.devoorkant.sbdr.ws.UserNsResource;
import nl.devoorkant.sbdr.ws.UserResource;
import nl.devoorkant.sbdr.ws.transfer.CompanyAccount;
import nl.devoorkant.sbdr.ws.transfer.ExactOnlineAccessTransfer;
import nl.devoorkant.sbdr.ws.transfer.GenericBooleanTransfer;
import nl.devoorkant.sbdr.ws.transfer.KlantEntityTransfer;
import nl.devoorkant.sbdr.ws.transfer.RecaptchaSiteKeyTransfer;
import nl.devoorkant.sbdr.ws.transfer.TariefTransfer;
import nl.devoorkant.sbdr.ws.transfer.UserAccount;
import nl.devoorkant.sbdr.ws.transfer.UserTransfer;
import nl.devoorkant.sbdr.ws.transfer.WebSocketTransfer;

@Configuration
//@ApplicationPath("/services") // /register/services
public class JerseyConfig extends ResourceConfig{
	private static final Logger LOGGER = LoggerFactory.getLogger(JerseyConfig.class);
	//private final Class<?>[] cTypes = {ActivatieCodeTransfer.class, AdminAlertTransfer.class, AdminOverviewTransfer.class, AlertOverviewTransfer.class, BedrijfEntityTransfer.class, BedrijfReportTransfer.class, BedrijfTransfer.class, BedrijfTransferNs.class, BedrijfTransferXml.class, BigDecimal.class, BriefBatchTransfer.class, ChartDataTransfer.class, CompanyAccount.class, CompanyInfoEntityTransfer.class, ContactMomentTransfer.class, DataLast24h.class, DataStatusAantal.class, ExceptionBedrijfOverviewTransfer.class, FactuurTransfer.class, FaillissementenOverviewTransfer.class, GebruikerTransfer.class, GebruikerBedrijfTransfer.class, GenericBooleanTransfer.class, HistorieTransfer.class, InsolventiePublicTransfer.class, InternalProcessTransfer.class, Klant.class, KlantBedrijfOverviewTransfer.class, KlantEntityTransfer.class, KortingsCode.class, KvkDossierTransfer.class, KvkBestuurderTransfer.class, KvkBestuurderFunctieTransfer.class, MeldingOverviewTransfer.class, MeldingTransfer.class, MonitoringDetailsTransfer.class, MonitoringOverviewTransfer.class, NotificationPublicTransfer.class, NotificationsPublicTransfer.class, OpmerkingenTransfer.class, RecaptchaSiteKeyTransfer.class, RemovedBedrijfOverviewTransfer.class, ReportRequestedTransfer.class, SearchResultsOverviewTransfer.class, SupportTransfer.class, SupportBestandTransfer.class, TariefTransfer.class, UserAccount.class, UserTransfer.class, ExactOnlineAccessTransfer.class, WebSocketTransfer.class};

    public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
        private final ObjectMapper mapper;

        public ObjectMapperContextResolver() {
            mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JaxbAnnotationModule module = new JaxbAnnotationModule();
	         // configure as necessary
	         mapper.registerModule(module);          
	        
            LOGGER.info("Jersey contextResolver initialized...");

        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
           return mapper;
        }
    }
    
//	public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
//        private final ObjectMapper mapper;
//
//        
//        public ObjectMapperContextResolver() {
//            mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(Include.NON_NULL);
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            JaxbAnnotationModule module = new JaxbAnnotationModule();
//	         // configure as necessary
//	         mapper.registerModule(module);          
//	        
//            LOGGER.info("Jersey contextResolver initialized...");
//        }
//
//        @Override
//        public ObjectMapper getContext(Class<?> type) {
//        	LOGGER.info("return Jersey mapper");
//           return mapper;
//        }
//    }
	
	public JerseyConfig(JerseyProperties jerseyProperties) {
		LOGGER.info("Jersey services entering...");
				
		register(new ObjectMapperContextResolver());
		
	    /*register(AccountResource.class);
	     register(CompanyResource.class);
	      register(DashboardResource.class);
	      register(DocumentResource.class);
	      register(ExactOnlineResource.class);
	      register(GebruikerResource.class);
	      register(InternalProcessResource.class);
	      register(KortingsCodeResource.class);
	      register(LoginResource.class);
	      register(NewAccountResource.class);
	      register(SupportResource.class);
	      register(MultiPartFeature.class);
	      register(UserNsResource.class);
	      register(UserResource.class);
	      register(ExternalResource.class);*/
		register(MultiPartFeature.class);
		
		// Resource components
		//packages("nl.devoorkant.sbdr.ws");
		  
		// Transfer classes
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
		    .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
		    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
		    .filterInputsBy(new FilterBuilder()
		    		.include(FilterBuilder.prefix("nl.devoorkant.sbdr.ws"))
		    		.include(FilterBuilder.prefix("nl.devoorkant.sbdr.business.transfer"))
		    		.include(FilterBuilder.prefix("nl.devoorkant.sbdr.ws.transfer"))
		    		.include(FilterBuilder.prefix("nl.devoorkant.sbdr.data"))
		    		.include(FilterBuilder.prefix("nl.devoorkant.sbdr.data.model"))));		  
		  
		Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);		
		
		registerClasses(classes);
		
		// to debug registered providers and resources
		register(new ProviderLoggingListener(jerseyProperties.getApplicationPath()));
		
	    LOGGER.info("Jersey services registered");		
	}
	
//	@Bean
//	  ResourceConfig resourceConfig() {		
//		LOGGER.info("Jersey services entering...");
//		ResourceConfig resConf = new ResourceConfig();	
//		  resConf.register(new ObjectMapperContextResolver());
//	      resConf.register(AccountResource.class);
//	      resConf.register(CompanyResource.class);
//	      resConf.register(DashboardResource.class);
//	      resConf.register(DocumentResource.class);
//	      resConf.register(ExactOnlineResource.class);
//	      resConf.register(GebruikerResource.class);
//	      resConf.register(InternalProcessResource.class);
//	      resConf.register(KortingsCodeResource.class);
//	      resConf.register(LoginResource.class);
//	      resConf.register(NewAccountResource.class);
//	      resConf.register(SupportResource.class);
//	      resConf.register(MultiPartFeature.class);
//	      resConf.register(UserNsResource.class);
//	      resConf.register(UserResource.class);
//	      resConf.register(ExternalResource.class);
//	      LOGGER.info("Jersey services registered");
//	      Set<Class<?>> types = new HashSet<Class<?>>(Arrays.asList(cTypes));
//	      resConf.registerClasses(types);
//	      return resConf;
//	  }	
	
	@Bean (name = "passwordEncoder")
	  public StandardPasswordEncoder passwordEncoder() {
	    return new StandardPasswordEncoder("ThisIsASecretSoChangeMee");
	  }	
}
