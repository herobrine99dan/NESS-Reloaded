package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.MathUtils;
import com.github.ness.utility.Utility;

public class SpeedFriction extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	int airTicks;
	double lastDeltaXZ;
	private int buffer;
	private float moveStrafe = 0, moveForward = 0;
	private float motionX, motionZ, lastMotionX, lastMotionZ;

	public SpeedFriction(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	/**
	 * Simple Friction Check Made with help of Frap
	 */
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (player.isFlying() || values.getHelper().isPlayerUsingElytra(nessPlayer) || values.isAroundLiquids()
				|| values.isAroundSlime() || values.hasBlockNearHead()) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		double xzDiff = values.getXZDiff();
		if (!values.getHelper().isMathematicallyOnGround(values.getTo().getY())) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		if (airTicks > 1) {
			final double prediction = (lastDeltaXZ * 0.91f);
			final double difference = xzDiff - prediction;
			if (difference > 0.026 && prediction > 0.075) {
				if (++buffer > 1) {
					this.flagEvent(event);
				}
			} else if (buffer > 0) {
				buffer -= 0.5;
			}
		}
		this.lastDeltaXZ = xzDiff;
	}
}
