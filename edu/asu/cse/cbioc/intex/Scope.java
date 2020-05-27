package edu.asu.cse.cbioc.intex;

import java.util.StringTokenizer;
/*
 *  Scope.java
 */

/**
 * @author Toufeeq
 *
 * 
 * 
 */
public class Scope{
	public boolean isElementary;
	public boolean isComplete;
	public boolean isPartial;
	public boolean isNothing;
	private String sentence;
	private Interactions interactions;
	
	// constructor
	public Scope(){              
		isComplete = false;
		isPartial = false;
		isElementary = false;
		isNothing = false;
		interactions = new Interactions();
	}
	
	public Scope(String sentence){
		this();
		this.sentence = sentence;
		
		if(sentence != null){
			if(!(isComplete = isComplete(sentence))){
				if(!(isPartial = isPartial(sentence))){
					if(!(isElementary = isElementary(sentence))){
						isNothing = true;
					}
				}
			}
		}else{
			isNothing = true;
		}
	}

	public String getSentence(){
		return sentence;
	}
	
	public boolean isElementary(String Scope){
		StringTokenizer st = new StringTokenizer(Scope);
		String token = new String();
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if(isGene(token)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isComplete(String scope){
		StringTokenizer st = new StringTokenizer(scope);
		String token = new String();
		boolean I=false;
		int gCount =0;
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if(isGene(token)){
				gCount++;
			}
			if(interactions.isIntWord(token)){
				I = true;
			}
		} 
		
		if(gCount >= 2 && I == true){
			System.out.println("Complete");
			return true;
		}
		
		return false;
	}
	
	
	
	public boolean isPartial(String Scope){
		StringTokenizer st = new StringTokenizer(Scope);
		String token = new String();
		boolean I=false;
		int gCount =0;
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if(isGene(token))
				gCount++;
			if(interactions.isIntWord(token))
				I = true;
		} 
		
		if(gCount >= 1 && I == true){
			return true;
		}		
		
		return false;
	}
	
	public boolean isGene(String token){
		if(token != null)
			if(token.length() >= 6){
				//String token2 = token.substring(0,4);
				//if(token2.equals("Gene")){
				if(token.indexOf("GENE_") != -1){
					//System.out.println("found");
					return true;
				}
			}
		return false;
	}
	
	public String doElementary(String Scope){
		StringTokenizer st = new StringTokenizer(Scope);
		String token = new String();
		String str = "";
		boolean I=false;
		int gCount =0;
		
		while (st.hasMoreTokens()) {
			
			token = st.nextToken();
			if(isGene(token)&& gCount < 1){
				if(str.length() == 0)
					str = token.substring(5);
				else
					str	= str + "@" + token.substring(5);	
				gCount++;
			}
		}
		
		if(gCount >= 1 && str.length() > 0){
			return str;
		}		
		
		return "##$$##";
	}
	
	public  boolean isGenePresent(){
		if(sentence != null){
			StringTokenizer st = new StringTokenizer(sentence);
			String token = new String();
			
			while (st.hasMoreTokens()) {
				token = st.nextToken();
				if(isGene(token)){
					return true;
				}
			}
		}
		return false;
	}
	
	public  boolean isLorCPresent(){
		if(sentence != null){
			StringTokenizer st = new StringTokenizer(sentence);
			String token = new String();
			
			while (st.hasMoreTokens()) {
				token = st.nextToken();
			}
		}
		return false;
	}
	
