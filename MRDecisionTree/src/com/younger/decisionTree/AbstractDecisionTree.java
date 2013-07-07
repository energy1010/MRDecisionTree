package com.younger.decisionTree;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.classifiers.Classifier;
import com.younger.constant.Enum.AttrType;
import com.younger.data.Data;
import com.younger.data.DataNode;
import com.younger.data.DataNode.DataType;
import com.younger.data.DataSet;
import com.younger.eval.Accuracy;
import com.younger.struct.BestBinarySplitForCategoryAttribute;
import com.younger.struct.BestSplitPointForContinuousAttribute;
import com.younger.struct.ContinousHistogram;
import com.younger.tool.Tool;

public abstract class AbstractDecisionTree extends Classifier  {
	
	
	private static final Logger log = LoggerFactory
			.getLogger(AbstractDecisionTree.class);
	/** the attributeIndes for the attributeName */
	protected static List<Integer> m_attributeIndexs = null; 
	
	/**
	 * the name of attributes names  </br>
	 * the order is as same as the order in datafile attribute lines
	 */
	protected static List<String> m_attributeNamesList ;  // attributeName values
	/**
	 * the num of attribute  
	 */
	protected static int m_attributeNum = 0;
	
	protected static int m_classNum = 0;
	/**
	 * attributeName, attributeValue,  num </br>
	 * describe the attributeValue 's num in each attribute name
	 */
	protected static Hashtable<String, Hashtable<String, Integer>> m_attributeValueDistributionOfEveryAttributeTable ;

	/**
	 * (attributeName, attributeType) </br>
	 * Node that: the order in the table usually is not the same as the order in attributeNamesList  
	 */
	protected static Hashtable<String, AttrType> m_attrTypesMapTable ;

	protected static String m_classAttributeName=null;
	
	/** the classValueIndex  , its size is num of class values */
	protected static List<Integer> m_classIndexs = null;
	
	/** the class Label values , its size is num of class values */
//	protected static List<String> m_classLabelValues  = null; 
	
	protected static Map<String, Integer> m_classLabelValuesMap = null;
	
	
	protected static int m_LeafNodesSize= 0 ; 
	
	protected static int m_level= 0 ;
	
	/** the rootNode of the Decision Tree the NodeId start from 1 */
	protected static DataNode m_rootNode = null;
	
	protected static int m_totalNodesSize = 0;
	/**
	 * the training dataSet
	 */
	protected static DataSet m_traingData = null;  
	
	protected static List<Data> m_validDataset = null;
	
	private static final long serialVersionUID = -3062807321652546950L;
	
	/**
	 * classify dataset  return classvalue index for each data 
	 * @param rootNode
	 * @param dataList
	 * @return
	 */
	private List<Integer> classifyDataSet(DataNode rootNode, List<Data> dataList){
		List<Integer> arrayList= new ArrayList<Integer>(dataList.size());
		for(Data d: dataList){
			arrayList.add(classifyInstanceReturnClassvalueIndex(rootNode, d));
		}
		return arrayList;
	}
	


	@Override
	public List<Integer> classifyDataSet(Classifier classifier,
			List<Data> dataList) {
		return classifyDataSet( ((AbstractDecisionTree)classifier).getRootNode(), dataList);
	}



	@Override
	public String classifyInstance(Classifier classifier, Data classifyData) {
		int classValueIndex= classifyInstanceReturnClassValueIndex(classifier,classifyData);
		if(classValueIndex==-1){
			return "none";
		}
		return getClassValueByIndex(classValueIndex);
	}
	

	@Override
	public int classifyInstanceReturnClassValueIndex(Classifier classifier,
			Data classifyData) {
		return classifyInstanceReturnClassvalueIndex( ((AbstractDecisionTree)classifier).getRootNode(), classifyData.getAttributevaluesList());
	}

	@Override
	/**
	 * 
	 */
	public int classifyInstanceReturnClassValueIndex(Classifier classifier,
			List<String> attributeValuesOfData) {
		return classifyInstanceReturnClassvalueIndex( ((AbstractDecisionTree)classifier).getRootNode(), attributeValuesOfData);
	}
	
	
	private int classifyInstanceReturnClassvalueIndex(DataNode node,
			Data data) {
	return classifyInstanceReturnClassvalueIndex(node, data.getAttributevaluesList());
	}


	
	/**
	 * classify data with DataNode
	 * @param node
	 * @param attributeValuesOfData the order should be consistent with the order in attributeNamesList,namely the order in the data file
	 * @return
	 */
	private int classifyInstanceReturnClassvalueIndex(DataNode node,
			List<String> attributeValuesOfData) {
	int targetClassValueIndex = -1;
	if (node.getTag() != null) {
		if (node.getTag().equals(DataType.ClassValue)) {
			targetClassValueIndex = getClassValueIndexByValue(node.getSplitAttributeName());
			return targetClassValueIndex;
		}
	}
	if (node.getChildDataNodeNum() > 0) {

		int attributeIndex = m_attributeNamesList.indexOf(node
				.getSplitAttributeName());
		if (attributeIndex == -1) {
			log.error("no attribute value for " + m_rootNode.getSplitAttributeName());
		}
		String testAttributeValue = attributeValuesOfData
				.get(attributeIndex);
		DataNode childNode = new DataNode();
		for (int i = 0; i < node.getChildDataNodeNum(); i++) {
			childNode = (DataNode) node.getChildDataNodes().get(i);
			if (childNode != null) {
				if (childNode.getSplitAttributevalueSplit().equalsIgnoreCase(
						testAttributeValue)) {
					break;
				}
			}
			continue;
		}// for
		targetClassValueIndex = classifyInstanceReturnClassvalueIndex(childNode, attributeValuesOfData);
	}
	return targetClassValueIndex;
	}



	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	
	public List<String> getAttributeNamesList() {
		return m_attributeNamesList;
	}
	
