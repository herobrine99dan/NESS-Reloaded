package com.github.ness.violation;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NessAnticheat;
import com.github.ness.api.Infraction;
import com.github.ness.api.InfractionManager;
import com.github.ness.api.InfractionTrigger;
import com.github.ness.api.InfractionTrigger.SynchronisationContext;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.check.InfractionImpl;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.dragdown.SetBack;
import com.github.ness.config.CheckConfig;

public class ViolationManager implements InfractionManager {

	private final NessAnticheat ness;
	
	private ScheduledFuture<?> periodicTask;

	private final Map<SynchronisationContext, Set<InfractionTrigger>> triggers;
	private volatile Map<String, CancelEvent> eventCancellation;

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
				.replace("%DETAILS%", infraction.getDetails())
				.replace("%PING%", Integer.toString(infraction.getPlayer().getPing()));
		return ChatColor.translateAlternateColorCodes('&', replaced);
	}

	public synchronized void initiate() {
		addDefaultTriggers();
		eventCancellation = addCheckSpecificTriggersAndGetCancelEvents();
		periodicTask = initiatePeriodicTask();
	}

	private void addDefaultTriggers() {
		ViolationHandling violationHandling = ness.getMainConfig().getViolationHandling();
		for (ViolationTriggerSection triggerSection : violationHandling.getTriggerSections()) {
			if (!triggerSection.enable()) {
				continue;
			}
			addTrigger(triggerSection.toTrigger(this, ness));
		}
	}

	private Map<String, CancelEvent> addCheckSpecificTriggersAndGetCancelEvents() {
		Map<String, CancelEvent> eventsCancellation = new HashMap<>();
		//Actually this configuration setting was disabled due to test purposes
		//TODO Fix the DazzleConf Bug
		//Map<String, CheckConfig> perCheckConfiguration = Collections.emptyMap();
		Map<String, CheckConfig> perCheckConfiguration = ness.getMainConfig().perCheckConfiguration();
		for (Map.Entry<String, CheckConfig> checkConfigEntry : perCheckConfiguration.entrySet()) {
			String checkName = checkConfigEntry.getKey();
			ViolationHandling checkViolationHandling = checkConfigEntry.getValue().violationHandling();

			for (ViolationTriggerSection triggerSection : checkViolationHandling.getTriggerSections()) {
				if (!triggerSection.enable()) {
					continue;
				}
				InfractionTrigger delegate = triggerSection.toTrigger(this, ness);
				InfractionTrigger trigger = new CheckSpecificInfractionTrigger(delegate, checkName);
				addTrigger(trigger);
			}
			CancelEvent previousCancel = eventsCancellation.put(checkName, checkViolationHandling.cancelEvent());
			if (previousCancel != null) {
				throw new RuntimeException("Duplicate 'cancel' sections in per-check violation handling");
			}
		}
		return eventsCancellation;
	}

	/**
	 * Determines whether to apply a setback
	 *
	 * @param check the check
	 * @param violations the violation count
	 * @return the setback to use, or {@code null} for none
	 */
	public SetBack shouldCancelWithSetBack(ListeningCheck<?> check, int violations) {
		CancelEvent cancelEvent = getCancelEventTrigger(check);
		int violationsThreshold = cancelEvent.violations();
		if (violationsThreshold != -1 && violations >= violationsThreshold) {
			return cancelEvent.setBack().getSetBack();
		}
		return null;
	}

	/**
	 * Determines whether to cancel a packet check
	 *
	 * @param check the packet check
	 * @param violations the violation count
	 * @return true to cancel, false otherwise
	 */
	public boolean shouldCancelPacketCheck(PacketCheck check, int violations) {
		CancelEvent cancelEvent = getCancelEventTrigger(check);
		int violationsThreshold = cancelEvent.violations();
		return violationsThreshold != -1 && violations >= violationsThreshold;
	}

	private CancelEvent getCancelEventTrigger(Check check) {
		String checkName = check.getFactory().getCheckName();
		//TODO Temporary fix
		if(eventCancellation == null) {
			return ness.getMainConfig().getViolationHandling().cancelEvent();
		}
		CancelEvent cancelEvent = eventCancellation.get(checkName);
		if (cancelEvent != null) {
			return cancelEvent;
		}
		return ness.getMainConfig().getViolationHandling().cancelEvent();
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
