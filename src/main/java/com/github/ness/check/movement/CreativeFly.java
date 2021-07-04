package com.github.ness.check.movement;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class CreativeFly extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public CreativeFly(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private float lastMotionXZ, lastMotionY;
	private int flyingTicks, buffer;

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		MovementValues values = this.player().getMovementValues();
		float xzDiff = (float) values.getXZDiff();
		float yDiff = (float) values.getyDiff();

		Player player = event.getPlayer();
		if (player.isFlying()) {
			flyingTicks++;
		} else {
			flyingTicks = 0;
		}
		if (flyingTicks > 1) {
			flyingTicks -= 2;
		}
		boolean onGround = isOnGround(event.getTo());
		if (flyingTicks > 20 && !values.isOnGroundCollider()) {
			final Location underBlock = event.getTo().clone().add(0, -0.8, 0);
			float friction = getFrictionBlock(underBlock);
			float f1 = 0.16277136F / (friction * friction * friction);
			float speedXZ = onGround ? 0.1F * f1 : 0.02F;
			float predictedXZ = (lastMotionXZ * friction) + speedXZ;
			float predictedY = lastMotionY * friction;
			float resultXZ = xzDiff - predictedXZ;
			float resultY = yDiff - predictedY;
			player.sendMessage("resultXZ: " + resultXZ + " resultY: " + resultY);
			
		} else if (buffer > 0) {
			buffer--;
		}
		this.lastMotionXZ = xzDiff;
	}

	private boolean isOnGround(Location loc) {
		final Location cloned = loc.clone();
		double limit = 0.30;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				if (isBlockConsideredOnGround(cloned.clone().add(x, -0.15, z))
						|| isBlockConsideredOnGround(cloned.clone().add(x, -0.2, z))
						|| isBlockConsideredOnGround(cloned.clone().add(x, -0.25, z))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isBlockConsideredOnGround(Location loc) {
		Block block = loc.getBlock();
		Material material = this.getMaterialAccess().getMaterial(block);
		String name = material.name();
		// TODO Walls aren't handled correctly
		if (material.isSolid() || name.contains("SNOW") || name.contains("CARPET") || name.contains("SCAFFOLDING")
				|| name.contains("SKULL") || name.contains("LADDER") || name.contains("WEB") || name.contains("WALL")) {
			return true;
		}
		return false;
	}

	private float getFrictionBlock(Location loc) {
		Material material = this.getMaterialAccess().getMaterial(loc);
		final String name = material.name();
		final boolean isIce = name.contains("ICE");
		if (name.contains("BLUE") && isIce) {
			return 0.989f;
		} else if (isIce) {
			return 0.98f;
		} else if (name.contains("SLIME")) {
			return 0.8f; // or 0,68, default 0.8
		} else if (name.contains("AIR")) {
			return 1f;
		} else {
			return 0.6f;
		}
	}

}
