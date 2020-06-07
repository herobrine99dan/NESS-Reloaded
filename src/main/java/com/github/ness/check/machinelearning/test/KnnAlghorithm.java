package com.github.ness.check.machinelearning.test;

import java.util.ArrayList;

public class KnnAlghorithm {
	/**
	 * @author herobrine99dan
	 */
	private ArrayList<String> namepatterns;
	private ArrayList<Float> floatpatterns;
	private ArrayList<String> patterns;

	/**
	 * The Constructor!
	 * 
	 * @param An ArrayList<String> Object, you should do nameOfTheClass:floatValue
	 *           for every value
	 * 
	 */
	public KnnAlghorithm(ArrayList<String> list) {
		for (String s : list) {
			String name = s.substring(0, s.indexOf(":"));
			float value = Float.valueOf(s.substring(s.indexOf(":")));
			namepatterns.add(name);
			floatpatterns.add(value);
		}
		patterns = list;
	}

	/**
	 * Get all the raw patterns
	 * 
	 * @return an ArrayList<String> Object
	 */
	public ArrayList<String> getPatterns() {
		return patterns;
	}

	/**
	 * Get all the patterns name
	 * 
	 * @return an ArrayList<String> Object
	 */
	public ArrayList<String> getPatternsName() {
		return namepatterns;
	}

	/**
	 * Get all the patterns float value
	 * 
	 * @return an ArrayList<Float> Object
	 */
	public ArrayList<Float> getPatternsFloat() {
		return floatpatterns;
	}

	/**
	 * Calculate the the closest float value
	 * 
	 * @return a float value
	 */
	public String getResult(float[] values) {
		
		return "class"+"floatvalue";
	}
	
	

}
