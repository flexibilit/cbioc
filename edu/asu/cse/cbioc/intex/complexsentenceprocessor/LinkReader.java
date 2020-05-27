package edu.asu.cse.cbioc.intex.complexsentenceprocessor;

/*
 * LinkReader.java
 *
 */

import java.util.regex.*;
import java.util.*;

public class LinkReader{
  private static Pattern startPattern;
  private static Pattern linkPattern;
  private static Pattern sentencePattern; // The pattern of the line right before the sentence.
  private static Pattern endPattern;
  private static String leftWall = "LEFT-WALL ";
  static{
    startPattern = Pattern.compile("cost vector = \\(UNUSED=(\\d+) DIS=(\\d+) AND=(\\d+) LEN=(\\d+)\\)");
    linkPattern = Pattern.compile("(?<=\\+|^)(-+([^-^\\+]*)|-*([A-Za-z\\*]+))-*(?=\\+|$)");
    sentencePattern = Pattern.compile("\\s*(\\|\\s*)+");  
    endPattern = Pattern.compile("Press RETURN for the next linkage.|linkparser>");
  }
  
  private List linkages;
  
  public Linkage getLinkage(int i){
    if(linkages != null && i < linkages.size())
      return (Linkage)linkages.get(i);
    else
      return null;
  }
  
  public int getLinkageCount(){ return linkages == null ? 0 : linkages.size(); }
  
  // aLine is a start line if it starts with leftWall, or contains first 35 chars
  // of originalSen.
  private boolean isStartLine(String aLine, String originalSen){
    if(aLine.startsWith(leftWall)) return true;
      
    // Case-insensitive.
    aLine = aLine.toLowerCase();
    originalSen = originalSen.toLowerCase();
    
    int checkCount = originalSen.length();
    if(checkCount > 35)
        checkCount = 35;
    for(int aIndex = 0, oIndex = 0, n = aLine.length(); aIndex < n; aIndex++){
      if(originalSen.charAt(oIndex) == aLine.charAt(aIndex))
        if(++oIndex >= checkCount)
          return true;
    }
    return false;
  }
  
  private void extractSentence(String originalSen, String[] output){
    linkages = new ArrayList();
    CostVector cv = null;
    Linkage aLinkage = null;
    StringBuffer sen = null;  // for concatenating the sentence.
    
    int state = 0;
    
   // System.out.println(output[0]);
   // System.out.println(output.length);
    
    for(int i = 0, n = output.length; i < n; i++){
      if(output[i].length() == 0) continue; // Skip blank line
      
      // state 0: skip head lines until startPattern. Then -> state 1.
      if(state == 0){
        Matcher startMt = startPattern.matcher(output[i]);
        if(startMt.find()){
               	
          int u = Integer.parseInt(startMt.group(1));
          int d = Integer.parseInt(startMt.group(2));
          int a = Integer.parseInt(startMt.group(3));
          int l = Integer.parseInt(startMt.group(4));
          cv = new CostVector(u, d, a, l);
          state = 1;
          
          System.out.println("startPattern  match !!!");
        }
        else
        	System.out.println("startPattern Pattern did not match !!!");
        
        continue; // Go to next line with state 0 or 1.
      }
      
     // System.out.println("before end pattern");
      // Check line pattern. State 1.
      Matcher linkMt = linkPattern.matcher(output[i]);
      
     
      
      if(linkMt.find())   // A link line.
        continue;         
     
      Matcher endMt = endPattern.matcher(output[i]);
      
      if(endMt.matches())
        break;    // endPattern terminate loop.
      Matcher senMt = sentencePattern.matcher(output[i]);
      if(senMt.matches()){ // A sentencePattern match. State 2.
        // If i+2 line is blank, i+1 is a sentence line.
        if(i < output.length - 2 && output[i+2].length() == 0)
          i++;
        else // skip this line
          continue;
      }
      
      // No pattern line or sentencePattern + 1 line
      if(isStartLine(output[i], originalSen)){ // First line of a sentence. State 3
        System.out.println("Find 1st line: " + output[i]);
        if(aLinkage != null)
          aLinkage.setSentence(sen.toString());
        aLinkage = new Linkage();           // Initialize a new linkage
        aLinkage.setCV(cv);
        linkages.add(aLinkage);
        sen = new StringBuffer();         // Reset sentence buffer.
      }
      sen.append(output[i]);  // State 4.
      aLinkage.addLineNumber(i);
	  //DC commented//System.out.println("Append line " + i + ": " + output[i]);
    }// End of main for() loop.
    
    if(aLinkage != null)                    // Add last sentence.
      aLinkage.setSentence(sen.toString());
	//DC commented//System.out.println("Sentence extract completed for: " + originalSen);
  }
    
