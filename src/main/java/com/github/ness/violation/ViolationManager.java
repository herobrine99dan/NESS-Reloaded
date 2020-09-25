package com.github.ness.violation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.ness.NESSAnticheat;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.InfractionTrigger;
import com.github.ness.api.InfractionTrigger.SynchronisationContext;
import com.github.ness.check.CheckManager;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class ViolationManager {

	private final NESSAnticheat ness;
	
	private ScheduledFuture<?> periodicTask;

	private final Map<SynchronisationContext, Set<InfractionTrigger>> triggers;

	public ViolationManager(NESSAnticheat ness) {
		this.ness = ness;

		triggers = new EnumMap<>(SynchronisationContext.class);
		for (SynchronisationContext context : SynchronisationContext.values()) {
			triggers.put(context, new CopyOnWriteArraySet<>());
		}
	}

	String addViolationVariables(String message, AnticheatPlayer player, Infraction infraction) {
		String replaced = message.replace("%PLAYER%", player.getPlayer().getName())
				.replace("%HACK%", infraction.getCheck().getCheckName())
				.replace("%VIOLATIONS%", Integer.toString(infraction.getCount()));
		return ChatColor.translateAlternateColorCodes('&', replaced);
	}

	public synchronized void initiate() {
		addDefaultTriggers();
		periodicTask = initiatePeriodicTask();
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

	public void addTrigger(InfractionTrigger trigger) {
		Objects.requireNonNull(trigger, "trigger");
		SynchronisationContext context = Objects.requireNonNull(trigger.context(), "context");
		triggers.get(context).add(trigger);
	}

	private ScheduledFuture<?> initiatePeriodicTask() {
		CheckManager checkManager = ness.getCheckManager();
		return ness.getExecutor().scheduleWithFixedDelay(() -> {
			checkManager.forEachPlayer((player) -> {

				Queue<Infraction> infractions = player.getInfractions();
				Infraction infraction;
				while ((infraction = infractions.poll()) != null) {
					cascadeViolations(player, infraction);
				}
			});
		}, 1L, 1L, TimeUnit.SECONDS);
	}

	private void cascadeViolations(AnticheatPlayer player, Infraction infraction) {

		JavaPlugin plugin = ness.getPlugin();
		Set<InfractionTrigger> syncTriggers = triggers.get(SynchronisationContext.FORCE_SYNC);
		if (!syncTriggers.isEmpty()) {
			plugin.getServer().getScheduler().runTask(plugin, () -> {
				syncTriggers.forEach((trigger) -> {
					trigger.trigger(player, infraction);
				});
			});
		}

		Set<InfractionTrigger> asyncTriggers = triggers.get(SynchronisationContext.FORCE_ASYNC);
		if (!asyncTriggers.isEmpty()) {
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
				asyncTriggers.forEach((trigger) -> {
					trigger.trigger(player, infraction);
				});
			});
		}

		triggers.get(SynchronisationContext.EITHER).forEach((trigger) -> {
			trigger.trigger(player, infraction);
		});
	}
	
	public synchronized void close() {
		periodicTask.cancel(false);
	}

}
