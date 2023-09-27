package nl.devoorkant.sbdr.spring;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication(scanBasePackages= {"nl.devoorkant.sbdr.data.service", "nl.devoorkant.sbdr.data.service.cir", "nl.devoorkant.sbdr.business.**", "nl.devoorkant.insolventie.client",
										  "nl.devoorkant.sbdr.spring", "nl.devoorkant.cxf", "nl.devoorkant.sbdr.oauth", "nl.devoorkant.sbdr.ws", "nl.devoorkant.sbdr.websocket"}, 
                       exclude={DataSourceAutoConfiguration.class})
// Non-SpringBootApplication config
@ComponentScan(basePackages = {"nl.devoorkant.sbdr.data.service", "nl.devoorkant.sbdr.data.service.cir", "nl.devoorkant.sbdr.business.**", "nl.devoorkant.insolventie.client"})
@ComponentScan(basePackages = {"nl.devoorkant.sbdr.spring", "nl.devoorkant.cxf", "nl.devoorkant.sbdr.oauth", "nl.devoorkant.sbdr.ws", "nl.devoorkant.sbdr.websocket"})
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class}) 
 
public class ZbrApp extends SpringBootServletInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZbrApp.class);
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		LOGGER.info("Running in external container...");
		
		Map<String, Object> map = new HashMap<>();
		map.put("server.servlet.context-path", "/dashboard");
		map.put("spring.jersey.application-path",  "/services");
		map.put("spring.config.name", "application,sbdr");
		map.put("spring.config.location", "classpath:/");
		//map.put("server.port", "8585");
		application.properties(map);
		
		return application.sources(ZbrApp.class);
	}	
	
	public static void main(String[] args) throws Exception {

		LOGGER.info("Running embedded Tomcat...");
		
		System.setProperty("server.servlet.context-path", "/dashboard");
		System.setProperty("spring.jersey.application-path",  "/services");
		LOGGER.info("TEST LOG");
		
		ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(ZbrApp.class)
                .properties("spring.config.name:application,sbdr",
                        "spring.config.location:classpath:/tst")
                .build().run(args);		
		
//		ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(ZbrApp.class)
//                .properties("server.servlet.context-path", "/register",
//                		"spring.jersey.application-path",  "/services",
//                		"spring.config.name:application,sbdr",
//                        "spring.config.location:classpath:/")
//                .build().run(args);
	}

}
