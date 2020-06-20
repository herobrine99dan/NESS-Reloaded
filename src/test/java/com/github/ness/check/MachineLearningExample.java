package com.github.ness.check;

import java.util.ArrayList;
import java.util.List;

import com.github.ness.knn.DataSet;
import com.github.ness.knn.Knn;

public class MachineLearningExample {

	public static void main(String[] args) {
		List<DataSet> sets = new ArrayList<DataSet>();
		sets.add(new DataSet("1algo", new double[] { 1, 4, 3, 5, 2 }));
		sets.add(new DataSet("2algo", new double[] { 6, 8, 7, 10, 9 }));
		sets.add(new DataSet("3algo", new double[] { 0.1, 0.5, 0.3, 0.4, 0.2 }));
		sets.add(new DataSet("4algo", new double[] { 0.6, 0.9, 0.7, 0.8, 1 }));
		sets.add(new DataSet("5algo", new double[] { 1.1, 1.3, 1.2, 1.5, 1.4 }));
		Knn alghorithm = new Knn(sets, 1, true);
		System.out.println("Prediction: " + alghorithm.predict(new double[] { 1.134, 1.9, 1.5, 1.03, 1.056 }));
	}

}
