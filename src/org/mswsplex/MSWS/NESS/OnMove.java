package org.mswsplex.MSWS.NESS;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.MSWS.NESS.checks.Aimbot;
import org.mswsplex.MSWS.NESS.checks.EntitySpeedCheck;
import org.mswsplex.MSWS.NESS.checks.FastStairs;
import org.mswsplex.MSWS.NESS.checks.Fly;
import org.mswsplex.MSWS.NESS.checks.Headless;
import org.mswsplex.MSWS.NESS.checks.Jesus;
import org.mswsplex.MSWS.NESS.checks.NoClip;
import org.mswsplex.MSWS.NESS.checks.Speed;
import org.mswsplex.MSWS.NESS.checks.Sprint;
import org.mswsplex.MSWS.NESS.checks.Strafe;
import org.mswsplex.MSWS.NESS.checks.killaura.KillauraBotCheck;
import org.mswsplex.MSWS.NESS.checks.killaura.PatternKillaura;

public class OnMove implements Listener {
	protected static HashMap<String, Integer> noground = new HashMap<String, Integer>();

	@EventHandler
	public void onMove(final PlayerMoveEvent event) {
		//register check
		final Player player = event.getPlayer();
		NESSPlayer p = NESSPlayer.getInstance(player);
		p.setValuesMovement(event);
		Sprint.Check(event);
		Fly.Check(event);
		Fly.Check1(event);
		Fly.Check2(event);
		Fly.Check3(event);
		Fly.Check4(event);
		Fly.Check5(event);
		Fly.Check6(event);
		Fly.Check7(event);
		Fly.Check8(event);
		Fly.Check9(event);
		Fly.Check10(event);
		Fly.Check11(event);
		Sprint.Check1(event);
		Headless.Check(event);
		Speed.Check(event);
		Speed.Check1(event);
		Speed.Check2(event);
		FastStairs.Check(event);
		//Step.Check(event);
		Jesus.Check(event);
		Jesus.Check2(event);
		NoClip.Check(event);
		Aimbot.Check(event);
		Aimbot.Check1(event);
		Aimbot.Check3(event);
		EntitySpeedCheck.Check(event);
		Strafe.Check(event);
		PatternKillaura.Check1(event);
		//Utility.createChecks(event);
		/*
		 * if(Utility.debug==event.getPlayer().getName()) { Utility.createChecks(event);
		 * }
		 */
        if(Utility.hasflybypass(player)) {return;}
		final Material below = player.getWorld().getBlockAt(player.getLocation().subtract(0.0, 1.0, 0.0)).getType();
		Material bottom = null;
		final Location from = event.getFrom();
		final Location to = event.getTo();
		final Double dist = from.distance(to);
		Double hozDist = dist - (to.getY() - from.getY());
		final Double fallDist = Double.valueOf(Float.valueOf(player.getFallDistance()).toString()).doubleValue();
		if (to.getY() < from.getY()) {
			hozDist = dist - (from.getY() - to.getY());
		}
		final Double vertDist = Math.abs(dist - hozDist);
		double dTG = 0.0;
		NESS.main.legit.put(player, true);
		final boolean groundAround = PlayerManager.groundAround(player.getLocation());
		boolean waterAround = false;
		final int radius = 2;
		boolean ice = false;
		boolean surrounded = true;
		boolean lilypad = false;
		boolean web = false;
		boolean cactus = false;
		final double lastYaw = PlayerManager.getAction("lastYaw", player);
		double yawDiff = Math.abs(lastYaw - player.getLocation().getYaw());
		if (yawDiff > 0.0) {
		}
		PlayerManager.setAction("lastYaw", player, (double) player.getLocation().getYaw());
		for (int x = -1; x <= 1; ++x) {
			for (int z = -1; z <= 1; ++z) {
				int y;
				for (y = 0; !player.getLocation().subtract((double) x, (double) y, (double) z).getBlock().getType()
						.isSolid() && y < 20; ++y) {
				}
				if (y < dTG || dTG == 0.0) {
					dTG = y;
				}
			}
		}
		dTG += player.getLocation().getY() % 1.0;
		bottom = player.getLocation().getWorld().getBlockAt(player.getLocation().subtract(0.0, dTG, 0.0)).getType();
		boolean carpet = false;
		for (int x2 = -1; x2 <= 1; ++x2) {
			for (int z2 = -1; z2 <= 1; ++z2) {
				Material belowSel = player.getWorld()
						.getBlockAt(player.getLocation().add((double) x2, -1.0, (double) z2)).getType();
				Material[] array;
				for (int length = (array = new Material[] { Material.ICE, Material.PACKED_ICE, Material.PISTON_BASE,
						Material.PISTON_STICKY_BASE }).length, i = 0; i < length; ++i) {
					final Material mat = array[i];
					if (belowSel == mat) {
						ice = true;
					}
				}
				belowSel = player.getWorld().getBlockAt(player.getLocation().add((double) x2, -0.01, (double) z2))
						.getType();
				if (belowSel == Material.WATER_LILY) {
					lilypad = true;
				}
				if (belowSel == Material.CARPET || belowSel.toString().toLowerCase().contains("diode")
						|| belowSel.toString().toLowerCase().contains("comparator") || belowSel == Material.SNOW) {
					carpet = true;
				}
				if (belowSel.isSolid()) {
					PlayerManager.setAction("wasGround", player, Double.valueOf(System.currentTimeMillis()));
				}
			}
		}
		for (int x2 = -2; x2 <= 2; ++x2) {
			for (int y = -2; y <= 3; ++y) {
				for (int z3 = -2; z3 <= 2; ++z3) {
					final Material belowSel2 = player.getWorld()
							.getBlockAt(player.getLocation().add((double) x2, (double) y, (double) z3)).getType();
					if (!belowSel2.isSolid()) {
						surrounded = false;
					}
					if (belowSel2 == Material.WEB) {
						web = true;
					}
					if (belowSel2 == Material.CACTUS) {
						cactus = true;
					}
				}
			}
		}
		if (ice) {
			PlayerManager.setAction("wasIce", player, Double.valueOf(System.currentTimeMillis()));
		}
		for (int x2 = -radius; x2 < radius; ++x2) {
			for (int y = -1; y < radius; ++y) {
				for (int z3 = -radius; z3 < radius; ++z3) {
					final Material mat = to.getWorld()
							.getBlockAt(player.getLocation().add((double) x2, (double) y, (double) z3)).getType();
					if (mat.isSolid()) {
						waterAround = true;
					}
				}
			}
		}
		if (NESS.main.debugMode) {
			MSG.tell((CommandSender) player, "&7dist: &e" + dist);
			MSG.tell((CommandSender) player,
					"&7X: &e" + player.getLocation().getX() + " &7V: &e" + player.getVelocity().getX());
			MSG.tell((CommandSender) player,
					"&7Y: &e" + player.getLocation().getY() + " &7V: &e" + player.getVelocity().getY());
			MSG.tell((CommandSender) player,
					"&7Z: &e" + player.getLocation().getZ() + " &7V: &e" + player.getVelocity().getZ());
			MSG.tell((CommandSender) player,
					"&7hozDist: &e" + hozDist + " &7vertDist: &e" + vertDist + " &7fallDist: &e" + fallDist);
			MSG.tell((CommandSender) player,
					"&7below: &e" + MSG.camelCase(below.toString()) + " bottom: " + MSG.camelCase(bottom.toString()));
			MSG.tell((CommandSender) player, "&7dTG: " + dTG);
			MSG.tell((CommandSender) player,
					"&7groundAround: &e" + MSG.TorF(groundAround) + " &7onGround: " + MSG.TorF(player.isOnGround()));
			MSG.tell((CommandSender) player, "&7ice: " + MSG.TorF(ice) + " &7surrounded: " + MSG.TorF(surrounded)
					+ " &7lilypad: " + MSG.TorF(lilypad) + " &7web: " + MSG.TorF(web));
			MSG.tell((CommandSender) player, " &7waterAround: " + MSG.TorF(waterAround));
		}
		if (player.isInsideVehicle() && !groundAround && from.getY() <= to.getY() && (!player.isInsideVehicle()
				|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))) {
			WarnHacks.warnHacks(player, "Flight", 10, -1.0, 24,"Fly",true);
		}
		Double maxSpd = 0.4209;
		Material mat2 = null;
		if (player.isBlocking()) {
			maxSpd = 0.1729;
		}
		if (PlayerManager.getInfo("blocking", (OfflinePlayer) player) != null) {
			if (player.getLocation().getY() % 0.5 == 0.0) {
				maxSpd = 0.2;
			} else {
				maxSpd = 0.3;
			}
		}
		for (int x3 = -1; x3 < 1; ++x3) {
			for (int z4 = -1; z4 < 1; ++z4) {
				mat2 = from.getWorld().getBlockAt(from.getBlockX() + x3, player.getEyeLocation().getBlockY() + 1,
						from.getBlockZ() + z4).getType();
				if (mat2.isSolid()) {
					maxSpd = 0.50602;
					break;
				}
			}
		}
		if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.BOAT) {
			maxSpd = 2.787;
		}
		if (hozDist > maxSpd && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.SPEED)
				&& PlayerManager.timeSince("wasFlight", player) >= 2000.0
				&& PlayerManager.timeSince("isHit", player) >= 2000.0
				&& PlayerManager.timeSince("teleported", player) >= 100.0) {
			if (groundAround) {
				if (PlayerManager.timeSince("wasIce", player) >= 1000.0 && (!player.isInsideVehicle()
						|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))) {
					final Material small = player.getWorld().getBlockAt(player.getLocation().subtract(0.0, 0.1, 0.0))
							.getType();
					if (!player.getWorld().getBlockAt(from).getType().isSolid()
							&& !player.getWorld().getBlockAt(to).getType().isSolid() && small != Material.TRAP_DOOR
							&& small != Material.IRON_TRAPDOOR) {
						if (NESS.main.devMode) {
							MSG.tell((CommandSender) player, "&9Dev> &7Speed amo: " + hozDist);
						}
						if (PlayerManager.getAction("blocks", player) < 2.0) {
							if (player.isBlocking()
									|| PlayerManager.getInfo("blocking", (OfflinePlayer) player) != null) {
								WarnHacks.warnHacks(player, "NoSlowDown", 10, 500.0, 25,"MaxDistance",false);
							} else {
								WarnHacks.warnHacks(player, "Speed", 20, 500.0, 26,"MaxDistance",false);
							}
						}
					}
				}
			} else if (PlayerManager.timeSince("wasIce", player) >= 1000.0
					&& PlayerManager.timeSince("teleported", player) >= 500.0) {
				WarnHacks.warnHacks(player, "Flight", 20, 500.0, 27,"Fly",true);
			}
		}
		if (player.isSneaking() && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.SPEED)
				&& hozDist > 0.2 && NESS.main.oldLoc.containsKey(player)
				&& NESS.main.oldLoc.get(player).getY() == player.getLocation().getY()
				&& PlayerManager.timeSince("wasFlight", player) >= 2000.0
				&& PlayerManager.timeSince("wasIce", player) >= 1000.0
				&& PlayerManager.timeSince("isHit", player) >= 1000.0
				&& PlayerManager.timeSince("teleported", player) >= 500.0
				&& !player.getWorld().getBlockAt(from).getType().isSolid()
				&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
			WarnHacks.warnHacks(player, "Fast Sneak", 20, -1.0, 28,"Timer",false);
		}
		if (to.getY() == from.getY()) {
			if (!groundAround) {
				if (hozDist > 0.35 && PlayerManager.timeSince("wasIce", player) >= 1000.0 && !player.isFlying()) {
					WarnHacks.warnHacks(player, "Flight", 5, -1.0, 29,"Fly",true);
				}
			} else if (!player.isOnGround() && NESS.main.oldLoc.containsKey(player)
					&& NESS.main.oldLoc.get(player).getY() == player.getLocation().getY() && hozDist > 0.35
					&& !player.hasPotionEffect(PotionEffectType.SPEED) && !player.isFlying()
					&& PlayerManager.timeSince("teleported", player) >= 2000.0
					&& PlayerManager.timeSince("isHit", player) >= 1000.0
					&& (!player.isInsideVehicle()
							|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))
					&& !player.getWorld().getBlockAt(from).getType().isSolid()
					&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
				WarnHacks.warnHacks(player, "Flight", 20, -1.0, 30,"Fly",true);
			}
		} else if (groundAround && !player.isFlying() && below == Material.LADDER
				&& player.getWorld().getBlockAt(player.getLocation()).getType() == Material.LADDER
				&& from.getY() < to.getY() && PlayerManager.timeSince("isHit", player) >= 1000.0
				&& PlayerManager.distToBlock(player.getLocation()) >= 3
				&& PlayerManager.timeSince("wasGround", player) >= 2000.0 && vertDist > 0.118 && !player.isSneaking()) {
			WarnHacks.warnHacks(player, "FastLadder", 20, -1.0, 31,"FastClimb",false);
		}
		if (from.getY() % 0.5 != 0.0 && to.getY() % 0.5 != 0.0 && !player.isFlying()) {
			String amo = "";
			Double diff = 1.0;
			if (NESS.main.oldLoc.containsKey(player)) {
				amo = new StringBuilder(String.valueOf(to.getY() - NESS.main.oldLoc.get(player).getY())).toString();
				diff = Math.abs(to.getY() - NESS.main.oldLoc.get(player).getY());
				if (amo.contains("999999") || amo.contains("0000000")
						|| (diff < 0.05 && diff >= 0.0 && !groundAround)) {
					boolean fly = true;
					Material[] array2;
					for (int length2 = (array2 = new Material[] { Material.STATIONARY_WATER, Material.WATER,
							Material.LAVA, Material.STATIONARY_LAVA, Material.CAULDRON, Material.CACTUS,
							Material.CARPET, Material.SNOW, Material.LADDER, Material.CHEST, Material.ENDER_CHEST,
							Material.TRAPPED_CHEST, Material.VINE }).length, j = 0; j < length2; ++j) {
						final Material antMat = array2[j];
						if (player.getWorld().getBlockAt(player.getLocation().add(0.0, 1.0, 0.0)).isLiquid()
								|| player.getWorld().getBlockAt(player.getLocation()).getType() == antMat
								|| below == antMat
								|| player.getWorld().getBlockAt(player.getLocation()).getType().isSolid()) {
							fly = false;
						}
					}
					if (fly && !web && PlayerManager.timeSince("sincePlace", player) >= 1000.0
							&& PlayerManager.timeSince("wasIce", player) >= 1000.0
							&& PlayerManager.timeSince("isHit", player) >= 1000.0 && bottom != Material.SLIME_BLOCK
							&& PlayerManager.timeSince("wasFlight", player) >= 500.0
							&& PlayerManager.timeSince("wasGround", player) >= 1500.0
							&& (!player.isInsideVehicle() || (player.isInsideVehicle()
									&& player.getVehicle().getType() != EntityType.HORSE))) {
						WarnHacks.warnHacks(player, "Flight", 5, 150.0, 32,"Fly",true);
					}
				}
				PlayerManager.setAction("oldFlight", player, to.getY() - NESS.main.oldLoc.get(player).getY());
			}
			if (player.isSneaking() && !player.hasPotionEffect(PotionEffectType.SPEED) && hozDist > 0.2
					&& NESS.main.oldLoc.containsKey(player)
					&& NESS.main.oldLoc.get(player).getY() == player.getLocation().getY()
					&& PlayerManager.timeSince("wasFlight", player) >= 2000.0
					&& PlayerManager.timeSince("wasIce", player) >= 1000.0
					&& PlayerManager.timeSince("isHit", player) >= 1000.0
					&& !player.getWorld().getBlockAt(from).getType().isSolid()
					&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
				WarnHacks.warnHacks(player, "Fast Sneak", 20, -1.0, 33,"Timer",false);
			}
		}
		if (player.getWorld().getHighestBlockAt(player.getLocation()).getLocation()
				.distance(player.getLocation()) <= 0.5 || player.isOnGround()) {
			if (hozDist > 0.6 && !player.hasPotionEffect(PotionEffectType.SPEED) && !player.isFlying()
					&& PlayerManager.timeSince("wasFlight", player) >= 3000.0 && NESS.main.oldLoc.containsKey(player)
					&& NESS.main.oldLoc.get(player).getY() < to.getY() + 2.0
					&& PlayerManager.timeSince("isHit", player) >= 2000.0
					&& PlayerManager.timeSince("wasIce", player) >= 1000.0) {
				if (NESS.main.devMode) {
					MSG.tell((CommandSender) player, "&9Dev> &7Speed amo: " + hozDist);
				}
				WarnHacks.warnHacks(player, "Speed", 10, 400.0, 34,"MaxDistance",false);
			}
		} else if (from.getY() == to.getY() && groundAround && player.isOnGround() && hozDist > 0.6
				&& !player.hasPotionEffect(PotionEffectType.SPEED) && !player.isFlying()) {
			WarnHacks.warnHacks(player, "Speed", 30, -1.0, 35,"MaxDistance",false);
		}
		if (player.getLocation().getYaw() > 360.0f || player.getLocation().getYaw() < -360.0f
				|| player.getLocation().getPitch() > 90.0f || player.getLocation().getPitch() < -90.0f) {
			WarnHacks.warnHacks(player, "Illegal Movement", 500, -1.0, 36,"Impossible",false);
		}
		if (dist == 0.0 && !groundAround && !web && !player.isFlying()
				&& PlayerManager.getAction("placeTicks", player) == 0.0 && bottom != Material.SLIME_BLOCK
				&& bottom != Material.VINE && !cactus && PlayerManager.timeSince("isHit", player) >= 500.0) {
			WarnHacks.warnHacks(player, "Flight", 10, 300.0, 37,"Fly",true);
		}
		yawDiff = Math.abs(from.getPitch() - to.getPitch());
		if (yawDiff > 30.0) {
			PlayerManager.setAction("extremeYaw", player, Double.valueOf(System.currentTimeMillis()));
		}
		if ((!player.isSneaking() || below != Material.LADDER) && !player.isFlying() && !player.isOnGround()
				&& player.getLocation().getY() % 1.0 == 0.0 && PlayerManager.timeSince("lastJoin", player) >= 1000.0
				&& PlayerManager.timeSince("teleported", player) >= 500.0 && !below.toString().contains("stairs")) {
			int failed = noground.getOrDefault(player.getName(), 0);
			noground.put(player.getName(), failed + 1);
			if (failed > 2) {
				// MSG.tell((CommandSender)player, "&7NoGround: &e" + failed);
				if (!below.equals(Material.SLIME_BLOCK)) {
					WarnHacks.warnHacks(player, "NoGround", 10, 300.0, 38,"AntiGround",true);
				}
			}
		}
		if (to.getY() != from.getY()) {
			if (from.getY() < to.getY()) {
				maxSpd = 1.52;
			} else {
				maxSpd = 10.0;
			}
			if (!groundAround && !player.isFlying()) {
				if (dist > maxSpd && !player.hasPotionEffect(PotionEffectType.JUMP) && !player.isFlying()
						&& PlayerManager.timeSince("isHit", player) >= 2000.0 && bottom != Material.SLIME_BLOCK) {
					WarnHacks.warnHacks(player, "Flight", 5, -1.0, 39,"Fly",true);
				}
				if (from.getY() >= to.getY()) {
					final double vel = from.getY() - to.getY();
					if (!web && ((vel > 0.0799 && vel < 0.08) || (vel > 0.01 && vel < 0.02)
							|| (vel > 0.549 && vel < 0.55)) && !player.isFlying()
							&& PlayerManager.timeSince("wasFlight", player) >= 3000.0
							&& PlayerManager.timeSince("isHit", player) >= 1000.0) {
						WarnHacks.warnHacks(player, "Flight", 5, -1.0, 40,"Fly",true);
					}
					if (vel > 0.0999 && vel < 0.1 && to.getY() > 0.0) {
						WarnHacks.warnHacks(player, "Glide", 5, -1.0, 41,"Fly",true);
					}
					if (vel == 0.125) {
						WarnHacks.warnHacks(player, "Glide", 5, -1.0, 42,"Fly",true);
					}
				} else if (hozDist == 0.0 && !player.hasPotionEffect(PotionEffectType.JUMP)
						&& PlayerManager.timeSince("wasFlight", player) >= 3000.0
						&& PlayerManager.timeSince("sincePlace", player) >= 1000.0 && bottom != Material.SLIME_BLOCK
						&& !cactus && PlayerManager.timeSince("isHit", player) >= 500.0) {
					WarnHacks.warnHacks(player, "Flight", 10, -1.0, 54,"Fly",true);
				}
			} else {
				Label_4877: {
					if (to.getY() - from.getY() > 0.6 && !player.isFlying() && groundAround
							&& !player.hasPotionEffect(PotionEffectType.JUMP)
							&& PlayerManager.timeSince("wasFlight", player) >= 100.0
							&& bottom != Material.SLIME_BLOCK) {
						for (final Entity ent : player.getNearbyEntities(2.0, 2.0, 2.0)) {
							if (ent instanceof Boat) {
								break Label_4877;
							}
						}
						if(!player.getNearbyEntities(5, 5, 5).isEmpty()) {
						  WarnHacks.warnHacks(player, "Step", 10 * (int) (to.getY() - from.getY()), -1.0, 43,"Vanilla",true);
						}			
					}
				}
				if (from.getY() - to.getY() > 1.0 && fallDist == 0.0 && from.getY() - to.getY() > 2.0) {
					WarnHacks.warnHacks(player, "Phase", 50, -1.0, 44,"SkipBlock",false);
				}
			}
			if (from.getY() - to.getY() > 0.3 && fallDist <= 0.4 && below != Material.STATIONARY_WATER
					&& !player.getLocation().getBlock().isLiquid()) {
				if (hozDist < 0.1 || !groundAround) {
					if (groundAround && hozDist > 0.05 && PlayerManager.timeSince("isHit", player) >= 1000.0) {
						if (!player.isInsideVehicle()
								|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE)) {
							WarnHacks.warnHacks(player, "Speed", 10, -1.0, 45,"MaxDistance",false);
						}
					} else if (PlayerManager.timeSince("breakTime", player) >= 2000.0
							&& PlayerManager.timeSince("teleported", player) >= 500.0 && below != Material.PISTON_BASE
							&& below != Material.PISTON_STICKY_BASE
							&& (!player.isInsideVehicle()
									|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))
							&& !player.isFlying() && to.getY() > 0.0 && bottom != Material.SLIME_BLOCK) {
						NESSPlayer np = NESSPlayer.getInstance(player);
						double ydist = player.getLocation().getY()-np.getOnGroundLocation().getY();
						double falldistance = Math.round(Math.abs(ydist));
						if(NESS.main.devMode) {
							player.sendMessage(" falldistance: "+ falldistance);
						}
						WarnHacks.warnHacks(player, "NoFall", 20, -1.0, 46,"NoGround",true);
						if(!(player.getHealth()-falldistance/2<0)) {
							player.setHealth(player.getHealth()-falldistance/2);	
						}
					}
				} else if (bottom != Material.SLIME_BLOCK && (!player.isInsideVehicle()
						|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE
								&& PlayerManager.timeSince("isHit", player) >= 1000.0))&&!Utility.hasBlock(player, Material.SLIME_BLOCK)) {
					WarnHacks.warnHacks(player, "Speed", 25, -1.0, 47,"LowHop",false);
				}
			}
			if (from.getY() - to.getY() > 0.3 && below != Material.STATIONARY_WATER
					&& !player.getLocation().getBlock().isLiquid()) {
				Double[] array3;
				for (int length3 = (array3 = new Double[] { 0.3959395, 0.8152412, 0.4751395,
						0.5317675 }).length, k = 0; k < length3; ++k) {
					final Double amo2 = array3[k];
					if (Math.abs(fallDist - amo2) < 0.01 && !web && groundAround && below.isSolid()
							&& PlayerManager.timeSince("sincePlace", player) >= 1000.0
							&& PlayerManager.timeSince("isHit", player) >= 1000.0&&!Utility.hasBlock(player, Material.SLIME_BLOCK)) {
						WarnHacks.warnHacks(player, "Speed", 25, -1.0, 48,"SmallHop",false);
					}
				}
				boolean flag = true;
				if (fallDist > 1.0 || PlayerManager.timeSince("wasFlight", player) <= 500.0) {
					flag = false;
				} else {
					Double[] array4;
					for (int length4 = (array4 = new Double[] { 0.7684762,
							0.46415937 }).length, l = 0; l < length4; ++l) {
						final Double amo3 = array4[l];
						if (fallDist - amo3 < 0.01) {
							flag = false;
						}
					}
				}
				if (flag && PlayerManager.timeSince("isHit", player) >= 1000.0 && !player.isFlying()
						&& PlayerManager.timeSince("sincePlace", player) >= 1000.0 && below != Material.LADDER
						&& PlayerManager.timeSince("isHit", player) >= 1000.0 && !web&&!Utility.hasBlock(player, Material.SLIME_BLOCK)) {
					if(!Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_LAVA)&&!Utility.SpecificBlockNear(player.getLocation(), Material.WATER)&&!Utility.SpecificBlockNear(player.getLocation(), Material.LAVA)&&!!Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_WATER)) {
				          return;
						}
					WarnHacks.warnHacks(player, "Speed", 5, -1.0, 49,"LowHop",false);
				}
			}
			if (to.getY() > from.getY()) {
				final double lastDTG = PlayerManager.getAction("lastDTG", player);
				final String diff2 = new StringBuilder(String.valueOf(Math.abs(dTG - lastDTG))).toString();
				if (player.getLocation().getY() % 0.5 != 0.0 && !player.isFlying() && !below.isSolid()
						&& (new StringBuilder(String.valueOf(dTG)).toString().contains("99999999")
								|| new StringBuilder(String.valueOf(dTG)).toString().contains("00000000")
								|| diff2.contains("000000") || diff2.startsWith("0.286"))
						&& PlayerManager.timeSince("isHit", player) >= 500.0
						&& !below.toString().toLowerCase().contains("water")
						&& !below.toString().toLowerCase().contains("lava")) {
					if(!Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_LAVA)&&!Utility.SpecificBlockNear(player.getLocation(), Material.WATER)&&!Utility.SpecificBlockNear(player.getLocation(), Material.LAVA)&&!Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_WATER)) {
						WarnHacks.warnHacks(player, "Spider", 20, -1.0, 50,"FastSpeedY",true);	
					}
					if (NESS.main.devMode) {
						MSG.tell((CommandSender) player, "&9Dev> &7dTG: " + dTG + " diff: " + diff2);
					}
				}
				PlayerManager.setInfo("lastDTG", (OfflinePlayer) player, dTG);
			}
		} else {
			if (!groundAround && hozDist > 0.32 && vertDist == 0.0 && !player.isFlying()
					&& PlayerManager.timeSince("sincePlace", player) >= 1000.0
					&& PlayerManager.timeSince("wasIce", player) >= 1000.0) {
				WarnHacks.warnHacks(player, "Flight", 5, -1.0, 51,"Fly",true);
			}
			if (player.getLocation().getY() % 0.5 != 0.0 && !player.isFlying()
					&& PlayerManager.timeSince("wasGround", player) > 1000.0
					&& PlayerManager.timeSince("sincePlace", player) >= 1500.0
					&& !bottom.toString().toLowerCase().contains("fence")
					&& !bottom.toString().toLowerCase().contains("wall") && !web && !carpet
					&& !below.toString().toLowerCase().contains("diode")
					&& !below.toString().toLowerCase().contains("comparator") && below != Material.SNOW && !lilypad) {
				WarnHacks.warnHacks(player, "Flight", 5, 100, 52,"Fly",true);
			}
		}
		if (player.getWorld().getBlockAt(player.getLocation()).getType() == Material.WEB && dist > 0.140
				&& !player.isFlying() && !player.hasPotionEffect(PotionEffectType.SPEED)) {
			WarnHacks.warnHacks(player, "NoWeb", (int) Math.round(dist * 20.0), -1.0, 53,"MaxDistance",false);
		}
		PlayerManager.addAction("moveTicks", player);
		if (NESS.main.legit.get(player) && below.isSolid()) {
			NESS.main.safeLoc.put(player, player.getLocation());
		}
		PlayerManager.setAction("oldYaw", player, (double) player.getLocation().getYaw());
	}
}
