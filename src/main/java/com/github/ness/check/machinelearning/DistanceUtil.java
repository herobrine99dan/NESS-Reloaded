package com.github.ness.check.machinelearning;

public class DistanceUtil {
	/**
	 * @author mybreeze77
	 * from https://github.com/mybreeze77/Simple-KNN-with-Java8
	 */
	
	/**
	 * Euclidean distance algorithm
	 * d(i,j)=sqrt((x1-x2)^2+(y1-y2)^2)
	 * @param the first vector
	 * @param the second vector
	 * @return distance
	 */
	public static double euclidean(double[] d1, double[] d2) {
        if(d1.length != d2.length) {
        	throw new RuntimeException("The length of characters are not identical!");
        }
        
        double sum = 0;
        for(int i = 0; i < d1.length; i++) {
        	sum += Math.pow(d1[i] - d2[i], 2);
        }
        
        return Math.sqrt(sum);
    }
	
	/**
	 * Manhattan distance algorithm
	 * d(i,j)=|X1-X2|+|Y1-Y2|
	 * @param the first vector
	 * @param the second vector
	 * @return distance
	 */
	public static double manhattan(double[] d1, double[] d2) {
        if(d1.length != d2.length) {
        	throw new RuntimeException("The length of characters are not identical!");
        }
        
        double sum = 0;
        for(int i = 0; i < d1.length; i++) {
        	sum += Math.abs(d1[i] - d2[i]);
        }
        
        return sum;
    }
	
}
