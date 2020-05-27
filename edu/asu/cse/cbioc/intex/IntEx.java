package edu.asu.cse.cbioc.intex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import montytagger.JMontyTagger;
import abner.Tagger;
import edu.asu.cse.cbioc.biomedabstract.BioMedAbstract;
import edu.asu.cse.cbioc.biomedabstract.PubMedExtractor;
import edu.asu.cse.cbioc.intex.complexsentenceprocessor.ComplexSentenceBreaker;
import edu.asu.cse.cbioc.intex.linkgrammer.LinkGrammerFormatter;
import edu.asu.cse.cbioc.intex.pronounresolution.PronResPreProcessor;
import edu.asu.cse.cbioc.intex.pronounresolution.PronounResolver;

/*
 * Created on May 6, 2005
 *
 * History
 * 	May 12	-	Documented File for team to add their data.
 * 
 * 
 */

/**
 * @author Brandon Logsdon
 *
 * 
 */
public class IntEx {
	private BioMedAbstract bmAbstract;
	
	/**
	 * Default Constructor:  Hidden so that the Static method extractFromPubMed
	 * is the only way to create an instance.
	 */
	private IntEx(){
		this.bmAbstract = null;
	}

	/**
	 * Constructor:  Hidden so that only the Static method extractFromPubMed
	 * can call it to create an instance of IntEx.
	 */
	private IntEx(BioMedAbstract bmAbstract){
		this();
		this.bmAbstract = bmAbstract;
	}
	
	/**
	 * Static Method used to create an instance of IntEx
	 * Currently this is the only way to create an IntEx instance.
	 * In the future we may add more if we extract from different databases
	 * 
	 * PreCondition: The pubMedId is a valid pubMedId
	 * 
	 * If the PreCondition fails then null will be returned until I create
	 * an Exception for it.
	 * 
	 * @return an instance of IntEx with completed extraction
	 * 
	 */
	public static IntEx extractFromPubMed(int pubMedId){
		BioMedAbstract bmAbstract = PubMedExtractor.getAbstractFromPubmed(String.valueOf(pubMedId));
		if(bmAbstract != null){
			IntEx ie = new IntEx(bmAbstract);
			
			ie.extract();
		
			return ie;
		}
		return null;
	}
	
