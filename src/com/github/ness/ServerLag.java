package com.github.ness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ServerLag {
	private static long TimeBehind = 0;
	private static long LastTimeBehind = 0;
	public static long LastTimeReset = 0;

	public static int getServerLag() {
		long newTimeBehind = (System.nanoTime() - LastTimeBehind - 50000000) / 1000000;

		if (newTimeBehind > TimeBehind) {
			TimeBehind = newTimeBehind;

			if (newTimeBehind > 75) {
				LastTimeReset = System.currentTimeMillis();
			}
		}

		return (int) TimeBehind;
	}
	
	public static void startcounter() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(NESS.main, new Runnable() {
			@Override
			public void run() {
				long newTimeBehind = (System.nanoTime() - LastTimeBehind - 50000000) / 1000000;
				
				if(newTimeBehind < 0) {
					newTimeBehind = 0;
				}
				
				if(LastTimeBehind == 0) {
					newTimeBehind = 0;
				}
					
				if(newTimeBehind > 75
						&& newTimeBehind >= TimeBehind) {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(p.hasPermission("ness.notify.hacks")) {
							MSG.tell(p, "Server is lagging! " + (newTimeBehind) + "ms behind");
						}
					}
					LastTimeReset = System.currentTimeMillis();
				}
				
				if(newTimeBehind > TimeBehind) {
					TimeBehind = newTimeBehind;
					
					if(newTimeBehind > 75) {
						LastTimeReset = System.currentTimeMillis();
					}
				} else if(System.currentTimeMillis() - LastTimeReset > 1000) {
					TimeBehind = (TimeBehind * 5 + newTimeBehind) / 6;
				}	
				LastTimeBehind = System.nanoTime();
			}
		}, 20L, 1L);
	}

}
