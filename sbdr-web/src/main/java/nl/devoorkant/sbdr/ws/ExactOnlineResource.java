package nl.devoorkant.sbdr.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.devoorkant.sbdr.business.service.ExactOnlineService;
import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.data.model.ExactOnlineAccess;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.ExactOnlineAccessTransfer;
import nl.devoorkant.sbdr.ws.transfer.UserTransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Path("/ExactOnlineService/services")
public class ExactOnlineResource {
    private static Logger LOGGER = LoggerFactory.getLogger(ExactOnlineResource.class);

    @Value("${sbdrweb_base_uri}")
	private String sbdrwebBaseUri;   

	@Value("${exactonline_redirect_web_uri}")
	private String exactOnlineRedirectWebUri;   
	
    @Autowired
    ExactOnlineService exactOnlineService;

	@Autowired
	private GebruikerService gebruikerService;
    
	@Autowired
	private UserResource userResource;
    
	/**
	 * Callback URL for ExactOnline oauth2 authentication.
	 * 
	 * @return
	 */
	@GET
	@Path("/callback")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchToken(@QueryParam("code") String code) 
	{
		ErrorResource error = null;
		Response httpResponse = null;
		
		LOGGER.info("ExactOnline callback authenticate code Value: " + code);
		
		try {
			// call http POST tokenRequest
			exactOnlineService.tokenRequest(code);
		} catch (Exception e) {
			// do nothing
		}
		
		// redirect to dashboard in case callback is initiated by front-end activity (= oauth login)
		return Response.status(Status.MOVED_PERMANENTLY).header("Location", sbdrwebBaseUri + exactOnlineRedirectWebUri).build();					
	}

	@GET
	@Path("/exactOnlineAccess")
	@Produces(MediaType.APPLICATION_JSON)
	public Response exactOnlineAccess() {
		ExactOnlineAccessTransfer result = null;
		ErrorResource error = null;
		
		try {
			UserTransfer user = userResource.getUser();
	
			if(user != null) {
				if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.EXACTONLINE)) {
					result = new ExactOnlineAccessTransfer("notAllowed");					
				} else {
					ExactOnlineAccess exactOnlineAccess = exactOnlineService.findExactOnlineAccess();
					if (exactOnlineAccess != null && exactOnlineAccess.getAccessToken() != null)
						result = new ExactOnlineAccessTransfer("connected");
					else
						result = new ExactOnlineAccessTransfer("notConnected");
				}
			}
		} catch (Exception e) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_EXACTONLINEACCESS);			
		}
		
        if (error != null)
        	return Response.ok(error).build();		        	
        else
        	return Response.ok(result).build();		
		
	}

}
