package com.younger.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.common.HdfsMapWritable;
import com.younger.constant.Enum.AttrType;
import com.younger.tool.MathUtil;
import com.younger.tool.Tool;
import com.younger.tool.hadoop.HdfsWritableUtils;

/**
 * @author Administrator
 * List<String> m_attributeNamesList, int m_attributeNum,
 *			Hashtable<Integer, AttrType> m_attributeTypeTable,
 *			List<Integer> m_classIndexs, int m_classNum,
 *			Dictionary<String, Integer> m_classValueDistributionTable,
 *			List<String> m_classValues, int trainingDataNum,
 *			List<Data> m_trainingDatas
 * 
 */
public class DataSet extends AbstractDataSet implements Serializable,Writable{

	
//	public static DataSet loadDataSet(File dataFile){
//		List<Data> dataList = new ArrayList<Data>();
//		try {
//		FileReader fr = new FileReader(dataFile);
//		BufferedReader br = new BufferedReader(fr);
//		String line;
//		Pattern pattern = Pattern.compile(Parameter.attributePatternString);
//		while ((line = br.readLine()) != null) { // read and process each
//			log.trace(line);
//			readArffProcessEachLine(br, line, pattern,  dataList, NumOfEachattributeValueTable);
//		}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(DataSet.class);
	
	/**
	 * sort dataList on sortAttributeIndex
	 * @param dataList
	 * @param sortAttributeIndex
	 * @return
	 */
	public static List<Data> sortDataSet(List<Data> dataList,
			int sortAttributeIndex) {
		/**sort data with attribute Index */
		Collections.sort(dataList, new Data.DataComparator(
				sortAttributeIndex));
		return dataList;
	}

	/**
	 *  the attributeNames of the dataSet
	 */
	public List<String> m_attributeNamesList = null ;

	/**
	 * the attribute num of dataset
	 */
	public int m_attributeNum = 0;

	/**
	 * key : attributeNameIndex, value : attrType
	 */
	public Hashtable<Integer, AttrType> m_attributeTypeTable = null;

	
	/**
	 * the class value for each data </br>
	 * Note that it can store same class value 
	 * </br> Type :ArrayList<String>
	 */
	public List<String> m_dataClassValues = null;
	/**
	 * 
	 */
	public List<Integer> m_classIndexes = null;
	/**
	 * 
	 */
	public int m_classNum = 0;

	public AttrType m_classType= AttrType.Category;
	
	/**
	 * key : classValue value : num
	 * class values </br>
	 * the classValues has the same order as classValueDistributionTable (classValue , num)</br>
	 * not contain same class value </br>
	 * Type :LinkedHashMap
	 */
	public Map<String,Integer> m_classValueMap = null;

	public int m_datasetSize = Integer.MIN_VALUE;

	/**
	 * collection of datas
	 */
	public List<Data> m_dataList= null;

	public  String m_className=null;
	
	public DataSet() {
		m_classValueMap = new LinkedHashMap<String, Integer>();
		m_dataClassValues = new ArrayList<String>();
	}

	
	/**
	 * 
	 * @param dataList
	 */
	public DataSet(List<Data> dataList) {
		this();
		this.m_dataList = dataList;
		this.m_datasetSize = dataList.size();
	}

	public DataSet(List<Data> dataList, int size) {
		this();
		this.m_dataList = dataList;
		this.m_datasetSize = size;
	}

	public DataSet(List<String> m_attributeNamesList, int m_attributeNum,
			Hashtable<Integer, AttrType> m_attributeTypeTable,
			List<Integer> m_classIndexs, int m_classNum,
			Map<String, Integer> m_classValues, List<String> dataClassValueList,int trainingDataNum,
			List<Data> trainingDatas,String className) {
		super();
		this.m_attributeNamesList = m_attributeNamesList;
		this.m_attributeNum = m_attributeNum;
		this.m_attributeTypeTable = m_attributeTypeTable;
		this.m_classIndexes = m_classIndexs;
		this.m_classNum = m_classNum;
		this.m_classValueMap = m_classValues;
		this.m_dataClassValues= dataClassValueList;
		this.m_datasetSize = trainingDataNum;
		this.m_dataList = trainingDatas;
		this.m_className= className;
	}

