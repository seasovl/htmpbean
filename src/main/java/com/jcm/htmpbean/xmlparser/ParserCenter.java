package com.jcm.htmpbean.xmlparser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.dom.AttributeMap;
import org.cyberneko.html.parsers.DOMParser;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jcm.htmpbean.bean.EleBlock;
import com.jcm.htmpbean.bean.EleData;
import com.jcm.htmpbean.htmlparser.HtmlParser;
import com.jcm.htmpbean.tagparser.TagParser;

public class ParserCenter {
	private  static ParserCenter center;
	private  static HtmlParser htmlParser=new HtmlParser();
	private static Map<String,EleBlock> mapEle=new HashMap<String, EleBlock>();
	private static Map<String,Pattern> regexp=new HashMap<String, Pattern>();
	private static String PATH=ParserCenter.class.getResource("/").getPath();
	static {
		center=new ParserCenter();
	}
	private ParserCenter(){}
	public static  ParserCenter getInstance()
	{
		return center;
	}
	private  void loadXMLInfo(Node node)
	{
		NodeList nodeList= node.getChildNodes();
		for(int i=0;nodeList!=null&&i!=nodeList.getLength();++i)
		{
			Node nodelis=nodeList.item(i);
			if(nodelis.getNodeType()==Node.TEXT_NODE){
				continue;
			}else if(nodelis.getNodeName().trim().equalsIgnoreCase("bean")){
				AttributeMap attributeMap=(AttributeMap) nodelis.getAttributes();
				Node id=attributeMap.getNamedItem("id");
				Node regxurl=attributeMap.getNamedItem("regx-url");
				Node htmlbean=attributeMap.getNamedItem("htmlbean");
				regexp.put(id.getNodeValue(),Pattern.compile(regxurl.getNodeValue()));
				
				EleBlock eleBlock= TagParser.parser(new InputSource(PATH+"/"+htmlbean.getNodeValue()));
				mapEle.put(id.getNodeValue(), eleBlock);
				
			}else if(nodelis.getNodeName().trim().equalsIgnoreCase("include")){
				AttributeMap attributeMap=(AttributeMap) nodelis.getAttributes();
				Node file=attributeMap.getNamedItem("file");
				load(new InputSource(PATH+"/"+file.getNodeValue()));
			}
		}
	}
	public  String findRegexpID(String url)
	{
		if(regexp!=null)
		{
			Set<Entry<String, Pattern>> setentry=regexp.entrySet();
			Iterator<Entry<String, Pattern>> ientry= setentry.iterator();
			while(ientry.hasNext())
			{
				Entry<String, Pattern> entry=ientry.next();
				Pattern pattern=entry.getValue();
				 Matcher matcher= pattern.matcher(url);
				 if(matcher.find())
				 {
					 return entry.getKey();
				 }
			}
		}
		return null;
	}
	public synchronized void load(InputSource stream)
	{	
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = builderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(stream);
			loadXMLInfo(document.getFirstChild());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<JSONObject> parserJSON(InputSource inputSource,String url) {
		List<Map<String, String>> mapList = parserMap(inputSource,url);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (Map<String, String> map : mapList) {
			JSONObject jsonObject = new JSONObject(map);
			jsonList.add(jsonObject);
		}
		return jsonList;
	}
	
	public List<JSONObject> parserJSON(String url) {
		List<Map<String, String>> mapList = parserMap(url);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (Map<String, String> map : mapList) {
			JSONObject jsonObject = new JSONObject(map);
			jsonList.add(jsonObject);
		}
		return jsonList;
	}
	
	public EleData parserEleData(InputSource inputSource,String url)
	{
		 Document document= getDocument( inputSource );
		 String id=findRegexpID(url);
		 if(id!=null)
		 {
			 System.out.println(id);
			 return htmlParser.parserHtml(mapEle.get(id), document);
		 }
		return null;
	}
	public EleData parserEleData(String url)
	{
		 Document document= getDocument( url );
		 String id=findRegexpID(url);
		 if(id!=null)
		 {
			 System.out.println(id);
			 return htmlParser.parserHtml(mapEle.get(id), document);
		 }
		return null;
	}
	
	public List<Map<String,String>> parserMap(String url)
	{ 
		EleData eleData=this.parserEleData(url);
		List<Map<String,String>> maplist=new ArrayList<Map<String,String>>();
		this.conventMap(maplist, eleData);
		return maplist;
	}
	public List<Map<String,String>> parserMap(InputSource inputSource,String url)
	{
		EleData eleData=this.parserEleData(inputSource, url);
		List<Map<String,String>> maplist=new ArrayList<Map<String,String>>();
		this.conventMap(maplist, eleData);
		return maplist;
	}
	private void conventMap(List<Map<String,String>> maplist,EleData eleData)
	{
		List<EleData> list= eleData.getSubData();
		if(list!=null && list.size()>0){
			for(int i=0; i!=list.size();++i){
				conventMap(maplist,list.get(i));
			}
			for(int j=0; j!=maplist.size();++j){
				maplist.get(j).putAll(eleData.getBlocks().get(0));
			}
		}else{
			maplist.addAll(eleData.getBlocks());
		}

		
	}
	 // 通过url，将相应的html解析为DOM文档
    public  Document getDocument(InputSource inputSource) {
        DOMParser parser = new DOMParser();
        try {
        	parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(inputSource);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = parser.getDocument();
        return doc;
    }
    public  Document getDocument(String url) {
        DOMParser parser = new DOMParser();
        try {
        	parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(url);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = parser.getDocument();
        return doc;
    }

	public static void main(String[] args) throws Exception {
		
		long c=System.currentTimeMillis();
		center.load(new InputSource( ParserCenter.class.getResourceAsStream("/htmlconf.xml")));
	
		List<JSONObject> eleData =center.parserJSON(new InputSource(new StringReader("<div class='t1203_fbox'><span class='f_eclip' id='w7'><a target='_blank' href='/7/'>奥迪A8L</a></span>		</div>")), "carAutohome");
		System.out.println(System.currentTimeMillis()-c);
		for(JSONObject jsonObject:eleData)
		{
			System.out.println(jsonObject);
		}
	}
}
