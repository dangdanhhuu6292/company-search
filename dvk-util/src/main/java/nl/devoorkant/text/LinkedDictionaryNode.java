package nl.devoorkant.text;

public final class LinkedDictionaryNode<K, V> {
  private LinkedDictionaryNode<K, V> ioParent;
  
  private LinkedDictionaryNode<K, V> ioChild;
  
  private K ioKey;
  
  private V ioValue;
  
  public LinkedDictionaryNode(K poKey, V poValue) {
    if (poKey == null)
      throw new IllegalArgumentException("key object is null. The key is mandatory"); 
    this.ioKey = poKey;
    this.ioValue = poValue;
  }
  
  public LinkedDictionaryNode(LinkedDictionaryNode<K, V> poParent, K poKey, V poValue) {
    if (poKey == null)
      throw new IllegalArgumentException("key object is null. The key is mandatory"); 
    this.ioParent = poParent;
    this.ioKey = poKey;
    this.ioValue = poValue;
  }
  
  public V getValue() {
    return this.ioValue;
  }
  
  protected void setValue(V poValue) {
    this.ioValue = poValue;
  }
  
  public K getKey() {
    return this.ioKey;
  }
  
  public LinkedDictionaryNode<K, V> getParent() {
    return this.ioParent;
  }
  
  public void setParent(LinkedDictionaryNode<K, V> poParent) {
    this.ioParent = poParent;
  }
  
  public LinkedDictionaryNode<K, V> getChild() {
    return this.ioChild;
  }
  
  public void setChild(LinkedDictionaryNode<K, V> poChild) {
    this.ioChild = poChild;
  }
  
  public boolean isRootNode() {
    return (this.ioParent == null);
  }
  
  public boolean isLeafNode() {
    return (this.ioChild == null);
  }
}