	public int getAttributeNum() {
		return m_attributeNum;
	}
	
	
	/**
	 * 
	 * @param dataList
	 * @param attributeIndex
	 * @return Dictionary<attributeValue,num>
	 */
	public   Dictionary<String, Integer> getAttributeValueDistribution(List<Data> dataList,int attributeIndex){
		if (dataList == null || dataList.size() == 0)
			return null;
		Dictionary<String, Integer> attributeValueDistribution = new Hashtable<String, Integer>();
		for (int i = 0; i < dataList.size(); i++) {
			String attributeValue = dataList.get(i).getAttributevaluesList().get(attributeIndex);
			if (attributeValueDistribution.isEmpty()) {
				attributeValueDistribution.put(attributeValue, 1);
			} else {
				if (attributeValueDistribution.get(attributeValue) == null)
					attributeValueDistribution.put(attributeValue, 1);
				else {
					int oldNum = attributeValueDistribution.get(attributeValue);
					attributeValueDistribution.put(attributeValue, oldNum + 1);
				}
			}
		}
		return attributeValueDistribution;

	}
	
	public Hashtable<String, Hashtable<String, Integer>> getAttributeValueDistributionOfEveryAttributeTable() {
		return m_attributeValueDistributionOfEveryAttributeTable;
	}
	
	
	
	public Hashtable<String, AttrType> getAttrTypesMap() {
		return m_attrTypesMapTable;
	}
	
	
	public double getAvgGainInfoForEachAttributeIndex(List<Data> examples,
			List<Integer> attributeIndexs) {
		double totalGain = 0;
		double gain = 0;
		double avgGain=0;
		for (Integer i : attributeIndexs) {
			gain = infoGain(examples, i);
			totalGain+=gain; //get the sum of gain 
		}
		avgGain = totalGain/attributeIndexs.size();
		log.debug("the avg of gain  is : "
				+avgGain );
		return avgGain;
	}
	
	
	/**
	 * get best attributeIndex by GainRation in attributeIndexs
	 * @param examples
	 * @param attributeIndexs
	 * @return
	 */
	public int getBestAttributeIndexByGainRatio(List<Data> examples,
			List<Integer> attributeIndexs) {
		assert(!attributeIndexs.isEmpty() && attributeIndexs.size()>0);
		log.debug("getBestAttributeIndexByGainRatio: on attributeIndexes "+Arrays.toString(attributeIndexs.toArray()));
		double maxGainRatio = 0;
		double totalGain=0;
		double[] gainForEachAttribute = new double[attributeIndexs.size()];
		Arrays.fill(gainForEachAttribute, 0);
		double gain = 0;
		double avgGain = 0;
		int bestAttributeIndex = -1;
		
		if(attributeIndexs.size()<=1){
			return attributeIndexs.get(0);
		}
		
		for (int i=0;i<attributeIndexs.size();i++) {
			gain = infoGain(examples, attributeIndexs.get(i));
			gainForEachAttribute[i] =gain;
			totalGain+=gain;
		}
		for(int a=0;a<gainForEachAttribute.length;a++){
			log.debug("the gain for attribute "+getAttributeNameByIndex(attributeIndexs.get(a)) +" index "+attributeIndexs.get(a)+" is " +gainForEachAttribute[a]);
		}
		avgGain = totalGain/attributeIndexs.size();
		log.debug("the avg of gain  is : "	+avgGain );
		double gainRatio=0;
		for (int i=0;i<attributeIndexs.size();i++) {
			if(gainForEachAttribute[i]>=avgGain){ gainRatio =gainForEachAttribute[i]/splitInformationForAttribute(examples,  attributeIndexs.get(i));
//				gainRatio = getGainRatio(examples, attributeIndexs.get(i)); // calc the gainRatio
				String attributeName = m_attributeNamesList.get(attributeIndexs.get(i));
				maxGainRatio = maxGainRatio > gainRatio ? maxGainRatio : gainRatio;
				bestAttributeIndex = maxGainRatio > gainRatio  ? bestAttributeIndex : attributeIndexs.get(i);
				log.debug("the gain ratio for " + attributeName + "index " +attributeIndexs.get(i)+ " on datas is : "
						+ gainRatio);
			}
		}
		return bestAttributeIndex;
	}
	
	
	/**
	 * get the minumum gini 
	 * @param examples
	 * @param attributeIndexes
	 * @return
	 */
	public int getBestAttributeIndexByGiniGain(List<Data> examples,
			List<Integer> attributeIndexes) {
		log.debug("find the best attributeInformationGain on attributeIndexes "+Arrays.toString(attributeIndexes.toArray()));
		double minGini = 1.0d;
		double[] giniGainForEachAttribute = new double[attributeIndexes.size()];
		Arrays.fill(giniGainForEachAttribute, 0);
		double giniGain = 0;
		int bestAttributeIndex = -1;
		for (int i=0;i<attributeIndexes.size();i++) {
			giniGain = getGiniGainForCategoryAttribute(examples, attributeIndexes.get(i)); 
			//clac the gini value for the attribute
			giniGainForEachAttribute[i] =giniGain;
			minGini= minGini<giniGain? minGini:giniGain;
			bestAttributeIndex = minGini<giniGain? bestAttributeIndex:i;
		}
		for(int a=0;a<giniGainForEachAttribute.length;a++){
			log.debug("the gini for attribute "+getAttributeNameByIndex(attributeIndexes.get(a)) +" index "+a+ " is " +giniGainForEachAttribute[a]);
		}
		log.debug("the best attribute is "+getAttributeNameByIndex(attributeIndexes.get(bestAttributeIndex)) );
		return bestAttributeIndex;
	}
	
