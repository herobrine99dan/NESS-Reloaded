package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;

public class ChestESP extends AbstractCheck<PlayerMoveEvent> {
	public ChestESP(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(manager.getNess(), () -> {
			int r = 10;
			for (int x = r * -1; x <= r; x++) {
				for (int y = r * -1; y <= r; y++) {
					for (int z = r * -1; z <= r; z++) {
						Location loc = new Location(e.getTo().getWorld(), (e.getTo().getBlockX() + x),
								(e.getTo().getBlockY() + y), (e.getTo().getBlockZ() + z));
						Block b = loc.getBlock();
						if (b.getType().name().toLowerCase().contains("chest")) {
							e.getPlayer().sendBlockChange(loc, Material.STONE, (byte) 0);
						}
					}
				}
			}
		});
	}
}
