package nl.devoorkant.random;

public class Seed {
  private long id = 0L;
  
  private String firstRandom = "";
  
  private String secondRandom = "";
  
  private String thirdRandom = "";
  
  public Seed(long id) {
    this(id, "", "", "");
  }
  
  public Seed(long id, String firstRandom, String secondRandom, String thirdRandom) {
    this.id = id;
    this.firstRandom = firstRandom;
    this.secondRandom = secondRandom;
    this.thirdRandom = thirdRandom;
  }
  
  public long getId() {
    return this.id;
  }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public String getFirstRandom() {
    return this.firstRandom;
  }
  
  public void setFirstRandom(String firstRandom) {
    this.firstRandom = firstRandom;
  }
  
  public String getSecondRandom() {
    return this.secondRandom;
  }
  
  public void setSecondRandom(String secondRandom) {
    this.secondRandom = secondRandom;
  }
  
  public String getThirdRandom() {
    return this.thirdRandom;
  }
  
  public void setThirdRandom(String thirdRandom) {
    this.thirdRandom = thirdRandom;
  }
  
  public String toString() {
    String currentNanos = String.valueOf(System.nanoTime());
    StringBuffer sb = new StringBuffer();
    sb.append(this.id).append(this.firstRandom).append(currentNanos).append(this.secondRandom).append(currentNanos).append(this.thirdRandom);
    return sb.toString();
  }
}
