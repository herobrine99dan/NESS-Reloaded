package com.github.ness.check.combat;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class AutoClicker extends AbstractCheck<PlayerInteractEvent> {

    int maxCPS;

    public AutoClicker(CheckManager manager) {
        super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 1, TimeUnit.SECONDS));
        this.maxCPS = this.manager.getNess().getNessConfig().getCheck(AutoClicker.class).getInt("maxCPS", 18);
    }

    @Override
    protected void checkEvent(PlayerInteractEvent e) {
        Check(e);
    }

    void Check(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            NessPlayer player = manager.getPlayer(e.getPlayer());
            player.CPS++;
            if (player.CPS > maxCPS && !e.getPlayer().getTargetBlock(null, 5).getType().name().contains("grass")) {
                player.setViolation(new Violation("AutoClicker", "MaxCPS: " + player.CPS), e);
            }
        }
    }

    @Override
    protected void checkAsyncPeriodic(NessPlayer player) {
        player.CPS = 0;
    }
}
