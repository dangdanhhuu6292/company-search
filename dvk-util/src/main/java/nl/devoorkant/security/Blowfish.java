package nl.devoorkant.security;

import java.nio.charset.Charset;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import nl.devoorkant.exception.DVKException;
import nl.devoorkant.util.StringUtil;

public class Blowfish {
  public static String encrypt(String pstrKey, String pstrValue) throws DVKException {
    if (StringUtil.isNotEmptyOrNull(pstrKey) && StringUtil.isNotEmptyOrNull(pstrValue)) {
      byte[] lnaValue = pstrValue.getBytes(Charset.forName("UTF-8"));
      return convertBinaryToHexadecimal(encrypt(pstrKey, lnaValue));
    } 
    throw new DVKException("Unable to encrypt when necessary parameters are missing.");
  }
  
  public static byte[] encrypt(String pstrKey, byte[] pnaValue) throws DVKException {
    try {
      if (StringUtil.isNotEmptyOrNull(pstrKey) && pnaValue != null && pnaValue.length > 0) {
        SecretKeySpec loBlowfishKey = new SecretKeySpec(pstrKey.getBytes(), "Blowfish");
        Cipher loBlowfishCipher = Cipher.getInstance("Blowfish");
        loBlowfishCipher.init(1, loBlowfishKey);
        return loBlowfishCipher.doFinal(pnaValue);
      } 
      throw new DVKException("Unable to encrypt when necessary parameters are missing.");
    } catch (Exception loEx) {
      throw new DVKException("Exception occurred while trying to encrypt: " + loEx.getMessage());
    } 
  }
  
  public static String decrypt(String pstrKey, String pstrValue) throws DVKException {
    if (StringUtil.isNotEmptyOrNull(pstrKey) && StringUtil.isNotEmptyOrNull(pstrValue)) {
      byte[] lnaValue = convertHexadecimalToBinary(pstrValue.getBytes(Charset.forName("UTF-8")));
      return new String(decrypt(pstrKey, lnaValue));
    } 
    throw new DVKException("Unable to decrypt when necessary parameters are missing.");
  }
  
  public static byte[] decrypt(String pstrKey, byte[] pnaValue) throws DVKException {
    try {
      if (StringUtil.isNotEmptyOrNull(pstrKey) && pnaValue != null && pnaValue.length > 0) {
        SecretKeySpec loBlowfishKey = new SecretKeySpec(pstrKey.getBytes(), "Blowfish");
        Cipher loBlowfishCipher = Cipher.getInstance("Blowfish");
        loBlowfishCipher.init(2, loBlowfishKey);
        return loBlowfishCipher.doFinal(pnaValue);
      } 
      throw new DVKException("Unable to decrypt when necessary parameters are missing.");
    } catch (Exception loEx) {
      throw new DVKException("Exception occurred while trying to decrypt:" + loEx.getMessage());
    } 
  }
  
  private static String convertBinaryToHexadecimal(byte[] pnaValue) {
    StringBuffer loStringBuffer = new StringBuffer();
    for (byte lnBinary : pnaValue) {
      int lnBlock = lnBinary & 0xFF;
      loStringBuffer.append("0123456789ABCDEF".charAt(lnBlock >> 4));
      loStringBuffer.append("0123456789ABCDEF".charAt(lnBinary & 0xF));
    } 
    return loStringBuffer.toString();
  }
  
  private static byte[] convertHexadecimalToBinary(byte[] pnaHex) {
    int lnBlock = 0;
    byte[] lnaData = new byte[pnaHex.length / 2];
    int lnIndex = 0;
    boolean lbNext = false;
    for (byte lnHex : pnaHex) {
      lnBlock <<= 4;
      int lnPos = "0123456789ABCDEF".indexOf(Character.toUpperCase((char)lnHex));
      if (lnPos > -1)
        lnBlock += lnPos; 
      if (lbNext) {
        lnaData[lnIndex] = (byte)(lnBlock & 0xFF);
        lnIndex++;
        lbNext = false;
      } else {
        lbNext = true;
      } 
    } 
    return lnaData;
  }
}
