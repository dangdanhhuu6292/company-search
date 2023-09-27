package nl.devoorkant.sbdr.ws;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.service.MobileGebruikerService;
import nl.devoorkant.sbdr.business.transfer.GebruikersDetails;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.util.ERol;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.oauth.AuthenticationFacade;
import nl.devoorkant.sbdr.oauth.OAuth2AuthorizationServerConfiguration;
import nl.devoorkant.sbdr.oauth.ServerSecurityConfig;
import nl.devoorkant.sbdr.ws.auth.GebruikersDetailsService;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.UserTransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.stereotype.Component;


@Component
@Path("/UserService/user")
public class UserResource {
    @Value("${api.client_id}")
    private String apiClientId;
    
    @Value("${api.secret}")
    private String apiSecret;
	//@Autowired
	//@Qualifier("authenticationManager")
	//private AuthenticationManager authManager;
	@Autowired
	ServerSecurityConfig securityConfig;
	@Autowired
	OAuth2AuthorizationServerConfiguration oauth2AuthorizationServer;	
	@Autowired
	DefaultOAuth2RequestFactory defaultOAuth2RequestFactory;	
	@Autowired
	AuthenticationFacade authenticationFacade;

	@Autowired
	private GebruikerService gebruikerService;

	@Autowired
	private GebruikersDetailsService gebruikersDetailsService;
	
	@Autowired
	private UserDetailsService userService;

	@Autowired
	private MobileGebruikerService mobileGebruikerService;

	private static Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

	public Map<String, Boolean> createRoleMapSpring(UserDetails userDetails) {
		Map<String, Boolean> roles = new HashMap<String, Boolean>();
		for(org.springframework.security.core.GrantedAuthority authority : userDetails.getAuthorities()) {
			if(authority != null) {
				LOGGER.debug("createRole: " + authority.getAuthority());
				roles.put(authority.getAuthority(), Boolean.TRUE);
			}
		}
		//extra auth for test purpose
		//roles.put("test", Boolean.TRUE);

		return roles;
	}

