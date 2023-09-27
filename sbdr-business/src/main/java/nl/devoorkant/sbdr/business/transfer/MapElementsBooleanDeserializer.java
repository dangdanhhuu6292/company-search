package nl.devoorkant.sbdr.business.transfer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class MapElementsBooleanDeserializer extends JsonDeserializer<MapElementsBoolean> {
	Logger LOGGER = LoggerFactory.getLogger(MapElementsBooleanDeserializer.class);

    @Override
    public MapElementsBoolean deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        
        LOGGER.debug("Deserialize MapElementsBoolean 1: " );

        String key = node.get("key").asText();
        String value = node.get("value").asText();
        Boolean valueValue = null;
        if (value != null && value.length() > 0)
        	valueValue = Boolean.parseBoolean(value);        
        //Boolean value = (Boolean) ((BooleanNode) node.get("@value")).asBoolean();
        
        
        return new MapElementsBoolean(key, valueValue);
    }
}
