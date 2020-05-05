package com.github.ness.check;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.CheckManager;

public class PatternKillauraAttack extends AbstractCheck<EntityDamageByEntityEvent>{
	static HashMap<UUID, Long> lastAttack = new HashMap<UUID, Long>();
	static HashMap<UUID, LivingEntity> lastHit = new HashMap<UUID, LivingEntity>();
	static HashMap<UUID, Float> lastRange = new HashMap<UUID, Float>();
	
	public PatternKillauraAttack(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
       Check(e);
	}
	
	public void Check(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			Entity entity = event.getEntity();

			if (entity instanceof LivingEntity) {
				lastAttack.put(p.getUniqueId(), System.currentTimeMillis());
				lastHit.put(p.getUniqueId(), (LivingEntity) entity);
			}
		}
	}

}
