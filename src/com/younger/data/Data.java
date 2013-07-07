package com.younger.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.constant.Enum.AttrType;
import com.younger.tool.Tool;
import com.younger.tool.hadoop.HdfsWritableUtils;

@SuppressWarnings("finally")
public class Data implements Serializable , Cloneable,WritableComparable<Data>,Writable{
	
	private static final Logger log = LoggerFactory
			.getLogger(Data.class);
	
	/**
	 * Inner Class of Data Class </br>
	 * custom data Comparator on the attributeIndex of data
	 * @author apple
	 *
	 */
	public static class DataComparator implements Comparator<Data> {
		
		private int m_attributeIndex = -1;
		
		
		public DataComparator(int attributeIndex) {
			super();
			this.m_attributeIndex = attributeIndex;
		}

		public int getAttributeIndex() {
			return m_attributeIndex;
		}

		public void setAttributeIndex(int attributeIndex) {
			this.m_attributeIndex = attributeIndex;
		}

		@Override
		public int compare(Data o1, Data o2) {
			return new Double(o1.getAttributevaluesList().get(m_attributeIndex))
					.compareTo(new Double(o2.getAttributevaluesList().get(
							m_attributeIndex)));
		}

		}
	
	public static  class DataWritableComparator extends WritableComparator{

		 private int m_attributeIndex = -1;
		protected DataWritableComparator() {
			super(Data.class);
		}
		protected DataWritableComparator(
				int compareAttributeIndex) {
			super(Data.class);
			m_attributeIndex = compareAttributeIndex;
		}
		public int getAttributeIndex() {
			return m_attributeIndex;
		}

		public void setAttributeIndex(int attributeIndex) {
			this.m_attributeIndex = attributeIndex;
		}

		 public int compare(byte[] b1, int s1, int l1,
                 byte[] b2, int s2, int l2) {
			 	return compareBytes(b1, s1, l1, b2, s2, l2);
		 	}
		  static {                                        // register this comparator
		      WritableComparator.define(Data.class, new DataWritableComparator());
		    }
			
			@Override
			public int compare(Object a, Object b) {
		   if ( ((Data)a).m_attributevaluesList.get(m_attributeIndex) != ((Data)b).m_attributevaluesList.get(m_attributeIndex) ) {
				double var1=   Double.valueOf(((Data)a).getAttributevaluesList().get(m_attributeIndex));
				  double var2= Double.valueOf(((Data)b).getAttributevaluesList().get(m_attributeIndex));
				   return  var1<var2 ? -1 : 1;
			      }  else {
			        return 0;
			      }
			}
		    
		
	}
	
	public Data(List<String> attributeValuesList){
		super();
		m_attributevaluesList = attributeValuesList;
	}
	
	public Data() {

	}

	public Data(int recordId) {
		this.setRecordId(recordId);
	}

	public Data(int recordId, String classValue, List<String> attributevaluesList) {
		super();
		this.m_attributevaluesList = attributevaluesList;
		m_classValue = classValue;
		this.setRecordId(recordId);
	}

	public Data(String classValue, List<String> attrs, List<AttrType> attrType) {
		super();
		m_classValue = classValue;
		this.m_attributevaluesList = attrs;
	}

	public Data(int m_recordId,List<String> m_attributevaluesList, String m_classValue,
			int m_classValueIndex) {
		super();
		this.m_attributevaluesList = m_attributevaluesList;
		this.m_classValue = m_classValue;
		this.m_classValueIndex = m_classValueIndex;
		this.setRecordId(m_recordId);
	}

	// /** for serialization */
	public static final long serialVersionUID = -19412345060742748L;
	
	/**
	 * 属性名集合
	 */
	private List<String> m_attributevaluesList;

	/**
	 * 样本类别
	 */
	private String m_classValue;
	

	/**
	 * 样本类别值的序号
	 */
	private int m_classValueIndex;
	
	private int m_recordId;

//	private double weight;
	/**
	 * Checks for attributes of the given type in the dataset
	 * 
	 * @param attType
	 *            the attribute type to look for
	 * @return true if attributes of the given type are present
	 */
	// public boolean checkForAttributeType(AttrType attType) {
	// int i = 0;
	// while (i < this.attrTypeList.size()) {
	// if (attrTypeList.get(i) == attType) {
	// return true;
	// }
	// }
	// return false;
	// }
	
	/**
	 * Data之间比较大小
	 * 
	 * @return -1, 0, or +1 对应于小于, 等于，大于 Data o 对象.
	 * @author Administrator
	 */
	public int compareTo(Data o, int AttributeIndex) {
		int compareResult = -1;
		try {
			Double d = Double.valueOf((String) this.m_attributevaluesList
					.get(AttributeIndex));
			Double d1 = Double.valueOf((String) o.m_attributevaluesList
					.get(AttributeIndex));
			compareResult = d.compareTo(d1);
		} catch (ClassCastException ca) {
			ca.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return compareResult;
		}
	}

