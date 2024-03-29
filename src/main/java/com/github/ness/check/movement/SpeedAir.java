package com.github.ness.check.movement;

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

public class SpeedAir extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	private final double speedAirBaseSpeed, speedAirIceSpeed;
	private int airTicks;

	public SpeedAir(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		speedAirBaseSpeed = 0.34;
		speedAirIceSpeed = 0.1;
	}

	private float getBaseSpeed(NessPlayer player) {
		float returner = (float) speedAirBaseSpeed
				+ (Utility.getPotionEffectLevel(player.getBukkitPlayer(), PotionEffectType.SPEED) * 0.062f)
				+ ((player.getBukkitPlayer().getWalkSpeed() - 0.2f) * 1.6f);
		if (player.getMovementValues().isNearMaterials("ICE")) {
			returner *= 1.2;
		}
		if (player.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			returner += Math.hypot(player.getLastVelocity().getX(), player.getLastVelocity().getZ());
		}
		return returner;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (!values.getHelper().isMathematicallyOnGround(event.getTo().getY())) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		final double maxDist = getBaseSpeed(nessPlayer);
		if (airTicks > 5 && values.getXZDiff() > maxDist && !values.getHelper().hasflybypass(nessPlayer)
				&& !player.getAllowFlight() && !Utility.hasVehicleNear(player)
				&& !player().getAcquaticUpdateFixes().isRiptiding()) {
			flag();
		}
	}

}
