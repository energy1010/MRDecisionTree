//
//package com.younger.eval;
//
//import com.younger.sample.TrainDatas;
//
///**
// * @author Yasser Ganjisaffar <ganjisaffar at gmail dot com>
// */
//
//public class BalancedYoundenIndex extends EvaluationMetric {
//	
//	private static final double THRESHOLD = 0.5;
//	
//	public BalancedYoundenIndex() {
//		super(true);
//	}
//	
//	@Override
//	public double measure(double[] predictions, TrainDatas sample) {
//		int tp = 0;
//		int tn = 0;
//		int fn = 0;
//		int fp = 0;
//		for (int i = 0; i < sample.size; i++) {
//			double target = sample.targets[i];
//			double pred = predictions[i];
//			if (target > THRESHOLD) {
//				if (pred > THRESHOLD) {
//					tp++;
//				} else {
//					fn++;
//				}
//			} else {
//				if (pred > THRESHOLD) {
//					fp++;
//				} else {
//					tn++;
//				}
//			}
//		}
//		
//		double sensitivity = (double) tp / (tp + fn);
//		double specificity = (double) tn / (tn + fp);
//		
//		return Math.min(sensitivity, specificity);
//	}
//
//	@Override
//	public double measure(Integer[] predictions, TrainDatas sample) throws Exception {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//}
