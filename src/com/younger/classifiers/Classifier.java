package com.younger.classifiers;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.apache.hadoop.io.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.conf.Parameter;
import com.younger.constant.Enum.AttrType;
import com.younger.core.SerializedObject;
import com.younger.data.Data;
import com.younger.data.DataSet;
import com.younger.learning.LearningModule;
import com.younger.tool.Timer;
import com.younger.tool.Utils;

/**
 * Abstract classifier. All schemes for numeric or nominal prediction in Weka
 * extend this class. Note that a classifier MUST either implement
 * distributionForInstance() or classifyInstance().
 * 
 */
public abstract class Classifier extends LearningModule implements Cloneable,
		Serializable, Parameter,Writable {

	/** for serialization */
	private static final long serialVersionUID = 6502780192411755341L;

	private Logger log = LoggerFactory.getLogger(Classifier.class);

	/** Whether the classifier is run in debug mode. */
	protected boolean m_Debug = false;

	/**
	 * init local variable in subclass, the subClass should implement this
	 * method
	 */
	protected abstract void init();

	/**
	 * Abstract method classify instance
	 * 
	 * @param treeClassifier
	 * @param classifyData
	 * @return classValue
	 */
	public abstract String classifyInstance(Classifier classifier,
			Data classifyData);

	/**
	 * Abstract method classify instance
	 * 
	 * @param treeClassifier
	 * @param classifyData
	 * @return classValueIndex
	 */
	public abstract int classifyInstanceReturnClassValueIndex(
			Classifier classifier, Data classifyData);

	/**
	 * init Dataset from file file can be .arff or .txt , .csv and so on</br>
	 * Note : the DataSet are raw dataset may contain missing data
	 * 
	 * @param fileName
	 * @return
	 */
	public DataSet loadDataSet(String fileName) {
		DataSet dataSet = new DataSet();
		if (fileName.toLowerCase().endsWith(".arff")) {
			dataSet = readARFF(fileName);
		} else {
			dataSet = readData(fileName);
		}
		log.debug("--------read dataSet-------\n" + dataSet);
		return dataSet;
	}

	public DataSet loadDataSetMR(String fileName){
		DataSet dataSet = new DataSet();
		if (fileName.toLowerCase().endsWith(".arff")) {
			dataSet = readARFFMR(fileName);
		} else {
			dataSet = readDataMR(fileName);
		}
		log.debug("--------read dataSet-------\n" + dataSet);
		return dataSet;
	}
	
	public DataSet getDataSet(File file) {
		return loadDataSet(file.getAbsolutePath());
	}

	/** read dataSet from arff file */
	protected abstract DataSet readARFF(String filePath);

	protected abstract DataSet readData(String fileName);

	protected abstract DataSet readARFFMR(String filePath);
	
	protected abstract DataSet readDataMR(String fileName);
	/**
	 * 
	 * @param attributeValuesOfData
	 * @return
	 */
	public abstract int classifyInstanceReturnClassValueIndex(
			Classifier classifier, List<String> attributeValuesOfData);

	/**
	 * 
	 * @param attributeValuesOfData
	 * @return
	 */
	public abstract String classifyInstance(Classifier classifier,
			List<String> attributeValuesOfData);

	/**
	 * Abstract method
	 * 
	 * @param dataList
	 * @return
	 */
	public abstract List<Integer> classifyDataSet(Classifier classifier,
			List<Data> dataList);

	/**
	 * Abstract method
	 * @param classifier
	 * @param dataSet {@link DataSet}
	 * @return
	 */
	public abstract List<Integer> classifyDataSet(Classifier classifier,
			DataSet dataSet);

	/**
	 * 
	 * @param dataList
	 * @return
	 */
	public abstract double getClassifyAccuracy(Classifier classifier,
			List<Data> dataList);

	/**
	 * 
	 * @param dataSet
	 * @return
	 * @throws Exception
	 */
	public abstract double getClassifyAccuracy(Classifier classifier,
			DataSet dataSet) throws Exception;

	/**
	 * Generates a classifier. Must initialize all fields of the classifier that
	 * are not being set via options (ie. multiple calls of buildClassifier must
	 * always lead to the same result). Must not change the dataset in any way.
	 * 
	 * @param data
	 *            set of instances serving as training data
	 * @exception Exception
	 *                if the classifier has not been generated successfully
	 */


	// /**
	// * Classifies the given test instance. The instance has to belong to a
	// * dataset when it's being classified. Note that a classifier MUST
	// * implement either this or distributionForInstance().
	// *
	// * @param instance the instance to be classified
	// * @return the predicted most likely class for the instance or
	// * Instance.missingValue() if no prediction is made
	// * @exception Exception if an error occurred during the prediction
	// */
	// public double classifyInstance(Data instance) throws Exception {
	//
	// double [] dist = distributionForInstance(instance);
	// if (dist == null) {
	// throw new Exception("Null distribution predicted");
	// }
	// switch (instance.classAttribute().type()) {
	// case Attribute.NOMINAL:
	// double max = 0;
	// int maxIndex = 0;
	//
	// for (int i = 0; i < dist.length; i++) {
	// if (dist[i] > max) {
	// maxIndex = i;
	// max = dist[i];
	// }
	// }
	// if (max > 0) {
	// return maxIndex;
	// } else {
	// return Instance.missingValue();
	// }
	// case Attribute.NUMERIC:
	// return dist[0];
	// default:
	// return Instance.missingValue();
	// }
	// }

	/**
	 * Predicts the class memberships for a given instance. If an instance is
	 * unclassified, the returned array elements must be all zero. If the class
	 * is numeric, the array must consist of only one element, which contains
	 * the predicted value. Note that a classifier MUST implement either this or
	 * classifyInstance().
	 * 
	 * @param instance
	 *            the instance to be classified
	 * @return an array containing the estimated membership probabilities of the
	 *         test instance in each class or the numeric prediction
	 * @exception Exception
	 *                if distribution could not be computed successfully
	 */
	// public double[] distributionForInstance(Instance instance) throws
	// Exception {
	//
	// double[] dist = new double[instance.numClasses()];
	// switch (instance.classAttribute().type()) {
	// case Attribute.NOMINAL:
	// double classification = classifyInstance(instance);
	// if (Instance.isMissingValue(classification)) {
	// return dist;
	// } else {
	// dist[(int)classification] = 1.0;
	// }
	// return dist;
	// case Attribute.NUMERIC:
	// dist[0] = classifyInstance(instance);
	// return dist;
	// default:
	// return dist;
	// }
	// }
	//
	/**
	 * Creates a new instance of a classifier given it's class name and
	 * (optional) arguments to pass to it's setOptions method. If the classifier
	 * implements OptionHandler and the options parameter is non-null, the
	 * classifier will have it's options set.
	 * 
	 * @param classifierName
	 *            the fully qualified class name of the classifier
	 * @param options
	 *            an array of options suitable for passing to setOptions. May be
	 *            null.
	 * @return the newly created classifier, ready for use.
	 * @exception Exception
	 *                if the classifier name is invalid, or the options supplied
	 *                are not acceptable to the classifier
	 */
	public static Classifier forName(String classifierName, String[] options)
			throws Exception {

		return (Classifier) Utils.forName(Classifier.class, classifierName,
				options);
	}

	/**
	 * Creates a deep copy of the given classifier using serialization.
	 * 
	 * @param model
	 *            the classifier to copy
	 * @return a deep copy of the classifier
	 * @exception Exception
	 *                if an error occurs
	 */
	public static Classifier makeCopy(Classifier model) throws Exception {

		return (Classifier) new SerializedObject(model).getObject();
	}

	/**
	 * Creates a given number of deep copies of the given classifier using
	 * serialization.
	 * 
	 * @param model
	 *            the classifier to copy
	 * @param num
	 *            the number of classifier copies to create.
	 * @return an array of classifiers.
	 * @exception Exception
	 *                if an error occurs
	 */
	public static Classifier[] makeCopies(Classifier model, int num)
			throws Exception {

		if (model == null) {
			throw new Exception("No model classifier set");
		}
		Classifier[] classifiers = new Classifier[num];
		SerializedObject so = new SerializedObject(model);
		for (int i = 0; i < classifiers.length; i++) {
			classifiers[i] = (Classifier) so.getObject();
		}
		return classifiers;
	}

	/**
	 * Set debugging mode.
	 * 
	 * @param debug
	 *            true if debug output should be printed
	 */
	public void setDebug(boolean debug) {

		m_Debug = debug;
	}

	/**
	 * Get whether debugging is turned on.
	 * 
	 * @return true if debugging output is on
	 */
	public boolean getDebug() {

		return m_Debug;
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String debugTipText() {
		return "If set to true, classifier may output additional info to "
				+ "the console.";
	}

	/**
	 * runs the classifier instance with the given options.
	 * 
	 * @param classifier
	 *            the classifier to run
	 * @param options
	 *            the commandline options
	 */
	protected static void runClassifier(Classifier classifier, String[] options) {
		try {
			// log.debug(Evaluation.evaluateModel(classifier,
			// options));
		} catch (Exception e) {
			if (((e.getMessage() != null) && (e.getMessage().indexOf(
					"General options") == -1))
					|| (e.getMessage() == null))
				e.printStackTrace();
			else
				System.err.println(e.getMessage());
		}
	}

	/**
	 * runs the classifier instance with the given options.
	 * 
	 * @param classifier
	 *            the classifier to run
	 * @param options
	 *            the commandline options
	 */
	protected static void runClassifier(Classifier classifier) {
		try {
			// log.debug(Evaluation.evaluateModel(classifier,
			// options));
		} catch (Exception e) {
			if (((e.getMessage() != null) && (e.getMessage().indexOf(
					"General options") == -1))
					|| (e.getMessage() == null))
				e.printStackTrace();
			else
				System.err.println(e.getMessage());
		}
	}

	/**
	 * subclass should overrite this method to build classifier
	 * 
	 * @param data
	 * @return
	 */
	public abstract Classifier buildClassifierOverrite(DataSet data);

	/**
	 * subclass should overrite this method to build classifier
	 * 
	 * @param data
	 * @return
	 */
	public Classifier buildClassifierOverrite(String dataFilePath) {
		DataSet dataSet = loadDataSet(dataFilePath);
		return buildClassifierOverrite(dataSet);
	}

	/**
	 * build Classifier , this method shouldn't be overrite if the
	 * buildClassifierOverrite return null ,then log error </b> else do nothiog
	 * 
	 * @param dataSet
	 */
	public void buildClassifier(DataSet dataSet) {
		Timer timer = Timer.Instance();
		timer.start();
		if (buildClassifierOverrite(dataSet) != null) {
			log.info("Time taken to build the classifier model: "
					+ timer.getElapsedMillis() + " millis");
			// log.info("Time for building the classifier model "
			// +timer.getElapsedSeconds()+" seconds");
		} else {
			log.error("build classfier failed !");
		}
	}

	public void buildClassifier(String dataFilePath) {
		Timer timer = Timer.Instance();
		timer.start();
		if (buildClassifierOverrite(dataFilePath) != null) {
			log.info("Time taken to build the classifier model: "
					+ timer.getElapsedMillis() + " millis");
			// log.info("Time for building the classifier model "
			// +timer.getElapsedSeconds()+" seconds");
		} else {
			log.error("build classfier failed !");
		}
	}

	/** show the classifier */
	public abstract String showClassifier();

	/**
	 * clear variables
	 */
	public abstract void clearClassifier();
	
	/**
	 * get the class value by index 
	 * @param classValueIndex
	 * @return
	 */
	public abstract String getClassValueByIndex(int classValueIndex);
	
	
	public abstract int getClassValueIndexByValue(String classValue) ;

	public abstract int getAttributeNameIndexByValue(String attributeName);
	
	public abstract String getAttributeNameByIndex(int attributeNameIndex);
	
	public abstract AttrType getTypeOfAttribute(int attributeNameIndex);

}
