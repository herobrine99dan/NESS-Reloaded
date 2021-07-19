package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.MultipleListeningCheck;
import com.github.ness.check.MultipleListeningCheckFactory;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.Utility;

public class Step extends MultipleListeningCheck {

	private float yVelocity;
	private boolean velocityAlreadyUsed = false;
	private float lastYVelocity;
	public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListener(PlayerMoveEvent.class,
			PlayerVelocityEvent.class);

	public Step(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(Event event) {
		Player player = ((PlayerEvent) event).getPlayer(); // It must extends at least PlayerEvent, we declared this
															// before!
		if (player().isNot(player))
			return;
		if (event instanceof PlayerVelocityEvent)
			onVelocity((PlayerVelocityEvent) event);
		if (event instanceof PlayerMoveEvent)
			onMove((PlayerMoveEvent) event);
	}

	private void onVelocity(PlayerVelocityEvent e) {
		yVelocity = (float) e.getVelocity().getY();
		velocityAlreadyUsed = true;
	}

	private void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		MovementValues values = player().getMovementValues();
		if (values.getHelper().hasflybypass(player()) || player.getAllowFlight() || player().hasBeenInVehicle()
				|| player().isTeleported() || player().getAcquaticUpdateFixes().isRiptiding()) {
			return;
		}
		double jumpBoost = Utility.getPotionEffectLevel(player, PotionEffectType.JUMP);
		float yDiffUpper = (float) values.getyDiff();
		if(yDiffUpper < 0.001) { //This check is for high y Values, not low ones!
			return;
		}
		float minY = this.player().isUsingGeyserMC() ? 0.76f : 0.6f;
		minY += jumpBoost * 0.1;
		//Handling Velocity: one tick velocity isn't enough, we have to even predict the next ticks!!
		if (velocityAlreadyUsed) {
			velocityAlreadyUsed = false;
			if(yVelocity > 0) {
				minY += yVelocity;
			}
			lastYVelocity = yVelocity;
		} else {
			float predictedY = (lastYVelocity - 0.08f) * 0.98f;
			if (predictedY > 0) {
				minY += predictedY;
				lastYVelocity = predictedY;
			}
		}
		if (yDiffUpper > minY && !values.isNearMaterials("SLIME")) { // TODO Add groundAround
			flagEvent(e, "High Distance: " + yDiffUpper + " minY: " + minY);
		}
	}

}
