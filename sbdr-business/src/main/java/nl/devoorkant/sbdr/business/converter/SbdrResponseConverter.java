package nl.devoorkant.sbdr.business.converter;

import org.springframework.stereotype.Component;

import nl.devoorkant.sbdr.business.exception.ConverterException;
import nl.devoorkant.sbdr.business.wrapper.HelloResponseWrapper;

@Component
public class SbdrResponseConverter {
	
    public String convertHelloResponse(HelloResponseWrapper responseWrapper) throws ConverterException {
        if (responseWrapper == null) {
            throw new IllegalArgumentException("Response wrapper is null");
        }

        	
        if (responseWrapper != null )
        {
        	return responseWrapper.getStatus() + " " + responseWrapper.getInformation();
        }
        else
        	return "no result";
    }	

}
