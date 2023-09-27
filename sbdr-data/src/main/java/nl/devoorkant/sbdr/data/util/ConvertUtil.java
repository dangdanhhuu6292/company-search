package nl.devoorkant.sbdr.data.util;

import java.lang.reflect.InvocationTargetException;

import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.BedrijfHistorie;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertUtil {
	private static final Logger ioLogger = LoggerFactory.getLogger(ConvertUtil.class);

    public static Bedrijf copyBedrijf(Bedrijf source, Bedrijf dest) throws IllegalAccessException, InvocationTargetException {
        ioLogger.debug("Method copyBedrijf.");
        Bedrijf result = null;

        if (source == null)
        	result = new Bedrijf();
        else
        	result = dest;
        
        BeanUtils.copyProperties(dest, source);                    

        return result;
    }  
    
    public static BedrijfHistorie transformToBedrijfHistorie(Bedrijf bedrijf) throws IllegalAccessException, InvocationTargetException {
        ioLogger.debug("Method transformTotransformToBedrijfHistorie.");
        BedrijfHistorie result = null;

        if (bedrijf != null) {
            result = new BedrijfHistorie();
            
            BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
            BeanUtils.copyProperties(result, bedrijf);
			         
        }

        return result;
    }   
}
