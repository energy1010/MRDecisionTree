package com.younger.data;



public abstract class AbstractDataSet {

	public AbstractDataSet() {
		super();
	}
	
	/**
	 * write DataSet to String
	 * @return
	 */
	public abstract   String writeDataSetToString();

	public abstract boolean hasMissingValue();
	

	/**
	 * get the classvalue by classValueIndex in m_classLableValuesMap
	 * @see {@link #m_classLableValuesMap }
	 * @param classValueIndex
	 * @return
	 */
	public  abstract String getClassValueByIndex(int classValueIndex);
	
	public abstract int getClassValueIndexByValue(String classValue) ;
	
	/**
	 * get the num of class value in m_classValueMap
	 * @see {@link m_classValueMap}
	 * @return
	 */
	public abstract int getNumOfClassValue(int classValueIndex);
	
	public abstract String getAttributeNameByIndex(int attributeNameIndex);
	
//	public abstract int getAttributeValueByIndex(int )
	
}
	
