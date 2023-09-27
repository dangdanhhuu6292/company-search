package nl.devoorkant.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.dom.DOMOutputImpl;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

import nl.devoorkant.exception.DVKException;

public class XMLUtil {
  public static interface NodeIterator {
    void visit(Node param1Node) throws TransformerException;
  }
  
  public enum XMLGregorianCalendarFormat {
    DATETIME, DATE, TIME;
  }
  
  public static Date convertToDate(XMLGregorianCalendar poValue) {
    if (poValue != null) {
      GregorianCalendar loCalendar = poValue.toGregorianCalendar();
      return DateUtil.convertToDate(loCalendar);
    } 
    return null;
  }
  
  public static Date convertToSQLDate(XMLGregorianCalendar poValue) {
    if (poValue != null) {
      GregorianCalendar loCalendar = poValue.toGregorianCalendar();
      return DateUtil.convertToSQLDate(loCalendar);
    } 
    return null;
  }
  
  public static XMLGregorianCalendar createXMLGregorianCalendarDate(int pnYear, int pnMonth, int pnDay) throws DVKException {
    GregorianCalendar loCalendar = new GregorianCalendar(pnYear, pnMonth - 1, pnDay);
    return convertToXMLGregorianCalendar(loCalendar, XMLGregorianCalendarFormat.DATE);
  }
  
  public static XMLGregorianCalendar convertToXMLGregorianCalendar(GregorianCalendar poCalendar, XMLGregorianCalendarFormat poFormat) throws DVKException {
    try {
      if (poCalendar != null) {
        XMLGregorianCalendar loXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(poCalendar);
        if (XMLGregorianCalendarFormat.TIME.compareTo(poFormat) == 0) {
          loXMLGregorianCalendar.setYear(-2147483648);
          loXMLGregorianCalendar.setMonth(-2147483648);
          loXMLGregorianCalendar.setDay(-2147483648);
          loXMLGregorianCalendar.setTimezone(-2147483648);
        } else if (XMLGregorianCalendarFormat.DATE.compareTo(poFormat) == 0) {
          loXMLGregorianCalendar.setTime(-2147483648, -2147483648, -2147483648);
          loXMLGregorianCalendar.setMillisecond(-2147483648);
          loXMLGregorianCalendar.setTimezone(-2147483648);
        } else {
          loXMLGregorianCalendar.setTimezone(-2147483648);
        } 
        return loXMLGregorianCalendar;
      } 
    } catch (DatatypeConfigurationException loEx) {
      throw new DVKException("Unable to convert to XMLGregorianCalendar.");
    } 
    return null;
  }
  
