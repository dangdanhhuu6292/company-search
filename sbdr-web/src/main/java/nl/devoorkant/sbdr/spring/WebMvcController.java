package nl.devoorkant.sbdr.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebMvcController {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebMvcController.class);
		
//	@RequestMapping(value = "/{[path:[^\\.]*}")
//	public String redirect() {
//	  return "forward:/";
//	}
	
    @RequestMapping(value="/register/**", method = RequestMethod.GET) // "/register/"
    public String homepage(){
        return "/dashboard.html";
    }
    
   // @RequestMapping(value="/api/oauth/token")
    public String noDirectOAuth() {
    	LOGGER.debug("No direct oauth token call allowed, so redirect to index.html");
    	return "/dashboard.html";
    }
  
}
