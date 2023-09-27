package nl.devoorkant.util;

import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationMessage;
import nl.devoorkant.validation.ValidationObject;

public class AddressUtil {
  private static final String VALID_POSTALCODE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  
  public static String formatDutchPostalCode(String pstrValue) {
    if (pstrValue != null && pstrValue.trim().length() > 0) {
      if (validateDutchPostalCode(pstrValue).isValid()) {
        if (pstrValue.charAt(4) != ' ' && pstrValue.length() == 6)
          return pstrValue.substring(0, 4) + " " + pstrValue.substring(4); 
        if (pstrValue.charAt(4) == ' ' && pstrValue.length() == 7)
          return pstrValue; 
        return "";
      } 
      return "";
    } 
    return "";
  }
  
  public static ValidationObject validateDutchPostalCode(String pstrValue) {
    ValidationObject loValidation = new ValidationObject();
    try {
      if (pstrValue != null && pstrValue.trim().length() > 0) {
        StringBuffer loPostalCode = new StringBuffer();
        char[] laToRemove = { ' ' };
        loPostalCode.append(StringUtil.removeChars(pstrValue, laToRemove));
        if (loPostalCode.length() == 6) {
          try {
            int lnNumbers = Integer.parseInt(loPostalCode.substring(0, 4));
            if (lnNumbers < 1000)
              loValidation.addMessage(new ValidationMessage("Ongeldige postcode, eerste cijfer mag geen 0 zijn.", ValidationConstants.MessageType.INVALID)); 
          } catch (NumberFormatException loEx) {
            loValidation.addMessage(new ValidationMessage("Ongeldige postcode, postcode moet beginnen met 4 cijfers.", ValidationConstants.MessageType.INVALID));
          } 
          String lnLetters = loPostalCode.substring(4, 6).toUpperCase();
          if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(lnLetters.substring(0, 1)) == -1 || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(lnLetters.substring(1, 2)) == -1)
            loValidation.addMessage(new ValidationMessage("Ongeldige postcode, postcode moet met twee letters eindigen.", ValidationConstants.MessageType.INVALID)); 
        } else {
          loValidation.addMessage(new ValidationMessage("Ongeldige postcode.", ValidationConstants.MessageType.INVALID));
        } 
      } else {
        loValidation.addMessage(new ValidationMessage("Geen postcode mee gegeven.", ValidationConstants.MessageType.INVALID));
      } 
    } catch (Exception loEx) {
      loValidation.addMessage(new ValidationMessage("Validation failed", ValidationConstants.MessageType.ERROR));
    } 
    return loValidation;
  }
}
