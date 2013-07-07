package com.younger.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.io.Writable;


/**
 *  the treeNode 
 * @author apple
 *
 */
public class DataNode implements Serializable ,Writable{
	
	/**
	 * enum class DataType
	 * Two types of dataNode</br>
	 * AttributeName for interior node </br>
	 * ClassValue for leaf node</br>
	 * UnKown for node haven't assiyed
	 * @author apple
	 *
	 */
	public enum DataType{
		UnKown,SplitAttributeName,ClassValue;
		//   MOCKLEAF,
//	    LEAF,
//	    NUMERICAL,
//	    CATEGORICAL
		public boolean isSplitAttributeName(){
			return this==SplitAttributeName;
		}
		public boolean isClassValue(){
			return this==ClassValue;
		}
		public boolean isUnKown(){
			return this==UnKown;
		}
	}

	private static final long serialVersionUID = 1L;
	
//	/** 子结点，结点顺序对应属性值集合中属性值的顺序 */
//	private List<String> m_attributeValues= new ArrayList<String>()  ;
	
	/**
	 * the candidate attributeName indexes for the node 
	 */
	private List<Integer> m_candidateSplitAttributeNameIndexes = new ArrayList<Integer>();
	
	/** the num of children data nodes */
	private int m_childDataNodeNum = 0;;
	
	/** ids od the node's children  */
	private List<Integer> m_childDataNodesIds = new ArrayList<Integer>();
	
	private List<DataNode> m_childDataNodes = new ArrayList<DataNode>(); 
	
	/** the classNumDistribution of dataSet on the treeNode
	 *  type : List<Integer>
	 */
	private List<Integer> m_classNumDistribution = new ArrayList<Integer>();  
	
	/** start from 1 */
	private int m_nodeId=-1;
	
	/** the records in this tree node */
	private Collection<Integer> m_recordIds = new HashSet<Integer>();
	
	/**
	 * the records num of the treeNode , namely, the size of record ids
	 */
	private int m_recordsNum=0;
	
	/**
	 * the split attribute index of split attribute in attribute Indexes
	 */
	private int m_splitAttributeIndex = -1; 
	
	/** 
	 * the node value of treeNode, </p>
	 * if the node is root node of tree , then it's null</p>
	 * if have no children then it's classValue 
	 * otherwise it's split attributeValue on the dataSet on the treeNode 
	 * 
	 * */
	private String m_splitAttributeName; 
	
//	private List<Integer> attributeIndexs = new LinkedList<Integer>();
	
	/** For nominal attribute the split attribute value 
	 *  for continuous attribute the split point value*/
	private String m_splitAttributeValue; 
	
	/**If children is null or empty ，then attribute value is classValue */
	private DataType m_tag= DataType.UnKown; 
	
	
	public DataNode(){
		super();
	}
	
	
	public DataNode(int nodeId){
		super();
		this.m_nodeId=nodeId; 
		Collections.fill(this.m_classNumDistribution,0);
	}
	
	public DataNode(List<Integer> m_candidateSplitAttributeNameIndexes,
			int m_childDataNodeNum, List<DataNode> m_childDataNodes,
			List<Integer> m_classNumDistribution, int m_nodeId,
			Collection<Integer> m_recordIds, int m_recordsNum,
			int m_splitAttributeIndex, String m_splitAttributeName,
			String m_splitAttributeValue, DataType m_tag) {
		super();
		this.m_candidateSplitAttributeNameIndexes = m_candidateSplitAttributeNameIndexes;
		this.m_childDataNodeNum = m_childDataNodeNum;
		this.m_childDataNodes = m_childDataNodes;
		this.m_classNumDistribution = m_classNumDistribution;
		this.m_nodeId = m_nodeId;
		this.m_recordIds = m_recordIds;
		this.m_recordsNum = m_recordsNum;
		this.m_splitAttributeIndex = m_splitAttributeIndex;
		this.m_splitAttributeName = m_splitAttributeName;
		this.m_splitAttributeValue = m_splitAttributeValue;
		this.m_tag = m_tag;
	}

	
	


	public int getSplitAttributeIndex() {
		return m_splitAttributeIndex;
	}
	
	
//	public List<String> getAttributeValues() {
//		return m_attributeValues;
//	}
	
	public String getSplitAttributevalueSplit() {
		return m_splitAttributeValue;
	}
	public int getChildDataNodeNum() {
		return m_childDataNodeNum;
	}
	public List<DataNode> getChildDataNodes() {
		return m_childDataNodes;
	}
	
