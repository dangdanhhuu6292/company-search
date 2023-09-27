package nl.devoorkant.validation.annotation;

import nl.devoorkant.util.FormatUtil;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationMessage;

public class DefaultValidator extends Validator {
  public ValidationMessage invalidMaxLength(String property, String value, long length, long maxLength) {
    StringBuffer result = new StringBuffer();
    result.append("Property '");
    result.append(property);
    result.append("' with value '");
    result.append(value);
    result.append("' has exceeded the maximum length of ");
    result.append(maxLength);
    result.append(".");
    return new ValidationMessage(property, result.toString(), ValidationConstants.MessageType.ERROR);
  }
  
  public ValidationMessage invalidMinLength(String property, String value, long length, long minLength) {
    StringBuffer result = new StringBuffer();
    result.append("Property '");
    result.append(property);
    result.append("' with value '");
    result.append(value);
    result.append("' has exceeded the minimum length of ");
    result.append(minLength);
    result.append(".");
    return new ValidationMessage(property, result.toString(), ValidationConstants.MessageType.ERROR);
  }
  
  public ValidationMessage invalidMinValue(String property, Number value, double minValue) {
    StringBuffer result = new StringBuffer();
    result.append("Property '");
    result.append(property);
    result.append("' with value '");
    result.append(FormatUtil.formatDouble(Double.valueOf(value.doubleValue())));
    result.append("' must have a minimum value ");
    result.append(FormatUtil.formatDouble(Double.valueOf(minValue)));
    result.append(".");
    return new ValidationMessage(property, result.toString(), ValidationConstants.MessageType.ERROR);
  }
  
  public ValidationMessage invalidMaxValue(String property, Number value, double maxValue) {
    StringBuffer result = new StringBuffer();
    result.append("Property '");
    result.append(property);
    result.append("' with value '");
    result.append(FormatUtil.formatDouble(Double.valueOf(value.doubleValue())));
    result.append("' exceeds the maximum value of ");
    result.append(FormatUtil.formatDouble(Double.valueOf(maxValue)));
    result.append(".");
    return new ValidationMessage(property, result.toString(), ValidationConstants.MessageType.ERROR);
  }
  
  public ValidationMessage invalidNotNull(String property) {
    StringBuffer result = new StringBuffer();
    result.append("Property '");
    result.append(property);
    result.append("' value is null.");
    return new ValidationMessage(property, result.toString(), ValidationConstants.MessageType.ERROR);
  }
  
  public ValidationMessage invalidNotNullOrEmpty(String property) {
    StringBuffer result = new StringBuffer();
    result.append("Property '");
    result.append(property);
    result.append("' value is null or empty.");
    return new ValidationMessage(property, result.toString(), ValidationConstants.MessageType.ERROR);
  }
  
  public ValidationMessage invalidRegExValidate(String property, String value, String description) {
    StringBuffer result = new StringBuffer();
    result.append("Property '");
    result.append(property);
    result.append("' value '");
    result.append(value);
    result.append("' doesn't comply with the ");
    result.append(description);
    result.append(" format.");
    return new ValidationMessage(property, result.toString(), ValidationConstants.MessageType.ERROR);
  }
}
