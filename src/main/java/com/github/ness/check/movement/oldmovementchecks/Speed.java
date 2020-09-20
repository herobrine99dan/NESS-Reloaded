package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.MSG;
import com.github.ness.utility.Utility;

public class Speed extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo.eventOnly(PlayerMoveEvent.class);

	public Speed(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location from = e.getFrom();
		double maxSpd = 0.4209;
		Player player = e.getPlayer();
		MovementValues movementValues = this.player().getMovementValues();
		if (Utility.hasflybypass(player) || player.getAllowFlight() || Utility.hasVehicleNear(player, 4)
				|| player().isTeleported()) {
			return;
		}
		Material mat = null;
		if (player.isBlocking()) {
			if (to.getY() % .5 == 0.0) {
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
			maxSpd = 2.8;
		if (movementValues.AroundStairs) {
			maxSpd += 0.4;
		}
		if (player().getTimeSinceLastWasOnIce() < 1300) {
			maxSpd += 0.14;
			if (Utility.groundAround(to.clone().add(0, 2, 0))) {
				maxSpd += 0.3;
			}
		}
		if (movementValues.XZDiff > maxSpd && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.SPEED)
				&& !player.getAllowFlight() && player().nanoTimeDifference(PlayerAction.DAMAGE) >= 2000
				&& !player().isTeleported()) {
			if (Utility.groundAround(player.getLocation())) {
				if (!player.isInsideVehicle()
						|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE)) {
					Material small = player.getWorld().getBlockAt(player.getLocation().subtract(0, .1, 0)).getType();
					if (!player.getWorld().getBlockAt(from).getType().isSolid()
							&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
						if (!small.name().toLowerCase().contains("trap")) {
							if (player().isDevMode())
								MSG.tell(player, "&9Dev> &7Speed amo: " + movementValues.XZDiff);
							if (player.isBlocking()) {
								player().setViolation(new Violation("NoSlowDown", "HighDistance(OnMove)"), e);
							} else {
								player().setViolation(new Violation("Speed", "MaxDistance(OnMove)"),e);
							}
						}
					}

				}
			}
		}
	}

}
