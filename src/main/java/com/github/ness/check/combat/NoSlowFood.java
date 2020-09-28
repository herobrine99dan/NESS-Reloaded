package com.github.ness.check.combat;

import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

@Deprecated //This Check needs a complete recode
public class NoSlowFood extends ListeningCheck<PlayerItemConsumeEvent> {

	public static final ListeningCheckInfo<PlayerItemConsumeEvent> checkInfo = CheckInfos.forEvent(PlayerItemConsumeEvent.class);

	public NoSlowFood(ListeningCheckFactory<?,PlayerItemConsumeEvent> factory, NessPlayer player) {
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
        	flagEvent(e);
            //if(p.setViolation(new Violation("NoSlowDown", ""))) e.setCancelled(true);
        }
    }
}
