package com.github.ness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.ness.check.InventoryHack;
import com.github.ness.check.OldMovementChecks;

public class Scheduler {

	public void start() {
		BukkitScheduler scheduler = NESSAnticheat.main.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(NESSAnticheat.main, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					NessPlayer np = InventoryHack.manageraccess.getPlayer(p);
					if (!(np == null)) {
						np.setBlockplace(0);
						np.setClicks(0);
						np.setDrop(0);
						np.setTeleported(false);
						np.setOnMoveRepeat(0);
						np.setNormalPacketsCounter(0);
						np.setPackets(0);
						np.setMovementpacketscounter(0);
						OldMovementChecks.blockPackets.put(p.getName(), false);
						OldMovementChecks.noground.put(p.getName(), 0);
					}
				}
			}
		}, 0L, 20L);
	}

}
