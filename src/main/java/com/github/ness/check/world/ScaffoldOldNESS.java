package com.github.ness.check.world;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;

public class ScaffoldOldNESS extends ListeningCheck<BlockPlaceEvent> {

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos
			.forEventWithTask(BlockPlaceEvent.class, PeriodicTaskInfo.syncTask(Duration.ofMillis(500)));

	public ScaffoldOldNESS(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private Location oldLoc = new Location(Bukkit.getWorlds().get(0),0,0,0);
	private int placeTicks;

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		Check1(e);
		
	}
	
	@Override
	protected void checkSyncPeriodic() {
		oldLoc = this.player().getBukkitPlayer().getLocation();
		placeTicks = 0;
	}

	public void Check1(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (oldLoc.getY() == player.getLocation().getY()) {
			if (!player.isSneaking() && !player.isFlying() && this.player().getMovementValues().isGroundAround()) {
				if (placeTicks > 1) { //Since we are updating this value every 500 milliseconds (and not every 1000 milliseconds), we must divide this maxPlaceTicks by two
					if (player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).equals(event.getBlock()))
						this.flagEvent(event);
				}
			}
		}
		placeTicks++;
	}
}
