package com.github.ness.check;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

public class ElytraCheats extends AbstractCheck<PlayerMoveEvent> {

	public ElytraCheats(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (!p.isGliding()) {
			return;
		}
		float yDiff = (float) this.manager.getPlayer(p).getMovementValues().yDiff;
		float xzDiff = (float) this.manager.getPlayer(p).getMovementValues().XZDiff;
		if (xzDiff > 1.5 || yDiff > 1) {
			manager.getPlayer(p).setViolation(new Violation("ElytraCheats", "HighDistance"));
			try {
				if (manager.getPlayer(p).shouldCancel(event, this.getClass().getSimpleName())) {
					event.setCancelled(true);
				}
			} catch (Exception ex) {
				event.setCancelled(true);
			}
		}
	}

}
