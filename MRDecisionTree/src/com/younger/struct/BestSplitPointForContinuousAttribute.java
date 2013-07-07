package com.younger.struct;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * the structure describe the split point for continuous attribute
 * 	 double m_splitAttributeValue=0.0d;
 *	private int m_splitRecordIdData=0;
 *	private double m_maxGain = 0.0d;
 *	private ContinousHistogram m_ContinousHistogram=null;
 * @author apple
 *
 */
public class BestSplitPointForContinuousAttribute extends ClassifierSplitModel{
	

	private static final long serialVersionUID = 3205996834579589388L;
	
	/**
	 * the split attribute value type: double
	 */
	public double m_splitAttributeValue=0.0d;
	
	public BestSplitPointForContinuousAttribute(){
		
	}
	
	
	public BestSplitPointForContinuousAttribute(double splitValue,
			int splitRecordIdData, double maxGain,
			ContinousHistogram continousHistogram) {
		super();
		this.m_splitAttributeValue = splitValue;
		this.m_splitRecordIdData = splitRecordIdData;
		this.m_maxGain = maxGain;
		this.m_ContinousHistogram = continousHistogram;
	}


	private int m_splitRecordIdData=0;
	private ContinousHistogram m_ContinousHistogram=null;
	
	public double getSplitAttributeValue() {
		return m_splitAttributeValue;
	}

	public void setSplitAttributeValue(double splitAttributeValue) {
		this.m_splitAttributeValue = splitAttributeValue;
	}


	public int getSplitRecordIdData() {
		return m_splitRecordIdData;
	}
	public void setSplitRecordIdData(int splitRecordIdData) {
		this.m_splitRecordIdData = splitRecordIdData;
	}

	
	public ContinousHistogram getContinousHistogram() {
		return m_ContinousHistogram;
	}
	public void setContinousHistogram(ContinousHistogram continousHistogram) {
		this.m_ContinousHistogram = continousHistogram;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	

}
