package com.github.ness.check.movement.fly;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class FlyInvalidClientGravity extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	private final int minAirTicks;
	private final double minBuffer;
	private final boolean useAbsoluteDifference;
	private final boolean usePlayerIsOnGround;

	public FlyInvalidClientGravity(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		minAirTicks = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity().airTicks();
		minBuffer = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity().buffer();
		useAbsoluteDifference = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity()
				.useAbsoluteDifference();
		usePlayerIsOnGround = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity()
				.usePlayerIsOnGroundMethod();
	}

	private int airTicks;
	private double buffer;

	public interface Config {
		@DefaultInteger(2)
		int airTicks();

		@DefaultDouble(0)
		double buffer();

		@DefaultBoolean(false)
		boolean useAbsoluteDifference();

		@DefaultBoolean(true)
		@ConfComments({ "Player.isOnGround() is suggested because the other method that",
				" NESS Reloaded currently has is a math method (ground = (y%0,015625) < 0.001)",
				"and, while this method works correctly, it can produce disablers.",
				" Also invalid values of Player.isOnGround() will be detected by FlyFalseGround." })
		boolean usePlayerIsOnGroundMethod();
	}

	/**
	 * Powerful Y-Prediction check made with https://www.mcpk.wiki/wiki/ Loving
	 * those guys who made it. This is the young-brother of SpeedFriction :D
	 */
	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		// Flying will be handled in another class. Same thing for Elytra. Lava and
		// Water are handled in Jesus
		if (player.isFlying() || player.getAllowFlight() || values.getHelper().isPlayerUsingElytra(nessPlayer)
				|| values.isNearLiquid()) {
			return;
		}
		// Velocity won't be handled for now: when i finish the check, i will simply
		// handle it
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1000) {
			return;
		}
		// When you fly, the gravity is the one of the entity, TODO Make checks for that
		if (player.isInsideVehicle() || nessPlayer.milliSecondTimeDifference(PlayerAction.VEHICLEENTER) < 500) {
			return;
		}
		if (this.getMaterialAccess().getMaterial(event.getTo()).name().contains("LADDER")
				|| this.getMaterialAccess().getMaterial(event.getFrom()).name().contains("LADDER")) {
			return;
		}
		// TODO There is one false flag with jump boost because Minecraft (aka
		// Shitcraft) rounds the number if it is very low

		// Sometimes this onGround Value is too
		final boolean onGroundFixer = usePlayerIsOnGround ? nessPlayer.isOnGroundPacket() : isOnGround1(event.getTo());
		// boolean onGround = !isOnGround(event.getTo()) ? onGroundFixer : true;
		boolean onGround = !onGroundFixer ? isOnGround(event.getTo()) : true; // Little micro optimization: if the
																				// player isn't onGround, then use the
																				// boolean value from
																				// isOnGround(Location loc)

		float yDiff = (float) values.getyDiff();
		if (onGround) {
			yDiff = 0.0f;
		}
		if (!onGround) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		// To make a prediction we get the last Y value and we try to predict the next.
		float motionY = nessPlayer.getLastYDeltaPrediction();
		if (this.getMaterialAccess().getMaterial(event.getTo()).name().contains("WEB")) {
			motionY = 0.31f; //Web gravity is too small and there are issues with PlayerMoveEvent, so we just set a limit for this
		}
		motionY -= 0.08f; // Gravity
		motionY *= 0.98f; // Air Resistance

		float result = yDiff - motionY;
		// this.player().sendDevMessage("result: " + result + " onGround: " + onGround +
		// " airTicks: " + airTicks);
		if (useAbsoluteDifference) {
			result = Math.abs(result);
		}
		// Here we use 0.005 because in newer Minecraft versions the yMotion is clamped
		// if it's low.
		if (result > 0.005 && airTicks > minAirTicks) { // After some tests i discovered that minAirTicks should be two
			if (++buffer > minBuffer) { // And that we don't need a buffer
				spawnArmorStand("To", event.getTo()); // These armor stands are just for me to see the exact position
														// where false flags happens
				spawnArmorStand("From", event.getFrom());
				this.flag("yResult: " + result + " ground: " + onGround + " yDiff: " + yDiff + " motionY: " + motionY);
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		nessPlayer.setLastYDeltaPrediction(yDiff);
	}

	private void spawnArmorStand(String name, Location loc) {
		// Spawn the stands only in dev mode
		if (this.player().isDevMode()) {
			ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			stand.setGravity(false);
			stand.setAI(false);
			stand.setSmall(true);
			stand.setCustomNameVisible(true);
			stand.setCustomName(name);
		}
	}

	private boolean isOnGround1(Location loc) {
		return this.player().getMovementValues().getHelper().isMathematicallyOnGround(loc.getY());
	}

	private boolean isOnGround(Location loc) {
		final Location cloned = loc.clone();
		double limit = 0.28;
		for (double x = -limit; x <= limit; x += 0.05) {
			for (double z = -limit; z <= limit; z += 0.05) {
				for (double y = -0.3; y <= 0; y += 0.05) {
					if (isBlockConsideredOnGround(cloned.clone().add(x, y, z))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isBlockConsideredOnGround(Location loc) {
		Material material = this.getMaterialAccess().getMaterial(loc);
		String name = material.name();
		if (material.isSolid() || name.contains("SNOW") || name.contains("CARPET") || name.contains("SCAFFOLDING")
				|| name.contains("SKULL") || name.contains("WALL") || name.contains("LILY")) {
			return true;
		}
		return false;
	}
}
