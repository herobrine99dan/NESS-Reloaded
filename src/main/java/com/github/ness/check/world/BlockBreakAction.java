package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.Utility;

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
		//Block target = e.getPlayer().getTargetBlock(Utility.occludingMaterials, 10);
		final double max = 5.4;
		final double placedAngle = Utility.getAngle(e.getPlayer(), block.getLocation(), null);
		if (xDiff > max || yDiff > max || zDiff > max) {
			flagEvent(e, " HighDistance");
			// if(player().setViolation(new Violation("BreakActions", "HighDistance")))
			// e.setCancelled(true);
		} else if (placedAngle > MAX_ANGLE) {
			flagEvent(e, " Angle: " + placedAngle);
			// if(player().setViolation(new Violation("BreakActions", "FalseAngle")))
			// e.setCancelled(true);
		}

	}
}
