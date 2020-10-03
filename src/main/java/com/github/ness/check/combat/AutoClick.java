package com.github.ness.check.combat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfSerialisers;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class AutoClick extends ListeningCheck<PlayerInteractEvent> {

	private static final Logger logger = NessLogger.getLogger(AutoClick.class);
	private final CheckConf conf;
	private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();
	
	public static final ListeningCheckInfo<PlayerInteractEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(PlayerInteractEvent.class, Duration.ofSeconds(2));

	public AutoClick(ListeningCheckFactory<?, PlayerInteractEvent> factory, NessPlayer player) {
		super(factory, player);

		conf = manager().getNess().getMainConfig().getCheckSection().autoClick();
	}
	
	@ConfSerialisers(HardLimitSerialiser.class)
	public interface CheckConf {
		
		@ConfKey("total-retention-secs")
		@ConfComments("Clicks older than this are completely ignored")
		@DefaultInteger(32)
		int totalRetentionSecs();
		
		@ConfKey("hard-limit.cps-and-required-span")
		@ConfComments({
			"These are pairs of CPS limits and required time spans",
			"",
			"If the player's CPS measured over the time span is greater than the CPS limit, a violation is triggered.",
			"",
			"      # For example, '16:3' means that if the player's clicks in the past 3 seconds average 16 CPS, ",
			"trigger a violation."})
		@DefaultStrings({"35:2"})
		List<HardLimitEntry> hardLimits();
		
		@SubSection
		@ConfComments("# A more advanced consistency check")
		Constancy constancy();
		
		@ConfSerialisers(DeviationEntrySerialiser.class)
		interface Constancy {
			
			@ConfKey("deviation-and-sample")
			@ConfComments({
				"These are pairs of standard deviation percentages and sample counts",
				"",
				"The standard deviation is calculated based on the interval between clicks in the sample.",
				"The second number in the pair determines the sample size.",
				"Then, the standard deviation percentage is calculated as the standard deviation as percent of the average.",
				"If this percent is less than the first number in the pair, a violation is triggered.",
				"",
				"For example, '30:8' means that if the standard deviation in the interval between clicks over a sample",
				"of 8 intervals, divided by the average interval, is greater than 30%, trigger a violation."
			})
			@DefaultStrings({"30:10"})
			List<DeviationEntry> deviationRequirements();
			
			@ConfKey("superdeviation-and-supersample")
			@ConfComments({
				"These are pairs of standard deviation percentages and sample counts",
				"",
				"These are conceptually similar to the previous. However, this measures the standard deviations between",
				"the standard deviations. Thus, it is called the \"super deviation\"."
			})
			@DefaultStrings({"60:10"})
			List<DeviationEntry> superDeviationRequirements();
			
		}
		
	}
	
	private static abstract class IntPairSerialiser<T> implements ValueSerialiser<T> {

		@Override
		public T deserialise(FlexibleType flexibleType) throws BadValueException {
			String[] info = flexibleType.getString().split(":", 2);
			try {
				return fromInts(Integer.parseInt(info[0]), Integer.parseInt(info[1]));
			} catch (NumberFormatException ex) {
				throw new BadValueException.Builder().key(flexibleType.getAssociatedKey()).cause(ex).build();
			}
		}

		@Override
		public Object serialise(T value) {
			int[] toInts = toInts(value);
			return toInts[0] + ":" + toInts[1];
		}
		
		abstract T fromInts(int value1, int value2);
		
		abstract int[] toInts(T value);
		
	}
	
	public static class HardLimitSerialiser extends IntPairSerialiser<HardLimitEntry> {

		@Override
		HardLimitEntry fromInts(int value1, int value2) {
			return new HardLimitEntry(value1, value2);
		}

		@Override
		int[] toInts(HardLimitEntry value) {
			return new int[] {value.maxCps, value.retentionSecs};
		}

		@Override
		public Class<HardLimitEntry> getTargetClass() {
			return HardLimitEntry.class;
		}
		
	}
	
	public static class DeviationEntrySerialiser extends IntPairSerialiser<DeviationEntry> {

		@Override
		DeviationEntry fromInts(int value1, int value2) {
			return new DeviationEntry(value1, value2);
		}

		@Override
		int[] toInts(DeviationEntry value) {
			return new int[] {value.deviationPercentage, value.sampleCount};
		}

		@Override
		public Class<DeviationEntry> getTargetClass() {
			return DeviationEntry.class;
		}
		
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
		return "AutoClick [conf=" + conf + "]";
	}

	private long totalRetentionMillis() {
		return conf.totalRetentionSecs() * 1_000L;
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

		for (HardLimitEntry hardLimitEntry : conf.hardLimits()) {
			long now2 = monotonicMillis();
			int span = hardLimitEntry.retentionSecs * 1_000;
			copy1.removeIf((time) -> time - now2 > span);
			int cps = copy1.size() / hardLimitEntry.retentionSecs;

			int maxCps = hardLimitEntry.maxCps;
			logger.log(Level.FINE, "Clicks Per Second: {0}. Limit: {1}", new Object[] { cps, maxCps });
			if (cps > maxCps) {
				flag();
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

			for (DeviationEntry deviationRequirement : conf.constancy().deviationRequirements()) {
				for (int m = n; m < periods.size() && subPeriods.size() < deviationRequirement.sampleCount; m++) {
					subPeriods.add(periods.get(m));
				}
				logger.log(Level.FINEST, "SubPeriods for iteration {0} and deviation requirement {1} are {2}",
						new Object[] { n, deviationRequirement, subPeriods });
				if (subPeriods.size() == deviationRequirement.sampleCount) {
					int stdDevPercent = getStdDevPercent(subPeriods);
					if (stdDevPercent < deviationRequirement.deviationPercentage) {
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
		for (List<Long> standardDeviations : standardDeviationMap.values()) {
			logger.log(Level.FINEST, "StandardDeviations are {0}", standardDeviations);
			for (DeviationEntry superDeviationRequirement : conf.constancy().superDeviationRequirements()) {
				if (standardDeviations.size() < superDeviationRequirement.sampleCount) {
					continue;
				}
				int superStdDevPercent = getStdDevPercent(standardDeviations);
				if (superStdDevPercent < superDeviationRequirement.deviationPercentage) {
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