package com.github.ness.check;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class FastPlace extends AbstractCheck<BlockPlaceEvent>{
	
	public FastPlace(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(BlockPlaceEvent e) {
       Check(e);
	}
    /**
     * A Simple FastPlace check
     * @param event
     */
	public void Check(BlockPlaceEvent e) {
		NessPlayer player = manager.getPlayer(e.getPlayer());
		player.setBlockplace(player.getBlockplace()+1);
		if(player.getBlockplace()>6) {
			try {
				if(manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}catch(Exception ex) {}
			player.setViolation(new Violation("FastPlace",""));
		}
		
	}

}
