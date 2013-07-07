package com.younger.struct;

import java.io.Serializable;

/**
 * the classifier split model
 * 
 * @author apple
 * 
 */
public abstract class ClassifierSplitModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1808318875752892506L;

	// /** Distribution of class values. */
	// protected Hashtable<String, Integer> m_distribution;

	// public int m_splitRecordIdData=0;

	public double m_maxGain = 0.0d;

	/** Number of created subsets. */
	// public int m_numSubsets;

	public double getMaxGain() {
		return m_maxGain;
	}

	public void setMaxGain(double m_maxGain) {
		this.m_maxGain = m_maxGain;
	}

	/**
	 * Allows to clone a model (shallow copy).
	 */
	public Object clone() {

		Object clone = null;

		try {
			clone = super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return clone;
	}


	// /**
	// * Checks if generated model is valid.
	// */
	// public final boolean checkModel() {
	//
	// if (m_numSubsets > 0)
	// return true;
	// else
	// return false;
	// }

	// /**
	// * Classifies a given instance.
	// *
	// * @exception Exception if something goes wrong
	// */
	// public final double classifyInstance(Instance instance)
	// throws Exception {
	//
	// int theSubset;
	//
	// theSubset = whichSubset(instance);
	// if (theSubset > -1)
	// return (double)m_distribution.maxClass(theSubset);
	// else
	// return (double)m_distribution.maxClass();
	// }

	// /**
	// * Gets class probability for instance.
	// *
	// * @exception Exception if something goes wrong
	// */
	// public double classProb(int classIndex, Instance instance, int theSubset)
	// throws Exception {
	//
	// if (theSubset > -1) {
	// return m_distribution.prob(classIndex,theSubset);
	// } else {
	// double [] weights = weights(instance);
	// if (weights == null) {
	// return m_distribution.prob(classIndex);
	// } else {
	// double prob = 0;
	// for (int i = 0; i < weights.length; i++) {
	// prob += weights[i] * m_distribution.prob(classIndex, i);
	// }
	// return prob;
	// }
	// }
	// }

	// /**
	// * Gets class probability for instance.
	// *
	// * @exception Exception if something goes wrong
	// */
	// public double classProbLaplace(int classIndex, Instance instance,
	// int theSubset) throws Exception {
	//
	// if (theSubset > -1) {
	// return m_distribution.laplaceProb(classIndex, theSubset);
	// } else {
	// double [] weights = weights(instance);
	// if (weights == null) {
	// return m_distribution.laplaceProb(classIndex);
	// } else {
	// double prob = 0;
	// for (int i = 0; i < weights.length; i++) {
	// prob += weights[i] * m_distribution.laplaceProb(classIndex, i);
	// }
	// return prob;
	// }
	// }
	// }

	// /**
	// * Returns coding costs of model. Returns 0 if not overwritten.
	// */
	// public double codingCost() {
	//
	// return 0;
	// }

	// /**
	// * Returns the distribution of class values induced by the model.
	// */
	// public final Distribution distribution() {
	//
	// return m_distribution;
	// }
	//
	// /**
	// * Prints left side of condition satisfied by instances.
	// *
	// * @param data the data.
	// */
	// public abstract String leftSide(Instances data);
	//
	// /**
	// * Prints left side of condition satisfied by instances in subset index.
	// */
	// public abstract String rightSide(int index,Instances data);
	//
	// /**
	// * Prints label for subset index of instances (eg class).
	// *
	// * @exception Exception if something goes wrong
	// */
	// public final String dumpLabel(int index,Instances data) throws Exception
	// {
	//
	// StringBuffer text;
	//
	// text = new StringBuffer();
	// text.append(((Instances)data).classAttribute().
	// value(m_distribution.maxClass(index)));
	// text.append(" ("+Utils.roundDouble(m_distribution.perBag(index),2));
	// if (Utils.gr(m_distribution.numIncorrect(index),0))
	// text.append("/"+Utils.roundDouble(m_distribution.numIncorrect(index),2));
	// text.append(")");
	//
	// return text.toString();
	// }
	//
	// public final String sourceClass(int index, Instances data) throws
	// Exception {
	//
	// System.err.println("sourceClass");
	// return (new StringBuffer(m_distribution.maxClass(index))).toString();
	// }
	//
	// public abstract String sourceExpression(int index, Instances data);
	//
	// /**
	// * Prints the split model.
	// *
	// * @exception Exception if something goes wrong
	// */
	// public final String dumpModel(Instances data) throws Exception {
	//
	// StringBuffer text;
	// int i;
	//
	// text = new StringBuffer();
	// for (i=0;i<m_numSubsets;i++) {
	// text.append(leftSide(data)+rightSide(i,data)+": ");
	// text.append(dumpLabel(i,data)+"\n");
	// }
	// return text.toString();
	// }
	//
	// /**
	// * Returns the number of created subsets for the split.
	// */
	// public final int numSubsets() {
	//
	// return m_numSubsets;
	// }
	//
	// /**
	// * Sets distribution associated with model.
	// */
	// public void resetDistribution(Instances data) throws Exception {
	//
	// m_distribution = new Distribution(data, this);
	// }
	//
	// /**
	// * Splits the given set of instances into subsets.
	// *
	// * @exception Exception if something goes wrong
	// */
	// public final Instances [] split(Instances data)
	// throws Exception {
	//
	// Instances [] instances = new Instances [m_numSubsets];
	// double [] weights;
	// double newWeight;
	// Instance instance;
	// int subset, i, j;
	//
	// for (j=0;j<m_numSubsets;j++)
	// instances[j] = new Instances((Instances)data,
	// data.numInstances());
	// for (i = 0; i < data.numInstances(); i++) {
	// instance = ((Instances) data).instance(i);
	// weights = weights(instance);
	// subset = whichSubset(instance);
	// if (subset > -1)
	// instances[subset].add(instance);
	// else
	// for (j = 0; j < m_numSubsets; j++)
	// if (Utils.gr(weights[j],0)) {
	// newWeight = weights[j]*instance.weight();
	// instances[j].add(instance);
	// instances[j].lastInstance().setWeight(newWeight);
	// }
	// }
	// for (j = 0; j < m_numSubsets; j++)
	// instances[j].compactify();
	//
	// return instances;
	// }

	/**
	 * Returns weights if instance is assigned to more than one subset. Returns
	 * null if instance is only assigned to one subset.
	 */
	// public abstract double [] weights(Instance instance);

	/**
	 * Returns index of subset instance is assigned to. Returns -1 if instance
	 * is assigned to more than one subset.
	 * 
	 * @exception Exception
	 *                if something goes wrong
	 */
	// public abstract int whichSubset(Instance instance) throws Exception;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
