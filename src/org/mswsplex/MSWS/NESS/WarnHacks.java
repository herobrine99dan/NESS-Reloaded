package org.mswsplex.MSWS.NESS;

import com.google.common.io.ByteArrayDataOutput;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.api.PlayerViolationEvent;
import org.mswsplex.MSWS.NESS.discord.DiscordHackWarn;

import java.util.HashMap;

public class WarnHacks {
	static HashMap<Player, String> notify;

	static {
		WarnHacks.notify = new HashMap<Player, String>();
	}

	@SuppressWarnings("deprecation")
	public static void warnHacks(final Player hacker, final String hack, final int level, final double maxPing,
			final int identifier, String module, Boolean teleport) {
		NESS.main.legit.put(hacker, false);
		final int currentLine = Thread.currentThread().getStackTrace()[2].getLineNumber();
		if (hacker.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		if (NESS.main.config.getStringList("DisabledWorlds").contains(hacker.getWorld().getName())) {
			return;
		}
		if (hacker.isInsideVehicle()) {
			EntityType vehicleType = hacker.getVehicle().getType();
			if (vehicleType == EntityType.ENDER_PEARL || vehicleType == EntityType.HORSE || vehicleType.name().equals("DONKEY)")) {
				return;
			}
		}
		if (hacker.isDead()) {
			return;
		}
		if (hacker.hasPermission("ness.bypass." + hack.replace(" ", "")) || hacker.hasPermission("ness.bypass.*")) {
			return;
		}
		if (PlayerManager.timeSince("lastJoin", hacker) <= 3000.0) {
			return;
		}
		if (NESS.main.config.getBoolean("Settings.Cancel") && NESS.main.safeLoc.containsKey(hacker)
				&& Math.random() < NESS.main.config.getDouble("Configuration.LagPossibility") / 100.0) {
			if (teleport == null) {

			} else if (!teleport) {
				hacker.teleport((Location) NESS.main.safeLoc.get(hacker));
			} else {
				DragDown.PlayerDragDown(hacker);
			}
		}
		for (final String res : NESS.main.config.getStringList("DisabledChecks")) {
			if (String.valueOf(currentLine).toString().equals(res) || hack.equals(res)) {
				return;
			}
		}
		if (NESS.main.config.getIntegerList("DisabledChecks").contains(currentLine)) {
			return;
		}
		PlayerViolationEvent event = new PlayerViolationEvent(hacker.getName(), hack,
				PlayerManager.getVl((OfflinePlayer) hacker, hack), module); // Initialize your Event
		Bukkit.getPluginManager().callEvent(event); // This fires the event and allows any listener to listen to the
													// event
		if (event.isCancelled()) {
			return;
		}
		if (PlayerManager.getPing(hacker) < maxPing || maxPing == -1.0) {
			String add = String.valueOf(hack) + ": " + currentLine + " VL: "
					+ PlayerManager.getVl((OfflinePlayer) hacker, hack) + " TIME: %time% X: "
					+ parseDecimal(hacker.getLocation().getX(), 2) + " Y: "
					+ parseDecimal(hacker.getLocation().getY(), 2) + " Z: "
					+ parseDecimal(hacker.getLocation().getZ(), 2);
			String add2 = "";
			if (hacker.getItemInHand() != null && hacker.getItemInHand().getType() != Material.AIR) {
				add = String.valueOf(add) + " Hand: " + MSG.camelCase(hacker.getItemInHand().getType().toString());
			}
			add = String.valueOf(add) + " ";
			String[] array;
			for (int length = (array = new String[] { "tooManyTicks", "clicks", "timerTick", "regenTicks", "placeTicks",
					"moveTicks", "oldFire", "shiftTicks", "wasFlight", "isHit",
					"packets" }).length, j = 0; j < length; ++j) {
				final String res2 = array[j];
				if (PlayerManager.getAction(res2, hacker) != 0.0 && PlayerManager.getAction(res2, hacker) != -20.0) {
					add2 = String.valueOf(add2) + res2 + ": " + PlayerManager.getAction(res2, hacker) + " ";
				} else if (PlayerManager.getInfo(res2, (OfflinePlayer) hacker) != null) {
					if (!(PlayerManager.getInfo(res2, (OfflinePlayer) hacker) instanceof Double)
							|| (double) PlayerManager.getInfo(res2, (OfflinePlayer) hacker) != -20.0) {
						add2 = String.valueOf(add2) + res2 + ": " + PlayerManager.getInfo(res2, (OfflinePlayer) hacker)
								+ " ";
					}
				}
			}
			if (!add2.isEmpty()) {
				PlayerManager.addLogMessage((OfflinePlayer) hacker, add2.trim());
				PlayerManager.addLogMessage((OfflinePlayer) hacker, "");
			}
			PlayerManager.addLogMessage((OfflinePlayer) hacker, add.trim());
			if (NESS.main.devMode) {
				for (final Player player : Bukkit.getOnlinePlayers()) {
					if (player.hasPermission("ness.notify.developer")) {
						final String fromClass = new Exception().getStackTrace()[1].getClassName();
						String className = "";
						for (int i = 0; i < fromClass.length(); ++i) {
							if (fromClass.substring(fromClass.length() - i, fromClass.length()).startsWith(".")) {
								className = fromClass.substring(fromClass.length() - i + 1, fromClass.length());
								break;
							}
						}
						MSG.tell((CommandSender) player,
								"&9Dev> &7" + hacker.getDisplayName() + ": &e" + currentLine + " &7("
										+ PlayerManager.getVl((OfflinePlayer) hacker, hack) + hack + "&7) [&e"
										+ className + "&7]" + ",&e " + identifier + " Module: " + module);
					}
				}
			}
			if (PlayerManager.getVl((OfflinePlayer) hacker, hack) >= NESS.main.config
					.getInt("Configuration.MinimumVL")) {
				final String oldCol = PlayerManager.getVl((OfflinePlayer) hacker, hack) + "";
				final String newCol = (PlayerManager.getVl((OfflinePlayer) hacker, hack) + level) + "";
				final String oldHack = WarnHacks.notify.get(hacker);
				final String newHack = hack;
				if (!WarnHacks.notify.containsKey(hacker)
						|| (WarnHacks.notify.containsKey(hacker) && ((!oldCol.equals(newCol) && oldHack.equals(newHack))
								|| PlayerManager.timeSince("hacks", hacker) >= 2000.0))) {
					for (final Player target : Bukkit.getOnlinePlayers()) {
						if (target.hasPermission("ness.notify.hacks")) {
							final String msg = NESS.main.config.getString("Messages.WarnHacks")
									.replace("%player%", hacker.getDisplayName()).replace("%hack%", hack)
									.replace("%prefix%", NESS.main.prefix).replace("%module%", module)
									.replace("%world%", hacker.getWorld().getName()).replace("%vl%",
											new StringBuilder(String
													.valueOf(PlayerManager.getVl((OfflinePlayer) hacker, hack) + level))
															.toString());
							MSG.tell((CommandSender) target, msg);
							DiscordHackWarn.WebHookSender(hacker, hack, level, identifier, module);
							final ByteArrayDataOutput out = ByteStreams.newDataOutput();
							out.writeUTF("Forward");
							out.writeUTF("ALL");
							out.writeUTF("NESS");
							final ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
							final DataOutputStream msgout = new DataOutputStream(msgbytes);
							try {
								msgout.writeUTF(msg);
								msgout.writeShort(123);
							} catch (IOException e) {
								e.printStackTrace();
							}
							out.writeShort(msgbytes.toByteArray().length);
							out.write(msgbytes.toByteArray());
						}
					}
					PlayerManager.setAction("hacks", hacker, Double.valueOf(System.currentTimeMillis()));
					WarnHacks.notify.put(hacker, hack);
				}
			}
			PlayerManager.addVL(hacker, hack, level);
			if (PlayerManager.getVl((OfflinePlayer) hacker, hack) >= NESS.main.config
					.getInt("Configuration.VLForInstakick")) {
				if (NESS.main.devMode) {
					for (final Player target2 : Bukkit.getOnlinePlayers()) {
						if (target2.hasPermission("ness.notify.title")) {
							target2.sendTitle(MSG.color("&4&lINSTANT"),
									MSG.color("&e" + hacker.getDisplayName() + " &7for " + hack));
						}
					}
					WarnHacks.notify.put(hacker, PlayerManager.getVl((OfflinePlayer) hacker, hack) + "");
				}
				PlayerManager.resetVL((OfflinePlayer) hacker, hack);
				hacker.kickPlayer(ChatColor.translateAlternateColorCodes('&',
						NESS.main.config.getString("Messages.KickMessage").replace("%prefix%", NESS.main.prefix)));
			}
		}
	}

	public static String parseDecimal(final double d, final int length) {
		String name = new StringBuilder(String.valueOf(d)).toString();
		if (name.contains(".") && name.split("\\.")[1].length() > 2) {
			name = String.valueOf(name.split("\\.")[0]) + "."
					+ name.split("\\.")[1].substring(0, Math.min(name.split("\\.")[1].length(), length));
		}
		return name;
	}
}