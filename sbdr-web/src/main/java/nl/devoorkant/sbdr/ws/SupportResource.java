package nl.devoorkant.sbdr.ws;

import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.service.SupportService;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.transfer.SupportBestandTransfer;
import nl.devoorkant.sbdr.business.transfer.SupportTransfer;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.data.model.Support;
import nl.devoorkant.sbdr.data.model.SupportBestand;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.UserTransfer;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Component
@Path("/SupportService/support")
public class SupportResource {
	@Autowired
	private GebruikerService gebruikerService;
	@Autowired
	private SupportService supportService;

	@Autowired
	private UserResource userResource;
	private static Logger LOGGER = LoggerFactory.getLogger(SupportResource.class);

	@Path("createSupportTicket")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSupportTicket(final SupportTransfer sT) {
		SupportTransfer savedST = null;
		ErrorResource error = null;

		if(sT != null) {
			Support convertS = new Support();
			convertS.setBericht(sT.getBericht());
			convertS.setSupportRedenCode(sT.getSupportReden());
			convertS.setSupportSupportId(sT.getSupportParentId());
			convertS.setSupportTypeCode(sT.getSupportType());

			convertS.setGebruikerByGebruikerIdGebruikerId(sT.getGebruiker().getGebruikersId());

			if(sT.getMelding() != null) convertS.setMeldingMeldingId(sT.getMelding().getMeldingId());

			try {
				//Second argument is not used when creating initieal tickets, no matter whether it's true or false
				savedST = supportService.saveSupportTicket(convertS, null);
			} catch(Exception e) {
				//OPGEPAST: als zowel de business laag als de data laag een @Transactional annotation hebben, worden mogelijke errors pas hier afgevangen!
				LOGGER.error(e.getMessage());
				error = new ErrorResource(ErrorResource.CANNOT_SAVE_SUPPORT);
			}
		} else {
			error = new ErrorResource(ErrorResource.ERROR_PARAMETER_EMPTY);
		}

		if(savedST != null) return Response.ok(savedST).build();
		else return Response.ok(error).build();
	}

	@Path("createSupportTicketBestand")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSupportTicketBestand(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("BestandsNaam") String bestandsNaam, @FormDataParam("SupportId") String obfSId, @FormDataParam("SupportReferentie") String refNo) {
		ErrorResource error = null;
		SupportBestandTransfer savedSBT = null;

		if(fileInputStream != null && bestandsNaam != null && !bestandsNaam.isEmpty() && obfSId != null && refNo != null && !refNo.isEmpty()) {
			Integer sId = ObfuscatorUtils.deofuscateInteger(obfSId);
			
			try {
				byte[] doc = ByteStreams.toByteArray(fileInputStream);

				SupportBestand sBToSave = new SupportBestand();
				sBToSave.setSupportSupportId(sId);
				sBToSave.setOorspronkelijkBestandsNaam(bestandsNaam);
				sBToSave.setGearchiveerd(false);

				savedSBT = supportService.saveSupportTicketBestand(sBToSave, doc);
			} catch(IOException e) {
				error = new ErrorResource(ErrorResource.CANNOT_UPLOAD_FILE);
				supportService.deleteSupportTicketAndBestanden(sId);
			} catch(ServiceException e) {
				error = new ErrorResource(ErrorResource.CANNOT_CREATE_ATTACHMENT);
				supportService.deleteSupportTicketAndBestanden(sId);
			} catch(Exception e) {
				supportService.deleteSupportTicketAndBestanden(sId);
			}
		} else {
			if(obfSId != null) {
				Integer sId = ObfuscatorUtils.deofuscateInteger(obfSId);
				supportService.deleteSupportTicketAndBestanden(sId);
			}
		}

		if(savedSBT != null) return Response.ok(savedSBT).build();
		else return Response.ok(error).build();
	}

	@Path("createSupportTicketReaction")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSupportTicketReaction(final SupportTransfer sT) {
		return createObjectionReaction(sT, null);
	}

	@Path("findSupportTicketListForUser")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response findSupportTicketListForUser(@QueryParam("gebruikerId") String obfGId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @HeaderParam("Range") String range) {
		Integer gId = ObfuscatorUtils.deofuscateInteger(obfGId);
		SupportTransfer[] supportTickets = null;
		ErrorResource error = null;

		String contentRange = null;

		if(gId != null) {
			List<Sort.Order> orders = new LinkedList<>();

			if(sortedBy != null && !sortedBy.equals("")) {
				Sort.Order order = null;
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

			if(range != null && !range.equals("")) {
				String[] rangeParts = range.replace("items=", "").split("-");
				int from = new Integer(rangeParts[0]);
				int to = new Integer(rangeParts[1]);
				int size = to - from + 1;
				int page = to / size - 1;

				if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
				else pageRequest = new PageRequest(page, size);

				PageTransfer<SupportTransfer> userSupportTickets = null;

				try {
					userSupportTickets = supportService.findSupportTicketsByGebruikerId(gId, pageRequest);
				} catch(ServiceException e) {
					LOGGER.error(e.getMessage());
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_USER_SUPPORT_TICKETS);
				}

				if(userSupportTickets != null) {
					long count = userSupportTickets.getTotalElements();

					contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

					if(userSupportTickets.getContent() != null)
						supportTickets = userSupportTickets.getContent().toArray(new SupportTransfer[userSupportTickets.getContent().size()]);
				} else {
					LOGGER.error("null result in fetching supporttickets of user " + gId);
					error = new ErrorResource(ErrorResource.ERROR_RESULTS_EMPTY);
				}
			} else {
				error = new ErrorResource(ErrorResource.ERROR_PAGING);
			}

		} else {
			error = new ErrorResource(ErrorResource.ERROR_PARAMETER_EMPTY);
		}

		if(supportTickets != null) return Response.ok(supportTickets).header("Content-Range", contentRange).build();
		else return Response.ok(error).build();
	}

