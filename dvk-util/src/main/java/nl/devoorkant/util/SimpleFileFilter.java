package nl.devoorkant.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class SimpleFileFilter implements FileFilter {
  private String istrDescription;
  
  private Pattern[] ioaPatterns = new Pattern[0];
  
  public SimpleFileFilter(String pstrDescription, Pattern[] poaPatterns) {
    this.istrDescription = pstrDescription;
    this.ioaPatterns = poaPatterns;
  }
  
  public boolean accept(File poFile) {
    for (Pattern loPattern : this.ioaPatterns) {
      if (loPattern.matcher(poFile.getName()).matches())
        return true; 
    } 
    return false;
  }
  
  public String getDescription() {
    return this.istrDescription;
  }
}
