package com.younger.bayes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.RuntimeErrorException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.common.HdfsMapWritable;
import com.younger.struct.ContinousDistribution;
import com.younger.tool.hadoop.HdfsWritableUtils;

/**
 * record the naive bayes model 
 * @author apple
 */
public class NaiveBayesModel implements Serializable,Writable {

	private static final Logger log = LoggerFactory.getLogger(NaiveBayesModel.class);

	private static final long serialVersionUID = -1630778264688098453L;

	/**
	 * the probability of categorical class of data </br>
	 * p(y_{i})</br>
	 * Type: ArrayList </br>
	 */
	private List<Double> m_classProbabilityList;
	
	/**
	 * the probability of continuous class of data </br>
	 */
	private ContinousDistribution m_classContinousDistribution;
	
	private int m_classNum;
	
	/**
	 * (classValueIndex attributeNameIndex ,attributeValue, p)</br>
	 * p(x_{i}| y_{j})
	 * </br> for category/discrete attribute
	 * @author apple
	 */
	private Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> m_categoryAttributeProbabilityOfClassTable =null;
	
	/**
	 *  (classValueIndex attributeNameIndex, ContinousDistribution)
	 *  </br> for continuous/numeric attribute
	 */
	private Hashtable<Integer, Hashtable<Integer, ContinousDistribution> > m_continuousAttributeProbabilityOfClassTable=null  ;
	
	/**
	 * used when class attribute is numeric</br>
	 * (attributeNameIndex, ContinousDistribution)
	 */
	private Hashtable<Integer, ContinousDistribution> m_continuousAttributeProbabilityOfContinuousClassTable=null;
	
	/**
	 * return the m_continuousAttributeProbabilityOfClassTable 
	 *  (classValueIndex attributeNameIndex, ContinousDistribution)</br>
	 *  </br> for continuous/numeric attribute
	 *  @see m_continuousAttributeProbabilityOfClassTable
	 * @return
	 */
	public Hashtable<Integer, Hashtable<Integer, ContinousDistribution>> getContinuousAttributeProbabilityOfClassTable() {
		return this.m_continuousAttributeProbabilityOfClassTable;
	}

	public void setContinuousAttributeProbabilityOfClassTable(
			Hashtable<Integer, Hashtable<Integer, ContinousDistribution>> m_continuousAttributeProbabilityOfClassTable) {
		this.m_continuousAttributeProbabilityOfClassTable = m_continuousAttributeProbabilityOfClassTable;
	}

	public NaiveBayesModel(){
		m_classProbabilityList = new ArrayList<Double>(this.m_classNum);
		/**init the local vairables  */
		m_categoryAttributeProbabilityOfClassTable = new Hashtable<Integer, Hashtable<Integer,Hashtable<Integer,Double>>>();
		m_continuousAttributeProbabilityOfClassTable = new Hashtable<Integer, Hashtable<Integer,ContinousDistribution>>();
		m_continuousAttributeProbabilityOfContinuousClassTable = new Hashtable<Integer, ContinousDistribution>();
		m_classContinousDistribution = new ContinousDistribution();
	}

	public List<Double> getClassProbalityList() {
		return m_classProbabilityList;
	}

	public void setClassProbalityList(List<Double> classProbalityList) {
		this.m_classProbabilityList = classProbalityList;
	}

	public int getClassNum() {
		return m_classNum;
	}

	public void setClassNum(int m_classNum) {
		this.m_classNum = m_classNum;
	}

	public Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> getCategoryAttributeProbalityOfClassTable() {
		return m_categoryAttributeProbabilityOfClassTable;
	}

