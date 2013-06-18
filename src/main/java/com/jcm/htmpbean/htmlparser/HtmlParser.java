package com.jcm.htmpbean.htmlparser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jcm.htmpbean.bean.EleBlock;
import com.jcm.htmpbean.bean.EleData;
import com.jcm.htmpbean.bean.EleMeta;

public class HtmlParser {
	public EleData parserHtml(EleBlock eleBlock,Document doc)
	{
		EleData eleData=new EleData();
		analysisHtml(eleBlock,doc,eleData);
		return eleData;
	}
	private synchronized void analysisHtml(EleBlock eleBlock,Node doc,EleData eleData)
	{
		List<EleBlock> eleBlocks=eleBlock.getSubBlocks();
		if(eleBlocks.size()>0)
		{
			for(int in=0;in!=eleBlocks.size();++in)//处理深度
			{
				EleBlock block=eleBlocks.get(in);
				NodeList list=getExactNode(doc,eleBlock.getCurrentPath().get(in));
				for(int i=0;i!=list.getLength();++i)
				{
					EleData subData=new EleData();
					eleData.getSubData().add(subData);
					analysisHtml(block,list.item(i),subData);
				}

			}
		}
		
		List<EleMeta> eleMetas=eleBlock.getEleMetas();//元数据
		List<NodeList> nodeLists=new ArrayList<NodeList>();//纵向数据集合
		List<Map<String,String>> maps=eleData.getBlocks();//new ArrayList<Map<String,String>>();//横向数据集合
		//单独处理  一个map  或者多个map
		for(EleMeta eleMeta:eleMetas)//处理块
		{
			nodeLists.add(getExactNode(doc,eleMeta.getHtmlpath()));
		}
		for(int e=0;e!=eleMetas.size();++e)
		{
			EleMeta eleMeta=eleMetas.get(e);
			NodeList nodeList=nodeLists.get(e);
			if(nodeList==null){
				continue;
			}
			System.out.println(eleMeta.getJname()+".size: "+nodeList.getLength());
			int index=0;
			co:	for(int n=0;n!=nodeList.getLength();++n)
			{
				if(eleMeta.getEquals())//检测相等条件
				{
					Node node=nodeList.item(n).getAttributes().getNamedItem(eleMeta.getEqualsname());
					if(node ==null || ! node.getNodeValue().trim().equalsIgnoreCase(eleMeta.getEqualsvalue()))
					{
						continue co;
					} 
				}
				if(e==0)//以第一纵向数据为基准
				{
					maps.add(new HashMap<String, String>());
				}
				
				Map<String, String> mapObj;
				//Map<String, String> tempMap=new HashMap<String, String>();
				try {
					mapObj = new HashMap<String, String>();//maps.get(index++);
					if(eleMeta.getType().trim().equalsIgnoreCase("attr")){
						Node node=nodeList.item(n);
						Node attr=node.getAttributes().getNamedItem(eleMeta.getProname());
						if(attr==null)
						{
							mapObj.put(eleMeta.getJname(), "");
							continue;
						}
						String attrValue=attr.getNodeValue();
						attrValue=regex(eleMeta, attrValue);
						mapObj.put(eleMeta.getJname(), attrValue);
					}else if(eleMeta.getType().trim().equalsIgnoreCase("txt")){
						Node node=nodeList.item(n);
						if(eleMeta.getIsRef()){
							
						   Source source = new DOMSource(node); 
						   ByteArrayOutputStream  bos  =  new  ByteArrayOutputStream(); 
				           Result res = new StreamResult(bos); 
				            Transformer xformer = TransformerFactory.newInstance().newTransformer(); 
				            xformer.setOutputProperty(OutputKeys.ENCODING, "utf-8"); 
				            xformer.transform(source, res); 
				            String txtValue=bos.toString();
				            txtValue=  txtValue.substring(txtValue.indexOf(">")+1);
							mapObj.put(eleMeta.getJname(), txtValue);
						}else{
							String txtValue=node.getTextContent().trim();
							txtValue=regex(eleMeta, txtValue);
							mapObj.put(eleMeta.getJname(), txtValue);
						}
					}
					if( eleMetas.size() !=nodeList.getLength() && nodeList.getLength()==1)
					{
						for(Map<String,String> map: maps)
						{
							map.putAll(mapObj);
						}
					}else{
						maps.get(index++).putAll(mapObj);
					}
				} catch (Exception e1) {
						System.err.println("块中存在大于第一个元素个数的节点："+ eleMeta.getJname()+".size: "+nodeList.getLength()+"    导致异常.");
						e1.printStackTrace();
				}
			}
		}
//		if(mapList.size() == 0){
//			mapList.addAll(maps);
//			return;
//		}else{
//			List<Map<String,String>> mapsList=new ArrayList<Map<String,String>>();
//			for(int i=0;i!=mapList.size();++i)
//			{
//				Map<String,String> map=mapList.get(i);
//				for(int j=0;j!=maps.size();++j)
//				{
//					Map<String,String> tmp=new HashMap<String, String>();
//					tmp.putAll(map);
//					tmp.putAll(maps.get(j));
//					mapsList.add(tmp);
//				}
//			}
//			if(mapList.removeAll(mapList)){
//				mapList.addAll(mapsList);
//			};
//		}
	}
	/**
	 * 0.0.8 版本
	 * @param eleBlock
	 * @param doc
	 * @return
	 */
	private List<Map<String, String>> parserHtml_bc(EleBlock eleBlock,Document doc)
	{
		List<Map<String, String>> mapList=new ArrayList<Map<String, String>>();
		analysisHtml_bc(eleBlock,doc,mapList);
		return mapList;
	}
	/**
	 * 0.0.8 版本 数据处理方式改变 ,不使用 留作参考
	 * @param eleBlock
	 * @param doc
	 * @param mapList
	 */
	private synchronized void analysisHtml_bc(EleBlock eleBlock,Document doc,List<Map<String, String>> mapList)
	{
		List<EleBlock> eleBlocks=eleBlock.getSubBlocks();
		if(eleBlocks.size()>0)
		{
			for(EleBlock block:eleBlocks)//处理深度
			{
				analysisHtml_bc(block,doc,mapList);
			}
		}
		
		List<EleMeta> eleMetas=eleBlock.getEleMetas();//元数据
		List<NodeList> nodeLists=new ArrayList<NodeList>();//纵向数据集合
		List<Map<String,String>> maps=new ArrayList<Map<String,String>>();//横向数据集合
		//单独处理  一个map  或者多个map
		for(EleMeta eleMeta:eleMetas)//处理块
		{
			nodeLists.add(getExactNode(doc,eleMeta.getHtmlpath()));
		}
		for(int e=0;e!=eleMetas.size();++e)
		{
			EleMeta eleMeta=eleMetas.get(e);
			NodeList nodeList=nodeLists.get(e);
			if(nodeList==null){
				continue;
			}
			System.out.println(eleMeta.getJname()+".size: "+nodeList.getLength());
			int index=0;
			co:	for(int n=0;n!=nodeList.getLength();++n)
			{
				if(eleMeta.getEquals())//检测相等条件
				{
					Node node=nodeList.item(n).getAttributes().getNamedItem(eleMeta.getEqualsname());
					if(node ==null || ! node.getNodeValue().trim().equalsIgnoreCase(eleMeta.getEqualsvalue()))
					{
						continue co;
					}
				}
				if(e==0)//以第一纵向数据为基准
				{
					maps.add(new HashMap<String, String>());
				}
				
				Map<String, String> mapObj;
				try {
					mapObj = maps.get(index++);
					if(eleMeta.getType().trim().equalsIgnoreCase("attr")){
						Node node=nodeList.item(n);
						Node attr=node.getAttributes().getNamedItem(eleMeta.getProname());
						if(attr==null)
						{
							mapObj.put(eleMeta.getJname(), "");
							continue;
						}
						String attrValue=attr.getNodeValue();
						attrValue=regex(eleMeta, attrValue);
						mapObj.put(eleMeta.getJname(), attrValue);
					}else if(eleMeta.getType().trim().equalsIgnoreCase("txt")){
						Node node=nodeList.item(n);
						String txtValue=node.getTextContent().trim();
						txtValue=regex(eleMeta, txtValue);
						mapObj.put(eleMeta.getJname(), txtValue);
					}
				} catch (Exception e1) {
						System.err.println("块中存在大于第一个元素个数的节点："+ eleMeta.getJname()+".size: "+nodeList.getLength()+"    导致异常.");
						e1.printStackTrace();
				}
			}
		}
		if(mapList.size() == 0){
			mapList.addAll(maps);
			return;
		}else{
			List<Map<String,String>> mapsList=new ArrayList<Map<String,String>>();
			for(int i=0;i!=mapList.size();++i)
			{
				Map<String,String> map=mapList.get(i);
				for(int j=0;j!=maps.size();++j)
				{
					Map<String,String> tmp=new HashMap<String, String>();
					tmp.putAll(map);
					tmp.putAll(maps.get(j));
					mapsList.add(tmp);
				}
			}
			if(mapList.removeAll(mapList)){
				mapList.addAll(mapsList);
			};
		}
	}
	
