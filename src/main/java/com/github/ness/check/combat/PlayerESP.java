package com.github.ness.check.combat;

import com.github.ness.check.CheckManager;
import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.ImmutableVector;
import com.github.ness.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class PlayerESP extends AbstractCheck<PlayerInteractEvent> {

    final double minangle;
    
	public static final CheckInfo<PlayerInteractEvent> checkInfo = CheckInfo
			.eventOnly(PlayerInteractEvent.class);

	public PlayerESP(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
        this.minangle = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getDouble("minangle", -0.05);
	}

    @Override
    protected void checkAsyncPeriodic() {
        (new BukkitRunnable() {
            public void run() {
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
            }
        }).runTask(NESSAnticheat.getInstance());
    }

}
