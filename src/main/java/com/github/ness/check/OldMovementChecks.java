package com.github.ness.check;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.MSG;
import com.github.ness.utility.PlayerManager;
import com.github.ness.utility.Utilities;
import com.github.ness.utility.Utility;

public class OldMovementChecks extends AbstractCheck<PlayerMoveEvent> {

	public static HashMap<String, Integer> noground = new HashMap<>();

	HashMap<Player, Location> oldLoc = new HashMap<>();
	public static HashMap<String, Boolean> blockPackets = new HashMap<>();

	public OldMovementChecks(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	private void punish(PlayerMoveEvent e, String cheat) {
		NessPlayer nessPlayer = manager.getPlayer(e.getPlayer());
		if(nessPlayer.isTeleported()) {
			return;
		}
		if(nessPlayer.shouldCancel(e, cheat)) {
			e.setCancelled(true);
		}
	}

	@Override
	void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Material below = player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).getType();
		Material bottom = null;
		if(!event.getTo().getWorld().getName().equals(event.getFrom().getWorld().getName())) {
			return;
		}
		boolean devMode = false;
		boolean debugMode = false;
		Location from = event.getFrom(), to = event.getTo();
		Double dist = from.distance(to);
		Double hozDist = dist - (to.getY() - from.getY());
		Double fallDist = (double) player.getFallDistance();
		NessPlayer nessPlayer = this.manager.getPlayer(player);
		if (Utility.hasflybypass(player) || player.getAllowFlight() || Utility.hasVehicleNear(player, 4)
				|| nessPlayer.isTeleported()) {
			return;
		}
		if (blockPackets.getOrDefault(player.getName(), false)) {
			event.setCancelled(true);
			return;
		}
		if (to.getY() < from.getY())
			hozDist = dist - (from.getY() - to.getY());
		Double vertDist = Math.abs(dist - hozDist);
		double dTG = 0; // Distance to ground
		boolean groundAround = PlayerManager.groundAround(player.getLocation()), waterAround = false;
		int radius = 2;
		boolean ice = false, surrounded = true, lilypad = false, web = false, cactus = false;

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				int y = 0;
				while (!player.getLocation().subtract(x, y, z).getBlock().getType().isSolid() && y < 20) {
					y++;
				}
				if (y < dTG || dTG == 0)
					dTG = y;
			}
		}
		dTG += player.getLocation().getY() % 1;
		bottom = player.getLocation().getWorld().getBlockAt(player.getLocation().subtract(0, dTG, 0)).getType();
		boolean carpet = false;
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Material belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, -1, z)).getType();
				for (Material mat : new Material[] { Material.ICE, Material.PACKED_ICE, Material.PISTON_BASE,
						Material.PISTON_STICKY_BASE }) {
					if (belowSel == mat)
						ice = true;
				}
				belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, -.01, z)).getType();
				if (belowSel == Material.WATER_LILY)
					lilypad = true;
				if (belowSel == Material.CARPET || belowSel.toString().toLowerCase().contains("diode")
						|| belowSel.toString().toLowerCase().contains("comparator") || belowSel == Material.SNOW)
					carpet = true;
				if (belowSel.isSolid()) {
					nessPlayer.updateLastWasOnGround();
				}
			}
		}
		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 3; y++) {
				for (int z = -2; z <= 2; z++) {
					Material belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, y, z)).getType();
					if (!belowSel.isSolid())
						surrounded = false;
					if (belowSel == Material.WEB) {
						web = true;
					}
					if (belowSel == Material.CACTUS)
						cactus = true;
				}
			}
		}
		if (ice) {
			nessPlayer.updateLastWasOnIce();
		}
		for (int x = -radius; x < radius; x++) {
			for (int y = -1; y < radius; y++) {
				for (int z = -radius; z < radius; z++) {
					Block b = to.getWorld().getBlockAt(player.getLocation().add(x, y, z));
					if (b.isLiquid())
						waterAround = true;
				}
			}
		}
		if ((below == Material.WATER || below == Material.STATIONARY_WATER || below == Material.LAVA
				|| below == Material.STATIONARY_LAVA) && !player.isFlying()) {
			if (!waterAround && !lilypad
					&& !player.getWorld().getBlockAt(player.getLocation().add(0, 1, 0)).isLiquid()) {
				if ((Math.abs(from.getY() - to.getY()) + "").contains("00000000") || to.getY() == from.getY()) {
					punish(event, "Jesus");
					manager.getPlayer(player).setViolation(new Violation("Jesus", "Physics(OnMove)"));
				}
			}
		}

		if (debugMode) {
			MSG.tell(player, "&7dist: &e" + dist);
			MSG.tell(player, "&7X: &e" + player.getLocation().getX() + " &7V: &e" + player.getVelocity().getX());
			MSG.tell(player, "&7Y: &e" + player.getLocation().getY() + " &7V: &e" + player.getVelocity().getY());
			MSG.tell(player, "&7Z: &e" + player.getLocation().getZ() + " &7V: &e" + player.getVelocity().getZ());
			MSG.tell(player, "&7hozDist: &e" + hozDist + " &7vertDist: &e" + vertDist + " &7fallDist: &e" + fallDist);
			MSG.tell(player,
					"&7below: &e" + MSG.camelCase(below.toString()) + " bottom: " + MSG.camelCase(bottom.toString()));
			MSG.tell(player, "&7dTG: " + dTG);
			MSG.tell(player,
					"&7groundAround: &e" + MSG.torF(groundAround) + " &7onGround: " + MSG.torF(player.isOnGround()));
			MSG.tell(player, "&7ice: " + MSG.torF(ice) + " &7surrounded: " + MSG.torF(surrounded) + " &7lilypad: "
					+ MSG.torF(lilypad) + " &7web: " + MSG.torF(web));
			MSG.tell(player, " &7waterAround: " + MSG.torF(waterAround));
		}

		if (surrounded && (hozDist > .2 || to.getBlockY() < from.getBlockY())) {
			punish(event, "NoClip");
			manager.getPlayer(player).setViolation(new Violation("NoClip", "(OnMove)"));
		}
		if (player.isInsideVehicle()) {
			if (!groundAround && from.getY() <= to.getY()) {
				if (!player.isInsideVehicle()
						|| player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE)
					punish(event, "Fly");
				manager.getPlayer(player).setViolation(new Violation("Fly", "HighDistance(OnMove)"));
			}
		}
		// SPEED/FLIGHT CHECK
		Double maxSpd = 0.4209;
		Material mat = null;
		if (player.isBlocking())
			maxSpd = .1729;
		if (player.isBlocking()) {
			if (player.getLocation().getY() % .5 == 0.0) {
				maxSpd = .2;
			} else {
				maxSpd = .3;
			}
		}
		for (int x = -1; x < 1; x++) {
			for (int z = -1; z < 1; z++) {
				mat = from.getWorld()
						.getBlockAt(from.getBlockX() + x, player.getEyeLocation().getBlockY() + 1, from.getBlockZ() + z)
						.getType();
				if (mat.isSolid()) {
					maxSpd = 0.50602;
					break;
				}
			}
		}
		if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.BOAT)
			maxSpd = 2.787;
		if (hozDist > maxSpd && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.SPEED)
				&& PlayerManager.timeSince("wasFlight", player) >= 2000
				&& PlayerManager.timeSince("isHit", player) >= 2000
				&& PlayerManager.timeSince("teleported", player) >= 100) {
			if (groundAround) {
				if (nessPlayer.getTimeSinceLastWasOnIce() >= 1000) {
					if (!player.isInsideVehicle()
							|| player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE) {
						Material small = player.getWorld().getBlockAt(player.getLocation().subtract(0, .1, 0))
								.getType();
						if (!player.getWorld().getBlockAt(from).getType().isSolid()
								&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
							if (small != Material.TRAP_DOOR && small != Material.IRON_TRAPDOOR) {
								if (devMode)
									MSG.tell(player, "&9Dev> &7Speed amo: " + hozDist);
								if (player.isBlocking()) {
									punish(event, "NoSlowDown");
									manager.getPlayer(player)
											.setViolation(new Violation("NoSlowDown", "HighDistance(OnMove)"));
								} else {
									punish(event, "Speed");
									manager.getPlayer(player)
											.setViolation(new Violation("Speed", "MaxDistance(OnMove)"));
								}
							}
						}
					}
				}
			} else if (nessPlayer.getTimeSinceLastWasOnIce() >= 1000
					&& PlayerManager.timeSince("teleported", player) >= 500) {
				punish(event, "Fly");
				manager.getPlayer(player).setViolation(new Violation("Fly", "InvalidDistance(OnMove)"));
			}
		}
		if (player.isSneaking() && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.SPEED)) {
			if (hozDist > .2 && oldLoc.containsKey(player) && oldLoc.get(player).getY() == player.getLocation().getY()
					&& PlayerManager.timeSince("wasFlight", player) >= 2000
					&& nessPlayer.getTimeSinceLastWasOnIce() >= 1000
					&& PlayerManager.timeSince("isHit", player) >= 1000
					&& PlayerManager.timeSince("teleported", player) >= 500) {
				if (!player.getWorld().getBlockAt(from).getType().isSolid()
						&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
					punish(event, "FastSneak");
					manager.getPlayer(player).setViolation(new Violation("FastSneak", "(OnMove)"));
				}
			}
		}
		if (to.getY() == from.getY()) {
			if (!groundAround) {
				if (hozDist > .35 && nessPlayer.getTimeSinceLastWasOnIce() >= 1000) {
					if (!player.isFlying()) {
						punish(event, "Fly");
						manager.getPlayer(player)
								.setViolation(new Violation("Fly", "InvalidDistance(NoGround OnMove)"));
					}
				}
			} else {
				if (!player.isOnGround()) {
					if (oldLoc.containsKey(player)) {
						if (oldLoc.get(player).getY() == player.getLocation().getY()) {
							if (hozDist > .35 && !player.hasPotionEffect(PotionEffectType.SPEED) && !player.isFlying())
								if (PlayerManager.timeSince("teleported", player) >= 2000
										&& PlayerManager.timeSince("isHit", player) >= 1000
										&& PlayerManager.timeSince("wasFlight", player) >= 2000) {
									if (!player.isInsideVehicle() || player.isInsideVehicle()
											&& player.getVehicle().getType() != EntityType.HORSE) {
										if (!player.getWorld().getBlockAt(from).getType().isSolid()
												&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
											punish(event, "Fly");
											manager.getPlayer(player)
													.setViolation(new Violation("Fly", "InvalidDistancenoHit(OnMove)"));
										}
									}
								}
						}
					}
				}
			}
		} else {
			if (groundAround && !player.isFlying() && below == Material.LADDER
					&& player.getWorld().getBlockAt(player.getLocation()).getType() == Material.LADDER) {
				if (from.getY() < to.getY() && PlayerManager.timeSince("isHit", player) >= 1000
						&& PlayerManager.distToBlock(player.getLocation()) >= 3
						&& nessPlayer.getTimeSinceLastWasOnGround() >= 2000) {
					if (vertDist > .118 && !player.isSneaking()) {
						punish(event, "FastLadder");
						manager.getPlayer(player).setViolation(new Violation("FastLadder", "(OnMove)"));
					}
				}
			}
		}
		if (from.getY() % .5 != 0 && to.getY() % .5 != 0 && !player.isFlying()) {
			String amo = "";
			Double diff = 1.0;
			if (oldLoc.containsKey(player)) {
				amo = to.getY() - oldLoc.get(player).getY() + "";
				diff = Math.abs(to.getY() - oldLoc.get(player).getY());
				if (amo.contains("999999") || amo.contains("0000000")
						|| ((diff < 0.05 && diff >= 0)) && !groundAround) {
					boolean fly = true;
					for (Material antMat : new Material[] { Material.STATIONARY_WATER, Material.WATER, Material.LAVA,
							Material.STATIONARY_LAVA, Material.CAULDRON, Material.CACTUS, Material.CARPET,
							Material.SNOW, Material.LADDER, Material.CHEST, Material.ENDER_CHEST,
							Material.TRAPPED_CHEST, Material.VINE }) {
						if (player.getWorld().getBlockAt(player.getLocation().add(0, 1, 0)).isLiquid()
								|| player.getWorld().getBlockAt(player.getLocation()).getType() == antMat
								|| below == antMat
								|| player.getWorld().getBlockAt(player.getLocation()).getType().isSolid()) {
							fly = false;
						}
					}
					if (fly && !web && PlayerManager.timeSince("sincePlace", player) >= 1000
							&& nessPlayer.getTimeSinceLastWasOnIce() >= 1000
							&& PlayerManager.timeSince("isHit", player) >= 1000 && bottom != Material.SLIME_BLOCK
							&& PlayerManager.timeSince("wasFlight", player) >= 500
							&& nessPlayer.getTimeSinceLastWasOnGround() >= 1500) {
						if (!player.isInsideVehicle()
								|| player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE)
							punish(event, "Fly");
						manager.getPlayer(player).setViolation(new Violation("Fly", "InvalidDistance1(OnMove)"));
					}
				}
			}
			if (player.isSneaking() && !player.hasPotionEffect(PotionEffectType.SPEED)) {
				if (hozDist > .2 && oldLoc.containsKey(player)
						&& oldLoc.get(player).getY() == player.getLocation().getY()
						&& PlayerManager.timeSince("wasFlight", player) >= 2000
						&& nessPlayer.getTimeSinceLastWasOnIce() >= 1000
						&& PlayerManager.timeSince("isHit", player) >= 1000) {
					if (!player.getWorld().getBlockAt(from).getType().isSolid()
							&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
						punish(event, "FastSneak");
						manager.getPlayer(player).setViolation(new Violation("FastSneak", "(OnMove)"));
					}
				}
			}
		}
		if (player.getWorld().getHighestBlockAt(player.getLocation()).getLocation().distance(player.getLocation()) <= .5
				|| player.isOnGround()) {
			if (hozDist > .6 && !player.hasPotionEffect(PotionEffectType.SPEED) && !player.isFlying()
					&& PlayerManager.timeSince("wasFlight", player) >= 3000) {
				if (oldLoc.containsKey(player)) {
					if (oldLoc.get(player).getY() < to.getY() + 2 && PlayerManager.timeSince("isHit", player) >= 2000) {
						if (nessPlayer.getTimeSinceLastWasOnIce() >= 1000) {
							if (devMode)
								MSG.tell(player, "&9Dev> &7Speed amo: " + hozDist);
							punish(event, "Speed");
							manager.getPlayer(player).setViolation(new Violation("Speed", "(OnMove)"));
						}
					}
				}
			}
		} else {
			if (from.getY() == to.getY() && groundAround && player.isOnGround()) {
				if (hozDist > .6 && !player.hasPotionEffect(PotionEffectType.SPEED) && !player.isFlying()) {
					punish(event, "Speed");
					manager.getPlayer(player).setViolation(new Violation("Speed", "(OnMove)"));
				}
			}
		}
		if (player.getLocation().getYaw() > 360 || player.getLocation().getYaw() < -360
				|| player.getLocation().getPitch() > 90 || player.getLocation().getPitch() < -90) {
			punish(event, "IllegalMovement");
			manager.getPlayer(player).setViolation(new Violation("IllegalMovement", "(OnMove)"));
		}
		if (dist == 0) {
			if (!groundAround && !web && !player.isFlying() && bottom != Material.SLIME_BLOCK && bottom != Material.VINE
					&& !cactus && PlayerManager.timeSince("isHit", player) >= 500) {
				punish(event, "Fly");
				manager.getPlayer(player).setViolation(new Violation("Fly", "NoDist(OnMove)"));
			}
		} // Changing isOnGround method, check in server side
		if (!(player.isSneaking() && below == Material.LADDER) && !player.isFlying() && !player.isOnGround()
				&& to.getY() % 1.0 == 0 && PlayerManager.timeSince("lastJoin", player) >= 1000
				&& PlayerManager.timeSince("teleported", player) >= 5000
				&& !below.toString().toLowerCase().contains("stairs") && below != Material.SLIME_BLOCK) {
			if (!Utilities.getPlayerUnderBlock(player).getType().name().toLowerCase().contains("ice")
					&& !Utilities.getPlayerUpperBlock(player).getType().isSolid()) {
				int failed = ((Integer) noground.getOrDefault(player.getName(), Integer.valueOf(0))).intValue();
				noground.put(player.getName(), Integer.valueOf(failed + 1));
				if (failed > 3) {
					if (!below.equals(Material.SLIME_BLOCK)) {
						punish(event, "NoGround");
						manager.getPlayer(player).setViolation(new Violation("NoGround", "(OnMove)"));
					}
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
						&& PlayerManager.timeSince("isHit", player) >= 2000 && bottom != Material.SLIME_BLOCK) {
					punish(event, "Fly");
					manager.getPlayer(player).setViolation(new Violation("Fly", "NoGround(OnMove)"));
				}
				if (from.getY() >= to.getY()) {
					double vel = from.getY() - to.getY();
					if (!web && ((vel > 0.0799 && vel < 0.08) || (vel > .01 && vel < .02) || (vel > .549 && vel < .55))
							&& !player.isFlying() && PlayerManager.timeSince("wasFlight", player) >= 3000
							&& PlayerManager.timeSince("isHit", player) >= 1000) {
						punish(event, "Fly");
						manager.getPlayer(player).setViolation(new Violation("Fly", "InvalidDistance2(OnMove)"));
					}
					if ((vel > 0.0999 && vel < 0.1) && to.getY() > 0) {
						punish(event, "Fly");
						manager.getPlayer(player).setViolation(new Violation("Fly", "InvalidDistance3(OnMove)"));
					}
					if (vel == .125) {
						punish(event, "Fly");
						manager.getPlayer(player).setViolation(new Violation("Fly", "InvalidDistance4(OnMove)"));
					}
				} else {
					if (hozDist == 0 && !player.hasPotionEffect(PotionEffectType.JUMP)
							&& PlayerManager.timeSince("wasFlight", player) >= 3000
							&& PlayerManager.timeSince("sincePlace", player) >= 1000 && bottom != Material.SLIME_BLOCK
							&& !cactus && PlayerManager.timeSince("isHit", player) >= 500) {
						punish(event, "Fly");
						manager.getPlayer(player).setViolation(new Violation("Fly", "InvalidDistance5(OnMove)"));
					}
				}
			} else {
				step: if (to.getY() - from.getY() > .6 && !player.isFlying() && groundAround
						&& !player.hasPotionEffect(PotionEffectType.JUMP)
						&& PlayerManager.timeSince("wasFlight", player) >= 100 && bottom != Material.SLIME_BLOCK) {
					for (Entity ent : player.getNearbyEntities(2, 2, 2)) {
						if (ent instanceof Boat)
							break step;
					}
					punish(event, "Step");
					manager.getPlayer(player).setViolation(new Violation("Step", "(OnMove)"));
				}
				if (from.getY() - to.getY() > 1 && fallDist == 0) {
					if (from.getY() - to.getY() > 2) {
						punish(event, "Phase");
						manager.getPlayer(player).setViolation(new Violation("Phase", "(OnMove)"));
					}
				}
			}
			if (from.getY() - to.getY() > .3 && fallDist <= .4 && below != Material.STATIONARY_WATER
					&& !player.getLocation().getBlock().isLiquid()) {
				if (hozDist < .1 || !groundAround) {
					if (groundAround && hozDist > .05 && PlayerManager.timeSince("isHit", player) >= 1000) {
						if (!player.isInsideVehicle()
								|| player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE)
							punish(event, "Speed");
						manager.getPlayer(player).setViolation(new Violation("Speed", "(OnMove)"));
					} else if (PlayerManager.timeSince("breakTime", player) >= 2000
							&& PlayerManager.timeSince("teleported", player) >= 500 && below != Material.PISTON_BASE
							&& below != Material.PISTON_STICKY_BASE) {
						if ((!player.isInsideVehicle()
								|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))
								&& !player.isFlying() && to.getY() > 0) {
							if (bottom != Material.SLIME_BLOCK)
								punish(event, "NoFall");
							manager.getPlayer(player).setViolation(new Violation("NoFall", "(OnMove)"));
						}
					}
				} else if (bottom != Material.SLIME_BLOCK) {
					if (!player.isInsideVehicle()
							|| player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE
									&& PlayerManager.timeSince("isHit", player) >= 1000)
						punish(event, "Speed");
					manager.getPlayer(player).setViolation(new Violation("Speed", "BunnyHop (OnMove)"));
				}
			}
			if (from.getY() - to.getY() > 0.3 && below != Material.STATIONARY_WATER
					&& !player.getLocation().getBlock().isLiquid()) {
				for (Double amo : new Double[] { .3959395, .8152412, .4751395, .5317675 }) {
					if (Math.abs(fallDist - amo) < .01 && !web) {
						if (groundAround && below.isSolid() && PlayerManager.timeSince("sincePlace", player) >= 1000
								&& PlayerManager.timeSince("isHit", player) >= 1000)
							punish(event, "Speed");
						manager.getPlayer(player).setViolation(new Violation("Speed", "BunnyHop (OnMove)"));
					}
				}
				/*
				 * boolean flag = true; if (fallDist > 1 || PlayerManager.timeSince("wasFlight",
				 * player) <= 500) { flag = false; } else { for (Double amo : new Double[] {
				 * .7684762, .46415937 }) { if ((fallDist - amo) < .01) { flag = false; } } }
				 * 
				 * if (to.getY() > from.getY()) { double lastDTG =
				 * PlayerManager.getAction("lastDTG", player); String diff = Math.abs(dTG -
				 * lastDTG) + ""; if (player.getLocation().getY() % .5 != 0 &&
				 * !player.isFlying() && !below.isSolid() && (((dTG + "").contains("99999999")
				 * || (dTG + "").contains("00000000")) || diff.contains("000000") ||
				 * diff.startsWith("0.286")) && PlayerManager.timeSince("isHit", player) >= 500
				 * && !below.toString().toLowerCase().contains("water") &&
				 * !below.toString().toLowerCase().contains("lava")) { punish(event, "Spider");
				 * manager.getPlayer(player).setViolation(new Violation("Spider", "(OnMove)"));
				 * if (devMode) MSG.tell(player, "&9Dev> &7dTG: " + dTG + " diff: " + diff); } }
				 */
			} else {
				if (!groundAround && hozDist > .32 && vertDist == 0 && !player.isFlying()
						&& PlayerManager.timeSince("sincePlace", player) >= 1000
						&& nessPlayer.getTimeSinceLastWasOnIce() >= 1000)
					manager.getPlayer(player).setViolation(new Violation("Fly", "InvalidDistance6(OnMove)"));
				// Block rightBelow = player.getLocation().subtract(0, .1, 0).getBlock();
			}
			if (player.getWorld().getBlockAt(player.getLocation()).getType() == Material.WEB) {
				if (dist > .2 && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.SPEED))
					punish(event, "NoWeb");
				manager.getPlayer(player).setViolation(new Violation("NoWeb", "(OnMove)"));
			}
			if (below.isSolid() && Utility.isOnGround(from)) {
				this.manager.getPlayer(player).safeLoc = from;
			}
		}
	}
}
