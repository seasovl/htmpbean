package com.jcm.htmpbean;



import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.html.dom.HTMLDivElementImpl;
import org.apache.xpath.XPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
    public static NodeList getExactNode(Node doc, String xp) {
        NodeList list = null;
        try {
            list = XPathAPI.selectNodeList(doc, xp);
        	
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return list;
    }
 
    public static void main(String[] args) {
        
        String art = "http://car.autohome.com.cn/photo/series-11619-2120-1-1746243.html";
        
        String art1 = "http://dealer.autohome.com.cn/3966/contact.html";
        String art1Path = "/HTML/BODY/SCRIPT";
        // 将指定的页面解析为DOM文档
        Document doc = getDocument("http://dealer.xgo.com.cn/d_11615/price.html");
        // 根据xpath获得指定的节点
        String artPath = "/HTML/BODY/DIV[4]/DIV[1]/DIV[2]/DIV[2]/DIV[1]/DIV[2]/DIV[1]/DIV";
        NodeList list = getExactNode(doc, artPath);
        System.out.println(list.getLength());
        System.out.println(list.item(0).getAttributes().getNamedItem("class").getNodeValue());
        System.out.println(list.item(0).getChildNodes().getLength());
        HTMLDivElementImpl node=(HTMLDivElementImpl)list.item(0);

    //    print((Node)node,"");
      //  System.out.println(node.getLastChild().getTextContent());
         //list = getExactNode(node, "DIV/DIV");


   /*     System.out.println("符合条件的结点个数为：" + list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            System.out.println("获取的节点属性为："
                    + list.item(i).getTextContent());
        }*/
    }
    public static void print(Node node,String str)
    {
    	System.out.println(str+node.getNodeName());
    	Node no=node.getFirstChild();
    	while(no!=null)
    	{
    		str+="  ";
    		print(no,str);
    		no=no.getNextSibling();
    	}
    	
    }
}
