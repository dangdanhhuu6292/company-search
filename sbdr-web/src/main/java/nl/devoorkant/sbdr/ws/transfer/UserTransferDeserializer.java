package nl.devoorkant.sbdr.ws.transfer;

import nl.devoorkant.sbdr.business.transfer.MapAdapterBoolean;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class UserTransferDeserializer extends JsonDeserializer<UserTransfer> {
	Logger LOGGER = LoggerFactory.getLogger(UserTransferDeserializer.class);

	@Override
	public UserTransfer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);

		LOGGER.debug("Deserialize 1: ");

		String userName = node.get("userName").getTextValue();
		String functie = node.get("functie").getTextValue();
		String afdeling = node.get("afdeling").getTextValue();
		String emailAdres = node.get("emailAdres").getTextValue();
		//String firstName = node.get("firstName").getTextValue();
		//String lastName = node.get("lastName").getTextValue();
		String fullName = node.get("fullName").getTextValue();
		//String userIdValue = node.get("userId").getTextValue();
		Integer userId = ObfuscatorUtils.deofuscateInteger(node.get("userId").getTextValue());
		//if(userIdValue != null && userIdValue.length() > 0) userId = Integer.parseInt(userIdValue);
		//String bedrijfIdValue = node.get("bedrijfId").getTextValue();
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(node.get("bedrijfId").getTextValue());
		//if(bedrijfIdValue != null && bedrijfIdValue.length() > 0) bedrijfId = Integer.parseInt(bedrijfIdValue);
		String bedrijfNaam = node.get("bedrijfNaam").getTextValue();
		String klantValue = node.get("klant").getTextValue();
		boolean isKlant = false;
		if(klantValue != null && klantValue.length() > 0) isKlant = Boolean.parseBoolean(klantValue);
		String prospectValue = node.get("prospect").getTextValue();
		boolean isProspect = false;
		if(prospectValue != null && prospectValue.length() > 0) isProspect = Boolean.parseBoolean(prospectValue);
		String adresOkValue = node.get("adresOk").getTextValue();
		boolean isAdresOk = false;
		if(adresOkValue != null && adresOkValue.length() > 0) isAdresOk = Boolean.parseBoolean(adresOkValue);		
		String gebruikerTelefoon = node.get("gebruikerTelefoonNummer").getTextValue();
		String bedrijfTelefoon = node.get("bedrijfTelefoonNummer").getTextValue();
		String actionsPresentValue = node.get("actionsPresent").getTextValue();
		boolean isActionsPresent = false;
		if(actionsPresentValue != null && actionsPresentValue.length() > 0)
			isActionsPresent = Boolean.parseBoolean(actionsPresentValue);
		Integer showHelp = null;
		String showHelpValue = node.get("showHelp").getTextValue();
		if(showHelpValue != null && showHelpValue.length() > 0) userId = Integer.parseInt(showHelpValue);
		String mobileUserValue = node.get("mobileUser").getTextValue();
		boolean isMobileUser = false;
		if(mobileUserValue != null && mobileUserValue.length() > 0)
			isMobileUser = Boolean.parseBoolean(mobileUserValue);
		String bedrijfManagerValue = node.get("bedrijfManager").getTextValue();
		boolean isBedrijfManager = false;
		if(bedrijfManagerValue != null && bedrijfManagerValue.length() > 0)
			isBedrijfManager = Boolean.parseBoolean(bedrijfManagerValue);

		LOGGER.debug("Deserialize 2: " + node.get("roles").toString());
		String jsonRoles = node.get("roles").toString();

		Map<String, Boolean> roles = MapAdapterBoolean.fromString(jsonRoles);

		return new UserTransfer(userName, fullName, functie, afdeling, emailAdres, userId, bedrijfId, bedrijfNaam, gebruikerTelefoon, bedrijfTelefoon, isKlant, isProspect, isAdresOk, roles, isActionsPresent, showHelp, null, isMobileUser, isBedrijfManager);
	}
}
