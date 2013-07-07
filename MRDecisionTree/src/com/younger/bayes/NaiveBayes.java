package com.younger.bayes;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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

import javax.management.RuntimeErrorException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.classifiers.Classifier;
import com.younger.common.HdfsMapWritable;
import com.younger.conf.Parameter;
import com.younger.constant.Enum.AttrType;
import com.younger.data.Data;
import com.younger.data.DataConverter;
import com.younger.data.DataSet;
import com.younger.eval.Accuracy;
import com.younger.struct.ContinousDistribution;
import com.younger.tool.MathUtil;
import com.younger.tool.Tool;
import com.younger.tool.hadoop.HdfsWritableUtils;

/**
 * NavieBayes Classifier
 * 
 * @author apple
 * 
 */
public class NaiveBayes extends Classifier {

	private static final Logger log = LoggerFactory.getLogger(NaiveBayes.class);

	/** for serialization */
	static final long serialVersionUID = 5995231201785697655L;

	/**
	 * NavieBayesModel</br>
	 *  which used to classifiy and predict instance 
	 */
	protected NaiveBayesModel m_Model = null;

	/**
	 * attributeName values</br>
	 * Type: ArrayList
	 */
	protected List<String> m_attributeNamesList;
	
	/** attribute Num */
	protected int m_attributeNum = 0;
	/**
	 * attributeName, attributeValue,num
	 */
	protected Hashtable<String, Hashtable<String, Integer>> m_attributeValueDistributionOfEveryAttributeTable;
	protected ContinousDistribution m_classContinousDistribution = null;
	/**
	 * className, atttributeName, attributeValue,num </br> to calc the condition
	 * P ( x_{i}|y_{j}) </br>
	 * eg. {yes={temperature={mild=4, hot=2, cool=3},
	 *  windy={false=6, true=3}, humidity={high=3, normal=6}, outlook={rainy=3, sunny=2, overcast=4}},</br>
	 *   no={temperature={mild=2, hot=2,cool=1}, windy={false=2, true=3},</br>
	 * humidity={high=4, normal=1}, outlook={rainy=2, sunny=3}}}
	 */
	protected Hashtable<String, Hashtable<String, Hashtable<String, Integer>>> m_attributeValueNumOfEveryClassHashtable;
	/** AttributeName, AttributeType */
	protected Hashtable<String, AttrType> m_attrTypesMap = null;

	protected String m_classAttributeName = null;

	protected DataSet m_traingDataSet;
	
	/**
	 * 
	 */
	protected List<Data> m_validDatasetList;
	/**
	 * Type: LinkedList
	 * the order same as the m_attributeNamesList
	 * @see {@linkplain #m_attributeNamesList}
	 */
	protected List<Integer> m_attributeIndexsList;
	/**
	 * Type: ArrayList
	 * the order same as the m_classLableValuesMap
	 * @see {@link #m_classLableValuesMap}
	 */
	protected List<Integer> m_classIndexsList;

	/** Class Label values </br> Type : ArrayList<String> </br>
	 * Different class lable values
	 */
//	protected List<String> m_classLabelValuesList;
	/**
	 * LinkedHashMap (classValue,num)
	 */
	protected Map<String, Integer> m_classLableValuesMap;
	
	protected int m_classNum = 0;
	/**indicate the class attr type Categorical or Continuous */
	protected AttrType m_classType;
	
	protected int m_datasetSize = 0;
	
	/**
	 * init local variables
	 */
	protected void init() {
		this.m_attributeNamesList = new ArrayList<String>();
		this.m_attributeValueDistributionOfEveryAttributeTable = new Hashtable<String, Hashtable<String, Integer>>();
		this.m_attrTypesMap = new Hashtable<String, AttrType>();
		this.m_traingDataSet = new DataSet();
		this.m_attributeIndexsList = new LinkedList<Integer>();
		this.m_classIndexsList = new ArrayList<Integer>();
//		this.m_classLabelValuesList = new ArrayList<String>();
		this.m_classLableValuesMap = new LinkedHashMap<String, Integer>();
		this.m_Model = new NaiveBayesModel();
		this.m_attributeValueNumOfEveryClassHashtable = new Hashtable<String, Hashtable<String, Hashtable<String, Integer>>>();
	}

	public NaiveBayes() {
		init();
	}

	/**
	 * build Classifier
	 * 
	 * @param dataFileName
	 */
	public NaiveBayes(String dataFileName) {
		buildClassifier(dataFileName);
	}

	public int getClassNum() {
		return m_classNum;
	}

	public void setClassNum(int classNum) {
		this.m_classNum = classNum;
	}

	/**
	 * get the P of classValue Index in m_classLabelValuesList
	 * 
	 * @param classValueIndex
	 * @return
	 */
	public double getProbabilityOfClassValue(int classValueIndex) {
		double classP = 0.0d;
		if(m_classType.isCategorical()){
		int classValueNum = this.m_traingDataSet.getClassValueMap()
				.get(getClassValueByIndex(classValueIndex));
		 classP = (double) classValueNum
				/ Tool.getSum(this.m_traingDataSet
						.getClassValueMap());
		}else{
		double classValue=Double.valueOf(getClassValueByIndex(classValueIndex));
		classP = MathUtil.normalProbability(classValue,m_classContinousDistribution.getMean(),m_classContinousDistribution.getStd());
		}
		return classP;
	}

	/**
	 * get the p for each class in m_classIndexsList
	 * 
	 * @return
	 */
	public List<Double> getProbabilityOfClassValue() {
		List<Double> classP = new ArrayList<Double>(m_classNum);
		double p=0.0d;
		for (int classValueIndex : m_classIndexsList) {
			p = getProbabilityOfClassValue(classValueIndex);
			classP.add(p);
		}
		return classP;
	}

	/**
	 * get the attributeValue Index in attributeName
	 * 
	 * @param attributeName
	 * @param attributeValue
	 * @return
	 */
	public int getAttributeValueIndex(String attributeName,
			String attributeValue) {
		int index = -1;
		int attributeValueIndex = 0;
		for (String s : getAttributeValuesOfAttribute(attributeName)) {
			if (s.equalsIgnoreCase(attributeValue)) {
				index = attributeValueIndex;
				break;
			}
			attributeValueIndex++;
		}
		return index;
	}

	/**
	 * get the attribute values in (attributeValue , num) table </br> from
	 * {@link m_attributeValueDistributionOfEveryAttributeTable} (
	 * attributeName, attributeValue,num)
	 * 
	 * @param attributeName
	 * @return
	 */
	public Set<String> getAttributeValuesOfAttribute(String attributeName) {
		// get the attribute value ,num table
		Hashtable<String, Integer> attributeValueNumDictionary = this.m_attributeValueDistributionOfEveryAttributeTable
				.get(attributeName);
		return attributeValueNumDictionary.keySet();
	}

