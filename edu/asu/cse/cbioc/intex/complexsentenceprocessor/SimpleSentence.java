package edu.asu.cse.cbioc.intex.complexsentenceprocessor;

/*
 * Created with Eclipse IDE
 * user: Deepthi
 * date: Jun 2, 2004
 * SimpleSentence.java
 */

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author deepthi
 * 
 * Copyright 2004, CIPS. All rights reserved.
 * 
 * A datastructure to hold the simple sentence contains subject, object, verb
 * and modifying phrase
 */
public class SimpleSentence {

	private String subject;

	private String verb;

	private ArrayList objects;

	private ArrayList modifyingPhrases;

	private ArrayList coPhrases;

	private int verbStart;

	private int verbEnd;

	private int subjectStart;

	private int subjectEnd;

	private ArrayList modStartIndices;

	private ArrayList coPhraseStartIndices;

	/**
	 * Default constructor.
	 *  
	 */
	public SimpleSentence() {
		modStartIndices = null;
		modifyingPhrases = null;
	}

	/**
	 * @return the list of modiying phrases
	 */
	public ArrayList getModifyingPhrases() {
		return modifyingPhrases;
	}

	/**
	 * @return the list of objects
	 */
	public ArrayList getObjects() {
		return objects;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the verb
	 */
	public String getVerb() {
		return verb;
	}

	/**
	 * @param list
	 *            the list of MPs
	 */
	public void setModifyingPhrases(ArrayList list) {
		modifyingPhrases = list;
	}

	/**
	 * @param list
	 *            the list of objects
	 */
	public void setObjects(ArrayList list) {
		objects = list;
	}

	/**
	 * @param string
	 *            the subject
	 */
	public void setSubject(String string) {
		subject = string;
	}

	/**
	 * @param string
	 *            the verb
	 */
	public void setVerb(String string) {
		verb = string;
	}

	/**
	 * @return the end of range for the verb
	 */
	public int getVerbEnd() {
		return verbEnd;
	}

	/**
	 * @return the start of range for the verb
	 */
	public int getVerbStart() {
		return verbStart;
	}

	/**
	 * @param i
	 *            the end of range of verb
	 */
	public void setVerbEnd(int i) {
		verbEnd = i;
	}

	/**
	 * @param i
	 *            the start of range
	 */
	public void setVerbStart(int i) {
		verbStart = i;
	}

	/**
	 * @return the end of subject range
	 */
	public int getSubjectEnd() {
		return subjectEnd;
	}

	/**
	 * @return the start of subject range
	 */
	public int getSubjectStart() {
		return subjectStart;
	}

	/**
	 * @param i
	 *            the end of subject range
	 */
	public void setSubjectEnd(int i) {
		subjectEnd = i;
	}

	/**
	 * @param i
	 *            the start of subject range
	 */
	public void setSubjectStart(int i) {
		subjectStart = i;
	}

	/**
	 * @return the list of starting indices for modifying phrases
	 */
	public ArrayList getModStartIndices() {
		return modStartIndices;
	}

	/**
	 * @param list
	 *            the list of starting indices for modifying phrases
	 */
	public void setModStartIndices(ArrayList list) {
		modStartIndices = list;
	}

	/**
	 * 
	 * @param i
	 *            A start index for MP
	 */
	public void addModStartIndex(int i) {
		if (modStartIndices == null) modStartIndices = new ArrayList();
		modStartIndices.add(new Integer(i));
	}

	/**
	 * 
	 * @param s
	 *            A MP
	 */
	public void addModifyingPhrase(String s) {
		if (modifyingPhrases == null) modifyingPhrases = new ArrayList();
		modifyingPhrases.add(s);
	}

	/**
	 * @return the list of subject modifying phrases
	 */
	public ArrayList getCoPhrases() {
		return coPhrases;
	}

	/**
	 * @param list
	 *            the list of subject modifying phrases
	 */
	public void setCoPhrases(ArrayList list) {
		coPhrases = list;
	}

	/**
	 * 
	 * @param s
	 *            the subject modifying phrases
	 */
	public void addcoPhrase(String s) {
		if (coPhrases == null) coPhrases = new ArrayList();
		coPhrases.add(s);
	}

	/**
	 * @return the list of subject modifying phrases start indices
	 */
	public ArrayList getCoPhraseStartIndices() {
		return coPhraseStartIndices;
	}

	/**
	 * @param list
	 *            the list of subject modifying phrases start indices
	 */
	public void setCoPhraseStartIndices(ArrayList list) {
		coPhraseStartIndices = list;
	}

	/**
	 * 
	 * @param i
	 *            the start index of subject modifying phrases
	 */
	public void addcoPhraseStartIndex(int i) {
		if (coPhraseStartIndices == null)
			coPhraseStartIndices = new ArrayList();
		coPhraseStartIndices.add(new Integer(i));
	}
	
	public String toString(){
		String tmpString = "";
		tmpString += getSubject() + "|";
		tmpString += getVerb() + "|";
		if (getObjects() != null) {
			Iterator o = getObjects().iterator();
			while(o.hasNext()) {
				tmpString += o.next().toString() + "#";
			}
		}
		tmpString += "|";
		if (getModifyingPhrases() != null) {
			Iterator j = getModifyingPhrases().iterator();
			while (j.hasNext())
				tmpString += j.next().toString() + "#";
		}
		tmpString += "|";
		tmpString += System.getProperty("line.separator");
		
		return tmpString;
	}

}