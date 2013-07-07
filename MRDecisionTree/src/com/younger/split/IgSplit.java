package com.younger.split;

import com.younger.data.DataSet;



/**
 * Computes the best split using the Information Gain measure
 */
public abstract class IgSplit {
  
  protected static final double LOG2 = Math.log(2.0);
  
  /**
   * Computes the best split for the given attribute
   */
  public abstract Split computeSplit(DataSet dataSet, int attr);
  
}