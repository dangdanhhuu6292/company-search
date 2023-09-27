package nl.devoorkant.sbdr.ws.auth;

import nl.devoorkant.sbdr.business.service.BedrijfService;
import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.service.MobileGebruikerService;
import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.transfer.AlertOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikerTransfer;
import nl.devoorkant.sbdr.business.transfer.GebruikersDetails;
import nl.devoorkant.sbdr.business.transfer.LoginAllowed;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.util.CompareUtil;
import nl.devoorkant.sbdr.data.model.MobileGebruiker;
import nl.devoorkant.sbdr.data.util.ERol;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.oauth.AuthenticationFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

@Service("gebruikersDetailsService")
public class GebruikersDetailsServiceImpl implements GebruikersDetailsService {
	private static Logger LOGGER = LoggerFactory.getLogger(GebruikersDetailsService.class);

	@Value("${application.client_id}")
	private String applicationClientId;

	@Value("${api.client_id}")
	private String apiClientId;

	@Autowired
	BedrijfService bedrijfService;
	@Autowired
	GebruikerService gebruikerService;
	@Autowired
	MobileGebruikerService mobileGebruikerService;
	
	@Autowired
	AuthenticationFacade authenticationFacade;

	@Override
	public GebruikersDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
		return findByName(token.getName(), null);
	}

	@Override
	public GebruikersDetails findByName(String name, Integer bedrijfId) {

		GebruikersDetails gebruikersDetails = null;

		try {
			GebruikerTransfer gebruikerTransfer = gebruikerService.findTransferByGebruikersnaam(name, bedrijfId);

			if(gebruikerTransfer != null)
				LOGGER.info("findByName: " + gebruikerTransfer.getGebruikersNaam() + " pwd: " + gebruikerTransfer.getWachtwoord() + " roles: " + gebruikerTransfer.getRoles());
			else LOGGER.info("findByName: No user found!");

			if(gebruikerTransfer != null) {
				gebruikersDetails = new GebruikersDetails(gebruikerTransfer);

				if (gebruikerTransfer.getBedrijfId() != null) {
					Pageable pageable = new PageRequest(0, 10);
					PageTransfer<AlertOverviewTransfer> alerts = bedrijfService.findActiveAlertsOfBedrijf(gebruikerTransfer.getBedrijfId(), gebruikerTransfer.getGebruikersId(), "", pageable);
					if(alerts != null && alerts.getContent() != null && alerts.getContent().size() > 0)
						gebruikersDetails.setActionsPresent(true);
					else gebruikersDetails.setActionsPresent(false);
				}
			}

		} catch(ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gebruikersDetails;
	}

	@Override
	public GebruikersDetails findById(Integer gebruikerId, Integer bedrijfId, boolean isMobileUser) {

		GebruikersDetails gebruikersDetails = null;

		try {
			GebruikerTransfer gebruikerTransfer = gebruikerService.findTransferByGebruikerId(gebruikerId, bedrijfId);

			if(gebruikerTransfer != null)
				LOGGER.info("findByName: " + gebruikerTransfer.getGebruikersNaam() + " pwd: " + gebruikerTransfer.getWachtwoord() + " roles: " + gebruikerTransfer.getRoles());
			else LOGGER.info("findById: No user found!");

			if(gebruikerTransfer != null) {
				gebruikersDetails = new GebruikersDetails(gebruikerTransfer);

				gebruikersDetails.setMobileUser(isMobileUser);

				Pageable pageable = new PageRequest(0, 10);
				PageTransfer<AlertOverviewTransfer> alerts = bedrijfService.findActiveAlertsOfBedrijf(gebruikerTransfer.getBedrijfId(), gebruikerId, "", pageable);
				if(alerts != null && alerts.getContent() != null && alerts.getContent().size() > 0)
					gebruikersDetails.setActionsPresent(true);
				else gebruikersDetails.setActionsPresent(false);
			}

		} catch(ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gebruikersDetails;
	}

	@Override
	public GebruikersDetails findByMobileKey(String key) {
		GebruikersDetails gebruikersDetails = null;

		try{
			MobileGebruiker mG = mobileGebruikerService.findByKey(key);

			if(mG!=null){
				gebruikersDetails = this.findById(mG.getGebruikerGebruikerId(), null, true);
			}
		} catch(ServiceException e){
			LOGGER.error("findByMobileKey, error: " + e.getMessage());
		}

		return gebruikersDetails;
	}

	@Override
	public GebruikersDetails loadUserByUsername(String usernamebedrijfid) throws UsernameNotFoundException {
		GebruikersDetails user = null;
		String[] parts = null;
		String username = null;
		Integer bedrijfId = null;	

		if (usernamebedrijfid != null)
			parts = usernamebedrijfid.split(":");
		if (parts != null) {
			username = parts[0];
			bedrijfId = null;
			if (parts.length == 2 && !parts[1].equals("0"))
				bedrijfId = !parts[1].equals("0") ? ObfuscatorUtils.deofuscateInteger(parts[1]) : null;
		} else
			username = usernamebedrijfid;

		if (bedrijfId != null)
			user = this.findByName(username, bedrijfId);
		else
			user = this.findByName(username, null);

		if(user == null){
			LOGGER.info("No user found when searching on username, trying with mobile client key...");
			user = this.findByMobileKey(username);
		}

		try {
			if(user != null) {
				LoginAllowed loginAllowed = gebruikerService.isGebruikerAllowedToLogin(user, false);

				user.setLoginAllowed(loginAllowed);

				if(loginAllowed.getLoginAllowed() == LoginAllowed.LOGIN_NOTALLOWED_ACCOUNTBLOCKED)
					user.setAccountNonLocked(false);
				else if(loginAllowed.getLoginAllowed() == LoginAllowed.LOGIN_NOTALLOWED_ACCOUNTDISABLED || loginAllowed.getLoginAllowed() == LoginAllowed.LOGIN_NOTACTIVATED)
					user.setEnabled(false);
				else if(loginAllowed.getLoginAllowed() == LoginAllowed.LOGIN_NOTALLOWED_NOBEDRIJF || loginAllowed.getLoginAllowed() == LoginAllowed.LOGIN_NOTALLOWED_NOGEBRUIKER || loginAllowed.getLoginAllowed() == LoginAllowed.LOGIN_NOTALLOWED_NOKLANT)
					user.setAccountNonExpired(false);
			}
		} catch(ServiceException e) {
			user = null;
		}
		
		
		// validate application ClientId to user rights
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null) {
	
			Object clientPrincipal = authentication.getPrincipal();
	
			if (user != null && clientPrincipal instanceof User) {
				if (applicationClientId.equalsIgnoreCase(((User) clientPrincipal).getUsername())) {
					// web application client
					// do nothing
				} else if (apiClientId.equalsIgnoreCase(((User) clientPrincipal).getUsername())) {
					// api client
					if (user.getAuthorities() != null && !user.getAuthorities().contains(new SimpleGrantedAuthority("API")))
						user = null;
				}
			}
		}
		
		if(user != null)
			LOGGER.info("loadUserByUserName: " + user.getUsername() + " roles: " + user.getAuthorities()); //" pwd: " + user.getPassword() +
		else LOGGER.info("findByName: No user found!");

		if(user == null) {
			throw new UsernameNotFoundException("The user with name " + username + " was not found");
		}

		return user;
	}

}
