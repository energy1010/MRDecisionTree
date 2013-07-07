
package com.younger.learning;

import com.younger.eval.EvaluationMetric;


public abstract class LearningModule {

	public LearningModule() {
		super();
	}

	protected String m_algorithmName;
	protected int m_moduleLevel = 1;

	protected LearningModule m_subLearner;
	protected LearningModule m_parentLearner;
//	protected double treeWeight = 1.0;
	protected EvaluationMetric m_evaluationMetric;
	protected LearningProgressListener m_progressListener;

	public LearningModule(String algorithmName) {
		this.m_algorithmName = algorithmName;
	}

	public void setProgressListener(LearningProgressListener progressListener) {
		this.m_progressListener = progressListener;
	}

//	public abstract Ensemble learn(Sample trainSet, Sample validSet) throws Exception;

//	public abstract double getValidationMeasurement() throws Exception;

//	/**
//	 * @param tree
//	 * @param treeLeafInstances
//	 */
//	public void postProcess(Tree tree, TreeLeafInstances treeLeafInstances) {
//		// Subclasses can override this method if needed.
//	}

	public void setSubModule(LearningModule subModule) {
		this.m_subLearner = subModule;
		this.m_subLearner.setParentModule(this);
		this.m_subLearner.m_moduleLevel = this.m_moduleLevel + 1;
	}

	public void setParentModule(LearningModule parentModule) {
		this.m_parentLearner = parentModule;
	}

//	public void setTreeWeight(double treeWeight) {
//		this.treeWeight = treeWeight;
//	}

	public void setAlgorithmName(String name) {
		this.m_algorithmName = name;
	}

	private double bestPrintedValidMeasurement = Double.NaN;

	public void printTrainAndValidMeasurement(int iteration, double validMeasurement, double trainMeasurement, EvaluationMetric evaluationMetric) {
		if (evaluationMetric.isFirstBetter(validMeasurement, bestPrintedValidMeasurement, 0)) {
			bestPrintedValidMeasurement = validMeasurement;
		}
		if (m_moduleLevel > 1) {
			return;
		}
		for (int i = 0; i < m_moduleLevel - 1; i++) {
			System.out.print("\t");
		}
		if (Double.isNaN(trainMeasurement)) {
			System.out.println(m_algorithmName + ": [Iteration: " + iteration + ", Valid: " + validMeasurement + ", Best: " + bestPrintedValidMeasurement
					+ "]");
		} else {
			System.out.println(iteration + "\t" + validMeasurement + "\t" + trainMeasurement);
		}
	}

	public void printValidMeasurement(int iteration, double validMeasurement, EvaluationMetric evaluationMetric) {
		printTrainAndValidMeasurement(iteration, validMeasurement, Double.NaN, evaluationMetric);
	}

	protected void onIterationEnd() {
		if (m_progressListener != null) {
			m_progressListener.onIterationEnd();
		}
	}

	protected void onLearningEnd() {
	}
}
