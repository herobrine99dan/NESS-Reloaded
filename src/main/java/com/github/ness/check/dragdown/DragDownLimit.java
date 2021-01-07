package com.github.ness.check.dragdown;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.NessPlayer;

public class DragDownLimit implements SetBack {

	@Override
	public boolean doSetBack(NessPlayer nessPlayer, Cancellable e) {
		final Location block = nessPlayer.getBukkitPlayer().getLocation().clone().add(0, -0.5, 0);
		for (int i = 0; i < 10; i++) {
			if (block.getBlock().getType().isSolid()) {
				block.add(0, 0.1, 0);
			} else {
				break;
			}
		}
		if(block.getBlock().getType().isSolid()) {
			nessPlayer.completeDragDown();
			return false;
		}
		nessPlayer.getBukkitPlayer().teleport(block,TeleportCause.PLUGIN);
		return true;
	}

}
