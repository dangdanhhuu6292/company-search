package nl.devoorkant.sbdr.ws.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import nl.devoorkant.sbdr.business.transfer.GebruikersDetails;

public interface GebruikersDetailsService extends UserDetailsService, AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
	
	GebruikersDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException;
	
	GebruikersDetails findByName(String name, Integer bedrijfId);

	GebruikersDetails findById(Integer gebruikerId, Integer bedrijfId, boolean isMobileUser);

	GebruikersDetails findByMobileKey(String key);
}
