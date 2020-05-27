package edu.asu.cse.cbioc.intex.pronounresolution;

import java.util.ArrayList;

/*
 * Created on May 20, 2005
 * 
 */

/*
 * Created on May 20, 2005
 * 
 */

/*
 * Created on May 20, 2005
 * 
 */

/*
 * Created on May 20, 2005
 * 
 */

/**
 * @author Luis Tari
 *
 * PronResPreProcessor is a class that serves as the preprocessing of pronoun resolution,
 * so that redundant pronouns are removed.
 */
public class PronResPreProcessor
{
    // a list of tagged sentences
    private ArrayList sentences;
    // a list of words in a tagged sentence
    private ArrayList words;
    // a list of corresponding POS tags
    private ArrayList posTags;
    // preprocessed tagged sentence
    private String output;
    // symbol for line separator
    private String lineSeparator;
    // a list of preprocessed tagged sentences
    private ArrayList processedSentences;
    
    public PronResPreProcessor(){
    	init();
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
    
    public String preProcess(String content){
     	sentences = extractSentences(content);

        // content is read from String and do preprocessing
        for (int i=0; i < sentences.size(); i++)
        {
            String sent = (String) sentences.get(i);
            parseTaggedSent(sent);
            removeRedundancy();
            removeRedundantBy();
            removeRedundantIts();
            performOutput();
        }
    	return this.output;
    }
    
    /**
     * Initializes the arrays
     *
     */
    private void init()
    {
        this.output = new String();
        this.sentences = new ArrayList();
        this.words = new ArrayList();
        this.posTags = new ArrayList();
        this.processedSentences = new ArrayList();
        this.lineSeparator = System.getProperty("line.separator");
    }
    
    /**
     * Parse the tagged sentence
     * @param sentence tagged sentence
     */
    private void parseTaggedSent(String sentence)
    {
        this.words = new ArrayList();
        this.posTags = new ArrayList();

        // store the words and their corresponding POS tags
        // into ArrayLists
        String[] tokens = sentence.split(" ");
        for (int i=0; i < tokens.length; i++)
        {
            if (tokens[i].indexOf("/") > -1)
            {
                String[] tokens2 = tokens[i].split("/");
                this.posTags.add(tokens2[1]);
                this.words.add(tokens2[0]);
            }
        }
    }
    /**
     * Removes redundant "itself" and "themselves" that show up right after the POS tag "NN"
     * e.g. Gene A itself activates Gene B
     * => Gene A activates Gene B
     */
    private void removeRedundancy()
    {
        for (int i=words.size()-1; i > -1; i--)
        {
            String word = (String) words.get(i);
            if ((word.equalsIgnoreCase("itself")) || (word.equalsIgnoreCase("themselves")))
            {
                // check previous pos tag
                String posTag = (String) posTags.get(i-1);
                if (posTag.indexOf("NN") > -1)
                {
                    words.remove(i);
                    posTags.remove(i);
                }
            }
        }        
    }
    
    /**
     * Removes "itself" and "themselves" that follows the pattern "VBZ, by itself|themselves"
     * e.g. Gene A activates, by itself, gene B.
     * => Gene A activates gene B.
     */
    private void removeRedundantBy()
    {
        for (int i=words.size()-1; i > -1; i--)
        {
            String word = (String) words.get(i);
            if ((word.equalsIgnoreCase("itself")) || (word.equalsIgnoreCase("themselves")))
            {
                try
                {
                    // the word that places one position ahead of the word "itself" or "themselves"
                    String prevWord = (String) words.get(i-1);
                    // the word that places two positions ahead of the word "itself" or "themselves"
                    String prevWord2 = (String) words.get(i-2);
                    
                    if (prevWord.equalsIgnoreCase("by") && prevWord2.equalsIgnoreCase(","))
                    {                    
	                    // check the POS tag before the word that contains a comma
	                    String posTag = (String) posTags.get(i-3);
	                    if (posTag.indexOf("VBZ") > -1)
	                    {
	                        String afterWord = (String) words.get(i+1);
	                        if (afterWord.equalsIgnoreCase(","))
	                        {
		                        words.remove(i+1);
		                        posTags.remove(i+1);
	                        }
	                        words.remove(i);
	                        posTags.remove(i);
	                        words.remove(i-1);
	                        posTags.remove(i-1);
	                        words.remove(i-2);
	                        posTags.remove(i-2);
	                    }
                    }
                }
                catch (IndexOutOfBoundsException e)
                {
                    
                }
            }
        }
    }
    
    /**
     * Replaces redundant "its" and "their" that follows the pattern "NN and its|their" with the noun group
     *
     */
    private void removeRedundantIts()
    {        
        for (int i=words.size()-1; i > -1; i--)
        {
            String word = (String) words.get(i);
            
            if ((word.equalsIgnoreCase("its")) || (word.equalsIgnoreCase("their")))
            {
                // check the POS tag that is placed one position ahead of the word "its" or "their"
                String prevPosTag = (String) posTags.get(i-1);
                // check the POs tag that is placed two positions ahead of the word "its" or "their"
                String prevPosTag2 = (String) posTags.get(i-2);
                // replace the pronoun with the noun that precedes the conjunction
                if ((prevPosTag2.indexOf("NNP") > -1) && (prevPosTag.indexOf("CC") > -1))
                {
                    String wordNG = (String) words.get(i-2);
                    words.set(i, wordNG);
                    posTags.set(i, "NNP");
                }
            }
        }       
    }
    
    /**
     * Add each preprocessed sentence into the processedSentences ArrayList
     *
     */
    private void performOutput()
    {
        String temp = new String();
        for (int i=0; i < words.size(); i++)
        {
            String word = (String) words.get(i);
            String posTag = (String) posTags.get(i);
            temp += word + "/" + posTag + " ";
        }
        
        this.processedSentences.add(temp);
        //System.out.println(temp);
        this.output += temp + "\n\n";
    }
}