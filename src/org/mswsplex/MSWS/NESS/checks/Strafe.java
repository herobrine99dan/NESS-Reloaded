package org.mswsplex.MSWS.NESS.checks;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.MovementPlayerData;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.PlayerManager;
import org.mswsplex.MSWS.NESS.ServerLag;
import org.mswsplex.MSWS.NESS.Utilities;
import org.mswsplex.MSWS.NESS.Utility;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class Strafe {
	public static HashMap<String, Integer> strafe = new HashMap<String, Integer>();

	public static void Check(PlayerMoveEvent e) {
		final Player player = e.getPlayer();
		MovementPlayerData p = MovementPlayerData.getInstance(player);
		if (!Utility.isInAir(player) || player.isOnGround() || Utility.hasflybypass(player)
				|| !Utility.blockAdjacentIsLiquid(player.getLocation()) || Utilities.BlockOverPlayer(e.getTo())
				|| Utility.onSteps(player.getLocation()) || Utility.isOnGround(e.getTo())
				|| Utility.isOnGround(e.getFrom()) || Utilities.onBlock(e.getTo())
				) {//Manager.getPlayerData(player).getMovementVelocityLog().size() != 0
			p.setStrafeValueX(0);
			p.setStrafeValueZ(0);
			return;
		}

		final double diffX = e.getTo().getX() - e.getFrom().getX();
		final double diffZ = e.getTo().getZ() - e.getFrom().getZ();

		if (!Bukkit.getVersion().contains("1.8") && p.getFlyMoves() <= 3) {
			p.setStrafeValueX(0);
			p.setStrafeValueZ(0);
			return;
		}

		if (p.getStrafeValueX() == 0 || p.getStrafeValueZ() == 0) {
			p.setStrafeValueX(diffX);
			p.setStrafeValueZ(diffZ);
			return;
		}

		final double speed = Math.sqrt(diffX * diffX + diffZ * diffZ);
		final double oldSpeed = Math
				.sqrt(p.getStrafeValueX() * p.getStrafeValueX() + p.getStrafeValueZ() * p.getStrafeValueZ());

		double SpeedMultiply = speed / oldSpeed;

		boolean OnBoat = false;
		for (Entity entity : player.getWorld().getEntities()) {
			if (entity.getLocation().distance(e.getTo()) < 4 && entity.getType() == EntityType.BOAT) {
				OnBoat = true;
			}
		}

		/*
		 * if (SpeedMultiply > 1.95 && speed > 0.06 && (e.getTo().getY() -
		 * e.getFrom().getY() > -0.07 || e.getTo().getY() - e.getFrom().getY() < -0.08)
		 * && (System.nanoTime() - Manager.getPlayerData(player).getSprintLastToggle())
		 * / 1000000 - PlayerManager.getPing(player) - Main.getServerLag() > 500 &&
		 * !OnBoat && !Utility.SpecificBlockNear(player.getLocation(),
		 * Material.SLIME_BLOCK) && !Utility.SpecificBlockNear(e.getFrom(),
		 * Material.WEB)) { }
		 */

		if (SpeedMultiply > 1) {
			SpeedMultiply = 1 - SpeedMultiply;
		}

		if (SpeedMultiply < 0) {
			SpeedMultiply = 0;
		}

		double DirectionDiffX = (diffX - p.getStrafeValueX()) * SpeedMultiply;
		double DirectionDiffZ = (diffZ - p.getStrafeValueZ()) * SpeedMultiply;

		if (DirectionDiffX < 0) {
			DirectionDiffX = 0;
		}
		if (DirectionDiffZ < 0) {
			DirectionDiffZ = 0;
		}

		if (DirectionDiffX == Double.POSITIVE_INFINITY) {
			DirectionDiffX = 0;
		}
		if (DirectionDiffZ == Double.POSITIVE_INFINITY) {
			DirectionDiffZ = 0;
		}

		final double directionChanging = Math.sqrt(DirectionDiffZ * DirectionDiffZ + DirectionDiffX * DirectionDiffX);

		double value = 0.042;

		if (Bukkit.getVersion().contains("1.8")) {
			value *= 0.95;
		} else {
			double nearestEntity = Double.MAX_VALUE;

			for (LivingEntity entity : player.getWorld().getLivingEntities()) {
				if (entity.getLocation().distance(e.getTo()) < nearestEntity) {
					nearestEntity = entity.getLocation().distance(e.getTo());
				}
			}

			if (nearestEntity < 1.8) {
				value *= 1.5;
			}
		}

		if ((System.nanoTime() - p.getSprintLastToggle()) / 1000000 - PlayerManager.getPing(player) - ServerLag.getServerLag() < 500) {
			value *= 3.1;
		}

		if (player.isSneaking()) {
			value *= 2;
		}

		if (directionChanging > value) {
			WarnHacks.warnHacks(player, "Speed", 5, 500, 1, "Strafe", false);
		}

		p.setStrafeValueX(diffX);
		p.setStrafeValueZ(diffZ);
	}
}
