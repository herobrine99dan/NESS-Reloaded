package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class FlyInvalidMove extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos
			.forEvent(PlayerMoveEvent.class);

	public FlyInvalidMove(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getFallDistance() < 7 && player.getVelocity().getY() < -2.0D && !player.isFlying()) {
        	flag();
        	//if(player().setViolation(new Violation("Fly", "InvalidMove" + " FallDist: "
            //        + player.getFallDistance() + " Velocity: " + (float) player.getVelocity().getY()))) event.setCancelled(true);
        }
    }

}
