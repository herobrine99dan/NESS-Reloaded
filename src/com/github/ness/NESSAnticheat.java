package com.github.ness;

import org.bukkit.plugin.java.JavaPlugin;

public class NESSAnticheat extends JavaPlugin {

	private NessScheduler scheduler;
	
	public void onEnable() {
		scheduler = new NessScheduler();
		getServer().getScheduler().runTaskAsynchronously(this, scheduler);
	}
	
	public void onDisable() {
		scheduler.close();
	}
	
	
}
