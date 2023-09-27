package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.LoginAllowed;
import nl.devoorkant.sbdr.business.util.ApiKeyTuple;
import nl.devoorkant.sbdr.business.util.WebTokenTuple;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.MobileGebruiker;
import nl.devoorkant.sbdr.data.service.GebruikerDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Service("webTokenService")
public class WebTokenServiceImpl implements WebTokenService {

	@Autowired
	private GebruikerService gebruikerService;

	@Autowired
	private GebruikerDataService gebruikerDataService;

	@Autowired
	private MobileGebruikerService mobileGebruikerService;

	private static final Logger LOGGER = LoggerFactory.getLogger(WebTokenServiceImpl.class);

	/* Expires in 20 minutes */ long expiresInMinutes = LoginAllowed.MAX_ACTIVETOKEN_MINUTES;
	
	/* Expired in 5 minutes */  long expiredInMinutesApiKey = 5;
	/* Max public api calls */  int maxPublicApiCalls = 150;
	/* Max anonymous clients */ int maxAnonymousClients = 2500;
	/* Max apiKeys per ip address */ int maxApiKeysPerIpAddress = 50;

	// Logged in sessions
	ConcurrentHashMap<String, WebTokenTuple> tokens = new ConcurrentHashMap<String, WebTokenTuple>();
	
	// Public sessions
	ConcurrentHashMap<String, ApiKeyTuple> apiKeys = new ConcurrentHashMap<String, ApiKeyTuple>();

	@Override
	public boolean validateToken(String authToken, String userName) {
		boolean result = true;

		WebTokenTuple webtoken = getWebTokenTuple(authToken);

		if(webtoken != null) {
			if(isWebTokenExpired(webtoken)) {
				result = false;
			} else {
				try {
					// Refresh WebToken
					refreshWebTokenTuple(authToken, userName, webtoken);
				} catch(ServiceException e) {
					throw new ServiceException(e);
				}
			}

		} else result = false;

		return result;
	}
	
	@Override
	public boolean validateApiKey(String key, String ipAddress, Long expiration) {
		boolean result = true;
		
		ApiKeyTuple apiKey = getApiKeyTuple(key);
		
		if (apiKey != null) {
			if (!apiKey.getIpAddress().equals(ipAddress) || isApiKeyExpiredOrOverused(apiKey)) {
				result = false;
			} else {
				try {
					// Refresh WebToken
					refreshApiKeyTuple(key, ipAddress, apiKey, expiration);
				} catch(ServiceException e) {
					throw new ServiceException(e);
				}				
			}
		} else
			result = false;
		
		return result;
	}

	@Override
	public boolean addToken(String userName, String authToken) {
		if(alreadyLoggedIn(userName)) return false;

		WebTokenTuple webtoken = getWebTokenTuple(authToken);

		// Create WebToken
		refreshWebTokenTuple(authToken, userName, webtoken);

		return true;
	}

	@Override
	public boolean addApiKey(String key, String ipAddress, Long expiration) {
		ApiKeyTuple apiKey = getApiKeyTuple(key);

		// Create WebToken
		if (apiKey == null) {
			refreshApiKeyTuple(key, ipAddress, apiKey, expiration);

			return true;
		} else
			return false;
	}	
	
	@Override
	@Transactional(readOnly = true)
	public void removeInvalidTokens() {
		Set<Entry<String, WebTokenTuple>> tokenset = tokens.entrySet();
		Set<Entry<String, WebTokenTuple>> toremove = new HashSet<>();

		for(Entry<String, WebTokenTuple> entry : tokenset) {
			// if expired remove from webtokens
			if(isWebTokenExpired(entry.getValue())) {
				toremove.add(entry);
			}
		}

		// remove obsolete tokens
		if(toremove.size() > 0) for(Entry<String, WebTokenTuple> entry : toremove) {
			tokens.remove(entry.getKey());
			Gebruiker g = null;
			try{
				g = gebruikerDataService.findByGebruikersnaam(entry.getValue().getUserName());
			} catch(DataServiceException e){
				LOGGER.error("Method removeInvalidTokesn: " + e.getMessage());
			}

			if(g!=null){
				gebruikerService.logoutGebruiker(entry.getValue().getToken(), g.getGebruikerId());
			}
		}
	}
	
	@Override
	public void removeInvalidApiKeys() {
		Set<Entry<String, ApiKeyTuple>> apiKeyset = apiKeys.entrySet();
		Set<Entry<String, ApiKeyTuple>> toremove = new HashSet<>();

		for(Entry<String, ApiKeyTuple> entry : apiKeyset) {
			// if expired remove from webtokens
			if(isApiKeyExpiredOrOverused(entry.getValue())) {
				toremove.add(entry);
			}
		}

		// remove obsolete apiKeys
		if(toremove.size() > 0) for(Entry<String, ApiKeyTuple> entry : toremove) {
			apiKeys.remove(entry.getKey());
		}
	}	

	@Override
	public boolean removeToken(String signature) {
		boolean result = false;

		if(signature != null) {
			//WebTokenTuple webtoken= getWebTokenTuple(authToken);
			WebTokenTuple webtoken = tokens.get(signature);

			if(webtoken != null) {
				if(tokens.remove(signature) != null) {
					result = true;
					LOGGER.info("Removed webtoken: " + webtoken.getToken());
				} else LOGGER.warn("Could not remove webtoken. Probably already invalidated: " + webtoken.getToken());
			} else LOGGER.warn("Could not find signature. Probably already invalidated: " + signature);
		}

		return result;
	}
	
