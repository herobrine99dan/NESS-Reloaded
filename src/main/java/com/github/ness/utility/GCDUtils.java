package com.github.ness.utility;

import java.util.List;

public class GCDUtils {
	
	/**
	 * @author Islandscout
	 * Methods from https://github.com/HawkAnticheat/Hawk/blob/master/src/me/islandscout/hawk/util/MathPlus.java
	 */

    public static double gcdRational(double a, double b) {
        if(a == 0) {
            return b;
        }
        int quotient = getIntQuotient(b, a);
        double remainder = ((b / a) - quotient) * a;
        if(Math.abs(remainder) < Math.max(a, b) * 1E-3F)
            remainder = 0;
        return gcdRational(remainder, a);
    }

    public static double gcdRational(List<Double> numbers) {
        double result = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            result = gcdRational(numbers.get(i), result);
        }
        return result;
    }

    public static int getIntQuotient(double dividend, double divisor) {
        double ans = dividend / divisor;
        double error = Math.max(dividend, divisor) * 1E-3F;
        return (int)(ans + error);
    }


}
