package nl.devoorkant.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
  private static final String DATE_PATTERN = "dd-MM-yyyy H:m:s";
  
  public static Timestamp addDays(Timestamp poValue, int pnDays) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      loCalendar.add(5, pnDays);
      return new Timestamp(loCalendar.getTimeInMillis());
    } 
    return null;
  }
  
  public static Timestamp addMonths(Timestamp poValue, int pnMonths) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      loCalendar.add(2, pnMonths);
      return new Timestamp(loCalendar.getTimeInMillis());
    } 
    return null;
  }
  
  public static Timestamp addYears(Timestamp poValue, int pnYears) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      loCalendar.add(1, pnYears);
      return new Timestamp(loCalendar.getTimeInMillis());
    } 
    return null;
  }
  
  public static Integer age(Calendar poValue) {
    if (poValue != null)
      return age(new Timestamp(poValue.getTimeInMillis())); 
    return null;
  }
  
  public static Integer age(Date poValue) {
    if (poValue != null)
      return age(new Timestamp(poValue.getTime())); 
    return null;
  }
  
  public static Integer age(Timestamp poValue) {
    if (poValue != null) {
      Calendar loCurrentDate = new GregorianCalendar();
      Calendar loBirthDate = new GregorianCalendar();
      loBirthDate.setTime(poValue);
      int lnResult = loCurrentDate.get(1) - loBirthDate.get(1) - 1;
      if (loCurrentDate.get(6) >= loBirthDate.get(6))
        lnResult++; 
      return Integer.valueOf(lnResult);
    } 
    return null;
  }
  
  public static Integer ageInMonths(Timestamp poValue) {
    if (poValue != null) {
      Calendar loCurrentDate = new GregorianCalendar();
      Calendar loBirthDate = new GregorianCalendar();
      loBirthDate.setTime(poValue);
      int lnResult = (loCurrentDate.get(1) - loBirthDate.get(1)) * 12;
      lnResult += loCurrentDate.get(2) - loBirthDate.get(2);
      if (loCurrentDate.get(5) < loBirthDate.get(5))
        lnResult--; 
      return Integer.valueOf(lnResult);
    } 
    return null;
  }
  
  public static boolean after(Date poFirstDate, Date poSecondDate) {
    Date loDate1 = FormatUtil.parseDate(poFirstDate);
    Date loDate2 = FormatUtil.parseDate(poSecondDate);
    return (loDate1 != null && loDate2 != null && loDate1.after(loDate2));
  }
  
  public static boolean afterToday(Date poDate) {
    Date loDate = FormatUtil.parseDate(poDate);
    return (loDate != null && loDate.after(getCurrentDate()));
  }
  
  public static boolean before(Date poFirstDate, Date poSecondDate) {
    Date loDate1 = FormatUtil.parseDate(poFirstDate);
    Date loDate2 = FormatUtil.parseDate(poSecondDate);
    return (loDate1 != null && loDate2 != null && loDate1.before(loDate2));
  }
  
  public static boolean beforeToday(Date poDate) {
    Date loDate = FormatUtil.parseDate(poDate);
    return (loDate != null && loDate.before(getCurrentDate()));
  }
  
  public static Date convertUtilToSQLDate(Date poValue) {
	  
	  return (poValue != null) ? new Date(poValue.getTime()) : null;
  }
  
  public static Date convertUtilToDate(String poValue, String format) {
	Date datumLaatsteUpdate = null;
	try {
		datumLaatsteUpdate = new SimpleDateFormat(format).parse(poValue);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    
	return datumLaatsteUpdate;
	  
  }
  
  public static Date convertToDate(Timestamp poValue) {
    return (poValue != null) ? new Date(poValue.getTime()) : null;
  }
  
  public static Date convertToDate(Calendar poValue) {
    return (poValue != null) ? new Date(poValue.getTimeInMillis()) : null;
  }
  
  public static Date convertToSQLDate(Calendar poValue) {
    return (poValue != null) ? new Date(poValue.getTimeInMillis()) : null;
  }
  
  public static Timestamp convertToTimestamp(Calendar poValue) {
    return (poValue != null) ? new Timestamp(poValue.getTimeInMillis()) : null;
  }
  
  public static Timestamp convertToTimestamp(Date poValue) {
    return (poValue != null) ? new Timestamp(poValue.getTime()) : null;
  }
  
  public static Calendar convertToCalendar(Date poValue) {
    if (poValue != null) {
      Calendar loResult = new GregorianCalendar();
      loResult.setTime(poValue);
      return loResult;
    } 
    return null;
  }
  
  public static Calendar convertToCalendar(Timestamp poValue) {
    if (poValue != null) {
      Calendar loResult = new GregorianCalendar();
      loResult.setTime(poValue);
      return loResult;
    } 
    return null;
  }
  
  public static Integer differenceInMonths_Ceiling(Timestamp poFirstDate, Timestamp poSecondDate) {
    if (poFirstDate != null && poSecondDate != null) {
      Calendar loFirstDate = new GregorianCalendar();
      Calendar loSecondDate = new GregorianCalendar();
      loFirstDate.setTime(poFirstDate);
      loSecondDate.setTime(poSecondDate);
      return differenceInMonths_Ceiling(loFirstDate, loSecondDate);
    } 
    return null;
  }
  
  public static Integer differenceInMonths_Ceiling(Calendar poFirstDate, Calendar poSecondDate) {
    if (poFirstDate != null && poSecondDate != null) {
      int lnResult = (poSecondDate.get(1) - poFirstDate.get(1)) * 12;
      lnResult += poSecondDate.get(2) - poFirstDate.get(2);
      if (poSecondDate.get(5) > poFirstDate.get(5))
        lnResult++; 
      return Integer.valueOf(lnResult);
    } 
    return null;
  }
  
  public static Integer differenceInMonths_Floor(Timestamp poFirstDate, Timestamp poSecondDate) {
    if (poFirstDate != null && poSecondDate != null) {
      Calendar loFirstDate = new GregorianCalendar();
      Calendar loSecondDate = new GregorianCalendar();
      loFirstDate.setTime(poFirstDate);
      loSecondDate.setTime(poSecondDate);
      return differenceInMonths_Floor(loFirstDate, loSecondDate);
    } 
    return null;
  }
  
  public static Integer differenceInMonths_Floor(Calendar poFirstDate, Calendar poSecondDate) {
    if (poFirstDate != null && poSecondDate != null) {
      int lnResult = (poSecondDate.get(1) - poFirstDate.get(1)) * 12;
      lnResult += poSecondDate.get(2) - poFirstDate.get(2);
      if (poSecondDate.get(5) < poFirstDate.get(5))
        lnResult--; 
      return Integer.valueOf(lnResult);
    } 
    return null;
  }
  
  public static int differenceInDays(Calendar startDate, Calendar endDate) {
    int loResult = 0;
    if (startDate != null && endDate != null && startDate.compareTo(endDate) < 0) {
      Calendar sDate = (Calendar)startDate.clone();
      int y1 = sDate.get(1);
      int y2 = endDate.get(1);
      int m1 = sDate.get(2);
      int m2 = endDate.get(2);
      while ((y2 - y1) * 12 + m2 - m1 > 12) {
        if (sDate.get(2) == 0 && sDate.get(5) == sDate.getActualMinimum(5)) {
          loResult += sDate.getActualMaximum(6);
          sDate.add(1, 1);
        } else {
          int diff = 1 + sDate.getActualMaximum(6) - sDate.get(6);
          sDate.add(6, diff);
          loResult += diff;
        } 
        y1 = sDate.get(1);
      } 
      while ((m2 - m1) % 12 > 1) {
        loResult += sDate.getActualMaximum(5);
        sDate.add(2, 1);
        m1 = sDate.get(2);
      } 
      while (sDate.before(endDate)) {
        sDate.add(5, 1);
        loResult++;
      } 
    } 
    return loResult;
  }
  
  public static boolean equals(Date poFirstDate, Date poSecondDate) {
    Date loDate1 = FormatUtil.parseDate(poFirstDate);
    Date loDate2 = FormatUtil.parseDate(poSecondDate);
    return (loDate1 != null && loDate2 != null && loDate1.compareTo(loDate2) == 0);
  }
  
  public static Date getCurrentDate() {
    return (new GregorianCalendar()).getTime();
  }
  
  public static Date getCurrentSQLDate() {
    return new Date((new GregorianCalendar()).getTimeInMillis());
  }
  
  public static Timestamp getCurrentTimestamp() {
    return new Timestamp((new GregorianCalendar()).getTimeInMillis());
  }
  
  public static Integer getCurrentDay() {
    return Integer.valueOf((new GregorianCalendar()).get(5));
  }
  
  public static Integer getCurrentMonth() {
    return Integer.valueOf((new GregorianCalendar()).get(2));
  }
  
  public static Integer getCurrentYear() {
    return Integer.valueOf((new GregorianCalendar()).get(1));
  }
  
  public static Integer getDay(Date poValue) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      return Integer.valueOf(loCalendar.get(5));
    } 
    return null;
  }
  
  public static Integer getDay(Timestamp poValue) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      return Integer.valueOf(loCalendar.get(5));
    } 
    return null;
  }
  
  public static Integer getMonth(Date poValue) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      return Integer.valueOf(loCalendar.get(2));
    } 
    return null;
  }
  
  public static Integer getMonth(Timestamp poValue) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      return Integer.valueOf(loCalendar.get(2));
    } 
    return null;
  }
  
  public static Integer getYear(Date poValue) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      return Integer.valueOf(loCalendar.get(1));
    } 
    return null;
  }
  
  public static Integer getYear(Timestamp poValue) {
    if (poValue != null) {
      Calendar loCalendar = new GregorianCalendar();
      loCalendar.setTimeInMillis(poValue.getTime());
      return Integer.valueOf(loCalendar.get(1));
    } 
    return null;
  }
  
  public static Date composeDate(int day, int month, int year) throws ParseException {
    return composeDate(day, month, year, 0, 0, 0);
  }
  
  public static Date composeDate(int day, int month, int year, int hour, int minute, int second) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy H:m:s");
    return sdf.parse(day + "-" + month + "-" + year + " " + hour + ":" + minute + ":" + second);
  }
  
  public static Integer monthsFromNow(Timestamp poValue) {
    if (poValue != null) {
      Calendar loCurrentDate = new GregorianCalendar();
      Calendar loFutureDate = new GregorianCalendar();
      loFutureDate.setTime(poValue);
      int lnResult = (loFutureDate.get(1) - loCurrentDate.get(1)) * 12;
      lnResult += loFutureDate.get(2) - loCurrentDate.get(2);
      if (loFutureDate.get(5) < loCurrentDate.get(5))
        lnResult--; 
      return Integer.valueOf(lnResult);
    } 
    return null;
  }
  
  public static Integer monthsFromNow(Date poValue) {
    if (poValue != null)
      return monthsFromNow(new Timestamp(poValue.getTime())); 
    return null;
  }
  
  public static boolean isLeapYear(Integer poValue) {
    if (poValue != null) {
      Integer loYear = Integer.valueOf((poValue.intValue() < 0) ? (1 - poValue.intValue()) : poValue.intValue());
      return (new GregorianCalendar()).isLeapYear(loYear.intValue());
    } 
    return false;
  }
  
  public static boolean isLeapYear(Date poValue) {
    return (poValue != null && isLeapYear(convertToCalendar(poValue)));
  }
  
  public static boolean isLeapYear(Timestamp poValue) {
    return (poValue != null && isLeapYear(convertToCalendar(poValue)));
  }
  
  public static boolean isLeapYear(Calendar poValue) {
    if (poValue != null) {
      if (poValue.get(0) == 0)
        return isLeapYear(Integer.valueOf(1 - poValue.get(1))); 
      return isLeapYear(Integer.valueOf(poValue.get(1)));
    } 
    return false;
  }
  
  public static Integer getDaysInMonth() {
    return getDaysInMonth(getCurrentTimestamp());
  }
  
  public static Integer getDaysInMonth(Date poValue) {
    return getDaysInMonth(convertToTimestamp(poValue));
  }
  
  public static Integer getDaysInMonth(Timestamp poValue) {
    Integer loDaysInMonth = null;
    if (poValue != null) {
      Calendar loCal = convertToCalendar(poValue);
      loDaysInMonth = Integer.valueOf(loCal.getActualMaximum(5));
    } 
    return loDaysInMonth;
  }
}
