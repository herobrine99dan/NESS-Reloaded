package com.github.ness;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NessCommands implements CommandExecutor {

	private final NESSAnticheat ness;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("ness.admin")) {
			if (args.length >= 1) {
				switch (args[0].toLowerCase()) {
				case "reload":
					ness.getCheckManager().reloadChecks();
					sendMessage(sender, ness.getNessConfig().getMessages().getString(
							"commands.reloaded", "&aReloaded NESS!"));
					break;
				default:
					usage(sender);
					break;
				}
			} else {
				usage(sender);
			}
		} else {
			sendMessage(sender, ness.getNessConfig().getMessages().getString(
					"commands.no-permission", "&cSorry, you cannot use this."));
		}
		return true;
	}
	
	private void usage(CommandSender sender) {
		sendMessage(sender, ness.getNessConfig().getMessages().getString("commands.usage", "&cUsage: /ness reload"));
	}
	
	private void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

}