	public BestBinarySplitForCategoryAttribute getBestBinarySplitForCategroyAttribute(List<Data> examples,int attributeIndex){
		// binray split
//		find the max attribute value for one attribute
//		 <attributeValue, classValue , num>  eg : {rainy={no=2, yes=3}, overcast={yes=4}, sunny={yes=2, no=3}}
		Dictionary<String, Dictionary<String, Integer>> attributeDictionary = getClassDistributionByDiscreteAttribute(
				examples, attributeIndex);
		double traingDataEntropy = Entropy.entropy(getClassDistribution(examples));
		double maxInfoGain=0.0d;
		double maxEntropy = -1;
		String maxAttributeValue = null;
		Dictionary<String, Integer> dictionary = null;
		Dictionary<String, Integer> sumdDictionary = Tool.addDictionary(attributeDictionary.elements(), 0);
		Dictionary<String, Dictionary<String, Integer> > dic = new Hashtable<String, Dictionary<String,Integer>>();
			for (Enumeration<Dictionary<String, Integer>> enumeration = attributeDictionary
					.elements(); enumeration.hasMoreElements();) {
				dictionary = enumeration.nextElement();
				String attributeValueString = dictionary.keys().nextElement();
				sumdDictionary =Tool.addDictionary(sumdDictionary,dictionary, 1);
				dic.put("0", sumdDictionary);
				dic.put("1",dictionary);
				double entropy = Entropy.InfoOfattributeClassDictionary(dic);
				maxEntropy = maxEntropy>= entropy? maxEntropy: entropy;
				maxAttributeValue = maxEntropy>= entropy? maxAttributeValue: attributeValueString;
			}
			maxInfoGain = traingDataEntropy - maxEntropy;
			BestBinarySplitForCategoryAttribute bestBinarySplitForCategoryAttribute = new BestBinarySplitForCategoryAttribute(maxInfoGain, maxAttributeValue);
			return bestBinarySplitForCategoryAttribute;
	}

	/**
	 * get the best split attribute based on 
	 * select max infoGain and min Entropy or Gini gain
	 * select the attribute with min mean entropy
	 * @param dataList
	 * @param attributeIndexs
	 * @param valType
	 * @return
	 */
	public int getBestSplitAttributeIndex(List<Data> dataList,
			List<Integer> attributeIndexs, AttributeVal valType){
		// For each attribute.
		log.debug("find the best split attribute on "+Arrays.toString(attributeIndexs.toArray()));
		if(valType.equals(AttributeVal.InformationGain)){
			return this.getBestSplitAttributeIndexByInformationGain(dataList, attributeIndexs);
		}
		else if (valType.equals(AttributeVal.GainRatio)) {
			return this.getBestAttributeIndexByGainRatio(dataList, attributeIndexs);
		}else if(valType.equals(AttributeVal.GiniGain))
			return this.getBestAttributeIndexByGiniGain(dataList, attributeIndexs);
		else{
			return this.getBestAttributeIndexByGainRatio(dataList, attributeIndexs);
		}
	}
	
	/**
	 * get the best split attribute on datalist with attribute Indexes
	 * @param dataList
	 * @param attributeIndexes the attribute Indexes in m_attributeNamesList
	 * @return
	 */
	public int getBestSplitAttributeIndexByInformationGain(List<Data> dataList,
			List<Integer> attributeIndexes) {
		log.debug("find the best attributeInformationGain on attributeIndexes "+Arrays.toString(attributeIndexes.toArray()));
		double maxInfoGain = 0;
		double infoGain = 0;
		int bestAttributeIndex = -1;
		for (Integer i : attributeIndexes) {
			String attributeName = m_attributeNamesList.get(i);
				// calc the  attribute information gain
			infoGain = infoGain(dataList, i);
			maxInfoGain = maxInfoGain > infoGain ? maxInfoGain : infoGain;
			bestAttributeIndex = maxInfoGain > infoGain ? bestAttributeIndex : i;
			log.debug("the gain for " + attributeName + "index  "+i+" on datas is : "
					+ infoGain);
		}
		return bestAttributeIndex;
	}
	
