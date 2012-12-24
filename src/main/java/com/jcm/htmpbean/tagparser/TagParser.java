package com.jcm.htmpbean.tagparser;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jcm.htmpbean.bean.EleBlock;
import com.jcm.htmpbean.bean.EleMeta;

public class TagParser {
	public static Pattern pattern=Pattern.compile("[#\\$]\\{.*");
	public static EleBlock parser(InputSource stream)
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		 EleBlock eleBlock=new EleBlock();
		try {
			documentBuilder = builderFactory.newDocumentBuilder();
			Document document=documentBuilder.parse(stream);
			xmlparser(document.getFirstChild(),eleBlock,new EleMeta());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return eleBlock;
	}
	private static void getJname(Node node,EleMeta eleMeta)
	{
		//设置jname
		String str=node.getNodeValue();
		if (str.indexOf("$")>=0) {
			//正则设置 
			str=str.substring(str.indexOf("${")+"${".length(), str.lastIndexOf("}"));
			eleMeta.setFistregstr(str.substring(0, str.indexOf("#{")));
			eleMeta.setLastregstr(str.substring(str.lastIndexOf("}")+1));
			eleMeta.setIsregxp(true);
			str=str.substring(str.indexOf("#{"), str.lastIndexOf("}")+1);
		} 
		//正常属性设置
		str=str.substring(str.indexOf("#{")+"#{".length(), str.lastIndexOf("}"));
		eleMeta.setJname(str);
	}
	//填充数据
	private static  void xmlparser(Node node,EleBlock eleBlock,EleMeta eleMeta)
	{
		if(node.getNodeType()==Node.TEXT_NODE)
		{
			if(node.getNodeValue()!=null && ! node.getNodeValue().trim().equals("") &&  pattern.matcher( node.getNodeValue() ).find()){
				getJname(node, eleMeta);
				eleBlock.getEleMetas().add(eleMeta);
//				System.out.println(node.getNodeName());
//				System.out.println(eleMeta.print());
				
			}
		}else if(node.getNodeType()==Node.ATTRIBUTE_NODE)
		{
			getJname(node, eleMeta);
			eleMeta.setType("attr");
			eleMeta.setProname(node.getNodeName());
			eleBlock.getEleMetas().add(eleMeta);
//			System.out.println(node.getNodeName());
//			System.out.println(eleMeta.print());
		}else if(node.getNodeType()==Node.ELEMENT_NODE){
			if(node.getNodeName().trim().equalsIgnoreCase("list"))
			{
				EleBlock subEleBlock=new EleBlock();
				NodeList nodeList=node.getChildNodes();
				if(nodeList!=null)
				{
					for(int i=0;i!=nodeList.getLength();++i)
					{
						EleMeta subele=new EleMeta();
						subele.clone(eleMeta);
						xmlparser(nodeList.item(i),subEleBlock,subele);
					}
				}
				eleBlock.getSubBlocks().add(subEleBlock);
			}else{
//				System.out.println(node.getNodeName());
//				System.out.println(eleMeta.print());
				//设置路径
				String parent=eleMeta.getHtmlpath();
				String curr=node.getNodeName().trim();
				if(curr.indexOf("-")>0)
				{
					curr=curr.replace("-", "[");
					curr=curr.trim()+"]";
				}else{
					curr+="[1]";
				} 
				eleMeta.setHtmlpath(parent+"/"+curr);
				NodeList nodeList=node.getChildNodes();
				for(int i=0; nodeList !=null && i!=nodeList.getLength();++i)
				{
					EleMeta ele=new EleMeta();
					ele.clone(eleMeta);
					xmlparser(nodeList.item(i),eleBlock,ele);
					NamedNodeMap attrList= nodeList.item(i).getAttributes();
				
					if(attrList!=null){
						for(int j=0 ; j!=attrList.getLength();++j)
						{
							EleMeta attrele=new EleMeta();
							attrele.clone(ele);
							xmlparser(attrList.item(j),eleBlock,attrele);
						}
					}
				}
			}
		}
	}
/*	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory builderFactory=DocumentBuilderFactory.newInstance();
		 DocumentBuilder documentBuilder= builderFactory.newDocumentBuilder();
		 Document document=documentBuilder.parse(TagParser.class.getResourceAsStream("/htmlbean/autohomeImg.xml"));
		 EleBlock eleBlock=new EleBlock();
		xmlparser(document.getChildNodes().item(0),eleBlock,new EleMeta());
		System.out.println(eleBlock.getEleMetas().size());
	}*/
}
