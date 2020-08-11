package com.github.ness.check;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class GhostHand extends AbstractCheck<PlayerInteractEvent> {

	public GhostHand(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerInteractEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerInteractEvent e) {
		Check(e);

	}

	/**
	 * Check for Ghost Hand
	 * 
	 * @param event
	 */
	public void Check(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		final Location loc = player.getLocation();
		Block targetBlock = player.getTargetBlock((Set<Material>) null, 7);
		if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		if (!targetBlock.getType().isSolid())
			return;
		if (targetBlock.equals(event.getClickedBlock())) {
			return;
		}
		NessPlayer p = manager.getPlayer(event.getPlayer());
		if (p.getMovementValues().XZDiff > 0.15 || Math.abs(p.lastPitch) > 0.0) {
			return;
		}
		if (targetBlock.getLocation().getBlock().getType().name().toLowerCase().contains("slab")) {
			return;
		}
		if (targetBlock.getLocation().add(0, 1, 0).getBlock().getType().name().toLowerCase().contains("slab")) {
			return;
		}
		if(event.getClickedBlock() == null || event.getBlockFace() == null) {
			return;
		}
		Location block = event.getClickedBlock().getLocation().add(event.getBlockFace().getModX(),
				event.getBlockFace().getModY(), event.getBlockFace().getModZ());
		Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {

			Location loc1 = player.getLocation();
			float grade = Math.abs(loc.getYaw() - loc1.getYaw()) + Math.abs(loc.getPitch() - loc1.getPitch());

			if (!(grade == 0)) {
				return;
			}
			if (block.getBlock().getType().isSolid() || !targetBlock.equals(event.getClickedBlock())) {
				try {
					if (manager.getPlayer(event.getPlayer()).shouldCancel(event, this.getClass().getSimpleName())) {
						event.setCancelled(true);
					}
				} catch (Exception ex) {
				}
				p.setViolation(new Violation("GhostHand", ""));
			}
		}, 2L);
	}

}
