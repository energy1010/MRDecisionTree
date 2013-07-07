package com.younger.tool.hadoop;

import java.io.InputStream;
import java.net.URL;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ��hdfs��ȡ�ļ�
 * @author apple
 *
 */
public class URLCat {
   
	
	static{
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
	}
	
	/*
	 * ��������
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
