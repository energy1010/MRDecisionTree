package com.younger.decisionTree;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.classifiers.Classifier;
import com.younger.conf.Parameter;
import com.younger.constant.Enum.AttrType;
import com.younger.data.Data;
import com.younger.data.DataConverter;
import com.younger.data.DataNode;
import com.younger.data.DataNode.DataType;
import com.younger.data.DataSet;
import com.younger.struct.BestSplitPointForContinuousAttribute;
import com.younger.tool.FileUtil;
import com.younger.tool.Tool;



/**
 * Task ：EntropyDecision Tree 
 * Read ARFF file to init DataSet</br> The Data file attribute index are defined a begining
 * build classifier with DataSet</br>
 * @author Administrator
 *                                                                      
 */
public class EntropyDecisionTree extends AbstractDecisionTree{
	
	private static final Logger log = LoggerFactory
			.getLogger(EntropyDecisionTree.class);

	private static final long serialVersionUID = 1L;
	
	public EntropyDecisionTree() {
		init();
	}

	/**
	 * init local variables
	 */
	protected void init() {
		m_algorithmName = "Algorithm Name: EntropyDecision Tree";
		m_traingData = new DataSet();
		m_attributeIndexs=new LinkedList<Integer>();
		m_attributeValueDistributionOfEveryAttributeTable= new Hashtable<String, Hashtable<String, Integer>>();
		m_attributeNamesList = new LinkedList<String>();
		m_attrTypesMapTable= new Hashtable<String, AttrType>();
		m_classLabelValuesMap = new LinkedHashMap<String, Integer>();
		m_classIndexs = new ArrayList<Integer>();
		m_level = 0;
		m_totalNodesSize = 0;
		m_attributeNum= 0;
		m_classAttributeName = null ;
//		m_moduleLevel = 0;
	}

	
	@Override
	public void clearClassifier() {
		m_algorithmName = "";
//		m_traingData.clearDataSet();
		m_attributeIndexs.clear();
		m_attributeValueDistributionOfEveryAttributeTable.clear();
		m_attributeNamesList .clear();
		m_attrTypesMapTable.clear();
		m_classLabelValuesMap .clear();
		m_classIndexs .clear();
		m_level = 0;
		m_totalNodesSize = 0;
		m_attributeNum= 0;
		m_classAttributeName ="";
//		m_moduleLevel = 0;
	}
	
	/**
	 * build DT from file
	 * 
	 * @param fileName
	 * @author Administrator
	 */
	public EntropyDecisionTree(String fileName) {
		buildClassifier(fileName);
	}
	

