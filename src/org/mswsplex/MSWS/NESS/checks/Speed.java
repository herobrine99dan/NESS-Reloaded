package org.mswsplex.MSWS.NESS.checks;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

public class Speed {
	public static HashMap<String, Integer> speed = new HashMap<String, Integer>();

	public static void Check(PlayerMoveEvent e) {
		Location from = e.getFrom();
		Location to = e.getTo();
		// Bukkit.getPlayer("herobrine99dan").sendMessage(
		// "Player: " + e.getPlayer().getName() + " YDist: " + Utility.around(to.getY()
		// - from.getY(), 6)
		// + " Dist: " + Utility.around(Utility.getMaxSpeed(from, to), 6));
		Player player = e.getPlayer();
		if (Utilities.isStairs(Utilities.getPlayerUnderBlock(player)) || Utilities.isStairs(to.getBlock())) {
			return;
		}
		// player.sendMessage("Time: "+Utility.around(System.currentTimeMillis(), 12));
		if (Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_LAVA)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.WATER)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.LAVA)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_WATER)
				|| Utility.hasflybypass(player)) {
			return;
		}
		if (!player.getNearbyEntities(5, 5, 5).isEmpty()) {
			return;
		}
		if (!player.isInsideVehicle() && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.JUMP)) {
			if (to.getY() > from.getY()) {
				double y = Utility.around(to.getY() - from.getY(), 6);

				ArrayList<Block> blocchivicini = Utility.getSurrounding(Utilities.getPlayerUnderBlock(player), false);
				boolean bypass = false;
				for (Block s : blocchivicini) {
					if (s.getType().equals(Material.SLIME_BLOCK)) {
						bypass = true;
					}
				}
				if (y > 0.36 && y < 0.419 && !(y == 0.404) && !(y == 0.365) && !(y == 0.395) && !bypass && !(y == 0.386) && !(y == 0.414)
						&& !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
					if (Utility.hasflybypass(player)) {
						return;
					}
					WarnHacks.warnHacks(player, "Speed", 5, -1.0D, 45, "MiniJump", true);
					if (NESS.main.devMode) {
						player.sendMessage("y:" + y);
					}
				} else if (y > 0.248 && y < 0.333 && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
					WarnHacks.warnHacks(player, "Speed", 5, -1.0D, 46, "MiniJump", true);
					if (NESS.main.devMode) {
						player.sendMessage("Ydist: " + y);
					}
				}
			}
		}
	}

	public static void Check1(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (Utility.hasflybypass(player)) {
			return;
		}
		double soulsand = 0.211;
		double dist = Utility.getMaxSpeed(event.getFrom(), event.getTo());
		if (player.isOnGround() && !player.isInsideVehicle() && !player.isFlying()
				&& !player.hasPotionEffect(PotionEffectType.SPEED) && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
			if (dist > 0.62D) {
				if (Utilities.getPlayerUpperBlock(player).getType().isSolid()
						&& Utilities.getPlayerUnderBlock(player).getType().name().toLowerCase().contains("ice")) {
                   return;
				}
				if (NESS.main.devMode) {
					event.getPlayer().sendMessage("First Distance: " + dist);
				}
				WarnHacks.warnHacks(event.getPlayer(), "Speed", 10, -1.0D, 79, "MaxDistance", false);
			} else if (dist > soulsand && player.getFallDistance() == 0
					&& player.getLocation().getBlock().getType().equals(Material.SOUL_SAND)) {
				WarnHacks.warnHacks(event.getPlayer(), "Speed", 5, -1.0D, 79, "NoSlowDown", false);
			}
		}
	}

	public static void Check2(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (Utility.hasflybypass(p)) {
			return;
		}
		int ping = PlayerManager.getPing(p);
		int maxPackets = NESS.main.maxpackets * (ping/100);
		if(ping<150) {
			maxPackets = NESS.main.maxpackets;
		}
		NESSPlayer player = NESSPlayer.getInstance(p);
		if (player.getOnMoveRepeat() > maxPackets) {
			WarnHacks.warnHacks(p, "Timer", 10, -1.0D, 26, "TooMovements", false);
			// p.sendMessage("Repeat: " + player.getOnMoveRepeat());
		}
	}

	public static void Check3(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (Utility.hasflybypass(p) || p.hasPotionEffect(PotionEffectType.SPEED)
				|| p.hasPotionEffect(PotionEffectType.JUMP)) {
			return;
		}
		if (!(event.getFrom().getY() > event.getTo().getY())) {
			return;
		}
		if (event.getTo().getY() == event.getFrom().getY()) {
			return;
		}
		double Airmaxspeed = 0.4D;
		Location l = p.getLocation();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		double speed = Speed.getHV(event.getTo().toVector()).subtract(Speed.getHV(event.getFrom().toVector())).length();
		Location above = new Location(p.getWorld(), (double) x, (double) (y + 2), (double) z);
		Location above3 = new Location(p.getWorld(), (double) (x - 1), (double) (y + 2), (double) (z - 1));
		if (!Utility.isOnGround(p) && !Utility.checkGround(p.getLocation().getY()) && speed >= Airmaxspeed
				&& !Utilities.getPlayerUnderBlock(p).getType().equals(Material.PACKED_ICE)
				&& !Utilities.getPlayerUnderBlock(p).getType().equals(Material.ICE)
				&& !Utilities.getPlayerUnderBlock(p).getType().isSolid() && !l.getBlock().isLiquid()
				&& above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
				&& !Utilities.getPlayerUnderBlock(p).getType().equals(Material.AIR)) {
			return;
		} else {
			WarnHacks.warnHacks(p, "Speed", 5, -1.0D, 27, "MidAir", false);
		}
	}

	public static void Check4(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (Utility.hasflybypass(p)) {
			return;
		}
		if (Utility.isSpeed(Utility.getMaxSpeed(event.getFrom(), event.getTo()))) {
			WarnHacks.warnHacks(event.getPlayer(), "Speed", 2, -1.0D, 82, "CheckedDistance", true);
		}
	}

	private static Vector getHV(Vector V) {
		V.setY(0);
		return V;
	}

}
