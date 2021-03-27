package com.github.ness.check.misc;

import java.util.ArrayList;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class TestCheck extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	ArrayList<String> yDiffs;
	ArrayList<String> lastYDiffs;
	double lastYDiff;

	public TestCheck(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		yDiffs = new ArrayList<String>();
		lastYDiffs = new ArrayList<String>();
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		MovementValues values = player().getMovementValues();
		if (this.player().isOnGroundPacket() != event.getPlayer().isOnGround()) {
			this.player().sendDevMessage(
					"Message: " + this.player().isOnGroundPacket());
		}
	}

}
