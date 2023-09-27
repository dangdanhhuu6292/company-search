package nl.devoorkant.sbdr.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.devoorkant.sbdr.business.service.BedrijfService;
import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.service.WebTokenService;
import nl.devoorkant.sbdr.business.transfer.GebruikersDetails;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import nl.devoorkant.sbdr.data.util.ERol;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.oauth.StoreJwtTokenStore;
import nl.devoorkant.sbdr.ws.auth.GebruikersDetailsService;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.GebruikerIdsTransfer;
import nl.devoorkant.sbdr.ws.transfer.UserAccount;
import nl.devoorkant.sbdr.ws.transfer.UserTransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.stereotype.Component;

@Component
@Path("/GebruikerService/gebruiker")
public class GebruikerResource {
    @Value("${api.client_id}")
    private String apiClientId;
    
    @Resource(name = "tokenStore")
    StoreJwtTokenStore tokenStore;
    
	@Autowired
	private GebruikerService gebruikerService;
	@Autowired
	private BedrijfService bedrijfService;
	@Autowired
	private GebruikersDetailsService gebruikersDetailsService;
	@Autowired
	private UserResource userResource;
	private static Logger LOGGER = LoggerFactory.getLogger(GebruikerResource.class);
	@Autowired
	GebruikerService gebruikersService;
	@Autowired
	WebTokenService webTokenService;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(final UserAccount userAccount) {
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		LOGGER.info("create(): " + userAccount);

		try {
			if(userAccount != null &&
				((bedrijfService.isBedrijfOfKlant(userAccount.getBedrijfId()) && gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.KLANTGEBRUIKER_BEHEER)) ||
				(!bedrijfService.isBedrijfOfKlant(userAccount.getBedrijfId()) && gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDRGEBRUIKER_BEHEER)))) {
		
	
				Gebruiker gebruiker = new Gebruiker();
	
				gebruiker.setNaam(userAccount.getNaam());
				gebruiker.setVoornaam(userAccount.getVoornaam());
				gebruiker.setEmailAdres(userAccount.getEmailAdres());
				gebruiker.setBedrijfBedrijfId(userAccount.getBedrijfId());
				gebruiker.setFunctie(userAccount.getFunctie());
				gebruiker.setAfdeling(userAccount.getAfdeling());
				gebruiker.setTelefoonNummer(userAccount.getGebruikerTelefoonNummer()); // actually phone of user
				gebruiker.setActief(true);
				gebruiker.setGeslacht(userAccount.getGeslacht());
	
				gebruiker.setGebruikersNaam(userAccount.getUserName());
				// User password will be set on activation
				Wachtwoord wachtwoord = null; // new Wachtwoord();
				//wachtwoord.setWachtwoord(userAccount.getWachtwoord());
	
				boolean isBedrijfManaged = false;
				
				Map<String, Boolean> rollen = null;
				if(userAccount.getRoles() != null) {
					rollen = new HashMap<String, Boolean>();
	
					Boolean auth = (Boolean) userAccount.getRoles().get("admin_klant");
					if(auth != null) rollen.put(ERol.KLANT.getCode(), auth); // ROLE_KLANT
	
					// If KLANT Company create KLANT user
					if (bedrijfService.isBedrijfOfKlant(userAccount.getBedrijfId())) {
						auth = (Boolean) userAccount.getRoles().get("hoofd_klant");
						if(auth != null) rollen.put(ERol.HOOFD.getCode(), auth); // ROLE_HOOFD
		
						auth = (Boolean) userAccount.getRoles().get("gebruiker_klant");
						if(auth != null) rollen.put(ERol.GEBRUIKER.getCode(), auth); // ROLE_USER
						
						auth = (Boolean) userAccount.getRoles().get("bedrijf_manager");
						if(auth != null) {
							rollen.put(ERol.MANAGED.getCode(), auth); // ROLE_MANAGED	
							isBedrijfManaged = true;
						}
					} // else if SBDR Company create SBDR user
					else {
						auth = (Boolean) userAccount.getRoles().get("gebruiker_klant"); // "admin_sbdr"
						if(auth != null) rollen.put(ERol.SBDR.getCode(), auth); // ROLE_SBDR
		
						auth = (Boolean) userAccount.getRoles().get("hoofd_klant"); // "admin_sbdr_hoofd"
						if(auth != null) rollen.put(ERol.SBDRHOOFD.getCode(), auth); // ROLE_SBDR_HOOFD
					}
					
					auth = (Boolean) userAccount.getRoles().get("registraties_toegestaan"); // "registratiesToegestaan"
					// Add registraties toegestaan always for KLANT and SBDR main users
					if(auth != null || rollen.containsKey(ERol.KLANT.getCode()) || rollen.containsKey(ERol.SBDR.getCode())) rollen.put(ERol.REGISTRATIESTOEGESTAAN.getCode(), auth); // ROLE_REGISTRATIESTOEGESTAAN
					
					// Add api toegestaan for GEBRUIKER users if SBDR user is creating the user!
					if (gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDRGEBRUIKER_BEHEER)) {
						auth = (Boolean) userAccount.getRoles().get("api_toegestaan");
						if (auth != null || rollen.containsKey(ERol.GEBRUIKER.getCode())) rollen.put(ERol.APITOEGESTAAN.getCode(), auth);
					}
				}
	
