/*
 * Created on May 12, 2005
 *
 * History
 * 	May 12	-	Class Created
 */
package edu.asu.cse.cbioc.intex.linkgrammer;

import java.util.StringTokenizer;
import java.util.regex.Pattern;
/**
 * @author bllogsdon
 *
 * This class is used to format a string for use by the Link Grammer Parser
 * It goes through and removes all Punctuation except '.', ',', '?', '!', ':', ';'
 * It also removes the <PROTEIN></PROTEIN> tags from Abner and remarks the contained
 * text replacing the spaces with a hyphen
 * 
 * Example: <PROTEIN> Human CDC6/CDC18</PROTEIN> to  GENE-Human-CDC6-CDC18
 * 
 */
public class LinkGrammerFormatter {
	private static Pattern allPunctuation = Pattern.compile("\\p{Punct}");
	private static Pattern badPunctuation = Pattern.compile("[\\p{Punct}&&[^.,?!:;]]");
	private static Pattern goodPunctuation = Pattern.compile("[.,?!:;]");
	/**
	 * This method does the formatting given a source string and returning
	 * the formatted string
	 * @param source is the string to be formatted
	 * @return	a new string with LinkGrammer freindly words and punctuation
	 * 
	 */
	public String format(String source){
		String proteinToken = "";
		String token = "";
		StringBuffer destination = new StringBuffer();
		
	    StringTokenizer st = new StringTokenizer(source);
	    while (st.hasMoreTokens()) {
	    	token = st.nextToken();
	    	
	    	if(token.equalsIgnoreCase("<PROTEIN>")){
	    		destination.append(" GENE_");
		    	while(st.hasMoreTokens()){
		    		proteinToken = st.nextToken();
		    		
		    		if(proteinToken.equalsIgnoreCase("</PROTEIN>")){
		    			destination.deleteCharAt(destination.length()-1);
		    			break;
		    		}else if(LinkGrammerFormatter.badPunctuation.matcher(proteinToken).matches()){
			    		destination.append("_");
		    		}else if(LinkGrammerFormatter.allPunctuation.matcher(proteinToken).find()){
		    			destination.append(LinkGrammerFormatter.badPunctuation.matcher(proteinToken).replaceAll("_"));	    			
		    		}else{
		    			destination.append(proteinToken + "_");
		    		}
	    		}
	    	}else if(LinkGrammerFormatter.badPunctuation.matcher(token).matches()){
	    		
	    	}else if(LinkGrammerFormatter.badPunctuation.matcher(token).find()){
	    		destination.append(LinkGrammerFormatter.badPunctuation.matcher(token).replaceAll("_"));
    		}else{
    			destination.append(" " + token);
    		}
	    }
		return destination.toString();
	}
}
