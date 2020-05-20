package com.github.ness.check;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.ness.CheckManager;

public class BlockBreakChecks extends AbstractCheck<BlockBreakEvent> {
	
	public BlockBreakChecks(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockBreakEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(BlockBreakEvent e) {
		Check(e);
		Check1(e);
	}
	
	void Check(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		Block target = p.getTargetBlock(null, 5);
		   if(!(target.getY()==b.getY() && target.getX()==b.getX() && target.getZ()==b.getZ())) {
				try {
					ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
							.getConfigurationSection("cancel");
					if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()), 0) > cancelsec.getInt("vl",10)) {
						event.setCancelled(true);
					}
				}catch(Exception ex) {}
		  }		
	}
	
	void Check1(BlockBreakEvent e) {
		
	}

}
