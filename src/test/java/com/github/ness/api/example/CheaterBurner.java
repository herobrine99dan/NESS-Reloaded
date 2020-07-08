package com.github.ness.api.example;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;

public class CheaterBurner extends ViolationAction {

	@Override
	public void actOn(Player player, Violation violation, int violationCount) {
		if(violationCount > 10) {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + player.getName() + " was punished for using cheats!"));
			player.getWorld().strikeLightning(player.getLocation().add(0, -1, 0));
		}
	}

}
