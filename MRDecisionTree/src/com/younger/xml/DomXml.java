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
 * dom�Ļ���������5����document��node��nodelist��element��attr
 * document�������������xml���ĵ�������������node������һ����˳�������document����֮�ڣ����г�һ�����εĽṹ������Ա����ͨ��������������õ�xml�ĵ������е����ݣ���Ҳ�Ƕ�xml�ĵ���������㡣����������ͨ������xmlԴ�ļ����õ�һ��document����Ȼ������ִ�к����Ĳ���.
 * node������dom�ṹ����Ϊ�����Ķ��󣬴������ĵ����е�һ������Ľڵ㡣��ʵ��ʹ�õ�ʱ�򣬺��ٻ��������õ�node������󣬶����õ�����element��attr��text��node������Ӷ����������ĵ���node����Ϊ��Щ�����ṩ��һ������ġ������ĸ�����Ȼ��node�����ж����˶����ӽڵ���д�ȡ�ķ�����������һЩnode�Ӷ��󣬱���text���������������ӽڵ㣬��һ����Ҫע��ġ�
 * nodelist���󣬹���˼�壬���Ǵ�����һ��������һ�����߶��node���б�.
 * element����������xml�ĵ��еı�ǩԪ�أ��̳���node������node������Ҫ���Ӷ����ڱ�ǩ�п��԰��������ԣ����element�������д�ȡ�����Եķ��������κ�node�ж���ķ�����Ҳ��������element�������档
 * attr���������ĳ����ǩ�е����ԡ�attr�̳���node��������Ϊattrʵ�����ǰ�����element�еģ��������ܱ�������element���Ӷ��������dom��attr������dom����һ���֣�����node�е�getparentnode()��getprevioussibling()��getnextsibling()���صĶ�����null��Ҳ����˵��attr��ʵ�Ǳ�������������element�����һ���֣���������Ϊdom���е�����һ���ڵ���֡���һ����ʹ�õ�ʱ��Ҫͬ������node�Ӷ���������
 */
public class DomXml {
	
	private static final Logger log = LoggerFactory.getLogger(DomXml.class);

