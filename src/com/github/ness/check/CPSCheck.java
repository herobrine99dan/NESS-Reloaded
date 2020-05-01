package com.github.ness.check;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.Violation;

public class CPSCheck extends AbstractCheck<PlayerInteractEvent> {

	private int hardLimit;
	private int hardLimitRetention;
	private int constancyThreshold;
	private int constancyRetention;
	
	public CPSCheck(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 4, TimeUnit.SECONDS));
		ConfigurationSection section = manager.getNess().getNessConfig().getCheck("cps");
		hardLimit = section.getInt("hard-limit.cps", 16);
		hardLimitRetention = 1000 * section.getInt("hard-limit.retention-span", 4);
		constancyThreshold = section.getInt("constancy.threshold", 4);
		constancyRetention = 1000 * section.getInt("constancy.retention-span", 24);
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		// Cleanup old history
		Set<Long> clickHistory = player.getClickHistory();
		long now1 = System.currentTimeMillis();
		clickHistory.removeIf((time) -> time - now1 > constancyRetention);

		if (clickHistory.isEmpty()) {
			return; // Don't check players who aren't clicking
		}

		// Copy temporary state
		Set<Long> copy1 = new HashSet<>(clickHistory);
		ArrayList<Long> copy2 = new ArrayList<>(copy1);

		// Hard limit check

		long now2 = System.currentTimeMillis();
		copy1.removeIf((time) -> time - now2 > hardLimitRetention);
		int cps = copy1.size() / hardLimitRetention;
		if (cps > hardLimit) {
			player.setViolation(new Violation("CPS", cps));
		}

		// Constancy check

		if (cps > constancyThreshold) {
			copy2.sort(null);
			long start = copy2.get(0);
			long end = copy2.get(copy2.size() - 1);
			ArrayList<Long> subClicks = new ArrayList<>();
			
		}
	}

	@Override
	void checkEvent(PlayerInteractEvent evt) {
		manager.getPlayer(evt.getPlayer()).getClickHistory().add(System.currentTimeMillis());
	}
	
}
