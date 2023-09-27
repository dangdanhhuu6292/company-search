package nl.devoorkant.sbdr.data.util;

import nl.devoorkant.util.StringUtil;

public class SearchUtil {

	public static String abrNumber(String nummer) {
		String result = null;

		if(nummer != null && nummer.length() > 5) {
			if(nummer.toUpperCase().startsWith(EDocumentType.AANMELDBRIEF.getPrefix()))
				result = nummer.substring(EDocumentType.AANMELDBRIEF.getPrefix().length());
		}
		return result;

	}

	public static String convertDbSearchString(String search) {
		String regex = "[\\[\\]%_]";

		return search.replaceAll(regex, " ");
	}

	public static boolean isAlfanumeric(String nummer) {
		return nummer != null && !isNumeric(nummer);

	}

	public static boolean isFullKvKNumber(String kvKNo) {
		return StringUtil.isNotEmptyOrNull(kvKNo) && kvKNo.length() == 12 && kvKNo.matches("[0-9]+");
	}

	public static boolean isKvKNumber(String kvkNumber) {
		// 8 = plain kvk; 12 = CI kvk
		return StringUtil.isNotEmptyOrNull(kvkNumber) && (kvkNumber.length() == 8 || kvkNumber.length() == 12) && kvkNumber.matches("[0-9]+");

	}

	public static boolean onlyHasNumbers(String KvKNo){
		return KvKNo.matches("[0-9]+");
	}

	public static boolean isNumeric(String nummer) {
		return nummer != null && nummer.matches("[0-9]+");

	}

	public static String mbrNumber(String nummer) {
		String result = null;

		if(nummer != null && nummer.length() > 5) {
			if(nummer.toUpperCase().startsWith(EDocumentType.MELDINGBRIEF.getPrefix()))
				result = nummer.substring(EDocumentType.MELDINGBRIEF.getPrefix().length());
		}
		return result;

	}

	public static String moNumber(String nummer) {
		String result = null;

		if(nummer != null && nummer.length() > 5) {
			if(nummer.toUpperCase().startsWith(EReferentieInternType.MONITORING.getPrefix()))
				result = nummer.substring(EReferentieInternType.MONITORING.getPrefix().length());
		}
		return result;

	}

	public static String repNumber(String nummer) {
		String result = null;

		if(nummer != null && nummer.length() > 5) {
			if(nummer.toUpperCase().startsWith(EDocumentType.RAPPORT.getPrefix()))
				result = nummer.substring(EDocumentType.RAPPORT.getPrefix().length());
		}
		return result;

	}

	public static String sbdrNumber(String nummer) {
		String result = null;

		if(nummer != null && nummer.length() > 5) {
			if(nummer.toUpperCase().startsWith(EReferentieInternType.BEDRIJF.getPrefix()))
				result = nummer.substring(EReferentieInternType.BEDRIJF.getPrefix().length());
		}
		return result;

	}

	public static String veNumber(String nummer) {
		String result = null;

		if(nummer != null && nummer.length() > 5) {
			if(nummer.toUpperCase().startsWith(EReferentieInternType.VERMELDING.getPrefix()))
				result = nummer.substring(EReferentieInternType.VERMELDING.getPrefix().length());
		}
		return result;

	}
}