	/**
	 * 
	 * @param examples
	 * @param attributeIndexs
	 * @param startNodeId     from 1 
	 * @param m_level   from 1
	 * @return
	 */
	public DataNode createNodeInBFS(List<Data> examples, List<Integer> attributeIndexs,int startNodeId){
		Deque<DataNode> deque = new LinkedList<DataNode>();
		int enqueueId= startNodeId;
		DataNode tagNode = new DataNode(-1); // tag node for level
		DataNode RootNode = new DataNode(startNodeId);
		RootNode.setRecordIds(getIDsOfDataList(examples)); //get the ids collection of examples
		RootNode.setRecordsNum(getIDsOfDataList(examples).size());
		RootNode.setCandidateSplitAttributeNameIndexes(attributeIndexs);
		RootNode.setAttributevalueSplit(null);
		deque.add(RootNode);  //init the root node enqueue
		deque.add(tagNode); // add the tag node
		m_level = 1; // tree level start from 1 
		m_LeafNodesSize=0; //tree leaf node start from 0
		while ((!deque.isEmpty())&& (deque.peek()!=null)) {
			DataNode currentNode = deque.poll();
			if(currentNode.equals(tagNode)){
				//tag node to note the level
				if(!deque.isEmpty()){
					m_level++; // level +1
					deque.add(tagNode);
					continue;
				}else{
					// over
					m_totalNodesSize = enqueueId;
					return RootNode;
				}
				} 
			else{
				// if have these three situation , the node stop spliting and form a leaf node
				// 1.no records on the node in this branch form a leaf
				List<Data> currentDatas= getDatasByIds(examples, currentNode.getRecordIds());
					if(currentDatas.size()<=0){
						// the node is empty then most common class value
						log.debug("the current node "+currentNode.getNodeId()+currentNode+" is null!");
//						currentNode.setTag(DataType.ClassValue);
						continue;
					}
					// data node 
					//2. stopCretia 
				 if (currentNode.getRecordIds().size() <= Parameter.MinDataSizeInLeafNode
							||m_level > Parameter.MaxTreeHeight) {
					 //case 1 : stop cretia
						log.debug("the currentNode "+currentNode.getNodeId() +" satifies the stop cretia!" );
						currentNode.setChildDataNodeNum(0);
						currentNode.setTag(DataType.ClassValue);
						currentNode.setSplitAttributeName(mostCommonTargetAttribute(currentDatas));
						Dictionary<String, Integer> classDictionary = getClassDistribution(currentDatas);
						List<Integer> classNumDistributionList = new ArrayList<Integer>(m_classIndexs.size());
						for(int i=0;i<m_classIndexs.size();i++){
							classNumDistributionList.add(0);
						}
						for(Enumeration<String> classValueEnumeration= classDictionary.keys();classValueEnumeration.hasMoreElements();){
							String classValue =classValueEnumeration.nextElement();
							classNumDistributionList.set( getClassValueIndexByValue(classValue), classDictionary.get(classValue));
						}
						currentNode.setClassNumDistribution(classNumDistributionList);
						currentNode.setRecordIds(getIDsOfDataList(currentDatas) );
						currentNode.setRecordsNum(currentDatas.size());
						currentNode.setTag(DataType.ClassValue);
						m_LeafNodesSize++;
						continue;
					} 
				 else if (isSameClass(currentDatas)) 
					 //case 2: a leaf node
					{ //3 . both have the same class value
						currentNode.setRecordsNum(currentDatas.size());
						currentNode.setChildDataNodeNum(0);
						currentNode.setTag(DataType.ClassValue);
						int recordId = currentNode.getRecordIds().iterator().next();//start from 1
						String classValue = examples.get(recordId-1).getClassValue();
						log.debug("the currentNode "+currentNode.getNodeId() +" has the  same class value "+ classValue);
						int classValueIndex= getClassValueIndexByValue(classValue); 
						currentNode.setSplitAttributeName(classValue);
						currentNode.setSplitAttributeIndex(getClassValueIndexByValue(classValue) );
						//set the class num distribution in the leaf node 
						List<Integer> classNumDistributionList = new ArrayList<Integer>(m_classIndexs.size());
						for(int i=0;i<m_classIndexs.size();i++){
							classNumDistributionList.add(0);
						}
						 //DataNode [nodeId=6, splitAttributeName=recurrence-events, attributeIndex=-1, attributeValues=[], tag=TargetName, attributevalueSplit=9-11, childDataNodes=[], childDataNodeNum=0, recordIds=[141], classNumDistribution=[0, 1]
						classNumDistributionList.set(classValueIndex, currentDatas.size());
						currentNode.setClassNumDistribution(classNumDistributionList);
						m_LeafNodesSize++;
						continue;
					}
				 
					else if (currentNode.getCandidateSplitAttributeNameIndexes()==null||currentNode.getCandidateSplitAttributeNameIndexes().isEmpty()) { 
						// case 3 : attributes are empty
						log.debug("the currentNode "+currentNode.getNodeId() +" the attributes are empty!" );
						currentNode.setChildDataNodeNum(0);
						currentNode.setTag(DataType.ClassValue);
						currentNode.setSplitAttributeName(mostCommonTargetAttribute(currentDatas));
						Dictionary<String, Integer> classDictionary = getClassDistribution(currentDatas);
						List<Integer> classNumDistributionList = new ArrayList<Integer>(m_classIndexs.size());
						for(int i=0;i<m_classIndexs.size();i++){
							classNumDistributionList.add(0);
						}
						for(Enumeration<String> classValueEnumeration= classDictionary.keys();classValueEnumeration.hasMoreElements();){
							String classValue =classValueEnumeration.nextElement();
							classNumDistributionList.set( getClassValueIndexByValue(classValue), classDictionary.get(classValue));
						}
						currentNode.setClassNumDistribution(classNumDistributionList);
						currentNode.setRecordIds(getIDsOfDataList(currentDatas) );
						currentNode.setRecordsNum(currentDatas.size());
						m_LeafNodesSize++;
						continue;
					}
			
					// case 4: find the split attribute
				// find the max gain splitAttributeIndex and splitAttributeName
				log.debug("find the best split attribute for node "+currentNode.getNodeId() +" in candidate splitAttributeIndex "+Arrays.toString(currentNode.getCandidateSplitAttributeNameIndexes().toArray())); 
				int selectedBestSplitAttributeIndex  = -1;
//				if(Parameter.binarySplit == false){
				 selectedBestSplitAttributeIndex = getBestSplitAttributeIndex(currentDatas, currentNode.getCandidateSplitAttributeNameIndexes(),Parameter.attributeVal) ;
//				}else{
//					BestBinarySplitForCategoryAttribute maxBinarySplitForCategoryAttribute = null;
//					for(int i=0 ; i<currentNode.getCandidateSplitAttributeNameIndexes().size();i++){
//						double maxgain= -1;
//						int testattributeIndex  = currentNode.getCandidateSplitAttributeNameIndexes().get(i);
//					BestBinarySplitForCategoryAttribute bestBinarySplitForCategoryAttribute = getBestBinarySplitForCategroyAttribute(currentDatas, testattributeIndex);
//					maxgain= maxgain >= bestBinarySplitForCategoryAttribute.getMaxGain()? maxgain: bestBinarySplitForCategoryAttribute.getMaxGain();
//					selectedBestSplitAttributeIndex = maxgain >= bestBinarySplitForCategoryAttribute.getMaxGain()?selectedBestSplitAttributeIndex:testattributeIndex;
//					maxBinarySplitForCategoryAttribute = maxgain >= bestBinarySplitForCategoryAttribute.getMaxGain()?maxBinarySplitForCategoryAttribute:bestBinarySplitForCategoryAttribute;
//					}
//					log.debug("binary split " + maxBinarySplitForCategoryAttribute.getSplitAttributeValue());
//				}
 //				log.debug("the selected BestSplitAttributeIndex is " +selectedBestSplitAttributeIndex);
				String selectedBestSplitAttributeName = m_attributeNamesList.get(selectedBestSplitAttributeIndex);
				AttrType selectAttributeType=m_attrTypesMapTable.get(selectedBestSplitAttributeName);
				log.debug("split attributeName for Node "+currentNode.getNodeId()+" is " + selectedBestSplitAttributeName +"   index in attributeNames is: "+ selectedBestSplitAttributeIndex+" attribute type: "+ selectAttributeType);
				// set the test split attribute for the node
				currentNode.setSplitAttributeName(selectedBestSplitAttributeName); // 记录当前节点以某种属性划分
				currentNode.setSplitAttributeIndex(selectedBestSplitAttributeIndex);
				currentNode.setTag(DataType.SplitAttributeName);// 标记当前节点为属性节点
				// add the child node to the queue
//				若一个属性一旦在某个节点出现，那它就不能再出现在该节点之后所
//				产生的子树节点中。
				int lastNodeId =enqueueId;
				if(selectAttributeType==AttrType.Category){
//					1. the split attribute category
					log.debug("the currentNode "+currentNode.getNodeId() +" select attribute "+selectedBestSplitAttributeName +" is category" );
				// 获得划分属性下的每种属性值
				Set<String> attributeValueSet = m_attributeValueDistributionOfEveryAttributeTable.get(
						selectedBestSplitAttributeName).keySet();
				currentNode.setChildDataNodeNum(attributeValueSet.size());
				/** update candidateAttributeIndexes for currentNode's child nodes ,each child has the same candidate  attribute indexes */
				List<Integer> candidateAttributeIndexes = updateCandidateAttributeIndexes(
						currentNode.getCandidateSplitAttributeNameIndexes(), selectedBestSplitAttributeIndex);
				// 在结点下加新的分支
				// 对划分属性下的每种属性值划分数据集,each attribute value form a dataset for child node 
				String mostCommonClassValueInSplitNode  = mostCommonTargetAttribute(currentDatas);
				for (String attributeValueInSplitAttribute : attributeValueSet) {
					// 获得以某种属性的属性值过滤的数据集
					List<Data> dataList = getDatasByAttributeValue(currentDatas,
							selectedBestSplitAttributeIndex, selectAttributeType, attributeValueInSplitAttribute)
							.getValue();
					 lastNodeId = enqueueId;
					DataNode newChildNode  = new DataNode(lastNodeId+1);
					currentNode.getChildDataNodesIds().add(lastNodeId+1);
					if(dataList==null||dataList.size()<=0||dataList.isEmpty()){
						// the child node dataset is empty, then class most common class value in parentNode
						newChildNode.setCandidateSplitAttributeNameIndexes(candidateAttributeIndexes);
						newChildNode.setSplitAttributeName(mostCommonClassValueInSplitNode);
						newChildNode.setSplitAttributeIndex(getClassValueIndexByValue((mostCommonClassValueInSplitNode)));
						newChildNode.setAttributevalueSplit(attributeValueInSplitAttribute);
						newChildNode.setTag(DataType.ClassValue);
						// newChildNode=new DataNode(candidateAttributeIndexes, 0, null, null, lastNodeId+1, null, 0, m_classLabelValues.indexOf(mostCommonClassValueInSplitNode), mostCommonClassValueInSplitNode, attributeValueInSplitAttribute, DataType.ClassValue);
					}
					else{
					currentDatas.removeAll(dataList); // remove the selected ids
//					newChildNode = new DataNode(candidateAttributeIndexes, m_childDataNodeNum, m_childDataNodes, m_classNumDistribution, m_nodeId, m_recordIds, m_recordsNum, m_splitAttributeIndex, m_splitAttributeName, m_splitAttributeValue, m_tag)
					newChildNode = new DataNode();
					newChildNode.setNodeId(lastNodeId+1);
					newChildNode.setCandidateSplitAttributeNameIndexes(candidateAttributeIndexes); 
					newChildNode.setRecordIds(getIDsOfDataList(dataList)); //get the ids collection of examples
					newChildNode.setRecordsNum(dataList.size()); 
					newChildNode.setAttributevalueSplit(attributeValueInSplitAttribute); // 设置子节点根据某个数据的某个属性值划分节点
					// add this new childNode to currentNode's childNodes
					currentNode.addChildDataNodeToDataNode(newChildNode);
					currentNode.setTag(DataType.SplitAttributeName);
					}//else
				    	deque.add(newChildNode); // child node enque
				    	enqueueId++; // node id +1
					}// for
					log.debug("the currentNode "+currentNode.getNodeId() +"has "+attributeValueSet.size()+" children" );
				}
				else{
					// 2.  for continuous attribute
				currentNode.setChildDataNodeNum(2);
				// TO do : error
				BestSplitPointForContinuousAttribute bestSplitPointForContinuousAttribute = getBestSplitPointForContinousAttribute(currentDatas, selectedBestSplitAttributeIndex,Parameter.attributeVal);
				List<Integer> candidateAttributeIndexes = updateCandidateAttributeIndexes(
						currentNode.getCandidateSplitAttributeNameIndexes(), selectedBestSplitAttributeIndex);
				DataNode leftChildNode = new DataNode(lastNodeId+1);
				Entry<Integer, List<Data>> preDatasEntry= getDatasByAttributeValue(currentDatas, selectedBestSplitAttributeIndex,
						selectAttributeType,bestSplitPointForContinuousAttribute.getSplitAttributeValue()+"");
				List<Integer> preDataIds=	(List<Integer>) getIDsOfDataList(preDatasEntry.getValue());
				leftChildNode.setAttributevalueSplit("<="+bestSplitPointForContinuousAttribute.getSplitAttributeValue()+"");
				leftChildNode.setRecordIds(preDataIds);
				leftChildNode.setRecordsNum(preDataIds.size());
				leftChildNode.setCandidateSplitAttributeNameIndexes(candidateAttributeIndexes);// 下层节点中将该属性移除)
//				new DataNode(m_candidateSplitAttributeNameIndexes, m_childDataNodeNum, m_childDataNodes, m_classNumDistribution, m_nodeId, m_recordIds, m_recordsNum, m_splitAttributeIndex, m_splitAttributeName, m_splitAttributeValue, m_tag)
//				DataNode leftChildNode=new DataNode(candidateAttributeIndexes, 0, null, null, enqueueId+1, preDataIds, preDataIds.size(), selectedBestSplitAttributeIndex, selectedBestSplitAttributeName, "<="+bestSplitPointForContinuousAttribute.getSplitAttributeValue(),DataType.UnKown);
				deque.add(leftChildNode);
				enqueueId++;
				
				List<Integer> currDataIds=(List<Integer>) getIDsOfDataList(currentDatas);
				currDataIds.removeAll(preDataIds);
				DataNode rDataNode=new DataNode(enqueueId+1);
				rDataNode.setRecordIds(currDataIds);
				rDataNode.setRecordsNum(currDataIds.size());
				rDataNode.setCandidateSplitAttributeNameIndexes(candidateAttributeIndexes);// 下层节点中将该父节点属性移除)
				rDataNode.setAttributevalueSplit(">"+bestSplitPointForContinuousAttribute.getSplitAttributeValue()+"");
//				DataNode rDataNode=new DataNode(candidateAttributeIndexes, 0, null, null, enqueueId+1, currDataIds, currDataIds.size(), selectedBestSplitAttributeIndex, selectedBestSplitAttributeName, ">"+bestSplitPointForContinuousAttribute.getSplitAttributeValue(),DataType.UnKown);
//				new DataNode(m_candidateSplitAttributeNameIndexes, m_childDataNodeNum, m_childDataNodes, m_classNumDistribution, m_nodeId, m_recordIds, m_recordsNum, m_splitAttributeIndex, m_splitAttributeName, m_splitAttributeValue, m_tag)
				deque.add(rDataNode);
				currentNode.addChildDataNodeToDataNode(leftChildNode);
				currentNode.addChildDataNodeToDataNode(rDataNode);
				enqueueId++;
				}
			}
		}//while
		m_totalNodesSize = enqueueId;
		return RootNode;
	}
	

