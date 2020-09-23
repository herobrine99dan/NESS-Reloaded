package com.github.ness.check.combat;

import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

@Deprecated //This Check needs a complete recode
public class NoSlowFood extends AbstractCheck<PlayerItemConsumeEvent> {

	public static final CheckInfo<PlayerItemConsumeEvent> checkInfo = CheckInfo.eventOnly(PlayerItemConsumeEvent.class);

	public NoSlowFood(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerItemConsumeEvent e) {
        check(e);
    }

    private void check(PlayerItemConsumeEvent e) {
        if (Utility.hasflybypass(e.getPlayer())) {
            return;
        }
        NessPlayer p = player();
        double distance = p.getMovementValues().XZDiff;
        distance -= e.getPlayer().getVelocity().getX();
        distance -= e.getPlayer().getVelocity().getZ();
        if (distance > 0.25 || e.getPlayer().isSprinting()) {
            if(p.setViolation(new Violation("NoSlowDown", ""))) e.setCancelled(true);
        }
    }
}
