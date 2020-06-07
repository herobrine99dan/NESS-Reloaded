package com.github.ness.check.killaura.heuristics;

import java.util.ArrayList;
import java.util.List;

public class AngleHeuristics {

	/**
	 * @author herobrine99dan
	 */
	private List<Float> floatpatterns;

	/**
	 * 
	 * @param An ArrayList<Float> Object
	 * 
	 */
	public AngleHeuristics(List<Float> list) {
		floatpatterns = list;
	}
	
	/**
	 * Get all the patterns float value
	 * 
	 * @return an ArrayList<Float> Object
	 */
	public List<Float> getPatternsFloat() {
		return floatpatterns;
	}

	public float getMineResult(float[] values) {
		float firstdist = 0;
		float seconddist = 0;
		for (int i = 0; i < floatpatterns.size(); i++) {
				firstdist += floatpatterns.get(i);
		}
		for (int i = 0; i < values.length; i++) {
			seconddist += values[i];
		}
		System.out.println("First Dist: " + firstdist + " Second Dist: " + seconddist);
		// System.out.println("Euclidean: " +
		// Utility.euclidean(Utility.convertListToArray(floatpatterns), values));
		// System.out.println("Low Distance: " + Utility.getSmallest(values,
		// values.length));
		return seconddist;
	}

	
}
