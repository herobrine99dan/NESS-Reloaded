package org.mswsplex.MSWS.NESS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class NESSCommand implements TabCompleter, CommandExecutor {
	String[] accepted = { "Fly", "KillAura", "Scaffold", "Speed" };

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		if (args.length == 1) {
			for (String tempRes : new String[] { "toggle", "vl", "clear", "version", "reload", "report" }) {
				if (sender.hasPermission("ness.command." + tempRes) && tempRes.startsWith(args[0].toLowerCase()))
					result.add(tempRes);
			}
		} else {
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("clear") && sender.hasPermission("ness.command.clear")) {
					for (String tempRes : new String[] { "All", "AntiFire", "AutoSneak", "Blink", "BunnyHop",
							"Fast Bow", "FastLadder", "Fast Sneak", "Flight", "Glide", "High CPS",
							"Illegal Interaction", "Illegal Movement", "Jesus", "Kill Aura", "NoClip", "NoFall",
							"NoWeb", "Phase", "Reach", "Regen", "Scaffold", "Spambot", "Speed", "Spider", "Step",
							"Timer" }) {
						if (tempRes.toLowerCase().startsWith(args[2].toLowerCase()))
							result.add(tempRes);
					}
				}
				if (args[0].equalsIgnoreCase("report") && sender.hasPermission("ness.command.report")) {
					for (String res : accepted) {
						if (res.toLowerCase().startsWith(args[2].toLowerCase()))
							result.add(res.replace("_", ""));
					}
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("toggle") && sender.hasPermission("ness.command.toggle")) {
					for (String tempRes : new String[] { "cancel", "dev", "global", "manual", "debug" }) {
						if (tempRes.toLowerCase().startsWith(args[1].toLowerCase()))
							result.add(tempRes);
					}
				}
				if (args[0].equalsIgnoreCase("clear") && sender.hasPermission("ness.command.clear")) {
					for (String tempRes : new String[] { "All" }) {
						if (tempRes.toLowerCase().startsWith(args[1].toLowerCase()))
							result.add(tempRes);
					}
				}
			}
			if (result.isEmpty()) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (target.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
						result.add(target.getName());
					}
				}
			}
		}
		return result;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String prefix = NESS.main.prefix + " &7";
		OfflinePlayer target;
		Player player;
		switch (command.getName().toLowerCase()) {
		case "ness":
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
				case "vl":
					if (!(sender.hasPermission("ness.command.vl"))) {
						MSG.noPerm(sender);
						return true;
					}
					if (args.length == 1) {
						boolean did = false;
						for (String res : NESS.main.vl.getKeys(false)) {
							if (Bukkit.getPlayer(UUID.fromString(res)) != null) {
								String msg = "";
								for (String user : NESS.main.vl.getConfigurationSection(res).getKeys(false)) {
									if (user.matches("(category|accuracy)"))
										continue;
									msg = msg + ("&6" + user + " " + NESS.main.vl.getInt(res + "." + user)
											+ NESS.main.vl.getInt(res + "." + user)) + " ";
								}
								if (msg != "") {
									sender.sendMessage(MSG.color(prefix + "&e"
											+ Bukkit.getOfflinePlayer(UUID.fromString(res)).getName() + " &7" + msg));
									did = true;
								}
							}
						}
						if (!did) {
							MSG.tell(sender, NESS.main.prefix + " There are no violations.");
						}
					} else if (args.length == 2) {
						target = PlayerManager.getPlayer(sender, args[1]);
						if (target != null) {
							if (NESS.main.vl.contains(target.getUniqueId() + "")) {
								String msg = "";
								String res = target.getUniqueId() + "";
								for (String user : NESS.main.vl.getConfigurationSection(res).getKeys(false)) {
									if (user.matches("(category|accuracy)"))
										continue;
									msg = msg + ("&6" + user + " " + NESS.main.vl.getInt(res + "." + user)
											+ NESS.main.vl.getInt(res + "." + user)) + " ";
								}
								sender.sendMessage(MSG.color(prefix + "&e"
										+ Bukkit.getOfflinePlayer(UUID.fromString(res)).getName() + " &7" + msg));
							} else {
								sender.sendMessage(MSG.color(prefix + "That player has no violations!"));
							}
						}
					} else {
						sender.sendMessage(MSG.color(prefix + "/ness vl <player>"));
					}
					break;
				case "ping":
						if(Bukkit.getPlayer(args[2])==null) {
							MSG.tell(sender, NESS.main.prefix + "Player maybe isn't online");
						}
						MSG.tell(sender, NESS.main.prefix + PlayerManager.getPing((Bukkit.getPlayer(args[2]) )) + "");
					break;
				case "report":
					if (!(sender.hasPermission("ness.command.report"))) {
						MSG.noPerm(sender);
						return true;
					}
					if (!(sender instanceof Player)) {
						return true;
					}
					player = (Player) sender;
					if (args.length < 3) {
						MSG.tell(sender, MSG.color(getStringConfig("Messages.ReportCommand")));
						return true;
					}

					target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						MSG.tell(sender, getStringConfig("Messages.ReportUnknowPlayer"));
						return true;
					}
					boolean accept = false;
					String acceptMessage = "", hack = "";
					for (String res : accepted) {
						if (res.replace("_", "").equalsIgnoreCase(args[2])) {
							hack = res.replace("_", " ");
							accept = true;
						}
						acceptMessage = acceptMessage + "&c" + res.replace("_", "") + "&7, ";
					}

					acceptMessage = acceptMessage.substring(0, Math.max(acceptMessage.length() - 4, 0));

					if (!accept) {
						MSG.tell(sender, getStringConfig("Messages.ReportAcceptedHacks").replace("%acceptedhack%",
								acceptMessage));
						return true;
					}

					if (PlayerManager.getInfo("lastReport", player) != null
							&& PlayerManager.timeSince("lastReport", player) <= 3.6e+6
							&& !sender.hasPermission("ness.bypass.reportcooldown")) {
						MSG.tell(sender, getStringConfig("Messages.ReportTooFast"));
						return true;
					}

					if (PlayerManager.getInfo("lastReported", target) != null
							&& PlayerManager.timeSince("lastReported", player) <= 200000
							&& !sender.hasPermission("ness.bypass.reportcooldown")) {
						MSG.tell(sender, getStringConfig("Messages.ReportAlready"));
						return true;
					}
					MSG.tell(sender, getStringConfig("Messages.ReportSuccess").replace("%player%", target.getName())
							.replace("%hack%", hack));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (!p.hasPermission("ness.notify.report"))
							continue;
						MSG.tell(p, getStringConfig("Messages.ReportStaff").replace("%player%", sender.getName())
								.replace("%hack%", hack).replace("%cheater%", target.getName()));
						// "&a" + sender.getName() + " &7reported &e" + target.getName() + "&7 for &c"
						// + hack + "&7."
					}
					int amo = PlayerManager.getVl(target, hack);
					if (amo < 20) {
						WarnHacks.warnHacks((Player) target, hack, 20, -1, 0, "", false);
					} else if (amo < 40) {
						WarnHacks.warnHacks((Player) target, hack, 50, -1, 1, "", false);
					} else if (amo < 100) {
						WarnHacks.warnHacks((Player) target, hack, 100, -1, 1, "", false);
					} else {
						WarnHacks.warnHacks((Player) target, hack, 200, -1, 1, "", false);
					}
					PlayerManager.setInfo("lastReport", player, System.currentTimeMillis());
					PlayerManager.setInfo("lastReported", target, System.currentTimeMillis());
					PlayerManager.addLogMessage(target, "REPORTED BY " + sender.getName() + " for " + hack);
					break;
				case "reload":
					if (!(sender.hasPermission("ness.command.reload"))) {
						MSG.noPerm(sender);
						return true;
					}
					NESS.main.refresh();
					MSG.tell(sender, NESS.main.prefix + " NESS succesfully reloaded.");
					break;
				case "reset":
					if (!(sender.hasPermission("ness.command.reset"))) {
						MSG.noPerm(sender);
						return true;
					}
					NESS.main.saveResource("config.yml", true);
					NESS.main.refresh();
					MSG.tell(sender, NESS.main.prefix + " NESS succesfully reset.");
					break;
				case "warn":
					if (!(sender.hasPermission("ness.command.warn"))) {
						MSG.noPerm(sender);
						return true;
					}
					if (args.length >= 4) {
						hack = "";
						String vl = "", active = "h";
						for (String res : args) {
							if (!res.equals(args[0]) && !res.equals(args[1])) {
								if (res.startsWith("v:")) {
									active = "v";
								} else if (res.startsWith("h:")) {
									active = "h";
								}
								if (active.equals("v")) {
									if (res.startsWith("v:")) {
										vl = vl + res.substring(2);
									} else {
										vl = vl + res;
									}
								} else if (active.equals("h")) {
									if (res.startsWith("h:")) {
										hack = hack + res.substring(2) + " ";
									} else {
										hack = hack + res + " ";
									}
								}
							}
						}
						player = PlayerManager.getPlayer(sender, args[1]);
						if (player != null)
							WarnHacks.warnHacks(player, hack.trim(), Integer.valueOf(vl.trim()), -1, 1, hack.trim(),
									false);
					} else {
						MSG.tell(sender, "/ness warn [player] h:[hacks] v:[vl]");
					}
					break;
				case "loadprotocol":
					if (!(sender.hasPermission("ness.command.loadprotocol"))) {
						MSG.noPerm(sender);
						return true;
					}
					try {
						new Protocols();
						MSG.tell(sender, NESS.main.prefix + " Succesfully enabled ProtocolLib checks.");
					} catch (Exception e) {
						MSG.tell(sender, NESS.main.prefix + " Error enabling ProtocolLib checks.");
					}
					break;
				case "version":
					MSG.tell(sender, "&c&lBuild Version:");
					MSG.tell(sender, "  &6Date &rn".replace("n", NESS.main.build));
					MSG.tell(sender, "  &6Git &rMSWS");
					MSG.tell(sender,
							"  &6Protocol " + MSG.TorF(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null));
					MSG.tell(sender, "  &6Version &r" + NESS.main.ver);
					break;
				case "clear":
					if (!(sender.hasPermission("ness.command.clear"))) {
						MSG.noPerm(sender);
						return true;
					}
					if (args.length >= 3) {
						String name = "";
						for (String res : args)
							if (!res.matches("(?i)(clear|" + args[1] + ")"))
								name = name + res + " ";
						name = name.trim();
						if (args[2].equalsIgnoreCase(args[1]))
							name = args[1];
						if (args[1].equalsIgnoreCase("all")) {
							for (String res : NESS.main.vl.getKeys(false)) {
								OfflinePlayer offtarget = Bukkit.getOfflinePlayer(UUID.fromString(res));
								if (offtarget != null)
									PlayerManager.resetVL(offtarget, MSG.camelCase(name));
							}
							sender.sendMessage(MSG.color(prefix + "Succesfully cleared &eeveryone&7 of &6"
									+ MSG.camelCase(name) + " &7violations."));
							return true;
						}
						target = PlayerManager.getPlayer(sender, args[1]);
						if (target != null) {
							PlayerManager.resetVL(target, MSG.camelCase(name));
							sender.sendMessage(MSG.color(prefix + "Succesfully cleared &e" + target.getName()
									+ "&7 of &6" + MSG.camelCase(name) + " &7violations."));
						}
					} else {
						sender.sendMessage(MSG.color(prefix + "/ness clear [player] [violation|all]"));
					}
					break;
				case "toggle":
					if (!(sender.hasPermission("ness.command.toggle"))) {
						MSG.noPerm(sender);
						return true;
					}
					if (args.length == 2) {
						String name = "", id = "";
						switch (args[1].toLowerCase()) {
						case "cancel":
							name = "Lag Back";
							id = "Cancel";
							break;
						case "dev":
							name = "Developer Mode";
							id = "DeveloperMode";
							break;
						case "everywhere":
						case "global":
							name = "Global";
							id = "Global";
							break;
						case "manual":
						case "debug":
							name = "Debug Mode";
							id = "DebugMode";
							break;
						default:
							return true;
						}
						NESS.main.config.set("Settings." + id, !NESS.main.config.getBoolean("Settings." + id));
						MSG.tell(sender,
								prefix + name + ": " + MSG.TorF(NESS.main.config.getBoolean("Settings." + id)));
					} else {
						sender.sendMessage(MSG.color(prefix + "/ness toggle [cancel|dev|lobby|debug]"));
					}
					NESS.main.devMode = NESS.main.config.getBoolean("Settings.DeveloperMode");
					NESS.main.debugMode = NESS.main.config.getBoolean("Settings.DebugMode");
					NESS.main.saveConfig();
					break;
				}
			} else {
				sendHelp(sender);
			}
			break;
		default:
			return false;
		}
		return true;
	}

	private String getStringConfig(String configline) {
		return ChatColor.translateAlternateColorCodes('&',
				NESS.main.config.getString(configline).replace("%prefix%", NESS.main.prefix));
	}

	public void sendHelp(CommandSender sender) {
		MSG.tell(sender, NESS.main.prefix + " &7NESS &e" + NESS.main.ver
				+ "&7 coded by &4&lMSWS&7 and recoded by &4&lherobrine99dan&7.");
		if (sender.hasPermission("ness.command.version"))
			MSG.tell(sender, "/ness version");
		if (sender.hasPermission("ness.command.report"))
			MSG.tell(sender, "/ness report [Player] [Hack]");
		if (sender.hasPermission("ness.command.vl"))
			MSG.tell(sender, "/ness vl <player>");
		if (sender.hasPermission("ness.command.clear"))
			MSG.tell(sender, "/ness clear [player] [violation|all]");
		if (sender.hasPermission("ness.command.reload"))
			MSG.tell(sender, "/ness reload");
		if (sender.hasPermission("ness.command.toggle"))
			MSG.tell(sender, "/ness toggle <feature>");
	}
}