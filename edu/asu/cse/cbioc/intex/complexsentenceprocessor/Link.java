package edu.asu.cse.cbioc.intex.complexsentenceprocessor;

/*
 *  Class to represent the Links from the linkage given by the parser
 *
 */
public class Link {
  private int leftIndex;
  private int rightIndex;
  private String type;
  
  public int getLeftIndex(){ return leftIndex; }
  public void setLeftIndex(int leftIndex){ this.leftIndex = leftIndex; }
  public int getRightIndex(){ return rightIndex; }
  public void setRightIndex(int rightIndex){ this.rightIndex = rightIndex; }
  public String getType(){ return type; }
  public void setType(String type){ this.type = type; }
  public String toString(){ return leftIndex + type + rightIndex; }
}
