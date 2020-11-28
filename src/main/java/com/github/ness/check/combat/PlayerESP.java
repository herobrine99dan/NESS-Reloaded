package com.github.ness.check.combat;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.utility.Utility;

public class PlayerESP extends Check {
    
    private double minAngle;

    public PlayerESP(NessPlayer nessPlayer) {
        super(PlayerESP.class, nessPlayer, true, Duration.ofSeconds(1).toMillis());
        minAngle = -0.5;
    }
    
    @Override
    public void checkAsyncPeriodic() {
        runTaskLater(() -> {
            for (Player cheater : Bukkit.getOnlinePlayers()) {
                for (Player tohide : Bukkit.getOnlinePlayers()) {
                    if ((Utility.getAngle(cheater, tohide.getLocation(), null) < minAngle
                            || !cheater.hasLineOfSight(tohide))
                            && cheater.getLocation().distance(tohide.getLocation()) > 13) {
                        cheater.hidePlayer(this.manager().getNess().getPlugin(), tohide);
                    } else {
                        cheater.showPlayer(this.manager().getNess().getPlugin(), tohide);
                    }
                }
            }
        }, Duration.ZERO);
    }

}
