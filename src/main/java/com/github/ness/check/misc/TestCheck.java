package com.github.ness.check.misc;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TestCheck extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo
			.eventOnly(PlayerMoveEvent.class);

	public TestCheck(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
    	if(player().isNot(event.getPlayer())) return;
        NessPlayer nessPlayer = player();
        double pitch = nessPlayer.getMovementValues().pitchDiff;
        double yaw = nessPlayer.getMovementValues().yawDiff;
        Player p = event.getPlayer();
    }

}
