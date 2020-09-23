package com.github.ness;

import com.github.ness.gui.ViolationGUI;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class NESSCommands implements CommandExecutor {

	private static final Logger logger = NessLogger.getLogger(NESSCommands.class);
	private final NESSAnticheat ness;

	private String getPermission(String arg) {
		if (arg == null) {
			return "ness.command.version";
		}
		switch (arg) {
		case "reload":
			return "ness.command.reload";
		case "report":
			return "ness.command.report";
		case "vl":
			return "ness.command.vl";
		case "clear":
			return "ness.command.clear";
		case "debug":
			return "ness.command.debug";
		case "gui":
			return "ness.command.gui";
		case "debugvelocity":
			return "ness.command.debugvelocity";
		case "mouserecord":
			return "ness.command.mouserecord";
		default:
			return "ness.command.version";
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
					ness.getConfigManager().reload().thenCompose((ignore) -> ness.getCheckManager().reload())
							.whenComplete((ignore, ex) -> {
						if (ex != null) {
							logger.log(Level.WARNING, "Error while reloading", ex);
							sendMessage(sender, "&cError reloading NESS, check your server console for details.");
							return;
						}
						sendMessage(sender, "&aReloaded NESS!");
					});
					break;
				case "violations":
					showViolations(sender, (args.length >= 2) ? Bukkit.getPlayer(args[1]) : null);
					break;
				case "clear":
					Player target = (args.length >= 2) ? Bukkit.getPlayer(args[1]) : null;
					if (target == null) {
						sendUnknownTarget(sender);
						break;
					}
					NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(target);
					if (nessPlayer == null) {
						sendUnknownTarget(sender);
						break;
					}
					nessPlayer.checkViolationCounts.clear();
					sendMessage(sender, "&7Cleared violations for &e%TARGET%".replace("%TARGET%", target.getName()));
					break;
				case "version":
					sendMessage(sender, "&7NESS Version: " + ness.getDescription().getVersion());
					break;
				case "gui":
					if (sender instanceof Player) {
						new ViolationGUI(sender).createGUI();
					}
					break;
				case "debug":
					if (sender instanceof Player) {
						NessPlayer np = ness.getCheckManager().getExistingPlayer((Player) sender);
						if (np == null) {
							// This shouldn't happen, but just in case
							sendMessage(sender, "Your NESSPlayer isn't loaded");
							return true;
						}
						if (np.isDebugMode()) {
							this.sendMessage(sender, "&7Debug Mode &cDisabled&7!");
							np.setDebugMode(false);
						} else {
							this.sendMessage(sender, "&7Debug Mode &aEnabled&7!");
							np.setDebugMode(true);
						}
					} else {
						this.sendMessage(sender, "&7Sorry but to use this command you have to be a Player");
					}
					break;
				case "debugvelocity":
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (args.length > 1) {
							p.setVelocity(p.getLocation().getDirection().multiply(Double.valueOf(args[1])));
							p.sendMessage("Done!");
						}
					} else {
						this.sendMessage(sender, "&7Sorry but to use this command you have to be a Player");
					}
					break;
				case "mouserecord":
					if (sender instanceof Player) {
						NessPlayer np = ness.getCheckManager().getExistingPlayer((Player) sender);
						if (np == null) {
							// This shouldn't happen, but just in case
							sendMessage(sender, "Your NESSPlayer isn't loaded");
							return true;
						}
						if (np.isMouseRecord()) {
							this.sendMessage(sender, "&7You've stopped the Mouse Record Process!");
							np.mouseRecordValues.clear();
							np.setMouseRecord(false);
						} else {
							this.sendMessage(sender, "&7Recording Mouse Movements");
							np.setMouseRecord(true);
						}
					} else {
						this.sendMessage(sender, "&7Sorry but to use this command you have to be a Player");
					}
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
			sendUnknownTarget(sender);
			return;
		}
		NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(target);
		if (nessPlayer == null) {
			sendUnknownTarget(sender);
			return;
		}
		String name = target.getName();
		String header = ness.getMessagesConfig().commands().showViolationsHeader();
		String body = ness.getMessagesConfig().commands().showViolationsBody();

		Map<String, Integer> violationMap = nessPlayer.checkViolationCounts;
		sendMessage(sender, header.replace("%TARGET%", name));
		for (Map.Entry<String, Integer> entry : violationMap.entrySet()) {
			sendMessage(sender,
					body.replace("%HACK%", entry.getKey()).replace("%VL%", Integer.toString(entry.getValue())));
		}
	}
	
	private void sendUnknownTarget(CommandSender sender) {
		sendMessage(sender, ness.getNessConfig().getMessages().getString("commands.not-found-player",
				"&cTarget player not specified, not found, or offline."));
	}

	private void usage(CommandSender sender) {
		sendMessage(sender, "&aNESS Anticheat");
		sendMessage(sender, "&7Version " + ness.getDescription().getVersion());
		sendMessage(sender, "/ness reload - Reload configuration");
		sendMessage(sender, "/ness violations <player> - Show violations for a player");
		sendMessage(sender, "/ness clear <player> - Clear player violations");
		sendMessage(sender, "/ness version - View the NESS Reloaded Version");
		sendMessage(sender, "/ness report <player> <reason> - Send a report to staffers");
		sendMessage(sender, "/ness gui - Open a gui where you can see violations of players");
		sendMessage(sender, "/ness debug - Enable / Disable Debug Mode");
		sendMessage(sender, "&7Authors: " + String.join(", ", ness.getDescription().getAuthors()));
	}

	private void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

}
