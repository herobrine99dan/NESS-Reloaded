package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class CreativeFly extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public CreativeFly(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double lastMotionXZ, lastMotionY;
	private int flyingTicks;

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		MovementValues values = this.player().getMovementValues();
		double xzDiff = values.getXZDiff();
		double yDiff = values.getyDiff();
		Player player = event.getPlayer();
		if (player.isFlying()) {
			flyingTicks++;
		} else {
			flyingTicks = 0;
		}
		if(flyingTicks > 1 && player.isGliding()) {
			flyingTicks -= 2;
		}
		if (flyingTicks > 10) {
			double predictedXZ = lastMotionXZ * 0.91f;
			double predictedY = lastMotionY * 0.6;
			double resultXZ = Math.abs(xzDiff - predictedXZ);
			double resultY = Math.abs(yDiff - predictedY);
			double maxXZ = player.getFlySpeed(); // (player.getFlySpeed() * 0.1) / 0.1 that become player.getFlySpeed()
			double maxY = (player.getFlySpeed() * 0.38) / 0.1;
			if(values.isAroundStairs()) {
				maxXZ += player.getFlySpeed();
			}
			if (resultXZ > maxXZ) {
				this.flagEvent(event, "resultXZ: " + (float) resultXZ);
			}
			if (resultY > maxY && yDiff > 0) {
				this.flagEvent(event, "resultY: " + (float) resultY);
			}
		}
		this.lastMotionXZ = xzDiff;
	}

}