	/**
	 * remove the selectedBestSplitAttributeIndex from candidateAttributeIndexes to Update candidateAttributeIndexes </br>
	 * @param candidateAttributeIndexes the parent node's candidateAttributeIndexes
	 * @param selectedBestSplitAttributeIndex
	 * @return
	 */
	private List<Integer> updateCandidateAttributeIndexes(List<Integer> candidateAttributeIndexes,
			int selectedBestSplitAttributeIndex) {
		assert(candidateAttributeIndexes.size()>0);
		List<Integer> copyCandidateAttributeIndexesList =new LinkedList<Integer>();
		copyCandidateAttributeIndexesList.clear();
		copyCandidateAttributeIndexesList.addAll(candidateAttributeIndexes);
		copyCandidateAttributeIndexesList.remove(new Integer(selectedBestSplitAttributeIndex)); //remove the splitAttribute for child TreeNodes
		return copyCandidateAttributeIndexesList;
	}
	
	
	/**
	 * root recordId is 1
	 * @param examples
	 * @param attributeIndexs
	 * @param parentNodeId
	 * @return
	 */
	public DataNode createNodeInDFS(List<Data> examples,
			List<Integer> attributeIndexs, int lastNodeId, int level,
			String nodeName, String nodevalue) {
		DataNode currentNode = new DataNode();
		currentNode.setRecordIds(getIDsOfDataList(examples));
		currentNode.setNodeId(lastNodeId + 1);
		m_LeafNodesSize=0;
		if (isSameClass(examples)) // 叶子节点
		{
			currentNode.setChildDataNodeNum(0);
			currentNode.setTag(DataType.ClassValue);
			currentNode.setSplitAttributeName(examples.get(0).getClassValue());
			level++;
			m_LeafNodesSize++;
			return currentNode;
		}

		if (attributeIndexs.isEmpty()) {
			currentNode.setChildDataNodeNum(0);
			currentNode.setTag(DataType.ClassValue);
			currentNode.setSplitAttributeName(mostCommonTargetAttribute(examples));
			level++;
			m_LeafNodesSize++;
			return currentNode;
		}
		//stopCretia 
		if (examples.size() <= Parameter.MinDataSizeInLeafNode
				|| level > Parameter.MaxTreeHeight) {
			currentNode.setChildDataNodeNum(0);
			currentNode.setTag(DataType.ClassValue);
			currentNode.setSplitAttributeName(mostCommonTargetAttribute(examples));
			m_LeafNodesSize++;
			return currentNode;
		}
		// find the max gain attributeIndex and attributeName
		int selectedBestSplitAttributeIndex = getBestSplitAttributeIndex(examples, attributeIndexs,Parameter.attributeVal);
		String selectedBestSplitAttributeName = m_attributeNamesList.get(selectedBestSplitAttributeIndex);
		AttrType selectedBestAttrType = m_attrTypesMapTable.get(selectedBestSplitAttributeName);
		log.debug("select attributeName is " + selectedBestSplitAttributeName +"   index in attributeNames is: "+ selectedBestSplitAttributeIndex+"type is "+selectedBestAttrType);
		currentNode.setSplitAttributeName(selectedBestSplitAttributeName); // 记录当前节点以某种属性划分
		currentNode.setTag(DataType.SplitAttributeName);// 标记当前节点为属性节点
		attributeIndexs.remove(new Integer(selectedBestSplitAttributeIndex));// 下层节点中将该属性移除
		// 获得划分属性下的每种属性值
		Set<String> attributeValueSet = m_attributeValueDistributionOfEveryAttributeTable.get(
				selectedBestSplitAttributeName).keySet();
		currentNode.setChildDataNodeNum(attributeValueSet.size());
		// 在结点下加新的分支
		// 对划分属性下的每种属性值划分数据集
		int attributeValueIndex = 0;
		for (String attributeValueInSplitAttribute : attributeValueSet) {
			// 获得以某种属性的属性值过滤的数据集
			List<Data> dataList = getDatasByAttributeValue(examples,
					selectedBestSplitAttributeIndex,selectedBestAttrType, attributeValueInSplitAttribute)
					.getValue();
			// 获得过滤后的数据集的数量
			int count = getDatasByAttributeValue(examples, selectedBestSplitAttributeIndex,selectedBestAttrType,
					attributeValueInSplitAttribute).getKey();
			int lastId = getMaxTreeNodeId(attributeValueIndex, currentNode);
			DataNode childNode = createNodeInDFS(dataList, attributeIndexs,
					lastId, level + 1, selectedBestSplitAttributeName,
					attributeValueInSplitAttribute);
			childNode.setAttributevalueSplit(attributeValueInSplitAttribute); // 设置子节点根据某个数据的某个属性值划分节点
			List<DataNode> childDataNodes = null;
			if (currentNode.getChildDataNodes() != null) {
				childDataNodes = currentNode.getChildDataNodes();
				childDataNodes.add(childNode);
			} else {
				childDataNodes = new LinkedList<DataNode>();
				childDataNodes.add(childNode);
				currentNode.setChildDataNodes(childDataNodes);
			}
			attributeValueIndex++;
		}// for
		return currentNode;
	}

