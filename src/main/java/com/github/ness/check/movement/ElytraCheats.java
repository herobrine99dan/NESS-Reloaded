package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class ElytraCheats extends AbstractCheck<PlayerMoveEvent> {

    double maxXZDiff;
    double maxYDiff;
    
	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo
			.eventOnly(PlayerMoveEvent.class);

	public ElytraCheats(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
        this.maxYDiff = this.ness().getNessConfig().getCheck(this.getClass())
                .getDouble("maxxzdiff", 1.5);
        this.maxXZDiff = this.ness().getNessConfig().getCheck(this.getClass())
                .getDouble("maxydiff", 1);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (!p.isGliding()) {
            return;
        }
        float yDiff = (float) this.player().getMovementValues().yDiff;
        float xzDiff = (float) this.player().getMovementValues().XZDiff;
        if (xzDiff > maxXZDiff || yDiff > this.maxYDiff) {
        	if(player().setViolation(new Violation("ElytraCheats", "HighDistance"))) event.setCancelled(true);
        }
    }

}
