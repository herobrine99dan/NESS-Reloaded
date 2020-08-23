package com.github.ness.check;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.utility.Utility;

public class TestCheck extends AbstractCheck<PlayerMoveEvent> {

	public TestCheck(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		//getBlocksAround
		p.sendMessage("damage: " + (float) Utility.calcDamage((3.5*p.getVelocity().getY())/-0.71));
	}

}
