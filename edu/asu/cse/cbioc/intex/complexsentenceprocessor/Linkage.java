package edu.asu.cse.cbioc.intex.complexsentenceprocessor;

/*
 * Linkage.java
 *
  */
import java.util.*;

/**
 * A sentence and a set of its all possible link connections.
  */
public class Linkage {
  private String sentence;
  private CostVector cv;
  private List links;
  private LinkedList lineNumbers;
  
  /** Creates a new instance of Linkage */
  public Linkage(){
    links = new ArrayList(); 
    lineNumbers = new LinkedList();
  }
  
  
  public String getSentence(){ return sentence; }
  
  
  public void setSentence(String sen){ sentence = sen;}
  
  public void setCV(CostVector v){ cv = v; }
  
  public CostVector getCV(){ return cv; }
  
  public void addLink(Link lk){ links.add(lk); }
  
  public List getLinks(){ return links; }
  
  public LinkedList getLineNumbers(){ return lineNumbers; }
  
  public void addLineNumber(int el){ lineNumbers.addLast(new Integer(el)); }
  
  public String toString(){
    StringBuffer buf = new StringBuffer();
    buf.append(sentence + "\n");
    buf.append("Total links: " + links.size() + "\n");
    for(int i = 0, n = links.size(); i < n; i++)
      buf.append((Link)links.get(i) + "\n");
    return buf.toString();
  }
}
