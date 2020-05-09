package com.github.ness;

import org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
				case "version":
					sendVersion(sender);
					break;
				default:
					usage(sender);
					break;
				}
			}
		} else {
			sendMessage(sender, ness.getNessConfig().getMessages().getString(
					"commands.no-permission", "&cSorry, you cannot use this."));
		}
		return true;
	}
	
	private void sendVersion(CommandSender sender) {
		sendMessage(sender, "&7Version " + ness.getDescription().getVersion());
	}
	
	private void usage(CommandSender sender) {
		sendMessage(sender, "&aNESS Anticheat");
		sendVersion(sender);
		sendMessage(sender, '\n'
				+ "/ness toggle <dev|debug> - Toggle debug or dev mode." + '\n'
				+ "/ness vl <player> - View player violations." + '\n'
				+ "/ness reload - Reload configuration." + '\n'
				+ '\n'
				+ "&7Authors: " + StringUtils.join(ness.getDescription().getAuthors(), ", "));
	}
	
	private void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

}