	/**
	 * get the probability of Data when it was classified to be ClassvalueIndex</br>
	 * Note that the order of attributeNames in attributeValueList should be consistent with the order in m_attributeNamesList </br>
	 * @param classValueIndex
	 * the class value Index in classValueList
	 * @param attributeValueList
	 * the data's attributeValues
	 * P(C=c1|x1,x2,x3..xn) = \alpha P(C=c1) *P(x=x1|C=c1)* P(x=x2|C=c1)*.......P(x=xn|C=c1)*)  
	 * @return
	 */
	public double getProbalityOfData(List<String> attributeValueList,
			int classValueIndex,Boolean smooth) {
		assert (attributeValueList.size() > 0&&attributeValueList.size()==m_attributeNum);
		double p = 1.0;
		double conditionP;
		for (int attributeIndex = 0; attributeIndex < attributeValueList.size(); attributeIndex++) {
			String attributeValue = attributeValueList.get(attributeIndex);
			conditionP = getConditionPOfAttributeValue(classValueIndex,attributeIndex , attributeValue,smooth);
			p = p * conditionP;
		}
		double classP = this.m_Model.getClassProbalityList().get(
				classValueIndex);
		p = p * classP;
		return p;
	}

	/**
	 * get the probability of Data when it was classified to be ClassvalueIndex
	 * 
	 * @param data
	 * @param classValueIndex
	 *            the class value Index in classValueList
	 * @return
	 */
	public double getProbalityOfData(Data data, int classValueIndex,Boolean smooth) {
		return getProbalityOfData(data.getAttributevaluesList(),
				classValueIndex,smooth);
	}

	/**
	 * get the P for data at each class and sumOne </br>
	 * 
	 * @param data
	 * @return
	 */
	public List<Double> predictClassProbalityOfData(Data data,Boolean smooth) {
		List<Double> pList = new ArrayList<Double>();
		for (int i = 0; i < this.m_classNum; i++) {
			pList.add(getProbalityOfData(data, i,smooth));
		}
		log.debug("the probality for each class："
				+ Arrays.toString(pList.toArray()));
		log.debug("the probality for each class after sum one："
				+ Arrays.toString(MathUtil.toSumOne(pList).toArray()));
		return MathUtil.toSumOne(pList);
	}

	/**
	 * get the P for data's attributeValuesList at each class and sumOne </br>
	 * 
	 * @param attributeValuesList
	 * @param smooth
	 * @param sumOne
	 * @return
	 */
	public List<Double> predictClassProbalityOfData(
			List<String> attributeValuesList,boolean sumOne,boolean smooth) {
		List<Double> pList = new ArrayList<Double>();
		for (int i = 0; i < this.m_classNum; i++) {
			pList.add(getProbalityOfData(attributeValuesList, i,smooth));
		}
		log.debug("the probality for data "+Arrays.toString(attributeValuesList.toArray())+" each class："
				+ Arrays.toString(pList.toArray()));
		log.debug("the probality for data "+Arrays.toString(attributeValuesList.toArray())+" each class after sum one："
				+ Arrays.toString(MathUtil.toSumOne(pList).toArray()));
		if(sumOne){
		return MathUtil.toSumOne(pList);
		}
		return pList;
	}

	public List<String> getAttributeNamesList() {
		return m_attributeNamesList;
	}

	public void setAttributeNamesList(List<String> attributeNamesList) {
		this.m_attributeNamesList = attributeNamesList;
	}

	public int getAttributeNum() {
		return m_attributeNum;
	}

	public void setAttributeNum(int attributeNum) {
		this.m_attributeNum = attributeNum;
	}

	public Hashtable<String, Hashtable<String, Integer>> getAttributeValueDistributionOfEveryAttributeTable() {
		return m_attributeValueDistributionOfEveryAttributeTable;
	}

	public void setAttributeValueDistributionOfEveryAttributeTable(
			Hashtable<String, Hashtable<String, Integer>> attributeValueDistributionOfEveryAttributeTable) {
		this.m_attributeValueDistributionOfEveryAttributeTable = attributeValueDistributionOfEveryAttributeTable;
	}

	public Hashtable<String, AttrType> getAttrTypesMap() {
		return m_attrTypesMap;
	}

	public void setAttrTypesMap(Hashtable<String, AttrType> attrTypesMap) {
		this.m_attrTypesMap = attrTypesMap;
	}

	public String getTargetClassAttributeName() {
		return m_classAttributeName;
	}

	public void setTargetClassAttributeName(String targetAttributeName) {
		this.m_classAttributeName = targetAttributeName;
	}

	
	public Map<String, Integer> getClassLableValuesMap() {
		return this.m_classLableValuesMap;
	}

	public void setClassLableValuesMap(Map<String, Integer> m_classLableValuesMap) {
		this.m_classLableValuesMap = m_classLableValuesMap;
	}

	public DataSet getTraingDataSet() {
		return m_traingDataSet;
	}

	public void setTraingDataSet(DataSet traingDataSet) {
		this.m_traingDataSet = traingDataSet;
	}

	public List<Data> getValidDataset() {
		return m_validDatasetList;
	}

	public void setValidDataset(List<Data> validDataset) {
		this.m_validDatasetList = validDataset;
	}

	public List<Integer> getAttributeIndexs() {
		return m_attributeIndexsList;
	}

	public void setAttributeIndexs(List<Integer> attributeIndexs) {
		this.m_attributeIndexsList = attributeIndexs;
	}

	public List<Integer> getClassIndexs() {
		return m_classIndexsList;
	}

	public void setClassIndexs(List<Integer> classIndexs) {
		this.m_classIndexsList = classIndexs;
	}


	public boolean isUseDiscretization() {
		return m_UseDiscretization;
	}

	public void setUseDiscretization(boolean m_UseDiscretization) {
		this.m_UseDiscretization = m_UseDiscretization;
	}

	/**
	 * Whether to use discretization than normal distribution for numeric
	 * attributes
	 */
	protected boolean m_UseDiscretization = false;

	/*** The precision parameter used for numeric attributes */
	protected static final double DEFAULT_NUM_PRECISION = 0.01;

	public double likehood(Data testData) {
		return 0;
	}

