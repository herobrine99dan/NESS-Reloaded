package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class FlyInvalidMove extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo
			.eventOnly(PlayerMoveEvent.class);

	public FlyInvalidMove(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getFallDistance() < 7 && player.getVelocity().getY() < -2.0D && !player.isFlying()) {
        	if(player().setViolation(new Violation("Fly", "InvalidMove" + " FallDist: "
                    + player.getFallDistance() + " Velocity: " + (float) player.getVelocity().getY()))) event.setCancelled(true);
        }
    }

}
