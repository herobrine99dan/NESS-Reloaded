package com.github.ness.utility;

public class MathUtils {

    private static final double[] table = new double[65536];

    static {
        for (int i = 0; i < table.length; ++i) {
            table[i] = Math.sin((double) i * Math.PI * 2.0D / table.length);
        }
    }

    /**
     * Get the sine of a double value from the table
     * @param double f
     * @return the sine of that double value
     */
    public static final double sin(double f) {
        return table[(int) (f * 10430.378F) & '\uffff'];
    }

    /**
     * Get the cosine of a double value from the table
     * @param double f
     * @return the cosine of that double value
     */
    public static final double cos(double f) {
        return table[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    /**
     * Calculate Percentage
     * @param obtained Value
     * @param total Values
     * @return the Percentage
     */
    public static double calculatePercentage(double obtained, double total) {
        return obtained * 100.0D / total;
    }

    /**
     * Calculate the Greatest Common Divisor from two double values
     * @param double a
     * @param double b
     * @return the GCD
     */
    public static double gcd(double a, double b) {
        return b <= 16384L ? a : MathUtils.gcd(b, a % b);
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