	public int getMaxTreeNodeId(int attributeValueIndex,DataNode currentNode){
		int lastId = Integer.MIN_VALUE;
		if(attributeValueIndex==0){
			lastId = currentNode.getNodeId();
		}else{
			DataNode rightChildDataNode = currentNode.getChildDataNodes().get(attributeValueIndex-1); //最近的右孩子节点
			DataNode iNode=rightChildDataNode;
			while(iNode.getChildDataNodeNum()>0){
				iNode=	iNode.getChildDataNodes().get(iNode.getChildDataNodeNum()-1);
			}
			lastId = iNode.getNodeId();
		}
		return lastId;
	}

	/**
	 * build tree classifier
	 * @param examples
	 * @param attributeIndexs
	 * @param rootNodeId  if rootNodeId=1 then node id start from 1
	 * @return
	 */
	private DataNode buildTree(List<Data> examples,
			List<Integer> attributeIndexs, int rootNodeId) {
		List<Integer> attributesList = new LinkedList<Integer>(getAttributeIndexs());
		// 创建树的结点
//		rootNode = createNodeInDFS(examples, attributeIndexs, rootNodeId, rootNodeId,
//				"DecisionTree", "null");
		m_rootNode = createNodeInBFS(examples, attributeIndexs, rootNodeId);
		return m_rootNode;
	}


