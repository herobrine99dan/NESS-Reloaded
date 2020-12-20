package com.github.ness.check.movement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

	private double lastDeltaY;
	private int airTicks;
	private double buffer;

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Check(e);
	}

	/**
	 * Check for Invalid Gravity
	 *
	 * @param e
	 */
	public void Check(PlayerMoveEvent e) {
		NessPlayer nessPlayer = this.player();
		Player p = e.getPlayer();
		MovementValues values = nessPlayer.getMovementValues();
		double y = values.getyDiff();
		if (values.isOnGroundCollider()) {
			airTicks = 0;
		} else {
			airTicks++;
		}
		if (jumpBoost()) {
			this.flagEvent(e, "HighJumpBoost");
		}
		if (levitationEffect()) {
			this.flagEvent(e, "NoLevitation");
		}
		lastDeltaY = y;
	}

	public boolean levitationEffect() {
		if (!Bukkit.getVersion().contains("1.8")) {
			//this.player().sendDevMessage("YDiff: " + (float) this.player().getMovementValues().getyDiff());
			int effect = Utility.getPotionEffectLevel(this.player().getBukkitPlayer(), PotionEffectType.LEVITATION);
			// this.motY += (0.05D * (getEffect(MobEffects.LEVITATION).getAmplifier() + 1) -
			// this.motY) * 0.2D;
			double predictedY = this.lastDeltaY + (0.05D * effect - this.lastDeltaY) * 0.2D;
			if (effect > 0) {
				return false;
			}
		}
		return false;
	}

	public boolean jumpBoost() {
		int jumpBoost = Utility.getPotionEffectLevel(this.player().getBukkitPlayer(), PotionEffectType.JUMP);
		double max = 0.42F + (jumpBoost * 0.1);
		if(this.player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 1700) {
			max += this.player().getLastVelocity().getY();
		}
		if ((float) this.player().getMovementValues().getyDiff() > max && jumpBoost > 0) {
			return true;
		}
		return false;
	}

}
