package nl.devoorkant.sbdr.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import nl.devoorkant.sbdr.spring.OauthDbConfig;
import nl.devoorkant.sbdr.ws.auth.GebruikersDetailsService;

//@Configuration
public class StoreJwtTokenStore extends JwtTokenStore {
    //@Autowired
    //@Qualifier("dataSourceOauth")
    //private DataSource dataSource;
	   
    private ApprovalStore approvalStore;
    
    private DataSource oauthDataSource;

    /**
     * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices if used).
     *
     * @param jwtTokenEnhancer
     */
    @Autowired
    public StoreJwtTokenStore(JwtAccessTokenConverter accessTokenConverter, DataSource datasource) {
        super(accessTokenConverter);
        this.oauthDataSource = datasource;
        this.setApprovalStore(jdbcApprovalStore());
    }

    @Override
    public void setApprovalStore(ApprovalStore approvalStore) {
        super.setApprovalStore(approvalStore);
        this.approvalStore = approvalStore;
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        if (null != approvalStore) {
            OAuth2Authentication auth = readAuthenticationForRefreshToken(refreshToken);
            String clientId = auth.getOAuth2Request().getClientId();
            Authentication user = auth.getUserAuthentication();
            if (user != null) {
                Date date = null;
                if (refreshToken instanceof DefaultExpiringOAuth2RefreshToken) {
                    date = ((DefaultExpiringOAuth2RefreshToken) refreshToken).getExpiration();
                }
                else {
                    date = new Date();
                }
                Collection<Approval> approvals = new ArrayList<Approval>();
                for (String scope : auth.getOAuth2Request().getScope()) {
                    approvals.add(new Approval(user.getName(), clientId, scope,
                            date, Approval.ApprovalStatus.APPROVED));
                }
                approvalStore.addApprovals(approvals);
            }
        }
    }	
    
    // JWT token store configuration

    //@Bean(name="dataSourceOauth")
    public DataSource dataSource() {
        //final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        //dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        //dataSource.setUrl("jdbc:mysql://localhost:3306/oauthsbdr?autoReconnect=true&useSSL=false");
        //dataSource.setUsername("oauth");
        //dataSource.setPassword("oauth"); //2x2$7YRtN8ay
        
        return oauthDataSource;    	
    }
    
   // @Bean
    public JdbcApprovalStore jdbcApprovalStore() {
        return new JdbcApprovalStore(dataSource());
    }      

}
