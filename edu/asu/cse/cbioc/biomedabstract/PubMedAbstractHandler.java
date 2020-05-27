/*
 * Created on May 10, 2005
 *
 * History
 * 
 */
package edu.asu.cse.cbioc.biomedabstract;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author bllogsdon
 *
 * The BioMedAbstractHandler marks which tags to save. From the
 * PubMed XML file.
 * 
 */
public class PubMedAbstractHandler extends DefaultHandler {
	private boolean inArticleTitle;
	private boolean inAbstractText;
	private boolean inAuthorList;
	private StringBuffer articleTitle;
	private StringBuffer abstractText;
	private StringBuffer authorList;
	
	/**
	 * Default Constructor
	 */
	public PubMedAbstractHandler(){
		super();
		
		inArticleTitle = false;
		inAbstractText = false;
		inAuthorList = false;
		
		articleTitle = new StringBuffer();
		abstractText = new StringBuffer();
		authorList = new StringBuffer();
	}
	
	/**
	 * This method is called when an opening tag is found.
	 * The inArticleTitle is set to true when the <ArticleTitle> tag is parsed.
	 * The inAbstractText is set to true when the <AbstractText> tag is parsed.
	 * the inAuthorList is set to true when the <AuthorList> tag is parsed.
	 */
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes atts) throws SAXException {
		if (localName.equals("ArticleTitle")){
			inArticleTitle = true;
		}
		if(localName.equals("AbstractText")){
			inAbstractText = true;
		}
		if(localName.equals("AuthorList")){
			inAuthorList = true;
		}
		
	}

	/**
	 * This method is called when a closing tag is found.
	 * The inArticleTitle is set to false when the </ArticleTitle> tag is parsed.
	 * The inAbstractText is set to false when the </AbstractText> tag is parsed.
	 * the inAuthorList is set to false when the </AuthorList> tag is parsed.
	 */
	public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
	    if (localName.equals("ArticleTitle")){
	    	inArticleTitle = false;
	    }
		if(localName.equals("AbstractText")){
			inAbstractText = false;
		}
		if(localName.equals("AuthorList")){
			inAuthorList = false;
		}	    
	}

	/**
	 * This method writes the information between ArticleTitle, AbstractText, and AuthorList
	 * tags to strings.
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
	    if (inArticleTitle) {
	    	for (int i = start; i < start+length; i++) {
	    		 articleTitle.append(ch[i]);
	    	}
	    }
	    if (inAbstractText) {
	    	for (int i = start; i < start+length; i++) {
	    		 abstractText.append(ch[i]);
	    	}
	    }
	    if (inAuthorList) {
	    	for (int i = start; i < start+length; i++) {
	    		 authorList.append(ch[i]);
	    	}
	    }
	}
	
	/**
	 * @return the parsed abstractText
	 */
	public String getAbstractText(){
		return abstractText.toString();
	}
	
	/**
	 * @return the parsed articleTitle
	 */
	public String getArticleTitle(){
		return articleTitle.toString();
	}
	
	/**
	 * @return the parsed authorList
	 */
	public String getAuthorList(){
		return authorList.toString();
	}
}
