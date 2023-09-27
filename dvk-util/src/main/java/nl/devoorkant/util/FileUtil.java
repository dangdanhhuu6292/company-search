package nl.devoorkant.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import nl.devoorkant.exception.DVKException;
import org.w3c.dom.Document;

public class FileUtil {
  public static String readFile(String pstrFileName) throws DVKException {
    try {
      FileInputStream loInputStream = new FileInputStream(pstrFileName);
      return parseInputStreamToString(loInputStream);
    } catch (FileNotFoundException loEx) {
      throw new DVKException("File not found: " + loEx.getMessage());
    } 
  }
  
  public static String parseInputStreamToString(InputStream loInputStream) throws DVKException {
    StringBuffer loResult = new StringBuffer();
    try {
      BufferedReader loReader = new BufferedReader(new InputStreamReader(loInputStream, "UTF-8"));
      String lstrLine;
      while ((lstrLine = loReader.readLine()) != null)
        loResult.append(lstrLine).append("\n"); 
    } catch (Exception loEx) {
      throw new DVKException("Error parsing InputStream: " + loEx.getMessage());
    } finally {
      try {
        loInputStream.close();
      } catch (IOException loEx) {}
    } 
    return loResult.toString();
  }
  
  public static InputStream parseXMLStringToInputStream(String pstrXML) throws DVKException {
    if (pstrXML == null)
      return null; 
    pstrXML = pstrXML.trim();
    try {
      return new ByteArrayInputStream(pstrXML.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException loEx) {
      throw new DVKException("Invalid encoding: " + loEx.getMessage());
    } 
  }
  
  public static boolean writeToFile(byte[] pbaContent, String pstrFilename) throws DVKException {
    BufferedOutputStream loOut = null;
    try {
      loOut = new BufferedOutputStream(new FileOutputStream(pstrFilename, false));
      loOut.write(pbaContent);
      return true;
    } catch (IOException loEx) {
      throw new DVKException("Error writing to file: " + loEx.getMessage());
    } finally {
      if (loOut != null)
        try {
          loOut.close();
        } catch (IOException e) {} 
    } 
  }
  
  public static void flow(InputStream is, OutputStream os, byte[] buf) throws IOException {
    int numRead;
    while ((numRead = is.read(buf)) >= 0)
      os.write(buf, 0, numRead); 
  }
  
  public static void writeToFile(InputStream poInStream, String pstrFilename) throws DVKException {
    BufferedOutputStream loOut = null;
    try {
      loOut = new BufferedOutputStream(new FileOutputStream(pstrFilename));
      byte[] buf = new byte[1024];
      int numRead;
      while ((numRead = poInStream.read(buf)) >= 0)
        loOut.write(buf, 0, numRead); 
    } catch (IOException loEx) {
      throw new DVKException("Error writing to file: " + loEx.getMessage());
    } finally {
      if (loOut != null)
        try {
          loOut.close();
        } catch (IOException e) {} 
    } 
  }
  
  public static boolean writeXmlFile(Document poXMLContent, String pstrFilename, String pstrEncoding) throws DVKException {
    try {
      Source loSource = new DOMSource(poXMLContent);
      File loFile = new File(pstrFilename);
      Result loResult = new StreamResult(loFile);
      Transformer loXFormer = TransformerFactory.newInstance().newTransformer();
      Properties loProperties = new Properties();
      loProperties.setProperty("encoding", (pstrEncoding != null) ? pstrEncoding : "UTF-8");
      loProperties.setProperty("version", "1");
      loXFormer.setOutputProperties(loProperties);
      loXFormer.transform(loSource, loResult);
      return true;
    } catch (Exception loEx) {
      throw new DVKException("Error writing XML file: " + loEx.getMessage());
    } 
  }
  
  public static Set<File> getFilesFromDirectory(String pstrDirectory) {
    Set<File> loResult = null;
    if (StringUtil.isNotEmptyOrNull(pstrDirectory)) {
      File loFile = new File(pstrDirectory);
      loResult = getFilesFromDirectory(loFile);
    } 
    return loResult;
  }
  
  public static Set<File> getFilesFromDirectory(File poDir) {
    Set<File> loResult = null;
    if (poDir != null && poDir.listFiles() != null)
      for (File fileEntry : poDir.listFiles()) {
        if (fileEntry.isDirectory()) {
          Set<File> loTempSet = getFilesFromDirectory(fileEntry);
          if (loTempSet != null)
            if (loResult != null) {
              loResult.addAll(loTempSet);
            } else {
              loResult = new HashSet<File>();
              loResult.addAll(loTempSet);
            }  
        } else if (loResult != null) {
          loResult.add(fileEntry);
        } else {
          loResult = new HashSet<File>();
          loResult.add(fileEntry);
        } 
      }  
    return loResult;
  }
  
  public static boolean doesFolderContainFiles(String pstrDirectory) {
    return (getFilesFromDirectory(pstrDirectory) != null);
  }
}
