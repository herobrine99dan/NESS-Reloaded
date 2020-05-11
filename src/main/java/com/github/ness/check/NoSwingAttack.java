package com.github.ness.check;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

public class NoSwingAttack extends AbstractCheck<EntityDamageByEntityEvent> {
	static HashMap<UUID, Long> delay = new HashMap<UUID, Long>();

	public NoSwingAttack(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
		Check(e);
	}

	public void Check(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getDamager();
		if (delay.getOrDefault(p.getUniqueId(), (long) 1) > 1) {
			manager.getPlayer(p).setViolation(new Violation("NoSwing"));
		}
		delay.put(p.getUniqueId(), System.currentTimeMillis());
	}
	
	

}
