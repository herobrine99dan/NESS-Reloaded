package com.github.ness.check.movement;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.blockgetter.MaterialAccess;
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

	double distmultiplier = 0.75;
	double lastXZDist;
	double lastYDist;
	int liquidTicks = 0;
	private double noGravityBuffer;

	public Jesus(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.distmultiplier = this.ness().getMainConfig().getCheckSection().jesus().distmultiplier();
	}

	public interface Config {
		@DefaultDouble(0.7)
		double distmultiplier();
	}

	// TODO Implement Lava Movement
	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = nessPlayer.getMovementValues();
		if (movementValues.getHelper().hasflybypass(nessPlayer) || Utility.hasVehicleNear(p)
				|| p.getAllowFlight() || nessPlayer.milliSecondTimeDifference(PlayerAction.DAMAGE) < 2000) {
			return;
		}
		if (p.getLocation().getBlock().isLiquid()) {
			liquidTicks++;
		} else {
			liquidTicks = 0;
		}
		// We handle Prediction for Y Value
		double yDist = movementValues.getyDiff();
		double xzDist = movementValues.getXZDiff();
		if (event.getTo().clone().add(0, -0.1, 0).getBlock().isLiquid() && event.getFrom().getBlock().isLiquid()
				&& isNearLava(event.getTo(), this.manager().getNess().getMaterialAccess())) {
			handleLava(movementValues, event, nessPlayer);
		} else if (event.getTo().clone().add(0, -0.1, 0).getBlock().isLiquid() && event.getFrom().getBlock().isLiquid()
				&& isNearWater(event.getTo(), this.manager().getNess().getMaterialAccess())) {
			handleWater(movementValues, event, nessPlayer);
		}
		lastXZDist = xzDist;
		lastYDist = yDist;
	}

	public void handleWater(MovementValues values, Cancellable event, NessPlayer nessPlayer) {
		double yDist = values.getyDiff();
		double predictedY = lastYDist * 0.80D;
		predictedY -= 0.02D; // We handle Prediction for XZ Values
		double resultY = yDist - predictedY;
		double xzDist = values.getXZDiff();
		double predictedXZ = lastXZDist * 0.8f;
		double resultXZ = xzDist - predictedXZ;
		// nessPlayer.sendDevMessage("WaterTicks: " + this.liquidTicks + " resultY: " +
		// resultY);
		if (!values.isOnGroundCollider() && !values.isAroundLily()) {
			if (yDist > 0.302D) {
				this.flagEvent(event, "HighDistanceY");
			} else if (resultY > 0.105 && !values.isGroundAround()) {
				this.flagEvent(event, "HighVarianceY: " + (float) resultY);
			} else if (resultXZ > 0.11) {
				this.flagEvent(event, "HighDistanceXZ: " + resultXZ);
			} else if (yDist == 0.0) {
				if (++noGravityBuffer > 4) {
					this.flagEvent(event, "NoGravityY");
				}
			} else if (noGravityBuffer > 0) {
				noGravityBuffer -= 0.25;
			}
		}
	}

	public void handleLava(MovementValues values, Cancellable event, NessPlayer nessPlayer) {
		double yDist = values.getyDiff();
		double predictedY = lastYDist * 0.5D;
		predictedY -= 0.02D; // We handle Prediction for XZ Values
		double resultY = yDist - predictedY;
		double xzDist = values.getXZDiff();
		double predictedXZ = lastXZDist * 0.5D;
		double resultXZ = xzDist - predictedXZ;
		// nessPlayer.sendDevMessage("LavaTicks: " + this.liquidTicks + " resultY: " +
		// resultY);
		if (!values.isOnGroundCollider() && !values.isAroundLily()) {
			if (yDist > 0.302D) {
				this.flagEvent(event, "HighDistanceY");
			} else if (resultY > 0.13) {
				this.flagEvent(event, "HighVarianceY");
			} else if (resultXZ > 0.17) {
				this.flagEvent(event, "HighDistanceXZ");
				this.player().sendDevMessage("resultXZ: " + (float) resultXZ + " resultY: " + (float) resultY);
			} else if (yDist == 0.0) {
				if (++noGravityBuffer > 4) {
					this.flagEvent(event, "NoGravityY");
				}
			} else if (noGravityBuffer > 0) {
				noGravityBuffer -= 0.25;
			}
		}
	}

	private boolean isNearWater(Location loc, MaterialAccess access) {
		int water = 0;
		for (Block b : this.player().getMovementValues().getHelper().getCollidingBlocks(loc, 0.3, 0.1)) {
			String material = access.getMaterial(b).name();
			if (material.contains("WATER")) {
				water++;
			}
		}
		return water > 4;
	}

	private boolean isNearLava(Location loc, MaterialAccess access) {
		int water = 0;
		for (Block b : this.player().getMovementValues().getHelper().getCollidingBlocks(loc, 0.5, 0.1)) {
			String material = access.getMaterial(b).name();
			if (material.contains("LAVA")) {
				water++;
			}
		}
		return water > 4;
	}

}
