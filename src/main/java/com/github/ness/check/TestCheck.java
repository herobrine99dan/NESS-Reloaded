package com.github.ness.check;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.utility.Utility;

public class TestCheck extends AbstractCheck<PlayerInteractEvent> {

	public TestCheck(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerInteractEvent.class));
	}

	@Override
	void checkEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		//To Test getBlocksAround
		
	}

}
