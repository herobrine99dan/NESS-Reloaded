package com.github.ness.check.movement.fly;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.MultipleListeningCheck;
import java.util.List;
import java.util.ArrayList;
import com.github.ness.check.MultipleListeningCheckFactory;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class FlyInvalidClientGravity extends MultipleListeningCheck {

	public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListener(PlayerMoveEvent.class,
			PlayerVelocityEvent.class);
	private final int minAirTicks;
	private final double minBuffer;
	private final boolean useAbsoluteDifference;
	private final List<Float> velocitys = new ArrayList<>();

	public FlyInvalidClientGravity(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		minAirTicks = ness().getMainConfig().getCheckSection().flyInvalidClientGravity().airTicks();
		minBuffer = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity().buffer();
		useAbsoluteDifference = this.ness().getMainConfig().getCheckSection().flyInvalidClientGravity()
				.useAbsoluteDifference();
	}

	private int airTicks;
	private float lastYDelta;
	private double buffer;

	public interface Config {
		@DefaultInteger(2)
		int airTicks();

		@DefaultDouble(0)
		double buffer();

		@DefaultBoolean(false)
		boolean useAbsoluteDifference();
	}

	/**
	 * Simple Y-Gravity Check.
         * @param event
	 */

	@Override
	protected void checkEvent(Event event) {
		Player player = ((PlayerEvent) event).getPlayer();
		if (event instanceof PlayerVelocityEvent) {
                    onVelocity((PlayerVelocityEvent) event);
                }
		if (event instanceof PlayerMoveEvent) {
                    onMove((PlayerMoveEvent) event);
                }
	}

	private void onVelocity(PlayerVelocityEvent e) {
		velocitys.add((float) e.getVelocity().getY());
	}

	private void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		// Flying will be handled in another class. Same thing for Elytra. Lava and
		// Water are handled in Jesus
		if (player.getAllowFlight() || values.getHelper().hasflybypass(nessPlayer)
				|| values.isNearLiquid()) {
			return;
		}
		// When you fly, the gravity is the one of the entity
		if (player.isInsideVehicle() || nessPlayer.milliSecondTimeDifference(PlayerAction.VEHICLEENTER) < 500) {
			return;
		}
		// Ladders aren't handled by this check
		if (this.getMaterialAccess().getMaterial(event.getTo().clone().subtract(0, 0.005, 0)).name().contains("LADDER")
				|| this.getMaterialAccess().getMaterial(event.getFrom().clone().subtract(0, 0.005, 0)).name().contains(
						"LADDER")
				|| this.getMaterialAccess().getMaterial(event.getFrom()).name().contains("LADDER")
				|| this.getMaterialAccess().getMaterial(event.getTo()).name().contains("LADDER")) {
			return;
		}
		// TODO There is one false flag with jump boost because Minecraft rounds the number if it is very low
		boolean onGround = isOnGround(event.getTo()); //|| isOnGround(event.getFrom());
		float yDiff = onGround ? 0.0f : (float) values.getyDiff();
		if (!onGround) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		// To make a prediction we get the last Y value and we try to predict the next.
		float motionY = lastYDelta;
		if (this.getMaterialAccess().getMaterial(event.getTo()).name().contains("WEB")
				|| this.getMaterialAccess().getMaterial(event.getFrom()).name().contains("WEB")) {
			motionY = 0.2f; // Web gravity is too small and there are issues with PlayerMoveEvent, so we
							// just set a limit for this
		}
		motionY -= 0.08f; // Gravity
		motionY *= 0.98f; // Air Resistance
		float yVelocity = 0.0f;
		List<Float> clonedVelocitys = new ArrayList<>(velocitys); //Prevent ConcurrentModificationExceptions
		for(float f : clonedVelocitys) {
			if(Math.abs(yDiff - f) < 0.005) {
				velocitys.remove(f);
				motionY = f;
				yVelocity = f;
			}
		}
		float result = useAbsoluteDifference ? Math.abs(yDiff - motionY) : yDiff - motionY;
		if (motionY > 0.005 && result > 0.003 && airTicks > minAirTicks) { // After some tests i discovered that minAirTicks should be two
			if (++buffer > minBuffer) { // And that we don't need a buffer
				spawnArmorStand("To", event.getTo()); // These armor stands are just for me to see the exact position
														// where false flags happens
				spawnArmorStand("From", event.getFrom());
				this.flag("yResult: " + result + " ground: " + onGround + " yDiff: " + yDiff + " motionY: " + motionY);
				this.player().sendDevMessage("yVelocity: " + yVelocity + " lastYDelta: " + lastYDelta);
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		lastYDelta = yDiff;
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

	private boolean isOnGround(Location loc) {
		double limit = 0.3; //TODO Maybe this fix allows some little WallClimb that glitch you in the wall
		for (double x = -limit; x <= limit; x += limit) {
			for (double z = -limit; z <= limit; z += limit) {
				if (isBlockConsideredOnGround(loc.clone().add(x, -0.501, z))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isBlockConsideredOnGround(Location loc) {
		Material material = this.getMaterialAccess().getMaterial(loc);
		String name = material.name();
                
		return material.isSolid() || name.contains("SNOW") || name.contains("CARPET") || name.contains("SCAFFOLDING")
                        || name.contains("SKULL") || name.contains("WALL") || name.contains("LILY");
	}
}
