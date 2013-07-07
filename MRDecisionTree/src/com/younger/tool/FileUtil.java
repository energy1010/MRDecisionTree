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

package com.younger.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 */

public class FileUtil {

	
	private static final Logger log = LoggerFactory
			.getLogger(FileUtil.class);
	
	public static void output(Map map) {
		if (map != null) {
			Object key = null;
			Object value = null;
			Iterator iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				key = iterator.next();
				value = map.get(key);
				System.out.println("key: " + key + " ; value= " + value);
			}
		}
	}
	
	public static int putLongInByteArray(long l, byte[] array, int offset) {
		int i, shift;
		for (i = 0, shift = 56; i < 8; i++, shift -= 8) {
			array[offset] = (byte) (0xFF & (l >> shift));
			offset++;
		}
		return offset;
	}

	public static int putIntInByteArray(int value, byte[] array, int offset) {
		for (int i = 0; i < 4; i++) {
			int off = (3 - i) * 8;
			array[offset] = (byte) ((value >>> off) & 0xFF);
			offset++;
		}
		return offset;
	}
	
	public static int putBooleanInByteArray(boolean value, byte[] array, int offset) {
		array[offset] = (byte) (value ? 1 : 0);
		return offset + 1;
	}

	public static int putShortInByteArray(short value, byte[] array, int offset) {
		array[offset] = (byte) ((value >>> 8) & 0xFF);
		array[offset + 1] = (byte) (value & 0xFF);
		return offset + 2;
	}

	public static int putFloatInByteArray(float f, byte[] array, int offset) {
		int i = Float.floatToIntBits(f);
		return putIntInByteArray(i, array, offset);
	}

	public static int putDoubleInByteArray(double d, byte[] array, int offset) {
		long l = Double.doubleToRawLongBits(d);
		return putLongInByteArray(l, array, offset);
	}

	public static int putIntArrayInByteArray(int[] src, byte[] dest, int offset) {
		offset = FileUtil.putIntInByteArray(src.length, dest, offset);

		for (int i = 0; i < src.length; i++) {
			offset = FileUtil.putIntInByteArray(src[i], dest, offset);
		}
		return offset;
	}

	public static int putDoubleArrayInByteArray(double[] src, byte[] dest,
			int offset) {
		offset = FileUtil.putIntInByteArray(src.length, dest, offset);

		for (int i = 0; i < src.length; i++) {
			offset = FileUtil.putDoubleInByteArray(src[i], dest, offset);
		}
		return offset;
	}

	public static int putStringInByteArray(String src, byte[] dest, int offset) {
		if (src == null) {
			offset = FileUtil.putShortInByteArray((short) 0, dest, offset);
		} else {
			char[] chars = src.toCharArray();
			offset = FileUtil.putShortInByteArray((short) chars.length, dest,
					offset);

			for (int i = 0; i < chars.length; i++) {
				dest[offset] = (byte) chars[i];
				offset++;
			}
		}
		return offset;
	}

	public static byte[] toByteArray(int value) {
		byte[] array = new byte[4];
		putIntInByteArray(value, array, 0);
		return array;
	}

	public static long toLong(byte[] bytearray, int offset) {
		long result = 0;
		for (int i = offset; i < offset + 8 /* Bytes in long */; i++) {
			result = (result << 8 /* Bits in byte */)
					| (0xff & (byte) (bytearray[i] & 0xff));
		}
		return result;
	}

	public static int toInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	public static short toShort(byte[] b, int offset) {
		return (short) (((b[offset] & 0x000000FF) << 8) + ((b[offset + 1] & 0x000000FF)));
	}
	
	public static boolean toBoolean(byte[] data, int offset) {
		return (data[offset] == 1);
	}

	public static float toFloat(byte[] data, int offset) {
		int i = toInt(data, offset);
		return Float.intBitsToFloat(i);
	}

	public static double toDouble(byte[] data, int offset) {
		long l = toLong(data, offset);
		return Double.longBitsToDouble(l);
	}

	public static int[] toIntArray(byte[] data, int offset) {
		int length = toInt(data, offset);
		offset += 4;
		int[] arr = new int[length];
		for (int i = 0; i < length; i++) {
			arr[i] = toInt(data, offset);
			offset += 4;
		}
		return arr;
	}

	public static String toString(byte[] data, int offset) {
		int length = toShort(data, offset);
		offset += 2;
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = (char) data[offset];
			offset++;
		}
		return new String(chars);
	}
	
	public static String getFileNameWithoutExtension(String name) {
		int idx = name.lastIndexOf('.');
		if (idx < 0) {
			return name;
		}
		return name.substring(0, idx);
	}
	
	public static List<Integer> loadIntegersFromFile(String filename) throws Exception {
		List<Integer> result = new ArrayList<Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		String line;
		while ((line = reader.readLine()) != null) {
			result.add(Integer.parseInt(line.trim()));
		}
		return result;
	}
	
	  /**
	   * Rounds a double and converts it into String.
	   * 
	   * @param value the double value
	   * @param afterDecimalPoint the (maximum) number of digits permitted after the
	   *          decimal point
	   * @return the double as a formatted string
	   */
	  public static String doubleToString(double value,
	      int afterDecimalPoint) {

	    StringBuffer stringBuffer;
	    double temp;
	    int dotPosition;
	    long precisionValue;

	    temp = value * Math.pow(10.0, afterDecimalPoint);
	    if (Math.abs(temp) < Long.MAX_VALUE) {
	      precisionValue = (temp > 0) ? (long) (temp + 0.5) : -(long) (Math
	          .abs(temp) + 0.5);
	      if (precisionValue == 0) {
	        stringBuffer = new StringBuffer(String.valueOf(0));
	      } else {
	        stringBuffer = new StringBuffer(String.valueOf(precisionValue));
	      }
	      if (afterDecimalPoint == 0) {
	        return stringBuffer.toString();
	      }
	      dotPosition = stringBuffer.length() - afterDecimalPoint;
	      while (((precisionValue < 0) && (dotPosition < 1)) || (dotPosition < 0)) {
	        if (precisionValue < 0) {
	          stringBuffer.insert(1, '0');
	        } else {
	          stringBuffer.insert(0, '0');
	        }
	        dotPosition++;
	      }
	      stringBuffer.insert(dotPosition, '.');
	      if ((precisionValue < 0) && (stringBuffer.charAt(1) == '.')) {
	        stringBuffer.insert(1, '0');
	      } else if (stringBuffer.charAt(0) == '.') {
	        stringBuffer.insert(0, '0');
	      }
	      int currentPos = stringBuffer.length() - 1;
	      while ((currentPos > dotPosition)
	          && (stringBuffer.charAt(currentPos) == '0')) {
	        stringBuffer.setCharAt(currentPos--, ' ');
	      }
	      if (stringBuffer.charAt(currentPos) == '.') {
	        stringBuffer.setCharAt(currentPos, ' ');
	      }

	      return stringBuffer.toString().trim();
	    }
	    return new String("" + value);
	  }

	  
	  /**
	   * Returns the correlation coefficient of two double vectors.
	   * 
	   * @param y1 double vector 1
	   * @param y2 double vector 2
	   * @param n the length of two double vectors
	   * @return the correlation coefficient
	   */
	  public static final double correlation(double y1[], double y2[], int n) {

	    int i;
	    double av1 = 0.0, av2 = 0.0, y11 = 0.0, y22 = 0.0, y12 = 0.0, c;

	    if (n <= 1) {
	      return 1.0;
	    }
	    for (i = 0; i < n; i++) {
	      av1 += y1[i];
	      av2 += y2[i];
	    }
	    av1 /= n;
	    av2 /= n;
	    for (i = 0; i < n; i++) {
	      y11 += (y1[i] - av1) * (y1[i] - av1);
	      y22 += (y2[i] - av2) * (y2[i] - av2);
	      y12 += (y1[i] - av1) * (y2[i] - av2);
	    }
	    if (y11 * y22 == 0.0) {
	      c = 1.0;
	    } else {
	      c = y12 / Math.sqrt(Math.abs(y11 * y22));
	    }

	    return c;
	  }

	  /**
	   * Removes all occurrences of a string from another string.
	   * 
	   * @param inString the string to remove substrings from.
	   * @param substring the substring to remove.
	   * @return the input string with occurrences of substring removed.
	   */
	  public static String removeSubstring(String inString, String substring) {

	    StringBuffer result = new StringBuffer();
	    int oldLoc = 0, loc = 0;
	    while ((loc = inString.indexOf(substring, oldLoc)) != -1) {
	      result.append(inString.substring(oldLoc, loc));
	      oldLoc = loc + substring.length();
	    }
	    result.append(inString.substring(oldLoc));
	    return result.toString();
	  }
	  /**
	   * Replaces with a new string, all occurrences of a string from another
	   * string.
	   * 
	   * @param inString the string to replace substrings in.
	   * @param subString the substring to replace.
	   * @param replaceString the replacement substring
	   * @return the input string with occurrences of substring replaced.
	   */
	  public static String replaceSubstring(String inString, String subString,
	      String replaceString) {

	    StringBuffer result = new StringBuffer();
	    int oldLoc = 0, loc = 0;
	    while ((loc = inString.indexOf(subString, oldLoc)) != -1) {
	      result.append(inString.substring(oldLoc, loc));
	      result.append(replaceString);
	      oldLoc = loc + subString.length();
	    }
	    result.append(inString.substring(oldLoc));
	    return result.toString();
	  }

	  /**
	   * Pads a string to a specified length, inserting spaces on the left as
	   * required. If the string is too long, characters are removed (from the
	   * right).
	   * 
	   * @param inString the input string
	   * @param length the desired length of the output string
	   * @return the output string
	   */
	  public static String padLeft(String inString, int length) {

	    return fixStringLength(inString, length, false);
	  }

	  /**
	   * Pads a string to a specified length, inserting spaces on the right as
	   * required. If the string is too long, characters are removed (from the
	   * right).
	   * 
	   * @param inString the input string
	   * @param length the desired length of the output string
	   * @return the output string
	   */
	  public static String padRight(String inString, int length) {

	    return fixStringLength(inString, length, true);
	  }
	  
	  /**
	   * Pads a string to a specified length, inserting spaces as required. If the
	   * string is too long, characters are removed (from the right).
	   * 
	   * @param inString the input string
	   * @param length the desired length of the output string
	   * @param right true if inserted spaces should be added to the right
	   * @return the output string
	   */
	  public static String fixStringLength(String inString, int length,
	      boolean right) {

	    if (inString.length() < length) {
	      while (inString.length() < length) {
	        inString = (right ? inString.concat(" ") : " ".concat(inString));
	      }
	    } else if (inString.length() > length) {
	      inString = inString.substring(0, length);
	    }
	    return inString;
	  }
	  
	  
	  /**
	   * Normalizes the doubles in the array by their sum.
	   * 
	   * @param doubles the array of double
	   * @exception IllegalArgumentException if sum is Zero or NaN
	   */
	  public static void normalize(double[] doubles) {

	    double sum = 0;
	    for (int i = 0; i < doubles.length; i++) {
	      sum += doubles[i];
	    }
	    normalize(doubles, sum);
	  }

	  /**
	   * Normalizes the doubles in the array using the given value.
	   * 
	   * @param doubles the array of double
	   * @param sum the value by which the doubles are to be normalized
	   * @exception IllegalArgumentException if sum is zero or NaN
	   */
	  public static void normalize(double[] doubles, double sum) {

	    if (Double.isNaN(sum)) {
	      throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
	    }
	    if (sum == 0) {
	      // Maybe this should just be a return.
	      throw new IllegalArgumentException("Can't normalize array. Sum is zero.");
	    }
	    for (int i = 0; i < doubles.length; i++) {
	      doubles[i] /= sum;
	    }
	  }
	  
	  /**
	   * Converts an array containing the natural logarithms of probabilities stored
	   * in a vector back into probabilities. The probabilities are assumed to sum
	   * to one.
	   * 
	   * @param a an array holding the natural logarithms of the probabilities
	   * @return the converted array
	   */
	  public static double[] logs2probs(double[] a) {
	    double max = a[a.length];
	    double sum = 0.0;
	    double[] result = new double[a.length];
	    for (int i = 0; i < a.length; i++) {
	      result[i] = Math.exp(a[i] - max);
	      sum += result[i];
	    }

	    normalize(result, sum);

	    return result;
	  }

	  

	  /**
	   * Computes the variance for an array of doubles.
	   * 
	   * @param vector the array
	   * @return the variance
	   */
	  public static/* @pure@ */double variance(double[] vector) {

	    double sum = 0, sumSquared = 0;

	    if (vector.length <= 1) {
	      return 0;
	    }
	    for (int i = 0; i < vector.length; i++) {
	      sum += vector[i];
	      sumSquared += (vector[i] * vector[i]);
	    }
	    double result = (sumSquared - (sum * sum / vector.length))
	        / (vector.length - 1);

	    // We don't like negative variance
	    if (result < 0) {
	      return 0;
	    } else {
	      return result;
	    }
	  }

	  /**
	   * Returns the logarithm of a for base 2.
	   * 
	   * @param a a double
	   * @return the logarithm for base 2
	   */
	  public static/* @pure@ */double log2(double a) {

	    return Math.log(a) / Math.log(2);
	  }

	  
	  /**
	   * Returns c*log2(c) for a given integer value c.
	   * 
	   * @param c an integer value
	   * @return c*log2(c) (but is careful to return 0 if c is 0)
	   */
	  public static/* @pure@ */double xlogx(int c) {

	    if (c == 0) {
	      return 0.0;
	    }
	    return c *log2(c);
	  }
	  
	  /**
	   * Initial index, filled with values from 0 to size - 1.
	   */
	  public static int[] initialIndex(int size) {

	    int[] index = new int[size];
	    for (int i = 0; i < size; i++) {
	      index[i] = i;
	    }
	    return index;
	  }
	  
	  /**
	   * get the filepath for the file</br>
	   * if not found ,it will find in the project folder 
	   * @param filePath
	   * @return
	   */
	  public static String getFilePath(String filePath){
		try {
				String fileName=FileUtil.getFileNameFromFilePath(filePath);
				log.debug("try to find "+fileName+"  in the project..............");
				String basePath=new File("").getAbsoluteFile().getAbsolutePath();
			List<File> fileList=(List<File>) FileFinder.findFiles(basePath, fileName, 0);
				if(fileList.size()<=0){
					log.error("Cannot retrieve filePath using : " + fileName);
				 throw new IllegalArgumentException("Cannot retrieve filePath using : " + fileName);
				}else{
					log.debug("find "+fileList.size()+fileName);
					for (File f: fileList) {
						log.debug(f.toString());
					}
					filePath =fileList.get(0).getAbsolutePath();
					}
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
				assert(!filePath.isEmpty());
				return filePath;
	  }
	  
	  public static InputStream getFileInputStream(String filePath){
			try {
					String fileName=FileUtil.getFileNameFromFilePath(filePath);
					log.debug("try to find "+fileName+"  in the project..............");
					String basePath=new File("").getAbsoluteFile().getAbsolutePath();
				List<File> fileList=(List<File>) FileFinder.findFiles(basePath, fileName, 0);
					if(fileList.size()<=0){
					 throw new IllegalArgumentException("Cannot retrieve filePath using : " + fileName);
					}else{
						log.debug("find "+fileList.size()+fileName);
						for (File f: fileList) {
							log.debug(f.toString());
						}
						filePath =fileList.get(0).getAbsolutePath();
						}
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
					assert(!filePath.isEmpty());
					InputStream fileInputStream = null;
					try {
						fileInputStream = new FileInputStream(filePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return fileInputStream;
		  }
	  
	  public static Scanner getScanner(String fileName){
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(fileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return scanner;
	  }
	  
	  /**
	   * Converts a File's absolute path to a path relative to the user (ie start)
	   * directory. Includes an additional workaround for Cygwin, which doesn't like
	   * upper case drive letters.
	   * 
	   * @param absolute the File to convert to relative path
	   * @return a File with a path that is relative to the user's directory
	   * @exception Exception if the path cannot be constructed
	   */
	  public static File convertToRelativePath(File absolute) throws Exception {
	    File result;
	    String fileStr;

	    result = null;

	    // if we're running windows, it could be Cygwin
	    if (File.separator.equals("\\")) {
	      // Cygwin doesn't like upper case drives -> try lower case drive
	      try {
	        fileStr = absolute.getPath();
	        fileStr = fileStr.substring(0, 1).toLowerCase() + fileStr.substring(1);
	        result = createRelativePath(new File(fileStr));
	      } catch (Exception e) {
	        // no luck with Cygwin workaround, convert it like it is
	        result = createRelativePath(absolute);
	      }
	    } else {
	      result = createRelativePath(absolute);
	    }

	    return result;
	  }
	  
	  /**
	   * Converts a File's absolute path to a path relative to the user (ie start)
	   * directory.
	   * 
	   * @param absolute the File to convert to relative path
	   * @return a File with a path that is relative to the user's directory
	   * @exception Exception if the path cannot be constructed
	   */
	  public static File createRelativePath(File absolute) throws Exception {
	    File userDir = new File(System.getProperty("user.dir"));
	    String userPath = userDir.getAbsolutePath() + File.separator;
	    String targetPath = (new File(absolute.getParent())).getPath()
	        + File.separator;
	    String fileName = absolute.getName();
	    StringBuffer relativePath = new StringBuffer();
	    // relativePath.append("."+File.separator);
	    // System.err.println("User dir "+userPath);
	    // System.err.println("Target path "+targetPath);

	    // file is in user dir (or subdir)
	    int subdir = targetPath.indexOf(userPath);
	    if (subdir == 0) {
	      if (userPath.length() == targetPath.length()) {
	        relativePath.append(fileName);
	      } else {
	        int ll = userPath.length();
	        relativePath.append(targetPath.substring(ll));
	        relativePath.append(fileName);
	      }
	    } else {
	      int sepCount = 0;
	      String temp = new String(userPath);
	      while (temp.indexOf(File.separator) != -1) {
	        int ind = temp.indexOf(File.separator);
	        sepCount++;
	        temp = temp.substring(ind + 1, temp.length());
	      }

	      String targetTemp = new String(targetPath);
	      String userTemp = new String(userPath);
	      int tcount = 0;
	      while (targetTemp.indexOf(File.separator) != -1) {
	        int ind = targetTemp.indexOf(File.separator);
	        int ind2 = userTemp.indexOf(File.separator);
	        String tpart = targetTemp.substring(0, ind + 1);
	        String upart = userTemp.substring(0, ind2 + 1);
	        if (tpart.compareTo(upart) != 0) {
	          if (tcount == 0) {
	            tcount = -1;
	          }
	          break;
	        }
	        tcount++;
	        targetTemp = targetTemp.substring(ind + 1, targetTemp.length());
	        userTemp = userTemp.substring(ind2 + 1, userTemp.length());
	      }
	      if (tcount == -1) {
	        // then target file is probably on another drive (under windows)
	        throw new Exception("Can't construct a path to file relative to user "
	            + "dir.");
	      }
	      if (targetTemp.indexOf(File.separator) == -1) {
	        targetTemp = "";
	      }
	      for (int i = 0; i < sepCount - tcount; i++) {
	        relativePath.append(".." + File.separator);
	      }
	      relativePath.append(targetTemp + fileName);
	    }
	    // System.err.println("new path : "+relativePath.toString());
	    return new File(relativePath.toString());
	  }
	  
	  	/**
	  	 * get the filename from filepath
	  	 * eg D:\\R4.2chm --> R4.2chm
	  	 * @param filepath
	  	 * @return
	  	 */
	  	public static String getFileNameFromFilePath(String filepath){
	  		boolean c=filepath.lastIndexOf("/")==-1?false:true;
	  		if(c){
	  		filepath=filepath.substring(filepath.lastIndexOf("/")+1);
	  		}else{
	  		filepath =  filepath.substring(filepath.lastIndexOf(File.separator)+1);
	  		}
	  		return filepath;
	  	}
	  
}
