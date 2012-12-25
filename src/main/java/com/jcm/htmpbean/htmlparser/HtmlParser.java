package com.jcm.htmpbean.htmlparser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jcm.htmpbean.bean.EleBlock;
import com.jcm.htmpbean.bean.EleMeta;

public class HtmlParser {
	public List<Map<String, String>> parserHtml(EleBlock eleBlock,Document doc)
	{
		List<Map<String, String>> mapList=new ArrayList<Map<String, String>>();
		analysisHtml(eleBlock,doc,mapList);
		return mapList;
	}
	private void analysisHtml(EleBlock eleBlock,Document doc,List<Map<String, String>> mapList)
	{
		List<EleBlock> eleBlocks=eleBlock.getSubBlocks();
		Map<String, String> mapObj=new HashMap<String, String>();
		if(eleBlocks.size()>0)
		{
			for(EleBlock block:eleBlocks)//处理深度
			{
				analysisHtml(block,doc,mapList);
			}
		}else{
			mapList.add(mapObj);
		}

		List<EleMeta> eleMetas=eleBlock.getEleMetas();
		for(EleMeta eleMeta:eleMetas)//处理块
		{
			NodeList nodeList=getExactNode(doc,eleMeta.getHtmlpath().toUpperCase());
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
				System.out.println(txtValue);
				txtValue=regex(eleMeta, txtValue);
				try {
	
					System.out.println(new String(txtValue.getBytes("iso-8859-1"),"gbk"));
					System.out.println();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			if(eleMeta.getFistregstr()!=null && ! eleMeta.getFistregstr().equals(""))
			{
				Pattern pattern=Pattern.compile(eleMeta.getFistregstr());
				 Matcher matcher= pattern.matcher(str);
				 if(matcher.find())
				 {
					String subStr= matcher.group();
					int index=str.indexOf(subStr);
					beginIndex=index+subStr.length();
				 }
			}
			if(eleMeta.getLastregstr()!=null && ! eleMeta.getLastregstr().equals(""))
			{
				Pattern pattern=Pattern.compile(eleMeta.getLastregstr());
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
    private static NodeList getExactNode(Document doc, String xp) {
        NodeList list = null;
        try {
            list = XPathAPI.selectNodeList(doc, xp);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return list;
    }
}
