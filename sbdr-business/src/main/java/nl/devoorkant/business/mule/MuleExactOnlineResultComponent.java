package nl.devoorkant.business.mule;


import java.util.Calendar;
import java.util.Date;

import nl.devoorkant.exactonline.business.transfer.CurrentDivisionResponse;
import nl.devoorkant.exactonline.business.transfer.ErrorResponse;
import nl.devoorkant.exactonline.business.transfer.TokenResponse;
import nl.devoorkant.sbdr.business.service.ExactOnlineService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.ExactOnlineAccess;
import nl.devoorkant.sbdr.data.service.ExactOnlineAccessDataService;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component //Serviced by Mule
public class MuleExactOnlineResultComponent {	
	private static Logger LOGGER = LoggerFactory.getLogger(MuleExactOnlineResultComponent.class);

	@Autowired
	ExactOnlineService exactOnlineService;
	
	@Autowired
	private ExactOnlineAccessDataService exactOnlineAccessDataService;
		
	
	private String message;
	
	
	private void resetExactOnlineAccess() {
		try {
			exactOnlineAccessDataService.reset();
		} catch (DataServiceException e) {
			LOGGER.error("Error resetExactOnlineAccess: " + e.getMessage());
		}
	}
	
	public void setAuthenticationResult(String message) {
		LOGGER.debug("setAuthenticationResult: " + message);
		this.message = message;
	}
	
	public String getAuthenticationResult() {
		LOGGER.debug("getAuthenticationResult: " + message);
		
		return message;
	}
	

	
	// http 200 or 201
	public void processHttpOkResult(String muleFlow) {
		LOGGER.debug("ExactOnline call succeeded: " + muleFlow);
	}
	
	public void processErrorResult(String muleFlow, ErrorResponse errorResponse) {
		if (errorResponse != null && errorResponse.getError() != null) {
			if (errorResponse.getError().getMessage() != null)
				LOGGER.error("Error in " + muleFlow + " ErrorResponse: " + errorResponse.getError().getCode() + " " + errorResponse.getError().getMessage().getValue());
			else
				LOGGER.error("Error in " + muleFlow + " ErrorResponse: " + errorResponse.getError().getCode());
		} else
			LOGGER.error("Error in " + muleFlow + " ErrorResponse is null");
		
		// if processing token/refreshtoken failed, empty exactOnlineAccess tokens, to force new login/auth
		if (muleFlow != null && (muleFlow.equals("ExactOnlineTokenRequest") || muleFlow.equals("ExactOnlineRefreshTokenRequest")))
			resetExactOnlineAccess();
	}
	
	public void processHttpErrorResult(String muleFlow, String httpStatusCode) {
		LOGGER.error("Error in " + muleFlow + " http error: " + httpStatusCode);		
		
		// if processing token/refreshtoken failed, empty exactOnlineAccess tokens, to force new login/auth
		// HTTP 500 errors might be server errors, while 400 might be authentication errors
		boolean resetAccess = false;
		if (httpStatusCode != null) {
			Integer httpresult = FormatUtil.parseInteger(httpStatusCode);
			resetAccess = httpresult >= 400 && httpresult <= 499;
		}

		if (resetAccess)
			LOGGER.info("ExactOnline error " + httpStatusCode + " will resetExactOnlineAccess");
		if (resetAccess && muleFlow != null && (muleFlow.equals("ExactOnlineTokenRequest") || muleFlow.equals("ExactOnlineRefreshTokenRequest")))
			resetExactOnlineAccess();
	
	}
	
	public void processError(String error, String callId) {
		LOGGER.error("Error in calling: " + callId + "ERROR: " + error);
	}
	
	// not used
	public void processAuthenticationResult(String result) {
		LOGGER.debug("exactOnline processAuthenticationResult: \n" + result);
		
		//exactOnlineService.tokenRequest(result);
	}
	
	public void processTokenResult(TokenResponse tokenResponse) {
		LOGGER.debug("tokenResponse: " + tokenResponse.getAccess_token());
		
		try {
			ExactOnlineAccess exactOnlineAccess = exactOnlineAccessDataService.find();
			if (exactOnlineAccess == null)
				exactOnlineAccess = new ExactOnlineAccess();
			
			Calendar expire = Calendar.getInstance();
			expire.setTime(new Date());
			int expiresecs = 600; // default
			if (tokenResponse.getExpires_in() != null) {				
				expiresecs = Integer.parseInt(tokenResponse.getExpires_in());
			}
			expire.add(Calendar.SECOND, expiresecs);
			
			exactOnlineAccess.setDatumAccessTokenExpire(expire.getTime());
			exactOnlineAccess.setAccessToken(tokenResponse.getAccess_token());
			exactOnlineAccess.setRefreshToken(tokenResponse.getRefresh_token());
			
			exactOnlineAccessDataService.save(exactOnlineAccess);
		} catch (DataServiceException e) {
			LOGGER.error(e.getMessage());
		}
		
		
	}
	
	public String processCurrentDivisionResult(String currentDivisionResponse) {
		CurrentDivisionResponse currentDivision = null;
		String curDiv = null;
		
		LOGGER.debug("currentDivisionResponse: " + currentDivisionResponse);
		
		try {
			// not working
			//JsonFactory factory = new JsonFactory();
			//JsonParser jp = factory.createJsonParser(currentDivisionResponse);    
			//JsonNode jsonObj = jp.readValueAsTree();
			
			// working?
			//ObjectMapper mapper = new ObjectMapper();
			//JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead
			//JsonParser jp = factory.createJsonParser(currentDivisionResponse);
			//JsonNode actualObj = mapper.readTree(jp);
			
			// alternative
			JsonNode jsonObj = new ObjectMapper().readTree(currentDivisionResponse).get("d");
			JsonNode results = jsonObj.get("results");
			
			if (results.isArray()) {
				JsonNode result = results.get(0);
				
				if (result != null) {
					ObjectMapper mapper = new ObjectMapper();
					currentDivision = mapper.readValue(result.asText(), CurrentDivisionResponse.class);
					LOGGER.info("currentDivision fetched!!!! " + currentDivision.getCurrentDivision());
					
					curDiv = result.toString();
				}
				
				
			}	
			
			
			return curDiv;
			//return currentDivision;
			
		} catch (Exception e) {
			LOGGER.error("Cannot convert currentDivisionResponse to json: " + e.getMessage());
			return null;
		}
		
	}

}
