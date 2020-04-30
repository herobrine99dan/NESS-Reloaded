package com.github.ness.listener;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.CheckManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DamageListener implements Listener {

	private final CheckManager manager;
	
	private void onDamageByEntity(EntityDamageByEntityEvent evt) {
		
	}
	
	private void onDamageByBlock(EntityDamageByBlockEvent evt) {
		
	}
	
}
