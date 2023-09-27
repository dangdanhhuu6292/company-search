package nl.devoorkant.validation.exception;

import nl.devoorkant.exception.DVKException;

public class ValidationException extends DVKException {
  public ValidationException() {}
  
  public ValidationException(String pstrMessage) {
    super(pstrMessage);
  }
  
  public ValidationException(String pstrMessage, Throwable poThrowable) {
    super(pstrMessage, poThrowable);
  }
}
