package com.younger.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.younger.data.DataNode;
import com.younger.data.DataNode.DataType;
import com.younger.decisionTree.AbstractDecisionTree;


/**
 * dom的基本对象有5个：document，node，nodelist，element和attr
 * document对象代表了整个xml的文档，所有其它的node，都以一定的顺序包含在document对象之内，排列成一个树形的结构，程序员可以通过遍历这颗树来得到xml文档的所有的内容，这也是对xml文档操作的起点。我们总是先通过解析xml源文件而得到一个document对象，然后再来执行后续的操作.
 * node对象是dom结构中最为基本的对象，代表了文档树中的一个抽象的节点。在实际使用的时候，很少会真正的用到node这个对象，而是用到诸如element、attr、text等node对象的子对象来操作文档。node对象为这些对象提供了一个抽象的、公共的根。虽然在node对象中定义了对其子节点进行存取的方法，但是有一些node子对象，比如text对象，它并不存在子节点，这一点是要注意的。
 * nodelist对象，顾名思义，就是代表了一个包含了一个或者多个node的列表.
 * element对象代表的是xml文档中的标签元素，继承于node，亦是node的最主要的子对象。在标签中可以包含有属性，因而element对象中有存取其属性的方法，而任何node中定义的方法，也可以用在element对象上面。
 * attr对象代表了某个标签中的属性。attr继承于node，但是因为attr实际上是包含在element中的，它并不能被看作是element的子对象，因而在dom中attr并不是dom树的一部分，所以node中的getparentnode()，getprevioussibling()和getnextsibling()返回的都将是null。也就是说，attr其实是被看作包含它的element对象的一部分，它并不作为dom树中单独的一个节点出现。这一点在使用的时候要同其它的node子对象相区别。
 */
public class DomXml {
	
	private static final Logger log = LoggerFactory.getLogger(DomXml.class);

