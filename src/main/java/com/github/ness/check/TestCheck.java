package com.github.ness.check;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;

public class TestCheck extends AbstractCheck<PlayerMoveEvent> {

	public TestCheck(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	void checkEvent(PlayerMoveEvent event) {
	}

}