	public int getAttributeNum() {
		return this.m_attributevaluesList.size();
	}

	public List<String> getAttributevaluesList() {
		return m_attributevaluesList;
	}

	public String getClassValue() {
		return m_classValue;
	}

	public int getRecordId() {
		return m_recordId;
	}

	public void deleteAttributeValueAt(int position){
		assert(position>=0&&position<m_attributevaluesList.size());
		m_attributevaluesList.remove(position);
	}
	

	public void setRecordId(int m_recordId) {
		this.m_recordId = m_recordId;
	}

	public boolean isClassLabelMissing() {
		return this.m_classValue.equalsIgnoreCase("") || this.m_classValue == null;
	}
	
	  /**
	   * Tests if a specific value is "missing".
	   *
	   * @param attIndex the attribute's index
	   * @return true if the value is "missing"
	   */
    public  boolean isAttributeValueMissing(int attIndex) {
	    if (this.m_attributevaluesList.get(attIndex).isEmpty()) {
	      return true;
	    }
	    return false;
	  }

	  /**
	   * Tests whether an instance has a missing value.
	   *  Skips the class attribute if set.
	   * @return true if instance has a missing attribute value.
	   * @throws Exception 
	   * @throws UnassignedDatasetException if instance doesn't have access to any
	   * dataset
	   */
	  public   boolean hasMissingValue() throws Exception {
	    if (this == null) {
	    	log.error("Instance doesn't have access to a dataset!");
	      throw new Exception("Instance doesn't have access to a dataset!");
	    }
	    for (int i = 0; i < this.getAttributeNum(); i++) {
		if (this.isAttributeValueMissing(i)) {
		  return true;
		}
	      }
	    return false;
	  }
	  
	
	  
	public int getClassValueIndex() {
		return m_classValueIndex;
	}

	public void setClassValueIndex(int classValueIndex) {
		m_classValueIndex = classValueIndex;
	}

	public boolean isSameClass(Data anotherData) {
		return anotherData.getClassValue().equals(this.getClassValue());
	}

	public void setAttributevaluesList(List<String> attributeNamesList) {
//		this.m_attributevaluesList =  new ArrayList<String>(attributeNamesList);
		m_attributevaluesList = attributeNamesList;
	}

	public void setAttributevaluesList(String... attributeNamesList) {
		this.m_attributevaluesList = Arrays.asList(attributeNamesList);
	}

	public void setAttributevaluesList(String[] attributeNamesList, int start,
			int end) {
		this.m_attributevaluesList = new ArrayList<String>(end-start);
		for (int i = start; i < end; i++) {
			m_attributevaluesList.add(attributeNamesList[i]);
		}
	}

	public void setClassValue(String classValue) {
		m_classValue = classValue;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
//		return "Data [ recordId=" + recordId + ", ClassId=" + ClassId
//				+ ", attributeValuesList=" + attributevaluesList + "]";
	}

	public String writeAttributeNames() {
		return Tool.listDataToString(this.m_attributevaluesList, "\t").toString();
	}

	/**
	 * write Data to String
	 * @return
	 */
	public String writeDataToString() {
		StringBuffer sBuffer = new StringBuffer(this.m_classValue);
		sBuffer.append(":");
		for (String attValue : m_attributevaluesList) {
			sBuffer.append(attValue);
			sBuffer.append("\t");
		}
		return sBuffer.toString();
	}


	@Override
	protected Object clone() throws CloneNotSupportedException {
		Data data =  new Data(getRecordId(),new ArrayList<String>(m_attributevaluesList),m_classValue,m_classValueIndex);
		return data;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(getRecordId());
		HdfsWritableUtils.writeCollection(out, m_attributevaluesList);
		WritableUtils.writeString(out,m_classValue);
		out.writeInt(m_classValueIndex);
	}

	
	@Override
	/**
	 * init the Data writable
	 */
	public void readFields(DataInput in) throws IOException {
		m_recordId = in.readInt();
		Collection<String> collection = HdfsWritableUtils.readCollection(in);
		if(collection==null)
		m_attributevaluesList = new  LinkedList<String>();
		else{
			m_attributevaluesList = new LinkedList<String>(collection);
		}
		m_classValue = WritableUtils.readString(in);
		m_classValueIndex = in.readInt();
	}

	
	@Override
	public int compareTo(Data o) {
		return -1;
	}



}
