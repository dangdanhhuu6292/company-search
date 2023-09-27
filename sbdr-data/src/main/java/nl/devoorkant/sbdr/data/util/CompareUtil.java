package nl.devoorkant.sbdr.data.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.data.model.Bedrijf;

public class CompareUtil {
	private static final Logger ioLogger = LoggerFactory.getLogger(CompareUtil.class);

	public static boolean compareBedrijf(Bedrijf one, Bedrijf two) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	boolean result = false;
    	BeanMap map = new BeanMap(one);

        PropertyUtilsBean propUtils = new PropertyUtilsBean();

        for (Object propNameObject : map.keySet()) {
            String propertyName = (String) propNameObject;
            ioLogger.info("check  " + propertyName);
            
            if (!propertyName.equals("bedrijfId") && !propertyName.equals("datumWijziging") && !propertyName.equals("bedrijfStatus") && !propertyName.equals("isAdresOk") && !propertyName.equals("isHandmatigGewijzigd"))
            {
	            Object property1 = propUtils.getProperty(one, propertyName);
	            Object property2 = propUtils.getProperty(two, propertyName);
	
	            try {
		            if ((property1 == null && property2 == null) || (property1 != null && property1.equals(property2)))
		            {
		            	ioLogger.debug("  " + propertyName + " is equal");
		                result = true;
		            } else {
		            	ioLogger.debug("> " + propertyName + " is different (oldValue=\"" + property1 + "\", newValue=\"" + property2 + "\")");	 
		            	result = false;
		            	break;
		            }
	            } catch (org.hibernate.LazyInitializationException e) {
	            	result = true;
	            }
            }
        }

        return result;
    }
}
