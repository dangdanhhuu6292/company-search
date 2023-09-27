package nl.devoorkant.sbdr.ws;

import nl.devoorkant.creditsafe.converter.ECreditSafeStatusType;
import nl.devoorkant.sbdr.business.service.*;
import nl.devoorkant.sbdr.business.transfer.BedrijfEntityTransfer;
import nl.devoorkant.sbdr.business.transfer.BedrijfTransferNs;
import nl.devoorkant.sbdr.business.transfer.TokenTransfer;
import nl.devoorkant.sbdr.business.util.EBedrijfType;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.util.EReferentieInternType;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.ws.transfer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Component
@Path("/NewAccountService/newaccount")
public class NewAccountResource {
	private static Logger LOGGER = LoggerFactory.getLogger(NewAccountResource.class);

	@Autowired
	private WebTokenService webTokenService;

	@Autowired
	private CompanyInfoService companyInfoService;

	@Autowired
	private CompanyAccountService companyAccountService;

	@Autowired
	private BedrijfService bedrijfService;

	@Autowired
	private UserResource userResource;

	@Autowired
	private GebruikerService gebruikerService;

	@Autowired
	private KortingsCodeService kortingsCodeService;
	
	@Value("${recaptchaSiteKey}")
	private String recaptchaSiteKey;	

	
	/**
	 * Retrieves the companies with an alert.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("getApiKey")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApiKey(@HeaderParam("ipAddress") String ipAddress) {
		ErrorResource error = null;
		String result = null;

		try {
			result = UUID.randomUUID().toString();
			if (!webTokenService.addApiKey(result, ipAddress, null))
				result = null;
		}  catch (ThirdPartyServiceException e) {
			result = null;
			error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
		}

		if(error != null) return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).build();
		else
			return Response.ok(new TokenTransfer(result, null, null, 0, null)).build();
	}
	
	/**
	 * Retrieves the companies with an alert.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("getApiKey2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApiKey2(@HeaderParam("ipAddress") String ipAddress) {
		ErrorResource error = null;
		String result = null;

		try {
			result = UUID.randomUUID().toString();
			// override default apikey expiration with 30 minutes
			long expires = System.currentTimeMillis() + 1000L * 60 * 30;
			
			if (!webTokenService.addApiKey(result, ipAddress, expires))
				result = null;
		}  catch (ThirdPartyServiceException e) {
			result = null;
			error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
		}

		if(error != null) return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).build();
		else
			return Response.ok(new TokenTransfer(result, null, null, 0, null)).build();
	}	
	
	
	/**
	 * Retrieves the companies with an alert.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("searchCompany")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchCompany(@QueryParam("searchValue") String searchValue, @QueryParam("city") String city, @QueryParam("requestPerPage")boolean requestPerPage, @HeaderParam("Range") String range) {
		LOGGER.info("CompanySearch GET searchValue: " + searchValue + " city: " + city + " range: " + range);
		ErrorResource error = null;

		// Sort order check with PFO?
		List<Sort.Order> orders = new LinkedList<Sort.Order>();

		PageRequest pageRequest = null;
		String contentRange = null;

		List<CompanyInfo> companies = null;

		try {
			companies = companyInfoService.retrieveFromCompanyInfo(searchValue, city, 100);
		}  catch (ThirdPartyServiceException e) {
			error = new ErrorResource(ErrorResource.ERROR_COMPANY_SERVICE);
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
		}

		List<BedrijfTransferNs> companiesTransfer = new ArrayList<BedrijfTransferNs>();

		if(companies != null) {
			// total items
			long count = companies.size();

			if(range != null) {
				String[] rangeParts = range.replace("items=", "").split("-");
				int from = requestPerPage ? (new Integer(rangeParts[0]) - 1) : 0;
				int to = requestPerPage? (new Integer(rangeParts[1]) - 1) : companies.size();
				int size = to - from;
				int page = size!=0? (to / size - 1):0;

				contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

				if(orders.size() > 0) pageRequest = new PageRequest(page, size + 1, new Sort(orders));
				else pageRequest = new PageRequest(page, size + 1);

				for(int i = from; i < Math.min(companies.size(), (to + 1)); i++) {
					CompanyInfo company = companies.get(i);
					LOGGER.debug("NOT SURE New ACC Search company: type " + company.getType() + " naam: " + company.getBedrijfsNaam() + " kvknr: " + company.getKvKnummer());
					boolean isHoofd = false;
					// only for company info purpose
					//if (companies.get(i).getSub() == null || companies.get(i).getSub().equals("0000"))
					//	isHoofd = true;
					if(company.getCreditSafeHeadQuarters() != null && company.getCreditSafeHeadQuarters().equals(EBedrijfType.HOOFD.getCode()))
						isHoofd = true;
					boolean isActief = true;
					if(company.getCreditSafeStatus() != null && !company.getCreditSafeStatus().equals(ECreditSafeStatusType.ACTIEF.getOmschrijving()))
						isActief = false;
					companiesTransfer.add(new BedrijfTransferNs(null, isActief, isHoofd, company.getBedrijfsNaam(), company.getKvKnummer() + "", company.getSub(), null, company.getStraat(), company.getHuisNummer(), null, company.getPostcode(), company.getPlaats(), null, null));
				}
			}

			if(pageRequest != null) {
				System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
			}

			try {
				companiesTransfer = bedrijfService.addSbdrNummerProperty(companiesTransfer);
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
			}
		}

		//Company[] companiesTransferArray = null;

		//if (companiesTransfer != null)
		//{
		//companiesTransferArray = new Company[companiesTransfer.size()];
		//companiesTransferArray = companiesTransfer.toArray(new Company[0]);
		//}

		if(error != null) return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).build();
		else
			return Response.ok(companiesTransfer.toArray(new BedrijfTransferNs[companiesTransfer.size()])).header("Content-Range", contentRange).build();
	}

	/**
	 * Retrieves a specific company.
	 *
	 * @return A transfer containing the company data.
	 */
	@Path("getCompanyNewAccountData")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCompanyNewAccountData(@QueryParam("kvknumber") String kvkNumber, @QueryParam("hoofdvestiging") Boolean hoofdVestiging, @Context HttpHeaders headers) {
		ErrorResource error = null;
		boolean activateKlantBedrijf = true;

		LOGGER.info("getCompanyAccountData kvkNumber: " + kvkNumber);

		CompanyAccount account = new CompanyAccount();

		try {
			if(kvkNumber != null) {
				String subDossier = null;
				String kvknummer = kvkNumber;
				if(kvkNumber.length() == 12) {
					subDossier = kvkNumber.substring(8);
					kvknummer = kvkNumber.substring(0, 8);

					// Subdossier may be consist of '0' keep zeros. 
					//if(subDossier.replace('0', ' ').trim().equals("")) subDossier = null;
				}
				if(companyAccountService.hasBedrijfAccount(kvknummer, subDossier, false)) {
					error = new ErrorResource(ErrorResource.COMPANY_ACCOUNT_EXISTS);
					// Because user may not know which companies already exists, no error is returned.
					// An email is sent to SBDR to inform fraud requests.
					error = null;
					// Account will not be activated
					activateKlantBedrijf = false;
				}

				CIKvKDossier kvkDossier = null;

				kvkDossier = companyInfoService.getCIKvKDossierFromCompanyInfo(kvknummer, subDossier, hoofdVestiging);

				kvkDossier = companyInfoService.saveCIKvkDossier(kvkDossier);	
				
				Bedrijf bedrijf = companyAccountService.createBedrijfFromCIKvkDossier(kvkDossier, false, activateKlantBedrijf);

				String referentieIntern = null;
				if(bedrijf.getSbdrNummer() != null)
					referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

				account.setBedrijf(bedrijf != null ? new BedrijfEntityTransfer(bedrijf) : null);
				account.setReferentieIntern(referentieIntern);

				Klant klant = new Klant();
				klant.setGeslacht("M");
				klant.setActivatieReminderSent(false);
				account.setKlant(klant != null ? new KlantEntityTransfer(klant) : null);

				account.setWachtwoord(new WachtwoordEntityTransfer());
			}


		} catch (ThirdPartyServiceException e) {
			error = new ErrorResource(ErrorResource.ERROR_COMPANY_SERVICE);
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
		}

		if(error == null) return Response.ok(account).build();
		else return Response.ok(error).build();
	}

