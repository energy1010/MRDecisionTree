package com.younger.bayes;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.common.IntPairWritable;

import com.younger.core.hadoop.AbstractMapReduceJob;

public class readArffJob extends AbstractMapReduceJob{
	
	public static class readArffMapper extends Mapper<Object, Text, IntPairWritable, IntWritable>{
		
	}

	
	public static class readArffReducer extends Reducer<IntPairWritable, IntWritable, IntPairWritable, IntWritable>{
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public int run(String[] args) throws Exception {
		return 0;
	}


}
