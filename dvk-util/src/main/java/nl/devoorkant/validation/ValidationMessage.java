package nl.devoorkant.validation;

import java.io.Serializable;

public class ValidationMessage implements Serializable {
  private String istrMessage;
  
  private Object ioObject;
  
  private ValidationConstants.MessageType ioMessageType;
  
  public ValidationMessage() {}
  
  public ValidationMessage(String pstrMessage, ValidationConstants.MessageType poMessageType) {
    this.istrMessage = pstrMessage;
    this.ioMessageType = poMessageType;
  }
  
  public ValidationMessage(Object poObject, String pstrMessage, ValidationConstants.MessageType poMessageType) {
    this.ioObject = poObject;
    this.istrMessage = pstrMessage;
    this.ioMessageType = poMessageType;
  }
  
  public void setObject(Object poObject) {
    this.ioObject = poObject;
  }
  
  public Object getObject() {
    return this.ioObject;
  }
  
  public void setMessage(String pstrMessage) {
    this.istrMessage = pstrMessage;
  }
  
  public String getMessage() {
    return this.istrMessage;
  }
  
  public void setMessageType(ValidationConstants.MessageType poMessageType) {
    this.ioMessageType = poMessageType;
  }
  
  public ValidationConstants.MessageType getMessageType() {
    return this.ioMessageType;
  }
}
