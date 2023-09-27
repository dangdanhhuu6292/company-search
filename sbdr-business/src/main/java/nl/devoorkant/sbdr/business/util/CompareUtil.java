package nl.devoorkant.sbdr.business.util;

import java.util.Set;

import nl.devoorkant.sbdr.data.model.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompareUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(CompareUtil.class);

	public static boolean equalsInteger(Integer val1, Integer val2) {
		if ((val1 == null && val2 != null) || (val1 != null && val2 == null))
			return false;
		
		if (val1 != null && val2 != null) {
			return val1.equals(val2);
		} else 
			return true;
	}
	
	public static boolean equalsString(String str1, String str2) {
		if ((str1 == null && str2 != null) || (str1 != null && str2 == null))
			return false;
		
		if (str1 != null && str2 != null) {
			return str1.equals(str2);
		} else 
			return true;
	}
	
	private static boolean equalsRol(Rol rol1, Rol rol2) {
		// No bevoegdheden check !!!
		
		if (rol1 == null && rol2 == null)
			return false;
		
		if (rol1 == null || rol2 == null)
			return false;
		
		if (rol1.isActief() != rol2.isActief())
			return false;
		
		if (rol1.getCode() == null ? rol2.getCode() != null : !rol1.getCode().equals(rol2.getCode()))
			return false;

		if (rol1.getOmschrijving() == null ? rol2.getOmschrijving() != null : !rol1.getOmschrijving().equals(rol2.getOmschrijving()))
			return false;

		return true;

	}
	
	public static boolean containsRol(Set<Rol> rollen, Rol rol) {
		LOGGER.debug("Comparing roles...");
		boolean result = false;
		
		if (rollen == null)
			return false;
		
		for (Rol rolcheck : rollen) {
			LOGGER.debug("Role in set: " + rolcheck.getCode() + ", role: " + rol.getCode());
			if (equalsRol(rolcheck, rol)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
}
