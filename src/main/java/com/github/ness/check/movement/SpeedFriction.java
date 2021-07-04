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
import com.github.ness.utility.raytracer.rays.AABB;

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
	 * Powerful XZ-Prediction check made with https://www.mcpk.wiki/wiki/ 
	 * Loving those guys who made it.
	 */
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (player.isFlying() || values.getHelper().isPlayerUsingElytra(nessPlayer) || values.isAroundLiquids()) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		float xzDiff = (float) values.getXZDiff();
		final boolean sprinting = nessPlayer.getSprinting().get();
		final boolean sneaking = nessPlayer.getSneaking().get();
		this.player().sendDevMessage("xzDiff: " + (float) xzDiff + " speed: " + player.getWalkSpeed() + " sprint: "
				+ sprinting + " sneak: " + sneaking);
		final boolean isInWeb = isCollidingWithMaterial(event.getTo(), "WEB");
		if (!values.getHelper().isMathematicallyOnGround(values.getTo().getY())) {
			airTicks++;
			groundTicks = 0;
		} else {
			airTicks = 0;
			groundTicks++;
		}
		if (airTicks > 1) {
			float prediction = (lastDeltaXZ * 0.91f);
			float difference = xzDiff - prediction;
			float maxSpeed = sprinting ? 0.026f : 0.02f;
			//TODO There are some errors doing some calculations, so you get 0.0254f instead of 0.026f
			//this.player().sendDevMessage("AirSpeed diff: " + difference);
			if (difference > maxSpeed && prediction > 0.075) {
				if (++buffer > 1) {
					this.flagEvent(event);
				}
			} else if (buffer > 0) {
				buffer -= 0.5;
			}
		} else if (groundTicks > 2) {
			final Location underBlock = event.getTo().clone().add(0, -0.8, 0);
			float collidedBlockMultiplier = getCollidedBlockMultiplier(underBlock);
			float limit = 0.001f;
			float friction = getFrictionBlock(underBlock) * 0.91f;
			float momentum = (lastDeltaXZ * friction);

			float walkSpeed = (player.getWalkSpeed() / 2f);
			float baseSpeed = sprinting ? walkSpeed + walkSpeed * 0.3f : walkSpeed;
			float speedSlownessMultiplier = getSlownessAndSpeedEffectMultiplier(player);

			if (sneaking) {
				baseSpeed = walkSpeed * 0.3f;
			}
			float acceleration = (float) (baseSpeed * speedSlownessMultiplier * collidedBlockMultiplier
					* (0.16277f / Math.pow(friction, 3)));
			if (isInWeb) {
				// momentum = lastDeltaXZ * 0.25f; // Minecraft just multiply the motion, and
				// then it set momentum to 0
				// acceleration *= 0.25f;
				acceleration *= 0.25f;
				if (!sprinting) {
					xzDiff /= 2.0f; //Fixing retarded Minecraft not sending position packet is xzDiff is low
				}
			}
			float prediction = (momentum + acceleration);
			final float difference = (float) (xzDiff - prediction);
			if (difference > 0.01) {
				this.player().sendDevMessage("CHEATS! lastDeltaXZ: " + lastDeltaXZ + " acceleration: " + acceleration
						+ " difference: " + difference);
			}
		}
		this.lastDeltaXZ = xzDiff;
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

	private AABB getBoundingBoxFromBlock(Block block) {
		Material material = block.getType();
		String name = material.name();
		if (material.isSolid() && material.isOccluding()) {
			return new AABB(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 1,
					block.getZ() + 1);
		}
		if (name.contains("WEB")) {
			return new AABB(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 1,
					block.getZ() + 1);
		}
		return null;
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
			return 0.8f; // or 0,68, default 0.8
		} else {
			return 0.6f;
		}
	}
}
