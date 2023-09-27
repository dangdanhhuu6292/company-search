package nl.devoorkant.exception;

public class DVKException extends Exception {
  public DVKException() {}
  
  public DVKException(String pstrMessage) {
    super(pstrMessage);
  }
  
  public DVKException(String pstrMessage, Throwable poThrowable) {
    super(pstrMessage, poThrowable);
  }
}
