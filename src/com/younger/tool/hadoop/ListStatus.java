package com.younger.tool.hadoop;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListStatus {
	
	private static final Logger log = LoggerFactory.getLogger(ListStatus.class);

	private static String[] argStrings;
	

	public static String[] getArgStrings() {
		return argStrings;
	}

	public ListStatus(){
		
	}

	public ListStatus(String[] as){
		argStrings = as;
	}
	public void setArgStrings(String[] argStrings) {
		this.argStrings = argStrings;
	}


	/**
	 * 传入命令行参数，显示文件夹内容
	 * @param args
	 */
	public synchronized static void  ListStatus(){
		String[] args=argStrings;
		String uri = args[0];
		Configuration configuration = new Configuration();
		FileSystem fsFileSystem ;
		try {
		fsFileSystem =FileSystem.get(URI.create(uri),configuration);
		Path[] paths = new Path[args.length];
		for(int i =0 ; i<paths.length;i++){
			//paths[i]= new Path(args[i]);
			paths[i] = new Path(args[i]);
		}
		FileStatus[] fileStatus = fsFileSystem.listStatus(paths);
		Path[] listedpathsPaths =FileUtil.stat2Paths(fileStatus);
		for(Path p:listedpathsPaths)
			log.info(p.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());		}
	}
	
	
	public synchronized static void createFile(String uri) {
		//String uri = "hdfs://localhost:9000/user/hadoop/hdfs/apple.txt";
		Configuration conf = new Configuration();
		FileSystem fs = null;
		try {
			Path path = new Path(uri);
			fs = FileSystem.get(URI.create(uri), conf);
			fs.mkdirs(path);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(fs);
		}
	}

	public synchronized static void list(String uri) {
		//String uri = "hdfs://localhost:9000/user/hadoop/hdfs/";
		Configuration conf = new Configuration();
		FileSystem fs = null;
		try {
			 fs = FileSystem.get(URI.create(uri), conf);
		} catch (Exception e) {
			// TODO: handle exception
		}
	
		Path[] paths = new Path[1];
		paths[0] = new Path(uri);
		//FileStatus[] status = fs.listStatus(paths);
		    //Path pathPattern, PathFilter filte
		FileStatus[] status = null;
		try {
//			 status = fs.globStatus(new Path("hdfs://localhost:9000/user/hadoop/*/*"),
//					new RegexExcludePathFilter("hdfs://localhost:9000/user/hadoop/hdfs/t.*t.txt"));
		} catch (Exception e) {
			// TODO: handle exception
		}
//		FileStatus[] status = fs.globStatus(new Path("hdfs://localhost:9000/user/hadoop/*/*"),
//				new RegexExcludePathFilter("hdfs://localhost:9000/user/hadoop/hdfs/t.*t.txt"));
		Path[] listedPaths = FileUtil.stat2Paths(status);
		for (Path p : listedPaths) {
			log.info(p.toString());
		}
	}



	public synchronized static FileSystem getFileSystem(String ip , int port ){
   
		FileSystem fSystem = null;
		String urlString = "hdfs://" + ip + ":" + String.valueOf(port);
		Configuration configuration = new Configuration();
		configuration.set("fs.default.name",urlString);
		try {
			fSystem = FileSystem.get(configuration);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return fSystem;
		
	}
	
	public static void main(String[] args) {
		ListStatus listStatus = new ListStatus();
		String[] s= new String[1];//
		s[0] = new String("/Users/apple/Desktop/1/");
		listStatus.setArgStrings(s);
		listStatus.ListStatus();
		
	}
	
}
