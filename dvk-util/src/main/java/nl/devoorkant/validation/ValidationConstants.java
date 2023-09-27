package nl.devoorkant.validation;

public class ValidationConstants {
  @Deprecated
  public static final int MESSAGE_TYPE_VALID = 0;
  
  @Deprecated
  public static final int MESSAGE_TYPE_INVALID = 1;
  
  @Deprecated
  public static final int MESSAGE_TYPE_INFO = 2;
  
  @Deprecated
  public static final int MESSAGE_TYPE_ERROR = 3;
  
  public enum MessageType {
    NONE, VALID, INVALID, INFO, ERROR;
  }
}
