package com.github.ness;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.listener.InteractionListener;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {

	@Getter
	private Executor executor;
	
	@Override
	public void onEnable() {
		PlayerManager manager = new PlayerManager();
		executor = Executors.newSingleThreadExecutor();
		getServer().getPluginManager().registerEvents(new InteractionListener(manager), this);
	}
	
	@Override
	public void onDisable() {
		ExecutorService service = ((ExecutorService) executor);
		service.shutdown();
		try {
			service.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		executor = null;
	}
	
	
}
