package com.github.ness.check;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class NoSlowDownFood extends AbstractCheck<PlayerItemConsumeEvent> {

	public NoSlowDownFood(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerItemConsumeEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerItemConsumeEvent e) {
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
		if (distance > 0.23 || e.getPlayer().isSprinting()) {
			if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
				e.setCancelled(true);
			}
			p.setViolation(new Violation("NoSlowDown", ""));
		}
	}
}
