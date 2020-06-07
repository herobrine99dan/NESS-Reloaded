package com.github.ness.check.machinelearning.test;

import java.util.List;

public class Utility {
	
	/**
	 * Euclidean distance algorithm
	 * d(i,j)=sqrt((x1-x2)^2+(y1-y2)^2)
	 * @param the first vector
	 * @param the second vector
	 * @return distance
	 */
	public static float euclidean(float[] d1, float[] d2) {
        if(d1.length != d2.length) {
        	throw new RuntimeException("The length of characters are not identical!");
        }
        
        double sum = 0;
        for(int i = 0; i < d1.length; i++) {
        	sum += Math.pow(d1[i] - d2[i], 2);
        }
        
        return (float) Math.sqrt(sum);
    }
	
	public static float getSmallest(float[] a, int total){  
		float temp;  
		for (int i = 0; i < total; i++)   
		        {  
		            for (int j = i + 1; j < total; j++)   
		            {  
		                if (a[i] > a[j])   
		                {  
		                    temp = a[i];  
		                    a[i] = a[j];  
		                    a[j] = temp;  
		                }  
		            }  
		        }  
		       return a[0];  
		}  

	public static float[] convertListToArray(List<Float> arrList) {
	      float[] arr = new float[arrList.size()];
	      int index = 0;
	      for (final Float value: arrList) {
	         arr[index++] = value;
	      }
	      return arr;
	}
	
}
