package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class AutoClicker extends AbstractCheck<PlayerInteractEvent> {

	public AutoClicker(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 1, TimeUnit.SECONDS));
	}

	@Override
	void checkEvent(PlayerInteractEvent e) {
		Check(e);
		Check1(e);
	}

	void Check(PlayerInteractEvent e) {
		Action action = e.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			NessPlayer player = manager.getPlayer(e.getPlayer());
			player.setCPS(player.getCPS() + 1);
			ConfigurationSection config = this.manager.getNess().getNessConfig().getCheck(this.getClass());
			if (player.getCPS() > config.getInt("maxcps",15) && !e.getPlayer().getTargetBlock(null, 5).getType().name().contains("grass")) {
				player.setViolation(new Violation("AutoClicker", "MaxCPS: " + player.getCPS()));
				if(manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}
		}
	}

	void Check1(PlayerInteractEvent e) {
		NessPlayer player = manager.getPlayer(e.getPlayer());
		long delay = System.currentTimeMillis() - player.getCPSDelay();
		long lastDelay = delay - player.getCPSlastDelay();
		ConfigurationSection config = this.manager.getNess().getNessConfig().getCheck(this.getClass());
		if (delay > config.getInt("maxdelay") || e.getPlayer().isBlocking()) {
			return;
		}
		if(manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
			e.setCancelled(true);
		}
		player.setViolation(new Violation("AutoClicker", "RepeatedDelay: " + delay + " Result: " + lastDelay));
		player.setCPSlastDelay(delay);
		player.setCPSDelay(System.currentTimeMillis());
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		player.setCPS(0);
	}
}
