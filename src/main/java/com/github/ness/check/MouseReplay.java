package com.github.ness.check;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

public class MouseReplay extends AbstractCheck<PlayerMoveEvent> {
	
	public MouseReplay(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		NessPlayer nessPlayer = this.getNessPlayer(e.getPlayer());
		
	}
	
}
