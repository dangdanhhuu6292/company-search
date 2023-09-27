package nl.devoorkant.sbdr.business.service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import nl.devoorkant.business.mule.MuleExactOnlineResultComponent;
import nl.devoorkant.exactonline.business.transfer.Account;
import nl.devoorkant.exactonline.business.transfer.AuthenticationRequest;
import nl.devoorkant.exactonline.business.transfer.BankAccount;
import nl.devoorkant.exactonline.business.transfer.Contact;
import nl.devoorkant.exactonline.business.transfer.CurrentDivisionResponse;
import nl.devoorkant.exactonline.business.transfer.DirectDebitMandates;
import nl.devoorkant.exactonline.business.transfer.ErrorResponse;
import nl.devoorkant.exactonline.business.transfer.GlAccount;
import nl.devoorkant.exactonline.business.transfer.RefreshTokenRequest;
import nl.devoorkant.exactonline.business.transfer.SalesEntry;
import nl.devoorkant.exactonline.business.transfer.SalesEntryBase;
import nl.devoorkant.exactonline.business.transfer.SalesEntryLine;
import nl.devoorkant.exactonline.business.transfer.TokenRequest;
import nl.devoorkant.exactonline.business.transfer.TokenResponse;
import nl.devoorkant.exactonline.business.transfer.VatCode;
import nl.devoorkant.sbdr.business.util.EExactOnlineRestApi;
import nl.devoorkant.sbdr.business.util.EPaymentType;
import nl.devoorkant.sbdr.business.util.EVatType;
import nl.devoorkant.sbdr.data.util.EProduct;
import nl.devoorkant.sbdr.business.util.JaxbJacksonObjectMapper;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Configuratie;
import nl.devoorkant.sbdr.data.model.ExactOnlineAccess;
import nl.devoorkant.sbdr.data.model.Factuur;
import nl.devoorkant.sbdr.data.model.FactuurConfig;
import nl.devoorkant.sbdr.data.model.FactuurRegel;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.service.BedrijfDataService;
import nl.devoorkant.sbdr.data.service.ConfiguratieDataService;
import nl.devoorkant.sbdr.data.service.ExactOnlineAccessDataService;
import nl.devoorkant.sbdr.data.service.FactuurConfigDataService;
import nl.devoorkant.sbdr.data.service.FactuurDataService;
import nl.devoorkant.sbdr.data.service.KlantDataService;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service("exactOnlineService")
@Transactional(readOnly = true)
public class ExactOnlineServiceImpl implements ExactOnlineService {

	@Autowired
	private BedrijfDataService bedrijfDataService;

	@Autowired
	private ExactOnlineAccessDataService exactOnlineAccessDataService;

	@Value("${exactonline_website}")
	private String exactOnlineWebSite;
	
	@Value("${exactonline_client_id}")
	private String exactOnlineClientId;

	@Value("${exactonline_redirect_auth_uri}")
	private String exactOnlineRedirectAuthUri;

	@Value("${exactonline_redirect_callback_uri}")
	private String exactOnlineRedirectCallbackUri;

	@Value("${exactonline_redirect_refreshtoken_uri}")
	private String exactOnlineRedirectRefreshTokenUri;

	@Value("${exactonline_redirect_token_uri}")
	private String exactOnlineRedirectTokenUri;

	@Value("${exactonline_secret}")
	private String exactOnlineSecret;
	
	@Autowired
	private EmailService emailService;	

	@Autowired
	private FactuurConfigDataService factuurConfigDataService;

	@Autowired
	private FactuurDataService factuurDataService;

	@Autowired
	private KlantDataService klantDataService;
	
	@Autowired
	private ConfiguratieDataService configuratieDataService;

	@Value("${exactonline_base_uri}")
	private String exactonlineBaseUri;

