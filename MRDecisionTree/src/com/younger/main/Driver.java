package com.younger.main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.younger.bayes.NaiveBayes;
import com.younger.conf.Parameter;
import com.younger.data.Data;
import com.younger.data.DataSet;
import com.younger.frame.DecisionTreeTest;

public class Driver {
	
	public static void main(String[] args) {
	Driver driver = new Driver();
//	driver.testBayes();
	driver.testUI();
//	driver.testDT();
//	driver.testCopy();
	}
	
	public void testBayes() {
//		NavieBayes navieBayes = new NavieBayes(Parameter.ARFFfilePath);
		NaiveBayes navieBayes = new NaiveBayes();
		
		DataSet dataSet = navieBayes.loadDataSet(Parameter.ARFFfilePath);
//		DataSet dataSet = navieBayes.getDataSet("file/weather.numeric.arff");
		System.out.println(navieBayes);
		navieBayes.buildClassifier(dataSet);
		 System.out.println("-----------------------------------------");
		 Data data = new Data();
		 List<String> attvalues= new LinkedList<String>();
//		 attvalues.add("sunny");
//		 attvalues.add("cool");
//		 attvalues.add("high");
		 
//		 attvalues.add("TRUE");
//		 List<AttrType> attTypeList = new LinkedList<AttrType>();
//		 attTypeList.add(AttrType.Category);
//		 attTypeList.add(AttrType.Category);
//		 attTypeList.add(AttrType.Category);
//		 attTypeList.add(AttrType.Category);
//		 data.setAttributevaluesList(attvalues);
//		 System.out.println(data);
//		 System.out.println(navieBayes.classifyInstance(navieBayes,data) );
//		 navieBayes. predictClassProbalityOfData(data);
		System.out.println( navieBayes.getClassifyAccuracy(navieBayes, dataSet)  );
//		overcast, mild, high, true
//		attvalues.add("sunny");
//		 attvalues.add("mild");
//		 attvalues.add("high");
//		 attvalues.add("TRUE");
//		navieBayes.classifyInstance(navieBayes, attvalues);
	}
	
	
	public void testCopy(){
		List<Integer> aList = new ArrayList<Integer>();
		List<Integer> bList = new ArrayList<Integer>();
		for(int i=0;i<10;i++){
			aList.add(i);
		}
		System.out.println(aList);
		List<Integer> cList = aList.subList(2, 5);
		System.out.println(cList);
		bList.addAll(cList);
		System.out.println(bList);
	}
	
	public void testUI(){
		  DecisionTreeTest localDecisionTreeTest = DecisionTreeTest.instance();
	}

