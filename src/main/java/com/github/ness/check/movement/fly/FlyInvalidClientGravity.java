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

		@DefaultDouble(1)
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
		if (values.getHelper().isOnGroundUsingCollider(e.getTo(), this.getMaterialAccess())
				|| values.getHelper().isOnGroundUsingCollider(e.getFrom(), this.getMaterialAccess())
				|| p.isOnGround()) {
			airTicks = 0;
		} else {
			airTicks++;
		}
		if (values.getHelper().hasflybypass(nessPlayer) || p.getAllowFlight() || values.isAroundLiquids()
				|| Utility.hasVehicleNear(p) || values.isAroundWeb() || values.isAroundSlime()
				|| values.isAroundLadders() || values.isAroundSnow()) {
			lastDeltaY = deltaY;
			return;
		}
		// TODO If XZ Diff are too low (0.005) Minecraft skip the motion Update so we
		// get 7 airTicks instead of 8 airTicks with a jump, make a method to detect if
		// motion Y is skipped
		float yPredicted = (float) ((lastDeltaY - 0.08D) * 0.9800000190734863D);
		float yResult = (float) Math.abs(deltaY - yPredicted);
		this.player().sendDevMessage("3ticks: " + airTicks);
		if (airTicks > minAirTicks && nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 1000) {
			if (Math.abs(yPredicted) > 0.01) {
				this.player().sendDevMessage("4result: " + yResult + " yPredicted: " + yPredicted);
				if (Math.abs(yResult) > 0.01) {
					this.player().sendDevMessage("6ticks: " + airTicks);
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
