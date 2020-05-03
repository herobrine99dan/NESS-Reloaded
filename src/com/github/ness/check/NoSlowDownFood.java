package com.github.ness.check;

import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.Utility;
import com.github.ness.Violation;

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
		if(Utility.hasflybypass(e.getPlayer())) {
			return;
		}
		NessPlayer p = manager.getPlayer(e.getPlayer());
		double distance = p.getDistance();
		if (distance > 0.2||e.getPlayer().isSprinting()) {
			p.setViolation(new Violation("NoSlowDown"));
		}
	}
}
