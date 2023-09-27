package nl.devoorkant.sbdr.ws;

import nl.devoorkant.creditsafe.converter.ECreditSafeStatusType;
import nl.devoorkant.sbdr.business.service.*;
import nl.devoorkant.sbdr.business.transfer.*;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.business.util.EBedrijfType;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.business.util.SerialNumber;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.service.DocumentDataService;
import nl.devoorkant.sbdr.data.util.EAlertType;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import nl.devoorkant.sbdr.data.util.EMeldingStatus;
import nl.devoorkant.sbdr.data.util.ENotitieType;
import nl.devoorkant.sbdr.data.util.EProduct;
import nl.devoorkant.sbdr.data.util.EReferentieInternType;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.ws.transfer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.*;

@Component
@Path("/CompanyService/company")
public class CompanyResource {

	@Autowired
	private AlertService alertService;
	@Autowired
	private BedrijfService bedrijfService;
	@Autowired
	private CompanyInfoService companyInfoService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private DocumentDataService documentDataService;
	@Autowired
	private GebruikerService gebruikerService;
	@Autowired
	private InternalProcessService internalProcessService;
	@Autowired
	private MonitorService monitorService;
	@Autowired
	private ProductService productService;
	@Autowired
	private UserResource userResource;
	private static Logger LOGGER = LoggerFactory.getLogger(CompanyResource.class);

	//@Autowired
	//private CompaniesService companiesService;

	@Autowired
	CompanyAccountService companyAccountService;

	@Path("achterstandCheck")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response achterstandCheck(@QueryParam("bedrijfId") String obfBedrijfId) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		BedrijfReportTransfer report = null;