	public void setCategoryAttributeProbalityOfClassTable(
			Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> attributeProbalityOfClassTable) {
		this.m_categoryAttributeProbabilityOfClassTable = attributeProbalityOfClassTable;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,ToStringStyle.DEFAULT_STYLE);
	}
	
	/**
	 * add continousDistribution to m_continuousAttributeProbabilityOfClassTable
	 * @param classValueIndex
	 * @param attributeIndex
	 * @param continousDistribution
	 * @see {@linkplain #m_continuousAttributeProbabilityOfClassTable}
	 */
	public void addContinousDistributionToTable(int classValueIndex,int attributeIndex,ContinousDistribution continousDistribution){
		if(!m_continuousAttributeProbabilityOfClassTable.containsKey(classValueIndex)){
			Hashtable<Integer, ContinousDistribution> hashtable = new Hashtable<Integer, ContinousDistribution>();
			hashtable.put(attributeIndex, continousDistribution);
			m_continuousAttributeProbabilityOfClassTable.put(classValueIndex, hashtable);
		}
			else{
				//contain the classValueIndex
				if(!m_continuousAttributeProbabilityOfClassTable.get(classValueIndex).containsKey(attributeIndex) ){
					m_continuousAttributeProbabilityOfClassTable.get(classValueIndex).put(attributeIndex, continousDistribution);
					return;
				}else
					log.error("addContinousDistributionToTable error!");
				throw new RuntimeErrorException(new Error("addContinousDistributionToTable error!"));
				}
		}
	
	/**
	 * get the Probality Of Discrete Attribute
	 * 
	 * @param classValueIndex
	 *  the class value index in m_classLabelValuesList
	 *        
	 * @param attributeIndex
	 *             the index of attribute in m_attributeNamesList   
	 * @param attributeValueIndex
	 *      the attribute value in this attribute name     
	 * @return
	 */
	public double getProbalityOfDiscreteAttribute( int classValueIndex,int attributeIndex,
			int attributeValueIndex) {
		double p = getCategoryAttributeProbalityOfClassTable()
				.get(classValueIndex).get(attributeIndex)
				.get(attributeValueIndex);
		return p;
	}

	
	/**
	 * get the prabability of continuous attributeValue </br> 
	 * There are two method : Gause or KDE
	 * 
	 * @param classValueIndex
	 * @param attributeIndex
	 * @return
	 */
	public double getProbalityOfContiousAttributeValue(int classValueIndex,int attributeIndex, Double attributeValue) {
		
		return getContinuousAttributeProbabilityOfClassTable().get(classValueIndex).get(attributeIndex).
				getProbabilityForValue(attributeValue);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(m_classNum);
		writeContinuousAttributeProbabilityOfClassTable(out);
		writeCategoryAttributeProbabilityOfClassTable(out);
		HdfsWritableUtils.writeCollection(out, m_classProbabilityList);
		m_classContinousDistribution.write(out);
	}

	/**
	 * Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>>
	 * @param out
	 * @throws IOException
	 */
	private void writeCategoryAttributeProbabilityOfClassTable(DataOutput out) throws IOException {
//		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>>
		if(m_categoryAttributeProbabilityOfClassTable==null){
			out.write(null);
		}
		MapWritable mapWritable = new MapWritable();
		for(Integer e: m_categoryAttributeProbabilityOfClassTable.keySet()){
			Hashtable<Integer, Hashtable<Integer, Double>> hashtable = m_categoryAttributeProbabilityOfClassTable.get(e);
			MapWritable mapWritable1  = new MapWritable();
			for (Integer integer: hashtable.keySet() ) {
				MapWritable mapWritable2 = new MapWritable();
				Hashtable<Integer, Double> local = hashtable.get(integer);
				for(Integer integer2: local.keySet()){
					mapWritable2.put(new IntWritable(integer2), new DoubleWritable(local.get(integer2) ) );
				}
				mapWritable1.put(new IntWritable(integer), mapWritable2);
			}
		mapWritable.put(new IntWritable(e), mapWritable1);
		}
		HdfsMapWritable hdfsMapWritable = new HdfsMapWritable(mapWritable);
		hdfsMapWritable.write(out);
	}
	
	private Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> readCategoryAttributeProbabilityOfClassTable(DataInput in) throws IOException {
		if(in==null) return null;
		 HdfsMapWritable hdfsMapWritable = new HdfsMapWritable();
		 hdfsMapWritable.readFields(in);
		Map<Writable, Writable> mapWritable= hdfsMapWritable.getInstance();
		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> categoryAttributeProbabilityOfClassTable = 
				new Hashtable<Integer, Hashtable<Integer,Hashtable<Integer,Double>>>(mapWritable.size());
		for(Writable e: mapWritable.keySet()){
			int key = ((IntWritable) e).get();
			Writable value = mapWritable.get(e);
			Hashtable<Integer, Hashtable<Integer, Double>> hashTable2= new Hashtable<Integer, Hashtable<Integer,Double>>();
			Hashtable<IntWritable, Hashtable<IntWritable, DoubleWritable>> hashtable = (Hashtable<IntWritable, Hashtable<IntWritable, DoubleWritable>>) value;
			for(IntWritable k:hashtable.keySet()){
				Hashtable<IntWritable, DoubleWritable> value1= hashtable.get(k);	
				int key2 = k.get();
				Hashtable<Integer, Double> hashtable2 = new Hashtable<Integer, Double>();
				for(IntWritable k1:value1.keySet()){
					int key3= k1.get();
					double d = value1.get(k1).get();
					hashtable2.put(key3, d);
				}
				hashTable2.put(key2, hashtable2);
			}
				categoryAttributeProbabilityOfClassTable.put(key,hashTable2);
		}
		return categoryAttributeProbabilityOfClassTable;
	}
	
	/**
	 * Hashtable<Integer, Hashtable<Integer, ContinousDistribution>>
	 * @param out
	 * @throws IOException
	 */
	private void writeContinuousAttributeProbabilityOfClassTable(DataOutput out)
			throws IOException {
		if(m_continuousAttributeProbabilityOfClassTable==null) 
			out.write(null);
		MapWritable mapWritable = new MapWritable();
		for(Entry<Integer, Hashtable<Integer, ContinousDistribution>> e:  m_continuousAttributeProbabilityOfClassTable.entrySet()){
			Hashtable<Integer, ContinousDistribution> hashtable = e.getValue();
			for(Integer integer: hashtable.keySet()){
				mapWritable.put(new IntWritable(integer), hashtable.get(integer));
			}
		}
		HdfsMapWritable hdfsMapWritable = new HdfsMapWritable(mapWritable);
		hdfsMapWritable.write(out);
	}
	
	private Hashtable<Integer, Hashtable<Integer, ContinousDistribution>> readContinuousAttributeProbabilityOfClassTable(DataInput in) throws IOException {
		if(m_continuousAttributeProbabilityOfClassTable==null) 
			return null;
		Hashtable<Integer, Hashtable<Integer, ContinousDistribution> > continuousAttributeProbabilityOfClassTable = new Hashtable<Integer, Hashtable<Integer,ContinousDistribution>>();
		HdfsMapWritable hdfsMapWritable = new HdfsMapWritable();
		Map<Writable, Writable> mapWritable  =hdfsMapWritable.getInstance(); 
//		Hashtable<IntWritable, Hashtable<IntWritable, ContinousDistribution>> hhasHashtable = (Hashtable<IntWritable, Hashtable<IntWritable, ContinousDistribution>>) mapWritable;
		for( Writable key:  mapWritable.keySet()){
			int k = ((IntWritable) key).get();
		MapWritable mapWritable2 =	(MapWritable) mapWritable.get(key);
			Hashtable<Integer, ContinousDistribution> hashtable2 = new Hashtable<Integer, ContinousDistribution>();
			for(Writable key2: mapWritable2.keySet()){
				int k2= ((IntWritable)key2).get();
				ContinousDistribution continousDistribution =(ContinousDistribution) mapWritable2.get(key2);
				hashtable2.put(k2, continousDistribution);
			}
			continuousAttributeProbabilityOfClassTable.put(k, hashtable2);
		}
		return continuousAttributeProbabilityOfClassTable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readFields(DataInput in) throws IOException {
		m_classNum = in.readInt();
		m_continuousAttributeProbabilityOfClassTable = readContinuousAttributeProbabilityOfClassTable(in);
		m_categoryAttributeProbabilityOfClassTable = readCategoryAttributeProbabilityOfClassTable(in);
		m_classProbabilityList = new ArrayList(HdfsWritableUtils.readCollection(in));
		 m_classContinousDistribution.readFields(in);
		
	}

	public ContinousDistribution getClassContinousDistribution() {
		return this.m_classContinousDistribution;
	}

	public void setClassContinousDistribution(
			ContinousDistribution m_classContinousDistribution) {
		this.m_classContinousDistribution = m_classContinousDistribution;
	}

	/**
	 * @see {@linkplain #m_continuousAttributeProbabilityOfContinuousClassTable}
	 * @return
	 */
	public Hashtable<Integer, ContinousDistribution> getContinuousAttributeProbabilityOfContinuousClassTable() {
		return this.m_continuousAttributeProbabilityOfContinuousClassTable;
	}

	public void setContinuousAttributeProbabilityOfContinuousClassTable(
			Hashtable<Integer, ContinousDistribution> m_continuousAttributeProbabilityOfContinuousClassTable) {
		this.m_continuousAttributeProbabilityOfContinuousClassTable = m_continuousAttributeProbabilityOfContinuousClassTable;
	}

	

}
