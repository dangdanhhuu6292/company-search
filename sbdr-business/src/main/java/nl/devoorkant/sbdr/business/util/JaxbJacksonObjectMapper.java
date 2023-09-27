package nl.devoorkant.sbdr.business.util;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

//import org.codehaus.jackson.map.AnnotationIntrospector;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class JaxbJacksonObjectMapper extends ObjectMapper {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JaxbJacksonObjectMapper() {
        super();

        final AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();

        //this.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
        this.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.getDeserializationConfig().withAppendedAnnotationIntrospector(introspector);
        this.getSerializationConfig().withAppendedAnnotationIntrospector(introspector);

    }
}
