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
	public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListener(PlayerMoveEvent.class,
			PlayerVelocityEvent.class);

	public Step(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(Event event) {
		Player player = ((PlayerEvent) event).getPlayer();
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
		float yDiff = (float) values.getyDiff();
		if(yDiff < 0.001) { //This check is for high y Values, not low ones!
			return;
		}
		float minY = this.player().isUsingGeyserMC() ? 0.76f : 0.6f;
		minY += jumpBoost * 0.1;
		this.player().sendDevMessage("yDiff: " + yDiff);
		if (velocityAlreadyUsed) {
			velocityAlreadyUsed = false;
			if(yVelocity > 0) {
				minY += yVelocity;
			}
		}
                boolean isOnSlime = e.getTo().clone().add(0,-1.0,0).getBlock().getType().name().contains("SLIME") || e.getFrom().clone().add(0,-1.0,0).getBlock().getType().name().contains("SLIME");
		if (yDiff > minY && values.isGroundAround()) { // TODO Add groundAround
			if(!isOnSlime) {
                            flagEvent(e, "High Distance: " + yDiff + " minY: " + minY);
                        }
		}
	}

}
