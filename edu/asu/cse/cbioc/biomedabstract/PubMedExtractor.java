/*
 * Updated by Brandon Logsdon
 * Modified on May 05, 2005
 * 
 */
package edu.asu.cse.cbioc.biomedabstract;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/*
 * 
 * @author 
 *
 * BioTextExtractor - This class will extract an Abstract from the Pub Med Database
 * 					however I would like to open it up such that other Abstracts from
 * 					other databases could eventually be extracted.
 * 
 * 					Code has been modulerized such that we will create an extractor for
 * 					each database we want to extract from.  But each abstract regardless of
 * 					database will be saved to an instance of BioMedAbstract.
 * 
 */
public class PubMedExtractor {
	private final static String PUBMED_URL_FRONT = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=";
	private final static String PUBMED_URL_END = "&retmode=xml&rettype=abstract";
	
	/**
	 * Hide Default Constructor
	 */
	private PubMedExtractor() {		
	}
	
	/**
	 * This is a static method for building the pubmed URL with the given PMID parameter
	 * @param PMID	is a pubmed id
	 * @return	the url string for pubmed
	 */
	private static String buildPubmedURL(String PMID){
		return PubMedExtractor.PUBMED_URL_FRONT + PMID.trim() + PubMedExtractor.PUBMED_URL_END;
	}
	
	/**
	 * Get the Abstract from PubMed using the given PMID
	 * 
	 * @parameter PMID is a string representing a Pubmed ID
	 * @return BioMedAbstract is a class defined to handle the retrieved Abstract
	 */
	public static BioMedAbstract getAbstractFromPubmed(String PMID){
		// Abstract is being retrieved in XML so parse it for the data
		return parseXML(PMID);
	}
	
	/**
	 * This method parses the XML data returned from the URL given PMID
	 * the BioMedAbstractHandler is basically the rule for what data we
	 * want.
	 * 
	 * @param PMID is the PubMed Id
	 * @return	an instance of a BioMedAbstract
	 */
	private static BioMedAbstract parseXML(String PMID){
		try{
	    	URL u = new URL(buildPubmedURL(PMID));
	    	URLConnection uc = u.openConnection();
	    	InputStream xml = uc.getInputStream();
	    	InputSource source = new InputSource(xml);
	    	
	    	XMLReader xr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
	    	PubMedAbstractHandler handler = new PubMedAbstractHandler();
	    	
	    	xr.setContentHandler(handler);
	        
	        xr.parse(source);
	        
	        xml.close();
	        
	        return new BioMedAbstract(Integer.parseInt(PMID),handler.getArticleTitle(),handler.getAuthorList(),handler.getAbstractText());
	    	
		}catch (SAXException sxe) {
	       // Error generated during parsing
	       sxe.printStackTrace();
	    } catch (IOException ioe) {
	       // I/O error
	       ioe.printStackTrace();
	    }catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

}