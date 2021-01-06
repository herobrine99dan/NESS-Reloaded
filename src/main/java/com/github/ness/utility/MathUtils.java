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

	public static boolean isExponentiallySmall(double d) {
		return d < .0001 && d > 0;
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
	 * GCD Utils from Hawk Anticheat
	 * (https://github.com/HawkAnticheat/Hawk/blob/master/src/me/islandscout/hawk/util/MathPlus.java)
	 * 
	 * @author Islandscout
	 */
	public static double gcdRational(double a, double b) {
		if (Math.abs(a) < 0.001) {
			return b;
		} else if (Math.abs(b) < 0.001) {
			return a;
		}
		int quotient = getIntQuotient(b, a);
		double remainder = ((b / a) - quotient) * a;
		if (Math.abs(remainder) < Math.max(a, b) * 1E-3F)
			remainder = 0;
		return gcdRational(remainder, a);
	}

	public static int getIntQuotient(double dividend, double divisor) {
		double ans = dividend / divisor;
		double error = Math.max(dividend, divisor) * 1E-3F;
		return (int) (ans + error);
	}

	/**
	 * From
	 * https://www.spigotmc.org/threads/determining-a-players-sensitivity.468373/
	 * 
	 * @param double gcd
	 * @return the sensitivity
	 */
	public static double getSensitivity(double gcd) {
		return (1.655 * Math.cbrt(0.8333 * gcd)) - 0.3333;
	}

	public static double average(List<Float> angles) {
		double sum = 0;
		for (float f : angles) {
			sum += f;
		}
		return sum / angles.size();
	}

	public static long averageLong(List<Long> angles) {
		long sum = 0;
		for (long f : angles) {
			sum += f;
		}
		return sum / angles.size();
	}

	public static double gcdRational(List<Double> numbers) {
		double result = numbers.get(0);
		for (int i = 1; i < numbers.size(); i++) {
			result = gcdRational(numbers.get(i), result);
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

	public static float pitchTo100F(float flub) {
		if ((flub %= 360.0f) >= 180.0f) {
			flub -= 360.0f;
		}
		if (flub < -180.0f) {
			flub += 360.0f;
		}
		return flub;
	}
}
