package com.younger.data;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.tool.Tool;

/**
 * To process line to Data instance
 * @author apple
 *
 */
public class DataConverter {

	
	 
	  private static final Logger log = LoggerFactory.getLogger(DataConverter.class);
	  
	  /**
	   * convert a line to data
	   * @param id
	   * @param line
	   * @return
	   */
	  public static Data convert(int id, String line) {
		  if(line==null) return null;
		  if(line.contains("?")) return null;
			line = Tool.removeReduntSpace(line).toLowerCase();
			String[] row = line.split(","); // include the class attribute
			if(Arrays.asList(row).contains("?")){
				// missing value 
				return null;
			}
//			log.debug("id " +id +" : "+ Arrays.toString(row));
			String dataClassValue = row[row.length - 1].toLowerCase(); // get the target calss value
			// get the targetAttributeClassDistribution
			Data data = new Data(id);
			data.setClassValue(dataClassValue);
//			data.setClassValueIndex(getClassValueIndex(dataClassValue)); the classValue haven't been inited yet
			data.setAttributevaluesList(row, 0, row.length - 1);
     		  return data;
	  }
	
	  /**
	   * if true then skip this line 
	   * else process this line
	   * @param line
	   * @return
	   */
		public  static boolean skipLine(String line){
			if(line==null) return true;
			line = Tool.removeReduntSpace(line);
			if ((line ==null)||line.trim().equalsIgnoreCase("") || line.trim().startsWith("%")||line.startsWith("#")||line.isEmpty())
				return true;
			return false;
		}

}