	@Value("${sbdrweb_base_uri}")
	private String sbdrwebBaseUri;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExactOnlineServiceImpl.class);

	//@Autowired // Wired by Mule
	//		MuleExactOnlineResultComponent muleExactOnlineResultComponent;

	/**
	 * Main process to process:
	 * - new/update companies + addresses
	 * - invoices if applicable
	 */
	@Override
	//Transactional per company and per invoice so move to lower level: @Transactional
	public void processExactOnline() throws ServiceException {

		CurrentDivisionResponse currentDivision = doCurrentDivisionRequest();

		if(currentDivision != null) {			
			
			String curDiv = Objects.toString(currentDivision.getCurrentDivision(), null);

			processCompanies(curDiv);

			HashMap<String, String> vatcodes;
			try {
				vatcodes = getVatCodes();
			
				// vatcodes available must correspond with EO values
				if (areVatCodesOk(curDiv, vatcodes))
					processFacturen(curDiv);
			} catch (DataServiceException e) {
				throw new ServiceException(e);
			}				
		}
	}

	@Override
	public void tokenRequest(String code) throws ServiceException {
		sendTokenRequest(code);
	}

	// called by daily service + during processing of ExactOnline interaction
	@Override
	public void refreshToken() throws ServiceException {
		ExactOnlineAccess exactOnlineAccess = null;

		try {
			exactOnlineAccess = findExactOnlineAccess();

			if(exactOnlineAccess != null && exactOnlineAccess.getRefreshToken() != null) {
				Calendar checkexpire = Calendar.getInstance();
				checkexpire.add(Calendar.MINUTE, 1); // add one minute
				Date checkexpiredate = checkexpire.getTime();

				// if there is no expiration date(not possible) or checkexpire is after token expiration date then refresh token
				if(exactOnlineAccess.getDatumAccessTokenExpire() == null || checkexpiredate.after(exactOnlineAccess.getDatumAccessTokenExpire()))
					sendRefreshTokenRequest(exactOnlineAccess);
			} // otherwise there is no token refresh possible and user needs to login in for auth/access
			else LOGGER.warn("Cannot find ExactOnline refresh token. There might be no oauth access yet.");
		} catch(Exception e) {
			// refresh token may not be valid (anymore). Maybe reset due authentication failure.
			
			// If login/auth failure persists for min 3 succeeding days (also due server side errors). Remove exactonline login record to force new login by administrator.
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -3);
			
			if(exactOnlineAccess != null) {
				// Date to compare is DatumGewijzigd or DatumAangemaakt
				Date dateToCompare = exactOnlineAccess.getDatumAangemaakt();
				if (exactOnlineAccess.getDatumGewijzigd() != null)
					dateToCompare = exactOnlineAccess.getDatumGewijzigd();
				
				// If there is no date to compare or exactOnlineAccess is not changed for period then remove record to force new ExactOnline login
				if (dateToCompare == null || dateToCompare.before(cal.getTime())) {
					try {
						LOGGER.info("Delete ExactOnlineAccess due no succesfull ExactOnline communication.");
						exactOnlineAccessDataService.delete(exactOnlineAccess);
						
					} catch(DataServiceException e1) {
						LOGGER.error("Error removing invalid exactOnlineAccess record.");
					}
				}
			}

			LOGGER.error("Cannot refresh ExactOnline token" + e.getMessage());
		}
	}

	@Override
	public ExactOnlineAccess findExactOnlineAccess() throws ServiceException {
		try {
			return exactOnlineAccessDataService.find();
		} catch(DataServiceException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public AuthenticationRequest fetchAuthenticationRequest() {
		AuthenticationRequest authenticationRequest = null;
		
		authenticationRequest = new AuthenticationRequest();
		authenticationRequest.setClientId(exactOnlineClientId);
		// MBR 23-05-2016 for DEV: replace webBasedUri with muleBaseUri
		authenticationRequest.setRedirectUri(sbdrwebBaseUri + exactOnlineRedirectCallbackUri);
		authenticationRequest.setSite(exactOnlineWebSite);
		
		return authenticationRequest;	
		
	}

	private String ObjectToJson(Object object) throws ServiceException {
		String json = null;
		try {
			JaxbJacksonObjectMapper mapper = new JaxbJacksonObjectMapper();
			//mapper.setSerializationInclusion(Include.NON_NULL);
			StringWriter outputWriter = new StringWriter();
			mapper.writeValue(outputWriter, object.getClass());
			json = outputWriter.toString();

			return json;

		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	private void authenticate() throws ServiceException {
		AuthenticationRequest authRequest = fetchAuthenticationRequest();

		try {
			// remove old token
			exactOnlineAccessDataService.reset();

			LOGGER.info("Send ExactOnline authentication request: " + new Date());
			
			CloseableHttpClient httpClient = HttpClients.custom()
	                .setSSLHostnameVerifier(new NoopHostnameVerifier())
	                .build();
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setHttpClient(httpClient);		
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

				HttpEntity<AuthenticationRequest> entity = new HttpEntity<AuthenticationRequest>(authRequest,headers);		
				ResponseEntity<String> result = restTemplate.exchange(exactonlineBaseUri + exactOnlineRedirectAuthUri, HttpMethod.GET, entity, String.class);
				
				if(result != null) {
					if (result.getStatusCode().equals(HttpStatus.CREATED) || result.getStatusCode().equals(HttpStatus.OK)) {
						
					} else if (result.getStatusCodeValue() == 500) {
						LOGGER.error("Error in auth request ExactOnline: " + result.getBody());	
					}
				}
			} catch(RestClientException e) {
				throw new Exception("Cannot call authenticate exactonline service");
			}			
			
			LOGGER.debug("ExactOnline authentication sent");

		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	private CurrentDivisionResponse doCurrentDivisionRequest() throws ServiceException {
		CurrentDivisionResponse currentDivision = null;

		try {
			LOGGER.debug("Send CurrentDivisionRequest request");
			
			HttpHeaders webclientProperties = restCallProperties(EExactOnlineRestApi.CurrentDivision, "current");

			if(webclientProperties != null) {
				CloseableHttpClient httpClient = HttpClients.custom()
		                .setSSLHostnameVerifier(new NoopHostnameVerifier())
		                .build();
				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
				requestFactory.setHttpClient(httpClient);		
				RestTemplate restTemplate = new RestTemplate(requestFactory);
				
				try {
					
					HttpEntity<Object> entity = new HttpEntity<>(webclientProperties);		
					ResponseEntity<String> result = restTemplate.exchange(exactonlineBaseUri + webclientProperties.get("path"), HttpMethod.GET, entity, String.class);
					
					if(result != null)
						LOGGER.debug("Result currentDivisionRequest EO: " + result);	
					
					LOGGER.debug("ExactOnline current division request sent");

					if(result != null && result.getBody() != null &&
							(result.getStatusCode().equals(HttpStatus.CREATED) || result.getStatusCode().equals(HttpStatus.OK))) {
						String payload = result.getBody();

						//JsonNode jsonObj = new ObjectMapper().readTree(payload).get("d");
						//JsonNode results = jsonObj.get("results");
						//
						//if (results.isArray()) {
						//	JsonNode result = results.get(0);
						//
						//	if (result != null) {
						//		ObjectMapper mapper = new ObjectMapper();
						//		currentDivision = mapper.readValue(result, CurrentDivisionResponse.class);
						//		LOGGER.info("currentDivision fetched!!!! " + currentDivision.getCurrentDivision());
						//
						//	}
						//
						//
						//}

						List<CurrentDivisionResponse> currentDivisions = (List<CurrentDivisionResponse>) processExactOnlineResponseList(payload, CurrentDivisionResponse.class);

						if(currentDivisions != null && currentDivisions.size() > 0)
							currentDivision = currentDivisions.get(0);
					}					
				} catch(RestClientException e) {
					throw new Exception("Cannot call sendMail service");
				}							
				

				return currentDivision;

			} else {
				LOGGER.error("No access token to send CurrenctDivisionRequest");
				return null;
			}
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	private boolean saveDirectDebitMandates(HttpHeaders webclientPropertiesStoreDirectDebitMandates, Bedrijf bedrijf, Klant klant, DirectDebitMandates mandates) throws Exception {	
		
		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);				
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		
		try {
			HttpEntity<DirectDebitMandates> entity = new HttpEntity<DirectDebitMandates>(mandates, webclientPropertiesStoreDirectDebitMandates);				
			ResponseEntity<String> result = restTemplate.postForEntity(exactonlineBaseUri + webclientPropertiesStoreDirectDebitMandates.get("path"), entity, String.class);
			
			if(result != null)
				LOGGER.info("Result saveDirectDebitMandates: " + result);	
			
			DirectDebitMandates resultDirectDebitMandates = null;
			if(result != null && result.getBody() != null) {
				HttpStatus httpStatus = result.getStatusCode();

				if(httpStatus.equals(HttpStatus.ACCEPTED) || httpStatus.equals(HttpStatus.OK)) {

					String payload = result.getBody();

					if(klant.getDirectDebitMandatesGuid() == null) {
						resultDirectDebitMandates = processExactOnlineResponse(payload, DirectDebitMandates.class);
						if(resultDirectDebitMandates != null) {
							LOGGER.info("DirectDebitMandates processed in EO, ID = " + resultDirectDebitMandates.getId());
							klant.setDirectDebitMandatesGuid(resultDirectDebitMandates.getId());
							klant.setEersteFactuurVerzonden(false);
						}
						else
							LOGGER.error("DirectDebitMandates processing of account: " + mandates.getAccount() + " went wrong...");					
					} else resultDirectDebitMandates = mandates;
					// else nothing is ok for update
					
					return true;

				} else {
					LOGGER.error("\"Error create/update DirectDebitMandates data in ExactOnline: unknown error");
					return false;
				}
			} else
				return false;
			
		} catch(RestClientException e) {
			throw new Exception("Cannot call sendMail service");
		}			
	}
	
	@Transactional
	private void processCompanies(String currentDivision) throws ServiceException {
		try {
			HttpHeaders webclientPropertiesStoreAccount = restCallProperties(EExactOnlineRestApi.StoreAccount, currentDivision);
			HttpHeaders webclientPropertiesStoreContact = restCallProperties(EExactOnlineRestApi.StoreContact, currentDivision);
			HttpHeaders webclientPropertiesStoreBankAccount = restCallProperties(EExactOnlineRestApi.StoreBankAccount, currentDivision);
			HttpHeaders webclientPropertiesStoreDirectDebitMandates = restCallProperties(EExactOnlineRestApi.StoreDirectDebitMandates, currentDivision);

			List<Object[]> klantbedrijven = klantDataService.findKlantenToProcessExactOnline();

			if(webclientPropertiesStoreAccount != null && webclientPropertiesStoreContact != null &&
					klantbedrijven != null && klantbedrijven.size() > 0) {
				for(Object[] klantbedrijf : klantbedrijven) {
					Klant klant = (Klant) klantbedrijf[0];
					Bedrijf bedrijf = (Bedrijf) klantbedrijf[1];

					Account account = new Account();
					// if update
					if(bedrijf.getAccountGuid() != null) {
						account.setId(bedrijf.getAccountGuid());
						webclientPropertiesStoreAccount.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreAccount.getPath() + "(guid'" + bedrijf.getAccountGuid() + "')");
						webclientPropertiesStoreAccount.add("method", "PUT");
					} else {
						account.setStartDate(new Date());

						webclientPropertiesStoreAccount.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreAccount.getPath());
						webclientPropertiesStoreAccount.add("method", "POST");
					}
					account.setName(bedrijf.getBedrijfsNaam());
					account.setPhone(bedrijf.getTelefoonnummer());
					account.setPostcode(bedrijf.getPostcode());
					account.setChamberOfCommerce(bedrijf.getKvKnummer());
					account.setCity(bedrijf.getPlaats());
					String adres = "";
					if(bedrijf.getStraat() != null && bedrijf.getHuisNr() != null)
						adres = bedrijf.getStraat() + " " + bedrijf.getHuisNr() + (bedrijf.getHuisNrToevoeging() == null ? "" : (" " + bedrijf.getHuisNrToevoeging()));
					account.setAddressLine1(adres.trim());
					account.setStatus("C");
					if(klant.getBtwnummer() != null && !klant.getBtwnummer().equals("") && !klant.getBtwnummer().equals("0000000")) // not null && not empty && not dummy!!
						account.setVatNumber(klant.getBtwnummer());

					CloseableHttpClient httpClient = HttpClients.custom()
			                .setSSLHostnameVerifier(new NoopHostnameVerifier())
			                .build();
					HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
					requestFactory.setHttpClient(httpClient);				
					RestTemplate restTemplate = new RestTemplate(requestFactory);
					
					Account resultAccount = null;
					boolean accountSuccess = false;
					try {
						HttpEntity<Account> entity = new HttpEntity<Account>(account,webclientPropertiesStoreAccount);				
						ResponseEntity<String> result = restTemplate.postForEntity(exactonlineBaseUri + webclientPropertiesStoreAccount.get("path"), entity, String.class);
						
						if(result != null)
							LOGGER.info("Result account post EO: " + result);	

						if(result != null && result.getBody() != null) {
							HttpStatus httpStatus =  result.getStatusCode();

							if(httpStatus.equals(HttpStatus.CREATED) || httpStatus.equals(HttpStatus.OK)) {
								String payload = result.getBody();

								if(bedrijf.getAccountGuid() == null) {
									resultAccount = processExactOnlineResponse(payload, Account.class);

									if(resultAccount != null) {
										LOGGER.info("Account processed in EO, ID = " + resultAccount.getId());
										bedrijf.setAccountGuid(resultAccount.getId());
									}
									else LOGGER.error("Account processing of " + account.getName() + "went wrong...");								
								} else resultAccount = account;
								// else nothing is ok for update

								bedrijf.setDatumVerwerktExactOnline(new Date());
								bedrijfDataService.save(bedrijf);
								accountSuccess = true;
							} else {
								//ErrorResponse errorResponse = (ErrorResponse) response.getPayload(ErrorResponse.class);
								//if (errorResponse != null && errorResponse.getError() != null && errorResponse.getError().getCode() != null && errorResponse.getError().getMessage() != null)
								//	LOGGER.error("Error create/update Bedrijf data in ExactOnline: " + errorResponse.getError().getCode() + " " + errorResponse.getError().getMessage().getValue());
								//else
								
								LOGGER.error("\"Error create/update Bedrijf data in ExactOnline: unknown error: "); // + (String) response.getPayloadAsString());
							}

						}
						
					} catch(RestClientException e) {
						throw new Exception("Cannot call sendMail service");
					}								
						

					boolean bankAccountSuccess = false;
					boolean saveKlant = false;
					if(accountSuccess) {
						if (klant.getBankrekeningNummer() != null && !klant.getBankrekeningNummer().isEmpty()) {
							BankAccount bankAccount = new BankAccount();
							// if update
							if(klant.getBankAccountGuid() != null) {
								bankAccount.setId(klant.getBankAccountGuid());
								webclientPropertiesStoreBankAccount.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreBankAccount.getPath() + "(guid'" + klant.getBankAccountGuid() + "')");
								webclientPropertiesStoreBankAccount.add("method", "PUT");
							} else {
								bankAccount.setCreated(new Date());
								bankAccount.setAccount(resultAccount.getId());
	
								webclientPropertiesStoreBankAccount.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreBankAccount.getPath());
								webclientPropertiesStoreBankAccount.add("method", "POST");
							}
							bankAccount.setBankAccount(klant.getBankrekeningNummer());
							bankAccount.setBankAccountHolderName(klant.getTenaamstelling());
							bankAccount.setMain(true);
	
							httpClient = HttpClients.custom()
					                .setSSLHostnameVerifier(new NoopHostnameVerifier())
					                .build();
							requestFactory = new HttpComponentsClientHttpRequestFactory();
							requestFactory.setHttpClient(httpClient);				
							restTemplate = new RestTemplate(requestFactory);							
							
							try {
								HttpEntity<BankAccount> entity = new HttpEntity<BankAccount>(bankAccount, webclientPropertiesStoreBankAccount);				
								ResponseEntity<String> result = restTemplate.postForEntity(exactonlineBaseUri + webclientPropertiesStoreBankAccount.get("path"), entity, String.class);
								
								if(result != null)
									LOGGER.info("Result saveDirectDebitMandates: " + result);	
								
								BankAccount resultBankAccount = null;
								if(result != null && result.getBody() != null) {
									HttpStatus httpStatus =  result.getStatusCode();

									if(httpStatus.equals(HttpStatus.CREATED) || httpStatus.equals(HttpStatus.OK)) {
										String payload = result.getBody();

										if(klant.getBankAccountGuid() == null) {
											resultBankAccount = processExactOnlineResponse(payload, BankAccount.class);
											if(resultBankAccount != null) {
												LOGGER.info("BankAccount processed in EO, ID = " + bankAccount.getId());
												klant.setBankAccountGuid(resultBankAccount.getId());
												saveKlant = true;
											}
											else
												LOGGER.error("BankAccount processing of " + account.getName() + "went wrong...");										
										} else resultBankAccount = bankAccount;
										// else nothing is ok for update
		
										bankAccountSuccess = true;
									} else {
										LOGGER.error("\"Error create/update BankAccount data in ExactOnline: unknown error");
									}
								}
							} catch(RestClientException e) {
								throw new Exception("Cannot call sendMail service");
							}
						} else
							bankAccountSuccess = true; // although no bankaccount is saved in EO																
							
					}

					if(bankAccountSuccess) {
						Contact contact = new Contact();
						// if update
						if(klant.getContactGuid() != null) {
							//contact.setId(klant.getContactGuid());
							webclientPropertiesStoreContact.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreContact.getPath() + "(guid'" + klant.getContactGuid() + "')");
							webclientPropertiesStoreContact.add("method", "PUT");
						} else {
							contact.setStartDate(new Date());
							contact.setAccount(resultAccount.getId());

							webclientPropertiesStoreContact.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreContact.getPath());
							webclientPropertiesStoreContact.add("method", "POST");
						}
						contact.setBusinessPhone(bedrijf.getTelefoonnummer());
						contact.setEmail(klant.getEmailAdres());
						contact.setFirstName(klant.getVoornaam());
						contact.setGender(klant.getGeslacht()); // ? which code/letter
						contact.setLastName(klant.getNaam());

						try {
							HttpEntity<Contact> entity = new HttpEntity<Contact>(contact, webclientPropertiesStoreContact);				
							ResponseEntity<String> result = restTemplate.postForEntity(exactonlineBaseUri + webclientPropertiesStoreContact.get("path"), entity, String.class);
							
							if(result != null)
								LOGGER.info("Result saveDirectDebitMandates: " + result);	
							
							Contact resultContact = null;
							if(result != null && result.getBody() != null) {
								HttpStatus httpStatus =  result.getStatusCode();

								if(httpStatus.equals(HttpStatus.CREATED) || httpStatus.equals(HttpStatus.OK)) {
									String payload = result.getBody();						

									if(klant.getContactGuid() == null) {
										resultContact = processExactOnlineResponse(payload, Contact.class);
										if(resultContact != null) {
											LOGGER.info("Contact processed in EO, ID = " + contact.getId());
											klant.setContactGuid(resultContact.getId());
											klant.setDatumVerwerktExactOnline(new Date());
											saveKlant = true;
										}
										else LOGGER.error("Contact processing of " + account.getName() + "went wrong...");									
									}
									// else nothing is ok for update								
								} else {
									LOGGER.error("\"Error create/update Contact data in ExactOnline: unknown error");
								}
							}
						} catch(RestClientException e) {
							throw new Exception("Cannot call sendMail service");
						}							
					}
					
					// Create DirectDebitMandates on new bankAccountSuccess + there must be a bankaccountGuid + on DirectDebitMandates not exists
					if (bankAccountSuccess && klant.getBankAccountGuid() != null && klant.getDirectDebitMandatesGuid() == null) { // create
						DirectDebitMandates mandates = new DirectDebitMandates();
						
						mandates.setAccount(bedrijf.getAccountGuid());
						mandates.setBankAccount(klant.getBankAccountGuid());
						//mandates.setCancellationDate(cancellationDate); // infinity
						mandates.setDescription("Incasso machtiging: " + bedrijf.getBedrijfsNaam());
						if (!klant.isEersteFactuurVerzonden())
							mandates.setFirstSend((short)1); // Set to 1 for first invoice
						else
							mandates.setFirstSend((short)0); // Set to 0 for reoccurring invoice
						mandates.setPaymentType(EPaymentType.RECURRENT_PAYMENT.getCode());
						mandates.setSignatureDate(new Date());
						mandates.setType(0); //use default
						
						// Create new DirectDebitMandates
						webclientPropertiesStoreDirectDebitMandates.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreDirectDebitMandates.getPath());
						webclientPropertiesStoreDirectDebitMandates.add("method", "POST");
						
						if (saveDirectDebitMandates(webclientPropertiesStoreDirectDebitMandates, bedrijf, klant, mandates))		
							saveKlant = true;
						
					} // no update needed
					
					if (saveKlant)
						klantDataService.save(klant);

				}
			} else {
				LOGGER.info("No Companies/Customers to process in ExactOnline");
				// no error exception throw new ServiceException("Error processing ExactOnline Companies/Customers");
			}

		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	private <T> T processExactOnlineResponse(String response, Class<T> type) throws ServiceException {
		T result = null;
		try {
			JsonNode jsonObj = new ObjectMapper().readTree(response).get("d");

			if(jsonObj != null) {
				JaxbJacksonObjectMapper mapper = new JaxbJacksonObjectMapper();
				result = mapper.readValue(jsonObj.asText(), type);
				LOGGER.debug(type.getName() + " response object fetched ");
			}

			return result;
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	private <T> List<T> processExactOnlineResponseList(String response, Class<T> type) throws ServiceException {
		List<T> result = null;
		try {
			JsonNode jsonObj = new ObjectMapper().readTree(response).get("d");
			JsonNode jsonResults = jsonObj.get("results");

			if(jsonResults.isArray()) {
				result = new ArrayList<>();
				JaxbJacksonObjectMapper mapper = new JaxbJacksonObjectMapper();
				for(JsonNode jsonNode : jsonResults) {
					T singleresult = mapper.readValue(jsonNode.asText(), type);
					LOGGER.debug(type.getName() + " response single object fetched ");
					result.add(singleresult);
				}
			}

			return result;
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}
	
	private boolean areVatCodesOk(String currentDivision, HashMap<String,String> vatcodes) throws ServiceException  {
		try {
			HttpHeaders webclientPropertiesVatCodes = restCallProperties(EExactOnlineRestApi.VatCodes, currentDivision);
			boolean result = true;
			
			CloseableHttpClient httpClient = HttpClients.custom()
	                .setSSLHostnameVerifier(new NoopHostnameVerifier())
	                .build();
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setHttpClient(httpClient);				
			RestTemplate restTemplate = new RestTemplate(requestFactory);			
			
			try {
				HttpEntity<Object> entity = new HttpEntity<>(webclientPropertiesVatCodes);		
				ResponseEntity<String> httpResult = restTemplate.exchange(exactonlineBaseUri + webclientPropertiesVatCodes.get("path"), HttpMethod.GET, entity, String.class);
				
				if(httpResult != null)
					LOGGER.debug("Result areVatCodesOk EO: " + result);	
				
				if(httpResult != null && httpResult.getBody() != null &&
						(httpResult.getStatusCode().equals(HttpStatus.CREATED) || httpResult.getStatusCode().equals(HttpStatus.OK))) {
					String payload = (String) httpResult.getBody();

					List<VatCode> vatcodeseo = (List<VatCode>) processExactOnlineResponseList(payload, VatCode.class);

					for (String vatcodekey : vatcodes.keySet()) {
						boolean found = false;
						for (VatCode vt : vatcodeseo) {
							//LOGGER.info("VATCODE id: " + vt.getId() + " code: " + vt.getCode() + " desc: " + vt.getDescription() + " perc: " + vt.getPercentage());
							Integer pct = new Integer(new Double(vt.getPercentage() * 100.0).intValue());
							Integer vatcodepct = new Integer(vatcodekey);
							if (pct.equals(vatcodepct)) {
								found = true;
								break;
							}
						}
						
						if (!found)
							result = false;					
					}				
									
				}				
			} catch(RestClientException e) {
				throw new Exception("Cannot call sendMail service");
			}			

			return result;
			
		} catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	private HashMap<String,String> getVatCodes() throws DataServiceException {
		HashMap<String,String> results = new HashMap<String,String>();
		
		Configuratie pct0 = configuratieDataService.findByPrimaryKey(EVatType.PCT_0.getCode());
		Configuratie pct21 = configuratieDataService.findByPrimaryKey(EVatType.PCT_21.getCode());
		
		results.put(pct0.getCode(), pct0.getWaardeInt().toString());
		results.put(pct21.getCode(), pct21.getWaardeInt().toString());
		
		return results;
	}

	@Transactional
	private void processFacturen(String currentDivision) throws ServiceException {
		try {
			HttpHeaders webclientPropertiesGLAccount = restCallProperties(EExactOnlineRestApi.GLAccountSalesSBDR, currentDivision);
			HttpHeaders webclientPropertiesStoreSalesEntry = restCallProperties(EExactOnlineRestApi.StoreSalesEntry, currentDivision);
			HttpHeaders webclientPropertiesStoreDirectDebitMandates = restCallProperties(EExactOnlineRestApi.StoreDirectDebitMandates, currentDivision);
			
			HashMap<String, String> vatcodes = getVatCodes();
			
			
			List<Factuur> facturen = factuurDataService.getAllFacturenToProcessExactOnline();
			List<FactuurConfig> factuurConfigs = factuurConfigDataService.findAllActive();

			// not nice, but for now every active product must have the same journal AND GLAccount....so pick one
			FactuurConfig factuurConfig = null;
			if(!factuurConfigs.isEmpty()) {
				factuurConfig = factuurConfigs.get(0);

				// change GLAccountnr if needed
				String newpath = (String) webclientPropertiesGLAccount.getFirst("path");
				newpath.replace("'8000'", "'" + factuurConfig.getExactOnlineGlAccountCode() + "'");
				webclientPropertiesGLAccount.add("path", newpath);
			}

			if(facturen != null && facturen.size() > 0) {
				
				GlAccount glAccount = null;
				CloseableHttpClient httpClient = HttpClients.custom()
		                .setSSLHostnameVerifier(new NoopHostnameVerifier())
		                .build();
				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
				requestFactory.setHttpClient(httpClient);		
				RestTemplate restTemplate = new RestTemplate(requestFactory);
				
				try {
					HttpEntity<Object> entity = new HttpEntity<>(webclientPropertiesGLAccount);		
					ResponseEntity<String> httpResult = restTemplate.exchange(exactonlineBaseUri + webclientPropertiesGLAccount.get("path"), HttpMethod.GET, entity, String.class);
					
					if(httpResult != null)
						LOGGER.debug("Result invoices GLAccount EO: " + httpResult);	
					
					if(httpResult != null && httpResult.getBody() != null &&
							(httpResult.getStatusCode().equals(HttpStatus.CREATED) || httpResult.getStatusCode().equals(HttpStatus.OK))) {
						String payload = (String) httpResult.getBody();

						List<GlAccount> glAccounts = (List<GlAccount>) processExactOnlineResponseList(payload, GlAccount.class);

						if(glAccounts != null && glAccounts.size() > 0) glAccount = glAccounts.get(0);
										
					}				
				} catch(RestClientException e) {
					throw new Exception("Cannot call sendMail service");
				}							


				if(glAccount != null) {
					LOGGER.info("GLAccount CODE:" + glAccount.getCode() + " ID:" + glAccount.getId());

					// process facturen
					for(Factuur factuur : facturen) {
						Bedrijf bedrijf = bedrijfDataService.findByBedrijfId(factuur.getBedrijf().getBedrijfId());
						Klant klant = bedrijfDataService.findKlantOfBedrijf(bedrijf.getBedrijfId());
						
						List<FactuurRegel> factuurRegels = factuurDataService.findFactuurRegelsByFactuurId(factuur.getFactuurId());

						if(bedrijf != null && bedrijf.getAccountGuid() != null && klant != null && factuurRegels != null && factuurRegels.size() > 0) {
							try {
								LOGGER.info("Process invoice: " + factuur.getReferentie() + " of " + bedrijf.getBedrijfsNaam() + " accountguid=" + bedrijf.getAccountGuid());
								SalesEntry salesEntry = new SalesEntry();
								salesEntry.setCustomer(bedrijf.getAccountGuid());
								salesEntry.setEntryDate(new Date());
								salesEntry.setDescription("CRZB ref#" + factuur.getReferentie());
								salesEntry.setJournal(factuurConfig.getExactOnlineJournal()); // 70 = Verkoop?
								salesEntry.setYourRef(factuur.getReferentie());
								salesEntry.setStatus(20); // open
								List<SalesEntryLine> salesEntryLines = new ArrayList<SalesEntryLine>();
	
								Map<String, AggregateFactuur> aggregateRegels = new HashMap<String, AggregateFactuur>();
								for(FactuurRegel factuurRegel : factuurRegels) {
	
									if(factuurRegel.getBedrag() != null) {
										AggregateFactuur aggregateRegel = null;
										String key = null;
										if(factuurRegel.getProductCode().equals(EProduct.MONITORING.getCode())) {
											key = EProduct.MONITORING.getCode();
										} else if(factuurRegel.getProductCode().equals(EProduct.RAPPORT.getCode())) {
											key = EProduct.RAPPORT.getCode();
										} else if (factuurRegel.getProductCode().equals(EProduct.DONATIE.getCode())) {
											key = EProduct.DONATIE.getCode();
										} else if(factuurRegel.getProductCode().equals(EProduct.ACHTERSTANDCHECK.getCode())){
											key = EProduct.ACHTERSTANDCHECK.getCode();
										} else if(factuurRegel.getProductCode().equals(EProduct.VERMELDING.getCode())){
											key = EProduct.VERMELDING.getCode();
										}
	
										aggregateRegel = aggregateRegels.get(key);
	
										if(aggregateRegel == null) {
											aggregateRegel = new AggregateFactuur();
											aggregateRegel.aantal = 1;
										} else
											aggregateRegel.aantal++;
	
										aggregateRegel.add(factuurRegel.getBedrag());
	
										if(key != null) aggregateRegels.put(key, aggregateRegel);
									}
								}
	
								if(aggregateRegels.size() > 0) {
									for(String key : aggregateRegels.keySet()) {
										AggregateFactuur aggregateRegel = aggregateRegels.get(key);
	
										String vatcode = null;
										if (key.equals(EProduct.MONITORING.getCode()) || key.equals(EProduct.RAPPORT.getCode()) || key.equals(EProduct.ACHTERSTANDCHECK.getCode()) || key.equals(EProduct.VERMELDING.getCode()))
											vatcode = vatcodes.get(EVatType.PCT_21.getCode());
										else if (key.equals(EProduct.DONATIE.getCode()))
											vatcode = vatcodes.get(EVatType.PCT_0.getCode());
										
										if(aggregateRegel.getAantal() > 0 ) { // Also 0.0 amounts may pass so disable: && aggregateRegel.getBedrag() > 0) {
											SalesEntryLine entryLine = new SalesEntryLine();
											entryLine.setGlAccount(glAccount.getId());
	
											entryLine.setDescription(aggregateRegel.getAantal() + " x " + key);
											entryLine.setAmountFc(aggregateRegel.getBedrag());
											if (vatcode != null)
												entryLine.setVatCode(vatcode);
											
											salesEntryLines.add(entryLine);
	
										}
	
									}
								}
	
	
								salesEntry.setSalesEntryLines(salesEntryLines.toArray(new SalesEntryLine[salesEntryLines.size()]));
	
								// SalesEntry Message
								httpClient = HttpClients.custom()
						                .setSSLHostnameVerifier(new NoopHostnameVerifier())
						                .build();
								requestFactory = new HttpComponentsClientHttpRequestFactory();
								requestFactory.setHttpClient(httpClient);		
								restTemplate = new RestTemplate(requestFactory);
								SalesEntryBase resultSalesEntry = null;
								boolean factuurVerwerkt = false;
								try {
									HttpEntity<SalesEntry> entity = new HttpEntity<SalesEntry>(salesEntry,webclientPropertiesStoreSalesEntry);				
									ResponseEntity<String> result = restTemplate.postForEntity(exactonlineBaseUri + webclientPropertiesStoreSalesEntry.get("path"), entity, String.class);
									
									if(result != null)
										LOGGER.info("Result account post EO: " + result);	

									if(result != null && result.getBody() != null) {
										HttpStatus httpStatus =  result.getStatusCode();

										if(httpStatus.equals(HttpStatus.CREATED) || httpStatus.equals(HttpStatus.OK)) {
											String payload = result.getBody();

											resultSalesEntry = processExactOnlineResponse(payload, SalesEntryBase.class);
											
											if(resultSalesEntry != null)
												LOGGER.info("SalesEntry processed in EO, ID = " + resultSalesEntry.getEntryId());
											else
												LOGGER.error("Error Processing invoice in ExactOnline of company (bedrijf_id, bedrijfnaam): " + bedrijf.getBedrijfId() + " " + bedrijf.getBedrijfsNaam() + " invoice (factuur_id): " + factuur.getFactuurId());
			
											factuur.setSalesEntryGuid(resultSalesEntry.getEntryId());
											factuur.setDatumVerwerktExactOnline(new Date());
											factuurDataService.save(factuur);
											factuurVerwerkt = true;		
										}
									}
									
								} catch(RestClientException e) {
									throw new Exception("Cannot call sendMail service");
								}										
								
								
								if (factuurVerwerkt) {
									if (klant.isEersteFactuurVerzonden() && klant.getDirectDebitMandatesGuid() != null) { // if this is NOT the first invoice, then set first send to false
										DirectDebitMandates mandates = new DirectDebitMandates();
										
										mandates.setAccount(bedrijf.getAccountGuid());
										mandates.setBankAccount(klant.getBankAccountGuid());
										mandates.setFirstSend((short)0); // update field! Only 1 for first invoice									
										
										// Update DirectDebitMandates
										webclientPropertiesStoreDirectDebitMandates.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreDirectDebitMandates.getPath() + "(guid'" + klant.getDirectDebitMandatesGuid() + "')");
										webclientPropertiesStoreDirectDebitMandates.add("method", "PUT");
										
										saveDirectDebitMandates(webclientPropertiesStoreDirectDebitMandates, bedrijf, klant, mandates);												
									} else if (klant.getDirectDebitMandatesGuid() != null) { // if there is already a mandatesGuid update first invoice sent. Set to true
										klant.setEersteFactuurVerzonden(true);
										klantDataService.save(klant);
									} else if (klant.getDirectDebitMandatesGuid() == null && klant.getBankAccountGuid() != null) { // if there was no mandatesGuid, create one and set first invoice sent to false.
										DirectDebitMandates mandates = new DirectDebitMandates();
										
										mandates.setAccount(bedrijf.getAccountGuid());
										mandates.setBankAccount(klant.getBankAccountGuid());
										//mandates.setCancellationDate(cancellationDate); // infinity
										mandates.setDescription("Incasso machtiging: " + bedrijf.getBedrijfsNaam());
										if (!klant.isEersteFactuurVerzonden())
											mandates.setFirstSend((short)1); // Set to 1 for first invoice
										else
											mandates.setFirstSend((short)0); // Set to 0 for reocurring invoice
										mandates.setPaymentType(EPaymentType.RECURRENT_PAYMENT.getCode());
										mandates.setSignatureDate(new Date());
										mandates.setType(0); //use default
										
										// Create new DirectDebitMandates
										webclientPropertiesStoreDirectDebitMandates.add("path", "/api/v1/" + currentDivision + EExactOnlineRestApi.StoreDirectDebitMandates.getPath());
										webclientPropertiesStoreDirectDebitMandates.add("method", "POST");
										
										if (saveDirectDebitMandates(webclientPropertiesStoreDirectDebitMandates, bedrijf, klant, mandates))		
											klantDataService.save(klant);
									} else {
										LOGGER.error("ExactOnline processFacturen error: Cannot process DirectDebitMandates of klant: " + klant.getGebruikerId());
									}
								}						
							} catch (Exception e) {
								LOGGER.error("Error Processing invoice in ExactOnline of company (bedrijf_id, bedrijfnaam): " + bedrijf.getBedrijfId() + " " + bedrijf.getBedrijfsNaam() + " invoice (factuur_id): " + factuur.getFactuurId() + " Skipping this invoice, going for next one.\n" + e.getMessage());
							}
						} else
							LOGGER.error("Cannot process invoices of company (bedrijf_id, bedrijfnaam): " + bedrijf.getBedrijfId() + " " + bedrijf.getBedrijfsNaam() + " invoice (factuur_id): " + factuur.getFactuurId());
					}
				}
			} else {
				LOGGER.info("No invoices to process in ExactOnline");
			}

		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	private HttpHeaders restCallProperties(EExactOnlineRestApi eoRestApi, String division) throws ServiceException {
		HttpHeaders webclientProperties = null;
		ExactOnlineAccess exactOnlineAccess = null;

		// do we need to refresh access token?
		refreshToken();

		// create call props
		try {
			exactOnlineAccess = exactOnlineAccessDataService.find();
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}

		if(exactOnlineAccess != null && exactOnlineAccess.getAccessToken() != null) {

			webclientProperties = new HttpHeaders();
			webclientProperties.add("callId", eoRestApi.getCallId());
			webclientProperties.add("path", "/" + division + eoRestApi.getPath()); // api/v1/
			webclientProperties.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			//webclientProperties.put("Content-Type", "application/json");
			webclientProperties.add("Accept", "application/json");
			webclientProperties.add("Authorization", "Bearer " + exactOnlineAccess.getAccessToken());
		}

		return webclientProperties;

	}

	//	@Override
	//	public void processExactOnline() throws ServiceException {
	//		authenticate();
	//	}
	
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	private void processTokenResult(TokenResponse tokenResponse) {
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

	private void sendRefreshTokenRequest(ExactOnlineAccess exactOnlineAccess) throws ServiceException {
		RefreshTokenRequest tokenRequest = new RefreshTokenRequest();


		if(exactOnlineAccess == null || exactOnlineAccess.getRefreshToken() == null)
			throw new ServiceException("Cannot refresh token on empty exactOnlineAccess");

		tokenRequest.setRefresh_token(exactOnlineAccess.getRefreshToken());
		tokenRequest.setClient_id(exactOnlineClientId);
		tokenRequest.setClient_secret(exactOnlineSecret);

		String payload = null;
		
		try {

			LOGGER.debug("Send ExactOnline refresh token request");
			CloseableHttpClient httpClient = HttpClients.custom()
	                .setSSLHostnameVerifier(new NoopHostnameVerifier())
	                .build();
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setHttpClient(httpClient);		
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

				HttpEntity<RefreshTokenRequest> entity = new HttpEntity<RefreshTokenRequest>(tokenRequest,headers);		
				//ResponseEntity<String> result = restTemplate.exchange(sbdrmuleBaseUri + exactOnlineRedirectRefreshTokenUri, HttpMethod.GET, entity, String.class);
				ResponseEntity<String> result = restTemplate.exchange(exactonlineBaseUri + "/api/oauth2/token", HttpMethod.POST, entity, String.class);
				
				if (result != null) {
					if (result.getStatusCode().equals(HttpStatus.CREATED) || result.getStatusCode().equals(HttpStatus.OK)) {
						payload = result.getBody();
						
						JaxbJacksonObjectMapper mapper = new JaxbJacksonObjectMapper();
						TokenResponse resultToken = mapper.readValue(payload, TokenResponse.class);
	
						processTokenResult(resultToken);
					} else if (result.getStatusCodeValue() == 500) {
						JaxbJacksonObjectMapper mapper = new JaxbJacksonObjectMapper();
						ErrorResponse resultError = mapper.readValue(result.getBody(), ErrorResponse.class);
						
						LOGGER.error("Error fetching ExactOnline refreshtoken: " + resultError.getError().getCode() + " " + resultError.getError().getMessage());	
						
					} else {
						LOGGER.error("Error fetching ExactOnline refreshtoken. ");	
					}
				}
			} catch(RestClientException e) {
				throw new Exception("Cannot call sendMail service");
			}				

			LOGGER.debug("ExactOnline refresh token request processed");

		} catch(Exception e) {
			if (payload != null)
				LOGGER.error("sendRefreshTokenRequest wrong payload received: " + payload);
			throw new ServiceException(e);
		}
	}

	private void sendTokenRequest(String code) throws ServiceException {
		TokenRequest tokenRequest = new TokenRequest();

		tokenRequest.setClient_id(exactOnlineClientId);
		// MBR 23-05-2016 for DEV: replace webBaseUri with muleBaseUri
		tokenRequest.setRedirect_uri(sbdrwebBaseUri + exactOnlineRedirectCallbackUri);
		tokenRequest.setCode(code); // returned from ExactOnline Authenticate
		tokenRequest.setClient_secret(exactOnlineSecret);

		try {
			LOGGER.debug("Send ExactOnline token request");
			CloseableHttpClient httpClient = HttpClients.custom()
	                .setSSLHostnameVerifier(new NoopHostnameVerifier())
	                .build();
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setHttpClient(httpClient);		
			RestTemplate restTemplate = new RestTemplate(requestFactory);			
			
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

				HttpEntity<TokenRequest> entity = new HttpEntity<TokenRequest>(tokenRequest,headers);		
				////ResponseEntity<String> result = restTemplate.exchange(sbdrmuleBaseUri + exactOnlineRedirectTokenUri, HttpMethod.GET, entity, String.class);
				// ResponseEntity<String> result = restTemplate.exchange(sbdrmuleBaseUri + exactOnlineRedirectTokenUri, HttpMethod.POST, entity, String.class);
				ResponseEntity<String> result = restTemplate.exchange(exactonlineBaseUri + "/api/oauth2/token" , HttpMethod.POST, entity, String.class);
				
				if(result != null) {
					if (result.getStatusCode().equals(HttpStatus.CREATED) || result.getStatusCode().equals(HttpStatus.OK)) {
						JaxbJacksonObjectMapper mapper = new JaxbJacksonObjectMapper();
						TokenResponse resultToken = mapper.readValue(result.getBody(), TokenResponse.class);
						
						processTokenResult(resultToken);
					} else if (result.getStatusCodeValue() == 500) {
						JaxbJacksonObjectMapper mapper = new JaxbJacksonObjectMapper();
						ErrorResponse resultError = mapper.readValue(result.getBody(), ErrorResponse.class);
						
						LOGGER.error("Error fetching ExactOnline token: " + resultError.getError().getCode() + " " + resultError.getError().getMessage());	
						
					} else {
						LOGGER.error("Error fetching ExactOnline token. ");	
					}
				}

			} catch(RestClientException e) {
				throw new Exception("Cannot call sendMail service");
			}		
			
			LOGGER.debug("ExactOnline token request sent");

		} catch(Exception e) {	
			throw new ServiceException(e);
		}
	}

	private HashMap<String, Object> xmlCallProperties(EExactOnlineRestApi eoRestApi, String division) throws ServiceException {
		HashMap<String, Object> webclientProperties = null;
		ExactOnlineAccess exactOnlineAccess = null;

		// do we need to refresh access token?
		refreshToken();

		// create call props
		try {
			exactOnlineAccess = exactOnlineAccessDataService.find();
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}

		if(exactOnlineAccess != null && exactOnlineAccess.getAccessToken() != null) {

			webclientProperties = new HashMap<String, Object>();
			webclientProperties.put("callId", eoRestApi.getCallId());
			webclientProperties.put("path", "/api/v1/" + division + eoRestApi.getPath());
			webclientProperties.put("Content-Type", "application/xml");
			webclientProperties.put("Accept", "application/xml");
			webclientProperties.put("Authorization", "Bearer " + exactOnlineAccess.getAccessToken());
		}

		return webclientProperties;

	}

	private class AggregateFactuur {
		private int aantal = 0;
		private double bedrag = 0;

		public AggregateFactuur() {

		}

		public void add(BigDecimal bedrag) {
			this.bedrag += bedrag.doubleValue();
		}

		public int getAantal() {
			return aantal;
		}

		public double getBedrag() {
			return bedrag;
		}
	}
}