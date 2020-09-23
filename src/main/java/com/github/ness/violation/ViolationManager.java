package com.github.ness.violation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationTrigger;
import com.github.ness.api.ViolationTrigger.SynchronisationContext;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ViolationManager {

	private final NESSAnticheat ness;

	private final Map<SynchronisationContext, Set<ViolationTrigger>> triggers;

	public ViolationManager(NESSAnticheat ness) {
		this.ness = ness;

		triggers = new EnumMap<>(SynchronisationContext.class);
		for (SynchronisationContext context : SynchronisationContext.values()) {
			triggers.put(context, new CopyOnWriteArraySet<>());
		}
	}

	String addViolationVariables(String message, Player player, Violation violation, int violationCount) {
		String replaced = message.replace("%PLAYER%", player.getName()).replace("%HACK%", violation.getCheck())
				.replace("%VIOLATIONS%", Integer.toString(violationCount));
		return ChatColor.translateAlternateColorCodes('&', replaced);
	}

	public void initiate() {
		addDefaultTriggers();
		initiatePeriodicTask();
	}

	private void addDefaultTriggers() {
		for (ViolationTriggerSection triggerSection : ness.getMainConfig().getViolationHandling()
				.getTriggerSections()) {
			if (!triggerSection.enable()) {
				continue;
			}
			addTrigger(triggerSection.toTrigger(this, ness));
		}
	}

	public void addTrigger(ViolationTrigger trigger) {
		Objects.requireNonNull(trigger, "trigger");
		SynchronisationContext context = Objects.requireNonNull(trigger.context(), "context");
		triggers.get(context).add(trigger);
	}

	private void initiatePeriodicTask() {
		ness.getExecutor().scheduleWithFixedDelay(() -> {
			ness.getCheckManager().forEachPlayer((player) -> {
				cascadeViolations(player);
			});
		}, 1L, 1L, TimeUnit.SECONDS);
	}

	private void cascadeViolations(NessPlayer player) {
		// Atomicaly get the existing violation and set it to null
		final Violation previous = player.violation.getAndSet(null);
		if (previous == null) {
			return;
		}

		Map<String, Integer> checkViolationCounts = player.checkViolationCounts;
		int violationCount = checkViolationCounts.get(previous.getCheck());

		Set<ViolationTrigger> syncTriggers = triggers.get(SynchronisationContext.FORCE_SYNC);
		if (!syncTriggers.isEmpty()) {
			ness.getServer().getScheduler().runTask(ness, () -> {
				for (ViolationTrigger trigger : syncTriggers) {
					trigger.actOn(player.getPlayer(), previous, violationCount);
				}
			});
		}

		Set<ViolationTrigger> asyncTriggers = triggers.get(SynchronisationContext.FORCE_ASYNC);
		if (!asyncTriggers.isEmpty()) {
			ness.getServer().getScheduler().runTaskAsynchronously(ness, () -> {
				for (ViolationTrigger trigger : asyncTriggers) {
					trigger.actOn(player.getPlayer(), previous, violationCount);
				}
			});
		}

		for (ViolationTrigger anywhere : triggers.get(SynchronisationContext.EITHER)) {
			anywhere.actOn(player.getPlayer(), previous, violationCount);
		}
	}

}
