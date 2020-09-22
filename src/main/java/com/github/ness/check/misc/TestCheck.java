package com.github.ness.check.misc;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class TestCheck extends AbstractCheck<PlayerInteractEvent> {

	public static final CheckInfo<PlayerInteractEvent> checkInfo = CheckInfo
			.eventOnly(PlayerInteractEvent.class);
	long lastClick;

	public TestCheck(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		lastClick = System.nanoTime() / 1000_000L;
	}

    @Override
    protected void checkEvent(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	final long currentMS = System.nanoTime() / 1000_000L;
    	final long subtraction = currentMS - lastClick;
    	player.sendMessage("Delay: " + subtraction);
    	lastClick = currentMS;
    }

}
