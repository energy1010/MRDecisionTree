package com.younger.tool.hadoop;

import java.io.InputStream;
import java.net.URL;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从hdfs读取文件
 * @author apple
 *
 */
public class URLCat {
   
	
	static{
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
	}
	
	/*
	 * 测试驱动
	 */
	public static void main(String[] args) {

		InputStream in = null;
	   try{
		   in = new URL(args[0]).openStream();
		   IOUtils.copyBytes(in, System.out, 4096,false);
		   
	   }catch (Exception e) {
		System.out.println(e.getMessage());
	}finally{
		   IOUtils.closeStream(in);
	   }
	}
	
	
}
