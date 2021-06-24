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
	private float lastXZDiff;

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		float xzDiff = (float) values.getXZDiff();
		final float walkSpeed = p.getWalkSpeed() / 2;
		xzDiff -= (xzDiff / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1300) {
			xzDiff -= Math.hypot(nessPlayer.getLastVelocity().getX(), nessPlayer.getLastVelocity().getZ());
		}
		if (event.getTo().getBlock().getType().name().contains("WEB")
				&& event.getFrom().getBlock().getType().name().contains("WEB") && !values.getHelper().hasflybypass(nessPlayer)
				&& nessPlayer.milliSecondTimeDifference(PlayerAction.WEBBREAKED) > 1300) {
			float prediction = lastXZDiff * 0.25f;
			nessPlayer.sendDevMessage("xzDiff: " + (float) xzDiff + " prediction: " + (float) prediction);
			if (xzDiff > walkSpeed) {
				if (++buffer > 2) {
					flagEvent(event);
				}
			} else if(buffer > 0) {
				buffer = 0;
			}
		}
		this.lastXZDiff = xzDiff;
	}
}
