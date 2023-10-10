package nl.devoorkant.sbdr.ws;

import nl.devoorkant.sbdr.business.service.BedrijfService;
import nl.devoorkant.sbdr.business.service.CompanyAccountService;
import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.service.cir.InsolventieService;
import nl.devoorkant.sbdr.business.transfer.*;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.util.EKlantStatus;
import nl.devoorkant.sbdr.data.util.EReferentieInternType;
import nl.devoorkant.sbdr.data.util.ERol;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.ws.transfer.*;
import nl.devoorkant.util.StringUtil;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Component
@Path("/AccountService/account")
public class AccountResource {
    @Value("${api.client_id}")
    private String apiClientId;
    
    @Value("${api.secret}")
    private String apiSecret;
    
	@Autowired
	private InsolventieService insolventieService;
	@Autowired
	private UserResource userResource;
	private static Logger LOGGER = LoggerFactory.getLogger(AccountResource.class);
	@Autowired
	BedrijfService bedrijfService;
	@Autowired
	CompanyAccountService companyAccountService;
	@Autowired
	GebruikerService gebruikerService;
	@Autowired
	LoginResource loginResource;

	@Path("activateCustomerBriefCode")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activateCustomerBriefCode(@QueryParam("activationcode") String activationCode, @QueryParam("userid") String obfUserId) {
		Integer userId = null;
		if(NumberUtils.isParsable(obfUserId)){
			userId = Integer.parseInt(obfUserId);
		}else {
			userId = ObfuscatorUtils.deofuscateInteger(obfUserId);
		}		
		ErrorResource error = null;

		LOGGER.info("activateKlant brief code: " + activationCode);

		UserTransfer user = userResource.getUser();

		if(activationCode != null && userId != null) {
			if((userId.equals(user.getUserId()) && user.getKlant()) || gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER)) { //sbdr admin
				try {
					ErrorService errorservice = companyAccountService.activateKlantTwoFactorCode(activationCode, userId);
					if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
				} catch(ServiceException e) {
					error = new ErrorResource(ErrorResource.ACCOUNT_NOT_ACTIVATED);
				}
			} else error = new ErrorResource(ErrorResource.ACCOUNT_NOT_ACTIVATED);
		} else error = new ErrorResource(ErrorResource.ACCOUNT_NOT_ACTIVATED);

