package com.github.ness;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NessCommands implements CommandExecutor {

	private final NESSAnticheat ness;

	private String getPermission(String arg) {
		if (arg == null) {
			return "ness.command.usage";
		}
		switch (arg) {
		case "reload":
			return "ness.command.reload";
		case "version":
			return "ness.command.usage";
		default:
			return "ness.command.usage";
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String firstArg = (args.length >= 1) ? args[0] : null;
		if (sender.hasPermission(getPermission(firstArg))) {
			if (firstArg == null) {
				usage(sender);
			} else {
				switch (args[0].toLowerCase()) {
				case "reload":
					ness.getNessConfig().reloadConfiguration(ness);
					ness.getCheckManager().reloadChecks();
					sendMessage(sender, "&aReloaded NESS!");
					break;
				case "vl":
					showViolations(sender, (args.length >= 2) ? Bukkit.getPlayer(args[1]) : null);
					break;
				case "clear":
					clearViolations(sender, (args.length >= 2) ? Bukkit.getPlayer(args[1]) : null);
					break;
				case "version":
					sendVersion(sender);
					break;
				default:
					usage(sender);
					break;
				}
			}
		} else {
			sendMessage(sender, ness.getNessConfig().getMessages().getString("commands.no-permission",
					"&cSorry, you cannot use this."));
		}
		return true;
	}
	
	private void showViolations(CommandSender sender, Player target) {
		if (target == null) {
			sendMessage(sender, ness.getNessConfig().getMessages().getString("commands.not-found-player",
					"&cTarget player not specified, not found, or offline."));
			return;
		}
		String name = target.getName();
		sendMessage(sender, ness.getNessConfig().getMessages().getString("commands.show-violations.starting",
				"7Listing violations...").replace("%TARGET%", name));
		String header = ness.getNessConfig().getMessages().getString("commands.show-violations.header",
				"&7Violations for &e%TARGET%");
		String body = ness.getNessConfig().getMessages().getString("commands.show-violations.body",
				"&7Hack: &e%HACK%&7. Count: %VL%");

		ness.getViolationManager().getCopyOfViolationMap(ness.getCheckManager().getPlayer(target)).thenAccept((violations) -> {
			sendMessage(sender, header.replace("%TARGET%", name));
			for (Map.Entry<String, Integer> entry : violations.entrySet()) {
				sendMessage(sender, body.replace("%HACK%", entry.getKey()).replace("%VL%", Integer.toString(entry.getValue())));
			}
		});
	}
	
	private void clearViolations(CommandSender sender, Player target) {
		if (target == null) {
			sendMessage(sender, ness.getNessConfig().getMessages().getString("commands.not-found-player",
					"&cTarget player not specified, not found, or offline."));
			return;
		}
		String name = target.getName();
		sendMessage(sender, ness.getNessConfig().getMessages().getString("commands.clear-violations.starting",
				"7Clearing violations...").replace("%TARGET%", name));
		String completeMsg = ness.getNessConfig().getMessages().getString("", "&7Cleared violations for &e%TARGET%");

		ness.getViolationManager().clearViolations(ness.getCheckManager().getPlayer(target)).thenRun(() -> {
			sendMessage(sender, completeMsg.replace("%TAGET%", name));
		});
	}
	
	private void sendVersion(CommandSender sender) {
		sendMessage(sender, "&7Version " + ness.getDescription().getVersion());
	}

	private void usage(CommandSender sender) {
		sendMessage(sender, "&aNESS Anticheat");
		sendVersion(sender);
		sendMessage(sender,
				'\n' + "/ness toggle <dev|debug> - Toggle debug or dev mode." + '\n'
						+ "/ness vl <player> - View player violations." + '\n' + "/ness reload - Reload configuration."
						+ '\n' + '\n' + "&7Authors: " + StringUtils.join(ness.getDescription().getAuthors(), ", "));
	}

	private void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

}