	// public void getRules(){
	// if(rootNode.getChildDataNodeNum()<=0){ //leaf node
	//log.debug(path); // print out the path from root to leaf
	// }else{
	// List<DataNode> chilDataNodes = rootNode.getChildDataNodes();
	// for (DataNode dataNode : chilDataNodes) {
	// if(dataNode!=null){
	// if(rootNode.getAttributevalueSplit()==""||rootNode.getAttributevalueSplit()==null){
	// //root node
	// printPath(dataNode,path+rootNode.getAttributeName()+"( "
	// +dataNode.getAttributevalueSplit()+ " )"+dataNode.getAttributeName());
	// }else{
	// //not root node
	// printPath(dataNode,path+"( " +dataNode.getAttributevalueSplit()+
	// " )"+dataNode.getAttributeName());
	// }
	// }
	// }
	// }
	// }



	public Set<String> getAttributeValue(String attributeName) {
		Hashtable<String, Integer> attributeValueNumDictionary = m_attributeValueDistributionOfEveryAttributeTable
				.get(attributeName);
		return attributeValueNumDictionary.keySet();
	}


	@Override
	public String getClassicationResult() {
		return null;
	}

	/**
	 * Task : 获得训练数据的属性个数
	 * @return
	 */
	public int getTrainingDataAttributeNum() {
		return m_traingData.getDataList().get(0).getAttributeNum();
	}

	public int getTreeNodeSize() {
		int size = -1;
		return size;
	}
	

	/**
	 * print the path from root node to leaf node
	 * 
	 * @param rootNode
	 * @param path
	 */
	public void printPath(DataNode rootNode, String path) {
		if (rootNode.getChildDataNodeNum() <= 0) { // leaf node
			log.debug(path); // print out the path from root to leaf
		} else {
			List<DataNode> chilDataNodes = rootNode.getChildDataNodes();
			for (DataNode dataNode : chilDataNodes) {
				if (dataNode != null) {
					if (rootNode.getSplitAttributevalueSplit() == ""
							|| rootNode.getSplitAttributevalueSplit() == null) {
						// root node
						printPath(dataNode, path + rootNode.getSplitAttributeName()
								+ "( " + dataNode.getSplitAttributevalueSplit()
								+ " )" + dataNode.getSplitAttributeName());
					} else {
						// not root node
						printPath(dataNode,
								path + "( " + dataNode.getSplitAttributevalueSplit()
										+ " )" + dataNode.getSplitAttributeName());
					}
				}
			}
		}
	}


	/**
	 * read arff File and initiate trainData, attribute,attributevalue
	 * process each line
	 * @author apple
	 */
	protected DataSet readARFF(String filePath) {
		// 属性值集合，<AttributeName <attributeValue , num> >
		List<Data> dataList = new ArrayList<Data>();
		try{
		FileReader fr=new FileReader(FileUtil.getFilePath(filePath));
			BufferedReader br = new BufferedReader(fr);
			String line;
			Pattern pattern = Pattern.compile(Parameter.attributePatternString);
			int record = 1;
			int readAttributeNum = 0;
//			int lineNo=0;
			while ((line = br.readLine()) != null) {
				// read and process each line
//				lineNo++;
				String lineWithoutSpace = Tool.removeReduntSpace(line).toLowerCase();
				if(lineWithoutSpace.equalsIgnoreCase("")){
					continue;
				}
//				log.debug(lineNo+" : "+ lineWithoutSpace);
				Matcher matcher = pattern.matcher(lineWithoutSpace);
				if (lineWithoutSpace.startsWith("#")||lineWithoutSpace.startsWith("%")) {
					// notation line skip
					continue;
				}
				if (matcher.find()) {
					// init the attributeName, attributeType,attrTypesMapTable
					String attributeName = matcher.group(1).trim().toLowerCase();
					m_attributeNamesList.add(attributeName); // add attributeName it includes the class attribute
					//the m_attributeNamesList are the same order as the order in arff datafile 
					// get the attributevalue for each attributeName
					AttrType attrType = (matcher.group(3).trim().equalsIgnoreCase(AttrType.Continuous.toString())|| matcher.group(3).trim().equalsIgnoreCase("numeric") ) ? AttrType.Continuous: AttrType.Category;
					readAttributeNum++; // the attributeNum
					m_attrTypesMapTable.put(attributeName, attrType);
					log.debug("attribute :" +attributeName + " " +attrType );
					continue;
				} else if (lineWithoutSpace.startsWith("@data")) {
					// init the classAttributeName, attributeNamesList, attrTypesmap
					m_classAttributeName =  m_attributeNamesList.get(m_attributeNamesList.size()-1);
					m_attributeNamesList.remove(m_attributeNamesList.size()-1);
					m_attrTypesMapTable.remove(m_classAttributeName);//remove the class attribute from attrTypesMapTable
					readAttributeNum--;
					String lineString = br.readLine();
					while( lineString!=null)  {
						while (DataConverter.skipLine(lineString)) {
							lineString=br.readLine();
						}
						if(lineString==null) break;
						// get the targetAttributeClassDistribution
						Data data = DataConverter.convert(record, lineString);
						if(data!=null){
						dataList.add(data); record++;
						addDataClassValueIntoClassLableValuesMap(data, m_classLabelValuesMap);
						addDataAttributeValuesToAttributeValueDistributionOfEveryAttributeTable(data, m_attributeValueDistributionOfEveryAttributeTable);
						}
						lineString = br.readLine();
						if(lineString==null) break;
					}//while
				}else {
						continue;
						}
			}
			br.close();
			setAttributeNum(readAttributeNum);
			// the order same as datafile attribute lines
			m_traingData.setAttributeNamesList(m_attributeNamesList);
			// the attribute oder can be different the order in m_attributeNamesList
			m_traingData.setAttributeTypeTable(getAttrTypesMap(m_attrTypesMapTable));
			m_traingData.setDatasetSize(record-1);
			m_traingData.setAttributeNum(m_attributeNum);
			m_traingData.setAttributeNamesList(m_attributeNamesList);
			m_traingData.setClassValueMap(m_classLabelValuesMap);
			m_traingData.setDataList(dataList);
			m_traingData.setClassNum(m_classLabelValuesMap.size());
			m_traingData.setClassName(m_classAttributeName);
			m_traingData.setClassValuesMap(m_classLabelValuesMap);
			setClassNum(m_classLabelValuesMap.size()); 
			//the classValues has the same order as classValueDistributionTable
			m_traingData.setClassValues(m_classLabelValuesMap.keySet());
			initClassIndexesAndAttributeIndexes();
			log.debug("the dataSet size: "+(record-1) );
			log.debug(this.toString());
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}catch (Exception e) {
		e.printStackTrace();
		}
		return m_traingData;
	}
	
