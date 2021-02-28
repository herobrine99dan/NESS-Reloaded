package com.github.ness.check.movement;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.Utility;

public class Phase extends ListeningCheck<PlayerMoveEvent> {

	private double buffer = 0;

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public Phase(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Check(event);
		Check1(event);
	}

	private void Check(PlayerMoveEvent event) {
		Block block = event.getTo().clone().add(0, event.getPlayer().getEyeHeight(), 0).getBlock();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (event.getPlayer().getGameMode().name().contains("SPECTATOR")) {
			return;
		}
		Material material = this.ness().getMaterialAccess().getMaterial(block);
		boolean occluder = material.isOccluding() && material.isSolid() && !material.name().contains("STAINED");
		if (occluder && !Utility.hasVehicleNear(event.getPlayer()) && values.isGroundAround()
				&& !nessPlayer.isTeleported() && values.getXZDiff() > 0.25) {
			if (++buffer > 1) {
				flagEvent(event);
			}
		} else if (buffer > 0) {
			buffer -= 0.25;
		}
	}

	private void Check1(PlayerMoveEvent e) {
		int colliders = 0;
		for (Block block : isColliding(e.getTo())) {
			if (block.getType().isSolid() && block.getType().isOccluding()) {
				colliders++;
			}
		}
		//this.player().sendDevMessage("PhaseColliders: " + colliders);
	}

	public Set<Block> isColliding(Location to) {
		Set<Block> result = new HashSet<Block>();
		double radius = 0.3; //0.3 is good
		result.add(to.clone().add(radius, 0, radius).getBlock());
		result.add(to.clone().add(-radius, 0, -radius).getBlock());

		result.add(to.clone().add(-radius, 0, radius).getBlock());
		result.add(to.clone().add(radius, 0, -radius).getBlock());

		result.add(to.clone().add(radius, 0, -radius).getBlock());
		result.add(to.clone().add(-radius, 0, radius).getBlock());

		result.add(to.clone().add(radius, 0, 0).getBlock());
		result.add(to.clone().add(-radius, 0, 0).getBlock());

		result.add(to.clone().add(0, 0, radius).getBlock());
		result.add(to.clone().add(0, 0, -radius).getBlock());
		return result;
	}

}
