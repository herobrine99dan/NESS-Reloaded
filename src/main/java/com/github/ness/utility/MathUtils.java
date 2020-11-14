package com.github.ness.utility;

import java.util.List;

public class MathUtils {

	private static final double[] table = new double[65536];

	static {
		for (int i = 0; i < table.length; ++i) {
			table[i] = Math.sin((double) i * Math.PI * 2.0D / table.length);
		}
	}

	/**
	 * Get the sine of a double value from the table
	 * 
	 * @param double f
	 * @return the sine of that double value
	 */
	public static final double sin(double f) {
		return table[(int) (f * 10430.378F) & '\uffff'];
	}

	/**
	 * Get the cosine of a double value from the table
	 * 
	 * @param double f
	 * @return the cosine of that double value
	 */
	public static final double cos(double f) {
		return table[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
	}

	/**
	 * Calculate Percentage
	 * 
	 * @param obtained Value
	 * @param total    Values
	 * @return the Percentage
	 */
	public static double calculatePercentage(double obtained, double total) {
		return obtained * 100.0D / total;
	}

	/**
	 * Calculate the Greatest Common Divisor from two double values
	 * 
	 * @author Frap (or Frappay)
	 */
	public static double getGCD(double a, double b) {
		a = Math.abs(a);
		b = Math.abs(b);
		if (a < b) {
			return getGCD(b, a);
		}
		if (b < 0.001) {
			return a;
		} else {
			return getGCD(b, a - Math.floor(a / b) * b);
		}
	}

	/**
	 * From
	 * https://www.spigotmc.org/threads/determining-a-players-sensitivity.468373/
	 * 
	 * @param gcd
	 * @return
	 */
	public static double getSensitivity(double gcd) {
		return (1.655 * Math.cbrt(0.8333 * gcd)) - 0.3333;
	}

	public static double gcdRational(List<Double> numbers) {
		double result = numbers.get(0);
		for (int i = 1; i < numbers.size(); i++) {
			result = getGCD(numbers.get(i), result);
		}
		return result;
	}

	public static float yawTo180F(float flub) {
		if ((flub %= 360.0f) >= 180.0f) {
			flub -= 360.0f;
		}
		if (flub < -180.0f) {
			flub += 360.0f;
		}
		return flub;
	}
}
