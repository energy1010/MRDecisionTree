package com.younger.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class XmlPath {
	
	private static final Logger log = LoggerFactory.getLogger(XmlPath.class);
	
	
	public static String getStringValue(String fileName,String experssion){
		String result = null;
		try{
			Document doc = getDocument(fileName);
		 XPathExpression expr   = getXPathExpression( experssion);
				 result =   expr.evaluate(doc);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} 
		return result;
	}
	
//	public static Boolean getObjectValue(String fileName,String experssion){
//		Object result = null;
//		try{
//			Document doc = getDocument(fileName);
//		 XPathExpression expr   = getXPathExpression(experssion);
//				 result =   expr.evaluate(doc,XPathConstants.BOOLEAN);
//		}  catch (Exception e) {
//			log.error(e.getMessage());
//			e.printStackTrace();
//		} 
//		return result;
//	}
	
	public static Object getObjectValue(String fileName,String experssion){
		Object result = null;
		try{
			Document doc = getDocument(fileName);
		 XPathExpression expr   = getXPathExpression(experssion);
				 result =   expr.evaluate(doc,XPathConstants.NODE);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} 
		return result;
	}
	
	
	public static Document getDocument(String fileName){
		DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();   
		domBuilderFactory.setNamespaceAware(true); // never forget this!   
		DocumentBuilder builder = null;
		Object result = null;
		Document doc = null;
		try {
			builder = domBuilderFactory.newDocumentBuilder();
			 doc = builder.parse(fileName);
		}catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return doc;
	}
	
//	/**
//	 * 
//	 * @param fileName
//	 * @param experssion
//	 * @param qName   XPathConstants.NODESET
//	 * @return
//	 */
//	public static XPathFunction getXPathFunction(String experssion){
//			XPathFunctionResolver xPathFunction = null;
//			try{
//			 XPathFactory xPathFactory = XPathFactory.newInstance(); 
//			 XPath xpath = xPathFactory.newXPath(); 
//			 xPathFunction   = xpath.getXPathFunctionResolver();
////			 xPathFactory.
//		}  catch (Exception e) {
//			log.error(e.getMessage());
//			e.printStackTrace();
//		} 
//		return expr;
//	}
//	
	/**
	 * 
	 * @param fileName
	 * @param experssion
	 * @param qName   XPathConstants.NODESET
	 * @return
	 */
	public static XPathExpression getXPathExpression(String experssion){
			XPathExpression expr = null;
			try{
			 XPathFactory xPathFactory = XPathFactory.newInstance(); 
			 XPath xpath = xPathFactory.newXPath(); 
			  expr   = xpath.compile(experssion); 
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} 
		return expr;
	}
	
	
	
	public static void main(String[] args) {
		String result = XmlPath.getStringValue("save/tree.xml", "//node[@nodeId='1']//attName") ;
				System.out.println(result);
//				result = XmlPath.getStringValue("save/tree.xml", "//node[@nodeId='1']//tag") ;
//				System.out.println(result);
//				result = XmlPath.getStringValue("save/tree.xml", "//node[@nodeId='1']//attributevalueSplit") ;
//				System.out.println(result);
//				result = XmlPath.getStringValue("save/tree.xml", "//node[@nodeId='1']//childNodeIds") ;
//				System.out.println(result);
//				result = XmlPath.getStringValue("save/tree.xml", "//node[@nodeId='1']//recordIds") ;
//				String[] recordIds = result.split(" ");
//				System.out.println(result);
//				result = XmlPath.getStringValue("save/tree.xml", "//node[@nodeId='1']/attribute::totalNodeSize") ;
//				System.out.println(result);
				result = XmlPath.getStringValue("save/tree.xml", "//DecisionTree[@totalNodeSize]//child::*") ;
				System.out.println(result);
				result = XmlPath.getStringValue("save/tree.xml", "//node[@nodeId='1']//child::*") ; //选取当前节点的所有属性。
				System.out.println(result);
				result = XmlPath.getStringValue("save/tree.xml", "//node[@nodeId='1']//child::text()") ; //选取当前节点的所有文本子节点。
				System.out.println(result);
//				result = XmlPath.getStringValue("save/tree.xml", "//DecisionTree[0]/@totalNodeSize") ; //error
//				System.out.println(result);
//				result = XmlPath.getStringValue("save/tree.xml", "//DecisionTree[1]//@totalNodeSize") ; //start from 1
//				System.out.println(result);
//				result = XmlPath.getStringValue("sava/tree.xml", "//node[@nodeId='1']//node[@nodeId='5']");
//				System.out.println(result);
				result = XmlPath.getStringValue("sava/tree.xml", " count(//node)");
				System.out.println(result);
				
	}


}