	/**
	 * add a row of data's attribute values into the targetAttributeDistributionTable (classValue ,num)
	 * @param dataClassValue
	 * @param targetAttributeDistributionTable  (classValue ,num)
	 */
	public void addDataClassValueIntoClassLableValuesMap(String dataClassValue,Map<String, Integer> classLableValuesmap){
	if (classLableValuesmap.get(dataClassValue) == null||classLableValuesmap.get(dataClassValue)==0) {
		classLableValuesmap.put(dataClassValue, 1);
	} else {
		int oldvalue = classLableValuesmap.get(dataClassValue);
		classLableValuesmap.put(dataClassValue, oldvalue + 1);
		}
	}

	/**
	 * add a row of data's attribute values into the targetAttributeDistributionTable (classValue ,num)
	 * @param dataClassValue
	 * @param targetAttributeDistributionTable  (classValue ,num)
	 */
	public void addDataClassValueIntoClassLableValuesMap(Data data,Map<String, Integer> classLableValuesmap){
		addDataClassValueIntoClassLableValuesMap(data.getClassValue(),classLableValuesmap);
	}

	
	
	
	/**
	 * get the NumOfEachattributeValueTable from row data
	 * @param row[] one row of data's attribute values
	 * @param startIndex  the startIndex
	 * @param size the attribute value's num
	 * @param attributeValueDistributionOfEveryAttributeTable (attributeName,attributeValue ,num)
	 * @author apple
	 */
	public Hashtable<String, Integer> addDataAttributeValuesToAttributeValueDistributionOfEveryAttributeTable (String[] row,int startIndex ,int size,
			Hashtable<String, Hashtable<String, Integer>> attributeValueDistributionOfEveryAttributeTable){
		// 属性值集合，<AttributeName <attributeValue , num> >
				Hashtable<String, Integer> NumOfEachattributeValueTable = new Hashtable<String, Integer>();
		// get every attribute value
		for (int i = 0; i < size; i++) {
			String dataAttributeValue = row[startIndex+i];
			String attributeName = m_attributeNamesList.get(i);
			if (attributeValueDistributionOfEveryAttributeTable.get(attributeName)== null) {
				// 属性值集合，<AttributeName <attributeValue , num> >
				if (NumOfEachattributeValueTable.size() == 0) {
					NumOfEachattributeValueTable.put(dataAttributeValue, 1);
					attributeValueDistributionOfEveryAttributeTable.put(attributeName,
							NumOfEachattributeValueTable);
				} else {
					Hashtable<String, Integer> newAttributeValueNum = new Hashtable<String, Integer>();
					newAttributeValueNum.put(dataAttributeValue, 1);
					attributeValueDistributionOfEveryAttributeTable.put(attributeName,
							newAttributeValueNum);
				}
			} else {
				NumOfEachattributeValueTable = (Hashtable<String, Integer>) attributeValueDistributionOfEveryAttributeTable
						.get(attributeName);
				if (NumOfEachattributeValueTable.get(dataAttributeValue) == null) {
					NumOfEachattributeValueTable.put(dataAttributeValue, 1);
				} else {
					int oldnum = (Integer) NumOfEachattributeValueTable
							.get(dataAttributeValue);
					NumOfEachattributeValueTable.put(dataAttributeValue,
							new Integer(oldnum + 1) ) ;
				}
				attributeValueDistributionOfEveryAttributeTable.put(attributeName,
						NumOfEachattributeValueTable);
			}
		}//for
		return NumOfEachattributeValueTable;
	}
	
	
	
	/**
	 * get the NumOfEachattributeValueTable from row data
	 * @param row[] one row of data's attribute values
	 * @param startIndex  the startIndex
	 * @param size the attribute value's num
	 * @param attributeValueDistributionOfEveryAttributeTable (attributeName,attributeValue ,num)
	 * @author apple
	 */
	public Hashtable<String, Integer> addDataAttributeValuesToAttributeValueDistributionOfEveryAttributeTable (Data data,
			Hashtable<String, Hashtable<String, Integer>> attributeValueDistributionOfEveryAttributeTable){
		// 属性值集合，<AttributeName <attributeValue , num> >
		Hashtable<String, Integer> NumOfEachattributeValueTable = new Hashtable<String, Integer>();
		// get every attribute value
		for (int i = 0; i < data.getAttributeNum(); i++) {
			String dataAttributeValue = data.getAttributevaluesList().get(i);
			String attributeName = m_attributeNamesList.get(i);
			if (attributeValueDistributionOfEveryAttributeTable.get(attributeName)== null) {
				// 属性值集合，<AttributeName <attributeValue , num> >
				if (NumOfEachattributeValueTable.size() == 0) {
					NumOfEachattributeValueTable.put(dataAttributeValue, 1);
					attributeValueDistributionOfEveryAttributeTable.put(attributeName,
							NumOfEachattributeValueTable);
				} else {
					Hashtable<String, Integer> newAttributeValueNum = new Hashtable<String, Integer>();
					newAttributeValueNum.put(dataAttributeValue, 1);
					attributeValueDistributionOfEveryAttributeTable.put(attributeName,
							newAttributeValueNum);
				}
			} else {
				NumOfEachattributeValueTable = (Hashtable<String, Integer>) attributeValueDistributionOfEveryAttributeTable
						.get(attributeName);
				if (NumOfEachattributeValueTable.get(dataAttributeValue) == null) {
					NumOfEachattributeValueTable.put(dataAttributeValue, 1);
				} else {
					int oldnum = (Integer) NumOfEachattributeValueTable
							.get(dataAttributeValue);
					NumOfEachattributeValueTable.put(dataAttributeValue,
							new Integer(oldnum + 1) ) ;
				}
				attributeValueDistributionOfEveryAttributeTable.put(attributeName,
						NumOfEachattributeValueTable);
			}
		}//for
		return NumOfEachattributeValueTable;
	}
	
