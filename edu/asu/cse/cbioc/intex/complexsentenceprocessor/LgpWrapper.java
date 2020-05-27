package edu.asu.cse.cbioc.intex.complexsentenceprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A wrapper class for the link grammar parser.
 *
 * Please set 'defaultPath' string to the path of link grammar parser ("./parse" ) 
 *  
 *
 */

public class LgpWrapper{
  private static final String defaultPath = "./parse";
  private static final String nextLinkage = "Press RETURN for the next linkage.";
  private static final String notComplete = "No complete linkages found.";
  private static final String panicMode = "Entering \"panic\" mode...";
  private static final String prompt = "linkparser>";
  //private static final Pattern statPtn = Pattern.compile("Time\\s+(\\d+\\.\\d\\d) seconds .+\\nFound ",Pattern.UNIX_LINES);
  private static final Pattern statPtn = Pattern.compile("Time\\s+(\\d+\\.\\d\\d) seconds .+\\nFound \\d+ .+ \\((\\d+) .+ no P.P. violations\\)",Pattern.UNIX_LINES);
  //private static final Pattern statPtn = Pattern.compile("Time\\s+(\\d+\\.\\d\\d) seconds .+\\r\\nFound \\d+ linkages \\((\\d+) .+ no P.P. violations\\)");
  private static Process parserProcess;
  private static BufferedReader parserReader;
  private static PrintStream parserWriter;
  private static boolean nullLinkMode = true;
  private static boolean moreLinkage = false;
  private static boolean complete;
  private static boolean panic;
  private static float parseTime;
  private static int linkageCount;
  
  private static LgpWrapper lw;     // Singleton instance.

  /* Disable constructor. Must use startParser(). */
  private LgpWrapper(){}

	/**
  * Start a parser process. Connect to its input and output.
  */
  public static LgpWrapper startParser(String cmdline){
    if(lw == null){
      lw = new LgpWrapper();
      try{
        // Start process
        if(cmdline != null && cmdline.length() > 0)
          lw.parserProcess = Runtime.getRuntime().exec(cmdline);
        else
          lw.parserProcess = Runtime.getRuntime().exec(defaultPath);
		
        // Get output and input streams
        if(lw.parserProcess != null){
          lw.parserReader = new BufferedReader(new InputStreamReader(lw.parserProcess.getInputStream()));
          lw.parserWriter = new PrintStream(lw.parserProcess.getOutputStream(), true);
          // Discard initializing output.
          StringBuffer buf = new StringBuffer();
          char[] cbuf = new char[4000];  
          while(true){
            if(parserReader.ready()){
              int charCount = parserReader.read(cbuf, 0, 4000);
              buf.append(cbuf, 0, charCount);
            }else if(buf.indexOf(prompt) >= 0)
            {
              break;
            }
          }
        }
      }catch(Exception e){
          System.err.println("Can't start parser.");
          e.printStackTrace();
          return null;
      }
    }
    return lw;
  }
  
  public static LgpWrapper startParser(){ return startParser(""); }

	/**
  * Parse a string.
  */
  public String[] parse(String s){
    StringBuffer buf = null;
		try{
      // Write the sentence to the parser for processing
      parserWriter.println(s);
      
      
      //  milli seconds to wait for the output *********************
      buf = readOutput(200);
      /*String temp = new String(buf.toString());
      System.out.println("-----------");
      System.out.println(temp);
      System.out.println("----------");
      */
      
    }catch(Exception e){
        System.err.println("Unexpected exception");
        e.printStackTrace();
    }
    
    // Set parsing statistics.
    if(buf.indexOf(nextLinkage) >= 0)
      moreLinkage = true;
    else
      moreLinkage = false;
    if(buf.indexOf(notComplete) >= 0){
      complete = false;
      if(buf.indexOf(panicMode) >= 0)
        panic = true;
      else
        panic = false;
    }
    else{
      complete = true;
      panic = false;
    }
    
    Matcher mt = statPtn.matcher(buf);
    if(mt.find()){
      parseTime = Float.parseFloat(mt.group(1));
      	//System.out.println(parseTime + "parseTime");
      linkageCount = Integer.parseInt(mt.group(2));
      	//System.out.println(linkageCount + "linkageCount");
    }
    else
    	System.out.println("Pattern did not match !!!");
    
    
    // Split output into lines
    if(buf.length() > 0)
      return buf.toString().split("\\r\\n");
    else
      return null;
	}
  
  public String[] getNextLinkage(){
    StringBuffer buf = null;
    try{
	    parserWriter.println();
      buf = readOutput(2);          
    }catch(IOException e){ e.printStackTrace(); }
    
    // Flag more linkage.
    if(buf.indexOf(nextLinkage) >= 0)
      moreLinkage = true;
    else
      moreLinkage = false;
    
    // Split output into lines
    if(buf.length() > 0)
      return buf.toString().split("\\r\\n");
    else
      return null;
  }
  
