package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
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
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class FlyFalseGround extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyFalseGround(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxBuffer = this.ness().getMainConfig().getCheckSection().flyFalseGround().maxBuffer();
	}

	private double buffer;
	private final double maxBuffer;

	public interface Config {
		@DefaultDouble(2)
		double maxBuffer();
	}

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = nessPlayer.getMovementValues();
		if (movementValues.isAroundLily() || movementValues.isAroundCarpet() || movementValues.isAroundSnow()
				|| this.ness().getMaterialAccess().getMaterial(player.getLocation().clone().add(0, -0.5, 0)).name()
						.contains("SCAFFOLD")) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500
				&& nessPlayer.getLastVelocity().getY() > 0.35) {
			return;
		}
		if (movementValues.getHelper().hasflybypass(nessPlayer) || movementValues.isAroundSlime()
				|| Utility.hasLivingEntityNear(player)) {
			return;
		}
		if (Utility.hasVehicleNear(player) || nessPlayer.getMovementValues().isAroundWeb()) {
			return;
		}
		// boolean isReallyOnGround =
		/*
		 * nessPlayer.sendDevMessage( "FalseGround, blocks near player? " +
		 * (movementValues.isGroundAround() ? "yes" : "no") + " The player says he " +
		 * (nessPlayer.isOnGroundPacket() ? "is" : "isn't") + " onGround");
		 * nessPlayer.sendDevMessage( "FalseGround1, is y divisible by 0,015625 ? " +
		 * (e.getTo().getY() % 0.015625 < 0.001 ? "yes" : "no") + "Result: " + (float)
		 * (e.getTo().getY() % 0.015625) + " The player says he " +
		 * (nessPlayer.isOnGroundPacket() ? "is" : "isn't") + " onGround");
		 */
		if (nessPlayer.isOnGroundPacket() && !movementValues.isGroundAround() && !movementValues.isAroundLadders()) {
			flagEvent(e, " FalseGround");
		} else if (nessPlayer.isOnGroundPacket()
				&& !movementValues.getHelper().isMathematicallyOnGround(e.getTo().getY())
				&& !movementValues.isOnGroundCollider() && ++buffer > maxBuffer) {
			flagEvent(e, " FalseGround1");
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
	}
}