	@Path("findSupportTicketsByReferenceNo")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response findSupportTicketsByReferenceNo(@QueryParam("refNo") String refNo) {
		UserTransfer user = userResource.getUser();
		SupportTransfer[] sTs = null;
		ErrorResource error = null;

		if(user != null) {
			if(refNo != null && !refNo.isEmpty()) {
				List<SupportTransfer> sTl = null;

				try {
					sTl = supportService.findSupportTicketsByReferentieNummer(refNo);

					boolean userIsAllowed = false;

					if(gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SUPPORT_ADMIN))
						userIsAllowed = true;
					else {
						for(SupportTransfer st : sTl) {
							if(st.getGebruiker().getGebruikersId().equals(user.getUserId()) || (st.getBestemdVoorBedrijf() != null && st.getBestemdVoorBedrijf().getBedrijfId().equals(user.getBedrijfId())) || (st.getGeslotenDoorGebruikerId() != null && st.getGeslotenDoorGebruikerId().equals(user.getUserId()))) {
								userIsAllowed = true;
								break;
							}
						}
					}

					if(!userIsAllowed) error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
				} catch(ServiceException e) {
					LOGGER.error(e.getMessage());
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_USER_SUPPORT_TICKETS);
				}

				if(sTl != null) {
					sTs = sTl.toArray(new SupportTransfer[sTl.size()]);
				} else {
					LOGGER.error("null result in fetching supporttickets of referene " + refNo);
					error = new ErrorResource(ErrorResource.ERROR_RESULTS_EMPTY);
				}
			} else {
				error = new ErrorResource(ErrorResource.ERROR_PARAMETER_EMPTY);
			}
		}

		if(error != null) {
			return Response.ok(new Object[]{error}).build();
		} else return Response.ok(sTs).build();
	}

	@Path("pickUpSupportTicket")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response pickUpSupportTicket(@QueryParam("refNo") String refNo, @QueryParam("userId") String obfGId) {
		Integer gId = ObfuscatorUtils.deofuscateInteger(obfGId);
		ErrorResource error = null;

		if(refNo != null && !refNo.isEmpty() && gId != null) {
			try {
				supportService.pickUpSupportTicket(refNo, gId);
			} catch(ServiceException e) {
				LOGGER.error(e.getMessage());
				error = new ErrorResource(ErrorResource.CANNOT_PICKUP_SUPPORTTICKET);
			}
		} else {
			error = new ErrorResource(ErrorResource.ERROR_PARAMETER_EMPTY);
		}

		//When succeeded, nothing is returned!
		if(error == null) return Response.ok().build();
		else return Response.ok(error).build();
	}

	@Path("sbdrAcceptObjection")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sbdrAcceptObjection(final SupportTransfer sT) {
		ErrorResource error;
		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SUPPORT_ADMIN)) {
				error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);

			} else {
				return createObjectionReaction(sT, false);
			}
		} else {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_WEBSITEPARAM);
		}
		return Response.ok(error).build();
	}

	@Path("sbdrDiscardObjection")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sbdrDiscardObjection(final SupportTransfer sT) {
		ErrorResource error;
		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SUPPORT_ADMIN)) {
				error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);

			} else {
				return createObjectionReaction(sT, true);
			}
		} else {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_WEBSITEPARAM);
		}
		return Response.ok(error).build();
	}

	private Response createObjectionReaction(final SupportTransfer sT, final Boolean activateMelding) {
		SupportTransfer savesST = null;
		ErrorResource error = null;

		if(sT != null) {
			Support convertS = new Support();
			convertS.setBericht(sT.getBericht());
			convertS.setBetwistBezwaar(sT.getBetwistBezwaar());
			convertS.setSupportRedenCode(sT.getSupportReden());
			convertS.setSupportSupportId(sT.getSupportParentId());
			convertS.setSupportTypeCode(sT.getSupportType());
			convertS.setGebruikerByGebruikerIdGebruikerId(sT.getGebruiker().getGebruikersId());

			try {
				savesST = supportService.saveSupportTicket(convertS, activateMelding);
			} catch(Exception e) {
				//OPGEPAST: als zowel de business laag als de data laag een @Transactional annotation hebben, worden mogelijke errors pas hier afgevangen!
				LOGGER.error(e.getMessage());
				error = new ErrorResource(ErrorResource.CANNOT_SAVE_SUPPORT);
			}
		} else {
			error = new ErrorResource(ErrorResource.ERROR_PARAMETER_EMPTY);
		}

		if(savesST != null) return Response.ok(savesST).build();
		else return Response.ok(error).build();
	}
}
