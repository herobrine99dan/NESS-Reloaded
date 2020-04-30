package com.github.ness.checks;

import org.bukkit.Bukkit;

public abstract class Check {

	protected final String type;
	
	public Check(String type) {
		this.type = type;
		
	}
	
	public String getType() {
		return type;
	}
	
	public void debug(Object message) {
		Bukkit.broadcastMessage(String.valueOf(message));
	}
	
}