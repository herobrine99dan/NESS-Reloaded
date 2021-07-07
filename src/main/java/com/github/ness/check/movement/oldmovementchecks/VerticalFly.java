package com.github.ness.check.movement.oldmovementchecks;

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

public class VerticalFly extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public VerticalFly(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double buffer;

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = player().getMovementValues();
		Player player = e.getPlayer();
		if (values.getHelper().hasflybypass(player()) || player.getAllowFlight() || Utility.hasVehicleNear(player)
				|| player().isTeleported()) {
			return;
		}
		if (values.isGroundAround()) {
			if (buffer > 0) {
				buffer--;
			}
		}
		if (values.getTo().getY() > values.getFrom().getY() && !values.isGroundAround()) {
			if (values.getXZDiff() == 0.0D && !player.hasPotionEffect(PotionEffectType.JUMP)
					&& !values.isNearMaterials("CACTUS", "SLIME")
					&& this.player().milliSecondTimeDifference(PlayerAction.ATTACK) >= 500.0D) {
				this.flagEvent(e);
				return;
			}
		} else if (buffer > 0) {
			buffer -= 0.25;
		}
	}

}
