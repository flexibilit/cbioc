package edu.asu.cse.cbioc.intex;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import edu.asu.cse.cbioc.intex.complexsentenceprocessor.Link;
import edu.asu.cse.cbioc.intex.complexsentenceprocessor.Linkage;

/* 
 * Finds all possible protein/Gene interactions in a sentence.
 */

/* 
 * 
 * Finds all possible protein/Gene interactions in a sentence.
 * 
 * 
 */


/* 
 * Finds all possible protein/Gene interactions in a sentence.
 */

public class IntFinder{
	private String Subject;
	private String Object;
	private String MPhrase; 
	private String MVerb;
	private int MVno;
	private String Sen;
	private Scope S1,S2,S3,S4,S5;
	private Interactions interactions;
	private ArrayList foundInteractions;
	
	public static int Sencount  = 0;
	public static int IntCount = 1 ;
	private static int LinkCount = 1;
	private static int iWordCount = 1;
	
	public IntFinder(){
		// Initialize
	    Subject = new String();
		Object = new String();
		MPhrase = new String();
		MVerb = new String();
		Sen = new String();
		MVno = -1;
		interactions = new Interactions();
		foundInteractions = new ArrayList();
	}
		
	public ArrayList findInteraction(String S,String V,String O,String M, String sentence){
		S1 = new Scope(S);
		S2 = new Scope(O);
		S3 = new Scope(M);
		S4 = new Scope();
		foundInteractions = new ArrayList();
		
		MVerb = V;
		
		//System.out.println("Step 1");
		stepOne();
		//System.out.println("Step 2");
		stepTwo();
		//System.out.println("Step 3");
		stepThree();
		//System.out.println("Step 4");
		stepFour();
		//System.out.println("Step 5");
		stepFive();
		
		return foundInteractions;
	}
	
	private void stepOne(){	
		if(S1.isComplete){
			getIntWhenComplete(S1.getSentence());
		}
	}
	
	private void stepTwo(){	
		if(S2.isComplete){
			getIntWhenComplete(S2.getSentence());
		}
	}
	
	private void stepThree(){	
		if(S3.isComplete){
			getIntWhenComplete(S3.getSentence());
		}
	}
	
	private void stepFour(){	
		if(isMainVerbAnIword()){
			if(S1.isElementary && S2.isElementary){
				getIntWhenEVE(S1.getSentence(),MVerb,S2.getSentence());
				S4.isComplete = true;
		    }
			if(S1.isElementary && S2.isPartial){
				getIntWhenEVP(S1.getSentence(),MVerb,S2.getSentence());
				S4.isComplete = true;
			}
			if(S1.isPartial && S2.isElementary){
				getIntWhenPVE(S1.getSentence(),MVerb,S2.getSentence());
				S4.isComplete = true;
			}
			if(S1.isPartial && S2.isPartial){
				getIntWhenPVP(S1.getSentence(),MVerb,S2.getSentence());
			    S4.isComplete = true;
			}
			if(S1.isComplete && S2.isElementary){
				getIntWhenCVE(S1.getSentence(),MVerb,S2.getSentence());
				S4.isComplete = true;	
			}		
			if(S1.isElementary && S2.isComplete){
				getIntWhenEVC(S1.getSentence(),MVerb,S2.getSentence());
				S4.isComplete = true;
			}
		}else{
			if(S1.isElementary && S2.isPartial){
				getIntWhenEP(S1.getSentence(),S2.getSentence());
				S4.isComplete = true;
			}
			if(S1.isPartial && S2.isElementary){
				getIntWhenPE(S1.getSentence(),S2.getSentence());
				S4.isComplete = true;
			}
			if(S1.isComplete && S2.isPartial){
				getIntWhenCP(S1.getSentence(),S2.getSentence());
				S4.isComplete = true;
			}
			if(S1.isPartial && S2.isComplete){
				getIntWhenPC(S1.getSentence(),S2.getSentence());
				S4.isComplete = true;
			}
		}
	}
	
