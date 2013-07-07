package com.younger.data;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.df.data.Dataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.core.hadoop.AbstractMapReduceJob;
import com.younger.tool.hadoop.HdfsFileUtil;



public class DataLoader extends AbstractMapReduceJob{

	private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public  Dataset generateDataset(String descriptor, FileSystem fs, Path dataFilePath) throws IOException{
		   Path[] files=HdfsFileUtil.listAllFile(fs, dataFilePath.getParent() );
		    int id = 0;
		    for(Path path: files){
		    FSDataInputStream input = fs.open(path);
//		    HdfsFileUtil.catFileToSystemOut(fs, path);
		    Scanner scanner = new Scanner(input);
		    // used to convert CATEGORICAL attribute to Integer
//		    List<String>[] values = new List[attrs.length];
//		    int id = 0;
		    int lineNo = 0;
		    while (scanner.hasNextLine()) {
		      String line = scanner.nextLine();
		      if (line.isEmpty()) {
		        continue;
		      }
		      log.debug(String.format("read line %s :%s",lineNo,line));
		      lineNo++;
		      if(line.trim().startsWith("@")){
		    	  continue;
		      }
		      if (parseString(id, attrs, values, line) != null) {
		        id++;
		      }
		    }
		    scanner.close();
		    }
		return null;
	}
	
	public static class  readMapper extends Mapper<Object, Text, Text, IntWritable>{
		
	}
	
	public static class readReducer extends Reducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT>{
		
	}
	
	
//	public static DataSet loadDataSet(){
//		
//	}
//	
//	 public static DataSet loadDataSet(String filePath) {
//		    List<Data> instances = new ArrayList<Data>();
//		    DataConverter converter = new DataConverter(dataset);
//		    for (String line : data) {
//		      if (line.isEmpty()) {
//		        log.warn("{}: empty string", instances.size());
//		        continue;
//		      }
//		      
//		      Instance instance = converter.convert(instances.size(), line);
//		      if (instance == null) {
//		        // missing values found
//		        log.warn("{}: missing values", instances.size());
//		        continue;
//		      }
//		      
//		      instances.add(instance);
//		    }
//		    
//		    return new Data(dataset, instances);
//		  }
	
//	 public static DataSet loadDataSet(Dataset dataset, String[] data) {
//		    List<Data> instances = new ArrayList<Data>();
//		    DataConverter converter = new DataConverter(dataset);
//		    for (String line : data) {
//		      if (line.isEmpty()) {
//		        log.warn("{}: empty string", instances.size());
//		        continue;
//		      }
//		      
//		      Instance instance = converter.convert(instances.size(), line);
//		      if (instance == null) {
//		        // missing values found
//		        log.warn("{}: missing values", instances.size());
//		        continue;
//		      }
//		      
//		      instances.add(instance);
//		    }
//		    
//		    return new Data(dataset, instances);
//		  }
	
//	private static DataSet generateDataset(String descriptor, String dataPath)
//			 {
////		Path path = new Path(dataPath);
////		return DataLoader.generateDataset(descriptor, path);
//		return null;
//	}
//	
//	
//	 /**
//	   * Converts a comma-separated String to a Vector.
//	   * 
//	   * @param id
//	   *          unique id for the current instance
//	   * @param attrs
//	   *          attributes description
//	   * @param values
//	   *          used to convert CATEGORICAL attribute values to Integer
//	   * @return null if there are missing values '?'
//	   */
//	  private static Data parseString(int id, AttrType[] attrs, List<String>[] values, String string) {
////	    StringTokenizer tokenizer = new StringTokenizer(string, ", ");
////	    Preconditions.checkArgument(tokenizer.countTokens() == attrs.length, "Wrong number of attributes in the string tokenizer.countTokens: "+tokenizer.countTokens() +"while attrs length :"+attrs.length);
////
////	    // extract tokens and check is there is any missing value
////	    String[] tokens = new String[attrs.length];
////	    for (int attr = 0; attr < attrs.length; attr++) {
////	      String token = tokenizer.nextToken();
////	      
////	      if (attrs[attr].isIgnored()) {
////	        continue;
////	      }
////	      
////	      if ("?".equals(token)) {
////	        return null; // missing value
////	      }
////	      
////	      tokens[attr] = token;
////	    }
////	    
////	    int nbattrs = Dataset.countAttributes(attrs);
////	    
////	    DenseVector vector = new DenseVector(nbattrs);
////	    
////	    int aId = 0;
////	    int label = -1;
////	    for (int attr = 0; attr < attrs.length; attr++) {
////	      if (attrs[attr].isIgnored()) {
////	        continue;
////	      }
////	      
////	      String token = tokens[attr];
////	      
////	      if (attrs[attr].isNumerical()) {
////	        vector.set(aId++, Double.parseDouble(token));
////	      } else { // CATEGORICAL or LABEL
////	        // update values
////	        if (values[attr] == null) {
////	          values[attr] = new ArrayList<String>();
////	        }
////	        if (!values[attr].contains(token)) {
////	          values[attr].add(token);
////	        }
////	        
////	        if (attrs[attr].isCategorical()) {
////	          vector.set(aId++, values[attr].indexOf(token));
////	        } else { // LABEL
////	          label = values[attr].indexOf(token);
////	        }
////	      }
////	    }
////	    
////	    if (label == -1) {
////	      throw new IllegalStateException("Label not found!");
////	    }
////	    
////	    return new Instance(id, vector, label);
//		  return new Data();
//	  }
//	

}


