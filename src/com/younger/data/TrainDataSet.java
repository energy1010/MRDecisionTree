
package com.younger.data;

import java.util.List;

import com.younger.conf.Constants;

/**
 *  Training Data Set
 */
public class TrainDataSet extends DataSet {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 5638112538382004697L;

	//	public int[] m_indicesInDataSet;
	public double[] m_weights;
//	public double[] targets;

	// Only used in sub samples
	public int[] m_indicesInParentSample;

	public TrainDataSet(List<Data> dataList){
		this.m_dataList = dataList;
		this.m_datasetSize = dataList.size(); 
		Constants.init(m_datasetSize);
//		m_indicesInDataSet = Constants.ONE_TWO_THREE_ETC;
		m_weights = Constants.DOUBLE_ONE_ONE_ONE_ETC;
	}
	
	public TrainDataSet(List<Data> dataList,int datasetSize) {
		this.m_dataList = dataList;
		this.m_datasetSize = datasetSize; 
		Constants.init(m_datasetSize);
//		m_indicesInDataSet = Constants.ONE_TWO_THREE_ETC;
		m_weights = Constants.DOUBLE_ONE_ONE_ONE_ETC;
	}

	public TrainDataSet(List<Data> dataList, double[] weights,
			int[] indicesInParentSample, int size) {
		this.m_dataList = dataList;
//		this.m_indicesInDataSet = indicesInDataSet;
		this.m_weights = weights;
		this.m_datasetSize = size;
		this.m_indicesInParentSample = indicesInParentSample;
	}

	public TrainDataSet getClone() {
		return new TrainDataSet(this.m_dataList, m_weights,  m_indicesInParentSample, this.m_datasetSize);
	}
	
//	public double evaluate(double[] predictions, EvaluationMetric evaluationMetric) throws Exception {
//		return evaluationMetric.measure(predictions, this);
//	}
//	
//	public double evaluate(double[] predictions, EvaluationMetric evaluationMetric, double factor) throws Exception {
//		double[] newPredictions = new double[predictions.length];
//		for (int i = 0; i < predictions.length; i++) {
//			newPredictions[i] = predictions[i] * factor;
//		}
//		return evaluationMetric.measure(newPredictions, this);
//	}

	
//
//	/**
//	 * Creates a sample from instances that are in this sample and not in
//	 * subSample
//	 */
//	public TrainDatas getOutOfSample(DataSet subSample) {
//		int oosSize = this.m_datasetSize - subSample.getDatasetSize();
//		int[] oosIndicesInDataSet = new int[oosSize];
//		double[] oosWeights = new double[oosSize];
//		double[] oosTargets = new double[oosSize];
//		int samplePtr = 0;
//		int oosCurSize = 0;
//		for (int i = 0; i < this.m_datasetSize; i++) {
//			if (subSample.m_indicesInDataSet[samplePtr] > m_indicesInDataSet[i]) {
//				oosIndicesInDataSet[oosCurSize] = m_indicesInDataSet[i];
//				oosWeights[oosCurSize] = m_weights[i];
//				oosTargets[oosCurSize] = targets[i];
//				oosCurSize++;
//			} else if (subSample.m_indicesInDataSet[samplePtr] == m_indicesInDataSet[i]) {
//				samplePtr++;
//				if (samplePtr >= subSample.size) {
//					break;
//				}
//			}
//		}
//		return new TrainDatas(DataSet, oosIndicesInDataSet, oosWeights, oosTargets, null, oosSize);
//	}


//	public int[] getIndicesInDataSet() {
//		return m_indicesInDataSet;
//	}
//
//	public void setIndicesInDataSet(int[] indicesInDataSet) {
//		this.m_indicesInDataSet = indicesInDataSet;
//	}

	public double[] getWeights() {
		return m_weights;
	}

	public void setWeights(double[] weights) {
		this.m_weights = weights;
	}


	public int[] getIndicesInParentSample() {
		return m_indicesInParentSample;
	}

	public void setIndicesInParentSample(int[] indicesInParentSample) {
		this.m_indicesInParentSample = indicesInParentSample;
	}
	
	
}