	private void stepFive(){
		if(!(S4.isComplete && S3.isElementary)){
			if(isMainVerbAnIword()){
				if(S1.isElementary && S3.isElementary){
					if(S3.getSentence().indexOf("by") != -1){
						getIntWhenEVE(S3.getSentence(),MVerb,S1.getSentence());
					}else{
						getIntWhenEVE(S1.getSentence(),MVerb,S3.getSentence());
					}
					if(S1.isElementary && S3.isPartial){
						getIntWhenEVP(S1.getSentence(),MVerb,S3.getSentence());
					}
					if(S1.isPartial && S3.isElementary){
						if((S3.getSentence().indexOf("by") != -1 ) || (S3.getSentence().indexOf("via") != -1)){
							getIntWhenEVE(S3.getSentence(),MVerb,S1.getSentence());
						}else{
							getIntWhenPVE(S1.getSentence(),MVerb,S3.getSentence());
						}
					}	
					if(S1.isPartial && S3.isPartial){
						getIntWhenPVP(S1.getSentence(),MVerb,S3.getSentence());
					}
					if(S1.isComplete && S3.isElementary){
						if(S3.getSentence().indexOf("by") != -1){
							getIntWhenEVE(S3.getSentence(),MVerb,S1.getSentence());
						}else{
							getIntWhenCVE(S1.getSentence(),MVerb,S3.getSentence());
						}
					}					
					if(S1.isElementary && S3.isComplete){
						getIntWhenEVC(S1.getSentence(),MVerb,S3.getSentence());
					}
				}else{
					if(S1.isElementary && S3.isPartial){
						getIntWhenEP(S1.getSentence(),S3.getSentence());
					}
							
					if(S1.isPartial && S3.isElementary){
						if(S3.getSentence().indexOf("by") != -1){
							getIntWhenEVE(S3.getSentence(),MVerb,S1.getSentence());
						}else{
							getIntWhenPE(S1.getSentence(),S3.getSentence());
						}
					}
				}		
				if(S1.isComplete && S3.isPartial){
					getIntWhenCP(S1.getSentence(),S3.getSentence());
				}
				if(S1.isPartial && S3.isComplete){
					getIntWhenPC(S1.getSentence(),S3.getSentence());
				}
			}
		}
	}
	
