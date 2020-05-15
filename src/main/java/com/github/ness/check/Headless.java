package com.github.ness.check;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

public class Headless extends AbstractCheck<PlayerMoveEvent> {
	
	public Headless(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(PlayerMoveEvent e) {
       Check(e);
	}
    /**
     * A Simple HeadLess Check
     * @param event
     */
	public void Check(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        float pitch = p.getLocation().getPitch();
        if(pitch < -90 || pitch > 90)
        {
    		try {
    			ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
    					.getConfigurationSection("cancel");
    			if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()), 0) > cancelsec.getInt("vl",10)) {
    				event.setCancelled(true);
    			}
    		}catch(Exception ex) {}
            manager.getPlayer(p).setViolation(new Violation("HeadLess",""));
        }
	}

}
