package com.younger.split;



//
///**
// * Default, not optimized, implementation of IgSplit
// */
//public class DefaultIgSplit extends IgSplit {
//  
//  /** used by entropy() */
//  private int[] counts;
//  
////  @Override
//  public Split computeSplit(DataSet data, int attrIndex) {
//    if (data.getAttributeTypeTable().get(attrIndex).isCategorical()) {
//      double[] values = data.values(attrIndex);
//      double bestIg = -1;
//      double bestSplit = 0.0;
//      
//      for (double value : values) {
//        double ig = numericalIg(data, attrIndex, value);
//        if (ig > bestIg) {
//          bestIg = ig;
//          bestSplit = value;
//        }
//      }
//      
//      return new Split(attrIndex, bestIg, bestSplit);
//    } else {
//      double ig = categoricalIg(data, attrIndex);
//      
//      return new Split(attrIndex, ig);
//    }
//  }
//  
//  /**
//   * Computes the Information Gain for a CATEGORICAL attribute
//   */
//  protected double categoricalIg(Data data, int attr) {
//    double[] values = data.values(attr);
//    double hy = entropy(data); // H(Y)
//    double hyx = 0.0; // H(Y|X)
//    double invDataSize = 1.0 / data.size();
//    
//    for (double value : values) {
//      Data subset = data.subset(Condition.equals(attr, value));
//      hyx += subset.size() * invDataSize * entropy(subset);
//    }
//    
//    return hy - hyx;
//  }
  
//  /**
//   * Computes the Information Gain for a NUMERICAL attribute given a splitting value
//   */
//  protected double numericalIg(DataSet dataSet, int attr, double split) {
//    double hy = entropy(dataSet);
//    double invDataSize = 1.0 / dataSet.getDatasetSize();
//    
//    // LO subset
//    Data subset = data.subset(Condition.lesser(attr, split));
//    hy -= subset.size() * invDataSize * entropy(subset);
//    
//    // HI subset
//    subset = data.subset(Condition.greaterOrEquals(attr, split));
//    hy -= subset.size() * invDataSize * entropy(subset);
//    
//    return hy;
//  }
  
//  /**
//   * Computes the Entropy
//   */
//  protected double entropy(DataSet data) {
//    double invDataSize = 1.0 / data.size();
//    
//    if (counts == null) {
//      counts = new int[data.getDataset().nblabels()];
//    }
//    
//    Arrays.fill(counts, 0);
//    data.countLabels(counts);
//    
//    double entropy = 0.0;
//    for (int label = 0; label < data.getDataset().nblabels(); label++) {
//      int count = counts[label];
//      if (count == 0) {
//        continue; // otherwise we get a NaN
//      }
//      double p = count * invDataSize;
//      entropy += -p * Math.log(p) / LOG2;
//    }
//    
//    return entropy;
//  }
//  
//}