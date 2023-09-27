package nl.devoorkant.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import nl.devoorkant.exception.DVKException;

public class StringUtil {
  private static SecureRandom ioSecureRandom = new SecureRandom();
  
  private static String lstrHexServerIP = null;
  
  public static boolean isEmptyOrNull(String pstrValue) {
    return (pstrValue == null || pstrValue.trim().length() == 0);
  }
  
  public static boolean isNotEmptyOrNull(String pstrValue) {
    return (pstrValue != null && pstrValue.trim().length() != 0);
  }
  
  public static ArrayList<String> split(String pstrText, char pcSeparator) {
    ArrayList<String> lstraSplitted = new ArrayList<String>();
    int lnIndex = 0;
    if (pstrText != null) {
      lnIndex = pstrText.indexOf(pcSeparator, lnIndex);
      if (lnIndex == -1) {
        lstraSplitted.add(pstrText);
      } else {
        lstraSplitted.add(pstrText.substring(0, lnIndex));
        int lnOffset = lnIndex;
        while ((lnIndex = pstrText.indexOf(pcSeparator, lnIndex)) > -1)
          lstraSplitted.add(pstrText.substring(lnOffset, lnIndex)); 
      } 
    } 
    return lstraSplitted;
  }
  
  public static ArrayList<String> split(String pstrText, String pstrSeparator) {
    ArrayList<String> lstraSplitted = new ArrayList<String>();
    int lnIndex = 0;
    if (pstrText != null) {
      lnIndex = pstrText.indexOf(pstrSeparator, lnIndex);
      if (lnIndex == -1) {
        lstraSplitted.add(pstrText);
      } else {
        lstraSplitted.add(pstrText.substring(0, lnIndex++).trim());
        int lnOffset = lnIndex;
        while ((lnIndex = pstrText.indexOf(pstrSeparator, lnIndex)) > -1) {
          lstraSplitted.add(pstrText.substring(lnOffset, lnIndex++).trim());
          lnOffset = lnIndex;
        } 
        lstraSplitted.add(pstrText.substring(lnOffset).trim());
      } 
    } 
    return lstraSplitted;
  }
  
  public static String replace(String pstrValue, String pstrSearch, String pstrReplace) {
    int lnOffset = 0;
    String lstrValue = pstrValue;
    if (lstrValue != null)
      while ((lnOffset = lstrValue.indexOf(pstrSearch, lnOffset)) != -1) {
        lstrValue = lstrValue.substring(0, lnOffset) + pstrReplace + lstrValue.substring(lnOffset + pstrSearch.length());
        lnOffset += pstrReplace.length();
      }  
    return lstrValue;
  }
  
  public static String escapeBackslash(String pstrValue) {
    String lstrResult = "";
    if (pstrValue != null && !pstrValue.equals("")) {
      int lnIndex = 0;
      while (lnIndex < pstrValue.length()) {
        if (pstrValue.charAt(lnIndex) == '\\') {
          lstrResult = lstrResult + "\\\\";
        } else {
          lstrResult = lstrResult + pstrValue.charAt(lnIndex);
        } 
        lnIndex++;
      } 
    } 
    return lstrResult;
  }
  
  public static String truncate(String pstrValue, int pnLength) {
    String lstrResult = null;
    if (pstrValue != null)
      lstrResult = (pstrValue.length() > pnLength) ? pstrValue.substring(0, pnLength) : pstrValue; 
    return lstrResult;
  }
  
  public static String removeChars(String pstrValue, char[] paToRemove) {
    StringBuffer r = new StringBuffer();
    if (pstrValue != null)
      for (int i = 0; i < pstrValue.length(); i++) {
        if (!contains(pstrValue.charAt(i), paToRemove))
          r.append(pstrValue.charAt(i)); 
      }  
    return r.toString();
  }
  
  public static boolean contains(char c, char[] paToSearch) {
    for (char a : paToSearch) {
      if (c == a)
        return true; 
    } 
    return false;
  }
  
  public static String getRandomString() {
    return getRandomString(7);
  }
  
  public static String getRandomString(int pnLength) {
    StringBuffer loResult = new StringBuffer();
    String[] lstraFirst = { 
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
        "U", "V", "W" };
    String[] lstraSecond = { "1", "2", "3", "4", "6", "7", "8", "9", "0" };
    String[] lstraThird = { 
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", 
        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", 
        "u", "v", "w" };
    ArrayList<String> loCharacterList = new ArrayList<String>();
    loCharacterList.addAll(Arrays.asList(lstraFirst));
    loCharacterList.addAll(Arrays.asList(lstraSecond));
    loCharacterList.addAll(Arrays.asList(lstraThird));
    int lnLength = loCharacterList.size() - 1;
    for (int i = 0; i < pnLength; i++)
      loResult.append(loCharacterList.get((int)(Math.random() * lnLength))); 
    return loResult.toString();
  }
  
  public static String getUniqueString_ID() throws DVKException {
    return getUniqueString_ID("DVK");
  }
  
  public static String getUniqueString_ID(Object poObject) throws DVKException {
    StringBuffer loResult = new StringBuffer(32);
    StringBuffer loBuffer = new StringBuffer(16);
    if (lstrHexServerIP == null) {
      InetAddress loInetAddress = null;
      try {
        loInetAddress = InetAddress.getLocalHost();
      } catch (UnknownHostException loEx) {
        throw new DVKException("Unable to retrieve the local IP address using InetAddress.getLocalHost()! Cause: " + loEx.getMessage());
      } 
      lstrHexServerIP = getHexFormat(getInt(loInetAddress.getAddress()), 8);
    } 
    String lstrHashCode = getHexFormat(System.identityHashCode(poObject), 8);
    loBuffer.append(lstrHexServerIP);
    loBuffer.append(lstrHashCode);
    int timeLow = (int)System.currentTimeMillis();
    int node = ioSecureRandom.nextInt();
    loResult.append(getHexFormat(timeLow, 8));
    loResult.append(loBuffer.toString());
    loResult.append(getHexFormat(node, 8));
    return loResult.toString();
  }
  
  public static String join(Collection<String> collection, String delimiter) {
    StringBuffer buffer = new StringBuffer();
    Iterator<String> iter = collection.iterator();
    while (iter.hasNext()) {
      buffer.append(iter.next());
      if (iter.hasNext())
        buffer.append(delimiter); 
    } 
    return buffer.toString();
  }
  
  private static int getInt(byte[] pbaValue) {
    int i = 0;
    int j = 24;
    for (int k = 0; j >= 0; k++) {
      int l = pbaValue[k] & 0xFF;
      i += l << j;
      j -= 8;
    } 
    return i;
  }
  
  private static String getHexFormat(int i, int j) {
    String lstrString = Integer.toHexString(i);
    return padHex(lstrString, j) + lstrString;
  }
  
  private static String padHex(String pstrValue, int i) {
    StringBuffer loBuffer = new StringBuffer();
    if (pstrValue.length() < i)
      for (int j = 0; j < i - pstrValue.length(); j++)
        loBuffer.append('0');  
    return loBuffer.toString();
  }
}
