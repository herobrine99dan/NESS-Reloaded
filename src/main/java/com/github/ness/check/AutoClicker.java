package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class AutoClicker extends AbstractCheck<PlayerInteractEvent> {

	public AutoClicker(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 1, TimeUnit.SECONDS));
	}

	@Override
	void checkEvent(PlayerInteractEvent e) {
		Check(e);
	}

	void Check(PlayerInteractEvent e) {
		Action action = e.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			NessPlayer player = manager.getPlayer(e.getPlayer());
			player.CPS++;
			if (player.CPS > 18 && !e.getPlayer().getTargetBlock(null, 5).getType().name().contains("grass")) {
				player.setViolation(new Violation("AutoClicker", "MaxCPS: " + player.CPS));
				if(manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}
		}
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		player.CPS = 0;
	}
}
