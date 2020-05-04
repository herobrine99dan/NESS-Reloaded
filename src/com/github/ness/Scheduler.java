package com.github.ness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class Scheduler {

	public void start() {
        BukkitScheduler scheduler = NESSAnticheat.main.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(NESSAnticheat.main, new Runnable() {
            @Override
            public void run() {
                for(Player p: Bukkit.getOnlinePlayers()) {
                	
                }
            }
        }, 0L, 20L);
	}
	
}
