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
				Node pnode=node.getParentNode();
				NamedNodeMap attrList=pnode.getAttributes();
				if (attrList != null) {
					EleMeta equals = new EleMeta();
					equalsDraw(attrList, equals);
					if (equals.getEquals()) {
						eleMeta.setEquals(equals.getEquals());
						eleMeta.setEqualsname(equals.getEqualsname());
						eleMeta.setEqualsvalue(equals.getEqualsvalue());
					}
				}
				getJname(node, eleMeta);
				eleBlock.getEleMetas().add(eleMeta);	
			}
		}else if(node.getNodeType()==Node.ATTRIBUTE_NODE)
		{
			getJname(node, eleMeta);
			eleMeta.setType("attr");
			eleMeta.setProname(node.getNodeName());
			eleBlock.getEleMetas().add(eleMeta);
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
				//设置路径
				String parent=eleMeta.getHtmlpath();
				String curr=node.getNodeName().trim();
				if(curr.indexOf("-")>0)
				{
					curr=curr.replace("-", "[");
					curr=curr.trim()+"]";
				}else{
				//	curr+="[1]";
				} 
				eleMeta.setHtmlpath(parent+"/"+curr);
				NodeList nodeList=node.getChildNodes();
				for(int i=0; nodeList !=null && i!=nodeList.getLength();++i)
				{
					NamedNodeMap attrList= nodeList.item(i).getAttributes();
					
					
					EleMeta ele=new EleMeta();
					ele.clone(eleMeta);
					xmlparser(nodeList.item(i),eleBlock,ele);
					
					if(attrList!=null){
						EleMeta equals=new EleMeta();
						equalsDraw(attrList,equals);
						for(int j=0 ; j!=attrList.getLength();++j)
						{
							if(attrList.item(j).getNodeValue().trim().indexOf("equals")>=0)
							{
								continue;
							}
							EleMeta attrele=new EleMeta();
							attrele.clone(ele);
							if(equals.getEquals()){
								attrele.setEquals(equals.getEquals());
								attrele.setEqualsname(equals.getEqualsname());
								attrele.setEqualsvalue(equals.getEqualsvalue());
							}
							xmlparser(attrList.item(j),eleBlock,attrele);
						}
					}
				}
			}
		}
	}
	private static void equalsDraw(NamedNodeMap attrList, EleMeta equals){
		for(int k=0 ; k!=attrList.getLength();++k)
		{
			if(attrList.item(k).getNodeValue().trim().indexOf("equals")>=0)
			{
				Node ae=attrList.item(k);
				equals.setEquals(true);
				equals.setEqualsname(ae.getNodeName());
				String estr=ae.getNodeValue();
				estr=estr.substring(estr.indexOf("(")+1, estr.indexOf(")"));
			//	attrList.removeNamedItem(ae.getNodeName());
				equals.setEqualsvalue(estr);
				break;
			}
		}
	}
/*	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory builderFactory=DocumentBuilderFactory.newInstance();
		 DocumentBuilder documentBuilder= builderFactory.newDocumentBuilder();
		 Document document=documentBuilder.parse(TagParser.class.getResourceAsStream("/htmlbean/autohomeDealer.xml"));
		 EleBlock eleBlock=new EleBlock();
		xmlparser(document.getChildNodes().item(0),eleBlock,new EleMeta());
		System.out.println(eleBlock.getEleMetas().size());
	}*/
}
