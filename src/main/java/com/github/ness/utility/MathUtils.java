package com.github.ness.utility;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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

	public static double calculatePercentage(double obtained, double total) {
		return obtained * 100.0D / total;
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

	public static double sigmoid(double x) {
		return 1.0D / (1.0D + Math.exp(-x));
	}

}
