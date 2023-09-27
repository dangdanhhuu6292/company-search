package nl.devoorkant.sbdr.business.transfer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class MapAdapterBoolean extends XmlAdapter<AdaptedMapElementsBoolean, Map<String, Boolean>> {
	private static Logger LOGGER = LoggerFactory.getLogger(MapAdapterBoolean.class);
	
    public MapAdapterBoolean() {
    }

    public AdaptedMapElementsBoolean marshal(Map<String, Boolean> arg0) throws Exception {
		if(arg0!=null) {
			AdaptedMapElementsBoolean adaptedMap = new AdaptedMapElementsBoolean();
			
			LOGGER.debug("MapElements size Map: " + arg0.size());
			for(Map.Entry<String, Boolean> entry : arg0.entrySet()) {
				LOGGER.debug("MapElements entry: " + entry.getKey() + " value: " + entry.getValue());

				adaptedMap.item.add(new MapElementsBoolean(entry.getKey(), entry.getValue()));
			}

			return adaptedMap;
		} else {
			return null;
		}
    }

    public Map<String, Boolean> unmarshal(AdaptedMapElementsBoolean arg0) throws Exception {
        Map<String, Boolean> r = new HashMap<String, Boolean>();
        if (arg0 != null) {
	        LOGGER.debug("MapElements size MapElements: " + arg0.item.size());
	        for (MapElementsBoolean mapelement : arg0.item)
	        {
	            LOGGER.debug("MapElements mapelement: " + mapelement.key + " value: " + mapelement.value);
	        	
	        	r.put(mapelement.key, mapelement.value);
	        }
        }
        
        return r;
    }
    
	/**
	 * Deserializes an Object of class Map<String, Boolean> from its JSON representation
	 */
	public static Map<String, Boolean> fromString(String jsonRepresentation) {
		ObjectMapper mapper = new ObjectMapper(); // Jackson's JSON marshaller
		Map<String, Boolean> result = null;
		List<MapElementsBoolean> o = null;
		MapElementsBoolean p = null;
		
		LOGGER.debug("Deserialize Map 1");
		
		if (jsonRepresentation != null) {
			// MBR 05-10-2014 Jackson 1.9.13
			//TypeFactory typeFactory = mapper.getTypeFactory();
			//CollectionType colType = typeFactory.constructCollectionType(List.class, MapElementsBoolean.class);
			
			// MBR 05-10-2014 Jackson 1.7.1
			TypeFactory typeFactory = mapper.getTypeFactory();
			JavaType colType = typeFactory.constructCollectionType(List.class, MapElementsBoolean.class);
			
			LOGGER.debug("Deserialize Map 2");
			try {
				LOGGER.debug("TEST 1");
				
				JsonNode item = mapper.readTree(jsonRepresentation);
				JsonNode map = item.get("item");
				
				String mapObject = item.get("item").asText();
				mapObject = item.get("item").asText();
				
				Map<String, Object> mapObject2 = mapper.readValue(jsonRepresentation.toString(), new TypeReference<Map<String, Object>>() {
					
				});
				
				LOGGER.debug("TEST 2: " + map.toString());
				
				if (!map.isArray())
				{
					p = mapper.readValue(map.toString(), MapElementsBoolean.class);
					
					LOGGER.debug("Deserialize Map 3: " + p);
					
					result = new TreeMap<String, Boolean>();
					
					result.put(p.key, p.value);
				}
				else
				{
					o = mapper.readValue(map.toString(), colType);
									
					
					
					result = new TreeMap<String, Boolean>();
			        LOGGER.debug("MapElements size MapElements: " + o.size());
			        for (MapElementsBoolean mapelement : o)
			        {
			            LOGGER.debug("MapElements mapelement: " + mapelement.key + " value: " + mapelement.value);
			        	
			        	result.put(mapelement.key, mapelement.value);
			        }
				}
		        
			} catch (IOException e) {
				LOGGER.debug("TEST");
				LOGGER.error(e.getMessage());
				throw new WebApplicationException();
			} catch (Exception e) {
				LOGGER.debug("TEST");
				LOGGER.error(e.getMessage());
				throw new WebApplicationException();
			}
		}
		
		return result;
	}    
}
