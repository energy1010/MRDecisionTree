package com.younger.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import org.apache.mahout.df.data.Instance;


/**
 * Writes to a destination in arff text format. <p/>
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -i &lt;the input file&gt;
 * The input file</pre>
 *
 * <pre> -o &lt;the output file&gt;
 * The output file</pre>
 *
 <!-- options-end -->
 *
 * @see Saver
 */
public class ArffSaver {

  /** for serialization */
  static final long serialVersionUID = 2223634248900042228L;
  
  /** whether to compress the output */
  protected boolean m_CompressOutput = false;

  /** Constructor */
  public ArffSaver(){

  }

  /**
   * Gets whether the output data is compressed.
   *
   * @return            true if the output data is compressed
   */
  public boolean getCompressOutput() {
    return m_CompressOutput;
  }

  /**
   * Sets whether to compress the output.
   *
   * @param value       if truee the output will be compressed
   */
  public void setCompressOutput(boolean value) {
    m_CompressOutput = value;
  }


  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  public String[] getFileExtensions() {
	  /** the file extension */
//	  public static String FILE_EXTENSION = Instances.FILE_EXTENSION;
//	  public static String FILE_EXTENSION_COMPRESSED = FILE_EXTENSION + ".gz";
    return new String[]{".arff", ".arff.gz"};
  }
  
  /** 
   * Sets the destination file.
   * 
   * @param outputFile the destination file.
   * @throws IOException throws an IOException if file cannot be set
   */
  public void setFile(File outputFile) throws IOException  {
    if (outputFile.getAbsolutePath().endsWith(".arff.gz"))
      setCompressOutput(true);
  }
  
  /** 
   * Sets the destination output stream.
   * BufferedWriter m_writer;
   * @param output              the output stream.
   * @throws IOException        throws an IOException if destination cannot be set
   */
  public BufferedWriter setDestination(OutputStream output) throws IOException {
	  BufferedWriter writer=null;
    if (getCompressOutput()){
    	 output = new GZIPOutputStream(output);
    	writer = new BufferedWriter(new OutputStreamWriter(output));
    }
    else{
     writer =  setDestination(output);
    }
    return writer;
  }


  /** Saves an instances incrementally. Structure has to be set by using the
   * setStructure() method or setInstances() method.
   * @param inst the instance to save
   * @throws IOException throws IOEXception if an instance cannot be saved incrementally.
   */
  public void writeIncremental(Instance inst) throws IOException{

    
  }

  /** Writes a Batch of instances
   * @throws IOException throws IOException if saving in batch mode is not possible
   */
  public void writeBatch() throws IOException {

   
  }


  /**
   * Main method.
   *
   * @param args should contain the options of a Saver.
   */
  public static void main(String[] args) {
  }
}
