package com.younger.estimators;

import com.younger.tool.Utils;


/** 
 * Simple probability estimator that places a single Poisson distribution
 * over the observed values.
 *
 */
public class PoissonEstimator{
  /** for serialization */
  private static final long serialVersionUID = 7669362595289236662L;
  
  /** The number of values seen */
  private double m_NumValues;
  
  /** The sum of the values seen */
  private double m_SumOfValues;
  
  /** 
   * The average number of times
   * an event occurs in an interval.
   */
  private double m_Lambda;
  
  
  /**
   * Calculates the log factorial of a number.
   *
   * @param x input number.
   * @return log factorial of x.
   */
  private double logFac(double x) {
    
    double result = 0;
    for (double i = 2; i <= x; i++) {
      result += Math.log(i);
    }
    return result;
  }
  
  /**
   * Returns value for Poisson distribution
   *
   * @param x the argument to the kernel function
   * @return the value for a Poisson kernel
   */
  private double Poisson(double x) {
    
    return Math.exp(-m_Lambda + (x * Math.log(m_Lambda)) - logFac(x));
  }
  
  /**
   * Add a new data value to the current estimator.
   *
   * @param data the new data value 
   * @param weight the weight assigned to the data value 
   */
  public void addValue(double data, double weight) {
    
    m_NumValues += weight;
    m_SumOfValues += data * weight;
    if (m_NumValues != 0) {
      m_Lambda = m_SumOfValues / m_NumValues;
    }
  }
  
  /**
   * Get a probability estimate for a value
   *
   * @param data the value to estimate the probability of
   * @return the estimated probability of the supplied value
   */
  public double getProbability(double data) {
    
    return Poisson(data);
  }
  
  /** Display a representation of this estimator */
  public String toString() {
    
    return "Poisson Lambda = " + Utils.doubleToString(m_Lambda, 4, 2) + "\n";
  }
  
  /**
   * Main method for testing this class.
   *
   * @param argv should contain a sequence of numeric values
   */
  public static void main(String [] argv) {
    
    try {
      if (argv.length == 0) {
        System.out.println("Please specify a set of instances.");
        return;
      }
      PoissonEstimator newEst = new PoissonEstimator();
      for(int i = 0; i < argv.length; i++) {
        double current = Double.valueOf(argv[i]).doubleValue();
        System.out.println(newEst);
        System.out.println("Prediction for " + current 
            + " = " + newEst.getProbability(current));
        newEst.addValue(current, 1);
      }
      
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
