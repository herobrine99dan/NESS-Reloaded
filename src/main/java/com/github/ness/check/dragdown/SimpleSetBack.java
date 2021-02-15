package com.github.ness.check.dragdown;

import org.bukkit.event.Cancellable;

import com.github.ness.NessPlayer;

public class SimpleSetBack implements SetBack {

	@Override
	public boolean doSetBack(NessPlayer nessPlayer, Cancellable e) {
		e.setCancelled(true);
		return true;
	}

	@Override
	public boolean shouldRunOnDelay() {
		return false;
	}
}