	/**
	 * get the best split point for 
	 * choose the minValue 
	 * @param dataList
	 * @param attributeIndex
	 * @return BestSplitPointForContinuousAttribute
	 */
	public BestSplitPointForContinuousAttribute getBestSplitPointForContinousAttribute(List<Data> dataList,int attributeIndex,AttributeVal attributeVal){
		//sort the datas by the attributename
		DataSet.sortDataSet(dataList, attributeIndex); 
		log.debug("sorting datas by attribute "+getAttributeNameByIndex(attributeIndex));
	  	Dictionary<Integer,Integer> preClassNum= new Hashtable<Integer, Integer>();
	  	Dictionary<Integer,Integer> postClassNum= new Hashtable<Integer, Integer>();
	  	// init postClassNum
	  	for(int i=0;i<m_classNum;i++){
	  		String classValue = getClassValueByIndex(i);
	  		Integer classNum = 	getClassDistribution(dataList).get(classValue);
	  	int classValueIndex = getClassValueIndexByValue(classValue);
	  	preClassNum.put(classValueIndex, 0);
	  	postClassNum.put(classValueIndex, classNum);
	  	} 
	  	ContinousHistogram continousHistogram = new ContinousHistogram(preClassNum,postClassNum);
		int oldNum= -1;
		double minValue = Double.MAX_VALUE;
		double maxGain = -1.0d;
		double dataValue = 0.0d;
		int splitDataId = 0;
		double splitAttributeValue=0.0d;
		BestSplitPointForContinuousAttribute bestSplitPointForContinuousAttribute =new BestSplitPointForContinuousAttribute();
		ContinousHistogram bestContinousHistogram = new ContinousHistogram();
		Data data=null;
		/* exclude the last one*/
		for (int i=0;i<dataList.size()-1;i++) {
			data=dataList.get(i);
			if(dataList.get(i+1)==null||dataList.get(i)==null){
				continue;
			}
			double dataAttributeValue=Double.valueOf(data.getAttributevaluesList().get(attributeIndex));
			double nextAttributeValue= Double.valueOf(dataList.get(i+1).getAttributevaluesList().get(attributeIndex));
			double midAttributeValue = (dataAttributeValue+nextAttributeValue)/2;
			int dataClassValueIndex = getClassValueIndexByValue(data.getClassValue());
		 oldNum=	continousHistogram.getPreClassNum().get(dataClassValueIndex);
		continousHistogram.getPreClassNum().put(dataClassValueIndex, ++oldNum);
		oldNum = continousHistogram.getPostClassNum().get(dataClassValueIndex);
		continousHistogram.getPostClassNum().put(dataClassValueIndex, --oldNum);
		if(attributeVal == AttributeVal.GainRatio|| attributeVal== AttributeVal.GainRatio){
		dataValue = Entropy.entropy(continousHistogram); 
		}else{
			// gini
			dataValue = Gini.gini(continousHistogram);
		}
		log.debug("the value split at "+midAttributeValue +" is "+dataValue);
		//choose the minimum
		if(dataValue<minValue){
			bestContinousHistogram.setPreClassNum(preClassNum);
			bestContinousHistogram.setPostClassNum(postClassNum);
			splitDataId=data.getRecordId();
			splitAttributeValue =midAttributeValue;
			minValue =dataValue; 
		} 
		}
		if(attributeVal == AttributeVal.GainRatio|| attributeVal== AttributeVal.GainRatio){
		double traingDataEntropy = Entropy.entropy(getClassDistribution(dataList));
		maxGain = traingDataEntropy - minValue;
		}else{
			maxGain = Gini.gini(getClassDistribution(dataList));
		}
		bestSplitPointForContinuousAttribute.setContinousHistogram(continousHistogram);
		bestSplitPointForContinuousAttribute.setMaxGain(maxGain);
		bestSplitPointForContinuousAttribute.setSplitRecordIdData(splitDataId);
		bestSplitPointForContinuousAttribute.setSplitAttributeValue(splitAttributeValue);
		log.debug("the best split point for "+getAttributeNameByIndex(attributeIndex)+ " is "+bestContinousHistogram.toString()
				+"the maxGain for attribute :"+m_attributeNamesList.get(attributeIndex) +" the split data Id is "+splitDataId +" split attributeValue :"+splitAttributeValue);
		return bestSplitPointForContinuousAttribute;
	}
	
	public  String getClassAttributeName() {
		return m_classAttributeName;
	}
	
	
	/**
	 * Task: Dictionary<String , Integer > key:classId,value: classNum 
	 * eg  [no:5 yes:9]
	 * @param dataList
	 * @return <classId, classNum>
	 * @author Administrator
	 */
	public  Dictionary<String, Integer> getClassDistribution(List<Data> dataList) {
		if (dataList == null || dataList.size() == 0)
			return null;
		Dictionary<String, Integer> classDistibution = new Hashtable<String, Integer>();
		for (int i = 0; i < dataList.size(); i++) {
			String classId = dataList.get(i).getClassValue();
			if (classDistibution.isEmpty()) {
				classDistibution.put(classId, 1);
			} else {
				if (classDistibution.get(classId) == null)
					classDistibution.put(classId, 1);
				else {
					int oldNum = classDistibution.get(classId);
					classDistibution.put(classId, oldNum + 1);
				}
			}
		}
		return classDistibution;

	}
	
	/**
	 * AttributeValue classValue ClassNum
	 * one class have different classvalue 
	 * {rainy={no=2, yes=3}, 
	 * overcast={yes=4},
	 * sunny={yes=2, no=3}}
	 * @param examples
	 * @param AttributeIndex
	 * @return <attributeValue, <classValue , classNum> >
	 */
	public Dictionary<String, Dictionary<String, Integer>> getClassDistributionByDiscreteAttribute(
			List<Data> examples, int AttributeIndex) {
		Dictionary<String, Dictionary<String, Integer>> attributeDistribution = new Hashtable<String, Dictionary<String, Integer>>();
		Dictionary<String, Integer> classDictionary = null;
		for (int i = 0; i < examples.size(); i++) {
			Data currentData = examples.get(i);
			// this.attributeNamesList.get(AttributeIndex); // the attribute tSo
			String dataAttributeValue = currentData.getAttributevaluesList().get(
					AttributeIndex);
			String dataClassValue = currentData.getClassValue();
			if (attributeDistribution.isEmpty()) {
				classDictionary = new Hashtable<String, Integer>();// key
				classDictionary.put(dataClassValue, 1);
				attributeDistribution.put(dataAttributeValue, classDictionary);
			} else {
				if (attributeDistribution.get(dataAttributeValue) == null) {
					classDictionary = new Hashtable<String, Integer>();// key
					classDictionary.put(dataClassValue, 1);
					attributeDistribution.put(dataAttributeValue, classDictionary);
				} else {
					classDictionary = attributeDistribution.get(dataAttributeValue);
					if (classDictionary.get(dataClassValue) == null) {
						classDictionary.put(dataClassValue, 1);
					} else {
						int oldNum = attributeDistribution.get(dataAttributeValue)
								.get(dataClassValue);
						classDictionary.put(dataClassValue, oldNum + 1);
					}
					attributeDistribution.put(dataAttributeValue, classDictionary);
				}
			}
		}
		return attributeDistribution;
	}
	
