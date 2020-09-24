package com.github.ness.violation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Infraction;
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

	String addViolationVariables(String message, Player player, Infraction infraction) {
		String replaced = message.replace("%PLAYER%", player.getName()).replace("%HACK%", infraction.getCheck())
				.replace("%VIOLATIONS%", Integer.toString(infraction.getCount()));
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

				Queue<Infraction> infractions = player.getInfractions();
				Infraction infraction;
				while ((infraction = infractions.poll()) != null) {
					cascadeViolations(player, infraction);
				}
			});
		}, 1L, 1L, TimeUnit.SECONDS);
	}

	private void cascadeViolations(NessPlayer nessPlayer, Infraction infraction) {

		Set<ViolationTrigger> syncTriggers = triggers.get(SynchronisationContext.FORCE_SYNC);
		if (!syncTriggers.isEmpty()) {
			ness.getServer().getScheduler().runTask(ness, () -> {
				for (ViolationTrigger trigger : syncTriggers) {
					trigger.trigger(nessPlayer.getPlayer(), infraction);
				}
			});
		}

		Set<ViolationTrigger> asyncTriggers = triggers.get(SynchronisationContext.FORCE_ASYNC);
		if (!asyncTriggers.isEmpty()) {
			ness.getServer().getScheduler().runTaskAsynchronously(ness, () -> {
				for (ViolationTrigger trigger : asyncTriggers) {
					trigger.trigger(nessPlayer.getPlayer(), infraction);
				}
			});
		}

		for (ViolationTrigger anywhere : triggers.get(SynchronisationContext.EITHER)) {
			anywhere.trigger(nessPlayer.getPlayer(), infraction);
		}
	}

}
