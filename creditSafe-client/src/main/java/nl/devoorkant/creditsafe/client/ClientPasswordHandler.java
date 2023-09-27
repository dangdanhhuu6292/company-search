package nl.devoorkant.creditsafe.client;

//import nl.devoorkant.insolventie.facade.ServiceFacadeSB;
import org.apache.ws.security.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Callback Handler for Client Passwords.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public class ClientPasswordHandler implements CallbackHandler {
    private static final Logger ioLogger = LoggerFactory.getLogger(ClientPasswordHandler.class);

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        ioLogger.info("Calling back");
        WSPasswordCallback pc = (WSPasswordCallback)callbacks[0];

        // test omgeving
       // pc.setPassword("sdbr1357");
        
        // live omgeving
        pc.setPassword("horizonbouw335691"); //"westklus148925"
    }

    /**
     * Load facade object.<br/>
     *
     * @return Facade object.
     */
/*    private ServiceFacadeSB loadServiceFacade() {
        ServiceFacadeSB loObject = null;
        ioLogger.info("Method loadFacade.");

        try {
            Context loContext = new InitialContext();

            loObject = (ServiceFacadeSB)loContext.lookup("java:app/cir-service-0.0.1-SNAPSHOT/ServiceFacadeSB");

        } catch (NamingException poEx) {
            ioLogger.error("Unable to load ServiceFacade.", poEx);
        }

        return loObject;
    }*/
}