	@Path("createMobileAccount")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMobileAccount(final CompanyAccount account) {
		ErrorResource error = null;

		LOGGER.info("createMobileAccount");

		if(account != null) {
			try {
				Klant klant = account.getKlant().getDataObject();
				klant.setWachtwoord(account.getWachtwoord().getDataObject());
				Bedrijf bedrijf = account.getBedrijf().getDataObject();
				ErrorService errorservice = companyAccountService.createAccount(klant, bedrijf, account.getAdresOk(), null);
				if(errorservice != null) {
					if(errorservice.getErrorCode().equals(ErrorService.COMPANY_ACCOUNT_EXISTS)) {
						// Because user may not know which companies already exists, no error is returned.
						// An email is sent to SBDR to inform fraud requests.
						error = null;
					} else error = new ErrorResource(errorservice.getErrorCode());

				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_ACCOUNT);
			} catch(Exception e) {
				LOGGER.debug("Wrong exception: " + e.getMessage());
			}
		} else error = new ErrorResource(ErrorResource.INCOMPLETE_ACCOUNT_DATA);

		return Response.ok(error).build();
	}

	@Path("createAccount")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response createAccount(final CompanyAccount account) {
		ErrorResource error = null;
		
		LOGGER.info("createAccount");

		boolean verifyrecaptcharesult = false;
		VerifyRecaptcha verifyRecaptcha = account.getVerifyRecaptcha();

		Response verifyResult = verifyRecaptcha(verifyRecaptcha);

		if(verifyResult != null && verifyResult.getEntity() != null) {
			if(verifyResult.getEntity() instanceof ErrorResource) {
				verifyrecaptcharesult = false;
				error = (ErrorResource) verifyResult.getEntity();
			} else if(verifyResult.getEntity() instanceof RecaptchaResult) {
				RecaptchaResult answer = (RecaptchaResult) verifyResult.getEntity();

				if(answer.result != null && answer.result) verifyrecaptcharesult = true;
				else error = new ErrorResource(ErrorResource.CANNOT_PROCESS_RECAPTCHA);
			} else error = new ErrorResource(ErrorResource.CANNOT_PROCESS_RECAPTCHA);
		}

		if(account != null && account.getKlant() != null && verifyrecaptcharesult) {
			Klant klant = account.getKlant().getDataObject();
			
			klant.setWachtwoord(account.getWachtwoord().getDataObject());

			if(account.getKortingsCode() != null && account.getKortingsCode().getCode() != null && !account.getKortingsCode().getCode().isEmpty()) {
				if(kortingsCodeService.checkIfCodeIsValid(account.getKortingsCode().getCode(), new Date()))
					klant.setKortingsCode(account.getKortingsCode());
			}

			try {				
				Bedrijf bedrijf = account.getBedrijf().getDataObject();				
				ErrorService errorservice = companyAccountService.createAccount(klant, bedrijf, account.getAdresOk(), account.getOpmerkingenAdres());
				if(errorservice != null) {
					error = new ErrorResource(errorservice.getErrorCode());
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_ACCOUNT);
			} catch(Exception e) {
				LOGGER.debug("Wrong exception: " + e.getMessage());
			}
		} else error = new ErrorResource(ErrorResource.INCOMPLETE_ACCOUNT_DATA);

		return Response.ok(error).build();
	}

	
	/**
	 * Retrieves the recaptchaSiteKey settings.
	 *
	 * @return A transfer containing the recaptchaSiteKey values.
	 */
	@GET
	@Path("recaptchasitekey")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recaptchaSiteKey() {
		
		//if (muleContext != null)
		//	LOGGER.debug("recaptchaparam method in DashboardResource: " + muleContext..getRemoteAddr());
		RecaptchaSiteKeyTransfer recaptchaSiteKeyTransfer = new RecaptchaSiteKeyTransfer();

		recaptchaSiteKeyTransfer.recaptchaSiteKey = recaptchaSiteKey;
		
		return Response.ok(recaptchaSiteKeyTransfer).build();
	}
	
	/**
	 * Performs recaptcha check.
	 *
	 * @return Boolean true is VIES is ok, false otherwise.
	 */
	
	private Response verifyRecaptcha(final VerifyRecaptcha verifyRecaptcha) {
		RecaptchaResult result = new RecaptchaResult();
		LOGGER.info("verify recaptcha");

		try {
			boolean recaptcharesult = companyAccountService.isRecaptchaOk(verifyRecaptcha.ipaddress, verifyRecaptcha.response, verifyRecaptcha.challenge);

			result.result = new Boolean(recaptcharesult);

		} catch(ServiceException e) {
			ErrorResource error = new ErrorResource(ErrorResource.CANNOT_PERFORM_RECAPTCHAVERIFY);

			// return Error
			return Response.ok(error).build();
		}

		return Response.ok(result).build();
	}

}
