package nl.devoorkant.validation.annotation;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nl.devoorkant.validation.ValidationMessage;
import nl.devoorkant.validation.ValidationObject;
import nl.devoorkant.validation.exception.ValidationException;

public abstract class Validator {
  public ValidationObject validate(Object poObject) throws ValidationException {
    ValidationObject loValidationObject = new ValidationObject();
    if (poObject != null) {
      PropertyDescriptor[] loaPropertyDescriptor;
      try {
        loaPropertyDescriptor = Introspector.getBeanInfo(poObject.getClass()).getPropertyDescriptors();
      } catch (IntrospectionException loEx) {
        throw new ValidationException("Unable to get property descriptors of class " + poObject.getClass() + ".", loEx);
      } 
      for (PropertyDescriptor loPropertyDescriptor : loaPropertyDescriptor)
        validate(loValidationObject, poObject, loPropertyDescriptor); 
    } 
    return loValidationObject;
  }
  
  public ValidationObject validate(ValidationObject poValidationResult, Object poObject, String pstrPropertyName) throws ValidationException {
    PropertyDescriptor[] loaPropertyDescriptor;
    try {
      loaPropertyDescriptor = Introspector.getBeanInfo(poObject.getClass()).getPropertyDescriptors();
    } catch (IntrospectionException loEx) {
      throw new ValidationException("Unable to get property descriptors of class " + poObject.getClass() + ".", loEx);
    } 
    for (PropertyDescriptor loPropertyDescriptor : loaPropertyDescriptor) {
      if (pstrPropertyName.equals(loPropertyDescriptor.getName()))
        validate(poValidationResult, poObject, loPropertyDescriptor); 
    } 
    return poValidationResult;
  }
  
  public ValidationObject validate(Object poObject, String pstrPropertyName) throws ValidationException {
    ValidationObject loResult = new ValidationObject();
    return validate(loResult, poObject, pstrPropertyName);
  }
  
  public abstract ValidationMessage invalidRegExValidate(String paramString1, String paramString2, String paramString3);
  
  public abstract ValidationMessage invalidMaxLength(String paramString1, String paramString2, long paramLong1, long paramLong2);
  
  public abstract ValidationMessage invalidMinLength(String paramString1, String paramString2, long paramLong1, long paramLong2);
  
  public abstract ValidationMessage invalidNotNullOrEmpty(String paramString);
  
  public abstract ValidationMessage invalidMinValue(String paramString, Number paramNumber, double paramDouble);
  
  public abstract ValidationMessage invalidMaxValue(String paramString, Number paramNumber, double paramDouble);
  
  public abstract ValidationMessage invalidNotNull(String paramString);
  
  private boolean isConstraint(Annotation annotation) {
    if (annotation == null)
      throw new IllegalArgumentException("null is not a legal value for annotation"); 
    return annotation.annotationType().isAnnotationPresent((Class)ValidateConstraint.class);
  }
  
  private void validate(ValidationObject poValidationObject, Object poObject, PropertyDescriptor poPropertyDescriptor) throws ValidationException {
    if (poPropertyDescriptor != null) {
      Method write = poPropertyDescriptor.getWriteMethod();
      Method read = poPropertyDescriptor.getReadMethod();
      if (write != null && read != null)
        try {
          Object value = read.invoke(poObject, new Object[0]);
          for (Annotation annotation : write.getAnnotations()) {
            if (isConstraint(annotation)) {
              ValidationMessage message = executeValidation(annotation, poPropertyDescriptor.getName(), value);
              if (message != null)
                poValidationObject.addMessage(message); 
            } 
          } 
        } catch (IllegalAccessException loEx) {
          throw new ValidationException("Unable to execute method " + read.getName() + ".", loEx);
        } catch (InvocationTargetException loEx) {
          throw new ValidationException("Unable to execute method " + read.getName() + ".", loEx);
        }  
    } 
  }
  
  private ValidationMessage executeValidation(Annotation poAnnotation, String pstrPropertyName, Object poValue) {
    ValidationMessage loValidationMessage = null;
    if (poAnnotation == null)
      throw new IllegalArgumentException("null is not a legal value for annotation"); 
    if (poAnnotation instanceof NotNull) {
      if (poValue == null)
        loValidationMessage = invalidNotNull(pstrPropertyName); 
    } else if (poAnnotation instanceof NotNullOrEmpty) {
      if (poValue == null) {
        loValidationMessage = invalidNotNullOrEmpty(pstrPropertyName);
      } else if ((poValue instanceof String || poValue instanceof Character) && 
        poValue.toString().trim().length() == 0) {
        loValidationMessage = invalidNotNullOrEmpty(pstrPropertyName);
      } 
    } else if (poAnnotation instanceof MaxLength) {
      if (poValue != null && poValue instanceof String) {
        String lstrText = poValue.toString();
        long lnMaxLength = ((MaxLength)poAnnotation).length();
        long lnLength = lstrText.length();
        if (lnLength > lnMaxLength)
          loValidationMessage = invalidMaxLength(pstrPropertyName, lstrText, lnLength, lnMaxLength); 
      } 
    } else if (poAnnotation instanceof MinLength) {
      if (poValue != null && poValue instanceof String) {
        String lstrText = poValue.toString();
        long lnMinLength = ((MinLength)poAnnotation).length();
        long lnLength = lstrText.length();
        if (lnLength < lnMinLength)
          loValidationMessage = invalidMinLength(pstrPropertyName, lstrText, lnLength, lnMinLength); 
      } 
    } else if (poAnnotation instanceof RegExValidate) {
      if (poValue != null && poValue instanceof String) {
        String lstrText = poValue.toString();
        RegExValidate loValidator = (RegExValidate)poAnnotation;
        RegExEnum loType = loValidator.type();
        String lstrDescription = loValidator.description();
        String lstrExpression = (loType == RegExEnum.GENERIC) ? loValidator.expression() : loType.getExpression();
        if (lstrExpression != null && !lstrText.matches(lstrExpression))
          loValidationMessage = invalidRegExValidate(pstrPropertyName, lstrText, lstrDescription); 
      } 
    } else if (poAnnotation instanceof MinValue) {
      if (poValue != null && poValue instanceof Number) {
        Number lnNumber = (Number)poValue;
        double lnMinValue = ((MinValue)poAnnotation).minValue();
        if (lnNumber.doubleValue() < lnMinValue)
          loValidationMessage = invalidMinValue(pstrPropertyName, lnNumber, lnMinValue); 
      } 
    } else if (poAnnotation instanceof MaxValue && 
      poValue != null && poValue instanceof Number) {
      Number lnNumber = (Number)poValue;
      double lnMaxValue = ((MaxValue)poAnnotation).maxValue();
      if (lnNumber.doubleValue() > lnMaxValue)
        loValidationMessage = invalidMaxValue(pstrPropertyName, lnNumber, lnMaxValue); 
    } 
    return loValidationMessage;
  }
}
