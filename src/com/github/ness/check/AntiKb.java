package com.github.ness.check;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.ness.CheckManager;
import com.github.ness.NESSAnticheat;

public class AntiKb extends AbstractCheck<EntityDamageByEntityEvent> {

	public AntiKb(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
		Check(e);
	}

	public void Check(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			final Location from = p.getLocation();
	        BukkitScheduler scheduler = NESSAnticheat.main.getServer().getScheduler();
	        scheduler.scheduleSyncDelayedTask(NESSAnticheat.main, new Runnable() {
	            @Override
	            public void run() {
	                Location to = p.getLocation();
	                if(to.distanceSquared(from)<0.1) {
	                	
	                }
	            }
	        }, 2L);
		}
	}
}
