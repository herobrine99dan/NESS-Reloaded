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
import com.github.ness.utility.Utility;

public class VerticalFly1 extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public VerticalFly1(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double buffer;

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = player().getMovementValues();
		Player player = e.getPlayer();
		if (values.getHelper().hasflybypass(player()) || player.getAllowFlight() || Utility.hasVehicleNear(player)
				|| player().isTeleported()
				|| getMaterialAccess().getMaterial(e.getTo().clone().add(0, 0.5, 0)).name().contains("SCAFFOLD")
				|| getMaterialAccess().getMaterial(e.getTo().clone().add(0, -0.5, 0)).name().contains("SCAFFOLD")) {
			return;
		}
		if (values.isGroundAround()) {
			if (buffer > 0) {
				buffer--;
			}
		}
		if (values.getTo().getY() > values.getFrom().getY() && !values.isGroundAround()) {
			if (player.getLocation().getY() % 0.5D != 0.0D && !values.isOnGroundCollider()
					&& !values.isAroundNonOccludingBlocks() && !values.isAroundLiquids() && !values.isAroundLadders()
					&& !values.isAroundKelp() && !player.hasPotionEffect(PotionEffectType.JUMP)
					&& !values.isAroundSlime()) {
				if (++buffer > 2) {
					this.flagEvent(e);
				}
			}
		} else if (buffer > 0) {
			buffer -= 0.25;
		}
	}

}
