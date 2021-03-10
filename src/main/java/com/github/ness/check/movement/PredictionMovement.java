package com.github.ness.check.movement;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class PredictionMovement extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public PredictionMovement(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double motionX, motionZ;

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = this.player().getMovementValues();
		float yDelta = (float) ((float) values.getyDiff() - (float) values.getServerVelocity().getY());
		e.getPlayer().sendMessage("yDelta: " + yDelta);
		this.moveEntityWithHeading(e.getPlayer().isOnGround()); // Try changing position of this line
	}

	public void moveEntityWithHeading(boolean onGround) {
		double flyingMove = (this.player().getBukkitPlayer().isSprinting() ? 0.026 : 0.02);
		this.motionX += flyingMove;
		this.motionZ += flyingMove;
		float f4 = 0.91F;
		if (onGround)
			f4 = this.getSlipperness() * 0.91F;
		this.motionX *= f4;
		this.motionZ *= f4;
	}

	public float getSlipperness() {
		// TODO Make the check
		return 1f;
	}

}
