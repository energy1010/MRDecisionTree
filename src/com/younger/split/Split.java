package com.younger.split;

import java.util.Locale;


/**
 * split 
 * @author apple
 *
 */
public final  class Split {
	/**  attribute to split for */
	  private final int attr;
	  /**Information Gain of the split */
	  private final double ig;
	  /** split value for NUMERICAL attributes */
	  private final double split;
	  
	  public Split(int attr, double ig, double split) {
	    this.attr = attr;
	    this.ig = ig;
	    this.split = split;
	  }
	  
	  public Split(int attr, double ig) {
	    this(attr, ig, Double.NaN);
	  }

	  /** attribute to split for */
	  public int getAttr() {
	    return attr;
	  }

	  /** Information Gain of the split */
	  public double getIg() {
	    return ig;
	  }

	  /** split value for NUMERICAL attributes */
	  public double getSplit() {
	    return split;
	  }

	  @Override
	  public String toString() {
	    return String.format(Locale.ENGLISH, "attr: %d, ig: %f, split: %f", attr, ig, split);
	  }

}
