package org.mswsplex.MSWS.NESS.checks.movement;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.Utilities;
import org.mswsplex.MSWS.NESS.Utility;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class Sprint {
	public static HashMap<String, Integer> sprintcheck = new HashMap<String, Integer>();
	public static HashMap<String, Integer> speed = new HashMap<String, Integer>();

	public static void Check(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Vector vector = e.getFrom().toVector();
		Vector vettore = e.getTo().toVector().subtract(vector);
		Vector vettorino1 = e.getTo().toVector();
		Vector vettorino = e.getFrom().toVector().subtract(vettorino1);
		if (p.hasPotionEffect(PotionEffectType.SPEED) || Utility.hasflybypass(p) || Math.abs(e.getTo().getYaw()-e.getFrom().getYaw())>60) {
			return;
		}
		Bukkit.getScheduler().runTaskLater(NESS.main, new Runnable() {
			public void run() {
				String yaw = Utilities.DeterminateDirection(p.getLocation().getYaw());
				if (p.isSprinting()) {
					if (yaw.equals("sud") && vettore.getBlockX() == -1 && vettore.getBlockZ() == -1) {
						checkfailed(p, "Sprint", "(Omni)");
					} else if (yaw.equals("est") && vettore.getBlockX() == -1 && vettorino.getBlockZ() == -1) {
						checkfailed(p, "Sprint", "(Omni)");
					} else if (yaw.equals("ovest") && vettorino.getBlockX() == -1 && vettorino.getBlockZ() == -1) {
						checkfailed(p, "Sprint", "(Omni)");
					} else if (yaw.equals("nord") && vettore.getBlockX() == -1 && vettorino.getBlockZ() == -1) {
						checkfailed(p, "Sprint", "(Omni)");
					}
					if (p.getFoodLevel() < 6) {
						checkfailed(p, "Sprint", "FoodLevel");
					}
				}
			}
		}, 3L);
	}

	private static void checkfailed(Player p, String module, String check) {
		int failed = sprintcheck.getOrDefault(p.getName(), 0);
		sprintcheck.put(p.getName(), failed + 1);
		if (failed > 6) {
			WarnHacks.warnHacks(p, check, 10, -1.0D, 65, module, false);
			// MSG.tell(p, "Sprint(Omni)[nord], VL: orec".replace("nord",
			// yaw).replace("orec", failed+""));
		}
	}

	public static void Check1(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (p.hasPotionEffect(PotionEffectType.SPEED) || Utility.hasflybypass(p)) {
			return;
		}
		Location from = e.getFrom();
		Location to = e.getTo();
		if (!p.isSprinting()) {
			double distance = Utility.getMaxSpeed(from, to);
			if (distance > 0.280 && !p.isFlying()) {
				int failed = speed.getOrDefault(p.getName(), 0);
				speed.put(p.getName(), failed + 1);
				if (failed > 1) {
					checkfailed(p, "Speed", "HighDistance");
					speed.put(p.getName(), 0);
					// MSG.tell(p, "Sprint(Omni)[nord], VL: orec".replace("nord",
					// yaw).replace("orec", failed+""));
				}
			}
		}
	}

	public static void Check2(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if (player.hasPotionEffect(PotionEffectType.SPEED) || Utility.hasflybypass(player) || Math.abs(to.getYaw()-from.getYaw())>80) {
			return;
		}
		if (player.isSprinting()) {
			double deltaX = to.getX() - from.getX();
			double deltaZ = to.getZ() - from.getZ();

			float yaw = Math.abs(from.getYaw()) % 360;

			if (!player.isSprinting()) {
				return;
			}

			if (deltaX < 0.0 && deltaZ > 0.0 && yaw > 180.0f && yaw < 270.0f
					|| deltaX < 0.0 && deltaZ < 0.0 && yaw > 270.0f && yaw < 360.0f
					|| deltaX > 0.0 && deltaZ < 0.0 && yaw > 0.0f && yaw < 90.0f
					|| deltaX > 0.0 && deltaZ > 0.0 && yaw > 90.0f && yaw < 180.0f) {
				checkfailed(player, "Sprint", "(SecondOmni)");
			}
		}
	}
}