	public static ArrayList extractFromAbstractsInDirectory(String inputDirectory,String outputDirectory){
		File directory = new File(inputDirectory);
		BioMedAbstract bmAbstract = new BioMedAbstract();
		String tmpStringFile = new String();
		String[] fileNames = directory.list();
		ArrayList intExs = new ArrayList();
		
		int count = 0;
			
		for (int i = 0; i < fileNames.length; i++){
			int index = fileNames[i].indexOf(".txt");
			int lastDot = fileNames[i].lastIndexOf(".");
			if ((index < 0) || index != lastDot){
				continue;
			}
				
			count++;
			String pubmed = fileNames[i].substring(0, index);
			System.out.println("\nProcessing file " + fileNames[i]);
			System.out.println("File " + (count) + " of " + fileNames.length);

			try{
				BufferedReader br = new BufferedReader(new FileReader(fileNames[i]));
				String line = "";
		
				while ((line = br.readLine()) != null) {
					tmpStringFile += line;
				}
				
				bmAbstract = new BioMedAbstract(Integer.parseInt(pubmed),"","",tmpStringFile);
				
				if(bmAbstract != null){
					IntEx ie = new IntEx(bmAbstract);
					
					ie.extract();
					
					outputDirectory = (outputDirectory.charAt(outputDirectory.length()-1) == '/' ? outputDirectory = outputDirectory.substring(0,outputDirectory.length()-2) : outputDirectory);
					
					ie.getBioMedAbstract().saveInteractionsToFile(outputDirectory + "/" + pubmed + ".interactions.txt");
					intExs.add(ie);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return intExs;
	}
	
	//*************************************************************************
	//	Getters and Setters
	//*************************************************************************
	/**
	 * The getter returns the local copy of the BioMedAbstract instance that was used during
	 * extraction.
	 * @return the local instance of the BioMedAbstract bmAbstract
	 * @see BioMedAbstract
	 */
	public BioMedAbstract getBioMedAbstract(){
		return this.bmAbstract;
	}
	
	/**
	 * This is the "MAIN" method used by this class to extract the gene-gene, and protein-protein
	 * interactions.  Extract runs each module one at a time processing the working abstract as it
	 * goes.  Currently the extract method uses the System.out IOStream in order to display progress.
	 * I will be updating this to use log4J and a specified IOStream for further flexibility.
	 * 
	 * PreCondition:	bmAbstract is not null
	 * @return if the extract process completed successfuly(true), or unsuccessfuly(false)
	 */
	public boolean extract(){
		try{
			//*****************************************************************
			// Abner Tagger
			System.out.println("Tagging Abstract Using BioCreative Protein Tagger (ABNER)...");
			abnerTagger();
			System.out.println("Tagging Complete");
			//*****************************************************************
			
			//System.out.println(bmAbstract.getWorkingAbstract());
			System.out.println("Continuing");
			
			//*****************************************************************
			// Link Grammer PreProcessing
			System.out.println("Formatting Tagged Abstract for LinkGrammer");
			prepareAbstractForLinkGrammer();
			System.out.println("Format Complete");
			//*****************************************************************
			
			//System.out.println(bmAbstract.getWorkingAbstract());
			System.out.println("Continuing");
			
			//*****************************************************************
			// Monty Tagger
			System.out.println("Tagging Abstract Using JMontyTagger...");
			montyTagger();
			System.out.println("Tagging Complete");
			//*****************************************************************
			
			//System.out.println(bmAbstract.getWorkingAbstract());
			System.out.println("Continuing");
			
			//*****************************************************************
			// Pronoun resolution PreProcessor
			System.out.println("Pronoun Preprocessing...");
			pronounResPreProcessor();
			System.out.println("Preprocessing Complete");
			//*****************************************************************
			
			//System.out.println(bmAbstract.getWorkingAbstract());
			System.out.println("Continuing");
			
			//*****************************************************************
			// Pronoun resolution
			System.out.println("Pronoun Resolution...");
			pronounResolution();
			System.out.println("Resolution Complete");
			//*****************************************************************
			
			//System.out.println(bmAbstract.getWorkingAbstract());
			System.out.println("Continuing");
			
			//*****************************************************************
			// Complex Sentence Processing
			System.out.println("Complex Sentence Processing...");
			complexSentenceProcessing();
			System.out.println("Processing Complete");
			//*****************************************************************
			
			//System.out.println(bmAbstract.getWorkingAbstract());
			System.out.println("Continuing");
			
			//*****************************************************************
			// Interaction Extraction
			System.out.println("Interaction Extraction...");
			interactionExtraction();
			System.out.println("Extraction Complete");
			//*****************************************************************
			
			//System.out.println(bmAbstract.getWorkingAbstract());
			System.out.println("Continuing");
			
			//*****************************************************************
			// TODO The next step in the process
			System.out.println("This phase is under Construction");
			//*****************************************************************
			
			// Extract completed successfully
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * This method uses the Abner.jar file to tag the bmAbstract
	 * The tagger is currently useing the BIOCREATIVE library for tagging.
	 * It is also being returned in SGML.  There are others if needed but this
	 * seems to be the best for us.
	 * 
	 * PreCondition:	bmAbstract is not null
	 * 
	 * @see Abner.jar
	 *
	 */
	private void abnerTagger(){
		Tagger bcTagger = new Tagger(Tagger.BIOCREATIVE);
		String results = bcTagger.tagSGML(bmAbstract.getWorkingAbstract());
		bmAbstract.setWorkingAbstract(results);
	}
	
	private void montyTagger(){
		JMontyTagger jmt = new JMontyTagger();
		String results = jmt.Tag(bmAbstract.getWorkingAbstract());
		bmAbstract.setWorkingAbstract(results);
	}
	
	private void pronounResPreProcessor(){
		PronResPreProcessor ppp = new PronResPreProcessor();
		bmAbstract.setWorkingAbstract(ppp.preProcess(bmAbstract.getWorkingAbstract()));
	}
	
	private void pronounResolution(){
		PronounResolver pr = new PronounResolver();
		bmAbstract.setWorkingAbstract(pr.resolve(bmAbstract.getWorkingAbstract()));
		
	}
	
	private void complexSentenceProcessing(){
		ComplexSentenceBreaker csb = new ComplexSentenceBreaker();
		bmAbstract.setWorkingAbstract(csb.processString(bmAbstract.getWorkingAbstract()));
	}
	
	private void interactionExtraction(){
		IntExtractor ie = new IntExtractor();
		bmAbstract.setInteractions(ie.findInteractions(bmAbstract.getWorkingAbstract()));
	}
	/**
	 * This method uses LinkGrammerFormatter to format the working bmAbstract in a
	 * manner such that LinkGrammer works better.
	 * 
	 * @see LinkGrammerFormatter
	 * 
	 */
	private void prepareAbstractForLinkGrammer(){
		LinkGrammerFormatter lgf = new LinkGrammerFormatter();
		String results = lgf.format(bmAbstract.getWorkingAbstract());
		bmAbstract.setWorkingAbstract(results);
	}
	
	//*************************************************************************
	//	add helper methods here to access your modules
	
	
	
	
	//*************************************************************************
}
