package com.github.ness.check;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.ness.knn.DataSet;
import com.github.ness.knn.Knn;

public class MachineLearningExample {

	@Test
	public void executeTest() {
		MachineLearningExample.main(null);
	}

	public static void main(String[] args) {
		List<DataSet> sets = new ArrayList<DataSet>();
		sets.add(new DataSet("Legit", new double[] { 1, 2, 3, 4, 5 }));
		sets.add(new DataSet("Legit", new double[] { 6, 7, 8, 9, 0 }));
		sets.add(new DataSet("Killaura", new double[] { 10, 11, 12, 13, 14 }));
		sets.add(new DataSet("Killaura", new double[] { 15, 16, 17, 18, 0 }));
		Knn alghorithm = new Knn(sets, 1, true);
		System.out.println("Prediction: " + alghorithm.predict(new double[] { 1 , 20, 1, 1, 20 }));
	}

}