				// MBR 8-12-2017 Cannot create account with managed bedrijf. Yes you can!
				ErrorService errorservice = gebruikersService.saveGebruiker(gebruiker, isBedrijfManaged, rollen, wachtwoord);
				if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());

			} else error = new ErrorResource(ErrorResource.CANNOT_CREATE_GEBRUIKER);
		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_GEBRUIKER);
		} catch(Exception e) {
			LOGGER.debug("Wrong exception: " + e.getMessage());
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_GEBRUIKER);
		}
				
		return Response.ok(error).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response delete(@PathParam("id") String obfId) {
		Integer id = ObfuscatorUtils.deofuscateInteger(obfId);
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		LOGGER.info("gebruiker delete(id)");

		try {
			if((gebruikerService.isGebruikerOfKlant(id, user.getBedrijfId()) && gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.KLANTGEBRUIKER_BEHEER)) ||
				(!gebruikerService.isGebruikerOfKlant(id, user.getBedrijfId()) && gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDRGEBRUIKER_BEHEER))) {
				Gebruiker gebruiker = gebruikerService.findByGebruikerId(id);
				String gebruikerNaam = gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.API_TOEGANG) == true ? gebruiker.getGebruikersNaam() : null;
				ErrorService errorservice = gebruikersService.deleteGebruiker(id, user.getBedrijfId());
				
				if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
				else if (gebruikerNaam != null) {
					Collection<Approval> approvals = tokenStore.jdbcApprovalStore().getApprovals(gebruikerNaam, apiClientId);
		    		
		    		if (approvals != null && approvals.size() > 0)
		    			tokenStore.jdbcApprovalStore().revokeApprovals(approvals);										
				}
			}
		} catch(ServiceException e) {
			LOGGER.error("Error deleting Gebruiker with id: " + id + " " + e.getMessage());
			error = new ErrorResource(ErrorResource.ERROR_DELETE_GEBRUIKER);
		} catch(Exception e) {
			error = new ErrorResource(ErrorResource.ERROR_DELETE_GEBRUIKER);
		}


		return Response.ok(error).build();
	}

	@Path("sbdruser")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSbdrUser(){
		try{
			Gebruiker g =gebruikerService.findSbdrGebruiker();

			boolean isBedrijfManager = g.getBedrijvenManagedDoorGebruikerId() != null && !g.getBedrijvenManagedDoorGebruikerId().isEmpty();
			UserTransfer userTr =  new UserTransfer(g.getGebruikersNaam(), (g.getVoornaam() + " " + g.getNaam()), g.getFunctie(), g.getAfdeling(), g.getEmailAdres(), g.getGebruikerId(), g.getBedrijfBedrijfId(), null, null, null, false, false, true, null, false, g.getShowHelp(), g.getGeslacht(), false, isBedrijfManager);

			return Response.ok(userTr).build();
		} catch(Exception e){
			return Response.ok(new ErrorResource(ErrorResource.CANNOT_FETCH_GEBRUIKERS)).build();
		}
	}

	/**
	 * Retrieves a user for admin purpose.
	 *
	 * @return A transfer containing the user data.
	 */
	@POST
	@Path("gebruikerdata")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response gebruikerData(final GebruikerIdsTransfer gebruikerIds) {		
		ErrorResource errorResource = null;
		List<UserTransfer> userTransfers = null;

		UserTransfer user = userResource.getUser();
		boolean sbdrUser = false;

		if(user != null) {
			Integer gebruikerId = user.getUserId();

			if(gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.KLANT_BEHEER)) // sbdr gebruiker
			{
				sbdrUser = true;
			}

		}

		if(errorResource == null && gebruikerIds != null) {
			try {
				userTransfers = new ArrayList<UserTransfer>();

				UserTransfer userTransfer = null;
				// null objects are allowed!!
				for(String obfGebruikerIdstr : gebruikerIds.getGebruikerIds()) {
					userTransfer = null;
					if(obfGebruikerIdstr != null && !obfGebruikerIdstr.equals("null")) {
						Integer gebruikerId = ObfuscatorUtils.deofuscateInteger(obfGebruikerIdstr);
						// link user to company of user
						GebruikersDetails userDetails = null;
						if (!sbdrUser)
							userDetails = gebruikersDetailsService.findById(gebruikerId, user.getBedrijfId(), false);
						else
							userDetails = gebruikersDetailsService.findById(gebruikerId, null, false);

						if(userDetails != null) {
							userTransfer = new UserTransfer(userDetails.getUsername(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getFunctie(), userDetails.getAfdeling(), userDetails.getEmailAdres(), userDetails.getUserId(), userDetails.getBedrijfId(), userDetails.getBedrijfNaam(), userDetails.getGebruikerTelefoonNummer(), userDetails.getBedrijfTelefoonNummer(), userDetails.isKlant(), userDetails.isProspect(), userDetails.isAdresOk(), userResource.createRoleMapSpring(userDetails), userDetails.isActionsPresent(), userDetails.getShowHelp(), userDetails.getGeslacht(), userDetails.getMobileClientKey() == null ? false : true, userDetails.isBedrijfManager());

							// Only SBDR users + users of own company may see user details of user + SBDR employee in usertransfer
							if(sbdrUser || (userTransfer.getFunctie() != null && userTransfer.getFunctie().equals("Beheer")) || (user.getBedrijfId().equals(userDetails.getBedrijfId()) || isSbdrEmployee(userTransfer.getRoles()))) {
								userTransfers.add(userTransfer);
							}
						}
					}
				}
			} catch(Exception e) {
				errorResource = new ErrorResource(ErrorResource.CANNOT_FETCH_GEBRUIKERS);
			}
		}

		if(errorResource != null) {
			return Response.ok(errorResource).build();
		} else {
			if(userTransfers != null) {
				if(userTransfers.size() == 0) {
					return Response.ok().build();
				} else if(userTransfers.size() > 0) {
					UserTransfer[] userTransfersArray = userTransfers.toArray(new UserTransfer[0]);
					return Response.ok(userTransfersArray).build();
				} else {
					errorResource = new ErrorResource(ErrorResource.CANNOT_FETCH_GEBRUIKERS);
					return Response.ok(errorResource).build();
				}
			} else {
				errorResource = new ErrorResource(ErrorResource.CANNOT_FETCH_GEBRUIKERS);
				return Response.ok(errorResource).build();
			}
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//@Path("{id}")
	public Response update(UserAccount userAccount) {
		ErrorResource error = null;

		UserTransfer user = userResource.getUser();

		LOGGER.info("update(): " + userAccount);

		try {
			if(userAccount != null &&
				((bedrijfService.isBedrijfOfKlant(userAccount.getBedrijfId()) && gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.KLANTGEBRUIKER_BEHEER)) ||
				(!bedrijfService.isBedrijfOfKlant(userAccount.getBedrijfId()) && gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.SBDRGEBRUIKER_BEHEER)))) {

				if(userAccount.getRoles()!=null){
					if(userAccount.getRoles().containsKey("hoofd_klant")&&userAccount.getRoles().get("hoofd_klant")&&!userAccount.isVerantwoordelijkheidAkkoord()){
						error = new ErrorResource(ErrorResource.VERANTWOORDELIJKHEID_NIET_AKKOORD);
					}
				}

				if(error==null){
					Gebruiker gebruiker = new Gebruiker();

					gebruiker.setVoornaam(userAccount.getVoornaam());
					gebruiker.setNaam(userAccount.getNaam());

					gebruiker.setGebruikerId(userAccount.getUserId());
					gebruiker.setEmailAdres(userAccount.getEmailAdres());
					gebruiker.setBedrijfBedrijfId(userAccount.getBedrijfId());
					gebruiker.setFunctie(userAccount.getFunctie());
					gebruiker.setAfdeling(userAccount.getAfdeling());
					gebruiker.setTelefoonNummer(userAccount.getGebruikerTelefoonNummer()); // actually phone of user
					gebruiker.setActief(true);
					gebruiker.setGeslacht(userAccount.getGeslacht());

					boolean isBedrijfManaged = false;
					
					Map<String, Boolean> rollen = null;
					if(userAccount.getRoles() != null) {
						rollen = new HashMap<String, Boolean>();

						Boolean auth = (Boolean) userAccount.getRoles().get("admin_klant");
						if(auth != null) rollen.put(ERol.KLANT.getCode(), auth); // ROLE_KLANT

						// If KLANT Company update KLANT user
						if (bedrijfService.isBedrijfOfKlant(userAccount.getBedrijfId())) {
							auth = (Boolean) userAccount.getRoles().get("hoofd_klant");
							if(auth != null) rollen.put(ERol.HOOFD.getCode(), auth); // ROLE_HOOFD

							auth = (Boolean) userAccount.getRoles().get("gebruiker_klant");
							if(auth != null) rollen.put(ERol.GEBRUIKER.getCode(), auth); // ROLE_USER
							
							auth = (Boolean) userAccount.getRoles().get("bedrijf_manager");
							if(auth != null) {
								rollen.put(ERol.MANAGED.getCode(), auth); // ROLE_MANAGED	
								isBedrijfManaged = true;
							}
						} // else if SBDR Company update SBDR user
						else {
							auth = (Boolean) userAccount.getRoles().get("admin_sbdr");
							if(auth != null) rollen.put(ERol.SBDR.getCode(), auth); // ROLE_SBDR

							auth = (Boolean) userAccount.getRoles().get("admin_sbdr_hoofd");
							if(auth != null) rollen.put(ERol.SBDRHOOFD.getCode(), auth); // ROLE_SBDR_HOOFD
						}
						
						auth = (Boolean) userAccount.getRoles().get("registraties_toegestaan"); // "registratiesToegestaan"
						if(auth != null) rollen.put(ERol.REGISTRATIESTOEGESTAAN.getCode(), auth); // ROLE_REGISTRATIESTOEGESTAAN
						
						// Add api toegestaan for GEBRUIKER users if SBDR user is creating the user!
						if (gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDRGEBRUIKER_BEHEER)) {
							auth = (Boolean) userAccount.getRoles().get("api_toegestaan");
							if (auth != null || rollen.containsKey(ERol.GEBRUIKER.getCode())) rollen.put(ERol.APITOEGESTAAN.getCode(), auth);
						}						
					}

					// set gebruikersnaam, may be changed!
					gebruiker.setGebruikersNaam(userAccount.getUserName());
					Wachtwoord wachtwoord = new Wachtwoord();
					wachtwoord.setWachtwoord(userAccount.getWachtwoord());
					
					ErrorService errorservice = gebruikersService.saveGebruiker(gebruiker, isBedrijfManaged, rollen, wachtwoord);
										
					if(errorservice != null) error = new ErrorResource(errorservice.getErrorCode());
					else {
						// User email address is to be updated!!
						//if (user.getUserId().equals(userAccount.getUserId()) && !user.getEmailAdres().equals(userAccount.getEmailAdres())) {
						//	webTokenService.connectWebTokenToNewUser(user.getEmailAdres(), userAccount.getEmailAdres());
						//}		
							
						if (!rollen.containsKey(ERol.APITOEGESTAAN.getCode())) {
							Collection<Approval> approvals = tokenStore.jdbcApprovalStore().getApprovals(userAccount.getUserName(), apiClientId);
				    		
				    		if (approvals != null && approvals.size() > 0)
				    			tokenStore.jdbcApprovalStore().revokeApprovals(approvals);										
						}
						
					}
				}
			} else error = new ErrorResource(ErrorResource.INCOMPLETE_GEBRUIKER_DATA);
		} catch(ServiceException e) {
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_GEBRUIKER);
		} catch(Exception e) {
			LOGGER.debug("Wrong exception: " + e.getMessage());
			error = new ErrorResource(ErrorResource.CANNOT_CREATE_GEBRUIKER);
		}

		return Response.ok(error).build();
	}

	private boolean isSbdrEmployee(Map<String, Boolean> roles) {
		boolean result = false;

		Boolean auth = (Boolean) (roles.get(ERol.SBDR.getCode()) || roles.get(ERol.SBDRHOOFD.getCode()));
		Boolean auth2 = (Boolean) (roles.get("admin_sbdr") || roles.get("admin_sbdr_hoofd"));

		if(auth != null) result = auth;
		else if(auth2 != null) result = auth2;

		return result;
	}
}
