package com.younger.conf;

import java.util.regex.Pattern;



public interface Parameter {
	
	
	public static final int MaxTreeHeight =10;
	
	
	  /** Minimum number of instances */
	public static final int MinDataSizeInLeafNode=2;
	
	public static final int len=-1;
	
	public static final boolean deleteMissingValue = false;
	
	public static final boolean pruneTree=false;
	
	public static final String filePath ="file/samples.txt";
	
	/**
	 *   Binary splits on nominal attributes?
	 * split attribute only two children, default it's false
	 */
	public static final boolean binarySplit = false;
			//"file/weather.arff";
			//"file/samples.txt";

	public static final String ARFFfilePath = "file/weather.arff";
	// "samples.txt";
	// "file/file.txt";
	/**for read attribute in arff file */
	public static final String attributePatternString = "@attribute(.*)[{](.*?)[}](.*)";
	/**
	 * 
	 */
	public static final Pattern SPACE_PATTERN =Pattern.compile("[ ]+");
	
	/** */
	public static final String dataPatternString="(.*)[:](.*)";
	
	  /** Minimum number of instances */
//    public static final int m_minNumObj = 2;
    
    
    /** Determines whether probabilities are smoothed using
    Laplace correction when predictions are generated */
	public  static boolean m_useLaplace = false;
	
	/** Use reduced error pruning? */
	public boolean m_reducedErrorPruning = false;
	
	/** Number of folds for reduced error pruning. */
	public int m_numFolds = 3;
	/**
	 * the attribute  selection cretia
	 * @author Administrator
	 *
	 */
	public static enum AttributeVal {InformationGain,GainRatio,GiniGain};
	
	public static  final AttributeVal attributeVal = AttributeVal.GainRatio;
	
	  /** Subtree raising to be performed? */
   public boolean m_subtreeRaising = true;
}
