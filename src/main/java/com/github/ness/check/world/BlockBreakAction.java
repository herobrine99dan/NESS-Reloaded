package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

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
		if (xDiff > max || yDiff > max || zDiff > max) {
			flagEvent(event, " HighDistance");
		} else if (placedAngle > MAX_ANGLE) {
			flagEvent(event, " Angle: " + placedAngle);
		} else {
			Block targetBlock = event.getPlayer().getTargetBlock(null, 7);
			if (targetBlock.getType() != block.getType()) {
				final RayCaster customCaster = new RayCaster(event.getPlayer(), 6, RayCaster.RaycastType.BLOCK,
						this.ness()).compute();
				if (customCaster.getBlockFound() != null) {
					if (!customCaster.getBlockFound().equals(block)) {
						this.flagEvent(event, "targetBlock: " + targetBlock.getType().name() + " customRayCaster:  "
								+ customCaster.getBlockFound().getType().name());
					}
				}
			}
		}

	}
}