	@SuppressWarnings("rawtypes")
	public static List readXMLFile(String inFile) throws Exception {
		//	得到DOM解析器的工厂实例
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			// 从DOM工厂获得DOM解析器
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce); 
			return null;
		}

		Document doc = null;
		try {
			// 解析XML文档的输入流，得到一个Document
			doc = db.parse(inFile);
			// 对document对象调用normalize()，可以去掉xml文档中作为格式化内容的空白，
			// 避免了这些空白映射在dom树中成为不必要的text node对象。
			// 否则你得到的dom树可能并不是你所想象的那样。
			// 特别是在输出的时候，这个normalize()更为有用。 
			doc.normalize();
		} catch (DOMException dom) {
			System.err.println(dom.getMessage());
			return null;
		} catch (IOException ioe) {
			System.err.println(ioe);
			return null;
		}

		List studentBeans = new ArrayList();
		StudentBean studentBean = null;
		//	得到XML文档的根节点“学生花名册”
		Element root = doc.getDocumentElement();
		//	取"学生"元素列表
		NodeList students = root.getElementsByTagName("学生");
		for (int i = 0; i < students.getLength(); i++) {
			//	依次取每个"学生"元素
			Element student = (Element) students.item(i);
			//	创建一个学生的Bean实例
			studentBean = new StudentBean();
			//	取学生的性别属性
			studentBean.setGender(student.getAttribute("性别"));
			
			//	取“姓名”元素
			NodeList names = student.getElementsByTagName("姓名");
			if (names.getLength() == 1) {
				Element e = (Element) names.item(0);
				// 取姓名元素的第一个子节点，即为姓名的值节点
				Text t = (Text) e.getFirstChild();
				// 获取值节点的值
				studentBean.setName(t.getNodeValue());
			}

			// 取“年龄”元素
			NodeList ages = student.getElementsByTagName("年龄");
			if (ages.getLength() == 1) {
				Element e = (Element) ages.item(0);
				Text t = (Text) e.getFirstChild();
				studentBean.setAge(Integer.parseInt(t.getNodeValue()));
			}

			//	取“电话”元素
			NodeList phones = student.getElementsByTagName("电话");
			if (phones.getLength() == 1) {
				Element e = (Element) phones.item(0);
				Text t = (Text) e.getFirstChild();
				studentBean.setPhone(t.getNodeValue());
			}
			// 将新建的Bean加到结果列表中
			studentBeans.add(studentBean);
		}
		// 返回结果列表
		return studentBeans;
	}
	
	/**
	 * 用DOM写XML文档，以XML文档的形式存储
	 * @param outFile	输出XML文档的路径
	 * @param nodeList	
	 * @throws Exception
	 */
	public static String writeXMLFile(String outFile, List<DataNode> nodeList) throws Exception {
		//为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			return null;
		}
		// 新建一个空文档
		Document doc = null;
		doc = db.newDocument();

		// 下面是建立XML文档内容的过程.
		// 先建立根元素"学生花名册"，并添加到文档中 
		Element root = doc.createElement("学生花名册");
		doc.appendChild(root);

		//取学生信息的Bean列表 
		for (int i = 0; i < nodeList.size(); i++) {
			//	依次取每个学生的信息 
			DataNode node =  nodeList.get(i);
			Element nodeElement = treeNodeToElement(node, doc);
			root.appendChild(nodeElement);
		}
		//		使用JAXP将DOM对象写到XML文档里
		//	把XML文档输出到指定的文件 
		return domDocToFile(doc, outFile, "GB2312");
	}
	

	/**
	 * 用DOM写XML文档，以XML文档的形式存储
	 * @param outFile	输出XML文档的路径
	 * @param nodeList	
	 * @throws Exception
	 */
	public static Element writeParentTreeNode(Element parentNodeElement, List<DataNode> nodeList,Document doc) throws Exception {
		assert(parentNodeElement!=null);
		assert(nodeList!=null);
		assert(nodeList.size()>0);
		for (int i = 0; i < nodeList.size(); i++) {
			DataNode node =  nodeList.get(i);
			Element nodeElement = treeNodeToElement(node, doc);
			parentNodeElement.appendChild(nodeElement);
		}
		return parentNodeElement;
	}
	
	
	/**
	 * write treeNode to element in xml
	 * build a Node element 
	 * @param node
	 * @param doc
	 * @return node element
	 */
	public static Element treeNodeToElement(DataNode node,Document doc){
		  Element nodeEle = doc.createElement("node"); // build a Node element  
		  
		  // the nodeId attribute for node element
		  nodeEle.setAttribute("nodeId", node.getNodeId()+"");
		  // tag element
		  org.w3c.dom.Element tagNamElement= doc.createElement("tag");
		  String tagNameString =null;
		  if(node.getTag()==DataType.SplitAttributeName){
			  tagNameString = "internal";
		  }else{
			  tagNameString = "leaf";
		  }
		  nodeEle.appendChild(tagNamElement);// the node element add the tagName element 
		  Text tagText = doc.createTextNode(tagNameString);
		  tagNamElement.appendChild(tagText);

		  // candidateSplitAttributeIndexes element
	       Element candidateSplitAttributeIndexesElement= doc.createElement("candidateSplitAttributeIndexes");
			  nodeEle.appendChild(candidateSplitAttributeIndexesElement);
			  Text candidateSplitAttributeIndexesText = null;
			  if(node.getCandidateSplitAttributeNameIndexes().size()>0){
				  StringBuffer sBuffer=new StringBuffer();
				  for(Integer n:node.getCandidateSplitAttributeNameIndexes()){
					  sBuffer.append(n);
					  sBuffer.append(" ");
				  }
				  candidateSplitAttributeIndexesText= doc.createTextNode(sBuffer.toString());
			  }else{
				  candidateSplitAttributeIndexesText =  doc.createTextNode("");
			  }
			 candidateSplitAttributeIndexesElement.appendChild(candidateSplitAttributeIndexesText);
		  
			 // splitAttributeIndex element
			 org.w3c.dom.Element splitAttributeIndexElement= doc.createElement("splitAttributeIndex");
			 nodeEle.appendChild(splitAttributeIndexElement); 
			 Text splitAttributeIndexText = doc.createTextNode(node.getSplitAttributeIndex()+"");
			 splitAttributeIndexElement.appendChild(splitAttributeIndexText);

			 // attName  element
		  Element attributeNameElement = doc.createElement("splitAttributeName");
		  nodeEle.appendChild(attributeNameElement);
		 Text attNameText = null;
		 if(node.getSplitAttributeName()!=null){
		   attNameText=doc.createTextNode(node.getSplitAttributeName());
		 }else{
			  attNameText=doc.createTextNode("null");
		 }
		  attributeNameElement.appendChild(attNameText);   //set the value of attributeName
		
		  // attributevalueSplit element
		  org.w3c.dom.Element attributevalueSplitElement= doc.createElement("splitAttributevalueSplit");
		  nodeEle.appendChild(attributevalueSplitElement);
		  Text attributevalueSplitText =null;
		  if(node.getSplitAttributevalueSplit()!=null){
			  attributevalueSplitText = doc.createTextNode(node.getSplitAttributevalueSplit());
		  }else{
			  attributevalueSplitText =doc.createTextNode("null");
		  }
		  attributevalueSplitElement.appendChild(attributevalueSplitText);
		 
			// childNodesNum element
			 org.w3c.dom.Element childNodeNumElement= doc.createElement("childNodeNum");
			  nodeEle.appendChild(childNodeNumElement);
			  Text childNodeNumText = null;
				  childNodeNumText= doc.createTextNode(node.getChildDataNodeNum()+"");
			 childNodeNumElement.appendChild(childNodeNumText);
		  
		// childNodeIds element
			 org.w3c.dom.Element childNodeIdsElement= doc.createElement("childNodeIds");
			  nodeEle.appendChild(childNodeIdsElement);
			  Text childNodeIdsText = null;
			  if(node.getChildDataNodeNum()>0){
				  StringBuffer sBuffer=new StringBuffer();
				  for(DataNode n:node.getChildDataNodes()){
					  sBuffer.append(n.getNodeId());
					  sBuffer.append(" ");
				  }
				  childNodeIdsText= doc.createTextNode(sBuffer.toString());
			  }else{
				  childNodeIdsText =  doc.createTextNode("");
			  }
			 childNodeIdsElement.appendChild(childNodeIdsText);
			 
			 // recordIds element
		  org.w3c.dom.Element recoredIdsElement= doc.createElement("recordIds");
		  nodeEle.appendChild(recoredIdsElement);
			 Text recoredIdsText  = null;
			 if(node.getRecordIds().size()>0){
		  StringBuffer sBuffer1=new StringBuffer();
		  for(Integer n:node.getRecordIds()){
				 sBuffer1.append(n);
				 sBuffer1.append(" ");
				  recoredIdsText = doc.createTextNode(sBuffer1.toString());
			  }
			 }else{
				 recoredIdsText = doc.createTextNode("");
			 }
			 recoredIdsElement.appendChild(recoredIdsText);
			 
			 // recordNum element
			  org.w3c.dom.Element recoredNumElement= doc.createElement("recordNum");
			  nodeEle.appendChild(recoredNumElement); 
			  Text recordNumText = doc.createTextNode(node.getRecordsNum()+"");
			  recoredNumElement.appendChild(recordNumText);
			  assert(nodeEle!=null);
			  return nodeEle;
		
	}
	
	public static String writeTreeNodeListToXML(String filename,List<DataNode> dataNodesList){
		  File file = new File(filename);
			if (file.exists()){
				log.debug("file "+filename +" exist , delete it ----");
				file.delete();
				log.debug("recreated file "+filename );
			}
			//为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException pce) {
				System.err.println(pce);
				return null;
			}
			// 新建一个空文档
			Document doc = null;
			doc = db.newDocument();
			// 下面是建立XML文档内容的过程.
			// 先建立根元素，并添加到文档中 
			Element root = doc.createElement("DecisionTree");
			doc.appendChild(root);
			root.setAttribute("totalNodeSize",dataNodesList.size()+"");
