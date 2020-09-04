package com.github.ness.utility;

public class MathUtils {
	
	private static double[] table = new double[65536];
	
	static {
		for (int i = 0; i < table.length; ++i) {
			table[i] = Math.sin((double) i * Math.PI * 2.0D / table.length);
		}
	}

	public static final double sin(double f) {
		return table[(int) (f * 10430.378F) & '\uffff'];
	}

	public static final double cos(double f) {
		return table[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
	}

}
