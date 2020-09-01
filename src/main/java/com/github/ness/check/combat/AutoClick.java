package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class AutoClick extends AbstractCheck<PlayerInteractEvent> {

	private final List<HardLimitEntry> hardLimits = new ArrayList<>();
	
	private final Set<DeviationEntry> deviationRequirements = new HashSet<>();
	
	private final Set<DeviationEntry> superDeviationRequirements = new HashSet<>();

	private final int totalRetentionSecs;
	
	private static final Logger logger = LogManager.getLogger(AutoClick.class);
	
	public AutoClick(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 2, TimeUnit.SECONDS));

		ConfigurationSection section = manager.getNess().getNessConfig().getCheck(AutoClick.class);

		totalRetentionSecs = section.getInt("total-retention-secs", 32);

		List<String> hardLimitList = section.getStringList("hard-limit.cps-and-required-span");
		if (hardLimitList == null) {
			makeDefaultHardLimits(hardLimits);
		} else {
			parseHardLimits(hardLimitList, hardLimits);
		}
		hardLimits.sort(null);
		
		List<String> deviationRequirementList = section.getStringList("constancy.deviation-and-sample");
		if (deviationRequirementList == null) {
			makeDefaultDeviationRequirements(deviationRequirements);
		} else {
			parseDeviationRequirements(deviationRequirementList, deviationRequirements);
		}
		
		List<String> superDeviationRequirementList = section.getStringList("constancy.superdeviation-and-supersample");
		if (superDeviationRequirementList == null) {
			makeDefaultSuperDeviationRequirements(superDeviationRequirements);
		} else {
			parseDeviationRequirements(superDeviationRequirementList, superDeviationRequirements);
		}
		
		logger.trace("Configuration: {}", this);
	}

	@Override
	public String toString() {
		return "AutoClick [hardLimits=" + hardLimits + ", deviationRequirements=" + deviationRequirements
				+ ", superDeviationRequirements=" + superDeviationRequirements + ", totalRetentionSecs="
				+ totalRetentionSecs + "]";
	}

	private void makeDefaultHardLimits(List<HardLimitEntry> listToMutate) {
		listToMutate.add(new HardLimitEntry(16, 3));
		listToMutate.add(new HardLimitEntry(20, 4));
	}
	
	private void parseIntegerPairStringList(List<String> configValues, IntBinaryOperator whenParsed) {
		for (String str : configValues) {
			String[] info = str.split(":");
			if (info.length == 2) {
				try {
					whenParsed.applyAsInt(Integer.parseInt(info[0]), Integer.parseInt(info[1]));
				} catch (NumberFormatException ignored) {
					logger.debug("Cannot format {}:{} to integers", info[0], info[1]);
				}
			} else {
				logger.debug("Cannot format illegal entry length of {}", (Object) info);
			}
		}
	}
	
	private void parseHardLimits(List<String> configValues, List<HardLimitEntry> listToMutate) {
		parseIntegerPairStringList(configValues, (i1, i2) -> {
			listToMutate.add(new HardLimitEntry(i1, i2));
			return 0;
		});
	}
	
	private void makeDefaultDeviationRequirements(Set<DeviationEntry> setToMutate) {
		setToMutate.add(new DeviationEntry(30, 8));
		setToMutate.add(new DeviationEntry(15, 16));
	}
	
	private void makeDefaultSuperDeviationRequirements(Set<DeviationEntry> setToMutate) {
		setToMutate.add(new DeviationEntry(10, 8));
	}
	
	private void parseDeviationRequirements(List<String> configValues, Set<DeviationEntry> setToMutate) {
		parseIntegerPairStringList(configValues, (i1, i2) -> {
			setToMutate.add(new DeviationEntry(i1, i2));
			return 0;
		});
	}
	
	@AllArgsConstructor
	private static class HardLimitEntry implements Comparable<HardLimitEntry> {
		final int maxCps;
		final int retentionSecs;
		// We reverse the order to make it descending, so that entries with the most retentionSecs come first
		@Override
		public int compareTo(HardLimitEntry o) {
			return o.retentionSecs - retentionSecs;
		}
	}
	
	@AllArgsConstructor
	@EqualsAndHashCode
	private static class DeviationEntry {
		final int deviationPercentage;
		final int sampleCount;
	}
	
	private long totalRetentionMillis() {
		return totalRetentionSecs * 1_000L;
	}
	
	private static long monotonicMillis() {
		return System.nanoTime() / 1_000_000L;
	}

	@Override
	protected void checkAsyncPeriodic(NessPlayer player) {
		// Cleanup old history
		Set<Long> clickHistory = player.getClickHistory();
		long now1 = monotonicMillis();
		long totalRetentionMillis = totalRetentionMillis();
		clickHistory.removeIf((time) -> time - now1 > totalRetentionMillis);

		if (clickHistory.isEmpty()) {
			return; // Don't check players who aren't clicking
		}

		// Copy temporary state
		Set<Long> copy1 = new HashSet<>(clickHistory);
		List<Long> copy2 = new ArrayList<>(copy1);

		// Hard limit checks

		for (HardLimitEntry hardLimitEntry : hardLimits) {
			long now2 = monotonicMillis();
			int span = hardLimitEntry.retentionSecs * 1_000;
			copy1.removeIf((time) -> time - now2 > span);
			int cps = copy1.size() / hardLimitEntry.retentionSecs;

			int maxCps = hardLimitEntry.maxCps;
			logger.debug("Clicks Per Second: {}. Limit: {}", cps, maxCps);
			if (cps > maxCps) {
				player.setViolation(new Violation("AutoClick", Integer.toString(cps)), null);
				return;
			}
		}

		// Constancy check

		/*
		 * Task: Measure the standard deviation of the intervals between clicks
		 * 
		 */
		copy2.sort(null);
		List<Long> periods = new ArrayList<>();

		/*
		 * Centering our numbers such that the first is zero
		 */
		long initial = copy2.get(0); // we center at this number
		long start = 0L;
		for (int n = 1; n < copy2.size(); n++) {
			long end = copy2.get(n) - initial;
			periods.add(end - start);
			start = end;
		}
		logger.debug("Click periods: {}", periods);

		/*
		 * Sublist of the total list of periods
		 */
		List<Long> subPeriods = new ArrayList<>();
		/*
		 * Standard deviation percentages of all the subspans
		 */
		Map<DeviationEntry, List<Long>> standardDeviationMap = new HashMap<>();
		for (int n = 0; n < periods.size(); n++) {

			for (DeviationEntry deviationRequirement : deviationRequirements) {
				for (int m = n; m < periods.size() && subPeriods.size() < deviationRequirement.sampleCount; m++) {
					subPeriods.add(periods.get(m));
				}
				logger.trace("SubPeriods for iteration {} and deviation requirement {} are {}", n, deviationRequirement, subPeriods);
				if (subPeriods.size() == deviationRequirement.sampleCount) {
					int stdDevPercent = getStdDevPercent(subPeriods);
					if (stdDevPercent < deviationRequirement.deviationPercentage) {
						player.setViolation(new Violation("AutoClick", "StdDevPercent: " + stdDevPercent), null);
						return;
					}
					standardDeviationMap.computeIfAbsent(deviationRequirement, (d) -> new ArrayList<>()).add((long) stdDevPercent);
				}
				subPeriods.clear();
			}
		}
		for (List<Long> standardDeviations : standardDeviationMap.values()) {
			logger.trace("StandardDeviations are {}", standardDeviations);
			for (DeviationEntry superDeviationRequirement : superDeviationRequirements) {
				if (standardDeviations.size() >= superDeviationRequirement.sampleCount) {
					int superStdDevPercent = getStdDevPercent(standardDeviations);
					if (superStdDevPercent < superDeviationRequirement.deviationPercentage) {
						player.setViolation(new Violation("AutoClick", "SuperStdDevPercent: " + superStdDevPercent), null);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * Visible for testing. <br>
	 * Calculates the standard deviation as a percent of the average
	 * 
	 * @param periods the numbers from which to calculate the standard deviation percentage
	 * @return the percentage
	 */
	public static int getStdDevPercent(List<Long> periods) {
		long average = calculateAverage(periods);
		logger.trace("Calculated average is {}", average);

		double standardDeviation = 0;
		for (long period : periods) {
			standardDeviation += Math.pow(period - average, 2);
		}
		standardDeviation = Math.sqrt(standardDeviation / periods.size());
		logger.trace("Standard deviation is calculated to be {}", standardDeviation);
		return (int) (100 * standardDeviation / average);
	}
	
	/**
	 * Visible for testing. <br>
	 * Calculates the average from a list of samples
	 * 
	 * @param samples the numbers from which to calculate the average
	 * @return the average
	 */
	public static long calculateAverage(List<Long> samples) {
		long sum = 0L;
		for (long period : samples) {
			sum += period;
		}
		long result = sum / samples.size();
		return result;
	}

	@Override
	protected void checkEvent(PlayerInteractEvent evt) {
		Action action = evt.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			Player player = evt.getPlayer();
			logger.trace("Added click from {}", player);
			manager.getPlayer(player).getClickHistory().add(monotonicMillis());
		}
	}

}
