package com.younger.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.conf.Constants;

/**
 * @author Knuke Gemini <yyyang@tju.edu.cn>
 * @version 1.0
 */

public class MathUtil {
	
	private static final Logger log = LoggerFactory.getLogger(MathUtil.class);
	/**
	 * Task: 获得以2为底的对数
	 * 
	 * @param num
	 * @return
	 */
 	public static double log2Num(double num) {
		assert (num >= 0);
		if (num == 0)
			return 0; // 0log0 = 0;
		if (num == 1)
			return 0;
		return Math.log(num) / Math.log(2);
	}
	
	public static double log2(double value) {
		return Math.log(value) / Constants.LN2;
	}

	public static double getAvg(double[] arr) {
		double sum = 0;
		for (double item : arr) {
			sum += item;
		}
		return sum / arr.length;
	}
	
	public static double getAvg1(double ...arr) {
		double sum = 0;
		for (int i=0;i< arr.length;i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}

	public static double getAvg(Iterable<Double> arr){
		double sum = 0;
		int num=0;
		for (Double d: arr) {
			sum += d;
			num++;
		}
		return sum/num;
	}
	
	public static double getAvg(float[] arr) {
		double sum = 0;
		for (double item : arr) {
			sum += item;
		}
		return sum / arr.length;
	}
	
	
	public static double getAvgOfString(Iterable<String> arr) {
		double sum = 0;
		int num = 0;
		for (String item : arr) {
			sum += Double.valueOf(item);
		}
		return sum /num;
	}

	/**
	 * get the avg of dictionary .</br>
	 * eg. { {temperature={85=1, 83=1, 81=1, 80=1, 75=2, 72=2, 71=1, 69=1, 68=1, 70=1, 65=1, 64=1},</br>
	 *  windy={false=8, true=6}, humidity={91=1, 90=2, 86=1, 85=1, 80=2, 75=1, 70=3, 96=1, 95=1, 65=1},</br>
	 *   outlook={rainy=5, overcast=4, sunny=5}}} </br>
	 * @param map
	 * @return
	 */
	public static double getAvg(Map<String, Integer> map){
		double sum =0.0d;
		int totalNum = 0;
		for(String key: map.keySet()){
			int num = map.get(key);
			totalNum +=num;
			sum+= (num*Double.valueOf(key));	
		}
//		log.debug("the avage for dictionary "+dictionary.toString()+ " is "+ sum/totalNum);
		return sum/totalNum;
	}
	
	public static double getAvg(List<Double> arr) {
		double sum = 0;
		for (double item : arr) {
			sum += item;
		}
		return sum / arr.size();
	}
	
	public static double getStd(Map<String, Integer> map){
		return getStdOfHashTable(map,getAvg(map));
	}
	
	public static double getStd(Map<String, Integer> map,double avg){
		return getStdOfHashTable(map,avg);
	}

     public static double getStd(double[] arr) {
		return getStd(arr, getAvg(arr));
	}
	
	public static double getStd1(double ...arr){
		return getStd(getAvg1(arr), arr);
	}

	public static double getStd(List<Double> arr) {
		return getStd(arr, getAvg(arr));
	}

	public static double getStd(Iterable<Double> arr){
		return getStd(arr,getAvg(arr));
	}
	
	
	public static double getStdOfString(Iterable<String> arr){
		return getStdOfString(arr,getAvgOfString(arr));
	}
	

	public static double getStdOfString(Iterable<String> arr, double avg) {
		double sum = 0;
		int num = 0;
		for (String item : arr) {
			sum += Math.pow(Double.valueOf(item) - avg, 2);
			num++;
		}
		return Math.sqrt(sum / (num-1) );
	}

	public static double getStdOfHashTable(Map<String,Integer> map, double avg) {
		double sum = 0;
		int totalNum = 0;
		for(String valueString :map.keySet()){
			double value = Double.valueOf(valueString);
			int num = map.get(valueString);
			totalNum +=num;
			sum+=  (Math.pow(value- avg, 2))*num; 
		}
//		log.debug("the std for dictionary "+dictionary.toString()+ " is "+ Math.sqrt(sum / (totalNum-1)));
		return Math.sqrt(sum / (totalNum-1) );
	}
	
	private static double getStd(double[] arr, double avg) {
		return getStd(arr, avg, 0, arr.length);
	}
	
	/**
	 * sqrt( D(x) )
	 */
	public static double getStd(double[] arr, double avg, int offset, int count) {
		double sum = 0;
		int end = offset + count;
		for (int i = offset; i < end; i++) {
			sum += Math.pow(arr[i] - avg, 2);
		}
		return Math.sqrt(sum / (count-1) );
	}

	public static double getStd(List<Double> arr, double avg) {
		double sum = 0;
		for (double item : arr) {
			sum += Math.pow(item - avg, 2);
		}
		return Math.sqrt(sum / (arr.size()-1));
	}
	
	public static double getStd(Iterable<Double> arr, double avg) {
		int num=0;
		double sum = 0;
		for (double item : arr) {
			sum += Math.pow(item - avg, 2);
			num++;
		}
		return Math.sqrt(sum / (num-1));
	}
	
	/**
	 * D(x)
	 * @param avg
	 * @param arr
	 * @return
	 */
	public static double getVar(double avg,double ...arr){
		double sum=0.0d;
		for(int i=0;i< arr.length;i++){
			sum += Math.pow(arr[i] - avg, 2);
		}
		return sum / (arr.length-1);
	}
	
	public static double getVar1(double ...ar){
		return getVar(getAvg1(ar), ar);
	}
	
	/**
	 * sqrt{D(x)}
	 * @param avg
	 * @param arr
	 * @return
	 */
	public static double getStd(double avg,double ...arr){
		double sum=0.0d;
		for(int i=0;i< arr.length;i++){
			sum += Math.pow(arr[i] - avg, 2);
		}
		return Math.sqrt(sum / (arr.length-1));
	}
	

	public static double getDotProduct(float[] vector1, float[] vector2, int length) {
		double product = 0;
		for (int i = 0; i < length; i++) {
			product += vector1[i] * vector2[i];
		}
		return product;
	}
	
	public static double getDotProduct(double[] vector1, double[] vector2, int length) {
		double product = 0;
		for (int i = 0; i < length; i++) {
			product += vector1[i] * vector2[i];
		}
		return product;
	}
	
	public static double getDotProduct(float[] vector1, float[] vector2)
    {
        return getDotProduct(vector1, vector2, vector1.length);
    }
	
	/**
	 * Divides the second vector from the first one (vector1[i] /= val)
	 * @param vector	the input vector.
	 * @param val		the denominator
	 */
    public static void divideInPlace(float[] vector, float val)
    {
        int length = vector.length;
        for (int i = 0; i < length; i++)
        {
        	vector[i] /= val;
        }
    }
    
    public static double[][] allocateDoubleMatrix(int m, int n) {
		double[][] mat = new double[m][];
		for (int i = 0; i < m; i++) {
			mat[i] = new double[n];
		}
		return mat;
	}
    
    public static void clearDoubleMatrix(double[][] mat) {
    	int rows = mat.length;
    	for (int r = 0; r < rows; r++) {
    		int cols = mat[r].length;
    		for (int c = 0; c < cols; c++) {
    			mat[r][c] = 0;
    		}
    	}
    }
    
    public static double[][] cloneDoubleMatrix(double[][] src) {
    	int rows = src.length;
    	double[][] clone = new double[rows][];
    	for (int r = 0; r < rows; r++) {
    		int cols = src[r].length;
    		clone[r] = new double[cols];
    		for (int c = 0; c < cols; c++) {
    			clone[r][c] = src[r][c];
    		}
    	}
    	return clone;
    }
    
	/**
	 * @author 
	 * @param v 做标准化的样本数据
	 * @param Min 样本数据最小值
	 * @param Max 样本数据最大值
	 * @param newMin 新的映射区间最小值
	 * @param newMax 新的映射区间最大值
	 * @return
	 */
	public double normalization(double v, double Min, double Max, double newMin, double newMax) {
		return (v-Min)/(Max-Min)*(newMax-newMin)+newMin;
	}
	
	/**
	 * 归一化
	 * @param values
	 * @return
	 */
	public static List<Double> toSumOne(List<Double> values){
		List<Double> sDoubles= new ArrayList<Double>();
		Double sum=	Tool.getSum(values);
		for (Double d:values) {
			sDoubles.add(d/sum);
		}
		return sDoubles;
	}
	
	public static List<Double> laplaceList(List<Double> list){
		for(int i=0;i<list.size();i++){
		list.set(i, (list.get(i)+1)/(Tool.getSum(list)+(double)list.size()) );
		}
		return list;
	}
	
	public static Double laplaceList(List<Double> list,int index){
		double value  = (list.get(index)+1)/(Tool.getSum(list)+(double)list.size()) ;
		return value;
	}
	
	
	public static Double[] laplaceArray(Double[] list){
		double sum = 0;
		for (double item : list) {
			sum += item;
		}
		for(int i=0;i<list.length;i++){
			list[i]= (list[i]+1)/(sum+(double)list.length) ;
		}
		return list;
	}
	
	public static Double[] laplace(Double... list){
		double sum = 0;
		for (double item : list) {
			sum += item;
		}
		for(int i=0;i<list.length;i++){
			list[i]= (list[i]+1)/(sum+(double)list.length) ;
		}
		return list;
	}
	
	
	public static double normalProbability(double value){
		double y = Math.pow(value, 2)/2.0;
		double exp=Math.exp(-y);
		double a= 1.0/(Math.sqrt(2*Math.PI));
		return a*exp;
	}
	
	public static double normalProbability(double value,double mean ,double sigma){
		double y = (value-mean)/sigma;
		return normalProbability(y);
	}
	
	
	public static void main(String[] args) {
//		MathUtil.laplace(2.0,3.0);
//		System.out.println(getAvg1(1,1.2,3));
//		System.out.println(getStd1(1,1.2,3.0,4.4,4.5));
		System.out.println(getAvg1(64.0, 75,75, 72, 83, 69, 70, 81, 68));
		System.out.println(getStd1(64.0, 75,75, 72, 83, 69, 70, 81, 68));
		
		System.out.println(getVar(getAvg1(64.0, 75,75, 72, 83, 69, 70, 81, 68),64.0, 75,75, 72, 83, 69, 70, 81, 68));
		System.out.println(getVar1(64.0, 75,75, 72, 83, 69, 70, 81, 68));
		System.out.println(getStd1(64.0, 75,75, 72, 83, 69, 70, 81, 68));
		System.out.println(getVar(getAvg1(1.0,2.0,3.0),1.0,2.0,3.0));
//		System.out.println(getv
		System.out.println(getStd1(1.0,2.0,3.0));
		
		System.out.println(getAvg1(6.0, 5.92, 5.58, 5.92));
		System.out.println(getStd1(6.0, 5.92, 5.58, 5.92));
		System.out.println(getVar1(6.0, 5.92, 5.58, 5.92));
		System.out.println(normalProbability(1));
		System.out.println(normalProbability(-1));
	}
	
}
