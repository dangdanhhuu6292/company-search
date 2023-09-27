package nl.devoorkant.text;

import java.util.Hashtable;

public class LinkedDictionary<K, V> {
  private LinkedDictionaryNode<K, V> ioRootNode;
  
  public void setRootNode(LinkedDictionaryNode<K, V> poRootNode) {
    this.ioRootNode = poRootNode;
  }
  
  public LinkedDictionaryNode<K, V> getRootNode() {
    return this.ioRootNode;
  }
  
  public boolean put(K poParentKey, K poKey, V poValue) {
    if (poParentKey == null)
      throw new IllegalArgumentException("parent key object is null. The key is mandatory"); 
    LinkedDictionaryNode<K, V> loParent = get(poParentKey);
    if (loParent != null) {
      LinkedDictionaryNode<K, V> loNewNode = new LinkedDictionaryNode<K, V>(poKey, poValue);
      loParent.setChild(loNewNode);
      loNewNode.setParent(loParent);
      return true;
    } 
    return false;
  }
  
  public boolean containsKey(K poKey) {
    return (get(poKey) != null);
  }
  
  public boolean set(K poKey, V poValue) throws IllegalArgumentException {
    if (poKey == null)
      throw new IllegalArgumentException("key object is null. The key is mandatory"); 
    LinkedDictionaryNode<K, V> loNode = get(poKey);
    if (loNode != null)
      loNode.setValue(poValue); 
    return false;
  }
  
  public LinkedDictionaryNode<K, V> get(K poKey) {
    if (poKey == null)
      throw new IllegalArgumentException("key object is null. The key is mandatory"); 
    if (this.ioRootNode != null) {
      LinkedDictionaryNode<K, V> loCurrentNode = this.ioRootNode;
      if (loCurrentNode.getKey().equals(poKey))
        return loCurrentNode; 
      while (!loCurrentNode.isLeafNode()) {
        loCurrentNode = loCurrentNode.getChild();
        if (loCurrentNode.getKey().equals(poKey))
          return loCurrentNode; 
      } 
    } 
    return null;
  }
  
  public Hashtable<K, V> toHashtable() {
    Hashtable<K, V> loHashtable = new Hashtable<K, V>();
    LinkedDictionaryNode<K, V> loCurrentNode = this.ioRootNode;
    while (loCurrentNode != null) {
      loHashtable.put(loCurrentNode.getKey(), loCurrentNode.getValue());
      loCurrentNode = loCurrentNode.getChild();
    } 
    return loHashtable;
  }
}
