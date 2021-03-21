package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class FlyInvalidClientGravity extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	private final int minAirTicks;
	private final double minBuffer;

	public FlyInvalidClientGravity(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		minAirTicks = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity().airTicks();
		minBuffer = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity().buffer();
	}

	private double lastDeltaY;
	private int airTicks;
	private double buffer;

	public interface Config {
		@DefaultInteger(4)
		int airTicks();

		//After some tests i discovered i fixed all the bugs maybe
		@DefaultDouble(0)
		double buffer();
	}

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Check(e);
	}

	/**
	 * Check for Invalid Gravity
	 *
	 * @param e
	 */
	public void Check(PlayerMoveEvent e) {
		NessPlayer nessPlayer = this.player();
		Player p = e.getPlayer();
		MovementValues values = nessPlayer.getMovementValues();
		double deltaY = values.getyDiff();
		if (values.getHelper().isOnGroundUsingCollider(e.getTo())
				|| values.getHelper().isOnGroundUsingCollider(e.getFrom())
				|| p.isOnGround()) {
			airTicks = 0;
		} else {
			airTicks++;
		}
		if (values.getHelper().hasflybypass(nessPlayer) || p.getAllowFlight() || values.isAroundLiquids()
				|| Utility.hasVehicleNear(p) || values.isAroundWeb() || values.isAroundSlime()
				|| values.isAroundLadders() || values.isAroundSnow() || values.hasBlockNearHead() || values.isAroundKelp()) {
			lastDeltaY = deltaY;
			return;
		}
		//Even if this isn't the best fix, at least it allows for me to get good detection
		final float yVelocityDelta = Math.abs(((float) values.getyDiff() - (float) values.getServerVelocity().getY()));
		float yPredicted = (float) ((lastDeltaY - 0.08D) * 0.9800000190734863D);
		float yResult = (float) Math.abs(deltaY - yPredicted);
		if (airTicks > minAirTicks && nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 1000) {
			if (Math.abs(yPredicted) > 0.01 && yVelocityDelta > 0.1) {
				if (Math.abs(yResult) > 0.01) {
					if (++buffer > minBuffer) {
						this.flagEvent(e, "yResult: " + yResult + " AirTicks: " + airTicks);
					}
				} else if (buffer > 0) {
					buffer -= 0.5;
				}
			}
		}
		lastDeltaY = deltaY;
	}
}
