package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class ElytraCheats extends ListeningCheck<PlayerMoveEvent> {

    double maxXZDiff;
    double maxYDiff;
    
	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos
			.forEvent(PlayerMoveEvent.class);

	public ElytraCheats(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
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
