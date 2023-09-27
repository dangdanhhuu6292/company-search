package nl.devoorkant.sbdr.ws.transfer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;

import java.io.IOException;

public class UserAccountDeserializer extends UserTransferDeserializer {
	Logger LOGGER = LoggerFactory.getLogger(UserAccountDeserializer.class);

	//@Override
	public UserAccount deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);

		LOGGER.debug("Deserialize 1: ");

		UserTransfer userTransfer = super.deserialize(jp, ctxt);
		//String bedrijfIdValue = node.get("bedrijfId").getTextValue();
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(node.get("bedrijfId").getTextValue());
		//if(bedrijfIdValue != null && bedrijfIdValue.length() > 0) bedrijfId = Integer.parseInt(bedrijfIdValue);
		String wachtwoord = node.get("wachtwoord").getTextValue();
		String voornaam = node.get("voornaam").getTextValue();
		String naam = node.get("naam").getTextValue();
		String emailAdres = node.get("emailAdres").getTextValue();
		String gebruikerTelefoon = node.get("gebruikerTelefoonNummer").getTextValue();
		String bedrijfTelefoon = node.get("bedrijfTelefoonNummer").getTextValue();
		String functie = node.get("functie").getTextValue();
		String afdeling = node.get("afdeling").getTextValue();
		String actionsPresentValue = node.get("actionsPresent").getTextValue();
		boolean isActionsPresent = false;
		if(actionsPresentValue != null && actionsPresentValue.length() > 0)
			isActionsPresent = Boolean.parseBoolean(actionsPresentValue);
		String mobileUserValue = node.get("mobileUser").getTextValue();
		boolean isMobileUser = false;
		if(mobileUserValue != null && mobileUserValue.length() > 0)
			isMobileUser = Boolean.parseBoolean(mobileUserValue);
		String bedrijfManagerValue = node.get("bedrijfManager").getTextValue();
		boolean isBedrijfManager = false;
		if(bedrijfManagerValue != null && bedrijfManagerValue.length() > 0)
			isBedrijfManager = Boolean.parseBoolean(bedrijfManagerValue);

		return new UserAccount(bedrijfId, userTransfer.getBedrijfNaam(), userTransfer.getUserId(), userTransfer.getUserName(), wachtwoord, voornaam, naam, functie, afdeling, emailAdres, gebruikerTelefoon, bedrijfTelefoon, userTransfer.getKlant(), userTransfer.getProspect(), userTransfer.getAdresOk(), userTransfer.getRoles(), isActionsPresent, userTransfer.getShowHelp(), null, isMobileUser, isBedrijfManager);
	}
}
