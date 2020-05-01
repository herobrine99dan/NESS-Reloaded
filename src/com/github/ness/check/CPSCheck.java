package com.github.ness.check;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

public class CPSCheck extends AbstractCheck<PlayerInteractEvent> {

	public CPSCheck(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 4, TimeUnit.SECONDS));
	}

	/**
	 * Retain clicks for 4 seconds in CPS check
	 * 
	 */
	private static final int CLICK_HISTORY_RETENTION = 4;

	@Override
	public void checkAsyncPeriodic(NessPlayer player) {
		Set<Long> clickHistory = player.getClickHistory();
		long now = System.currentTimeMillis();
		clickHistory.add(now);
		clickHistory.removeIf((time) -> time - now > (CLICK_HISTORY_RETENTION * 1000L));
		int clicks = clickHistory.size();
		if (clicks / CLICK_HISTORY_RETENTION > 14) {
			hasViolated = true;
		}
	}

	@Override
	void checkEvent(PlayerInteractEvent evt) {
		manager.getPlayer(evt.getPlayer()).getClickHistory().add(System.currentTimeMillis());
	}
	
}
