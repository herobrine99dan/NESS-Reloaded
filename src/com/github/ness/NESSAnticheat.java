package com.github.ness;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.listener.InteractionListener;

public class NESSAnticheat extends JavaPlugin {

	private NessScheduler scheduler;
	
	@Override
	public void onEnable() {
		CheckManager manager = new CheckManager();
		scheduler = new NessScheduler();
		getServer().getScheduler().runTaskAsynchronously(this, scheduler);
		getServer().getPluginManager().registerEvents(new InteractionListener(manager), this);
	}
	
	@Override
	public void onDisable() {
		scheduler.close();
	}
	
	
}
