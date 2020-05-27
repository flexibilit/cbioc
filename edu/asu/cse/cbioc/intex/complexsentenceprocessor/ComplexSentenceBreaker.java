package edu.asu.cse.cbioc.intex.complexsentenceprocessor;
/*
 * Created with Eclipse IDE
 * Date: Jun 7, 2004
 * user: deepthi
 * ComplexSentenceBreaker.java
 *
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author deepthi (modified by Luis)
 * 
 * Copyright CIPS, 2004. All rights reserved.
 * 
 * The class takes sentences that are resolved by pronoun resolution as input,
 * and the resultant output is the simple sentences utilizing the Link Grammar Parser
 */

public class ComplexSentenceBreaker {
    
	/**
	 * The instance of the link grammar parser wrapper class
	 */
	private LgpWrapper lgpWrapper; 

	/**
	 * The class that reads the linkages from the parser and converts them to
	 * internal representation
	 */
	private LinkReader linkReader;

	/**
	 * Default constructor Initilizes all data structures.Loads the
	 * dictionaries, prolog modules and starts the link grammar parser
	 *  
	 */
	public ComplexSentenceBreaker() {
		lgpWrapper = LgpWrapper.startParser();
		lgpWrapper.setMemoryToMax();
		lgpWrapper.setUnionMode();
		lgpWrapper.setTimer(30);
		lgpWrapper.setPanicMode();
		linkReader = new LinkReader();
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
	
	public String processString(String content){
		String line = "";
		String prefix = "";

		//System.out.println("Extracting Sentences");
		ArrayList sentences = extractSentences(content);
		Iterator i = sentences.iterator();
		String output = "";
		//System.out.println("Iterate through " + sentences.size() + " sentences.");
		while (i.hasNext()) {
			line = (String) i.next();
			//System.out.println(line);
			if (line.endsWith("|"))
				line = line.substring(0, line.length() - 1);
			if (line.indexOf("|") > 0) {
				prefix = line.substring(0, line.lastIndexOf("|"));
				line = line.substring(line.lastIndexOf("|") + 1, line
						.length());
			} else if (!prefix.equals("")) {
				//prefix = fileName;
			}
			if (line.equals("")) continue;
			line = line.replaceAll("(\\,\\s*and)", " and");
			line = line.replaceAll("(\\,\\s*but)", " but");
			line = line.replaceAll("(\\,\\s*yet)", " yet");
			line = line.replaceAll("(\\,\\s*or)", " or");
			/*
			 * each line is a sentence as a result of preprocessing. send
			 * each sentence for processing by the link grammr
			 */
			try{
				output += processSentence(line);
				//System.out.println(output);
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
		return output;
	}

	/**
	 * 
	 * @param sentence
	 *            the sentence to be processed
	 * @param fileName
	 *            name of the file to store the output
	 * @param prefix
	 *            the abstracts id and sentence id
	 * @param discardFileName
	 *            the name of the file used to write out sentences discarded by
	 *            link grammar if sentence too long / word too long / sentence
	 *            structure crashes link grammar
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public String processSentence(String sentence) throws IOException, NullPointerException {
		StringTokenizer st = new StringTokenizer(sentence);
		//discard if sentence has more than 70 words
		if (st.countTokens() > 70) {
			System.err.println("Sentence too long for link grammar");
			return "";
		}
		//discard if any word is more than 50 characters long
		while (st.hasMoreTokens()) {
			if (st.nextToken().length() >= 50) {
				System.err.println("Word too long for link grammar to parse ");
				return "";
			}
		}
		//discard if no linkages have been found for the sentence
		//System.out.println("Before lgpWrapper");
		String[] parseResult = lgpWrapper.parse(sentence);
		linkReader.extractLink(sentence, parseResult);
		if (linkReader.getLinkageCount() <= 0) {
			System.err.println("No linkages found");
			return "";
		}

		Linkage linkage = linkReader.getLinkage(0);
		
		//analyze the linkage to get the clause components
		ArrayList simpleSentenceList = populateStruct(linkage.getLinks(),linkage.getSentence());
		
		if (simpleSentenceList == null) {
			return "";
		}
		//write clauses to file in bar separated format
		
		Iterator i = simpleSentenceList.iterator();
		String output = "";
		while(i.hasNext()){
			output += ((SimpleSentence) i.next()).toString() + "\n\n";
		}
		
		return output;
	}

	// from IntFinder - new that T sent
	/**
	 * Gets the word that contains the character position.
	 */
	public String getWord(String Sen, int index) {

		String temp = new String();
		int len = 0;

		StringTokenizer st = new StringTokenizer(Sen);
		String token = new String();

		while (st.hasMoreTokens()) {
			token = st.nextToken();
			len = len + token.length() + 1;
			if (len > index) {
				token = token.trim();
				return token;
			}
		}
		return null;
	}

	/**
	 * Removes suffixes to denote noun, verb, preposition etc from the word to
	 * return the word alone.
	 * 
	 * @param str
	 *            The string to be processed
	 * @return the Suffix-removed string
	 */
	private String removeSuffix(String str) {
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		StringTokenizer st = new StringTokenizer(str);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token.trim();

			if (token.startsWith("["))
				token = token.substring(1, token.length());
			if (token.endsWith(".v") || token.endsWith(".n")
					|| token.endsWith(".a") || token.endsWith(".g")
					|| token.endsWith(".p") || token.endsWith(".c")
					|| token.endsWith(".e") || token.endsWith(".d")
					|| token.endsWith(".s"))
				token = token.substring(0, token.length() - 2);

			if (token.endsWith("[?]") || token.endsWith("[!]"))
				token = token.substring(0, token.length() - 3);
			if (token.endsWith("]"))
				token = token.substring(0, token.length() - 1);
			if (token.equals("(") || token.equals(")")) {
				token = "";
			}
			ret.append(token);
			if (st.hasMoreTokens() && !token.equals("")) ret.append(" ");
		}

		return ret.toString();
	}

	/**
	 * 
	 * @param links
	 *            the list of linkage representations between the words
	 * @param sentence
	 *            the sentence being processed
	 * @return Returns the Arraylist containing the simple sentence constructs
	 */
	private ArrayList populateStruct(List links, String sentence) {
		ArrayList simpleSentenceList = null;

		int[] subjectLinks = new int[30];
		int subjectCounter = 0;

		for (int i = 0; i < links.size(); i++) {
			Link currentLink = (Link) links.get(i);
			String linkName = "";
			if (currentLink.getType() != null)
				linkName = currentLink.getType().trim();
			if (linkName.equals("")) continue;
			if (linkName.charAt(0) == 'S' || linkName.charAt(0) == 'B') {
				subjectLinks[subjectCounter] = i;
				subjectCounter++;
			}
		}

		if (subjectCounter == 0) {
			System.err.println("No subjects in sentence");
			return null;
		}
		simpleSentenceList = new ArrayList();
		for (int s = 0; s < subjectCounter; s++) {
			SimpleSentence simpleSentence = new SimpleSentence();

			Link subjectLink = (Link) links.get(subjectLinks[s]);

			int verbStart = subjectLink.getRightIndex();
			int subjectStart = subjectLink.getLeftIndex();
			simpleSentence.setSubjectStart(subjectStart);
			simpleSentence.setVerbStart(verbStart);

			findVerb(links, simpleSentence, sentence);

			findSubject(subjectLink, links, simpleSentence, sentence);

			findObjects(links, simpleSentence, sentence);

			findModifyingPhrases(links, simpleSentence, sentence);

			simpleSentenceList.add(simpleSentence);
		}

		return simpleSentenceList;
	}

	/**
	 * 
	 * @param links
	 *            the list of linkage representations between the words
	 * @param simpleSentence
	 *            the SimpleSentence structure for the current clause
	 * @param sentence
	 *            the sentence being processed
	 */
	public void findVerb(List links, SimpleSentence simpleSentence,
			String sentence) {
		int verbEnd = simpleSentence.getVerbStart();

		for (int i = 0; i < links.size(); i++) {
			Link currentLink = (Link) links.get(i);
			String linkName = "";
			if (currentLink.getType() != null)
				linkName = currentLink.getType().trim();
			if (linkName.equals("")) continue;
			int leftIndex = currentLink.getLeftIndex();
			if ((linkName.startsWith("I") || linkName.startsWith("P")
					|| linkName.startsWith("TO") || linkName.startsWith("N"))
					&& leftIndex == simpleSentence.getVerbStart()) {
				if (currentLink.getRightIndex() > verbEnd)
					verbEnd = currentLink.getRightIndex();

			} else if ((linkName.startsWith("P") || linkName.startsWith("I")
					|| linkName.startsWith("TO") || linkName.startsWith("J"))
					&& currentLink.getLeftIndex() == verbEnd) {
				if (currentLink.getRightIndex() > verbEnd)
					verbEnd = currentLink.getRightIndex();
			}
			if (linkName.startsWith("E")
					&& currentLink.getRightIndex() == verbEnd) {
				if (currentLink.getLeftIndex() < simpleSentence.getVerbStart())
					simpleSentence.setVerbStart(currentLink.getLeftIndex());
			}
			if (linkName.startsWith("E")
					&& currentLink.getRightIndex() == simpleSentence
							.getVerbStart()) {
				if (verbEnd < currentLink.getRightIndex())
					verbEnd = currentLink.getRightIndex();
			}

			if ((linkName.startsWith("M") || linkName.startsWith("J"))
					&& currentLink.getLeftIndex() < verbEnd
					&& currentLink.getLeftIndex() > simpleSentence
							.getVerbStart()) {
				if (currentLink.getRightIndex() > verbEnd)
					verbEnd = currentLink.getRightIndex();
			}

		}
		for (int i = 0; i < links.size(); i++) {
			Link currentLink = (Link) links.get(i);
			String linkName = "";
			if (currentLink.getType() != null)
				linkName = currentLink.getType().trim();
			if (linkName.equals("")) continue;

			if ((linkName.startsWith("P") || linkName.startsWith("J"))
					&& currentLink.getLeftIndex() == verbEnd) {
				if (currentLink.getRightIndex() > verbEnd)
					verbEnd = currentLink.getRightIndex();
			}
			if ((linkName.startsWith("E") || linkName.startsWith("D"))
					&& currentLink.getRightIndex() == verbEnd) {
				if (currentLink.getLeftIndex() < simpleSentence.getVerbStart())
					simpleSentence.setVerbStart(currentLink.getLeftIndex());
			}
		}
		simpleSentence.setVerbEnd(verbEnd);
		String verb = "";
		if (simpleSentence.getVerbStart() != simpleSentence.getVerbEnd()) {
			String start = getWord(sentence, simpleSentence.getVerbStart());
			String end = getWord(sentence, simpleSentence.getVerbEnd());
			verb = sentence.substring(simpleSentence.getVerbStart(),
					simpleSentence.getVerbEnd());
			verb = insertAtFirst(verb, start);
			verb = verb.substring(0, verb.lastIndexOf(" ") + 1) + end;
		} else
			verb = getWord(sentence, simpleSentence.getVerbStart());

		verb = removeSuffix(verb);
		simpleSentence.setVerb(verb);
	}

	/**
	 * 
	 * @param subjectLink
	 *            the current subject link being considered
	 * @param links
	 *            the list of linkage representations between the words
	 * @param simpleSentence
	 *            the structure for the current clause
	 * @param sentence
	 *            the sentence being processed
	 */
	private void findSubject(Link subjectLink, List links,
			SimpleSentence simpleSentence, String sentence) {
		int subjectStart = simpleSentence.getSubjectStart();
		int subjectEnd = simpleSentence.getSubjectStart();
		if (getWord(sentence, subjectStart).equalsIgnoreCase("which")) {
			for (int i = 0; i < links.size(); i++) {
				Link currentLink = (Link) links.get(i);
				String linkName = currentLink.getType().trim();
				if (currentLink.getRightIndex() == subjectStart
						&& linkName.startsWith("MX")) {
					subjectStart = currentLink.getLeftIndex();
					subjectLink = currentLink;
					break;
				}
			}
		}
		subjectEnd = subjectStart;
		for (int i = 0; i < links.size(); i++) {
			Link currentLink = (Link) links.get(i);
			String linkName = "";
			if (currentLink.getType() != null)
				linkName = currentLink.getType().trim();
			if (linkName.equals("")) continue;
			if ((linkName.startsWith("M") || linkName.startsWith("J") || linkName
					.startsWith("OF"))
					&& currentLink.getLeftIndex() == subjectStart) {
				if (!getWord(sentence, currentLink.getRightIndex())
						.equalsIgnoreCase("which")
						&& currentLink.getRightIndex() > subjectEnd)
					subjectEnd = currentLink.getRightIndex();
			}
			if ((linkName.startsWith("M") || linkName.startsWith("J"))
					&& currentLink.getLeftIndex() == subjectEnd) {
				if (!getWord(sentence, currentLink.getRightIndex())
						.equalsIgnoreCase("which")
						&& currentLink.getRightIndex() > subjectEnd)
					subjectEnd = currentLink.getRightIndex();
			}

			if ((linkName.startsWith("J") || linkName.startsWith("D")
					|| linkName.startsWith("A") || linkName.startsWith("G"))
					&& currentLink.getRightIndex() == subjectStart) {
				if (currentLink.getLeftIndex() < subjectStart)
					subjectStart = currentLink.getLeftIndex();
			}
			if ((linkName.startsWith("D") || linkName.startsWith("E"))
					&& currentLink.getRightIndex() == subjectEnd) {
				if (currentLink.getLeftIndex() < subjectStart)
					subjectStart = currentLink.getLeftIndex();
			}
			if (linkName.startsWith("M")
					&& currentLink.getLeftIndex() > subjectStart
					&& currentLink.getLeftIndex() < subjectEnd) {
				if (!getWord(sentence, currentLink.getRightIndex())
						.equalsIgnoreCase("which")
						&& currentLink.getRightIndex() > subjectEnd) {
					subjectEnd = currentLink.getRightIndex();
				}
			}
		}
		for (int i = links.size() - 1; i >= 0; i--) {
			Link currentLink = (Link) links.get(i);
			String linkName = "";
			if (currentLink.getType() != null)
				linkName = currentLink.getType().trim();
			if (linkName.equals("")) continue;
			if ((linkName.startsWith("J") || linkName.startsWith("A")
					|| linkName.startsWith("G") || linkName.startsWith("M"))
					&& (currentLink.getLeftIndex() == subjectEnd || ((currentLink
							.getLeftIndex() < subjectEnd && currentLink
							.getLeftIndex() > subjectStart)))) {
				if (currentLink.getRightIndex() > subjectEnd)
					subjectEnd = currentLink.getRightIndex();

			}
			if (linkName.startsWith("E")
					&& currentLink.getRightIndex() == subjectStart) {
				if (currentLink.getLeftIndex() < subjectStart)
					subjectStart = currentLink.getLeftIndex();
			}
		}
		for (int i = 0; i < links.size(); i++) {
			Link currentLink = (Link) links.get(i);
			String linkName = "";
			if (currentLink.getType() != null)
				linkName = currentLink.getType().trim();
			if (linkName.equals("")) continue;
			if ((linkName.startsWith("J") || linkName.startsWith("A")
					|| linkName.startsWith("G") || linkName.startsWith("M"))
					&& currentLink.getLeftIndex() == subjectEnd) {
				if (currentLink.getRightIndex() > subjectEnd)
					subjectEnd = currentLink.getRightIndex();

			}
		}
		String subject = "";
		simpleSentence.setSubjectStart(subjectStart);
		simpleSentence.setSubjectEnd(subjectEnd);
		if (subjectStart == subjectEnd)
			subject = getWord(sentence, subjectStart);
		else {
			String start = getWord(sentence, subjectStart);
			String end = getWord(sentence, subjectEnd);
			subject = sentence.substring(subjectStart, subjectEnd);
			subject = insertAtFirst(subject, start);
			subject = subject.substring(0, subject.lastIndexOf(" ") + 1) + end;
		}

		if (subject != null) {
			subject = removeSuffix(subject);
			String subjectLinkName = subjectLink.getType().trim();
			if (subjectLinkName.startsWith("B")
					|| subjectLinkName.startsWith("MX"))
				subject = removePrepositionsAtBegin(subject);
			subject = subject.replaceFirst(", which", "");
			simpleSentence.setSubject(subject);
		}
	}

	/**
	 * Traverses through the links to obtain the objects associated with the
	 * verb in the clause being considered
	 * 
	 * @param links
	 *            the list of linkage representations between the words
	 * @param simpleSentence
	 *            the structure for the current clause
	 * @param sentence
	 *            the sentence being processed
	 */
	private void findObjects(List links, SimpleSentence simpleSentence,
			String sentence) {
		ArrayList objects = null;
		int[] objectLinks = new int[30];
		int indexCounter = 0;
		for (int i = 0; i < links.size(); i++) {
			Link currentLink = (Link) links.get(i);
			String linkName = "";
			if (currentLink.getType() != null)
				linkName = currentLink.getType().trim();
			if (linkName.equals("")) continue;

			if (linkName.startsWith("O")) {
				objectLinks[indexCounter] = i;
				indexCounter++;
			}
		}
		if (indexCounter == 0) {
			return;
		}

		for (int oVar = 0; oVar < indexCounter; oVar++) {
			Link objectLink = (Link) links.get(objectLinks[oVar]);

			if (!(simpleSentence.getVerbEnd() == objectLink.getLeftIndex() || simpleSentence
					.getVerbStart() == objectLink.getLeftIndex())) {
				continue;
			}
			int objectStart = objectLink.getRightIndex();
			int objectEnd = objectStart;
			for (int i = 0; i < links.size(); i++) {
				Link currentLink = (Link) links.get(i);
				String linkName = "";
				if (currentLink.getType() != null)
					linkName = currentLink.getType().trim();
				if (linkName.equals("")) continue;

				if ((linkName.startsWith("M") || linkName.startsWith("J")
						|| linkName.startsWith("OF") || linkName
						.startsWith("O"))
						&& currentLink.getLeftIndex() == objectEnd) {
					if (currentLink.getRightIndex() > objectEnd)
						objectEnd = currentLink.getRightIndex();
				} else if (linkName.startsWith("J")
						&& currentLink.getLeftIndex() == objectStart) {
					if (currentLink.getRightIndex() > objectEnd)
						objectEnd = currentLink.getRightIndex();
				}
				if ((linkName.startsWith("J") || linkName.startsWith("D")
						|| linkName.startsWith("A") || linkName.startsWith("G"))
						&& currentLink.getRightIndex() == objectStart) {
					if (currentLink.getLeftIndex() < objectStart)
						objectStart = currentLink.getLeftIndex();
				}
				if (linkName.startsWith("E")
						&& currentLink.getRightIndex() == objectEnd) {
					if (currentLink.getLeftIndex() < objectStart)
						objectStart = currentLink.getLeftIndex();
				}
				if (linkName.startsWith("M")
						&& currentLink.getLeftIndex() < objectEnd
						&& currentLink.getLeftIndex() >= objectStart) {
					if (currentLink.getRightIndex() > objectEnd)
						objectEnd = currentLink.getRightIndex();
				}
			}
			for (int i = links.size() - 1; i >= 0; i--) {
				Link currentLink = (Link) links.get(i);
				String linkName = "";
				if (currentLink.getType() != null)
					linkName = currentLink.getType().trim();
				if (linkName.equals("")) continue;
				if ((linkName.startsWith("J") || linkName.startsWith("D")
						|| linkName.startsWith("A") || linkName.startsWith("G") || linkName
						.startsWith("M"))
						&& currentLink.getLeftIndex() == objectEnd) {
					if (currentLink.getRightIndex() > objectEnd)
						objectEnd = currentLink.getRightIndex();
				}
				if (linkName.startsWith("M")
						&& currentLink.getLeftIndex() == objectStart) {
					if (currentLink.getRightIndex() > objectEnd)
						objectEnd = currentLink.getRightIndex();
				}
			}

			for (int i = 0; i < links.size(); i++) {
				Link currentLink = (Link) links.get(i);
				String linkName = "";
				if (currentLink.getType() != null)
					linkName = currentLink.getType().trim();
				if (linkName.equals("")) continue;
				if ((linkName.startsWith("J") || linkName.startsWith("D")
						|| linkName.startsWith("A") || linkName.startsWith("G") || linkName
						.startsWith("M"))
						&& currentLink.getLeftIndex() == objectEnd) {
					if (currentLink.getRightIndex() > objectEnd)
						objectEnd = currentLink.getRightIndex();
				}
				if ((linkName.startsWith("M") || linkName.startsWith("J"))
						&& currentLink.getLeftIndex() < objectEnd
						&& currentLink.getLeftIndex() >= objectStart) {
					if (currentLink.getRightIndex() > objectEnd)
						objectEnd = currentLink.getRightIndex();
				}
			}

			String object = "";
			if (objectStart == objectEnd)
				object = getWord(sentence, objectStart);
			else {
				String start = getWord(sentence, objectStart);
				String end = getWord(sentence, objectEnd);
				object = sentence.substring(objectStart, objectEnd);
				object = insertAtFirst(object, start);
				object = object.substring(0, object.lastIndexOf(" ") + 1) + end;
			}
			if (object != null) {
				object = removeSuffix(object);
				if (objects == null) objects = new ArrayList();
				objects.add(object);
			}
		}
		simpleSentence.setObjects(objects);
	}

	/**
	 * Traverses through the links to find the modifying phrases associated with
	 * the verb in the current clause
	 * 
	 * @param links
	 *            the list of linkage representations between the words
	 * @param simpleSentence
	 *            the structure for current clause
	 * @param sentence
	 *            the sentence being processed
	 */
	private void findModifyingPhrases(List links,
			SimpleSentence simpleSentence, String sentence) {
		ArrayList modPhrases = null;
		ArrayList coPhrases = null;
		int[] modLinks = new int[30];
		int modCounter = 0;
		boolean coLink = false;
		for (int i = 0; i < links.size(); i++) {
			Link currentLink = (Link) links.get(i);
			String linkName = "";
			if (currentLink.getType() != null)
				linkName = currentLink.getType().trim();
			if (linkName.equals("")) continue;
			if (linkName.startsWith("MV")
					&& (currentLink.getLeftIndex() == simpleSentence
							.getVerbStart() || currentLink.getLeftIndex() == simpleSentence
							.getVerbEnd())) {
				modLinks[modCounter] = i;
				modCounter++;
			} else if (linkName.startsWith("CO")
					&& (currentLink.getRightIndex() == simpleSentence
							.getSubjectStart() || currentLink.getRightIndex() == simpleSentence
							.getSubjectEnd())) {
				modLinks[modCounter] = i;
				modCounter++;
			}
		}
		if (modCounter == 0) {
			return;
		}
		for (int m = 0; m < modCounter; m++) {
			Link modLink = (Link) links.get(modLinks[m]);
			int modStart = -1, modEnd = -1;
			if (modLink.getType().startsWith("MV")) {
				coLink = false;
				modStart = modLink.getRightIndex();
			} else if (modLink.getType().startsWith("CO")) {
				coLink = true;
				modStart = modLink.getLeftIndex();
			}
			modEnd = modStart;
			for (int i = 0; i < links.size(); i++) {
				Link currentLink = (Link) links.get(i);
				String linkName = "";
				if (currentLink.getType() != null)
					linkName = currentLink.getType().trim();
				if (linkName.equals("")) continue;

				if ((linkName.startsWith("I") || linkName.startsWith("M")
						|| linkName.startsWith("J") || linkName.startsWith("P") || linkName
						.startsWith("TO"))
						&& currentLink.getLeftIndex() == modEnd) {
					if (modEnd < currentLink.getRightIndex())
						modEnd = currentLink.getRightIndex();
				}
				if (linkName.startsWith("ID")
						&& currentLink.getRightIndex() == modStart) {
					if (currentLink.getLeftIndex() < modStart)
						modStart = currentLink.getLeftIndex();
				}

				if ((linkName.startsWith("J") || linkName.startsWith("ID") || linkName
						.startsWith("M"))
						&& currentLink.getLeftIndex() < modEnd
						&& currentLink.getLeftIndex() >= modStart) {
					if (currentLink.getRightIndex() > modEnd)
						modEnd = currentLink.getRightIndex();
				}

				if ((linkName.startsWith("A") || linkName.startsWith("D")
						|| linkName.startsWith("G") || linkName.startsWith("E"))
						&& currentLink.getRightIndex() == modEnd) {
					if (currentLink.getLeftIndex() < modStart)
						modStart = currentLink.getLeftIndex();
				}
				if (linkName.startsWith("J")
						&& currentLink.getLeftIndex() == modStart) {
					if (modEnd < currentLink.getRightIndex())
						modEnd = currentLink.getRightIndex();
				}
			}
			for (int i = 0; i < links.size(); i++) {
				Link currentLink = (Link) links.get(i);
				String linkName = "";
				if (currentLink.getType() != null)
					linkName = currentLink.getType().trim();
				if (linkName.equals("")) continue;
				if (linkName.startsWith("ID")
						&& currentLink.getRightIndex() == modStart) {
					if (currentLink.getLeftIndex() < modStart)
						modStart = currentLink.getLeftIndex();
				}
				if ((linkName.startsWith("M") || linkName.startsWith("J"))
						&& currentLink.getLeftIndex() == modEnd) {
					if (currentLink.getRightIndex() > modEnd) {
						String word = "";
						word = getWord(sentence, currentLink.getRightIndex());
						if (word != null && !word.equalsIgnoreCase("which"))
							modEnd = currentLink.getRightIndex();
					}
				}
				if (linkName.startsWith("A")
						&& currentLink.getRightIndex() == modEnd) {
					if (currentLink.getLeftIndex() < modStart)
						modStart = currentLink.getLeftIndex();
				}
				if (linkName.startsWith("O")
						&& currentLink.getLeftIndex() == modEnd
						&& currentLink.getLeftIndex() != simpleSentence
								.getVerbEnd()) {
					if (currentLink.getRightIndex() > modEnd)
						modEnd = currentLink.getRightIndex();
				}
				if ((linkName.startsWith("O") || linkName.startsWith("I")
						|| linkName.startsWith("M") || linkName.startsWith("J"))
						&& currentLink.getLeftIndex() < modEnd
						&& currentLink.getLeftIndex() >= modStart
						&& currentLink.getLeftIndex() != simpleSentence
								.getVerbEnd()) {
					if (currentLink.getRightIndex() > modEnd)
						modEnd = currentLink.getRightIndex();
				}
			}
			for (int i = 0; i < links.size(); i++) {
				Link currentLink = (Link) links.get(i);
				String linkName = "";
				if (currentLink.getType() != null)
					linkName = currentLink.getType().trim();
				if (linkName.equals("")) continue;
				if (linkName.startsWith("J")
						&& currentLink.getLeftIndex() == modEnd) {
					if (currentLink.getRightIndex() > modEnd)
						modEnd = currentLink.getRightIndex();
				}
				if (linkName.startsWith("J")
						&& currentLink.getLeftIndex() < modEnd
						&& currentLink.getLeftIndex() >= modStart) {
					if (currentLink.getRightIndex() > modEnd)
						modEnd = currentLink.getRightIndex();
				}
			}
			String modP = "";
			if (modStart == modEnd)
				modP = getWord(sentence, modStart);
			else {
				String start = getWord(sentence, modStart);
				String end = getWord(sentence, modEnd);
				modP = sentence.substring(modStart, modEnd + 1);
				modP = insertAtFirst(modP, start);
				if (end != null && !end.equals("."))
					modP = modP.substring(0, modP.lastIndexOf(" ") + 1) + end;
			}

			modP = removeSuffix(modP);
			if (!coLink) {
				if (modPhrases == null) modPhrases = new ArrayList();
				modPhrases.add(modP);
				simpleSentence.addModStartIndex(modStart);
			} else {
				if (coPhrases == null) coPhrases = new ArrayList();
				coPhrases.add(modP);
				simpleSentence.addcoPhraseStartIndex(modStart);
			}
		}
		ArrayList sortedList = new ArrayList();
		if (simpleSentence.getModStartIndices() != null) {
			if (simpleSentence.getModStartIndices().size() > 1) {
				sortedList.addAll(simpleSentence.getModStartIndices());
				Collections.sort(sortedList);
				Iterator s = sortedList.iterator();
				while (s.hasNext()) {
					simpleSentence.addModifyingPhrase(modPhrases.get(
							simpleSentence.getModStartIndices().indexOf(
									s.next())).toString());
				}
			} else if (simpleSentence.getModStartIndices().size() == 1) {
				simpleSentence.setModifyingPhrases(modPhrases);
			}
		}
		if (simpleSentence.getCoPhraseStartIndices() != null) {
			if (simpleSentence.getCoPhraseStartIndices().size() > 1) {
				sortedList.clear();
				sortedList.addAll(simpleSentence.getCoPhraseStartIndices());
				Collections.sort(sortedList);
				Iterator s = sortedList.iterator();
				while (s.hasNext()) {
					simpleSentence.addcoPhrase(coPhrases.get(
							simpleSentence.getCoPhraseStartIndices().indexOf(
									s.next())).toString());
				}
			} else if (simpleSentence.getCoPhraseStartIndices().size() == 1) {
				simpleSentence.setCoPhrases(coPhrases);
			}
			Iterator i = simpleSentence.getCoPhrases().iterator();
			StringBuffer coBuf = new StringBuffer();
			while (i.hasNext()) {
				String currentPhrase = i.next().toString();
				coBuf.append(currentPhrase + ", ");
			}
			String subj = coBuf.toString() + simpleSentence.getSubject();
			simpleSentence.setSubject(subj);
		}
	}

	/**
	 * 
	 * @param whole
	 *            the string to which the word has to be added.
	 * @param first
	 *            the string to be added
	 * @return The concatenated strings
	 */
	private String insertAtFirst(String whole, String first) {
		StringTokenizer st = new StringTokenizer(whole);
		StringBuffer buf = new StringBuffer();
		String one = st.nextToken();
		buf.append(first);
		while (st.hasMoreTokens()) {
			buf.append(" ");
			buf.append(st.nextToken());
		}

		return buf.toString();

	}

	/**
	 * 
	 * @param original
	 *            The original string
	 * @return the string with prepositions at the beginning removed
	 */
	private String removePrepositionsAtBegin(String original) {
		String result = "";
		String[] prepositions = {"into", "from", "with", "on", "at", "in",
				"of", "to", "by"};

		for (int i = 0; i < prepositions.length; i++)
			if (original.startsWith(prepositions[i])) {
				int start = original.indexOf(prepositions[i])
						+ prepositions[i].length() + 1;
				result = original.substring(start, original.length());
				return result;
			}

		return original;
	}
}