	public abstract String getClassicationResult();
	
	
	public  List<Integer> getClassIndexs() {
		return m_classIndexs;
	}
	
	
	
	
	
	/**
	 * get the class distribution of dataList
	 * @param dataList
	 * @return
	 */
	public List<Integer> getDataClassDistribution(List<Data> dataList) {
		List<Integer> list = new ArrayList<Integer>();
		Dictionary<String, Integer> classDistributionDictionary = getClassDistribution(dataList);
		for (Enumeration<Integer> enumeration = classDistributionDictionary
				.elements(); enumeration.hasMoreElements();) {
			list.add(enumeration.nextElement());
		}
		return list;
	}
	
	/**
	 *  count, dataList
	 * @param examples
	 * @param attributeValueIndex
	 * @param attrType
	 * @param equalValue
	 * @return <count, dataList>
	 */
	public  Entry<Integer, List<Data>> getDatasByAttributeValue(
			List<Data> examples, int attributeValueIndex,AttrType attrType, String equalValue) {
		List<Data> dataList = new ArrayList<Data>();
		int count = 0;
		for (Data data : examples) {
			String dataAttributeValue = data.getAttributevaluesList().get(attributeValueIndex);
			if(attrType==AttrType.Category){
			if (dataAttributeValue.equalsIgnoreCase(equalValue)) {
				dataList.add(data);
				count++;
			}
			}else{
				Double dataAttributeValueDouble = Double .valueOf(dataAttributeValue);
				Double splitAttributeValue=Double.valueOf(equalValue);
				if(dataAttributeValueDouble<=splitAttributeValue){
				dataList.add(data);
				count++;
				}
			}
		}
		Map.Entry<Integer, List<Data>> pair = new AbstractMap.SimpleEntry(new Integer(count), dataList);
		return pair;
	}
	
	
	
	public List<Data> getDatasByIds(List<Data> datas , Collection<Integer> ids){
		List<Data> currentDatas= new LinkedList<Data>();
		// not the tagNode
		for (Integer i: ids) {
			currentDatas.add(datas.get(i-1));    // start from 0
		} //get the datas
		return currentDatas;
	}

	
	/**
	 * 
	 * @param
	 * @param AttributeIndex
	 * @return
	 */
	public  Dictionary<String, Integer> getDistributionByDiscreteAttribute(
			List<Data> examples, int AttributeIndex) {
		Dictionary<String, Integer> attributeDistribution = new Hashtable<String, Integer>();
		for (int i = 0; i < examples.size(); i++) {
			String attributeName = (String) ((Data) examples.get(i))
					.getAttributevaluesList().get(AttributeIndex);
			if (attributeDistribution.isEmpty()) {
				attributeDistribution.put(attributeName, 1);
			} else {
				if (attributeDistribution.get(attributeName) == null)
					attributeDistribution.put(attributeName, 1);
				else {
					int oldNum = attributeDistribution.get(attributeName);
					attributeDistribution.put(attributeName, oldNum + 1);
				}
			}
		}
		return attributeDistribution;
	}
	
	/**
	 * GainRatio = infoGain(S,A)/splitInformation(S,A)
	 * that is to say , GainRation = Info(S,A) / V(S,A)
	 * V(S,A) = -\sum {\frac {|C_{i}||{|S|} log{\frac {|C_{i}||{|S|} } 
	 * @param examples
	 * @param attributeIndex
	 * @return
	 */
	public double getGainRatio(List<Data> examples, int attributeIndex) {
		log.debug("calc the gain ratio for "+getAttributeNameByIndex(attributeIndex)+" index "+attributeIndex);
		double gainRatio = 0;
		gainRatio = infoGain(examples, attributeIndex)/splitInformationForAttribute(examples, attributeIndex);
		return gainRatio;
	}

	/**
	 * cacl the gini gain for category attribute
	 * giniGain = gini(T) - gini(T,A) for category attribute
	 * @param examples
	 * @param attributeIndex
	 * @return
	 */
	public double getGiniGainForCategoryAttribute(List<Data> examples,int attributeIndex){
		double giniGain=0.0d;
		double instancesGini = Gini.gini(getClassDistribution(examples));
		double childGini = GiniOfSplitCategoryAttribute(examples,attributeIndex);
		giniGain = instancesGini - childGini;
		return giniGain;
	}

	/**
	 * 
	 * @param rootNode
	 * @return
	 */
	public int getHeight(DataNode rootNode) {
		if (rootNode.getChildDataNodeNum() <= 0) {
			return 1;
		}
		List<Integer> childDepths = new LinkedList<Integer>();
		int maxChildDepths = Integer.MIN_VALUE;
		for (DataNode childDataNode : rootNode.getChildDataNodes()) {
			childDepths.add(getHeight(childDataNode));
		}
		maxChildDepths = Collections.max(childDepths);
		return maxChildDepths + 1;
	}
	

	public Collection<Integer> getIDsOfDataList(List<Data> dataList){
		List<Integer> recordIds = new LinkedList<Integer>();
		for (Data data : dataList) {
			if (data != null) {
				recordIds.add(data.getRecordId());
			}
		}
		return recordIds;
	}
	
