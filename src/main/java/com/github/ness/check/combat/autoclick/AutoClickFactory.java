package com.github.ness.check.combat.autoclick;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.github.ness.check.CheckInfos;
import com.github.ness.check.CheckManager;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;

import org.bukkit.event.player.PlayerInteractEvent;

public class AutoClickFactory extends ListeningCheckFactory<AutoClick, PlayerInteractEvent> {
	
	private static final ListeningCheckInfo<PlayerInteractEvent> CHECK_INFO = CheckInfos
			.forEventWithTask(PlayerInteractEvent.class, PeriodicTaskInfo.syncTask(Duration.ofSeconds(2)));
	
	private final AutoClickConfig.Constancy constancy;
	private final List<HardLimitEntry> sortedHardLimits;
	
	private final long totalRetentionMillis;

	public AutoClickFactory(CheckManager manager) {
		super((factory, nessPlayer) -> new AutoClick((AutoClickFactory) factory, nessPlayer),
				"AutoClick", manager, CHECK_INFO);

		AutoClickConfig config = manager.getNess().getMainConfig().getCheckSection().autoClick();
		constancy = config.constancy();

		// Sort hard limits in descending order of retention
		sortedHardLimits = new ArrayList<>(config.hardLimits());
		sortedHardLimits.sort(Comparator.comparing(HardLimitEntry::retentionSecs).reversed());

		totalRetentionMillis = config.totalRetentionSecs() * 1_000L;
	}
	
	List<HardLimitEntry> sortedHardLimits() {
		return sortedHardLimits;
	}
	
	Set<DeviationEntry> deviationRequirements() {
		return constancy.deviationRequirements();
	}
	
	Set<DeviationEntry> superDeviationRequirements() {
		return constancy.superDeviationRequirements();
	}

	long totalRetentionMillis() {
		return totalRetentionMillis;
	}

}
