package com.github.ness.violation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.ness.NessAnticheat;
import com.github.ness.api.Infraction;
import com.github.ness.api.InfractionManager;
import com.github.ness.api.InfractionTrigger;
import com.github.ness.api.InfractionTrigger.SynchronisationContext;
import com.github.ness.check.CheckManager;
import com.github.ness.check.InfractionImpl;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class ViolationManager implements InfractionManager {

	private final NessAnticheat ness;
	
	private ScheduledFuture<?> periodicTask;

	private final Map<SynchronisationContext, Set<InfractionTrigger>> triggers;

	public ViolationManager(NessAnticheat ness) {
		this.ness = ness;

		triggers = new EnumMap<>(SynchronisationContext.class);
		for (SynchronisationContext context : SynchronisationContext.values()) {
			triggers.put(context, new CopyOnWriteArraySet<>());
		}
	}

	String addViolationVariables(String message, InfractionImpl infraction) {
		String replaced = message
				.replace("%PLAYER%", infraction.getPlayer().getBukkitPlayer().getName())
				.replace("%HACK%", infraction.getCheck().getCheckName())
				.replace("%VIOLATIONS%", Integer.toString(infraction.getCount()))
				.replace("%DETAILS%", infraction.getDetails());
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

	@Override
	public void addTrigger(InfractionTrigger trigger) {
		Objects.requireNonNull(trigger, "trigger");
		SynchronisationContext context = Objects.requireNonNull(trigger.context(), "context");
		boolean added = triggers.get(context).add(trigger);
		if (!added) {
			throw new IllegalStateException("Trigger " + trigger + " is already added");
		}
	}
	
	@Override
	public boolean removeTrigger(InfractionTrigger trigger) {
		Objects.requireNonNull(trigger, "trigger");
		SynchronisationContext context = trigger.context();
		return triggers.get(context).remove(trigger);
	}

	private ScheduledFuture<?> initiatePeriodicTask() {
		CheckManager checkManager = ness.getCheckManager();
		return ness.getExecutor().scheduleWithFixedDelay(() -> {
			checkManager.forEachPlayer((player) -> {
				player.pollInfractions(this::cascadeTriggers);
			});
		}, 1L, 1L, TimeUnit.SECONDS);
	}

	private void cascadeTriggers(Infraction infraction) {

		JavaPlugin plugin = ness.getPlugin();
		Set<InfractionTrigger> syncTriggers = triggers.get(SynchronisationContext.FORCE_SYNC);
		if (!syncTriggers.isEmpty()) {
			plugin.getServer().getScheduler().runTask(plugin, () -> {
				syncTriggers.forEach((trigger) -> {
					trigger.trigger(infraction);
				});
			});
		}

		Set<InfractionTrigger> asyncTriggers = triggers.get(SynchronisationContext.FORCE_ASYNC);
		if (!asyncTriggers.isEmpty()) {
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
				asyncTriggers.forEach((trigger) -> {
					trigger.trigger(infraction);
				});
			});
		}

		triggers.get(SynchronisationContext.ANY).forEach((trigger) -> {
			trigger.trigger(infraction);
		});
	}
	
	public synchronized void close() {
		periodicTask.cancel(false);
	}

}
