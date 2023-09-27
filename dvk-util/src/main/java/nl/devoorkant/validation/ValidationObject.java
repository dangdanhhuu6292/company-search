package nl.devoorkant.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ValidationObject implements Serializable {
  private Collection<ValidationMessage> ioValidationMessages = new ArrayList<ValidationMessage>();
  
  public ValidationObject() {}
  
  public ValidationObject(ValidationMessage poValidationMessage) {
    this.ioValidationMessages.add(poValidationMessage);
  }
  
  public ValidationObject(Collection<ValidationMessage> poValidationMessages) {
    this.ioValidationMessages = poValidationMessages;
  }
  
  public void addMessage(ValidationMessage poValidationMessage) {
    this.ioValidationMessages.add(poValidationMessage);
  }
  
  public void addMessage(String pstrMessage, ValidationConstants.MessageType poMessageType) {
    this.ioValidationMessages.add(new ValidationMessage(pstrMessage, poMessageType));
  }
  
  public void addMessage(Object poObject, String pstrMessage, ValidationConstants.MessageType poMessageType) {
    this.ioValidationMessages.add(new ValidationMessage(poObject, pstrMessage, poMessageType));
  }
  
  public void addMessages(Collection<ValidationMessage> poValidationMessages) {
    this.ioValidationMessages.addAll(poValidationMessages);
  }
  
  public Collection<ValidationMessage> getValidationMessages() {
    return this.ioValidationMessages;
  }
  
  public Collection<ValidationMessage> getValidationMessagesByMessageType(ValidationConstants.MessageType poMessageType) {
    Collection<ValidationMessage> loResult = new ArrayList<ValidationMessage>();
    for (ValidationMessage loValidationMessage : this.ioValidationMessages) {
      if (loValidationMessage.getMessageType() == poMessageType)
        loResult.add(loValidationMessage); 
    } 
    return loResult;
  }
  
  public boolean isValid() {
    for (ValidationMessage loValidationMessage : this.ioValidationMessages) {
      if (loValidationMessage.getMessageType() == ValidationConstants.MessageType.INVALID || loValidationMessage.getMessageType() == ValidationConstants.MessageType.ERROR)
        return false; 
    } 
    return true;
  }
}
