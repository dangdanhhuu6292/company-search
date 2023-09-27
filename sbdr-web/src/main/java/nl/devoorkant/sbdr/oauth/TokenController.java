package nl.devoorkant.sbdr.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TokenController {
    @Value("${application.client_id}")
    private String applicationClientId;
    
    @Value("${api.client_id}")
    private String apiClientId;
    
    @Resource(name = "tokenServices")
    ConsumerTokenServices tokenServices;

    @Resource(name = "tokenStore")
    StoreJwtTokenStore tokenStore;

    @RequestMapping(method = RequestMethod.POST, value = "/api/tokens/revokeById/{tokenId}")
    @ResponseBody
    public String revokeToken(HttpServletRequest request, @PathVariable String tokenId) {
        Boolean result = tokenServices.revokeToken(tokenId);
        if (result)
        	return "Token id revoked"; 
        else
        	return "Token id not found";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tokens")
    @ResponseBody
    public List<String> getTokens() {
        List<String> tokenValues = new ArrayList<String>();
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(applicationClientId);
        if (tokens != null) {
            for (OAuth2AccessToken token : tokens) {
                tokenValues.add(token.getValue());
            }
        }

        return tokenValues;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/tokens/revokeRefreshToken/{tokenId:.*}")
    @ResponseBody
    public String revokeRefreshToken(@PathVariable String tokenId) {

    	if (tokenId != null) {
    		Collection<Approval> approvals = tokenStore.jdbcApprovalStore().getApprovals(tokenId, applicationClientId);
    		
    		if (approvals != null && approvals.size() > 0)
    			tokenStore.jdbcApprovalStore().revokeApprovals(approvals);
    	}
    	
       return tokenId;
   	
    }

}