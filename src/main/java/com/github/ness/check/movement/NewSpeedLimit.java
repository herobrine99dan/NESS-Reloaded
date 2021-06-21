package com.github.ness.check.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class NewSpeedLimit extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public NewSpeedLimit(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);

	}

	public interface Config {
	}

	protected void checkEvent(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location from = e.getFrom();
		Player player = e.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = this.player().getMovementValues();
		
	}
}