package nl.devoorkant.sbdr.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;

import nl.devoorkant.sbdr.data.util.ERol;
import nl.devoorkant.sbdr.ws.auth.GebruikersDetailsService;


@Configuration
public class ServerSecurityConfig extends WebSecurityConfigurerAdapter {
	//@Autowired
    //private CustomAuthenticationProvider authProvider;
	
	@Autowired 
	private GebruikersDetailsService gebruikersDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
    
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
	  throws Exception {
	    //auth.authenticationProvider(webAuthenticationProvider)
	    //.authenticationProvider(apiAuthenticationProvider);	
		//auth.authenticationProvider(authProvider);
		//auth.userDetailsService(gebruikersDetailsService).passwordEncoder(passwordEncoder());
		auth //.parentAuthenticationManager(authenticationManagerBean())
        .userDetailsService(gebruikersDetailsService).passwordEncoder(passwordEncoder);
	}
	
    @Override
    public void configure(HttpSecurity http) throws Exception {
    	// Spring security login form off
    	//http.httpBasic().disable();
      // @formatter:off
      http.authorizeRequests().antMatchers(
    		  "/api/login").denyAll()
		//.antMatchers("/api/tokens/**").permitAll()
		//.antMatchers("/api/tokens/**").hasAuthority(ERol.KLANT.getCode())
		//.anyRequest().authenticated()
		//.and().formLogin()
      	.and().authorizeRequests()
        //.and().httpBasic().disable()
		.and().csrf().disable();            
      // @formatter:on
      
    }    	 
    
	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	 
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//	    return new StandardPasswordEncoder("ThisIsASecretSoChangeMee");
//	    //return new BCryptPasswordEncoder(11);
//	}
	
	@Bean
    public AuthenticationProvider authenticationProvider() {
		PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(gebruikersDetailsService);
        return authenticationProvider;
    }

//	@SuppressWarnings("deprecation")
//	@Bean
//	public static NoOpPasswordEncoder encoder() {
//		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
//	}	
	


}
