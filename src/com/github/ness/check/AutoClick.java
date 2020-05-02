package com.github.ness.check;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.Violation;

public class AutoClick extends AbstractCheck<PlayerInteractEvent> {

	private int hardLimit;
	private int hardLimitRetention;

	private int constancyThreshold;
	private int constancyDeviation;
	private int constancyMinSample;

	private int constancySuperDeviation;
	private int constancySuperMinSample;

	private long constancySpan;

	private int totalRetention;
	
	public AutoClick(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 4, TimeUnit.SECONDS));
		ConfigurationSection section = manager.getNess().getNessConfig().getCheck(AutoClick.class);
		totalRetention = 1000 * section.getInt("total-retention-secs", 32);
		hardLimit = section.getInt("hard-limit.cps", 16);
		hardLimitRetention = 1000 * section.getInt("hard-limit.retention-span-secs", 4);

		constancyThreshold = section.getInt("constancy.threshold", 4);
		constancyDeviation = section.getInt("constancy.deviation-percent", 20);
		constancyMinSample = section.getInt("constancy.min-sample", 6);

		constancySuperDeviation = section.getInt("constancy.super.deviation-percent", 10);
		constancySuperMinSample = section.getInt("constancy.super.min-sample", 12);

		constancySpan = section.getLong("constancy.span-millis", 800);
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		// Cleanup old history
		Set<Long> clickHistory = player.getClickHistory();
		long now1 = System.currentTimeMillis();
		clickHistory.removeIf((time) -> time - now1 > totalRetention);

		if (clickHistory.isEmpty()) {
			return; // Don't check players who aren't clicking
		}

		// Copy temporary state
		Set<Long> copy1 = new HashSet<>(clickHistory);
		List<Long> copy2 = new ArrayList<>(copy1);

		// Hard limit check

		long now2 = System.currentTimeMillis();
		copy1.removeIf((time) -> time - now2 > hardLimitRetention);
		int cps = copy1.size() / hardLimitRetention;
		if (cps > hardLimit) {
			player.setViolation(new Violation("CPS", cps));
			return;
		}

		// Constancy check

		if (cps > constancyThreshold) {
			/*
			 * Task:
			 * Measure the standard deviation of the intervals between clicks
			 * 
			 */
			copy2.sort(null);
			List<Integer> periods = new ArrayList<>();

			/*
			 * Centering our numbers such that the first is zero
			 */
			long initial = copy2.get(0); // we center at this number
			long start = 0L;
			for (int n = 1; n < copy2.size(); n++) {
				long end = copy2.get(n) - initial;
				periods.add((int) (end - start));
				start = end;
			}

			List<Integer> subPeriods = new ArrayList<>();
			List<Integer> superPeriods = new ArrayList<>();
			for (int n = 0; n < periods.size(); n++) {
				int subStart = periods.get(n);
				int subPeriod;
				for (int m = n; (subPeriod = periods.get(m)) - subStart < constancySpan; m++) {
					subPeriods.add(subPeriod);
				}
				if (subPeriods.size() >= constancyMinSample) {
					int stdDevPercent = getStdDevPercent(subPeriods);
					if (stdDevPercent < constancyDeviation) {
						player.setViolation(new Violation("CPS", cps, stdDevPercent));
						return;
					}
					superPeriods.add(stdDevPercent);
				}
				subPeriods.clear();
			}
			if (superPeriods.size() >= constancySuperMinSample) {
				int superStdDevPercent = getStdDevPercent(superPeriods);
				if (superStdDevPercent < constancySuperDeviation) {
					player.setViolation(new Violation("CPS", cps, superStdDevPercent));
					return;
				}
			}
		}
	}
	
	private int getStdDevPercent(List<Integer> periods) {
		// Calculate the average
		long average = 0L;
		for (long period : periods) {
			average += period;
		}
		average = average/periods.size();

		double stdDevPercent = 0;
		for (long period : periods) {
			stdDevPercent += Math.pow(period - average, 2);
		}
		return (int) (100 * Math.sqrt(stdDevPercent/periods.size()) / average);
	}

	@Override
	void checkEvent(PlayerInteractEvent evt) {
		Action action = evt.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			manager.getPlayer(evt.getPlayer()).getClickHistory().add(System.currentTimeMillis());
		}
	}
	
}