	public void testDT(){
		
//		file/breast-cancer.arff
//		weather.numeric.arff
//		EntropyDecisionTree entropyDecisionTree = new EntropyDecisionTree("file/breast-cancer.arff");
//		EntropyDecisionTree entropyDecisionTree = new EntropyDecisionTree("weather.numeric.arff");
//		EntropyDecisionTree entropyDecisionTree = new EntropyDecisionTree("contact-lenses.arff");
//		EntropyDecisionTree entropyDecisionTree = new EntropyDecisionTree();
//		DataSet trainDataSet = entropyDecisionTree.getDataSet("file/weather.numeric.arff");
//		System.out.println("-----------------------------------------");
//		System.out.println(entropyDecisionTree);
//		System.out.println("-----------------------------------------");
//		file/weather.arff
//		try {
//			entropyDecisionTree.buildClassifier(trainDataSet);
//			"file/weather.numeric.arff"   Parameter.ARFFfilePath
//			entropyDecisionTree.buildClassifier("file/weather.numeric.arff");
//			entropyDecisionTree.buildClassifier(Parameter.ARFFfilePath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println("-----------------------------------------");
//		System.out.println(entropyDecisionTree);
//		System.out.println("-----------------------------------------");
//		entropyDecisionTree.showClassifier();
//		entropyDecisionTree.getTreeDataNodesList();
//		System.out.println( entropyDecisionTree.getClassifyAccuracy(entropyDecisionTree, entropyDecisionTree.getTraingData()) );
//		entropyDecisionTree.writeTreeNodesToFile(new File("/Users/apple/Desktop/b.txt") );
//		DomXml.writeTreeNodeListToXML("/Users/apple/Desktop/listd.xml", dataNodesList);
		//		Data classifyData=new Data();
//		classifyData.setAttributevaluesList("sunny","57","85","TRUE");
//		System.out.println( entropyDecisionTree.classifyInstance(entropyDecisionTree, classifyData) );
//		DomXml.writeTreeToXml1("/Users/apple/Desktop/contact.xml",entropyDecisionTree);
//		DataSet.writeDataListToFile(entropyDecisionTree.getTraingData().getDataList(), "/Users/apple/Desktop/i.txt");
//		System.out.println( entropyDecisionTree.getBestAttributeIndexByGainRatio(entropyDecisionTree.getTraingData().getDataList(), entropyDecisionTree.getAttributeIndexs()) );
//		Data data = entropyDecisionTree.getTraingData().getTrainingDatas().get(0);
//		System.out.println(data);
//		System.out.println(entropyDecisionTree.getClassifyAccuracy(entropyDecisionTree.getRootNode(), entropyDecisionTree.getTraingData().getTrainingDatas()));
//		AttributeSelectionPanel attributeSelectionPanel = new AttributeSelectionPanel();
//		attributeSelectionPanel.setInstances(entropyDecisionTree.getTraingData());
////		attributeSelectionPanel.set
//		attributeSelectionPanel.setVisible(true);
		//		entropyDecisionTree.getbe
//		DomXml.writeTreeToXml1("/Users/apple/Desktop/god2.xml",entropyDecisionTree);
//		 XpathXml.writeXML("dt.xml",entropyDecisionTree.getXmldoc());
//		entropyDecisionTree.getTraingData().writeDatasetToFile("file/file2.txt");
		
		// int height =
		// entropyDecisionTree.getHeight(entropyDecisionTree.rootNode);
		// System.out.println("tree Height: "+height);
		// System.out.println("-----------------------------------------");
//		entropyDecisionTree.printPath(entropyDecisionTree.getRootNode(), "");
//		System.out.println("tree size "+entropyDecisionTree.LeafNodesSize);
		// Dictionary<String, Dictionary<String, Integer>> attriDictionary =
		// entropyDecisionTree.getClassDistributionByDiscreteAttribute(entropyDecisionTree.traingDataList,0);
		// double outlook = entropyDecisionTree.entropy(5,4,5);
		// double temp = entropyDecisionTree.entropy(4,6,4);
		// double hum = entropyDecisionTree.entropy(7,7);
		// double wind = entropyDecisionTree.entropy(8,6);
		//
		// Hashtable<String, List<Integer>> dictionary = new Hashtable<String,
		// List<Integer>>();
		// List<Integer> sunny = new ArrayList<Integer>();
		// sunny.add(2);
		// sunny.add(3);
		// dictionary.put("sunny", sunny);
		// List<Integer> overcast = new ArrayList<Integer>();
		// overcast.add(4);
		// overcast.add(0);
		// dictionary.put("overcast", overcast);
		// List<Integer> rain = new ArrayList<Integer>();
		// rain.add(3);
		// rain.add(2);
		// dictionary.put("rain", rain);
		//
		// double o1 = entropyDecisionTree.entropy(dictionary);
		// int n = entropyDecisionTree.traingDataList.get(0).getAttributeNum();
		// List<ContinousAttributeDistribution> con =
		// entropyDecisionTree.getDistributionByContinousAttribute(0);
		// Dictionary<String,Integer> d =
		// entropyDecisionTree.getDistributionByAttribute(1);
		// Dictionary<String, Integer> c=
		// entropyDecisionTree.getDataDistribution(entropyDecisionTree.traingDataList);
		// System.out.println(entropyDecisionTree.getTotalTrainingDataEntropy());
		// System.out.println(entropyDecisionTree.entropy(4,2));
		// System.out.println(
		// entropyDecisionTree.informationGain(entropyDecisionTree.traingDataList,
		// 2) );
//		 List<String> v=new ArrayList<String>();
//		 v.add("Sunny");
//		 v.add("Hot");
//		 v.add("Normal");
//		 v.add("Strong");
//		 System.out.println("----------------------------------");
//		 System.out.println("the predict class for the test is :"
//		 +entropyDecisionTree.classifyInstance(entropyDecisionTree.getRootNode(),
//		 v));
	}

}