  public void toggleNullLinkMode(){
    try{
	    parserWriter.println("!null");
      moreLinkage = false;
      nullLinkMode = !nullLinkMode;
      readOutput(0); // Discard output.
    }catch(IOException e){ e.printStackTrace(); }
  }
  
  public void togglePanicMode(){
    try{
	    parserWriter.println("!panic");
      moreLinkage = false;
      readOutput(0); // Discard output.
    }catch(IOException e){ e.printStackTrace(); }
  }
  
  
  public void setUnionMode(){
	  try{
		  parserWriter.println("!union=1");
		moreLinkage = false;
		readOutput(0); // Discard output.
	  }catch(IOException e){ e.printStackTrace(); }
	}
  
	public void setPanicMode(){
		  try{
			  parserWriter.println("!panic=1");
			moreLinkage = false;
			readOutput(0); // Discard output.
		  }catch(IOException e){ e.printStackTrace(); }
		}
  
  public void setMemoryToMax(){
	  try{
		  parserWriter.println("!memory=512000000");
		moreLinkage = false;
		nullLinkMode = !nullLinkMode;
		readOutput(0); // Discard output.
	  }catch(IOException e){ e.printStackTrace(); }
	}
  public void setTimer(int sec){
    try{
	    parserWriter.println("!timeout=" + sec);
      moreLinkage = false;
      readOutput(0); // Discard output.
    }catch(IOException e){ e.printStackTrace(); }
  }
  
  public void runCmd(String cmd){
    try{
	    parserWriter.println(cmd);
      moreLinkage = false;
      readOutput(0); // Discard output.
    }catch(IOException e){ e.printStackTrace(); }
  }
  
  private synchronized StringBuffer readOutput(long ms) throws IOException {
    StringBuffer buf = new StringBuffer();
    // Read output. Use non-blocking read().
    char[] cbuf = new char[4000];         
    while(true){
      if(parserReader.ready()){
        int charCount = parserReader.read(cbuf, 0, 4000);
		buf.append(cbuf, 0, charCount);
      }else if(buf.indexOf(prompt) >= 0)
        break;
      else{
        try{
          if(ms > 0)
            wait(ms);
        }catch(InterruptedException ie){}
      }
    }
    return buf;
  }
  
  public boolean isNullLinkAllowed(){ return nullLinkMode; }
  
  public boolean hasMoreLinkage(){ return moreLinkage; }
  
  public boolean isLinkageComplete(){ return complete; }
  
  public boolean isPanicMode(){ return panic; }
  
  public float getParseTime(){ return parseTime; }
  
  public int getLinkageCount(){ return linkageCount; }

  /**
   * Terminate the parser process.
   */
  public void quit(){
    try{
	    parserWriter.println("quit");
  		parserWriter.close();
      parserReader.close();
    }catch(IOException e){
        System.err.println("Unexpected exception");
        e.printStackTrace();
    }
    parserProcess = null;
    lw = null;
	}

  /**
	 * Usage: java LgpWrapper <sentence>
	 */
  public static void main(String[] args){
    
  	String sample = "John is a boy";
 //	String sample = "GABA mediates the inhibitory effect of NO on the AVP and OXT responses to insulin-induced hypoglycemia";
 // String sample = "In summary, we demonstrate that INSULIN treatment results in activation of both PLD1 and PLD2 in appropriate cell types when the appropriate upstream intermediate signalling components, i.e. PKCalpha and PLCgamma, are expressed at sufficient levels";
    LgpWrapper p = LgpWrapper.startParser();

//    p.togglePanicMode(); // Turn off null-link mode.
//    p.setTimer(5);
    long startTime = System.currentTimeMillis();
    String[] result = null;
    if(args.length > 1)
      result = p.parse(args[0]);
    else
      result = p.parse(sample);
    
 
    //System.out.println(result.length);
 
    // Show parsing result.
    System.out.println("Complete linkage: " + p.isLinkageComplete());
    System.out.println("Panic mode: " + p.isPanicMode());
    System.out.println("Parsing time: " + p.getParseTime() + " seconds.");
    System.out.println("Total valid linkages: " + p.getLinkageCount() + ".");
    
 		for(int i = 0, n = result.length; i < n; i++){
      if(result[i].length() == 0)
        System.out.println("Blank line");
      else
        System.out.println(result[i]);
 		}
    System.out.println("Line count: " + result.length);
    while(p.hasMoreLinkage())
      result = p.getNextLinkage();
 		for(int i = 0, n = result.length; i < n; i++){
      if(result[i].length() == 0)
        System.out.println("Blank line");
      else
       System.out.println(result[i]);
 		}
    long endTime = System.currentTimeMillis();
    System.out.println("Total time: " + (float)(endTime - startTime)/1000);
    
	
	p.quit();
	}
}