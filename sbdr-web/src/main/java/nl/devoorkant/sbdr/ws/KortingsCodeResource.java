package nl.devoorkant.sbdr.ws;

import nl.devoorkant.sbdr.business.service.KortingsCodeService;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.GenericBooleanTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Component
@Path("/KortingsCodeService/kortingscode")
public class KortingsCodeResource {
	@Autowired
	private KortingsCodeService kortingsCodeService;

	@Path("checkIfCodeIsValid")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkIfCodeIsValid(@QueryParam("code")String code){
		ErrorResource error = null;
		GenericBooleanTransfer val = null;
		if(code!=null){
			try{
				val = new GenericBooleanTransfer(kortingsCodeService.checkIfCodeIsValid(code, new Date()));
			} catch(Exception e){
				error = new ErrorResource(ErrorResource.ERROR_DISCOUNT);
			}
		} else {
			error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
		}

		if(error!=null) return Response.ok(error).build();
		else return Response.ok(val).build();
	}

	@Path("checkIfCodeIsExpired")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkIfCodeIsExpired(@QueryParam("code")String code){
		ErrorResource error = null;
		GenericBooleanTransfer val = null;
		if(code!=null){
			try{
				val = new GenericBooleanTransfer(kortingsCodeService.checkIfCodeIsExpired(code, new Date()));
			} catch(Exception e){
				error = new ErrorResource(ErrorResource.ERROR_DISCOUNT);
			}
		} else {
			error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
		}

		if(error!=null) return Response.ok(error).build();
		else return Response.ok(val).build();
	}
}