	@Override
	public boolean removeApiKey(String key) {
		boolean result = false;

		if(key != null) {
			ApiKeyTuple apiKey = apiKeys.get(key);

			if(apiKey != null) {
				if(apiKeys.remove(key) != null) {
					result = true;
					LOGGER.info("Removed apiKey: " + apiKey.getKey());
				} else LOGGER.warn("Could not remove apiKey. Probably already invalidated: " + apiKey.getKey());
			} else LOGGER.warn("Could not find signature. Probably already invalidated: " + key);
		}

		return result;
	}	

	@Override
	public int getNrOfValidTokens() {
		return tokens.entrySet().size();
	}
	
	@Override
	public int getNrOfValidApiKeys() {
		return apiKeys.entrySet().size();
	}	

	private boolean alreadyLoggedIn(String userName) {
		boolean result = false;
		Set<Entry<String, WebTokenTuple>> tokenset = tokens.entrySet();

		for(Entry<String, WebTokenTuple> entry : tokenset) {
			// if found username then already logged in
			if(isWebTokenOfUser(userName, entry.getValue()) && !isWebTokenExpired(entry.getValue())) {
				result = true;
				break;
			}
		}

		return result;
	}

	private WebTokenTuple getWebTokenTuple(String authToken) {
		String[] parts = authToken.split(":");
		//long expires = Long.parseLong(parts[1]);
		String signature = parts[3];

		return tokens.get(signature);
	}

	private ApiKeyTuple getApiKeyTuple(String key) {
		return apiKeys.get(key);
	}
	
	private boolean isWebTokenExpired(WebTokenTuple webtoken) {
		long expires = webtoken.getExpires();
		return expires < System.currentTimeMillis();

	}
	
	private boolean isApiKeyExpiredOrOverused(ApiKeyTuple apiKey) {
		long expires = apiKey.getExpires();
		return expires < System.currentTimeMillis() || apiKey.getRequests() > maxPublicApiCalls; // MAX 25 Requests public api

	}	

	private boolean isWebTokenOfUser(String userName, WebTokenTuple webtoken) {
		boolean result = false;

		if(userName != null) if(webtoken.getUserName().equals(userName)) result = true;

		return result;
	}

	private void refreshWebTokenTuple(String authToken, String userName, WebTokenTuple webtoken) throws ServiceException {
		if(authToken == null) throw new ServiceException("Cannot refreshWebTokenTuple, because authToken is null");

		String[] parts = authToken.split(":");
		//long expires = Long.parseLong(parts[1]);
		String signature = parts[3];

		/* Expires in 20 minutes */
		long expires = System.currentTimeMillis() + 1000L * 60 * expiresInMinutes;

		if(webtoken != null) {
			webtoken.setExpires(expires);
			tokens.put(signature, webtoken);
		} else {
			tokens.put(signature, new WebTokenTuple(authToken, userName, expires));
		}
	}
	
	private void refreshApiKeyTuple(String key, String ipAddress, ApiKeyTuple apiKey, Long expiration) throws ServiceException {
		if(key == null) throw new ServiceException("Cannot refreshApiKeyTuple, because key is null");
		if (apiKeys.size() > maxAnonymousClients) throw new ServiceException("Too many clients");		

		/* Expires in x minutes */
		long expires = System.currentTimeMillis() + 1000L * 60 * expiredInMinutesApiKey;
		
		if (expiration != null)
			expires = expiration;
		
		if(apiKey != null) {
			//apiKey.setExpires(expires);
			apiKey.setRequests(apiKey.getRequests()+1);
			apiKeys.put(key, apiKey);
		} else {
			apiKeys.put(key, new ApiKeyTuple(key, ipAddress, expires, 0));
			
			// check for max duplicate ip addresses
			Set<Entry<String, ApiKeyTuple>> apiKeyset = apiKeys.entrySet();
			int countIp = 0;
			for (Entry<String, ApiKeyTuple> entry : apiKeyset) {
				if (entry != null && entry.getValue() != null) {
					ApiKeyTuple apiKeyEntry = entry.getValue();
					if (apiKeyEntry.getIpAddress() != null && apiKeyEntry.getIpAddress().equals(ipAddress))
						countIp++;
				}
			}
			/// if max exceeded, remove apiKey
			if (countIp > maxApiKeysPerIpAddress)
				apiKeys.remove(key);
		}
	}	
	
	public boolean connectWebTokenToNewUser(String userNameOld, String userNameNew) {
		boolean result = false;
		Set<Entry<String, WebTokenTuple>> tokenset = tokens.entrySet();

		for(Entry<String, WebTokenTuple> entry : tokenset) {
			// if found username then already logged in, so update username
			if(isWebTokenOfUser(userNameOld, entry.getValue()) && !isWebTokenExpired(entry.getValue())) {
				result = true;
				WebTokenTuple webtoken = entry.getValue();
				webtoken.setUserName(userNameNew);
				break;
			}
		}

		return result;		
	}
	
	public boolean removeWebTokenOfUser(String userName) {
		boolean result = false;
		Set<Entry<String, WebTokenTuple>> tokenset = tokens.entrySet();

		for(Entry<String, WebTokenTuple> entry : tokenset) {
			// if found username then remove webtoken
			if(isWebTokenOfUser(userName, entry.getValue())) {
				result = true;
				tokens.remove(entry.getKey());
				break;
			}
		}

		return result;		
	}	
}
