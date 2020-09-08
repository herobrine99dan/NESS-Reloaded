package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;

public class FlyGhostMode extends AbstractCheck<PlayerMoveEvent> {


	public FlyGhostMode(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.isDead()) {
			NessPlayer np = this.manager.getPlayer(player);
			if ((np.getMovementValues().XZDiff > 0.3 || np.getMovementValues().yDiff > 0.16) && !np.isTeleported()) {
				this.getNessPlayer(player).setViolation(new Violation("Fly", "GhostMode"), event);
			}
		}
	}

}
