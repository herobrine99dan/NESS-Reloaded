package com.github.ness.check.movement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class IrregularMovement extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public IrregularMovement(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double levitationBuffer;
	private double flyYSum;
	private double illegalXZBuffer;

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		jumpBoost(e);
		levitationEffect(e);
		stepYCheck(e);
		IllegalXZDistance(e);
	}

	public void IllegalXZDistance(Cancellable e) {
		MovementValues values = player().getMovementValues();
		Player player = player().getBukkitPlayer();
		if (values.isGroundAround() || player.getAllowFlight() || player.isFlying() || values.isInsideVehicle()
				|| Utility.hasVehicleNear(player().getBukkitPlayer()) || values.isAroundLiquids()) {
			return;
		}
		double xzDiff = values.getXZDiff();
		double yDiff = values.getyDiff();
		if (xzDiff > 0.0D && Math.abs(yDiff) < 0.005) {
			if (++illegalXZBuffer > 3) {
				this.flagEvent(e, "IllegalXZDistance");
			}
		} else if(illegalXZBuffer > 0) {
			illegalXZBuffer -= 0.25;
		}
	}

	// Thanks funkemunky for the idea
	public void stepYCheck(Cancellable e) {
		MovementValues values = player().getMovementValues();
		double yDelta = values.getyDiff();
		if (values.isAroundStairs() || player().isTeleported() || player().isHasSetback()
				|| Utility.hasVehicleNear(player().getBukkitPlayer())) {
			flyYSum = 0.0;
		}
		if (yDelta > 0) {
			flyYSum += yDelta;
			double minY = this.player().isUsingGeyserMC() ? 1.3 : 0.52;
			if (flyYSum % 0.5 == 0 && flyYSum > minY && !player().getAcquaticUpdateFixes().isRiptiding()) {
				this.flagEvent(e, "flyYSum: " + (float) flyYSum);
			}
		} else {
			flyYSum = 0.0;
		}
	}

	public void levitationEffect(Cancellable e) {
		if (!Bukkit.getVersion().contains("1.8")) {
			int effect = Utility.getPotionEffectLevel(this.player().getBukkitPlayer(), PotionEffectType.LEVITATION);
			if (effect > 0) {
				float yDiff = (float) this.player().getMovementValues().getyDiff();
				float superPrediction = 0.045370374f * effect;
				float resultY = superPrediction - yDiff;
				if (resultY > 0.005) {
					if (++levitationBuffer > 50) {
						this.flagEvent(e, "NoLevitation ResultY: " + resultY);
					}
				} else if (levitationBuffer > 0) {
					levitationBuffer -= 0.25;
				}
			} else if (levitationBuffer > 0) {
				levitationBuffer -= 0.25;
			}
		}
	}

	public void jumpBoost(Cancellable e) {
		int jumpBoost = Utility.getPotionEffectLevel(this.player().getBukkitPlayer(), PotionEffectType.JUMP);
		double max = 0.42F + (jumpBoost * 0.13);
		if (this.player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 1700) {
			max += this.player().getLastVelocity().getY();
		}
		if ((float) this.player().getMovementValues().getyDiff() > max && jumpBoost > 0) {
			this.flagEvent(e, "HighJumpBoost: " + (float) player().getMovementValues().getyDiff());
		}
	}

}
