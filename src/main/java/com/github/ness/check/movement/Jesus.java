package com.github.ness.check.movement;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class Jesus extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private final double maxYVariance;
	private float lastXZDist;
	private float lastYDist;
	private final double maxXZVariance;
	private final double maxHighDistanceWaterY;
	private float buffer;

	public Jesus(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxYVariance = this.ness().getMainConfig().getCheckSection().jesus().maxYVariance();
		this.maxXZVariance = this.ness().getMainConfig().getCheckSection().jesus().maxXZVariance();
		this.maxHighDistanceWaterY = this.ness().getMainConfig().getCheckSection().jesus().maxHighDistanceWaterY();
	}

	public interface Config {
		@DefaultDouble(0.022)
		double maxYVariance();

		@DefaultDouble(3)
		double minBufferToFlag();

		@DefaultDouble(0.022)
		double maxXZVariance();

		@DefaultDouble(0.322)
		double maxHighDistanceWaterY();

	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = nessPlayer.getMovementValues();
		if (movementValues.getHelper().hasflybypass(nessPlayer) || Utility.hasVehicleNear(p) || p.getAllowFlight()
				|| nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000
				|| nessPlayer.getAcquaticUpdateFixes().isRiptiding()) {
			return;
		}
		// We handle Prediction for Y Value
		double yDist = movementValues.getyDiff();
		double xzDist = movementValues.getXZDiff();
		if (movementValues.getHelper().isNearLava(event.getTo())) {
			handleLava(movementValues, event, nessPlayer);
		} else if (movementValues.getHelper().isNearWater(event.getTo())) {
			handleWater(movementValues, event, nessPlayer);
		}
		lastXZDist = (float) xzDist;
		lastYDist = (float) yDist;
	}

	public void handleWater(MovementValues values, Cancellable event, NessPlayer nessPlayer) {
		// First check
		float xzDist = (float) values.getXZDiff();
		float yDist = (float) values.getyDiff();

		float waterInertia = 0.8F;
		float acceleration = 0.02F;
		float depthStriderLVL = getDepthStrider(nessPlayer.getBukkitPlayer());
		if (depthStriderLVL > 3.0F)
			depthStriderLVL = 3.0F;
		if (depthStriderLVL > 0.0F) {
			final boolean sprinting = nessPlayer.getSprinting().get();
			float walkSpeed = (nessPlayer.getBukkitPlayer().getWalkSpeed() / 2f);
			float baseSpeed = sprinting ? walkSpeed + walkSpeed * 0.3f : walkSpeed;
			waterInertia += (0.546F - waterInertia) * depthStriderLVL / 3.0F;
			acceleration += (baseSpeed * 1.0F - acceleration) * depthStriderLVL / 3.0F;
		}
		float predictionXZ = lastXZDist * waterInertia;
		predictionXZ += acceleration;
		float predictionY = (lastYDist * 0.8f);
		float resultXZ = xzDist - predictionXZ;
		float resultY = Math.abs(yDist - predictionY);
		if (values.getHelper().isMathematicallyOnGround(values.getTo().getY())) {
			resultY = 0.0f;
		}
		if (values.isOnGroundCollider()) {
			resultY -= 0.05;
		}
		if (resultXZ > maxXZVariance) {
			if(++buffer > 3) {
			this.flag("HighVarianceXZ");
			}
			return;
		} else if (buffer > 0) {
			buffer -= 1;
		}
		if (resultY > maxYVariance) {
			if(++buffer > 3) {
			this.flag("HighVarianceY");
			}
			return;
		} else if (buffer > 0) {
			buffer -= 1;
		}
		// Second check
		if (yDist > maxHighDistanceWaterY && !values.isAroundLily() && !values.isGroundAround()) {
			if (values.hasBubblesColumns() == 1 && yDist > maxHighDistanceWaterY + 0.268) {
				this.flagEvent(event, "HighDistanceYBubble");
			} else {
				this.flagEvent(event, "HighDistanceY");
			}
		}
	}

	private float getDepthStrider(Player player) {
		if (player.getInventory().getBoots() != null) {
			return player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
		}
		return 0.0f;
	}

	public void handleLava(MovementValues values, Cancellable event, NessPlayer nessPlayer) {
		//TODO Lava check
	}
}
