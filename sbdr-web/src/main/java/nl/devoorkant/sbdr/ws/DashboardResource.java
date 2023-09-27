package nl.devoorkant.sbdr.ws;

import nl.devoorkant.exactonline.business.transfer.AuthenticationRequest;
import nl.devoorkant.sbdr.business.service.*;
import nl.devoorkant.sbdr.business.transfer.*;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.business.util.ECustomMeldingStatus;
import nl.devoorkant.sbdr.business.util.ESupportType;
import nl.devoorkant.sbdr.data.util.EKlantStatus;
import nl.devoorkant.sbdr.data.util.EMeldingStatus;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.UserTransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@Path("/DashboardService/overview")
public class DashboardResource {
	@Autowired
	private BedrijfService bedrijfService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private ExactOnlineService exactOnlineService;
	@Autowired
	private GebruikerService gebruikerService;
	@Autowired
	private UserResource userResource;

	private static Logger LOGGER = LoggerFactory.getLogger(DashboardResource.class);

	
	
	@Path("allCompaniesOfUser")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response allCompaniesOfUser(@QueryParam("bedrijfId") String obfCurrentBedrijfId) {
		List<GebruikerBedrijfTransfer> gebruikerBedrijfTransfers = null;
		
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfCurrentBedrijfId);
		LOGGER.info("allCompaniesOfUser method in DashboardResource. parameters: websiteParam=object");
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);} else {
			gebruikerBedrijfTransfers = gebruikerService.findGebruikerBedrijvenByGebruikerId(user.getUserId(), user.getBedrijfId());
		}
		
		if(error != null) {
			return Response.ok(error).build();
		} else {
			if(gebruikerBedrijfTransfers != null) {
				if(gebruikerBedrijfTransfers.size() == 0) {
					return Response.ok().build();
				} else if(gebruikerBedrijfTransfers.size() > 0) {
					GebruikerBedrijfTransfer[] gebruikerBedrijvenArray = gebruikerBedrijfTransfers.toArray(new GebruikerBedrijfTransfer[0]);
					return Response.ok(gebruikerBedrijvenArray).build();
				} else {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
					return Response.ok(error).build();
				}
			} else {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_GEBRUIKERS);
				return Response.ok(error).build();
			}
		}		
	}
	
	/**
	 * Retrieves the companies with an alert.
	 * For mobile users now also all alerts. Not only non-monitoring alerts anymore!
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("companiesalert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companiesAlert(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("companiesAlert method in DashboardResource. parameters: bedrijfId=" + bedrijfId + ", sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " +
				"filterValue=" + filterValue + ", range=" + range);
		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		AlertOverviewTransfer[] alertoverviewtransfer = null;
		String contentRange = null;
		
		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);} else {
			List<Sort.Order> orders = new LinkedList<>();
			PageRequest pageRequest = null;			

			if(sortedBy != null && !sortedBy.equals("")) {
				Order order;
				Direction sortDirection = null;

				if(sortDir != null) {
					if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
					else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
				}

				if(sortDirection != null) { order = new Order(Sort.Direction.ASC, sortedBy);} else {
					order = new Order(sortedBy);
				}

				orders.add(order);
			}

			if(range != null && !range.equals("")) {
				String[] rangeParts = range.replace("items=", "").split("-");
				int from = new Integer(rangeParts[0]);
				int to = new Integer(rangeParts[1]);
				int size = to - from + 1;
				int page = to / size - 1;

				if(orders.size() > 0) { pageRequest = new PageRequest(page, size, new Sort(orders));} else {
					pageRequest = new PageRequest(page, size);
				}

				try {					
					PageTransfer<AlertOverviewTransfer> aOPT = null;
					
					//if (!user.isMobileUser())
						aOPT = bedrijfService.findActiveAlertsOfBedrijf(bedrijfId, user.getUserId(), filterValue, pageRequest);
					//else
					//	aOPT = bedrijfService.findActiveAlertsNoMonitoringOfBedrijf(bedrijfId, user.getUserId(), filterValue, pageRequest);

					if(aOPT != null) {
						long count = aOPT.getTotalElements();

						contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

						if(aOPT.getContent() != null) {
							alertoverviewtransfer = aOPT.getContent().toArray(new AlertOverviewTransfer[aOPT.getContent().size()]);								
						}
						
						// websocket sendmessage
						//webSocketResource.sendMessage("{\"alerts\":\"" + count + "\"}", user.getUserName());
					}
				} catch(ServiceException e) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);}
			} else {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);}

			if(pageRequest != null) {
				System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
			}
		}

		//if(error != null) { return Response.ok(error).build();} else { return Response.ok().build();}
		
		if(error != null) { 
			return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).header("Content-Range", contentRange).build();
		} else {
			return Response.ok(alertoverviewtransfer).header("Content-Range", contentRange).build();
		}
	}

	/**
	 * Retrieves the monitored companies with an alert.
	 * For mobile users these are all monitoring companies with a notification
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("companiesmonitoringalert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companiesMonitoringAlert(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("companiesAlert method in DashboardResource. parameters: bedrijfId=" + bedrijfId + ", sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " +
				"filterValue=" + filterValue + ", range=" + range);
		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		AlertOverviewTransfer[] alertoverviewtransfer = null;
		String contentRange = null;
		
		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);} else {
			List<Sort.Order> orders = new LinkedList<>();
			PageRequest pageRequest = null;			

			if(sortedBy != null && !sortedBy.equals("")) {
				Order order;
				Direction sortDirection = null;

				if(sortDir != null) {
					if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
					else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
				}

				if(sortDirection != null) { order = new Order(Sort.Direction.ASC, sortedBy);} else {
					order = new Order(sortedBy);
				}

				orders.add(order);
			}

			if(range != null && !range.equals("")) {
				String[] rangeParts = range.replace("items=", "").split("-");
				int from = new Integer(rangeParts[0]);
				int to = new Integer(rangeParts[1]);
				int size = to - from + 1;
				int page = to / size - 1;

				if(orders.size() > 0) { pageRequest = new PageRequest(page, size, new Sort(orders));} else {
					pageRequest = new PageRequest(page, size);
				}

				try {					
					PageTransfer<AlertOverviewTransfer> aOPT = null;
					
					// These view alerts only for mobileusers
					if (user.isMobileUser())
						aOPT = bedrijfService.findActiveMonitoringNotificationsOfBedrijf(bedrijfId, pageRequest);
						//aOPT = bedrijfService.findActiveMonitoringAlertsOfBedrijf(bedrijfId, user.getUserId(), filterValue, pageRequest);

					if(aOPT != null) {
						long count = aOPT.getTotalElements();

						contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

						if(aOPT.getContent() != null) {
							alertoverviewtransfer = aOPT.getContent().toArray(new AlertOverviewTransfer[aOPT.getContent().size()]);								
						}
					}
				} catch(ServiceException e) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);}
			} else {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);}

			if(pageRequest != null) {
				System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
			}
		}

		//if(error != null) { return Response.ok(error).build();} else { return Response.ok().build();}
		
		if(error != null) { 
			return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).header("Content-Range", contentRange).build();
		} else {
			return Response.ok(alertoverviewtransfer).header("Content-Range", contentRange).build();
		}
	}
	
	/**
	 * Retrieves the companies with an active notification.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("companiesmonitor")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companiesMonitor(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("companiesMonitor method in DashboardResource. parameters: bedrijfId=" + bedrijfId + ", sortDir=" + sortDir + ", sortedBy=" + sortedBy +
				", filterValue=" + filterValue + ", range=" + range);

		ErrorResource error = null;
		List<Sort.Order> orders = new LinkedList<>();
		PageRequest pageRequest = null;
		String contentRange = null;
		MonitoringOverviewTransfer[] companymonitoring = null;

		if(sortedBy != null && !sortedBy.equals("")) {
			Order order;
			Direction sortDirection = null;

			if(sortDir != null) {
				if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
				else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
			}

			if(sortDirection != null) order = new Order(sortDirection, sortedBy);
			else order = new Order(sortedBy);

			orders.add(order);
		}

		if(range != null && !range.equals("")) {
			String[] rangeParts = range.replace("items=", "").split("-");
			int from = new Integer(rangeParts[0]);
			int to = new Integer(rangeParts[1]);
			int size = to - from + 1;
			int page = to / size - 1;

			if(orders.size() > 0) { pageRequest = new PageRequest(page, size, new Sort(orders));} else {
				pageRequest = new PageRequest(page, size);
			}

			try {
				PageTransfer<MonitoringOverviewTransfer> monitoring = bedrijfService.findActiveMonitoringOfBedrijf(bedrijfId, filterValue, pageRequest);

				if(monitoring != null) {
					long count = monitoring.getTotalElements();

					contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

					if(monitoring.getContent() != null) {
						companymonitoring = monitoring.getContent().toArray(new MonitoringOverviewTransfer[monitoring.getContent().size()]);
					}
				} else {LOGGER.error("null result in fetching gebruikersOfKlantGebruiker");}
			} catch(ServiceException e) {
				LOGGER.error(e.getMessage());

				error = new ErrorResource(ErrorResource.CANNOT_FETCH_MONITORINGOVERVIEW);
			}
		} else {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_MONITORINGOVERVIEW);
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
		}

		if(error != null) { 
			return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).header("Content-Range", contentRange).build();
		} else {
			return Response.ok(companymonitoring).header("Content-Range", contentRange).build();
		}
	}

	/**
	 * Retrieves the companies with an active notification.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("companiesnotified")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companiesNotified(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("companiesNotified method in DashboardResource. parameters: bedrijfId=" + bedrijfId + ", sortDir=" + sortDir + ", sortedBy=" + sortedBy +
				", filterValue=" + filterValue + ", range=" + range);

		List<Sort.Order> orders = new LinkedList<>();
		PageRequest pageRequest = null;
		String contentRange = null;
		ErrorResource error = null;
		MeldingOverviewTransfer[] companynotified = null;
		UserTransfer user = userResource.getUser();

		if(sortedBy != null && !sortedBy.equals("")) {
			Order order;
			Direction sortDirection = null;

			if(sortDir != null) {
				if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
				else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
			}

			if(sortDirection != null) order = new Order(Sort.Direction.ASC, sortedBy);
			else order = new Order(sortedBy);

			orders.add(order);
		}

		if(gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_AANPASSEN) && range != null && !range.equals("")) {
			String[] rangeParts = range.replace("items=", "").split("-");
			int from = new Integer(rangeParts[0]);
			int to = new Integer(rangeParts[1]);
			int size = to - from + 1;
			int page = to / size - 1;

			if(orders.size() > 0) { pageRequest = new PageRequest(page, size, new Sort(orders));} else {
				pageRequest = new PageRequest(page, size);
			}

			try {
				PageTransfer<MeldingOverviewTransfer> notification = bedrijfService.findActiveMeldingenOfBedrijf(bedrijfId, filterValue, pageRequest);

				if(notification != null) {
					long count = notification.getTotalElements();

					contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

					if(notification.getContent() != null) {
						companynotified = notification.getContent().toArray(new MeldingOverviewTransfer[notification.getContent().size()]);
					}
				} else {LOGGER.error("null result in fetching gebruikersOfKlantGebruiker");}
			} catch(ServiceException e) {
				LOGGER.error(e.getMessage());

				error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTIFICATIONOVERVIEW);
			}
		} else {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_NOTIFICATIONOVERVIEW);
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" +
					pageRequest.getOffset());
		}

		if(error != null) {
			return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).header("Content-Range", contentRange).build();
		} else {
			return Response.ok(companynotified).header("Content-Range", contentRange).build();
		}
	}

	/**
	 * Retrieves the companies removed from monitoring or with a removed notification.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("companiesremoved")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response companiesRemoved(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("view") String view, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("companiesRemoved method in DashboardResource. parameters: bedrijfId=" + bedrijfId + ", view=" + view + ", sortDir=" + sortDir + ", " +
				"sortedBy=" + sortedBy + ", filterValue=" + filterValue + ", range=" + range);

		List<Sort.Order> orders = new LinkedList<>();
		PageRequest pageRequest = null;
		String contentRange = null;
		RemovedBedrijfOverviewTransfer[] companyremoved = null;
		ErrorResource error = null;

		if(sortedBy != null && !sortedBy.equals("")) {
			Order order;
			Direction sortDirection = null;

			if(sortDir != null) {
				if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
				else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
			}

			if(sortDirection != null) order = new Order(sortDirection, sortedBy);
			else order = new Order(Sort.Direction.ASC, sortedBy);

			orders.add(order);
		}

		if(range != null && !range.equals("")) {
			String[] rangeParts = range.replace("items=", "").split("-");
			int from = new Integer(rangeParts[0]);
			int to = new Integer(rangeParts[1]);
			int size = to - from + 1;
			int page = to / size - 1;

			if(orders.size() > 0) { pageRequest = new PageRequest(page, size, new Sort(orders));} else {
				pageRequest = new PageRequest(page, size);
			}

			try {
				PageTransfer<RemovedBedrijfOverviewTransfer> removedbedrijven = bedrijfService.findRemovedBedrijvenOfBedrijf(bedrijfId, view, filterValue, pageRequest);

				if(removedbedrijven != null) {
					long count = removedbedrijven.getTotalElements();

					contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

					if(removedbedrijven.getContent() != null) {
						companyremoved = removedbedrijven.getContent().toArray(new RemovedBedrijfOverviewTransfer[removedbedrijven.getContent().size()]);
					}

				} else {
					LOGGER.error("null result in fetching gebruikersOfKlantGebruiker");
				}

			} catch(ServiceException e) {
				LOGGER.error(e.getMessage());

				error = new ErrorResource(ErrorResource.CANNOT_FETCH_REMOVEDOVERVIEW);
			}
		} else {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_REMOVEDOVERVIEW);
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
		}

		if(error != null) { 
			return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).header("Content-Range", contentRange).build();
		} else {
			return Response.ok(companyremoved).header("Content-Range", contentRange).build();
		}
	}

	/**
	 * Retrieves the objections admin.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("objectionsadmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response objectionsAdmin(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("objectionsAdmin method in DashboardResource. parameters: bedrijfId=" + bedrijfId + ", sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " +
				"filterValue=" + filterValue + ", range=" + range);
		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		AdminAlertTransfer[] aATa = null;
		String contentRange = null;
		
		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);} else {
			List<Sort.Order> orders = new LinkedList<>();
			PageRequest pageRequest = null;			

			if(sortedBy != null && !sortedBy.equals("")) {
				Order order;
				Direction sortDirection = null;

				if(sortDir != null) {
					if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
					else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
				}

				if(sortDirection != null) { order = new Order(Sort.Direction.ASC, sortedBy);} else {
					order = new Order(sortedBy);
				}

				orders.add(order);
			}

			if(range != null && !range.equals("")) {
				String[] rangeParts = range.replace("items=", "").split("-");
				int from = new Integer(rangeParts[0]);
				int to = new Integer(rangeParts[1]);
				int size = to - from + 1;
				int page = to / size - 1;

				if(orders.size() > 0) { pageRequest = new PageRequest(page, size, new Sort(orders));} else {
					pageRequest = new PageRequest(page, size);
				}

				try {
					if(gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ALERT_ADMIN_BEZWAAR)) {
						PageTransfer<AdminAlertTransfer> aAPT = null;
						boolean includeObjections = gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ALERT_ADMIN_BEZWAAR);

						aAPT = bedrijfService.findActiveObjectionsForSbdrAdmin(bedrijfId, pageRequest);

						if(aAPT != null) {
							if(aAPT.getContent() != null) {								
								long count = aAPT.getContent().size();

								contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

								aATa = aAPT.getContent().toArray(new AdminAlertTransfer[aAPT.getContent().size()]);								
							}
						}
					} else {
						error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);
					}
				} catch(ServiceException e) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);}
			} else {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);}

			if(pageRequest != null) {
				System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
			}
		}

		//if(error != null) { return Response.ok(error).build();} else { return Response.ok().build();}
		
		if(error != null) { return Response.ok(error).header("Content-Range", contentRange).build();} else {
			return Response.ok(aATa).header("Content-Range", contentRange).build();
		}
	}
	
	/**
	 * Retrieves the support tickets admin.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("supportticketsadmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response supportTicketsAdmin(@QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("supportTicketsAdmin method in DashboardResource. parameters: bedrijfId=" + bedrijfId + ", sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " +
				"filterValue=" + filterValue + ", range=" + range);
		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		AdminAlertTransfer[] aATa = null;
		String contentRange = null;
		
		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);} else {
			List<Sort.Order> orders = new LinkedList<>();
			PageRequest pageRequest = null;			

			if(sortedBy != null && !sortedBy.equals("")) {
				Order order;
				Direction sortDirection = null;

				if(sortDir != null) {
					if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
					else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
				}

				if(sortDirection != null) { order = new Order(Sort.Direction.ASC, sortedBy);} else {
					order = new Order(sortedBy);
				}

				orders.add(order);
			}

			if(range != null && !range.equals("")) {
				String[] rangeParts = range.replace("items=", "").split("-");
				int from = new Integer(rangeParts[0]);
				int to = new Integer(rangeParts[1]);
				int size = to - from + 1;
				int page = to / size - 1;

				if(orders.size() > 0) { pageRequest = new PageRequest(page, size, new Sort(orders));} else {
					pageRequest = new PageRequest(page, size);
				}

				try {
					if(gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SUPPORT_ADMIN)) {
						PageTransfer<AdminAlertTransfer> aAPT = null;
						boolean includeObjections = gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ALERT_ADMIN_BEZWAAR);

						aAPT = bedrijfService.findActiveSupportTicketsForSbdrAdmin(bedrijfId, pageRequest);

						if(aAPT != null) {
							if(aAPT.getContent() != null) {								
								long count = aAPT.getContent().size();

								contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

								aATa = aAPT.getContent().toArray(new AdminAlertTransfer[aAPT.getContent().size()]);								
							}
						}
					} else {
						error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);
					}
				} catch(ServiceException e) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);}
			} else {error = new ErrorResource(ErrorResource.CANNOT_FETCH_ALERTOVERVIEW);}

			if(pageRequest != null) {
				System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
			}
		}

		//if(error != null) { return Response.ok(error).build();} else { return Response.ok().build();}
		
		if(error != null) { return Response.ok(error).header("Content-Range", contentRange).build();} else {
			return Response.ok(aATa).header("Content-Range", contentRange).build();
		}
	}
	
	/**
	 * Retrieves the companies with REG, PRO or ACT status to be maintained by SBDR users.
	 *
	 * @return A transfer containing the KlantBedrijfOverviewTransfer.
	 */
	@GET
	@Path("customersadmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response customersadmin(@QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		LOGGER.info("customersadmin method in DashboardResource. parameters: sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " + "filterValue=" + filterValue + ", range=" + range);

		ErrorResource error = null;
		PageRequest pageRequest = null;
		String contentRange = null;
		KlantBedrijfOverviewTransfer[] customercompanies = null;
		UserTransfer user = userResource.getUser();

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_KLANTBEDRIJFOVERVIEW);
		} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.MELDING_BEHEER) && !gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.ADMIN_SBDR_HOOFD)) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_KLANTBEDRIJFOVERVIEW);
			} else {
				List<Sort.Order> orders = new LinkedList<>();

				if(sortedBy != null && !sortedBy.equals("")) {
					Order order;
					Direction sortDirection = null;

					if(sortDir != null) {
						if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
						else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
					}

					if(sortDirection != null) { order = new Order(Sort.Direction.ASC, sortedBy);} else {
						order = new Order(sortedBy);
					}

					orders.add(order);
				}

				if(range != null && !range.equals("")) {
					String[] rangeParts = range.replace("items=", "").split("-");
					int from = new Integer(rangeParts[0]);
					int to = new Integer(rangeParts[1]);
					int size = to - from + 1;
					int page = to / size - 1;

					if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
					else pageRequest = new PageRequest(page, size);

					try {
						PageTransfer<KlantBedrijfOverviewTransfer> klantbedrijven = bedrijfService.findAllKlantBedrijvenOnActiveKlantStatus(filterValue, EKlantStatus.getAllIncompleteCodes(), pageRequest);

						if(klantbedrijven != null) {
							long count = klantbedrijven.getTotalElements();

							contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

							if(klantbedrijven.getContent() != null) {
								customercompanies = klantbedrijven.getContent().toArray(new KlantBedrijfOverviewTransfer[klantbedrijven.getContent().size()]);
							}

						} else {
							LOGGER.error("null result in fetching KlantBedrijfOverviewTransfer(s)");
						}

					} catch(ServiceException e) {
						LOGGER.error(e.getMessage());

						error = new ErrorResource(ErrorResource.CANNOT_FETCH_KLANTBEDRIJFOVERVIEW);
					}
				} else {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_KLANTBEDRIJFOVERVIEW);
				}

				if(pageRequest != null) {
					System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" + pageRequest.getOffset());
				}
			}
		}

		LOGGER.info("Filtercriteria: filter '" + filterValue + "' sortedBy '" + sortedBy + "'" + " sortDir '" + sortDir);

		if(error != null) {return Response.ok(error).header("Content-Range", contentRange).build();} else {
			return Response.ok(customercompanies).header("Content-Range", contentRange).build();
		}
	}

	// SBDR Admin tabs functionality

	/**
	 * Retrieves the websiteparam settings.
	 *
	 * @return A transfer containing the exactonlineparam values.
	 */
	@GET
	@Path("exactonlineparam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response exactonlineparam() {
		LOGGER.info("exactonlineparam method in DashboardResource.");
		ErrorResource error = null;
		AuthenticationRequest exactonlineparamTransfer = null;

		UserTransfer user = userResource.getUser();

		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_EXACTONLINEPARAM);} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.EXACTONLINE)) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_EXACTONLINEPARAM);
			} else {
				exactonlineparamTransfer = exactOnlineService.fetchAuthenticationRequest();

				if(exactonlineparamTransfer == null) {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_EXACTONLINEPARAM);
				}
			}
		}

		if(error == null) {return Response.ok(exactonlineparamTransfer).build();} else {
			return Response.ok(error).build();
		}
	}	

	/**
	 * Retrieves the companies with REG, PRO or ACT status to be maintained by SBDR users.
	 *
	 * @return A transfer containing the KlantBedrijfOverviewTransfer.
	 */
	@GET
	@Path("exceptioncompaniesadmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response exceptioncompaniesadmin(@QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		LOGGER.info("exceptioncompaniesadmin method in DashboardResource. parameters: sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " + "filterValue=" + filterValue + ", range=" + range);
		ErrorResource error = null;
		PageRequest pageRequest = null;

		String contentRange = null;

		UserTransfer user = userResource.getUser();

		ExceptionBedrijfOverviewTransfer[] exceptioncompanies = null;

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
		} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ADMIN_SBDR_HOOFD)) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
			} else {
				List<Sort.Order> orders = new LinkedList<>();

				if(sortedBy != null && !sortedBy.equals("")) {
					Order order;
					Direction sortDirection = null;

					if(sortDir != null) {
						if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
						else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
					}

					if(sortDirection != null) order = new Order(Sort.Direction.ASC, sortedBy);
					else order = new Order(sortedBy);

					orders.add(order);
				}

				if(range != null && !range.equals("")) {
					String[] rangeParts = range.replace("items=", "").split("-");
					int from = new Integer(rangeParts[0]);
					int to = new Integer(rangeParts[1]);
					int size = to - from + 1;
					int page = to / size - 1;

					if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
					else pageRequest = new PageRequest(page, size);

					try {
						List<String> statuscodes = new ArrayList<>();
						statuscodes.add(ECustomMeldingStatus.INBEHANDELING.getCode());
						PageTransfer<ExceptionBedrijfOverviewTransfer> exceptionbedrijven = bedrijfService.findAllExceptionBedrijven(filterValue, statuscodes, pageRequest);

						if(exceptionbedrijven != null) {
							long count = exceptionbedrijven.getTotalElements();

							contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

							if(exceptionbedrijven.getContent() != null) {
								try {
									exceptionbedrijven.setContent(bedrijfService.addKlantProperty(exceptionbedrijven.getContent()));

								} catch(ServiceException e) {
									error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
								}

								exceptioncompanies = exceptionbedrijven.getContent().toArray(new ExceptionBedrijfOverviewTransfer[exceptionbedrijven.getContent().size()]);
							}

						} else {
							LOGGER.error("null result in fetching ExceptionBedrijfOverviewTransfer(s)");
						}

					} catch(ServiceException e) {
						LOGGER.error(e.getMessage());

						error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
					}
				} else {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_COMPANY);
				}
			}
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" +
					pageRequest.getOffset());
		}

		if(error != null) {return Response.ok(error).header("Content-Range", contentRange).build();} else {
			return Response.ok(exceptioncompanies).header("Content-Range", contentRange).build();
		}
	}

	/**
	 * Retrieves the companies with an active notification.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("generaladmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response generaladmin() {
		LOGGER.info("generaladmin method in DashboardResource.");
		ErrorResource error = null;
		AdminOverviewTransfer adminOverviewTransfer = null;

		UserTransfer user = userResource.getUser();

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
		} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ADMIN_SBDR_HOOFD)) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
			} else {
				try {
					adminOverviewTransfer = bedrijfService.adminOverviewTransfer();

					if(adminOverviewTransfer == null)
						error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
				} catch(ServiceException e) {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
				}
			}
		}

		if(error == null) return Response.ok(adminOverviewTransfer).build();
		else return Response.ok(error).build();
	}

	/**
	 * Retrieves the companies with an active notification.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("notificationsadmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notificationsadmin(@QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @QueryParam("showActive") Boolean showActive, @HeaderParam("Range") String range) {

		LOGGER.info("notificationsadmin method in DashboardResource. parameters: sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " + "filterValue=" + filterValue + ", range=" + range);
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		List<Sort.Order> orders = new LinkedList<>();

		PageRequest pageRequest = null;
		String contentRange = null;

		MeldingTransfer[] notifications = null;

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
		} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER) && !gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ADMIN_SBDR_HOOFD)) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
			} else {
				if(sortedBy != null && !sortedBy.equals("")) {
					Order order;
					Direction sortDirection = null;

					if(sortDir != null) {
						if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
						else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
					}

					if(sortDirection != null) order = new Order(Sort.Direction.ASC, sortedBy);
					else order = new Order(sortedBy);

					orders.add(order);
				}

				if(range != null && !range.equals("")) {
					String[] rangeParts = range.replace("items=", "").split("-");
					int from = new Integer(rangeParts[0]);
					int to = new Integer(rangeParts[1]);
					int size = to - from + 1;
					int page = to / size - 1;

					if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
					else pageRequest = new PageRequest(page, size);

					try {
						List<String> klantstatuscodes = new ArrayList<>();
						klantstatuscodes.add(EKlantStatus.ACTIEF.getCode());
						List<String> meldingStatusCodes = new ArrayList();
						// 'NOK', 'INI', 'INB', 'BLK' or 'ACT'
						if (showActive != null && showActive)
							meldingStatusCodes.add(EMeldingStatus.ACTIEF.getCode());
						else {
							meldingStatusCodes.add(EMeldingStatus.DATA_NOK.getCode());
							meldingStatusCodes.add(EMeldingStatus.INBEHANDELING.getCode());
							meldingStatusCodes.add(EMeldingStatus.GEBLOKKEERD.getCode());
							meldingStatusCodes.add(EMeldingStatus.DATA_NOK.getCode());
						}
						PageTransfer<MeldingTransfer> meldingen = bedrijfService.findMeldingenOfAllBedrijven(filterValue, klantstatuscodes, meldingStatusCodes, pageRequest);

						if(meldingen != null) {
							long count = meldingen.getTotalElements();

							contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

							if(meldingen.getContent() != null)
								notifications = meldingen.getContent().toArray(new MeldingTransfer[meldingen.getContent().size()]);

						} else {
							LOGGER.debug("null result in fetching MeldingTransfer(s)");

						}

					} catch(ServiceException e) {
						LOGGER.error(e.getMessage());

						error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
					}
				} else {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
				}
			}
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" +
					pageRequest.getOffset());
		}

		if(error != null) {return Response.ok(error).header("Content-Range", contentRange).build();} else {
			return Response.ok(notifications).header("Content-Range", contentRange).build();
		}
	}

	/**
	 * Retrieves the companies with an active notification.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("notificationsofprospectadmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notificationsofprospectadmin(@QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		LOGGER.info("notificationsofprospectadmin method in DashboardResource. parameters: sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " + "filterValue=" + filterValue + ", range=" + range);
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		List<Sort.Order> orders = new LinkedList<>();

		PageRequest pageRequest = null;
		String contentRange = null;

		MeldingTransfer[] notifications = null;

		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
		} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER) && !gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.ADMIN_SBDR_HOOFD)) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
			} else {
				if(sortedBy != null && !sortedBy.equals("")) {
					Order order;
					Direction sortDirection = null;

					if(sortDir != null) {
						if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
						else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
					}

					if(sortDirection != null) order = new Order(Sort.Direction.ASC, sortedBy);
					else order = new Order(sortedBy);

					orders.add(order);
				}

				if(range != null && !range.equals("")) {
					String[] rangeParts = range.replace("items=", "").split("-");
					int from = new Integer(rangeParts[0]);
					int to = new Integer(rangeParts[1]);
					int size = to - from + 1;
					int page = to / size - 1;

					if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
					else pageRequest = new PageRequest(page, size);

					try {
						List<String> klantstatuscodes = new ArrayList<>();
						klantstatuscodes.add(EKlantStatus.DATA_NOK.getCode());
						klantstatuscodes.add(EKlantStatus.PROSPECT.getCode());
						List<String> meldingStatusCodes = new ArrayList();
						// 'NOK', 'INI', 'INB', 'BLK' or 'ACT'
						meldingStatusCodes.add(EMeldingStatus.DATA_NOK.getCode());
						meldingStatusCodes.add(EMeldingStatus.INBEHANDELING.getCode());
						meldingStatusCodes.add(EMeldingStatus.GEBLOKKEERD.getCode());
						meldingStatusCodes.add(EMeldingStatus.DATA_NOK.getCode());
						
						PageTransfer<MeldingTransfer> meldingen = bedrijfService.findMeldingenOfAllBedrijven(filterValue, klantstatuscodes, meldingStatusCodes, pageRequest);

						if(meldingen != null) {
							long count = meldingen.getTotalElements();

							contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

							if(meldingen.getContent() != null)
								notifications = meldingen.getContent().toArray(new MeldingTransfer[meldingen.getContent().size()]);

						} else {
							LOGGER.debug("null result in fetching MeldingTransfer(s)");
						}

					} catch(ServiceException e) {
						LOGGER.error(e.getMessage());

						error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
					}
				} else {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_MELDINGOVERVIEW);
				}
			}
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" +
					pageRequest.getOffset());
		}

		if(error != null) {return Response.ok(error).header("Content-Range", contentRange).build();} else {
			return Response.ok(notifications).header("Content-Range", contentRange).build();
		}
	}

	/**
	 * Retrieves the companies with an active notification.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("reportrequested")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reportRequested(@QueryParam("gebruikerId") String obfGebruikerId, @QueryParam("bedrijfId") String obfBedrijfId, @QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @HeaderParam("Range") String range) {
		Integer gebruikerId = ObfuscatorUtils.deofuscateInteger(obfGebruikerId);
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		LOGGER.info("reportRequested method in DashboardResource. parameters: gebruikerId=" + gebruikerId + ", bedrijfId=" + bedrijfId + " sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " + "filterValue=" + filterValue + ", range=" + range);

		List<Sort.Order> orders = new LinkedList<>();

		PageRequest pageRequest = null;
		String contentRange = null;

		ErrorResource error = null;
		ReportRequestedTransfer[] reportsRequested = null;

		if(sortedBy != null && !sortedBy.equals("")) {
			Order order;
			Direction sortDirection = null;

			if(sortDir != null) {
				if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
				else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
			}

			if(sortDirection != null) order = new Order(Sort.Direction.ASC, sortedBy);
			else order = new Order(sortedBy);

			orders.add(order);
		}

		if(range != null && !range.equals("")) {
			String[] rangeParts = range.replace("items=", "").split("-");
			int from = new Integer(rangeParts[0]);
			int to = new Integer(rangeParts[1]);
			int size = to - from + 1;
			int page = to / size - 1;

			if(orders.size() > 0) pageRequest = new PageRequest(page, size, new Sort(orders));
			else pageRequest = new PageRequest(page, size);

			try {
				PageTransfer<ReportRequestedTransfer> reportsRequestedPage = documentService.getRequestedReportsByGebruikerIdBedrijfId(gebruikerId, bedrijfId, filterValue, pageRequest);

				if(reportsRequestedPage != null) {
					long count = reportsRequestedPage.getTotalElements();

					contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

					if(reportsRequestedPage.getContent() != null)
						reportsRequested = reportsRequestedPage.getContent().toArray(new ReportRequestedTransfer[reportsRequestedPage.getContent().size()]);

				} else {
					LOGGER.error("null result in fetching gebruikersOfKlantGebruiker");
				}

			} catch(ServiceException e) {
				LOGGER.error(e.getMessage());

				error = new ErrorResource(ErrorResource.CANNOT_FETCH_REPORTREQUESTEDOVERVIEW);
			}
		} else {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_REPORTREQUESTEDOVERVIEW);
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" +
					pageRequest.getOffset());
		}

		if(error != null) {
			return Response.status(new Integer(error.getErrorCode())).entity(error.getErrorMsg()).build(); //return Response.ok(error).header("Content-Range", contentRange).build();
		} else {
			return Response.ok(reportsRequested).header("Content-Range", contentRange).build();
		}
	}

	@Path("saveWebsiteparam")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@Produces({"application/xml","application/json"})
	public Response saveWebsiteparam(final WebsiteparamTransfer websiteparam) {
		LOGGER.info("saveWebsiteparam method in DashboardResource. parameters: websiteParam=object");
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_SAVE_WEBSITEPARAM);} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.WEBSITEPARAM)) {
				error = new ErrorResource(ErrorResource.CANNOT_SAVE_WEBSITEPARAM);
			} else {
				try {
					bedrijfService.saveWebsiteparam(websiteparam);

				} catch(ServiceException e) {
					error = new ErrorResource(ErrorResource.CANNOT_SAVE_WEBSITEPARAM);
				}
			}
		}

		if(error != null) return Response.ok(error).build();
		else return Response.ok().build();
	}

	/**
	 * Retrieves the companies with an active notification.
	 *
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("searchresultsadmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchresultsadmin(@QueryParam("sortDir") String sortDir, @QueryParam("sortedBy") String sortedBy, @QueryParam("filterValue") String filterValue, @QueryParam("vermelder") Boolean vermelder, @HeaderParam("Range") String range) {

		LOGGER.info("reportRequested method in DashboardResource. parameters: sortDir=" + sortDir + ", sortedBy=" + sortedBy + ", " + "filterValue=" + filterValue + ", range=" + range);
		ErrorResource error = null;
		List<Sort.Order> orders = new LinkedList<>();

		PageRequest pageRequest = null;
		String contentRange = null;

		SearchResultsOverviewTransfer[] searchResults = null;

		UserTransfer user = userResource.getUser();
		if(user == null) {
			error = new ErrorResource(ErrorResource.CANNOT_FETCH_ADMINOVERVIEWDATA);
		} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.KLANT_BEHEER)) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_ADMINOVERVIEWDATA);
			} else {
				if(sortedBy != null && !sortedBy.equals("")) {
					Order order;
					Direction sortDirection = null;

					if(sortDir != null) {
						if(sortDir.equals("ASC")) sortDirection = Sort.Direction.ASC;
						else if(sortDir.equals("DESC")) sortDirection = Sort.Direction.DESC;
					}

					if(sortDirection != null) order = new Order(Sort.Direction.ASC, sortedBy);
					else order = new Order(sortedBy);

					orders.add(order);
				}

				if(range != null && !range.equals("")) {
					String[] rangeParts = range.replace("items=", "").split("-");
					int from = new Integer(rangeParts[0]);
					int to = new Integer(rangeParts[1]);
					int size = to - from;
					int page = to / size - 1;

					if(orders.size() > 0) pageRequest = new PageRequest(page, size + 1, new Sort(orders));
					else pageRequest = new PageRequest(page, size + 1);

					try {
						// if vermelder = true find DOOR vermeldingen else find VAN vermeldingen
						PageTransfer<SearchResultsOverviewTransfer> searchResultsTransfer = bedrijfService.findSearchResults(filterValue, vermelder, pageRequest);

						if(searchResultsTransfer != null) {
							long count = searchResultsTransfer.getTotalElements();

							contentRange = "items " + from + "-" + Math.min(to, count) + "/" + count;

							if(searchResultsTransfer.getContent() != null)
								searchResults = searchResultsTransfer.getContent().toArray(new SearchResultsOverviewTransfer[searchResultsTransfer.getContent().size()]);

						} else {
							LOGGER.debug("null result in fetching search results");
						}

					} catch(ServiceException e) {
						LOGGER.error(e.getMessage());

						error = new ErrorResource(ErrorResource.CANNOT_FETCH_ADMINOVERVIEWDATA);
					}
				} else {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_ADMINOVERVIEWDATA);
				}
			}
		}

		if(pageRequest != null) {
			System.out.println("pageRequest " + contentRange + ", page=" + pageRequest.getPageNumber() + ", size=" + pageRequest.getPageSize() + ", offset=" +
					pageRequest.getOffset());
		}

		if(error != null) {return Response.ok(error).header("Content-Range", contentRange).build();} else {
			return Response.ok(searchResults).header("Content-Range", contentRange).build();
		}
	}

	/**
	 * Retrieves the websiteparam settings.
	 *
	 * @return A transfer containing the websiteparam values.
	 */
	@GET
	@Path("websiteparam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response websiteparam() {
		LOGGER.info("websiteparam method in DashboardResource.");
		ErrorResource error = null;
		WebsiteparamTransfer websiteparamTransfer = null;

		UserTransfer user = userResource.getUser();

		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_WEBSITEPARAM);} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.WEBSITEPARAM)) {
				error = new ErrorResource(ErrorResource.CANNOT_FETCH_WEBSITEPARAM);
			} else {
				try {
					websiteparamTransfer = bedrijfService.fetchWebsiteparam();

					if(websiteparamTransfer == null) error = new ErrorResource(ErrorResource.CANNOT_FETCH_WEBSITEPARAM);
				} catch(ServiceException e) {
					error = new ErrorResource(ErrorResource.CANNOT_FETCH_WEBSITEPARAM);
				}
			}
		}

		if(error != null) { return Response.ok(error).build();} else {
			return Response.ok(websiteparamTransfer).build();
		}
	}
}