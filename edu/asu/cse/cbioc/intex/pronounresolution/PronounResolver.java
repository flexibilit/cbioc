package edu.asu.cse.cbioc.intex.pronounresolution;

import java.util.ArrayList;
import java.util.StringTokenizer;

/*
 * Created on Jun 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Kalpesh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PronounResolver {
	private ArrayList resolvedSentences = new ArrayList();
    // a list of tagged sentences
    private ArrayList sentences;
	private String[] frame;// = new String[50];
	private int frameNumber=0;				
	private SemanticChunkObject[] sChunkObject;// = new semanticChunkObject[100];
	private AnaphoraObject[] anaphor;// = new AnaphoraObject[100];
	private int scount;// = 0;
	private int acount;// = 0;		
	private int distance;// =0;
	
    public PronounResolver(){
    	init();
    }
    
    /**
     * Initializes the arrays
     *
     */
    private void init(){
    	resolvedSentences = new ArrayList();
        this.sentences = new ArrayList();
    	frame = new String[50];
    	frameNumber=0;				
    	sChunkObject = new SemanticChunkObject[1000];
    	anaphor = new AnaphoraObject[100];
        scount = 0;
    	acount = 0;		
    	distance =0;
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
    
    public String resolve(String content){
    	sentences = extractSentences(content);
		
    	for (int i=0; i < sentences.size(); i++){
    		String temp = "";	        	
	        StringTokenizer st = new StringTokenizer((String) sentences.get(i));
	        
	        while (st.hasMoreTokens()){
	        	String token = st.nextToken();
	        	temp = temp.concat(token);
	        	temp = temp.concat(" ");
	        	if(token.endsWith("./.")){
	        		frame[frameNumber] = temp;
	        		frameNumber++;
	        		temp = "";	        		
	        	}        		
	        }
	    }
    
		for (int k=0;k<frameNumber; k++){			
			String[] result = frame[k].split("\\s");
			String finalSentence = "";
			
			for (int x=0;x<result.length; x++){
				distance++;
				String word = extractName(result[x]);
				if(word.equalsIgnoreCase("it") == true ||word.equalsIgnoreCase("its") == true  || word.equalsIgnoreCase("their") == true || word.equalsIgnoreCase("themselves") == true || word.equalsIgnoreCase("they")== true){	
					String AnaphoraName = extractName(result[x]);
					String TAG = extractTag(result[x]);
					String AType = extractName(result[x]);					
					anaphor[acount] = new AnaphoraObject((k+1),TAG,AnaphoraName,distance );	
					
					if(AnaphoraName.equalsIgnoreCase("it") == true ||AnaphoraName.equalsIgnoreCase("its") == true ){						
						String replace = processIT(anaphor[acount], sChunkObject,scount);
						//System.out.println("ITS : "+ replace);
						finalSentence = finalSentence.concat(replace);
						finalSentence = finalSentence.concat(" ");
					}
					if(AnaphoraName.equalsIgnoreCase("their") == true){
						
						String replace2 = processTheirs(anaphor[acount], sChunkObject,scount);
						//System.out.println("Their : "+ replace2 );
						finalSentence = finalSentence.concat(processTheirs(anaphor[acount], sChunkObject,scount));
						finalSentence = finalSentence.concat(" ");
					}
					
					if(AnaphoraName.equalsIgnoreCase("themselves") == true){
						finalSentence = finalSentence.concat(processThemselves(anaphor[acount], sChunkObject,scount));
						finalSentence = finalSentence.concat(" ");
						//processThemselves(Anaphor[acount], SChunkObject,scount);
					}
					
					if(AnaphoraName.equalsIgnoreCase("they") == true){
						finalSentence = finalSentence.concat(processTheirs(anaphor[acount], sChunkObject,scount));
						finalSentence = finalSentence.concat(" ");
					}
					
					acount++;
					
					for(int p=0; p<scount;p++){
						sChunkObject[p].setScore(0);
					}
					
					//send this for processesing and add it to the final sentence				
				}else{
					if(((x+1)<result.length) && (result[x].indexOf("/NN") != -1 ||result[x].indexOf("/NNP") != -1 ) && result[x+1].indexOf("/NN") != -1){
						String temp = extractName(result[x]);
						temp = temp.concat(" ");
						temp = temp.concat(extractName(result[x+1]));
						String stag = extractTag(result[x]);
						String scoName = temp;
						int score = 0;
						sChunkObject[scount] = new SemanticChunkObject((k+1),stag, scoName,distance, score);
						scount++;
						x++;
						//add it to the final sentence
						finalSentence = finalSentence.concat(scoName);
						finalSentence = finalSentence.concat(" ");
					}else{
						if(result[x].startsWith("GENE_") == true || result[x].indexOf("/NNP") != -1 || result[x].indexOf("/NNS") != -1 || result[x].indexOf("/NN") != -1  )
						{
							String stag = extractTag(result[x]);
							String scoName = extractName(result[x]);
							int score = 0;
							sChunkObject[scount] = new SemanticChunkObject((k+1),stag, scoName,distance, score);
							scount++;
							//add it to the final sentence
							finalSentence = finalSentence.concat(scoName);
							finalSentence = finalSentence.concat(" ");						
						}
						else{
							//just a regualr word and add it to the final sentence
							finalSentence = finalSentence.concat(extractName(result[x]));
							finalSentence = finalSentence.concat(" ");
						}
					}
				}
			}
			resolvedSentences.add(finalSentence);
		}
		return writeOutput(resolvedSentences);
    }
    
    public  String processIT(AnaphoraObject anaphora, SemanticChunkObject[] sco, int stop){
		//System.out.println("came here");
		String replace = "";
		String[][] candidates = new String[500][2];
		//System.out.println("------ Aanphora----:" + anaphora.getAnaphoraType());
		//System.out.println("frame number of anaphora :"+anaphora.getFrameNum());
		int frameLocation = 0;
		int flag=0;
		
		for(int x=0; x<stop; x++)
		{			
			if( anaphora.getFrameNum() - sco[x].getFrameNumber()== 0  && sco[x].getPOStag().equalsIgnoreCase("NN")==true)
			{
				//System.out.println("Comes here");
				replace = sco[x].getstringName();
				//System.out.println("replace :"+ replace);
				flag = 1;							
			}		
		}		
		return replace;
	}
	
    public static String processTheirs(AnaphoraObject anaphora, SemanticChunkObject[] sco, int stop){
    	String[][] candidates = new String[10][3];
    	int CandidateCount = 0;
    	String replace = "their";
    	int flag = 0;
    	for(int x=0; x<stop;x++){
    		if( flag ==0 && (anaphora.getFrameNum() - sco[x].getFrameNumber()) == 0 && (sco[x].getPOStag().indexOf("NN")) != -1 ){
    			candidates[CandidateCount][0] = sco[x].getstringName();
    			candidates[CandidateCount][1] = String.valueOf(sco[x].getDistance());
    			candidates[CandidateCount][2] = sco[x].getPOStag();
    			replace = candidates[CandidateCount][0];
    			//System.out.println(candidates[CandidateCount][0]);
    			//flag=1;
    		}
    	}
    	
    	if(flag ==0){
    		for(int counter=(stop-1); counter>1; counter--){
    			if(flag ==0 && sco[counter].getPOStag().equalsIgnoreCase("NNPS")==true ||sco[counter].getPOStag().equalsIgnoreCase("NNS") ==true){
    				//System.out.println(sco[counter].getstringName());
    				replace = sco[counter].getstringName();
    				flag =1;
    			}
    		}
    	}
    	return replace;
    }
    
    public static String processThemselves(AnaphoraObject anaphora, SemanticChunkObject[] sco, int stop){
    	String[][] candidates = new String[10][3];
    	int CandidateCount = 0;
    	String replace = "themselves";
    	
    	//System.out.println(anaphora.getFrameNum());
    	int flag = 0;
    	
    	for(int x=0; x<stop;x++){
    		if(flag == 0 && (anaphora.getFrameNum() - sco[x].getFrameNumber()) == 0 && (sco[x].getPOStag().equalsIgnoreCase("NNP"))== true ){
    			candidates[CandidateCount][0] = sco[x].getstringName();
    			candidates[CandidateCount][1] = String.valueOf(sco[x].getDistance());
    			candidates[CandidateCount][2] = sco[x].getPOStag();
    			replace = candidates[CandidateCount][0];
    			flag = 1;
    		}
    	}
    	
    	for(int counter=0; counter<CandidateCount;counter++){
    		System.out.println(candidates[counter][0]);
    		if(candidates[counter][2].equalsIgnoreCase("NNP")== true ||candidates[counter][2].equalsIgnoreCase("NNPS")== true ){
    			replace = "themselves";
    		}
    	}
    	return replace;
    }
    
	public String extractName(String word){
		int end = word.indexOf("/");
		String name = word.substring(0,end);
		return name;
	}
	private String extractTag(String word)
	{
		int start = word.indexOf("/");
		String tag = word.substring((start+1));
		//System.out.println(tag);
		return tag;		
	}
	
    private String writeOutput(ArrayList resolvedSentences){
    	String output = new String();
    	if (resolvedSentences.size() > 1){
    		String sentence = (String) resolvedSentences.get(0);
            output += sentence + "\n";
        }
    	
    	for (int i=1; i < resolvedSentences.size(); i++){
    		String sentence = (String) resolvedSentences.get(i);
    		output += "\n";
    		output += sentence + "\n";
    	}
    	return output;
    }
}