	public List<Integer> getClassNumDistribution() {
		return m_classNumDistribution;
	}
	public int getNodeId() {
		return m_nodeId;
	}
	public Collection<Integer> getRecordIds() {
		return m_recordIds;
	}
	public String getSplitAttributeName() {
		return m_splitAttributeName;
	}
	public DataType getTag() {
		return m_tag;
	}
	public void setSplitAttributeIndex(int attributeIndex) {
		this.m_splitAttributeIndex = attributeIndex;
	}
	
//	public void setAttributeValues(List<String> attributeValues) {
//		this.m_attributeValues = attributeValues;
//	}
	
	public void setAttributevalueSplit(String attributevalueSplit) {
		this.m_splitAttributeValue = attributevalueSplit;
	}
	
	public List<Integer> getCandidateSplitAttributeNameIndexes() {
		return m_candidateSplitAttributeNameIndexes;
	}

	public void setCandidateSplitAttributeNameIndexes(
			List<Integer> candidateSplitAttributeNameIndexes) {
		this.m_candidateSplitAttributeNameIndexes = candidateSplitAttributeNameIndexes;
	}

	public void setChildDataNodeNum(int childDataNodeNum) {
		this.m_childDataNodeNum = childDataNodeNum;
	}
//	public List<Integer> getAttributeIndexs() {
//		return attributeIndexs;
//	}
//	public void setAttributeIndexs(List<Integer> attributeIndexs) {
//		this.attributeIndexs = attributeIndexs;
//	}
	public void setChildDataNodes(List<DataNode> childDataNodes) {
		this.m_childDataNodes = childDataNodes;
	}
	public void setClassNumDistribution(List<Integer> classNumDistribution) {
		this.m_classNumDistribution = classNumDistribution;
	}
	public void setNodeId(int nodeId) {
		this.m_nodeId = nodeId;
	}
	public void setRecordIds(Collection<Integer> recordIds) {
		this.m_recordIds = recordIds;
	}
	public void setSplitAttributeName(String splitAttributeName) {
		this.m_splitAttributeName = splitAttributeName;
	}

	
	public void setTag(DataType tag) {
		this.m_tag = tag;
	}

	
	public int getRecordsNum() {
		return m_recordsNum;
	}

	public void setRecordsNum(int m_recordsNum) {
		this.m_recordsNum = m_recordsNum;
	}

	@Override
	public String toString() {
//		return dataNodeString();
//		return String.format(Locale.ENGLISH, "DataNode: %d, splitAttrName: %s, splitAttrVal: %s", m_nodeId, m_splitAttributeName,m_splitAttributeValue);
		return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
	}
	
	
	
	public void addChildDataNodeToDataNode(DataNode chilDataNode ){
		m_childDataNodes.add(chilDataNode);
	}
	
	public List<Integer> getChildNodesIds(){
		if(m_childDataNodeNum>0){
		List<Integer> list  = new ArrayList<Integer>(m_childDataNodeNum);
		for (DataNode dataNode :m_childDataNodes) {
			list.add(dataNode.getNodeId());
		}
		return list;
		}
		return null;
	}
	
	
	
	public List<Integer> getChildDataNodesIds() {
		return this.m_childDataNodesIds;
	}


	public void setChildDataNodesIds(List<Integer> m_childDataNodesIds) {
		this.m_childDataNodesIds = m_childDataNodesIds;
	}

	
	 /**
	   * predicts the label for the instance
	   * 
	   * @return -1 if the label cannot be predicted
	   */
	  public int classify(Data data){
		  return -1;
	  }

	/**
	 * same as toString()
	 * @param outputFile
	 */
	public String dataNodeString(){
			StringBuffer sBuffer = new StringBuffer("DataNode [nodeId=");
			sBuffer.append(m_nodeId).append(",tag=").append(m_tag).
			append(",splitAttributeName=").append(m_splitAttributeName).
			append(",splitAttributeValue=").append(m_splitAttributeValue).
			append(",recordsNum=").append(m_recordsNum).
			append(",candidateSplitAttributeNameIndexes=").append(Arrays.toString(m_candidateSplitAttributeNameIndexes.toArray()) ).
			append(",childNum=").append(m_childDataNodeNum).
			append(",classNumDistribution").append(m_classNumDistribution).
			append(",childNodes").append(Arrays.toString(getChildNodesIds().toArray()));
			return sBuffer.toString();
	}


	@Override
	public void write(DataOutput out) throws IOException {
		
		
	}


	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
