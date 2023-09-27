package nl.devoorkant.sbdr.ws;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.transfer.ActivatieCodeTransfer;
import nl.devoorkant.sbdr.business.transfer.TokenTransfer;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.LoginData;
import nl.devoorkant.sbdr.ws.transfer.ResetPasswordData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource object, containing password forgetting and -reset functionality.
 * <p/>
 * EDO - Applicatie voor het verwerken van Export Documenten
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Carsten Houweling
 * @version         %I%
 */
@Component
@Path("/UserNsService/user")
public class UserNsResource {
    private static Logger LOGGER = LoggerFactory.getLogger(UserNsResource.class);

    @Autowired
    LoginResource loginResource;
    @Autowired
    GebruikerService gebruikerService;


    @POST
    @Path("forgotpassword")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(String userId) {
        ErrorResource error = null;

        try {
            ErrorService errorService = gebruikerService.forgotWachtwoord(userId);
            if (errorService != null)
                error = new ErrorResource(errorService.getErrorCode());

            if (error == null) {
                LOGGER.info("Password reset successfully requested for user: " + userId);
            }
        } catch(ServiceException e) {
            LOGGER.error(e.getMessage());
            error = new ErrorResource(ErrorResource.CANNOT_RESET_WACHTWOORD);
        }

        return Response.ok(error).build();        
    }

    @POST
    @Path("resetpassword")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(final ResetPasswordData resetPasswordData) {
        ErrorResource error = null;
        TokenTransfer tokenTransfer = null;

        try {
            ErrorService errorService = gebruikerService.resetWachtwoord(resetPasswordData.userId, resetPasswordData.activationId, resetPasswordData.newPassword);
            if (errorService != null)
                error = new ErrorResource(errorService.getErrorCode());

            if (error == null) {
                // Log in using the new password
                LoginData loginData = new LoginData();
                loginData.username = resetPasswordData.userId;
                loginData.password = resetPasswordData.newPassword;
                loginData.cntLoginAttempt = 0;

                tokenTransfer = loginResource.authenticate(loginData);
            }
        } catch(ServiceException e) {
            LOGGER.error(e.getMessage());
			error = new ErrorResource(ErrorResource.CANNOT_RESET_WACHTWOORD);
        }

        if (error != null)
            return Response.ok(error).build();
        else
            return Response.ok(tokenTransfer).build();
    }

    @POST
    @Path("activateuser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(final ResetPasswordData resetPasswordData) {
        ErrorResource error = null;
        TokenTransfer tokenTransfer = null;

        try {
            ErrorService errorService = gebruikerService.activateGebruiker(resetPasswordData.userId, resetPasswordData.activationId, resetPasswordData.newPassword);
            if (errorService != null)
                error = new ErrorResource(errorService.getErrorCode());

            if (error == null) {
                // Log in using the new password
                LoginData loginData = new LoginData();
                loginData.username = resetPasswordData.userId;
                loginData.password = resetPasswordData.newPassword;
                if (resetPasswordData.bedrijfId != null && !resetPasswordData.bedrijfId.equals(""))
                	loginData.bedrijfId = resetPasswordData.bedrijfId;
                else
                	loginData.bedrijfId = null;
                loginData.cntLoginAttempt = 0;

                tokenTransfer = loginResource.authenticate(loginData);
            }
        } catch(ServiceException e) {
            LOGGER.error(e.getMessage());
			error = new ErrorResource(ErrorResource.CANNOT_ACTIVATE_GEBRUIKER);
        }

        if (error != null)
            return Response.ok(error).build();
        else
            return Response.ok(tokenTransfer).build();
    }
    
    @POST
    @Path("findActivationCode")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findActivationCode(final ResetPasswordData resetPasswordData) {
        ErrorResource error = null;
        ActivatieCodeTransfer activatieCodeTransfer = null;
        
        try {
        	activatieCodeTransfer = gebruikerService.findActivatieCodeOfGebruiker(resetPasswordData.userId, resetPasswordData.activationId);
        
            if (activatieCodeTransfer == null)
                error = new ErrorResource(ErrorResource.CANNOT_ACTIVATE_GEBRUIKER);
        } catch(ServiceException e) {
            LOGGER.error(e.getMessage());
			error = new ErrorResource(ErrorResource.CANNOT_ACTIVATE_GEBRUIKER);
        }

        if (error != null)
            return Response.ok(error).build();
        else
            return Response.ok(activatieCodeTransfer).build();
    }    
}