	/**
	 * calc the information gain for categeroy attribute
	 * infoGain = info(T) - info(T,A) for category attribute
	 * @param examples
	 * @param attributeIndex the attributeIndex in m_attributeNamesList
	 * @return
	 */
	public double getInfoGainForCategoryAttribute(List<Data> examples,int attributeIndex){
		double gain=0.0d;
		double childEntropy = 0.0d;
		double traingDataEntropy = Entropy.entropy(getClassDistribution(examples));
		 childEntropy = InfoOfSplitCategoryAttribute(examples,attributeIndex);
		gain = traingDataEntropy - childEntropy;
		return gain;
		}
	
	public int getLeafNodesSize() {
		return m_LeafNodesSize;
	}

	public  int getLevel() {
		return m_level;
	}


	
	public DataNode getRootNode() {
		return m_rootNode;
	}

	public   int getTotalNodesSize() {
		return m_totalNodesSize;
	}

	
	
	/**
	 * 
	 * @return
	 */
	public Double getTotalTrainingDataEntropy() {
		Dictionary<String, Integer> classDictionary = getClassDistribution(m_traingData.getDataList());
		List<Integer> dataList = new ArrayList<Integer>();
		Enumeration<Integer> e = classDictionary.elements();
		while (e.hasMoreElements()) {
			dataList.add(e.nextElement());
		}
		return Entropy.entropy(dataList);
	}
	
	public DataSet getTraingData() {
		return m_traingData;
	}
	
	
	
	public abstract int getTrainingDataAttributeNum() ;
	
	public List<Data> getValidDataset() {
		return m_validDataset;
	}
	
	public double gini(Dictionary<?, Integer> dictionary) {
		double gini = 1;
		int integer;
		for (Enumeration<Integer> e = dictionary.elements(); e
				.hasMoreElements();) {
			integer = e.nextElement();
			double p = (double) integer / m_traingData.getDataList().size();
			gini -= Math.pow(p, 2);
		}
		return gini;
	}

	@Override
	public String classifyInstance(Classifier classifier,
			List<String> attributeValuesOfData) {
		int classValueIndex= classifyInstanceReturnClassValueIndex( classifier,
			attributeValuesOfData);
		if(classValueIndex==-1){
			return "none";
		}
		return  getClassValueByIndex(classValueIndex);
	}



