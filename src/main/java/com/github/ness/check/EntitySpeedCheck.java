package com.github.ness.check;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class EntitySpeedCheck extends AbstractCheck<PlayerMoveEvent>{

	public EntitySpeedCheck(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}
	
	@Override
	void checkEvent(PlayerMoveEvent e) {
       Check(e);
	}
	/**
	 * Check for EntitySpeed
	 * @param event
	 */
	public void Check(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if(p.isInsideVehicle()) {
			Entity e = p.getVehicle();
			if (e.getType() == EntityType.MINECART) {
				return;
			}
			double limit = 5.75;
			if(Utility.getMaxSpeed(event.getFrom(), event.getTo())>limit) {
				try {
					ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
							.getConfigurationSection("cancel");
					if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()), 0) > cancelsec.getInt("vl",10)) {
						event.setCancelled(true);
					}
				}catch(Exception ex) {}
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("EntitySpeedCheck"));
			}
		}
		
	}

}
