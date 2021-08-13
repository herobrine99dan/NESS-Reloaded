package com.github.ness.check.movement.predictions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.raytracer.rays.AABB;

public class SpeedOnGroundFriction extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private int groundTicks;
	private float lastDeltaXZ;

	public SpeedOnGroundFriction(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	/**
	 * Powerful Air XZ-Prediction check made with https://www.mcpk.wiki/wiki/ Loving
	 * those guys who made it.
	 */
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (values.getHelper().hasflybypass(nessPlayer)
				|| values.getHelper().isNearLiquid(event.getTo()) || values.getHelper().isNearLiquid(event.getFrom())) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		float xzDiff = (float) values.getXZDiff();
		final boolean lastOnGround = values.getHelper().isMathematicallyOnGround(values.getFrom().getY());
		final boolean onGround = values.getHelper().isMathematicallyOnGround(values.getTo().getY());
		final boolean sprinting = nessPlayer.getSprinting().get();
		final boolean sneaking = nessPlayer.getSneaking().get();
		final boolean isOnSoulSand = isOnSoulSand(player);

		if (lastOnGround && onGround) {
			groundTicks++;
		} else {
			groundTicks = 0;
		}
		if (groundTicks > 2) {
			float momentum = 0.91f * getFrictionBlock(event.getFrom().clone().add(0, -1.0, 0));
			float baseSpeed = getBaseSpeed(player, sprinting);
			float acceleration = (float) (baseSpeed * getEffectMultipliers(player)
					* (0.16277f / Math.pow(momentum, 3)));
			float prediction = (lastDeltaXZ * momentum) + acceleration; // Momentum + acceleration
			float result = (xzDiff - prediction) * 0.98f;
			if(result > 0.005) {
				this.player().sendDevMessage("result: " + result);
			}
		}
		this.lastDeltaXZ = xzDiff;
	}

	private float getBaseSpeed(Player player, boolean sprinting) {
		float walkSpeed = (player.getWalkSpeed() / 2f);
		float baseSpeed = sprinting ? walkSpeed + walkSpeed * 0.3f : walkSpeed;
		return baseSpeed;
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
			return 0.8f;
		} else if (name.contains("AIR")) {
			return 1f;
		} else {
			return 0.6f; // Normal OnGround friction
		}
	}

	private boolean isOnSoulSand(Player player) {
		// Better detection with boundingboxes
		AABB playerBB = AABB.from(player, null, 0);
		final Location cloned = player.getLocation().clone().add(0, 0.1, 0);
		final double limit = 0.3;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				Block block = cloned.clone().add(x, 0, z).getBlock();
				String name = this.getMaterialAccess().getMaterial(block).name();
				if (name.contains("SOUL") && name.contains("SAND")) {
					AABB bb = new AABB(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 1,
							block.getZ() + 1);
					if (bb.collides(playerBB)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private float getEffectMultipliers(Player player) {
		float speed = 0;
		float slowness = 0;
		for (PotionEffect pe : player.getActivePotionEffects()) {
			String name = pe.getType().getName();
			if (name.equalsIgnoreCase("SLOW")) { // equalsIgnoreCase allows us better performance (+37%
													// of perfomance)
				slowness = pe.getAmplifier() + 1;
			}
			if (name.equalsIgnoreCase("SPEED")) {
				speed = pe.getAmplifier() + 1;
			}
		}
		float speedSlownessMultiplier = (1 + 0.2f * speed) * (1 - 0.15f * slowness);
		return speedSlownessMultiplier;
	}

	private float getCollidedBlockMultiplier(Location loc) {
		String name = this.getMaterialAccess().getMaterial(loc).name();
		if (name.contains("SOUL") && name.contains("SAND")) {
			return 0.4f;
		} else if (name.contains("SLIME")) {
			final float entityMotionY = 0;
			float mult = 0.4f + entityMotionY * 0.2f;
			return mult;
		}
		return 1f;
	}

	private double roundNumber(double n) {
		return Math.round(n * 1000.0) / 1000.0;
	}
}
