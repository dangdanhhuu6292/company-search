package nl.devoorkant.sbdr.business.transfer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODataDateTimeAdapter extends XmlAdapter<String, Date> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ODataDateTimeAdapter.class);

    // the desired format
    //private String pattern = "'datetime'''yyyy-MM-dd'T'HH:mm:ss''";
	private String patterndate = "yyyy-MM-dd'T'HH:mm:ss";
	private String regexpdate = "/DATE\\((\\w+)\\)/"; //"/DATE\\((\\w+)\\)/"; // "/DATE\\(\\w+\\)/"; // "\\/DATE\\(\\w+\\)\\/"; //"date\\([0-9]+\\)";

    public String marshal(Date date) throws Exception {

		return new SimpleDateFormat(patterndate).format(date);
    }

    public Date unmarshal(String dateString) throws Exception {
    	Date date = null;
    	
    	try {
	    	if (dateString != null) {
	    		Pattern pattern = Pattern.compile(regexpdate);
	    		Matcher matcher = pattern.matcher(dateString.toUpperCase());
	    		//LOGGER.info("Input String matches regex - "+matcher.matches());
	    		
	    		if (matcher.find()) {
		    		String longdate = matcher.group(1);
		    		if (longdate != null)
		    			date = new Date(Long.parseLong(longdate));		    			
	    		}
	    		
	    		if (date == null)
	    			date = new SimpleDateFormat(patterndate).parse(dateString.toUpperCase());
	    	}
    	} catch (Exception e) {
    		LOGGER.error("ODataDateTimeAdapter: Cannot convert: " + dateString + " to date.");
    	}
    	
        return date;
    }   
}
