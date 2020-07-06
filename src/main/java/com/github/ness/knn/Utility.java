package com.github.ness.knn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utility {
	/**
	 * @author herobrine99dan
	 */

	/**
	 * Calculate the distance beetween the class data and the test data
	 * 
	 * @param double[] array1
	 * @param double[] array2
	 * @return
	 */
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

	/**
	 * Load the test file
	 * 
	 * @param File f object
	 * @return a double[] array
	 * @throws FileNotFoundException
	 */
	public static double[] getTestFile(File f) throws FileNotFoundException {
		Scanner fileReader = new Scanner(f);
		String[] args = fileReader.nextLine().split(";");
		double[] values = new double[args.length + 1];// Create an Array
		for (int i = 0; i < args.length; i++) {
			values[i] = Double.valueOf(args[i]);
		}
		values[args.length] = 0.0;
		fileReader.close();
		return values;
	}

	/**
	 * Create a DataSet List From CSV File
	 * 
	 * @param File f
	 * @return a List<DataSet> Object
	 * @throws IOException
	 */
	public static List<DataSet> createDataSetListByCSVFile(File f) throws IOException {
		List<String> lines = Files.readAllLines(f.toPath()); // Read the File
		List<DataSet> sets = new ArrayList<DataSet>();
		for (String s : lines) {// Parse the file using ";" char
			String[] args = s.split(";");
			double[] values = new double[args.length];// Create an Array
			for (int i = 1; i < args.length; i++) {
				values[i] = Double.valueOf(args[i]);
			}
			sets.add(new DataSet(args[0], values)); // Create A DataSet Object
		}
		return sets;
	}

}
