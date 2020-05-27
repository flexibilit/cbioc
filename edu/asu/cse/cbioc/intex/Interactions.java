/*
 * Created on Jun 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.asu.cse.cbioc.intex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * @author bllogsdon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Interactions {
	private static final String INTERACTIONLISTFILE = "./edu/asu/cse/cbioc/intex/IWordList.txt";
	private static Hashtable interactionHash;
	private static PorterStemmer PS;

	static{
		// Instantiate instance of static porterStemmer to save on resources
		PS = new PorterStemmer();
		// create hash table for Interaction words
		createIntHash();
	}

	private static void createIntHash(){
		interactionHash = new Hashtable();
		
		BufferedReader in_file;
		String Line = new String();
		String stemmed = new String();
		
		try{	
			in_file = new BufferedReader(new InputStreamReader(new FileInputStream(INTERACTIONLISTFILE),"ASCII"));
			
			while((Line = in_file.readLine())!=null) {
				if(Line != null ){
					interactionHash.put(Line,Line);
				}
			}	
			in_file.close();
		}catch (IOException ex1){
			System.err.println(ex1);
			ex1.printStackTrace();
		}catch (Exception ex2){
			System.err.println(ex2);
			ex2.printStackTrace();
		}	
	}
	
	public boolean isIntWord(String token){
		String stemmed = new String();
		String fromHash = new String();
		
		if(token != null){
			if((token.endsWith(".v")
					|| token.endsWith(".n")
					|| token.endsWith(".g")
					|| token.endsWith(".a")
					|| token.endsWith(".p")) 
					&& (token.length() > 2)){
				token = token.substring(0,token.length()-2);
			}
				
			if((token.endsWith("[?]") || token.endsWith("[!]")) && (token.length() > 3)){
				token = token.substring(0,token.length()-3);
			}
				
			stemmed = PS.stem(token);
			if(stemmed.length() > 0){
				//System.out.println("Stem" +stemmed);
				try{				
					fromHash = (String) interactionHash.get(stemmed);
					if(fromHash != null){
						return true;
					}
				}catch(NullPointerException ne){
					ne.printStackTrace();
				}
			}
		}	
		return false;
	}
	
	public boolean isIntWordPresent(String scope){
		StringTokenizer st = new StringTokenizer(scope);
		String token = new String();
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			
			if(isIntWord(token)){
				return true;
			}
		}
		return false;
	}
}