  public void extractLink(String originalSen, String[] output){
  	
    extractSentence(originalSen, output); // First extract sentences.
    if(linkages.size() == 0) return;
    
    
    int linkageCount = 0;
    Linkage aLinkage = (Linkage)linkages.get(linkageCount);
    LinkedList lineNumbers = aLinkage.getLineNumbers();
    int nextLineNumber = ((Integer)lineNumbers.removeFirst()).intValue();
    int processedLength = 0;
    LinkedList partialLinks = new LinkedList(); // temp for incomplete links.
    for(int i = 0, n = output.length; i < n; i++){
      if(output[i].length() == 0) continue; // Skip blank line
	  
      System.out.println("Process line: " + output[i]);
      
      // Check line pattern.
      Matcher linkMt = linkPattern.matcher(output[i]);
      if(!linkMt.find()){  // Not a link line.
        if(i == nextLineNumber){ // A sentence line
          processedLength += output[i].length();
          if(lineNumbers.size() > 0)
            nextLineNumber = ((Integer)lineNumbers.removeFirst()).intValue();
          else{ // last line of the sentence
            if(++linkageCount < linkages.size()){
              //System.out.println(aLinkage);
              aLinkage = (Linkage)linkages.get(linkageCount);
              lineNumbers = aLinkage.getLineNumbers();
              nextLineNumber = ((Integer)lineNumbers.removeFirst()).intValue();
              processedLength = 0;
            }else
              break;
          }
        }
        continue;
      }else{  // Check for false linkPattern such as "Ca+ ..."
        if(linkMt.start() == 0 && output[i+1].length() == 0)
          continue;
      }
      
      do{ // Process link line. Matcher already in 1st match.
        Link aLink = null;
        // Create a new link or retrieve from partialLinks.
        int leftIndex = linkMt.start();
        if(leftIndex == 0){ // This is a partial link.
          aLink = (Link)partialLinks.removeFirst();
		  //DC commented//System.out.println("Completing partial link: " + aLink);
        }else{  // This is a new one.
          aLink = new Link();
          aLink.setLeftIndex(leftIndex - 1 + processedLength);
       }

        // Set the link type.
        String type = linkMt.group(2);
        if(type == null && !linkMt.group(1).endsWith("-"))
          type = linkMt.group(1);
        if(type != null && type.length() > 0){
          String preType = aLink.getType();
          if(preType != null && preType.length() > 0)
            aLink.setType(preType + type);
          else
            aLink.setType(type);
        }

        // Set the right index for a complete link. Or put in the partial list.
        int rightIndex = linkMt.end();
        if(rightIndex == output[i].length()){  // This is a partial link.
          partialLinks.addLast(aLink);
		  //DC commented//System.out.println("Partial link: " + aLink);
        }else{
          aLink.setRightIndex(rightIndex + processedLength);
		  aLinkage.addLink(aLink);
        }
      }while(linkMt.find());      // End of processing link line
    }// End of main for() loop.
    
    // Print complete linkage
	//DC commented//System.out.println(aLinkage);
	//DC commented//System.out.println("Link extract completed for: " + originalSen);
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args){

     String sample = "HMBA activates the MEC cell proliferation.";
    // String sample = "In summary, we demonstrate that INSULIN treatment results in activation of both PLD1 and PLD2 in appropriate cell types when the appropriate upstream intermediate signalling components, i.e. PKCalpha and PLCgamma, are expressed at sufficient levels";
		LgpWrapper p = LgpWrapper.startParser();
    p.setTimer(-1);
    p.setUnionMode();
    String[] result = null;
    if(args.length > 1)
      result = p.parse(args[0]);
    else
      result = p.parse(sample);
    p.quit();
    
    LinkReader lr = new LinkReader();
	//lr.extractSentence(sample, result);
    lr.extractLink(sample, result);
    System.out.println("Link set count: " + lr.getLinkageCount());
    for(int j = 0, m = lr.getLinkageCount(); j < m; j++){
      Linkage aLinkage = (Linkage)lr.getLinkage(j);
      System.out.println("Cost vector: " + aLinkage.getCV());
      List links = aLinkage.getLinks();
      for(int i = 0, n = links.size(); i < n; i++){
        Link aLink = (Link)links.get(i);
        System.out.println(aLink);
      }
     // System.out.println(aLinkage.getSentence());
    }
  }
}
