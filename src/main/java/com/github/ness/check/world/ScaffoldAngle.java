package com.github.ness.check.world;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class ScaffoldAngle extends ListeningCheck<BlockPlaceEvent> {
	private static final double MAX_ANGLE = Math.toRadians(90);

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEvent(BlockPlaceEvent.class);

	public ScaffoldAngle(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(BlockPlaceEvent event) {
		BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
		if (placedFace == null) {
			return;
		}
		if(this.getMaterialAccess().getMaterial(event.getBlockAgainst()).name().contains("SCAFFOLD")) { //Scaffoldings can be placed anywhere
			return;
		}
		NessPlayer nessPlayer = player();
		final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
		float placedAngle = nessPlayer.getMovementValues().getTo().getDirectionVector()
				.angle(placedVector);
		if (placedAngle > MAX_ANGLE) {
			flagEvent(event, "placedAngle: " + placedAngle + " pitch: " + event.getPlayer().getLocation().getPitch());
		}
	}

}