	private void analysisHtml_back (EleBlock eleBlock,Document doc,List<Map<String, String>> mapList)
	{
		List<EleBlock> eleBlocks=eleBlock.getSubBlocks();
		Map<String, String> mapObj=new HashMap<String, String>();
		if(eleBlocks.size()>0)
		{
			for(EleBlock block:eleBlocks)//处理深度
			{
				analysisHtml_back(block,doc,mapList);
			}
		}else{
			mapList.add(mapObj);
		}

		List<EleMeta> eleMetas=eleBlock.getEleMetas();
		for(EleMeta eleMeta:eleMetas)//处理块
		{
			NodeList nodeList=getExactNode(doc,eleMeta.getHtmlpath());
			if(nodeList==null){
				continue;
			}else if(eleMeta.getType().trim().equalsIgnoreCase("attr")){
				Node node=nodeList.item(0);
				Node attr=node.getAttributes().getNamedItem(eleMeta.getProname());
				String attrValue=attr.getNodeValue();
				attrValue=regex(eleMeta, attrValue);
				mapObj.put(eleMeta.getJname(), attrValue);
			}else if(eleMeta.getType().trim().equalsIgnoreCase("txt")){
				Node node=nodeList.item(0);
				String txtValue=node.getTextContent().trim();
				txtValue=regex(eleMeta, txtValue);
				mapObj.put(eleMeta.getJname(), txtValue);
			}
		}
		for(Map<String, String> map:mapList)//补全
		{
			map.putAll(mapObj);
		}
	}
	//处理正则
	private String regex(EleMeta eleMeta,String str)
	{
		if(eleMeta.getIsregxp()){
			int beginIndex=-1;
			int endIndex=-1;
			if(eleMeta.getFistregstr()!=null)
			{
				Pattern pattern=eleMeta.getFistregstr();
				 Matcher matcher= pattern.matcher(str);
				 if(matcher.find())
				 {
					String subStr= matcher.group();
					int index=str.indexOf(subStr);
					beginIndex=index+subStr.length();
				 }
			}
			if(eleMeta.getLastregstr()!=null)
			{
				Pattern pattern=eleMeta.getLastregstr();
				 Matcher matcher= pattern.matcher(str);
				 if(matcher.find())
				 {
					String subStr= matcher.group();
					endIndex=str.indexOf(subStr);
				 }
			}
			if(beginIndex > -1 )
			{
				 if(endIndex > -1)
				{
					 str=str.substring(beginIndex, endIndex);
				 }else{
					 str=str.substring(beginIndex);
				 }
			}else if(endIndex > -1)
			{
				 str=str.substring(0,endIndex);
			}
			
		}
		return str;
	}
    // 通过XPath定位具体的节点
    private  synchronized NodeList getExactNode(Node doc, String xp) {
    	xp=xp.toUpperCase();
        NodeList list = null;
        try {
            list = XPathAPI.selectNodeList(doc, xp);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return list;
    }
}
