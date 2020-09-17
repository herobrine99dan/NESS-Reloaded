package com.github.ness.check.combat;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.ImmutableVector;
import com.github.ness.utility.Utility;

public class PlayerESP extends AbstractCheck<PlayerInteractEvent> {

    private final double minangle;
    
	public static final CheckInfo<PlayerInteractEvent> checkInfo = CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 700, TimeUnit.MILLISECONDS);

	public PlayerESP(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
        this.minangle = this.ness().getNessConfig().getCheck(this.getClass()).getDouble("minangle", -0.05);
	}

    @Override
    protected void checkAsyncPeriodic() {
    	runTaskLater(() -> {
            for (Player cheater : Bukkit.getOnlinePlayers()) {
                ImmutableVector direction = player().getMovementValues().getTo().getDirectionVector();
                for (Player tohide : Bukkit.getOnlinePlayers()) {
                    if ((Utility.getAngle(cheater, tohide.getLocation(), direction) < minangle
                            || !cheater.hasLineOfSight(tohide))
                            && cheater.getLocation().distance(tohide.getLocation()) > 13) {
                        cheater.hidePlayer(NESSAnticheat.getInstance(), tohide);
                    } else {
                        cheater.showPlayer(NESSAnticheat.getInstance(), tohide);
                    }
                }
            }
    	}, Duration.ZERO);
    }

}