	/**
	 * Removes all instances with missing values for a particular attribute from
	 * the dataset.
	 * 
	 * @param attIndex
	 *            the attribute's index (index starts with 0)
	 */
	// @ requires 0 <= attIndex && attIndex < numAttributes();
	public void deleteWithMissing() {
		for (int i = 0; i < this.m_datasetSize; i++) {
			if (this.m_dataList.get(i).getClassValue().isEmpty()) {
				this.m_dataList.remove(i);
			}
			if (this.m_dataList.get(i).getClassValue().trim()
					.equalsIgnoreCase("")) {
				this.m_dataList.remove(i);
			}
		}
	}

	public List<String> getAttributeNamesList() {
		return m_attributeNamesList;
	}

	public int getAttributeNum() {
		return m_attributeNum;
	}

	/**
	 *  key : attributeNameIndex, value : attrType
	 * @return Hashtable (Integer, AttrType)
	 */
	public Hashtable<Integer, AttrType> getAttributeTypeTable() {
		return m_attributeTypeTable;
	}

	public List<Integer> getClassIndexs() {
		return m_classIndexes;
	}

	public int getClassNum() {
		return m_classNum;
	}


	/**
	 * get the class distribution of dataSet
	 * 
	 * @param dataList
	 * @return
	 */
	public List<Integer> getDataClassDistribution() {
		List<Integer> list = new ArrayList<Integer>();
		Dictionary<String, Integer> classDistributionDictionary = getDataDistribution();
		for (Enumeration<Integer> enumeration = classDistributionDictionary
				.elements(); enumeration.hasMoreElements();) {
			list.add(enumeration.nextElement());
		}
		return list;
	}

