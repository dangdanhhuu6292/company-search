package nl.devoorkant.text;

public class URIEncoderException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public URIEncoderException() {}
  
  public URIEncoderException(String msg, Throwable throwable) {
    super(msg, throwable);
  }
  
  public URIEncoderException(String msg) {
    super(msg);
  }
  
  public URIEncoderException(Throwable throwable) {
    super(throwable);
  }
}
