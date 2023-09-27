package nl.devoorkant.util;

import java.text.DecimalFormat;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationObject;

public class FinancialUtil {
  public static ValidationObject validateBankAccountNr(String pstrValue) {
    ValidationObject loValidation = new ValidationObject();
    if (pstrValue != null && pstrValue.trim().length() > 0) {
      Long loBankAccountNr = FormatUtil.parseLong(pstrValue);
      if (loBankAccountNr == null)
        loValidation.addMessage("Bankrekeningnummer bestaat niet volledig uit cijfers.", ValidationConstants.MessageType.INVALID); 
      String lstrBankAccountNr = pstrValue.trim();
      if (lstrBankAccountNr.length() > 7)
        if (lstrBankAccountNr.length() < 9 || lstrBankAccountNr.length() > 10) {
          loValidation.addMessage("Een bankrekeningnummer bestaat uit 9 of 10 cijfers.", ValidationConstants.MessageType.INVALID);
        } else {
          int lnTotal = 0;
          for (int i = 0, j = lstrBankAccountNr.length(); i < lstrBankAccountNr.length(); i++, j--)
            lnTotal += j * Integer.parseInt(lstrBankAccountNr.substring(i, i + 1)); 
          if (lnTotal % 11 > 0)
            loValidation.addMessage("Het bankrekeningnummer voldoet niet aan de elfproef.", ValidationConstants.MessageType.INVALID); 
        }  
    } else {
      loValidation.addMessage("Geen bankrekening nummer mee gegeven.", ValidationConstants.MessageType.INVALID);
    } 
    return loValidation;
  }
  
  public static ValidationObject validateBSN(String pstrValue) {
    ValidationObject loValidation = new ValidationObject();
    if (pstrValue != null && pstrValue.trim().length() > 0) {
      Long loBSN = FormatUtil.parseLong(pstrValue);
      if (loBSN == null)
        loValidation.addMessage("Burgerservicenummer bestaat niet volledig uit cijfers.", ValidationConstants.MessageType.INVALID); 
      String lstrBSN = pstrValue.trim();
      if (lstrBSN.length() == 8 || lstrBSN.length() == 9) {
        if (lstrBSN.length() == 8)
          lstrBSN = "0" + lstrBSN; 
        int lnTotal = 0;
        for (int i = 0, j = 9; i < lstrBSN.length() - 1; i++, j--)
          lnTotal += j * Integer.parseInt(lstrBSN.substring(i, i + 1)); 
        int lnMod = lnTotal % 11;
        if (lnMod == 10 || !String.valueOf(lnMod).equals(lstrBSN.substring(8)))
          loValidation.addMessage("Het burgerservicenummer voldoet niet aan de elfproef.", ValidationConstants.MessageType.INVALID); 
      } else {
        loValidation.addMessage("Een burgerservicenummer bestaat uit 8 of 9 cijfers.", ValidationConstants.MessageType.INVALID);
      } 
    } else {
      loValidation.addMessage("Geen burgerservicenummer mee gegeven.", ValidationConstants.MessageType.INVALID);
    } 
    return loValidation;
  }
  
  public static String formatBankAccountNr(Double poValue) {
    String lstrResult = null;
    if (poValue != null && poValue.doubleValue() > 0.0D) {
      DecimalFormat loFormat = new DecimalFormat("##########");
      String lstrValue = loFormat.format(poValue);
      int lnLength = lstrValue.length();
      switch (lnLength) {
        case 1:
        case 2:
        case 3:
        case 4:
          lstrResult = lstrValue;
          break;
        case 5:
          lstrResult = lstrValue.substring(0, 2) + "." + lstrValue.substring(2, 5);
          break;
        case 6:
          lstrResult = lstrValue.substring(0, 2) + "." + lstrValue.substring(2, 4) + "." + lstrValue.substring(4, 6);
          break;
        case 7:
          lstrResult = lstrValue.substring(0, 2) + "." + lstrValue.substring(2, 4) + "." + lstrValue.substring(4, 7);
          break;
        case 8:
          lstrResult = lstrValue.substring(0, 3) + "." + lstrValue.substring(3, 5) + "." + lstrValue.substring(5, 8);
          break;
        case 9:
          lstrResult = lstrValue.substring(0, 2) + "." + lstrValue.substring(2, 4) + "." + lstrValue.substring(4, 6) + "." + lstrValue.substring(6, 9);
          break;
        case 10:
          lstrResult = lstrValue.substring(0, 3) + "." + lstrValue.substring(3, 5) + "." + lstrValue.substring(5, 7) + "." + lstrValue.substring(7, 10);
          break;
      } 
    } 
    return lstrResult;
  }
  
  public static String formatBankAccountNr(String pstrValue) {
    Double loValue = parseBankAccountNr(pstrValue);
    if (loValue != null)
      return formatBankAccountNr(loValue); 
    return null;
  }
  
  public static Double parseBankAccountNr(String pstrValue) {
    if (pstrValue != null && !pstrValue.equalsIgnoreCase("")) {
      String lstrValue = pstrValue.replaceAll("\\.", "");
      return Double.valueOf(Double.parseDouble(lstrValue));
    } 
    return null;
  }
  
  public static Double calcPercMndRenteNominaal(Double poPercJrRenteEffectief) {
    double lnEffectieveJaarRente = (poPercJrRenteEffectief != null) ? poPercJrRenteEffectief.doubleValue() : 0.0D;
    return Double.valueOf((Math.pow(1.0D + lnEffectieveJaarRente / 100.0D, 0.08333333333333333D) - 1.0D) * 100.0D);
  }
}
