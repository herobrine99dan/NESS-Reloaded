package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Scaffold extends AbstractCheck<BlockPlaceEvent> {

	public Scaffold(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(BlockPlaceEvent e) {
		Check1(e);
		Check2(e);
	}

	public void Check1(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		final double MAX_ANGLE = Math.toRadians(90);
		BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
		final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
		float placedAngle = player.getLocation().getDirection().angle(placedVector);

		if (placedAngle > MAX_ANGLE) {
			if (manager.getPlayer(event.getPlayer()).shouldCancel(event, this.getClass().getSimpleName())) {
				event.setCancelled(true);
			}
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold", "HighAngle"));
		}
	}

	public void Check2(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block target = player.getTargetBlock(null, 5);
		if (event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0.0D, 1.0D, 0.0D))
				.getType() == Material.AIR) {
			if (!event.getBlock().getLocation().equals(target.getLocation()) && !event.isCancelled()
					&& target.getType().isSolid() && !target.getType().name().toLowerCase().contains("sign")
					&& !target.getType().toString().toLowerCase().contains("fence")
					&& player.getLocation().getY() > event.getBlock().getLocation().getY()) {
				if (manager.getPlayer(event.getPlayer()).shouldCancel(event, this.getClass().getSimpleName())) {
					event.setCancelled(true);
				}
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold", "Impossible1"));
			}
		}
	}
}
