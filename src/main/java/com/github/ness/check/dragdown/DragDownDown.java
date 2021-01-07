package com.github.ness.check.dragdown;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.NessPlayer;

public class DragDownDown implements SetBack {

	@Override
	public boolean doSetBack(NessPlayer nessPlayer, Cancellable e) {
		final Location block = nessPlayer.getBukkitPlayer().getLocation().clone().add(0,
				-getDistance(nessPlayer.getBukkitPlayer()), 0);
		for (int i = 0; i < 10; i++) {
			if (block.getBlock().getType().isSolid()) {
				block.add(0, 0.1, 0);
			} else {
				break;
			}
		}
		nessPlayer.getBukkitPlayer().teleport(block, TeleportCause.PLUGIN);
		return true;
	}

	private int getDistance(Entity e) {
		Location loc = e.getLocation().clone();
		double y = loc.getBlockY();
		int distance = 0;
		for (double i = y; i >= 0; i--) {
			loc.setY(i);
			if (loc.getBlock().getType().isSolid())
				break;
			distance++;
		}
		return distance;
	}

}
