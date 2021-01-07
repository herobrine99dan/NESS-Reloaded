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

public class NoWeb extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public NoWeb(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private int buffer;

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		double xDiff = Math.abs(values.getxDiff());
		double zDiff = Math.abs(values.getzDiff());
		final double walkSpeed = p.getWalkSpeed() * 0.625;
		xDiff -= (xDiff / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
		zDiff -= (zDiff / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1300) {
			xDiff -= Math.abs(nessPlayer.getLastVelocity().getX());
			zDiff -= Math.abs(nessPlayer.getLastVelocity().getZ());
		}
		if (event.getTo().getBlock().getType().name().contains("WEB")
				&& event.getFrom().getBlock().getType().name().contains("WEB") && !values.getHelper().hasflybypass(p)
				&& nessPlayer.milliSecondTimeDifference(PlayerAction.WEBBREAKED) > 1300) {
			nessPlayer.sendDevMessage("X: " + (float) xDiff + " Z: " + (float) zDiff);
			if ((xDiff > walkSpeed || zDiff > walkSpeed)) {
				if (++buffer > 2) {
					flagEvent(event);
				}
			} else if(buffer > 0) {
				buffer = 0;
			}
		}

	}

}
