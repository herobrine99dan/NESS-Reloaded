package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

public class FastLadder extends ListeningCheck<PlayerMoveEvent> {

    double maxDist;

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos
			.forEvent(PlayerMoveEvent.class);

	public FastLadder(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
        this.maxDist = this.ness().getNessConfig().getCheck(this.getClass())
                .getDouble("maxdist", 0.201D);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        NessPlayer np = this.player();
        if (Utility.isClimbableBlock(p.getLocation().getBlock()) && !p.hasPotionEffect(PotionEffectType.JUMP)
                && !Utility.hasflybypass(p) && !np.isTeleported()) {
            double distance = np.getMovementValues().yDiff;
            if (distance > 0.155D && p.getVelocity().getY() < 0) {
            	if(player().setViolation(new Violation("FastLadder", "Dist: " + distance))) event.setCancelled(true);
            }
        }
    }
}
