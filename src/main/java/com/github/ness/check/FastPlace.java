package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class FastPlace extends AbstractCheck<BlockPlaceEvent> {

	int max;
	
	public FastPlace(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(BlockPlaceEvent.class, 1, TimeUnit.SECONDS));
		this.max = this.manager.getNess().getNessConfig().getCheck(this.getClass())
				.getInt("maxblockplaced", 14);
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
		if (player.blockPlace > max) {
			player.setViolation(new Violation("FastPlace", "Placing: " + player.blockPlace), e);
		}
	}

}
