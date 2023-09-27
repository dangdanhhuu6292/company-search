package nl.devoorkant.sbdr.oauth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import nl.devoorkant.sbdr.data.util.ERol;

@Configuration
public class OAuth2ResourceServerConfiguration {
  @Configuration
  @EnableResourceServer
  protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(ResourceServerConfiguration.class);
	
	@Override
    public void configure(ResourceServerSecurityConfigurer config) {
    	config.resourceId("OAuth2 server"); 
    	// only necessary when Resource Server is seperate from Authorization Server
    	// DO NOT DELETE!
    	//config.tokenServices(tokenServices());
    }    
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
    	String[] authorities = new String[8];
    	authorities[0]= ERol.GEBRUIKER.getCode();
    	authorities[1] = ERol.HOOFD.getCode();
    	authorities[2] = ERol.KLANT.getCode();
    	authorities[3] = ERol.MANAGED.getCode();
    	authorities[4] = ERol.REGISTRATIESTOEGESTAAN.getCode();
    	authorities[5] = ERol.SBDR.getCode();
    	authorities[6] = ERol.SBDRHOOFD.getCode();
    	authorities[7] = ERol.SYSTEEM.getCode();
    	
    	
    	http
        .authorizeRequests()	
    		.antMatchers("/services/UserNsService/**").permitAll()
    		.antMatchers("/services/LoginService/**").permitAll()
    		.antMatchers("/services/NewAccountService/**").permitAll()
    		.antMatchers("/services/api/v1/**").hasAuthority(ERol.APITOEGESTAAN.getCode())
    		.antMatchers("/services/**").hasAnyAuthority(authorities)  
    		.antMatchers("/api/tokens/**").hasAuthority(ERol.SYSTEEM.getCode())
    		.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
    
    public Map<String, Object> getExtraInfo(Authentication auth) {
        OAuth2AuthenticationDetails oauthDetails
          = (OAuth2AuthenticationDetails) auth.getDetails();
        return (Map<String, Object>) oauthDetails
          .getDecodedDetails();
    }  
    
    // Below only necessary when Resource Server is in seperate from Authorization Server
    // DO NOT DELETE!
    
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        Resource resource = new ClassPathResource("publickey.txt");
//        String publicKey = null;
//        try {
//            publicKey = IOUtils.toString(resource.getInputStream());
//        } catch (final IOException e) {
//            throw new RuntimeException(e);
//        }
//        converter.setVerifierKey(publicKey);
//        return converter;
//    }
//    
//    @Bean
//    public TokenStore tokenStore() {
//        return new JwtTokenStore(accessTokenConverter());
//    }    
//    
//    @Bean
//    @Primary
//    public DefaultTokenServices tokenServices() {
//        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//        
//        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
//        defaultTokenServices.setTokenStore(tokenStore());
//        defaultTokenServices.setSupportRefreshToken(true);
//        return defaultTokenServices;
//    }
//    
    
  }
}
