package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class SpeedAir extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	int airTicks;

	public SpeedAir(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	public static float getBaseSpeed(NessPlayer nessPlayer) {
		Player player = nessPlayer.getBukkitPlayer();
		float max = 0.36f + (Utility.getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f)
				+ ((player.getWalkSpeed() - 0.2f) * 1.6f);
		if (nessPlayer.getMovementValues().isAroundIce()
				|| nessPlayer.getTimeSinceLastWasOnIce() < 1500) {
			max *= 1.4;
		}
		if (nessPlayer.getMovementValues().isAroundSlime()) {
			max *= 1.2;
		}
		if (nessPlayer.getMovementValues().isAroundSnow()) {
			max *= 1.15;
		}
		if (nessPlayer.getMovementValues().isAroundSlabs()) {
			max *= 1.15;
		}
		if (nessPlayer.getMovementValues().isAroundLiquids()) {
			max *= 1.2;
		}
		return max;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		double xDiff = nessPlayer.getMovementValues().xDiff;
		double zDiff = nessPlayer.getMovementValues().zDiff;
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2200) {
			xDiff -= Math.abs(nessPlayer.getLastVelocity().getX());
			zDiff -= Math.abs(nessPlayer.getLastVelocity().getZ());
		}
		if (!Utility.isMathematicallyOnGround(event.getTo().getY())) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		final double maxDist = getBaseSpeed(nessPlayer);
		if (airTicks > 4 && (Math.abs(xDiff) > maxDist || Math.abs(zDiff) > maxDist) && !Utility.hasflybypass(player)
				&& !player.getAllowFlight() && !Utility.hasVehicleNear(player, 3)) {
			flagEvent(event);
		}
	}

}
