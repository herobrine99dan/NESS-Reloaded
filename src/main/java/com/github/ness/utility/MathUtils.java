package com.github.ness.utility;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class MathUtils {

	private static final float[] SIN_TABLE = new float[65536];
	static {
		for (int i = 0; i < 65536; i++)
			SIN_TABLE[i] = (float) Math.sin(i * Math.PI * 2.0D / 65536.0D);
	}

	public static float sin(float p_76126_0_) {
		return SIN_TABLE[(int) (p_76126_0_ * 10430.378F) & 0xFFFF];
	}

	public static float cos(float value) {
		return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 0xFFFF];
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
		if (a < 0.001) {
			return b;
		} else if (b < 0.001) {
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

	public static double getSensitivity(double gcd) {
		return (Math.cbrt(gcd / 8 / 1 / 0.15) - 0.2) / 0.6;
	}

	public static double otherGetSensitivity(double gcd) {
		return (1.655 * Math.cbrt(0.8333 * gcd)) - 0.3333;
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
}
