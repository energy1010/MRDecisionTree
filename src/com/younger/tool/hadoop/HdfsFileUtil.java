package com.younger.tool.hadoop;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.Progressable;
import org.apache.mahout.df.node.Node;
import org.apache.mahout.ga.watchmaker.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsFileUtil {

	private static Logger log = LoggerFactory.getLogger(HdfsFileUtil.class);

	public synchronized static FileSystem getFileSystem(String ip, int port) {
		FileSystem fs = null;
		String url = "hdfs://" + ip + ":" + String.valueOf(port);
		Configuration config = new Configuration();
		config.set("fs.default.name", url);
		try {
			fs = FileSystem.get(config);
		} catch (Exception e) {
			log.error("getFileSystem failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return fs;
	}

	public synchronized static void listNode(FileSystem fs) {
		DistributedFileSystem dfs = (DistributedFileSystem) fs;
		try {
			DatanodeInfo[] infos = dfs.getDataNodeStats();
			for (DatanodeInfo node : infos) {
				System.out.println("HostName: " + node.getHostName() + "/n"
						+ node.getDatanodeReport());
				System.out.println("--------------------------------");
			}
		} catch (Exception e) {
			log.error("list node list failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	public synchronized static void listConfig(FileSystem fs) {
		Iterator<Entry<String, String>> entrys = fs.getConf().iterator();
		while (entrys.hasNext()) {
			Entry<String, String> item = entrys.next();
			log.info(item.getKey() + ": " + item.getValue());
		}
	}

	public synchronized static void mkdirs(FileSystem fs, String dirName) {
		// Path home = fs.getHomeDirectory();
		Path workDir = fs.getWorkingDirectory();
		String dir = workDir + "/" + dirName;
		Path src = new Path(dir);
		// FsPermission p = FsPermission.getDefault();
		boolean succ;
		try {
			succ = fs.mkdirs(src);
			if (succ) {
				log.info("create directory " + dir + " successed. ");
			} else {
				log.info("create directory " + dir + " failed. ");
			}
		} catch (Exception e) {
			log.error("create directory " + dir + " failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	public synchronized static void rmdirs(FileSystem fs, String dirName) {
		// Path home = fs.getHomeDirectory();
		Path workDir = fs.getWorkingDirectory();
		String dir = workDir + "/" + dirName;
		Path src = new Path(dir);
		boolean succ;
		try {
			succ = fs.delete(src, true);
			if (succ) {
				log.info("remove directory " + dir + " successed. ");
			} else {
				log.info("remove directory " + dir + " failed. ");
			}
		} catch (Exception e) {
			log.error("remove directory " + dir + " failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	public synchronized static void upload(FileSystem fs, String local,
			String remote) {
		// Path home = fs.getHomeDirectory();
		Path workDir = fs.getWorkingDirectory();
		Path dst = new Path(workDir + "/" + remote);
		Path src = new Path(local);
		try {
			fs.copyFromLocalFile(false, true, src, dst);
			log.info("upload " + local + " to  " + remote + " successed. ");
		} catch (Exception e) {
			log.error("upload " + local + " to  " + remote + " failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	/**
	 * down load file from hdfs to local
	 * @param fs
	 * @param local
	 * @param remote
	 */
	public synchronized static void download(FileSystem fs, String local,
			String remote) {
		// Path home = fs.getHomeDirectory();
		Path workDir = fs.getWorkingDirectory();
		Path dst = new Path(workDir + "/" + remote);
		Path src = new Path(local);
		try {
			fs.copyToLocalFile(false, dst, src);
			log.info("download from " + remote + " to  " + local
					+ " successed. ");
		} catch (Exception e) {
			log.error("download from " + remote + " to  " + local + " failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	
	 public synchronized static boolean getFromHdfs(String src,String dst, Configuration conf) {
	    Path dstPath = new Path(src);
	    try {
	      FileSystem hdfs = dstPath.getFileSystem(conf);
	      hdfs.copyToLocalFile(false, new Path(src),new Path(dst));
	    } catch (IOException e) {
	      e.printStackTrace();
	      return false;
	    }
	    return true;
	  }
	
	 
	public synchronized static void listFile(FileSystem fs, String path) {
		Path workDir = fs.getWorkingDirectory();
		Path dst;
		if (null == path || "".equals(path)) {
			dst = new Path(workDir + "/" + path);
		} else {
			dst = new Path(path);
		}
		try {
			String relativePath = "";
			FileStatus[] fList = fs.listStatus(dst);
			for (FileStatus f : fList) {
				if (null != f) {
					relativePath = new StringBuffer()
							.append(f.getPath().getParent()).append("/")
							.append(f.getPath().getName()).toString();
					if (f.isDir()) {
						listFile(fs, relativePath);
					} else {
						System.out.println(convertSize(f.getLen()) + "/t/t"
								+ relativePath);
					}
				}
			}
		} catch (Exception e) {
			log.error("list files of " + path + " failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
		}
	}

	public synchronized static void write(FileSystem fs, String path,
			String data) {
		// Path home = fs.getHomeDirectory();
		Path workDir = fs.getWorkingDirectory();
		Path dst = new Path(workDir + "/" + path);
		try {
			FSDataOutputStream dos = fs.create(dst);
			dos.writeUTF(data);
			dos.close();
			log.info("write content to " + path + " successed. ");
		} catch (Exception e) {
			log.error("write content to " + path + " failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	public synchronized static void append(FileSystem fs, String path,
			String data) {
		// Path home = fs.getHomeDirectory();
		Path workDir = fs.getWorkingDirectory();
		Path dst = new Path(workDir + "/" + path);
		try {
			FSDataOutputStream dos = fs.append(dst);
			dos.writeUTF(data);
			dos.close();
			log.info("append content to " + path + " successed. ");
		} catch (Exception e) {
			log.error("append content to " + path + " failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	public synchronized static void copyFileWithProgress(FileSystem fs,String localSrc,String dest){
		InputStream inputStream;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(localSrc));
			OutputStream outputStream = fs.create(new Path(dest),new Progressable() {
				@Override
				public void progress() {
					System.out.println(".");
				}
			});
			IOUtils.copyBytes(inputStream, outputStream, 4096,true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param fs
	 * @param path
	 * @return
	 */
	public synchronized static String read(FileSystem fs, String path) {
		String content = null;
		// Path home = fs.getHomeDirectory();
		Path workDir = fs.getWorkingDirectory();
		Path dst = new Path(workDir + "/" + path);
		try {
			// reading
			FSDataInputStream dis = fs.open(dst);
			content = dis.readUTF();
			dis.close();
			log.info("read content from " + path + " successed. ");
		} catch (Exception e) {
			log.error("read content from " + path + " failed :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return content;
	}

	public synchronized static String convertSize(long size) {
		String result = String.valueOf(size);
		if (size < 1024 * 1024) {
			result = String.valueOf(size / 1024) + " KB";
		} else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
			result = String.valueOf(size / 1024 / 1024) + " MB";
		} else if (size >= 1024 * 1024 * 1024) {
			result = String.valueOf(size / 1024 / 1024 / 1024) + " GB";
		} else {
			result = result + " B";
		}
		return result;
	}
	
	/**
	 * list all files in the path 
	 * @param fs
	 * @param dirPath
	 * @return files path[]
	 */
	public synchronized static Path[]  listAllFile(FileSystem fs, Path dirPath) {
		//String uri = "hdfs://localhost:9000/user/hadoop/hdfs/";
//		Path path= new Path(uri);
		FileStatus[] status;
		try {
			status = fs.listStatus(dirPath);
			Path[] listedPaths = FileUtil.stat2Paths(status);
			for (Path p : listedPaths) {
				System.out.println(p);
			}
			return listedPaths;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
//		FileStatus[] status = fs.globStatus(new Path("hdfs://localhost:9000/user/hadoop/*/*"),
//				new RegexExcludePathFilter("hdfs://localhost:9000/user/hadoop/hdfs/t.*t.txt"));
	}
	
	/**
	 * cat all files in the dir Path
	 * @param fSystem
	 * @param dirPath
	 */
	public synchronized static void catFileListToSystemOut(FileSystem fSystem,Path dirPath) {
		Path[] paths= listAllFile(fSystem,dirPath);
		for(Path p: paths){
			catFileToSystemOut(fSystem, p);
		}
	}

	/**
	 * cat a file 
	 * @param fSystem
	 * @param filePath
	 */
	public synchronized static void catFileToSystemOut(FileSystem fSystem,Path filePath) {
	    InputStream in= null;
	    try{
	    	try {
//	    	return	FSDataOutputStream
				in= fSystem.open(filePath);
				IOUtils.copyBytes(in, System.out, 4096,false);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }finally{
	    	IOUtils.closeStream(in);
	    }
	   
	}

		  // upload local file to  hdfs , src file on local , dst path on  hdfs
	public	synchronized  static boolean putToHdfs(String src, String dst, Configuration conf) {
		    Path dstPath = new Path(dst);
		    try {
		      FileSystem hdfs = dstPath.getFileSystem(conf);
		      hdfs.copyFromLocalFile(false, new Path(src),new Path(dst));
		    } catch (IOException e) {
		      e.printStackTrace();
		      return false;
		    }
		    return true;
		  }	

	  /**
	   * Writes an Node[] into a DataOutput
	   */
	  public static void writeArray(DataOutput out, Node[] array) throws IOException {
	    out.writeInt(array.length);
	    for (Node w : array) {
	      w.write(out);
	    }
	  }
	  
	  /**
	   * Reads a Node[] from a DataInput
	   */
	  public static Node[] readNodeArray(DataInput in) throws IOException {
	    int length = in.readInt();
	    Node[] nodes = new Node[length];
	    for (int index = 0; index < length; index++) {
	      nodes[index] = Node.read(in);
	    }
	    
	    return nodes;
	  }
	  
	  /**
	   * Writes a double[] into a DataOutput
	   */
	  public static void writeArray(DataOutput out, double[] array) throws IOException {
	    out.writeInt(array.length);
	    for (double value : array) {
	      out.writeDouble(value);
	    }
	  }
	  
	  /**
	   * Reads a double[] from a DataInput
	   */
	  public static double[] readDoubleArray(DataInput in) throws IOException {
	    int length = in.readInt();
	    double[] array = new double[length];
	    for (int index = 0; index < length; index++) {
	      array[index] = in.readDouble();
	    }
	    
	    return array;
	  }
	  
	  /**
	   * Writes an int[] into a DataOutput
	   */
	  public static void writeArray(DataOutput out, int[] array) throws IOException {
	    out.writeInt(array.length);
	    for (int value : array) {
	      out.writeInt(value);
	    }
	  }
	  
	  /**
	   * Reads an int[] from a DataInput
	   */
	  public static int[] readIntArray(DataInput in) throws IOException {
	    int length = in.readInt();
	    int[] array = new int[length];
	    for (int index = 0; index < length; index++) {
	      array[index] = in.readInt();
	    }
	    
	    return array;
	  }
	  
	  /**
	   * Return a list of all files in the output directory
	   *
	   * @throws IOException if no file is found
	   */
	  public static Path[] listOutputFiles(FileSystem fs, Path outputPath) throws IOException {
	    Path[] outfiles = OutputUtils.listOutputFiles(fs, outputPath);
	    if (outfiles.length == 0) {
	      throw new IOException("No output found !");
	    }
	    
	    return outfiles;
	  }
	  
	  /**
	   * Formats a time interval in milliseconds to a String in the form "hours:minutes:seconds:millis"
	   */
	  public static String elapsedTime(long milli) {
	    long seconds = milli / 1000;
	    milli %= 1000;
	    
	    long minutes = seconds / 60;
	    seconds %= 60;
	    
	    long hours = minutes / 60;
	    minutes %= 60;
	    
	    return hours + "h " + minutes + "m " + seconds + "s " + milli;
	  }

	  public static void storeWritable(Configuration conf, Path path, Writable writable) throws IOException {
	    FileSystem fs = path.getFileSystem(conf);

	    FSDataOutputStream out = fs.create(path);
	    writable.write(out);
	    out.close();
	  }
	
	  public static String getProcessFileName(Context context){
//		  获取当前输入的文件名
		  FileSplit fileSplit = (FileSplit)context.getInputSplit();
		  String filename = fileSplit.getPath().getName();
		  System.out.println("File name "+filename);
		  System.out.println("Directory and File name"+fileSplit.getPath().toString());
		  return filename;
	  }
	  
	  /**
	   * merget local files  to hdfs path
	   * @param fs
	   * @param conf
	   * @param localSrcPath
	   * @param destPath
	   * @throws IOException
	   */
	  public static void putMerge(FileSystem fs,Configuration conf,String localSrcPath,String destPath) throws IOException{
		  FileSystem hdfs = FileSystem.get(conf);
		  FileSystem local = FileSystem.getLocal(conf);
		  Path inputDir = new Path(localSrcPath);
		  Path hdfsFile = new Path(destPath);
		  try {
				  FileStatus[] inputFiles = local.listStatus(inputDir);
				  FSDataOutputStream out = hdfs.create(hdfsFile); 
				  for (int i=0; i<inputFiles.length; i++) { 
					  System.out.println(inputFiles[i].getPath().getName()); 
					  FSDataInputStream in =local.open(inputFiles[i].getPath());
					  byte buffer[] = new byte[256];
					  int bytesRead = 0; 
					  while( (bytesRead = in.read(buffer)) > 0) {
					  out.write(buffer, 0, bytesRead); in.close();
					  }
					  in.close();
				  }
				  out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	  }
	  
	public static void main(String[] args) throws Exception {
		FileSystem fs = HdfsFileUtil.getFileSystem("192.168.1.101", 9000);
		// FsUtil.listConfig(fs);
		String dirName = "demo.txt";
		// FsUtil.mkdirs(fs, dirName);
		// FsUtil.rmdirs(fs, dirName);
		// FsUtil.upload(fs, "D:/help/js", dirName);
		// FsUtil.download(fs, "D:/help/js2", dirName);
		HdfsFileUtil.write(fs, dirName, "test-测试");
		HdfsFileUtil.append(fs, dirName, "/ntest-测试2");
		//
		// String content = FsUtil.read(fs, dirName);
		//
		// System.out.println(content);
		// FsUtil.listFile(fs, "");
		// FsUtil.listNode(fs);
		fs.close();
	}

}
