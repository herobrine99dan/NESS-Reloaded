package com.github.ness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.ness.api.ViolationAction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViolationManager {

	private final NESSAnticheat ness;

	private final Set<ViolationAction> actions = ConcurrentHashMap.newKeySet(8);

	private String addViolationVariables(String message, Player player, Violation violation) {
		return message.replace("%PLAYER%", player.getName()).replace("%HACK%", violation.getCheck())
				.replace("%DETAILS%", StringUtils.join(violation.getDetails(), ", "))
				.replace("%VL%", Integer.toString(ness.getCheckManager().getPlayer(player).getVL(violation))).replace("&", "§");
	}

	void addDefaultActions() {
		ConfigurationSection section = ness.getNessConfig().getViolationHandling();
		if (section != null) {
			ConfigurationSection notifyStaff = section.getConfigurationSection("notify-staff");
			if (notifyStaff != null && notifyStaff.getBoolean("enable", false)) {
				final String notification = notifyStaff.getString("notification");
				if (notification != null) {
					addAction(new ViolationAction(false) {

						@Override
						public void accept(Player player, Violation violation) {
							if(player.hasPermission("ness.bypass.*") || player.hasPermission("ness.bypass."+violation.getCheck().toLowerCase())) {
								return;
							}
							String notif = addViolationVariables(notification, player, violation);
							for (Player staff : Bukkit.getOnlinePlayers()) {
								if (staff.hasPermission("ness.notify")) {
									staff.sendMessage(notif);
								}
							}
						}

					});
				}
			}
			ConfigurationSection execCmd = section.getConfigurationSection("execute-command");
			if (execCmd != null && execCmd.getBoolean("enable", false)) {
				final String command = execCmd.getString("command");
				if (command != null) {
					addAction(new ViolationAction(false) {

						@Override
						public void accept(Player player, Violation violation) {
							String cmd = addViolationVariables(command, player, violation);
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
						}

					});
				}
			}
		}
	}

	void addAction(ViolationAction action) {
		actions.add(action);
	}

	void initiatePeriodicTask() {
		ness.getExecutor().scheduleWithFixedDelay(() -> {
			ness.getCheckManager().forEachPlayer((player) -> {

				// Atomicaly get the existing violation and set it to null
				final Violation previous = player.violation.getAndSet(null);
				if (previous != null) {

					Map<String, Integer> checkViolationCounts = player.checkViolationCounts;
					if (checkViolationCounts == null) {
						checkViolationCounts = new HashMap<>();
						player.checkViolationCounts = checkViolationCounts;
					}
					checkViolationCounts.merge(previous.getCheck(), 1, Math::addExact);

					// actions we have to run on the main thread
					Set<ViolationAction> syncActions = null;

					for (ViolationAction action : actions) {
						if (action.canRunAsync()) {
							action.accept(player.getPlayer(), previous);
						} else {
							if (syncActions == null) {
								syncActions = new HashSet<>();
							}
							syncActions.add(action);
						}
					}
					if (syncActions != null) {
						final Set<ViolationAction> syncActionsFinal = syncActions;
						Bukkit.getScheduler().runTask(ness, () -> {
							for (ViolationAction syncAction : syncActionsFinal) {
								syncAction.accept(player.getPlayer(), previous);
							}
						});
					}
				}
			});
		}, 1L, 1L, TimeUnit.SECONDS);
	}

}