		return Response.ok(error).build();
	}
	
	@Path("changepassword")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//@Path("{id}")
	public Response changePassword(ChangePasswordTransfer changePassword) {
		LOGGER.info("AccountResource, changepassword");

		ErrorResource error = null;
		TokenTransfer tokenTransfer = null;

		UserTransfer user = userResource.getUser();
		
		if(user == null || changePassword == null || (changePassword.currentPassword == null && changePassword.newPassword == null)) {
			error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
			LOGGER.error("changepassword: one or more parameters is empty/null");
		} else {
			ErrorService errorService = null;

			try {
				errorService = gebruikerService.changeWachtwoordGebruiker(changePassword.userId, user.getBedrijfId(), user.getBedrijfId(), changePassword.currentPassword, changePassword.newPassword, false);
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CHANGE_WACHTWOORD);
				LOGGER.error("changepassword: " + e.getMessage());
			}

			if(errorService != null) {
				error = new ErrorResource(errorService.getErrorCode());
				LOGGER.error("changepassword: " + errorService.getErrorMsg());
			} else {
				LoginData loginData = new LoginData();
				loginData.username = changePassword.userName;
				loginData.password = changePassword.newPassword;
				loginData.cntLoginAttempt = 0;

				Response logoutResponse = userResource.logout();

				if(logoutResponse.getEntity() != null) {
					error = new ErrorResource(ErrorResource.CANNOT_RELOGIN);
					LOGGER.error("changepassword: logoutResponse.getEntity is not null");
				} else {
					tokenTransfer = loginResource.authenticate(loginData);
					if(tokenTransfer == null || tokenTransfer.getToken() == null || (tokenTransfer.getErrorMsg() != null && !tokenTransfer.getErrorMsg().equals(""))) {
						error = new ErrorResource(ErrorResource.CANNOT_RELOGIN);
						LOGGER.error("changepassword: authenticate method failed");
					}
				}
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(tokenTransfer).build();
	}

	/**
	 * Create new customer letter.
	 *
	 * @return nothing.
	 */
	@Path("createNewAccountLetter")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNewAccountLetter(@QueryParam("bedrijfId") String obfBedrijfId) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);

		LOGGER.info("AccountResource, createNewAccountLetter");

		UserTransfer user = userResource.getUser();
		ErrorResource error = null;

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_LETTER);
			LOGGER.error("createNewAccountLetter: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("createNewAccountLetter: action not allowed(KLANT_BEHEER");
				} else {
					if(bedrijfId == null) {
						error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
						LOGGER.error("createNewAccountLetter: one or more parameters is empty/null");
					} else {
						companyAccountService.createNewAccountLetterForKlant(bedrijfId);

					}
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_LETTER);
				LOGGER.error("createNewAccountLetter: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok().build();
	}

	@Path("deleteAccount")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(String obfKlantId) {
		Integer klantId = null;
		if(NumberUtils.isParsable(obfKlantId)){
			klantId = Integer.parseInt(obfKlantId);
		}else {
			klantId = ObfuscatorUtils.deofuscateInteger(obfKlantId);
		}
		LOGGER.info("AccountResource, deleteAccount");

		ErrorResource error = null;
		UserTransfer user = userResource.getUser();

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_REMOVE_KLANT);
			LOGGER.error("deleteAccountOfBedrijf: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("deleteAccountOfBedrijf: action not allowed(KLANT_BEHEER");
				} else {
					if(klantId == null) {
						error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
						LOGGER.error("deleteAccountOfBedrijf: one or more paramters is empty/null");
					} else {
						ErrorService errorservice = companyAccountService.deleteKlant(klantId, user.getUserId());
						if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
					}
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_REMOVE_KLANT);
				LOGGER.error("deleteAccountOfBedrijf: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok().build();
	}

	@Path("deleteAccountOfBedrijf")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAccountOfBedrijf(String obfBedrijfId) {		
		Integer bedrijfId = null;
		if(NumberUtils.isParsable(obfBedrijfId)){
			bedrijfId = Integer.parseInt(obfBedrijfId);
		}else {
			bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		}	
		LOGGER.info("accountResource: deleteAccountOfBedrijf");

		ErrorResource error = null;
		UserTransfer user = userResource.getUser();

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_REMOVE_KLANT);
			LOGGER.error("deleteAccountBedrijf: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), bedrijfId, EBevoegdheid.KLANT_BEHEER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("deleteAccountBedrijf: action not allowed(KLANT_BEHEER)");
				} else {
					if(bedrijfId == null) {
						error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
						LOGGER.error("deleteAccountBedrijf: one or more parameters is empty/null");
					} else {
						ErrorService errorservice = companyAccountService.deleteKlantOfBedrijf(bedrijfId, user.getUserId());
						if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
					}
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_REMOVE_KLANT);
				LOGGER.error("deleteAccountOfBedrijf: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok().build();
	}

	/**
	 * Retrieves the bankruptcies overview of last week.
	 *
	 * @return A transfer containing the bankruptcies.
	 */
	@GET
	@Path("faillissementenafgelopenweek")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response faillissementenAfgelopenWeek(@QueryParam("userId") String obfUserId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer userId = ObfuscatorUtils.deofuscateInteger(obfUserId);
		LOGGER.info("accountResource: faillissementenafgelopenweek");

		ErrorResource error = null;
		List<Sort.Order> orders = new LinkedList<>();
		FaillissementenOverviewTransfer[] faillissementen = null;
		PageRequest pageRequest;
		String contentRange = null;

		if(sortedBy != null && !sortedBy.equals("")) {
			Order order;
			Direction sortDirection = null;

			if(sortDir != null) {
				if(sortDir.equals("ASC")) sortDirection = Direction.ASC;
				else if(sortDir.equals("DESC")) sortDirection = Direction.DESC;
			}

			if(sortDirection != null) order = new Order(sortDirection, sortedBy);
			else order = new Order(Direction.ASC, sortedBy);

			orders.add(order);
		}

		try {
			if(range == null || range.equals("")) {
				error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
				LOGGER.error("faillissementenafgelopenweek: parameter range is empty/null");
			} else {
				String[] rangeParts = range.replace("items=", "").split("-");
				int from = new Integer(rangeParts[0]);
				int to = new Integer(rangeParts[1]);
				int size = to - from + 1;
				int page = to / size - 1;

				if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
				else pageRequest = new PageRequest(page, size);

				PageTransfer<FaillissementenOverviewTransfer> faillissementenAfgelopenWeek = insolventieService.findFaillissementenAfgelopenWeek(pageRequest);

				if(faillissementenAfgelopenWeek == null) {
					//error = new ErrorResource(ErrorResource.RESULTS_EMPTY_MSG);
					LOGGER.error("null result in fetching gebruikersOfKlantGebruiker");
				} else {
					long count = faillissementenAfgelopenWeek.getTotalElements();

					contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

					if(faillissementenAfgelopenWeek.getContent() != null)
						faillissementen = faillissementenAfgelopenWeek.getContent().toArray(new FaillissementenOverviewTransfer[faillissementenAfgelopenWeek.getContent().size()]);

					LOGGER.debug("faillissementenafgelopenweek: pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
				}
			}
		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_FAILLISSEMENTENOVERZICHT);
			LOGGER.error("faillissementenafgelopenweek: " + e.getMessage());
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(faillissementen).header("Content-Range", contentRange).build();
	}

	/**
	 * Retrieves a specific company.
	 *
	 * @return A transfer containing the company data.
	 */
	@Path("getCompanyAccountData")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCompanyAccountData(@QueryParam("bedrijfId") String obfBedrijfId) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("accountResource: getCompanyAccountData");

		UserTransfer user = userResource.getUser();
		CompanyAccount account = new CompanyAccount();

		ErrorResource error = null;

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_ACCOUNTDATA);
			LOGGER.error("getCompanyAccountData: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), bedrijfId, EBevoegdheid.KLANTGEGEVENS_MUTEREN) || (!gebruikerService.hasRightToDo(user.getUserId(), bedrijfId,  EBevoegdheid.KLANTGEGEVENS_MUTEREN) && !user.getBedrijfId().equals(bedrijfId))) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("getCompanyAccountData: action not allowed(KLANTGEGEVENS_MUTEREN)");
				} else {
					Bedrijf bedrijf = bedrijfService.getBedrijf(bedrijfId);
					Collection<String> klantCodes = new ArrayList<>();
					klantCodes.add(EKlantStatus.DATA_NOK.getCode());
					klantCodes.add(EKlantStatus.PROSPECT.getCode());
					klantCodes.add(EKlantStatus.ACTIEF.getCode());

					Klant klant = gebruikerService.findKlantOfBedrijfByBedrijfIdAndStatusCode(bedrijfId, klantCodes);
					String referentieIntern;

					// SbdrNummer is not prefixed by BA- here!!!

					if(bedrijf == null || bedrijf.getSbdrNummer() == null) {
						account.setErrorMessage("Kan gegevens niet ophalen.");
						LOGGER.error("getCompanyAccountData: bedrijf and/or bedrijf.sbdrNummer is null");
					} else {
						referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

						account.setBedrijf(bedrijf != null ? new BedrijfEntityTransfer(bedrijf) : null);
						account.setReferentieIntern(referentieIntern);
						account.setKlant(klant != null ? new KlantEntityTransfer(klant) : null);
						// reset emptied fields to values for editing
						if (account.getKlant() != null) {
							account.getKlant().setBankrekeningNummer(klant.getBankrekeningNummer());
							account.getKlant().setTenaamstelling(klant.getTenaamstelling());
							account.getKlant().setBtwnummer(klant.getBtwnummer());
							account.getKlant().setNietBtwPlichtig(klant.isNietBtwPlichtig());
							account.getKlant().setEmailAdresFacturatie(klant.getEmailAdresFacturatie());
						}
					}
				}
			} catch(Exception e) {
				LOGGER.error("getCompanyAccountData: " + e.getMessage());
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_ACCOUNTDATA);
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(account).build();
	}

	/**
	 * Retrieves a specific company.
	 *
	 * @return A transfer containing the company data.
	 */
	@Path("getCompanyData")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCompanyData(@QueryParam("bedrijfId") String obfBedrijfId) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("accountResource: getCompanyData");

		UserTransfer user = userResource.getUser();
		CompanyAccount account = null;
		ErrorResource error = null;

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_ACCOUNTDATA);
			LOGGER.error("getCompanyData: user is null");
		} else {
			try {
				account = new CompanyAccount();

				if(!gebruikerService.hasRightToDo(user.getUserId(), bedrijfId, EBevoegdheid.KLANT_BEHEER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("getCompanyData: action not allowed(KLANT_BEHEER)");
				} else {
					Bedrijf bedrijf = bedrijfService.getBedrijf(bedrijfId);
					Klant klant = gebruikerService.findKlantgebruikerByBedrijfId(bedrijfId);

					if(bedrijf == null) {
						account.setErrorMessage("Kan gegevens niet ophalen.");
						LOGGER.error("getCompanyData: bedrijf is null");
					} else {
						String referentieIntern = null;
						if(bedrijf.getSbdrNummer() != null)
							referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

						account.setBedrijf(bedrijf != null ? new BedrijfEntityTransfer(bedrijf) : null);
						account.setReferentieIntern(referentieIntern);
						account.setKlant(klant != null ? new KlantEntityTransfer(klant) : null);
					}
				}

			} catch(Exception e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
				LOGGER.error("getCompanyData: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(account).build();
	}

	@Path("getFacturen")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFacturen(@QueryParam("userId") String obfUid, @QueryParam("bedrijfId") String obfBId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer uId = ObfuscatorUtils.deofuscateInteger(obfUid);
		Integer bId = ObfuscatorUtils.deofuscateInteger(obfBId);
		FactuurTransfer[] fTa = null;
		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		boolean isAllowed = false;

		if(uId != null && uId.equals(user.getUserId()) && gebruikerService.hasRightToDo(user.getUserId(), bId, EBevoegdheid.RAPPORT_INZIEN))
			isAllowed = true;

		List<Sort.Order> orders = new LinkedList<>();

		if(sortedBy != null && !sortedBy.equals("")) {
			Sort.Order order;
			Sort.Direction sortDirection = null;

			if(sortDir != null) {
				if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
				else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
			}

			if(sortDirection != null) order = new Sort.Order(Sort.Direction.ASC, sortedBy);
			else order = new Sort.Order(sortedBy);

			orders.add(order);
		}

		PageRequest pageRequest = null;
		String contentRange = null;

		if(isAllowed && range != null && !range.equals("")) {
			String[] rangeParts = range.replace("items=", "").split("-");
			int from = new Integer(rangeParts[0]);
			int to = new Integer(rangeParts[1]);
			int size = to - from + 1;
			int page = to / size - 1;

			if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
			else pageRequest = new PageRequest(page, size);

			PageTransfer<FactuurTransfer> fTp = null;

			try {
				fTp = bedrijfService.findFacturenOfBedrijf(bId, pageRequest);
			} catch(ServiceException e) {
				LOGGER.error(e.getMessage());
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_FACTUUR);
			}

			if(fTp != null) {
				long count = fTp.getTotalElements();

				contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

				if(fTp.getContent() != null)
					fTa = fTp.getContent().toArray(new FactuurTransfer[fTp.getContent().size()]);
			} else {
				LOGGER.error("null result in fetching supporttickets of user " + uId);
				error = new ErrorResource(ErrorResource.ERROR_RESULTS_EMPTY);
			}
		} else {
			error = new ErrorResource(ErrorResource.ERROR_PAGING);
		}

		if(fTa != null) return Response.ok(fTa).header("Content-Range", contentRange).build();
		else return Response.ok(error).build();
	}

	@GET
	@Path("faillissementenstats")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFaillissementStats() {
		InsolventiePublicTransfer stats = null;
		ErrorResource error = null;

		try {
			stats = insolventieService.findPublicData();
		} catch(ServiceException e) {
			LOGGER.error("Method getFaillissementStats: " + e.getMessage());
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_FAILLISSEMENTENOVERZICHT);
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(stats).build();
	}

	/**
	 * Performs IBAN check.
	 *
	 * @return Boolean true is IBAN is ok, false otherwise.
	 */
	@GET
	@Path("ibanCheck")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response ibanCheck(@QueryParam("ibannummer") String ibanNummer) {
		IbanCheckResult result = new IbanCheckResult();
		LOGGER.info("ibanCheck: " + ibanNummer);

		try {
			boolean ibanresult = bedrijfService.isIbanCheckOk(ibanNummer);

			result.result = new Boolean(ibanresult);

		} catch(ServiceException e) {
			ErrorResource error = new ErrorResource(ErrorResource.CANNOT_PERFORM_IBANCHECK);

			// return Error
			return Response.ok(error).build();
		}

		return Response.ok(result).build();
	}

	@Path("updateAccountBedrijfData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAccountBedrijfData(final CompanyAccount companyAccount) {
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		LOGGER.info("updateAccountBedrijfData");
		if(companyAccount.getKlant() != null && companyAccount.getBedrijf() != null &&
				gebruikerService.hasRightToDo(user.getUserId(), companyAccount.getBedrijf().getBedrijfId(), EBevoegdheid.KLANTGEGEVENS_MUTEREN) && // sbdr admin / klant admin/ klant hoofd
				gebruikerService.hasRightToDo(user.getUserId(), companyAccount.getBedrijf().getBedrijfId(), EBevoegdheid.BEDRIJFGEGEVENS_MUTEREN)) // sbdr_admin
		{
			try {
				ErrorService errorservice = companyAccountService.updateKlantBedrijfAccountData(companyAccount.getKlant(), companyAccount.getBedrijf(), user.getUserId());
				if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_UPDATE_KLANT);
			} catch(Exception e) {
				LOGGER.debug("Wrong exception: " + e.getMessage());
			}
		} else error = new ErrorResource(ErrorResource.CANNOT_UPDATE_KLANT);

		return Response.ok(error).build();
	}

	@Path("updateAccountData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response updateAccountData(final CompanyAccount cA) {
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		Klant k = null;
		Bedrijf b = null;
		if(cA != null) {
			k = cA.getKlant();
			b = cA.getBedrijf();
		}

		LOGGER.info("updateAccountData");
		if(k != null && b != null && (gebruikerService.hasRightToDo(user.getUserId(), b.getBedrijfId(), EBevoegdheid.KLANTGEGEVENS_MUTEREN))) // sbdr admin / klant admin/ klant hoofd
		{
			try {
				ErrorService errorservice = companyAccountService.updateAccountData(k, b);
				if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_UPDATE_KLANT);
			} catch(Exception e) {
				LOGGER.debug("Wrong exception: " + e.getMessage());
			}
		} else error = new ErrorResource(ErrorResource.CANNOT_UPDATE_KLANT);

		return Response.ok(error).build();
	}

	@Path("updateBedrijfData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateBedrijfData(final BedrijfEntityTransfer bedrijf) {
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		LOGGER.info("updateAccountBedrijfData");
		if(bedrijf != null && gebruikerService.hasRightToDo(user.getUserId(), bedrijf.getBedrijfId(), EBevoegdheid.BEDRIJFGEGEVENS_MUTEREN)) // sbdr_admin
		{
			try {
				Klant klant = gebruikerService.findKlantgebruikerByBedrijfId(bedrijf.getBedrijfId());
				
				ErrorService errorservice = null;
				
				if (klant != null)  {
					errorservice = companyAccountService.updateKlantBedrijfAccountData(klant, bedrijf, user.getUserId());
				}
				if (errorservice == null)
					errorservice = companyAccountService.updateBedrijfData(bedrijf);
				if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_UPDATE_KLANT);
			} catch(Exception e) {
				LOGGER.debug("Wrong exception: " + e.getMessage());
			}
		} else error = new ErrorResource(ErrorResource.CANNOT_UPDATE_KLANT);

		return Response.ok(error).build();
	}

	/**
	 * Retrieves the companies with an alert.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("gebruikers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response users(@QueryParam("userId") String obfUserId, @QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer userId = null;
		if(NumberUtils.isParsable(obfUserId)){
			userId = Integer.parseInt(obfUserId);
		}else {
			userId = ObfuscatorUtils.deofuscateInteger(obfUserId);
		}	
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("CompaniesAlert GET");

		UserTransfer user = userResource.getUser();

		boolean isAllowed = false;
		boolean isSbdrAdmin = false;
		if(userId != null && userId.equals(user.getUserId()) && gebruikerService.hasRightToDo(user.getUserId(), bedrijfId, EBevoegdheid.KLANTGEGEVENS_MUTEREN)) // sbdr admin / klant admin/ klant hoofd
		{
			isAllowed = true;

			if(gebruikerService.hasRightToDo(user.getUserId(), bedrijfId, EBevoegdheid.BEDRIJFGEGEVENS_MUTEREN)) // sbdr admin!!
				isSbdrAdmin = true;
		}
		LOGGER.info("Filtercriteria: filter '" + filterValue + "' sortedBy '" + sortedBy + "'" + " sortDir '" + sortDir);

		// Sort order check with PFO?
		List<Sort.Order> orders = new LinkedList<Sort.Order>();

		if(sortedBy != null && !sortedBy.equals("")) {
			Order order = null;
			Direction sortDirection = null;

			if(sortDir != null) {
				if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
				else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
			}

			if(sortDirection != null) order = new Order(Sort.Direction.ASC, sortedBy);
			else order = new Order(sortedBy);

			orders.add(order);
		}

		PageRequest pageRequest = null;
		String contentRange = null;


		List<GebruikerTransfer> gebruikerlist = null;
		UserAccount[] users = null;
		int pageNumber = -1;

		if(isAllowed &&
				range != null && !range.equals("")) {
			String[] rangeParts = range.replace("items=", "").split("-");
			int from = new Integer(rangeParts[0]);
			int to = new Integer(rangeParts[1]);
			int size = to - from + 1;
			int page = to / size - 1;

			if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
			else pageRequest = new PageRequest(page, size);

			pageNumber = page;


			try {

				PageTransfer<GebruikerTransfer> gebruikers = null;

				// if user from company load users of company else if sbdr then load users of specific company
				if(!isSbdrAdmin)
					gebruikers = gebruikerService.findActiveGebruikersOfKlantGebruiker(userId, bedrijfId, pageRequest);
				else {
					Collection<String> klantCodes = new ArrayList<>();
					klantCodes.add(EKlantStatus.DATA_NOK.getCode());
					klantCodes.add(EKlantStatus.PROSPECT.getCode());
					klantCodes.add(EKlantStatus.ACTIEF.getCode());

					Klant klant = gebruikerService.findKlantOfBedrijfByBedrijfIdAndStatusCode(bedrijfId, klantCodes);

					if(klant != null)
						gebruikers = gebruikerService.findActiveGebruikersOfKlantGebruiker(klant.getGebruikerId(), klant.getBedrijfBedrijfId(), pageRequest);
				}


				if(gebruikers != null) {
					long count = gebruikers.getTotalElements();
					long countRecordsInPage = gebruikers.getSize();

					contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

					users = new UserAccount[(int) count];
					gebruikerlist = gebruikers.getContent();
					for(int i = 0; i < count; i++) {
						GebruikerTransfer gebruiker = gebruikerlist.get(i);
						
						// TODO: klantTelefoonNummer is gebruiker.TelefoonNummer, must be klant phone number? Is this ok?
						users[i] = new UserAccount(gebruiker.getBedrijfId(), gebruiker.getBedrijfNaam(), gebruiker.getGebruikersId(), gebruiker.getGebruikersNaam(), gebruiker.getWachtwoord(), gebruiker.getVoornaam(), gebruiker.getNaam(), gebruiker.getFunctie(), gebruiker.getAfdeling(), gebruiker.getEmailAdres(), gebruiker.getTelefoonNummer(), gebruiker.getBedrijfTelefoonNummer(), gebruiker.isKlant(), gebruiker.isProspect(), gebruiker.isAdresOk(), gebruiker.getRoles(), gebruiker.isActionsPresent(), gebruiker.getShowHelp(), gebruiker.getGeslacht(), false, gebruiker.isBedrijfManager());
						Boolean auth = (Boolean) gebruiker.getRoles().get(ERol.APITOEGESTAAN.getCode());
						if (auth != null && auth) {
							// provide Basic auth string to show user how to connect to API
							users[i].setClientId(apiClientId);
							users[i].setClientSecret(apiSecret);	
						}												
					}
				} else {
					LOGGER.error("Error in fetching gebruikersOfKlantGebruiker");
					ErrorResource error = new ErrorResource(ErrorResource.CANNOT_FETCH_GEBRUIKERS);

					// return Error
					return Response.ok(error).build();

				}

			} catch(ServiceException e) {
				LOGGER.error(e.getMessage());

				ErrorResource error = new ErrorResource(ErrorResource.CANNOT_FETCH_GEBRUIKERS);

				// return Error
				return Response.ok(error).build();
			}
		} else {
			ErrorResource error = new ErrorResource(ErrorResource.CANNOT_FETCH_GEBRUIKERS);

			// return Error
			return Response.ok(error).build();
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
		}

		return Response.ok(users).header("Content-Range", contentRange).build();
	}
	
	/**
	 * Performs VIES check.
	 *
	 * @return Boolean true is VIES is ok, false otherwise.
	 */
	@GET
	@Path("viesCheck")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response viesCheck(@QueryParam("btwNummer") String vatNumber) {
		ViesCheckResult result = new ViesCheckResult();
		LOGGER.info("viesCheck GET bedrijfId: " + vatNumber);

		try {
			boolean viesresult = bedrijfService.isViesCheckOk(vatNumber);

			result.result = new Boolean(viesresult);

		} catch(ServiceException e) {
			ErrorResource error = new ErrorResource(ErrorResource.CANNOT_PERFORM_VIESCHECK);

			// return Error
			return Response.ok(error).build();
		}

		return Response.ok(result).build();
	}	
}