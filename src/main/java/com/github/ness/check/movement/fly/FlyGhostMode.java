package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class FlyGhostMode extends AbstractCheck<PlayerMoveEvent> {


	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo
			.eventOnly(PlayerMoveEvent.class);

	public FlyGhostMode(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isDead()) {
            NessPlayer np = this.player();
            if ((np.getMovementValues().XZDiff > 0.3 || np.getMovementValues().yDiff > 0.16) && !np.isTeleported()) {
	        	if(player().setViolation(new Violation("Fly", "GhostMode"))) event.setCancelled(true);
            }
        }
    }

}
