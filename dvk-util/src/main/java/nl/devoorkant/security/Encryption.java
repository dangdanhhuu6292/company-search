package nl.devoorkant.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import nl.devoorkant.exception.DVKException;

public class Encryption {
  private static byte[] encrypt(byte[] pnaValue, String pstrEncryptionAlgorithm) throws NoSuchAlgorithmException {
    if (pnaValue != null && pnaValue.length > 0) {
      MessageDigest loMessageDigest = MessageDigest.getInstance(pstrEncryptionAlgorithm);
      return loMessageDigest.digest(pnaValue);
    } 
    return null;
  }
  
  public static String encrypt(String pstrValue) throws DVKException {
    try {
      if (pstrValue != null && pstrValue.trim().length() > 0) {
        byte[] lnaValue = pstrValue.getBytes();
        byte[] lnaResult = encrypt(lnaValue, "SHA-256");
        return (lnaResult != null) ? formatHexString(lnaResult) : null;
      } 
    } catch (NoSuchAlgorithmException loEx) {
      throw new DVKException("Unable to encrypt: " + loEx.getMessage());
    } 
    return null;
  }
  
  public static String encrypt(String pstrValue, String pstrEncryptionAlgorithm) throws DVKException {
    try {
      if (pstrValue != null && pstrValue.trim().length() > 0) {
        byte[] lnaValue = pstrValue.getBytes();
        byte[] lnaResult = encrypt(lnaValue, pstrEncryptionAlgorithm);
        return (lnaResult != null) ? formatHexString(lnaResult) : null;
      } 
    } catch (NoSuchAlgorithmException loEx) {
      throw new DVKException("Unable to encrypt: " + loEx.getMessage());
    } 
    return null;
  }
  
  private static String formatHexString(byte[] pnaValue) {
    if (pnaValue != null && pnaValue.length > 0) {
      StringBuffer loBuffer = new StringBuffer(pnaValue.length * 2);
      for (byte pnaByte : pnaValue) {
        int lnHexValue = pnaByte & 0xFF;
        if (lnHexValue < 16)
          loBuffer.append('0'); 
        loBuffer.append(Integer.toHexString(lnHexValue));
      } 
      return loBuffer.toString().toUpperCase();
    } 
    return null;
  }
}
