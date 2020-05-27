package edu.asu.cse.cbioc.intex.pronounresolution;

/*
 * Created on May 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Kalpesh Shah
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */



public class SemanticChunkObject {
	int FrameNumber, distance, score;
	String POStag;
	String stringName;
	
	//constructor
	public SemanticChunkObject(int FN, String PT, String SN, int S, int s)
	{
		FrameNumber = FN;
		POStag  = PT;
		stringName = SN;
		distance = S;	
		score = s;
	}
	
	//method to get Frame number
	public int getFrameNumber()
	{
		return FrameNumber;
	}
	
	//method to set Frame number
	public int setFrameNumber(int FN)
	{
		FrameNumber = FN;
		return FrameNumber;
	}
	
	//method to get Part of Speech tag POSTag
	public String getPOStag()
	{
		return POStag;
	}
	
	//method to get the Stringname of the semantic chunck object
	public String getstringName()
	{
		return stringName;
	}
	
	//method to get the score of the semantic chunck object
	public int getDistance()
	{
		return distance;
	}
	
	//method to set the score of the semantic chunck object
	public int setDistance(int S)
	{
		distance = S;
		return distance;
	}
	
	//method to get the score of the semantic chunck object
	public int getScore()
	{
		return score;
	}
	
	//method to set the score of the semantic chunck object
	public int setScore(int S)
	{
		score = S;
		return score;
	}
}
