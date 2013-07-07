/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.younger.decisionTree;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.younger.conf.Constants;
import com.younger.data.DataSet;
import com.younger.struct.ContinousHistogram;
import com.younger.tool.FileUtil;
import com.younger.tool.FloatingPointUtil;
import com.younger.tool.MathUtil;
import com.younger.tool.Tool;

/**
 * Entropy  (Singleton)
 * @author Administrator
 */
public class Entropy {
	
	private  static  Entropy instance = null;
	
	private Entropy(){
	
	}
	
	public static Entropy getInstance () {
		if(instance==null){
			instance = new Entropy();
		}
			return instance;
	}

	/**
	 * Computes the entropy of a given distribution.
	 * Entropy is computed as sum(-ci/T log2(ci/T)),
	 * where ci is the i-th element in the distribution
	 * and T is the sum of these elements. The following
	 * method computes the above definition using a faster
	 * computation.
	 * 
	 * @param dist
	 * @return
	 */
	public static double getEntropy(double[] dist) {
		double numerator = 0;
		double total = 0;
		//step 1 : calc the sum 
		for (int i = 0; i < dist.length; i++) {
			numerator -= entropyLn(dist[i]);
			total += dist[i];
		}
		if (FloatingPointUtil.equal(total, 0)) {
			return 0;
		}
		//step 2 : clac the entropy
		return (numerator + entropyLn(total)) / (total * Constants.LN2);
	}

	public static double getSplitEntropy(double[] leftDist, double[] rightDist) {
		double numerator = 0;
		double leftTotal = 0;
		for (int i = 0; i < leftDist.length; i++) {
			numerator -= entropyLn(leftDist[i]);
			leftTotal += leftDist[i];
		}
		numerator += entropyLn(leftTotal);
		double rightTotal = 0;
		for (int i = 0; i < rightDist.length; i++) {
			numerator -= entropyLn(rightDist[i]);
			rightTotal += rightDist[i];
		}
		numerator += entropyLn(rightTotal);
		double total = leftTotal + rightTotal;//calc the total num
		if (FloatingPointUtil.equal(total, 0)) {
			return 0;
		}

		  return numerator / (total * Constants.LN2);
	}

	/**
	 * Helper function for computing entropy.
	 * 
	 * @param num
	 * 			input value
	 * @return
	 * 			num * ln(num)
	 */
	public static double entropyLn(double num) {
		if (num < 1e-6) {
			return 0;
		} else {
			return num * Math.log(num);
		}
	}
	
	/**
	 * 
	 * @param dictionary
	 * @return
	 */
	public static double entropyOfDictionary(Dictionary<?, Integer> dictionary){
		List<Integer> list = new LinkedList<Integer>();
		for(Enumeration<Integer> enumeration = dictionary.elements();enumeration.hasMoreElements();enumeration.nextElement()){
			list.add((Integer) enumeration.nextElement());
		}
		return entropy(list);
	}
	
	/**
	 *  calc the info [ attributeValue , classValue , Num ] of each attribute
	 *   av1 [yes 2 no 1] ,av2[yes 1 no 2] .....
	 * @param attributeClassDictionary
	 * @param binarySplit
	 * @return
	 */
	public static double InfoOfattributeClassDictionary(
			Dictionary<String, Dictionary<String, Integer>> attributeClassDictionary) {
		double result = 0;
		double entropy = 0;
		Dictionary<String, Integer> dictionary = null;
		int sum = 0;
		for (Enumeration<Dictionary<String, Integer>> e = attributeClassDictionary
				.elements(); e.hasMoreElements();) {
			dictionary = e.nextElement();
			for (Enumeration<Integer> enumeration = dictionary.elements(); enumeration
					.hasMoreElements();) {
				//get the total num
				sum += enumeration.nextElement();
			}
		}
			for (Enumeration<Dictionary<String, Integer>> e = attributeClassDictionary
					.elements(); e.hasMoreElements();) {
				dictionary = e.nextElement();
				// get each part entropy
				entropy = Entropy.entropy(dictionary);
				result += (Tool.getSum(dictionary.elements()) / sum )* entropy;
			}
		return result;
	}
	
	
	/**
	 * 根据每类数量，计算Entropy值
	 * 分裂信息度量SplitInformation(S，A)
	 * @param groupList
	 *            可变数组
	 * @return double
	 */
	public static double entropy(Dictionary<?, Integer> dictionary) {
		double sum = 0;
		double dictionaySum =   Tool.getSum(dictionary.elements());
		int integer;
		for (Enumeration<Integer> e = dictionary.elements(); e
				.hasMoreElements();) {
			integer = e.nextElement();
			double p = (double) integer /dictionaySum;
			sum += (-p) *MathUtil. log2Num(p);
		}
		return sum;
	}
	/**
	 * same as 
	 * @param dictionary
	 * @return
	 */
	public static  double entropy(Hashtable<?, List<Integer>> dictionary) {
		double sum = 0;
		double total = 0;
		for (Enumeration<List<Integer>> e = dictionary.elements(); e
				.hasMoreElements();) {
			List<Integer> list = e.nextElement();
			sum += Tool.getSum(list.iterator());
		}
		for (Enumeration<List<Integer>> e = dictionary.elements(); e
				.hasMoreElements();) {
			List<Integer> list = e.nextElement();
			double p = (double) Tool.getSum(list.iterator()) / sum;
			total += entropy(list) * p;
		}
		return total;
	}
	
