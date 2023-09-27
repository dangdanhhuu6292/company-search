package nl.devoorkant.sbdr.idobfuscator.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;

public class BeanCopyUtils {
	private static IntegerConverter integerConverter = new IntegerConverter(null);
	private static DateConverter dateConverter = new DateConverter(null);
	
	private static BeanUtilsBean beanUtilsBean = null;
	
	public static void copyProperties(Object dst, Object src) throws IllegalAccessException, InvocationTargetException {
		if (beanUtilsBean == null) {
			beanUtilsBean = new BeanUtilsBean();
			beanUtilsBean.getConvertUtils().register(integerConverter, Integer.class);
			beanUtilsBean.getConvertUtils().register(dateConverter, Date.class);
		}
		beanUtilsBean.copyProperties(dst, src);
	}
}
