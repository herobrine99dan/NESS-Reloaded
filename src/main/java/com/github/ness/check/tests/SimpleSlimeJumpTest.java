package com.github.ness.check.tests;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.MultipleListeningCheck;
import com.github.ness.check.MultipleListeningCheckFactory;
import com.github.ness.data.MovementValues;

public class SimpleSlimeJumpTest extends MultipleListeningCheck {
	public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListener(PlayerMoveEvent.class,
			BlockPistonExtendEvent.class);

	public SimpleSlimeJumpTest(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(Event event) {
		if (event instanceof BlockPistonExtendEvent) //TODO Check what player the piston is moving
			onPiston((BlockPistonExtendEvent) event);
		if (event instanceof PlayerMoveEvent)
			onMove((PlayerMoveEvent) event);
	}

	private float lastYDelta;
	
	private void onPiston(BlockPistonExtendEvent event) {
		this.player().sendDevMessage("Piston Action!");
	}

	private void onMove(PlayerMoveEvent e) {
		MovementValues values = this.player().getMovementValues();
		float yDiff = (float) values.getyDiff();
		float predictedY = !e.getPlayer().isOnGround() ? (lastYDelta - 0.08f) * 0.98f : 0.0f;
		if(e.getFrom().clone().add(0,-0.01,0).getBlock().getType().name().contains("SLIME")) {
			predictedY *= -1;
		}
		this.player().sendDevMessage("yDiff: " + yDiff + " predictedMotion: " + predictedY);
		lastYDelta = yDiff;
	}

}
