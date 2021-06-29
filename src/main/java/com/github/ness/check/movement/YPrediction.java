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
import com.github.ness.data.PlayerAction;

public class YPrediction extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private float lastDeltaY;
	private int buffer;

	public YPrediction(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
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
		// TODO We have to handle Slimes
		final boolean sprinting = nessPlayer.getSprinting().get();
		final boolean sneaking = nessPlayer.getSneaking().get();
		final boolean onGround = isOnGround(event.getTo());

		float yDiff = (float) values.getyDiff();		
		if (onGround) {
			yDiff = 0.0f;
		}
		// this.player()
		// .sendDevMessage("yDiff: " + yDiff + " collidedHorizontally: " +
		// isCollidedHorizontally(event.getTo()));
		float motionY = lastDeltaY;
		motionY -= 0.08f;
		motionY *= 0.98f;
		float result = yDiff - motionY;
		if (result > 0.001 && !onGround) {
			this.player().sendDevMessage("yResult: " + result + "yDiff: " + yDiff);
		}
		this.lastDeltaY = yDiff;
	}

	public boolean isCollidedHorizontally(Location loc) {
		double limit = 0.5;
		for (double x = -limit; x < limit + limit; x += limit) {
			for (double z = -limit; z < limit + limit; z += limit) {
				Block block = loc.clone().add(x, 0, z).getBlock();
				if (block.getType().isSolid() && block.getType().isOccluding()) {
					return true;
				}
				block = loc.clone().add(x, 1.3, z).getBlock();
				if (block.getType().isSolid() && block.getType().isOccluding()) {
					return true;
				}
			}
		}
		return false;
	}

	// TODO Update this method
	private boolean isOnGround(Location loc) {
		final Location cloned = loc.clone();
		double limit = 0.33;
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
		if (material.isSolid() || name.contains("SNOW") || name.contains("CARPET") || name.contains("SCAFFOLDING")
				|| name.contains("SKULL") || name.contains("LADDER") || name.contains("WEB")) {
			return true;
		}
		return false;
	}
}