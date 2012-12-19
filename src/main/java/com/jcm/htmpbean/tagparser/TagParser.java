package com.jcm.htmpbean.tagparser;

import java.io.IOException;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jcm.htmpbean.bean.EleBlock;

public class TagParser {
	public static EleBlock parser(EleBlock eleBlock,InputSource stream) throws SAXException, IOException
	{
		DOMParser domParser=new DOMParser();
		domParser.parse(stream);
		Document document=domParser.getDocument();
		return eleBlock;
	}
	//填充数据
	
}
