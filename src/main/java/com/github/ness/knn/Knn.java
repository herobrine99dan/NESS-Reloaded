package com.github.ness.knn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Knn {
	/**
	 * @author herobrine99dan
	 */
	List<DataSet> classesvalues;
	int kvalue;
	boolean debug;

	public Knn(List<DataSet> values, int k, boolean debugging) {
		classesvalues = values;
		kvalue = k;
		debug = debugging;
	}

	/**
	 * Get the k Value
	 * 
	 * @return an int variable.
	 */
	public int getKValue() {
		return kvalue;
	}

	/**
	 * Get all the Classes in a list
	 * 
	 * @return a DataSet List Object
	 */
	public List<DataSet> getClassesValues() {
		return classesvalues;
	}

	// The entire training dataset is stored.
	// Step 1: Calculate Euclidean Distance.
	// Step 2: Get Nearest Neighbors.
	// Step 3: Make Predictions.
	/**
	 * Do a prediction
	 * 
	 * @param a double[] array
	 * @return a String Object witch contains the name of the class and the
	 *         distance. They are separated by a ":" char;
	 */
	public String predict(double[] values) {
		List<Double> distance = new ArrayList<Double>();
		// Step 1 We calculate the euclidean distance for every value
		for (DataSet d : classesvalues) {
			double dist = Utility.calculateDistance(d.classvalue, values);
			distance.add(dist);
			d.distance = dist;
		}
		// Step 2: We get all the Nearest Neighbors sorting the distance list
		distance.sort(new Comparator<Double>() { // We are sorting the distance from the lowest to the highest.
			@Override
			public int compare(Double o1, Double o2) {
				if (o1 == o2)
					return 0;
				else if (o1 > o2)
					return 1;
				else
					return -1;
			}
		});
		// Step 3 We Make Predictions. Actually this find the nearest neighbor,it can't
		// find two or more neighbors.
		if (kvalue == 1) {
			String name = "";
			double dist = distance.get(0);
			for (DataSet d : classesvalues) {
				if (debug) {
					System.out.println("Viewing: " + d.classname + ":" + d.distance);
				}
				if (d.distance == dist) {
					name = d.classname;
					break;
				}
			}
			return name + ":" + dist;
		} else {
			return "";
		}
	}

	public void destroy() {
		kvalue = 1;
		debug = false;
		classesvalues = null;
	}

}
