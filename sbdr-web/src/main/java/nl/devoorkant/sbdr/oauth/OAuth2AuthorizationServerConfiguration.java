package nl.devoorkant.sbdr.oauth;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import nl.devoorkant.sbdr.spring.OauthDbConfig;
import nl.devoorkant.sbdr.ws.auth.GebruikersDetailsService;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthorizationServerConfiguration.class);

	@Autowired
	@Qualifier("dataSourceOauth")
	private DataSource dataSourceOauth;
	
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;  
    
    @Autowired
    private GebruikersDetailsService gebruikersDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${application.client_id}")
    private String applicationClientId;
    
    @Value("${application.secret}")
    private String applicationSecret;
    
    @Value("${api.client_id}")
    private String apiClientId;
    
    @Value("${api.secret}")
    private String apiSecret;
    
    @Autowired
    private ClientDetailsService clientDetailsService;
    
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");  
        oauthServer.allowFormAuthenticationForClients();
    }
    
    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
       configurer
               .inMemory()
               .withClient(applicationClientId)
               .authorities("ROLE_WEB")
               .secret(passwordEncoder.encode(applicationSecret)) //.secret("b414aac470705ac66f7fd99afb1d3c529fe235c07aeaea39d72fcf6d2bc39fe845bbd5b97dacb1e3")
               //is 'secret'.secret("8c6ddc27834f34643f8873a5aedf51851ff1d54363b88d25cb5f423aa5e51519ec44d435ca71b602") //.secret("$2a$11$6fA9xHWDegFSc3HLvjALReKd/VybRgP2AT1v.yKG15tka7LkJP7DC")       
               .authorizedGrantTypes("password", "authorization_code", "refresh_token")
               .accessTokenValiditySeconds(900) // 7200 = 2 hour, 900 = 15 min
               .refreshTokenValiditySeconds(2592000) // 30 days
               .scopes("read", "write")
       		   .and()
       		   //.withClient("mobileClientIdPassword")
       		   //.secret("8c6ddc27834f34643f8873a5aedf51851ff1d54363b88d25cb5f423aa5e51519ec44d435ca71b602") //.secret("$2a$11$6fA9xHWDegFSc3HLvjALReKd/VybRgP2AT1v.yKG15tka7LkJP7DC")
	       		//.authorizedGrantTypes("password", "authorization_code", "refresh_token")
	            //.accessTokenValiditySeconds(7200) // 2 hour
	            //.refreshTokenValiditySeconds(2592000) // 30 days
	            //.scopes("read", "write")
	            //.and()
	            // client_id = MDAGPLCSVDFWDWEFE
	            // secret: 85048962178436008061658561419431
	            .withClient(apiClientId)
	            .authorities("ROLE_API")
	            	.secret(passwordEncoder.encode(apiSecret)) 	//.secret("c26c3e2e3b054a53b7a93b89af5cda487960929b4b04d71b9daf25cec90746b70572f9eb9d2692d7")
	       		    //is 'secret'.secret("8c6ddc27834f34643f8873a5aedf51851ff1d54363b88d25cb5f423aa5e51519ec44d435ca71b602") //.secret("$2a$11$6fA9xHWDegFSc3HLvjALReKd/VybRgP2AT1v.yKG15tka7LkJP7DC")
		       		.authorizedGrantTypes("password", "authorization_code", "refresh_token")
		            .accessTokenValiditySeconds(86400) // 24 hour
		            .refreshTokenValiditySeconds(2592000) // 30 days
		            .scopes("read", "write")		
       		   ;     
               //.resourceIds("OAuth2 server");
    }    


    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	endpoints
        .pathMapping("/oauth/token", "/api/oauth/token")
        .pathMapping("/oauth/authorize", "/api/oauth/authorize")
        .pathMapping("/oauth/check_token", "/api/oauth/check_token")
        .pathMapping("/oauth/confirm_access", "/api/oauth/confirm_access")
        .pathMapping("/oauth/error", "/api/oauth/error");
    	
    	endpoints.tokenStore(tokenStore())
		.authenticationManager(authenticationManager)
		//.tokenServices(tokenServices())
		.tokenStore(tokenStore())
		.tokenEnhancer(accessTokenConverter())
		.reuseRefreshTokens(false)
		
		.userDetailsService(gebruikersDetailsService)
        .accessTokenConverter(accessTokenConverter()); 
    }
    
//    private ClientDetailsService clientDetailsService() {
//        return new ClientDetailsService() {
//            @Override
//            public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
//                BaseClientDetails details = new BaseClientDetails();
//                details.setClientId(clientId);
//                details.setAuthorizedGrantTypes(Arrays.asList("authorization_code") );
//                details.setScope(Arrays.asList("read", "write"));
//                details.setRegisteredRedirectUri(Collections.singleton("http://anywhere.com"));
//                details.setResourceIds(Arrays.asList("OAuth2 server"));
//                Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
//                authorities.add(new SimpleGrantedAuthority("ROLE_WEB"));
//                details.setAuthorities(authorities);
//                return details;
//            }
//        };
//    } 
    
	@Bean
    DefaultOAuth2RequestFactory defaultOAuth2RequestFactory() {
		DefaultOAuth2RequestFactory defaultOAuth2RequestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
		defaultOAuth2RequestFactory.setCheckUserScopes(true);
		
		return defaultOAuth2RequestFactory;
	}	    

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        
        //TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        //tokenEnhancerChain.setTokenEnhancers(
        //        Arrays.asList(authTokenEnhancer, accessTokenConverter()));
        //defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);

        defaultTokenServices.setReuseRefreshToken(false);
        //defaultTokenServices.setAccessTokenValiditySeconds(applicationProperties.getAuth().getDefaultAccessTokenTimeout());
        //defaultTokenServices.setRefreshTokenValiditySeconds(applicationProperties.getAuth().getDefaultRefreshTokenTimeout());
        //defaultTokenServices.setClientDetailsService(new OauthClientDetailsServiceImpl(dataSource));

        // MBR 15-12-2018 ambigious??
//        defaultTokenServices.setAuthenticationManager(authentication -> {
//            UserDetails userDetails = gebruikersDetailsService.loadUserByUsername(authentication.getName());
//
//            PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
//                    authentication.getName(), authentication.getCredentials(), userDetails.getAuthorities());
//            
//            userDetails = gebruikersDetailsService.loadUserDetails(token);
//            
//            token.setDetails(userDetails);
//            return token;
//        });        
        
        
        return defaultTokenServices;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }
    
    @Bean
    public TokenStore tokenStore() {
    	//CustomTokenEnhancer customTokenEnhancer = new CustomTokenEnhancer();
    	StoreJwtTokenStore jwtTokenStore = new StoreJwtTokenStore(accessTokenConverter(), dataSourceOauth);
    	return jwtTokenStore;    	    	
    	
        ////JwtTokenStore jwtTokenStore = new JwtTokenStore(accessTokenConverter());
        ////jwtTokenStore .setApprovalStore(jdbcApprovalStore());
        ////return jwtTokenStore;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
    	DefaultUserAuthenticationConverter duac = new DefaultUserAuthenticationConverter();
        duac.setUserDetailsService(gebruikersDetailsService);

        DefaultAccessTokenConverter datc = new DefaultAccessTokenConverter();
        datc.setUserTokenConverter(duac);
    	
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // Symmetric key
        //converter.setSigningKey("as466gf");
        
        // Asymmetric key via certificate
        converter.setKeyPair(new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "19CnNZVJ5B".toCharArray()).getKeyPair("jwt"));
    	
        converter.setAccessTokenConverter(datc); // IMPORTANT
        return converter;
    } 
}
