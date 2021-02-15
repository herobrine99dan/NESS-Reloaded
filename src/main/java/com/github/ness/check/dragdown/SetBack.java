package com.github.ness.check.dragdown;

import org.bukkit.event.Cancellable;

import com.github.ness.NessPlayer;

public interface SetBack {
	
	boolean doSetBack(NessPlayer nessPlayer, Cancellable e);

	default boolean shouldRunOnDelay() {
		return true;
	}

}
