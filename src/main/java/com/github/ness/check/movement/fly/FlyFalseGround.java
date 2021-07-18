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

public class FlyFalseGround extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyFalseGround(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxBuffer = this.ness().getMainConfig().getCheckSection().flyFalseGround().maxBuffer();
	}

	private double buffer;
	private final double maxBuffer;

	public interface Config {
		@DefaultDouble(1)
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
		if (this.ness().getMaterialAccess().getMaterial(player.getLocation().clone().add(0, -0.5, 0)).name()
				.contains("SCAFFOLD")) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500
				&& nessPlayer.getLastVelocity().getY() > 0.35) {
			return;
		}
		if (movementValues.getHelper().hasflybypass(nessPlayer) || Utility.hasLivingEntityNear(player)) {
			return;
		}
		if (Utility.hasVehicleNear(player) || nessPlayer.getMovementValues().getHelper().isNearMaterials(e.getTo(),
				"SLIME", "WEB", "LILY", "LADDER")) {
			return;
		}
		if (nessPlayer.isOnGroundPacket() && !movementValues.isGroundAround()) {
			flagEvent(e, " TypeB");
		} else if (nessPlayer.isOnGroundPacket()
				&& !movementValues.getHelper().isMathematicallyOnGround(e.getTo().getY()) && ++buffer > maxBuffer) {
			flagEvent(e, " TypeA, result: " + (float) (movementValues.getTo().getY() % (1D / 64D)));
		} else if (buffer > 0) {
			buffer -= 0.25;
		}
	}
}
