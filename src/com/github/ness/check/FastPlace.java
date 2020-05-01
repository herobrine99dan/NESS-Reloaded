package com.github.ness.check;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.NESSPlayer;

import com.github.ness.CheckManager;
import com.github.ness.Violation;

public class FastPlace extends AbstractCheck<BlockPlaceEvent>{
	
	public FastPlace(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(BlockPlaceEvent e) {
       Check(e);
	}

	public void Check(BlockPlaceEvent event) {
		NESSPlayer player = NESSPlayer.getInstance(event.getPlayer());
		player.SetBlockPlace(player.getBlockPlace()+1);
		if(player.getBlockPlace()>5) {
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("FastPlace"));
			event.setCancelled(true);
		}
		
	}

}
