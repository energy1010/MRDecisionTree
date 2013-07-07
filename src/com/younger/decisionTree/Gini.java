package com.younger.decisionTree;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;

import com.younger.struct.ContinousHistogram;
import com.younger.tool.Tool;

public class Gini {
	
	
	/**
	 * gini(T,A) = 1- \sum {p_{i} ^{2} }
	 * @param dictionary
	 * @return
	 */
	public static double gini(Dictionary<?, Integer> dictionary) {
		return gini(dictionary.elements());
	}
	
	public static double gini(Enumeration<?> enumeration){
		double gini = 1.0d;
		double sum = Tool.getSum(enumeration);
		double p=0.0d;
		while (enumeration.hasMoreElements()) {
			Object object = (Object) enumeration.nextElement();
			 if (object instanceof Integer) {
				 int oInteger =(Integer) object;
				 p=oInteger/sum;
			}
			 else if (object instanceof Double || object instanceof Float) {
				 p= ((Double)object) /sum;
			}
			 gini-= Math.pow(p, 2);
		}
		return gini;
	}
	
	/**
	 * gini(T,A) = 1- \sum {p_{i} ^{2} }
	 * @param dictionary
	 * @return
	 */
	public static double gini(Iterator<?> iterator){
		double gini = 1.0d;
		double sum = Tool.getSum(iterator);
		double p=0.0d;
		while (iterator.hasNext()) {
			Object object = iterator.next();
			 if (object instanceof Integer) {
				 int oInteger =(Integer) object;
				 p=oInteger/sum;
			}
			 else if (object instanceof Double || object instanceof Float) {
				 p= ((Double)object) /sum;
			}
			 gini-= Math.pow(p, 2);
		}
		return gini;
	}
	
	
	public static double gini(int ...array){
		double sum = Tool.getSum(array);
		double gini = 1.0d;
		double p=0.0d;
		for(int i=0;i<array.length;i++){
			p = array[i]/sum;
			gini-=Math.pow(p, 2);
		}
		return gini;
	}
	
	/**
	 * 计算连续属性划分后的gini值
	 * 
	 * @param continousAttributeDistribution
	 * @return
	 */
	public static double gini(
			ContinousHistogram ContinousHistogram) {
		Dictionary<Integer, Integer> pre = ContinousHistogram.getPreClassNum();
		Dictionary<Integer, Integer> post = ContinousHistogram.getPostClassNum();
		double preSize=Tool.getSum(pre.elements());
		double postSize=Tool.getSum(post.elements());
		double gini = (preSize *Gini. gini(pre) + postSize*Gini.gini(post))
				/ ((double) ( preSize+postSize));
		return gini;
	}
	

	/**
	 * 
	 * @param attributeClassDictionary
	 * @return
	 */
	public static double GiniOfattributeClassDictionary(
			Dictionary<String, Dictionary<String, Integer>> attributeClassDictionary) {
		double result = 0;
		double gini = 0;
		Dictionary<String, Integer> dictionary = null;
		int sum = 0;
		for (Enumeration<Dictionary<String, Integer>> e = attributeClassDictionary
				.elements(); e.hasMoreElements();) {
			dictionary = e.nextElement();
			for (Enumeration<Integer> enumeration = dictionary.elements(); enumeration
					.hasMoreElements();) {
				sum += enumeration.nextElement();
			}
		}
		for (Enumeration<Dictionary<String, Integer>> e = attributeClassDictionary
				.elements(); e.hasMoreElements();) {
			dictionary = e.nextElement();
			gini = Gini.gini(dictionary);
			result += (Tool.getSum(dictionary.elements()) / sum )* gini;
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(Gini.gini(1,2,4));
	}
	
}
