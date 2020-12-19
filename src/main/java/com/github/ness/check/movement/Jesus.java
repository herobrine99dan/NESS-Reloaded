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
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class Jesus extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	double distmultiplier = 0.75;
	double lastXZDist;
	double lastYDist;
	int liquidTicks = 0;

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
		if (Utility.hasflybypass(p) || Utility.hasVehicleNear(p, 3) || p.getAllowFlight()) {
			return;
		}
		if (p.getLocation().getBlock().isLiquid()) {
			liquidTicks++;
		} else {
			liquidTicks = 0;
		}
		// We handle Prediction for Y Value
		double yDist = nessPlayer.getMovementValues().getyDiff();
		double xzDist = nessPlayer.getMovementValues().getXZDiff();
		if (event.getTo().clone().add(0, -0.1, 0).getBlock().isLiquid() && event.getFrom().getBlock().isLiquid()
				&& Utility.isNearLava(event.getTo(), this.manager().getNess().getMaterialAccess())) {
			handleLava(nessPlayer.getMovementValues(), event, nessPlayer);
		} else if (event.getTo().clone().add(0, -0.1, 0).getBlock().isLiquid() && event.getFrom().getBlock().isLiquid()
				&& Utility.isNearWater(event.getTo(), this.manager().getNess().getMaterialAccess())) {
			handleWater(nessPlayer.getMovementValues(), event, nessPlayer);
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
		//nessPlayer.sendDevMessage("WaterTicks: " + this.liquidTicks + " resultY: " + resultY);
		if (!values.isOnGroundCollider() && !values.isAroundLily()) {
			if (yDist > 0.302D) {
				this.flagEvent(event, "HighDistanceY");
			} else if (resultY > 0.105 && !values.isGroundAround()) {
				this.flagEvent(event, "HighVarianceY: " + (float) resultY);
			} else if (resultXZ > 0.11) {
				this.flagEvent(event, "HighDistanceXZ: " + resultXZ);
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
		//nessPlayer.sendDevMessage("LavaTicks: " + this.liquidTicks + " resultY: " + resultY);
		if (!values.isOnGroundCollider() && !values.isAroundLily()) {
			if (yDist > 0.302D) {
				this.flagEvent(event, "HighDistanceY");
			} else if (resultY > 0.13) {
				this.flagEvent(event, "HighVarianceY");
			} else if (resultXZ > 0.17) {
				this.flagEvent(event, "HighDistanceXZ");
				this.player().sendDevMessage("resultXZ: " + (float) resultXZ + " resultY: " + (float) resultY);
			}
		}
	}

}
