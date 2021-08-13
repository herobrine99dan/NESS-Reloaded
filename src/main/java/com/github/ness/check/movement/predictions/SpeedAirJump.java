package com.github.ness.check.movement.predictions;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class SpeedAirJump extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private int groundTicks;
	private float lastDeltaXZ;

	public SpeedAirJump(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	/**
	 * Powerful Air XZ-Prediction check made with https://www.mcpk.wiki/wiki/ Loving
	 * those guys who made it.
	 */
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (values.getHelper().hasflybypass(nessPlayer)
				|| values.getHelper().isNearLiquid(event.getTo()) || values.getHelper().isNearLiquid(event.getFrom())) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		float xzDiff = (float) values.getXZDiff();
		float yDiff = (float) values.getyDiff();
		final boolean lastOnGround = values.getHelper().isMathematicallyOnGround(values.getFrom().getY());
		final boolean onGround = values.getHelper().isMathematicallyOnGround(values.getTo().getY());
		final boolean sprinting = nessPlayer.getSprinting().get();
		final boolean sneaking = nessPlayer.getSneaking().get();
		if (lastOnGround && !onGround && yDiff > 0.0) {
			this.player().sendDevMessage("yDiff: " + yDiff + " xzDiff: " + xzDiff);
		}
		this.lastDeltaXZ = xzDiff;
	}
}