	@Override
	public double getClassifyAccuracy(Classifier classifier, List<Data> dataList) {
		double acc=-1;
		try {
			acc = Accuracy.getInstance().measure(classifier.classifyDataSet(classifier, dataList),dataList,new ArrayList<String>(((AbstractDecisionTree) classifier).m_classLabelValuesMap.keySet()));
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return acc;
	}

	/**
	 * calc the Gini Gain
	 * GiniGain(T,A) = Gini(T) - Gini(T,A)
	 * @param dataList
	 * @param attributeIndex
	 * @return
	 */
	public double gini(List<Data> dataList, int attributeIndex) {
		log.debug("calc the gini for "+getAttributeNameByIndex(attributeIndex) );
		double giniGain = 0;
		String attributeName = m_attributeNamesList.get(attributeIndex);
		if(m_attrTypesMapTable.get(attributeName)==AttrType.Category){
		giniGain = getGiniGainForCategoryAttribute(dataList, attributeIndex);
		}else{
		giniGain=getBestSplitPointForContinousAttribute(dataList, attributeIndex,AttributeVal.GiniGain).getMaxGain();
		}
		return giniGain;
	}

	

	/**
	 * calc the gini of attribute 
	 * Info(T, A ) =  \sum { \frac{ |T_{i}|}{ |T|}} * Info (T_{i}) }
	 * @param examples
	 * @param attributeIndex
	 * @return
	 */
	public double GiniOfSplitCategoryAttribute(List<Data> examples, int attributeIndex) {
		Dictionary<String, Dictionary<String, Integer>> attributeDictionary = getClassDistributionByDiscreteAttribute(
				examples, attributeIndex);
		double childGini =Gini.GiniOfattributeClassDictionary(attributeDictionary);
		return childGini;
	}
	
	/**
	 * calc the information Gain
	 * InfoGain(T,A) = Info(T) - Info(T,A)
	 * @param dataList
	 * @param attributeIndex
	 * @return
	 */
	public double infoGain(List<Data> dataList, int attributeIndex) {
		double infoGain = 0;
		String attributeName = m_attributeNamesList.get(attributeIndex);
		log.debug("gain: calc the info gain for "+attributeName +" index "+attributeIndex);
	
		if(m_attrTypesMapTable.get(attributeName)==AttrType.Category){
				infoGain = getInfoGainForCategoryAttribute(dataList, attributeIndex);
		}else{
		infoGain=getBestSplitPointForContinousAttribute(dataList, attributeIndex,AttributeVal.GainRatio).getMaxGain();
		}
		return infoGain;
	}
	
	
	/**
	 * calc the information of attribute ,namely , the information of subsets split by test attribute
	 * Info(T, A ) =  \sum { \frac{ |T_{i}|}{ |T|}} * Info (T_{i}) }
	 * @param examples
	 * @param attributeIndex
	 * @author apple
	 * @return
	 */
	public double InfoOfSplitCategoryAttribute(List<Data> examples,
			int attributeIndex) {
//		 <attributeValue, classValue , num>  eg : {rainy={no=2, yes=3}, overcast={yes=4}, sunny={yes=2, no=3}}
		Dictionary<String, Dictionary<String, Integer>> attributeDictionary = getClassDistributionByDiscreteAttribute(
				examples, attributeIndex);
		double childEntropy =Entropy.InfoOfattributeClassDictionary(attributeDictionary);
		return childEntropy;
	}
	
	/**
	 * 
	 * @param set
	 * @param attributeIndex
	 */
	public Double informationGain(List<Data> set, int attributeIndex) {
		Hashtable<String, Integer> distribution = (Hashtable<String, Integer>)
				getDistributionByDiscreteAttribute(m_traingData.getDataList(),
						attributeIndex);
		int[] a = new int[distribution.size()];
		Object[] obj = distribution.values().toArray();
		for (int j = 0; j < obj.length; j++) {
			a[j] = (Integer) obj[j];
		}
		double divideEntropy = Entropy.entropy(a);
		return this.getTotalTrainingDataEntropy() - divideEntropy;
	}
	
	
	public boolean isSameClass(List<Data> dataList){
		boolean isSameClass = true;
		if (dataList.size() == 1) {
			return true;
		}
		String classId = "";
		for (Data d : dataList) {
			if (classId.equals("")) {
				classId = d.getClassValue();
			} else {
				if (!d.getClassValue().equalsIgnoreCase(classId)) {
					isSameClass = false;
					break;
				}
			}
		}
		return isSameClass;
	}
	
	public boolean isSameClass(List<Data> dataList,Collection<Integer> idsList){
		boolean isSameClass = true;
		if (idsList.size() == 1) {
			return true;
		}
		String classId = "";
		for (Integer d : idsList) {
			if (classId.equals("")) {
				classId =((Data)dataList.get(d)).getClassValue();
			} else {
				if (!((Data)dataList.get(d)).getClassValue().equalsIgnoreCase(classId)) {
					isSameClass = false;
					break;
				}
			}
		}
		return isSameClass;
	}
	

	/**
	 * 
	 * @param examples
	 * @return
	 */
	public boolean isSameTargetAttribute(List<Data> examples) {
		boolean result = true;
		String classidString = examples.get(0).getClassValue();
		for (Data data : examples) {
			if (!data.getClassValue().equalsIgnoreCase(classidString)) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	/**
	 * @param examples
	 * @return
	 */
	public String mostCommonTargetAttribute(List<Data> examples) {
		String maxkey = "";
		int max = -1;
		Dictionary<String, Integer> classDictionary = getClassDistribution(examples);
		if (classDictionary != null) {
			for (Enumeration<String> classValueEnumeration = classDictionary.keys(); classValueEnumeration
					.hasMoreElements();) {
				String currentkey = classValueEnumeration.nextElement();
				maxkey = max > classDictionary.get(currentkey) ? maxkey
						:currentkey;
				max = max > classDictionary.get(currentkey) ? max
						: classDictionary.get(currentkey);
			}
		}
		return maxkey;
	}

	public abstract void printPath(DataNode rootNode, String path);

	
	/**
	 * get the attribute name in attributeNamesList
	 * @param attrIndex the attribute name index in attributeNamesList
	 * @return
	 */
	public String getAttributeNameByIndex(int attrIndex){
		assert(attrIndex<m_attributeNum);
		return m_attributeNamesList.get(attrIndex);
	}
	

	

	/**
	 * @deprecated
	 * @param StringLines
	 * @return
	 */
	private List<Data> readDatasFromFile(List<String> StringLines,
			Dictionary<String, Integer> targetAttributeDictionary) {
		List<Data> dataList = new ArrayList<Data>(StringLines.size());
		Hashtable<String, Hashtable<String, Integer>> attributeValueDictionary = new Hashtable<String, Hashtable<String, Integer>>();
		// <AttributeName <attributeValue , num> >
		Hashtable<String, Integer> attributeValueNumDictionay = new Hashtable<String, Integer>();
	 //recordId start from 1
		for (int recordId =0;recordId<StringLines.size();recordId++) { //each line is a data
			Data data = new Data();
			String[] linelist = (String[]) Tool.split(StringLines.get(recordId).trim(), ":", 0);
			String classValue = linelist[0].trim().toLowerCase(); // get the target calss
													// value
			// get the targetAttributeClassDistribution
			if (targetAttributeDictionary.get(classValue) == null) {
				targetAttributeDictionary.put(classValue, 1);
			} else {
				int oldvalue = targetAttributeDictionary.get(classValue);
				targetAttributeDictionary.put(classValue, oldvalue + 1);
			}
			// get each line of the data ,and split it
			String[] dataAttributeValues = (String[]) Tool.split(
					linelist[1].trim(), " +", 0); // 
			List<String> dataAttributeValuesList = Arrays
					.asList(dataAttributeValues);
			// get every attribute value
			for (int i = 0; i < dataAttributeValuesList.size(); i++) {
				String dataAttributeValue = dataAttributeValuesList.get(i);
				String attributeName = m_attributeNamesList.get(i);
				if (attributeValueDictionary.get(attributeName)== null) {
					//<AttributeName <attributeValue , num> >
					if (attributeValueNumDictionay.size() == 0) {
						attributeValueNumDictionay.put(dataAttributeValue, 1);
						attributeValueDictionary.put(attributeName,
								attributeValueNumDictionay);
					} else {
						Hashtable<String, Integer> newAttributeValueNum = new Hashtable<String, Integer>();
						newAttributeValueNum.put(dataAttributeValue, 1);
						attributeValueDictionary.put(attributeName,
								newAttributeValueNum);
					}
				} else {
					attributeValueNumDictionay = (Hashtable<String, Integer>) attributeValueDictionary
							.get(attributeName);
					if (attributeValueNumDictionay.get(dataAttributeValue) == null) {
						attributeValueNumDictionay.put(dataAttributeValue, 1);
					} else {
						int oldnum = (Integer) attributeValueNumDictionay
								.get(dataAttributeValue);
						attributeValueNumDictionay.put(dataAttributeValue,
								oldnum + 1);
					}
					attributeValueDictionary.put(attributeName,
							attributeValueNumDictionay);
				}
			}
			data.setClassValue(classValue);
			data.setRecordId(recordId+1); //record Id start from 1
			data.setAttributevaluesList(dataAttributeValuesList);
			dataList.add(data);
		}
		m_attributeValueDistributionOfEveryAttributeTable = attributeValueDictionary;
		return dataList;
	}

	public List<Integer> getAttributeIndexs() {
		return m_attributeIndexs;
	}
	
	public void setAttributeIndexs(List<Integer> attributeIndexs) {
		m_attributeIndexs = attributeIndexs;
	}

	public void setAttributeNamesList(List<String> attributeNamesList) {
		m_attributeNamesList = attributeNamesList;
	}

	public void setAttributeNum(int attributeNum) {
	m_attributeNum = attributeNum;
	}



	public void setAttributeValueDistributionOfEveryAttributeTable(
			Hashtable<String, Hashtable<String, Integer>> attributeValueDistributionOfEveryAttributeTable) {
		m_attributeValueDistributionOfEveryAttributeTable = attributeValueDistributionOfEveryAttributeTable;
	}

	public void setAttrTypesMap(Hashtable<String, AttrType> attrTypesMap) {
		m_attrTypesMapTable = attrTypesMap;
	}
	public  void setClassAttributeName(String classAttributeName) {
		m_classAttributeName = classAttributeName;
	}
	
	public  void setClassIndexs(List<Integer> classIndexs) {
		m_classIndexs = classIndexs;
	}
	

	public void setLeafNodesSize(int leafNodesSize) {
		m_LeafNodesSize = leafNodesSize;
	}

	public  void setLevel(int level) {
		m_level = level;
	}

	public void setRootNode(DataNode rootNode) {
		m_rootNode = rootNode;
	}

	public   void setTotalNodesSize(int totalNodesSize) {
		m_totalNodesSize = totalNodesSize;
	}

	public void setTraingData(DataSet traingData) {
		m_traingData = traingData;
	}

	public void setValidDataset(List<Data> validDataset) {
		m_validDataset = validDataset;
	}

	
	/**
	 * sort the dataList on the compareAttrIndex
	 * @param dataList
	 * @param compareAttrIndex
	 */
	public void SortTrainingDataList(List<Data> dataList, int compareAttrIndex) {
		DataSet.sortDataSet(dataList, compareAttrIndex);
	}

	/**
	 *  -sum(|S_i|/|S|)log_2 (|S_i|/|S|)
	 *  used to describe the split data by split attribute value
	 *  分裂信息度量被定义为(分裂信息用来衡量属性分裂数据的广度和均匀)：
	 * @param examples
	 * @param attributeIndex
	 * @return
	 */
	public double splitInformationForAttribute(List<Data> examples, int attributeIndex){
	 return Entropy.entropy(getAttributeValueDistribution(examples,attributeIndex));
	}
	
	/**
	 * get the attrType for the attribute with attributeIndex in attributeIndexes
	 * @param attributeIndex
	 * @return
	 */
	public AttrType getAttrTypeForAttribute(int attributeIndex){
		return m_attrTypesMapTable.get(m_attributeNamesList.get(attributeIndex));
	}

	
	/**
	 * get the attryTypes HashTable</br>
	 * Node that the order in attrimap is not same as the order in attributeNamesList
	 * @param attrTypeHashtable
	 * @return
	 */
	public Hashtable<Integer, AttrType> getAttrTypesMap(Hashtable<String, AttrType> attrTypeHashtable){
		Hashtable<Integer, AttrType> attrTypeTable = new Hashtable<Integer, AttrType>();
		for(String attributeName: m_attributeNamesList){
			attrTypeTable.put(m_attributeNamesList.indexOf(attributeName),m_attrTypesMapTable.get(attributeName));
		}
		return attrTypeTable;
	}


	public static int getClassNum() {
		return m_classNum;
	}



	public static void setClassNum(int m_classNum) {
		assert(m_classNum>0);
		AbstractDecisionTree.m_classNum = m_classNum;
	}
	
	
	@Override
	public List<Integer> classifyDataSet(Classifier classifier, DataSet dataSet) {
		return classifyDataSet(classifier, dataSet.getDataList());
	}
	
	
	/**
	 * get the classifiy accuracy on the dataSet with classifier
	 * @return double
	 */
	@Override
	public double getClassifyAccuracy(Classifier classifier, DataSet dataSet)  {
		double acc=-1;
		try {
			acc = Accuracy.getInstance().measure(classifier.classifyDataSet(classifier, dataSet), dataSet.getDataList(),new ArrayList<String>(((AbstractDecisionTree) classifier).m_classLabelValuesMap.keySet()));
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return acc;
	}
	
	/**
	 * get the list of DataNode of the tree </br> 
	 * List Type : ArrayList
	 * @return ArrayList of DataNode
	 */
	public abstract List<DataNode> getTreeDataNodesList();
	
	public abstract String writeTreeNodesToFile(File file);



	@Override
	public String getClassValueByIndex(int classValueIndex) {
		int i=-1;
		 for (Iterator<String> it =  m_classLabelValuesMap.keySet().iterator();it.hasNext();i++)
		   {
			 if(i==classValueIndex-1){
		    String key = it.next();
		  return key;
		  }
			 it.next(); // next value
			 continue;
		   }
		 log.error("can't find the classvalue in {}",classValueIndex );
		 return null;
	}
	
}
  