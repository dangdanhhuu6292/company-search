package nl.devoorkant.util;

public class EnumUtil {
  public static <T extends Enum<T>> boolean contains(Class<T> enumType, String name) {
    if (enumType != null && name != null)
      try {
        Enum.valueOf(enumType, name);
        return true;
      } catch (IllegalArgumentException e) {} 
    return false;
  }
}