	@Override
	/**
	 * read Data from file and init the trainData of Decision Tree
	 * @author Administrator
	 * @param fileName
	 */
	protected DataSet readData(String fileName) {
		return null;
//		assert(fileName!=null&&fileName!="");
//		log.debug("reading sample data from " + fileName);
//		Scanner  scanner = FileUtil.getScanner(fileName);
//		if(scanner==null){
//			log.error("scanner is null");
//		}
//		int line=0;
//		String dataPatternString="@attribute(.*)[ +](.*)";
//		Pattern pattern = Pattern.compile(dataPatternString);
////		// 属性值集合，<AttributeName <attributeValue , num> >
//		Hashtable<String, Integer> attributeValueNumDictionay = new Hashtable<String, Integer>();
//		List<Data> trainList=new ArrayList<Data>();
//		m_attributeNum = 0;
//	 //recordId start from 1
//		while(scanner.hasNextLine()){
//		String	dataLineWithoutSpace = scanner.nextLine().trim().replaceAll("\\s", " ").replaceAll(" +", " ").toLowerCase();
//			if (dataLineWithoutSpace.startsWith("#")||dataLineWithoutSpace.equals("")) {
//				// notation
//				continue;
//			}
//			Matcher matcher = pattern.matcher(dataLineWithoutSpace);
//			if(matcher.find()){
//				//attribute types
//			String attributeName=	matcher.group(1).trim().toLowerCase();
//			String attrVal=matcher.group(2).trim();
//			if(attrVal.equalsIgnoreCase("continuous")){
//			m_attrTypesMapTable.put(attributeName, AttrType.Continuous);
//			}else if(attrVal.equalsIgnoreCase("category")){
//				m_attrTypesMapTable.put(attributeName, AttrType.Category);
//			}else{
//				log.error("attribute type error ");
//			}
//			continue;
//			}
//			String[] linelist = (String[]) dataLineWithoutSpace.split(":");
//			String dataClassValue =  linelist[0].trim().toLowerCase();
//			List<String> valuesList =  Arrays.asList((String[]) Tool.split(linelist[1].trim(), " +", 0));
//			m_attributeNum = valuesList.size();
//			if(line==0){ // process data Attribute name and Label name
//				// get the first line then get the TargetAttribute, attributeName
//			m_classAttributeName =dataClassValue;
//			m_attributeNamesList = valuesList;
//			}else{
//				//process data attribute value
//				for(int i=0;i<valuesList.size();i++){
//					String attributeValue=valuesList.get(i);
//					String attributeName = m_attributeNamesList.get(i);
//					//init the attributeValueDistributionOfEveryAttributeTable
//					if (m_attributeValueDistributionOfEveryAttributeTable.get(attributeName)== null) {
//						// 属性值集合，<AttributeName <attributeValue , num> >
//						if (attributeValueNumDictionay.size() == 0) {
//							attributeValueNumDictionay.put(attributeValue, 1);
//							m_attributeValueDistributionOfEveryAttributeTable.put(attributeName,
//									attributeValueNumDictionay);
//						} else {
//							Hashtable<String, Integer> newAttributeValueNum = new Hashtable<String, Integer>();
//							newAttributeValueNum.put(attributeValue, 1);
//							m_attributeValueDistributionOfEveryAttributeTable.put(attributeName,
//									newAttributeValueNum);
//						}
//					} else {
//						attributeValueNumDictionay = (Hashtable<String, Integer>) m_attributeValueDistributionOfEveryAttributeTable
//								.get(attributeName);
//						if (attributeValueNumDictionay.get(attributeValue) == null) {
//							attributeValueNumDictionay.put(attributeValue, 1);
//						} else {
//							int oldnum = (Integer) attributeValueNumDictionay
//									.get(attributeValue);
//							attributeValueNumDictionay.put(attributeValue,
//									oldnum + 1);
//						}
//						m_attributeValueDistributionOfEveryAttributeTable.put(attributeName,
//								attributeValueNumDictionay);
//					}
//				} //for
//				addDataClassValueIntoClassAttributeDistributitonTable(data, targetAttributeDistributionTable)
////				if (m_traingData.getClassValueMap().get(dataClassValue) == null||m_traingData.getClassValueMap().get(dataClassValue) == 0) {
////					m_traingData.getClassValueDistributionTable().put(dataClassValue, 1);
////				} else {
////					int oldvalue = m_traingData.getClassValueDistributionTable().get(dataClassValue);
////					m_traingData.getClassValueDistributionTable().put(dataClassValue, oldvalue + 1);
////				}
////				int dataClassValueIndex = getClassValueIndex(dataClassValue);
//				Data data = new Data(line,dataClassValue,valuesList); //record id start from 1
//				trainList.add(data);
//				
//			}
//			line++;
//		}//while 
//		// process over
//		initClassIndexesAndAttributeIndexes();
//		m_traingData.setClassValues(m_classLabelValues);
//		m_traingData.setAttributeNamesList(m_attributeNamesList);
//		m_traingData.setAttributeNum(m_attributeNum);
//		m_traingData.setDatasetSize(line-1);
//		m_traingData.setDataList(trainList);
//		m_traingData.setClassNum(m_classLabelValues.size());
//		return m_traingData;
		}