	private boolean isMainVerbAnIword(){
		StringTokenizer st = new StringTokenizer(MVerb);
		String token = new String();
				
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if(interactions.isIntWord(token)){
				return true;
			}	
		}
 		return false;
	}
	
	private String getIword(String S){
		StringTokenizer st = new StringTokenizer(S);
		String token = new String();
				
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if(interactions.isIntWord(token)){
				return token;
			}	
		}
 		return  "##$$##";
	}

	private void getIntWhenComplete(String str) {
		foundInteractions.add(S1.doComplete(str));
	}
	
	private void getIntWhenEP(String s1,String s2) {
		foundInteractions.add("[" + S1.doElementary(s1) + " | " + S1.doRightPartial(s2)+"]");
	}
	
	private void getIntWhenPE(String s1,String s2) {
		foundInteractions.add("[" + S1.doLeftPartial(s1) + " | " + S2.doElementary(s2)+"]");
	}
	
	private void getIntWhenCP(String s1,String s2) {
		foundInteractions.add("[" + S1.doComplete(s1) + " | " + S2.doRightPartial(s2)+"]");
	}
	
	private void getIntWhenPC(String s1,String s2) {
		foundInteractions.add("[" + S1.doLeftPartial(s1) + " | " + S2.doComplete(s2)+"]");
	}
	
	private void getIntWhenEVE(String s1,String s2,String s3) {
		foundInteractions.add("[" + S1.doElementary(s1) + " | " + getIword(s2) + " | " + S3.doElementary(s3)+"]");
	}
	
	private void getIntWhenCVE(String s1,String s2,String s3) {
		foundInteractions.add("[" + S1.doComplete(s1) + " | " + getIword(s2) + " | " + S3.doElementary(s3)+"]");
	}
	
	private void getIntWhenEVC(String s1,String s2,String s3) {
		foundInteractions.add("[" + S1.doElementary(s1) + " | " + getIword(s2) + " | " + S3.doComplete(s3)+"]");
	}
	
	private void getIntWhenEVP(String s1,String s2,String s3) {
		foundInteractions.add("[" + S1.doElementary(s1) + " | " + getIword(s2) + " |[ " + S3.doPartialPrep(s3)+"]]");
	}
	
	private void getIntWhenPVE(String s1,String s2,String s3) {
		foundInteractions.add("[[" + S1.doPartialPrep(s1) + "] | " + getIword(s2) + " | " + S3.doElementary(s3)+"]");
	}
	
	private void getIntWhenPVP(String s1,String s2,String s3) {
		foundInteractions.add("[[" + S1.doPartialPrep(s1) + "] | " + getIword(s2) + " | [" + S3.doPartialPrep(s3)+"]]");
	}
	
	private String getSubject(List sList,List nList,List links) {
		String Subject = "";
		int Sno;
		int sIndex = -1;
	
		// Get the 'S' link
		for(int i = 0, n = links.size(); i < n; i++){
			Link aLink = (Link)links.get(i);
			String linkType = aLink.getType().trim();
			if(linkType.charAt(0) == 'S'){
				sIndex = i;
				break;
			}
		}
		
		if(sIndex != -1){
			Link aLink = (Link)links.get(sIndex);
			//String mSub = sList.get(nList.indexOf(new Integer(aLink.getLeftIndex()))).toString();
			Sno = aLink.getLeftIndex();
			
			// Get Main verb no........
			MVno = aLink.getRightIndex();
			
			// If Subject is connected to 'I' link then get the verb connected to 'I' link.
			if(links.size() > (sIndex + 1)){
				Link nLink = (Link)links.get(sIndex +1);
				String nlinkType = nLink.getType().trim();
				if(nlinkType.equals("N")){
					MVno = aLink.getRightIndex();
				}
			 	else{
				 	int v1 = aLink.getRightIndex();
				 	int i=0 ;
					Link iLink = (Link)links.get(i);
				   	while((v1 != iLink.getLeftIndex()) && (i < links.size()-1) )
				   		iLink = (Link)links.get(++i);
					
					String ilinkType = iLink.getType().trim();
				
					if(ilinkType.charAt(0) == 'I' 
					|| ilinkType.charAt(0) == 'P'){
						MVno = iLink.getRightIndex();
					}
			 	}
			}
			// Add all that are between subject and verb.
			int k = nList.indexOf(new Integer(aLink.getLeftIndex()));
			Integer i = new Integer(aLink.getLeftIndex());
			
			while(!i.equals(new Integer(aLink.getRightIndex()))){
				Subject = Subject.concat(sList.get(k).toString()+ " ");
				i = new Integer(nList.get(++k).toString());
			}
			
			// Add from 'D,A,J' links to the subject
			for(int j = links.size()-1; j >= 0; j--){
				aLink = (Link)links.get(j);
				String Str = sList.get(nList.indexOf(new Integer(aLink.getRightIndex()))).toString();
				String linkType = aLink.getType().trim();
				if(Str.equals(sList.get(nList.indexOf(new Integer(Sno)))) &&((linkType.charAt(0) == 'D') || 
				(linkType.charAt(0) == 'A') || (linkType.charAt(0) == 'J'))){
					String Str2 = sList.get(nList.indexOf(new Integer(aLink.getLeftIndex()))).toString();
					Subject = Str2.concat(" " + Subject);
				}
			}
				
			//System.out.println("Subject "+Subject);
			System.out.println(LinkCount++ +"-Subject :"+Subject);
			return Subject;
		}
		
		return null;
	}
	
	private String getObject(List sList,List nList,List links) {
		int oIndex = -1;
		
		// Get the 'O' link
		for(int i = 0, n = links.size(); i < n; i++){
			Link aLink = (Link)links.get(i);
			String linkType = aLink.getType().trim();
			int no = aLink.getLeftIndex();
			if(linkType.charAt(0) == 'O' && no == MVno ){
				oIndex = i;
				break;
			}
			 
			if(linkType.charAt(0) == 'I' && no == MVno ){
				oIndex = i+1;
				break;
			}	 
										
		}
		
		if(oIndex != -1){
			Link aLink = (Link)links.get(oIndex);
			//System.out.println(">>>>>>"+ sList.get(nList.indexOf(new Integer(aLink.getLeftIndex()))));
			String mObj = sList.get(nList.indexOf(new Integer(aLink.getRightIndex()))).toString();
			String Object = mObj;
			int Ono = aLink.getRightIndex();
						
			// Add from 'D,A,J' links
			for(int j = links.size()-1; j >= 0; j--){
				aLink = (Link)links.get(j);
				String Str = sList.get(nList.indexOf(new Integer(aLink.getRightIndex()))).toString();
				String linkType = aLink.getType().trim();
				if(Str.equals(sList.get(nList.indexOf(new Integer(Ono)))) &&((linkType.charAt(0) == 'D') || 
				(linkType.charAt(0) == 'A') || (linkType.charAt(0) == 'J'))){
					String Str2 = sList.get(nList.indexOf(new Integer(aLink.getLeftIndex()))).toString();
					Object = Str2.concat(" " + Object);
				}
			}
			
			//	Add from 'M,J' links
			for(int j =0,n = links.size(); j < n; j++){
				 aLink = (Link)links.get(j);
				 String Str = sList.get(nList.indexOf(new Integer(aLink.getLeftIndex()))).toString();
				 String linkType = aLink.getType().trim();
				 if(Str.equals(sList.get(nList.indexOf(new Integer(Ono)))) &&
				 ((linkType.charAt(0) == 'M') || 
				 (linkType.charAt(0) == 'J'))){
					 String Str2 = sList.get(nList.indexOf(new Integer(aLink.getRightIndex()))).toString();
					 Object = Object.concat(" " + Str2);
					 Ono = aLink.getRightIndex();
				 }
			 }
			
			//System.out.println("Object  "+Object);
			System.out.println("Object : "+Object);
			return Object;	
		}
		return null;
	}
	
	private String getMPhrase(List sList,List nList,List links) {
		int mpIndex = -1;
		
		// Get the 'MP' link
		for(int i = 0, n = links.size(); i < n; i++){
			Link aLink = (Link)links.get(i);
			String linkType = aLink.getType().trim();
			int no = aLink.getLeftIndex();
			if(linkType.length() >= 2)
				if(linkType.substring(0,2).equals("MV") && no == MVno ){
					mpIndex = i; 
					break;
				}
		}
		if(mpIndex != -1){
			Link aLink = (Link)links.get(mpIndex);
			int no = nList.indexOf(new Integer(aLink.getRightIndex()));
			String mp = "";
			for(int i= no,n = nList.size(); i < n; i++){
				mp = mp.concat(sList.get(i) + " ");
			}
			//System.out.println("MVPhrase  " + mp);
			System.out.println("MVPhrase : " + mp);
			return mp;
		}
		return null;
	}
	
	private String getMVerb(List sList,List nList,List links) {
		   String verb = new String();
		   if(MVno != -1){
		  		 verb = sList.get(nList.indexOf(new Integer(MVno))).toString();
		   		//System.out.println("Mverb " + verb);
				System.out.println("Mverb : " + verb);
				return verb;
		   }
		   return null;
	}

	public List makeSenList(String Sen){
			ArrayList l = new ArrayList();
			StringTokenizer st = new StringTokenizer(Sen);
			String token = new String();
		
			while (st.hasMoreTokens()) {
				token = st.nextToken().trim();
				// Dont add unrecognized words to the sentence
				//if((!token.substring(0,1).equals("[")) && (!token.equals("and"))){
				if((!token.substring(0,1).equals("["))){
					//System.out.println(token);
					if(token.endsWith(".v") 
					|| token.endsWith(".n")
					|| token.endsWith(".a")
					|| token.endsWith(".g")
					|| token.endsWith(".p") )
						token = token.substring(0,token.length()-2);
				
					if(token.endsWith("[?]")
					|| token.endsWith("[!]"))
						token = token.substring(0,token.length()-3);
					l.add(token);
				}
			}
			l.remove("linkparser>");
			return l;
	}
	
	public List makeNumList(Linkage aLinkage){
			String Sen = aLinkage.getSentence();
			SortedSet aSet = new TreeSet();
			List links = aLinkage.getLinks();
			for(int i = 0, n = links.size(); i < n; i++){
				Link aLink = (Link)links.get(i);
				aSet.add(new Integer(aLink.getLeftIndex()));
				//System.out.println(getWord(Sen,aLink.getLeftIndex()));
				aSet.add(new Integer(aLink.getRightIndex()));
				//System.out.println(getWord(Sen,aLink.getRightIndex()));
			}
			List l = new ArrayList(aSet);
			return l;
	}
	
	private List makeSenList(String Sen, List nList) {
		ArrayList l = new ArrayList();
		for(int i=0; i < nList.size(); i++){
			int j = Integer.parseInt(nList.get(i).toString());
			l.add(getWord(Sen,j));
		}
		return l;
	}
	
	public String getWord(String Sen,int index){
		String temp = new String();
		int len = 0;
		StringTokenizer st = new StringTokenizer(Sen);
		String token = new String();
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			len = len + token.length() + 1;
			//System.out.println(token);
			if(len > index){
				token = token.trim();
				if((!token.substring(0,1).equals("["))){
				//System.out.println(token);
					if(token.endsWith(".v") 
					|| token.endsWith(".n")
					|| token.endsWith(".a")
					|| token.endsWith(".g")
					|| token.endsWith(".p") )
						token = token.substring(0,token.length()-2);
				
					if(token.endsWith("[?]")
					|| token.endsWith("[!]"))
						token = token.substring(0,token.length()-3);
				
					return token;
				}
			}
		}
		return null;
	}
	/*
	public boolean findInteraction(Linkage aLinkage){
			// Finds all the interactions present in a given sentence
			List nList = makeNumList(aLinkage);
			List sList = makeSenList(aLinkage.getSentence(),nList);
			//List sList = makeSenList(aLinkage.getSentence());
			//System.out.println(nList.toString());
			//System.out.println(sList.toString());
		
			List links = aLinkage.getLinks();	
			
			for(int i = 0, n = links.size(); i < n; i++){
					Link aLink = (Link)links.get(i);
					//System.out.println(getWord(aLinkage.getSentence(),aLink.getLeftIndex())+ " "+ aLink.getType()+
					// " "+ getWord(aLinkage.getSentence(),aLink.getRightIndex()));
					System.out.println(sList.get(nList.indexOf(new Integer(aLink.getLeftIndex())))+ " "+ aLink.getType()+
										 " "+ sList.get(nList.indexOf(new Integer(aLink.getRightIndex()))));						
			}
		
			System.out.println("\n" + Sencount++ + "--"+  aLinkage.getSentence()+"\n");
		    System.out.println( "Sentence :" + aLinkage.getSentence());
		
			Sen = aLinkage.getSentence();
		
			Subject = getSubject(sList,nList,links);
			S1 = new Scope(Subject);
			Object = getObject(sList,nList,links);
			S2 = new Scope(Object);
			MPhrase = getMPhrase(sList,nList,links);
			S3 = new Scope(MPhrase);
			MVerb = getMVerb(sList,nList,links);
		
			//S4 = new Scope();
			interactions.isIntWordPresent(Sen);
			
			stepOne();
			stepTwo();
			stepThree();
			stepFour();
			stepFive();
		return false;
		}
	*/
}