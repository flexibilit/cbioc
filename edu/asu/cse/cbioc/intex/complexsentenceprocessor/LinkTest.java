package edu.asu.cse.cbioc.intex.complexsentenceprocessor;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class LinkTest{
	
	public  static Hashtable IntHash;
	public  static  PorterStemmer PS;
	private static BufferedWriter  outFile,intFile;
	private  static  LgpWrapper p;
	private static   LinkReader lr;
	public  static int smCount = 0;
	public  static int soCount = 0;
	public static  int Sencount = 0;
	public static  int Intcount = 0,tempCount=0;
	public static String absid = new String() ;
	public static String senid = new String();
	public static String Facts = new String();
	
	public LinkTest(){
		
		
		PS = new PorterStemmer();
		//create hash table for Interaction words
		
		
		Facts = "";
		
		
		
		
	}
	
	
	
	
	
	
		public static String getWord(String Sen,int index){
		
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
	
	public  static List makeNumList(Linkage aLinkage){
		
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
	
	
	private static List makeSenList(String Sen, List nList) {
		
		ArrayList l = new ArrayList();
		for(int i=0; i < nList.size(); i++){
			
			int j = Integer.parseInt(nList.get(i).toString());
			l.add(getWord(Sen,j));
			
		}
		
		return l;
	}
	
	
	
	
	
	public static void main(String[] args){
		
		String sample1 = "GABA mediates the inhibitory effect of NO on the AVP and OXT responses to insulin-induced hypoglycemia";
		//String sample1 = "In summary, we demonstrate that INSULIN treatment results in activation of both PLD1 and PLD2 in appropriate cell types when the appropriate upstream intermediate signalling components, i.e. PKCalpha and PLCgamma, are expressed at sufficient levels";
		String sample2= "Gene1 is activated by Gene2 cell proliferation";
		//String sample2 = "Gene57-Gene58-Gene59-Gene60 is blocked by Gene61 ";
		//String sample2 = "The kinase phosphorylation of Gene1 by the Gene2 could inhibit Gene3 ";
		//String sample2 = "Gene3 inhibits the kinase phosphorylation of Gene1 by the Gene2";
		//String sample2 = "Gene22 Gene23-Gene24 does.v not require.v Gene25";
		//String sample2 = "Gene43-Gene44 can stimulate promoters containing Gene46-Gene47 via a potent transactivation domain in Gene48-Gene49-Gene50 of Gene51-Gene52-Gene53 ";
		
		// Initialize all
		PS = new PorterStemmer();
		//create hash table for Interaction words
		
		
		
		try{
			
			//outFile = new BufferedWriter(new FileWriter("ed smith 7-12.txt"));
			//intFile = new BufferedWriter(new FileWriter("Extracted Interactions 12-15.txt"));
			
			
			p = LgpWrapper.startParser();
			/*p.setTimer(-1);
			p.setMemoryToMax();
			p.setUnionMode();
			*/
			
			lr = new LinkReader();
			
			
			
			
			 String[] result = null; 
			 result = p.parse(sample2);
			 lr.extractLink(sample2, result);
			 System.out.println("length: " + sample2.length());
			  
			 // if(lr.getLinkageCount() > 0 ){
			  
			  Linkage aLinkage = (Linkage)lr.getLinkage(0);
			  

				List nList = makeNumList(aLinkage);
		        List sList = makeSenList(aLinkage.getSentence(),nList);
				List links = aLinkage.getLinks();	
			
				for(int i = 0, n = links.size(); i < n; i++){
						Link aLink = (Link)links.get(i);
						System.out.println(getWord(aLinkage.getSentence(),aLink.getLeftIndex())+ " "+ aLink.getType()+
						 " "+ getWord(aLinkage.getSentence(),aLink.getRightIndex()));
						System.out.println(sList.get(nList.indexOf(new Integer(aLink.getLeftIndex())))+ " "+ aLink.getType()+
											 " "+ sList.get(nList.indexOf(new Integer(aLink.getRightIndex()))));						
				}




			   
			   
			  // }
			   
			   
			
			
			
		
			p.quit();
			
			
			
		}catch ( Exception e ) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
