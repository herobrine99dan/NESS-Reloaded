package com.github.ness.check.combat.autoclick;

import java.util.Set;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfSerialisers;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;
import space.arim.dazzleconf.annote.ConfHeader;

@ConfSerialisers(HardLimitSerialiser.class)
@ConfHeader({
	"",
	"AutoClick",
	"Caps clicks per second (CPS) at a hard limit, also calculates",
	"the variance in the user's clicks (deviation) and the variance",
	"in the variance (super deviation).",
	"",
	"Performance impact: Minimal",
	""})
public interface AutoClickConfig {

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
	Set<HardLimitEntry> hardLimits();
	
	@SubSection
	@ConfComments("# A more advanced consistency check")
	Constancy constancy();
	
	@ConfSerialisers(DeviationEntrySerialiser.class)
	interface Constancy {
		
		@ConfKey("deviation-and-sample")
		@ConfComments({
			"These are pairs of standard deviation percentages and sample counts",
			"",
			"The first number is a deviation percentage, and the second number is the sample count.",
			"",
			"The standard deviation is calculated based on the interval between clicks in the sample.",
			"Then, the standard deviation percentage is calculated as the standard deviation as percent of the average.",
			"",
			"If there are enough samples, and the deviation percent is less than the required deviation,",
			"a violation is triggered.",
			"",
			"For example, '30:8' means that if the standard deviation in the intervals between clicks in a sample",
			"divided by the average interval, is less than 30%, trigger a violation if the sample size is at least 8."
		})
		@DefaultStrings({"30:10"})
		Set<DeviationEntry> deviationRequirements();
		
		@ConfKey("superdeviation-and-supersample")
		@ConfComments({
			"These are pairs of standard deviation percentages and sample counts",
			"",
			"These are conceptually similar to the previous. However, this measures the standard deviations between",
			"the standard deviations. Thus, it is called the \"super deviation\"."
		})
		@DefaultStrings({"60:10"})
		Set<DeviationEntry> superDeviationRequirements();
		
	}
	
}
