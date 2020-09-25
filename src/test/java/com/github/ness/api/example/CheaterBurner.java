package com.github.ness.api.example;

import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.InfractionTrigger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class CheaterBurner implements InfractionTrigger {

	@Override
	public void trigger(AnticheatPlayer anticheatPlayer, Infraction infraction) {
		if (infraction.getCount() > 10) {
			Player player = anticheatPlayer.getPlayer();
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
					"&c&l" + player.getName() + " was punished for using cheats!"));
			player.getWorld().strikeLightning(player.getLocation().add(0, -1, 0));
		}
	}

}
