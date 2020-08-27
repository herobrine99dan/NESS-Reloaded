package com.github.ness.utility;

import java.util.List;

public class GCDUtils {

	/** @author Islandscout 
	 *  Methods from https://github.com/HawkAnticheat/Hawk/blob/master/src/me/islandscout/hawk/util/MathPlus.java
	 *  The Sensitivity Method come directly from Islandscout brain
	 */

	public static float gcdRational(float a, float b) {
		if (a == 0) {
			return b;
		}
		int quotient = getIntQuotient(b, a);
		float remainder = ((b / a) - quotient) * a;
		if (Math.abs(remainder) < Math.max(a, b) * 1E-3F)
			remainder = 0;
		return gcdRational(remainder, a);
	}

	public static float gcdRational(List<Float> numbers) {
		float result = numbers.get(0);
		for (int i = 1; i < numbers.size(); i++) {
			result = gcdRational(numbers.get(i), result);
		}
		return result;
	}

	public static int getIntQuotient(float dividend, float divisor) {
		float ans = dividend / divisor;
		float error = Math.max(dividend, divisor) * 1E-3F;
		return (int) (ans + error);
	}

	public static double getSensitivity(double gcd) {
		return (Math.cbrt(gcd / 8 / 0.15) - 0.2) / 0.6;
	}

}
