package com.jcm.htmpbean.xmlparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.ibatis.session.SqlSession;
import org.apache.xerces.dom.AttributeMap;
import org.cyberneko.html.parsers.DOMParser;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jcm.htmpbean.bean.EleBlock;
import com.jcm.htmpbean.dao.ConfPersistence;
import com.jcm.htmpbean.htmlparser.HtmlParser;
import com.jcm.htmpbean.tagparser.TagParser;

public class ParserCenter {
	private  static ParserCenter center;
	private  static HtmlParser htmlParser=new HtmlParser();
	private static Map<String,EleBlock> mapEle=new HashMap<String, EleBlock>();
	static {
		center=new ParserCenter();
	}
	private ParserCenter(){}
	public ParserCenter getInstance()
	{
		return center;
	}
	private void loadXMLInfo(Node node)
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
				Map<String,String> map=new HashMap<String,String>();
				map.put("id", id.getNodeValue());
				map.put("regxurl", regxurl.getNodeValue());
				map.put("htmlbean", htmlbean.getNodeValue());
				ConfPersistence confPersistence=new ConfPersistence();
				SqlSession session=confPersistence.openSession();
				int ins=session.insert("insertRegexpUrl", map);
				session.commit();
				session.close();
				EleBlock eleBlock= TagParser.parser(new InputSource(ParserCenter.class.getResourceAsStream("/"+map.get("htmlbean"))));
				mapEle.put(map.get("id"), eleBlock);
			}else if(nodelis.getNodeName().trim().equalsIgnoreCase("include")){
				AttributeMap attributeMap=(AttributeMap) nodelis.getAttributes();
				Node file=attributeMap.getNamedItem("file");
				load(new InputSource( ParserCenter.class.getResourceAsStream("/"+file.getNodeValue())));
			}
		}
	}
	public void load(InputSource stream)
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
	public List<Map<String,String>> parserMap(InputSource inputSource,String url)
	{
		 Document document= getDocument( inputSource );
		 ConfPersistence confPersistence=new ConfPersistence();
		 SqlSession session= confPersistence.openSession();
		 List<Map<String,String>> list=session.selectList("selectRegexpUrl",url);
		 if(list.size()>0)
		 {
			 Map<String,String> map= list.get(0);
			 System.out.println(map.get("ID"));
			 return htmlParser.parserHtml(mapEle.get(map.get("ID")), document);
		 }
		 session.close();
		return null;
	}
	public List<Map<String,String>> parserMap(String url)
	{
		 Document document= getDocument( url );
		 ConfPersistence confPersistence=new ConfPersistence();
		 SqlSession session= confPersistence.openSession();
		 List<Map<String,String>> list=session.selectList("selectRegexpUrl",url);
		 if(list.size()>0)
		 {
			 Map<String,String> map= list.get(0);
			 System.out.println(map.get("ID"));
			 return htmlParser.parserHtml(mapEle.get(map.get("ID")), document);
		 }
		 session.close();
		return null;
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
		ParserCenter center=new ParserCenter();
		long c=System.currentTimeMillis();
		center.load(new InputSource( ParserCenter.class.getResourceAsStream("/htmlconf.xml")));
		System.out.println(System.currentTimeMillis()-c);
		List<JSONObject> list=center.parserJSON("http://dealer.autohome.com.cn/1906/contact.html");
		for(JSONObject jsonObject:list)
		{
			System.out.println(jsonObject.toString());
		}
	}
}
