package edu.asu.cse.cbioc.intex.complexsentenceprocessor;

/*
 * CostVector.java
 *
 */

/**
 * Encapsulate cost vector of LGP.
 */
public class CostVector {
  private int unused, dis, and, len;
  
  /** Creates a new instance of CostVector */
  public CostVector(int u, int d, int a, int l){
    unused = u;
    dis = d;
    and = a;
    len = l;
  }
  
  public int getUnused(){ return unused; }
  
  public int getDis(){ return dis; }
  
  public int getAnd(){ return and; }
  
  public int getLen(){ return len; }
  
  public String toString(){
    return "(UNUSED=" + unused + " DIS=" + dis + " AND=" + and + " LEN=" + len + ")";
  }
}