	/**
	 * read Data from file and init the trainData of Decision Tree
	 * 
	 * @author Administrator
	 * @param fileName
	 */
	protected DataSet readData(String fileName) {
		// log.debug("reading sample data from " + fileName);
		// List<String> listString = Tool.readFileByLines(fileName); //get the
		// collection of lines in the file
		// String attributeNameLineString = null;
		// for (int f = 0; f < listString.size(); f++) {
		// if(Tool.formatStr(listString.get(f), " +")!=""){
		// attributeNameLineString = listString.get(f);
		// break;
		// }
		// }
		// if(attributeNameLineString==null){
		// log.error("the file is empty!");
		// }
		// // get the first line then get the TargetAttribute, attributeName
		// String[] linelist = (String[]) Tool.split(attributeNameLineString,
		// ":", 0);
		// this.targetAttributeName = linelist[0].toLowerCase();
		// this.attributeNamesList = Arrays.asList((String[]) Tool.split(
		// linelist[1].trim(), " +", 0));
		// listString.remove(0);
		// // get the sample data
		// this.traingData.setTraingingDatas(readFromFile(listString,
		// traingData.getTargetAttributeDictionary()));
		// this.traingData.setTrainingDataNum(this.traingData.getTraingingDatas().size());
		// this.attributeNum = attributeNamesList.size();
		// int attrNum = getAttributeNum();
		// this.setAttributeNum(attrNum);
		// for (int i = 0; i <attrNum ; i++) {
		// this.attributeIndexs.add(i);
		// }
		// traingData.setTrainingDataNum(traingData.getTraingingDatas().size());
		// log.debug("the num of the training data is" +
		// traingData.getTrainingDataNum());
		// int classIndex=0;
		// for (Enumeration<String> enumeration =
		// traingData.getTargetAttributeDictionary().keys();enumeration.hasMoreElements();)
		// {
		// this.classTargetName.add(enumeration.nextElement());
		// this.classIndexs.add(classIndex++);
		// }
		return null;
	}

	/**
	 * add a row of data's attribute values into the
	 * targetAttributeDistributionTable (classValue ,num) For Category attribute
	 * 
	 * @param dataClassValue
	 * @param targetAttributeDistributionTable
	 *            (classValue ,num)
	 */
	public void addDataClassValueIntoClassAttributeDistributitonTable(
			String dataClassValue,
			Map<String, Integer> targetAttributeDistributionTable) {
		if (targetAttributeDistributionTable.get(dataClassValue) == null) {
			targetAttributeDistributionTable.put(dataClassValue, 1);
		} else {
			int oldvalue = targetAttributeDistributionTable.get(dataClassValue);
			targetAttributeDistributionTable.put(dataClassValue, oldvalue + 1);
		}
	}

	protected void  readArffProcessEachLine(   BufferedReader br,String line,Pattern pattern,List<Data> dataList,Hashtable<String, Integer> NumOfEachattributeValueTable) throws IOException {
		String lineWithoutSpaceString = Tool.removeReduntSpace(line)
				.toLowerCase();
		if (lineWithoutSpaceString.equalsIgnoreCase("")) {
//			continue;
			return ;
		}
		Matcher matcher = pattern.matcher(lineWithoutSpaceString);
		if(DataConverter.skipLine(lineWithoutSpaceString)){
			return ;
		}
		if (matcher.find()) {
			// read the @attribute init the attributeNamesList
			String attributeName = matcher.group(1).trim().toLowerCase();
			m_attributeNamesList.add(attributeName);
			m_attributeNum++; // init the attributeNum
			// add attributeName
			// get the attributevalue for each attributeName
			AttrType attrType =( matcher.group(3).trim()
					.equalsIgnoreCase(AttrType.Continuous.toString())||
					 matcher.group(3).trim()
						.equalsIgnoreCase("numeric"			
							)) ? AttrType.Continuous
					: AttrType.Category;
			m_attrTypesMap.put(attributeName, attrType);
//			continue;
			return ;
		} else if (lineWithoutSpaceString.startsWith("@data")) {
			// read the @data to init the m_classLabelValuesList
			this.m_classAttributeName = m_attributeNamesList
					.get(m_attributeNamesList.size() - 1);
			m_attributeNamesList
					.remove(m_attributeNamesList.size() - 1);
			this.m_classType = this.m_attrTypesMap.get(m_classAttributeName);
			this.m_attrTypesMap.remove(m_classAttributeName);
			m_attributeNum--;
			String lineString = br.readLine();
			while( lineString!=null)  {
//				log.debug(lineString);
				while (DataConverter.skipLine(lineString)) {
					lineString=br.readLine();
					if(lineString==null) break;
				}
				if(lineString==null)  break;
				Data data = DataConverter.convert(m_datasetSize, lineString);
				if(data!=null){
				dataList.add(data); m_datasetSize++;
				addDataClassValueToClassLabelValuesMap(data);
				// get the targetAttributeClassDistribution
				if(m_classType.isCategorical()){
				// className, atttributeName, attributeValue,num
				addDataAttributeValuesToAttributeValueDistributionOfEveryAttributeTable(data, m_attributeValueNumOfEveryClassHashtable,NumOfEachattributeValueTable);
				}else{
					// classType is continuous
					}
				}
				lineString = br.readLine();
				if(lineString==null) break;
			}// while
		} 
	}
	
