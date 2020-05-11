package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

public class Scaffold extends AbstractCheck<BlockPlaceEvent>{
	
	public Scaffold(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(BlockPlaceEvent e) {
       Check(e);
       Check1(e);
       Check2(e);
	}

	public void Check(BlockPlaceEvent event) {

	}

	public void Check1(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		final double MAX_ANGLE = Math.toRadians(90);
		BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
		final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
		float placedAngle = player.getLocation().getDirection().angle(placedVector);

		if (placedAngle > MAX_ANGLE) {
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
		}
	}

	public void Check2(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		final float now = player.getLocation().getPitch();
		Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
			float pitchNow = player.getLocation().getPitch();
			float diff = Math.abs(now - pitchNow);
			if (diff > 20F) {
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold"));
			}
		}, 2L);
	}
}