//			root.setAttribute("leaftNodeSize",tree.getLeafNodesSize()+"");
			Element nodeElement = null;
			for (DataNode dataNode : dataNodesList) {
			try {
				nodeElement = treeNodeToElement(dataNode, doc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		  root.appendChild(nodeElement); //add to root node
				}//while
		//	把XML文档输出到指定的文件 
			try {
			return domDocToFile(doc, filename, "GBK");
//			return domDocToFile(doc, filename, "GB2312");
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * write the tree data structure to xml file in lever order</br>
	 * if fileName exist ,then delete it and recreate it </br>
	 * @param filename
	 * @param tree
	 * @return file.getAbsolutePath()
	 */
	public static String  writeTreeToXml1(String filename,AbstractDecisionTree tree){
		  File file = new File(filename);
		if (file.exists()){
			log.debug("file "+filename +" exist , delete it ----");
			file.delete();
			log.debug("recreated file "+filename );
		}
		//为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			return null;
		}
		// 新建一个空文档
		Document doc = null;
		doc = db.newDocument();
		// 下面是建立XML文档内容的过程.
		// 先建立根元素，并添加到文档中 
		Element root = doc.createElement("DecisionTree");
		doc.appendChild(root);
		root.setAttribute("totalNodeSize",tree.getTotalNodesSize()+"");
		root.setAttribute("leaftNodeSize",tree.getLeafNodesSize()+"");
		Deque<DataNode> queue = new LinkedList<DataNode>();
		queue.add(tree.getRootNode());
		while ((!queue.isEmpty())&&(queue.peekFirst()!=null)) {
			DataNode firstNode = queue.pollFirst();
				if(firstNode.getChildDataNodeNum()>0)
					queue.addAll(firstNode.getChildDataNodes());
				Element parentNodeElement= treeNodeToElement(firstNode, doc);
			Element nodeElement = null;
			try {
				nodeElement = writeParentTreeNode(parentNodeElement, firstNode.getChildDataNodes(), doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  root.appendChild(nodeElement); //add to root node
				}//while
			
		//	把XML文档输出到指定的文件 
		try {
			String filePath= domDocToFile(doc, filename, "GBK");
			log.info("write tree in XML to "+filePath+ " successfully!" );
			return filePath;
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public static String  writeTreeToXml(String filename,AbstractDecisionTree tree){
		  File file = new File(filename);
		if (file.exists()){
			log.debug("file "+filename +" exist , delete it ----");
			file.delete();
			log.debug("recreated file "+filename );
		}
		//为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			return null;
		}
		// 新建一个空文档
		Document doc = null;
		doc = db.newDocument();
		// 下面是建立XML文档内容的过程.
		// 先建立根元素，并添加到文档中 
		Element root = doc.createElement("DecisionTree");
		doc.appendChild(root);
//		DataNode firstNode = tree.getRootNode();
		root.setAttribute("totalNodeSize",tree.getTotalNodesSize()+"");
		root.setAttribute("leaftNodeSize",tree.getLeafNodesSize()+"");
		Deque<DataNode> queue = new LinkedList<DataNode>();
		queue.add(tree.getRootNode());
		while ((!queue.isEmpty())&&(queue.peekFirst()!=null)) {
			DataNode firstNode = queue.pollFirst();
				if(firstNode.getChildDataNodeNum()>0)
					queue.addAll(firstNode.getChildDataNodes());
			Element nodeElement = treeNodeToElement(firstNode, doc);
		  root.appendChild(nodeElement); //add to root node
				}//while
			
		//	把XML文档输出到指定的文件 
		try {
			return domDocToFile(doc, filename, "GBK");
//			return domDocToFile(doc, filename, "GB2312");
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * 使用JAXP将DOM对象写到XML文档里
	 * @param doc	DOM的文档对象
	 * @param fileName	写入的XML文档路径
	 * @param encoding	XML文档的编码
	 * @throws TransformerException
	 * @return file.getAbsolutePath()
	 */
	public static String domDocToFile(Document doc, String fileName, String encoding)
			throws TransformerException {
		// 首先创建一个TransformerFactory对象,再由此创建Transformer对象。
		// Transformer类相当于一个XSLT引擎。通常我们使用它来处理XSL文件,
		// 但是在这里我们使用它来输出XML文档。
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		
		// 获取Transformser对象的输出属性,亦即XSLT引擎的缺省输出属性,是java.util.Properties对象
		Properties properties = transformer.getOutputProperties();
		// 设置新的输出属性:输出字符编码为GB2312,这样可以支持中文字符,
		// XSLT引擎所输出的XML文档如果包含了中文字符,可以正常显示。
		properties.setProperty(OutputKeys.ENCODING, "GB2312");
		// 这里设置输出为XML格式，实际上这是XSLT引擎的默认输出格式
		properties.setProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperties(properties);
		
		// 创建一个DOMSource对象,该构造函数的参数可以是一个Document对象
		DOMSource source = new DOMSource(doc);
		// 创建XSLT引擎的输出对象，这里将输出写如文件
		File file = new File(fileName);
		StreamResult result = new StreamResult(file);
		// 执行DOM文档到XML文件的转换
		transformer.transform(source, result);
		// 将输出文件的路径返回
		return file.getAbsolutePath();
	}
	

	public static void main(String[] args) {
		String inFileName = "students.xml";
		String outFileName = "students_new2.xml";
//		String outFileName = Constants.TreeXmlPath;
		try {
			List studentBeans = DomXml.readXMLFile(inFileName);
			DomXml.writeXMLFile(outFileName, studentBeans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}