package com.github.ness.check.combat.autoclick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.check.ListeningCheck;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AutoClick extends ListeningCheck<PlayerInteractEvent> {

	private static final Logger logger = NessLogger.getLogger(AutoClick.class);
	
	private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();
	
	private final AutoClickFactory factory;

	public AutoClick(AutoClickFactory factory, NessPlayer player) {
		super(factory, player);
		this.factory = factory;
	}

	private static long monotonicMillis() {
		return System.nanoTime() / 1_000_000L;
	}

	/**
	 * Visible for testing. <br>
	 * Calculates the standard deviation as a percent of the average
	 *
	 * @param samples the numbers from which to calculate the standard deviation
	 *                percentage
	 * @return the percentage
	 */
	static int getStdDevPercent(List<Long> samples) {
		long average = calculateAverage(samples);

		double variance = 0;
		for (long period : samples) {
			long differenceFromMean = period - average;
			variance += (differenceFromMean * differenceFromMean);
		}
		double standardDeviation = Math.sqrt(variance / samples.size());
		return (int) (100 * standardDeviation / average);
	}

	/**
	 * Visible for testing. <br>
	 * Calculates the average from a list of samples
	 *
	 * @param samples the numbers from which to calculate the average
	 * @return the average
	 */
	static long calculateAverage(List<Long> samples) {
		long sum = 0L;
		for (long period : samples) {
			sum += period;
		}
		long result = sum / samples.size();
		return result;
	}

	@Override
	public String toString() {
		return "AutoClick [factory=" + factory + "]";
	}

	@Override
	protected void checkAsyncPeriodic() {
		// Cleanup old history
		Set<Long> clickHistory = this.clickHistory;

		final long now = monotonicMillis();
		clickHistory.removeIf((time) -> time - now > factory.totalRetentionMillis());

		if (clickHistory.isEmpty()) {
			return;
		}

		// Copy temporary state
		Set<Long> clicksCopy1 = new HashSet<>(clickHistory);
		List<Long> clicksCopy2 = new ArrayList<>(clicksCopy1);

		if (checkHardLimits(clicksCopy1)) {
			flag();
			return;
		}
		checkConstancyClickTimes(clicksCopy2);
	}
	
	private boolean checkHardLimits(Set<Long> clicksCopy) {
		final long now = monotonicMillis();

		for (HardLimitEntry hardLimitEntry : factory.sortedHardLimits()) {
			int rentionSpanMillis = hardLimitEntry.retentionSecs() * 1_000;
			clicksCopy.removeIf((time) -> time - now > rentionSpanMillis);

			int cps = clicksCopy.size() / hardLimitEntry.retentionSecs();
			if (cps > hardLimitEntry.maxCps()) {
				return true;
			}
		}
		return false;
	}
	
	private void checkConstancyClickTimes(List<Long> clicksCopy) {
		List<Long> intervals = getIntervals(clicksCopy);
		checkConstancyIntervals(intervals);
	}
	
	private List<Long> getIntervals(List<Long> clicksCopy) {
		clicksCopy.sort(null);
		List<Long> intervals = new ArrayList<>(clicksCopy.size());

		long startTime = clicksCopy.get(0);
		for (int n = 1; n < clicksCopy.size(); n++) {
			long clickTime = clicksCopy.get(n);
			intervals.add(clickTime - startTime);
			startTime = clickTime;
		}
		return intervals;
	}
	
	private void checkConstancyIntervals(List<Long> clickIntervals) {
		// Task: Measure the standard deviation of the intervals between clicks

		// Re-used list of periods for a specific deviation requirement
		List<Long> subPeriods = new ArrayList<>();
		/*
		 * Standard deviation percentages of all the subspans
		 */
		Map<DeviationEntry, List<Long>> standardDeviationMap = new HashMap<>();
		for (int n = 0; n < clickIntervals.size(); n++) {

			for (DeviationEntry deviationRequirement : factory.deviationRequirements()) {
				for (int m = n; m < clickIntervals.size() && subPeriods.size() < deviationRequirement.sampleCount(); m++) {
					subPeriods.add(clickIntervals.get(m));
				}
				if (subPeriods.size() == deviationRequirement.sampleCount()) {
					int stdDevPercent = getStdDevPercent(subPeriods);
					if (stdDevPercent < deviationRequirement.deviationPercentage()) {
						flag();
						return;
					}
					List<Long> deviations = standardDeviationMap.computeIfAbsent(deviationRequirement,
							(d) -> new ArrayList<>());
					deviations.add((long) stdDevPercent);
				}
				subPeriods.clear();
			}
		}
		checkSuperDeviation(standardDeviationMap.values());
	}
	
	private void checkSuperDeviation(Collection<List<Long>> standardDeviationsGroups) {
		for (List<Long> standardDeviations : standardDeviationsGroups) {
			for (DeviationEntry superDeviationRequirement : factory.superDeviationRequirements()) {
				if (standardDeviations.size() < superDeviationRequirement.sampleCount()) {
					continue;
				}
				int superStdDevPercent = getStdDevPercent(standardDeviations);
				if (superStdDevPercent < superDeviationRequirement.deviationPercentage()) {
					flag();
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

}