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

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location from = e.getFrom();
		Player player = e.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = this.player().getMovementValues();
		final double dist = from.distance(to);
		double hozDist = dist - (to.getY() - from.getY());
		if (to.getY() < from.getY())
			hozDist = dist - (from.getY() - to.getY());
		double maxSpd = 0.4209;
		if (player().nanoTimeDifference(PlayerAction.VELOCITY) < 1800) {
			hozDist -= Math.abs(player().velocity.getX());
			hozDist -= Math.abs(player().velocity.getZ());
		}
		if (player.hasPotionEffect(PotionEffectType.SPEED)) {
			final int level = Utility.getPotionEffectLevel(player, PotionEffectType.SPEED);
			hozDist = (float) (hozDist - hozDist / 100.0D * level * 20.0D);
		}
		Material mat = null;
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
		if (player.isInsideVehicle() && player.getVehicle().getType().name().contains("BOAT"))
			maxSpd = 2.787;
		if (hozDist > maxSpd && !player.isFlying() && !player.getAllowFlight()
				&& nessPlayer.nanoTimeDifference(PlayerAction.DAMAGE) >= 2000 && !nessPlayer.isTeleported()) {
			if (nessPlayer.getMovementValues().groundAround) {
				if (nessPlayer.getTimeSinceLastWasOnIce() >= 1000) {
					if (!player.isInsideVehicle()
							|| (player.isInsideVehicle() && !player.getVehicle().getType().name().contains("HORSE"))) {
						Material small = player.getWorld().getBlockAt(player.getLocation().subtract(0, .1, 0))
								.getType();
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
}
