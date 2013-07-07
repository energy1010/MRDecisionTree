package com.younger.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Tool {
	
	private static final Logger log = LoggerFactory.getLogger(Tool.class);
	
	/**
	 * 字符串List中添加Deliem 
	 * eg List<1 2 3>  --> [1,2,3]
	 * @param strList
	 * @param deliem
	 * @return
	 */
	public static  StringBuffer listDataToString(List<String> strList,String deliem){
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<strList.size();i++) {
			if(i!=0){
				sb.append(deliem);
			}
			sb.append(strList.get(i));
		}
		return sb;
	}
	
	/**
	 * 从文件中获得每行字符串的集合
	 * @param fileName
	 * @return
	 * @author Administrator
	 */
	public static List<String> readFileByLines(String fileName){
		List<String> lineList = new ArrayList<String>();
		File file = new File(fileName);
		BufferedReader reader = null;
		boolean haveError = false;
		int line =1;
		try{
			reader= new BufferedReader(new FileReader(file));
			String tempString = null;
			while( (tempString=reader.readLine())!=null){
				tempString = Tool.removeSpace(tempString.trim());
				log.debug("line " +line+": "+tempString);
				line++;
				lineList.add(tempString);
			}
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
			haveError = true;
		}finally{
			if(reader!=null){
				try{
					reader.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(haveError){
				return null;
			}
			//注意不必返回全部， 只返回有数据的列表部分
			return lineList.subList(0, line-1);
		}
		
	}
	
	/**
	 * remove redunt space eg a  b -> a b
	 * @param str
	 * @return
	 */
	public static String removeReduntSpace(String str){
		return  str.trim().replaceAll("\\s", " ").replaceAll(" +", " ");
	}
	
	/**
	 * remove redudant space
	 * @param inputString
	 * @return
	 */
	public static String removeSpace(String inputString){
		inputString =  inputString.replaceAll("\t", " ");
		return replace(inputString, " {2,} ", " ");
	}
	
	public static String replace(String str,String regex,String newStr){
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(str);
		String s = matcher.replaceAll(newStr);
//		log.debug(" \""+str+ " \"中匹配正则表达式\""+regex+"\"部分被\""+newStr+"\"替换后: "+s);
		return s;
	}
	/**
	 * 判断字符串是否为空
	 * 
	 * @param s 需要判断字符串对象
	 * @return true 表示不为空 false 为空
	 */
	public static boolean notNull(String s) {
		if (s == null || s.trim().length() <= 0 || "".equals(s) || "null".equals(s))              
			return false;
		else
			return true;
	}
	
	
	public static void readFileByChars(String fileName){
		File file = new File(fileName);
		Reader reader = null;
		try{
			reader = new InputStreamReader(new FileInputStream(file)); //Read by char
			int tempChar;
			while( (tempChar = reader.read())!=-1){
				if((char)tempChar!='\r'){
					System.out.print((char)tempChar);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
	/**
	 * 根据分割符，处理一行字符串，返回单个词的数组集合
	 * @param line
	 * @param delim
	 * @param returnDeline
	 * @return
	 */
	public static String[] process(String line,String delim,boolean returnDeline){
		List results = new ArrayList();
		StringTokenizer st = new StringTokenizer(line,delim,returnDeline);
		while(st.hasMoreTokens()){
			String s = st.nextToken();
			results.add(s);
		}
		return (String[])results.toArray();
	}
	
	
	public static double getSum(List<Double> list){
		Double sum=0.0;
		for (Double integer : list) {
			sum+=integer;
		}
		return sum;
	}
	
	public static <T> double getSum(Collection<T> collection ){
		double sum=0.0d;
		for(T d: collection){
			if(d instanceof Integer){
				sum+=(Integer) d;
			}
			else if(d instanceof Double ||d instanceof Float||d instanceof Long){
				sum+= (Double)d;
			}
			else if (d instanceof String) {
				sum+= Double.valueOf((String)d);
			}
		}
		return sum;
	}
	

	public static double getSum(Enumeration enumeration){
		Double sum=0.0;
		while (enumeration.hasMoreElements()) {
			Object object = enumeration.nextElement();
			if(object instanceof Double || object instanceof Float){
				Double valueDouble=(Double) object;
			sum+=  valueDouble;
			}else {
				Integer valueInteger = (Integer ) object;
				sum+= valueInteger;
			}
		}
		return sum;
	}
	
	public static double getSum(int ...array){
			double sum = 0.0d;
		for(int i=0;i<array.length;i++){
			sum+=array[i];
		}
		return sum;
	}
	
	/**
	 * 
	 * @param iterator
	 * @return
	 */
	public static <T> double getSum(Iterator<T> iterator){
		double sum=0.0;
		while (iterator.hasNext()) {
			T object= iterator.next();
			if (object instanceof Integer ){
				sum+=(Integer) object;
			}
			else if (object instanceof Double || object instanceof Float) {
				 sum+=(Double)object;
			}else if(object instanceof String){
				sum+=Double.valueOf((String)object);
			}
		}
		return sum;
	}

	
	
	
	public static double getSum(Map<?, ?> dictionary){
		return getSum(dictionary.values());
	}
	
	
	/**
	 * 
	 * @param enumeration
	 * @param op case 0 : add case 1: minus
	 * @return
	 */
	public static Dictionary<String, Integer> addDictionary(Enumeration<Dictionary<String, Integer>> enumeration,int op){
		Dictionary<String, Integer> firDictionary = (Dictionary<String, Integer>) enumeration
				.nextElement();
		Dictionary<String, Integer> sumDictionary = new Hashtable<String, Integer>();
		((Hashtable<String, Integer>)sumDictionary).putAll((Map<? extends String, ? extends Integer>) firDictionary);
		while (enumeration.hasMoreElements()) {
			Dictionary<String, Integer> dictionary = (Dictionary<String, Integer>) enumeration
					.nextElement();
			sumDictionary = Tool.addDictionary(firDictionary, dictionary,op);
		}
		return sumDictionary;
	}

	
	/**
	 * <k1,v1> <k2,v2> .... <kn,vn>  ---> < \sum{k_{i}} , \sum{v_{i}} >
	 * @param dictionary
	 * @param op  case 0: add , case 1: minus
	 * @return
	 */
	public static Hashtable<String, Integer> addDictionary(Dictionary<String, Integer> A,Dictionary<String, Integer> B, int op){
		Hashtable<String, Integer> sumDictionary = new Hashtable<String, Integer>();
		sumDictionary.putAll((Map<? extends String, ? extends Integer>) A);
		for (Enumeration<String> enumeration = A.keys();enumeration.hasMoreElements();){
			String keyString = enumeration.nextElement();
			int v1= A.get(keyString);
			int v2 = 0;
			if(B.get(keyString)!=null){
			 v2=  B.get(keyString);
			}
			if(op==0){
			sumDictionary.put(keyString, v1+v2);
			}else{
				sumDictionary.put(keyString, v1-v2);
			}
		}
		return sumDictionary;
	}
	
	
	
	/**
	 * Task: 根据正则表达式分割字符串为数组
	 * @param str
	 * @param regex
	 * @param count
	 * @return
	 * @author Administrator
	 */
	public static String[] split(String str,String regex ,int count){
		Pattern p = Pattern.compile(regex);
		return p.split(str,count);
	}
	
	
	/**
	 *  判断字符串是否是数字的函数
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (!notNull(str)) {
			return false;
		}
		Pattern pattern1 = Pattern.compile("^[+-]\\d+$");
		Pattern pattern2 = Pattern.compile("^[+-]\\d+\\.\\d+$");
		Matcher isNum1 = pattern1.matcher(str);
		Matcher isNum2 = pattern2.matcher(str);
		if (!isNum1.matches() && !isNum2.matches()) {
			return false;
		}
		return true;
	}
	
//	/**
//	 * 判断单个字符串是否为数字
//	 * @param str
//	 * @return
//	 */
//	private boolean isNum(String str){
//		boolean isNum = false;
//		str = str.toLowerCase().replaceAll("[+-]", "");
//		int charSize = str.length()-str.replaceAll("[a-zA-Z]", "").length();
//		int numSize= str.length() - str.replaceAll("[0-9]", "").length();
//		int specialSize = str.replaceAll("[\\w ]", "").length();
//		System.out.println("Character count is:" + (str.length() - str.replaceAll("[a-zA-Z]", "").length()));
//		System.out.println("Number count is:" + (str.length() - str.replaceAll("[0-9]", "").length()));	
//		System.out.println("Special count is :" + str.replaceAll("[\\w ]", "").length());
//		if(charSize==0&&specialSize==0&&numSize==str.length()){
//			isNum =true;
//		}
//		return isNum;
//	}
	
	/**
	 * 格式化字符串，去掉字符串中的某些字符
	 * eg  formatStr(line, " +");
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static String formatStr(String str, String pattern) {  
		Pattern p = Pattern.compile(pattern);   
		return p.matcher(str).replaceAll("");  
		}
	
	/**
	 * 判断某个字符串为正数，负数，整数，小数
	 * 分别为0 ，1,2,3,4
	 * @param 
	 * @param
	 * @return
	 * @author Administrator
	 */
	public static boolean checkNumeric(String numericStr, int flags) {
		 boolean checkResult = false;   
		 numericStr = formatStr(numericStr, "[`~!@#$%^&*()=|{}':;',\\s\\[\\]<>/?~]");  
		 Map<Integer, String> regMap = new HashMap<Integer,String>();  
		 regMap.put(0, "^(([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$");  
		 regMap.put(1, "^-(([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$");  
		 regMap.put(2,  "^(-?)(([1-9]+[0-9]*)|(0))$");  
		 regMap.put(3,  "^(-?)(([1-9]+[0-9]*[.][0-9]+)|([0][.][0-9]+[1-9]*))$");   
		 Map<Integer, String> nameMap = new HashMap<Integer,String>();   
		 nameMap.put(0, "Positive");   
		 nameMap.put(1, "Negative");   
		 nameMap.put(2, "Integer");   
		 nameMap.put(3, "Decimal");  
		 for(int availableFlag:regMap.keySet()) {  
			 if((flags & availableFlag)!=0) {   
				 checkResult = Pattern.compile(regMap.get(availableFlag)).matcher(numericStr).find();   
				System.out.println( numericStr+" is"+(checkResult?" ":" not ")+nameMap.get(availableFlag));  
				 if(!checkResult) { 
					 break;   
					 }   
				 }  
			 }
		 return checkResult;
	}
	
	
	public static boolean find(String str,String regex){
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(str);
		boolean b = matcher.find();
		log.debug("\""+str+"\" 匹配正则表达式 \"" +regex +"\" ? " +b);
		return b;
	}
	

	 /**
	   * Converts carriage returns and new lines in a string into \r and \n.
	   * Backquotes the following characters: ` " \ \t and %
	   * 
	   * @param string the string
	   * @return the converted string
	   * @see #unbackQuoteChars(String)
	   */
	  public static/* @pure@ */String backQuoteChars(String string) {

	    int index;
	    StringBuffer newStringBuffer;

	    // replace each of the following characters with the backquoted version
	    char charsFind[] = { '\\', '\'', '\t', '\n', '\r', '"', '%', '\u001E' };
	    String charsReplace[] = { "\\\\", "\\'", "\\t", "\\n", "\\r", "\\\"",
	        "\\%", "\\u001E" };
	    for (int i = 0; i < charsFind.length; i++) {
	      if (string.indexOf(charsFind[i]) != -1) {
	        newStringBuffer = new StringBuffer();
	        while ((index = string.indexOf(charsFind[i])) != -1) {
	          if (index > 0) {
	            newStringBuffer.append(string.substring(0, index));
	          }
	          newStringBuffer.append(charsReplace[i]);
	          if ((index + 1) < string.length()) {
	            string = string.substring(index + 1);
	          } else {
	            string = "";
	          }
	        }
	        newStringBuffer.append(string);
	        string = newStringBuffer.toString();
	      }
	    }

	    return string;
	  }

	
	
	public static void main(String[] args) {
//		Tool.find("abcdef", "^ab");
//		
//		String attributeNameLineString = "	No:Sunny			Hot			High			Strong	";
//		String[] attributeNames = attributeNameLineString.split("\\s");
//		String s1= attributeNameLineString.trim().replaceAll("\\s", " ");
//		s1= s1.replaceAll(" +", " ");
////		String s2= attributeNameLineString.replaceAll(" {2,}", " ");
//		String[] attributeNames1 =s1 .split("\\s");
//		System.out.println();
//		Dictionary<String, Integer> sumDictionary = new Hashtable<String, Integer>();
//		sumDictionary.put("yes",3);
//		sumDictionary.put("no", 2);
//		System.out.println(sumDictionary);
//		Dictionary<String, Integer> sum1Dictionary = new Hashtable<String, Integer>();
//		sum1Dictionary.put("no", 4);
//		sum1Dictionary.put("yes",5);
//		System.out.println(sum1Dictionary);
//		Dictionary<String, Integer> cDictionary= Tool.addDictionary(sum1Dictionary, sumDictionary,1);
//		
////		((Hashtable<String, Integer>)sum1Dictionary).( (Hashtable<String, Integer>)sumDictionary);
//		System.out.println(cDictionary);
		Collection<Integer> integers = new ArrayList<Integer>();
		for(int i=0;i<10;i++)
		integers.add(i);
		System.out.println(getSum(integers));
		
		Collection<String> strings = new ArrayList<String>();
		for(int i=0;i<10;i++)
		strings.add(i+"0");
	System.out.println(getSum(strings));
		Collection<Double> doubles = new ArrayList<Double>();
		for(double i=0;i<1;i=i+0.1){
		doubles.add(i);
		}
		System.out.println(getSum(doubles));
	}
	
	
	
	
}