	/**
	 * Retrieves the currently logged in user.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@Path("userdata")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserTransfer getUser() {
		LOGGER.debug("User GET");

		UserTransfer userTransfer = null;

		try {
			//Principal principal = (Principal) authenticationFacade.getAuthentication().getPrincipal();
			//GebruikersDetails userDetails = getPrincipal(principal);
			GebruikersDetails userDetails = (GebruikersDetails) authenticationFacade.getAuthentication().getPrincipal();
			
			userTransfer = new UserTransfer(userDetails.getUsername(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getFunctie(), userDetails.getAfdeling(), userDetails.getEmailAdres(), userDetails.getUserId(), userDetails.getBedrijfId(), userDetails.getBedrijfNaam(), userDetails.getGebruikerTelefoonNummer(), userDetails.getBedrijfTelefoonNummer(), userDetails.isKlant(), userDetails.isProspect(), userDetails.isAdresOk(), createRoleMapSpring(userDetails), userDetails.isActionsPresent(), userDetails.getShowHelp(), userDetails.getGeslacht(), userDetails.isMobileUser(), userDetails.isBedrijfManager());
			
			Boolean auth = (Boolean) userTransfer.getRoles().get(ERol.APITOEGESTAAN.getCode());
			if (auth != null && auth) {
				// provide Basic auth string to show user how to connect to API
				userTransfer.setClientId(apiClientId);
				userTransfer.setClientSecret(apiSecret);
			}				
			
		} catch(Exception e2) {
			LOGGER.error(e2.getMessage());
			LOGGER.error("No principal object found via Spring(1) or Mule(2) Security");
		}
		//		}


		return userTransfer;
	}
	
	/**
	 * Switches to another company of the currently logged in user.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@Path("userdataOfCompany")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserTransfer getUserOfCompany(@QueryParam("bedrijfId") String obfCurrentBedrijfId, @Context Principal principal) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfCurrentBedrijfId);
		LOGGER.debug("UserDataOfCompany POST");

		UserTransfer userTransfer = getUser();

		try {
			GebruikersDetails userDetails = gebruikersDetailsService.findById(userTransfer.getUserId(), bedrijfId, false);

			if (userDetails != null) {
				//UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				//authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
				// change logged in user update Spring security GebruikerDetails
				
				// also for Mule
				// copy grants
				ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				for(org.springframework.security.core.GrantedAuthority gauth : userDetails.getAuthorities()) {
					GrantedAuthority gauthMule = new SimpleGrantedAuthority(gauth.getAuthority());
					authorities.add(gauthMule);
				}

				Collection<GrantedAuthority> cgauthMule = new Vector<GrantedAuthority>();
				Collections.addAll(cgauthMule, new GrantedAuthority[authorities.size()]);
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, cgauthMule);
				
//
//
//				LOGGER.debug("before setDetails");
//				//authRequest.setDetails(event.getMessage().getInboundProperty(MuleProperties.MULE_ENDPOINT_PROPERTY)); // getProperty deprecated
//				
//				SecurityContextHolder.getContext().setAuthentication(authRequest);
//				
				Authentication authentication = null;
				
			    HashMap<String, String> parameters = new HashMap<String, String>();
			    parameters.put("client_id", "webClientIdPassword");
			    parameters.put("grant_type", "password");
			    parameters.put("password", "secret");
			    parameters.put("scope", "read, write");
			    parameters.put("username", userDetails.getUsername());

			    AuthorizationRequest authorizationRequest = defaultOAuth2RequestFactory.createAuthorizationRequest(parameters);
			    authorizationRequest.setApproved(true);			
				
			    OAuth2Request oauth2Request = defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);			
				
				// Authenticate via Spring security with Spring manager
				//authentication = this.authManager.authenticate(authenticationToken);
				authentication = securityConfig.authenticationManagerBean().authenticate(authenticationToken);
				
				OAuth2Authentication authenticationRequest = new OAuth2Authentication(oauth2Request, authentication);
			    authenticationRequest.setAuthenticated(true);			
				
				// Set Spring Security context
			    SecurityContextHolder.getContext().setAuthentication(authentication);
				
				userTransfer = getUser();
				//userTransfer = new UserTransfer(userDetails.getUsername(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getFunctie(), userDetails.getAfdeling(), userDetails.getEmailAdres(), userDetails.getUserId(), userDetails.getBedrijfId(), userDetails.getBedrijfNaam(), userDetails.getGebruikerTelefoonNummer(), userDetails.getBedrijfTelefoonNummer(), userDetails.isKlant(), userDetails.isProspect(), userDetails.isAdresOk(), createRoleMapSpring(userDetails), userDetails.isActionsPresent(), userDetails.getShowHelp(), userDetails.getGeslacht(), userDetails.isMobileUser(), userDetails.isBedrijfManager());
			}
		} catch(Exception e2) {
			LOGGER.error(e2.getMessage());
			LOGGER.error("No principal object found via Spring(1) or Mule(2) Security");
		}

		return userTransfer;
	}	

	@Path("mLogout")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response mLogout(String MCK, @Context Principal principal) {
		ErrorResource error = null;

		//GebruikersDetails userDetails = getPrincipal(principal);
		GebruikersDetails userDetails = (GebruikersDetails) principal;

		if(userDetails != null) {
			try{
				mobileGebruikerService.removeMobileGebruikerRecord(MCK);

				if(!gebruikerService.logoutGebruiker(userDetails.getSignature(), userDetails.getUserId())){
					error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
				}
			} catch(Exception e){
				LOGGER.error("mLogout, error: " + e.getMessage());
				error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
			}
		} else{
			error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
		}

		return Response.ok(error).build();
	}

	@Path("logout")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout() {
		ErrorResource error = null;

		//Principal principal = (Principal) authenticationFacade.getAuthentication().getPrincipal();
		//GebruikersDetails userDetails = getPrincipal(principal);
		GebruikersDetails userDetails = (GebruikersDetails) authenticationFacade.getAuthentication().getPrincipal();

		if(userDetails != null) {
			if(!gebruikerService.logoutGebruiker(userDetails.getSignature(), userDetails.getUserId())){
				error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
			}
		} else error = new ErrorResource(ErrorResource.GENERAL_FAILURE);

		return Response.ok(error).build();
	}

	@Path("updateShowHelp")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateShowHelp(Integer showHelp, @Context Principal principal) {
		ErrorResource error = null;

		try {
			//GebruikersDetails userDetails = getPrincipal(principal);
			GebruikersDetails userDetails = (GebruikersDetails) principal;

			if(userDetails != null) {
				boolean result = gebruikerService.updateShowHelp(userDetails.getUserId(), showHelp);
				if(!result) error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
			} else error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
		} catch(Exception e) {
			error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
		}
		return Response.ok(error).build();
	}

//	private GebruikersDetails getPrincipal(Principal principal) {
//		try {
//			LOGGER.debug("Find principal via Mule");
//			//org.acegisecurity.Authentication authentication2 = org.acegisecurity.context.SecurityContextHolder.getContext().getAuthentication();
//			//principal = authentication2.getPrincipal();
//			LOGGER.debug("Mule Principal found");
//
//			LOGGER.debug("There is a principal: " + principal.toString());
//
//			//org.acegisecurity.userdetails.UserDetails userDetails = (org.acegisecurity.userdetails.UserDetails) principal;
//			GebruikersDetails userDetails = (GebruikersDetails) principal;
//			LOGGER.debug("Principal UserDetails: " + userDetails.getUsername() ); //+ " " + userDetails.getPassword()
//
//			return userDetails;
//		} catch(Exception e2) {
//			LOGGER.error(e2.getMessage());
//			LOGGER.error("No principal object found via Spring(1) or Mule(2) Security");
//
//			return null;
//		}
//	}
}