package com.czm.module.common.update;

import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UpdateParser {
	public static List<UpdateFileItem> readXML(String XMLInfo)
	{ 			
		XmlPullParser parser = Xml.newPullParser();
		UpdateFileItem currentUpdateFileItem = null;
		List<UpdateFileItem> UpdateFileItems = null;
		
		try
		{ 		
			parser.setInput(new StringReader(XMLInfo));
			int eventType = parser.getEventType(); 	
			
			while (eventType != XmlPullParser.END_DOCUMENT)
			{ 
				switch (eventType) 
				{
					case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
						UpdateFileItems = new ArrayList<>();
						break;
					case XmlPullParser.START_TAG://开始元素事件
						String name = parser.getName();
						if (name.equalsIgnoreCase("file")) 
						{
							currentUpdateFileItem = new UpdateFileItem();
							currentUpdateFileItem.setName(parser.getAttributeValue(null, "name"));
							currentUpdateFileItem.setVer(parser.getAttributeValue(null, "ver"));
							currentUpdateFileItem.setDesc(URLDecoder.decode(parser.getAttributeValue(null, "desc"), "UTF-8"));
							currentUpdateFileItem.setSize(new Integer(parser.getAttributeValue(null, "size")));
							currentUpdateFileItem.setUpdate(parser.getAttributeValue(null, "update"));
                            assert UpdateFileItems != null;
                            UpdateFileItems.add(currentUpdateFileItem);
						}
						break;
					 case XmlPullParser.END_TAG://结束元素事件
					 {
						 if (currentUpdateFileItem != null && currentUpdateFileItem.getName().equalsIgnoreCase("file"))
						 {
							 UpdateFileItems.add(currentUpdateFileItem);
							 currentUpdateFileItem = null; 
						 }    
						 break;
					 }
				 }
				//读写下一个
				eventType = parser.next();
			}			 
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
		 return UpdateFileItems;
	}
	
	public static List<UpdateFileItem> readXMLDOM(String XMLInfo)
	{
		UpdateFileItem currentUpdateFileItem = null;
		List<UpdateFileItem> UpdateFileItems = new ArrayList<>();
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream bytArrInput=new ByteArrayInputStream(XMLInfo.getBytes());
			Document dom =builder.parse(bytArrInput);
			Element root = dom.getDocumentElement();
			Element updateNode=null;
			NodeList items = root.getElementsByTagName("file");
			for (int i = 0; i < items.getLength(); i++)
			{
				currentUpdateFileItem=new UpdateFileItem();
				updateNode= (Element) items.item(i);
				currentUpdateFileItem.setDesc(URLDecoder.decode(updateNode.getAttribute("desc"), "UTF-8"));
				currentUpdateFileItem.setVersion(updateNode.getAttribute("ver"));
				currentUpdateFileItem.setSize(new Integer(updateNode.getAttribute("size")));
				currentUpdateFileItem.setUpdate(updateNode.getAttribute("update"));
				currentUpdateFileItem.setUrl(updateNode.getAttribute("url"));
				UpdateFileItems.add(currentUpdateFileItem);
			 }
		}
		catch(Exception ignored)
		{
		}
		return UpdateFileItems;		
	}
}
