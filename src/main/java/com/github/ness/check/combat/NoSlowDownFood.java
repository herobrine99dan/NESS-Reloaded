package com.github.ness.check.combat;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class NoSlowDownFood extends AbstractCheck<PlayerItemConsumeEvent> {

    public NoSlowDownFood(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(PlayerItemConsumeEvent.class));
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void checkEvent(PlayerItemConsumeEvent e) {
        Check(e);
    }

    public void Check(PlayerItemConsumeEvent e) {
        if (Utility.hasflybypass(e.getPlayer())) {
            return;
        }
        NessPlayer p = manager.getPlayer(e.getPlayer());
        double distance = p.getMovementValues().XZDiff;
        distance -= e.getPlayer().getVelocity().getX();
        distance -= e.getPlayer().getVelocity().getZ();
        if (distance > 0.25 || e.getPlayer().isSprinting()) {
            p.setViolation(new Violation("NoSlowDown", ""), e);
        }
    }
}
