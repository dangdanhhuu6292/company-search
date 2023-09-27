package nl.devoorkant.sbdr.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.devoorkant.sbdr.business.service.BedrijfService;
import nl.devoorkant.sbdr.data.util.EMeldingStatus;
import nl.devoorkant.sbdr.ws.transfer.CheckResult;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;


@Component
@Path("/api/v1")
public class ExternalResource {
	@Autowired
	BedrijfService bedrijfService;

	@Path("check")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkIfCodeIsValid(@QueryParam("kvknumber")String kvknumber){
		ErrorResource error = null;
		CheckResult val = null;
		if(kvknumber!=null){
			EMeldingStatus meldingStatus = bedrijfService.maxMeldingStatus(kvknumber);
			val = new CheckResult();
			if (meldingStatus == null) {
				val.result = "unknown";
				val.description="No registration information";
			} else if (meldingStatus == EMeldingStatus.INBEHANDELING) {
				val.result = "pending";
				val.description="Registration pending. Check again after 7 days";				
			} else if (meldingStatus == EMeldingStatus.ACTIEF) {
				val.result = "registration";
				val.description="Active registration";				
			}
		} else {
			error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
		}

		if(error!=null) return Response.ok(error).build();
		else return Response.ok(val).build();
	}

}

