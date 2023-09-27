package nl.devoorkant.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {
  private static final int DEFAULT_FRACTION_DIGITS = 2;
  
  private static final String DEFAULT_FORMAT_DATE = "dd-MM-yyyy";
  
  private static final String DEFAULT_FORMAT_DATETIME = "dd-MM-yyyy HH:mm:ss";
  
  private static final String DEFAULT_FORMAT_TIME = "HH:mm:ss";
  
  private static final Locale DEFAULT_LOCALE = Locale.US;
  
  private static Double parseDouble(String pstrValue, int pnFraction, Locale poLocale) throws ParseException {
    if (pstrValue != null && pstrValue.trim().length() > 0) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(poLocale);
      loNumberFormat.setMinimumFractionDigits(pnFraction);
      loNumberFormat.setMaximumFractionDigits(pnFraction);
      return Double.valueOf(loNumberFormat.parse(pstrValue).doubleValue());
    } 
    return null;
  }
  
  private static Float parseFloat(String pstrValue, int pnFraction, Locale poLocale) throws ParseException {
    if (pstrValue != null && pstrValue.trim().length() > 0) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(poLocale);
      loNumberFormat.setMinimumFractionDigits(pnFraction);
      loNumberFormat.setMaximumFractionDigits(pnFraction);
      return Float.valueOf(loNumberFormat.parse(pstrValue).floatValue());
    } 
    return null;
  }
  
  private static Date parseDate(String pstrValue, String pstrFormat, Locale poLocale) throws ParseException {
    if (pstrValue != null && pstrValue.trim().length() > 0) {
      SimpleDateFormat loDateFormat = new SimpleDateFormat(pstrFormat, poLocale);
      return loDateFormat.parse(pstrValue);
    } 
    return null;
  }
  
  private static Date parseSQLDate(String pstrValue, String pstrFormat, Locale poLocale) throws ParseException {
    if (pstrValue != null && pstrValue.trim().length() > 0) {
      SimpleDateFormat loDateFormat = new SimpleDateFormat(pstrFormat, poLocale);
      return new Date(loDateFormat.parse(pstrValue).getTime());
    } 
    return null;
  }
  
  private static Date parseDate(Date poValue, String pstrFormat, Locale poLocale) throws ParseException {
    if (poValue != null) {
      SimpleDateFormat loDateFormat = new SimpleDateFormat(pstrFormat, poLocale);
      String lstrDate = loDateFormat.format(poValue);
      return loDateFormat.parse(lstrDate);
    } 
    return null;
  }
  
  public static String formatBigInteger(BigInteger poValue) {
    if (poValue != null) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
      loNumberFormat.setMaximumFractionDigits(0);
      loNumberFormat.setGroupingUsed(false);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatBooleanJN(Boolean poValue) {
    if (poValue != null)
      return poValue.booleanValue() ? "J" : "N"; 
    return "";
  }
  
  public static String formatBooleanTrueFalse(Boolean pbValue) {
    return pbValue.booleanValue() ? "true" : "false";
  }
  
  public static boolean parseBooleanTrueFalse(String pstrValue) {
    return (pstrValue.equalsIgnoreCase("true") || pstrValue.equalsIgnoreCase("1"));
  }
  
  public static String formatCurrency(Double poValue) {
    return formatCurrency(poValue, 2);
  }
  
  public static String formatCurrency(Double poValue, int pnFraction) {
    if (poValue != null) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
      loNumberFormat.setMinimumFractionDigits(pnFraction);
      loNumberFormat.setMaximumFractionDigits(pnFraction);
      loNumberFormat.setGroupingUsed(true);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatDate(Date poValue) {
    return formatDate(poValue, "dd-MM-yyyy");
  }
  
  public static String formatDate(Date poValue, String pstrFormat) {
    if (poValue != null) {
      String lstrFormat = (pstrFormat != null) ? pstrFormat : "dd-MM-yyyy";
      SimpleDateFormat loDateFormat = new SimpleDateFormat(lstrFormat, DEFAULT_LOCALE);
      return loDateFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatDateTime(Date poValue) {
    return formatDate(poValue, "dd-MM-yyyy HH:mm:ss");
  }
  
  public static String formatDateTime(Timestamp poValue, String pstrFormat) {
    return (poValue != null) ? formatDate(poValue, pstrFormat) : "";
  }
  
  public static String formatDouble(Double poValue) {
    if (poValue != null) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
      loNumberFormat.setGroupingUsed(false);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatFloat(Float poValue) {
    if (poValue != null) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
      loNumberFormat.setGroupingUsed(false);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatInteger(Integer poValue) {
    if (poValue != null) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
      loNumberFormat.setMaximumFractionDigits(0);
      loNumberFormat.setGroupingUsed(false);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatInteger(Double poValue) {
    if (poValue != null) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
      loNumberFormat.setMaximumFractionDigits(0);
      loNumberFormat.setGroupingUsed(false);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatLong(Long poValue) {
    if (poValue != null) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
      loNumberFormat.setMaximumFractionDigits(0);
      loNumberFormat.setGroupingUsed(false);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatShort(Short poValue) {
    if (poValue != null) {
      NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
      loNumberFormat.setMaximumFractionDigits(0);
      loNumberFormat.setGroupingUsed(false);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatPercentage(Double poValue) {
    return formatPercentage(poValue, 5, DEFAULT_LOCALE);
  }
  
  public static String formatPercentage(Double poValue, int pnFraction) {
    return formatPercentage(poValue, pnFraction, DEFAULT_LOCALE);
  }
  
  public static String formatPercentage2(Double poValue) {
    return formatPercentage(poValue, 2, DEFAULT_LOCALE);
  }
  
  public static String formatPercentage2(Double poValue, Locale poLocale) {
    return formatPercentage(poValue, 2, poLocale);
  }
  
  public static String formatPercentage(Double poValue, int pnFraction, Locale poLocale) {
    if (poValue != null) {
      Locale loLocale = (poLocale != null) ? poLocale : DEFAULT_LOCALE;
      NumberFormat loNumberFormat = NumberFormat.getInstance(loLocale);
      loNumberFormat.setMinimumFractionDigits(pnFraction);
      loNumberFormat.setMaximumFractionDigits(pnFraction);
      loNumberFormat.setGroupingUsed(false);
      return loNumberFormat.format(poValue);
    } 
    return "";
  }
  
  public static String formatElapsedTime(long pnMillis) {
    long lnHours = 0L;
    long lnMinutes = 0L;
    long lnSeconds = 0L;
    long lnMillis = 0L;
    String lstrResult = "";
    lnHours = pnMillis / 1000L / 60L / 60L;
    lnMinutes = (pnMillis - lnHours * 60L * 60L * 1000L) / 1000L / 60L;
    lnSeconds = (pnMillis - lnHours * 1000L * 60L * 60L - lnMinutes * 1000L * 60L) / 1000L;
    lnMillis = pnMillis - lnHours * 1000L * 60L * 60L - lnMinutes * 1000L * 60L - lnSeconds * 1000L;
    if (lnHours > 0L)
      lstrResult = lstrResult + String.valueOf(lnHours) + " hrs"; 
    if (lnMinutes > 0L)
      lstrResult = lstrResult + (!"".equals(lstrResult) ? ", " : "") + String.valueOf(lnMinutes) + " min"; 
    if (lnSeconds > 0L)
      lstrResult = lstrResult + (!"".equals(lstrResult) ? ", " : "") + String.valueOf(lnSeconds) + " secs"; 
    if (lnMillis > 0L || "".equals(lstrResult))
      lstrResult = lstrResult + (!"".equals(lstrResult) ? ", " : "") + String.valueOf(lnMillis) + " ms"; 
    return lstrResult;
  }
  
  public static String formatString(String pstrValue) {
    return (pstrValue == null) ? "" : pstrValue;
  }
  
  public static String formatHTMLString(String pstrValue) {
    if (pstrValue != null && pstrValue.trim().length() > 0) {
      String lstrResult = StringUtil.replace(pstrValue, "&", "&amp;");
      lstrResult = StringUtil.replace(lstrResult, "<", "&lt;");
      lstrResult = StringUtil.replace(lstrResult, ">", "&gt;");
      lstrResult = StringUtil.replace(lstrResult, "\"", "&quot;");
      return lstrResult;
    } 
    return "";
  }
  
  public static String formatAsString(Object poObject) {
    if (poObject != null) {
      if (poObject instanceof String)
        return formatString((String)poObject); 
      if (poObject instanceof Double)
        return formatDouble((Double)poObject); 
      if (poObject instanceof Date)
        return formatDateTime((Date)poObject); 
    } else {
      return "";
    } 
    return poObject.toString();
  }
  
  public static String formatTime(Date poValue) {
    return formatDate(poValue, "HH:mm:ss");
  }
  
  public static BigDecimal parseBigDecimal2(Double poValue) {
    if (poValue != null) {
      BigDecimal loBigDecimal = new BigDecimal(poValue.doubleValue());
      return loBigDecimal.setScale(2, 4);
    } 
    return null;
  }
  
  public static boolean parseBoolean(String pstrValue) {
    return (pstrValue.equalsIgnoreCase("true") || pstrValue.equalsIgnoreCase("1") || pstrValue.equalsIgnoreCase("Y") || pstrValue.equalsIgnoreCase("J"));
  }
  
  public static BigInteger parseBigInteger(Integer poValue) {
    if (poValue != null)
      return new BigInteger(formatInteger(poValue)); 
    return null;
  }
  
  public static Double parseCurrency(String pstrValue) {
    try {
      return parseDouble(pstrValue, 2, DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Date parseDate(String pstrValue) {
    try {
      return parseDate(pstrValue, "dd-MM-yyyy", DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Date parseDate(Date poValue) {
    try {
      return parseDate(poValue, "dd-MM-yyyy", DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Date parseSQLDate(String pstrValue) {
    try {
      return parseSQLDate(pstrValue, "dd-MM-yyyy", DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Date parseDate(String pstrValue, String pstrFormat) {
    try {
      return parseDate(pstrValue, pstrFormat, DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Date parseSQLDate(String pstrValue, String pstrFormat) {
    try {
      return parseSQLDate(pstrValue, pstrFormat, DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Date parseDateTime(String pstrValue) {
    try {
      return parseDate(pstrValue, "dd-MM-yyyy HH:mm:ss", DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Double parseDouble(BigDecimal poValue) {
    return (poValue != null) ? Double.valueOf(poValue.doubleValue()) : null;
  }
  
  public static Double parseDouble(Integer poValue) {
    return (poValue != null) ? Double.valueOf(poValue.doubleValue()) : null;
  }
  
  public static Double parseDouble(Long poValue) {
    return (poValue != null) ? parseDouble(poValue.toString()) : null;
  }
  
  public static Double parseDouble(String pstrValue) {
    try {
      return parseDouble(pstrValue, 0, DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Float parseFloat(String pstrValue) {
    try {
      return parseFloat(pstrValue, 0, DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Float parseFloat(Integer poValue) {
    return (poValue != null) ? Float.valueOf(poValue.floatValue()) : null;
  }
  
  public static Integer parseInteger(BigDecimal poValue) {
    return (poValue != null) ? Integer.valueOf(poValue.intValue()) : null;
  }
  
  public static Integer parseInteger(BigInteger poValue) {
    return (poValue != null) ? Integer.valueOf(poValue.intValue()) : null;
  }
  
  public static Integer parseInteger(Double poValue) {
    return (poValue != null) ? Integer.valueOf(poValue.intValue()) : null;
  }
  
  public static Integer parseInteger(Short poValue) {
    return (poValue != null) ? Integer.valueOf(poValue.intValue()) : null;
  }
  
  public static Integer parseInteger(Float poValue) {
    return (poValue != null) ? Integer.valueOf(Math.round(poValue.floatValue())) : null;
  }
  
  public static Integer parseInteger(String pstrValue) {
    if (pstrValue != null && pstrValue.trim().length() > 0)
      try {
        NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
        loNumberFormat.setMaximumFractionDigits(0);
        return Integer.valueOf(loNumberFormat.parse(pstrValue).intValue());
      } catch (ParseException e) {
        return null;
      }  
    return null;
  }
  
  public static Long parseLong(Double poValue) {
    return (poValue != null) ? Long.valueOf(poValue.longValue()) : null;
  }
  
  public static Long parseLong(String pstrValue) {
    if (pstrValue != null && pstrValue.trim().length() > 0)
      try {
        NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
        loNumberFormat.setMaximumFractionDigits(0);
        return Long.valueOf(loNumberFormat.parse(pstrValue).longValue());
      } catch (ParseException e) {
        return null;
      }  
    return null;
  }
  
  public static Double parsePercentage(String pstrValue, int pnFraction) {
    try {
      return parseDouble(pstrValue, pnFraction, DEFAULT_LOCALE);
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Short parseShort(String pstrValue) {
    if (pstrValue != null && pstrValue.trim().length() > 0)
      try {
        NumberFormat loNumberFormat = NumberFormat.getInstance(DEFAULT_LOCALE);
        loNumberFormat.setMaximumFractionDigits(0);
        return Short.valueOf(loNumberFormat.parse(pstrValue).shortValue());
      } catch (ParseException e) {
        return null;
      }  
    return null;
  }
  
  public static Short parseShort(Integer poValue) {
    return (poValue != null) ? Short.valueOf(poValue.shortValue()) : null;
  }
  
  public static Timestamp parseTimestamp(String pstrValue) {
    try {
      Date loDate = parseDate(pstrValue, "dd-MM-yyyy HH:mm:ss", DEFAULT_LOCALE);
      return new Timestamp(loDate.getTime());
    } catch (ParseException e) {
      return null;
    } 
  }
  
  public static Timestamp parseTimestamp(String pstrValue, String pstrFormat) {
    try {
      Date loDate = parseDate(pstrValue, pstrFormat, DEFAULT_LOCALE);
      return new Timestamp(loDate.getTime());
    } catch (ParseException e) {
      return null;
    } 
  }
}
