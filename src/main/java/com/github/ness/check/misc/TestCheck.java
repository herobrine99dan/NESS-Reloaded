package com.github.ness.check.misc;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class TestCheck extends ListeningCheck<PlayerInteractEvent> {

	public static final ListeningCheckInfo<PlayerInteractEvent> checkInfo = CheckInfos
			.forEvent(PlayerInteractEvent.class);
	long lastClick;

	public TestCheck(ListeningCheckFactory<?, PlayerInteractEvent> factory, NessPlayer player) {
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
