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

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class Speed extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	private final double maxNormalSpeed, speedUnderBlock, speedNearSlabs, speedOnBoat;

	public Speed(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		maxNormalSpeed = this.ness().getMainConfig().getCheckSection().speed().maxNormalSpeed();
		speedUnderBlock = this.ness().getMainConfig().getCheckSection().speed().speedUnderBlock();
		speedNearSlabs = this.ness().getMainConfig().getCheckSection().speed().speedNearSlabs();
		speedOnBoat = this.ness().getMainConfig().getCheckSection().speed().speedOnBoat();

	}

	public interface Config {
		@ConfComments("speed multiplier while walking")
		@DefaultDouble(2.1)
		double maxNormalSpeed();

		@ConfComments("speed multiplier while walking under a block")
		@DefaultDouble(2.54)
		double speedUnderBlock();

		@ConfComments("normal speed multiplier while walking near slabs (we add this value to the final maxSpeed)")
		@DefaultDouble(0.25)
		double speedNearSlabs();

		@ConfComments("max speed with boats")
		@DefaultDouble(2.787)
		double speedOnBoat();

		@ConfComments({
				"!WARNING! The following configuration settings are here only after someone asked them. These values aren't tested and may cause false flags!",
				"SpeedAir check, max normal speed" })
		// @ConfComments("SpeedAir check, max normal speed")
		@DefaultDouble(0.34)
		double speedAirBaseSpeed();

		@ConfComments("SpeedAir check, max speed with ice")
		@DefaultDouble(0.34)
		double speedAirIceSpeed();

		@ConfComments("SpeedAir check, max speed with slime")
		@DefaultDouble(0.34)
		double speedAirSlimeSpeed();

		@ConfComments("SpeedAir check, max speed walking under a block")
		@DefaultDouble(0.8)
		double speedAirUnderBlock();
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
		double maxSpd = player.getWalkSpeed() * maxNormalSpeed; // 0.42
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
					maxSpd = player.getWalkSpeed() * speedUnderBlock; // 0.507
					break;
				}
			}
		}
		if (movementValues.isAroundSlabs() || movementValues.isAroundStairs()) {
			maxSpd += player.getWalkSpeed() * speedNearSlabs;
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
			maxSpd = speedOnBoat;
		if (hozDist > maxSpd && !movementValues.getHelper().hasflybypass(nessPlayer) && !player.getAllowFlight()
				&& nessPlayer.milliSecondTimeDifference(PlayerAction.DAMAGE) >= 2000 && !nessPlayer.isTeleported()
				&& !nessPlayer.isHasSetback()) {
			if (nessPlayer.getTimeSinceLastWasOnIce() >= 1000 && movementValues.isGroundAround()) {
				if (!player.isInsideVehicle()
						|| (player.isInsideVehicle() && !player.getVehicle().getType().name().contains("HORSE"))) {
					Material small = player.getWorld().getBlockAt(player.getLocation().subtract(0, .1, 0)).getType();
					if (!player.getWorld().getBlockAt(from).getType().isSolid()
							&& !player.getWorld().getBlockAt(to).getType().isSolid()) {
						if (!small.name().contains("TRAPDOOR") && nessPlayer.milliSecondTimeDifference(PlayerAction.VEHICLEENTER) > 150) {
							this.flagEvent(e, maxSpd + " Dist: " + hozDist);
						}
					}
				}
			}
		}
	}
}
