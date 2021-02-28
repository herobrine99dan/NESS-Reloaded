package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class IllegalFallDistance extends ListeningCheck<PlayerMoveEvent> {
	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public IllegalFallDistance(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		MovementValues values = player().getMovementValues();
		if(player.isOnGround() && player.getFallDistance() > 2) {
			this.flagEvent(event);
		}
		if (values.isGroundAround() && !event.getPlayer().getGameMode().name().contains("SPECTATOR")) {
			if (values.getyDiff() < -2 && event.getPlayer().getFallDistance() == 0) {
				flagEvent(event, "IllegalDist");
			}
		}
	}
}
