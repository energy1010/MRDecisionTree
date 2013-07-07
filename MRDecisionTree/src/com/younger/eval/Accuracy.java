
package com.younger.eval;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.data.Data;
import com.younger.data.DataSet;


/**
 * @author apple
 */

public class Accuracy extends EvaluationMetric {
	
	private static final Logger log = LoggerFactory.getLogger(Accuracy.class);
	
	private static Accuracy instance = null;
	
	
	/**
	 * Default the value larger better
	 */
	private Accuracy() {
		super(true);
	}
	
	
	private Accuracy(boolean isLargerBetter){
		super(isLargerBetter);
	}
	
	/**
	 * 
	 * @param isLargerBetter
	 * @return
	 */
	public  static Accuracy getInstance(boolean isLargerBetter){
		if(instance ==null){
			instance = new Accuracy(isLargerBetter);
		}
		return instance;
	}
	
	/**
	 * Default the value larger better
	 * @return
	 */
	public  static Accuracy getInstance(){
		if(instance ==null){
			instance = new Accuracy();
		}
		return instance;
	}
	@Override
	/**
	 * 
	 */
	public double measure(int[] predictions, DataSet dataSet) {
		int correctCount = 0;
		for (int i = 0; i < dataSet.m_datasetSize; i++) {
			if (dataSet.getDataList().get(i).getClassValueIndex() == predictions[i]) {
				correctCount++;
			}			
		}		
		return (double) correctCount / dataSet.getDataList().size();
	}



	@Override
	public double measure(double[] predictions, DataSet dataSet)
			throws Exception {
		int correctCount = 0;
		for (int i = 0; i < dataSet.getDatasetSize(); i++) {
			int classIndex =dataSet.getDataList().get(i).getClassValueIndex();
			if ((double)classIndex == predictions[i]) {
				correctCount++;
			}			
		}		
		return (double) correctCount / dataSet.getDatasetSize();
	}

	@Override
	public double measure(int[] predictions, List<Data> dataList)
			throws Exception {
		int correctCount = 0;
		for (int i = 0; i < dataList.size(); i++) {
			int classIndex =dataList.get(i).getClassValueIndex();
			if (classIndex == predictions[i]) {
				correctCount++;
			}			
		}		
		return (double) correctCount / dataList.size();
	}

	@Override
	public double measure(double[] predictions, List<Data> dataList)
			throws Exception {
		int correctCount = 0;
		for (int i = 0; i < dataList.size(); i++) {
			int classIndex =dataList.get(i).getClassValueIndex();
			if ((double)classIndex == predictions[i]) {
				correctCount++;
			}			
		}		
		return (double) correctCount / dataList.size();
	}


	@Override
	public double measure(Integer[] predictions, DataSet dataSet)
			throws Exception {
		return measure(predictions, dataSet.getDataList());
	}


	@Override
	public double measure(Integer[] predictions, List<Data> dataList)
			throws Exception {
		int correctCount = 0;
		for (int i = 0; i < dataList.size(); i++) {
			int classIndex =dataList.get(i).getClassValueIndex();
			if ((double)classIndex == predictions[i]) {
				correctCount++;
			}			
		}		
		return (double) correctCount / dataList.size();
	}


	@Override
	/**
	 *  measure the accuracy for the dataset
	 *  @param predictions 
	 *  @param dataList
	 *  @param classValues  the classValues of the Classifier 
	 */
	public double measure(List<Integer> predictions, List<Data> dataList,List<String> classValues)
			throws Exception {
		int[] trueClassIndex = new int[dataList.size()];
		int correctCount = 0;
		for (int i = 0; i < dataList.size(); i++) {
			String collectDataClassValue = dataList.get(i).getClassValue().toLowerCase();
			int collectClassIndex = classValues.indexOf( collectDataClassValue );
			trueClassIndex[i] = collectClassIndex;
			if (collectClassIndex == predictions.get(i)) {
				correctCount++;
			}	
			
		}		
		log.debug("the predictions are" +Arrays.toString(predictions.toArray()));
		log.debug("the factural    are "+Arrays.toString(trueClassIndex));
		return (double) correctCount / dataList.size();
	}

//	/**
//	 *  measure the accuracy for the dataset
//	 *  @param predictions 
//	 *  @param dataList
//	 *  @param classValues  the classValues of the Classifier 
//	 */
//	public double measure(List<Integer> predictions, Iterable<Data> dataList,Iterable<String> classValues)
//			throws Exception {
////		int[] trueClassIndex = new int[dataList..size()];
//		Vector<Integer> trueClassIndex = new Vector<Integer>();
//		int correctCount = 0;
//		Iterator<Data> dataListIterator= dataList.iterator();
//		for(Iterator<String> classValueIterator=classValues.iterator();classValueIterator.hasNext() ;){
//			String collectDataClassValue = dataListIterator.next().getClassValue().toLowerCase();
//			int collectClassIndex =  classValues.indexOf( collectDataClassValue );
//			trueClassIndex[i] = collectClassIndex;
//			if (collectClassIndex == predictions.get(i)) {
//				correctCount++;
//			}	
//			
//		}		
//		log.debug("the predictions are" +Arrays.toString(predictions.toArray()));
//		log.debug("the factural    are "+Arrays.toString(trueClassIndex));
//		return (double) correctCount / dataList.size();
//	}

	
}