	/**
	 * read arff File and initiate trainData, attribute,attributevalue
	 */
	protected DataSet readARFF(String filePath) {
		/** attributeValue , num */
		Hashtable<String, Integer> NumOfEachattributeValueTable = new Hashtable<String, Integer>();
		/** AttributeName attributeValue , num */
		List<Data> dataList = new ArrayList<Data>();
		try {
			FileReader fr = new FileReader(new File(filePath));
			BufferedReader br = new BufferedReader(fr);
			String line;
			Pattern pattern = Pattern.compile(Parameter.attributePatternString);
			m_datasetSize = 1; // record start from 1
			while ((line = br.readLine()) != null) { // read and process each
				log.trace(line);
				readArffProcessEachLine(br, line, pattern,  dataList, NumOfEachattributeValueTable);
			}
			br.close();
			if(m_classType.isNumerical()){
			double avg = MathUtil.getAvg(m_classLableValuesMap);
			double std = MathUtil.getStdOfHashTable(m_classLableValuesMap,avg);
//			//get the total continuous data class value distribution
			m_classContinousDistribution = new ContinousDistribution(avg,std);
			}
			m_traingDataSet.setAttributeNum(m_attributeNum);
			m_traingDataSet.setDatasetSize(m_datasetSize - 1);
			m_traingDataSet.setClassValueMap(m_classLableValuesMap);
			m_traingDataSet.setAttributeNamesList(m_attributeNamesList);
			m_traingDataSet.setDataList(dataList);
			m_traingDataSet.setAttributeNum(m_attributeNum);
			m_traingDataSet.setAttributeTypeTable(getAttrTypesMap(m_attrTypesMap));
			this.m_classNum = m_classLableValuesMap.size();
			m_traingDataSet.setClassNum(m_classNum);
			initClassIndexesAndAttributeIndexes();
			m_traingDataSet.setClassIndexs(m_classIndexsList);
			m_traingDataSet.setClassName(m_classAttributeName);
			m_traingDataSet.setClassType(m_classType);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return m_traingDataSet;
	}

	/**
	 * add the data's different class value to classvalue list</br>
	 * @param data
	 * @see {@link #m_classLabelValuesList}
	 */
	public void addDataClassValueToClassLabelValuesMap(Data data){
		boolean contain=false;
		contain = m_classLableValuesMap.containsKey(data.getClassValue());
		if (!contain) {
			this.m_classLableValuesMap.put(data.getClassValue(), 1);
			}else{
			int oldNum=	this.m_classLableValuesMap.get(data.getClassValue());
			this.m_classLableValuesMap.put(data.getClassValue(), ++oldNum);
			}
	}

	/**
	 * init the m_attributeIndexs by 0 ,1,2... m_attributeNum-1 </br> init the
	 * classLabel values and classIndexes </br>
	 * 
	 * @author apple
	 */
	public void initClassIndexesAndAttributeIndexes() {
		for (int classValueIndex = 0; classValueIndex < m_classLableValuesMap.size()
				; classValueIndex++) {
			m_classIndexsList.add(classValueIndex);
		}
		for (int i = 0; i < m_attributeNum; i++)
			m_attributeIndexsList.add(i);
	}

	public Hashtable<Integer, AttrType> getAttrTypesMap(
			Hashtable<String, AttrType> attrTypeHashtable) {
		Hashtable<Integer, AttrType> attrTypeTable = new Hashtable<Integer, AttrType>();
		for (String attributeName : m_attributeNamesList) {
			attrTypeTable.put(m_attributeNamesList.indexOf(attributeName),
					m_attrTypesMap.get(attributeName));
		}
		return attrTypeTable;
	}

	/**
	 * get the condition P of attribute value in class value </br>
	 * 
	 * @author apple
	 * @return
	 */
	private double getConditionPOfAttributeValueInClassValueOfCategoryAttribute(
			int classValueIndex, int attributeIndex,
			String attributeValueInEachClass,boolean smooth) {
		double conditionP = 0.0d;
		String classValue = getClassValueByIndex(classValueIndex);
		String attributeName = this.m_attributeNamesList.get(attributeIndex);
		AttrType attrType = m_attrTypesMap.get(attributeName);
		int attributeValueNumOfAttributeOfClassValue = -1;
		assert (attrType.equals(AttrType.Category)) ;
			 attributeValueNumOfAttributeOfClassValue = getNumOfAttributeValueInClassValue(classValue, attributeName, attributeValueInEachClass);
			// /** get the class Value num */
			 int classValueTotal = m_classLableValuesMap.get(classValue);
			 if(!smooth){
			// calc the value of p(x_{i} | y_{i})
			conditionP = ((double) attributeValueNumOfAttributeOfClassValue)
					/ classValueTotal;
			 }else{//use smooth
				 int classNum = m_attributeValueNumOfEveryClassHashtable.get(classValue).get(attributeName).size();
				 conditionP =( ((double) attributeValueNumOfAttributeOfClassValue)+1)
							/ (classValueTotal+ classNum); 
			 }
		 log.debug("the condition p for " + attributeName
		 + " = " + attributeValueInEachClass
		 + " in class" + " = " + classValue + " is "
		 + attributeValueNumOfAttributeOfClassValue + " /"
		 + classValueTotal + "= " + conditionP);
		return conditionP;
	}
	
	/**
	 * get the num in m_attributeValueNumOfEveryClassHashtable
	 * @param classValue
	 * @param attributeName
	 * @param attributeValueInEachClass 
	 * @see  m_attributeValueNumOfEveryClassHashtable
	 * {@link #m_attributeValueNumOfEveryClassHashtable}
	 * @return
	 */
	public int getNumOfAttributeValueInClassValue(String classValue,String attributeName,String attributeValueInEachClass){
		 if(!m_attributeValueNumOfEveryClassHashtable.containsKey(classValue))
			 return 0;
		 if(!m_attributeValueNumOfEveryClassHashtable.get(classValue).containsKey(attributeName))
			 return 0;
		 if(!m_attributeValueNumOfEveryClassHashtable.get(classValue).get(attributeName).containsKey(attributeValueInEachClass) )
			 return 0;
		 return this.m_attributeValueNumOfEveryClassHashtable
					.get(classValue).get(attributeName)
					.get(attributeValueInEachClass);
		 
//		this.m_Model.m_attributeValueNumOfEveryClassHashtable
	}

	/**
	 * get the condition P of attribute value in class value </br>
	 * 
	 * @author apple
	 * @return
	 */
	private double getConditionPOfAttributeValueInClassValueOfContinuousAttribute(
			int  classValueIndex, int attributeIndex,
			String attributeValueInEachClass,Boolean smooth) {
		double conditionP = 0.0d;
		String attributeName = this.m_attributeNamesList.get(attributeIndex);
		double attributeValue = Double.valueOf(attributeValueInEachClass);
		assert(m_attrTypesMap.get(attributeName).isNumerical()) ;
		if(!smooth){
		conditionP=	m_Model.getContinuousAttributeProbabilityOfClassTable().get(classValueIndex).get(attributeIndex).getProbabilityForValue(attributeValue);
		}else{
			//To DO: smooth
			conditionP = m_Model.getContinuousAttributeProbabilityOfClassTable().get(classValueIndex).get(attributeIndex).getProbabilityForValue(attributeValue);
		}
		 log.debug("the condition p for " + attributeName
				 + " = " + attributeValueInEachClass
				 + " in class" + " = " +getClassValueByIndex(classValueIndex) + " is "
				 + conditionP);
		return conditionP;
	}

	/**
	 * get condition P for attribute value  in classValue
	 * @param classValue
	 * @param attributeIndex
	 * @param attributeValueInEachClass  namely , attributeValue
	 * @return
	 */
	public double getConditionPOfAttributeValue(int classValueIndex, int attributeIndex,
			String attributeValueInEachClass,Boolean smooth){
		double conditionP = 0.0d;
		assert(classValueIndex!=-1);
		if(m_classType.isCategorical()){
		String attributeName = this.m_attributeNamesList.get(attributeIndex);
		AttrType attrType = m_attrTypesMap.get(attributeName);
		if(attrType.equals(AttrType.Category)){
			// get conditionP from table
			conditionP = getConditionPOfAttributeValueInClassValueOfCategoryAttribute(classValueIndex, attributeIndex, attributeValueInEachClass,smooth);
		}else{
			// use the Normal Distribution get the conditionP
			conditionP = getConditionPOfAttributeValueInClassValueOfContinuousAttribute(classValueIndex, attributeIndex, attributeValueInEachClass,smooth);
		}
		}//m_classType is categorical
		else{
			//ToDo : m_classType is continuous
		}
		return conditionP;
	}
	
	/**
	 * 
	 * @param attributeProbalityOfClassTable
	 * @param classValueIndex
	 * @param attributeIndex
	 * @param attributeValueIndex
	 * @param contionP
	 */
	public void addContionPofAttributeValue(
			Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> attributeProbalityOfClassTable,
			int classValueIndex, int attributeIndex, int attributeValueIndex,
			double contionP) {
		if (!attributeProbalityOfClassTable.containsKey(classValueIndex)) {
			Hashtable<Integer, Double> hashtable = new Hashtable<Integer, Double>();
			hashtable.put(attributeValueIndex, contionP);
			Hashtable<Integer, Hashtable<Integer, Double>> hashtable2 = new Hashtable<Integer, Hashtable<Integer, Double>>();
			hashtable2.put(attributeIndex, hashtable);
			// <classValueIndex, <attributeNameIndex
			// ,<attributeValueIndex ,p> >
			attributeProbalityOfClassTable.put(classValueIndex, hashtable2);
		} else {
			if (!attributeProbalityOfClassTable.get(classValueIndex)
					.containsKey(attributeIndex)) {
				Hashtable<Integer, Hashtable<Integer, Double>> hashtable = attributeProbalityOfClassTable
						.get(classValueIndex);
				Hashtable<Integer, Double> ha = new Hashtable<Integer, Double>();
				ha.put(attributeValueIndex, contionP);
				hashtable.put(attributeIndex, ha);
				// attributeProbalityOfClassTable.put(
				// classValueIndex, hashtable);
			} else {
				Hashtable<Integer, Double> hashtable = attributeProbalityOfClassTable
						.get(classValueIndex).get(attributeIndex);
				hashtable.put(attributeValueIndex, contionP);
			}
		}
	}

	/**
	 * 
	 * @param data
	 * @param attributeValueNumOfEveryClassHashtable
	 * @param NumOfEachattributeValueTable
	 */
	public void addDataAttributeValuesToAttributeValueDistributionOfEveryAttributeTable(
		Data data,
			Hashtable<String, Hashtable<String, Hashtable<String, Integer>>> attributeValueNumOfEveryClassHashtable,
			Hashtable<String, Integer> NumOfEachattributeValueTable) {
		String[] attributeValues = new String[data.getAttributeNum()+1];
		int i;
		for(i=0;i<data.getAttributeNum();i++){
			attributeValues[i] = data.getAttributevaluesList().get(i);
		}
		attributeValues[i] = data.getClassValue();
		 addDataAttributeValuesToAttributeValueDistributionOfEveryAttributeTable(
				 attributeValues,
					 attributeValueNumOfEveryClassHashtable,
					 NumOfEachattributeValueTable);
	}
	
	
	/**
	 * add a row of data's attribute values and update the
	 * attributeValueNumOfEveryClassHashtable </br> (String ,String, String
	 * ,Integer) </br> className, atttributeName, attributeValue,num </br>
	 * 
	 * @param row
	 * @param startIndex
	 * @param size
	 * @param attributeValueNumOfEveryClassHashtable
	 *            (String ,String , Integer)
	 * @param NumOfEachattributeValueTable
	 *            (String, Integer) attributeValue Num
	 */
	public void addDataAttributeValuesToAttributeValueDistributionOfEveryAttributeTable(
			String[] row,
			Hashtable<String, Hashtable<String, Hashtable<String, Integer>>> attributeValueNumOfEveryClassHashtable,
			Hashtable<String, Integer> NumOfEachattributeValueTable) {
		// get every attribute value
		String dataClassValue = row[row.length - 1];
		for (int i = 0; i < row.length - 1; i++) {
			String dataAttributeValue = row[i];
			String attributeName = this.m_attributeNamesList.get(i);
			// <className,<attributeName <attributeValue num> >
			if (!attributeValueNumOfEveryClassHashtable
					.containsKey(dataClassValue)) {
				Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
				hashtable.put(dataAttributeValue, 1);
				Hashtable<String, Hashtable<String, Integer>> v = new Hashtable<String, Hashtable<String, Integer>>();
				v.put(attributeName, hashtable);
				attributeValueNumOfEveryClassHashtable.put(dataClassValue, v);
			} else {
				// contain classvalue
				if (attributeValueNumOfEveryClassHashtable.get(dataClassValue)
						.containsKey(attributeName)) {
					if (!attributeValueNumOfEveryClassHashtable
							.get(dataClassValue).get(attributeName)
							.containsKey(dataAttributeValue)) {
						attributeValueNumOfEveryClassHashtable
								.get(dataClassValue).get(attributeName)
								.put(dataAttributeValue, 1);
					} else {
						int old = attributeValueNumOfEveryClassHashtable
								.get(dataClassValue).get(attributeName)
								.get(dataAttributeValue);
						attributeValueNumOfEveryClassHashtable
								.get(dataClassValue).get(attributeName)
								.put(dataAttributeValue, old + 1);
					}
				} else {
					Hashtable<String, Hashtable<String, Integer>> vHashtable = attributeValueNumOfEveryClassHashtable
							.get(dataClassValue);
					Hashtable<String, Integer> nHashtable = new Hashtable<String, Integer>();
					nHashtable.put(dataAttributeValue, 1);
					vHashtable.put(attributeName, nHashtable);
				}
			}
			// 属性值集合，<AttributeName <attributeValue , num> >
			if (m_attributeValueDistributionOfEveryAttributeTable
					.get(attributeName) == null) {
				/** AttributeName attributeValue , num */
				if (NumOfEachattributeValueTable.size() == 0) {
					NumOfEachattributeValueTable.put(dataAttributeValue, 1);
					m_attributeValueDistributionOfEveryAttributeTable.put(
							attributeName, NumOfEachattributeValueTable);
				} else {
					Hashtable<String, Integer> newAttributeValueNum = new Hashtable<String, Integer>();
					newAttributeValueNum.put(dataAttributeValue, 1);
					m_attributeValueDistributionOfEveryAttributeTable.put(
							attributeName, newAttributeValueNum);
				}
			} else {
				NumOfEachattributeValueTable = (Hashtable<String, Integer>) m_attributeValueDistributionOfEveryAttributeTable
						.get(attributeName);
				if (NumOfEachattributeValueTable.get(dataAttributeValue) == null) {
					NumOfEachattributeValueTable.put(dataAttributeValue, 1);
				} else {
					int oldnum = (Integer) NumOfEachattributeValueTable
							.get(dataAttributeValue);
					NumOfEachattributeValueTable.put(dataAttributeValue,
							new Integer(oldnum + 1));
				}
				m_attributeValueDistributionOfEveryAttributeTable.put(
						attributeName, NumOfEachattributeValueTable);
			}
		} // for
	}

	/**
	 * find the classIndex in m_classLabelValuesList for classValue
	 * @see {@link #m_classLableValuesMap}
	 * @param classValue
	 * @return
	 */
	public int getClassValueIndexByValue(String classValue) {
		int index= 0;
		 for (Iterator<String> it =  m_classLableValuesMap.keySet().iterator();it.hasNext();index++)
		   {
		   String key = it.next();
		   if(key.equalsIgnoreCase(classValue)){
		    log.trace( key+"="+ m_classLableValuesMap.get(key));
		    return index; 
		   }
		   continue;
		   }
		 return -1;
	}

	/**
	 * init classIndexes start from 0 ,1 ,2 ... and classValues
	 */
	public void initClassIndexesAndClassValues() {
		 for(int classIndex = 0;classIndex<m_classNum;classIndex++){
			m_classIndexsList.add(classIndex);
		}
		m_traingDataSet.setClassIndexs(m_classIndexsList);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	/**
	 * get the class value for data the class value is find in
	 * m_classLabelValuesList
	 */
	@Override
	public String classifyInstance(Classifier classifier, Data classifyData) {
		int maxClassValueIndex = classifyInstanceReturnClassValueIndex(
				classifier, classifyData);
		assert(maxClassValueIndex>=0);
		return getClassValueByIndex(maxClassValueIndex);
	}

	@Override
	public int classifyInstanceReturnClassValueIndex(Classifier classifier,
			Data classifyData) {
		return classifyInstanceReturnClassValueIndex(classifier,
				classifyData.getAttributevaluesList());
	}

	@Override
	/**
	 * classify data's attributeValuesOfData with classifier 
	 */
	public int classifyInstanceReturnClassValueIndex(Classifier classifier,
			List<String> attributeValuesOfData) {
		List<Double> pList = predictClassProbalityOfData(attributeValuesOfData,true,true);
		int maxClassValueIndex = -1;
		double maxP = -1;
		for (int i = 0; i < pList.size(); i++) {
			if (maxP < pList.get(i)) {
				maxP = pList.get(i);
				maxClassValueIndex = i;
			}
		}
		return maxClassValueIndex;
	}

	@Override
	public String classifyInstance(Classifier classifier,
			List<String> attributeValuesOfData) {
		return getClassValueByIndex(classifyInstanceReturnClassValueIndex(classifier,
						attributeValuesOfData));
	}

	@Override
	public List<Integer> classifyDataSet(Classifier classifier,
			List<Data> dataList) {
		List<Integer> pList = new ArrayList<Integer>(dataList.size());
		for (Data classifyData : dataList) {
			pList.add(classifyInstanceReturnClassValueIndex(classifier,
					classifyData));
		}
		return pList;
	}

	@Override
	public double getClassifyAccuracy(Classifier classifier, List<Data> dataList) {
		double acc = -1;
		try {
			acc = Accuracy.getInstance().measure(
					classifier.classifyDataSet(classifier, dataList), dataList,
					new ArrayList<String>(((NaiveBayes) classifier).getClassLableValuesMap().keySet()));
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return acc;
	}

	@Override
	/**
	 * build calssifier 
	 * @author apple
	 */
	public Classifier buildClassifierOverrite(DataSet data) {
		if (Parameter.deleteMissingValue) {
			m_traingDataSet.deleteWithMissing();
		}
		if (createNavieBayesModel() == null) {
			return null;
		}
		return this;

	}

	@Override
	public String showClassifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearClassifier() {
		// TODO Auto-generated method stub

	}

	/**
	 * the core algorithm </b> create the NavieBayesModel</br> which include the
	 * p(y_{i}), p(x_{i}|y_{j}) and so on
	 *  classValueIndex, attributeName ,attributeValueIndex ,p 
	* @return
	*/
//	 * {yes={
//	 *	 temperature={mild=4, hot=2, cool=3},
//		 windy={false=6, true=3}, humidity={high=3, normal=6},
//		 outlook={rainy=3, sunny=2, overcast=4}},
//		 no={temperature={mild=2, hot=2, cool=1}, windy={false=2, true=3},
//		 humidity={high=4, normal=1}, outlook={rainy=2, sunny=3}}}
//		 {1={
//		3={1=0.6, 0=0.4},
//		2={1=0.2, 0=0.8}, 
//		1={2=0.2, 1=0.4, 0=0.4},
//	 0={2=0.6, 1=0.0, 0=0.4}},
//		 0={
//		3={1=0.3333333333333333, 0=0.6666666666666666},
//	 2={1=0.6666666666666666, 0=0.3333333333333333},
//	 1={2=0.3333333333333333, 1=0.2222222222222222, 0=0.4444444444444444},
//	 0={2=0.2222222222222222, 1=0.44444444444444444, 0=0.3333333333333333}}}
	
	public NaiveBayesModel createNavieBayesModel() {
		/** ClassValueIndex , attributeNameIndex, p */
		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> attributeProbalityOfClassTable = new Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>>();
		
		if(m_classType.isCategorical()){
		List<Double> classP = getProbabilityOfClassValue();
		this.m_Model.setClassProbalityList(classP);
		/** get the P for each ClassValue */
		double p1 = 0.0;
		for (int classValueIndex = 0; classValueIndex < this.m_classNum; classValueIndex++) {
			String classValue = getClassValueByIndex(classValueIndex);
			for (int attributeIndex = 0; attributeIndex < this.m_attributeNum; attributeIndex++) {
				String attributeName = this.m_attributeNamesList
						.get(attributeIndex);
				AttrType attrType = this.m_attrTypesMap.get(attributeName);
				log.debug("attributeName: " + attributeName + " attributeType: "
						+ attrType);
				/** get the class Value num */
				if (attrType.isCategorical()) {
					int classValueTotal =getClassValueNum(classValue);
					int attributeValueIndex = 0;
					/** get the condition P */
					for (String attributeValueInEachClass : getAttributeValuesOfAttribute(attributeName)) {
						int attributeValueNumOfAttributeOfClassValue =getAttributeValueNumInClass(classValue,attributeName,attributeValueInEachClass);
						/** calc the value of p(x_{i} | y_{i}) */
						p1 = ((double) attributeValueNumOfAttributeOfClassValue)
								/ classValueTotal; // calc the p for each
													// attributeValue
						log.debug("the condition p for " + attributeName
								+ " = " + attributeValueInEachClass
								+ " in class" + " = " + classValue + " is "
								+ attributeValueNumOfAttributeOfClassValue
								+ " /" + classValueTotal + "= " + p1);
						addContionPofAttributeValue(
								attributeProbalityOfClassTable,
								classValueIndex, attributeIndex,
								attributeValueIndex, p1);
						attributeValueIndex++;
					} // for
				} // if
				else if (attrType == AttrType.Continuous) {
					/** contious attribute */
					double avg = MathUtil
							.getAvg(m_attributeValueNumOfEveryClassHashtable
									.get(classValue).get(attributeName));
					double std = MathUtil
							.getStd(m_attributeValueNumOfEveryClassHashtable
									.get(classValue).get(attributeName),avg);
					ContinousDistribution continousDistribution = new ContinousDistribution(
							avg, std);
					log.debug("the continousDistribution for attribute "+attributeName +" in class "+classValue +" is "+continousDistribution);
					m_Model.addContinousDistributionToTable(classValueIndex,
							attributeIndex, continousDistribution);
				}
			} // for
		}
		}
		
		else{
			this.m_Model.setClassContinousDistribution(m_classContinousDistribution);
			double mean ;
			double std;
			for (int i=0;i<m_attributeNum;i++){
				if(getTypeOfAttribute(i).isNumerical()){
				Collection<String> collections=	m_traingDataSet.getDataSetAttributeValuesByIndex(i);
				mean = MathUtil.getAvgOfString(collections);
				std = MathUtil.getStdOfString(collections, mean);
				m_Model.getContinuousAttributeProbabilityOfContinuousClassTable().put(i, new ContinousDistribution(mean, std));
				}
			}
			throw new RuntimeErrorException(new Error("type error!"));
		}
	
		this.m_Model
				.setCategoryAttributeProbalityOfClassTable(attributeProbalityOfClassTable);
		
		this.m_Model.setClassNum(m_classNum);
		assert (m_Model != null);
		return this.m_Model;
	}

	public int getClassValueNum(String classValue){
		int classValueTotal = 0;
		if(this.m_classLableValuesMap.get(classValue)!=null){
		 classValueTotal = this.m_classLableValuesMap.get(classValue);
		}
		 return classValueTotal;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	/**
	 * get classify accuracy for the classifier on dataset
	 * @return classify accuracy 
	 */
	@Override
	public double getClassifyAccuracy(Classifier classifier, DataSet dataSet) {
		double acc = -1;
		try {
			List<String> classValueslist = new ArrayList<String>(((NaiveBayes) classifier).m_classLableValuesMap.keySet());
			acc = Accuracy.getInstance().measure(
					classifier.classifyDataSet(classifier, dataSet),
					dataSet.getDataList(),
					classValueslist);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return acc;
	}
	
	public int getAttributeValueNumInClass(String classValue,String attributeName,String attributeValueInEachClass){
		int attributeValueNumOfAttributeOfClassValue;
		if (this.m_attributeValueNumOfEveryClassHashtable
				.get(classValue).get(attributeName)
				.get(attributeValueInEachClass) == null) {
			attributeValueNumOfAttributeOfClassValue = 0;
		} else {
			attributeValueNumOfAttributeOfClassValue = this.m_attributeValueNumOfEveryClassHashtable
					.get(classValue).get(attributeName)
					.get(attributeValueInEachClass);
			log.debug("the num of " + attributeValueInEachClass
					+ "of " + attributeName + " in "
					+ classValue + " is "
					+ attributeValueNumOfAttributeOfClassValue);
		}
		return attributeValueNumOfAttributeOfClassValue;
	}
	
	
	@Override
	/**
	 * classify the dataset with classifier,preditions for dataSet 
	 * @return  classIndex in m_classLableValuesMap  
	 * @param classfier
	 */
	public List<Integer> classifyDataSet(Classifier classifier, DataSet dataSet) {
		return classifyDataSet(classifier, dataSet.getDataList());
	}

	/**
	 * 
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeString(out, m_algorithmName);
		HdfsWritableUtils.writeCollection(out, m_attributeIndexsList);
		WritableUtils.writeString(out, m_classAttributeName);
		out.writeInt(m_attributeNum);
		m_Model.write(out);
		HdfsWritableUtils.writeCollection(out,m_attributeNamesList);
		writeAttributeValueNumOfEveryClassHashtable(out);
		writeAttributeValueDistributionOfEveryAttributeTable(out);
		writeAttrTypesMap(out);
		m_traingDataSet.write(out);
		HdfsWritableUtils.writeCollection(out,m_attributeNamesList);
		HdfsWritableUtils.writeCollection(out,m_classIndexsList);
		out.writeInt(m_classNum);
		m_classType.write(out);
	}

	/**
	 * Hashtable<String, Hashtable<String, Hashtable<String, Integer>>>
	 * @param out
	 * @throws IOException
	 */
	private Hashtable<String, Hashtable<String, Hashtable<String, Integer>>> readAttributeValueNumOfEveryClassHashtable(DataInput in) throws IOException {
		if(in==null) return null;
		 HdfsMapWritable hdfsMapWritable = new HdfsMapWritable();
		 hdfsMapWritable.readFields(in);
		Map<Writable, Writable> mapWritable= hdfsMapWritable.getInstance();
		Hashtable<String, Hashtable<String, Hashtable<String, Integer>>> attributeValueNumOfEveryClassHashtable = 
				new Hashtable<String, Hashtable<String,Hashtable<String,Integer>>>(mapWritable.size());
		for(Writable e: mapWritable.keySet()){
			String key = new String( ((Text) e).getBytes());
			Writable value = mapWritable.get(e);
			Hashtable<Text, Hashtable<Text, IntWritable>> wHashtable1 = (Hashtable<Text, Hashtable<Text, IntWritable>>) value;
			Hashtable<String, Hashtable<String, Integer>> hashTable1= new Hashtable<String, Hashtable<String, Integer>>(wHashtable1.size());
			for(Text k:wHashtable1.keySet()){
				Hashtable<Text, IntWritable> value1= wHashtable1.get(k);	
				String key2 =new String( k.getBytes());
				Hashtable<String, Integer> hashtable2 = new Hashtable<String, Integer>(value1.size());
				for(Text k1:value1.keySet()){
					String key3= new String(k1.getBytes());
					int d = value1.get(k1).get();
					hashtable2.put(key3, d);
				}
				hashTable1.put(key2, hashtable2);
			}
				attributeValueNumOfEveryClassHashtable.put(key,hashTable1);
		}
		return attributeValueNumOfEveryClassHashtable;
	}
	
	/**
	 * Hashtable<String, Hashtable<String, Hashtable<String, Integer>>>
	 * @param out
	 * @throws IOException
	 */
	private void writeAttributeValueNumOfEveryClassHashtable(DataOutput out) throws IOException {
		if(m_attributeValueNumOfEveryClassHashtable==null){
			out.write(null);
		}
		MapWritable mapWritable = new MapWritable();
		for(String e: m_attributeValueNumOfEveryClassHashtable.keySet()){
			MapWritable mapWritable1 = new MapWritable();
			Hashtable<String, Hashtable<String, Integer>> hashtable = m_attributeValueNumOfEveryClassHashtable.get(e);
			for (String string: hashtable.keySet() ) {
				Hashtable<String, Integer> local = hashtable.get(string);
				MapWritable mapWritable2 = new MapWritable();
				for(String string2: local.keySet()){
					mapWritable2.put(new Text(string2), new IntWritable(local.get(string2) ) );
				}
				mapWritable1.put(new Text(string), mapWritable2);
			}
			mapWritable.put(new Text(e), mapWritable1);
		}
		HdfsMapWritable hdfsMapWritable = new HdfsMapWritable(mapWritable);
		hdfsMapWritable.write(out);
	}
	
	/**
	 * Hashtable<String, Hashtable<String, Integer>>
	 * @param out
	 * @throws IOException 
	 */
	private void writeAttributeValueDistributionOfEveryAttributeTable(DataOutput out) throws IOException{
		if(m_attributeValueDistributionOfEveryAttributeTable==null) 
			out.write(null);
		MapWritable mapWritable = new MapWritable();
		for(Entry<String, Hashtable<String, Integer>> e:  m_attributeValueDistributionOfEveryAttributeTable.entrySet()){
			Hashtable<String, Integer> hashtable = e.getValue();
			for(String s: hashtable.keySet()){
				mapWritable.put(new Text(s), new IntWritable(hashtable.get(s)) );
			}
		}
		HdfsMapWritable hdfsMapWritable = new HdfsMapWritable(mapWritable);
		hdfsMapWritable.write(out);
	}
	
	/**
	 * Hashtable<String, Hashtable<String, Integer>>
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private Hashtable<String, Hashtable<String, Integer>> readAttributeValueDistributionOfEveryAttributeTable(DataInput in) throws IOException{
		if(in ==null)  return null;
		Hashtable<String, Hashtable<String, Integer>> attributeValueDistributionOfEveryAttributeTable=new Hashtable<String, Hashtable<String,Integer>>();
		 HdfsMapWritable hdfsMapWritable = new HdfsMapWritable();
		 hdfsMapWritable.readFields(in);
		Map<Writable, Writable> mapWritable= hdfsMapWritable.getInstance();
		for(Writable e: mapWritable.keySet()){
			String key = new String( ((Text) e).getBytes());
			Writable value = mapWritable.get(e);
			Hashtable<Text,IntWritable> wHashtable1 = (Hashtable<Text,IntWritable>) value;
			Hashtable<String, Integer> hashTable1= new Hashtable<String,  Integer>(wHashtable1.size());
			for(Text k:wHashtable1.keySet()){
				String key2 =new String( k.getBytes());
				int d = wHashtable1.get(k).get();
					hashTable1.put(key2, d);
				}
			attributeValueDistributionOfEveryAttributeTable.put(key, hashTable1);
			}
		return attributeValueDistributionOfEveryAttributeTable;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		m_algorithmName =   WritableUtils.readString(in);
		m_attributeIndexsList = new ArrayList(HdfsWritableUtils.readCollection(in));
		m_classAttributeName = WritableUtils.readString(in);
		m_attributeNum = in.readInt();
		m_Model = new NaiveBayesModel();	m_Model.readFields(in);
		m_attributeNamesList =new ArrayList(HdfsWritableUtils.readCollection(in));
		readAttributeValueNumOfEveryClassHashtable(in);
		readAttributeValueDistributionOfEveryAttributeTable(in);
		readAttrTypesMap(in);
		m_traingDataSet.readFields(in);
		m_attributeNamesList =new ArrayList(HdfsWritableUtils.readCollection(in));
		m_classIndexsList =new ArrayList(HdfsWritableUtils.readCollection(in));
		m_classNum= in.readInt();
		m_classType.readFields(in);
	}

	/**
	 *  Hashtable<String, AttrType> m_attrTypesMap
	 *  {@link m_attrTypesMap}
	 * @param out
	 */
	private void writeAttrTypesMap(DataOutput out){
		Map<Writable,Writable> map = new HashMap<Writable, Writable>(m_attrTypesMap.size());
		for(Entry<String, AttrType> entry: m_attrTypesMap.entrySet()){
			map.put(new Text(entry.getKey()),entry.getValue());
		}
		MapWritable mapWritable = new MapWritable();
		mapWritable.putAll(map);
		try {
			mapWritable.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 *  Hashtable<String, AttrType> m_attrTypesMap
	 *  {@link m_attrTypesMap}
	 * @param out
	 */
	private void readAttrTypesMap(DataInput in){
		MapWritable mapWritable = new MapWritable();
		try {
			mapWritable.readFields(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap map = new HashMap(mapWritable.size());
		for(Entry<Writable, Writable> entry: mapWritable.entrySet()){
			map.put(HdfsWritableUtils.getValueFromWritable(entry.getKey()),(AttrType)entry.getValue());
		}
		m_attrTypesMap.clear();
		m_attrTypesMap.putAll(map);
	}
	
	
	
	public AttrType getClassType() {
		return this.m_classType;
	}

	public void setClassType(AttrType m_classType) {
		this.m_classType = m_classType;
	}

	/**
	 * get the classvalue by classValueIndex in m_classLableValuesMap
	 * @see {@link #m_classLableValuesMap }
	 * @param classValueIndex
	 * @return
	 */
	public String getClassValueByIndex(int classValueIndex){
		int i=-1;
		 for (Iterator<String> it =  m_classLableValuesMap.keySet().iterator();it.hasNext();i++)
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
	
	
	public static void main(String[] args) {
		Map<String, Integer> cl=new LinkedHashMap<String, Integer>();
		cl.put("male", 4);
		cl.put("female", 4);
		NaiveBayes naiveBayes = new NaiveBayes();
		naiveBayes.setClassLableValuesMap(cl);
		String k1=naiveBayes.getClassValueByIndex(0);
		String k2= naiveBayes.getClassValueByIndex(1);
		naiveBayes.getClassValueIndexByValue(k1);
		naiveBayes.getClassValueIndexByValue(k2);
	}

	@Override
	public int getAttributeNameIndexByValue(String attributeName) {
		return m_attributeNamesList.indexOf(attributeName);
	}

	@Override
	public String getAttributeNameByIndex(int attributeNameIndex) {
		return m_attributeNamesList.get(attributeNameIndex);
	}

	@Override
	public AttrType getTypeOfAttribute(int attributeNameIndex) {
		return m_attrTypesMap.get(getAttributeNameByIndex(attributeNameIndex));
	}

	@Override
	protected DataSet readARFFMR(String filePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DataSet readDataMR(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	



}
