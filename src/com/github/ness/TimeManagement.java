package com.github.ness;

import org.bukkit.OfflinePlayer;
import java.util.concurrent.TimeUnit;

public class TimeManagement {
	public static String getTime(final Long seconds) {
		double res;
		final double time = res = seconds;
		double dec = 0.0;
		String type = "Seconds";
		if (res >= 60.0) {
			res = (double) TimeUnit.MINUTES.convert((long) time, TimeUnit.SECONDS);
			type = "Minutes";
			dec = time % 60.0 / 60.0;
			if (res >= 60.0) {
				res = (double) TimeUnit.HOURS.convert((long) time, TimeUnit.SECONDS);
				type = "Hours";
				dec = time % 60.0 / 60.0;
				if (res >= 24.0) {
					res = (double) TimeUnit.DAYS.convert((long) time, TimeUnit.SECONDS);
					type = "Days";
					dec = time % 1440.0 / 1440.0;
				}
			}
		}
		return String.valueOf(Math.round(res)) + new StringBuilder(String.valueOf(dec)).toString().substring(1, 3) + " "
				+ type;
	}

	public static String lastOn(final OfflinePlayer target) {
		return getTime((System.currentTimeMillis() - target.getLastPlayed()) / 1000L / 60L);
	}

	public static String getTime(final Integer minutes) {
		double res;
		final double time = res = minutes;
		double dec = 0.0;
		String type = "Minutes";
		if (res >= 60.0) {
			res = (double) TimeUnit.HOURS.convert((long) time, TimeUnit.MINUTES);
			type = "Hours";
			dec = time % 60.0 / 60.0;
			if (res >= 24.0) {
				res = (double) TimeUnit.DAYS.convert((long) time, TimeUnit.MINUTES);
				type = "Days";
				dec = time % 1440.0 / 1440.0;
			}
		}
		return String.valueOf(Math.round(res)) + new StringBuilder(String.valueOf(dec)).toString().substring(1, 3) + " "
				+ type;
	}

	public static String getTime(final Double mils) {
		final boolean isNegative = mils < 0.0;
		double mil = Math.abs(mils);
		final String[] names = { "milliseconds", "seconds", "minutes", "hours", "days", "weeks", "months", "years",
				"decades", "centuries" };
		final String[] sNames = { "millisecond", "second", "minute", "hour", "day", "week", "month", "year", "decade",
				"century" };
		final Double[] length = { 1.0, 1000.0, 60000.0, 3600000.0, 8.64E7, 6.048E8, 2.628E9, 3.154E10, 3.154E11,
				3.154E12 };
		String suff = "";
		int i = length.length - 1;
		while (i >= 0) {
			if (mil >= length[i]) {
				if (suff.equals("")) {
					suff = names[i];
				}
				mil /= length[i];
				if (mil == 1.0) {
					suff = sNames[i];
					break;
				}
				break;
			} else {
				--i;
			}
		}
		String name = new StringBuilder(String.valueOf(mil)).toString();
		if (Math.round(mil) == mil) {
			name = new StringBuilder(String.valueOf((int) Math.round(mil))).toString();
		}
		if (name.contains(".") && name.split("\\.")[1].length() > 2) {
			name = String.valueOf(name.split("\\.")[0]) + "."
					+ name.split("\\.")[1].substring(0, Math.min(name.split("\\.")[1].length(), 2));
		}
		if (isNegative) {
			name = "-" + name;
		}
		return String.valueOf(name) + " " + suff;
	}
}