	@SuppressWarnings("rawtypes")
	public static List readXMLFile(String inFile) throws Exception {
		//	�õ�DOM�������Ĺ���ʵ��
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			// ��DOM�������DOM������
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce); 
			return null;
		}

		Document doc = null;
		try {
			// ����XML�ĵ������������õ�һ��Document
			doc = db.parse(inFile);
			// ��document�������normalize()������ȥ��xml�ĵ�����Ϊ��ʽ�����ݵĿհף�
			// ��������Щ�հ�ӳ����dom���г�Ϊ����Ҫ��text node����
			// ������õ���dom�����ܲ��������������������
			// �ر����������ʱ�����normalize()��Ϊ���á� 
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
		//	�õ�XML�ĵ��ĸ��ڵ㡰ѧ�������ᡱ
		Element root = doc.getDocumentElement();
		//	ȡ"ѧ��"Ԫ���б�
		NodeList students = root.getElementsByTagName("ѧ��");
		for (int i = 0; i < students.getLength(); i++) {
			//	����ȡÿ��"ѧ��"Ԫ��
			Element student = (Element) students.item(i);
			//	����һ��ѧ����Beanʵ��
			studentBean = new StudentBean();
			//	ȡѧ�����Ա�����
			studentBean.setGender(student.getAttribute("�Ա�"));
			
			//	ȡ��������Ԫ��
			NodeList names = student.getElementsByTagName("����");
			if (names.getLength() == 1) {
				Element e = (Element) names.item(0);
				// ȡ����Ԫ�صĵ�һ���ӽڵ㣬��Ϊ������ֵ�ڵ�
				Text t = (Text) e.getFirstChild();
				// ��ȡֵ�ڵ��ֵ
				studentBean.setName(t.getNodeValue());
			}

			// ȡ�����䡱Ԫ��
			NodeList ages = student.getElementsByTagName("����");
			if (ages.getLength() == 1) {
				Element e = (Element) ages.item(0);
				Text t = (Text) e.getFirstChild();
				studentBean.setAge(Integer.parseInt(t.getNodeValue()));
			}

			//	ȡ���绰��Ԫ��
			NodeList phones = student.getElementsByTagName("�绰");
			if (phones.getLength() == 1) {
				Element e = (Element) phones.item(0);
				Text t = (Text) e.getFirstChild();
				studentBean.setPhone(t.getNodeValue());
			}
			// ���½���Bean�ӵ�����б���
			studentBeans.add(studentBean);
		}
		// ���ؽ���б�
		return studentBeans;
	}
	
	/**
	 * ��DOMдXML�ĵ�����XML�ĵ�����ʽ�洢
	 * @param outFile	���XML�ĵ���·��
	 * @param nodeList	
	 * @throws Exception
	 */
	public static String writeXMLFile(String outFile, List<DataNode> nodeList) throws Exception {
		//Ϊ����XML��׼��������DocumentBuilderFactoryʵ��,ָ��DocumentBuilder 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			return null;
		}
		// �½�һ�����ĵ�
		Document doc = null;
		doc = db.newDocument();

		// �����ǽ���XML�ĵ����ݵĹ���.
		// �Ƚ�����Ԫ��"ѧ��������"������ӵ��ĵ��� 
		Element root = doc.createElement("ѧ��������");
		doc.appendChild(root);

		//ȡѧ����Ϣ��Bean�б� 
		for (int i = 0; i < nodeList.size(); i++) {
			//	����ȡÿ��ѧ������Ϣ 
			DataNode node =  nodeList.get(i);
			Element nodeElement = treeNodeToElement(node, doc);
			root.appendChild(nodeElement);
		}
		//		ʹ��JAXP��DOM����д��XML�ĵ���
		//	��XML�ĵ������ָ�����ļ� 
		return domDocToFile(doc, outFile, "GB2312");
	}
	

	/**
	 * ��DOMдXML�ĵ�����XML�ĵ�����ʽ�洢
	 * @param outFile	���XML�ĵ���·��
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
			//Ϊ����XML��׼��������DocumentBuilderFactoryʵ��,ָ��DocumentBuilder 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException pce) {
				System.err.println(pce);
				return null;
			}
			// �½�һ�����ĵ�
			Document doc = null;
			doc = db.newDocument();
			// �����ǽ���XML�ĵ����ݵĹ���.
			// �Ƚ�����Ԫ�أ�����ӵ��ĵ��� 
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
		//	��XML�ĵ������ָ�����ļ� 
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
		//Ϊ����XML��׼��������DocumentBuilderFactoryʵ��,ָ��DocumentBuilder 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			return null;
		}
		// �½�һ�����ĵ�
		Document doc = null;
		doc = db.newDocument();
		// �����ǽ���XML�ĵ����ݵĹ���.
		// �Ƚ�����Ԫ�أ�����ӵ��ĵ��� 
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
			
		//	��XML�ĵ������ָ�����ļ� 
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
		//Ϊ����XML��׼��������DocumentBuilderFactoryʵ��,ָ��DocumentBuilder 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			return null;
		}
		// �½�һ�����ĵ�
		Document doc = null;
		doc = db.newDocument();
		// �����ǽ���XML�ĵ����ݵĹ���.
		// �Ƚ�����Ԫ�أ�����ӵ��ĵ��� 
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
			
		//	��XML�ĵ������ָ�����ļ� 
		try {
			return domDocToFile(doc, filename, "GBK");
//			return domDocToFile(doc, filename, "GB2312");
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * ʹ��JAXP��DOM����д��XML�ĵ���
	 * @param doc	DOM���ĵ�����
	 * @param fileName	д���XML�ĵ�·��
	 * @param encoding	XML�ĵ��ı���
	 * @throws TransformerException
	 * @return file.getAbsolutePath()
	 */
	public static String domDocToFile(Document doc, String fileName, String encoding)
			throws TransformerException {
		// ���ȴ���һ��TransformerFactory����,���ɴ˴���Transformer����
		// Transformer���൱��һ��XSLT���档ͨ������ʹ����������XSL�ļ�,
		// ��������������ʹ���������XML�ĵ���
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		
		// ��ȡTransformser������������,�༴XSLT�����ȱʡ�������,��java.util.Properties����
		Properties properties = transformer.getOutputProperties();
		// �����µ��������:����ַ�����ΪGB2312,��������֧�������ַ�,
		// XSLT�����������XML�ĵ���������������ַ�,����������ʾ��
		properties.setProperty(OutputKeys.ENCODING, "GB2312");
		// �����������ΪXML��ʽ��ʵ��������XSLT�����Ĭ�������ʽ
		properties.setProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperties(properties);
		
		// ����һ��DOMSource����,�ù��캯���Ĳ���������һ��Document����
		DOMSource source = new DOMSource(doc);
		// ����XSLT���������������ｫ���д���ļ�
		File file = new File(fileName);
		StreamResult result = new StreamResult(file);
		// ִ��DOM�ĵ���XML�ļ���ת��
		transformer.transform(source, result);
		// ������ļ���·������
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