		if(user != null) {
			if(!gebruikerService.hasRightToDo(user.getUserId(), bedrijfId, EBevoegdheid.RAPPORT_INZIEN)) {
				error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
			} else {
				//List<Melding> actieveMeldingen = null;

				try {
					Gebruiker gebruiker = gebruikerService.findByGebruikerId(user.getUserId());
					Integer eigenBedrijfId = null;
					if (gebruiker != null)
						eigenBedrijfId = gebruiker.getBedrijfBedrijfId();
					
					if (eigenBedrijfId == null || eigenBedrijfId.equals(user.getBedrijfId()))					
						productService.purchaseAchterstandCheck(user.getBedrijfId(), null, new Date());
					else
						productService.purchaseAchterstandCheck(eigenBedrijfId, user.getBedrijfId(), new Date());

					Date date = new Date();
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					int year = cal.get(Calendar.YEAR) % 100; // last 2 numbers of year

					String referentieNummer = year + SerialNumber.generateRandomSerialNumber8_32();
					while(documentService.findByReferentieIntern(referentieNummer) != null) {
						referentieNummer = year + SerialNumber.generateRandomSerialNumber8_32();
					}

					BedrijfReportTransfer data = bedrijfService.getReportData(user.getBedrijfId(), bedrijfId, EDocumentType.MOBILEAPPCHECK.getPrefix() + referentieNummer);

					// Create document record
					try {
						Document document = new Document();

						document.setGebruikerGebruikerId(user.getUserId());
						document.setBedrijfByBedrijfIdBedrijfId(user.getBedrijfId());
						document.setBedrijfByRapportBedrijfIdBedrijfId(bedrijfId);
						document.setDatumAangemaakt(new Date());
						document.setDocumentTypeCode(EDocumentType.MOBILEAPPCHECK.getCode());
						document.setNaam("Mobile App Check");
						document.setReferentieNummer(referentieNummer);
						document.setActief(true);

						documentDataService.save(document);
						
					} catch(DataServiceException e) {
						throw new ServiceException(e);
					}					
					report = filterDataForMobile(data);
				} catch(ServiceException e) {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANYREPORTDATA);
				}

			}
		} else { error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);}
		if(error != null) {return Response.ok(error).build();} else {return Response.ok(report).build();}
	}

	@Path("companyHasNotifications")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response bedrijfHasMeldingenByGebruikerId(@QueryParam("gebruikerId") String obfGId) {
		Integer gId = ObfuscatorUtils.deofuscateInteger(obfGId);
		ErrorResource error = null;
		GenericBooleanTransfer val = null;
		try {
			val = new GenericBooleanTransfer(bedrijfService.bedrijfHasMeldingenByGebruikerId(gId));
		} catch(Exception e) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTIFICATION);
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(val).build();
	}

	@Path("bedrijfHasMonitor")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response bedrijfHasMonitor(@QueryParam("doorBedrijfId") String obfDoorBedrijfId, @QueryParam("overBedrijfId") String obfOverBedrijfId) {
		Integer doorBedrijfId = ObfuscatorUtils.deofuscateInteger(obfDoorBedrijfId);
		Integer overBedrijfId = ObfuscatorUtils.deofuscateInteger(obfOverBedrijfId);
		ErrorResource error = null;
		GenericBooleanTransfer boolContainer = null;

		if(doorBedrijfId != null && overBedrijfId != null) {
			try {
				boolean hasMonitor = monitorService.bedrijfHasMonitor(doorBedrijfId, overBedrijfId);
				boolContainer = new GenericBooleanTransfer(hasMonitor);
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CHECK_MONITORING);
			}
		} else {
			error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
		}

		if(error != null) {return Response.ok(error).build();} else {return Response.ok(boolContainer).build();}
	}

	@GET
	@Path("checkIfUserCanPay")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkIfUserCanPay(@QueryParam("userId") String obfGId, @QueryParam("bedrijfId") String obfBId) {
		Integer gId = ObfuscatorUtils.deofuscateInteger(obfGId);
		Integer bId = ObfuscatorUtils.deofuscateInteger(obfBId);
		GenericBooleanTransfer boolTransfer = null;
		ErrorResource error = null;
		if(gId != null) {
			try {
				boolTransfer = new GenericBooleanTransfer(companyAccountService.checkIfUserCanPay(gId, bId));
			} catch(ServiceException e) {
				LOGGER.error("Method checkIfUserCanPay: " + e.getMessage());
				error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
			}
		} else {
			error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(boolTransfer).build();
	}

	/**
	 * Retrieves the company data.
	 *
	 * @return A transfer containing the company data.
	 */
	@GET
	@Path("companyData")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companyData(@QueryParam("bedrijfId") String obfBedrijfId) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		BedrijfTransfer bedrijf = null;
		LOGGER.debug("companyData GET bedrijfId: " + bedrijfId);

		try {
			bedrijf = bedrijfService.getBedrijfData(bedrijfId);

		} catch(ServiceException e) {
			ErrorResource error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);

			// return Error
			return Response.ok(error).build();
		}

		return Response.ok(bedrijf).build();
	}
	
	/**
	 * Retrieves the company data + extra fields as postal address data.
	 *
	 * @return A transfer containing the company data.
	 */
	@GET
	@Path("companyDataExtra")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companyDataExtra(@QueryParam("bedrijfId") String obfBedrijfId) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		BedrijfTransferExtra bedrijf = null;
		LOGGER.debug("companyDataExta GET bedrijfId: " + bedrijfId);

		try {
			bedrijf = bedrijfService.getBedrijfDataExtra(bedrijfId);

		} catch(ServiceException e) {
			ErrorResource error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);

			// return Error
			return Response.ok(error).build();
		}

		return Response.ok(bedrijf).build();
	}	

	/**
	 * Retrieves the company data.
	 *
	 * @return A transfer containing the company data.
	 */
	@GET
	@Path("companyNotificationData")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companyNotificationData(@QueryParam("meldingId") String obfMeldingId, @QueryParam("bedrijfId") String obfBedrijfId) {
		Integer meldingId = ObfuscatorUtils.deofuscateInteger(obfMeldingId);
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		MeldingTransfer melding = null;
		LOGGER.info("CompanyNotificationData GET meldingId: " + meldingId + " of bedrijfId: " + bedrijfId);

		try {
			UserTransfer user = userResource.getUser();
			// is user allowed to all notifications?
			if (gebruikerService.hasRightToDo(user.getUserId(), bedrijfId, EBevoegdheid.ADMIN_SBDR_HOOFD))
				melding = bedrijfService.getMeldingData(meldingId, null, bedrijfId, user.getUserId());
			else
				melding = bedrijfService.getMeldingData(meldingId, user.getBedrijfId(), bedrijfId, user.getUserId());
			
			// is user allowed to view notities?
			//if (!gebruikerService.hasRightToDo(user.getUserId(), bedrijfId, EBevoegdheid.ADMIN_SBDR_HOOFD))
			//	melding.setNotities(null);

		} catch(ServiceException e) {
			ErrorResource error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTIFICATION);

			// return Error
			return Response.ok(error).build();
		}

		return Response.ok(melding).build();
	}

	/**
	 * Retrieves the company report data.
	 *
	 * @return A transfer containing the company report data.
	 */
	@GET
	@Path("companyReportData")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companyReportData(@QueryParam("bedrijfAanvragerId") String obfBedrijfAanvragerId, @QueryParam("bedrijfId") String obfBedrijfId) {
		Integer bedrijfAanvragerId = ObfuscatorUtils.deofuscateInteger(obfBedrijfAanvragerId);
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		ErrorResource errorResource = null;
		Integer gebruikerId = null;
		BedrijfReportTransfer reportData = null;
		LOGGER.info("CompanyReportData GET bedrijfId: " + bedrijfId);

		UserTransfer user = userResource.getUser();

		if(user != null) {
			gebruikerId = user.getUserId();

			if(!user.getBedrijfId().equals(bedrijfAanvragerId) || !gebruikerService.hasRightToDo(user.getUserId(), bedrijfAanvragerId, EBevoegdheid.RAPPORT_INZIEN))  // klant, hoofd, gebruiker
			{
				errorResource = new ErrorResource(ErrorResource.CANNOT_FETCH_DOCUMENT);
			} else if(!bedrijfService.hasRightToDo(bedrijfAanvragerId, EBevoegdheid.RAPPORT_INZIEN)) { // only active companies
				errorResource = new ErrorResource(ErrorResource.COMPANY_NOT_ACTIVE);
			}
		}

		if(errorResource == null) {
			try {
				//reportData = bedrijfService.getReportData(bedrijfAanvragerId, bedrijfId);
				reportData = documentService.createReportPdf(gebruikerId, bedrijfId, bedrijfAanvragerId);

				//Zet de H of N(van hoofd/neven vestiging) om naar een omschrijving voor mooiere weergave
				reportData.getKvkDossierTransfer().setHoofdNeven(EBedrijfType.get(reportData.getKvkDossierTransfer().getHoofdNeven()).getOmschrijving());
				//reportData.getBedrijf().setHoofdNeven(EBedrijfType.get(reportData.getBedrijf().getHoofdNeven()).getOmschrijving());

			} catch(ServiceException e) {
				ErrorResource error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANYREPORTDATA);
			}
		}

		if(errorResource != null) return Response.ok(errorResource).build();
		else return Response.ok(reportData).build();
	}

	@Path("createCustomMelding")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response createCustomMelding(final CustomMeldingTransfer customMelding) {
		ErrorResource error = null;
		NotificationBatchResult result = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(customMelding.getBedrijfIdGerapporteerd() != null && // customMelding.getBedrijfIdGerapporteerd().equals(user.getBedrijfId()) &&
					!gebruikerService.hasRightToDo(user.getUserId(), customMelding.getBedrijfIdGerapporteerd(), EBevoegdheid.MELDING_INVOEREN) && // klant, hoofd, gebruiker
					!user.getBedrijfId().equals(customMelding.getBedrijfIdGerapporteerd())) // check on bedrijfId who performs the notification
			{
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
			}

			try {
				if(!gebruikerService.validateUserData(user.getUserName(), customMelding.getWachtwoord())) {
					error = new ErrorResource(ErrorResource.WRONG_PASSWORD);
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
			}
		}

		try {
			ErrorService errorservice = null;

			if(error == null) {
				errorservice = bedrijfService.createCustomMelding(user.getUserId(), customMelding);

				if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
			}

		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(null).build();
	}

	@Path("createInitialNotificationLetter")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createInitialNotificationLetter(@QueryParam("meldingId") String obfMeldingId) {
		Integer meldingId = ObfuscatorUtils.deofuscateInteger(obfMeldingId);
		UserTransfer user = userResource.getUser();
		ErrorResource error = null;

		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_CREATE_LETTER);} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER)) {
				error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
			} else {
				if(meldingId != null) {
					try {
						bedrijfService.createInitialMeldingenLetter(meldingId, user.getUserId());
					} catch(Exception e) {
						error = new ErrorResource(ErrorResource.CANNOT_CREATE_LETTER);
					}
				} else {
					error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
				}
			}
		}

		if(error != null) {return Response.ok(error).build();} else {return Response.ok().build();}
	}

	@Path("createNewNotificationLetter")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNewNotificationLetter(@QueryParam("meldingId") String obfMeldingId, @QueryParam("allNotificationsInLetter") boolean processAllNotifications) {
		Integer meldingId = ObfuscatorUtils.deofuscateInteger(obfMeldingId);
		UserTransfer user = userResource.getUser();
		ErrorResource error = null;

		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_CREATE_LETTER);} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER)) {
				error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
			} else {
				if(meldingId != null) {
					try {
						bedrijfService.createNewMeldingenLetter(meldingId, user.getUserId(), processAllNotifications);
					} catch(Exception e) {
						error = new ErrorResource(ErrorResource.CANNOT_CREATE_LETTER);
					}
				} else {
					error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
				}
			}
		}

		if(error != null) {return Response.ok(error).build();} else {return Response.ok().build();}
	}

	/*
	 * Removes new notification active/deleted alert or ticket alert by removing the specific alert record
	 */
	@Path("deleteAlert")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAlert(String obfAlertId) {
		Integer alertId = ObfuscatorUtils.deofuscateInteger(obfAlertId);
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		LOGGER.info("account delete(id)");

		try {
			Alert alert = alertService.findAlertById(alertId);

			// alert may be removed by SBDR or alert owner company or specific alert-for-user
			if(alert != null && (gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.KLANT_BEHEER) || (alert.getGebruikerGebruikerId() == null && alert.getBedrijfByBedrijfIdBedrijfId().equals(user.getBedrijfId())) || (alert.getGebruikerGebruikerId() != null && alert.getGebruikerGebruikerId().equals(user.getUserId())))) {
				ErrorService errorservice = alertService.deleteAlert(alert);
				if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
			} else {
				error = new ErrorResource(ErrorResource.CANNOT_REMOVE_ALERT);
			}

		} catch(ServiceException e) {
			LOGGER.error("Error deleting Alert with id: " + alertId + " " + e.getMessage());
			error = new ErrorResource(ErrorResource.CANNOT_REMOVE_ALERT);
		} catch(Exception e) {
			error = new ErrorResource(ErrorResource.CANNOT_REMOVE_ALERT);
		}


		return Response.ok(error).build();
	}

	@Path("donate")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response donate(@QueryParam("donationAmount") BigDecimal donationAmount) {
		ErrorResource error = null;
		GenericBooleanTransfer result = new GenericBooleanTransfer();

		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(!gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.HOOFD_OF_KLANT)) {
				error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
			} else {
				try {
					Gebruiker gebruiker = gebruikerService.findByGebruikerId(user.getUserId());
					Integer eigenBedrijfId = null;
					if (gebruiker != null)
						eigenBedrijfId = gebruiker.getBedrijfBedrijfId();
					
					if (eigenBedrijfId == null || eigenBedrijfId.equals(user.getBedrijfId()))
						productService.makeDonation(user.getBedrijfId(), null, new Date(), donationAmount);
					else
						productService.makeDonation(eigenBedrijfId, user.getBedrijfId(), new Date(), donationAmount);
					result.setVal(true);
				} catch(Exception e) {
					error = new ErrorResource(ErrorResource.CANNOT_MAKE_DONATION);
				}
			}
		} else error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);

		if(error != null) return Response.ok(error).build();
		else return Response.ok(result).build();
	}

	@GET
	@Path("getTariefOfProduct")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentTarief(@QueryParam("ProductCode") String productCode) {
		ErrorResource error = null;
		TariefTransfer tT = null;

		if(productCode != null) {
			try {
				Tarief t = productService.getCurrentTarief(EProduct.get(productCode));
				tT = new TariefTransfer(t.getBedrag());
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_TARIEF);
			}
		} else error = new ErrorResource(ErrorResource.ERROR_PARAMETER_EMPTY);

		if(error != null) return Response.ok(error).build();
		else return Response.ok(tT).build();
	}

	@Path("getNotificationsOfCompany")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNotificationsOfCompany(@QueryParam("gebruikerId") String obfGebruikerId) {
		Integer gebruikerId = ObfuscatorUtils.deofuscateInteger(obfGebruikerId);
		ErrorResource error = null;
		List<MeldingTransfer> response = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			try {
				response = bedrijfService.findMeldingenAboutBedrijfByGebruikerId(gebruikerId);
			} catch(Exception e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTIFICATION);
			}
		} else
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTIFICATION);

		if(error == null) {
			if(response.size() > 0) {
				MeldingTransfer[] meldingTransferArray = response.toArray(new MeldingTransfer[0]);
				return Response.ok(meldingTransferArray).build();
			} else
				return Response.ok(null).build();
		}
		else return Response.ok(error).build();
	}
	
	@Path("getContactMomentsOfNotification")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContactMomentsOfNotification(@QueryParam("meldingId") String obfMeldingId, @QueryParam("gebruikerId") String obfGebruikerId) {
		Integer meldingId = ObfuscatorUtils.deofuscateInteger(obfMeldingId);
		Integer gebruikerId = ObfuscatorUtils.deofuscateInteger(obfGebruikerId);
		ErrorResource error = null;
		List<ContactMomentTransfer> response = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			try {
				response = bedrijfService.findContactMomentsOfNotification(meldingId, user.getUserId());
			} catch(Exception e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTIFICATION);
			}
		} else
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTIFICATION);

		if(error == null) {
			if(response.size() > 0) {
				ContactMomentTransfer[] contactMomentsTransferArray = response.toArray(new ContactMomentTransfer[0]);
				return Response.ok(contactMomentsTransferArray).build();
			} else
				return Response.ok(null).build();
		}
		else return Response.ok(error).build();
	}
	
	@Path("getAdminNoteOfNotification")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAdminNoteOfNotification(@QueryParam("meldingId") String obfMeldingId) {
		Integer meldingId = ObfuscatorUtils.deofuscateInteger(obfMeldingId);
		ErrorResource error = null;
		NotitieTransfer response = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			try {
				if(gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.SUPPORT_ADMIN))  // hoofd, gebruiker
						response = bedrijfService.getNotitieAdminData(meldingId);
				else
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTITIE);
			} catch(Exception e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTITIE);
			}
		} else
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTITIE);

		if(error == null) {
			if(response != null) {
				return Response.ok(response).build();
			} else
				return Response.ok(null).build();
		}
		else return Response.ok(error).build();
	}	
		

	/**
	 * Checks if company exists.
	 * If not, then it creates a Bedrijf record then it returns BedrijfTransfer
	 * Otherwise it returns an BedrijfTransfer
	 *
	 * @return A transfer containing the company data.
	 */
	@Path("getOrCreateCompanyData")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrCreateCompanyData(@QueryParam("kvknumber") String kvkNumber, @QueryParam("hoofd") Boolean isHoofd) {
		ErrorResource error = null;
		BedrijfTransfer response = null;
		Integer bedrijfId = null;
		boolean createNewIfNotExists = false;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			LOGGER.info("getOrCreateCompanyData kvkNumber: " + kvkNumber);
	
			try {
				if(kvkNumber != null) {
					String subDossier = null;
					String kvknummer = kvkNumber;
					if(kvkNumber.length() == 12) {
						subDossier = kvkNumber.substring(8);
						kvknummer = kvkNumber.substring(0, 8);
	
						// MBR subdossier issue 16-2-2016 if(subDossier.replace('0', ' ').trim().equals("")) subDossier = null;
					}
					Bedrijf bedrijf = companyAccountService.findBedrijf(kvknummer, subDossier);
					
					boolean isHoofdVestiging = false;
					if(bedrijf != null && bedrijf.getHoofdNeven() != null && bedrijf.getHoofdNeven().equals(EBedrijfType.HOOFD.getCode()))
						isHoofdVestiging = true;
					if(isHoofd != null) isHoofdVestiging = isHoofd;
	
					if(bedrijf != null) bedrijfId = bedrijf.getBedrijfId();
					else createNewIfNotExists = true;
	
					// 				//getOrCreate hoofdvestiging if applicable, needed for reporting purpose
					//				try {
					//					if(subDossier != null) {
					//						boolean createNewIfNotExistsHoofd = false;
					//						Bedrijf bedrijfHoofd = companyAccountService.findBedrijf(kvknummer, null);
					//
					//						if(bedrijfHoofd == null) createNewIfNotExistsHoofd = true;
					//
					//						getOrCreateBedrijf(kvknummer, createNewIfNotExistsHoofd);
					//					}
					//				} catch(ServiceException e) {
					//					LOGGER.warn("Hoofdvestiging could not be fetched/created/saved: " + kvkNumber + " " + e.getMessage());
					//				}
	
					bedrijf = getOrCreateBedrijf(kvknummer, subDossier, isHoofdVestiging, true); // MBR always create or update bedrijfdata if changed... createNewIfNotExists);
	
					if(createNewIfNotExists) bedrijfId = bedrijf.getBedrijfId();
	
					String huisnr = null;
					if(bedrijf.getHuisNr() != null) huisnr = bedrijf.getHuisNr().toString();
	
					String referentieIntern = null;
					if(bedrijf.getSbdrNummer() != null)
						referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();
	
					response = new BedrijfTransfer(bedrijfId, bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieIntern, bedrijf.getStraat(), huisnr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), bedrijf.getTelefoonnummer(), null);
				}
	
			} catch(ThirdPartyServiceException e) {
				LOGGER.error(e.getMessage());
				error = new ErrorResource(ErrorResource.ERROR_COMPANY_SERVICE);
			} catch(Exception e) {
				LOGGER.error(e.getMessage());
				error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
			}
		} else
			error = new ErrorResource(ErrorResource.GENERAL_FAILURE);
		
		if(error == null) return Response.ok(response).build();
		else return Response.ok(error).build();
	}

	@Path("holdNotificationCompany")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response holdNotificationCompany(final CompanyNotified companyNotified) {
		ErrorResource error = null;
		MeldingTransfer newMelding = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			boolean sbdrUser = gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER);

			if(!gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER) &&  // klant, hoofd, gebruiker
					!sbdrUser) //sbdr admin
			{
				error = new ErrorResource(ErrorResource.CANNOT_SAVE_MELDING);

				// return Error
				return Response.ok(error).build();
			}
		}

		try {
			if(companyNotified != null && companyNotified.meldingId != null) {
				bedrijfService.blokkeerMelding(companyNotified.meldingId, user.getUserId(), true);
			} else error = new ErrorResource(ErrorResource.CANNOT_SAVE_MELDING);

		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_SAVE_MELDING);
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(newMelding).build();
	}

	@Path("ignoreException")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response ignoreException(@QueryParam("customMeldingId") String obfCustomMeldingId, @QueryParam("bedrijfId") String obfBedrijfId) {
		Integer customMeldingId = ObfuscatorUtils.deofuscateInteger(obfCustomMeldingId);
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		if(bedrijfId != null) {
			if(gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.KLANT_BEHEER)) { //sbdr admin
				try {
					bedrijfService.ignoreException(customMeldingId, bedrijfId);

				} catch(ServiceException e) {
					error = new ErrorResource(ErrorResource.COMPANYEXCEPTION_NOT_RESOLVED);
				}
			} else error = new ErrorResource(ErrorResource.COMPANYEXCEPTION_NOT_RESOLVED);
		} else error = new ErrorResource(ErrorResource.COMPANYEXCEPTION_NOT_RESOLVED);

		return Response.ok(error).build();
	}

	@Path("monitoringCompany")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response monitoringCompany(final MonitoringTransfer companyMonitoring) {
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(!user.getBedrijfId().equals(companyMonitoring.getBedrijfIdGerapporteerd()) || !gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MONITORING_TOEVOEGEN))  // klant, hoofd, gebruiker
			{
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_MONITORING);
			} else if(!bedrijfService.hasRightToDo(companyMonitoring.getBedrijfIdGerapporteerd(), EBevoegdheid.MONITORING_TOEVOEGEN)) { // only active companies
				error = new ErrorResource(ErrorResource.COMPANY_NOT_ACTIVE);
			}

			if(error == null) {
				try {
					if(companyMonitoring != null) {
						if(!monitorService.bedrijfHasMonitor(companyMonitoring.getBedrijfIdGerapporteerd(), companyMonitoring.getBedrijfId())) {
							Monitoring monitoring = new Monitoring();

							monitoring.setBedrijfByMonitoringDoorBedrijfIdBedrijfId(companyMonitoring.getBedrijfIdGerapporteerd());
							monitoring.setBedrijfByMonitoringVanBedrijfIdBedrijfId(companyMonitoring.getBedrijfId());

							monitoring.setGebruikerByMonitoringDoorGebruikerIdGebruikerId(user.getUserId());

							ErrorService errorService = bedrijfService.createMonitoring(monitoring);

							if(errorService != null) {
								error = new ErrorResource(errorService.getErrorCode());
							}
						} else {
							error = new ErrorResource(ErrorResource.CANNOT_CREATE_MONITORING);
						}
					}
				} catch(ServiceException e) {
					error = new ErrorResource(ErrorResource.CANNOT_CREATE_MONITORING);
				}
			}
		} else error = new ErrorResource(ErrorResource.CANNOT_CREATE_MONITORING);

		return Response.ok(error).build();
	}

	/**
	 * Retrieves the monitoringDetail data.
	 *
	 * @return A transfer containing the monitoringDetail data.
	 */
	@GET
	@Path("monitoringDetailData")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response monitoringDetailData(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("monitoringId") String obfMonitoringId) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		Integer monitoringId = ObfuscatorUtils.deofuscateInteger(obfMonitoringId);
		MonitoringDetailsTransfer monitoringDetail = null;
		ErrorResource error = null;
		
		UserTransfer user = userResource.getUser();

		if(user != null) {
			LOGGER.info("monitoringDetailData GET bedrijfId: " + bedrijfId);
	
			try {
				monitoringDetail = bedrijfService.monitoringDetailTransfer(bedrijfId, monitoringId);
	
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_MONITORINGDETAILDATA);
			}
		} else
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_MONITORINGDETAILDATA);
		
		if (error != null)
			return Response.ok(error).build();
		else
			return Response.ok(monitoringDetail).build();
	}
	
	@Path("addContactMomentNotification")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response addContactMomentNotification(final ContactMomentTransfer contactMomentNotification) {
		ErrorResource error = null;
		ContactMomentTransfer newContactMomentNotification = null;
		
		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(!(contactMomentNotification != null && contactMomentNotification.getMeldingId() != null && gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER))) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_CONTACTMOMENT);

				// return Error
				return Response.ok(error).build();
			}

			// Company may not notify own company
			if(user.getBedrijfId().equals(contactMomentNotification.getBedrijfIdGerapporteerd())) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_CONTACTMOMENT);

				// return Error
				return Response.ok(error).build();
			}

		}
		
		try {
			if(contactMomentNotification != null) {
				ErrorService meldingOk = null;

				Notitie notitie = new Notitie();
				
				notitie.setDatum(new Date());
				notitie.setGebruikerGebruikerId(user.getUserId());
				notitie.setNotitie(contactMomentNotification.getNotitie());
				notitie.setNotitieType(ENotitieType.CONTACT_MOMENT.getCode());		
				contactMomentNotification.setGebruikerId(user.getUserId());

				ContactMoment createdContactMoment = bedrijfService.saveContactMomentNotification(contactMomentNotification, notitie);
				
				if(createdContactMoment == null) 
					error = new ErrorResource(ErrorResource.CANNOT_CREATE_CONTACTMOMENT);
				else
					newContactMomentNotification = bedrijfService.getContactMomentNotificationData(createdContactMoment.getContactMomentId());

			}
		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_CONTACTMOMENT);
		}

		if(error != null) 
			return Response.ok(error).build();
		else 			
			return Response.ok(newContactMomentNotification).build();	
	}

	@Path("addAdminNotitieNotification")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response addAdminNotitieNotification(final NotitieTransfer notitieNotification) {
		ErrorResource error = null;
		NotitieTransfer newNotitieNotification = null;
		
		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(!(gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER))) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTITIE);

				// return Error
				return Response.ok(error).build();
			}

		}
		
		try {
			if(notitieNotification != null) {

				Notitie notitie = new Notitie();
				
				notitie.setDatum(new Date());
				notitie.setGebruikerGebruikerId(user.getUserId());
				//notitie.setMeldingMeldingId(notitieNotification.getMeldingId());
				notitie.setNotitie(notitieNotification.getNotitie());
				notitie.setNotitieType(ENotitieType.MELDING_ADMIN.getCode());
				
				Notitie createdNotitie = bedrijfService.saveNotitie(notitie, notitieNotification.getMeldingId());
									
				if(createdNotitie == null) 
					error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTITIE);
				else
					newNotitieNotification = bedrijfService.getNotitieAdminData(notitieNotification.getMeldingId());
			}
		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTITIE);
		}

		if(error != null) 
			return Response.ok(error).build();
		else 			
			return Response.ok(newNotitieNotification).build();	
	}
	
	@Path("removeContactMomentNotification")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response removeContactMomentNotification(final RemoveContactMomentNotification contactMomentNotification) {
		ErrorResource error = null;
		
		UserTransfer user = userResource.getUser();

		if(user != null) {

			try {
				if(contactMomentNotification != null && contactMomentNotification.contactMomentId != null && gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER)) {
					ErrorService errorService = bedrijfService.removeContactMomentNotification(contactMomentNotification.contactMomentId, contactMomentNotification.bedrijfId, user.getUserId());

					if(errorService != null) error = new ErrorResource(errorService.getErrorCode());
				} else error = new ErrorResource(ErrorResource.CANNOT_REMOVE_CONTACTMOMENT);
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_REMOVE_CONTACTMOMENT);
			}
		} else error = new ErrorResource(ErrorResource.CANNOT_REMOVE_CONTACTMOMENT);

		return Response.ok(error).build();
	}

	@Path("notifyCompany")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response notifyCompany(final MeldingTransfer companyNotified) {
		ErrorResource error = null;
		MeldingTransfer newMelding = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(!(gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER) || gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER_KLANT)) ||
			   !((companyNotified.getMeldingId() == null && gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_INVOEREN)) ||
					(companyNotified.getMeldingId() != null && gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_AANPASSEN))) ||
			   (bedrijfService.isBedrijfOfKlant(user.getBedrijfId()) && !user.getBedrijfId().equals(companyNotified.getBedrijfIdGerapporteerd()))) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);

				// return Error
				return Response.ok(error).build();
			}

			// Company may not notify own company
			if(companyNotified.getBedrijfId().equals(companyNotified.getBedrijfIdGerapporteerd())) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);

				// return Error
				return Response.ok(error).build();
			}

		}
		try {
			if(companyNotified != null) {
				ErrorService meldingOk = null;

				if(!(gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER) || gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER_KLANT))) {
					meldingOk = bedrijfService.validateMeldingData(companyNotified);
				}

				if(meldingOk == null) {

					Melding melding = new Melding();

					melding.setMeldingId(companyNotified.getMeldingId());

					if(companyNotified.getMeldingId() != null) {
						melding.setGebruikerByLaatsteMutatieDoorGebruikerIdGebruikerId(user.getUserId());
						melding.setDatumLaatsteMutatie(new Date());
					} else {
						melding.setGebruikerByMeldingDoorGebruikerIdGebruikerId(companyNotified.getGebruikerAangemaaktId());
						melding.setDatumIngediend(new Date());
					}

					try {
						if(companyNotified.getBedrag() != null)
							melding.setBedrag(new BigDecimal(companyNotified.getBedrag().doubleValue()));
					} catch(Exception e) {
						// do nothing
					}
					melding.setDatumVerloopFactuur(companyNotified.getDatumFactuur());
					melding.setReferentieNummer(companyNotified.getReferentie());
					melding.setBedrijfByMeldingDoorBedrijfIdBedrijfId(companyNotified.getBedrijfIdGerapporteerd());
					melding.setBedrijfByMeldingOverBedrijfIdBedrijfId(companyNotified.getBedrijfId());

					// Changed to defaults
					//melding.setBedragWeergeven(companyNotified.isBedragWeergeven());
					//melding.setDoorBedrijfWeergeven(companyNotified.isDoorBedrijfWeergeven());
					melding.setBedragWeergeven(true);
					melding.setDoorBedrijfWeergeven(false);
					
					melding.setTelefoonNummerDebiteur(companyNotified.getTelefoonNummerDebiteur());
					melding.setEmailAdresDebiteur(companyNotified.getEmailAdresDebiteur());

					//melding.setNotities(companyNotified.getNotities());

					if(companyNotified.getMeldingId() == null) {
						if(companyNotified.isBedrijfsgegevensNietJuist())
							melding.setMeldingStatusCode(EMeldingStatus.DATA_NOK.getCode());
						else melding.setMeldingStatusCode(EMeldingStatus.INBEHANDELING.getCode());
					} else melding.setMeldingStatusCode(companyNotified.getMeldingstatusCode());
					
					
					Melding createdMelding = bedrijfService.saveMelding(melding);		
					
					if(createdMelding == null) 
						error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
					else if (companyNotified.getNotities() != null){	
							Notitie notitie = new Notitie();
							
							if (!gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER)) {
								// 'normal' user, not sbdr user
								
								if (melding.getNotitieGebruikerId() == null) 
									notitie.setNotitieId(melding.getNotitieGebruikerId());
								notitie.setDatum(new Date());
								notitie.setGebruikerGebruikerId(user.getUserId());
								//notitie.setMeldingMeldingId(createdMelding.getMeldingId());
								notitie.setNotitieType(ENotitieType.MELDING_GEBRUIKER.getCode());
								notitie.setNotitie(companyNotified.getNotities());
								Notitie createdNotitie = bedrijfService.saveNotitie(notitie, createdMelding.getMeldingId());
							}
					}
					
					// is user allowed to all notifications?
					if (gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ADMIN_SBDR_HOOFD))
						newMelding = bedrijfService.getMeldingData(createdMelding.getMeldingId(), null, createdMelding.getBedrijfByMeldingOverBedrijfIdBedrijfId(), user.getUserId());
					else
						newMelding = bedrijfService.getMeldingData(createdMelding.getMeldingId(), user.getBedrijfId(), createdMelding.getBedrijfByMeldingOverBedrijfIdBedrijfId(), user.getUserId());						
															
				} else error = new ErrorResource(meldingOk.getErrorCode());

			}
		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
		}

		if(error != null) return Response.ok(error).build();
		else {
			// is user allowed to view notities?
			//if (!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ADMIN_SBDR_HOOFD))
			//	newMelding.setNotities(null);
			return Response.ok(newMelding).build();
		}
	}

	@Path("notifyCompanyBatch")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response notifyCompanyBatch(final NotificationsBatchTransfer notificationsBatch) {
		ErrorResource error = null;
		NotificationBatchResult result = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(!gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_INVOEREN) || // klant, hoofd, gebruiker
					!user.getBedrijfId().equals(notificationsBatch.getBedrijfIdDoor())) // check on bedrijfId who performs the notification
			{
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
			}

			for(MeldingTransfer companyNotified : notificationsBatch.getMeldingen()) {
				// Company may not notify own company
				if(companyNotified.getBedrijfId().equals(companyNotified.getBedrijfIdGerapporteerd())) {
					error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);

					// return Error
					return Response.ok(error).build();
				}
			}

			try {
				if(!gebruikerService.validateUserData(user.getUserName(), notificationsBatch.getWachtwoord())) {
					error = new ErrorResource(ErrorResource.WRONG_PASSWORD);
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
			}
		}

		if(notificationsBatch.getMeldingen() != null && error == null) {
			try {
				// new meldingen, reset meldingId. Just to be sure....
				for(MeldingTransfer meldingTransfer : notificationsBatch.getMeldingen()) {
					meldingTransfer.setMeldingId(null);
				}

				ErrorService errorservice = bedrijfService.createMeldingBatch(user.getUserId(), notificationsBatch);

				if(errorservice == null) {
					result = new NotificationBatchResult();

					if(notificationsBatch.getMeldingen().length == 1)
						result.result = "Uw vermelding is ontvangen en wordt door ons in behandeling genomen.";
					else
						result.result = "Er zijn " + notificationsBatch.getMeldingen().length + " vermeldingen ontvangen. Deze worden door ons in behandeling genomen.";
				} else error = new ErrorResource(errorservice.getErrorCode());

			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(result).build();
	}

	@Path("removeHoldNotificationCompany")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeHoldNotificationCompany(final CompanyNotified companyNotified) {
		ErrorResource error = null;
		MeldingTransfer newMelding = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {
			boolean sbdrUser = gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER);

			if(!gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER) &&  // klant, hoofd, gebruiker
					!sbdrUser) //sbdr admin
			{
				error = new ErrorResource(ErrorResource.CANNOT_SAVE_MELDING);

				// return Error
				return Response.ok(error).build();
			}
		}

		try {
			if(companyNotified != null && companyNotified.meldingId != null) {
				bedrijfService.blokkeerMelding(companyNotified.meldingId, user.getUserId(), false);
			} else error = new ErrorResource(ErrorResource.CANNOT_SAVE_MELDING);

		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_SAVE_MELDING);
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(newMelding).build();
	}

	@Path("removeMonitoringCompany")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response removeMonitoringCompany(final RemoveCompanyMonitoring companyMonitoring) {
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		if(user != null) {

			try {
				if(companyMonitoring != null) {
					ErrorService errorService = bedrijfService.removeMonitoring(companyMonitoring.monitoringId, companyMonitoring.bedrijfId, user.getUserId());

					if(errorService != null) error = new ErrorResource(errorService.getErrorCode());
				} else error = new ErrorResource(ErrorResource.CANNOT_REMOVE_MONITORING);
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_REMOVE_MONITORING);
			}
		} else error = new ErrorResource(ErrorResource.CANNOT_REMOVE_MONITORING);

		return Response.ok(error).build();
	}

	@Path("removeNotificationCompany")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response removeNotificationCompany(final RemoveCompanyNotified companyNotified) {
		ErrorResource error = null;
		UserTransfer user = userResource.getUser();

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_REMOVE_NOTIFICATION);
			LOGGER.error("removeNotificationCompany: user is null");
		} else {
			try {
				Integer bedrijfIdGerapporteerd = user.getBedrijfId();
				boolean sbdrUser = gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER);

				if(!gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_INVOEREN) && !sbdrUser) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("removeNotificationCompany: action not allowed(MELDING_BEHEER, MELDING_INVOEREN");
				} else {
					if(companyNotified == null) {
						error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
						LOGGER.error("removeNotificationCompany: one or more parameters is empty/null(companyNotified)");
					} else {
						ErrorService errorService = bedrijfService.removeMelding(companyNotified.meldingId, companyNotified.bedrijfId, bedrijfIdGerapporteerd, user.getUserId(), companyNotified.reden, sbdrUser, true);

						if(errorService != null) {
							error = new ErrorResource(errorService.getErrorCode());
							LOGGER.error("removeNotificationCompany: " + error.getErrorCode() + " " + error.getErrorMsg());
						}
					}
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_REMOVE_NOTIFICATION);
				LOGGER.error("removeNotificationCompany: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok().build();
	}

	@Path("resolveException")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveException(@QueryParam("customMeldingId") String obfCustomMeldingId, @QueryParam("bedrijfId") String obfBedrijfId) {
		Integer customMeldingId = ObfuscatorUtils.deofuscateInteger(obfCustomMeldingId);
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		if(bedrijfId != null) {
			if(gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER)) { //sbdr admin
				try {
					bedrijfService.resolveException(customMeldingId, bedrijfId);

				} catch(ServiceException e) {
					error = new ErrorResource(ErrorResource.COMPANYEXCEPTION_NOT_RESOLVED);
				}
			} else error = new ErrorResource(ErrorResource.COMPANYEXCEPTION_NOT_RESOLVED);
		} else error = new ErrorResource(ErrorResource.COMPANYEXCEPTION_NOT_RESOLVED);

		return Response.ok(error).build();
	}

	/**
	 * Retrieves the companies with an alert.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("searchValue") String searchValue, @QueryParam("city") String city, @QueryParam("requestPerPage") boolean requestPerPage, @HeaderParam("Range") String range) //@QueryParam("kvknumber") String kvkNumber,
	{
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		ErrorResource error = null;

		LOGGER.info("CompanySearch GET searchValue: " + searchValue + " city: " + city + " range: " + range);

		// Sort order check with PFO?
		List<Sort.Order> orders = new LinkedList<Sort.Order>();

		PageRequest pageRequest = null;
		String contentRange = null;

		List<CompanyInfo> companies = null;

		try {
			companies = companyInfoService.retrieveFromCompanyInfo(searchValue, city, 100);
		} catch (ThirdPartyServiceException e) {
			error = new ErrorResource(ErrorResource.ERROR_COMPANY_SERVICE);
		} catch(Exception e) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
		}

		List<BedrijfTransfer> companiesTransfer = new ArrayList<BedrijfTransfer>();

		if(companies != null) {
			// total items
			long count = companies.size();

			if(range != null) {
				String[] rangeParts = range.replace("items=", "").split("-");
				int from = requestPerPage ? (new Integer(rangeParts[0]) - 1) : 0;
				int to = requestPerPage ? (new Integer(rangeParts[1]) - 1) : companies.size();
				int size = to - from;
				int page = size != 0 ? (to / size - 1) : 0;

				contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

				if(orders.size() > 0) pageRequest = new PageRequest(page, size + 1, new Sort(orders));
				else pageRequest = new PageRequest(page, size + 1);

				for(int i = from; i < Math.min(companies.size(), (to + 1)); i++) {
					CompanyInfo company = companies.get(i);
					LOGGER.debug("NOT SURE Search company: type " + company.getType() + " naam: " + company.getBedrijfsNaam() + " kvknr: " + company.getKvKnummer());
					boolean isHoofd = false;
					// only for company info purpose
					//if (companies.get(i).getSub() == null || companies.get(i).getSub().equals("0000"))
					//	isHoofd = true;
					if(company.getCreditSafeHeadQuarters() != null && company.getCreditSafeHeadQuarters().equals(EBedrijfType.HOOFD.getCode()))
						isHoofd = true;
					boolean isActief = true;
					if(company.getCreditSafeStatus() != null && !company.getCreditSafeStatus().equals(ECreditSafeStatusType.ACTIEF.getOmschrijving()))
						isActief = false;
					companiesTransfer.add(new BedrijfTransfer(null, isActief, isHoofd, company.getBedrijfsNaam(), company.getKvKnummer() + "", company.getSub(), null, company.getStraat(), company.getHuisNummer(), null, company.getPostcode(), company.getPlaats(), null, null));
				}
			}

			if(pageRequest != null) {
				System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
			}

			// addProperty
			try {
				companiesTransfer = bedrijfService.addBekendBijSbdrProperty(companiesTransfer); // + set SbdrNummer
				companiesTransfer = bedrijfService.addHeeftMeldingProperty(bedrijfId, companiesTransfer);
				companiesTransfer = bedrijfService.addHeeftMonitoringProperty(bedrijfId, companiesTransfer);
				companiesTransfer = bedrijfService.addRapportTodayCreated(bedrijfId, companiesTransfer);

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
		if(error != null) return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.serverError().ok(error).build();
		else
			return Response.ok(companiesTransfer.toArray(new BedrijfTransfer[companiesTransfer.size()])).header("Content-Range", contentRange).build();
	}

	/*
	 * Removes new notification alert on monitored company by updating monitoring change date
	 */
	@Path("updateMonitoring")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMonitoring(final UpdateCompanyMonitoring companyMonitoring) {
		ErrorResource error = null;

		try {
			if(companyMonitoring != null) {
				if(companyMonitoring.doorBedrijfId != null && companyMonitoring.vanBedrijfId != null) {
					ErrorService errorService = bedrijfService.updateMonitoring(companyMonitoring.vanBedrijfId, companyMonitoring.doorBedrijfId, companyMonitoring.gebruikerId);

					if(errorService != null) error = new ErrorResource(errorService.getErrorCode());
				}

			}
		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_MONITORING);
		}

		return Response.ok(error).build();
	}

	@Path("validateNotification")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response validateNotification(final MeldingTransfer meldingTransfer) {
		ErrorResource error = null;

		try {
			if(meldingTransfer != null) {
				ErrorService meldingOk = bedrijfService.validateMeldingData(meldingTransfer);

				if(meldingOk != null) {
					error = new ErrorResource(meldingOk.getErrorCode());
				}
			}
		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_NOTIFICATION);
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(null).build();
	}

	private BedrijfReportTransfer filterDataForMobile(BedrijfReportTransfer data) {
		BedrijfReportTransfer result = new BedrijfReportTransfer();

		result.setAantalMeldingenActief(data.getAantalMeldingenActief());
		result.setBedragOpenstaand(data.getBedragOpenstaand());
		result.setBedrijf(data.getBedrijf());
		result.setMeldingen(data.getMeldingen());
		result.setReferentieNummer(data.getReferentieNummer());
		result.setKvkDossierTransfer(data.getKvkDossierTransfer());
		result.setReportsLastTwoWeeks(data.getReportsLastTwoWeeks());

		return result;
	}

	private Bedrijf getOrCreateBedrijf(String kvkNumber, String subdossier, boolean isHoofdVestiging, boolean createNewIfNotExists) throws ServiceException, ThirdPartyServiceException {
		CIKvKDossier kvkDossier = null;

		kvkDossier = companyInfoService.getCIKvKDossierFromCompanyInfo(kvkNumber, subdossier, isHoofdVestiging);

		kvkDossier = companyInfoService.saveCIKvkDossier(kvkDossier);
		
		Bedrijf bedrijf = companyAccountService.createBedrijfFromCIKvkDossier(kvkDossier, createNewIfNotExists, false);
		
		return bedrijf;
	}

	// CRUD services for Company

	//	@GET
	//	@Path("{id}")
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public Company find(@PathParam("id") Long id)
	//	{
	//		LOGGER.info("Company find id=" + id);
	//
	//		Company x = new Company(1001, "Prutser B.V.", "123123", "Onzinstraat 12 3434JK Amersfoort");
	//
	//		return x;
	//	}
	//
	//
	//	@POST
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	public Company create(Company company)
	//	{
	//		LOGGER.info("create(): " + company);
	//
	//		return null; //this.newsEntryDao.save(newsEntry);
	//	}
	//
	//
	//	@POST
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Path("{id}")
	//	public Company update(@PathParam("id") Long id, Company company)
	//	{
	//		LOGGER.info("update(): " + company);
	//
	//		return null; //this.newsEntryDao.save(newsEntry);
	//	}
	//
	//
	//	@DELETE
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Path("{id}")
	//	public void delete(@PathParam("id") Long id)
	//	{
	//		LOGGER.info("delete(id)");
	//
	//		//this.newsEntryDao.delete(id);
	//	}

}