	@Override
	  public  boolean hasMissingValue(){
		  boolean hasMissing  = false;
		  for(Data d:this.m_dataList){
			  try {
				if(d.hasMissingValue()){
					  hasMissing =  true;
				  }
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		  return hasMissing;
	  }

	/**
	 * Task: Dictionary<String , Integer> key value
	 * 
	 * @param dataList
	 * @return <classId, classNum>
	 * @author Administrator
	 */
	public Dictionary<String, Integer> getDataDistribution() {
		if (this.m_dataList == null || m_dataList.size() == 0)
			return null;
		Dictionary<String, Integer> classDistibution = new Hashtable<String, Integer>();
		for (int i = 0; i < m_dataList.size(); i++) {
			String classId = getDataClassValueByIndex(i);  m_dataList.get(i).getClassValue();
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

	public List<?> getDatasByAttribute(int attributeNameIndex) {
		List attributeDataList = null;
		if (this.m_attributeTypeTable.get(attributeNameIndex).equals(
				AttrType.Category)) {
			attributeDataList = new ArrayList<String>();
			for (Data d : this.m_dataList) {
				attributeDataList.add(d.getAttributevaluesList().get(attributeNameIndex));
			}
		} else {
			attributeDataList = new ArrayList<Double>();
			for (Data d : this.m_dataList) {
				attributeDataList.add(Double.parseDouble(d
						.getAttributevaluesList().get(attributeNameIndex)
						.trim()));
			}
		}
		return attributeDataList;
	}

	public double getMeanOfAttribute(int attributeNameIndex) throws Exception {
		double mean = Double.MIN_VALUE;
		if (this.m_attributeTypeTable.get(attributeNameIndex).equals(
				AttrType.Category)) {
			log.error("attribute type is category type!");
			throw new Exception("attribute type is category type!");
		} else {
			mean = MathUtil
					.getAvg((List<Double>) getDatasByAttribute(attributeNameIndex));
		}
		return mean;
	}

	public double getProbalityOfClass(int classIndex) {
		double total = Tool.getSum(m_classValueMap.values());
		return getNumOfClassValue(classIndex)/ total;
	}

	/**
	 * 
	 * @return
	 */
	public Collection<Integer> getRecordIds() {
		Collection<Integer> recordIds = new HashSet<Integer>();
		for (Data data : this.m_dataList) {
			if (data != null) {
				recordIds.add(data.getRecordId());
			}
		}
		return recordIds;
	}

	public double getVarianceOfAttribute(int attributeNameIndex)
			throws Exception {
		double var = Double.MIN_VALUE;
		if (this.m_attributeTypeTable.get(attributeNameIndex).equals(
				AttrType.Category)) {
			log.error("attribute type is category type!");
			throw new Exception("attribute type is category type!");
		} else {
			var = MathUtil.getStd((List<Double>) getDatasByAttribute(attributeNameIndex));
		}
		return var;
	}

	/**
	 * Creates a new dataset of the same size using random sampling with
	 * replacement.
	 * 
	 * @param random
	 *            a random number generator
	 * @return the new dataset
	 */
	public List<Data> resample(Random random) {

		List<Data> newData = new LinkedList<Data>();
		while (newData.size() < this.m_dataList.size()) {
			newData.add(this.m_dataList.get(random
					.nextInt(this.m_dataList.size())));
		}
		return newData;
	}

	public void setAttributeNamesList(List<String> attributeNamesList) {
		this.m_attributeNamesList = attributeNamesList;
	}

	public void setAttributeNum(int attributeNum) {
		this.m_attributeNum = attributeNum;
	}

	public void setAttributeTypeTable(
			Hashtable<Integer, AttrType> attributeTypeTable) {
		this.m_attributeTypeTable = attributeTypeTable;
	}

	public void setClassIndexs(List<Integer> classIndexs) {
		this.m_classIndexes = classIndexs;
	}

	public void setClassNum(int classNum) {
		this.m_classNum = classNum;
	}

	public void setClassValuesMap(Map<String,Integer> classValuesMap) {
		this.m_classValueMap = classValuesMap;
	}
	
	/**
	 * set classValues with Enumeration
	 * @param classValues
	 */
	public void setClassValues(Enumeration<String> classValues){
		while (classValues.hasMoreElements()) {
			String classValue = (String) classValues.nextElement();
			this.m_dataClassValues.add(classValue);
		}
	}
	
	/**
	 * set classValues with Enumeration
	 * @param classValues
	 */
	public void setClassValues(Collection<String> classValues){
		for	(String classValue: classValues){
			this.m_dataClassValues.add(classValue);
		}
	}

	public int getDatasetSize() {
		return m_datasetSize;
	}

	public void setDatasetSize(int datasetSize) {
		this.m_datasetSize = datasetSize;
	}

	public List<Data> getDataList() {
		return m_dataList;
	}

	public void setDataList(List<Data> m_dataList) {
		this.m_dataList = m_dataList;
	}

	public Map<String, Integer> getClassValueMap() {
		return this.m_classValueMap;
	}


	public void setClassValueMap(Map<String, Integer> m_classValueMap) {
		this.m_classValueMap = m_classValueMap;
	}


	public void setDataList(List<Data> dataList, int start, int end) {
		this.m_dataList.clear();
		this.m_dataList.addAll(dataList.subList(start, end));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	/**
	 * 
	 * @param dataSet
	 * @param fileName
	 */
	public static boolean writeDataListToFile(List<Data> dataList, String fileName) {
		boolean succ = false;
		File file = new File(fileName);
		PrintWriter writer = null;
		if (file.exists()) {
			file.delete();
		}
		try {
			writer = new PrintWriter(new FileOutputStream(fileName));
			 writer.println("@data");
			 writer.println("%");
			 writer.println("%");
			 writer.println("% "+dataList.size() +" instances");
			 writer.println("%");
			 writer.println("%");
			for (Data d : dataList) {
				writer.println(d.writeDataToString());
			}
			succ = true;
			log.debug("write dataList to file "
					+ file.getAbsolutePath() + " successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("write dataList to file "
					+ file.getAbsolutePath() + " failed!");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return succ;
	}
	
	public boolean isEmpty() {
		return this.m_datasetSize == 0;
	}

	public  void clearDataSet(){
		this.m_attributeNamesList.clear();
		this.m_attributeNum =0;
		this.m_attributeTypeTable.clear();
		this.m_classIndexes.clear();
		this.m_classNum=0;
		this.m_classValueMap.clear();
		this.m_dataClassValues.clear();
		this.m_dataList.clear();
		this.m_datasetSize=0;
	}
	
	public static DataSet remove(DataSet dataSet,DataSet removeDataSet){
			dataSet.getDataList().removeAll(removeDataSet.getDataList());
			return dataSet;
	}
	
	public   String writeDataSetToString(){
		//TO DO: 
		return toString();
	}
	
	  /**
	   * @param rng
	   *          Random number generator
	   * @param ratio
	   *          [0,1]
	   * @return a random subset without modifying the current data
	   */
//	  public DataSet randomSubset(Random rng, double ratio) {
//	    List<Data> subset = new ArrayList<Data>();
//	    
//	    for (Data instance : m_dataList) {
//	      if (rng.nextDouble() < ratio) {
//	        subset.add(instance);
//	      }
//	    }
//	    return new DataSet(m_attributeNamesList,m_attributeNum,null,m_classIndexs,m_classNum,null,m_classValues,subset.size(),subset, m_className,m_classType);
//	  }
	  public DataSet randomSubset(Random rng, double ratio) {
	    List<Data> subset = new ArrayList<Data>();
	    
	    for (Data instance : m_dataList) {
	      if (rng.nextDouble() < ratio) {
	        subset.add(instance);
	      }
	    }
	    return new DataSet(m_attributeNamesList,m_attributeNum,null,m_classIndexes,m_classNum,null,m_dataClassValues,subset.size(),subset, m_className);
	  }
	  
	  /**
	   * @param rng
	   *          Random number generator
	   * @param ratio
	   *          [0,1]
	   * @return a random subset without modifying the current data
	   */
	  public List<Data> randomSubsetReturnDataList(Random rng, double ratio) {
	    List<Data> subset = new ArrayList<Data>();
	    
	    for (Data instance : m_dataList) {
	      if (rng.nextDouble() < ratio) {
	        subset.add(instance);
	      }
	    }
	    return subset;
	  }


	  /**
	   * if data has N cases, sample N cases at random -but with replacement.
	   * 
	   * @param rng
	   */
	  public List<Data> baggingReturnDataList(Random rng) {
	    int datasize = getDatasetSize();
	    List<Data> bag = new ArrayList<Data>(datasize);
	    
	    for (int i = 0; i < datasize; i++) {
	      bag.add(m_dataList.get(rng.nextInt(datasize)));
	    }
	    
	    return bag;
	  }
	  
	  /**
	   * if data has N cases, sample N cases at random -but with replacement.
	   * 
	   * @param rng
	   * @param sampled
	   *          indicating which instance has been sampled
	   * 
	   * @return sampled data
	   */
//	  public DataSet bagging(Random rng, boolean[] sampled) {
//		  int datasize = getDatasetSize();
//		    List<Data> bag = new ArrayList<Data>(datasize);
//		    
//		    for (int i = 0; i < datasize; i++) {
//		      bag.add(m_dataList.get(rng.nextInt(datasize)));
//		    }
//	    return new DataSet(m_attributeNamesList,m_attributeNum,null,m_classIndexs,m_classNum,null,m_classValues,bag.size(),bag, m_className,m_classType);
//	  }


	public DataSet bagging(Random rng, boolean[] sampled) {
		  int datasize = getDatasetSize();
		    List<Data> bag = new ArrayList<Data>(datasize);
		    
		    for (int i = 0; i < datasize; i++) {
		      bag.add(m_dataList.get(rng.nextInt(datasize)));
		    }
	    return new DataSet(m_attributeNamesList,m_attributeNum,null,m_classIndexes,m_classNum,null,m_dataClassValues,bag.size(),bag, m_className);
	  }

	  
	  /**
	   * Counts the number of attributes, except IGNORED and LABEL
	   * 
	   * @return number of attributes that are not IGNORED or LABEL
	   */
	  public static int countAttributes(AttrType[] attrs) {
	    int nbattrs = 0;
	    
	    for (AttrType attr1 : attrs) {
	      if (attr1.isNumerical()|| attr1.isCategorical()) {
	        nbattrs++;
	      }
	    }
	    
	    return nbattrs;
	  }

	  
	  public String getClassName() {
		return this.m_className;
	}


	public void setClassName(String m_className) {
		this.m_className = m_className;
	}


	@Override
	public void write(DataOutput out) throws IOException {
		writeDataset(out);
	}


	protected void writeDataset(DataOutput out) throws IOException {
		HdfsWritableUtils.writeCollection(out,m_attributeNamesList);
		out.writeInt(m_attributeNum);
		writeAttributeTypeTable(out);
		HdfsWritableUtils.writeCollection(out, m_dataClassValues);
		HdfsWritableUtils.writeCollection(out, m_classIndexes);
		out.writeInt(m_classNum);
		m_classType.write(out);
		HdfsWritableUtils.writeMap(m_classValueMap, out);
		out.writeInt(m_datasetSize);
		HdfsWritableUtils.writeCollection(out, m_dataList);
		WritableUtils.writeString(out, m_className);
	}


	@Override
	public void readFields(DataInput in) throws IOException {
		m_attributeTypeTable = new Hashtable<Integer,AttrType>( readAttributeTypeTable(in));
		m_dataClassValues = new  ArrayList(HdfsWritableUtils.readCollection(in));
		m_classIndexes = new LinkedList( HdfsWritableUtils.readCollection(in));
		m_classNum = in.readInt();
		m_classType = WritableUtils.readEnum(in, AttrType.class );
		m_classValueMap = HdfsWritableUtils.readMap(in);
	    m_datasetSize = in.readInt();
	    m_dataList =new LinkedList(HdfsWritableUtils.readCollection(in));
	    m_className = WritableUtils.readString(in);
	}
	  
	/**
	 * 
	 * Hashtable<Integer, AttrType>
	 * @throws IOException 
	 * @see {@link　m_attributeTypeTable }　m_attributeTypeTable
	 */
	public void writeAttributeTypeTable(DataOutput out) {
		HdfsWritableUtils.writeMap(m_attributeTypeTable, out);
//		try {
//			if(m_attributeTypeTable==null||m_attributeTypeTable.isEmpty()){
//				out.writeBoolean(false);
//				return;
//			}
//			out.writeBoolean(true);
//			out.writeInt(m_attributeTypeTable.size());
//			MapWritable mapWritable = new MapWritable();
//			HdfsMapWritable mapWritable1 = new HdfsMapWritable();
//			Map<Writable, Writable> map = new Hashtable<Writable, Writable>();
//			for (Entry<Integer, AttrType> m :m_attributeTypeTable.entrySet()) {
//				map.put(new IntWritable(m.getKey()), m.getValue());
//			}
//			mapWritable1.setInstance(map);
//			mapWritable1.write(out);
//		} catch (IOException e) {
//			System.err.println(e.getMessage());
//			e.printStackTrace();
//		}catch (Exception e) {
//			System.err.println(e.getMessage());
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 
	 * Hashtable<Integer, AttrType>
	 * @see {@link　m_attributeTypeTable }　m_attributeTypeTable
	 */
	public Map<Integer, AttrType> readAttributeTypeTable(DataInput in){
		return HdfsWritableUtils.readMap(in);
//		Boolean notEmpty;
//		try {
//			notEmpty = in.readBoolean();
//			if(!notEmpty) {
//				return null;
//			}
//			HdfsMapWritable hdfsMapWritable = new HdfsMapWritable();
//			hdfsMapWritable.readFields(in);
//			Map<Writable, Writable> mapWritable= hdfsMapWritable.getInstance();
//			int size = mapWritable.size();
//			Map<Integer, AttrType> map = new Hashtable<Integer, AttrType>(size);
//			for (Entry<Writable, Writable> m :mapWritable.entrySet()) {
//				map.put( new Integer( ((IntWritable)m.getKey()).get()), ((AttrType)m.getValue()));
//			}
//			return map;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
	}
	
	/**
	 * Dictionary<String, Integer> m_classValueDistributionTable
	 * @param in
	 * @return
	 * @see  m_classValueDistributionTable
	 */ 
	private void writeClassValueDistributionTable(DataOutput out){
//		try {
//			if(m_classValueDistributionTable==null||m_classValueDistributionTable.isEmpty()){
//				out.writeBoolean(false);
//				return;
//			}
//			MapWritable mapWritable = new MapWritable();
//			out.writeBoolean(true);
//			out.writeInt(m_classValueDistributionTable.size());
//			Map<Writable, Writable> map =HdfsWritableUtils.convertToMapWritable((Map)m_classValueDistributionTable);
//			mapWritable.putAll(map);
//			mapWritable.write(out);
//		} catch (IOException e) {
//			System.err.println(e.getMessage());
//			e.printStackTrace();
//		}catch (Exception e) {
//			System.err.println(e.getMessage());
//			e.printStackTrace();
//		}
		
	}
	
	/**
	 * Dictionary<String, Integer> m_classValueDistributionTable
	 * @param in
	 * @return
	 * @see  m_classValueDistributionTable
	 */
	private Map<String, Integer> readClassValueDistributionTable(DataInput in){
		try {
			Boolean notEmpty = in.readBoolean();
			if(!notEmpty){
				return null;
			}
			int mapSize = in.readInt();
			HdfsMapWritable mapWritable = new HdfsMapWritable();
			mapWritable.readFields(in);
			Map map =HdfsWritableUtils.convertFromMapWritable(mapWritable);
			return  map;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	
	}


	@Override
	public String getClassValueByIndex(int classValueIndex) {
		int index  = 0;
		 for (Iterator<String> it = m_classValueMap.keySet().iterator();it.hasNext();index++)
		   {
			 if(index==classValueIndex){
		    String key = it.next();
		    return key;
		    }
			continue; 
		   }
		 log.error("cant find the classvalue index"+classValueIndex);
		 return null;
	}


	@Override
	public int getClassValueIndexByValue(String classValue) {
		return 0;
	}
	

	public AttrType getClassType() {
		return this.m_classType;
	}


	public void setClassType(AttrType m_classType) {
		this.m_classType = m_classType;
	}


	@Override
	public int getNumOfClassValue(int classValueIndex) {
		return m_classValueMap.get(getClassValueByIndex(classValueIndex));
	}
	
	  /**
	   * Loads the dataset from a file
	   */
	  public static DataSet load(Configuration conf, Path path) throws IOException {
	    FileSystem fs = path.getFileSystem(conf);
	    FSDataInputStream input = fs.open(path);
	    DataSet dataset =   read(input);
	    input.close();
	    return dataset;
	  }
	
	/**
	 * get the record class value by record index in m_dataClassValues
	 * @see {@linkplain #m_dataClassValues}
	 * @param dataIndex
	 * @return
	 */
	public  String getDataClassValueByIndex(int dataIndex){
		return m_dataClassValues.get(dataIndex);
	}


	@Override
	public String getAttributeNameByIndex(int attributeNameIndex) {
		return m_attributeNamesList.get(attributeNameIndex);
	}

	public Collection<String> getDataSetAttributeValuesByIndex(int attributeIndex){
		Collection<String> collection= new Vector<String>(m_datasetSize);
		for (Data data :m_dataList) {
			collection.add( data.getAttributevaluesList().get(attributeIndex));
		}
		return collection;
	}

	
	/**
	 * read dataset from DataInput
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static DataSet read(DataSet dataSet, DataInput in) throws IOException	{
		 dataSet.readFields(in);
		 return dataSet;
	}
	
	public static DataSet read(DataInput in) throws IOException	{
		DataSet dataSet = new DataSet();
		 dataSet.readFields(in);
		 return dataSet;
	}
	
	public static void write(DataOutput out,DataSet dataSet) throws IOException {
		dataSet.write(out);
	}
}
