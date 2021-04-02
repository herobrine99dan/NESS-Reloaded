package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class Speed extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public Speed(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	protected void checkEvent(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location from = e.getFrom();
		Player player = e.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = this.player().getMovementValues();
		double dist = from.distance(to);
		double hozDist = dist - (to.getY() - from.getY());
		if (to.getY() < from.getY())
			hozDist = dist - (from.getY() - to.getY());
		double maxSpd = player.getWalkSpeed() * 2.1; // 0.42
		double velocity = 0;
		if (player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			velocity = Math.hypot(player().getLastVelocity().getX(), player().getLastVelocity().getZ());
			hozDist -= velocity;
		}
		if (player.hasPotionEffect(PotionEffectType.SPEED)) {
			final int level = Utility.getPotionEffectLevel(player, PotionEffectType.SPEED);
			hozDist -= (float) (hozDist / 100.0D * level * 20.0D);
		}
		Material mat = null;
		for (int x = -1; x < 1; x++) {
			for (int z = -1; z < 1; z++) {
				mat = from.getWorld()
						.getBlockAt(from.getBlockX() + x, player.getEyeLocation().getBlockY() + 1, from.getBlockZ() + z)
						.getType();
				if (mat.isSolid()) {
					maxSpd = player.getWalkSpeed() * 2.54; // 0.507
					break;
				}
			}
		}
		if (movementValues.isAroundSlabs() || movementValues.isAroundStairs()) {
			maxSpd += player.getWalkSpeed() * 0.25;
		}
		if (movementValues.isAroundIce() || nessPlayer.getTimeSinceLastWasOnIce() < 1000) {
			maxSpd += player.getWalkSpeed();
		}
		if (movementValues.isAroundSlime()) {
			maxSpd += velocity;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) < 1000) {
			maxSpd += 0.1;
		}
		if (player.isInsideVehicle() && player.getVehicle().getType().name().contains("BOAT"))
			maxSpd = 2.787;		
		if (hozDist > maxSpd && !movementValues.getHelper().hasflybypass(nessPlayer) && !player.getAllowFlight()
				&& nessPlayer.milliSecondTimeDifference(PlayerAction.DAMAGE) >= 2000 && !nessPlayer.isTeleported()
				&& !nessPlayer.isHasSetback()) {
			if (nessPlayer.getTimeSinceLastWasOnIce() >= 1000 && movementValues.isGroundAround()) {
				if (!player.isInsideVehicle()
						|| (player.isInsideVehicle() && !player.getVehicle().getType().name().contains("HORSE"))) {
					Material small = player.getWorld().getBlockAt(player.getLocation().subtract(0, .1, 0)).getType();
					if (!player.getWorld().getBlockAt(from).getType().isSolid()
							&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
						if (!small.name().contains("TRAPDOOR")) {
							this.flagEvent(e, maxSpd + " Dist: " + hozDist);
						}
					}
				}
			}
		}
	}
}
