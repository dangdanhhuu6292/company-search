package nl.devoorkant.text;

import java.io.UnsupportedEncodingException;

public class URIEncoder {
  public static String encode(String aString) throws URIEncoderException {
    int length = aString.length();
    StringBuffer uri = new StringBuffer(length);
    for (int i = 0; i < length; i++) {
      char c = aString.charAt(i);
      switch (c) {
        case ' ':
          uri.append("%20");
          break;
        case '!':
          uri.append("%21");
          break;
        case '"':
          uri.append("%22");
          break;
        case '#':
          uri.append("%23");
          break;
        case '$':
          uri.append("%24");
          break;
        case '%':
          uri.append("%25");
          break;
        case '&':
          uri.append("%26");
          break;
        case '\'':
          uri.append("%27");
          break;
        case '(':
          uri.append("%28");
          break;
        case ')':
          uri.append("%29");
          break;
        case '*':
          uri.append("%2A");
          break;
        case '+':
          uri.append("%2B");
          break;
        case ',':
          uri.append("%2C");
          break;
        case '-':
          uri.append("%2D");
          break;
        case '.':
          uri.append("%2E");
          break;
        case '/':
          uri.append("%2F");
          break;
        case '0':
          uri.append(c);
          break;
        case '1':
          uri.append(c);
          break;
        case '2':
          uri.append(c);
          break;
        case '3':
          uri.append(c);
          break;
        case '4':
          uri.append(c);
          break;
        case '5':
          uri.append(c);
          break;
        case '6':
          uri.append(c);
          break;
        case '7':
          uri.append(c);
          break;
        case '8':
          uri.append(c);
          break;
        case '9':
          uri.append(c);
          break;
        case ':':
          uri.append("%3A");
          break;
        case ';':
          uri.append("%3B");
          break;
        case '<':
          uri.append("%3C");
          break;
        case '=':
          uri.append("%3D");
          break;
        case '>':
          uri.append("%3E");
          break;
        case '?':
          uri.append("%3F");
          break;
        case '@':
          uri.append("%40");
          break;
        case 'A':
          uri.append(c);
          break;
        case 'B':
          uri.append(c);
          break;
        case 'C':
          uri.append(c);
          break;
        case 'D':
          uri.append(c);
          break;
        case 'E':
          uri.append(c);
          break;
        case 'F':
          uri.append(c);
          break;
        case 'G':
          uri.append(c);
          break;
        case 'H':
          uri.append(c);
          break;
        case 'I':
          uri.append(c);
          break;
        case 'J':
          uri.append(c);
          break;
        case 'K':
          uri.append(c);
          break;
        case 'L':
          uri.append(c);
          break;
        case 'M':
          uri.append(c);
          break;
        case 'N':
          uri.append(c);
          break;
        case 'O':
          uri.append(c);
          break;
        case 'P':
          uri.append(c);
          break;
        case 'Q':
          uri.append(c);
          break;
        case 'R':
          uri.append(c);
          break;
        case 'S':
          uri.append(c);
          break;
        case 'T':
          uri.append(c);
          break;
        case 'U':
          uri.append(c);
          break;
        case 'V':
          uri.append(c);
          break;
        case 'W':
          uri.append(c);
          break;
        case 'X':
          uri.append(c);
          break;
        case 'Y':
          uri.append(c);
          break;
        case 'Z':
          uri.append(c);
          break;
        case '[':
          uri.append("%5B");
          break;
        case '\\':
          uri.append("%5C");
          break;
        case ']':
          uri.append("%5D");
          break;
        case '^':
          uri.append("%5E");
          break;
        case '_':
          uri.append("%5F");
          break;
        case '`':
          uri.append("%60");
          break;
        case 'a':
          uri.append(c);
          break;
        case 'b':
          uri.append(c);
          break;
        case 'c':
          uri.append(c);
          break;
        case 'd':
          uri.append(c);
          break;
        case 'e':
          uri.append(c);
          break;
        case 'f':
          uri.append(c);
          break;
        case 'g':
          uri.append(c);
          break;
        case 'h':
          uri.append(c);
          break;
        case 'i':
          uri.append(c);
          break;
        case 'j':
          uri.append(c);
          break;
        case 'k':
          uri.append(c);
          break;
        case 'l':
          uri.append(c);
          break;
        case 'm':
          uri.append(c);
          break;
        case 'n':
          uri.append(c);
          break;
        case 'o':
          uri.append(c);
          break;
        case 'p':
          uri.append(c);
          break;
        case 'q':
          uri.append(c);
          break;
        case 'r':
          uri.append(c);
          break;
        case 's':
          uri.append(c);
          break;
        case 't':
          uri.append(c);
          break;
        case 'u':
          uri.append(c);
          break;
        case 'v':
          uri.append(c);
          break;
        case 'w':
          uri.append(c);
          break;
        case 'x':
          uri.append(c);
          break;
        case 'y':
          uri.append(c);
          break;
        case 'z':
          uri.append(c);
          break;
        case '{':
          uri.append("%7B");
          break;
        case '|':
          uri.append("%7C");
          break;
        case '}':
          uri.append("%7D");
          break;
        case '~':
          uri.append(c);
          break;
        default:
          uri.append(percentEscape(c));
          break;
      } 
    } 
    return uri.toString();
  }
  
  private static String percentEscape(char c) throws URIEncoderException {
    StringBuffer result = new StringBuffer(3);
    String s = String.valueOf(c);
    try {
      byte[] data = s.getBytes("UTF8");
      for (int i = 0; i < data.length; i++) {
        result.append('%');
        String hex = Integer.toHexString(data[i]).toUpperCase();
        if (c < '\020') {
          result.append('0');
          result.append(hex);
        } else {
          result.append(hex.substring(hex.length() - 2));
        } 
      } 
      return result.toString();
    } catch (UnsupportedEncodingException ex) {
      throw new URIEncoderException("unsupported encoding 'UTF-8'");
    } 
  }
}
