package com.jcm.htmpbean.xmlparser;

public class ParserCenter {
	private  static ParserCenter center;
	static {
		center=new ParserCenter();
	}
	private ParserCenter(){}
	public ParserCenter getInstance()
	{
		return center;
	}
	private ParserCenter loadXMLInfo(String path)
	{
		return null;
	}
}