  public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date poValue, XMLGregorianCalendarFormat poFormat) throws DVKException {
    if (poValue != null) {
      GregorianCalendar loCalendar = new GregorianCalendar();
      loCalendar.setTime(poValue);
      return convertToXMLGregorianCalendar(loCalendar, poFormat);
    } 
    return null;
  }
  
  public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date poValue, GregorianCalendar poFormat) throws DVKException {
    if (poValue != null) {
      Date loDate = DateUtil.convertUtilToSQLDate(poValue);
      return (loDate != null) ? convertToXMLGregorianCalendar(loDate, poFormat) : null;
    } 
    return null;
  }
  
  public static byte[] serializeObjectToByteArray(Object loObject) throws DVKException {
    try {
      ByteArrayOutputStream loByteArrayOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream loObjectOutputStream = new ObjectOutputStream(loByteArrayOutputStream);
      loObjectOutputStream.writeObject(loObject);
      return loByteArrayOutputStream.toByteArray();
    } catch (IOException loEx) {
      throw new DVKException("Unable to serialize Object.");
    } 
  }
  
  public static String[] toStringArray(StringList poStringList) {
    if (poStringList == null)
      return new String[0]; 
    String[] lstraResult = new String[poStringList.getLength()];
    for (int lnIndex = 0; lnIndex < poStringList.getLength(); lnIndex++)
      lstraResult[lnIndex] = poStringList.item(lnIndex); 
    return lstraResult;
  }
  
  public static String[] toStringArray(XSNamedMap poMap) {
    if (poMap == null)
      return new String[0]; 
    String[] lstraResult = new String[poMap.getLength()];
    for (int lnIndex = 0; lnIndex < poMap.getLength(); lnIndex++)
      lstraResult[lnIndex] = poMap.item(lnIndex).toString(); 
    return lstraResult;
  }
  
  public static String toString(Node poNode) throws DVKException {
    StringWriter loStringWriter = new StringWriter();
    try {
      Transformer loTransformer = TransformerFactory.newInstance().newTransformer();
      loTransformer.setOutputProperty("omit-xml-declaration", "yes");
      loTransformer.transform(new DOMSource(poNode), new StreamResult(loStringWriter));
    } catch (TransformerException loEx) {
      throw new DVKException("Exception during transformation process: " + loEx.getMessage());
    } 
    return loStringWriter.toString();
  }
  
  public static String toXMLString(Node poNode) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    return toXMLString(poNode, System.getProperty("file.encoding"));
  }
  
  public static String toXMLString(Node poNode, String pstrEncoding) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    StringWriter loWriter = new StringWriter();
    DOMImplementationRegistry loRegistry = DOMImplementationRegistry.newInstance();
    DOMImplementationLS loDOMImplementation = (DOMImplementationLS)loRegistry.getDOMImplementation("");
    LSSerializer loSerializer = loDOMImplementation.createLSSerializer();
    DOMOutputImpl dOMOutputImpl = new DOMOutputImpl();
    dOMOutputImpl.setEncoding(pstrEncoding);
    dOMOutputImpl.setCharacterStream(loWriter);
    loSerializer.write(poNode, (LSOutput)dOMOutputImpl);
    return loWriter.getBuffer().toString();
  }
  
  public static Document parse(String pstrXml) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    DOMImplementationRegistry loRegistry = DOMImplementationRegistry.newInstance();
    DOMImplementationLS loDOMImplementation = (DOMImplementationLS)loRegistry.getDOMImplementation("");
    LSParser loParser = loDOMImplementation.createLSParser((short)1, "http://www.w3.org/2001/XMLSchema");
    LSInput loLSInput = loDOMImplementation.createLSInput();
    loLSInput.setStringData(pstrXml);
    return loParser.parse(loLSInput);
  }
  
  public static Document parse(byte[] pnaBytes) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    DOMImplementationRegistry loRegistry = DOMImplementationRegistry.newInstance();
    DOMImplementationLS loDOMImplementation = (DOMImplementationLS)loRegistry.getDOMImplementation("");
    LSParser loParser = loDOMImplementation.createLSParser((short)1, "http://www.w3.org/2001/XMLSchema");
    LSInput loLSInput = loDOMImplementation.createLSInput();
    loLSInput.setByteStream(new ByteArrayInputStream(pnaBytes));
    return loParser.parse(loLSInput);
  }
  
  public static Document parse(String pstrSystemId, InputStream poInputStream) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    DOMImplementationRegistry loRegistry = DOMImplementationRegistry.newInstance();
    DOMImplementationLS loDOMImplementation = (DOMImplementationLS)loRegistry.getDOMImplementation("");
    LSParser loParser = loDOMImplementation.createLSParser((short)1, "http://www.w3.org/2001/XMLSchema");
    LSInput loLSInput = loDOMImplementation.createLSInput();
    loLSInput.setSystemId(pstrSystemId);
    loLSInput.setByteStream(poInputStream);
    return loParser.parse(loLSInput);
  }
  
  public static Document newDocument(String pstrDocElemName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    DOMImplementationRegistry loRegistry = DOMImplementationRegistry.newInstance();
    DOMImplementation loDOMImplementation = loRegistry.getDOMImplementation("");
    return loDOMImplementation.createDocument(null, pstrDocElemName, null);
  }
  
  public static void iterate(CachedXPathAPI poCachedXPathAPI, Node poNode, String pstrXPath, NodeIterator poNodeIterator) throws TransformerException {
    NodeList loNodeList = poCachedXPathAPI.selectNodeList(poNode, pstrXPath);
    for (int lnIndex = 0; lnIndex < loNodeList.getLength(); lnIndex++)
      poNodeIterator.visit(loNodeList.item(lnIndex)); 
  }
  
  public static int maxOccurs(Node poNode) {
    int lnResult = -1;
    ElementPSVI loElementPSVI = (ElementPSVI)poNode;
    if (loElementPSVI.getElementDeclaration() != null && loElementPSVI.getElementDeclaration().getEnclosingCTDefinition() != null) {
      XSModelGroup loModelGroup = (XSModelGroup)loElementPSVI.getElementDeclaration().getEnclosingCTDefinition().getParticle().getTerm();
      for (int lnIndex = 0; lnIndex < loModelGroup.getParticles().getLength(); lnIndex++) {
        XSParticle loParticle = (XSParticle)loModelGroup.getParticles().item(lnIndex);
        if (poNode.getNodeName().equals(loParticle.getTerm().getName())) {
          lnResult = loParticle.getMaxOccurs();
          break;
        } 
      } 
    } 
    return lnResult;
  }
  
  public static String[] getEnumerationValues(Node poNode) {
    String[] lstraResult = new String[0];
    ElementPSVI loElementPSVI = (ElementPSVI)poNode;
    if (loElementPSVI.getElementDeclaration() != null) {
      XSElementDeclaration loElemDecl = loElementPSVI.getElementDeclaration();
      if (loElemDecl.getTypeDefinition() instanceof XSSimpleTypeDefinition)
        lstraResult = toStringArray(((XSSimpleTypeDefinition)loElemDecl.getTypeDefinition()).getLexicalEnumeration()); 
    } 
    return lstraResult;
  }
  
  public static int position(CachedXPathAPI poXpathAPI, Node poNode) throws TransformerException {
    int lnPosition = 0;
    if (maxOccurs(poNode) > 1) {
      NodeList loNodeList = poXpathAPI.selectNodeList(poNode, "../" + poNode.getNodeName());
      for (; lnPosition < loNodeList.getLength(); lnPosition++) {
        if (loNodeList.item(lnPosition).equals(poNode)) {
          lnPosition++;
          break;
        } 
      } 
    } 
    return lnPosition;
  }
  
  public static String nodePath(CachedXPathAPI poXPathAPI, Node poNode) throws TransformerException {
    String lstrResult = "";
    if (poNode != null && poNode.getParentNode() != null) {
      int lnPosition = position(poXPathAPI, poNode);
      String lstrPosition = "";
      if (lnPosition > 0)
        lstrPosition = "[" + lnPosition + "]"; 
      lstrResult = nodePath(poXPathAPI, poNode.getParentNode()) + "/" + poNode.getNodeName() + lstrPosition;
    } 
    return lstrResult;
  }
  
  public static String escape(String pstrValue) {
    String lstrResult = null;
    if (pstrValue != null) {
      lstrResult = StringUtil.replace(pstrValue, "&", "&amp;");
      lstrResult = StringUtil.replace(lstrResult, "<", "&lt;");
      lstrResult = StringUtil.replace(lstrResult, ">", "&gt;");
      lstrResult = StringUtil.replace(lstrResult, "\"", "&quot;");
    } 
    return lstrResult;
  }
  
  public static String jsEscape(String pstrXml) {
    return StringUtil.replace(escape(pstrXml), "'", "\\'");
  }
  
  public static void writeFile(String pstrFileName, Document poDocument) throws Exception {
    writeFile(pstrFileName, poDocument, System.getProperty("file.encoding"));
  }
  
  public static void writeFile(String pstrFileName, Document poDocument, String pstrEncoding) throws Exception {
    FileWriter loWriter = null;
    try {
      loWriter = new FileWriter(pstrFileName);
      DOMImplementationRegistry loRegistry = DOMImplementationRegistry.newInstance();
      DOMImplementationLS loDOMImplementation = (DOMImplementationLS)loRegistry.getDOMImplementation("");
      LSSerializer loSerializer = loDOMImplementation.createLSSerializer();
      DOMOutputImpl dOMOutputImpl = new DOMOutputImpl();
      dOMOutputImpl.setEncoding(pstrEncoding);
      dOMOutputImpl.setCharacterStream(loWriter);
      loSerializer.write(poDocument, (LSOutput)dOMOutputImpl);
    } finally {
      if (loWriter != null)
        loWriter.close(); 
    } 
  }
}