	/**
	 * 根据每类数量，计算Entropy值
	 * 
	 * @param groupList
	 *            可变数组
	 * @return double
	 */
	public static  double entropy(int... groupList) {
		int sum = 0;
		double entropy = 0.0;
		for (int integer : groupList) {
			sum += integer;
		}
		for (int integer : groupList) {
			double p = ((double) integer) / sum;
			entropy += (-p) * MathUtil.log2Num(p);
		}
		return entropy;
	}
	
	/**
	 *  same as 
	 * @param groupList
	 * @return double
	 */
	public static double entropy(List<Integer> groupList) {
		double entropy = 0;
		double sum = Tool.getSum(groupList.iterator());
		for (Integer integer : groupList) {
			double p = (double) integer / sum;
			entropy += (-p) * MathUtil.log2Num(p);
		}
		return entropy;
	}
	
	/**
	 * calc the entropy for the dataset
	 * @param dataSet
	 * @return
	 */
	public static double entropy(DataSet dataSet){
		return 0.0d;
	}
	
	/**
	   * Computes entropy for an array of integers.
	   * 
	   * @param counts array of counts
	   * @return - a log2 a - b log2 b - c log2 c + (a+b+c) log2 (a+b+c) when given
	   *         array [a b c]
	   */
	  public static double info(int counts[]) {

	    int total = 0;
	    double x = 0;
	    for (int j = 0; j < counts.length; j++) {
	      x -=FileUtil. xlogx(counts[j]);
	      total += counts[j];
	    }
	    return x +FileUtil. xlogx(total);
	  }
	
	
		/**
		 * 计算连续属性划分后的entropy值
		 * 
		 * @param continousAttributeDistribution
		 * @return
		 */
		public static double entropy(
				ContinousHistogram ContinousHistogram) {
			Dictionary<Integer, Integer> pre = ContinousHistogram.getPreClassNum();
			Dictionary<Integer, Integer> post = ContinousHistogram.getPostClassNum();
			double preSize=Tool.getSum(pre.elements());
			double postSize=Tool.getSum(post.elements());
			double entropy = (preSize *Entropy. entropy(pre) + postSize*Entropy.entropy(post))
					/ ((double) ( preSize+postSize));
			return entropy;
		}

		/**
		 * calc the info of each attribute value class distribution for one attribute 
		 * @param attributeValueNum ([2,3],[4,0],[3,2])
		 *  [2,3] for attribute value sunny [4,0] for attribute value overcast ,
		 * [3,2] for attribute Value ranny
		 * @return
		 */
		public static double info (Vector<Vector<Integer>>  attributeValueNum ){
			double info = 0.0d;
			double total = 0.0d;
			for (int i = 0; i < attributeValueNum.size(); i++) {
			 Vector<Integer>	 vector=attributeValueNum.get(i);
			 double vecTotal=Tool.getSum(vector.iterator());
			   total+= vecTotal;
			}
			for (int i = 0; i < attributeValueNum.size(); i++) {
				 Vector<Integer>	 vector=attributeValueNum.get(i);
				  double vecEntropy= entropy(vector);
				  double vecTotal=Tool.getSum(vector.iterator());
				   info += (vecTotal/total)*vecEntropy;
				}
			return info;
		}
		
		
		
	 public static void main(String[] args) {
//		 double[] leftDist = {1,2};
//		 double[] rightDist = {2,3};
//		 Vector<Vector<Integer>> ve = new Vector<Vector<Integer>>();
//		 Vector<Integer> v1= new Vector<Integer>();
//		 v1.add(1);
//		 v1.add(2);
//		 Vector<Integer> v2= new Vector<Integer>();
//		 v2.add(2);
//		 v2.add(3);
//		 ve.add(v1);
//		 ve.add(v2);
//		double value = Entropy.getSplitEntropy(leftDist, rightDist);
//		double val1 = Entropy.info(ve);
//		assert(val1==value);
//		System.out.println(Entropy.entropy(4,0));
		System.out.println(Entropy.entropy(9,8,7));
	}
} 
