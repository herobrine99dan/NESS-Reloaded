package com.github.ness.check.movement;

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

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class Jesus extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private double maxYVariance;
	private double lastXZDist;
	private double lastYDist;
	private double maxXZVariance;
	private double maxHighDistanceY;
	private double liquidGravity;
	private double noGravityBuffer;
	private boolean useImpossibleYPattern, useNoGravityYCheck;
	private double waterFriction, lavaFriction;

	public Jesus(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxYVariance = this.ness().getMainConfig().getCheckSection().jesus().maxYVariance();
		this.liquidGravity = this.ness().getMainConfig().getCheckSection().jesus().liquidGravity();
		this.waterFriction = this.ness().getMainConfig().getCheckSection().jesus().waterFriction();
		this.maxXZVariance = this.ness().getMainConfig().getCheckSection().jesus().maxXZVariance();
		this.lavaFriction = this.ness().getMainConfig().getCheckSection().jesus().lavaFriction();
		this.useNoGravityYCheck = this.ness().getMainConfig().getCheckSection().jesus().useNoGravityYCheck();
		this.useImpossibleYPattern = this.ness().getMainConfig().getCheckSection().jesus().useImpossibleYPattern();
		this.maxHighDistanceY = this.ness().getMainConfig().getCheckSection().jesus().maxHighDistanceY();
	}

	public interface Config {
		@DefaultDouble(0.2)
		double maxYVariance();

		@DefaultDouble(0.14)
		double maxXZVariance();

		@DefaultDouble(0.302)
		double maxHighDistanceY();

		@DefaultDouble(-0.02)
		double liquidGravity();

		@DefaultDouble(0.8)
		double waterFriction();

		@DefaultDouble(0.5)
		double lavaFriction();

		@DefaultBoolean(true)
		boolean useImpossibleYPattern();

		@DefaultBoolean(true)
		boolean useNoGravityYCheck();
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = nessPlayer.getMovementValues();
		if (movementValues.getHelper().hasflybypass(nessPlayer) || Utility.hasVehicleNear(p) || p.getAllowFlight()
				|| nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		// We handle Prediction for Y Value
		double yDist = movementValues.getyDiff();
		double xzDist = movementValues.getXZDiff();
		if (event.getTo().clone().add(0, -0.1, 0).getBlock().isLiquid() && event.getFrom().getBlock().isLiquid()
				&& movementValues.getHelper().isNearLava(event.getTo())) {
			handleLava(movementValues, event, nessPlayer);
		} else if (event.getTo().clone().add(0, -0.1, 0).getBlock().isLiquid() && event.getFrom().getBlock().isLiquid()
				&& movementValues.getHelper().isNearWater(event.getTo())) {
			handleWater(movementValues, event, nessPlayer);
		}
		lastXZDist = xzDist;
		lastYDist = yDist;
	}

	public void handleWater(MovementValues values, Cancellable event, NessPlayer nessPlayer) {
		double yDist = values.getyDiff();
		double predictedY = lastYDist * waterFriction;
		predictedY -= liquidGravity; // We handle Prediction for XZ Values
		double resultY = yDist - predictedY;
		double xzDist = values.getXZDiff();
		double predictedXZ = lastXZDist * waterFriction;
		double resultXZ = xzDist - predictedXZ;
		// nessPlayer.sendDevMessage("WaterTicks: " + this.liquidTicks + " resultY: " +
		// resultY);
		if (!values.isOnGroundCollider() && !values.isAroundLily()) {
			if (yDist > maxHighDistanceY && !values.isGroundAround()) {
				if (values.hasBubblesColumns() == 1 && yDist > maxHighDistanceY + 0.268) {
					this.flagEvent(event, "HighDistanceYBubble");
				} else {
					this.flagEvent(event, "HighDistanceY");
				}
			} else if (resultY > maxYVariance && !values.isGroundAround()) {
				this.flagEvent(event, "HighVarianceY: " + (float) resultY);
			} else if (resultXZ > maxXZVariance) {
				this.flagEvent(event, "HighDistanceXZ: " + resultXZ);
			} else if (yDist == 0.0 && useNoGravityYCheck && xzDist > 0.05 && !values.hasBlockNearHead()) {
				if (++noGravityBuffer > 4) {
					this.flagEvent(event, "NoGravityY");
				}
			} else if (Double.toString(Math.abs(yDist)).contains("00000000") && useImpossibleYPattern) {
				this.flagEvent(event, "ImpossibleYPattern");
			} else if (noGravityBuffer > 0) {
				noGravityBuffer -= 0.25;
			}
		}
	}

	public void handleLava(MovementValues values, Cancellable event, NessPlayer nessPlayer) {
		double yDist = values.getyDiff();
		double predictedY = lastYDist * lavaFriction;
		predictedY -= liquidGravity; // We handle Prediction for XZ Values
		double resultY = yDist - predictedY;
		double xzDist = values.getXZDiff();
		double predictedXZ = lastXZDist * lavaFriction;
		double resultXZ = xzDist - predictedXZ;
		// nessPlayer.sendDevMessage("LavaTicks: " + this.liquidTicks + " resultY: " +
		// resultY);
		if (!values.isOnGroundCollider() && !values.isAroundLily()) {
			if (yDist > maxHighDistanceY) {
				if (values.hasBubblesColumns() == 1 && yDist > maxHighDistanceY + 0.268) {
					this.flagEvent(event, "HighDistanceYBubble");
				} else {
					this.flagEvent(event, "HighDistanceY");
				}
			} else if (resultY > maxYVariance + 0.25) {
				this.flagEvent(event, "HighVarianceY");
			} else if (resultXZ > (maxXZVariance + 0.05)) {
				this.flagEvent(event, "HighDistanceXZ");
				this.player().sendDevMessage("resultXZ: " + (float) resultXZ + " resultY: " + (float) resultY);
			} else if (yDist == 0.0 && useNoGravityYCheck && xzDist > 0.05 && !values.hasBlockNearHead()) {
				if (++noGravityBuffer > 4) {
					this.flagEvent(event, "NoGravityY");
				}
			} else if (Double.toString(Math.abs(yDist)).contains("00000000") && useImpossibleYPattern) {
				this.flagEvent(event, "ImpossibleYPattern");
			} else if (noGravityBuffer > 0) {
				noGravityBuffer -= 0.25;
			}
		}
	}
}
