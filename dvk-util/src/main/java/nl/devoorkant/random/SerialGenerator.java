package nl.devoorkant.random;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.devoorkant.exception.DVKException;

public class SerialGenerator {
  private static final String HEXES = "0123456789ABCDEF";
  
  private static final String SR_ALGORITHM = "SHA1PRNG";
  
  private static final String MD5_ALGORITHM = "MD5";
  
  private static final String SHA_ALGORITHM = "SHA1";
  
  private static final int MAX_RETRIES = 1000;
  
  private Seed seed;
  
  private String separatorSign;
  
  private int separatorPos;
  
  private String prefix;
  
  public SerialGenerator(Seed seed) {
    this(seed, "", 0, "");
  }
  
  public SerialGenerator(Seed seed, String separatorSign, int separatorPos, String prefix) {
    this.seed = seed;
    this.separatorSign = separatorSign;
    this.separatorPos = separatorPos;
    this.prefix = prefix;
  }
  
  public List<String> generateKeySets(int keySets, int keyLength) throws DVKException {
    List<String> loResult = new ArrayList<String>();
    int calculatedCharLength = generateMinimalKeyPositions(keySets, keyLength);
    List<Integer> randomPositions = generatePositionList(calculatedCharLength);
    for (int i = 0; i < keySets; i++) {
      String generatedKeySet = "";
      this.seed.setId(this.seed.getId() + i);
      String randomString = generateRandomString(this.seed, keyLength);
      boolean unique = false;
      int idx = 0;
      int tries = 0;
      do {
        tries++;
        if (tries >= 1000)
          throw new DVKException("maximum retries reached while generating unique key. Giving up..."); 
        label31: for (Integer currPos : randomPositions) {
          String c = Character.toString(randomString.charAt(currPos.intValue()));
          boolean foundChar = false;
          while (true) {
            if (c.length() == 1) {
              generatedKeySet = generatedKeySet + c;
              foundChar = true;
              idx++;
              if (this.separatorPos != 0 && idx % this.separatorPos == 0 && idx < randomPositions.size())
                generatedKeySet = generatedKeySet + this.separatorSign; 
            } 
            if (foundChar)
              continue label31; 
          } 
        } 
        if (loResult.contains(generatedKeySet))
          continue; 
        unique = true;
      } while (!unique);
      loResult.add((!"".equals(this.prefix) ? (this.prefix + this.separatorSign) : this.prefix) + generatedKeySet);
    } 
    return loResult;
  }
  
  private List<Integer> generatePositionList(int positions) throws DVKException {
    List<Integer> loResult = new ArrayList<Integer>();
    SecureRandom loSecureRandom = null;
    try {
      loSecureRandom = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException loNSAEx) {
      throw new DVKException(loNSAEx.getMessage());
    } 
    loSecureRandom.setSeed(System.nanoTime());
    String initRandomSeed = generateRandomString(new Seed(1L, "", "", ""), positions);
    for (int j = 0; j < positions; ) {
      boolean unique = false;
      while (true) {
        int pos = loSecureRandom.nextInt(initRandomSeed.length());
        if (!loResult.contains(Integer.valueOf(pos))) {
          loResult.add(Integer.valueOf(pos));
          unique = true;
        } 
        if (unique)
          j++; 
      } 
    } 
    return loResult;
  }
  
  private int generateMinimalKeyPositions(int totalKeySets, int userDefinedKeyNumber) {
    int result = (int)(Math.log((totalKeySets + 1)) / Math.log(16.0D) + 1.0D) + 2;
    return (userDefinedKeyNumber >= result) ? userDefinedKeyNumber : result;
  }
  
  private String generateRandomString(Seed input, int minLength) throws DVKException {
    String hash = input.toString();
    String md5 = byteArrayToHex(digest(hash, "MD5"));
    String sha1 = byteArrayToHex(digest(hash, "SHA1"));
    String result = md5 + sha1;
    while (result.length() < minLength)
      result = result + md5 + sha1; 
    return result;
  }
  
  private String combineStrings(String s1, String s2) {
    char[] ca1 = s1.toCharArray();
    char[] ca2 = s2.toCharArray();
    char[] result = new char[ca1.length + ca2.length];
    int rest = 0;
    for (int i = 0; i < ca1.length; i += 2) {
      result[i] = ca1[i];
      if (ca1.length < ca2.length)
        result[i + 1] = ca2[i]; 
      if (ca1.length >= ca2.length)
        rest++; 
    } 
    if (rest > 0);
    return new String(result);
  }
  
  private byte[] digest(String s, String algorithm) throws DVKException {
    List<String> loSupported = Arrays.asList(new String[] { "MD5", "SHA1" });
    if (!loSupported.contains(algorithm))
      throw new DVKException("The defined algorithm is not loSupported."); 
    MessageDigest loDigest = null;
    try {
      loDigest = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException loNSAEx) {
      throw new DVKException(loNSAEx.getMessage());
    } 
    loDigest.update(s.getBytes());
    return loDigest.digest();
  }
  
  private byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2)
      data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16)); 
    return data;
  }
  
  private String byteArrayToHex(byte[] ba) {
    if (ba == null)
      return null; 
    StringBuilder hex = new StringBuilder(2 * ba.length);
    for (byte b : ba)
      hex.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4)).append("0123456789ABCDEF".charAt(b & 0xF)); 
    return hex.toString();
  }
}
