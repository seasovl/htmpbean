package com.jcm.htmpbean;



import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class XpathDemo {
	 // 通过url，将相应的html解析为DOM文档
    public static Document getDocument(String url) {
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
 
    // 通过XPath定位具体的节点
    public static NodeList getExactNode(Document doc, String xp) {
        NodeList list = null;
        try {
        	list=doc.getElementsByTagName(xp);
            list = XPathAPI.selectNodeList(doc, xp);
        	
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return list;
    }
 
    public static void main(String[] args) {
        
        String art = "http://car.autohome.com.cn/photo/series-11619-2120-1-1746243.html";
        String artPath = "/HTML/BODY/DIV/DIV/UL/LI/A.RED";
        String art1 = "http://dealer.autohome.com.cn/3966/contact.html";
        String art1Path = "/HTML/BODY/SCRIPT";
        // 将指定的页面解析为DOM文档
        Document doc = getDocument(art1);
        // 根据xpath获得指定的节点
        System.out.println(doc.getFirstChild().getNodeName());
        NodeList list = getExactNode(doc, art1Path);
        System.out.println("符合条件的结点个数为：" + list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            System.out.println("获取的节点属性为："
                    + list.item(i).getTextContent());
        }
    }
}
