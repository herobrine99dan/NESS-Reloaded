package com.github.ness.check.movement;

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

	
	//TODO New idea for Phase check: get direction vector beetween to and from locations
	//Then get midpoint and check if it is a solid block
	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Block b = event.getTo().clone().add(0, event.getPlayer().getEyeHeight(), 0).getBlock();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (event.getPlayer().getGameMode().name().contains("SPECTATOR")) {
			return;
		}
		Material material = this.ness().getMaterialAccess().getMaterial(b);
		boolean occluder = material.isOccluding() && material.isSolid() && !material.name().contains("STAINED");
		if (occluder && !Utility.hasVehicleNear(event.getPlayer()) && values.isGroundAround()
				&& !nessPlayer.isTeleported() && values.getXZDiff() > 0.25) {
			if (++buffer > 1) {
				flagEvent(event);
			}
		} else if (buffer > 0) {
			buffer -= 0.25;
		}
		if (values.isGroundAround()) {
			if (values.getyDiff() < -2 && event.getPlayer().getFallDistance() == 0) {
				flagEvent(event, "IllegalDist");
			}
		}
	}
}
