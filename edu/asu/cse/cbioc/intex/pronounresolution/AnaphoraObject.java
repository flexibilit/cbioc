package edu.asu.cse.cbioc.intex.pronounresolution;
/*
 * Created on May 24, 2005
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
public class AnaphoraObject {
	int FrameNum, aDistance;
	String AnaphoraPOStag, AnaphoraType;
	
	//constructor
	public AnaphoraObject(int FN, String PT, String AN, int aDist)
	{
		FrameNum = FN;
		AnaphoraPOStag  = PT;
		AnaphoraType= AN;	
		aDistance = aDist;
	}	
	
	public int getFrameNum()
	{
		return FrameNum;
	}
	
	public int  setFrameNum(int FN)
	{
		FrameNum = FN;
		return FrameNum;
	}
	
	public String getAnaphoraPOStag()
	{
		return AnaphoraPOStag;
	}

	public String setAnaphoraPOStag(String APT)
	{
		AnaphoraPOStag = APT;
		return AnaphoraPOStag;
	}
	
	public String getAnaphoraType()
	{
		return AnaphoraType;	
	}
	
	public String setAnaphoraType(String AT)
	{
		AnaphoraType = AT;
		return AnaphoraType;
	}
	
	public int getaDistance()
	{
		return aDistance;	
	}
	
	public int setaDistance(int AD)
	{
		aDistance = AD;
		return aDistance;
	}
	
	
}
