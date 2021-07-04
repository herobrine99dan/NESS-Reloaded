package com.github.ness.check.movement.fly;

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
import com.github.ness.data.PlayerAction;

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class FlyInvalidClientGravity extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	private final int minAirTicks;
	private final double minBuffer;
	private final boolean useAbsoluteDifference;

	public FlyInvalidClientGravity(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		minAirTicks = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity().airTicks();
		minBuffer = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity().buffer();
		useAbsoluteDifference = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity()
				.useAbsoluteDifference();
	}

	private float lastDeltaY;
	private int airTicks;
	private double buffer;

	public interface Config {
		@DefaultInteger(2)
		int airTicks();

		@DefaultDouble(0)
		double buffer();

		@DefaultBoolean(false)
		boolean useAbsoluteDifference();
	}

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	@Override
	/**
	 * Powerful Y-Prediction check made with https://www.mcpk.wiki/wiki/ Loving
	 * those guys who made it. This is the young-brother of SpeedFriction :D
	 */
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		// Flying will be handled in another class. Same thing for Elytra. Lava and
		// Water are handled in Jesus
		if (player.isFlying() || values.getHelper().isPlayerUsingElytra(nessPlayer) || values.isAroundLiquids()) {
			return;
		}
		// Velocity won't be handled for now: when i finish the check, i will simply
		// handle it
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		//When you fly, the gravity is the one of the entity, TODO Make checks for that
		if(player.isInsideVehicle() || nessPlayer.milliSecondTimeDifference(PlayerAction.VEHICLEENTER) < 500) {
			return;
		}
		//TODO There is one false flag with jump boost because Minecraft (aka Shitcraft) rounds the number if it is very low
		final boolean onGround = isOnGround(event.getTo());

		float yDiff = (float) values.getyDiff();
		if (onGround) {
			yDiff = 0.0f;
		}
		if (!onGround) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		float motionY = lastDeltaY;
		motionY -= 0.08f;
		motionY *= 0.98f;
		float result = yDiff - motionY;
		if (useAbsoluteDifference) {
			result = Math.abs(result);
		}
		if (result > 0.001 && airTicks > minAirTicks) {
			if (++buffer > minBuffer) {
				this.flagEvent(event, "yResult: " + result + " airTicks: " + airTicks);
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		this.lastDeltaY = yDiff;
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
}
