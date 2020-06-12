package com.github.ness.check.killaura.machinelearning;

import java.util.List;

public class Utility {

	static double calculateDistance(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			System.err.println(
					"There was An Error! The input aren't the same lenght!" + array1.length + " " + array2.length);
			return 0;
		}
		double Sum = 0.0;
		for (int i = 0; i < array1.length; i++) {
			Sum = Sum + Math.pow((array1[i] - array2[i]), 2.0);
		}
		return Math.sqrt(Sum);
	}

	static void printValues(List<Double> distance) {
		for (double d : distance) {
			System.out.println("Dist: " + d);
		}
	}

}
