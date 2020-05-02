package com.github.ness.check;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;

import com.github.ness.CheckManager;

public class NoSwingAnimation extends AbstractCheck<PlayerAnimationEvent> {
	public NoSwingAnimation(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerAnimationEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerAnimationEvent e) {
		Check(e);
	}
	
	public void Check(PlayerAnimationEvent event) {
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			NoSwingAttack.delay.put(event.getPlayer().getUniqueId(), (long) 1);
		}
	}

}
