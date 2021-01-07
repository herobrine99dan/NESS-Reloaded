package com.github.ness.check.dragdown;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.NessPlayer;

public class SafeSetBack implements SetBack {

	@Override
	public boolean doSetBack(NessPlayer nessPlayer, Cancellable e) {
		nessPlayer.getBukkitPlayer().teleport(nessPlayer.getSafeLocation(), TeleportCause.PLUGIN);
		return true;
	}
}
