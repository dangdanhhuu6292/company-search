package nl.devoorkant.sbdr.ws;

import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.service.InternalProcessService;
import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.transfer.BriefBatchTransfer;
import nl.devoorkant.sbdr.business.transfer.InternalProcessTransfer;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.UserTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/InternalProcessService/internal")
public class InternalProcessResource {

	@Autowired
	private GebruikerService gebruikerService;
	@Autowired
	private InternalProcessService internalProcessService;
	@Autowired
	private UserResource userResource;

	private static Logger LOGGER = LoggerFactory.getLogger(InternalProcessResource.class);

	@GET
	@Path("getInternalProcessRows")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInternalProcessRows(@HeaderParam("Range") String range) {
		LOGGER.info("InternalProcessResource, getInternalProcessRows");

		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		InternalProcessTransfer[] newProcessRowsAsArray = null;
		String contentRange = null;

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_LETTERS);
			LOGGER.error("getInternalProcessRows: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDR_MEDEWERKER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("getInternalProcessRows: action not allowed(SBDR_MEDEWERKER)");
				} else {
					if(range == null || range.equals("")) {
						error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
						LOGGER.error("getInternalProcessRows: one or more parameters is empty/null(range)");
					} else {
						String[] rangeParts = range.replace("items=", "").split("-");
						int from = new Integer(rangeParts[0]);
						int to = new Integer(rangeParts[1]);
						int size = to - from + 1;
						int page = to / size - 1;

						PageRequest pageRequest = new PageRequest(page, size);

						PageTransfer<InternalProcessTransfer> newProcessRows = internalProcessService.findAllNewProcessRows(pageRequest);

						if(newProcessRows != null) {
							if(newProcessRows.getContent() != null) {
								List<InternalProcessTransfer> newProcessRowsAsList = newProcessRows.getContent();
								long count = newProcessRowsAsList.size();
								contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

								newProcessRowsAsArray = newProcessRows.getContent().toArray(new InternalProcessTransfer[newProcessRows.getContent().size()]);
							}
						}

						System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
					}
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_LETTERS);
				LOGGER.error("getInternalProcessRows: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(newProcessRowsAsArray).header("Content-Range", contentRange).build();
	}

	@GET
	@Path("getLetterBatchRows")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLetterBatchRows(@HeaderParam("Range") String range) {
		LOGGER.info("InternalProcessResource, getLetterBatchRows");

		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		BriefBatchTransfer[] briefBatchesAsArray = null;
		String contentRange = null;

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_LETTERS);
			LOGGER.error("getLetterBatchRows: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDR_MEDEWERKER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("getLetterBatchRows: action not allowed(SBDR_MEDEWERKER)");
				} else {
					if(range == null || range.equals("")) {
						error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
						LOGGER.error("getLetterBatchRows: one or more parameters is empty/null(range)");
					} else {
						String[] rangeParts = range.replace("items=", "").split("-");
						int from = new Integer(rangeParts[0]);
						int to = new Integer(rangeParts[1]);
						int size = to - from  + 1;
						int page = to / size - 1;

						PageRequest pageRequest = new PageRequest(page, size);

						PageTransfer<BriefBatchTransfer> briefBatches = internalProcessService.findNewBriefBatches(pageRequest);
						if(briefBatches != null) {
							if(briefBatches.getContent() != null) {
								List<BriefBatchTransfer> briefBatchesAsList = briefBatches.getContent();
								long count = briefBatchesAsList.size();
								contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

								briefBatchesAsArray = briefBatches.getContent().toArray(new BriefBatchTransfer[briefBatches.getContent().size()]);
							}
						}

						System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
					}
				}
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_LETTERS);
				LOGGER.error("getLetterBatchRows: " + e.getMessage());
			}

		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok(briefBatchesAsArray).header("Content-Range", contentRange).build();
	}

	@GET
	@Path("printCustomerLetter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response printCustomerLetter(@QueryParam("klantId") String obfKlantId) {
		Integer klantId = ObfuscatorUtils.deofuscateInteger(obfKlantId);
		LOGGER.info("InternalProcessResource, printCustomerLetter");

		ErrorResource error = null;
		UserTransfer user = userResource.getUser();

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_ADD_LETTERS);
			LOGGER.error("printCustomerLetter: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDR_MEDEWERKER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("printCustomerLetter: action not allowed(SBDR_MEDEWERKER)");
				} else {
					if(klantId == null) {
						error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
						LOGGER.error("printCustomerLetter: one or more parameters is empty/null(klantId)");
					} else {
						internalProcessService.printCustomerLetter(klantId);

					}
				}
			} catch(Exception e) {
				error = new ErrorResource(ErrorResource.CANNOT_ADD_LETTERS);
				LOGGER.error("printCustomerLetter: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok().build();
	}

	@GET
	@Path("printNotificationLetter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response printNotificationLetter(@QueryParam("meldingId") String obfMeldingId) {
		Integer meldingId = ObfuscatorUtils.deofuscateInteger(obfMeldingId);
		LOGGER.info("InternalProcessResource, printNotificationLetter");

		ErrorResource error = null;
		UserTransfer user = userResource.getUser();

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_ADD_LETTERS);
			LOGGER.error("printNotificationLetter: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDR_MEDEWERKER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("printNotificationLetter: action not allowed(SBDR_MEDEWERKER)");
				} else {
					if(meldingId == null) {
						error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
						LOGGER.error("printNotificationLetter: one or more parameters is empty/null(meldingId)");
					} else {
						internalProcessService.printNotificationLetter(meldingId);

					}
				}
			} catch(Exception e) {
				error = new ErrorResource(ErrorResource.CANNOT_ADD_LETTERS);
				LOGGER.error("printNotificationLetter: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok().build();
	}

	@GET
	@Path("setProcessRowAsSent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setProcessRowAsSent(@QueryParam("processRowId") String obfProcessRowId, @QueryParam("gebruikerId") String obfGebruikerId) {
		Integer processRowId = ObfuscatorUtils.deofuscateInteger(obfProcessRowId);
		Integer gebruikerId = ObfuscatorUtils.deofuscateInteger(obfGebruikerId);
		LOGGER.info("InternalProcessResource, setProcessRowAsSent");

		ErrorResource error = null;
		UserTransfer user = userResource.getUser();

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_CHANGE_LETTER);
			LOGGER.error("setProcessRowAsSent: user is null");
		} else {
			try {
				if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDR_MEDEWERKER)) {
					error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
					LOGGER.error("setProcessRowAsSent: action not allowed(SBDR_MEDEWERKER)");
				} else {
					if(processRowId == null || gebruikerId == null) {
						error = new ErrorResource(ErrorResource.CANNOT_CHANGE_LETTER);
						LOGGER.error("setProcessRowAsSent: one or more parameters is empty/null(processRowId)");
					} else {
						internalProcessService.setInternalProcessRowAsSent(processRowId, gebruikerId);

					}
				}
			} catch(Exception e) {
				error = new ErrorResource(ErrorResource.CANNOT_CHANGE_LETTER);
				LOGGER.error("setProcessRowAsSent: " + e.getMessage());
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok().build();
	}
}