	/**
	 * @param Scope
	 * Implements a pattern matcher
	 * @return returns interaction
	 */
	public String doComplete(String Scope){
		//findAll.writeToIntFile("-------------------Do complete-----------------------------");
		//findAll.writeToIntFile(Scope);
		StringTokenizer st = new StringTokenizer(Scope);
		String token = new String();
		String str = "[";
		boolean I=false;
		int gCount =0;
		String agent = new String();
		String theme = new String();
		String action = new String();
		boolean by = false,of =false,byfound =false,offound =false;
		
		
		//  pattern matcher
		// 
		
		String pattern[][] = {
				{"I","of","G2","by","G1"},
				{"G1","I","G2" },
				{"I","G1","G2" }
		};
		
		int patternElementCount[] ={5,3,3};
		int totalPatterns=3;
		int i=0;
		while(i < totalPatterns){
			st = new StringTokenizer(Scope);
			String Gene1 = new String();
			String Interaction = new String();
			String Gene2 = new String();
			
			int cur = 0;
			while (st.hasMoreTokens() && cur <  patternElementCount[i]) {
				
				token = st.nextToken();
				token = token.trim();
				if(pattern[i][cur].equals("G1") && isGene(token)){
					Gene1 = token.substring(5);
					cur ++;
					continue;
				}
				if(pattern[i][cur].equals("I") && interactions.isIntWord(token)){
					Interaction = token;
					cur ++;
					continue;
					
				}
				if(pattern[i][cur].equals("G2") && isGene(token)){
					Gene2 = token.substring(5);
					cur ++;
					continue;
				}
				if(pattern[i][cur].equals(token)){
					cur ++;
					continue;
				}
			}
			if(cur == patternElementCount[i]){
				if((Gene1.length() > 0) && (Interaction.length() > 0) && (Gene2.length() > 0))
					return( Gene1 +"#" + Interaction +"#" + Gene2 +"#");
			}
			i++;
		}
		 return "##$$##";
	}
		
	public String doRightPartial(String Scope){
		/*findAll.writeToIntFile("-----------------------doRightPartial------------------------");
		findAll.writeToIntFile(Scope);*/
		StringTokenizer st = new StringTokenizer(Scope);
		String token = new String();
		String str = "";
		boolean I=false;
		int gCount =0;
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if(isGene(token)&& (gCount < 1) && I == true){
				/*if(str.length() == 0)
				 str = token.substring(5);
				 else*/
				str	= str + "#" + token.substring(5);	
				gCount++;
			}
			if(interactions.isIntWord(token)&& I == false){
				if(str.length() == 0)
					str = token;
				/*else
				 str	= str + "#" + token;*/
				I = true;
			}
		} 
		
		if((gCount >= 1) && (I == true)  && (str.length() >0)){
			return str;
		}		
		
		return "##$$##";
	}
	
	public String doLeftPartial(String Scope){
		/*findAll.writeToIntFile("----------------------doLeftPartial--------------------------");
		findAll.writeToIntFile(Scope);*/
		StringTokenizer st = new StringTokenizer(Scope);
		String token = new String();
		String str = "";
		boolean I=false;
		int gCount =0;
		
		while (st.hasMoreTokens()) {
			
			token = st.nextToken();
			if(isGene(token)&& (gCount < 1) && (I == false)){
				if(str.length() == 0)
					str = token.substring(5);
				/*else
				 str	= str + "#" + token.substring(5);	*/
				gCount++;
			}
			if(interactions.isIntWord(token)&& (I == false) && (gCount >=1)  ){
				/*if(str.length() == 0)
				 str = token;
				 else*/
				str	= str + "#" + token;
				I = true;
			}
		} 
		
		if((gCount >= 1) && (I == true)  && (str.length() >0)){
			return str;
		}		
		
		return "##$$##";
	}
	
	public String doPartialPrep(String Scope){
		/*findAll.writeToIntFile("-----------------------doPartialPrep-------------------------");
		findAll.writeToIntFile(Scope);*/
		StringTokenizer st = new StringTokenizer(Scope);
		String token = new String();
		String str = "";
		boolean I=false;
		int gCount =0;
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if(isGene(token)&& gCount < 1){
				if(str.length() == 0)
					str = token.substring(5);
				else
					str	= str +" "+ token.substring(5);	
				gCount++;
			}
			if(interactions.isIntWord(token)&& I == false){
				if(str.length() == 0)
					str = token;
				else
					str	= str + " "+ token;
				I = true;
			}
			if(str.length() != 0){
				//Preposiion for theme/association
				if((token.equals("of")
				)&& gCount <1 && I){
					str	= str + "> ";
				}
				//	Preposiion of purpose
				if((token.equals("for")
				)&& gCount <1 && I){
					str	= str + " @ ";
				}
			}
		} 
		if(gCount >= 1 && I == true && str.length() >0){
			return str;
		}		
		return "##$$##";
	}
}