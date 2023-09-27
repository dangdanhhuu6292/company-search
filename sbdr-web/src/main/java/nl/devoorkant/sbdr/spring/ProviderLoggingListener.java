package nl.devoorkant.sbdr.spring;

import java.util.Set;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Provider
public class ProviderLoggingListener implements ApplicationEventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderLoggingListener.class);
	private final String applicationPath;
	
    public ProviderLoggingListener(String applicationPath) {
        this.applicationPath = applicationPath;
    }
    
    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_FINISHED: {
                Set<Class<?>> providers = event.getProviders();
                ResourceConfig immutableConfig = event.getResourceConfig();
                ResourceModel resourcesModel = event.getResourceModel();
                if (providers != null) {
                	for (Class cls : providers)
                		LOGGER.info("Jersey provider class registered: " + cls.getName() + "\n");
                }
                LOGGER.info("");
                break;
            }
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }
}
