/*
 * Created on May 3, 2005
 *
 *	History
 *		
 */
package edu.asu.cse.cbioc.biomedabstract;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author bllogsdon
 *
 * This class represents a BioMedical Abstract
 * 
 */
public class BioMedAbstract {
	private int abstractId;
	private String originalAbstract;
	private String originalArticleTitle;
	private String originalAbstractText;
	private ArrayList authorList;
	private String workingAbstract;
	private ArrayList interactions;
		
	/**
	 * Default Constructor
	 */
	public BioMedAbstract(){
		abstractId = -1;
		originalAbstract = null;
		originalArticleTitle = null;
		originalAbstractText = null;
		authorList = null;
		workingAbstract = null;
	}
	
	/**
	 * Constructor
	 * @param articleTitle	title of the Article
	 * @param authorList	a comma separated string of authors
	 * @param abstractText	the abstract itself
	 */
	public BioMedAbstract(int abstractId, String articleTitle,String authorList, String abstractText){
		this.abstractId = abstractId;
		this.originalArticleTitle = articleTitle;
		this.authorList = buildAuthorList(authorList);
		this.originalAbstractText = abstractText;
		this.originalAbstract = getCompleteAbstract();
		this.workingAbstract = articleTitle + "  " + abstractText;
	}
	
	//*************************************************************************
	//	Getters and Setters
	//*************************************************************************
	
	/**
	 * @return	an abstract from the individual strings
	 */
	private String getCompleteAbstract(){
		return originalArticleTitle + "  " + getAuthorListString() + "  " + originalAbstractText;
	}
	
	/**
	 * This is the original abstract before any modifications are done.  The original
	 * abstract is set once when the instance is created and is then never modified.
	 * @return the original abstract
	 */
	public String getOriginalAbstract(){
		return originalAbstract;
	}
	
	/**
	 * The abstract at this point in time
	 * @return the current modifiable abstract
	 */
	public String getWorkingAbstract(){
		return workingAbstract;
	}
	
	/**
	 * The unmodified Article Title
	 * @return the original Article Title
	 */
	public String getOriginalArticleTitle(){
		return originalArticleTitle;
	}
	
	/**
	 * The list of authors
	 * @return an arraylist of author names
	 */
	public ArrayList getAuthorList(){
		return authorList;
	}
	
	/**
	 * Set the newest version of the working abstract
	 * @param the modified biomedical abstract
	 */
	public void setWorkingAbstract(String bmAbstract){
		this.workingAbstract = bmAbstract;
	}
	
	/**
	 * Builds the list of Authors from a comma seperated string of author names
	 * @param authorList the string of author names
	 * @return an ArrayList of author names
	 */
	private ArrayList buildAuthorList(String authorList){
		String[] sa = authorList.split(",");
		ArrayList ar = new ArrayList();
		
		if(sa.length > 0){
			for(int i=0;i<sa.length;i++){
				ar.add(sa[i]);
			}
		}
		
		return ar;
	}
	
	/**
	 * Builds a comma seperated string of author names from an arraylist
	 * @return a string of author names
	 */
	public String getAuthorListString(){
		String tmpString = new String();
		int i;
		
		for(i=0;i<authorList.size()-1;i++){
			tmpString += (String) authorList.get(i) + ", ";
		}
		tmpString += (String) authorList.get(i);
		
		return tmpString;
	}
	
	public ArrayList getInteractions(){
		return interactions;
	}
	
	public String getInteractionsString(){
		String tmpString = new String();
		int i;
		
		for(i=0;i<interactions.size()-1;i++){
			tmpString += abstractId + "|" + i + "|" + ((String) interactions.get(i)) + "| ";
		}
		tmpString += (String) authorList.get(i);
		
		return tmpString;
	}
	
	public void setInteractions(ArrayList interactions){
		this.interactions = interactions;
	}
	
	/**
	 * Save the original abstract to the given filename
	 * @param fileName name of the file to create or modify
	 */
	public void saveOriginalToFile(String fileName){
		saveToFile(getOriginalAbstract(),fileName);
	}
	
	/**
	 * Save the working abstract to the given filename
	 * @param fileName name of the file to create or modify
	 */
	public void saveWorkingToFile(String fileName){
		saveToFile(getWorkingAbstract(),fileName);
	}
	
	public void saveInteractionsToFile(String fileName){
		String tmpString = new String();
		Iterator i = interactions.iterator();
		
		while(i.hasNext()){
			tmpString += ((String) i.next()) + "\n";
		}
		
		saveToFile(tmpString,fileName);
	}
	/**
	 * Save a given abstract to the given filename
	 * @param bmAbstract is a string of a biomedical abstract
	 * @param fileName is the name of the file to create or modify
	 */
	private void saveToFile(String bmAbstract,String fileName){
		PrintWriter pw;
		try{
			pw = new PrintWriter(new FileOutputStream(fileName));
			pw.print(bmAbstract);
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * If the original abstract is null or empty then the BioMedicalAbstract
	 * is empty
	 * @return if the BioMedicalAbstract is empty
	 */
	public boolean isEmpty(){
		if(originalAbstract == null | originalAbstract.length() == 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Overrided toString() to return a meaningful string for this object
	 */
	public String toString(){
		return getCompleteAbstract();
	}
}
