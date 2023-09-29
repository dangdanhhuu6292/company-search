package nl.devoorkant.kvk.converter;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.data.model.CompanyInfo;

/**
 * CIConverter, used for converting CompanyInfo Objects to SBDR Objects.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

public class CSConverter {
	//private static String regexpcommentary_employees = "De onderneming heeft ([0-9]+) werknemers?\\."; // De onderneming heeft 4 werknemers.
	//private static String regexpcsid = "NL[0-9]{3}\\/[A-Z]\\/(\\w+)"; // NL007/X/380247390000
	//private static String regexphuisnummer = "([\\d]+)([\\s]*)([\\W]*)(.*)"; // 4 groups: housenr, spacing opt., other chars opt., suffix opt.
	private static final Logger ioLogger = LoggerFactory.getLogger(CSConverter.class);

	/**
	 * Constructs a CSConverter.
	 */
	public CSConverter() {
	}
	public static List<CompanyInfo> transformToCompanyInfoList(JSONObject searchResults) {
		
		
		return null;
		
	}

	

}
