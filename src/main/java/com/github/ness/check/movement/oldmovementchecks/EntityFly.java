package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class EntityFly extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	int preVL;

	public EntityFly(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		preVL = 0;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		MovementValues values = player().getMovementValues();
		if (player.isInsideVehicle()) {
			if(!player.getVehicle().getType().name().contains("HORSE")) {
				if(values.getyDiff() > 0.1 && values.getXZDiff() > 0.1 && !values.isGroundAround() && !player.getVehicle().isOnGround()) {
					this.flag(player.getVehicle().getType().name());
				}
			}
		}
	}

}
