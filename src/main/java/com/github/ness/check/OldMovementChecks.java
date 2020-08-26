package com.github.ness.check;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import com.github.ness.utility.Utility;

public class OldMovementChecks extends AbstractCheck<PlayerMoveEvent> {

	public OldMovementChecks(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerMoveEvent.class, 1, TimeUnit.SECONDS));
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		player.noGround = 0;
	}

	private void punish(PlayerMoveEvent e, String cheat, String module) {
		NessPlayer nessPlayer = manager.getPlayer(e.getPlayer());
		if (nessPlayer.isTeleported()) {
			return;
		}
		nessPlayer.setViolation(new Violation(cheat, module), e);
	}

	@Override
	void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		Material below = player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).getType();
		Material bottom = null;
		NessPlayer nessPlayer = this.manager.getPlayer(player);
		final boolean devMode = nessPlayer.isDevMode();
		final boolean debugMode = nessPlayer.isDebugMode();
		Location from = event.getFrom(), to = event.getTo();
		Double dist = from.distance(to);
		Double hozDist = dist - (to.getY() - from.getY());
		Double fallDist = (double) player.getFallDistance();
		if (Utility.hasflybypass(player) || player.getAllowFlight() || Utility.hasVehicleNear(player, 4)
				|| nessPlayer.isTeleported()) {
			return;
		}
		if (to.getY() < from.getY())
			hozDist = dist - (from.getY() - to.getY());
		Double vertDist = Math.abs(dist - hozDist);
		double dTG = 0; // Distance to ground
		boolean groundAround = Utility.groundAround(player.getLocation()), waterAround = false;
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
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Material belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, -1, z)).getType();
				if (belowSel.name().toLowerCase().contains("piston") || belowSel.name().toLowerCase().contains("ice")) {
					ice = true;
				}
				belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, -.01, z)).getType();
				if (belowSel.name().toLowerCase().contains("lily"))
					lilypad = true;
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
					if (belowSel.name().toLowerCase().contains("web")) {
						web = true;
					}
					if (belowSel.name().toLowerCase().contains("cactus"))
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

		if (debugMode) {
			MSG.tell(player, "&7dist: &e" + dist);
			MSG.tell(player, "&7X: &e" + player.getLocation().getX() + " &7V: &e" + player.getVelocity().getX());
			MSG.tell(player, "&7Y: &e" + player.getLocation().getY() + " &7V: &e" + player.getVelocity().getY());
			MSG.tell(player, "&7Z: &e" + player.getLocation().getZ() + " &7V: &e" + player.getVelocity().getZ());
			MSG.tell(player, "&7hozDist: &e" + hozDist + " &7vertDist: &e" + vertDist + " &7fallDist: &e" + fallDist);
			MSG.tell(player,
					"&7below: &e" + Utility.getMaterialName(below) + " bottom: " + Utility.getMaterialName(bottom));
			MSG.tell(player, "&7dTG: " + dTG);
			MSG.tell(player,
					"&7groundAround: &e" + MSG.torF(groundAround) + " &7onGround: " + MSG.torF(player.isOnGround()));
			MSG.tell(player, "&7ice: " + MSG.torF(ice) + " &7surrounded: " + MSG.torF(surrounded) + " &7lilypad: "
					+ MSG.torF(lilypad) + " &7web: " + MSG.torF(web));
			MSG.tell(player, " &7waterAround: " + MSG.torF(waterAround));
		}

		if (surrounded && (hozDist > .2 || to.getBlockY() < from.getBlockY())) {
			punish(event, "NoClip", "(OnMove)");
		}
		if (player.isInsideVehicle()) {
			if (!groundAround && from.getY() <= to.getY()) {
				if (!player.isInsideVehicle()
						|| player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE)
					punish(event, "Fly", "HighDistance(OnMove)");
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
							if (!small.name().toLowerCase().contains("trap")) {
								if (devMode)
									MSG.tell(player, "&9Dev> &7Speed amo: " + hozDist);
								if (player.isBlocking()) {
									punish(event, "NoSlowDown", "HighDistance(OnMove)");
								} else {
									punish(event, "Speed", "MaxDistance(OnMove)");
								}
							}
						}
					}
				}
			}
		}
		if (from.getY() == to.getY() && groundAround && player.isOnGround()) {
			if (hozDist > .6 && !player.hasPotionEffect(PotionEffectType.SPEED) && !player.isFlying()) {
				punish(event, "Speed", "(OnMove)");
			}
		}
		if (player.getLocation().getYaw() > 360 || player.getLocation().getYaw() < -360
				|| player.getLocation().getPitch() > 90 || player.getLocation().getPitch() < -90) {
			punish(event, "IllegalMovement", "(OnMove)");
		} // Changing isOnGround method, check in server side
		if (!(player.isSneaking() && below.name().toLowerCase().contains("ladder")) && !player.isFlying()
				&& !player.isOnGround() && to.getY() % 1.0 == 0 && PlayerManager.timeSince("lastJoin", player) >= 1000
				&& PlayerManager.timeSince("teleported", player) >= 5000
				&& !below.toString().toLowerCase().contains("stairs")
				&& !below.toString().toLowerCase().contains("slime")) {
			if (!Utility.getPlayerUnderBlock(player).getType().name().toLowerCase().contains("ice")
					&& !Utility.getPlayerUpperBlock(player).getType().isSolid()) {
				int failed = nessPlayer.noGround++;
				if (failed > 3) {
					if (!below.name().toLowerCase().contains("slime")) {
						punish(event, "NoGround", "(OnMove)");
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
						&& PlayerManager.timeSince("isHit", player) >= 2000
						&& !bottom.name().toLowerCase().contains("slime")) {
					punish(event, "Fly", "NoGround(OnMove)");
				}
				if (from.getY() >= to.getY()) {
					double vel = from.getY() - to.getY();
					if ((vel > 0.0999 && vel < 0.1) && to.getY() > 0) {
						punish(event, "Fly", "InvalidDistance3(OnMove)");
					}
					if (vel == .125) {
						punish(event, "Fly", "InvalidDistance4(OnMove)");
					}
				} else {
					if (hozDist == 0 && !player.hasPotionEffect(PotionEffectType.JUMP)
							&& PlayerManager.timeSince("wasFlight", player) >= 3000
							&& PlayerManager.timeSince("sincePlace", player) >= 1000
							&& bottom.name().toLowerCase().contains("slime") && !cactus
							&& PlayerManager.timeSince("isHit", player) >= 500) {
						punish(event, "Fly", "InvalidDistance5(OnMove)");
					}
				}
			} else {
				step: if (to.getY() - from.getY() > .6 && !player.isFlying() && groundAround
						&& !player.hasPotionEffect(PotionEffectType.JUMP)
						&& PlayerManager.timeSince("wasFlight", player) >= 100
						&& !bottom.name().toLowerCase().contains("slime")) {
					for (Entity ent : player.getNearbyEntities(2, 2, 2)) {
						if (ent instanceof Boat)
							break step;
					}
					punish(event, "Step", "(OnMove)");
				}
				if (from.getY() - to.getY() > 1 && fallDist == 0) {
					if (from.getY() - to.getY() > 2) {
						punish(event, "Phase", "(OnMove)");
					}
				}
			}
			if (from.getY() - to.getY() > .3 && fallDist <= .4 && !below.name().toLowerCase().contains("water")
					&& !player.getLocation().getBlock().isLiquid()) {
				if (hozDist < .2 || !groundAround) {
					if (groundAround && hozDist > .05 && PlayerManager.timeSince("isHit", player) >= 1000) {
						if (!player.isInsideVehicle()
								|| player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE)
							punish(event, "Speed", "(OnMove)");
					} else if (PlayerManager.timeSince("breakTime", player) >= 2000
							&& PlayerManager.timeSince("teleported", player) >= 500
							&& !below.name().toLowerCase().contains("piston")) {
						if ((!player.isInsideVehicle()
								|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))
								&& !player.isFlying() && to.getY() > 0) {
							if (!bottom.name().toLowerCase().contains("slime") && !Utility.hasBlock(player, "water")
									&& !Utility.isInWater(player) && !Utility.specificBlockNear(event.getTo(), "liquid")
									&& !Utility.specificBlockNear(event.getTo(), "fire")
									&& !Utility.getMaterialName(event.getTo()).contains("fire") && !Utility
											.getMaterialName(event.getTo().clone().add(0, 0.4, 0)).contains("fire")) {
								punish(event, "NoFall", "(OnMove)");
								player.damage(
										Math.abs(Utility.calcDamage((3.5 * player.getVelocity().getY()) / -0.71)));
							}
						}
					}
				} else if (!bottom.name().toLowerCase().contains("slime")) {
					if (!player.isInsideVehicle()
							|| player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE
									&& PlayerManager.timeSince("isHit", player) >= 1000)
						punish(event, "Speed", "BunnyHop (OnMove)");
				}
			}
			if (from.getY() - to.getY() > 0.3 && !below.name().toLowerCase().contains("water")
					&& !player.getLocation().getBlock().isLiquid()) {
				for (Double amo : new Double[] { .3959395, .8152412, .4751395, .5317675 }) {
					if (Math.abs(fallDist - amo) < .01 && !web) {
						if (groundAround && below.isSolid() && PlayerManager.timeSince("sincePlace", player) >= 1000
								&& PlayerManager.timeSince("isHit", player) >= 1000)
							punish(event, "Speed", "BunnyHop (OnMove)");
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
					this.punish(event, "Fly", "InvalidDistance6(OnMove)");
				// Block rightBelow = player.getLocation().subtract(0, .1, 0).getBlock();
			}
			if (below.isSolid() && groundAround) {
				// this.manager.getPlayer(player).safeLoc = from; //TODO Make a good LagBack
				// System
			}
		}
	}
}
