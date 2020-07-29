package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

import net.minecraft.server.v1_12_R1.Material;

public class Scaffold extends AbstractCheck<BlockPlaceEvent> {

	public Scaffold(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(BlockPlaceEvent e) {
		Check1(e);
		Check2(e);
		Check3(e);
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
		final float now = player.getLocation().getPitch();
		Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
			float pitchNow = player.getLocation().getPitch();
			float diff = Math.abs(now - pitchNow);
			if (diff > 20F) {
				if (manager.getPlayer(event.getPlayer()).shouldCancel(event, this.getClass().getSimpleName())) {
					event.setCancelled(true);
				}
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold", "HighPitch"));
			}
		}, 2L);
	}

	public void Check3(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (p.isSprinting()) {
			if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
				e.setCancelled(true);
			}
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("Scaffold", "Impossible"));
		}
	}

}
