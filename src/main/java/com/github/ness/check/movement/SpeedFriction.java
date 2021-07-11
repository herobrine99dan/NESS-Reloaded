package com.github.ness.check.movement;

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

public class SpeedFriction extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private int airTicks, groundTicks;
	private float lastDeltaXZ;
	private int buffer;

	public SpeedFriction(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	/**
	 * Powerful XZ-Prediction check made with https://www.mcpk.wiki/wiki/ Loving
	 * those guys who made it.
	 */
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (player.isFlying() || values.getHelper().isPlayerUsingElytra(nessPlayer) || values.isNearLiquid()) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		float xzDiff = (float) values.getXZDiff();
		float yDiff = (float) values.getyDiff();
		final boolean sprinting = nessPlayer.getSprinting().get();
		final boolean sneaking = nessPlayer.getSneaking().get();
		final boolean isInWeb = isCollidingWithMaterial(event.getTo(), "WEB");
		if (!values.getHelper().isMathematicallyOnGround(values.getFrom().getY())) {
			airTicks++;
			groundTicks = 0;
		} else {
			airTicks = 0;
			groundTicks++;
		}
		final Location underBlock = event.getTo().clone().add(0, -1.0, 0);
		float friction = 0.91f * getFrictionBlock(underBlock);
		float walkSpeed = (player.getWalkSpeed() / 2f);
		float baseSpeed = sprinting ? walkSpeed + walkSpeed * 0.3f : walkSpeed;
		float speedSlownessMultiplier = getSlownessAndSpeedEffectMultiplier(player);
		float collidedBlockMultiplier = getCollidedBlockMultiplier(underBlock);
		if (sneaking) {
			baseSpeed = walkSpeed * 0.3f;
		}
		float movementSpeed = (float) 0.0f;
		if (groundTicks > 0) {
			movementSpeed = (float) (baseSpeed * speedSlownessMultiplier * collidedBlockMultiplier
					* (0.16277136f / Math.pow(friction, 3)));
		} else if (airTicks > 0) {
			movementSpeed = sprinting ? 0.026f : 0.02f;
		}
		if (yDiff == 0.42f) {
			movementSpeed += 0.2f;
		}
		float prediction = this.lastDeltaXZ * friction + movementSpeed;
		float result = xzDiff - prediction;
		this.player().sendDevMessage("xzDiff: " + roundNumber(xzDiff) + " yDiff: " + roundNumber(yDiff) + " prediction:"
				+ roundNumber(prediction) + " Result: " + roundNumber(result));
		this.lastDeltaXZ = xzDiff;
	}

	private double roundNumber(double n) {
		return Math.round(n * 1000.0) / 1000.0;
	}

	private boolean isCollidingWithMaterial(Location loc, String name) {
		final Location cloned = loc.clone().add(0, -0.125, 0);
		final double limit = 0.3;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				Block block = cloned.clone().add(x, 0, z).getBlock();
				if (block.getType().name().contains(name)) {
					return true;
				}
				block = cloned.clone().add(x, 0.125, z).getBlock();
				if (block.getType().name().contains(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private float getSlownessAndSpeedEffectMultiplier(Player player) {
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
			final float entityMotionY = 0.0f;
			float mult = 0.4f + entityMotionY * 0.2f;
			return mult;
		}
		return 1f;
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
}
