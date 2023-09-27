package nl.devoorkant.insolventie.client;

import nl.devoorkant.sbdr.cir.data.model.CirAansturing;
import nl.devoorkant.sbdr.data.service.cir.CirAansturingDataService;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jmeekel
 * Date: 6-3-14
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ClientPasswordHandler implements CallbackHandler {
    private static final Logger ioLogger = LoggerFactory.getLogger(ClientPasswordHandler.class);

    @Autowired
    CirAansturingDataService cirAansturingDataService;
    
    public ClientPasswordHandler() {
    	
    }
    
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        ioLogger.info("Calling back");
        WSPasswordCallback pc = (WSPasswordCallback)callbacks[0];
        //ioLogger.info("Wachtwoord voor CIR is {}", ioServiceFacadeSB.getCIRPassword());
        try {
	        //ApplicationContextProvider appContextProvider = new ApplicationContextProvider();
	        //ApplicationContext appContext = appContextProvider.getApplicationContext();
	        //CirAansturingDataService cirAansturingDataService = appContext.getBean("cirAansturingDataService", CirAansturingDataService.class);
	        
	        CirAansturing cirAansturing = cirAansturingDataService.getCIR_Aansturing();
	        if (cirAansturing != null) {
	        	ioLogger.debug("Aansturing params found in password callback.");
	        	pc.setPassword(cirAansturing.getWachtwoord());	        	
	        }
	        else
	        	ioLogger.error("No aansturing params found in password callback. No password set.");
        } catch (Exception e) {
        	ioLogger.error("No aansturingDataService bean found in password callback. No password set");
        }
    }

}