	/**
	 * init the m_attributeIndexs by 0 ,1,2... m_attributeNum-1 </br>
	 * init the classLabel values and classIndexes </br>
	 * init the attributeIndexes </br>
	 * @author apple
	 */
	public void initClassIndexesAndAttributeIndexes(){
		int classIndex=0; assert(m_traingData.m_classNum>0);
		for(int j=0;j<m_classNum;j++){
			m_classIndexs.add(j);
		}
		for(int i=0;i<m_attributeNum;i++){
			m_attributeIndexs.add(i);
		}
		m_traingData.setClassIndexs(m_classIndexs);
	}

	/**
	 * @deprecated
	 * show tree model have been build
	 * @author Administrator
	 */
	private void showTree(DataNode node, int level) {
		
		if (node.getChildDataNodes() == null || node.getChildDataNodeNum() == 0||node.getTag()==DataType.ClassValue) {
			//leaf node
//			System.out.println(" the node "+node +"is null ");
			System.out.println(node.getSplitAttributeName());
			return;
		}
		List<DataNode> childDataNodes = node.getChildDataNodes().subList(0,
				node.getChildDataNodeNum());
		for (int j = 1; j != level; ++j) {
			System.out.print("\t");
		}
		System.out.println(node.getSplitAttributeName());
		for (int i = 0; i < node.getChildDataNodeNum(); i++) {
			DataNode d = childDataNodes.get(i);
			for (int j = 0; j != level; ++j) {
				System.out.print("\t");
			}
			System.out.print(d.getSplitAttributevalueSplit() + " ->");
			showTree(d, level + 1);
		}
	}
	
	
	  /**
	   * Outputs a tree at a certain level.
	   * | 
	   * @param level the level at which the tree is to be printed <b>level : start from 0 </b>
	   * @return the tree as string at the given level
	   */
	  private String showTreeByLevel(DataNode node,int level) {
	    StringBuffer text = new StringBuffer();
	    if(node==null){
	    	log.error("node is null!");
	    	return null;
	    }
	    if(node.getChildDataNodes() == null || node.getChildDataNodeNum() == 0||node.getTag()==DataType.ClassValue){
	    	text.append(":"+node.getSplitAttributeName());
	    }else{
	        for (int j = 0; j < node.getChildDataNodeNum(); j++) {
		        text.append("\n");
		        for (int i = 0; i < level; i++) {
		          text.append("|  ");
		        }
		        DataNode childNode= node.getChildDataNodes().get(j);
		        text.append(node.getSplitAttributeName() + " = " + childNode.getSplitAttributevalueSplit());
		        text.append(showTreeByLevel(childNode,level + 1));
		      }
	    }
	    return text.toString();
	  }

	

	@Override
	public String showClassifier() {
		log.debug( showTreeByLevel(m_rootNode, 0) );
		return showTreeByLevel(m_rootNode, 0);
//		System.out.println("---------");
//		showTree(m_rootNode, 1) ;
	}

	@Override
	public String toString() {
//		return "EntropyDecisionTree [traingDataList=" + traingData.getTrainingDatas()
//				+ ", trainingDataNum=" + traingData.getTrainingDataNum()
//				+ ", attributeNamesList=" + attributeNamesList
//				+ ", targetAttributeName=" + targetAttributeName
//				+ ", attributeNum="
//				+ attributeNum + "]";
		
//		return new ToStringBuilder(this).
//			       append("name", name).
//			       append("age", age).
//			       append("smoker", smoker).
//			       toString();
//			   }
		return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public Classifier buildClassifierOverrite(DataSet dataSet) {
		if(Parameter.deleteMissingValue){
			m_traingData.deleteWithMissing();
		}
		List<Integer> attributesList = new LinkedList<Integer>(getAttributeIndexs());
		m_rootNode =	buildTree(dataSet.getDataList(),attributesList, 1);
		if(m_rootNode==null){
			return null;
		}
		return this;
	}


	
	@Override
	/**
	 * get the list of DataNodes of the tree
	 */
	public List<DataNode> getTreeDataNodesList() {
		List<DataNode> treeNodesArrayList = new ArrayList<DataNode>(m_totalNodesSize);
		Deque<DataNode> dequee = new LinkedList<DataNode>();
		DataNode tagNode = new DataNode(-1); // tag node for level
		dequee.add(m_rootNode);  //init the root node enqueue
		dequee.add(tagNode); // add the tag node
		while ((!dequee.isEmpty())&& (dequee.peek()!=null)) {
			DataNode currentNode = dequee.poll();
			log.debug(currentNode.getNodeId()+"");
			log.info(currentNode.toString());
			if(currentNode.equals(tagNode)){
				//tag node to note the level
				if((!dequee.isEmpty())||dequee.size()>0){
					dequee.add(tagNode);
					continue;
				}else{
					// over
//					log.info(treeNodesArrayList)
					return treeNodesArrayList;
				}
			}
			treeNodesArrayList.add(currentNode);
			if(currentNode.getChildDataNodeNum()>0){
				List<Integer> childIdsIntegers = currentNode.getChildDataNodesIds();
				List<DataNode> childNodes = currentNode.getChildDataNodes();
			dequee.addAll(childNodes);
			}
		}
		return treeNodesArrayList;

	}

	@Override
	@SuppressWarnings("finally")
	public String writeTreeNodesToFile(File file){
		List<DataNode> dataNodesList = getTreeDataNodesList();
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new File("/Users/apple/Desktop/a.txt"));
			for (DataNode dataNode : dataNodesList) {
				System.out.println(dataNode.getNodeId());
				printWriter.println(dataNode.dataNodeString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(printWriter!=null)
			printWriter.close();
			return file.getAbsolutePath();
		}
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public int getClassValueIndexByValue(String classValue) {
		int index=0;
		 for (Iterator<String> it =  m_classLabelValuesMap.keySet().iterator();it.hasNext();index++)
		   {
			 String key = it.next();
			 if(key .equalsIgnoreCase(classValue)){
				log.trace( key+"="+ m_classLabelValuesMap.get(key));
				 return index;
			 }
			 continue;
		   }
		 log.error("can't find the classValue {} in m_classLabelValuesMap",classValue);
		 return -1;
	}

	@Override
	public int getAttributeNameIndexByValue(String attributeName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AttrType getTypeOfAttribute(int attributeNameIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DataSet readARFFMR(String filePath) {
	 return null;
	}

	@Override
	protected DataSet readDataMR(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}


}
