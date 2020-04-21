package org.mswsplex.MSWS.NESS.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.NESSPlayer;
import org.mswsplex.MSWS.NESS.PlayerManager;
import org.mswsplex.MSWS.NESS.Utilities;
import org.mswsplex.MSWS.NESS.Utility;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class Fly {
	protected static List<String> bypasses = Arrays.asList("slab", "stair", "snow", "bed", "skull", "step", "slime");
	protected static Map<UUID, Long> flyTicksA = new HashMap<>();
	protected static List<String> checks = Arrays.asList("0.343", "0.345", "0.341", "0.352", "0.294");

	public static void Check(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (bypass(event.getPlayer()) || Utility.hasBlock(event.getPlayer(), Material.SLIME_BLOCK)) {
			return;
		}
		if(PlayerManager.hasPermissionBypass(player, "Fly")) {
			return;
		}
		NESSPlayer np = NESSPlayer.getInstance(player);
		if (!event.getPlayer().isOnGround()) {
			double fallDist = event.getPlayer().getFallDistance();
			if (event.getPlayer().getVelocity().getY() < -1.0D && fallDist == 0.0D) {
					if(player.getHealth()>1) {
						player.setHealth(player.getHealth() - 1);
					}
				WarnHacks.warnHacks(event.getPlayer(), "Fly", 5, -1.0D, 17, "NoVelocity", true);
			}
		}
	}

	public static void Check1(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (bypass(event.getPlayer()) || p.isFlying() || p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		if (Utilities.isClimbableBlock(p.getLocation().getBlock()) && !Utilities.isInWater(p)) {
			double distance = Utility.around(event.getTo().getY() - event.getFrom().getY(), 6);
			// p.sendMessage("Distance= " + distance);
			if (distance > 0.120) {
				if (distance == 0.164 || distance == 0.248 || distance == 0.333 || distance == 0.419) {
					return;
				} else {
					WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "FastClimb", false);
				}
			}
		}
	}

	public static void Check2(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (bypass(event.getPlayer())) {
			return;
		}
		if (p.getVelocity().getY() < -1.0D && p.isOnGround() && !Utility.hasBlock(p, Material.SLIME_BLOCK)) {
			WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "OnGround", true);
		}
	}

	public static void Check3(PlayerMoveEvent event) {
		int airBuffer = 0;
		double lastYOffset = 0;
		Player p = event.getPlayer();
		if (bypass(event.getPlayer())) {
			return;
		}
		double deltaY = event.getFrom().getY() - event.getTo().getY();
		if ((deltaY > 1.0D && p.getFallDistance() < 1.0F)
				|| deltaY > 3.0D && !Utility.hasBlock(p, Material.SLIME_BLOCK)) {
			WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "AirCheck", true);
		} else {

			if (p.getFallDistance() >= 1.0F) {
				airBuffer = 10;
			}

			if (airBuffer > 0) {
				airBuffer--;

				return;
			}

			Location playerLoc = p.getLocation();
			double change = Math.abs(deltaY - lastYOffset);

			float maxChange = 0.8F;

			if (Utility.isInAir(p) && playerLoc.getBlock().getType() == Material.AIR && change > maxChange
					&& change != 0.5D && !Utility.hasBlock(p, Material.SLIME_BLOCK)) {
				WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "AirCheck", true);
			}

			lastYOffset = deltaY;
		}
	}

	public static void Check4(PlayerMoveEvent e) {
		Location from = e.getFrom();
		Location to = e.getTo();
		double fromy = e.getFrom().getY();
		double toy = e.getTo().getY();
		Player p = e.getPlayer();
		double defaultvalue = 0.08307781780646906;
		double defaultjump = 0.41999998688697815;
		double distance = (toy - fromy);
		if (bypass(e.getPlayer()) || from.getBlock().getType().isSolid() || to.getBlock().getType().isSolid()) {
			return;
		}
		// p.sendMessage("Ydist: "+distance + " Velocity: " + p.getVelocity().getY());
		Bukkit.getScheduler().runTaskLater(NESS.main, new Runnable() {
			public void run() {
				if (to.getY() > from.getY()) {
					// p.sendMessage("Result toy:" + Utility.around(distance/defaultvalue)+"
					// Distance beetwen y: " + Utility.around(distance));
					if (distance > defaultjump) {
						ArrayList<Block> blocchivicini = Utility.getSurrounding(Utilities.getPlayerUnderBlock(p), true);
						boolean bypass = Utility.hasBlock(p, Material.SLIME_BLOCK);
						for (Block s : blocchivicini) {
							// p.sendMessage(s.getType().toString().toLowerCase());
							for (String b : bypasses) {
								if (s.getType().toString().toLowerCase().contains(b)) {
									bypass = true;
								}
							}
						}
						if (!bypass) {
							WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "AirJump", true);
						}
					} else if (distance == defaultvalue || distance == defaultjump) {
						Location loc = p.getLocation();
						Location loc1 = p.getLocation();
						loc1.setY(loc.getY() - 2);
						if (loc1.getBlock().getType() == Material.AIR
								&& Utilities.getPlayerUnderBlock(p).getType().equals(Material.AIR)
								&& !(p.getVelocity().getY() > -0.078)
								&& !loc.getBlock().getType().name().contains("STAIR")
								&& !loc1.getBlock().getType().name().contains("STAIR") && !(p.getNoDamageTicks() > 1)) {
							WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 18, "AirJump", true);
						}
					}
				}
			}
		}, 2L);
	}

	public static void Check5(PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		final Location from = event.getFrom();
		final Location to = event.getTo();
		final double dist = Math.pow(to.getX() - from.getX(), 2.0) + Math.pow(to.getZ() - from.getZ(), 2.0);
		final double defaultvalue = 0.9800000190734863;
		if (bypass(event.getPlayer())) {
			return;
		}
		final double result = dist / defaultvalue;
		if (calculate(result, 1.15) >= 0 && calculate(dist, 0.8) >= 0) {
			WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "Vanilla", true);
		}
	}

	public static void Check6(PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		if (bypass(event.getPlayer())) {
			return;
		}
		if (Utility.SpecificBlockNear(event.getTo(), Material.SLIME_BLOCK)
				|| Utility.SpecificBlockNear(event.getFrom(), Material.SLIME_BLOCK) || Utility.hasBlock(p, Material.SLIME_BLOCK)) {
			return;
		}
		if (p.isOnline()) {
			double yaw = Math.abs(event.getFrom().getYaw()-event.getTo().getYaw());
			double pitch = Math.abs(event.getFrom().getPitch()-event.getTo().getPitch());
			if (Math.abs(p.getVelocity().getY()) > 3.92D) {
				WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "HighVelocity", true);
			} else if (event.getTo().getY() - event.getFrom().getY() > 0.7) {
				WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "HighY", true);
			} else if (event.getFrom().distanceSquared(event.getTo()) == 0.0
					&& Utilities.getPlayerUpperBlock(p).getType().equals(Material.AIR) && !Utility.isOnGround(p)
					&& !Utility.isOnGround(p) && pitch<0.1 && yaw<0.1) {
				WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "CostantDist", true);
			}
		}
	}

	public static void Check7(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location from = e.getFrom();
		if (bypass(e.getPlayer())) {
			return;
		}
		boolean isonground = Utility.isOnGround(player.getLocation());
		Location to = e.getTo();
		if (player.isOnline() && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
			Vector vec = new Vector(to.getX(), to.getY(), to.getZ());
			double Distance = vec.distance(new Vector(from.getX(), from.getY(), from.getZ()));
			if (player.getFallDistance() == 0.0F
					&& player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR
					&& player.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
				if (Distance > 0.5D && !isonground && e.getTo().getY() > e.getFrom().getY()
						&& e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
					WarnHacks.warnHacks(player, "Fly", 5, -1.0D, 17, "Ascension", true);
				} else if (Distance > 0.9D && !isonground && e.getTo().getY() > e.getFrom().getY()
						&& e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
					WarnHacks.warnHacks(player, "Fly", 5, -1.0D, 17, "Ascension", true);
				} else if (Distance > 1.0D && !isonground && e.getTo().getY() > e.getFrom().getY()
						&& e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
					WarnHacks.warnHacks(player, "Fly", 5, -1.0D, 17, "Ascension", true);
				} else if (Distance > 3.24D && !isonground && e.getTo().getY() > e.getFrom().getY()
						&& e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
					WarnHacks.warnHacks(player, "Fly", 5, -1.0D, 17, "Ascension", true);
				}
			}
		}
	}

	public static void Check8(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (bypass(e.getPlayer())) {
			return;
		}
		if(!Bukkit.getVersion().contains("1.12")) {
			return;
		}
		if (player.isOnline() && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
			if (player.isOnGround()) {
				if (!Utility.checkGround(e.getTo().getY())) {
					WarnHacks.warnHacks(player, "Fly", 1, -1.0D, 17, "GroundSpoof", null);
				} // && !Utilities.isPlayerLocationOnGround(player)
			}
		}
	}

	public static void Check9(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (bypass(event.getPlayer())) {
			return;
		}
		if (player.isOnline() && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
			Location to = event.getTo();
			Location from = event.getFrom();
			if (!player.getAllowFlight() && player.getVehicle() == null
					&& !player.hasPotionEffect(PotionEffectType.SPEED)) {
				double dist = Math.pow(to.getX() - from.getX(), 2.0D) + Math.pow(to.getZ() - from.getZ(), 2.0D);
				double motion = dist / 0.9800000190734863D;
				if (motion >= 1.0D && dist >= 0.8D) {
						WarnHacks.warnHacks(player, "Fly", 5, -1.0D, 17, "HighSpeed", true);
				}
			}
		}
	}

	public static void Check10(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (bypass(e.getPlayer())) {
			return;
		}
		final double diff = e.getTo().getY() - e.getFrom().getY();
		if (e.getTo().getY() < e.getFrom().getY()) {
			return;
		}
		if (bypass(e.getPlayer())) {
			return;
		}
		if (p.getLocation().getBlock().getType() != Material.CHEST
				&& p.getLocation().getBlock().getType() != Material.TRAPPED_CHEST
				&& p.getLocation().getBlock().getType() != Material.ENDER_CHEST) {
			if (!Utility.checkGround(p.getLocation().getY()) && !Utility.isOnGround(p)) {
				if (Math.abs(p.getVelocity().getY() - diff) > 0.000001 && e.getFrom().getY() < e.getTo().getY()
						&& (p.getVelocity().getY() >= 0 || p.getVelocity().getY() < (-0.0784 * 5))
						&& p.getNoDamageTicks() == 0.0) {
						WarnHacks.warnHacks(p, "Fly", 5, -1.0D, 17, "Gravity", true);
				}
			}
		}
	}

	public static void Check11(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (bypass(e.getPlayer()) || p.isFlying() || p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		if (p.getLocation().getBlock().isLiquid()) {
			return;
		}
		if (Utility.checkGround(p.getLocation().getY()) || Utility.isOnGround(p)) {
			return;
		}
		ArrayList<Block> blocks = Utility.getSurrounding(p.getLocation().getBlock(), true);
		for (Block b : blocks) {
			if (b.isLiquid()) {
				return;
			} else if (b.getType().isSolid()) {
				return;
			}
		}
		if (bypass(e.getPlayer()) || e.getFrom().getBlock().getType().isSolid()
				|| e.getTo().getBlock().getType().isSolid()) {
			return;
		}
		double dist = e.getFrom().getY() - e.getTo().getY();
		NESSPlayer player = NESSPlayer.getInstance(p);
		final double oldY = player.getOldY();
//		user.wasGoingUp = distance.getFrom().getY() > distance.getTo().getY();
		player.SetOldY(dist);
		if (e.getFrom().getY() > e.getTo().getY()) {
			if (oldY >= dist && oldY != 0) {
				WarnHacks.warnHacks(p, "Fly", 3, -1.0D, 18, "Glide", true);
			}
		} else {
			player.SetOldY(0.0);
		}
	}

	private static int calculate(double var0, double var2) {
		double var4;
		return (var4 = var0 - var2) == 0.0D ? 0 : (var4 < 0.0D ? -1 : 1);
	}

	public static boolean bypass(Player p) {
		if (p.isInsideVehicle() || p.hasPotionEffect(PotionEffectType.SPEED)
				|| p.hasPotionEffect(PotionEffectType.JUMP)) {
			return true;
		}
		for (Block b : Utility.getSurrounding(p.getLocation().getBlock(), true)) {
			if (b.getType().isSolid()) {
				return true;
			}
		}
		if (Utilities.isInWeb(p)) {
			return true;
		}
		if (!Utilities.getPlayerUnderBlock(p).getType().equals(Material.LADDER)
				|| Utilities.getPlayerUnderBlock(p).getType().equals(Material.VINE)
				|| !Utilities.getPlayerUnderBlock(p).getType().equals(Material.WATER)) {
			return true;
		}
		if (p.hasPotionEffect(PotionEffectType.LEVITATION)) {
			return true;
		}
		if (Utility.hasflybypass(p)) {
			return true;
		}
		return false;
	}

}
