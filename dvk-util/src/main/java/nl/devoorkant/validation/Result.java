package nl.devoorkant.validation;

import java.io.Serializable;
import java.util.Collection;

public class Result implements Serializable {
  private boolean ibResult = true;
  
  private ValidationObject ioValidationObject = new ValidationObject();
  
  private Object ioResultObject = null;
  
  public Result() {}
  
  public Result(ValidationObject poValidationObject) {
    this.ioValidationObject = poValidationObject;
  }
  
  public void addResult(Result poResult) {
    addValidationObject(poResult.getValidationObject());
    if (poResult.getResultObject() != null)
      setResultObject(poResult.getResultObject()); 
    if (this.ibResult)
      this.ibResult = poResult.isSuccessful(); 
  }
  
  public void setValidationObject(ValidationObject poValidationObject) {
    this.ioValidationObject = poValidationObject;
  }
  
  public void addValidationObject(ValidationObject poValidationObject) {
    this.ioValidationObject.addMessages(poValidationObject.getValidationMessages());
  }
  
  public ValidationObject getValidationObject() {
    return this.ioValidationObject;
  }
  
  public Collection<ValidationMessage> getValidationMessages() {
    return this.ioValidationObject.getValidationMessages();
  }
  
  public Collection<ValidationMessage> getValidationMessages(ValidationConstants.MessageType poMessageType) {
    return this.ioValidationObject.getValidationMessagesByMessageType(poMessageType);
  }
  
  public void setResult(boolean pbResult) {
    this.ibResult = pbResult;
  }
  
  public void setResultObject(Object poResultObject) {
    this.ioResultObject = poResultObject;
  }
  
  public Object getResultObject() {
    return this.ioResultObject;
  }
  
  public boolean isSuccessful() {
    return (this.ibResult && this.ioValidationObject.isValid());
  }
}
