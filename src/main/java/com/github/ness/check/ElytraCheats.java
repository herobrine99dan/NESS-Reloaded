package com.github.ness.check;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

public class ElytraCheats extends AbstractCheck<PlayerMoveEvent> {
	
	double maxXZDiff;
	double maxYDiff;

	public ElytraCheats(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		this.maxYDiff = this.manager.getNess().getNessConfig().getCheck(this.getClass())
				.getDouble("maxxzdiff", 1.5);
		this.maxXZDiff = this.manager.getNess().getNessConfig().getCheck(this.getClass())
				.getDouble("maxydiff", 1);
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
		if (xzDiff > maxXZDiff || yDiff > this.maxYDiff) {
			manager.getPlayer(p).setViolation(new Violation("ElytraCheats", "HighDistance"), event);
		}
	}

}
