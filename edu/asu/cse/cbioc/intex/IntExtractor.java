package edu.asu.cse.cbioc.intex;

import java.util.ArrayList;
import java.util.Iterator;

public class IntExtractor{
	private ArrayList interactionsFound;
	private Interactions interactions;
	private IntFinder intFinder;
	
	public IntExtractor(){
		interactionsFound = new ArrayList();
		interactions = new Interactions();
		intFinder = new IntFinder();
	}
	
	private ArrayList extractSentences(String sentences){
    	String[] sentenceArray = sentences.split("\n");
        ArrayList sentenceList = new ArrayList();
    	for (int i=0; i < sentenceArray.length; i++){
            if (sentenceArray[i].length() > 0)
                sentenceList.add(sentenceArray[i]);
        }
    	return sentenceList;
    }
	
	public ArrayList findInteractions(String content){
		String[] result = null;
		String line = new String();
		
		ArrayList sentences = extractSentences(content);
		Iterator iter = sentences.iterator();
	
		while(iter.hasNext()){
			line = (String) iter.next();
			
			//System.out.println(line);
			String[] sentenceParts = line.split("\\|",4);
			String[] mphrases = null;
			String[] objects = null;
			
			
			if(sentenceParts.length>2){
				objects = sentenceParts[2].split("#");
			}
			if(sentenceParts.length >3){
				mphrases = sentenceParts[3].split("#");
			}
			
			if(objects!=null){
				// call intfinder here
				for(int i=0;i < objects.length;i++){
					if(mphrases!=null){
						for(int j=0; j<mphrases.length; j++){
							System.out.println("Searching for Interactions!");
							interactionsFound.addAll(intFinder.findInteraction(sentenceParts[0],sentenceParts[1],objects[i],mphrases[j],line));
							System.out.println("Search Complete");
						}
					}else{
						interactionsFound.addAll(intFinder.findInteraction(sentenceParts[0],sentenceParts[1],objects[i],"",line));
					}
				}
			}else{
				interactionsFound.addAll(intFinder.findInteraction(sentenceParts[0],sentenceParts[1],"","",line));
			}
		}
		return interactionsFound;
	}
}
