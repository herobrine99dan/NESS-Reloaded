package com.github.ness.check.misc;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.MultipleListeningCheck;
import com.github.ness.check.MultipleListeningCheckFactory;

public class TestCheck extends MultipleListeningCheck {

	public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListener(PlayerMoveEvent.class, PlayerAnimationEvent.class);

	public TestCheck(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(Event e) {
	}
}
