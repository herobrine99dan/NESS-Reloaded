package com.github.ness.check.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.raytracer.RayCaster;

public class BlockBreakAction extends ListeningCheck<BlockBreakEvent> {
	private static final double MAX_ANGLE = Math.toRadians(90);
	public static final ListeningCheckInfo<BlockBreakEvent> checkInfo = CheckInfos.forEvent(BlockBreakEvent.class);

	public BlockBreakAction(ListeningCheckFactory<?, BlockBreakEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(BlockBreakEvent event) {
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		Block block = event.getBlock();
		double xDiff = Math.abs(values.getTo().getX() - block.getLocation().getX());
		double yDiff = Math.abs(values.getTo().getY() - block.getLocation().getY());
		double zDiff = Math.abs(values.getTo().getZ() - block.getLocation().getZ());
		final double max = 6;
		final double placedAngle = values.getHelper().getAngle(event.getPlayer(), block.getLocation());
		if (event.getBlock().isLiquid()) {
			flagEvent(event);
		}
		if (xDiff > max || yDiff > max || zDiff > max) {
			flagEvent(event, " HighDistance");
		} else if (placedAngle > MAX_ANGLE) {
			flagEvent(event, " Angle: " + placedAngle);
		} else {
			Location fixedEyeLocation = event.getPlayer().getEyeLocation().subtract(0.0D, 0.0, 0.0D);
			// int interactedBlockCorrect = traceLocation(fixedEyeLocation,
			// event.getBlock().getLocation(), 6,
			// event.getBlock());
			int interactedBlockCorrect = traceLocation1(event.getPlayer().getLocation().getDirection(),
					fixedEyeLocation, 6, event.getBlock());
			if (interactedBlockCorrect > 0) {
				this.flagEvent(event, "val: " + interactedBlockCorrect);
			}
		}
	}

	private int traceLocation1(Vector direction, Location from, float maxDistance, Block blockToFind) {
		int impossibleLocations = 0;
		for (double i = 0; i < maxDistance; i+=0.1) {
			Location newLoc = direction.clone().normalize().multiply(i).add(from.toVector()).toLocation(from.getWorld());
			if (newLoc.getBlock().equals(blockToFind)) {
				return impossibleLocations;
			}
			if (newLoc.getBlock().getType().isOccluding() && newLoc.getBlock().getType().isSolid()) {
				impossibleLocations++;
			}
		}
		return impossibleLocations;
	}
}
