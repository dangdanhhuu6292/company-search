package nl.devoorkant.sbdr.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nl.devoorkant.sbdr.business.converter.SbdrResponseConverter;
import nl.devoorkant.sbdr.business.service.TestSbdrService;
import nl.devoorkant.sbdr.business.wrapper.HelloResponseWrapper;
//import nl.devoorkant.sbdr.xml.vo.EdoResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Path("/rest")
public class SbdrtestController {
    private static Logger LOGGER = LoggerFactory.getLogger(SbdrtestController.class);
    
    @Autowired
    private SbdrResponseConverter sbdrResponseConverter;

    @Autowired
    private TestSbdrService edoService;
    	
    @GET
    @Path("/sbdrtest")
    @Produces({"application/xml","application/json"})
    @Consumes({"application/xml","application/json","application/x-www-form-urlencoded"})
	public  String sbdrtest(@PathParam("activitiID") String activitiID) {
        String response = null;

        try {
            // Validate the request message. Throws a {@link ValidationEception} on errors.            
        	//messageValidatorService.validateActivitiRequest(request);
        	
        	//if (this.isAdmin()) {
            // Delegate request to the Activiti service
            HelloResponseWrapper responseWrapper = edoService.hello();
        	
            response = sbdrResponseConverter.convertHelloResponse(responseWrapper);
//        } catch (ValidatorConfigurationException e) {
//            if (e.getCause() != null) {
//                LOGGER.error("SingleBkrRequest validator configuration failed", e.getCause());
//            }
//
//            if (!e.toString().equals("")) {
//                LOGGER.error("SingleBkrRequest validator configuration errors:\n{}", e.toString());
//            }
//
//            response.setErrors(ConvertUtil.convertErrors(e));
//        } catch (BkrServiceException e) {
//            LOGGER.error("SingleBkr service error", e);
//            response.setErrors(ConvertUtil.convertErrors(e));
//            
        } catch (Throwable e) {
            LOGGER.error("Activiti Application Error", e);
            //response.setErrors(ConvertUtil.convertErrors(e));
        }
        return response;
    }
    
	private boolean isAdmin()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal instanceof String && ((String) principal).equals("anonymousUser")) {
			return false;
		}
		UserDetails userDetails = (UserDetails) principal;

		for (GrantedAuthority authority : userDetails.getAuthorities()) {
			if (authority.toString().equals("admin")) {
				return true;
			}
		}

		return false;
	}    
}
