
package com.younger.eval;

import java.util.List;

import com.younger.data.Data;
import com.younger.data.DataSet;

/**
 * @author apple
 */

public abstract class EvaluationMetric {
	
	protected boolean isLargerBetter;
	
	public EvaluationMetric(boolean isLargerBetter) {
		this.isLargerBetter = isLargerBetter;
	}
	
	public abstract double measure(int[] predictions, DataSet dataSet)  throws Exception;
	
	public abstract double measure(double[] predictions, DataSet dataSet) throws Exception;
	
	public abstract double measure(Integer[] predictions,DataSet dataSet) throws Exception;
	
	/**
	 *  measure the accuracy for the dataset
	 *  @param predictions 
	 *  @param dataList
	 *  @param classValues  the classValues of the Classifier 
	 */
	public abstract double measure(List<Integer> predictions, List<Data> dataList,List<String> classValues)  throws Exception;
	
	public abstract double measure(int[] predictions, List<Data> dataList)  throws Exception;
	
	public abstract double measure(double[] predictions, List<Data> dataList) throws Exception;
	
	public abstract double measure(Integer[] predictions,List<Data> dataList) throws Exception;
	
	
	
	public boolean isFirstBetter(double first, double second, double tolerance) {
		if (Double.isNaN(second)) {
			return true;
		}
		if (isLargerBetter) {
			return (first * (1 + tolerance)) > second;
		} else {
			return (first * (1 - tolerance)) < second;
		}
	}

	public boolean isLargerBetter() {
		return isLargerBetter;
	}

	public void setLargerBetter(boolean isLargerBetter) {
		this.isLargerBetter = isLargerBetter;
	}


}
