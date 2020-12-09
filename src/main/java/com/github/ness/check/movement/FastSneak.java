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

public class FastSneak extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FastSneak(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		double xDiff = Math.abs(nessPlayer.getMovementValues().xDiff);
		double zDiff = Math.abs(nessPlayer.getMovementValues().zDiff);
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1300) {
			xDiff -= Math.abs(nessPlayer.getLastVelocity().getX());
			zDiff -= Math.abs(nessPlayer.getLastVelocity().getZ());
		}
		double walkSpeed = p.getWalkSpeed() * 0.85;
		if (nessPlayer.getMovementValues().isAroundIce()
				|| nessPlayer.getTimeSinceLastWasOnIce() < 1500) {
			walkSpeed *= 1.4;
		}
		if (nessPlayer.getMovementValues().isAroundSlime()) {
			walkSpeed *= 1.2;
		}
		if (nessPlayer.getMovementValues().isAroundSnow()) {
			walkSpeed *= 1.15;
		}
		if (nessPlayer.getMovementValues().isAroundSlabs()) {
			walkSpeed *= 1.15;
		}
		if (nessPlayer.getMovementValues().isAroundLiquids()) {
			walkSpeed *= 1.2;
		}
		walkSpeed += Math.abs(nessPlayer.getMovementValues().getServerVelocity().getY()) * 0.35;
		xDiff -= (xDiff / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
		zDiff -= (zDiff / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
		if ((xDiff > walkSpeed || zDiff > walkSpeed) && p.isSneaking() && !Utility.hasflybypass(p)) {
			flagEvent(event);
		}
	}
}
