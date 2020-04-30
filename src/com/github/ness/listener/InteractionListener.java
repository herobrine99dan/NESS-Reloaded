package com.github.ness.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InteractionListener implements Listener {

	private final CheckManager manager;
	
	@EventHandler
	private void onInteraction(PlayerInteractEvent evt) {
		if (evt.getAction() == Action.LEFT_CLICK_AIR || evt.getAction() == Action.LEFT_CLICK_BLOCK) {
			manager.getPlayer(evt.getPlayer()).checkCpsAndPossiblyKick();
		}
	}
	
	
}
