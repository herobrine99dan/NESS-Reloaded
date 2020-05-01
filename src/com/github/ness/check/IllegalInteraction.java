package com.github.ness.check;

import java.util.Set;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.MSG;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.PlayerManager;

import com.github.ness.CheckManager;
import com.github.ness.Violation;

public class IllegalInteraction extends AbstractCheck<BlockPlaceEvent>{
	
	public IllegalInteraction(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(BlockPlaceEvent e) {
       Check(e);
	}

	public void Check(BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		if (event.getBlockAgainst().isLiquid() && event.getBlock().getType() != Material.WATER_LILY) {
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("LiquidInteraction"));
		}
	}

}
