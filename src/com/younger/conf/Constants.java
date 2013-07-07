
package com.younger.conf;

/**
 * @author apple
 */

public class Constants {
	
	
	
	public enum AttributeType {CONTIOUS,NUMERIC};
	public final static double EPSILON = 1.4e-45;
	public final static double MIN_EXP_POWER = -50;
	public final static double LN2 = Math.log(2);

	public static int[] ONE_TWO_THREE_ETC;
	public static double[] DOUBLE_ONE_ONE_ONE_ETC;
	
	public static synchronized void init(int maxSize) {
		if (ONE_TWO_THREE_ETC == null || ONE_TWO_THREE_ETC.length < maxSize) {
			ONE_TWO_THREE_ETC = new int[maxSize];
			DOUBLE_ONE_ONE_ONE_ETC = new double[maxSize];
			for (int i = 0; i < maxSize; i++) {
				ONE_TWO_THREE_ETC[i] = i;
				DOUBLE_ONE_ONE_ONE_ETC[i] = 1.0;
			}
		}
	}
	
	public static final String TreeXmlPath = "file/tree.xml";
}
