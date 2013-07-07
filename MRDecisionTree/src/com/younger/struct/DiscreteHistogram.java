package com.younger.struct;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Disrete Histogram for Category Attribute
 * @author Administrator
 *
 */
public class DiscreteHistogram extends AbstractHistogram {
	
	public static final long serialVersionUID = -19412345060742748L;
	
	private int classNum = -1;
	private int attributeValueNum = -1;
	
	/**
	 * [attributeValueNum][classNum]
	 */
	private int [][] attributeValueClassHistogram;
	
	public DiscreteHistogram(){
		
	}
	
	
	public DiscreteHistogram(int classnum,int attributeValueNum){
		this.classNum = classnum;
		this.attributeValueNum = attributeValueNum;
		this.attributeValueClassHistogram = new int[this.attributeValueNum][this.classNum];
	}

	public int getClassNum() {
		return classNum;
	}

	public void setClassNum(int classNum) {
		this.classNum = classNum;
	}


	public int getAttributeValueNum() {
		return attributeValueNum;
	}

	public void setAttributeValueNum(int attributeValueNum) {
		this.attributeValueNum = attributeValueNum;
	}

	public int[][] getHistogram() {
		return attributeValueClassHistogram;
	}

	public void setHistogram(int[][] histogram) {
		this.attributeValueClassHistogram = histogram;
	}
	
	
	public  void serialize(String fileName) throws Exception{
		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
		outputStream.writeObject(this);
		outputStream.close();
	}
	
	public DiscreteHistogram deserialize(String fileName) throws Exception{
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
		DiscreteHistogram histogram = (DiscreteHistogram)inputStream.readObject();
		inputStream.close();
		return histogram;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
	}
	
	public static void main(String[] args) {
	}
	
	/**
	 * [attributeValueNum][classNum]
	 * @param attributeValueIndex
	 * @return
	 */
	public int[] getClassDistribution(int attributeValueIndex){
		return this.attributeValueClassHistogram[attributeValueIndex];
	}
	
	/**
	 *[attributeValueNum] [classNum]
	 * @param attributeValueIndex
	 * @return
	 */
	public int[] getAttributeValueDistribution(int classIndex){
		int[] attributeValueNumArray = new int[this.attributeValueNum];
		for(int i=0;i<attributeValueNum;i++){
			attributeValueNumArray[i]= this.attributeValueClassHistogram[i][classIndex];
		}
		return attributeValueNumArray;
	}
	
}
