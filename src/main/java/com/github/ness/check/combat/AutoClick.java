package com.github.ness.check.combat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntBinaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.CheckManager;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class AutoClick extends ListeningCheck<PlayerInteractEvent> {

	private static final Logger logger = NessLogger.getLogger(AutoClick.class);
	private final List<HardLimitEntry> hardLimits = new ArrayList<>();
	private final Set<DeviationEntry> deviationRequirements = new HashSet<>();
	private final Set<DeviationEntry> superDeviationRequirements = new HashSet<>();
	private final int totalRetentionSecs;
	private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();

	public static final ListeningCheckInfo<PlayerInteractEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(PlayerInteractEvent.class, Duration.ofSeconds(2));

	public AutoClick(ListeningCheckFactory<?, PlayerInteractEvent> factory, NessPlayer player) {
		super(factory, player);

		CheckManager manager = manager();
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
		logger.log(Level.FINEST, "Configuration: {0}", this);
	}

	private static long monotonicMillis() {
		return System.nanoTime() / 1_000_000L;
	}

	/**
	 * Visible for testing. <br>
	 * Calculates the standard deviation as a percent of the average
	 *
	 * @param periods the numbers from which to calculate the standard deviation
	 *                percentage
	 * @return the percentage
	 */
	public static int getStdDevPercent(List<Long> periods) {
		long average = calculateAverage(periods);
		logger.log(Level.FINEST, "Calculated average is {0}", average);

		double standardDeviation = 0;
		for (long period : periods) {
			standardDeviation += Math.pow(period - average, 2);
		}
		standardDeviation = Math.sqrt(standardDeviation / periods.size());
		logger.log(Level.FINEST, "Standard deviation is calculated to be {0}", standardDeviation);
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
					logger.log(Level.FINE, "Cannot format {0}:{1} to integers", new Object[] { info[0], info[1] });
				}
			} else {
				logger.log(Level.FINE, "Cannot format illegal entry length of {0}", info.length);
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

	private long totalRetentionMillis() {
		return totalRetentionSecs * 1_000L;
	}

	@Override
	protected void checkAsyncPeriodic() {
		// Cleanup old history
		Set<Long> clickHistory = this.clickHistory;
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
			logger.log(Level.FINE, "Clicks Per Second: {0}. Limit: {1}", new Object[] { cps, maxCps });
			if (cps > maxCps) {
				player().setViolation(new Violation("AutoClick", Integer.toString(cps)));
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
		logger.log(Level.FINE, "Click periods: {0}", periods);

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
				logger.log(Level.FINEST, "SubPeriods for iteration {0} and deviation requirement {1} are {2}",
						new Object[] { n, deviationRequirement, subPeriods });
				if (subPeriods.size() == deviationRequirement.sampleCount) {
					int stdDevPercent = getStdDevPercent(subPeriods);
					if (stdDevPercent < deviationRequirement.deviationPercentage) {
						player().setViolation(new Violation("AutoClick", "StdDevPercent: " + stdDevPercent));
						return;
					}
					List<Long> deviations = standardDeviationMap.computeIfAbsent(deviationRequirement,
							(d) -> new ArrayList<>());
					deviations.add((long) stdDevPercent);
				}
				subPeriods.clear();
			}
		}
		for (List<Long> standardDeviations : standardDeviationMap.values()) {
			logger.log(Level.FINEST, "StandardDeviations are {0}", standardDeviations);
			for (DeviationEntry superDeviationRequirement : superDeviationRequirements) {
				if (standardDeviations.size() < superDeviationRequirement.sampleCount) {
					continue;
				}
				int superStdDevPercent = getStdDevPercent(standardDeviations);
				if (superStdDevPercent < superDeviationRequirement.deviationPercentage) {
					player().setViolation(new Violation("AutoClick", "SuperStdDevPercent: " + superStdDevPercent));
					return;
				}
			}
		}
	}

	@Override
	protected void checkEvent(PlayerInteractEvent evt) {
		Action action = evt.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			Player player = evt.getPlayer();
			logger.log(Level.FINEST, "Added click from {0}", player);
			clickHistory.add(monotonicMillis());
		}
	}

	@AllArgsConstructor
	private static class HardLimitEntry implements Comparable<HardLimitEntry> {
		final int maxCps;
		final int retentionSecs;

		// We reverse the order to make it descending, so that entries with the most
		// retentionSecs come first
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

}