package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class FastPlace extends AbstractCheck<BlockPlaceEvent> {

	public FastPlace(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(BlockPlaceEvent.class, 1, TimeUnit.SECONDS));
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		player.blockPlace = 0;
	}

	@Override
	void checkEvent(BlockPlaceEvent e) {
		Check(e);
	}

	/**
	 * A Simple FastPlace check
	 * 
	 * @param event
	 */
	public void Check(BlockPlaceEvent e) {
		NessPlayer player = manager.getPlayer(e.getPlayer());
		player.blockPlace++;
		if (player.blockPlace > 14) {
			try {
				if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			} catch (Exception ex) {
			}
			player.setViolation(new Violation("FastPlace", "Placing: " + player.blockPlace));
		}

	}

}
