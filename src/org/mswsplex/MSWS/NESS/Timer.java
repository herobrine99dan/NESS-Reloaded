package org.mswsplex.MSWS.NESS;

import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.MSWS.NESS.checks.BadPackets;
import org.mswsplex.MSWS.NESS.checks.Freecam;
import org.mswsplex.MSWS.NESS.checks.Speed;
import org.mswsplex.MSWS.NESS.checks.Sprint;
import org.mswsplex.MSWS.NESS.checks.Strafe;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class Timer {
	static HashMap<Player, Location> timerLoc;

	static {
		Timer.timerLoc = new HashMap<Player, Location>();
	}

	public void register() {
		new BukkitRunnable() {
			public void run() {
				for (final Player player : Bukkit.getOnlinePlayers()) {
	                if(Utility.hasflybypass(player)) {return;}
					if (NESS.main.vl.contains(new StringBuilder().append(player.getUniqueId()).toString())) {
						for (final String res : NESS.main.vl
								.getConfigurationSection(new StringBuilder().append(player.getUniqueId()).toString())
								.getKeys(false)) {
							if (!res.matches("(category|accuracy)")) {
								PlayerManager.addVL(player, res, -5);
							}
						}
					}
					if (PlayerManager.getAction("bowShots", player) > 25.0) {
						if (NESS.main.devMode) {
							MSG.tell((CommandSender) player,
									"&9Dev> &7Fast Bow Amount: " + PlayerManager.getAction("bowShots", player));
						}
						WarnHacks.warnHacks(player, "Fast Bow",
								(int) (50.0 * (PlayerManager.getAction("bowShots", player) - 25.0)), -1.0, 67,
								"Vanilla", false);
					}
					PlayerManager.removeAction("bowShots", player);
					PlayerManager.removeAction("blocks", player);
				}
			}
		}.runTaskTimer((Plugin) NESS.main, 0L, 100L);
		new BukkitRunnable() {
			public void run() {
				for (final Player player : Bukkit.getOnlinePlayers()) {
					NESS.main.vl.set(player.getUniqueId() + ".accuracy", (Object) null);
				}
			}
		}.runTaskTimer((Plugin) NESS.main, 0L, 200L);
		new BukkitRunnable() {
			public void run() {
				boolean groundAround = false;
				for (final Player player : Bukkit.getOnlinePlayers()) {
	                if(Utility.hasflybypass(player)) {return;}
	                NESSPlayer p = NESSPlayer.getInstance(player);
	                p.setDistance(0);
					if (OnAttack.fightingPlayers.contains(player.getUniqueId())) {
						OnAttack.fightingPlayers.remove(player.getUniqueId());
					}
					OnMove.noground.put(player.getName(), 0);
					Sprint.sprintcheck.put(player.getName(), 0);
					Sprint.speed.put(player.getName(), 0);
					groundAround = PlayerManager.groundAround(player.getLocation());
					if (PlayerManager.getAction("shiftTicks", player) > 20.0) {
						WarnHacks.warnHacks(player, "AutoSneak", 100, -1.0, 68, "SneakTimer", false);
					}
					if (PlayerManager.getAction("packets", player) > 10.0
							|| PlayerManager.getAction("timerTick", player) > 400.0) {
						player.kickPlayer(
								NESS.main.config.contains("PacketMessage") ? NESS.main.config.getString("PacketMessage")
										: "You are sending too many packets! [NESS]");
						PlayerManager.removeAction("packets", player);
						PlayerManager.removeAction("timerTick", player);
					}
					if (NESS.main.oldLoc.containsKey(player) && !groundAround) {
						Material bottom = null;
						Integer dTG = 0;
						boolean web = false;
						boolean cactus = false;
						for (int x = -2; x <= 2; ++x) {
							for (int y = -2; y <= 3; ++y) {
								for (int z = -2; z <= 2; ++z) {
									final Material belowSel = player.getWorld()
											.getBlockAt(player.getLocation().add((double) x, (double) y, (double) z))
											.getType();
									if (belowSel.name().toLowerCase().contains("web") || belowSel.name().toLowerCase().contains("cobweb")) {
										web = true;
									}
									if (belowSel == Material.CACTUS) {
										cactus = true;
									}
								}
							}
						}
						while (!player.getLocation().getWorld()
								.getBlockAt(player.getLocation().subtract(0.0, (double) dTG, 0.0)).getType().isSolid()
								&& dTG < 20) {
							++dTG;
						}
						bottom = player.getLocation().getWorld()
								.getBlockAt(player.getLocation().subtract(0.0, (double) dTG, 0.0)).getType();
						if (NESS.main.oldLoc.get(player).equals((Object) player.getLocation()) && !player.isFlying()
								&& PlayerManager.timeSince("wasFlight", player) >= 3000.0
								&& !NESS.main.oldLoc.get(player).equals((Object) player.getLocation())) {
			                if(Utility.hasflybypass(player)) {return;}
							WarnHacks.warnHacks(player, "Flight", 5, 50.0, 69, "FlyTimer", false);
						}
						if (Math.abs(NESS.main.oldLoc.get(player).getX()) - Math.abs(player.getLocation().getX())
								+ (Math.abs(NESS.main.oldLoc.get(player).getZ())
										- Math.abs(player.getLocation().getZ())) == 0.0) {
							final double vert = Math.abs(NESS.main.oldLoc.get(player).getY())
									- Math.abs(player.getLocation().getY());
							if (NESS.main.oldLoc.get(player).getY() < player.getLocation().getY() && !web && vert < 1.0
									&& !player.hasPotionEffect(PotionEffectType.JUMP) && !player.isFlying()
									&& PlayerManager.timeSince("wasFlight", player) >= 3000.0
									&& PlayerManager.getAction("placeTicks", player) == 0.0
									&& bottom != Material.SLIME_BLOCK && !cactus
									&& PlayerManager.timeSince("isHit", player) >= 500.0) {
				                if(Utility.hasflybypass(player)) {return;}
								WarnHacks.warnHacks(player, "Flight", 5, -1.0, 70, "FlyTimer", false);
							}
						}
						if (!web && !PlayerManager.groundAround(NESS.main.oldLoc.get(player)) && !player.isFlying()
								&& player.getLocation().getY() - NESS.main.oldLoc.get(player).getY() > -2.0
								&& PlayerManager.timeSince("wasFlight", player) >= 3000.0
								&& PlayerManager.timeSince("isHit", player) >= 2000.0
								&& PlayerManager.getAction("placeTicks", player) == 0.0
								&& !player.hasPotionEffect(PotionEffectType.JUMP)
								&& !NESS.main.oldLoc.get(player).equals((Object) player.getLocation())
								&& bottom != Material.SLIME_BLOCK) {
			                if(Utility.hasflybypass(player)) {return;}
							WarnHacks.warnHacks(player, "Flight", 5, -1.0, 71, "FlyTimer", false);
						}
					}
					NESS.main.oldLoc.put(player, player.getLocation());
					NESS.main.lastHitLoc.remove(player);
					NESS.main.lastLookLoc.remove(player);
					PlayerManager.removeAction("shiftTicks", player);
					boolean flag = true;
					if (PlayerManager.getAction("oldFire", player) > 100.0 && player.getFireTicks() == -20
							&& PlayerManager.timeSince("teleported", player) >= 1000.0
							&& player.getGameMode() != GameMode.CREATIVE) {
						for (int x2 = -1; x2 <= 1; ++x2) {
							for (int z2 = -1; z2 <= 1; ++z2) {
								if (player.getWorld()
										.getBlockAt(player.getLocation().add((double) x2, 0.0, (double) z2))
										.isLiquid()) {
									flag = false;
								}
							}
						}
						if (flag) {
							WarnHacks.warnHacks(player, "AntiFire", 50, -1.0, 72, "Vanilla", false);
						}
					}
					PlayerManager.setAction("oldFire", player, (double) player.getFireTicks());
				}
			}
		}.runTaskTimer((Plugin) NESS.main, 0L, 10L);
		new BukkitRunnable() {
			public void run() {
				for (final Player player : Bukkit.getOnlinePlayers()) {
	                if(Utility.hasflybypass(player)) {return;}
	                Strafe.strafe.put(player.getName(), 0);
	                Speed.speed.put(player.getName(), 0);
					if (PlayerManager.getAction("clicks", player) >= 15.0) {
						if (NESS.main.devMode) {
							MSG.tell((CommandSender) player, "&9Dev> &7" + PlayerManager.getAction("clicks", player));
						}
						WarnHacks.warnHacks(player, "High CPS", 20, -1.0, 79, "Simple", false);
					}
					if ((PlayerManager.getAction("regenTicks", player) >= 4.5
							&& !player.hasPotionEffect(PotionEffectType.SATURATION)
							&& !player.hasPotionEffect(PotionEffectType.HEAL)
							&& !player.hasPotionEffect(PotionEffectType.HEALTH_BOOST))
							|| PlayerManager.getAction("regenTicks", player) >= 7.5) {
						WarnHacks.warnHacks(player, "Regen", 50, -1.0, 73, "Vanilla", false);
					}
					if (PlayerManager.getAction("chatMessage", player) >= 10.0) {
						WarnHacks.warnHacks(player, "Spambot",
								(int) (PlayerManager.getAction("chatMessage", player) * 5.0), -1.0, 74, "Spammer",
								false);
					}
					if (Timer.timerLoc.containsKey(player) && PlayerManager.timeSince("wasFlight", player) >= 5000.0
							&& PlayerManager.timeSince("wasIce", player) >= 5000.0
							&& player.getLocation().getWorld().equals(Timer.timerLoc.get(player).getWorld())) {
						final Double dist = Timer.timerLoc.get(player).distance(player.getLocation());
						final Double hozDist = dist
								- Math.abs(player.getLocation().getY() - Timer.timerLoc.get(player).getY());
						if (hozDist > 8.0 && !player.isFlying() && !player.isInsideVehicle()) {
			                if(Utility.hasflybypass(player)) {return;}
							WarnHacks.warnHacks(player, "Timer", Math.min(100, (int) (hozDist - 7.1) * 10), 400.0, 75,
									"Packet", false);
							if (NESS.main.devMode) {
								MSG.tell((CommandSender) player, "&9Dev> &7amo: "
										+ PlayerManager.getAction("moveTicks", player) + " dist: " + hozDist);
							}
						}
					}
					if (PlayerManager.getAction("timerTick", player) >= 30.0) {
						PlayerManager.addAction("tooManyTicks", player);
						if (NESS.main.devMode) {
							MSG.tell((CommandSender) player,
									"&9Dev> &7Too many ticks: " + PlayerManager.getAction("timerTick", player));
						}
					}
					if (PlayerManager.getAction("tooManyTicks", player) >= 3.0) {
		                if(Utility.hasflybypass(player)) {return;}
						WarnHacks.warnHacks(player, "Timer", 50, 500.0, 76, "Packet", false);
						PlayerManager.removeAction("tooManyTicks", player);
					}
					if (PlayerManager.getAction("skinPackets", player) >= 5.0
							&& PlayerManager.timeSince("lastMove", player) <= 500.0) {
						WarnHacks.warnHacks(player, "SkinBlinker",
								(int) (5.0 * (PlayerManager.getAction("skinPackets", player) + 10.0)), -1.0, 77,
								"Packet", false);
					}
					Timer.timerLoc.put(player, player.getLocation());
					PlayerManager.removeAction("clicks", player);
					PlayerManager.removeAction("timerTick", player);
					PlayerManager.removeAction("regenTicks", player);
					PlayerManager.removeAction("foodTicks", player);
					PlayerManager.removeAction("chatMessage", player);
					PlayerManager.removeAction("placeTicks", player);
					PlayerManager.removeAction("vPlaceTicks", player);
					PlayerManager.removeAction("moveTicks", player);
					PlayerManager.removeAction("skinPackets", player);
				}
				final NESS main = NESS.main;
				++main.seconds;
			}
		}.runTaskTimer((Plugin) NESS.main, 0L, 20L);
		new BukkitRunnable() {
			public void run() {
				for (final Player p : Bukkit.getOnlinePlayers()) {
					NESSPlayer player = NESSPlayer.getInstance(p);
					player.resetOnMoveRepeat();
					player.SetPacketsNumber(0);
					player.SetDrops(0);
					player.SetClicks(0);
					player.SetBlockPlace(0);
					Freecam.Check(p);
				}
			}
		}.runTaskTimer((Plugin) NESS.main, 0L, 10L);
		new BukkitRunnable() {
			public void run() {
				for (final Player player : Bukkit.getOnlinePlayers()) {
	                if(Utility.hasflybypass(player)) {return;}
					if ((PlayerManager.getAction("foodTicks", player) >= 2.0
							&& !player.hasPotionEffect(PotionEffectType.SATURATION)
							&& !player.hasPotionEffect(PotionEffectType.HUNGER))
							|| PlayerManager.getAction("foodTicks", player) >= 5.0) {
						WarnHacks.warnHacks(player, "FastEat", 50, -1.0, 78, "Packet", false);
					}
					Freecam.Check(player);
				}
			}
		}.runTaskTimer((Plugin) NESS.main, 0L, 40L);
		new BukkitRunnable() {
			public void run() {
				for (final Player p : Bukkit.getOnlinePlayers()) {
					BadPackets.repeats.put(p.getName(), 0);
				}
			}
		}.runTaskTimer(NESS.main, 0, 50L);

	}
}
