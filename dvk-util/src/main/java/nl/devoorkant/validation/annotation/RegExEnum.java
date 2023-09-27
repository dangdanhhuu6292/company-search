package nl.devoorkant.validation.annotation;

public enum RegExEnum {
  GENERIC(""),
  EMAIL("(\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,3})"),
  NL_POSTCODE("^[1-9]{1}[0-9]{3}\\s?[A-Z]{2}$");
  
  private String expression;
  
  RegExEnum(String expression) {
    this.expression = expression;
  }
  
  public String getExpression() {
    return this.expression;
  }
}
