package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class BlockBreakAction extends ListeningCheck<BlockBreakEvent> {
	private static final double MAX_ANGLE = Math.toRadians(90);
	public static final ListeningCheckInfo<BlockBreakEvent> checkInfo = CheckInfos.forEvent(BlockBreakEvent.class);

	public BlockBreakAction(ListeningCheckFactory<?, BlockBreakEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(BlockBreakEvent e) {
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		Block block = e.getBlock();
		double xDiff = Math.abs(values.getTo().getX() - block.getLocation().getX());
		double yDiff = Math.abs(values.getTo().getY() - block.getLocation().getY());
		double zDiff = Math.abs(values.getTo().getZ() - block.getLocation().getZ());
		final double max = 5.4;
		final double placedAngle = values.getHelper().getAngle(nessPlayer, block.getLocation());
		if (xDiff > max || yDiff > max || zDiff > max) {
			flagEvent(e, " HighDistance");
		} else if (placedAngle > MAX_ANGLE) {
			flagEvent(e, " Angle: " + placedAngle);
		} else {
			Block targetBlock = e.getPlayer().getTargetBlock(MovementValues.getTrasparentMaterials(), 7);
			if (targetBlock.getType() != block.getType()) {
				flag("RayTrace failed: targetBlock: " + targetBlock.getType() + " block: " + block.getType());
			}
		}

	}
}
