package com.github.ness.check.movement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import com.github.ness.utility.raytracer.rays.AABB;

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
		Block block = event.getTo().clone().add(0, 0, 0).getBlock();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (event.getPlayer().getGameMode().name().contains("SPECTATOR") || nessPlayer.isTeleported()
				|| nessPlayer.getBukkitPlayer().isInsideVehicle()) {
			return;
		}
		Material material = this.ness().getMaterialAccess().getMaterial(block);
		boolean occluder = material.isOccluding() && material.isSolid() && !material.name().contains("STAINED");
		// nessPlayer.sendDevMessage("Phase1, occluder: " + occluder);
		// nessPlayer.sendDevMessage("groundAround: " + values.isGroundAround() + "
		// xzDiffCondition: " + (values.getXZDiff() > 0.1));
		if (occluder && values.isGroundAround() && values.getXZDiff() > 0.07) {
			nessPlayer.sendDevMessage("Phase2");
			if (++buffer > 1) {
				flagEvent(event);
			}
		} else if (buffer > 0) {
			buffer -= 0.25;
		}
	}

	private void Check1(PlayerMoveEvent e) {
		// Better detection with boundingboxes
		AABB playerBB = AABB.from(e.getPlayer(), null, 0);
		List<AABB> bbs = getBoundingBoxesAround(e.getPlayer().getLocation());
		for(AABB bb : bbs) {
			if(bb.collides(playerBB)) {
				this.player().sendDevMessage("Collides detected!" + bb);
			}
		}
	}

	private List<AABB> getBoundingBoxesAround(Location loc) {
		List<AABB> bbs = new ArrayList<AABB>();
		final Location cloned = loc.clone().add(0, 0.1, 0);
		final double limit = 0.3;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
					Block block = cloned.clone().add(x, 0, z).getBlock();
					if(block.getType().isSolid() && block.getType().isOccluding()) {
						AABB bb = getBoundingBoxFromBlock(block);
						if(!bbs.contains(bb)) {
							bbs.add(getBoundingBoxFromBlock(block));
						}
					}
					block = cloned.clone().add(x, 1, z).getBlock();
					if(block.getType().isSolid() && block.getType().isOccluding()) {
						AABB bb = getBoundingBoxFromBlock(block);
						if(!bbs.contains(bb)) {
							bbs.add(getBoundingBoxFromBlock(block));
						}
					}
			}
		}
		return bbs;
	}

	private AABB getBoundingBoxFromBlock(Block block) {
		Material material = block.getType();
		if (material.isSolid() && material.isOccluding()) {
			return new AABB(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 1,
					block.getZ() + 1);
		}
		return null;
	}

	public Set<Block> isColliding(Location to) {
		Set<Block> result = new HashSet<Block>();
		double radius = 0.3; // 0.3 is good
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
