package com.github.ness.check.dragdown;

import org.bukkit.event.Cancellable;

import com.github.ness.NessPlayer;

public class DragDownVelocity implements SetBack {

	@Override
	public boolean doSetBack(NessPlayer nessPlayer, Cancellable e) {
		return nessPlayer.completeDragDown();
	}

}
