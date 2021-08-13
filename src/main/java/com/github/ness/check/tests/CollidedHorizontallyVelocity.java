package com.github.ness.check.tests;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.MultipleListeningCheck;
import com.github.ness.check.MultipleListeningCheckFactory;
import com.github.ness.data.MovementValues;

public class CollidedHorizontallyVelocity extends MultipleListeningCheck {
	public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListener(PlayerMoveEvent.class);

	public CollidedHorizontallyVelocity(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(Event event) {
		if (event instanceof PlayerMoveEvent)
			onMove((PlayerMoveEvent) event);
	}

	private void onMove(PlayerMoveEvent e) {
		Location to = e.getTo();
		if((isSolid(to.clone().add(0.31,0,0)) || isSolid(to.clone().add(-0.31,0,0)) || isSolid(to.clone().add(0,0,0.31)) || isSolid(to.clone().add(0,0,-0.31))) && e.getPlayer().isSneaking()) 
			e.getPlayer().setVelocity(new Vector(0, 0.3, 0));
	}
	
	private boolean isSolid(Location loc) {
		return loc.getBlock().getType().isSolid();
	}

}
