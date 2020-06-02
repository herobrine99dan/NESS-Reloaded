package com.github.ness.check;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class AutoClick extends AbstractCheck<PlayerInteractEvent> {

	private int hardLimit;
	private int hardLimitRetentionSecs;

	private int constancyThreshold;
	private int constancyDeviation;
	private int constancyMinSample;

	private int constancySuperDeviation;
	private int constancySuperMinSample;

	private long constancySpan;

	private int totalRetentionSecs;
	
	private static final Logger logger = LoggerFactory.getLogger(AutoClick.class);
	
	public AutoClick(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 4, TimeUnit.SECONDS));

		ConfigurationSection section = manager.getNess().getNessConfig().getCheck(AutoClick.class);

		totalRetentionSecs = section.getInt("total-retention-secs", 32);

		hardLimit = section.getInt("hard-limit.cps", 16);
		hardLimitRetentionSecs = section.getInt("hard-limit.retention-span-secs", 4);

		constancyThreshold = section.getInt("constancy.threshold", 4);
		constancyDeviation = section.getInt("constancy.deviation-percent", 20);
		constancyMinSample = section.getInt("constancy.min-sample", 6);

		constancySuperDeviation = section.getInt("constancy.super.deviation-percent", 10);
		constancySuperMinSample = section.getInt("constancy.super.min-sample", 12);

		constancySpan = section.getLong("constancy.span-millis", 800);
	}
	
	private long convertSecsToNanos(int seconds) {
		return TimeUnit.NANOSECONDS.convert(seconds, TimeUnit.SECONDS);
	}
	
	private long totalRetentionNanos() {
		return convertSecsToNanos(totalRetentionSecs);
	}
	
	private long hardLimitRetentionNanos() {
		return convertSecsToNanos(hardLimitRetentionSecs);
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		// Cleanup old history
		Set<Long> clickHistory = player.getClickHistory();
		long now1 = System.nanoTime();
		long totalRetentionNanos = totalRetentionNanos();
		clickHistory.removeIf((time) -> time - now1 > totalRetentionNanos);

		if (clickHistory.isEmpty()) {
			return; // Don't check players who aren't clicking
		}

		// Copy temporary state
		Set<Long> copy1 = new HashSet<>(clickHistory);
		List<Long> copy2 = new ArrayList<>(copy1);

		// Hard limit check

		long now2 = System.nanoTime();
		long hardLimitRetentionNanos = hardLimitRetentionNanos();
		copy1.removeIf((time) -> time - now2 > hardLimitRetentionNanos);
		int cps = copy1.size() / hardLimitRetentionSecs;
		logger.debug("Clicks Per Second: {}", cps);
		if (cps > hardLimit) {
			player.setViolation(new Violation("AutoClick", Integer.toString(cps)));
			return;
		}

		// Constancy check

		if (cps <= constancyThreshold) {
			return;
		}
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

		/*
		 * Sublist of the total list of periods
		 * 
		 */
		List<Long> subPeriods = new ArrayList<>();
		/*
		 * Standard deviation percentages of all the subspans
		 * 
		 */
		List<Long> standardDeviations = new ArrayList<>();
		for (int n = 0; n < periods.size(); n++) {
			long subStart = periods.get(n);
			long subPeriod;
			for (int m = n; (subPeriod = periods.get(m)) - subStart < constancySpan; m++) {
				subPeriods.add(subPeriod);
			}
			if (subPeriods.size() >= constancyMinSample) {
				int stdDevPercent = getStdDevPercent(subPeriods);
				if (stdDevPercent < constancyDeviation) {
					player.setViolation(new Violation("AutoClick", cps + " " + stdDevPercent));
					return;
				}
				standardDeviations.add((long) stdDevPercent);
			}
			subPeriods.clear();
		}
		if (standardDeviations.size() >= constancySuperMinSample) {
			int superStdDevPercent = getStdDevPercent(standardDeviations);
			if (superStdDevPercent < constancySuperDeviation) {
				player.setViolation(new Violation("AutoClick", cps + " " + superStdDevPercent));
			}
		}
	}
	
	private static int getStdDevPercent(List<Long> periods) {
		// Calculate the average
		long average = 0L;
		for (long period : periods) {
			average += period;
		}
		average = average / periods.size();

		double stdDevPercent = 0;
		for (long period : periods) {
			stdDevPercent += Math.pow(period - average, 2);
		}
		return (int) (100 * Math.sqrt(stdDevPercent / periods.size()) / average);
	}

	@Override
	void checkEvent(PlayerInteractEvent evt) {
		Action action = evt.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			manager.getPlayer(evt.getPlayer()).getClickHistory().add(System.nanoTime());
		}
	}

}
