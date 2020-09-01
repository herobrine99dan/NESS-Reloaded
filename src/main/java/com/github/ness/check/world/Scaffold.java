package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class Scaffold extends AbstractCheck<BlockPlaceEvent> {

	public Scaffold(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		Check1(e);
		Check2(e);
	}

	public void Check1(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		final double MAX_ANGLE = Math.toRadians(90);
		BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
		if(placedFace == null) {
			return;
		}
		final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
		float placedAngle = player.getLocation().getDirection().angle(placedVector);

		if (placedAngle > MAX_ANGLE) {
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold", "HighAngle"), event);
		}
	}

	public void Check2(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block target = player.getTargetBlock(null, 5);
		if(target == null) {
			return;
		}
		if (Utility.getMaterialName(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0.0D, 1.0D, 0.0D)).getLocation()).contains("air")) {
			if (!event.getBlock().getLocation().equals(target.getLocation()) && !event.isCancelled()
					&& target.getType().isSolid() && !target.getType().name().toLowerCase().contains("sign")
					&& !target.getType().toString().toLowerCase().contains("fence")
					&& player.getLocation().getY() > event.getBlock().getLocation().getY()) {
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold", "Impossible1"), event);
			}
		}
	}
}
