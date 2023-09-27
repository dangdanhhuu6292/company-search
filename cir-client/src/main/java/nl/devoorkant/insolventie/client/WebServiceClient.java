package nl.devoorkant.insolventie.client;

import nl.devoorkant.exception.DVKException;
import nl.devoorkant.insolventie.CIRException;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatiekenmerk;
import nl.devoorkant.util.DateUtil;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.util.XMLUtil;
import nl.rechtspraak.namespaces.cir01.CIR;
import nl.rechtspraak.namespaces.cir01.CIRSoap;
import nl.rechtspraak.namespaces.cir01.GetCaseResponse;
import nl.rechtspraak.namespaces.cir01.SearchModifiedSinceResponse;
import nl.rechtspraak.namespaces.insolvency.content02.InspubWebserviceInsolvente;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.VersionTransformer;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.AddressingFeature;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebServiceClient {
    private static final Logger ioLogger = LoggerFactory.getLogger(WebServiceClient.class);   
    
    @Autowired
    ClientPasswordHandler clientPasswordHandler;

    public WebServiceClient() {
    	
    }

    /**
     * Execute all the functionality related to processing a modified since request.<br/>
     *
     * When no value is set for DatumLaatsteSynchronizatie, this value is set to 1-1-2013. This is according to the specifications in: "Technische documentatie CIR-WS 20130614.pdf"
     *
     */
    public SearchModifiedSinceResponse.SearchModifiedSinceResult modifiedSince(CirParameters cirParameters) throws CIRException, JAXBException {
        
        try {
            CIRSoap servicePort = initializeServicePort(cirParameters);

            //For testing purposes
            //BindingProvider portBP = (BindingProvider)servicePort;
            //portBP.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8090/cir.asmx");

            Date loTimeModifiedSince= cirParameters.getTijdLaatsteSynchronisatie() != null ? cirParameters.getTijdLaatsteSynchronisatie() : DateUtil.convertToTimestamp(DateUtil.composeDate(15, 3, 2015));

            ioLogger.info("Method modifiedSince CIR. Search from: {}", XMLUtil.convertToXMLGregorianCalendar(loTimeModifiedSince, XMLUtil.XMLGregorianCalendarFormat.DATETIME));

            SearchModifiedSinceResponse.SearchModifiedSinceResult searchModifiedSinceResult =  servicePort.searchModifiedSince(XMLUtil.convertToXMLGregorianCalendar(loTimeModifiedSince, XMLUtil.XMLGregorianCalendarFormat.DATETIME));

            ioLogger.info("After fetch modifiedSince CIR");
            
            return searchModifiedSinceResult;
            
        } catch (ParseException | DVKException e) {
            // ToDo --> Specify further
            throw new CIRException("Failed to execute executeModifiedSince on: " + new Date(), e);
        } catch (Exception e) {
            ioLogger.error("Method modifiedSince. Unexpected error: {}", e);
            throw new CIRException("Failed to execute executeModifiedSince on: " + new Date(), e);
        }

    }

    /**
     * Execute all the functionality related to fetch details of one PublicatieKenmerk.<br/>
     *
     * When no value is set for DatumLaatsteSynchronizatie, this value is set to 1-1-2013. This is according to the specifications in: "Technische documentatie CIR-WS 20130614.pdf"
     *
     */
    public InspubWebserviceInsolvente fetchCIR_PublicatieKenmerk(CirPublicatiekenmerk poCIR_PublicatieKenmerk, CirParameters cirParameters) throws CIRException, JAXBException {
        ioLogger.info("Method verwerkCIR_PublicatieKenmerk.");
        //boolean lbResult = false;

        if (poCIR_PublicatieKenmerk != null && StringUtil.isNotEmptyOrNull(poCIR_PublicatieKenmerk.getPublicatieKenmerk())) {
            CIRSoap servicePort = initializeServicePort(cirParameters);
            GetCaseResponse.GetCaseResult getCaseResult = servicePort.getCase(poCIR_PublicatieKenmerk.getPublicatieKenmerk());

            if (getCaseResult != null) {
            	InspubWebserviceInsolvente loInsolvente = null;
            	try {
            		loInsolvente = nodeToJAXB((Node)getCaseResult.getContent().get(0), InspubWebserviceInsolvente.class);
            	}
    			catch (JAXBException e) {
    				ioLogger.error("CIR error: " + e.getMessage());
    				ioLogger.error("Error processing cirPublicatiekenmerk: " + poCIR_PublicatieKenmerk.getPublicatieKenmerk());    				
    			}            		

                //lbResult = persistInsolventie(loInsolvente).isSuccessful();
                return loInsolvente;
            } else {
                // Nothing received from web service
                ioLogger.error("Method verwerkCIR_PublicatieKenmerk. No result received");
                throw new CIRException("No response receved: " + new Date());
            }
        } else {
        	throw new CIRException("No publicatiekenmerk to fetch from CIR");
        }
        //return lbResult;
    }

    private <T> T nodeToJAXB(Node node, Class<T> type) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return type.cast(unmarshaller.unmarshal(node));
    }

    private CIRSoap initializeServicePort(CirParameters cirParameters) throws CIRException {
        ioLogger.info("Method initializeServicePort.");

        CIR cir = new CIR();
        
        // To enable wsa addressing element in request. This is required for the web service!!
        CIRSoap loServicePort = cir.getCIRSoap12(new AddressingFeature(true,true));

        if (loServicePort != null) {

            // AdjustHeader
            Client loClient = ClientProxy.getClient(loServicePort);
            Endpoint endpoint = loClient.getEndpoint();

            AddressingProperties maps = new AddressingProperties();
            maps.exposeAs(VersionTransformer.Names200408.WSA_NAMESPACE_NAME);                        

//            ObjectFactory wsaObjectFactory = new ObjectFactory();
//            AttributedURIType messageID = wsaObjectFactory.createAttributedURIType();
//            messageID.setValue("urn:uuid:" + System.currentTimeMillis());
//            maps.setMessageID(messageID);

            //((BindingProvider)loServicePort).getRequestContext().put(CLIENT_ADDRESSING_PROPERTIES, maps);

            //loClient.getRequestContext().put(CLIENT_ADDRESSING_PROPERTIES, maps);
            loClient.getRequestContext().put("javax.xml.ws.addressing.context", maps);
            
            loClient.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY,
            		true);
            
            Map<String, Object> props = new HashMap<String, Object>();
            props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
            props.put(WSHandlerConstants.USER, cirParameters.getUsername());
            ioLogger.info("Username voor CIR is {}", cirParameters.getUsername());
            props.put(WSHandlerConstants.PW_CALLBACK_REF, clientPasswordHandler);
            

            endpoint.getOutInterceptors().add(new WSS4JOutInterceptor(props));
            //WSAddressingFeature wsaaddress = new WSAddressingFeature();
            //endpoint.getActiveFeatures().add(new WSAddressingFeature());            
        } else {
            throw new CIRException("Error initializing serviceport");
        }

        return loServicePort;
    }

}


