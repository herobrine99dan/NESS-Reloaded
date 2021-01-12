package com.github.ness.check.movement;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class GroundSpeed extends ListeningCheck<PlayerMoveEvent> {

	private int groundTicks;
	private int sneakTicks;

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public GroundSpeed(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	protected void checkEvent(PlayerMoveEvent e) {
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = this.player().getMovementValues();
		if (movementValues.getHelper().isMathematicallyOnGround(movementValues.getTo().getY())) {
			groundTicks++;
		} else {
			groundTicks = 0;
		}
		double maxDist = e.getPlayer().getWalkSpeed() * 1.435;
		if(e.getPlayer().isSneaking()) {
			sneakTicks++;
		} else {
			sneakTicks = 0;
		}
		if(sneakTicks > 6) {
			maxDist *= 0.5;
		}
		if (groundTicks > 7) {
			if (movementValues.getXZDiff() > maxDist) {
				nessPlayer.sendDevMessage("CheatsXZDiff: " + (float) movementValues.getXZDiff() + " ticks: " + groundTicks);
			} else {
				nessPlayer.sendDevMessage("XZDiff: " + (float) movementValues.getXZDiff() + " ticks: " + groundTicks);
			}
		}

	}
}
