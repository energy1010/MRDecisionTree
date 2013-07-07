package com.younger.struct;


/**
 * if binary split then the model describe the splitAttributeValue
 * @author apple
 *
 */
public class BestBinarySplitForCategoryAttribute extends ClassifierSplitModel {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5332171202072179159L;
	
	
	/**  the split attribute value for binary split*/
	public String m_splitAttributeValue=null;
	
	
	public BestBinarySplitForCategoryAttribute(){
		
	}
	
	public BestBinarySplitForCategoryAttribute(double maxGain,String splitAttributeValue){
		this.m_maxGain = maxGain;
		this.m_splitAttributeValue  = splitAttributeValue;
	}
	
	
	
	public String getSplitAttributeValue() {
		return m_splitAttributeValue;
	}

	public void setSplitAttributeValue(String m_splitAttributeValue) {
		this.m_splitAttributeValue = m_splitAttributeValue;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
