package com.github.ness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.ness.check.AntiBot;
import com.github.ness.check.InventoryHack;
import com.github.ness.check.OldMovementChecks;

public class Scheduler {

	public static void startSyncScheduler() {
		BukkitScheduler scheduler = NESSAnticheat.main.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(NESSAnticheat.main, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					NessPlayer np = InventoryHack.manageraccess.getPlayer(p);
					if (!(np == null)) {
						np.setBlockplace(-2);
						np.setClicks(-4);
						np.setNormalPacketsCounter(0);
						np.setMovementpacketscounter(0);
						OldMovementChecks.blockPackets.put(p.getName(), false);
						OldMovementChecks.noground.put(p.getName(), 0);
					}
				}
			}
		}, 0L, 20L);
	}

	public static void startAsyncScheduler() {
		new BukkitRunnable() {
			@Override
			public void run() {
				AntiBot.playerCounter = 0;
			}
		}.runTaskTimerAsynchronously(NESSAnticheat.main, 1L, 20L);
	}

}
