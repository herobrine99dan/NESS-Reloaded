package com.github.ness.check.killaura.machinelearning;

public class DataSet {
	/**
	 * @author herobrine99dan
	 */
	public double[] classvalue;
	public String classname;
	double distance;

	public DataSet(String name, double[] values) {
		classname = name;
		classvalue = values;
	}

}
